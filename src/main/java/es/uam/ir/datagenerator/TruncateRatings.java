/*
* Copyright (C) 2020 Information Retrieval Group at Universidad Autónoma
* de Madrid, http://ir.ii.uam.es.
*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/
package es.uam.ir.datagenerator;

import es.uam.eps.ir.ranksys.fast.preference.FastPreferenceData;
import es.uam.eps.ir.ranksys.fast.preference.SimpleFastPreferenceData;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import static org.ranksys.formats.parsing.Parsers.lp;
import org.ranksys.formats.preference.SimpleRatingPreferencesReader;

/**
 *
 * @author Rocío Cañamares
 * @author Pablo Castells
 *
 */
public class TruncateRatings {

    /**
     * 
     * @param data
     * @param threshold
     * @return
     * @throws IOException 
     */
    public static FastPreferenceData<Long, Long> run(FastPreferenceData<Long, Long> data, double threshold) {

        ByteArrayOutputStream newDataOutputStream = new ByteArrayOutputStream();
        PrintStream newData = new PrintStream(newDataOutputStream);

        data.getAllUsers().forEachOrdered(user -> {
            data.getUserPreferences(user).forEachOrdered(up -> newData.println(user + "\t" + up.v1 + "\t" + Math.max(up.v2 - threshold + 1, 0)));
        });

        ByteArrayInputStream newDataInputStream = new ByteArrayInputStream(newDataOutputStream.toByteArray());

        try {
            return SimpleFastPreferenceData.load(SimpleRatingPreferencesReader.get().read(newDataInputStream, lp, lp), data, data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
