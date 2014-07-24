import Constants.Action;
import Internal.CSEData;
import Internal.CSEDataJSONParser;

import java.io.File;

/**
 *
 */
public class CSEDataMain {

    public static void main(String[] args){
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
        File aut2013 = new File("CSRawData/2013/cse143spring2013.txt");
        CSEData autumn2013 = new CSEData(aut2013, year);
        winter2014.correlateTwoQuarters(autumn2013);
//        autumn2013.writeSingleQuarterToJSON();
//        autumn2013.graphGradeData();
//        autumn2013.getGradeDistribution();
    }



}
