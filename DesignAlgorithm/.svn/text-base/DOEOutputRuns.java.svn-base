/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DesignAlgorithm;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.text.DecimalFormat;
/**
 *This class is coupled with the DOEFactorArray.  The cvs output file will get saved to the same directory.
 * @author maccalman
 */
public class DOEOutputRuns {
    //fields
    private String csvOutputFileName;
    private String [][] factorArray;
    private ArrayList<String> responseNames;
    private ArrayList<String> responseOutputs;
    //constructor
    public DOEOutputRuns(String name, DOEFactorArray doeFactorArray, ArrayList<String> responseNames, ArrayList<String> responseOutputs){
        this.csvOutputFileName = name;
        this.factorArray = doeFactorArray.getFactorArray();
        this.responseNames = responseNames;
        this.responseOutputs = responseOutputs;

    }
    /**
     * Accepts the number of design points and reps
     * @param designPoints
     * @param rep
     */
    public void writeToCsv(int designPoints, int rep){
        try {
             BufferedWriter out = new BufferedWriter(new FileWriter(csvOutputFileName,true));


             out.write(designPoints + ",");
             out.write(rep + ",");
             //the second dimension of the factorArray (cols) are the factors
             //write factor levels to the output file for the design point passed in
             for (int factor = 0; factor < factorArray[0].length; factor++){
                  out.write(factorArray[designPoints][factor] + ",");
             }
             for (String responses : responseOutputs){
                 out.write(responses + ",");
             }
             out.write("\n");

             out.close();
         } catch (IOException e) { }
    }
    /**
     * clears the csv file in the same directory
     */
    public void clearCsvFile(){
        try {
            File file = new File(csvOutputFileName);
            file.delete();
            File newFile = new File(csvOutputFileName);
            newFile.createNewFile();
        //    newFile.close();
        } catch (IOException e) { }
    }
    /**
     * writes the column headings starting with the design point number, the rep, then the input
     * factor settings and finally the response outputs.
     * The csv file is formated for use in JMP for statistical analysis
     */
    public void writeColHeadings(){
        try {
             BufferedWriter out = new BufferedWriter(new FileWriter(csvOutputFileName,true));


             out.write("DP" + ",");
             out.write("Rep" + ",");
             //the second dimension of the factorArray (cols) are the factors
             //write factor names to the output file
             for (int factor = 0; factor < factorArray[0].length; factor++){
                out.write(factorArray[0][factor] + ",");
             }
             for (String outputNames : responseNames){
                 out.write(outputNames + ",");
             }
             out.write("\n");

             out.close();
         } catch (IOException e) { }
    }
}


// surround the below code onto a model's run class within the public static void main(String[] args) method

//        //set number of replications
//        int replications = 2;
//        //input the DOE matrix from the csv file to the doeFactorArray object
//        DOEFactorArray doeFactorArray = new DOEFactorArray("textcsv.csv");
//        //populate the response output names
//        ArrayList<String> responseNames = new ArrayList<String>();
//        responseNames.add("arrivals");
//        responseNames.add("served");
//        responseNames.add("avgInQueue");
//        responseNames.add("avgUtil");
//        //create the response output ArrayList<String>, this will be populated after the simulation run
//        ArrayList<String> responseOutputs = new ArrayList<String>();
//        //prepare the output file, clear the csv file and add the header names to the first row
//        DOEOutputRuns doeOutputRuns = new DOEOutputRuns("outputFile.csv", doeFactorArray, responseNames, responseOutputs);
//        doeOutputRuns.clearCsvFile();
//        doeOutputRuns.writeColHeadings();
//
//        //simulate each design point for the specified number of replications
//
//        for(int DP = 1; DP <= doeFactorArray.getNumberOfDesignPoints(); DP++){
//            //each rep
//            for( int rep = 1; rep <= replications; rep++){
//
//                //convert string to an integer or a double as needed
//                //insure factor column number matches the 2nd dimension of the FactorArray!!
//                totalNumberOfServers = Integer.parseInt(doeFactorArray.getFactorArray()[DP][0]);
//
//
//                //insert model from original Run class
//                // -------------------------------------------------------------------------------------------------------
//
//
//                //-------------------------------------------------------------------------------------------------------
//                //end of model from original Run Class
//
//                //add response outputs to the ArrayList<String>
//                //number of adds must match the number of response outputs
//                //must convert to Strings
//                responseOutputs.add(Integer.toString(arrivalProcess.getNumberArrivals()));
//                responseOutputs.add(Integer.toString(simpleServer.getNumberServed()));
//                responseOutputs.add(Double.toString(niqStat.getMean()));
//                responseOutputs.add(Double.toString(1.0 - nAServerStat.getMean()/simpleServer.getTotalNumberServers()));
//                //write to csv file
//                doeOutputRuns.writeToCsv(DP, rep);
//                //reset/clear model to prepare for the next rep/design point
//                Schedule.coldReset();
//                responseOutputs.clear();
//            }
//        }
//
