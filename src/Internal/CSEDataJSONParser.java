package Internal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Justin on 7/19/2014.
 */
public class CSEDataJSONParser {

    private Map<Integer, Student> allStudents;
    private Map<Integer, List<Student>> twoQuarterStudents;


    public CSEDataJSONParser() {
        allStudents = new HashMap<Integer, Student>();
        twoQuarterStudents = new HashMap<Integer, List<Student>>();
    }

    public void parseSingleQuarterJSON(File JSONFile) {
        try {
            String JSON = readFile(JSONFile);
            JSONObject file = new JSONObject(JSON);
            JSONArray students = file.getJSONArray("students");
            for (int i = 0; i < students.length(); i++) {
                JSONObject cur = students.getJSONObject(i);
                int code = cur.getInt("code");
                allStudents.put(code, createStudent(cur));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseTwoQuarterJSON(File JSONFile) {
        try {
            String JSON = readFile(JSONFile);
            JSONObject totalFile = new JSONObject(JSON);
            JSONArray allStudents = totalFile.getJSONArray("all_students");
            for (int i = 0; i < allStudents.length(); i++) {
                Student cs142 = createStudent(allStudents.getJSONObject(i).getJSONObject("142"));
                Student cs143 = createStudent(allStudents.getJSONObject(i).getJSONObject("143"));
                List<Student> bothPerformances = new ArrayList<Student>();
                bothPerformances.add(cs142);
                bothPerformances.add(cs143);
                twoQuarterStudents.put(cs142.getCode(), bothPerformances);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Student> getSingleQuarterMap() {
        return allStudents;
    }

    public Map<Integer, List<Student>> getTwoQuarterMap() {
        return twoQuarterStudents;
    }

    private String readFile(File JSONFile) {
        StringBuilder b = new StringBuilder();
        try {
            Scanner s = new Scanner(JSONFile);
            while (s.hasNext()) {
                b.append(s.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b.toString();
    }

    private Student createStudent(JSONObject student) {
        try {
            int code = student.getInt("code");
            double homework = student.getDouble("homework");
            int midterm = student.getInt("midterm");
            int finalExam = student.getInt("final_exam");
            double total = student.getDouble("total_score");
            double grade = student.getDouble("grade");
            return new Student(code, homework, midterm, finalExam, total, grade);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
