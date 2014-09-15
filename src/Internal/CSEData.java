package Internal;

import Constants.Action;
import Constants.AppConstant;
import Internal.Math.LinearRegressionModel;
import Internal.Math.MathUtils;
import Internal.Support.DrawingPanel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.List;

/**
 * This class serves to store one quarter of all student data of either CSE 142 or CSE 143. It has the capability to
 * convert all student data into JSON, graph the grade distribution of a single quarter in a bar graph, and
 * print to the console how many students got a certain grade along with a percentage of how many students got that
 * grade.
 *
 * Moreover, this class can also process two consecutive quarters where the first quarter is a CSE 142 quarter and
 * the second quarter is a CSE 143 quarter. In this configuration, both quarters can be analyzed for students who
 * take consecutive quarters. From obtaining their performance in CSE 142 and 143, correlations can be calculated,
 * a scatter plot can be formed, and the students' performances in both quarters can be written to JSON format.
 */
public class CSEData {
    private Map<Integer, Student> allData;
    private String fileTitle;
    private String year;

    // Two Quarter fields
    private Student[] cse142;
    private Student[] cse143;

    /**
     * Constructor for CSEData.
     * @param spreadSheet The spreadsheet containing all of the student data for a given quarter (CSE 142 or CSE 143).
     * @param year        The year that the quarter took place.
     */
    public CSEData(File spreadSheet, String year) {
        allData = new HashMap<Integer, Student>();
        fileTitle = spreadSheet.getName();
        this.year = year;
        createDataMap(spreadSheet);
    }

    /**
     * Constructor for CSEData. Used after a JSON file containing the CS Student data is already processed.
     * @param processedStudents The already processed students from the JSON file.
     */
    public CSEData(Map<Integer, Student> processedStudents) {
        allData = processedStudents;
        fileTitle = "none";
        year = "none";
    }

    /**
     * Constructor for CSEData. Used after a JSON file containing the CS Student data is already processed.
     * @param processedStudents The already processed students from the JSON file.
     * @param year              The year that the quarter took place.
     */
    public CSEData(Map<Integer, Student> processedStudents, String year) {
        allData = processedStudents;
        fileTitle = "none";
        this.year = year;
    }

    /**
     * Processes Actions for one quarter to be performed. Options include graphing the grade data,
     * writing out a percentage distribution to a file and the console, and parsing the grade data into a JSON file.
     * @param actions A List of Actions to be performed specified by the user.
     */
    public void processOneQuarter(List<Action> actions) {
        for (Action a : actions) {
            switch (a) {
                case GRAPH: {
                    graphGradeData();
                    break;
                }
                case PERCENTAGE_DISTRIBUTION: {
                    getGradeDistribution();
                    break;
                }
                case PARSE: {
                    writeSingleQuarterToJSON();
                    break;
                }
                case CORRELATE_ONE_QUARTER: {
                    correlateOneQuarter();
                    break;
                }
            }
        }
    }

    /**
     * Processes Actions for two quarters to be performed. Options include correlating both of the quarters and
     * printing out correlations to the console which calculates the correlations between final overall grades,
     * homework, midterm, and final exam grades.
     * @param cse143Data The other CSE 143 to be compared to.
     * @param actions    A List of Actions to be performed specified by the user.
     */
    public void processTwoQuarters(CSEData cse143Data, List<Action> actions) {
        constructStudents(constructIntersectMap(cse143Data));
        for (Action a : actions) {
            switch (a) {
                case CORRELATE_TWO_QUARTERS: {
                    processCorrelations(cse143Data);
                    break;
                }
                case GRAPH_GRADE_COMPARSION: {
                    graphCorrelationGradesTwoQuarters(cse143Data);
                    break;
                }
                case PARSE: {
                    writeBothQuartersToJSON(cse143Data);
                    break;
                }
            }
        }
    }

