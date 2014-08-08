package Constants;

/**
 * Created by Justin on 7/19/2014.
 */
public class AppConstant {

    /** All CSEDataMain Constants */

    public static final String CS_RAW_DATA_DIR = "CSRawData/";

    public static final String CS_JSON_DATA_DIR = "CSDataJSON/";

    public static final Action[] PROCESSING_TYPES = {Action.CORRELATE, Action.GRAPH_GRADE_COMPARSION};

    /** All category types */
    public static final int HOMEWORK = 0;

    public static final int MIDTERM = 1;

    public static final int FINAL = 2;

    public static final int TOTAL_PERCENT = 3;

    public static final int GRADE = 4;

    public static final int TOTAL_CATEGORIES = 5;

    public static final String[] CATEGORY_TYPES = {"Homework", "Midterm", "Final", "Total Percent", "Grade"};


    /**All graphing constants */
    public static final int GRAPH_WIDTH = 1250;

    public static final int GRAPH_HEIGHT = 900;

    public static final int GRAPH_MARGIN = 50;

    public static final int LABEL_Y_MARGIN = 20;

    public static final int LABEL_X_MARGIN = 20;

    public static final int ADJUSTED_GRAPH_HEIGHT = GRAPH_HEIGHT - GRAPH_MARGIN; // Used for drawing graph axis

    public static final int ADJUSTED_GRAPH_WIDTH = GRAPH_WIDTH - GRAPH_MARGIN; // Used for drawing graph axis

    public static final int TITLE_MARGIN = 20;

    public static final int POINT_SIZE = 3; // pixels the point size for a single student should be

    public static final int X_LABEL_SPACING = ADJUSTED_GRAPH_WIDTH / 41;

    public static final int Y_LABEL_SPACING = ADJUSTED_GRAPH_HEIGHT / 41;

    /**All correlation constants */
    public static final int CSE_142 = 0;

    public static final int CSE_143 = 1;



}
