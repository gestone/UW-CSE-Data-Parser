import Constants.Action;
import Constants.AppConstant;
import Internal.CSEData;
import Internal.CSEDataJSONParser;
import Internal.Math.MathUtils;
import Internal.Student;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

/**
 * Used for generating single quarter JSON files from raw single quarter .txt files along with conducting post-processing
 * for JSON comparison files with two quarters and for a single quarter. With regards post-processing for a single
 * quarter, the average grade cutoffs and the percentage distribution of each grade will be calculated and stored in a
 * JSON file. In post-processing for two quarters, all the overlapping student data will be collected, a scatter plot
 * graph will be displayed, and a JSON file containing this information will also be written to the CSDataJSON directory.
 *
 * This class differs from the CSEDataMain class as it does not prompt the user to choose what Actions will be performed
 * on the files.
 */
public class ProcessAllFilesMain {

    public static void main(String[] args) {
        processCutoffSingleQuarterFiles();
        // uncomment these methods below to parse.
//        processComparisonJSON();
//        combineAndCorrelateSingleQuarterFiles("142");
//        combineAndCorrelateSingleQuarterFiles("143");
    }

    /**
     * Processes all comparison JSON files and combines them into one, correlates, and graphs a scatter plot of all of
     * the grade comparisons.
     */
    public static void processComparisonJSON() {
        Map<Integer, List<Student>> allStudents = new HashMap<Integer, List<Student>>();
        File[] compareDir = new File(AppConstant.CS_JSON_DATA_DIR + "ComparingQuarters").listFiles();
        for (File year : compareDir) {
            File[] compareFile = year.listFiles();
            CSEDataJSONParser parse = new CSEDataJSONParser();
            parse.parseTwoQuarterJSON(compareFile[0]);
            allStudents.putAll(parse.getTwoQuarterMap());
        }
        Map<Integer, Student> cse142 = new HashMap<Integer, Student>();
        Map<Integer, Student> cse143 = new HashMap<Integer, Student>();
        for (Integer code : allStudents.keySet()) {
            List<Student> bothQuarters = allStudents.get(code);
            cse142.put(code, bothQuarters.get(AppConstant.CSE_142));
            cse143.put(code, bothQuarters.get(AppConstant.CSE_143));
        }
        CSEData allCS142 = new CSEData(cse142);
        CSEData allCS143 = new CSEData(cse143);
        List<Action> actions = new ArrayList<Action>();
        actions.add(Action.CORRELATE_TWO_QUARTERS);
        actions.add(Action.PARSE);
        actions.add(Action.GRAPH_GRADE_COMPARSION);
        allCS142.processTwoQuarters(allCS143, actions);
    }

    /**
     * Processes all single quarter files by generating a JSON file with calculated cutoff minimums, maximums, and
     * averages, and average grade distributions.
     */
    public static void processCutoffSingleQuarterFiles() {
        List<Action> parse = new ArrayList<Action>();
        parse.add(Action.PARSE);
        File[] csDir = new File(AppConstant.CS_RAW_DATA_DIR).listFiles();
        for (File year : csDir) {
            File[] csRawData = year.listFiles();
            for (File spreadsheet : csRawData) {
                System.out.println("Processing " + spreadsheet.getName());
                CSEData rawDataFile = new CSEData(spreadsheet, year.getName());
                rawDataFile.processOneQuarter(parse);
            }
        }
        calculateCutoffsAndDistribution();
        System.out.println("Processing complete!");
    }

    /**
     * Combines all of the single quarter JSON Files along with correlating homework, midterm,
     * and final scores against grades.
     * @param quarter The quarter to be obtained from the CS JSON files, either CSE 142 or CSE 143.
     */
    public static void combineAndCorrelateSingleQuarterFiles(String quarter){
        List<Action> allActions = new ArrayList<Action>();
        Map<Integer, Student> allStudents = new HashMap<Integer, Student>();
        CSEDataJSONParser dataParser = new CSEDataJSONParser();
        File[] csDir = new File(AppConstant.CS_JSON_DATA_DIR + "SingleQuarter").listFiles();
        for (File year : csDir) {
            File[] allYears = year.listFiles();
            for (File jsonFile : allYears) {
                if (jsonFile.getName().contains(quarter)) {
                    dataParser.parseSingleQuarterJSON(jsonFile);
                    allStudents.putAll(dataParser.getSingleQuarterMap());
                    System.out.println(jsonFile.getName() + "finished being processed!");
                }
            }
        }
        File allSingleProcessedData = new File(AppConstant.CS_JSON_DATA_DIR + "AllQuarterStats/all_" + quarter + "_" +
                "students.json");
        CSEData allData = new CSEData(allStudents, quarter);
        JSONObject totalFile = new JSONObject();
        try {
            totalFile.put("all_students", new JSONObject(allStudents));
            totalFile.put("correlations", allData.correlateOneQuarter());
            PrintStream p = new PrintStream(allSingleProcessedData);
            p.println(totalFile);
            p.flush();
            p.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }
        System.out.println("Processing complete! The file is available at " + allSingleProcessedData.getPath());
    }