    /**
     * Constructs the CSE 142 and 143 students to be stored.
     * @param intersect Contains the performances of students in both 142 and 143 mapped from a code.
     */
    private void constructStudents(Map<Integer, List<Student>> intersect) {
        cse142 = new Student[intersect.size()];
        cse143 = new Student[intersect.size()];
        int index = 0;
        for (Integer code : intersect.keySet()) {
            List<Student> studentPerformance = intersect.get(code);
            cse142[index] = studentPerformance.get(AppConstant.CSE_142);
            cse143[index] = studentPerformance.get(AppConstant.CSE_143);
            index++;
        }
    }

    /**
     * Correlates homework, midterm, and final scores to the Student's final grade.
     * @return A JSONObject storing the correlations.
     */
    public JSONObject correlateOneQuarter() {
        JSONObject correlations = new JSONObject();
        List<Student> allStudents = new ArrayList<Student>();
        double[] otherCategory = new double[allData.values().size()];
        double[] grades = new double[allData.values().size()];
        try {
            for (Student s : allData.values()) {
                allStudents.add(s);
            }
            for (int i = AppConstant.HOMEWORK; i < AppConstant.TOTAL_PERCENT; i++) {
                for (int j = 0; j < allStudents.size(); j++) {
                    otherCategory[j] = allStudents.get(j).getStudentInDoubleArray()[i];
                    grades[j] = allStudents.get(j).getStudentInDoubleArray()[AppConstant.GRADE];
                }
                correlations.put(determineCategory(i), MathUtils.calculateCorrelation(otherCategory, grades));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return correlations;
    }

    /**
     * Determines which category a particular index is and converts it to a String.
     * @param index The index to be converted to a String.
     * @return      A String representing which category the index maps to.
     */
    private String determineCategory(int index) {
        switch (index) {
            case 0: {
                return "homework";
            }
            case 1: {
                return "midterm";
            }
            case 2: {
                return "final";
            }
            case 3: {
                return "total_percent";
            }
        }
        return "";
    }

    /**
     * Graphs the correlation grades between two quarters in a scatter plot.
     * @param cse143Data The other CSE 143 to be compared to.
     */
    private void graphCorrelationGradesTwoQuarters(CSEData cse143Data) {
        DrawingPanel graph = new DrawingPanel(AppConstant.GRAPH_WIDTH, AppConstant.GRAPH_HEIGHT);
        Graphics g = graph.getGraphics();
        String title;
        if (cse143Data.fileTitle.equals("none")) {
            title = "All Quarter Combined Scatter Plot";
        } else {
            title = generateTitle() + " vs. " + cse143Data.generateTitle() + " Scatter Plot";
        }
        g.drawString(title, AppConstant.ADJUSTED_GRAPH_WIDTH / 2, AppConstant.TITLE_MARGIN);
        createBothAxis(g);
        createGradeXAxis(g);
        createGradeYAxis(g);
        plotGradePoints(cse142, cse143, g);
        double[] cse142Grades = new double[cse142.length];
        double[] cse143Grades = new double[cse143.length];
        for (int i = 0; i < cse142.length; i++) {
            cse142Grades[i] = cse142[i].getGrade();
            cse143Grades[i] = cse143[i].getGrade();
        }
        createLineBestFit(cse142Grades, cse143Grades, g);
    }

    /**
     * Creates a line of best fit for the graph.
     * @param cse142Grades The final overall grades of students in CSE 142.
     * @param cse143Grades The final overall grades of students in CSE 143.
     * @param g            The graphics used to draw the line.
     */
    private void createLineBestFit(double[] cse142Grades, double[] cse143Grades, Graphics g) {
        LinearRegressionModel l = new LinearRegressionModel(cse142Grades, cse143Grades);
        l.compute(); // compute coefficients
        double[] coeff = l.getCoefficients();
        double b = coeff[0];
        double mx = 1 - coeff[1];
        int x1 = AppConstant.GRAPH_MARGIN;
        int y1 = AppConstant.ADJUSTED_GRAPH_HEIGHT - (int) (b * AppConstant.Y_LABEL_SPACING);
        int x2 = AppConstant.GRAPH_WIDTH;
        int y2 = (int) (AppConstant.Y_LABEL_SPACING * 40 * mx) - (int) (b * AppConstant.Y_LABEL_SPACING);
        g.setColor(Color.BLACK);
        g.drawLine(x1, y1, x2, y2);
        double roundedMx = MathUtils.roundNPlaces(coeff[1], 2);
        double roundedB = MathUtils.roundNPlaces(coeff[0], 2);
        g.drawString("y = " + roundedMx + "x " + " + " + roundedB, AppConstant.GRAPH_MARGIN * 2,
                AppConstant.GRAPH_MARGIN);
        g.drawString("Pearson's Correlation: " + MathUtils.roundNPlaces(MathUtils.calculateCorrelation(cse142Grades,
                cse143Grades), 2), AppConstant.GRAPH_MARGIN * 2, AppConstant.GRAPH_MARGIN * 2);
        g.drawString("Total Students Taking Consecutive Quarters: " + cse142.length, AppConstant.GRAPH_MARGIN * 2,
                AppConstant.GRAPH_MARGIN * 3);
    }

    /**
     * Plots the individual points on the graph.
     * @param cse142 The CSE 142 student performance.
     * @param cse143 The CSE 143 student performance.
     * @param g      The graphics used to draw the line.
     */
    private void plotGradePoints(Student[] cse142, Student[] cse143, Graphics g) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < cse142.length; i++) {
            double cse142Grade = cse142[i].getGrade();
            double cse143Grade = cse143[i].getGrade();
            int instances = 0;
            for (int j = 0; j < cse142.length; j++) {
                if (cse142[j].getGrade() == cse142Grade && cse143[j].getGrade() == cse143Grade) {
                    instances++;
                }
            }
            int xShift = AppConstant.X_LABEL_SPACING + ((int) (cse142Grade * 10) * AppConstant.X_LABEL_SPACING) + AppConstant.GRAPH_MARGIN;
            int yShift = AppConstant.ADJUSTED_GRAPH_HEIGHT - (int) (cse143Grade * 10 * AppConstant.Y_LABEL_SPACING);
            g.fillOval(xShift, yShift, AppConstant.POINT_SIZE * instances, AppConstant.POINT_SIZE * instances);
        }
    }

