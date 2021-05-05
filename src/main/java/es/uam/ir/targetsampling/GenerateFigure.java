/*
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.ir.targetsampling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import static es.uam.ir.targetsampling.DataSetInitialize.*;
import static es.uam.ir.targetsampling.TargetSampling.*;

/**
 * @author Rocío Cañamares
 * @author Pablo Castells
 */
public class GenerateFigure {


    public static final String[] DATASETS = {
            ML100K, ML100K_MALE, ML100K_FEMALE, ML100K_YOUNG, ML100K_OLD,
            ML1M, ML1M_MALE, ML1M_FEMALE, ML1M_YOUNG, ML1M_OLD,
            YAHOO};
    public static final String[] METRICS = {"P@10", "Recall@10", "nDCG@10", "FScore@10"};

    public static void init() {
        TARGET_SIZES.put(ML1M, FULL_TARGET_SIZE_ML1M);
        TARGET_SIZES.put(ML1M_MALE, 3671);
        TARGET_SIZES.put(ML1M_FEMALE, 3481);
        TARGET_SIZES.put(ML1M_YOUNG, 2650);
        TARGET_SIZES.put(ML1M_OLD, 2913);

        TARGET_SIZES.put(ML100K, FULL_TARGET_SIZE_ML100K);
        TARGET_SIZES.put(ML100K_MALE, 1605);
        TARGET_SIZES.put(ML100K_FEMALE, 1534);
        TARGET_SIZES.put(ML100K_YOUNG, 1012);
        TARGET_SIZES.put(ML100K_OLD, 1042);

        TARGET_SIZES.put(YAHOO, FULL_TARGET_SIZE_YAHOO);
    }

    public final static Map<String, Integer> TARGET_SIZES = new HashMap<>();

    public final static int FULL_TARGET_SIZE_ML1M = 2000;
    public final static int FULL_TARGET_SIZE_ML100K = 1682;
    public final static int FULL_TARGET_SIZE_YAHOO = 1000;
    public final static int N_FOLDS = 5;
    private static final String[] rec_ordered = new String[]{
            "iMF (full)"
            , "iMF (test)"
            , "kNN (full/test)"
            , "kNN (full)"
            , "kNN (test)"
            , "Normalized kNN (full)"
            , "Normalized kNN (test)"
            , "Average Rating"
            , "Popularity"
            , "Random"
    };


