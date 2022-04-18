package DesignAlgorithm;

import java.util.Arrays;
import java.util.List;
import static DesignAlgorithm.Correlation.*;


/**
 *
 * @author maccalman
 */
public class RunCorrelation {

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//
//        double[][] matrix = new double[][]{{1, 5, 2}, {4, 2, 54}, {2, 8, 2}, {2, 3, 66}};
//        printMatrix(matrix);
//        calculateCorrelations(matrix);
//
//    }

    public static void calculateCorrelations(double[][] matrix) {
        double[] column1 = new double[matrix.length];
        double[] column2 = new double[matrix.length];
        column1 = newColumn(matrix, 0);
        column2 = newColumn(matrix, 2);

        Integer[] cols = new Integer[matrix[0].length];
        for (int i = 1; i <= matrix[1].length; i++) {
            cols[i - 1] = i;
        }

        List<Integer> set = Arrays.asList(cols);
        CombinationGenerator<Integer> cg = new CombinationGenerator<Integer>(set, 2);
        for (List<Integer> combination : cg) {
            column1 = newColumn(matrix, combination.get(0) - 1);
            column2 = newColumn(matrix, combination.get(1) - 1);
            System.out.println(combination + " " + getAlexCorrelation(column1, column2));
        }
    }

    /**
     * passes in the matrix and the column number and returns an array
     * with one dimension that is the  column designated
     * @param matrix
     * @param columnNumber
     * @return
     */
    public static double[] newColumn(double[][] matrix, int columnNumber) {

        double[] newArray = new double[matrix.length];
        int index = 0;
        for (int n = 0; n < matrix.length; n++) {
            newArray[index++] = matrix[n][columnNumber];
        }
        return newArray;
    }

    public static void printMatrix(double[][] matrix) {
        int ROWS = matrix.length;
        int COLS = matrix[1].length;
        String output = "";

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                output = " " + matrix[row][col];
                System.out.print(output);
            }
            System.out.println();
        }
    }

    public static void printColumn(double[] newArray) {
        int ROWS = newArray.length;
        String output = "";

        for (int row = 0; row < ROWS; row++) {

            output = " " + newArray[row];
            System.out.print(output);

            System.out.println();
        }
    }

    /**
     * Calculate the factorial of n.
     *
     * @param n the number to calculate the factorial of.
     * @return n! - the factorial of n.
     */
    public static int fact(int n) {

        // Base Case:
        //    If n <= 1 then n! = 1.
        if (n <= 1) {
            return 1;
        } // Recursive Case:
        //    If n > 1 then n! = n * (n-1)!
        else {
            return n * fact(n - 1);
        }
    }
}
