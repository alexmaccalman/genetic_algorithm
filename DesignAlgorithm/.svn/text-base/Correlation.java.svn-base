package DesignAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author maccalman
 */
public class Correlation {

    /**
     *   calculate Pearson product-moment correlation coefficient
     * @param point1     first point
     * @param point2     second point
     * @return     Pearson correlation coefficient
     */
    public static double getCorrelation(double[] point1, double[] point2) {
        double suma = 0;
        double sumb = 0;
        double sumaSq = 0;
        double sumbSq = 0;
        double pSum = 0;
        int n = point1.length;
        for (int i = 0; i < point1.length; i++) {
            suma = suma + point1[i];
            sumb = sumb + point2[i];
            sumaSq = sumaSq + point1[i] * point1[i];
            sumbSq = sumbSq + point2[i] * point1[i];
            pSum = pSum + point1[i] * point2[i];
        }
        double numerator = pSum - suma * sumb / n;
        double test = (sumaSq - suma * suma / n) * (sumbSq - sumb * sumb / n);
        double denominator = Math.sqrt((sumaSq - suma * suma / n) * (sumbSq - sumb * sumb / n));
        return numerator / denominator;
    }

    /**
     * covariance of point 1 and point2 divided by the standard deviations of point1 and point 2
     * @param point1
     * @param point2
     * @return
     */
    public static double getAlexCorrelation(double[] point1, double[] point2) {
        double meanX = 0;
        double meanY = 0;
        double sumXiminusMean = 0;
        double sumYiminusMean = 0;
        double sumXiminusMeanSq = 0;
        double sumYiminusMeanSq = 0;
        double cov = 0; //numerator
        double cumX = 0;
        double cumY = 0;
        int n = point1.length;
        for (int i = 0; i < point1.length; i++) {
            cumX = cumX + point1[i];
            cumY = cumY + point2[i];
        }
        meanX = cumX / n;
        meanY = cumY / n;

        for (int i = 0; i < point1.length; i++) {
            sumXiminusMean = point1[i] - meanX;
            sumYiminusMean = point2[i] - meanY;
            cov = cov + sumXiminusMean * sumYiminusMean; //numerator

            sumXiminusMeanSq += sumXiminusMean * sumXiminusMean;
            sumYiminusMeanSq += sumYiminusMean * sumYiminusMean;
        }
        return cov / (Math.sqrt(sumXiminusMeanSq) * Math.sqrt(sumYiminusMeanSq));
    }

    /**
     * uses the combination generator.
     * @param design
     */
    public static void calculateCorrelations(ArrayList<DesignColumn> design) {
        double[] column1;
        double[] column2;
        DesignColumn designColumn1;
        DesignColumn designColumn2;
        double max = 0;
        double corr = 999;
        boolean showCorr = true;

        if (design.size() <= 1) {
            System.out.println("only one column");
        } else {
            //create an integer array that is the size of the design
            Integer[] cols = new Integer[design.size()];
            for (int i = 1; i <= design.size(); i++) {
                cols[i - 1] = i;
            }
            //create a list with the Integer array
            List<Integer> set = Arrays.asList(cols);
            //create a combination generator
            CombinationGenerator<Integer> cg = new CombinationGenerator<Integer>(set, 2);
            for (List<Integer> combination : cg) {
                designColumn1 = design.get(combination.get(0) - 1);
                designColumn2 = design.get(combination.get(1) - 1);
                if (designColumn1.getType().equals("dummy") && designColumn2.getType().equals("dummy") && designColumn1.getCatIndex() == designColumn2.getCatIndex()) {
                    //do nothing                    
                    showCorr = false;
                } else {
                    column1 = designColumn1.getDesignColumn();
                    column2 = designColumn2.getDesignColumn();
                    corr = getAlexCorrelation(column1, column2);
                    if (Math.abs(corr) > max) {
                        max = Math.abs(corr);
                    }
                }
                if (showCorr) {
                    System.out.println(combination + " " + corr);
                } else {
                    System.out.println(combination + " dummy with dummy");
                }
                showCorr = true;
            }

            System.out.println("max corr: " + max);
            System.out.println("number of columns: " + design.size());
        }
    }

