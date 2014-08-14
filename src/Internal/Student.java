package Internal;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a single instance of a Student in a CSE 142 or CSE 143 class. Stores the code used to identify the
 * student, their midterm, final exam, and overall homework scores, their total score,
 * and the final grade they received.
 *
 * This class also implements the Comparable interface which allows for the comparison between different students and
 * decides which allows for easy sorting of students.
 */
public class Student implements Comparable<Student>{

    private int code;
    private int midterm;
    private int finalExam;
    private double totalScore;
    private double grade;
    private double homework;

    /**
     * Constructor for a Student.
     * @param code         The code associated with the Student initially used for looking up how the student's grade
     *                     was calculated.
     * @param homework     The final homework percentage.
     * @param midterm      The student's midterm score.
     * @param finalExam    The final exam score.
     * @param totalScore   The total combined final exam score, homework percentage, and midterm score.
     * @param grade        The final grade the student received.
     */
    public Student(int code, double homework, int midterm, int finalExam, double totalScore, double grade){
        this.code = code;
        this.homework = homework;
        this.midterm = midterm;
        this.finalExam = finalExam;
        this.totalScore = totalScore;
        this.grade = grade;
    }

    /******************
     * GETTER METHODS *
     ******************/
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

    /**
     * Implementation of the comparable interface.
     * @param other The other student to compare this Student to.
     * @return      An integer representing whether or not the other student had a lower final grade. If the other
     *              Student had a higher grade than 'this' Student, a positive integer will be returned. If 'this' Student has a
     *              higher grade than the other Student, a negative integer will be returned, and if both Students have the same
     *              grade, 0 will be returned.
     */
    @Override
    public int compareTo(Student other) {
        if (this.getGrade() - other.getGrade() == 0){
            return 0;
        } else if (this.getGrade() > other.getGrade()){
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return grade + "";
    }


    /**
     * Gets the Student in a basic double array for processing purposes.
     * @return A double array representation of the Student.
     */
    protected Double[] getStudentInDoubleArray(){
        return new Double[]{homework, (double) midterm, (double) finalExam, totalScore, grade};
    }
}
