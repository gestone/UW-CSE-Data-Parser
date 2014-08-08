import Constants.Action;
import Constants.AppConstant;
import Internal.CSEData;
import Internal.CSEDataJSONParser;
import Internal.Student;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessAllFilesMain {

    public static void main(String[] args) {
//        processAllSingleFiles();
        processComparisionJSON();
    }

    public static void processAllSingleFiles() {
        List<Action> parse = new ArrayList<Action>();
        parse.add(Action.PARSE);
        File[] csDir = new File(AppConstant.CS_RAW_DATA_DIR).listFiles();
        for (File year : csDir) {
            File[] csRawData = year.listFiles();
            for (File spreadsheet : csRawData) {
                CSEData rawDataFile = new CSEData(spreadsheet, year.getName());
                rawDataFile.processOneQuarter(parse);
            }
        }
        System.out.println("Processing complete!");
    }

    public static void processComparisionJSON() {
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
        actions.add(Action.CORRELATE);
        actions.add(Action.PARSE);
        actions.add(Action.GRAPH_GRADE_COMPARSION);
        allCS142.processTwoQuarters(allCS143, actions);
    }

}
