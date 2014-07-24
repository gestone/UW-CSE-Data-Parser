package Internal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Justin on 7/19/2014.
 */
public class CSEDataJSONParser {

    private Map<Integer, Student> allStudents;


    public CSEDataJSONParser(File JSONFile){
        allStudents = new HashMap<Integer, Student>();
        parseJSON(JSONFile);
    }

    private void parseJSON(File JSONFile){
        try {
            String JSON = readFile(JSONFile);
            JSONObject file = new JSONObject(JSON);
            JSONArray students = file.getJSONArray("students");
            for(int i = 0; i < students.length(); i++){
                JSONObject cur = students.getJSONObject(i);
                int code = cur.getInt("code");
                double homework = cur.getDouble("homework");
                int midterm = cur.getInt("midterm");
                int finalExam = cur.getInt("final");
                double total = cur.getDouble("total");
                double grade = cur.getDouble("grade");
                allStudents.put(code, new Student(code, homework, midterm, finalExam, total, grade));
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

}
