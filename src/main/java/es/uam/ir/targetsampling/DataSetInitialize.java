package es.uam.ir.targetsampling;

/**
 * @author Rocío Cañamares
 * @author Pablo Castells
 */
public class DataSetInitialize {
    public final static String DATASETS_PATH = "datasets/";
    /*
     *
     * ML1M
     *
     * */
    public final static String ML1M = "ml1m";

    public final static String ML1M_PATH = DATASETS_PATH + ML1M + "/";
    public final static String ORIGINAL_ML1M_DATASET_PATH = ML1M_PATH + "ratings.dat";
    public final static String PREPROCESSED_ML1M_DATASET_PATH = ML1M_PATH + "data.txt";
    public final static String ML1M_BIASED_PROPERTIES_FILE = "conf/ml1m-biased.properties";

    /*
     *
     * ML25M
     *
     * */
    public final static String ML25M = "ml25m";
    public final static String ML25M_PATH = DATASETS_PATH + ML25M + "/";
    public final static String ORIGINAL_ML25M_DATASET_PATH = ML25M_PATH + "ratings.csv";
    public final static String PREPROCESSED_ML25M_DATASET_PATH = ML25M_PATH + "data.txt";
    public final static String ML25M_BIASED_PROPERTIES_FILE = "conf/ml25m-biased.properties";

    /*
     *
     * ML10M
     *
     * */
    public final static String ML10M = "ml10m";
    public final static String ML10M_PATH = DATASETS_PATH + ML10M + "/";
    public final static String ORIGINAL_ML10M_DATASET_PATH = ML10M_PATH + "ratings.dat";
    public final static String PREPROCESSED_ML10M_DATASET_PATH = ML10M_PATH + "data.txt";
    public final static String ML10M_BIASED_PROPERTIES_FILE = "conf/ml10m-biased.properties";

    /*
     *
     * ML100K
     *
     * */
    public final static String ML100K = "ml100k";
    public final static String ML100K_PATH = DATASETS_PATH + ML100K + "/";
    public final static String ORIGINAL_ML100K_DATASET_PATH = ML100K_PATH + "u.data";
    public final static String PREPROCESSED_ML100K_DATASET_PATH = ML100K_PATH + "data.txt";
    public final static String ML100K_BIASED_PROPERTIES_FILE = "conf/ml100k-biased.properties";
    /*
     *
     * YAHOO
     *
     * */
    public final static String YAHOO = "yahoo";
    public final static String YAHOO_PATH = DATASETS_PATH + YAHOO + "/";
    public final static String ORIGINAL_YAHOO_TRAIN_DATASET_PATH = YAHOO_PATH + "ydata-ymusic-rating-study-v1_0-train.txt";
    public final static String ORIGINAL_YAHOO_TEST_DATASET_PATH = YAHOO_PATH + "ydata-ymusic-rating-study-v1_0-test.txt";
    public final static String PREPROCESSED_YAHOO_TRAIN_DATASET_PATH = YAHOO_PATH + "data.txt";
    public final static String PREPROCESSED_YAHOO_TEST_DATASET_PATH = YAHOO_PATH + "unbiased-test.txt";

    public final static String YAHOO_BIASED_PROPERTIES_FILE = "conf/yahoo-biased.properties";
    public final static String YAHOO_UNBIASED_PROPERTIES_FILE = "conf/yahoo-unbiased.properties";

    /*
     *
     * POPULAR
     *
     * */
    public final static String ML100K_POPULAR = "ml100k/popular";
    public final static String ML100K_POPULAR_BIASED_PROPERTIES_FILE = "conf/sampler/popular/ml100k-biased.properties";
    public final static String ML1M_POPULAR = "ml1m/popular";
    public final static String ML1M_POPULAR_BIASED_PROPERTIES_FILE = "conf/sampler/popular/ml1m-biased.properties";
    public final static String ML10M_POPULAR = "ml10m/popular";
    public final static String ML10M_POPULAR_BIASED_PROPERTIES_FILE = "conf/sampler/popular/ml10m-biased.properties";
    public final static String YAHOO_POPULAR_BIASED_PROPERTIES_FILE = "conf/sampler/popular/yahoo-biased.properties";
    public final static String YAHOO_POPULAR_UNBIASED_PROPERTIES_FILE = "conf/sampler/popular/yahoo-unbiased.properties";
    /*
     *
     * UNPOPULAR
     *
     * */
    public final static String ML100K_UNPOPULAR = "ml100k/unpopular";
    public final static String ML100K_UNPOPULAR_BIASED_PROPERTIES_FILE = "conf/sampler/unpopular/ml100k-biased.properties";
    public final static String ML1M_UNPOPULAR = "ml1m/unpopular";
    public final static String ML1M_UNPOPULAR_BIASED_PROPERTIES_FILE = "conf/sampler/unpopular/ml1m-biased.properties";
    public final static String ML10M_UNPOPULAR = "ml10m/unpopular";
    public final static String ML10M_UNPOPULAR_BIASED_PROPERTIES_FILE = "conf/sampler/unpopular/ml10m-biased.properties";
    public final static String YAHOO_UNPOPULAR_BIASED_PROPERTIES_FILE = "conf/sampler/unpopular/yahoo-biased.properties";
    public final static String YAHOO_UNPOPULAR_UNBIASED_PROPERTIES_FILE = "conf/sampler/unpopular/yahoo-unbiased.properties";

