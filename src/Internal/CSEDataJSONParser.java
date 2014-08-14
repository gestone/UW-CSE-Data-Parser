package Internal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * JSON Parser specifically for parsing a single quarter's worth of data of JSON and two quarters' worth of data of
 * JSON. After the file or files are successfully parsed, this data can be readily accessible in a form of a map.
 */
public class CSEDataJSONParser {

    private Map<Integer, Student> allStudents;
    private Map<Integer, List<Student>> twoQuarterStudents;

    /**
     * Constructor for the CSEDataJSONParser.
     */
    public CSEDataJSONParser() {
        allStudents = new HashMap<Integer, Student>();
        twoQuarterStudents = new HashMap<Integer, List<Student>>();
    }

    /**
     * Parses a single quarter's file JSON and stores it in the parser.
     * @param JSONFile The single quarter JSON file to be parsed.
     */
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

    /**
     * Parses a comparison quarter JSON and stores it in the parser.
     * @param JSONFile The comparison quarter JSON to be parsed.
     */
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

    /**
     * Gets the parsed data from the inputted single JSON file.
     * @return A map containing the parsed data from the inputted JSON file, returns null if parseSingleQuarterJSON
     *         has not been called.
     */
    public Map<Integer, Student> getSingleQuarterMap() {
        return allStudents;
    }

    /**
     * Gets the parsed data from the inputted comparison JSON file.
     * @return A map containing the parsed data from the inputted comparison JSON file,
     *         returns null if parseTwoQuarterJSON has not been called.
     */
    public Map<Integer, List<Student>> getTwoQuarterMap() {
        return twoQuarterStudents;
    }

    /**
     * Reads the JSON file in and returns a String representation of the file.
     * @param JSONFile The JSON file to be processed.
     * @return         A String representation of that JSON file.
     */
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

    /**
     * Creates a Student from a JSONObject.
     * @param student A JSONObject representing a single Student in a quarter.
     * @return        A new Student object representing a single Student in a CSE 142 or 143 quarter.
     */
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