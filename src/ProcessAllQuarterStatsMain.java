import Constants.AppConstant;
import Internal.Math.MathUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class ProcessAllQuarterStatsMain {

    public static void main(String[] args) {
        calculateCutoffsAndDistribution();
    }

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
                Collections.sort(cutOff142CurList);
                Collections.sort(cutOff143CurList);
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
                    "AllQuarterStats/csesingleprocessedavg.json");
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

    public static double findAverage(List<Double> list){
        double total = 0;
        for (Double d : list){
            total += d;
        }
        return MathUtils.roundNPlaces(total / list.size(), 2);
    }

    public static Map<Double, List<Double>> getMapping(String quarterType, boolean isPercent) {
        Map<Double, List<Double>> cutOffs = new TreeMap<Double, List<Double>>();
        List<File> cse142Files = getCSFiles(quarterType);
        for (File year : cse142Files) {
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
