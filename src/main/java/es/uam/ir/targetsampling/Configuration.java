/*
 * Copyright (C) 2020 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.ir.targetsampling;

import es.uam.ir.filler.Filler;
import es.uam.ir.filler.Filler.Mode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author Rocío Cañamares
 * @author Pablo Castells
 */
public class Configuration {

    private String dataPath;
    private final String testPath;
    private final int threshold;

    private String resultsPath;

    private final int nFolds;
    private int[] targetSizes;
    private final int cutoff;
    private Mode fillMode;

    //Params when all recs
    private boolean allRecs;
    private int[] knnParamK = null;
    private int[] normKnnParamK = null;
    private int[] normKnnParamMin = null;
    private int[] imfParamK = null;
    private double[] imfParamLambda = null;
    private double[] imfParamAlpha = null;

    //kNN params
    private int knnFullParamK = -1;
    private int knnTestParamK = -1;
    private int normKnnFullParamK = -1;
    private int normKnnTestParamK = -1;
    private int normKnnFullParamMin = -1;
    private int normKnnTestParamMin = -1;

    //iMF params
    private int imfFullParamK = -1;
    private int imfTestParamK = -1;
    private double imfFullParamLambda = -1;
    private double imfTestParamLambda = -1;
    private double imfFullParamAlpha = -1;
    private double imfTestParamAlpha = -1;
    private final Properties prop;

    /**
     * @param propFileName
     * @throws IOException
     */
    public Configuration(String propFileName) throws IOException {

        prop = new Properties();

        try (InputStream inputStream = new FileInputStream(propFileName)) {
            prop.load(inputStream);

            this.dataPath = prop.getProperty("data.path");
            this.testPath = prop.getProperty("data.test.path");
            this.threshold = Integer.parseInt(prop.getProperty("data.threshold"));
            this.resultsPath = prop.getProperty("results.path");
            this.nFolds = Integer.parseInt(prop.getProperty("crossvalidation.nfolds"));
            this.cutoff = Integer.parseInt(prop.getProperty("evaluation.cutoff"));
            switch (prop.getProperty("fill.mode")) {
                case "rnd":
                    this.fillMode = Mode.RND;
                    break;
                case "gender":
                    this.fillMode = Mode.Gender;
                    break;
                default:
                    this.fillMode = Mode.NONE;
                    break;
            }

            String[] targetSizeTokens = prop.getProperty("targetselection.targetsizes").split(",");
            this.targetSizes = new int[targetSizeTokens.length];
            for (int i = 0; i < targetSizeTokens.length; i++) {
                this.targetSizes[i] = Integer.parseInt(targetSizeTokens[i]);
            }

            this.allRecs = Boolean.parseBoolean(prop.getProperty("algorithms.run.all"));
            updateOnAllRecs(prop);
        }
    }

