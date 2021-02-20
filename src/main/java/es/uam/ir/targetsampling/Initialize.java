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
//    public static final String[] SPLITS = new String[]{
//            //"GroupShuffleSplit",
//            "KFold",
//            "ShuffleSplit",
//            "StratifiedKFold",
//            "StratifiedShuffleSplit",
//            "TimeSeriesSplit",
//    };


    /**
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        LogManager.getLogManager().reset();

        mkdir();

        //preprocessDatasets();
//        runMovieLens1mCrossValidation();

        List<Thread> threads = new ArrayList<>();

        //  threads.add(runMovieLens1M());
        //  threads.add(runMovieLens100k());
        //  threads.add(runYahooBiased());
        //  threads.add(runYahooUnbiased());
        threads.add(runMovieLens25M());

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

    private static Thread runYahooBiased() throws InterruptedException {
        return getThread("Initialize_YAHOO_BIASED_PROPERTIES_FILE", YAHOO_BIASED_PROPERTIES_FILE);
    }

    private static Thread runYahooUnbiased() throws InterruptedException {
        Thread tYahooUNBIASED = new Thread(() -> {
            try {
                String title = "Initialize_YAHOO_UNBIASED_PROPERTIES_FILE";
                Timer.start((Object) title, title);
                Configuration conf = new Configuration(YAHOO_UNBIASED_PROPERTIES_FILE);
                TargetSampling targetSelection = new TargetSampling(conf);
                targetSelection.runWithUnbiasedTest(conf.getTestPath(), title);
                Timer.done(title, title);
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace(System.out);
            }
        });

        tYahooUNBIASED.sleep(100);
        tYahooUNBIASED.start();
        return tYahooUNBIASED;
    }

    private static Thread runMovieLens1M() throws InterruptedException {
        return getThread("Initialize_ML1M_BIASED_PROPERTIES_FILE", ML1M_BIASED_PROPERTIES_FILE);
    }

    private static Thread runMovieLens25M() throws InterruptedException {
        return getThread("Initialize_ML25M_BIASED_PROPERTIES_FILE", ML25M_BIASED_PROPERTIES_FILE);
    }

    private static Thread getThread(String title, String config) throws InterruptedException {
        Thread thread = new Thread(() -> {
            //MovieLens
            try {
                Timer.start((Object) title, title);
                Configuration conf = new Configuration(config);
                TargetSampling targetSelection = new TargetSampling(conf);
                targetSelection.runCrossValidation(title);
                Timer.done(title, title);
            } catch (IOException e) {
                System.out.println(title + config + e.toString());
                System.out.println(e);
                e.printStackTrace(System.out);
            }
        });
        thread.sleep(100);
        thread.start();
        return thread;
    }

    private static Thread runMovieLens100k() throws InterruptedException {
        return getThread("Initialize_ML100K_BIASED_PROPERTIES_FILE", ML100K_BIASED_PROPERTIES_FILE);
    }

/*
    private static void runMovieLens1mCrossValidation() throws InterruptedException {

        List<Thread> threads = new ArrayList<>();

        for (String split : SPLITS) {
            String file = "conf/ml1m-biased.properties";
            String biased_results = "results/biased/" + split;


            String logSource = "Initialize_ML1M_BIASED_PROPERTIES_FILE_" + split;

            File directory = new File(biased_results);
            if (!directory.exists()) {
                directory.mkdir();
            }

            Thread thread = new Thread(() -> {
                //MovieLens
                try {
                    Configuration conf = new Configuration(file);
                    conf.setDataPath("datasets/ml1m/" + split + "/");
                    conf.setResultsPath(biased_results + "/ml1m-");
                    TargetSampling targetSelection = new TargetSampling(conf);
                    targetSelection.runCrossValidation(logSource);
                } catch (IOException e) {
                                System.out.println(e);
                    e.printStackTrace(System.out);
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
                            System.out.println(e);
                e.printStackTrace(System.out);
            }
        });
    }
*/

    private static void preprocessDatasets() throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        threads.add(StartThread("MovieLens25M", Initialize::preprocessMl25mDataset));
        threads.add(StartThread("Yahoo R3", Initialize::preprocessYahooDataset));
        threads.add(StartThread("MovieLens1M", Initialize::preprocessMl1mDataset));
        threads.add(StartThread("MovieLens100K", Initialize::preprocessMl100kDataset));

        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println(e);
                e.printStackTrace(System.out);
            }
        });
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

    static void preprocessMl1mDataset() {
        try {

            RandomAccessFile ml1mIn = new RandomAccessFile(ORIGINAL_ML1M_DATASET_PATH, "r");
            byte[] bytes = new byte[(int) ml1mIn.length()];
            ml1mIn.read(bytes);
            String ratings = new String(bytes, StandardCharsets.UTF_8);

            PrintStream ml1mOut = new PrintStream(PREPROCESSED_ML1M_DATASET_PATH);
            ml1mOut.print(ratings.replace("::", "\t"));
            ml1mOut.close();

            CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML1M_DATASET_PATH, ML1M_PATH, GenerateFigure.N_FOLDS);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace(System.out);
        }
    }

    static void preprocessMl25mDataset() {
        try {

            RandomAccessFile ml25mIn = new RandomAccessFile(ORIGINAL_ML25M_DATASET_PATH, "r");
            byte[] bytes = new byte[(int) ml25mIn.length()];
            ml25mIn.read(bytes);
            String ratings = new String(bytes, StandardCharsets.UTF_8);

            PrintStream ml25mOut = new PrintStream(PREPROCESSED_ML25M_DATASET_PATH);
            ml25mOut.print(ratings
                    .replace(",", "\t")
            );
            ml25mOut.close();

            CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML25M_DATASET_PATH, ML25M_PATH, GenerateFigure.N_FOLDS);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace(System.out);
        }
    }

    static void preprocessMl100kDataset() {
        try {
            Files.copy(
                    Paths.get(ORIGINAL_ML100K_DATASET_PATH),
                    Paths.get(PREPROCESSED_ML100K_DATASET_PATH),
                    StandardCopyOption.REPLACE_EXISTING);
            CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML100K_DATASET_PATH, ML100K_PATH, GenerateFigure.N_FOLDS);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace(System.out);
        }
    }

    static void preprocessYahooDataset() {
        try {
            // No format change needed
            Files.copy(
                    Paths.get(ORIGINAL_YAHOO_TEST_DATASET_PATH),
                    Paths.get(PREPROCESSED_YAHOO_TEST_DATASET_PATH),
                    StandardCopyOption.REPLACE_EXISTING);

            Set<Long> testUsers = new HashSet<>();
            Scanner scn;
            scn = new Scanner(new File(PREPROCESSED_YAHOO_TEST_DATASET_PATH));

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
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace(System.out);
        }
    }

}
