import Constants.Action;
import Constants.AppConstant;
import Internal.CSEData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 */
public class CSEDataMain {

    public static void main(String[] args){
//        Scanner userInput = new Scanner(System.in);
//        String year = getYear(userInput);
//        List<Action> actionsToProcess = actionsToPerform(userInput);


        getFile("2013", "cse143");
    }

    /**
     *
     * @param year
     * @param whichClass
     * @return
     */
    public static void getFile(String year, String whichClass){
        File f  = new File("CSRawData/2013/cse142winter2013.txt");
        CSEData winter2014 = new CSEData(f, year);
        winter2014.findGradeCutoffs();
//        File aut2013 = new File("CSRawData/2013/cse143spring2013.txt");
//        CSEData autumn2013 = new CSEData(aut2013, year);
//        winter2014.correlateTwoQuarters(autumn2013);
//        autumn2013.writeSingleQuarterToJSON();
//        autumn2013.graphGradeData();
//        autumn2013.getGradeDistribution();
    }

    /**
     *
     * @param userInput
     * @return
     */
    public static String getYear(Scanner userInput){
        String year;
        do {
            System.out.print("Which year in the CSRawData directory would you like to analyze? ");
            year = userInput.next();
            if (!yearChosen(year)) {
                System.out.println("That year is not a valid directory.");
                System.out.println();
            }
        } while(!yearChosen(year));
        return year;
    }

    /**
     *
     * @param year
     * @return
     */
    public static boolean yearChosen(String year){
        return new File(AppConstant.CS_RAW_DATA_DIR + year).exists();
    }

    public static List<File> filesToAnalyze(Scanner userInput, String year){
        List<File> files = new ArrayList<File>();
        File[] dir = new File(AppConstant.CS_RAW_DATA_DIR + year).listFiles();
        System.out.println("List of files: ");
        for(File f : dir){
            System.out.println(f.getName());
        }
        for(int i = 0; i < dir.length; i++){

        }
        return files;
    }

    /**
     *
     * @param userInput
     * @return
     */
    public static List<Action> actionsToPerform(Scanner userInput){
        List<Action> actionsToPerform = new ArrayList<Action>();
        if (yesTo("Would you like to process only one file?", userInput)) {
            if (yesTo("Would you like to parse the data into a JSON file?", userInput)) {
                actionsToPerform.add(Action.PARSE);
            }
            if (yesTo("Would you like to graph the distribution of grades of a quarter?", userInput)){
                actionsToPerform.add(Action.GRAPH);
            }
            if (yesTo("Would you like to see the percentage distribution of grades of a quarter?", userInput)){
                actionsToPerform.add(Action.PERCENTAGE_DISTRIBUTION);
            }
        } else {
            if (yesTo("Would you like to parse the overlapping data from the two quarters into a JSON file?",
                    userInput)){
                actionsToPerform.add(Action.PARSE);
            }
            if (yesTo("Would you like to calculate correlation coefficients between the two files?", userInput)){
                actionsToPerform.add(Action.CORRELATE);
            }
            if (yesTo("Would you like to see a scatter plot of CSE 142 grades vs CSE 143 grades?", userInput)){
                actionsToPerform.add(Action.GRAPH_GRADE_COMPARSION);
            }
        }
        return actionsToPerform;
    }

    public static boolean yesTo(String prompt, Scanner userInput) {
        String response = userInput.next().trim().toLowerCase();
        while (!response.equals("y") && !response.equals("n")) {
            System.out.println("Please answer y or n.");
            System.out.print(prompt + " (y/n)? ");
            response = userInput.next().trim().toLowerCase();
        }
        return response.equals("y");
    }


}
