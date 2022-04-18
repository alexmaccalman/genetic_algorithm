
package DesignAlgorithm;
import java.io.*;
import java.util.StringTokenizer;
/*
 * This class creates an object that stores the design matrix of a DOE
 * the csv file must be in the same directory, the csv file format should have the
 * first row, the header row contain the names of each factor.  Each subsequent row
 * should have each of the design points that will be simulated. There should be no
 * blanks in the matrix.
 * @author maccalman
 */
public class DOEFactorArray {
    //fields
    private String csvFileName;
    private String[][] factorArray;
    private int numberOfFactors;
    private int numberOfDesignPoints;
    //constructor
    public DOEFactorArray(String csvFileName){
        this.csvFileName = csvFileName;
        this.numberOfFactors = arrayColDimension(csvFileName);
        this.numberOfDesignPoints = arrayRowDimension(csvFileName) - 1;//subtract one for header row
        inputTextFile(csvFileName, numberOfFactors, numberOfDesignPoints);
    }
    /**
     * finds the number of design points in the DOE matrix plus the header row, each row is a design point
     * excluding the header row
     * Note: the int returned from this method is the number of rows in the matrix, subtract one (the header row) to
     * get the number of design points.
     * assumes the csv file is in the correct format
     * @param csvFileName
     * @return the row size of the DOE matrix
     */
    public int arrayRowDimension(String csvFileName){
       int count=0;
       try{
            BufferedReader inputStream = new BufferedReader(new FileReader(csvFileName));
            String line = inputStream.readLine();
            while(line !=null){
                count++;
                line = inputStream.readLine();
            }
       }
       catch(FileNotFoundException e){
             System.out.println("File was not found");
       }
       catch(IOException e){
             System.out.println("Error reading from file");
       }
       return count;
    }

    /**
     * finds the number of factors in the design matrix, each col is a factor
     * assumes the csv file is in the correct format
     * @param csvFileName
     * @return the col size of the DOE matrix from the csv file
     */
    public int arrayColDimension(String csvFileName){
        int count=0;

       try{
            BufferedReader inputStream = new BufferedReader(new FileReader(csvFileName));
            String line = inputStream.readLine();
            StringTokenizer lineTokenized = new StringTokenizer(line," ,");
            count = lineTokenized.countTokens();
       }
       catch(FileNotFoundException e){
             System.out.println("File was not found");
       }
       catch(IOException e){
             System.out.println("Error reading from file");
       }
       return count;
    }

    /**
     * populates the factorArray field with the csv file
     * assumes the csv file is in the correct format
     * @param csvFileName
     */
     public void inputTextFile(String csvFileName, int factors, int designPoints){
         // input data from text file
         try{
             BufferedReader inputStream = new BufferedReader(new FileReader(csvFileName));
             //add one to account for the header row
             factorArray = new String[designPoints+1][factors];

             //enter factor col headings
             String line;
             StringTokenizer lineTokenized;

             int i = 0;

            // each row is a design point
            for (int designPointRow = 0; designPointRow <= getFactorArray().length; designPointRow++){
                line = inputStream.readLine();
                if(line != null){
                    lineTokenized = new StringTokenizer(line," ,");

                     i = 0;
                    while(lineTokenized.hasMoreTokens()){
                        factorArray[designPointRow][i] = lineTokenized.nextToken();
                        i++;
                    }
                }
            }
            inputStream.close();
         }
         catch(FileNotFoundException e){
             System.out.println("File was not found");
         }
         catch(IOException e){
             System.out.println("Error reading from file");
         }
     }

    /**
     * @return the csvFileName
     */
    public String getCsvFileName() {
        return csvFileName;
    }

    /**
     * @return the factorArray
     */
    public String[][] getFactorArray() {
        return factorArray;
    }

    /**
     * @return the numberOfFactors
     */
    public int getNumberOfFactors() {
        return numberOfFactors;
    }

    /**
     * @return the numberOfDesignPoints
     */
    public int getNumberOfDesignPoints() {
        return numberOfDesignPoints;
    }
}
