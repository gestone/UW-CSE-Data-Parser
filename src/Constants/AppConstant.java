package Constants;

/**
 * Created by Justin on 7/19/2014.
 */
public class AppConstant {

    /** Change this to change what the program does.*/

    public static final Action[] PROCESSING_TYPES = {Action.CORRELATE, Action.PARSE};

    /** All category types */
    public static final int HOMEWORK = 0;

    public static final int MIDTERM = 1;

    public static final int FINAL = 2;

    public static final int TOTAL_PERCENT = 3;

    public static final int GRADE = 4;

    public static final int TOTAL_CATEGORIES = 5;

    public static final String[] CATEGORY_TYPES = {"Homework", "Midterm", "Final", "Total Percent", "Grade"};


    /**All graphing constants */
    public static final int GRAPH_WIDTH = 1300;

    public static final int GRAPH_HEIGHT = 500;

    public static final int GRAPH_MARGIN = 50;

    public static final int LABEL_Y_MARGIN = 20;

    public static final int LABEL_X_MARGIN = 20;

    public static final int ADJUSTED_GRAPH_HEIGHT = GRAPH_HEIGHT - GRAPH_MARGIN;

    public static final int ADJUSTED_GRAPH_WIDTH = GRAPH_WIDTH - GRAPH_MARGIN;

    public static final int TITLE_MARGIN = 20;

    /**All correlation constants */
    public static final int CSE_142 = 0;

    public static final int CSE_143 = 1;



}
