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
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static es.uam.ir.targetsampling.DataSetInitialize.*;

public class PreprocessDatasets {
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

    static void preprocessMl10mDataset_nothread() {
        try {
            String title = "MovieLens10M";
            Timer.start(title, "Processing: " + title);
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
            Timer.done(title, "Processing done: " + title);
        } catch (IOException e) {
            System.out.println("preprocessMl10mDataset " + e);
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
