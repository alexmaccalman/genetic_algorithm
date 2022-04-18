package DesignAlgorithm;

//import net.goui.util.MTRandom;
import DesignAlgorithm.MTRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
//import java.util.Random;
import static DesignAlgorithm.Correlation.*;
//import static matrix.RandomColumnCreator.*;

/**
 *
 * @author maccalman
 */
public class DesignColumn {

    private int levels;
    private double correlation;
    private double inverseCorrelation;
    private double fitness;
    private double[] designColumn;
    private double[] quadratic;
    private ArrayList<double[]> interactions; // this container will have all the dummy variables of a categorical factor
    //three way
    private boolean hasCorrelation;
    private double mean;
    private double[] basis;// used only for continuous factors
    private String type;
    private String mode;
    private int discreteLevels;
    private MTRandom randomNumber;
    private double balance;
    //private double[][] dummy;
    private ArrayList<double[]> dummy;
    private int catIndex;
    private int idealLevels;
    private ArrayList<Integer[]> interNames;
    private boolean startCol;

    //  private ArrayList<DesignColumn> candidateInteractions;
    public DesignColumn(int levels, boolean fillRandomly, MTRandom randomNumber, String type, int discreteLevels) {
        this.levels = levels;
        designColumn = new double[levels];
        basis = new double[levels];
        this.interactions = new ArrayList<double[]>();//assign size later.
        //three way
        this.type = type;
        this.discreteLevels = discreteLevels;
        this.setCatIndex(0);
        this.startCol = false;

       // this.mode = mode;
        
        this.randomNumber = randomNumber;
        if (fillRandomly && type.equals("continuous")) {
            this.discreteLevels = this.levels;
            designColumn = createRandomColumn(levels, randomNumber);
            centerColumn(designColumn);
            System.arraycopy(designColumn, 0, basis, 0, designColumn.length);
            this.quadratic = createQuadratic();
        }

        int attempts = 0;
        int maxAttempts = 50;
        double corr;
        double balance;
        double[] bestCol = new double[levels];
        double bestMean = 0;
        double bestBalance = 999999;
        if (fillRandomly && type.equals("discrete")) {

            

            

            //while balance is > .1 and balance attempts = limit
            //save a clone of the best balanced design column
            do{
                designColumn = createDiscreteRandomColumn(levels, discreteLevels, randomNumber);
                attempts += 1;
                centerColumn(designColumn);
                this.quadratic = createQuadratic();
                corr = Math.abs(getAlexCorrelation(designColumn, quadratic));

                if(attempts > maxAttempts){
                   break;
                }
                balance = getBalance();
                //System.out.println("balance: " + balance + " attempt " + attempts);

                if (balance < bestBalance){
                    bestCol = designColumn.clone();
                    bestMean = getMean();
                    bestBalance = balance;
                }
            }while(corr > 0.05 || balance > .1);

            


         //   centerColumn(designColumn);
         //   this.quadratic = createQuadratic();
        }
        if (fillRandomly && type.equals("categorical")) {
            
            //do until max attempts and meets balance constraint
            //designColumn = createDiscreteRandomColumn(levels, discreteLevels, randomNumber);

            
            //while balance is > .1 and balance attempts = limit
            //save a clone of the best balanced design column
            do{
                designColumn = createDiscreteRandomColumn(levels, discreteLevels, randomNumber);
                attempts += 1;
                if(attempts > maxAttempts){
                   break;
                }
                balance = getBalance();
                //System.out.println("balance: " + balance + " attempt " + attempts);
                if (balance < bestBalance){
                    bestCol = designColumn.clone();
                    bestMean = getMean();
                    bestBalance = balance;
                }
            }while(balance > .2);


            createDummy(discreteLevels);
            centerColumn(designColumn);  
            this.interNames = new ArrayList<Integer[]>();
        }

        if (attempts>maxAttempts){
                designColumn = bestCol;
                setMean(bestMean);
                setBalance(bestBalance);
            }
        hasCorrelation = false;
    }