    public static void calculateIntersWithInters(ArrayList<DesignColumn> interactions) {
        double[] column1;
        double[] column2;
        DesignColumn designColumn1;
        DesignColumn designColumn2;
        double max = 0;
        double corr = 999;
        boolean showCorr = true;
        if (interactions.size() <= 1) {
            System.out.println("only one column");
        } else {
            //create an integer array that is the size of the design
            Integer[] cols = new Integer[interactions.size()];
            for (int i = 1; i <= interactions.size(); i++) {
                cols[i - 1] = i;
            }
            //create a list with the Integer array
            List<Integer> set = Arrays.asList(cols);
            //create a combination generator
            CombinationGenerator<Integer> cg = new CombinationGenerator<Integer>(set, 2);
            for (List<Integer> combination : cg) {
                designColumn1 = interactions.get(combination.get(0) - 1);
                designColumn2 = interactions.get(combination.get(1) - 1);
                int cat1 = designColumn1.getCatIndex();
                int cat2 = designColumn2.getCatIndex();
                //if cat's equal each other then the are dummy varaiables from the same categorical factor.  If they both equal zero
          
                    if (designColumn1.getType().equals("dummy") && designColumn2.getType().equals("dummy") && cat1 == cat2 && cat1 != 0 && cat2 != 0) {
                        //do nothing
                        showCorr = false;
                    } else {
                        column1 = designColumn1.getDesignColumn();
                        column2 = designColumn2.getDesignColumn();
                        corr = getAlexCorrelation(column1, column2);
                        if (Math.abs(corr) > max) {
                            max = Math.abs(corr);
                        }
                    }
           



                if (showCorr) {
                    System.out.println(combination + " " + corr);
                } else {
                    System.out.println(combination + " dummy with dummy");
                }
                showCorr = true;
            }

            System.out.println("max corr: " + max);
            System.out.println("number of inter columns: " + interactions.size());
        }
    }

    /**
     * calculates main with quadratics
     * @param design
     * @param quads
     */
    public static void calculateQuadraticCorrelations(ArrayList<DesignColumn> design,
            ArrayList<DesignColumn> quads) {
        double corr;
        double[] designDoubleArray;
        double[] quadDoubleArray;

        if (design.size() <= 1) {
            System.out.println("only one column");
        } else {
            for (int designIndex = 0; designIndex < design.size(); designIndex++) {
                for (int quadIndex = 0; quadIndex < quads.size(); quadIndex++) {

                    designDoubleArray = design.get(designIndex).getDesignColumn();
                    quadDoubleArray = quads.get(quadIndex).getDesignColumn();
                    corr = getAlexCorrelation(designDoubleArray, quadDoubleArray);

                    if (corr > .05 || corr < -.05) {
                        //            System.out.println("we have a problem corr > .05");
                    }

                    //print out correlations
                    System.out.println("[" + (designIndex + 1) + ", " + (quadIndex + 1) + "^2] " + corr);
                    //    }
                }
            }
        }
    }

    /**
     * calculates the correlations of the main with the interactions
     * @param design
     * @param interactions
     */
    public static void calculateInteractionCorrelations(ArrayList<DesignColumn> design,
            ArrayList<DesignColumn> interactions) {

        double corr;
        DesignColumn designDoubleArray;
        DesignColumn interDoubleArray;

        if (design.size() <= 1) {
            System.out.println("only one column");
        } else {
            //for each of the columns in the design matrix
            for (int designIndex = 0; designIndex < design.size(); designIndex++) {

                for (int i = 0; i < interactions.size(); i++) {
                    //     if (designIndex != interCol.getKey()) {
                    designDoubleArray = design.get(designIndex);
                    interDoubleArray = interactions.get(i);
                    corr = getAlexCorrelation(designDoubleArray.getDesignColumn(), interDoubleArray.getDesignColumn());

                    if (corr > .05 || corr < -.05) {
                        //  System.out.println("we have a problem corr > .05");
                    }

                    //print out correlations
                    System.out.println("[" + (designIndex + 1) + ", " + (i + 1) + " " + corr);
                    //    }
                }
            }
        }
    }