    private void updateOnAllRecs(Properties prop) {
        if (this.allRecs) {
            this.knnParamK = Arrays.stream(prop.getProperty("algorithms.knn.k").split(",")).mapToInt(Integer::parseInt).toArray();

            this.normKnnParamK = Arrays.stream(prop.getProperty("algorithms.normknn.k").split(",")).mapToInt(Integer::parseInt).toArray();
            this.normKnnParamMin = Arrays.stream(prop.getProperty("algorithms.normknn.min").split(",")).mapToInt(Integer::parseInt).toArray();

            this.imfParamK = Arrays.stream(prop.getProperty("algorithms.imf.k").split(",")).mapToInt(Integer::parseInt).toArray();
            this.imfParamLambda = Arrays.stream(prop.getProperty("algorithms.imf.lambda").split(",")).mapToDouble(Double::parseDouble).toArray();
            this.imfParamAlpha = Arrays.stream(prop.getProperty("algorithms.imf.alpha").split(",")).mapToDouble(Double::parseDouble).toArray();
        } else {
            this.knnFullParamK = Integer.parseInt(prop.getProperty("algorithms.full.knn.k"));
            this.knnTestParamK = Integer.parseInt(prop.getProperty("algorithms.test.knn.k"));

            this.normKnnFullParamK = Integer.parseInt(prop.getProperty("algorithms.full.normknn.k"));
            this.normKnnTestParamK = Integer.parseInt(prop.getProperty("algorithms.test.normknn.k"));
            this.normKnnFullParamMin = Integer.parseInt(prop.getProperty("algorithms.full.normknn.min"));
            this.normKnnTestParamMin = Integer.parseInt(prop.getProperty("algorithms.test.normknn.min"));

            this.imfFullParamK = Integer.parseInt(prop.getProperty("algorithms.full.imf.k"));
            this.imfTestParamK = Integer.parseInt(prop.getProperty("algorithms.test.imf.k"));
            this.imfFullParamLambda = Double.parseDouble(prop.getProperty("algorithms.full.imf.lambda"));
            this.imfTestParamLambda = Double.parseDouble(prop.getProperty("algorithms.test.imf.lambda"));
            this.imfFullParamAlpha = Double.parseDouble(prop.getProperty("algorithms.full.imf.alpha"));
            this.imfTestParamAlpha = Double.parseDouble(prop.getProperty("algorithms.test.imf.alpha"));
        }
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String value) {
        dataPath = value;
    }

    public String getTestPath() {
        return testPath;
    }

    public int getThreshold() {
        return threshold;
    }

    public String getResultsPath() {
        return resultsPath;
    }

    public int getNFolds() {
        return nFolds;
    }

    public int[] getTargetSizes() {
        return targetSizes;
    }

    public int getCutoff() {
        return cutoff;
    }

    public Mode getFillMode() {
        return fillMode;
    }

    public boolean isAllRecs() {
        return allRecs;
    }

    public int[] getKnnParamK() {
        return knnParamK;
    }

    public int[] getNormKnnParamK() {
        return normKnnParamK;
    }

    public int[] getNormKnnParamMin() {
        return normKnnParamMin;
    }

    public int[] getImfParamK() {
        return imfParamK;
    }

    public double[] getImfParamLambda() {
        return imfParamLambda;
    }

    public double[] getImfParamAlpha() {
        return imfParamAlpha;
    }

    public int getKnnFullParamK() {
        return knnFullParamK;
    }

    public int getKnnTestParamK() {
        return knnTestParamK;
    }

    public int getNormKnnFullParamK() {
        return normKnnFullParamK;
    }

    public int getNormKnnTestParamK() {
        return normKnnTestParamK;
    }

    public int getNormKnnFullParamMin() {
        return normKnnFullParamMin;
    }

    public int getNormKnnTestParamMin() {
        return normKnnTestParamMin;
    }

    public int getImfFullParamK() {
        return imfFullParamK;
    }

    public int getImfTestParamK() {
        return imfTestParamK;
    }

    public double getImfFullParamLambda() {
        return imfFullParamLambda;
    }

    public double getImfTestParamLambda() {
        return imfTestParamLambda;
    }

    public double getImfFullParamAlpha() {
        return imfFullParamAlpha;
    }

    public double getImfTestParamAlpha() {
        return imfTestParamAlpha;
    }

    public void setFillMode(Mode fillMode) {
        this.fillMode = fillMode;
    }

    public void setAllRecs(boolean allRecs) {
        this.allRecs = allRecs;
        updateOnAllRecs(prop);
    }

    public void setResultsPath(String resultsPath) {
        this.resultsPath = resultsPath;
    }

    public Configuration forAll() {
        this.setAllRecs(true);
        this.resultsPath = this.resultsPath + "allrecs-";
        return this;
    }

    public Configuration forTestAndFull() {
        if (this.targetSizes.length > 2) {
            final int first = this.targetSizes[0];
            final int last = this.targetSizes[this.targetSizes.length - 1];
            this.targetSizes = new int[]{first, last};
        }

        return this;
    }

    public Configuration forNofill() {
        this.fillMode = Mode.NONE;
        this.resultsPath = this.getResultsPath() + "nofill-";
        return this;
    }
}
