
package DesignAlgorithm;

import java.util.ArrayList;

/**
 * class calculates the ML2 space filling metric (smaller the better)
 * must normalize matrix to 0 to 1
 * @author maccalman
 */
public class ML2 {

    public static double calculateML2(ArrayList<DesignColumn> design){
        double[][] matrix = new double[design.get(0).getLevels()][design.size()];
        matrix = normalize(design);
        return calculateFirst(matrix) + calculateSecond(matrix) + calculateThird(matrix);
    }

    public static double[][] normalize(ArrayList<DesignColumn> design){
        double[][] normalizedMatrix = new double[design.get(0).getLevels()][design.size()];
        long n, k;
        n = design.get(0).getLevels();
        k = design.size();
        for (int j = 0; j < k; j++){
            double max = 0;
            double min = 99999999;
            for (int i = 0; i < n; i++){
                normalizedMatrix[i][j] = design.get(j).getDesignColumn()[i];
                //find max and min
                if (normalizedMatrix[i][j] > max){
                    max = normalizedMatrix[i][j];
                }
                if (normalizedMatrix[i][j] < min){
                    min = normalizedMatrix[i][j];
                }
            }
            //normalize
            for (int i = 0; i < n; i++){
                normalizedMatrix[i][j] = (normalizedMatrix[i][j] - min)/(max - min);
            }
        }
        return normalizedMatrix;
    }

    public static double calculateFirst(double[][] normalizedMatrix){
        double k = normalizedMatrix[1].length;
        double first = Math.pow(4.0/3.0, k);
        return first;
    }

    public static double calculateSecond(double[][] normalizedMatrix){
        long n, k;
        n = normalizedMatrix.length;
        k = normalizedMatrix[1].length;
        double index;
        double product = 1;
        double sum = 0;
        for (int d = 0; d <  n; d++){
            for (int i = 0; i < k; i++){
                index = 3 - normalizedMatrix[d][i]*normalizedMatrix[d][i];
                product = product * index;
            }
            sum = sum + product;
            product = 1;
        }
        double second = -(Math.pow(2.0, 1-k)/n)*sum;
        return second;
    }

    public static double calculateThird(double[][] normalizedMatrix){
        long n, k;
        n = normalizedMatrix.length;
        k = normalizedMatrix[1].length;
        double index;
        double product = 1;
        double sum1 = 0;
        double sum2 = 0;

        for (int d = 0; d < n; d++){
            for (int j = 0; j < n; j++){
                for (int i = 0; i < k; i++){
                    index = 2.0 - Math.max(normalizedMatrix[d][i], normalizedMatrix[j][i]);
                    product = product * index;
                }
                sum1 = sum1 + product;
                product = 1;
            }
            sum2 = sum2 + sum1;
            sum1 = 0;
        }
        double rows = n;
        double third = 1/(rows*rows)*sum2;
        return third;
    }

    public static void readInDesign(){
        String fileName = "ML2Calc/ML2.csv";
        DOEFactorArray stringdDesign = new DOEFactorArray(fileName);
        int numberOfFactors = stringdDesign.getNumberOfFactors();
        int numberOfDesignPoints = stringdDesign.getNumberOfDesignPoints();

         double[][] matrix = new double[numberOfDesignPoints][numberOfFactors];
         for (int j = 0; j < numberOfFactors; j++) {
            for (int i = 1; i < numberOfDesignPoints + 1; i++) {
                matrix[i-1][j] = Double.parseDouble(stringdDesign.getFactorArray()[i][j]);
            }
        }
        System.out.println("ML2 = " + calculateReadInML2(matrix));
    }

     public static double calculateReadInML2(double[][] design){
        double[][] matrix = new double[design.length][design[1].length];
        matrix = normalizeReadIn(design);
        return calculateFirst(matrix) + calculateSecond(matrix) + calculateThird(matrix);
    }

        public static double[][] normalizeReadIn(double[][] design){
        double[][] normalizedMatrix = new double[design.length][design[1].length];
        long n, k;
        n = design.length;
        k = design[1].length;
        for (int j = 0; j < k; j++){
            double max = 0;
            double min = 99999999;
            for (int i = 0; i < n; i++){
                normalizedMatrix[i][j] = design[i][j];
                //find max and min
                if (normalizedMatrix[i][j] > max){
                    max = normalizedMatrix[i][j];
                }
                if (normalizedMatrix[i][j] < min){
                    min = normalizedMatrix[i][j];
                }
            }
            //normalize
            for (int i = 0; i < n; i++){
                normalizedMatrix[i][j] = (normalizedMatrix[i][j] - 1)/(max - min);
            }
        }
        return normalizedMatrix;
    }

//     public static void main(String[] args) {
//         readInDesign();
//     }
}
