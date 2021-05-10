/*
 * Copyright (C) 2020 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.ir.targetsampling;

import es.uam.ir.crossvalidation.CrossValidation;
import es.uam.ir.util.Timer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.LogManager;

import static es.uam.ir.targetsampling.DataSetInitialize.*;

public class Initialize extends PreprocessDatasets {


    /**
     * @param args
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        LogManager.getLogManager().reset();

        mkdir();
//        threads.add(StartThread("MovieLens10M", Initialize::preprocessMl10mDataset));
//        preprocessMl10mDataset_nothread();
//        runMovieLens10M_ALL_nothreading();
        // runMovieLens1mCrossValidation();
//        runMovieLens10M_nothreading();

        List<Thread> threads = new ArrayList<>();

        //threads.add(StartThread("MovieLens10M", Initialize::preprocessMl10mDataset));
        //threads.add(StartThread("MovieLens25M", Initialize::preprocessMl25mDataset));
        //threads.add(StartThread("Yahoo R3", Initialize::preprocessYahooDataset));
        //threads.add(StartThread("MovieLens1M", Initialize::preprocessMl1mDataset));
        //threads.add(StartThread("MovieLens100K", Initialize::preprocessMl100kDataset));
        //threads.add(StartThread("MovieLens1M_GENDER", Initialize::preprocessMl1m_GENDER_Dataset));
        //threads.add(StartThread("MovieLens100K_GENDER", Initialize::preprocessMl100k_GENDER_Dataset));
        //threads.add(StartThread("MovieLens1M_AGE", Initialize::preprocessMl1m_AGE_Dataset));
        //threads.add(StartThread("MovieLens100K_AGE", Initialize::preprocessMl100k_AGE_Dataset));

        ThreadJoin(threads);


        threads.clear();

        threads.addAll(run_NOFILL());
//        threads.addAll(runMovieLens1M_AGE());
//        threads.addAll(runMovieLens100K_AGE());
        threads.addAll(runMovieLens100K_AGE_ALL());
        threads.addAll(runMovieLens1M_AGE_ALL());
        threads.add(runMovieLens100k_ALL());
        threads.add(runMovieLens1M_ALL());
        threads.add(runYahooBiased_ALL());
//        threads.addAll(runMovieLens100K_Gender());
//        threads.addAll(runMovieLens1M_Gender());
        threads.addAll(runMovieLens100K_Gender_ALL());
        threads.addAll(runMovieLens1M_Gender_ALL());
//        threads.add(runMovieLens25M());
//        threads.add(runMovieLens10M_ALL());

/*
        threads.add(runMovieLens100k());
        threads.add(runMovieLens1M());
        threads.add(runMovieLens10M());
        threads.add(runYahooBiased());
        threads.add(runYahooUnbiased());
*/

        ThreadJoin(threads);
    }

    private static void ThreadJoin(List<Thread> threads) {
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

    private static Thread runYahooBiased_ALL() throws InterruptedException, IOException {
        return StartCrossValidateTargetSampling("YAHOO_ALL", new Configuration(YAHOO_BIASED_PROPERTIES_FILE).forAll());
    }

    private static Thread runYahooBiased() throws InterruptedException, IOException {
        return StartCrossValidateTargetSampling("YAHOO", new Configuration(YAHOO_BIASED_PROPERTIES_FILE));
    }

    private static Thread runYahooUnbiased() throws InterruptedException {
        Thread tYahooUNBIASED = new Thread(() -> {
            try {
                String title = "YAHOO_UNBIASED";
                Timer.start((Object) title, title);
                Configuration conf = new Configuration(YAHOO_UNBIASED_PROPERTIES_FILE);
                UnbiasedTargetSampling targetSelection = new UnbiasedTargetSampling(conf);
                targetSelection.runWithUnbiasedTest(conf.getTestPath(), title);
                Timer.done(title, title);
            } catch (IOException e) {
                System.out.println("runYahooUnbiased" + e);
                e.printStackTrace(System.out);
            }
        });

        tYahooUNBIASED.sleep(1000);
        tYahooUNBIASED.start();
        return tYahooUNBIASED;
    }

    private static Thread runMovieLens1M() throws InterruptedException, IOException {
        return StartCrossValidateTargetSampling("ML1M", new Configuration(ML1M_BIASED_PROPERTIES_FILE));
    }

    private static Thread runMovieLens1M_ALL() throws InterruptedException, IOException {
        return StartCrossValidateTargetSampling("ML1M_ALL", new Configuration(ML1M_BIASED_PROPERTIES_FILE).forAll());
    }

    private static Collection<? extends Thread> runMovieLens1M_Gender() throws InterruptedException, IOException {

        return Arrays.asList(StartCrossValidateTargetSampling("ML1M_MALE", new Configuration(ML1M_MALE_BIASED_PROPERTIES_FILE)),
                StartCrossValidateTargetSampling("ML1M_FEMALE", new Configuration(ML1M_FEMALE_BIASED_PROPERTIES_FILE)));
    }

    private static Collection<? extends Thread> runMovieLens100K_Gender() throws InterruptedException, IOException {

        return Arrays.asList(StartCrossValidateTargetSampling("ML100K_MALE", new Configuration(ML100K_MALE_BIASED_PROPERTIES_FILE)),
                StartCrossValidateTargetSampling("ML100K_FEMALE", new Configuration(ML100K_FEMALE_BIASED_PROPERTIES_FILE)));
    }

    private static Collection<? extends Thread> runMovieLens1M_AGE() throws InterruptedException, IOException {

        return Arrays.asList(StartCrossValidateTargetSampling("ML1M_YOUNG", new Configuration(ML1M_YOUNG_BIASED_PROPERTIES_FILE)),
                StartCrossValidateTargetSampling("ML1M_OLD", new Configuration(ML1M_OLD_BIASED_PROPERTIES_FILE)));
    }

    private static Collection<? extends Thread> runMovieLens100K_AGE() throws InterruptedException, IOException {

        return Arrays.asList(StartCrossValidateTargetSampling("ML100K_YOUNG", new Configuration(ML100K_YOUNG_BIASED_PROPERTIES_FILE)),
                StartCrossValidateTargetSampling("ML100K_OLD", new Configuration(ML100K_OLD_BIASED_PROPERTIES_FILE)));
    }

    private static Collection<? extends Thread> run_NOFILL() throws InterruptedException, IOException {

        return Arrays.asList(
                StartCrossValidateTargetSampling("ML100K_NOFILL", new Configuration(ML100K_BIASED_PROPERTIES_FILE).forNofill()),
//                StartCrossValidateTargetSampling("ML100K_NOFILL_MALE", new Configuration(ML100K_MALE_BIASED_PROPERTIES_FILE).forNofill()),
//                StartCrossValidateTargetSampling("ML100K_NOFILL_FEMALE", new Configuration(ML100K_FEMALE_BIASED_PROPERTIES_FILE).forNofill()),
//                StartCrossValidateTargetSampling("ML100K_NOFILL_YOUNG", new Configuration(ML100K_YOUNG_BIASED_PROPERTIES_FILE).forNofill()),
//                StartCrossValidateTargetSampling("ML100K_NOFILL_OLD", new Configuration(ML100K_OLD_BIASED_PROPERTIES_FILE).forNofill()),

                StartCrossValidateTargetSampling("ML1M_NOFILL", new Configuration(ML1M_BIASED_PROPERTIES_FILE).forNofill()),
//                StartCrossValidateTargetSampling("ML1M_NOFILL_MALE", new Configuration(ML1M_MALE_BIASED_PROPERTIES_FILE).forNofill()),
//                StartCrossValidateTargetSampling("ML1M_NOFILL_FEMALE", new Configuration(ML1M_FEMALE_BIASED_PROPERTIES_FILE).forNofill()),
//                StartCrossValidateTargetSampling("ML1M_NOFILL_YOUNG", new Configuration(ML1M_YOUNG_BIASED_PROPERTIES_FILE).forNofill()),
//                StartCrossValidateTargetSampling("ML1M_NOFILL_OLD", new Configuration(ML1M_OLD_BIASED_PROPERTIES_FILE).forNofill()),

                StartCrossValidateTargetSampling("ML10M_NOFILL", new Configuration(ML10M_BIASED_PROPERTIES_FILE).forNofill()),

                StartCrossValidateTargetSampling("YAHOO_NOFILL", new Configuration(YAHOO_BIASED_PROPERTIES_FILE).forNofill())
        );
    }

    private static Collection<? extends Thread> runMovieLens1M_Gender_ALL() throws InterruptedException, IOException {

        return Arrays.asList(StartCrossValidateTargetSampling("ML1M_MALE_BIASED_ALL", new Configuration(ML1M_MALE_BIASED_PROPERTIES_FILE).forAll()),
                StartCrossValidateTargetSampling("ML1M_FEMALE_BIASED_ALL", new Configuration(ML1M_FEMALE_BIASED_PROPERTIES_FILE).forAll()));
    }

    private static Collection<? extends Thread> runMovieLens100K_Gender_ALL() throws InterruptedException, IOException {

        return Arrays.asList(StartCrossValidateTargetSampling("ML100K_MALE_BIASED_ALL", new Configuration(ML100K_MALE_BIASED_PROPERTIES_FILE).forAll()),
                StartCrossValidateTargetSampling("ML100K_FEMALE_BIASED_ALL", new Configuration(ML100K_FEMALE_BIASED_PROPERTIES_FILE).forAll()));
    }

    private static Collection<? extends Thread> runMovieLens100K_AGE_ALL() throws InterruptedException, IOException {

        return Arrays.asList(StartCrossValidateTargetSampling("ML100K_YOUNG_BIASED_ALL", new Configuration(ML100K_YOUNG_BIASED_PROPERTIES_FILE).forAll()),
                StartCrossValidateTargetSampling("ML100K_OLD_BIASED_ALL", new Configuration(ML100K_OLD_BIASED_PROPERTIES_FILE).forAll()));
    }

    private static Collection<? extends Thread> runMovieLens1M_AGE_ALL() throws InterruptedException, IOException {

        return Arrays.asList(StartCrossValidateTargetSampling("ML1M_YOUNG_BIASED_ALL", new Configuration(ML1M_YOUNG_BIASED_PROPERTIES_FILE).forAll()),
                StartCrossValidateTargetSampling("ML1M_OLD_BIASED_ALL", new Configuration(ML1M_OLD_BIASED_PROPERTIES_FILE).forAll()));
    }

    private static Thread runMovieLens100k() throws InterruptedException, IOException {
        return StartCrossValidateTargetSampling("ML100K", new Configuration(ML100K_BIASED_PROPERTIES_FILE));
    }

    private static Thread runMovieLens100k_ALL() throws InterruptedException, IOException {
        return StartCrossValidateTargetSampling("ML100K_BIASED_ALL", new Configuration(ML100K_BIASED_PROPERTIES_FILE).forAll());
    }

    private static Thread runMovieLens25M() throws InterruptedException, IOException {
        return StartCrossValidateTargetSampling("ML25M", new Configuration(ML25M_BIASED_PROPERTIES_FILE));
    }

    private static Thread runMovieLens10M() throws InterruptedException, IOException {
        return StartCrossValidateTargetSampling("ML10M", new Configuration(ML10M_BIASED_PROPERTIES_FILE));
    }

    private static void runMovieLens10M_ALL_nothreading() throws IOException, InterruptedException {
        String log = "ML10M_ALL";
        Timer.start(log, log);
        TargetSampling targetSelection = new TargetSampling(new Configuration(ML10M_BIASED_PROPERTIES_FILE).forAll());
        targetSelection.runCrossValidation(log);
        Timer.done(log, log);
    }

    private static void runMovieLens10M_nothreading() throws IOException, InterruptedException {
        String log = "ML10M";
        Timer.start(log, log);
        TargetSampling targetSelection = new TargetSampling(new Configuration(ML10M_BIASED_PROPERTIES_FILE));
        targetSelection.runCrossValidation(log);
        Timer.done(log, log);
    }

    private static Thread runMovieLens10M_ALL() throws InterruptedException, IOException {
        return StartCrossValidateTargetSampling("ML10M", new Configuration(ML10M_BIASED_PROPERTIES_FILE).forAll());
    }

    private static Thread StartCrossValidateTargetSampling(String title, Configuration conf) throws InterruptedException {
        Thread thread = new Thread(() -> {
            //MovieLens
            try {
                Timer.start((Object) title, title);
                TargetSampling targetSelection = new TargetSampling(conf);
                targetSelection.runCrossValidation(title);
                Timer.done(title, title);
            } catch (IOException e) {
                System.out.println(title + conf + e.toString());
                System.out.println("StartCrossValidateTargetSampling " + e);
                e.printStackTrace(System.out);
            }
        });
        thread.sleep(100);
        thread.start();
        return thread;
    }

    private static Thread StartThread(String title, Runnable function) throws InterruptedException {
        Thread thread = new Thread(() -> {
            Timer.start((Object) title, "Processing: " + title);
            function.run();
            Timer.done(title, "Processing done: " + title);
        });
        thread.sleep(100);
        thread.start();
        return thread;
    }

}
