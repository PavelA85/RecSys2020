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

/**
 * @author Rocío Cañamares
 * @author Pablo Castells
 */
public class Initialize {
    public final static String ML1M = "ml1m";
    public final static String YAHOO = "yahoo";

    public final static String DATASETS_PATH = "datasets/";

    public final static String ML1M_PATH = DATASETS_PATH + ML1M + "/";
    public final static String ORIGINAL_ML1M_DATASET_PATH = ML1M_PATH + "ratings.dat";
    public final static String PREPROCESSED_ML1M_DATASET_PATH = ML1M_PATH + "data.txt";

    public final static String YAHOO_PATH = DATASETS_PATH + YAHOO + "/";
    public final static String ORIGINAL_YAHOO_TRAIN_DATASET_PATH = YAHOO_PATH + "ydata-ymusic-rating-study-v1_0-train.txt";
    public final static String ORIGINAL_YAHOO_TEST_DATASET_PATH = YAHOO_PATH + "ydata-ymusic-rating-study-v1_0-test.txt";
    public final static String PREPROCESSED_YAHOO_TRAIN_DATASET_PATH = YAHOO_PATH + "data.txt";
    public final static String PREPROCESSED_YAHOO_TEST_DATASET_PATH = YAHOO_PATH + "unbiased-test.txt";

    public final static String YAHOO_BIASED_PROPERTIES_FILE = "conf/yahoo-biased.properties";
    public final static String YAHOO_UNBIASED_PROPERTIES_FILE = "conf/yahoo-unbiased.properties";
    public final static String ML1M_BIASED_PROPERTIES_FILE = "conf/movielens-biased.properties";

    public final static String RESULTS_PATH = "results/";
    public final static String BIASED_PATH = "biased/";
    public final static String UNBIASED_PATH = "unbiased/";


    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        LogManager.getLogManager().reset();

        mkdir();

        if (false) {
            preprocessDatasets();
        }
        runExtraMovieLens();
//        Thread tMovieLens = runMovieLens();
//        WaitFor(tMovieLens, "Initialize_ML1M_BIASED_PROPERTIES_FILE");
//        Thread tYahooBIASED = runYahooBiased();
//        Thread tYahooUNBIASED = runYahooUnbiased();
//
//        WaitFor(tYahooBIASED, "Initialize_YAHOO_BIASED_PROPERTIES_FILE");
//        WaitFor(tYahooUNBIASED, "Initialize_YAHOO_UNBIASED_PROPERTIES_FILE");
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