    /*
     *
     * Ml1M_MALE
     *
     * */

    public final static String ML1M_MALE = "ml1m/male";
    public final static String ML1M_MALE_PATH = DATASETS_PATH + ML1M_MALE + "/";
    public final static String PREPROCESSED_ML1M_MALE_DATASET_PATH = ML1M_MALE_PATH + "data.txt";
    public final static String ML1M_MALE_BIASED_PROPERTIES_FILE = "conf/_old/ml1m-male-biased.properties";

    /*
     *
     * ML1M_FEMALE
     *
     * */
    public final static String ML1M_FEMALE = "ml1m/female";
    public final static String ML1M_FEMALE_PATH = DATASETS_PATH + ML1M_FEMALE + "/";
    public final static String PREPROCESSED_ML1M_FEMALE_DATASET_PATH = ML1M_FEMALE_PATH + "data.txt";
    public final static String ML1M_FEMALE_BIASED_PROPERTIES_FILE = "conf/_old/ml1m-female-biased.properties";

    /*
     *
     * Ml1M_YOUNG
     *
     * */

    public final static String ML1M_YOUNG = "ml1m/young";
    public final static String ML1M_YOUNG_PATH = DATASETS_PATH + ML1M_YOUNG + "/";
    public final static String PREPROCESSED_ML1M_YOUNG_DATASET_PATH = ML1M_YOUNG_PATH + "data.txt";
    public final static String ML1M_YOUNG_BIASED_PROPERTIES_FILE = "conf/_old/ml1m-young-biased.properties";

    /*
     *
     * ML1M_OLD
     *
     * */
    public final static String ML1M_OLD = "ml1m/old";
    public final static String ML1M_OLD_PATH = DATASETS_PATH + ML1M_OLD + "/";
    public final static String PREPROCESSED_ML1M_OLD_DATASET_PATH = ML1M_OLD_PATH + "data.txt";
    public final static String ML1M_OLD_BIASED_PROPERTIES_FILE = "conf/_old/ml1m-old-biased.properties";


    /*
     *
     * ML100K_MALE
     *
     * */

    public final static String ML100K_MALE = "ml100k/male";
    public final static String ML100K_MALE_PATH = DATASETS_PATH + ML100K_MALE + "/";
    public final static String PREPROCESSED_ML100K_MALE_DATASET_PATH = ML100K_MALE_PATH + "data.txt";
    public final static String ML100K_MALE_BIASED_PROPERTIES_FILE = "conf/_old/ml100k-male-biased.properties";

    /*
     *
     * ML100K_FEMALE
     *
     * */
    public final static String ML100K_FEMALE = "ml100k/female";
    public final static String ML100K_FEMALE_PATH = DATASETS_PATH + ML100K_FEMALE + "/";
    public final static String PREPROCESSED_ML100K_FEMALE_DATASET_PATH = ML100K_FEMALE_PATH + "data.txt";
    public final static String ML100K_FEMALE_BIASED_PROPERTIES_FILE = "conf/_old/ml100k-female-biased.properties";

    /*
     *
     * ML100K_YOUNG
     *
     * */

    public final static String ML100K_YOUNG = "ml100k/young";
    public final static String ML100K_YOUNG_PATH = DATASETS_PATH + ML100K_YOUNG + "/";
    public final static String PREPROCESSED_ML100K_YOUNG_DATASET_PATH = ML100K_YOUNG_PATH + "data.txt";
    public final static String ML100K_YOUNG_BIASED_PROPERTIES_FILE = "conf/_old/ml100k-young-biased.properties";

    /*
     *
     * ML100K_OLD
     *
     * */
    public final static String ML100K_OLD = "ml100k/old";
    public final static String ML100K_OLD_PATH = DATASETS_PATH + ML100K_OLD + "/";
    public final static String PREPROCESSED_ML100K_OLD_DATASET_PATH = ML100K_OLD_PATH + "data.txt";
    public final static String ML100K_OLD_BIASED_PROPERTIES_FILE = "conf/_old/ml100k-old-biased.properties";


    /*
     * Misc
     *
     * */
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

}
