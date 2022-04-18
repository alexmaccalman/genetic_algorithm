package DesignAlgorithm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
//import net.goui.util.MTRandom;
import DesignAlgorithm.MTRandom;

/**
 *
 * @author maccalman
 */
public class RandomColumnCreator {

    /**
     * pass the number of levels, return a random latin hypercube column
     * fill a double array with random numbers, put them in a hashMap with
     * the random number as the key and the index as the value.
     * Sort the double array with random numbers then fill the integer array
     * by iterating down the sorted double array with the random numbers
     * and get the integer index values from the hashMap using the random key number.
     * @param levels
     * @return
     */
    public static double[] createRandomColumn(int levels, MTRandom randomNumber) {
        double[] designColumn = new double[levels];
        double cum = 0;
        double mean = 0;
        double[] randomArray = new double[levels];
        HashMap<Double, Integer> indexMap = new HashMap<Double, Integer>();
        // double rand;
        for (int i = 0; i < levels; i++) {
            //   rand = randomNumber.nextDouble();
            randomArray[i] = randomNumber.nextDouble();
            indexMap.put(randomArray[i], i);
        }
        Arrays.sort(randomArray);
        for (int i = 0; i < levels; i++) {
            designColumn[i] = indexMap.get(randomArray[i]) + 1;
            cum += designColumn[i];
        }
        mean = cum / levels;
        centerColumn(designColumn, mean);

        return designColumn;
    }

    public static double[] createDiscreteRandomColumn(int levels, int discreteLevels, MTRandom randomNumber) {
        int setUpRows = (int) (Math.round(levels / discreteLevels + 0.5)) * discreteLevels; //rounds up
        int ideal = (int) Math.round(levels / discreteLevels - 0.5); //rounds down
        double[] setUpColumn = new double[setUpRows];
        double[] designColumn = new double[levels];
        HashMap<Double, Integer> indexMap = new HashMap<Double, Integer>();
        double[] randomArray = new double[setUpRows];
        double cum = 0;
        double mean = 0;
        for (int i = 0; i < setUpRows; i = i + discreteLevels) {
            for (int j = 0; j < discreteLevels; j++) {
                setUpColumn[i + j] = j + 1;
            }
        }

        for (int i = 0; i < setUpRows; i++) {
            //   rand = randomNumber.nextDouble();
            randomArray[i] = randomNumber.nextDouble();
            indexMap.put(randomArray[i], i);
        }
        Arrays.sort(randomArray);
        for (int i = 0; i < levels; i++) {
            designColumn[i] = setUpColumn[indexMap.get(randomArray[i])];
            cum += designColumn[i];
        }
        mean = cum / levels;

        double balance = checkBalance(designColumn, ideal, discreteLevels);
        centerColumn(designColumn, mean);

        return designColumn;
    }

    /**
     * this balance check must be done before centering.  All discrete levels should
     * be able to be casted to integers without loss of precision (i.e. 2.0 casted to 2)
     * @param array
     * @param ideal
     * @param discreteLevels
     * @return
     */
    public static double checkBalance(double[] array, int ideal, int discreteLevels) {
        double balance = 0;
        int[] discreteCount = new int[discreteLevels];
        //count discrete levels
        int discreteLevel;
        int maxDiscreteLevel = 0;
        for (int i = 0; i < array.length; i++) {
            discreteLevel = (int) array[i];
            discreteCount[discreteLevel - 1] += 1;
            if (discreteCount[discreteLevel - 1] > maxDiscreteLevel) {
                maxDiscreteLevel = discreteCount[discreteLevel - 1];
            }
        }
        balance = (double) maxDiscreteLevel / (double) ideal - 1;

        return balance;
    }

    public static double[] centerColumn(double[] point, double mean) {
        for (int i = 0; i < point.length; i++) {
            point[i] = point[i] - mean;
        }
        return point;
    }
}