    /**
     * Calculates the average, min, and max cutoffs and average grade distributions for all processed quarters.
     */
    public static void calculateCutoffsAndDistribution() {
        try {
            JSONObject file = new JSONObject();
            JSONArray cse142 = new JSONArray();
            JSONArray cse143 = new JSONArray();
            Map<Double, List<Double>> cutOffs142 = getMapping("cse142", false);
            Map<Double, List<Double>> cutOffs143 = getMapping("cse143", false);
            Map<Double, List<Double>> percent142 = getMapping("cse142", true);
            Map<Double, List<Double>> percent143 = getMapping("cse143", true);
            file.put("cse142", cse142);
            file.put("cse143", cse143);
            for (int i = 0; i < 41; i++) {
                double curGrade = i / 10.0;
                List<Double> cutOff142CurList = cutOffs142.get(curGrade);
                List<Double> cutOff143CurList = cutOffs143.get(curGrade);
//                Collections.sort(cutOff142CurList);
//                Collections.sort(cutOff143CurList);
                JSONObject cse142GradeData = new JSONObject();
                cse142GradeData.put(curGrade + "_avg_cutoff", findAverage(cutOff142CurList));
                cse142GradeData.put(curGrade + "_min_cutoff", Collections.min(cutOff142CurList));
                cse142GradeData.put(curGrade + "_max_cutoff", Collections.max(cutOff142CurList));
                cse142GradeData.put("all_cutoffs", cutOff142CurList);
                cse142GradeData.put("percent", findAverage(percent142.get(curGrade)));
                JSONObject cse143GradeData = new JSONObject();
                cse143GradeData.put(curGrade + "_avg_cutoff", findAverage(cutOff143CurList));
                cse143GradeData.put(curGrade + "_min_cutoff", Collections.min(cutOff143CurList));
                cse143GradeData.put(curGrade + "_max_cutoff", Collections.max(cutOff143CurList));
                cse143GradeData.put("all_cutoffs", cutOff143CurList);
                cse143GradeData.put("percent", findAverage(percent143.get(curGrade)));
                cse142.put(cse142GradeData);
                cse143.put(cse143GradeData);
            }
            File allSingleProcessedData = new File(AppConstant.CS_JSON_DATA_DIR +
                    "AllQuarterStats/single_quarter_cutoff_and_distribution.json");
            PrintStream p = new PrintStream(allSingleProcessedData);
            p.print(file.toString());
            p.flush();
            p.close();
            System.out.println("Success! Your processed file is available at " + allSingleProcessedData.getPath());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds the average of all the elements in a list of doubles.
     * @param list  The list of doubles containing either cutoffs or percentage distributions.
     * @return      A double representing the average of all of the elements.
     */
    public static double findAverage(List<Double> list){
        double total = 0;
        for (Double d : list){
            total += d;
        }
        return MathUtils.roundNPlaces(total / list.size(), 2);
    }

    /**
     * Groups all CSE 142 and CSE 143 cutoffs and percentages in a single map to be analyzed.
     * @param quarterType The specified quarter that is to be analyzed, either CSE 142 or CSE 143.
     * @param isPercent   Whether or not percentages are being calculated.
     * @return            A map from double to a list of doubles representing
     */
    public static Map<Double, List<Double>> getMapping(String quarterType, boolean isPercent) {
        Map<Double, List<Double>> cutOffs = new TreeMap<Double, List<Double>>();
        List<File> csFiles = getCSFiles(quarterType);
        for (File year : csFiles) {
            try {
                JSONObject cur = new JSONObject(readFile(year.getPath()));
                JSONArray gradeCutoffs = cur.getJSONArray("grade_cutoffs");
                for (int i = 0; i < gradeCutoffs.length(); i++) {
                    JSONObject gradeData = gradeCutoffs.getJSONObject(i);
                    double grade = (40 - i) / 10.0;
                    if (!cutOffs.containsKey((grade))) {
                        cutOffs.put(grade, new ArrayList<Double>());
                    }
                    if (!isPercent) {
                        double cutoff = gradeData.getDouble(grade + "");
                        cutOffs.get(grade).add(cutoff);
                    } else {
                        double percentage = gradeData.getDouble("percent");
                        cutOffs.get(grade).add(percentage);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cutOffs;
    }

    /**
     * Gets all of the CS JSON files of a given quarter.
     * @param quarter The quarter to be obtained from the CS JSON files, either CSE 142 or CSE 143.
     * @return        A list of Files representing all of the CS files of a given quarter.
     */
    public static List<File> getCSFiles(String quarter) {
        List<File> csFiles = new ArrayList<File>();
        File[] allYears = new File(AppConstant.CS_JSON_DATA_DIR + "SingleQuarter").listFiles();
        for (File year : allYears) {
            File[] spreadSheets = year.listFiles();
            for (File spreadSheet : spreadSheets) {
                if (spreadSheet.getName().contains(quarter)) {
                    csFiles.add(spreadSheet);
                }
            }
        }
        return csFiles;
    }

    /**
     * Reads a file in given its path.
     * @param file          The path of the file.
     * @return              A String representing the contents of the file.
     * @throws IOException  If the FileReader is malformed.
     */
    public static String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return stringBuilder.toString();
    }

}
