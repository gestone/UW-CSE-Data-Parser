package Internal;

import Constants.Action;
import Constants.AppConstant;
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
 * Created by Justin on 7/19/2014.
 */
public class CSEData {
    private Map<Integer, Student> allData;
    private String fileTitle;
    private String year;

    /**
     * @param spreadSheet
     */
    public CSEData(File spreadSheet, String year) {
        allData = new HashMap<Integer, Student>();
        fileTitle = spreadSheet.getName();
        this.year = year;
        createDataMap(spreadSheet);
    }

    /**
     *
     * @param cse143Data
     */
    public void correlateTwoQuarters(CSEData cse143Data){
        Map<Integer, List<Student>> intersect = constructIntersectMap(cse143Data);
        Student[] cse142 = new Student[intersect.size()];
        Student[] cse143 = new Student[intersect.size()];
        int index = 0;
        for(Integer code : intersect.keySet()){
            List<Student> studentPerformance = intersect.get(code);
            // Change these categories to correlate different aspects of student performance
            cse142[index] = studentPerformance.get(AppConstant.CSE_142);
            cse143[index] = studentPerformance.get(AppConstant.CSE_143);
            index++;
        }
        processCorrelations(cse142, cse143, cse143Data);
        if(Arrays.asList(AppConstant.PROCESSING_TYPES).contains(Action.PARSE)){
            twoQuartersToJSON(cse142, cse143, cse143Data);
        }
    }

    /**
     *
     * @param cse142
     * @param cse143
     * @param cse143Data
     */
    private void processCorrelations(Student[] cse142, Student[] cse143, CSEData cse143Data){
        checkIfDirExists("CSCorrelationData/", year);
        String fileName = "CSCorrelationData/" + year + "/" + createCombinedQuarterTitle(cse143Data) + ".txt";
        File correlationFile = new File(fileName);
        try {
            PrintStream p = new PrintStream(correlationFile);
            String header = this.generateTitle() + " and " + cse143Data.generateTitle();
            p.println(header);
            p.println("Total students taking consecutive quarters: " + cse142.length);
            p.println();
            System.out.println(header);
            System.out.println("Total students taking consecutive quarters: " + cse142.length);
            System.out.println();
            List<Double[]> decomposedStudent142 = new ArrayList<Double[]>();
            List<Double[]> decomposedStudent143 = new ArrayList<Double[]>();
            for(int i = 0; i < cse142.length; i++){
                decomposedStudent142.add(cse142[i].getStudentInDoubleArray());
                decomposedStudent143.add(cse143[i].getStudentInDoubleArray());
            }
            double[] processCSE142 = new double[cse142.length];
            double[] processCSE143 = new double[cse143.length];
            for(int j = 0; j < AppConstant.TOTAL_CATEGORIES; j++){
                for(int k = 0; k < decomposedStudent142.size(); k++){
                    processCSE142[k] = decomposedStudent142.get(k)[j];
                    processCSE143[k] = decomposedStudent143.get(k)[j];
                }
                if(j != AppConstant.TOTAL_PERCENT){
                    String correlationMsg = AppConstant.CATEGORY_TYPES[j] + " correlation: " + calculateCorrelation
                            (processCSE142, processCSE143);
                    System.out.println(correlationMsg);
                    p.println(correlationMsg);
                }
            }
            p.flush();
            p.close();
            System.out.println();
            System.out.println("This information is also available at " + fileName + ".");
        } catch(IOException e){
            e.printStackTrace();
        }
    }



