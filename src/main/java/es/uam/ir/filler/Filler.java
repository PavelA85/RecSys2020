/*
 * Copyright (C) 2020 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.ir.filler;

import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.ir.ranksys.rec.fast.basic.GenderRecommender;
import es.uam.ir.ranksys.rec.fast.basic.RandomRecommender;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

import org.ranksys.core.util.tuples.Tuple2id;

/**
 * @param <U>
 * @param <I>
 * @author Rocío Cañamares
 * @author Pablo Castells
 */
public class Filler<U, I> {

    public enum Mode {
        RND, Gender, NONE
    }

    private final Mode mode;
    private final FastPreferenceData<U, I> data;
    private final RandomRecommender<U, I> randomRecommender;
    private final GenderRecommender<U, I> genderRecommender;

    /**
     * @param mode
     * @param iIndex
     * @param uIndex
     * @param data
     */
    public Filler(Mode mode, FastItemIndex<I> iIndex, FastUserIndex<U> uIndex, FastPreferenceData<U, I> data) {
        this.mode = mode;
        this.data = data;
        if (mode == Mode.RND) {
            this.randomRecommender = new RandomRecommender<>(uIndex, iIndex);
        } else {
            this.randomRecommender = null;
        }
        if (mode == Mode.Gender) {
            this.genderRecommender = new GenderRecommender<>(uIndex, iIndex);
        } else {
            this.genderRecommender = null;
        }
    }

    /**
     * @param items
     * @param length
     * @param filter
     * @param user
     * @return
     */
    public List<Tuple2id> fill(List<Tuple2id> items, int length, IntPredicate filter, U user) {
        List<Tuple2id> newItems = new ArrayList<>(items);

        if (mode == Mode.NONE) {
            return newItems;
        }

        if (newItems.size() < length) {
            Set<Integer> iidxSet = new HashSet<>();
            items.forEach(item -> iidxSet.add(item.v1));

            FastRecommendation recommendations = null;

            if (mode == Mode.RND) {
                recommendations = randomRecommender.getRecommendation(data.user2uidx(user), length - newItems.size(), item -> !iidxSet.contains(item) && filter.test(item));
            } else if (mode == Mode.Gender) {
                recommendations = genderRecommender.getRecommendation(data.user2uidx(user), length - newItems.size(), item -> !iidxSet.contains(item) && filter.test(item));
            }

            List<Integer> newIndices = recommendations
                    .getIidxs()
                    .parallelStream()
                    .map(id -> id.v1)
                    .collect(Collectors.toList());

            if (length == 0) {
                length = newIndices.size();
            }

            List<Integer> allItems = newIndices.stream()
                    .filter(iidx -> !iidxSet.contains(iidx) && filter.test(iidx))
                    .collect(Collectors.toList());

            for (int i = 0; i < allItems.size() && newItems.size() < length; i++) {
                int iidx = allItems.get(i);
                newItems.add(new Tuple2id(iidx, 1.0));
            }
        }
        return newItems;
    }
}
