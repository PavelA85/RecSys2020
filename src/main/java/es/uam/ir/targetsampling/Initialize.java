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

public class Initialize {


    /**
     * @param args
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        LogManager.getLogManager().reset();

        mkdir();

        // runMovieLens1mCrossValidation();

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

//        threads.addAll(run_NOFILL());
//        threads.addAll(runMovieLens1M_AGE());
//        threads.addAll(runMovieLens100K_AGE());
//        threads.add(runMovieLens100k_ALL());
//        threads.add(runMovieLens1M_ALL());
//        threads.add(runMovieLens100k());
//        threads.addAll(runMovieLens100K_Gender());
//        threads.addAll(runMovieLens1M_Gender());
//        threads.addAll(runMovieLens1M_Gender_ALL());
//        threads.addAll(runMovieLens100K_Gender_ALL());
//        threads.add(runMovieLens1M());
//        threads.add(runYahooBiased());
//        threads.add(runYahooUnbiased());
//        threads.add(runMovieLens25M());
        threads.add(runMovieLens10M_ALL());
//        threads.add(runMovieLens10M());

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

        tYahooUNBIASED.sleep(100);
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
                StartCrossValidateTargetSampling("ML100K_NOFILL_MALE", new Configuration(ML100K_MALE_BIASED_PROPERTIES_FILE).forNofill()),
                StartCrossValidateTargetSampling("ML100K_NOFILL_FEMALE", new Configuration(ML100K_FEMALE_BIASED_PROPERTIES_FILE).forNofill()),
                StartCrossValidateTargetSampling("ML100K_NOFILL_YOUNG", new Configuration(ML100K_YOUNG_BIASED_PROPERTIES_FILE).forNofill()),
                StartCrossValidateTargetSampling("ML100K_NOFILL_OLD", new Configuration(ML100K_OLD_BIASED_PROPERTIES_FILE).forNofill()),

                StartCrossValidateTargetSampling("ML1M_NOFILL", new Configuration(ML1M_BIASED_PROPERTIES_FILE).forNofill()),
                StartCrossValidateTargetSampling("ML1M_NOFILL_MALE", new Configuration(ML1M_MALE_BIASED_PROPERTIES_FILE).forNofill()),
                StartCrossValidateTargetSampling("ML1M_NOFILL_FEMALE", new Configuration(ML1M_FEMALE_BIASED_PROPERTIES_FILE).forNofill()),
                StartCrossValidateTargetSampling("ML1M_NOFILL_YOUNG", new Configuration(ML1M_YOUNG_BIASED_PROPERTIES_FILE).forNofill()),
                StartCrossValidateTargetSampling("ML1M_NOFILL_OLD", new Configuration(ML1M_OLD_BIASED_PROPERTIES_FILE).forNofill()),

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
            System.out.println("preprocessMl1mDataset " + e);
            e.printStackTrace(System.out);
        }
    }

    static void preprocessMl1m_GENDER_Dataset() {
        try {
            CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML1M_MALE_DATASET_PATH, ML1M_MALE_PATH, GenerateFigure.N_FOLDS);
            CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML1M_FEMALE_DATASET_PATH, ML1M_FEMALE_PATH, GenerateFigure.N_FOLDS);
        } catch (IOException e) {
            System.out.println("preprocessMl1m_GENDER_Dataset " + e);
            e.printStackTrace(System.out);
        }
    }

    static void preprocessMl100k_GENDER_Dataset() {
        try {
            CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML100K_MALE_DATASET_PATH, ML100K_MALE_PATH, GenerateFigure.N_FOLDS);
            CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML100K_FEMALE_DATASET_PATH, ML100K_FEMALE_PATH, GenerateFigure.N_FOLDS);
        } catch (IOException e) {
            System.out.println("preprocessMl100k_GENDER_Dataset " + e);
            e.printStackTrace(System.out);
        }
    }

    static void preprocessMl1m_AGE_Dataset() {
        try {
            CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML1M_YOUNG_DATASET_PATH, ML1M_YOUNG_PATH, GenerateFigure.N_FOLDS);
            CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML1M_OLD_DATASET_PATH, ML1M_OLD_PATH, GenerateFigure.N_FOLDS);
        } catch (IOException e) {
            System.out.println("preprocessMl1m_AGE_Dataset " + e);
            e.printStackTrace(System.out);
        }
    }

    static void preprocessMl100k_AGE_Dataset() {
        try {
            CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML100K_YOUNG_DATASET_PATH, ML100K_YOUNG_PATH, GenerateFigure.N_FOLDS);
            CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML100K_OLD_DATASET_PATH, ML100K_OLD_PATH, GenerateFigure.N_FOLDS);
        } catch (IOException e) {
            System.out.println("preprocessMl100k_AGE_Dataset " + e);
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
            System.out.println("preprocessMl25mDataset " + e);
            e.printStackTrace(System.out);
        }
    }

    static void preprocessMl10mDataset() {
        try {

            RandomAccessFile ml10mIn = new RandomAccessFile(ORIGINAL_ML10M_DATASET_PATH, "r");
            byte[] bytes = new byte[(int) ml10mIn.length()];
            ml10mIn.read(bytes);
            String ratings = new String(bytes, StandardCharsets.UTF_8);

            PrintStream ml10mOut = new PrintStream(PREPROCESSED_ML10M_DATASET_PATH);
            ml10mOut.print(ratings
                    .replace("::", "\t")
                    .replace(".5", "")
            );
            ml10mOut.close();

            CrossValidation.randomNFoldCrossValidation(PREPROCESSED_ML10M_DATASET_PATH, ML10M_PATH, GenerateFigure.N_FOLDS);
        } catch (IOException e) {
            System.out.println("preprocessMl10mDataset " + e);
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
            System.out.println("preprocessMl100kDataset " + e);
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
            System.out.println("preprocessYahooDataset " + e);
            e.printStackTrace(System.out);
        }
    }

}