    /**
     *
     * @param cse142
     * @param cse143
     */
    private void twoQuartersToJSON(Student[] cse142, Student[] cse143, CSEData cse143Data){
        checkIfDirExists("CSDataJSON/ComparingQuarters/", year);
        String fileName = "CSDataJSON/ComparingQuarters/" + year + "/" + createCombinedQuarterTitle(cse143Data) + ".json";
        File jsonToWrite = new File(fileName);
        try{
            PrintStream p = new PrintStream(jsonToWrite);
            JSONArray file = new JSONArray();
            for(int i = 0; i < cse142.length; i++){
                JSONObject student = new JSONObject();
                student.put("142", cse142[i].getJSONObject());
                student.put("143", cse143[i].getJSONObject());
                file.put(student);
            }
            p.println(file.toString());
            p.flush();
            p.close();
        } catch(JSONException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Finished creating JSON! The file is available at " + fileName + ".");
    }

    /**
     *
     * @param cse143Data
     * @return
     */
    private String createCombinedQuarterTitle(CSEData cse143Data){
        String total = "";
        int start142 = fileTitle.indexOf("2"); // 142 end
        int start143 = cse143Data.fileTitle.indexOf("3"); // 143 end
        total = fileTitle.substring(start142 + 1, fileTitle.indexOf(".")) + cse143Data.fileTitle.substring
                (start143 + 1, fileTitle.indexOf("."));
        return total;
    }


    /**
     *
     * @param categoryOne
     * @param categoryTwo
     * @return
     */
    private double calculateCorrelation(double[] categoryOne, double[] categoryTwo){
        double cse142Avg = 0;
        double cse143Avg = 0;
        for(int i = 0; i < categoryOne.length; i++){
            cse142Avg += categoryOne[i];
            cse143Avg += categoryTwo[i];
        }
        cse142Avg /= categoryOne.length;
        cse143Avg /= categoryTwo.length;
        double[] cse142AColumn = new double[categoryOne.length];
        double[] cse143BColumn = new double[categoryTwo.length];
        double[] aColTimesB = new double[categoryOne.length];
        double[] aSquared = new double[categoryOne.length];
        double[] bSquared = new double[categoryTwo.length];
        for(int i = 0; i < categoryTwo.length; i++){
            cse142AColumn[i] = categoryOne[i] - cse142Avg;
            cse143BColumn[i] = categoryTwo[i] - cse143Avg;
            aColTimesB[i] = cse142AColumn[i] * cse143BColumn[i];
            aSquared[i] = cse142AColumn[i] * cse142AColumn[i];
            bSquared[i] = cse143BColumn[i] * cse143BColumn[i];
        }
        double aTimesBTotal, aSquaredTotal, bSquaredTotal;
        aTimesBTotal = aSquaredTotal = bSquaredTotal = 0;
        for(int i = 0; i < categoryTwo.length; i++){
            aTimesBTotal += aColTimesB[i];
            aSquaredTotal += aSquared[i];
            bSquaredTotal += bSquared[i];
        }
        return aTimesBTotal / Math.sqrt(aSquaredTotal * bSquaredTotal);
    }


    /**
     *
     * @param cse143Data
     * @return
     */
    private Map<Integer, List<Student>> constructIntersectMap(CSEData cse143Data){
        Map<Integer, List<Student>> intersect = new HashMap<Integer, List<Student>>();
        for(Integer code : this.allData.keySet()){
            if(cse143Data.allData.containsKey(code)){
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
    public void graphGradeData() {
        DrawingPanel graph = new DrawingPanel(AppConstant.GRAPH_WIDTH, AppConstant.GRAPH_HEIGHT);
        Graphics g = graph.getGraphics();
        int[] grades = getGradeFrequencies();
        int frequencyTimes = (findMaxFrequency(grades) + 4) / 5; // round to the nearest multiple of 5.
        createGraphAxis(g, grades, frequencyTimes);
        graphData(g, grades, frequencyTimes);
    }

    /**
     * Prints out both to the console and to a text file located in the CSGradeDistributionData directory statistics
     * about the grade distribution given a single quarter.
     */
    public void getGradeDistribution() {
        int[] gradeFreq = getGradeFrequencies();
        int totalStudents = getTotalStudents(gradeFreq);
        String fileName = fileTitle.substring(0, fileTitle.length() - 4) + "gradestats.txt";
        checkIfDirExists("CSGradeDistributionData/", year);
        File gradeDistribution = new File("CSGradeDistributionData/" + year + "/" + fileName);
        try {
            PrintStream gradeFile = new PrintStream(gradeDistribution);
            System.out.println(generateTitle());
            gradeFile.println(generateTitle());
            System.out.println("Total Students: " + totalStudents);
            gradeFile.println("Total Students: " + totalStudents);
            for(int i = 40; i >= 0; i--){
                double percentage = gradeFreq[i] * 1.0 / totalStudents * 100;
                System.out.println(i / 10.0 + ": " + Math.round(percentage * 100) / 100.0 + "% (" + gradeFreq[i] + " " +
                        "students)");
                gradeFile.println(i / 10.0 + ": " + Math.round(percentage * 100) / 100.0 + "% (" + gradeFreq[i] + " " +
                        "students)");
            }
            System.out.println("This information is also available at " + gradeDistribution.getPath() + ".");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void writeSingleQuarterToJSON() {
        String fileName = fileTitle.substring(0, fileTitle.length() - 4) + ".json";
        checkIfDirExists("CSDataJSON/SingleQuarter", year);
        File jsonToWrite = new File("CSDataJSON/SingleQuarter/" + year + "/" + fileName);
        try{
            PrintStream printToWrite = new PrintStream(jsonToWrite);
            JSONObject jsonFile = new JSONObject();
            JSONArray allStudents = new JSONArray();
            jsonFile.put("students", allStudents);
            for(Integer i : allData.keySet()){
                allStudents.put(allData.get(i).getJSONObject());
            }
            printToWrite.println(jsonFile.toString());
            printToWrite.flush();
            printToWrite.close();
        } catch(JSONException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Finished writing to JSON! The file is available at " + jsonToWrite.getPath() + ".");
    }



    /**
     *
     * @param g
     * @param grades
     */
    private void createGraphAxis(Graphics g, int[] grades, int frequencyTimes) {
        g.setColor(Color.BLACK);
        g.drawLine(AppConstant.GRAPH_MARGIN, 0, AppConstant.GRAPH_MARGIN, AppConstant.ADJUSTED_GRAPH_HEIGHT);
        g.drawLine(AppConstant.GRAPH_MARGIN, AppConstant.ADJUSTED_GRAPH_HEIGHT, AppConstant.GRAPH_WIDTH,
                AppConstant.ADJUSTED_GRAPH_HEIGHT);
        int xMargin = AppConstant.ADJUSTED_GRAPH_WIDTH / 41;
        int labelYPos = AppConstant.ADJUSTED_GRAPH_HEIGHT + AppConstant.LABEL_Y_MARGIN;
        for (int i = 0; i < grades.length; i++) { // 0.0 to 4.0
            int labelXPos = AppConstant.GRAPH_MARGIN + (i * xMargin) + AppConstant.LABEL_X_MARGIN;
            g.drawString((i * 1.0) / 10 + "", labelXPos, labelYPos);
        }
        int yMargin = AppConstant.ADJUSTED_GRAPH_HEIGHT / frequencyTimes;
        for (int i = 0; i <= frequencyTimes; i++) {
            g.drawString(i * 5 + "", AppConstant.GRAPH_MARGIN - AppConstant.LABEL_X_MARGIN,
                    AppConstant.ADJUSTED_GRAPH_HEIGHT - (yMargin * i));
        }
    }

    /**
     *
     * @param grades
     * @return
     */
    private int findMaxFrequency(int[] grades){
        int mostFreqGrade = 0;
        for(int i = 0; i < grades.length; i++){
            if(mostFreqGrade < grades[i]){
                mostFreqGrade = grades[i];
            }
        }
        return mostFreqGrade;
    }

    /**
     *
     * @param g
     * @param grades
     * @param frequencyTimes
     */
    private void graphData(Graphics g, int[] grades, int frequencyTimes){
        int xMargin = AppConstant.ADJUSTED_GRAPH_WIDTH / 41;
        int pixelsPerStudent = AppConstant.ADJUSTED_GRAPH_HEIGHT / (frequencyTimes * 5);
        for(int i = 0; i < grades.length; i++){
            int xPos = AppConstant.GRAPH_MARGIN + (i * xMargin) + AppConstant.LABEL_X_MARGIN / 2;
            int yPos = AppConstant.ADJUSTED_GRAPH_HEIGHT - pixelsPerStudent * grades[i];
            int totalPixels = pixelsPerStudent * grades[i];
            g.setColor(Color.GREEN);
            g.fillRect(xPos, yPos, xMargin, totalPixels);
            g.setColor(Color.WHITE);
            g.drawLine(xPos, AppConstant.ADJUSTED_GRAPH_HEIGHT - 1, xPos, AppConstant.ADJUSTED_GRAPH_HEIGHT - totalPixels);
            g.setColor(Color.BLACK);
            if(grades[i] != 0){
                g.drawString(grades[i] + "", xPos + AppConstant.LABEL_X_MARGIN / 2,
                        yPos + pixelsPerStudent / 2 * grades[i]);
            }
        }
        g.drawString(generateTitle(), AppConstant.ADJUSTED_GRAPH_WIDTH / 2, AppConstant.GRAPH_MARGIN / 2);
        g.drawString("Total students: " + getTotalStudents(grades), AppConstant.ADJUSTED_GRAPH_WIDTH / 2,
                AppConstant.GRAPH_MARGIN / 2 + AppConstant.TITLE_MARGIN);
    }

    /**
     *
     * @return
     */
    private String generateTitle(){
        String title = fileTitle.substring(0, 3).toUpperCase() + " " + fileTitle.substring(3,
                6) + ": " + fileTitle.substring(6, 7).toUpperCase();
        int i = 7;
        while(Character.isLetter(fileTitle.charAt(i))){
            title += fileTitle.charAt(i);
            i++;
        }
        title += " " + fileTitle.substring(i, i + 4);
        return title;
    }

    /**
     *
     * @param grades
     * @return
     */
    private int getTotalStudents(int[] grades){
        int total = 0;
        for(Integer i : grades){
            total += i;
        }
        return total;
    }


    /**
     *
     * @return
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
     *
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
     *
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
            if (entry.equals("weekly %")) {
                categoryIndex[AppConstant.HOMEWORK] = index;
            }
            if (entry.contains("mid")) {
                categoryIndex[AppConstant.MIDTERM] = index;
            }
            if (entry.contains("fin")) {
                categoryIndex[AppConstant.FINAL] = index;
            }
            if (entry.equals("total")) {
                categoryIndex[AppConstant.TOTAL_PERCENT] = index;
            }
            if (entry.equals("grade")) {
                categoryIndex[AppConstant.GRADE] = index;
            }
            index++;
        }
        return categoryIndex;
    }

    /**
     *
     * @param dir
     * @param year
     */
    private void checkIfDirExists(String dir, String year){
        File completeDir[] = new File(dir).listFiles();
        boolean dirExists = false;
        for(File yearDir : completeDir){
            if(yearDir.getName().equals(year)){
                dirExists = true;
            }
        }
        if(!dirExists){
            File yearDir = new File(dir + "/" + year);
            yearDir.mkdir();
        }
    }

}