    private static Thread runYahooBiased() {
        Thread tYahooBIASED = new Thread(() -> {
            try {
                Configuration conf = new Configuration(YAHOO_BIASED_PROPERTIES_FILE);
                TargetSampling targetSelection = new TargetSampling(conf);
                targetSelection.runCrossValidation("Initialize_YAHOO_BIASED_PROPERTIES_FILE");
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        });
        tYahooBIASED.start();
        return tYahooBIASED;
    }

    private static Thread runYahooUnbiased() {
        Thread tYahooUNBIASED = new Thread(() -> {
            try {
                Configuration conf = new Configuration(YAHOO_UNBIASED_PROPERTIES_FILE);
                TargetSampling targetSelection = new TargetSampling(conf);
                targetSelection.runWithUnbiasedTest(conf.getTestPath(), "Initialize_YAHOO_UNBIASED_PROPERTIES_FILE");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        tYahooUNBIASED.start();
        return tYahooUNBIASED;
    }

    private static Thread runMovieLens() throws IOException {
        Configuration conf = new Configuration(ML1M_BIASED_PROPERTIES_FILE);
        Thread tMovieLens = new Thread(() -> {
            //MovieLens
            try {
                TargetSampling targetSelection = new TargetSampling(conf);
                targetSelection.runCrossValidation("Initialize_ML1M_BIASED_PROPERTIES_FILE");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tMovieLens.start();
        return tMovieLens;
    }

    private static void runExtraMovieLens() throws IOException, InterruptedException {
        String[] splits = {
                "GroupShuffleSplit",
                "KFold",
                "ShuffleSplit",
                "StratifiedKFold",
                "StratifiedShuffleSplit",
                "TimeSeriesSplit",
        };

        List<Thread> threads = new ArrayList<>();

        for (String split : splits) {
            String file = "conf/movielens-biased.properties";
            String biased_results = "results/biased/" + split;

            Configuration conf = new Configuration(file);
            conf.setDataPath("datasets/ml1m/" + split + "/");
            conf.setResultsPath(biased_results + "/ml1m-");
            String logSource = "Initialize_ML1M_BIASED_PROPERTIES_FILE_" + split;

            File directory = new File(biased_results);
            if (!directory.exists()) {
                directory.mkdir();
            }

            Thread thread = new Thread(() -> {
                //MovieLens
                try {
                    TargetSampling targetSelection = new TargetSampling(conf);
                    targetSelection.runCrossValidation(logSource);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Thread.sleep(100);
            thread.start();
            threads.add(thread);

        }

        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static void preprocessDatasets() {
        String processMl1m1 = "processMl1m";
        Thread processMl1m = new Thread(() -> {
            try {
                Timer.start((Object) processMl1m1, "Processing Movielens 1M...");
                preprocessMl1mDataset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        processMl1m.start();

        String processYahoo1 = "processYahoo";
        Thread processYahoo = new Thread(() -> {
            try {
                Timer.start((Object) processYahoo1, "Processing Yahoo R3...");
                preprocessYahooDataset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        processYahoo.start();

        WaitFor(processMl1m, processMl1m1);
        WaitFor(processYahoo, processYahoo1);
    }

    private static void WaitFor(Thread thread, String processMl1m2) {
        try {
            thread.join();
            Timer.done(processMl1m2, processMl1m2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void preprocessMl1mDataset() throws IOException {

        RandomAccessFile ml1mIn = new RandomAccessFile(ORIGINAL_ML1M_DATASET_PATH, "r");
        byte[] bytes = new byte[(int) ml1mIn.length()];
        ml1mIn.read(bytes);
        String ratings = new String(bytes, StandardCharsets.UTF_8);

        PrintStream ml1mOut = new PrintStream(PREPROCESSED_ML1M_DATASET_PATH);
        ml1mOut.print(ratings.replace("::", "\t"));
        ml1mOut.close();

        CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML1M_DATASET_PATH, ML1M_PATH, GenerateFigure.N_FOLDS);
    }

    static void preprocessYahooDataset() throws IOException {
        // No format change needed
        Files.copy(
                Paths.get(ORIGINAL_YAHOO_TEST_DATASET_PATH),
                Paths.get(PREPROCESSED_YAHOO_TEST_DATASET_PATH),
                StandardCopyOption.REPLACE_EXISTING);

        Set<Long> testUsers = new HashSet<>();
        Scanner scn = new Scanner(new File(PREPROCESSED_YAHOO_TEST_DATASET_PATH));
        while (scn.hasNext()) {
            testUsers.add(new Long(scn.nextLine().split("\t")[0]));
        }

        PrintStream trainOut = new PrintStream(PREPROCESSED_YAHOO_TRAIN_DATASET_PATH);
        scn = new Scanner(new File(ORIGINAL_YAHOO_TRAIN_DATASET_PATH));
        while (scn.hasNext()) {
            String rating = scn.nextLine();
            if (testUsers.contains(new Long(rating.split("\t")[0]))) {
                trainOut.println(rating);
            }
        }
        trainOut.close();

        CrossValidation.randomNFoldCrossValidation(PREPROCESSED_YAHOO_TRAIN_DATASET_PATH, YAHOO_PATH, GenerateFigure.N_FOLDS);
    }

}
