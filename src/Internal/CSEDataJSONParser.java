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


    public CSEDataJSONParser(File JSONFile){
        allStudents = new HashMap<Integer, Student>();
        twoQuarterStudents = new HashMap<Integer, List<Student>>();
        parseSingleQuarterJSON(JSONFile);
    }

    private void parseSingleQuarterJSON(File JSONFile){
        try {
            String JSON = readFile(JSONFile);
            JSONObject file = new JSONObject(JSON);
            JSONArray students = file.getJSONArray("students");
            for(int i = 0; i < students.length(); i++){
                JSONObject cur = students.getJSONObject(i);
                int code = cur.getInt("code");
                allStudents.put(code, createStudent(cur));
            }
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void parseTwoQuarterJSON(File JSONFile){
        try{
            String JSON = readFile(JSONFile);
            JSONArray j = new JSONArray(JSON);
            for(int i = 0; i < j.length(); i++){
                Student cs142 = createStudent(j.getJSONObject(i).getJSONObject("cse142"));
                Student cs143 = createStudent(j.getJSONObject(i).getJSONObject("cse143"));
                List<Student> bothPerformances = new ArrayList<Student>();
                bothPerformances.add(cs142);
                bothPerformances.add(cs143);
                twoQuarterStudents.put(cs142.getCode(),bothPerformances);
            }
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public Map<Integer, Student> getMap(){
        return allStudents;
    }

    private String readFile(File JSONFile){
        StringBuilder b = new StringBuilder();
        try{
            Scanner s = new Scanner(JSONFile);
            while(s.hasNext()){
                b.append(s.next());
            }
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return b.toString();
    }

    private Student createStudent(JSONObject student){
        try{
            int code = student.getInt("code");
            double homework = student.getDouble("homework");
            int midterm = student.getInt("midterm");
            int finalExam = student.getInt("final");
            double total = student.getDouble("total");
            double grade = student.getDouble("grade");
            return new Student(code, homework, midterm, finalExam, total, grade);
        } catch(JSONException e){
            e.printStackTrace();
        }
        return null;
    }

}
