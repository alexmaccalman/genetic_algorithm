/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DesignAlgorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @version
 * @author alexmaccalman
 */
public class WriteOut {

    /**
     *
     */
    public static void writeArrayListToCsv(ArrayList<DesignColumn> arrayList, String fileName) {

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            //grab the first array column to get the number of rows
            for (int row = 0; row < arrayList.get(0).getDesignColumn().length; row++) {
                for (int col = 0; col < arrayList.size(); col++) {
                    double[] column = arrayList.get(col).getDesignColumn();
                    out.write(column[row] + ", ");
                }
                out.write("\n");
                // at the bottom, write out the factor types and number of levels
                if (row == arrayList.get(0).getDesignColumn().length - 1) {
                    for (int col = 0; col < arrayList.size(); col++) {
                        if (!arrayList.get(col).getType().equals("continuous")) {
                            out.write("balance" + ", ");
                        } else {
                            out.write(", ");
                        }
                    }
                    out.write("\n");
                    for (int col = 0; col < arrayList.size(); col++) {
                        if (arrayList.get(col).getType().equals("continuous")) {
                            out.write(", ");
                        } else {
                            out.write(arrayList.get(col).getBalance() + ", ");
                        }
                    }
                    out.write("\n");
                }



            }
            out.close();
        } catch (IOException e) {
        }
    }

    public static void writeTranslatedArrayListToCsv(ArrayList<DesignColumn> arrayList, String fileName) {

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            //grab the first array column to get the number of rows
            double[] column = null;
            for (int row = 0; row < arrayList.get(0).getDesignColumn().length; row++) {
                for (int col = 0; col < arrayList.size(); col++) {
                    column = arrayList.get(col).getDesignColumn();
                    column[row] = column[row] + arrayList.get(col).getMean();
                    out.write(column[row] + ", ");
                }
                out.write("\n");
                // at the bottom, write out the factor types and number of levels
                if (row == arrayList.get(0).getDesignColumn().length - 1) {
                    for (int col = 0; col < arrayList.size(); col++) {
                        if (!arrayList.get(col).getType().equals("continuous")) {
                            out.write("balance" + ", ");
                        } else {
                            out.write(", ");
                        }
                    }
                    out.write("\n");
                    for (int col = 0; col < arrayList.size(); col++) {
                        if (arrayList.get(col).getType().equals("continuous")) {
                            out.write(", ");
                        } else {
                            DesignColumn designColumn = arrayList.get(col);
                            column = designColumn.getDesignColumn();
                           
                           
                            
                            double ideal = (double)designColumn.getLevels()/(double)designColumn.getDiscreteLevels();
                            
                            designColumn.setBalance(designColumn.checkBalance(column, ideal, designColumn.getDiscreteLevels()));
                            
                            out.write(designColumn.getBalance() + ", ");
                        }
                    }
                    out.write("\n");
                }
            }
            out.close();
        } catch (IOException e) {
        }
    }

    public static void writeArrayToCsv(DesignColumn[] arrayList, String fileName) {

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            //grab the first array column to get the number of rows
            for (int row = 0; row < arrayList[0].getDesignColumn().length; row++) {
                for (int col = 0; col < arrayList.length; col++) {
                    double[] column = arrayList[col].getDesignColumn();
                    out.write(column[row] + ", ");
                }
                out.write("\n");
            }
            out.close();
        } catch (IOException e) {
        }
    }

    public static void writeDummyArrayListWithHeadersToCsv(ArrayList<DesignColumn> arrayList, String fileName) {

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            int discreteLevelCounter = 1;
            int dummyCounter = 1;
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).getType().equals("dummy")) {
                    out.write("x" + (dummyCounter) + "D" + discreteLevelCounter + ",");
                    discreteLevelCounter += 1;
                    if (discreteLevelCounter == arrayList.get(i).getDiscreteLevels()) {
                        discreteLevelCounter = 1;
                        dummyCounter += 1;
                    }
                } else {
                    out.write("x" + (i + 1) + ", ");
                }
                //grab the first array column to get the number of rows
            }
            out.write("\n");
            for (int row = 0; row < arrayList.get(0).getDesignColumn().length; row++) {
                for (int col = 0; col < arrayList.size(); col++) {
                    double[] column = arrayList.get(col).getDesignColumn();
                    out.write(column[row] + ", ");
                }
                out.write("\n");
            }
            out.write("\n");
            out.close();
        } catch (IOException e) {
        }
    }

    public static void writeColToCsv(DesignColumn col, String fileName, double corr, double time) {

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            //grab the first array column to get the number of rows
            for (int row = 0; row < col.getDesignColumn().length; row++) {
                double[] column = col.getDesignColumn();
                out.write(column[row] + ", ");
                out.write("\n");
            }

            out.write("\n");
            out.write(Double.toString(corr));
            out.write("\n");
            out.write(Double.toString(time));

            out.close();
        } catch (IOException e) {
        }
    }

    public static void writeDiagnosticsToCsv(ArrayList<GenerationDiagnostic> arrayList, String fileName) {

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            //grab the first array column to get the number of rows
            for (GenerationDiagnostic diagnostic : arrayList) {
                out.write(diagnostic.getGenerationNumber() + ", ");
                out.write(diagnostic.getMinFitness() + ", ");
                out.write(diagnostic.getMaxFitness() + ", ");
                out.write(diagnostic.getFitnessRange() + ", ");
                out.write(diagnostic.getFitnessMean() + ", ");
                out.write(diagnostic.getFitnessTotal() + ", ");
                out.write(diagnostic.getNumberColumns() + ", ");
                out.write(diagnostic.getTime() + ", ");
                out.write(diagnostic.getMinCorrelation() + ", ");
                out.write("\n");
            }
            out.close();
        } catch (IOException e) {
        }
    }

    /**
     * clears the csv file in the same directory
     */
    public static void clearCsvFile(String fileName) {
        try {
            File file = new File(fileName);
            file.delete();
            File newFile = new File(fileName);
            newFile.createNewFile();
            //    newFile.close();
        } catch (IOException e) {
        }
    }

    public static void writeToMaster(String fileName, int levels, int numberOfColumns,
            String designName, double maxConstraint) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));


            out.write(designName + ", ");

            out.write(numberOfColumns + ", ");
            out.write(maxConstraint + ", ");
            out.write("\n");

            out.close();
        } catch (IOException e) {
        }
    }

    public static void writeHeadersToMaster(String fileName) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            out.write("DesignFile, ");
            out.write("Columns, ");
            out.write("Constraint, ");
            out.write("\n");

            out.close();
        } catch (IOException e) {
        }
    }

    public static void writeHeadersToDesign(String fileName, int numberFactors, ArrayList<DesignColumn> design, double corr, double ML2, double time) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));

            out.write("corr" + ",");
            out.write("ML2" + ",");
            out.write("time" + ",");
            out.write("\n");
            out.write(corr + ",");
            out.write(ML2 + ",");
            out.write(time + ",");
            out.write("\n");
            //write factor types in first row and discrete levels in second row
            for (int col = 0; col < design.size(); col++) {
                out.write(design.get(col).getType() + ",");
            }
            out.write("\n");
            for (int col = 0; col < design.size(); col++) {
                if (design.get(col).getType().equals("continuous")) {
                    out.write(design.get(col).getLevels() + ",");
                } else {
                    out.write(design.get(col).getDiscreteLevels() + ",");
                }
            }
            out.write("\n");
            for (int col = 0; col < design.size(); col++) {
                out.write(design.get(col).getMode() + ",");
            }
            out.write("\n");


            for (int i = 1; i <= numberFactors; i++) {
                out.write("x" + i + ",");
            }
            out.write("\n");
            out.close();
        } catch (IOException e) {
        }
    }

    public static void writeHeadersToTranslatedDesign(String fileName, int numberFactors, ArrayList<DesignColumn> design) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));



            //write factor types in first rwo and discrete levels in second row
            for (int col = 0; col < design.size(); col++) {
                if (design.get(col).getType().equals("categorical") && design.get(col).getLevels() == 2) {
                    out.write("binary,");
                } else {
                    out.write(design.get(col).getType() + ",");
                }

            }
            out.write("\n");
            for (int col = 0; col < design.size(); col++) {
                if (design.get(col).getType().equals("continuous")) {
                    out.write(design.get(col).getLevels() + ",");
                } else {
                    out.write(design.get(col).getDiscreteLevels() + ",");
                }
            }
            out.write("\n");

            for (int col = 0; col < design.size(); col++) {
                out.write(design.get(col).getMode() + ",");
            }
            out.write("\n");


            for (int i = 1; i <= numberFactors; i++) {
                out.write("x" + i + ",");
            }
            out.write("\n");
            out.close();
        } catch (IOException e) {
        }
    }

    public static void writeHeadersToFullDesign(String fileName, int numberFactors) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));

            for (int i = 1; i <= numberFactors; i++) {
                out.write("x" + i + ",");
            }
            for (int i = 1; i <= numberFactors; i++) {
                out.write("xSq" + i + ",");
            }

            int firstIndex = 1;
            for (int i = 1; i < numberFactors; i++) {
                if (i == 1) {
                    out.write("x1x2,");
                } else {
                    firstIndex = 1 + i;
                    for (int j = 1; j <= firstIndex - 1; j++) {
                        out.write("x" + firstIndex + "x" + j + ",");
                    }
                }
            }
            out.write("\n");
            out.close();
        } catch (IOException e) {
        }
    }

    public static void writeHeadersToDiagnostics(String fileName) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            out.write("Gen Number, ");
            out.write("min, ");
            out.write("max, ");
            out.write("range, ");
            out.write("mean, ");
            out.write("total, ");
            out.write("number cols, ");
            out.write("time (min), ");
            out.write("min corr, ");
            out.write("\n");

            out.close();
        } catch (IOException e) {
        }
    }
}
