import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class CSEDataMain {

    private final String[] ACTION = new String[]{"PARSE", "CORRELATE"};

    public static void main(String[] args){

    }

    public static Map<String, Student> getFile(String year, String whichClass){
        Map<String, Student> allStudents = new HashMap<String, Student>();
        try {
            URL file = new URL("http://courses.cs.washington.edu/courses/" + whichClass + "/"  + year + "/scores.html");
            Scanner spreadSheet = new Scanner(file.openStream());
            processFile(spreadSheet, allStudents);
        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }

        return allStudents;
    }

    /**
     *
     * @param spreadSheet
     * @param allStudents
     */
    public static void processFile(Scanner spreadSheet, Map<String, Student> allStudents){
        // process first line to get cate
        String catagories = spreadSheet.nextLine();
        Scanner processCatagories = new Scanner(catagories);
        processCatagories.useDelimiter(" ");




        while(processCatagories.hasNext()){
            String entry = processCatagories.next();

        }

        while(spreadSheet.hasNextLine()){
            String wholeLine = spreadSheet.nextLine();
            Scanner processLine = new Scanner(wholeLine);
            processLine.useDelimiter(" ");

        }
    }

}
