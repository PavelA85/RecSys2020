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
    public final static String ML25M = "ml25m";
    public final static String ML100K = "ml100k";
    public final static String YAHOO = "yahoo";

    public final static String DATASETS_PATH = "datasets/";

    public final static String ML1M_PATH = DATASETS_PATH + ML1M + "/";
    public final static String ML25M_PATH = DATASETS_PATH + ML25M + "/";
    public final static String ML100K_PATH = DATASETS_PATH + ML100K + "/";
    public final static String ORIGINAL_ML1M_DATASET_PATH = ML1M_PATH + "ratings.dat";
    public final static String ORIGINAL_ML25M_DATASET_PATH = ML25M_PATH + "ratings.csv";
    public final static String ORIGINAL_ML100K_DATASET_PATH = ML100K_PATH + "u.data";
    public final static String PREPROCESSED_ML1M_DATASET_PATH = ML1M_PATH + "data.txt";
    public final static String PREPROCESSED_ML25M_DATASET_PATH = ML25M_PATH + "data.txt";
    public final static String PREPROCESSED_ML100K_DATASET_PATH = ML100K_PATH + "data.txt";

    public final static String YAHOO_PATH = DATASETS_PATH + YAHOO + "/";
    public final static String ORIGINAL_YAHOO_TRAIN_DATASET_PATH = YAHOO_PATH + "ydata-ymusic-rating-study-v1_0-train.txt";
    public final static String ORIGINAL_YAHOO_TEST_DATASET_PATH = YAHOO_PATH + "ydata-ymusic-rating-study-v1_0-test.txt";
    public final static String PREPROCESSED_YAHOO_TRAIN_DATASET_PATH = YAHOO_PATH + "data.txt";
    public final static String PREPROCESSED_YAHOO_TEST_DATASET_PATH = YAHOO_PATH + "unbiased-test.txt";

    public final static String YAHOO_BIASED_PROPERTIES_FILE = "conf/yahoo-biased.properties";
    public final static String YAHOO_UNBIASED_PROPERTIES_FILE = "conf/yahoo-unbiased.properties";
    public final static String ML1M_BIASED_PROPERTIES_FILE = "conf/ml1m-biased.properties";
    public final static String ML25M_BIASED_PROPERTIES_FILE = "conf/ml25m-biased.properties";
    public final static String ML100K_BIASED_PROPERTIES_FILE = "conf/ml100k-biased.properties";

    public final static String RESULTS_PATH = "results/";
    public final static String BIASED_PATH = "biased/";
    public final static String UNBIASED_PATH = "unbiased/";
    public static final String[] SPLITS = new String[]{
            //"GroupShuffleSplit",
            "KFold",
            "ShuffleSplit",
            "StratifiedKFold",
            "StratifiedShuffleSplit",
            "TimeSeriesSplit",
    };


    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        LogManager.getLogManager().reset();

        mkdir();

        if (true) {
            preprocessDatasets();
        }
//        runMovieLens1mCrossValidation();

//        WaitFor(runMovieLens1M(), "Initialize_ML1M_BIASED_PROPERTIES_FILE");
        WaitFor(runMovieLens25M(), "Initialize_ML25M_BIASED_PROPERTIES_FILE");
        WaitFor(runMovieLens100k(), "Initialize_ML100K_BIASED_PROPERTIES_FILE");

//        WaitFor(runYahooBiased(), "Initialize_YAHOO_BIASED_PROPERTIES_FILE");
//        WaitFor(runYahooUnbiased(), "Initialize_YAHOO_UNBIASED_PROPERTIES_FILE");
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

    private static Thread runMovieLens1M() throws IOException {
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

    private static Thread runMovieLens25M() throws IOException {
        Configuration conf = new Configuration(ML25M_BIASED_PROPERTIES_FILE);
        Thread tMovieLens = new Thread(() -> {
            //MovieLens
            try {
                TargetSampling targetSelection = new TargetSampling(conf);
                targetSelection.runCrossValidation("Initialize_ML25M_BIASED_PROPERTIES_FILE");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tMovieLens.start();
        return tMovieLens;
    }

    private static Thread runMovieLens100k() throws IOException {
        Configuration conf = new Configuration(ML100K_BIASED_PROPERTIES_FILE);
        Thread tMovieLens = new Thread(() -> {
            //MovieLens
            try {
                TargetSampling targetSelection = new TargetSampling(conf);
                targetSelection.runCrossValidation("Initialize_ML100K_BIASED_PROPERTIES_FILE");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tMovieLens.start();
        return tMovieLens;
    }

    private static void runMovieLens1mCrossValidation() throws IOException, InterruptedException {

        List<Thread> threads = new ArrayList<>();

        for (String split : SPLITS) {
            String file = "conf/ml1m-biased.properties";
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
                Timer.done(processMl1m1, "Processing Movielens 1M done");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        processMl1m.start();


        String processMl25m1 = "processMl25m";
        Thread processMl25m = new Thread(() -> {
            try {
                Timer.start((Object) processMl25m1, "Processing Movielens 25M...");
                preprocessMl25mDataset();
                Timer.done(processMl25m1, "Processing Movielens 25M done");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        processMl25m.start();

        String processMl100k1 = "processMl100k";
        Thread processMl100k = new Thread(() -> {
            try {
                Timer.start((Object) processMl100k1, "Processing Movielens 100K...");
                preprocessMl100kDataset();
                Timer.done(processMl100k1, "Processing Movielens 100K done");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        processMl100k.start();

        String processYahoo1 = "processYahoo";
        Thread processYahoo = new Thread(() -> {
            try {
                Timer.start((Object) processYahoo1, "Processing Yahoo R3...");
                preprocessYahooDataset();
                Timer.done(processYahoo1, "Processing Yahoo R3 done");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        processYahoo.start();

        WaitFor(processMl1m, processMl1m1);
        WaitFor(processMl100k, processMl1m1);
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

    static void preprocessMl25mDataset() throws IOException {

        RandomAccessFile ml25mIn = new RandomAccessFile(ORIGINAL_ML25M_DATASET_PATH, "r");
        byte[] bytes = new byte[(int) ml25mIn.length()];
        ml25mIn.read(bytes);
        String ratings = new String(bytes, StandardCharsets.UTF_8);

        PrintStream ml25mOut = new PrintStream(PREPROCESSED_ML25M_DATASET_PATH);
        ml25mOut.print(ratings.replace(",", "\t"));
        ml25mOut.close();

        CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML25M_DATASET_PATH, ML25M_PATH, GenerateFigure.N_FOLDS);
    }

    static void preprocessMl100kDataset() throws IOException {
        Files.copy(
                Paths.get(ORIGINAL_ML100K_DATASET_PATH),
                Paths.get(PREPROCESSED_ML100K_DATASET_PATH),
                StandardCopyOption.REPLACE_EXISTING);

        CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML100K_DATASET_PATH, ML100K_PATH, GenerateFigure.N_FOLDS);
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