    public double[] createQuadratic() {
        double[] quadratic = new double[levels];
        for (int i = 0; i < quadratic.length; i++) {
            quadratic[i] = this.designColumn[i] * this.designColumn[i];
        }
        return quadratic;
    }

    /**
     * creates the dummy variables for the categorical factors.  The number of dummy columns is
     * equal to the number of discreteLevels -1.   We want an arrayList
     * because we want instant access to the column as an array[].  We can't use a double[][]
     * because we don't have instant access to the second dimension (column).
     * @param discreteLevels
     */
    public void createDummy(int discreteLevels) {

        setDummy(new ArrayList<double[]>(discreteLevels - 1));
        //create lookup table to fill in the dummy
        double[][] lookup = new double[discreteLevels][discreteLevels - 1];
        for (int i = 0; i < discreteLevels; i++) {//row
            for (int j = 0; j < discreteLevels - 1; j++) {//col
                if (i == j) {
                    lookup[i][j] = 1;
                } else if (i == discreteLevels - 1) {
                    lookup[i][j] = 0;// this can either be -1 or 0
                } else {
                    lookup[i][j] = 0;
                }
            }
        }
        //fill in the setUpArray. setUpArray is used to create the dummy arrayList
        for (int dummyCol = 0; dummyCol < discreteLevels - 1; dummyCol++) {
            double[] col = new double[levels];
            for (int designPoint = 0; designPoint < levels; designPoint++) {
                int discretePoint = (int) designColumn[designPoint];
                col[designPoint] = lookup[discretePoint - 1][dummyCol];
            }
           // centerColumn(col);
            dummy.add(dummyCol, col);
        }
    }

    /**
     * pass the number of levels, return a random Latin hypercube column
     * fill a double array with random numbers, put them in a hashMap with
     * the random number as the key and the index as the value.
     * Sort the double array with random numbers then fill the integer array
     * by iterating down the sorted double array with the random numbers
     * and get the integer index values from the hashMap using the random key number.
     * @param levels
     * @return
     */
    public double[] createRandomColumn(int levels, MTRandom randomNumber) {
        double[] designColumn = new double[levels];
        double cum = 0;
        //double mean = 0;
        double[] randomArray = new double[levels];
        HashMap<Double, Integer> indexMap = new HashMap<Double, Integer>();
        // double rand;
        for (int i = 0; i
                < levels; i++) {
            //   rand = randomNumber.nextDouble();
            randomArray[i] = randomNumber.nextDouble();
            indexMap.put(randomArray[i], i);
        }
        Arrays.sort(randomArray);
        for (int i = 0; i
                < levels; i++) {
            designColumn[i] = indexMap.get(randomArray[i]) + 1;//put jiggle in here if we want (before we center it)
            cum += designColumn[i];
        }
        setMean(cum / levels);

        return designColumn;
    }

    public double[] createDiscreteRandomColumn(int levels, int discreteLevels, MTRandom randomNumber) {
        int setUpRows = (int) (Math.round(levels / discreteLevels + 0.5)) * discreteLevels; //rounds up
        //int ideal = (int) Math.round(levels / discreteLevels - 0.5); //rounds down
        double ideal = (double)levels/(double)discreteLevels;
        double[] setUpColumn = new double[setUpRows];
        double[] designColumn = new double[levels];
        HashMap<Double, Integer> indexMap = new HashMap<Double, Integer>();
        double[] randomArray = new double[setUpRows];
        double cum = 0;
        // double mean = 0;
        for (int i = 0; i
                < setUpRows; i = i + discreteLevels) {
            for (int j = 0; j
                    < discreteLevels; j++) {
                setUpColumn[i + j] = j + 1;
            }
        }
        for (int i = 0; i < setUpRows; i++) {
            //   rand = randomNumber.nextDouble();
            randomArray[i] = randomNumber.nextDouble();
            indexMap.put(randomArray[i], i);
        }
        Arrays.sort(randomArray);
        for (int i = 0; i
                < levels; i++) {
            designColumn[i] = setUpColumn[indexMap.get(randomArray[i])];
            cum += designColumn[i];
        }
        setMean(cum / levels);
        //setIdealLevels((int)ideal);
        double balance = checkBalance(designColumn, ideal, discreteLevels);
        setBalance(balance);

        return designColumn;
    }