    /**
     * Prints out correlations both to a file and the console.
     * @param cse143Data The other CSE 143 data to be compared to.
     */
    private void processCorrelations(CSEData cse143Data) {
        String fileName;
        if (cse143Data.fileTitle.equals("none")){
            fileName = "CSCorrelationData/allcorrelationdata.txt";
        } else {
            checkIfDirExists("CSCorrelationData/", year);
            fileName = "CSCorrelationData/" + year + "/" + createCombinedQuarterTitle(cse143Data) + ".txt";
        }
        File correlationFile = new File(fileName);
        try {
            PrintStream p = new PrintStream(correlationFile);
            String header;
            if (cse143Data.fileTitle.equals("none")){
                header = "All Students Correlations";
            } else {
                header = this.generateTitle() + " and " + cse143Data.generateTitle();
            }
            p.println(header);
            p.println("Total students taking coinnsecutive quarters: " + cse142.length);
            p.println();
            System.out.println(header);
            System.out.println("Total students taking consecutive quarters: " + cse142.length);
            System.out.println();
            List<Double[]> decomposedStudent142 = new ArrayList<Double[]>();
            List<Double[]> decomposedStudent143 = new ArrayList<Double[]>();
            for (int i = 0; i < cse142.length; i++) {
                decomposedStudent142.add(cse142[i].getStudentInDoubleArray());
                decomposedStudent143.add(cse143[i].getStudentInDoubleArray());
            }
            double[] processCSE142 = new double[cse142.length];
            double[] processCSE143 = new double[cse143.length];
            for (int j = 0; j < AppConstant.TOTAL_CATEGORIES; j++) {
                for (int k = 0; k < decomposedStudent142.size(); k++) {
                    processCSE142[k] = decomposedStudent142.get(k)[j];
                    processCSE143[k] = decomposedStudent143.get(k)[j];
                }
                if (j != AppConstant.TOTAL_PERCENT) {
                    String correlationMsg = AppConstant.CATEGORY_TYPES[j] + " correlation: " + MathUtils
                            .calculateCorrelation(processCSE142, processCSE143);
                    System.out.println(correlationMsg);
                    p.println(correlationMsg);
                }
            }
            p.flush();
            p.close();
            System.out.println();
            System.out.println("This information is also available at " + fileName + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Writes both quarters to an external file in JSON format. The resulting file will be stored in the CSDataJSON
     * directory in either AllQuarterStats or ComparingQuarters depending on whether or not this object was constructed
     * from JSON or from the raw CS spreadsheet file. The students' 142 and 143 performances will both be written in to
     * JSON format.
     * @param cse143Data The other CSE 143 to be compared to.
     */
    private void writeBothQuartersToJSON(CSEData cse143Data) {
        String fileName;
        if (cse143Data.fileTitle.equals("none")){
            fileName = "CSDataJSON/AllQuarterStats/combinedQuarterStats.json";
        } else {
            checkIfDirExists("CSDataJSON/ComparingQuarters/", year);
            fileName = "CSDataJSON/ComparingQuarters/" + year + "/" + createCombinedQuarterTitle(cse143Data) + ".json";
        }
        File jsonToWrite = new File(fileName);
        try {
            PrintStream p = new PrintStream(jsonToWrite);
            JSONObject file = new JSONObject();
            JSONArray allStudents = new JSONArray();
            for (int i = 0; i < cse142.length; i++) {
                JSONObject student = new JSONObject();
                student.put("142", cse142[i].getJSONObject());
                student.put("143", cse143[i].getJSONObject());
                allStudents.put(student);
            }
            file.put("all_students", allStudents);
            double[] cse142Grades = new double[cse142.length];
            double[] cse143Grades = new double[cse143.length];
            for (int j = 0; j < cse142.length; j++) {
                cse142Grades[j] = cse142[j].getGrade();
                cse143Grades[j] = cse143[j].getGrade();
            }
            file.put("grade_correlation", MathUtils.calculateCorrelation(cse142Grades, cse143Grades));
            p.println(file.toString());
            p.flush();
            p.close();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished creating JSON! The file is available at " + fileName + ".");
    }

    /**
     * Creates a combined quarter title for file naming purposes.
     * @param cse143Data The other CSE 143 to be compared to.
     * @return           A string representing the quarter title for the file or graph name.
     */
    private String createCombinedQuarterTitle(CSEData cse143Data) {
        String total = "";
        int start142 = fileTitle.indexOf("2"); // 142 end
        int start143 = cse143Data.fileTitle.indexOf("3"); // 143 end
        total = fileTitle.substring(start142 + 1, fileTitle.indexOf(".")) + cse143Data.fileTitle.substring
                (start143 + 1, fileTitle.indexOf("."));
        return total;
    }


    /**
     * Finds the intersection of students that took consecutive quarters in 142 and 143.
     * @param cse143Data The other CSE 143 data to be compared to.
     * @return           A Map from integer to a list of students representing the code to a list of the same student
     *                   and their performance in 142 and 143.
     */
    private Map<Integer, List<Student>> constructIntersectMap(CSEData cse143Data) {
        Map<Integer, List<Student>> intersect = new HashMap<Integer, List<Student>>();
        for (Integer code : this.allData.keySet()) {
            if (cse143Data.allData.containsKey(code)) {
                List<Student> studentPerformance = new ArrayList<Student>();
                studentPerformance.add(this.allData.get(code));
                studentPerformance.add(cse143Data.allData.get(code));
                intersect.put(code, studentPerformance);
            }
        }
        return intersect;
    }

    /**
     * Creates a bar graph representing the distribution of grades given a single quarter.
     */
    private void graphGradeData() {
        DrawingPanel graph = new DrawingPanel(AppConstant.GRAPH_WIDTH, AppConstant.GRAPH_HEIGHT);
        Graphics g = graph.getGraphics();
        int[] grades = getGradeFrequencies();
        int frequencyTimes = (findMaxFrequency(grades) + 4) / 5; // round to the nearest multiple of 5.
        createSingleGraphAxis(g, frequencyTimes);
        graphData(g, grades, frequencyTimes);
    }

    /**
     * Prints out both to the console and to a text file located in the CSGradeDistributionData directory statistics
     * about the grade distribution given a single quarter.
     */
    private void getGradeDistribution() {
        int[] gradeFreq = getGradeFrequencies();
        int totalStudents = getTotalStudents(gradeFreq);
        String fileName = fileTitle.substring(0, fileTitle.length() - 4) + "gradestats.txt";
        checkIfDirExists("CSGradeDistributionData/", year);
        File gradeDistribution = new File("CSGradeDistributionData/" + year + "/" + fileName);
        Map<Double, Double> cutoffs = findGradeCutoffs();
        try {
            PrintStream gradeFile = new PrintStream(gradeDistribution);
            System.out.println(generateTitle());
            gradeFile.println(generateTitle());
            System.out.println("Total Students: " + totalStudents);
            gradeFile.println("Total Students: " + totalStudents);
            for (int i = 40; i >= 0; i--) {
                double percentage = gradeFreq[i] * 1.0 / totalStudents * 100;
                System.out.println(cutoffs.get(i / 10.0) + "% cutoff for a " + (i / 10.0) + ":      " + Math.round
                        (percentage * 100) / 100.0 + "% (" + gradeFreq[i] + " students)");
                gradeFile.print(cutoffs.get(i / 10.0) + "% cutoff for a " + (i / 10.0) + ":      " + Math.round(percentage *
                        100) / 100.0 + "% (" + gradeFreq[i] + " students)");
            }
            System.out.println("This information is also available at " + gradeDistribution.getPath() + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a single quarter out in JSON format to a text file. The resulting text file is stored in the CSDataJSON
     * directory under SingleQuarter.
     */
    private void writeSingleQuarterToJSON() {
        String fileName = fileTitle.substring(0, fileTitle.length() - 4) + ".json";
        checkIfDirExists("CSDataJSON/SingleQuarter", year);
        File jsonToWrite = new File("CSDataJSON/SingleQuarter/" + year + "/" + fileName);
        try {
            PrintStream printToWrite = new PrintStream(jsonToWrite);
            JSONObject jsonFile = new JSONObject();
            JSONArray allStudents = new JSONArray();
            JSONArray gradeCutoffs = new JSONArray();
            jsonFile.put("students", allStudents);
            jsonFile.put("grade_cutoffs", gradeCutoffs);
            for (Integer i : allData.keySet()) {
                allStudents.put(allData.get(i).getJSONObject());
            }
            Map<Double, Double> cutoffs = findGradeCutoffs();
            int[] frequencies = getGradeFrequencies();
            int totalStudents = getTotalStudents(frequencies);
            for (int i = 40; i >= 0; i--) {
                JSONObject grade = new JSONObject();
                grade.put(i / 10.0 + "", cutoffs.get(i / 10.0));
                double percentage = (1.0 * frequencies[i]) / totalStudents * 100;
                grade.put("percent", MathUtils.roundNPlaces(percentage, 2));
                gradeCutoffs.put(grade);
            }
            printToWrite.println(jsonFile.toString());
            printToWrite.flush();
            printToWrite.close();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished writing to JSON! The file is available at " + jsonToWrite.getPath() + ".");
    }

    /**
     * Finds the grade cutoffs for each of the grade points (0.0 - 4.0),
     * @return A Map of grades to grade cutoffs.
     */
    private Map<Double, Double> findGradeCutoffs() {
        List<Student> allStudents = new ArrayList<Student>();
        for (Integer code : allData.keySet()) {
            allStudents.add(allData.get(code));
        }
        Collections.sort(allStudents);
        double[] cutoffs = new double[41];
        for (int i = 0; i < cutoffs.length; i++) {
            cutoffs[i] = -1.0;
        }
        double cur = allStudents.get(0).getGrade();
        for (int i = 0; i < allStudents.size() - 1; i++) {
            double nextGrade = allStudents.get(i + 1).getGrade();
            if (cur > nextGrade) {
                cutoffs[(int) (cur * 10)] = allStudents.get(i + 1).getTotalScore();
                cur = nextGrade;
            }
        }
        Map<Double, Double> gradeMapping = new TreeMap<Double, Double>();
        for (int j = 0; j <= 40; j++) {
            gradeMapping.put(j / 10.0, cutoffs[j]);
        }
        return gradeMapping;
    }


    /**
     * Creates a single quarter's graph's axis. Lines the x axis with grades and the y axis with number of students in
     * increments of 5 up until the max.
     * @param g              The graphics used to draw the axis and numbers.
     * @param frequencyTimes How many increments of 5 need to be placed on the graph.
     */
    private void createSingleGraphAxis(Graphics g, int frequencyTimes) {
        createBothAxis(g);
        createGradeXAxis(g);
        createFrequencyYAxis(g, frequencyTimes);
    }

    /**
     * Creates the grade x axis from 0.0 starting on the left to 4.0 going to the right.
     * @param g The graphics used to draw the grades.
     */
    private void createGradeXAxis(Graphics g) {
        int labelYPos = AppConstant.ADJUSTED_GRAPH_HEIGHT + AppConstant.LABEL_Y_MARGIN;
        for (int i = 0; i <= 40; i++) { // 0.0 to 4.0
            int labelXPos = AppConstant.GRAPH_MARGIN + (i * AppConstant.X_LABEL_SPACING) + AppConstant.LABEL_X_MARGIN;
            g.drawString((i * 1.0) / 10 + "", labelXPos, labelYPos);
        }
    }

    /**
     * Creates the grade y axis from 0.0 starting from the bottom to 4.0 going to the top of the graph.
     * @param g The graphics used to draw the grades.
     */
    private void createGradeYAxis(Graphics g) {
        int labelXPos = AppConstant.LABEL_Y_MARGIN;
        for (int i = 0; i <= 40; i++) {
            int labelYPos = AppConstant.ADJUSTED_GRAPH_HEIGHT - (i * AppConstant.Y_LABEL_SPACING);
            g.drawString((i * 1.0) / 10 + "", labelXPos, labelYPos);
        }
    }

    /**
     * Creates the lines to make up both of the axises of any given graph.
     * @param g The graphics used to draw the lines.
     */
    private void createBothAxis(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawLine(AppConstant.GRAPH_MARGIN, 0, AppConstant.GRAPH_MARGIN, AppConstant.ADJUSTED_GRAPH_HEIGHT);
        g.drawLine(AppConstant.GRAPH_MARGIN, AppConstant.ADJUSTED_GRAPH_HEIGHT, AppConstant.GRAPH_WIDTH,
                AppConstant.ADJUSTED_GRAPH_HEIGHT);
    }

    /**
     * Creates the frequency y axis incremented by 5s.
     * @param g              The graphics used to draw the numbers.
     * @param frequencyTimes The amount of times that increments of 5 need to be drawn.
     */
    private void createFrequencyYAxis(Graphics g, int frequencyTimes) {
        for (int i = 0; i <= frequencyTimes; i++) {
            g.drawString(i * 5 + "", AppConstant.GRAPH_MARGIN - AppConstant.LABEL_X_MARGIN,
                    AppConstant.ADJUSTED_GRAPH_HEIGHT - (AppConstant.ADJUSTED_GRAPH_HEIGHT / frequencyTimes * i));
        }
    }

    /**
     * Finds the maximum frequency in an array of grades.
     * @param grades The frequency of grades.
     * @return       The maximum frequency in the array of frequency of grades.
     */
    private int findMaxFrequency(int[] grades) {
        int mostFreqGrade = 0;
        for (int i = 0; i < grades.length; i++) {
            if (mostFreqGrade < grades[i]) {
                mostFreqGrade = grades[i];
            }
        }
        return mostFreqGrade;
    }

    /**
     * Graphs the frequency of grades of students in one quarter. Displays the grade (0.0 - 4.0) on the x axis along with
     * the frequency of that particular grade on the y axis. All of this is displayed in a bar graph.
     * @param g              The graphics used to draw the axis and numbers.
     * @param grades         The frequency of grades.
     * @param frequencyTimes The amount of times that increments of 5 need to be drawn.
     */
    private void graphData(Graphics g, int[] grades, int frequencyTimes) {
        int xMargin = AppConstant.ADJUSTED_GRAPH_WIDTH / 41;
        int pixelsPerStudent = AppConstant.ADJUSTED_GRAPH_HEIGHT / (frequencyTimes * 5);
        for (int i = 0; i < grades.length; i++) {
            int xPos = AppConstant.GRAPH_MARGIN + (i * xMargin) + AppConstant.LABEL_X_MARGIN / 2;
            int yPos = AppConstant.ADJUSTED_GRAPH_HEIGHT - pixelsPerStudent * grades[i];
            int totalPixels = pixelsPerStudent * grades[i];
            g.setColor(Color.GREEN);
            g.fillRect(xPos, yPos, xMargin, totalPixels);
            g.setColor(Color.WHITE);
            g.drawLine(xPos, AppConstant.ADJUSTED_GRAPH_HEIGHT - 1, xPos, AppConstant.ADJUSTED_GRAPH_HEIGHT - totalPixels);
            g.setColor(Color.BLACK);
            if (grades[i] != 0) {
                g.drawString(grades[i] + "", xPos + AppConstant.LABEL_X_MARGIN / 2,
                        yPos + pixelsPerStudent / 2 * grades[i]);
            }
        }
        g.drawString(generateTitle(), AppConstant.ADJUSTED_GRAPH_WIDTH / 2, AppConstant.GRAPH_MARGIN / 2);
        g.drawString("Total students: " + getTotalStudents(grades), AppConstant.ADJUSTED_GRAPH_WIDTH / 2,
                AppConstant.GRAPH_MARGIN / 2 + AppConstant.TITLE_MARGIN);
    }

    /**
     * Generates a title for the graph. Displays the course (either CSE 142 or CSE 143) along with the year and quarter
     * it was taken in.
     * @return A String representing the title of the graph. If there is no file title,
     *         a simple title with the year incorporated will be returned.
     */
    private String generateTitle() {
        if (!fileTitle.equals("none")) {
            String title = fileTitle.substring(0, 3).toUpperCase() + " " + fileTitle.substring(3,
                    6) + ": " + fileTitle.substring(6, 7).toUpperCase();
            int i = 7;
            while (Character.isLetter(fileTitle.charAt(i))) {
                title += fileTitle.charAt(i);
                i++;
            }
            title += " " + fileTitle.substring(i, i + 4);
            return title;
        }
        return "All CSE " + year + " students";
    }

    /**
     * Gets the total number of students within a single quarter.
     * @param grades The frequency of student grades.
     * @return An integer representing the total number of students that took that class in that particular quarter.
     */
    private int getTotalStudents(int[] grades) {
        int total = 0;
        for (Integer i : grades) {
            total += i;
        }
        return total;
    }


    /**
     * Gets the grade frequencies from Students.
     * @return An integer array representing the given frequencies for each grade in a single quarter.
     */
    private int[] getGradeFrequencies() {
        int[] frequencies = new int[41]; // 0.0 to 4.0
        for (Student s : allData.values()) {
            frequencies[(int) (s.getGrade() * 10)]++;
        }
        return frequencies;
    }


    /**
     * Creates a mapping of data from a Student's code to a Student.
     * @param spreadSheet The spreadsheet containing the data file.
     */
    private void createDataMap(File spreadSheet) {
        try {
            Scanner spreadSheetScan = new Scanner(spreadSheet);
            spreadSheetScan.useDelimiter("\t");
            processFile(spreadSheetScan);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes the File and creates the mapping of data.
     * @param spreadSheet The spreadsheet containing the data file.
     */
    private void processFile(Scanner spreadSheet) {
        int[] categoryIndexes = processCategories(spreadSheet);
        while (spreadSheet.hasNextLine()) {
            int index, midterm, finalExam, code;
            index = midterm = finalExam = code = 0;
            double homework, grade, totalPercent;
            grade = homework = totalPercent = 0;
            String wholeLine = spreadSheet.nextLine();
            Scanner processLine = new Scanner(wholeLine);
            processLine.useDelimiter("\t");
            boolean isEndRow = false;
            while (processLine.hasNext()) {
                String entry = processLine.next();
                if (index == 0) {
                    if (entry.equals("mean") || entry.equals("median")) {
                        isEndRow = true;
                    } else {
                        code = Integer.parseInt(entry);
                    }
                } else if (index == categoryIndexes[AppConstant.HOMEWORK] && !entry.contains(" ") && !isEndRow) {
                    homework = Double.parseDouble(entry.substring(0, entry.length() - 1));
                } else if (index == categoryIndexes[AppConstant.MIDTERM] && !entry.contains(" ") && !isEndRow) {
                    midterm = Integer.parseInt(entry);
                } else if (index == categoryIndexes[AppConstant.FINAL] && !entry.contains(" ") && !isEndRow) {
                    finalExam = Integer.parseInt(entry);
                } else if (index == categoryIndexes[AppConstant.TOTAL_PERCENT] && !entry.contains(" ") && !isEndRow) {
                    totalPercent = Double.parseDouble(entry);
                } else if (index == categoryIndexes[AppConstant.GRADE] && !entry.contains("I") && !isEndRow) {
                    grade = Double.parseDouble(entry);
                }
                index++;
            }
            if (code != 0) {
                allData.put(code, new Student(code, homework, midterm, finalExam, totalPercent, grade));
            }
        }
    }

    /**
     * Processes the locations of each of the categories in the file.
     *
     * @param spreadSheet The spreadsheet containing the data file.
     * @return An array representing the indexes at which each category is located in each row of the file.
     */
    private int[] processCategories(Scanner spreadSheet) {
        int[] categoryIndex = new int[AppConstant.TOTAL_CATEGORIES];
        // homework, midterm, final, total %, and final grade
        // process header to get categories and their respective indexes
        String categories = spreadSheet.nextLine();
        Scanner processCategories = new Scanner(categories);
        processCategories.useDelimiter("\t");
        int index = 0;
        while (processCategories.hasNext()) {
            String entry = processCategories.next();
            if (entry.toLowerCase().equals("weekly %") || entry.toLowerCase().equals("homework %")) {
                categoryIndex[AppConstant.HOMEWORK] = index;
            }
            if (entry.toLowerCase().contains("mid")) {
                categoryIndex[AppConstant.MIDTERM] = index;
            }
            if (entry.toLowerCase().contains("fin")) {
                categoryIndex[AppConstant.FINAL] = index;
            }
            if (entry.toLowerCase().equals("total")) {
                categoryIndex[AppConstant.TOTAL_PERCENT] = index;
            }
            if (entry.toLowerCase().equals("grade")) {
                categoryIndex[AppConstant.GRADE] = index;
            }
            index++;
        }
        return categoryIndex;
    }

    /**
     * Checks if a given directory exists. If it doesn't exist, then a directory with that extension will be created
     * along with the appropriate year.
     * @param dir  The directory to be checked.
     * @param year The year to be checked.
     */
    private void checkIfDirExists(String dir, String year) {
        File completeDir[] = new File(dir).listFiles();
        boolean dirExists = false;
        for (File yearDir : completeDir) {
            if (yearDir.getName().equals(year)) {
                dirExists = true;
            }
        }
        if (!dirExists) {
            File yearDir = new File(dir + "/" + year);
            yearDir.mkdir();
        }
    }

}
