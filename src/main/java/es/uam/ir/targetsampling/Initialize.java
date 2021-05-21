/*
 * Copyright (C) 2020 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.ir.targetsampling;

import es.uam.ir.util.Timer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.LogManager;

import static es.uam.ir.targetsampling.DataSetInitialize.*;

public class Initialize extends PreprocessDatasets {


    /**
     * @param args
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        LogManager.getLogManager().reset();

        Timer.start(args, "Initialize start");
        mkdir();

        List<Thread> threads = new ArrayList<>();

        threads.addAll(run_optimal_finder());

//        threads.addAll(run_for_all());
//        threads.addAll(run_NOFILL());
//        threads.addAll(run_experiments());

        boolean run_NOFILL = Arrays.asList(args).contains("run_NOFILL");
        boolean run_experiments = Arrays.asList(args).contains("run_experiments");
        if (run_experiments) {
            threads.addAll(run_experiments());
        }
        if (run_NOFILL) {
            threads.addAll(run_NOFILL());
        }
        ThreadJoin(threads);
        Timer.done(args, "Initialize end");

    }

    public static void ThreadJoin(List<Thread> threads) {
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace(System.out);
            }
        });
    }

    private static void mkdir() {
        File directory = new File(RESULTS_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }

        directory = new File(RESULTS_PATH + BIASED_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }

        directory = new File(RESULTS_PATH + UNBIASED_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }


    private static Collection<? extends Thread> run_optimal_finder() throws InterruptedException, IOException {

        return Arrays.asList(
                run("ML100K_FINDER_RANDOM", new Configuration(ML100K_BIASED_PROPERTIES_FILE).forAll().forTestAndFull()),
                run("ML1M_FINDER_RANDOM", new Configuration(ML1M_BIASED_PROPERTIES_FILE).forAll().forTestAndFull()),
//                StartCrossValidateTargetSampling("ML10M_FINDER", new Configuration(ML10M_BIASED_PROPERTIES_FILE).forAll().forTestAndFull()),
                run("YAHOO_BIASED_FINDER_RANDOM", new Configuration(YAHOO_BIASED_PROPERTIES_FILE).forAll().forTestAndFull()),
                run("YAHOO_UNBIASED_FINDER_RANDOM", new Configuration(YAHOO_UNBIASED_PROPERTIES_FILE).forAll().forTestAndFull()),

//                POPULAR

                run("ML100K_FINDER_POPULAR", new Configuration(ML100K_POPULAR_BIASED_PROPERTIES_FILE).forAll().forTestAndFull()),
                run("ML1M_FINDER_POPULAR", new Configuration(ML1M_POPULAR_BIASED_PROPERTIES_FILE).forAll().forTestAndFull()),
//                StartCrossValidateTargetSampling("ML10M_FINDER", new Configuration(ML10M_BIASED_PROPERTIES_FILE).forAll().forTestAndFull()),
                run("YAHOO_BIASED_FINDER_POPULAR", new Configuration(YAHOO_POPULAR_BIASED_PROPERTIES_FILE).forAll().forTestAndFull()),
                run("YAHOO_UNBIASED_FINDER_POPULAR", new Configuration(YAHOO_POPULAR_UNBIASED_PROPERTIES_FILE).forAll().forTestAndFull()),

//                UNPOPULAR

                run("ML100K_FINDER_UNPOPULAR", new Configuration(ML100K_UNPOPULAR_BIASED_PROPERTIES_FILE).forAll().forTestAndFull()),
                run("ML1M_FINDER_UNPOPULAR", new Configuration(ML1M_UNPOPULAR_BIASED_PROPERTIES_FILE).forAll().forTestAndFull()),
//                StartCrossValidateTargetSampling("ML10M_FINDER", new Configuration(ML10M_BIASED_PROPERTIES_FILE).forAll().forTestAndFull()),
                run("YAHOO_BIASED_FINDER_UNPOPULAR", new Configuration(YAHOO_UNPOPULAR_BIASED_PROPERTIES_FILE).forAll().forTestAndFull()),
                run("YAHOO_UNBIASED_FINDER_UNPOPULAR", new Configuration(YAHOO_UNPOPULAR_UNBIASED_PROPERTIES_FILE).forAll().forTestAndFull())

        );
    }

    private static Collection<? extends Thread> run_for_all() throws InterruptedException, IOException {

        return Arrays.asList(
                run("ML100K_ALL", new Configuration(ML100K_BIASED_PROPERTIES_FILE).forAll()),
                run("ML1M_ALL", new Configuration(ML1M_BIASED_PROPERTIES_FILE).forAll()),
//                StartCrossValidateTargetSampling("ML10M_ALL", new Configuration(ML10M_BIASED_PROPERTIES_FILE).forAll()),
                run("YAHOO_BIASED_ALL", new Configuration(YAHOO_BIASED_PROPERTIES_FILE).forAll())
        );
    }

    private static Collection<? extends Thread> run_experiments() throws InterruptedException, IOException {

        return Arrays.asList(
                run("ML100K", new Configuration(ML100K_BIASED_PROPERTIES_FILE)),
                run("ML1M", new Configuration(ML1M_BIASED_PROPERTIES_FILE)),
//                StartCrossValidateTargetSampling("ML10M", new Configuration(ML10M_BIASED_PROPERTIES_FILE))
                run("YAHOO_BIASED", new Configuration(YAHOO_BIASED_PROPERTIES_FILE)),
                run("YAHOO_UNBIASED", new Configuration(YAHOO_UNBIASED_PROPERTIES_FILE))
        );
    }

    private static Collection<? extends Thread> run_NOFILL() throws InterruptedException, IOException {

        return Arrays.asList(
                run("ML100K_NOFILL", new Configuration(ML100K_BIASED_PROPERTIES_FILE).forNofill()),
                run("ML1M_NOFILL", new Configuration(ML1M_BIASED_PROPERTIES_FILE).forNofill()),
//                StartCrossValidateTargetSampling("ML10M_NOFILL", new Configuration(ML10M_BIASED_PROPERTIES_FILE).forNofill())
                run("YAHOO_BIASED_NOFILL", new Configuration(YAHOO_BIASED_PROPERTIES_FILE).forNofill()),
                run("YAHOO_UNBIASED_NOFILL", new Configuration(YAHOO_UNBIASED_PROPERTIES_FILE).forNofill())
        );
    }


    private static Thread run(String title, Configuration conf) throws InterruptedException {
        Thread thread = new Thread(() -> {
            //MovieLens
            try {
                Timer.start(title, title);
                TargetSampling targetSelection = new TargetSampling(conf);
                targetSelection.runCrossValidation(title);
                Timer.done(title, title);
            } catch (IOException e) {
                System.out.println(title + conf + e);
                System.out.println("StartCrossValidateTargetSampling " + e);
                e.printStackTrace(System.out);
            }
        });
        Thread.sleep(100);
        thread.start();
        return thread;
    }


}
