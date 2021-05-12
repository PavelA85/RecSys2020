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
import java.util.stream.Stream;

/**
 * @author Rocío Cañamares
 * @author Pablo Castells
 */
public class FastSamplers {

    /**
     * @param <U>
     * @param <I>
     * @param trainData
     * @param inTestForUser
     * @param targetSize
     * @return
     */
    public static <U, I> Function<U, IntPredicate> uniform(FastPreferenceData<U, I> trainData, Map<U, IntSet> inTestForUser, int targetSize) {
        IntFunction<Double> weight = iidx -> 1.0;
        return sample(trainData, inTestForUser, targetSize, weight);
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
            IntFunction<Double> weight) {
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
                                    user -> getKSet(trainData, targetSize)));
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

    private static <U, I> IntSet getKSet(
            FastPreferenceData<U, I> trainData,
            int targetSize) {

        class TTuple {

            I item;
            long positiveCount;

            public long getPositiveCount() {
                return positiveCount;
            }
        }
        Comparator<TTuple> comparator = Comparator.comparing(TTuple::getPositiveCount);
        comparator = comparator.thenComparing(s -> (Long) s.item);


        IntSet kSet = new IntOpenHashSet();
        trainData.getAllItems().map(
                item -> {
                    final TTuple tuple = new TTuple();
                    tuple.item = item;
                    tuple.positiveCount = trainData.getItemPreferences(item).mapToDouble(IdPref::v2).filter(value -> value >= 4.0).count();
                    return tuple;
                })
                .sorted(comparator.reversed())
                .limit(targetSize)
                .forEachOrdered(i -> kSet.add(Math.toIntExact((Long) i.item)));

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
