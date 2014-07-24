package Internal;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Justin on 7/12/2014.
 */
public class Student {

    private int code;
    private int midterm;
    private int finalExam;
    private double totalScore;
    private double grade;
    private double homework;

    public Student(int code, double homework, int midterm, int finalExam, double totalScore, double grade){
        this.code = code;
        this.homework = homework;
        this.midterm = midterm;
        this.finalExam = finalExam;
        this.totalScore = totalScore;
        this.grade = grade;
    }

    public int getCode(){
        return code;
    }

    public int getMidterm(){
        return midterm;
    }

    public int getFinalExam(){
        return finalExam;
    }

    public double getTotalScore(){
        return totalScore;
    }

    public double getGrade(){
        return grade;
    }

    public double getHomework(){
        return homework;
    }

    @Override
    public String toString() {
        return homework + "% " + midterm + " midterm " + finalExam + " final " + totalScore + " total score " + grade
                + " overall grade";
    }

    protected JSONObject getJSONObject(){
        JSONObject student = new JSONObject();
        try {
            student.put("code", code);
            student.put("midterm", midterm);
            student.put("final_exam", finalExam);
            student.put("total_score", totalScore);
            student.put("grade", grade);
            student.put("homework", homework);
        } catch(JSONException e){
            e.printStackTrace();
        }
        return student;
    }

    public Double[] getStudentInDoubleArray(){
        return new Double[]{homework, (double) midterm, (double) finalExam, totalScore, grade};
    }
}