    /**
     * this balance check must be done before centering because all discrete levels should
     * be able to be casted to integers without loss of precision (i.e. 2.0 casted to 2)
     * @param array
     * @param ideal
     * @param discreteLevels
     * @return
     */
//    public double checkBalance(double[] array, int ideal, int discreteLevels) {
//        double balance = 0;
//
//
//        int[] discreteCount = new int[discreteLevels];
//        //count discrete levels
//        int discreteLevel;
//        int maxDiscreteLevel = 0;
//        for (int i = 0; i
//                < array.length; i++) {
//            discreteLevel = (int) array[i];
//            discreteCount[discreteLevel - 1] += 1;
//            if (discreteCount[discreteLevel - 1] > maxDiscreteLevel) {
//                maxDiscreteLevel = discreteCount[discreteLevel - 1];
//            }
//        }
//        balance = (double) maxDiscreteLevel / (double) ideal - 1;
//        return balance;
//    }

    public double checkBalance(double[] array, double ideal, int discreteLevels) {
        double balance = 0;


        //System.out.println(discreteLevels);
        //System.out.println(ideal);

        int[] discreteCount = new int[discreteLevels];
        //count discrete levels
        int discreteLevel;
        double ratio;
        double maxRatio = 0;
        for (int i = 0; i < array.length; i++) {
            discreteLevel = (int) array[i];
            
            discreteCount[discreteLevel - 1] += 1;
            }
        for (int j = 0; j < discreteCount.length; j++) {
            ratio = Math.abs(((double)discreteCount[j]-ideal)/ ideal);
            if (ratio > maxRatio){
                maxRatio = ratio;
            }
        }
       // System.out.println("check");
        balance = maxRatio;


     
        return balance;



    }

    public double[] centerColumn(double[] point) {
        for (int i = 0; i
                < point.length; i++) {
            point[i] = point[i] - getMean();
        }
        return point;
    }

    public double findMean(double[] array) {
        double cum = 0;
        for (int i = 0; i
                < array.length; i++) {
            cum += array[i];
        }
        return cum / array.length;
    }

    /**
     * @return the levels
     */
    public int getLevels() {
        return levels;
    }

    /**
     * @param levels the levels to set
     */
    public void setLevels(int levels) {
        this.levels = levels;
    }

    /**
     * @return the fitness
     */
    public double getCorrelation() {
        return correlation;
    }

    /**
     * @param fitness the fitness to set
     */
    public void setCorrelation(double fitness) {
        this.correlation = fitness;
    }

    /**
     * @return the intColumn
     */
    public double[] getDesignColumn() {
        return designColumn;


    }

    /**
     * @return the inverseCorrelation
     */
    public double getInverseCorrelation() {
        return inverseCorrelation;
    }

    /**
     * @param inverseCorrelation the inverseCorrelation to set
     */
    public void setInverseCorrelation(double inverseCorrelation) {
        this.inverseCorrelation = inverseCorrelation;
    }

    /**
     * @return the fitness
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * @param fitness the fitness to set
     */
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * @return the candidateInteractions
     */
    //  public ArrayList<DesignColumn> getCandidateInteractions() {
    //      return candidateInteractions;
    //  }
    /**
     * @param candidateInteractions the candidateInteractions to set
     */
    //   public void setCandidateInteractions(ArrayList<DesignColumn> candidateInteractions) {
    //       this.candidateInteractions = candidateInteractions;
    //   }
    /**
     * @return the quadratic
     */
//    public DesignColumn getQuadratic() {
//        return quadratic;
//    }
//
//    /**
//     * @param quadratic the quadratic to set
//     */
//    public void setQuadratic(DesignColumn quadratic) {
//        this.quadratic = quadratic;
//    }
    /**
     * @return the hasFitness
     */
    public boolean getHasCorrelation() {
        return hasCorrelation;
    }