    /**
     * combines two array list together.  The first array is the design and the
     * second array are the quadratics or interactions.  We combine these in order to calculate
     * correlations against the design and the interactions.
     * @param arrayList1
     * @param arrayList2
     * @return
     */
    public static ArrayList<DesignColumn> combineArrayLists(ArrayList<DesignColumn> design,
            ArrayList<DesignColumn> secondArray, ArrayList<DesignColumn> thirdArray) {
        ArrayList<DesignColumn> combinedArray = new ArrayList<DesignColumn>();
        //add design to new array
        for (DesignColumn column : design) {
            combinedArray.add(column);
        }
        //add second array to new array
        for (DesignColumn column : secondArray) {
            combinedArray.add(column);
        }

        for (DesignColumn interaction : thirdArray) {
            combinedArray.add(interaction);
        }
        return combinedArray;
    }

    public static double findMaxCorrelation(ArrayList<DesignColumn> design, ArrayList<DesignColumn> quadratics,
            ArrayList<DesignColumn> interactions, String mode) {
        double max = 0;
        if (mode.equals("MI")) {
            // calculate main with interactions
            double corr = 0;
            DesignColumn designDoubleArray;
            DesignColumn interDoubleArray;
            if (design.size() <= 1) {
                //do nothing
            } else {
                //for each of the columns in the design matrix
                for (int designIndex = 0; designIndex < design.size(); designIndex++) {
                    for (DesignColumn interCol : interactions) {
                        //     if (designIndex != interCol.getKey()) {
                        designDoubleArray = design.get(designIndex);
                        interDoubleArray = interCol;
                        corr = getAlexCorrelation(designDoubleArray.getDesignColumn(), interDoubleArray.getDesignColumn());
                        if (Math.abs(corr) > max) {
                            max = Math.abs(corr);
                        }
                    }
                }
            }

            //calculate interactions with interactions
            double[] column1;
            double[] column2;
            DesignColumn designColumn1;
            DesignColumn designColumn2;
            if (interactions.size() <= 1) {
                //do nothing
            } else {
                //create an integer array that is the size of the design
                Integer[] cols = new Integer[interactions.size()];
                for (int i = 1; i <= interactions.size(); i++) {
                    cols[i - 1] = i;
                }
                //create a list with the Integer array
                List<Integer> set = Arrays.asList(cols);
                //create a combination generator
                CombinationGenerator<Integer> cg = new CombinationGenerator<Integer>(set, 2);
                for (List<Integer> combination : cg) {
                    designColumn1 = interactions.get(combination.get(0) - 1);
                    designColumn2 = interactions.get(combination.get(1) - 1);
                    int cat1 = designColumn1.getCatIndex();
                    int cat2 = designColumn2.getCatIndex();
                    if (designColumn1.getType().equals("dummy") && designColumn2.getType().equals("dummy") && cat1 == cat2 && cat1 != 0 && cat2 != 0) {
                        //do nothing
                    } else {
                        column1 = designColumn1.getDesignColumn();
                        column2 = designColumn2.getDesignColumn();
                        corr = getAlexCorrelation(column1, column2);
                        if (Math.abs(corr) > max) {
                            max = Math.abs(corr);
                        }
                    }
                }
            }
        }

        if (mode.equals("MQ")) {
            // calculate main with quadratics
            double corr = 0;
            double[] designDoubleArray;
            double[] quadDoubleArray;
            if (design.size() <= 1) {
                //do nothing
            } else {
                for (int designIndex = 0; designIndex < design.size(); designIndex++) {
                    for (int quadIndex = 0; quadIndex < quadratics.size(); quadIndex++) {
                        //   if (designIndex != quadIndex) {
                        designDoubleArray = design.get(designIndex).getDesignColumn();
                        quadDoubleArray = quadratics.get(quadIndex).getDesignColumn();
                        corr = getAlexCorrelation(designDoubleArray, quadDoubleArray);
                        if (Math.abs(corr) > max) {
                            max = Math.abs(corr);
                        }
                    }
                }
            }
            //calculate quadratics with quadratics
            double[] column1;
            double[] column2;
            if (quadratics.size() <= 1) {
                //do nothing
            } else {
                //create an integer array that is the size of the design
                Integer[] cols = new Integer[quadratics.size()];
                for (int i = 1; i <= quadratics.size(); i++) {
                    cols[i - 1] = i;
                }
                //create a list with the Integer array
                List<Integer> set = Arrays.asList(cols);

                //create a combination generator
                CombinationGenerator<Integer> cg = new CombinationGenerator<Integer>(set, 2);
                for (List<Integer> combination : cg) {
                    column1 = quadratics.get(combination.get(0) - 1).getDesignColumn();
                    column2 = quadratics.get(combination.get(1) - 1).getDesignColumn();
                    corr = getAlexCorrelation(column1, column2);
                    if (Math.abs(corr) > max) {
                        max = Math.abs(corr);
                    }
                }


            }
        }

        if (mode.equals("MQI")) {
            //calculate main with quadratics

            double corr = 0;
            double[] designDoubleArray;
            double[] quadDoubleArray;
            if (design.size() <= 1) {
                //do nothing
            } else {
                //calculate main with quads
                for (int designIndex = 0; designIndex < design.size(); designIndex++) {
                    for (int quadIndex = 0; quadIndex < quadratics.size(); quadIndex++) {
                        //   if (designIndex != quadIndex) {
                        designDoubleArray = design.get(designIndex).getDesignColumn();
                        quadDoubleArray = quadratics.get(quadIndex).getDesignColumn();
                        corr = getAlexCorrelation(designDoubleArray, quadDoubleArray);
                        if (Math.abs(corr) > max) {
                            max = Math.abs(corr);
                        }
                    }
                }
            }
            //calculate quadratics with quadratics
            double[] column1;
            double[] column2;

            if (quadratics.size() <= 1) {
                //do nothing
            } else {
                //create an integer array that is the size of the design
                Integer[] cols = new Integer[quadratics.size()];
                for (int i = 1; i <= quadratics.size(); i++) {
                    cols[i - 1] = i;
                }
                //create a list with the Integer array
                List<Integer> set = Arrays.asList(cols);
                //create a combination generator           
                CombinationGenerator<Integer> cg = new CombinationGenerator<Integer>(set, 2);
                for (List<Integer> combination : cg) {
                    column1 = quadratics.get(combination.get(0) - 1).getDesignColumn();
                    column2 = quadratics.get(combination.get(1) - 1).getDesignColumn();
                    corr = getAlexCorrelation(column1, column2);
                    if (Math.abs(corr) > max) {
                        max = Math.abs(corr);
                    }
                }
            }
            // calculate interactions with quadraitcs
            double[] quadraticsDoubleArray;
            double[] interDoubleArray;
            if (design.size() <= 1) {
                //do nothing
            } else {
                //for each of the columns in the design matrix
                for (int designIndex = 0; designIndex < quadratics.size(); designIndex++) {
                    for (DesignColumn interCol : interactions) {
                        //     if (designIndex != interCol.getKey()) {
                        quadraticsDoubleArray = quadratics.get(designIndex).getDesignColumn();
                        interDoubleArray = interCol.getDesignColumn();
                        corr = getAlexCorrelation(quadraticsDoubleArray, interDoubleArray);
                        if (Math.abs(corr) > max) {
                            max = Math.abs(corr);
                        }
                    }
                }
            }
            // calculate interactions with interactions
            DesignColumn designColumn1;
            DesignColumn designColumn2;
            if (interactions.size() <= 1) {
                //do nothing
            } else {
                //create an integer array that is the size of the design
                Integer[] cols = new Integer[interactions.size()];
                for (int i = 1; i <= interactions.size(); i++) {
                    cols[i - 1] = i;
                }
                //create a list with the Integer array
                List<Integer> set = Arrays.asList(cols);
                //create a combination generator
                CombinationGenerator<Integer> cg = new CombinationGenerator<Integer>(set, 2);
                for (List<Integer> combination : cg) {
                    designColumn1 = interactions.get(combination.get(0) - 1);
                    designColumn2 = interactions.get(combination.get(1) - 1);
                    if (designColumn1.getType().equals("dummy") && designColumn2.getType().equals("dummy") && designColumn1.getCatIndex() == designColumn2.getCatIndex()) {
                        //do nothing
                    } else {
                        column1 = designColumn1.getDesignColumn();
                        column2 = designColumn2.getDesignColumn();
                        corr = getAlexCorrelation(column1, column2);
                        if (Math.abs(corr) > max) {
                            max = Math.abs(corr);
                        }
                    }
                }

            }


            // calculate interactions with main
            if (design.size() <= 1) {
                //do nothing
            } else {
                //for each of the columns in the design matrix
                for (int designIndex = 0; designIndex < design.size(); designIndex++) {
                    for (DesignColumn interCol : interactions) {
                        //     if (designIndex != interCol.getKey()) {
                        designDoubleArray = design.get(designIndex).getDesignColumn();
                        interDoubleArray = interCol.getDesignColumn();
                        corr = getAlexCorrelation(designDoubleArray, interDoubleArray);
                    }
                }
            }
            if (Math.abs(corr) > max) {
                max = Math.abs(corr);
            }
        }

        //calculate main with main
        double[] column1;
        double[] column2;
        DesignColumn designColumn1;
        DesignColumn designColumn2;
        double corr = 999;

        if (design.size() <= 1) {
            //do nothing
        } else {
            //create an integer array that is the size of the design
            Integer[] cols = new Integer[design.size()];
            for (int i = 1; i <= design.size(); i++) {
                cols[i - 1] = i;
            }
            //create a list with the Integer array
            List<Integer> set = Arrays.asList(cols);
            //create a combination generator
            CombinationGenerator<Integer> cg = new CombinationGenerator<Integer>(set, 2);
            for (List<Integer> combination : cg) {
                designColumn1 = design.get(combination.get(0) - 1);
                designColumn2 = design.get(combination.get(1) - 1);
                if (designColumn1.getType().equals("dummy") && designColumn2.getType().equals("dummy") && designColumn1.getCatIndex() == designColumn2.getCatIndex()) {
                    //do nothing
                } else {
                    column1 = designColumn1.getDesignColumn();
                    column2 = designColumn2.getDesignColumn();
                    corr = getAlexCorrelation(column1, column2);
                    if (Math.abs(corr) > max) {
                        max = Math.abs(corr);
                    }
                }
            }
        }
        return max;
    }
    /*
     * Used to check for correlations among a categorical variable's dummy variables
     */

    public static double maxCorrForDummies(ArrayList<double[]> dummies) {
        double max = 0;
        double[] column1;
        double[] column2;
        double corr;

        if (dummies.size() <= 1) {
            //do nothing
        } else {
            //create an integer array that is the size of the design
            Integer[] cols = new Integer[dummies.size()];
            for (int i = 1; i <= dummies.size(); i++) {
                cols[i - 1] = i;
            }
            //create a list with the Integer array
            List<Integer> set = Arrays.asList(cols);
            //create a combination generator
            CombinationGenerator<Integer> cg = new CombinationGenerator<Integer>(set, 2);
            for (List<Integer> combination : cg) {
                column1 = dummies.get(combination.get(0) - 1);
                column2 = dummies.get(combination.get(1) - 1);
                corr = getAlexCorrelation(column1, column2);
                if (Math.abs(corr) > max) {
                    max = Math.abs(corr);
                }
            }
        }
        return max;
    }

    public static double[] centerColumn(double[] point, double mean) {
        for (int i = 0; i < point.length; i++) {
            point[i] = point[i] - mean;
        }
        return point;
    }
}
