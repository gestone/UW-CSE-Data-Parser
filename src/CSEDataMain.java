import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 */
public class CSEDataMain {

    private final String[] ACTION = new String[]{"PARSE", "CORRELATE"};

    public static void main(String[] args){
        getFile("14wi", "cse143");
    }

    /**
     *
     * @param year
     * @param whichClass
     * @return
     */
    public static Map<String, Student> getFile(String year, String whichClass){
        Map<String, Student> allStudents = new HashMap<String, Student>();
        try {
            Document file = Jsoup.connect("http://courses.cs.washington.edu/courses/" + whichClass + "/"  + year +
                    "/scores.html").get();
            String title = file.text();

//            spreadSheet.useDelimiter(" ");
//            processFile(spreadSheet, allStudents);
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
        System.out.println(Arrays.toString(processCategories(spreadSheet)));
        while(spreadSheet.hasNext()){
            String wholeLine = spreadSheet.nextLine();
            Scanner processLine = new Scanner(wholeLine);
            processLine.useDelimiter(" ");
            System.out.println(wholeLine);
        }
    }

    public static void convertToFile(String file){

    }

    /**
     *
     * @return
     */
    public static int[] processCategories(Scanner spreadSheet){
        int[] catagoriesIndexes = new int[5]; // homework, midterm, final, total %, and final grade

        // process header to get categories and their respective indexes
        String catagories = spreadSheet.nextLine();
        Scanner processCatagories = new Scanner(catagories);
        processCatagories.useDelimiter(" ");
        int index = -1;
        boolean seenMidterm = false;
        boolean seenFinal = false;
        while(processCatagories.hasNext()){
            String entry = processCatagories.next();
            if(entry.startsWith("last")){ // 'last 4 digits of sid' do not correspond to a category
                index -= 4;
            }
            if(entry.equals("%")){
                catagoriesIndexes[0] = index;
            }
            if(entry.contains("mid")){
                catagoriesIndexes[1] = index;
                if(seenMidterm){ // 'new midterm' has 2 entries for 1 column
                    catagoriesIndexes[1]--;
                }
                seenMidterm = true;
            }
            if(entry.contains("fin")){
                catagoriesIndexes[2] = index;
                if(seenFinal){ // 'new final' has 2 entries
                    catagoriesIndexes[2]--;
                }
                seenFinal = true;
            }
            if(entry.equals("total")){
                catagoriesIndexes[3] = index;
            }
            if(entry.equals("grade")){
                catagoriesIndexes[4] = index;
            }
            index++;
        }
        return catagoriesIndexes;
    }

}
