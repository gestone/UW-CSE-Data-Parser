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
        List<Action> actionsToProcess;
        File quarterOne, quarterTwo;
        Scanner userInput = new Scanner(System.in);
        String year = getYear(userInput);
        if (yesTo("Would you like to process only one file? ", userInput)){
            actionsToProcess = getSingleQuarterActions(userInput, year);
            quarterOne = getSingleFile(userInput, year, "");
            CSEData quarter = new CSEData(quarterOne, year);
            quarter.processOneQuarter(actionsToProcess);
        } else {
            while (!hasBothQuarters(year)){
                System.out.println("Choose another year. This year does not contain CSE 142 and CSE 143.");
                System.out.println();
                year = getYear(userInput);
            }
            actionsToProcess = getTwoQuarterActions(userInput, year);
            System.out.println();
            System.out.println("Choose a CSE 142 quarter.");
            System.out.println();
            quarterOne = getSingleFile(userInput, year, "cse142");
            System.out.println("CSE 142 quarter, " + quarterOne.getName() + " successfully chosen.");
            System.out.println();
            System.out.println("Choose a CSE 143 quarter.");
            System.out.println();
            quarterTwo = getSingleFile(userInput, year, "cse143");
            System.out.println("CSE 143 quarter, " + quarterTwo.getName() + " successfully chosen.");
            System.out.println();
            CSEData cse142 = new CSEData(quarterOne, year);
            CSEData cse143 = new CSEData(quarterTwo, year);
            cse142.processTwoQuarters(cse143, actionsToProcess);
        }
    }


    /**
     *
     * @param userInput
     * @return
     */
    public static String getYear(Scanner userInput){
        String year = "-1";
        while(!yearChosen(year)){
            System.out.print("Which year in the CSRawData directory would you like to analyze? ");
            year = userInput.next();
            if (!yearChosen(year)) {
                System.out.println("That year is not a valid directory.");
                System.out.println();
            }
        }
        return year;
    }

    public static boolean hasBothQuarters(String year){
        File[] dir = new File(AppConstant.CS_RAW_DATA_DIR + year).listFiles();
        boolean cse142 = false;
        boolean cse143 = false;
        for (File f : dir){
            if (f.getName().contains("cse142")){
                cse142 = true;
            }
            if (f.getName().contains("cse143")){
                cse143 = true;
            }
        }
        return cse142 && cse143;
     }

    /**
     *
     * @param year
     * @return
     */
    public static boolean yearChosen(String year){
        return new File(AppConstant.CS_RAW_DATA_DIR + year).exists();
    }

    public static File getSingleFile(Scanner userInput, String year, String cseQuarter){
        File[] dir = new File(AppConstant.CS_RAW_DATA_DIR + year).listFiles();
        System.out.println("List of files:");
        for(int i = 0; i < dir.length; i++){
            System.out.println("(" + (i + 1) + ") " + dir[i].getName());
        }
        System.out.println("Which file would you like to process? (Type in the number before the file) ");
        String fileNumber;
        int fileIndex = -1;
        while (fileIndex <= 0 || fileIndex > dir.length){
            System.out.print("Please enter in a valid number (1 - " + dir.length + ") ");
            fileNumber = userInput.next();
            if (fileNumber.matches("[0-9]+")){
                fileIndex = Integer.parseInt(fileNumber);
                if(!cseQuarter.isEmpty() && fileIndex > 0 && fileIndex <= dir.length){
                    if(!dir[fileIndex - 1].getName().contains(cseQuarter)){
                        System.out.println("Please choose a " + cseQuarter.toUpperCase() + " quarter.");
                        fileIndex = -1;
                    }
                }
            }
        }
        return dir[fileIndex - 1];
    }

    /**
     *
     * @param userInput
     * @return
     */
    public static List<Action> getSingleQuarterActions(Scanner userInput, String year){
        List<Action> actionsToPerform = new ArrayList<Action>();
            if (yesTo("Would you like to parse the data into a JSON File?", userInput)) {
                actionsToPerform.add(Action.PARSE);
            }
            if (yesTo("Would you like to graph the distribution of grades of a quarter?", userInput)){
                actionsToPerform.add(Action.GRAPH);
            }
            if (yesTo("Would you like to see the percentage distribution of grades of a quarter with the cutoffs for " +
                            "each grade?", userInput)){
                actionsToPerform.add(Action.PERCENTAGE_DISTRIBUTION);
            }
        return actionsToPerform;
    }

    public static List<Action> getTwoQuarterActions(Scanner userInput, String year){
        List<Action> actionsToPerform = new ArrayList<Action>();
        if (yesTo("Would you like to parse the repeat students' data into a JSON file?", userInput)){
            actionsToPerform.add(Action.PARSE);
        }
        if (yesTo("Would you like to see a scatter plot of CSE 142 performance vs CSE 143 performance?", userInput)){
            actionsToPerform.add(Action.GRAPH_GRADE_COMPARSION);
        }
        if (yesTo("Would you like to correlate the students' CSE 142 grades and CSE 143 grades?", userInput)){
            actionsToPerform.add(Action.CORRELATE);
        }
        return actionsToPerform;
    }

    public static boolean yesTo(String prompt, Scanner userInput) {
        System.out.print(prompt + " (y/n)? ");
        String response = userInput.next().trim().toLowerCase();
        while (!response.equals("y") && !response.equals("n")) {
            System.out.println("Please answer y or n.");
            System.out.print(prompt + " (y/n)? ");
            response = userInput.next().trim().toLowerCase();
        }
        return response.equals("y");
    }


}