    /**
     * @param a
     */
    public static void main(String[] a) {
        init();
        List<Thread> threads = new ArrayList<>();
        int[] figures = {1, 2, 3, 101, 303, 4, 5};
        for (int f : figures) {
            Thread thread2 = new Thread(() -> {
                try {
//                    plotSplitFigure(f);
                    plotFigure(f);
                } catch (IOException e) {
                    System.out.println("main(String[] a) { " + e);
                    e.printStackTrace(System.out);
                }
            });
            thread2.start();
            threads.add(thread2);
        }

        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.out.println("main(String[] a) { ... t.join() " + e);
                e.printStackTrace(System.out);
            }
        });
    }

    private static void plotFigure(int figure) throws IOException {
        switch (figure) {
            case 1:
                generateFigure1(
                        RESULTS_PATH + BIASED_PATH + ML1M + "-" + TARGET_SAMPLING_FILE,
                        RESULTS_PATH + "figure1.txt",
                        N_FOLDS,
                        "P@10",
                        FULL_TARGET_SIZE_ML1M);
                break;
            case 101:
                generateFigure101(
                        N_FOLDS,
                        METRICS,
                        DATASETS
                );
                break;

            case 2:
                generateFigure2(
                        RESULTS_PATH + BIASED_PATH + YAHOO + "-" + TARGET_SAMPLING_FILE,
                        RESULTS_PATH + UNBIASED_PATH + YAHOO + "-" + TARGET_SAMPLING_FILE,
                        RESULTS_PATH + "figure2.txt",
                        N_FOLDS,
                        new String[]{"nDCG@10", "P@10", "Recall@10", "FScore@10"},
                        FULL_TARGET_SIZE_YAHOO);
                break;
            case 3:
                generateFigure3(
                        RESULTS_PATH + BIASED_PATH,
                        new String[]{ML1M, YAHOO},
                        METRICS,
                        RESULTS_PATH + "figure3.txt",
                        N_FOLDS);
                break;
            case 303:
                generateFigure303(
                        RESULTS_PATH + BIASED_PATH,
                        DATASETS,
                        METRICS,
                        N_FOLDS,
                        "figure303.");
                break;
            case 4:
                generateFigure4(
                        RESULTS_PATH + BIASED_PATH,
                        RESULTS_PATH + UNBIASED_PATH,
                        DATASETS,
                        METRICS,
                        RESULTS_PATH + "figure4.txt",
                        N_FOLDS);
                break;
            case 5:
                generateFigure5(
                        RESULTS_PATH + BIASED_PATH,
                        DATASETS,
                        new String[]{"Coverage@10"},
                        "figure505.",
                        N_FOLDS);
                break;
            default:
                System.out.println("Invalid figure number");
        }
    }

    public static void generateFigure1(
            String inFile,
            String outFile,
            int nFolds,
            String metric,
            int fullTargetSize) throws FileNotFoundException {
        Map<String, double[]> values = prepare_for_figure1(inFile, metric, fullTargetSize);

        PrintStream out = new PrintStream(outFile);
        out.println("====================");
        out.println("Dataset: ml1m");
        out.println("====================\n");

        out.println(metric);
        out.println("Recommender\tFull\tTest");

        String[] recommenders = getSortedRecommenders(values);

        for (String recommender : recommenders) {
            out.print(recommender);
            for (double value : values.get(recommender)) {
                out.print("\t" + value / nFolds);
            }
            out.println();
        }
    }

    private static Map<String, double[]> prepare_for_figure1(String inFile, String metric, int fullTargetSize) throws FileNotFoundException {
        Map<String, double[]> values = new TreeMap<>();
        Scanner in = new Scanner(new File(inFile));
        String[] colHeads = in.nextLine().split("\t");
        int colMetric = Arrays.asList(colHeads).indexOf(metric);
        while (in.hasNext()) {
            String[] colValues = in.nextLine().split("\t");
            int targetSize = Integer.parseInt(colValues[1]);
            if (targetSize != 0 && targetSize != fullTargetSize) {
                continue;
            }
            String recommender = colValues[2];

            double[] value = values.computeIfAbsent(recommender, k -> new double[2]);
            int index = (targetSize == fullTargetSize) ? 0 : 1;
            value[index] += Double.parseDouble(colValues[colMetric]);
        }
        return values;
    }

    public static void generateFigure101(
            int nFolds,
            String[] metrics,
            String[] datasets
    ) throws FileNotFoundException {

        for (String dataset : datasets) {
            String inFile = RESULTS_PATH + BIASED_PATH + dataset.replace('/', '-') + "-" + TARGET_SAMPLING_FILE;
            String outFile = RESULTS_PATH + "figure101." + dataset.replace('/', '-') + ".txt";
            PrintStream out = new PrintStream(outFile);

            int fullTargetSize = TARGET_SIZES.get(dataset);

            out.println("====================");
            out.println("Dataset: " + dataset);
            out.println("====================\n");

            for (String metric : metrics) {
                Map<String, double[]> values = prepare_for_figure1(inFile, metric, fullTargetSize);

                out.println(metric);
                out.println("Recommender\tFull\tTest");

                String[] recommenders = getSortedRecommenders(values);

                for (String recommender : recommenders) {
                    out.print(recommender);
                    for (double value : values.get(recommender)) {
                        out.print("\t" + value / nFolds);
                    }
                    out.println();
                }
                out.println();
            }
        }
    }

    private static String[] getSortedRecommenders(Map<String, double[]> values) {
        String[] recommenders = values.keySet().toArray(new String[0]);
        Arrays.sort(recommenders, Comparator.comparingInt(o -> getIndexOf(rec_ordered, o)));
        return recommenders;
    }

    public static int getIndexOf(String[] strings, String item) {
        for (int i = 0; i < strings.length; i++) {
            if (item.equals(strings[i])) return i;
        }
        return -1;
    }

    public static void generateFigure2(
            String biasedFile,
            String unbiasedFile,
            String outFile,
            int nFolds,
            String[] metrics,
            int fullTargetSize) throws FileNotFoundException {
        List<String> metricList = Arrays.asList(metrics);
        // Metric -> -> rec system -> full, unbiased, test
        Map<String, Map<String, double[]>> values = new TreeMap<>();

        //Biased file
        Scanner in = new Scanner(new File(biasedFile));
        String[] colHeads = in.nextLine().split("\t");
        for (int i = 3; i < colHeads.length; i++) {
            String metric = colHeads[i];
            if (metricList.contains(metric)) {
                values.put(metric, new TreeMap<>());
            }
        }
        while (in.hasNext()) {
            String[] colValues = in.nextLine().split("\t");
            for (int i = 3; i < colValues.length; i++) {
                String metric = colHeads[i];
                if (!metricList.contains(metric)) {
                    continue;
                }
                int targetSize = Integer.parseInt(colValues[1]);
                if (targetSize != 0 && targetSize != fullTargetSize) {
                    continue;
                }
                String recommender = colValues[2];

                double[] value = values.get(metric).computeIfAbsent(recommender, k -> new double[3]);
                int index = (targetSize == fullTargetSize) ? 0 : 2;
                value[index] += Double.parseDouble(colValues[i]);
            }
        }

        //Unbiased file
        in = new Scanner(new File(unbiasedFile));
        colHeads = in.nextLine().split("\t");
        while (in.hasNext()) {
            String[] colValues = in.nextLine().split("\t");
            for (int i = 3; i < colValues.length; i++) {
                String metric = colHeads[i];
                if (!metricList.contains(metric)) {
                    continue;
                }
                double[] value = values.get(metric).get(colValues[2]);
                value[1] += Double.parseDouble(colValues[i]);
            }
        }

        PrintStream out = new PrintStream(outFile);
        out.println("====================");
        out.println("Dataset: yahoo");
        out.println("====================");

        for (String metric : metricList) {
            out.println("\n" + metric);
            out.println("Recommender\tFull\tUnbiased\tTest");
            String[] recommenders = getSortedRecommenders(values.get(metric));
            for (String recommender : recommenders) {
                out.print(recommender);
                for (double value : values.get(metric).get(recommender)) {
                    out.print("\t" + value / nFolds);
                }
                out.println();
            }
        }
    }

    public static void generateFigure3(
            String folder,
            String[] datasets,
            String[] metrics,
            String outFile,
            int nFolds) throws FileNotFoundException {
        List<String> metricList = Arrays.asList(metrics);
        PrintStream out = new PrintStream(outFile);
        for (String dataset : datasets) {
            Map<String, Map<Integer, Map<String, Double>>> values = prepare_for_figure3(folder, metricList, dataset, out);

            for (String metric : metricList) {
                out.println(metric);
                String[] recommenders = GetSortedRecommenders(values.get(metric).get(0).keySet().toArray(new String[0]));
                PrintHeader(out, recommenders);
                PrintValues(nFolds, out, values, metric, recommenders);
            }
        }
        out.close();
    }

    private static String[] GetSortedRecommenders(String[] recommenders) {
        Arrays.sort(recommenders, (o1, o2) -> {
            final int i = Arrays.asList(rec_ordered).indexOf(o1);
            final int i1 = Arrays.asList(rec_ordered).indexOf(o2);
            return Integer.compareUnsigned(i, i1);
        });
        return recommenders;
    }

    public static void generateFigure303(
            String folder,
            String[] datasets,
            String[] metrics,
            int nFolds,
            String outFileName) throws FileNotFoundException {
        List<String> metricList = Arrays.asList(metrics);
        for (String dataset : datasets) {
            String outFile = RESULTS_PATH + outFileName + dataset.replace('/', '-') + ".txt";
            PrintStream out = new PrintStream(outFile);
            Map<String, Map<Integer, Map<String, Double>>> values = prepare_for_figure3(folder, metricList, dataset, out);

            for (String metric : metricList) {
                out.println(metric);
                String[] recommenders = getSortedMetrics(values.get(metric).get(0).keySet().toArray(new String[0]));
                PrintHeader(out, recommenders);
                PrintValues(nFolds, out, values, metric, recommenders);
            }
            out.close();
        }
    }

    private static Map<String, Map<Integer, Map<String, Double>>> prepare_for_figure3(String folder, List<String> metricList, String dataset, PrintStream out) throws FileNotFoundException {
        String inFile = folder + dataset.replace('/', '-') + "-" + TARGET_SAMPLING_FILE;
        out.println("====================");
        out.println("Dataset: " + dataset);
        out.println("====================\n");

        // Metric -> target size -> rec system -> value
        Map<String, Map<Integer, Map<String, Double>>> values = new TreeMap<>();

        Scanner in = new Scanner(new File(inFile));
        String[] colHeads = in.nextLine().split("\t");
        for (int i = 3; i < colHeads.length; i++) {
            String metric = colHeads[i];
            if (metricList.contains(metric)) {
                values.put(metric, new TreeMap<>());
            }
        }
        while (in.hasNext()) {
            String[] colValues = in.nextLine().split("\t");
            int targetSize = new Integer(colValues[1]);
            String rec = colValues[2];
            for (int i = 3; i < colValues.length; i++) {
                String metric = colHeads[i];
                if (!metricList.contains(metric)) {
                    continue;
                }
                double value = new Double(colValues[i]);
                if (!values.get(metric).containsKey(targetSize)) {
                    values.get(metric).put(targetSize, new TreeMap<>());
                }
                if (!values.get(metric).get(targetSize).containsKey(rec)) {
                    values.get(metric).get(targetSize).put(rec, 0.0);
                }
                values.get(metric).get(targetSize).put(rec, values.get(metric).get(targetSize).get(rec) + value);
            }
        }
        return values;
    }

    private static void PrintValues(int nFolds, PrintStream out, Map<String, Map<Integer, Map<String, Double>>> values, String metric, String[] recommenders) {
        for (int targetSize : values.get(metric).keySet()) {
            out.print(targetSize);

            for (String rec : recommenders) {
                double avgMetric = values.get(metric).get(targetSize).get(rec) / nFolds;
                out.print("\t" + avgMetric);
            }
            out.println();
        }
        out.println();
    }

    private static void PrintHeader(PrintStream out, String[] recommenders) {
        out.print("Target size");
        for (String rec : recommenders) {
            out.print("\t" + rec);
        }
        out.println();
    }

    private static String[] getSortedMetrics(String[] recommenders) {
        Arrays.sort(recommenders, (r1, r2) -> {
            final int x = Arrays.asList(rec_ordered).indexOf(r1);
            final int y = Arrays.asList(rec_ordered).indexOf(r2);
            return Integer.compare(x, y);
        });
        return recommenders;
    }

    public static void generateFigure4(
            String biasedFolder,
            String unbiasedFolder,
            String[] datasets,
            String[] metrics,
            String outFile, int nFolds) {
        List<Thread> threads = new ArrayList<>();
        for (String dataset : datasets) {
            Thread thread = new Thread(() -> {
                try {
                    generateFigure4_sub(biasedFolder, unbiasedFolder, metrics, outFile, nFolds, dataset);
                } catch (IOException e) {
                    System.out.println("generateFigure4" + e);
                    e.printStackTrace(System.out);
                }
            });
            thread.start();
            threads.add(thread);
        }

        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("generateFigure4 thread.join " + e);
                e.printStackTrace(System.out);
            }
        });
    }

    private static void generateFigure4_sub(String biasedFolder, String unbiasedFolder, String[] metrics, String outFile, int nFolds, String dataset) throws IOException {
        List<String> metricList = Arrays.asList(metrics);
        PrintStream out = new PrintStream(outFile + "." + dataset.replace('/', '-') + ".txt");
        out.println("====================");
        out.println("Dataset: " + dataset);
        out.println("====================\n");

        // Metric -> target size -> curve -> value
        Map<String, Map<Integer, Map<String, Double>>> values = new TreeMap<>();
        // Metric -> target size -> curve -> count
        Map<String, Map<Integer, Map<String, Integer>>> counts = new TreeMap<>();
        Set<String> curves = new TreeSet<>();

        //Ratio of ties & Ratio of ties at zero
        //curve -> input file
        Map<String, String> curveFiles = new HashMap<>();
        curveFiles.put("Ratio of ties", biasedFolder + dataset.replace('/', '-') + "-" + TIES_FILE);
        curveFiles.put("Ratio of ties at zero", biasedFolder + dataset.replace('/', '-') + "-" + TIES_AT_ZERO_FILE);
        for (String curve : curveFiles.keySet()) {
            curves.add(curve);
            String inFile = curveFiles.get(curve);
            Scanner in = new Scanner(new File(inFile));
            String[] colHeads = in.nextLine().split("\t");
            for (int i = 3; i < colHeads.length; i++) {
                String metric = colHeads[i];
                if (metricList.contains(metric) && !values.containsKey(metric)) {
                    values.put(metric, new TreeMap<>());
                    counts.put(metric, new TreeMap<>());
                }
            }
            while (in.hasNext()) {
                String[] colValues = in.nextLine().split("\t");
                int targetSize = new Integer(colValues[0]);
                for (int i = 3; i < colValues.length; i++) {
                    String metric = colHeads[i];
                    if (!metricList.contains(metric)) {
                        continue;
                    }
                    double value = new Double(colValues[i]);
                    if (!values.get(metric).containsKey(targetSize)) {
                        values.get(metric).put(targetSize, new TreeMap<>());
                        counts.get(metric).put(targetSize, new TreeMap<>());
                    }
                    if (!values.get(metric).get(targetSize).containsKey(curve)) {
                        values.get(metric).get(targetSize).put(curve, 0.0);
                        counts.get(metric).get(targetSize).put(curve, 0);
                    }
                    values.get(metric).get(targetSize).put(curve, values.get(metric).get(targetSize).get(curve) + value);
                    counts.get(metric).get(targetSize).put(curve, counts.get(metric).get(targetSize).get(curve) + 1);
                }
            }
        }

        {
            //Expected intersection ratio in top n
            String curve = "Expected intersection ratio in top n";
            curves.add(curve);
            String inFile = biasedFolder + dataset.replace('/', '-') + "-" + EXPECTED_INTERSECTION_RATIO_FILE;
            Scanner in = new Scanner(new File(inFile));
            in.nextLine();
            while (in.hasNext()) {
                String[] colValues = in.nextLine().split("\t");
                int targetSize = new Integer(colValues[1]);
                double expectation = new Double(colValues[2]);
                for (String metric : values.keySet()) {
                    if (!values.get(metric).get(targetSize).containsKey(curve)) {
                        values.get(metric).get(targetSize).put(curve, 0.0);
                        counts.get(metric).get(targetSize).put(curve, 0);
                    }
                    values.get(metric).get(targetSize).put(curve, values.get(metric).get(targetSize).get(curve) + expectation);
                    counts.get(metric).get(targetSize).put(curve, counts.get(metric).get(targetSize).get(curve) + 1);
                }
            }
        }

        //Average
        for (String metric : values.keySet()) {
            for (int targetSize : values.get(metric).keySet()) {
                for (String curve : values.get(metric).get(targetSize).keySet()) {
                    double value = values.get(metric).get(targetSize).get(curve);
                    int count = counts.get(metric).get(targetSize).get(curve);
                    values.get(metric).get(targetSize).put(curve, value / count);
                }
            }
        }

        //Curve: Correlation with unbiased evaluation
        try {
            // Metric -> rec system -> value
            Map<String, Map<String, Double>> unbiasedValues = new TreeMap<>();

            String unbiasedFiles = unbiasedFolder + dataset.replace('/', '-') + "-" + TARGET_SAMPLING_FILE;
            Scanner unbiasedIn = new Scanner(new File(unbiasedFiles));
            String[] unbiasedColHeads = unbiasedIn.nextLine().split("\t");
            for (int i = 2; i < unbiasedColHeads.length; i++) {
                String metric = unbiasedColHeads[i];
                if (metricList.contains(metric)) {
                    unbiasedValues.put(metric, new TreeMap<>());
                }
            }
            while (unbiasedIn.hasNext()) {
                String[] colValues = unbiasedIn.nextLine().split("\t");
                String rec = colValues[2];
                for (int i = 3; i < colValues.length; i++) {
                    String metric = unbiasedColHeads[i];
                    if (!metricList.contains(metric)) {
                        continue;
                    }
                    double value = new Double(colValues[i]);
                    if (!unbiasedValues.get(metric).containsKey(rec)) {
                        unbiasedValues.get(metric).put(rec, 0.0);
                    }
                    unbiasedValues.get(metric).put(rec, unbiasedValues.get(metric).get(rec) + value);
                }
            }

            // Metric -> target size -> rec system -> value
            Map<String, Map<Integer, Map<String, Double>>> biasedValues = new TreeMap<>();

            String biasedFiles = biasedFolder + dataset.replace('/', '-') + "-" + TARGET_SAMPLING_FILE;
            Scanner biasedIn = new Scanner(new File(biasedFiles));
            String[] biasedColHeads = biasedIn.nextLine().split("\t");
            for (int i = 2; i < biasedColHeads.length; i++) {
                String metric = biasedColHeads[i];
                if (metricList.contains(metric)) {
                    biasedValues.put(metric, new TreeMap<>());
                }
            }
            while (biasedIn.hasNext()) {
                String[] colValues = biasedIn.nextLine().split("\t");
                int targetSize = new Integer(colValues[1]);
                String rec = colValues[2];
                for (int i = 3; i < colValues.length; i++) {
                    String metric = biasedColHeads[i];
                    if (!metricList.contains(metric)) {
                        continue;
                    }
                    double value = new Double(colValues[i]);
                    if (!biasedValues.get(metric).containsKey(targetSize)) {
                        biasedValues.get(metric).put(targetSize, new TreeMap<>());
                    }
                    if (!biasedValues.get(metric).get(targetSize).containsKey(rec)) {
                        biasedValues.get(metric).get(targetSize).put(rec, 0.0);
                    }
                    biasedValues.get(metric).get(targetSize).put(rec, biasedValues.get(metric).get(targetSize).get(rec) + value / nFolds);
                }
            }

            //Compute kendal correlation
            String curve = "Correlation with unbiased evaluation";
            curves.add(curve);
            for (String metric : values.keySet()) {
                for (int targetSize : values.get(metric).keySet()) {
                    double kendalCorrelation = 0.0;
                    List<String> recommenders = new ArrayList<>(biasedValues.get(metric).get(targetSize).keySet());
                    int n = recommenders.size();
                    for (int i = 0; i < n; i++) {
                        String rec1 = recommenders.get(i);
                        double biasedValue1 = biasedValues.get(metric).get(targetSize).get(rec1);
                        double unbiasedValue1 = unbiasedValues.get(metric).get(rec1);
                        for (int j = i + 1; j < n; j++) {
                            String rec2 = recommenders.get(j);
                            double biasedValue2 = biasedValues.get(metric).get(targetSize).get(rec2);
                            double unbiasedValue2 = unbiasedValues.get(metric).get(rec2);
                            double diffBiased = biasedValue2 - biasedValue1;
                            double diffUnBiased = unbiasedValue2 - unbiasedValue1;
                            kendalCorrelation += Math.signum(diffBiased * diffUnBiased);
                        }
                    }
                    kendalCorrelation *= 2 * 1.0 / (n * (n - 1));
                    values.get(metric).get(targetSize).put(curve, kendalCorrelation);
                }
            }

        } catch (FileNotFoundException e) {
            //Do nothing: there is not unbiased data, as is the case for MovieLens 1M
        }

        sum_of_p_values(biasedFolder, dataset, metricList, values, counts, curves);

        for (String metric : metricList) {
            out.println(metric);
            out.print("Target size");
            for (String curve : curves) {
                out.print("\t" + curve);
            }
            out.println();
            for (int targetSize : values.get(metric).keySet()) {
                out.print(targetSize);
                for (String curve : values.get(metric).get(targetSize).keySet()) {
                    out.print("\t" + values.get(metric).get(targetSize).get(curve));
                }
                out.println();
            }
            out.println();
        }
        out.close();
    }

    private static void sum_of_p_values(String biasedFolder,
                                        String dataset,
                                        List<String> metricList,
                                        Map<String, Map<Integer, Map<String, Double>>> values,
                                        Map<String, Map<Integer, Map<String, Integer>>> counts,
                                        Set<String> curves) throws FileNotFoundException {

        // GenerateAllRecs(dataset);

        //Sum of p-values
        String curve = "Sum of p-values";
        curves.add(curve);
        String inFile = biasedFolder + dataset.replace('/', '-') + "-allrecs-pvalues.txt";
        Scanner in = new Scanner(new File(inFile));
        String[] colHeads = in.nextLine().split("\t");
        while (in.hasNext()) {
            String[] colValues = in.nextLine().split("\t");
            int targetSize = new Integer(colValues[0]);
            for (int i = 3; i < colValues.length; i++) {
                String metric = colHeads[i];
                if (!metricList.contains(metric)) {
                    continue;
                }
                double value = new Double(colValues[i]);

                if (!values.get(metric).get(targetSize).containsKey(curve)) {
                    values.get(metric).get(targetSize).put(curve, 0.0);
                    counts.get(metric).get(targetSize).put(curve, 0);
                }
                if (!Double.isNaN(value)) {
                    Double e = values.get(metric).get(targetSize).get(curve);
                    //System.out.println(curve + " " + e);
                    values.get(metric).get(targetSize).put(curve, e + value);
                }
            }
        }
    }

    public static void generateFigure5(
            String folder,
            String[] datasets,
            String[] metrics,
            String outFileName,
            int nFolds) throws IOException {

        String[] dataset_copy = Arrays.stream(datasets).toArray(String[]::new);

        for (int i = 0; i < datasets.length; i++) {
            dataset_copy[i] += "-nofill";
        }

        generateFigure303(folder, dataset_copy, metrics, nFolds, outFileName);
    }

}
