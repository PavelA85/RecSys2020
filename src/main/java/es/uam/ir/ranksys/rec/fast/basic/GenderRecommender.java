/*
* Copyright (C) 2020 Information Retrieval Group at Universidad Autónoma
* de Madrid, http://ir.ii.uam.es.
*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/
package es.uam.ir.ranksys.rec.fast.basic;

import es.uam.eps.ir.ranksys.core.Recommendation;
import es.uam.eps.ir.ranksys.fast.FastRecommendation;
import es.uam.eps.ir.ranksys.fast.index.FastItemIndex;
import es.uam.eps.ir.ranksys.fast.index.FastUserIndex;
import es.uam.eps.ir.ranksys.rec.fast.AbstractFastRecommender;
import org.ranksys.core.util.tuples.Tuple2id;
import org.ranksys.core.util.tuples.Tuple2od;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Double.NaN;
import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;

/**
 * Random recommender. It provides non-personalized recommendations without by
 * extracting a sequence of a shuffled list of the items.
 *
 * @author Rocío Cañamares
 * @author Pablo Castells
 *
 * @param <U> type of the users
 * @param <I> type of the items
 */
public class GenderRecommender<U, I> extends AbstractFastRecommender<U, I> {

    private final static Random random = new Random();
    private final List<Tuple2id> randomList;

    /**
     * Constructor.
     *
     * @param uIndex fast user index
     * @param iIndex fast item index
     */
    public GenderRecommender(FastUserIndex<U> uIndex, FastItemIndex<I> iIndex) {
        super(uIndex, iIndex);

        randomList = iIndex.getAllIidx()
                .mapToObj(iidx -> new Tuple2id(iidx, Double.NaN))
                .collect(toList());

        shuffle(randomList, random);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, int maxLength, IntPredicate filter) {
        if (maxLength == 0) {
            maxLength = randomList.size();
        }

        List<Tuple2id> recommended = new ArrayList<>();
        int s = random.nextInt(randomList.size());
        int j = s;
        for (int i = 0; i < maxLength; i++) {
            Tuple2id iv = randomList.get(j);
            while (!filter.test(iv.v1)) {
                j = (j + 1) % randomList.size();
                iv = randomList.get(j);
                if (s == j) {
                    return new FastRecommendation(uidx, recommended);
                }
            }
            recommended.add(iv);
            j = (j + 1) % randomList.size();
            if (s == j) {
                break;
            }
        }
        return new FastRecommendation(uidx, recommended);
    }

    @Override
    public Recommendation<U, I> getRecommendation(U u, Stream<I> candidates) {
        List<Tuple2od<I>> items = candidates.map(i -> new Tuple2od<>(i, NaN)).collect(toList());
        Collections.shuffle(items, random);

        return new Recommendation<>(u, items);
    }

    @Override
    public FastRecommendation getRecommendation(int uidx, IntStream candidates) {
        List<Tuple2id> items = candidates.mapToObj(iidx -> new Tuple2id(iidx, NaN)).collect(toList());
        Collections.shuffle(items, random);

        return new FastRecommendation(uidx, items);
    }

}
