/*
 * Copyright (C) 2020 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.ir.ranksys.rec.runner.fast;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * @author Rocío Cañamares
 * @author Pablo Castells
 */
public class FastSamplers {

    static Random rnd = new Random();

    public enum SamplerMode {
        RND, Popular, Unpopular, NONE
    }

    /**
     * @param <U>
     * @param <I>
     * @param trainData
     * @param inTestForUser
     * @param targetSize
     * @return
     */
    public static <U, I> Function<U, IntPredicate> uniform(FastPreferenceData<U, I> trainData,
                                                           Map<U, IntSet> inTestForUser,
                                                           int targetSize,
                                                           SamplerMode mode
    ) {
        IntFunction<Double> weight = iidx -> 1.0;
        return sample(trainData, inTestForUser, targetSize, weight, mode);
    }

    /**
     * @param <U>
     * @param <I>
     * @param trainData
     * @param inTestForUser
     * @param targetSize
     * @param weight
     * @return
     */
    public static <U, I> Function<U, IntPredicate> sample(
            FastPreferenceData<U, I> trainData,
            Map<U, IntSet> inTestForUser,
            int targetSize,
            IntFunction<Double> weight,
            SamplerMode mode) {
        Map<U, IntSet> mapKSets;
        if (targetSize >= trainData.numItems()) {
            IntSet kSet = new IntOpenHashSet();
            trainData
                    .getAllIidx()
                    .filter(iidx -> weight.apply(iidx) > 0)
                    .forEach(kSet::add);
            mapKSets = trainData
                    .getAllUsers()
                    .parallel()
                    .collect(Collectors
                            .toMap(
                                    user -> user,
                                    user -> kSet));
        } else {
            mapKSets = trainData
                    .getAllUsers()
                    .parallel()
                    .collect(Collectors
                            .toMap(
                                    user -> user,
                                    user -> getKSet(trainData, inTestForUser, targetSize, weight, user, mode)));
        }

        return user -> {
            IntSet testSet = inTestForUser.get(user);
            IntSet kSet = mapKSets.get(user);

            return iidx -> testSet.contains(iidx) || kSet.contains(iidx);
        };
    }

    public static List<?> flatten(List<?> list) {
        return list.stream()
                .flatMap(e -> e instanceof List ? flatten((List) e).stream() : Stream.of(e))
                .collect(Collectors.toList());
    }

    private static <U, I> IntSet getKSet(FastPreferenceData<U, I> trainData,
                                         Map<U, IntSet> inTestForUser,
                                         int targetSize,
                                         IntFunction<Double> weight,
                                         U user,
                                         SamplerMode mode) {

        class TTuple {

            private final I item;
            private final long positiveCount;
            private final long negativeCount;
            private final long ratingCount;
            private final double average;

            public TTuple(I item, DoubleStream ratings) {
                this.item = item;
                this.positiveCount = ratings.filter(value -> value >= 4.0).count();
                this.negativeCount = ratings.filter(value -> value < 4.0).count();
                this.ratingCount = ratings.count();
                this.average = ratings.average().orElse(0);

            }

            public long getPositiveCount() {
                return positiveCount;
            }

            public long getNegativeCount() {
                return negativeCount;
            }

            public long getRatingCount() {
                return ratingCount;
            }

            public double getAverage() {
                return average;
            }

            public I getItem() {
                return item;
            }
        }
        Comparator<TTuple> comparator = Comparator
                .comparing(TTuple::getPositiveCount)
                .thenComparing(TTuple::getNegativeCount).reversed()
                .thenComparing(s -> (Long) s.getItem());


        switch (mode) {
            case Popular:
                comparator = comparator.reversed();
                break;
            case Unpopular:
//                comparator = comparator;
                break;
            case RND:
                return getRandomKSet(trainData, inTestForUser, targetSize, weight, user);
            case NONE:
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }

        IntSet kSet = new IntOpenHashSet();
        trainData.getAllItems().map(
                item -> new TTuple(item, trainData.getItemPreferences(item).mapToDouble(IdPref::v2)))
                .sorted(comparator)
                .limit(targetSize)
                .forEachOrdered(i -> kSet.add(Math.toIntExact((Long) i.getItem())));

        return kSet;
    }


    private static <U, I> IntSet getRandomKSet(FastPreferenceData<U, I> trainData, Map<U, IntSet> inTestForUser, int targetSize, IntFunction<Double> weight, U user) {
        IntSet testSet = inTestForUser.get(user);

        int nItems = trainData.numItems();
        double items[] = new double[nItems];
        double sum = trainData
                .getAllIidx()
                .filter(iidx -> !testSet.contains(iidx))
                .mapToDouble(iidx -> {
                    items[iidx] = weight.apply(iidx);
                    return items[iidx];
                }).sum();

        IntSet kSet = new IntOpenHashSet();
        for (int i = 0; i < targetSize && sum > 0; i++) {
            double p = rnd.nextDouble() * sum;
            double acc = 0;

            int j = 0;
            while (acc < p) {
                acc += items[j];
                j++;
            }
            j--;
            kSet.add(j);

            sum -= items[j];
            items[j] = 0;

        }
        return kSet;
    }

    /**
     * @param <U>
     * @param <I>
     * @param testData
     * @return
     */
    public static <U, I> Map<U, IntSet> inTestForUser(FastPreferenceData<U, I> testData) {
        Map<U, IntSet> mapSets = testData.getAllUsers().parallel().collect(Collectors.toMap(
                user -> user,
                user -> {
                    IntSet set = new IntOpenHashSet();
                    testData.getUidxPreferences(testData.user2uidx(user))
                            .mapToInt(iv -> iv.v1)
                            .forEach(set::add);
                    return set;
                }));

        return mapSets;
    }
}