    /**
     * @param hasFitness the hasFitness to set
     */
    public void setHasCorrelation(boolean hasCorrelation) {
        this.hasCorrelation = hasCorrelation;
    }

    /**
     * @return the mean
     */
    public double getMean() {
        return mean;
    }

    /**
     * @param mean the mean to set
     */
    public void setMean(double mean) {
        this.mean = mean;
    }

    /**
     * @return the basis
     */
    public double[] getBasis() {
        return basis;
    }

    /**
     * @param basis the basis to set
     */
    public void setBasis(double[] basis) {
        this.basis = basis;


    }

    /**
     * @return the quadratic
     */
    public double[] getQuadratic() {
        return quadratic;


    }

    /**
     * @param quadratic the quadratic to set
     */
    public void setQuadratic(double[] quadratic) {
        this.quadratic = quadratic;
    }

    /**
     * @return the interactions
     */
    public ArrayList<double[]> getInteractions() {
        return interactions;
    }

    /**
     * @param interactions the interactions to set
     */
    public void setInteractions(ArrayList<double[]> interactions) {
        this.interactions = interactions;
    }

    /**
     * @param designColumn the designColumn to set
     */
    public void setDesignColumn(double[] designColumn) {
        this.designColumn = designColumn;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the order
     */
    public String getOrder() {
        return getMode();
    }

    /**
     * @param order the order to set
     */
    public void setOrder(String order) {
        this.setMode(order);
    }

    /**
     * @return the discreteLevels
     */
    public int getDiscreteLevels() {
        return discreteLevels;
    }

    /**
     * @param discreteLevels the discreteLevels to set
     */
    public void setDiscreteLevels(int discreteLevels) {
        this.discreteLevels = discreteLevels;
    }

    /**
     * @return the randomNumber
     */
    public MTRandom getRandomNumber() {
        return randomNumber;
    }

    /**
     * @param randomNumber the randomNumber to set
     */
    public void setRandomNumber(MTRandom randomNumber) {
        this.randomNumber = randomNumber;
    }

    /**
     * @return the mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * @return the balance
     */
    public double getBalance() {
        return balance;


    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(double balance) {
        this.balance = balance;

    }

    /**
     * @return the dummy
     */
    public ArrayList<double[]> getDummy() {
        return dummy;
    }

    /**
     * @param dummy the dummy to set
     */
    public void setDummy(ArrayList<double[]> dummy) {
        this.dummy = dummy;
    }

    /**
     * @return the catIndex
     */
    public int getCatIndex() {
        return catIndex;
    }

    /**
     * @param catIndex the catIndex to set
     */
    public void setCatIndex(int catIndex) {
        this.catIndex = catIndex;
    }

    /**
     * @return the idealLevels
     */
    public int getIdealLevels() {
        return idealLevels;
    }

    /**
     * @param idealLevels the idealLevels to set
     */
    public void setIdealLevels(int idealLevels) {
        this.idealLevels = idealLevels;
    }

    /**
     * @return the interNames
     */
    public ArrayList<Integer[]> getInterNames() {
        return interNames;
    }

    /**
     * @param interNames the interNames to set
     */
    public void setInterNames(ArrayList<Integer[]> interNames) {
        this.interNames = interNames;
    }

    /**
     * @return the startCol
     */
    public boolean isStartCol() {
        return startCol;
    }

    /**
     * @param startCol the startCol to set
     */
    public void setStartCol(boolean startCol) {
        this.startCol = startCol;
    }
}
