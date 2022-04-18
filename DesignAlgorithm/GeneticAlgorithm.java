/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DesignAlgorithm;

//import net.goui.util.MTRandom;
import DesignAlgorithm.MTRandom;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
//import java.util.Random;
import java.util.ArrayList;
import static DesignAlgorithm.Correlation.*;
import static DesignAlgorithm.WriteOut.*;

/**
 *
 * @author maccalman
 */
public class GeneticAlgorithm {

    private MTRandom randomNumber;
    private int levels;
    private int popNumber;
    private int jigglePopNumber;
    private double maxConstraint;
    private Population population;
    private Population bestPopulation;
    //  private ArrayList<DesignColumn> design;
    //  private ArrayList<DesignColumn> quadratics;
    //   private ArrayList<DesignColumn> interactions;
    private Design design;
    private ArrayList<GenerationDiagnostic> diagnostics;
    private Population nextGeneration;
    private Population jigglePopulation;
    private Population nextJiggleGeneration;
    private HashMap<Integer, LinkedList<DesignColumn>> reserve;
    private double[][] selectionProbs;
    private double valueRho;
    private double copyPortion;
    private double newPortion;
    private double jigglePortion;
    private int jiggleGeneratons;
    private int poolSetSize;
    private ArrayList<DesignColumn> poolCollection;
    private double swapPortion;
    private boolean useValueFunction;
    private String highestMode; //of all the columns what is the highest MQI, MQ, MI or M
    private String mode; // current mode of a column
    private boolean startWithDesign;
    private boolean startWithPopulation;
    private Timer timer;
    private double maxDesignCorrelation;
    private int attempts;
    private int maxAttempts;
    private SimpleStats attemptStats; //stats on correlation during an attempt
    private double avgCorrelationCriteria;
    private DesignColumn bestColumn;
    private double bestColCorr;
    private boolean bestOfPoolSet;
    private int discreteLevels;
    private double[] currentBest;
    private double lowerRange;
    private double upperRange;
    private String columnType;
    private int maxJigGenAttempts;
    private boolean CMDLine;
    private boolean showComments;

    public GeneticAlgorithm(int levels, int popNumber, double maxConstraint,
            double valueRho, double portion, String highestMode,
            int poolSetSize, double swapPortion, boolean useValueFunction, boolean startWithDesign,
            int maxAttempts, double avgCorrelationCriteria,
            boolean bestOfPoolSet, boolean startWithPopulation, double newPortion, MTRandom randomNumber,
            int jigglePopNumber, int jiggleGeneratons,
            double jigglePortion, Design design, int maxJigGenAttempts, boolean CMDLine, boolean showComments) {
        //levels assigned with start design

        this.randomNumber = randomNumber;
        this.startWithDesign = startWithDesign;
        this.startWithPopulation = startWithPopulation;
        this.levels = levels;
//        this.columnType = columnType;
//        this.discreteLevels = discreteLevels;
//        this.mode = mode;
        this.highestMode = highestMode;
        currentBest = new double[levels];
        this.jiggleGeneratons = jiggleGeneratons;
        this.jigglePortion = jigglePortion;
        this.popNumber = popNumber;
        this.jigglePopNumber = jigglePopNumber;
        this.maxConstraint = maxConstraint;
        population = new Population(popNumber);
        bestPopulation = new Population(popNumber);
        jigglePopulation = new Population(jigglePopNumber);

//        design = new ArrayList<DesignColumn>();
//        quadratics = new ArrayList<DesignColumn>();
//        interactions = new ArrayList<DesignColumn>();
        this.design = design;
        // if (startWithDesign) {
        //     initializeWithStartDesign();
        // }
//        else {
//            initialize();
//        }

        diagnostics = new ArrayList<GenerationDiagnostic>();
        nextGeneration = new Population(popNumber);
        nextJiggleGeneration = new Population(popNumber);
        //column 1 is the probabilites (pdf)
        //column 2 is the cdf
        selectionProbs = new double[popNumber][2];
        this.valueRho = valueRho;
        this.copyPortion = portion;
        this.poolSetSize = poolSetSize;

        int capacity = popNumber - (int) (popNumber * (copyPortion + newPortion));
        poolCollection = new ArrayList<DesignColumn>(capacity);

        this.swapPortion = swapPortion;
        this.useValueFunction = useValueFunction;
        timer = new Timer();
        this.maxAttempts = maxAttempts;
        attemptStats = new SimpleStats();

        bestColumn = new DesignColumn(this.levels, false, getRandomNumber(), getColumnType(), getDiscreteLevels());
        bestColCorr = 9999;

        this.bestOfPoolSet = bestOfPoolSet;
        this.newPortion = newPortion;
        this.upperRange = levels - latinHypercubeMean(levels);
        this.lowerRange = 1 - latinHypercubeMean(levels);
        this.maxJigGenAttempts = maxJigGenAttempts;
        this.CMDLine = CMDLine;
        this.showComments = showComments;
    }
    //used to calculate upper and lower bound for continuous factors

    public double latinHypercubeMean(int designPoints) {
        double sum = 0;
        double mean = 0;
        for (int i = 1; i <= designPoints; i++) {
            sum += i;
        }
        mean = sum / designPoints;
        return mean;
    }

    public void initialize() {

        //create first column
        DesignColumn firstDesignColumn = new DesignColumn(getLevels(), true, getRandomNumber(), getColumnType(), getDiscreteLevels());

        getDesign().getMain().add(firstDesignColumn);

        //if checking against quadratics, add the quatradic term to the quadratic arrayList, unless it is categorical
        //categorical factors don't have quadratics
        if (!firstDesignColumn.getType().equals("categorical")) {
            if (getHighestMode().equals("MQ") || getHighestMode().equals("MQI")) {
                DesignColumn quad = new DesignColumn(levels, false, getRandomNumber(), getColumnType(), getDiscreteLevels());
                //grab the column from the population and set its quadratic
                //to the new column just created
                double[] quadCol = new double[levels];
                //squares the first design column
                quadCol = firstDesignColumn.createQuadratic();
                //sets the double[] design column of the newly created design
                // (quadratic) column object
                quad.setDesignColumn(quadCol);
                //add the new quadratic to the quadratic array list
                getDesign().getQuadratics().add(quad);
            }
        }
    }

    public void readInPopulation() {
        //   getPopulation().getPop().clear();

        String fileName = "population/population.csv";
        DOEFactorArray startPopulation = new DOEFactorArray(fileName);
        int numberOfFactors = startPopulation.getNumberOfFactors();
        int numberOfDesignPoints = startPopulation.getNumberOfDesignPoints() + 1;
        for (int i = 0; i < numberOfFactors; i++) {
            double[] col = new double[numberOfDesignPoints];
            for (int j = 0; j < numberOfDesignPoints; j++) {
                col[j] = Double.parseDouble(startPopulation.getFactorArray()[j][i]);
            }
            DesignColumn designCol = new DesignColumn(numberOfDesignPoints, false, getRandomNumber(), getColumnType(), getDiscreteLevels());
            designCol.setDesignColumn(col);
            //this is the wrong mean, need the translated mean
            designCol.setMean(designCol.findMean(col));
            System.arraycopy(col, 0, designCol.getBasis(), 0, col.length);// set the basis
            // getPopulation().getPop().add(designCol);
            getPopulation().getPop()[i] = designCol;
        }
    }

    /**
     * first pop is what is getting copied, the second pop is the updated population
     * @param pop1
     * @param pop2
     */
    public void copyPopulation(Population pop1, Population pop2) {
        //  pop2.getPop().clear();


        for (int i = 0; i < pop1.getPop().length; i++) {
            DesignColumn designCol = new DesignColumn(levels, false, getRandomNumber(), getColumnType(), getDiscreteLevels());
            designCol = pop1.getPop()[i];
            //        System.arraycopy(col.getDesignColumn(), 0, designCol.getBasis(), 0, col.getDesignColumn().length);// set the basis
            // pop2.getPop().add(designCol);
            pop2.getPop()[i] = designCol;
        }
        pop2.setPopMax(pop1.getPopMax());
        pop2.setPopMin(pop1.getPopMin());
        pop2.setPopMean(pop1.getPopMean());
    }

    public void initializeWithStartDesign() {

        String fileName = "Startdesign.csv";
        DOEFactorArray startDesign = new DOEFactorArray(fileName);
        int numberOfFactors = startDesign.getNumberOfFactors();
        int numberOfDesignPoints = startDesign.getNumberOfDesignPoints() - 3;
        //set levels to what is in the cvs file, this may change what is set in the main method
        setLevels(numberOfDesignPoints);

        for (int i = 0; i < numberOfFactors; i++) {
            double[] col = new double[numberOfDesignPoints];
            for (int j = 4; j < numberOfDesignPoints + 4; j++) {//set to 4 with type string at first row and number of discrete levels second at second row
                col[j - 4] = Double.parseDouble(startDesign.getFactorArray()[j][i]);
            }
            String factorType = startDesign.getFactorArray()[0][i];
            int discreteLev = Integer.parseInt(startDesign.getFactorArray()[1][i]);
            String factorMode = startDesign.getFactorArray()[2][i];

            DesignColumn designCol = new DesignColumn(numberOfDesignPoints, false, getRandomNumber(), factorType, discreteLev);
            designCol.setDesignColumn(col);
            designCol.setMode(factorMode);
            //write something that counts the discrete levels
            if (designCol.getType().equals("categorical")) {
                designCol.createDummy(designCol.getDiscreteLevels());
            }

            //this is the wrong mean, need the translated mean
            designCol.setMean(designCol.findMean(col));
            designCol.setDesignColumn(designCol.centerColumn(designCol.getDesignColumn()));
            System.arraycopy(col, 0, designCol.getBasis(), 0, col.length);// set the basis
            getDesign().getMain().add(designCol);

            if (!designCol.getType().equals("categorical")) {
                //categorical factors don't have quadratics
                if (getHighestMode().equals("MQI") || getHighestMode().equals("MQ")) {
                    DesignColumn quad = new DesignColumn(numberOfDesignPoints, false, getRandomNumber(), getColumnType(), getDiscreteLevels());
                    double[] quadCol = new double[numberOfDesignPoints];
                    quadCol = designCol.createQuadratic();
                    quad.setDesignColumn(quadCol);
                    getDesign().getQuadratics().add(quad);
                }
            }
            if (getHighestMode().equals("MQI") || getHighestMode().equals("MI")) {
                ArrayList<DesignColumn> interactionSet = createStartInteractions(designCol, getDesign().getMain());
                for (int k = 0; k < interactionSet.size(); k++) {
                    //  int interactionSize = getInteractions().size();
                    getDesign().getInteractions().add(interactionSet.get(k));
                }
            }

            //MQ3I
        }
    }

    public int countLevels(double[] col) {
        int count = 0;
        ArrayList<Double> levels = new ArrayList<Double>();
        for (int i = 0; i < col.length; i++) {
            if (!levels.contains(col[0])) {
                levels.add(col[0]);
            }
        }
        count = levels.size();
        return count;
    }

    /**
     * 
     * @param arrayList
     */
    public void createQuadsAndInters(ArrayList<DesignColumn> arrayList) {
        getDesign().getQuadratics().clear();
        getDesign().getInteractions().clear();

        ArrayList<DesignColumn> translatedDesign = new ArrayList<DesignColumn>();
        int numberOfFactors = arrayList.size();
        int numberOfDesignPoints = arrayList.get(0).getLevels();

        for (int i = 0; i < numberOfFactors; i++) {
            DesignColumn designCol = new DesignColumn(numberOfDesignPoints, false, getRandomNumber(), arrayList.get(i).getType(), arrayList.get(i).getDiscreteLevels());
            designCol.setDesignColumn(arrayList.get(i).getDesignColumn());

            translatedDesign.add(designCol);
            if (!designCol.getType().equals("categorical")) {//categorical factors don't have quadratics
                if (getHighestMode().equals("MQI") || getHighestMode().equals("MQ")) {
                    DesignColumn quad = new DesignColumn(numberOfDesignPoints, false, getRandomNumber(), getColumnType(), getDiscreteLevels());
                    double[] quadCol = new double[numberOfDesignPoints];
                    quadCol = designCol.createQuadratic();
                    quad.setDesignColumn(quadCol);
                    quad.setType(designCol.getType());
                    quad.setDiscreteLevels(designCol.getDiscreteLevels());
                    quad.setMode(designCol.getMode());
                    getDesign().getQuadratics().add(quad);
                }
            }

            if (getHighestMode().equals("MQI") || getHighestMode().equals("MI")) {
                ArrayList<DesignColumn> interactionSet = createStartInteractions(designCol, translatedDesign);
                for (int k = 0; k < interactionSet.size(); k++) {
                    DesignColumn inter = interactionSet.get(k);

                    getDesign().getInteractions().add(inter);
                }
            }

            //MQ3I
        }
    }

    public void createQuadsOnly(ArrayList<DesignColumn> arrayList) {
        getDesign().getQuadratics().clear();
        ArrayList<DesignColumn> translatedDesign = new ArrayList<DesignColumn>();
        int numberOfFactors = arrayList.size();
        int numberOfDesignPoints = arrayList.get(0).getLevels();

        for (int i = 0; i < numberOfFactors; i++) {
            DesignColumn designCol = new DesignColumn(numberOfDesignPoints, false, getRandomNumber(), arrayList.get(i).getType(), arrayList.get(i).getDiscreteLevels());
            designCol.setDesignColumn(arrayList.get(i).getDesignColumn());

            translatedDesign.add(designCol);
            if (!designCol.getType().equals("categorical")) {//categorical factors don't have quadratics
                if (getHighestMode().equals("MQI") || getHighestMode().equals("MQ")) {
                    DesignColumn quad = new DesignColumn(numberOfDesignPoints, false, getRandomNumber(), getColumnType(), getDiscreteLevels());
                    double[] quadCol = new double[numberOfDesignPoints];
                    quadCol = designCol.createQuadratic();
                    quad.setDesignColumn(quadCol);
                    quad.setType(designCol.getType());
                    quad.setDiscreteLevels(designCol.getDiscreteLevels());
                    quad.setMode(designCol.getMode());
                    getDesign().getQuadratics().add(quad);
                }
            }
        }
    }

    /**
     * 
     * @param column
     * @return
     */
    public ArrayList<DesignColumn> createStartInteractions(DesignColumn column, ArrayList<DesignColumn> arrayList) {
        //create a temporary interaction array
        ArrayList<DesignColumn> tempInteractionList = new ArrayList<DesignColumn>();
        for (int j = 0; j < arrayList.size(); j++) {

            if (column != arrayList.get(j)) {
                double[] designCol = arrayList.get(j).getDesignColumn();

                double[] interactionColumn = createInteraction(designCol, column.getDesignColumn());

                //create an interaction column and add to the temp interaction set
                DesignColumn interaction = new DesignColumn(column.getDesignColumn().length, false, getRandomNumber(), getColumnType(), getDiscreteLevels());
                interaction.setDesignColumn(interactionColumn);
                interaction.setType(arrayList.get(j).getType() + "with" + column.getType());
                tempInteractionList.add(interaction);
            }

        }
        return tempInteractionList;
    }

//    public void translateDesignColumns(ArrayList<DesignColumn> arrayList) {
//        for (int col = 0; col < arrayList.size(); col++) {
//            arrayList.get(col).setDesignColumn(arrayList.get(col).translateColumn(arrayList.get(col).getDesignColumn()));
//        }
//    }
    /**
     * insertFinal is a boolean that if true, will add the best final col
     * to the next population.
     * @param insertFinal
     */
    public void createNewPopulation() {
        //   getPopulation().getPop().clear();
        for (int i = 0; i < getPopNumber(); i++) {
            DesignColumn newColumn = new DesignColumn(getLevels(), true, getRandomNumber(), getColumnType(), getDiscreteLevels());
            if (getMode().equals("MQI") || getMode().equals("MI")) {
                ArrayList<double[]> interactions = createInteractionArray(newColumn);
                newColumn.setInteractions(interactions);
            }
            //   getPopulation().getPop().add(newColumn);
            getPopulation().getPop()[i] = newColumn;
        }
    }

////////////////////////////////////////////////////////////////////////////
    /**
     * return the max correlation in an array.  If there is a categorical variable in the design
     * then iterate through each of its dummy variables to check for max correlation
     * @param array
     * @param candidateCol
     * @return
     */
    public double checkArrayList(ArrayList<DesignColumn> array, double[] candidateCol, String type) {
        double maxCorrelation = 0;
        for (int i = 0; i < array.size(); i++) {
            DesignColumn designColumn = array.get(i);

            String typeCheck = type.substring(3);
            //only do this if the fator in the design is categorical and the array is the main design
            //the interactions will already have the dummy variables in them

            if (designColumn.getType().equals("categorical") && typeCheck.equals("ME")) {
                //if categorical then iterate through the dummy variables

                for (int dummyCol = 0; dummyCol < designColumn.getDummy().size(); dummyCol++) {
                    double[] col = designColumn.getDummy().get(dummyCol);

                    maxCorrelation = updateMaxCorrelation(col, candidateCol, maxCorrelation);
                }

            } else {
                double[] column = designColumn.getDesignColumn();
                maxCorrelation = updateMaxCorrelation(column, candidateCol, maxCorrelation);
            }
        }
        return maxCorrelation;
    }

    /**
     * returns the max correlation in a HashMap
     * @param array
     * @param candidateCol
     * @return
     */
    public double checkHashMap(HashMap<Integer, DesignColumn> array, double[] candidateCol, String type) {
        double maxCorrelation = 0;
        for (Map.Entry<Integer, DesignColumn> interCol : array.entrySet()) {
            double[] column = interCol.getValue().getDesignColumn();
            maxCorrelation = updateMaxCorrelation(column, candidateCol, maxCorrelation);
        }
        return maxCorrelation;
    }

    /**
     * updates the max correlation given two columns
     * @param column1
     * @param column2
     * @param maxCorrelation
     * @return
     */
    public double updateMaxCorrelation(double[] column1, double[] column2, double maxCorrelation) { //populate column2 (candidate col) with the absCorrMatrix
        double correlation = getAlexCorrelation(column1, column2);
        double absoluteCorr = Math.abs(correlation);
        //put in hash map with an object ID of column 1 for the key value and the asbCorrelation for the value
        if (absoluteCorr > maxCorrelation) {
            maxCorrelation = absoluteCorr;
        }
        return maxCorrelation;
    }

    public double findMax(double maxCorrelation, double candidateCorrelation) {
        if (candidateCorrelation > maxCorrelation) {
            maxCorrelation = candidateCorrelation;
        }
        return maxCorrelation;
    }

    /**
     * creates a temporary interaction array
     * @param design
     * @param candidateCol
     * @return
     */
    public ArrayList<double[]> createInteractionArray(DesignColumn candidateCol) {

        //create a temporary interaction array
        ArrayList<double[]> tempInteractionList = new ArrayList<double[]>();
        ArrayList<Integer[]> interNames = candidateCol.getInterNames();//these names must coinside with the tempInteractionList
        for (int j = 0; j < getDesign().getMain().size(); j++) {
            DesignColumn designCol = getDesign().getMain().get(j);
            double[] interactionColumn;
            double[] col;
            if (candidateCol.getType().equals("categorical")) {// candidate is categorical

                ArrayList<double[]> dummies = candidateCol.getDummy();
                for (int i = 0; i < dummies.size(); i++) {
                    double[] candidateDummy = dummies.get(i);
                    if (designCol.getType().equals("categorical")) { //design col is categorical
                        ArrayList<double[]> designDummies = designCol.getDummy();
                        for (int k = 0; k < designDummies.size(); k++) {
                            interactionColumn = createInteraction(candidateDummy, designDummies.get(k));
                            //      Integer[] namesData = new Integer[3];
                            //       namesData[0] = i + 1;//the categorical factor number
                            //      namesData[1] = j + 1;//the design col number
                            //      namesData[2] = k + 1;//the dummy level number
                            //       interNames.add(namesData);
                            tempInteractionList.add(interactionColumn);

                        }
                    } else { // design col is not categorical
                        col = designCol.getDesignColumn();
                        interactionColumn = createInteraction(candidateDummy, col);
                        //    Integer[] namesData = new Integer[3];
                        //     namesData[0] = i + 1;//the categorical factor number
                        //    namesData[1] = -(j + 1);//the design col number. Negative indicates a non dummy variable
                        //    namesData[2] = 0;//the dummy level number
                        //     interNames.add(namesData);
                        tempInteractionList.add(interactionColumn);
                    }
                }
            } else {// candidate is not categorical

                if (designCol.getType().equals("categorical")) { // design col is categorical
                    ArrayList<double[]> designDummies = designCol.getDummy();
                    for (int k = 0; k < designDummies.size(); k++) {
                        interactionColumn = createInteraction(candidateCol.getDesignColumn(), designDummies.get(k));
                        //      Integer[] namesData = new Integer[3];
                        //      namesData[0] = 0;//the categorical factor number
                        //      namesData[1] = j + 1;//the design col number
                        //     namesData[2] = k + 1;//the dummy level number
                        //     interNames.add(namesData);
                        tempInteractionList.add(interactionColumn);
                    }
                } else { // design col is not categorical
                    col = designCol.getDesignColumn();
                    interactionColumn = createInteraction(candidateCol.getDesignColumn(), col);
                    Integer[] namesData = new Integer[3];
                    namesData[0] = 0;//the categorical factor number
                    namesData[1] = -(j + 1);//the design col number. Negative indicates a non dummy variable
                    namesData[2] = 0;//the dummy level number
                    //  interNames.add(namesData);
                    tempInteractionList.add(interactionColumn);
                }
            }
            //create an interaction column and add to the temp interaction set

            //  tempInteractionList.add(interactionColumn);
        }

        return tempInteractionList;
    }

    /**
     * creates an interaction given two columns
     * @param column1
     * @param column2
     * @return
     */
    public double[] createInteraction(double[] column1, double[] column2) {
        double[] interactionArray = new double[column1.length];
        for (int i = 0; i < column1.length; i++) {
            interactionArray[i] = column1[i] * column2[i];
        }
        return interactionArray;
    }

    /**
     * checks interactions with itself
     * @param tempInteractionList
     * @return
     */
    public double IC_IC(ArrayList<double[]> tempInteractionList, ArrayList<Integer[]> interNames) {
        double maxCorrelation = 0;
        //check for interaction within the temporary interaction list
        for (int k = 0; k < tempInteractionList.size(); k++) {
            double[] firstInter = tempInteractionList.get(k);
            for (int p = k + 1; p < tempInteractionList.size(); p++) {
                double[] secondInter = tempInteractionList.get(p);
                //if candidateInteraction is a "sister" of the candidateCol then skip this
                //if candidateInteraction contains the col name and the levels don't equal then skip
                //if dummy and k's A or B is contained in p's A or B and if it is a sister then skip
                //    Integer[] leftNames = interNames.get(k);
                //      Integer[] rightNames = interNames.get(p);

                //       if ((leftNames[0] > 0 || leftNames[1] < 0) && ((rightNames[0] > 0 || rightNames[1] < 0))) {//this means that one of the left and one of the right has a dummy variable
                //           if (leftNames[0] != rightNames[0]) {
                //skip the correlation check
                //      System.out.print("me" + interNames.get(k)[0] + "-x" + interNames.get(k)[1] + interNames.get(k)[2] + " : " + "me"
                //             + interNames.get(p)[0] + "-x" + interNames.get(p)[1] + interNames.get(p)[2]);
                //      System.out.println(" skipped");
                //           } else if (leftNames[1] == rightNames[1] && leftNames[2] != rightNames[2]) {
                //skip the correlation check
                //     System.out.print("me" + interNames.get(k)[0] + "-x" + interNames.get(k)[1] + interNames.get(k)[2] + " : " + "me"
                //             + interNames.get(p)[0] + "-x" + interNames.get(p)[1] + interNames.get(p)[2]);
                //     System.out.println(" skipped");
                //         } else {
                //             maxCorrelation = updateMaxCorrelation(firstInter,
                //                     secondInter, maxCorrelation);
                //    System.out.println("me" + interNames.get(k)[0] + "-x" + interNames.get(k)[1] + interNames.get(k)[2] + " : " + "me"
                //           + interNames.get(p)[0] + "-x" + interNames.get(p)[1] + interNames.get(p)[2]);
                //          }
                //      } else {
                maxCorrelation = updateMaxCorrelation(firstInter,
                        secondInter, maxCorrelation);
                //  System.out.print("me" + interNames.get(k)[0] + "-x" + interNames.get(k)[1] + interNames.get(k)[2] + " : " + "me"
                //          + interNames.get(p)[0] + "-x" + interNames.get(p)[1] + interNames.get(p)[2]);
                //  System.out.println(" not a dummy");
                //   }




                // System.out.println("me" + interNames.get(k)[0] + "-x" + interNames.get(k)[1] + interNames.get(k)[2] + " : " + "me" + interNames.get(p)[0] + "-x" + interNames.get(p)[1] + interNames.get(p)[2]);

                //           System.out.println("candidates with themselves " +maxInterCorr);
            }
        }
        //   System.out.println("done");
        return maxCorrelation;
    }

    public void setFitnessValues(double correlation, DesignColumn column) {

        //set the population column's fitness to the maximum absolute correlation
        column.setCorrelation(correlation);
        column.setInverseCorrelation(1 - correlation);
    }

    /**
     * checks main candidate with the existing mains
     * @param candidateCol
     */
    public double MC_ME(double[] candidateCol, String type) {

        return checkArrayList(getDesign().getMain(), candidateCol, type);
    }

    /**
     * checks main candidate with the existing quadratics
     * @param candidateCol
     */
    public double MC_QE(double[] candidateCol, String type) {
        return checkArrayList(getDesign().getQuadratics(), candidateCol, type);
    }

    /**
     * checks quadratic candidate with the existing mains
     * @param quadraticCandidateCol
     */
    public double QC_ME(double[] quadraticCandidateCol, String type) {
        return checkArrayList(getDesign().getMain(), quadraticCandidateCol, type);
    }

    /**
     * checks quadratic candidate with the existing quadratics
     * @param quadraticCandidateCol
     */
    public double QC_QE(double[] quadraticCandidateCol, String type) {
        return checkArrayList(getDesign().getQuadratics(), quadraticCandidateCol, type);
    }

    /**
     * checks main candidate with the existing interactions
     * @param candidateCol
     */
    public double MC_IE(double[] candidateCol, String type) {
        return checkArrayList(getDesign().getInteractions(), candidateCol, type);
    }

    /**
     * checks quadratic candidate with the existing interactions
     * @param candidateCol
     */
    public double QC_IE(double[] quadraticCandidateCol, String type) {
        return checkArrayList(getDesign().getInteractions(), quadraticCandidateCol, type);
    }

    /**
     * checks interaction candidate with the existing mains
     * @param candidateCol
     */
    public double IC_ME(double[] interactionCandidateCol, String type) {
        return checkArrayList(getDesign().getMain(), interactionCandidateCol, type);
    }

    /**
     * checks interaction candidate with the existing quadratics
     * @param candidateCol
     */
    public double IC_QE(double[] interactionCandidateCol, String type) {
        return checkArrayList(getDesign().getQuadratics(), interactionCandidateCol, type);
    }

    /**
     * checks interaction candidate with the existing interactions
     * @param candidateCol
     */
    public double IC_IE(double[] interactionCandidateCol, String type) {
        return checkArrayList(getDesign().getInteractions(), interactionCandidateCol, type);
    }

    /**
     * checks interaction candidate with the main candidate
     * @param candidateCol
     */
    public double IC_MC(double[] interCandidateCol, double[] mainCandidateCol) {
        return Math.abs(getAlexCorrelation(interCandidateCol, mainCandidateCol));
    }

    /**
     * checks interaction candidate with the quadratic candidate
     * @param candidateCol
     */
    public double IC_QC(double[] interCandidateCol, double[] quadraticCandidateCol) {
        return Math.abs(getAlexCorrelation(interCandidateCol, quadraticCandidateCol));
    }

    /**
     * checks main candidate with the quadratic candidate
     * Note: the correlation should always be zero if centered but when we start
     * jiggling, our centering doesn't work
     * @param candidateCol
     */
    public double MC_QC(double[] candidateCol, double[] quadraticCandidateCol) {
        return Math.abs(getAlexCorrelation(candidateCol, quadraticCandidateCol));
    }

    public double calculateMaxCorrelation(DesignColumn candidateDesignCol) {
        //if the column does not already have a correlation value
        // then calculate the max correlation
        //i is the population Column

        HashMap<String, Double> maxChecks = new HashMap<String, Double>();


        double correlation = 0;
        double maxCorrelation = 0;

        //System.out.println();

        double[] candidateCol = candidateDesignCol.getDesignColumn();

        if (getMode().equals("M")) {
            //if categorical, we must iterate through each of the dummy variable columns and get
            //the max dummy correlation
            if (candidateDesignCol.getType().equals("categorical")) {
                double maxDummy = 0;
                double dummyCorr;
                for (int dummyCol = 0; dummyCol < candidateDesignCol.getDiscreteLevels() - 1; dummyCol++) {
                    double[] col = candidateDesignCol.getDummy().get(dummyCol);
                    dummyCorr = MC_ME(col, "MC_ME");
                    if (dummyCorr > maxDummy) {
                        maxDummy = dummyCorr;
                    }
                }

                maxChecks.put("MC_ME", maxDummy);
                setFitnessValues(maxDummy, candidateDesignCol);
            } else {
                maxCorrelation = MC_ME(candidateCol, "MC_ME");
                maxChecks.put("MC_ME", maxCorrelation);
                setFitnessValues(maxCorrelation, candidateDesignCol);
                //clear out the candidate column
                candidateCol = null;
            }

        }

        if (getMode().equals("MQ")) {
            correlation = MC_ME(candidateCol, "MC_ME");
            maxChecks.put("MC_ME", correlation);


            correlation = MC_QE(candidateCol, "MC_QE");
            maxChecks.put("MC_QE", correlation);

            double[] quadCandidate = candidateDesignCol.getQuadratic();

//            DesignColumn quadCandidate = new DesignColumn(levels, false, getRandomNumber(), getColumnType(), getDiscreteLevels());
//            //grab the column from the population and set its quadratic
//            //to the new column just created
//            quadCandidate.setDesignColumn(candidateDesignCol.createQuadratic());


            correlation = QC_ME(quadCandidate, "QC_ME");
            maxChecks.put("QC_ME", correlation);

            correlation = QC_QE(quadCandidate, "QC_QE");
            maxChecks.put("QC_QE", correlation);

            correlation = MC_QC(candidateCol, quadCandidate);
            maxChecks.put("MC_QC", correlation);

            String maxType = "";
            for (Map.Entry<String, Double> corr : maxChecks.entrySet()) {
                //   System.out.println(corr.getKey() + ": " + corr.getValue());
                if (corr.getValue() > maxCorrelation) {
                    maxCorrelation = corr.getValue();
                    maxType = corr.getKey();
                }
            }
            setFitnessValues(maxCorrelation, candidateDesignCol);
            //clear out the candidate column, the quadratic
            candidateCol = null;
            quadCandidate = null;
        }

        if (getMode().equals("MQI") || getMode().equals("MI")) {


            //if categorical, we must iterate through each of the dummy variable columns and get
            //the max dummy correlation
            if (candidateDesignCol.getType().equals("categorical")) {
                double maxDummy = 0;
                double dummyCorr;
                for (int dummyCol = 0; dummyCol < candidateDesignCol.getDiscreteLevels() - 1; dummyCol++) {
                    double[] col = candidateDesignCol.getDummy().get(dummyCol);
                    dummyCorr = MC_ME(col, "MC_ME");
                    if (dummyCorr > maxDummy) {
                        maxDummy = dummyCorr;
                    }
                }

                maxChecks.put("MC_ME", maxDummy);

            } else {
                maxCorrelation = MC_ME(candidateCol, "MC_ME");
                maxChecks.put("MC_ME", maxCorrelation);
            }
            //  correlation = MC_ME(candidateCol, "MC_ME");


            //    maxChecks.put("MC_ME", correlation);


            double[] quadCandidate = candidateDesignCol.getQuadratic();
            if (getMode().equals("MQI")) {
                correlation = MC_QE(candidateCol, "MC_QE");
                maxChecks.put("MC_QE", correlation);

                correlation = QC_ME(quadCandidate, "QC_ME");
                maxChecks.put("QC_ME", correlation);

                correlation = QC_QE(quadCandidate, "QC_QE");
                maxChecks.put("QC_QE", correlation);

                correlation = MC_QC(candidateCol, quadCandidate);
                maxChecks.put("MC_QC", correlation);

                correlation = QC_IE(quadCandidate, "QC_IE");
                maxChecks.put("QC_IE", correlation);
            }

            correlation = MC_IE(candidateCol, "MC_IE");
            maxChecks.put("MC_IE", correlation);


            //create interactions

            ArrayList<double[]> interactionList = candidateDesignCol.getInteractions();

            ArrayList<Integer[]> interNames = candidateDesignCol.getInterNames();

            //for each candidate interaction
            double maxIC_ME = 0;
            double maxIC_QE = 0;
            double maxIC_IE = 0;
            double maxIC_MC = 0;
            double maxIC_QC = 0;
            for (int j = 0; j < interactionList.size(); j++) {
                double[] candidateInteraction = interactionList.get(j);

                //for each existing interaction
                correlation = IC_ME(candidateInteraction, "IC_ME");
                maxIC_ME = findMax(maxIC_ME, correlation);
                maxChecks.put("IC_ME", maxIC_ME);

                correlation = IC_IE(candidateInteraction, "IC_IE");
                maxIC_IE = findMax(maxIC_IE, correlation);
                maxChecks.put("IC_IE", maxIC_IE);

                //if categorical, we must iterate through each of the dummy variable columns and get
                //the max dummy correlation
                if (candidateDesignCol.getType().equals("categorical")) {
                    double maxDummy = 0;
                    double dummyCorr = 1;
                    for (int dummyCol = 0; dummyCol < candidateDesignCol.getDiscreteLevels() - 1; dummyCol++) {
                        double[] col = candidateDesignCol.getDummy().get(dummyCol);
                        //if candidateInteraction is a "sister" of the candidateCol then skip this
                        //if candidateInteraction contains the col name and the levels don't equal then skip
                        dummyCorr = IC_MC(candidateInteraction, col);
                        if (dummyCorr > maxDummy) {
                            maxDummy = dummyCorr;
                        }
                    }
                    maxIC_MC = findMax(maxIC_MC, dummyCorr);
                    maxChecks.put("IC_MC", maxIC_MC);

                } else {
                    correlation = IC_MC(candidateInteraction, candidateCol);
                    maxIC_MC = findMax(maxIC_MC, correlation);
                    maxChecks.put("IC_MC", maxIC_MC);
                }

                if (getMode().equals("MQI")) {
                    correlation = IC_QC(candidateInteraction, quadCandidate);
                    maxIC_QC = findMax(maxIC_QC, correlation);
                    maxChecks.put("IC_QC", maxIC_QC);

                    correlation = IC_QE(candidateInteraction, "IC_QE");
                    maxIC_QE = findMax(maxIC_QE, correlation);
                    maxChecks.put("IC_QE", maxIC_QE);
                }
            }
            correlation = IC_IC(interactionList, interNames);
            maxChecks.put("IC_IC", correlation);

            String maxType = "";
            for (Map.Entry<String, Double> corr : maxChecks.entrySet()) {
                // if (generationNumber == 100) {
                //     System.out.println(corr.getKey() + ": " + corr.getValue());
                //  }

                if (Math.abs(corr.getValue()) > maxCorrelation) {
                    maxCorrelation = Math.abs(corr.getValue());
                    maxType = corr.getKey();
                }
            }

//                if (trialNumber == 100) {
//                    System.out.println("col " + i);
//                    System.out.println("max: " + maxCorrelation + " type: " + maxType);
//                    System.out.println();
//                }

            setFitnessValues(maxCorrelation, candidateDesignCol);
            //clear out the candidate column, the quadratic and the interactions
            candidateCol = null;
            quadCandidate = null;
            interactionList = null;
        }

        //if MQ3I

        //set hasCorrelation to true so that we don't calculate the correlation again
        candidateDesignCol.setHasCorrelation(true);

//        for (Map.Entry<String, Double> corr : maxChecks.entrySet()) {
//            System.out.println(corr.getKey() + ": " + corr.getValue());
//        }
//        System.out.println("nextCheck");

        maxChecks = null;
        return maxCorrelation;
    }

    /**
     * assigns the fitness value to each column in the population.  It first calculates each col
     * max correlation and then assigns the col fitness value- (1-maxCorr) then re-defining with
     * linear ranking
     * @param pop
     */
    public void calculatePopulationCorrelation(Population pop) {
        double max = 0;
        double min = 999999;
        double total = 0;
        double corr;
        double inverseCorr;
        for (int i = 0; i < pop.getPop().length; i++) {
            //DesignColumn candidateCol = pop.getPop().get(i);
            DesignColumn candidateCol = pop.getPop()[i];
            if (!candidateCol.getHasCorrelation()) {
                corr = calculateMaxCorrelation(candidateCol);
            }
        }
        pop.setPopMax(max);
        pop.setPopMin(min);
        pop.setPopMean(total / pop.getPopSize());
        // once all the population columns have a max correlation assigned
        //now calculate each of their fitness
        calculateFitness(pop);
    }

    public void addToInteractions(DesignColumn popCol) {

        for (int j = 0; j < getDesign().getMain().size(); j++) {
            //get the size of the interaction container and add one to it
            // int interactionSize = getInteractions().size();
            //   DesignColumn inter = new DesignColumn(levels, false, getRandomNumber(), getColumnType(), getDiscreteLevels());
            DesignColumn designCol = getDesign().getMain().get(j);

            double[] interactionColumn;
            double[] col;
            if (popCol.getType().equals("categorical")) {
                ArrayList<double[]> dummies = popCol.getDummy();
                for (int i = 0; i < dummies.size(); i++) {
                    double[] candidateDummy = dummies.get(i);
                    if (designCol.getType().equals("categorical")) {
                        ArrayList<double[]> designDummies = designCol.getDummy();
                        for (int k = 0; k < designDummies.size(); k++) {
                            interactionColumn = createInteraction(candidateDummy, designDummies.get(k));
                            DesignColumn inter = new DesignColumn(levels, false, getRandomNumber(), "dummywithdummy", getDiscreteLevels());
                            inter.setDesignColumn(interactionColumn);
                            getDesign().getInteractions().add(inter);
                        }
                    } else {
                        col = designCol.getDesignColumn();
                        interactionColumn = createInteraction(candidateDummy, col);
                        DesignColumn inter = new DesignColumn(levels, false, getRandomNumber(), "dummywith" + designCol.getType(), getDiscreteLevels());
                        inter.setDesignColumn(interactionColumn);
                        getDesign().getInteractions().add(inter);
                    }
                }
            } else {
                if (designCol.getType().equals("categorical")) {
                    ArrayList<double[]> designDummies = designCol.getDummy();
                    for (int k = 0; k < designDummies.size(); k++) {
                        interactionColumn = createInteraction(popCol.getDesignColumn(), designDummies.get(k));
                        DesignColumn inter = new DesignColumn(levels, false, getRandomNumber(), popCol.getType() + "withdummy", getDiscreteLevels());
                        inter.setDesignColumn(interactionColumn);
                        getDesign().getInteractions().add(inter);
                    }
                } else {
                    col = designCol.getDesignColumn();
                    interactionColumn = createInteraction(popCol.getDesignColumn(), col);
                    DesignColumn inter = new DesignColumn(levels, false, getRandomNumber(), popCol.getType() + "with" + designCol.getType(), getDiscreteLevels());
                    inter.setDesignColumn(interactionColumn);
                    getDesign().getInteractions().add(inter);
                }
            }
            //    inter.setDesignColumn(createInteraction(popCol.getDesignColumn(), designCol.getDesignColumn()));
            //    getDesign().getInteractions().add(inter);
        }
    }

//    public void outputOnLineDesign(int seed) {
//        //  String designName = "designOutput/" + mode + "/onLineDesign/onLineDesign " + levels + " levels " + ".csv";
//        String designName = "onLineDesign " + levels + " levels " + seed + ".csv";
//        clearCsvFile(designName);
//        writeHeadersToDesign(designName, getDesign().getMain().size(), getDesign().getMain());
//        // ArrayList<DesignColumn> arrayList = new ArrayList<DesignColumn>();
//        //  arrayList = design;
//
//        // translateDesignColumns(arrayList);
//        writeArrayListToCsv(getDesign().getMain(), designName);
//    }
//    public void outputOnLinefinalCol(int seed, double time) {
//        //  String designName = "designOutput/" + mode + "/onLineFinalCol/finalCol " + levels + " levels " + seed + ".csv";
//        String designName = "onLinefinalCol " + levels + " levels " + seed + ".csv";
//        clearCsvFile(designName);
//        writeHeadersToDesign(designName, 1);
//        writeColToCsv(finalColumn, designName, finalColumn.getCorrelation(), time);
//    }
    public void outputOnLinePopulation() {
        String designName = "population.csv";
        clearCsvFile(designName);
        writeArrayToCsv(getPopulation().getPop(), designName);
    }

    public boolean checkColumnAddition(double averageCorrelationCriteria) {
        boolean added = false;
        DesignColumn newColumn = getPopulation().getPop()[0];
        double minimumCorrInPop = newColumn.getCorrelation();
        if (minimumCorrInPop <= maxConstraint) {
            addNewColumn(newColumn);
            added = true;
            setMaxDesignCorrelation(findMax(maxDesignCorrelation, minimumCorrInPop));
        }
        return added;
    }

    public void printColumnStuff(DesignColumn newColumn) {
        System.out.println("main");
        double[] main = design.getMain().get(0).getDesignColumn();
        for (int p = 0; p < main.length; p++) {
            System.out.println(main[p]);
        }
        System.out.println("candidate");
        double[] mainCandidate = newColumn.getDesignColumn();
        for (int p = 0; p < mainCandidate.length; p++) {
            System.out.println(mainCandidate[p]);
        }
        System.out.println("inters");
        ArrayList<double[]> inters = newColumn.getInteractions();
        //do we need the interNAmes?
        printArrayList(inters);

    }

    public static void printArrayList(ArrayList<double[]> arrayList) {
        //grab the first array column to get the number of rows
        for (int row = 0; row < arrayList.get(0).length; row++) {
            for (int col = 0; col < arrayList.size(); col++) {
                double[] column = arrayList.get(col);

                System.out.print(column[row] + " ");
            }
            System.out.println();
        }

    }

    public void addNewColumn(DesignColumn newColumn) {

        newColumn.setMode(getMode());
        
        System.out.println("adding new column " + design.getMain().size());
        
        //we care about quadratics
        String localMode = getHighestMode();
        if (localMode.equals("MQ")) {

            getDesign().getMain().add(newColumn);
            if (!newColumn.getType().equals("categorical")) {//categorical factors don't have quadratics
                DesignColumn quad = new DesignColumn(levels, false, getRandomNumber(), getColumnType(), getDiscreteLevels());
                //grab the column from the population and set its quadratic
                //to the new column just created
                quad.setDesignColumn(newColumn.createQuadratic());
                quad.setType(newColumn.getType());
                quad.setMode(newColumn.getMode());
                quad.setDiscreteLevels(newColumn.getDiscreteLevels());
                getDesign().getQuadratics().add(quad);
            }


            //we don't care about quadratics but do care about interactions
        } else if (localMode.equals("MQI") || localMode.equals("MI")) {

            if (localMode.equals("MQI") && !newColumn.getType().equals("categorical")) {
                if (!newColumn.getType().equals("categorical")) {//categorical factors don't have quadratics
                    DesignColumn quad = new DesignColumn(levels, false, getRandomNumber(), getColumnType(), getDiscreteLevels());
                    //grab the column from the population and set its quadratic
                    //to the new column just created
                    quad.setDesignColumn(newColumn.createQuadratic());
                    quad.setType(newColumn.getType());
                    quad.setMode(newColumn.getMode());
                    quad.setDiscreteLevels(newColumn.getDiscreteLevels());


                    getDesign().getQuadratics().add(quad);
                }
            }
            addToInteractions(newColumn);

            //must add column to mains after creating the interaction
            getDesign().getMain().add(newColumn);

            //we don't care about quadratics or interactions
        } else {
            getDesign().getMain().add(newColumn);
        }


    }

    //can we store a population's max and min in the pop object to save us from looping here?
    public void calculateFitness(Population pop) {
        //sort design columns by the inverseCorrelation
        // Collections.sort(pop.getPop(), new InverseCorrComparator());
        Arrays.sort(pop.getPop(), new InverseCorrComparator());
        double max = 0;
        double min = 999;
        double total = 0;
        double inverseCorr;

        for (DesignColumn column : pop.getPop()) {
            inverseCorr = column.getInverseCorrelation();
            if (inverseCorr < min) {
                min = inverseCorr;
            }
            if (inverseCorr > max) {
                max = inverseCorr;
            }
            total += inverseCorr;
        }
        pop.setPopMean(total / pop.getPopSize());
        // System.out.println("best fitness: " + max + " average fitness: " + pop.getPopMean());
        // System.out.println("Diff: " + (max - pop.getPopMean()));

        double fitness;
        double totalFitness = 0;
        for (int index = 0; index < pop.getPop().length; index++) {
            fitness = min + (max - min) * (pop.getPop().length - index + 1) / (pop.getPopSize() - 1);
            //insert value transformation here
            if (useValueFunction) {
                fitness = fitnessValue(fitness);
            }
            //pop.getPop().get(index).setFitness(fitness);
            pop.getPop()[index].setFitness(fitness);
            totalFitness += fitness;
        }
        updateWheel(totalFitness, pop.getPop());
    }

    /**
     * update the selectinProb array. The selectionProp[][0] stores the column's fitness
     * the selection[][1] stores the cumulative fitness used in the cdf function.
     * fitness equals 1 - the correlation.  We do this so that we can maximize fitness.
     * The larger translated fitness (1 - corr) the the bigger the pie selection in the
     * wheel and the more likely the column will be chosen to be a parent.
     * If the diagnostics are on add to the diagnostics arralyList
     * @param generationNumber
     * @param diagnosticsOn
     * @param cumulativeCorrelation
     */
    public void updateWheel(double totalFitness, DesignColumn[] pop) {
        double minFitness = 9999;
        double maxFitness = 0;
        double fitnessRange;
        double fitnessMean;

        //  System.out.println("fitness");

        // populate the selection prob (pdf and cdf)
        for (int i = 0; i < selectionProbs.length; i++) {

            //pdf
            double pdf;
            double fitness = pop[i].getFitness();
            selectionProbs[i][0] = fitness / totalFitness;
            //cdf
            if (i == 0) {
                selectionProbs[i][1] = fitness / totalFitness;
            } else {
                selectionProbs[i][1] = selectionProbs[i - 1][1] + selectionProbs[i][0];
            }

            if (fitness < minFitness) {
                minFitness = fitness;
            }
            if (fitness > maxFitness) {
                maxFitness = fitness;
            }
        }
    }

    public double fitnessValue(double invertedFitness) {
        double value = invertedFitness;
        double numerator;
        double denominator;
        numerator = (1 - Math.exp(-invertedFitness / valueRho));
        denominator = (1 - Math.exp(-1 / valueRho));
        value = numerator / denominator;
        return value;
    }

    public ArrayList<double[]> addDummyToDesignForJig(Design design) {

        ArrayList<double[]> newArrayList = new ArrayList<double[]>();
        //copy the design

        //   ArrayList<DesignColumn> removeList = new ArrayList<DesignColumn>();
        //add in dummy variables
        //   int categoricalIndex = 1;
        //the catIndex is used while calculating the design's correlation. We don't need to calculate correlation among a
        // a categorical variable's dummies
        for (int i = 0; i < design.getMain().size(); i++) {
            DesignColumn column = design.getMain().get(i);
            if (column.getType().equals("categorical")) {
                // removeList.add(column);

                int dummySize = column.getDummy().size();
                for (int dummyCol = 0; dummyCol < dummySize; dummyCol++) {
                    // newDummy.add(dummyCol, column.getDummy().get(dummyCol).clone());
                    //DesignColumn tempDummy = new DesignColumn(column.getLevels(), false, column.getRandomNumber(), "dummy", column.getDiscreteLevels());
                    double[] tempDummy = column.getDummy().get(dummyCol).clone();

                    // tempDummy.setCatIndex(categoricalIndex);
                    //   tempDummy.setDesignColumn(column.getDummy().get(dummyCol).clone());
                    newArrayList.add(tempDummy);
                }
                //     categoricalIndex += 1;//increment the cat Index
            } else {
                newArrayList.add(column.getDesignColumn());
            }
        }
        //remove categorical, find the index value in newArrayList of object in the removeList array
//        for (int i = 0; i < removeList.size(); i++) {
//            int indexToRemove = newArrayList.indexOf(removeList.get(i));
//            newArrayList.remove(indexToRemove);
//        }
        return newArrayList;
    }

    public ArrayList<Integer[]> copyInterNames(ArrayList<Integer[]> oldNames) {
        ArrayList<Integer[]> newNames = new ArrayList<Integer[]>(oldNames.size());

        for (int i = 0; i < oldNames.size(); i++) {
            Integer[] col = new Integer[3];
            col = oldNames.get(i).clone();
            newNames.add(col);
        }

        return newNames;
    }

    public DesignColumn jiggleColumn(DesignColumn column, double baseLineHalfWidth, ArrayList<double[]> dummyDesign) {

        int bestColLength = column.getDesignColumn().length;
        int jigTimes = (int) (randomNumber.nextInt(bestColLength) * jigglePortion) + 1;
        double jig;
        double[] basis = column.getBasis();//the basis is the original latin hypercube without te jiggle
        // translate column
        //    double[] translatedColumn = translateColumn(column.getDesignColumn(), column.getMean());

        DesignColumn newJig = new DesignColumn(levels, false, getRandomNumber(), column.getType(), getDiscreteLevels());

        System.arraycopy(column.getDesignColumn(), 0, newJig.getDesignColumn(), 0, bestColLength);//copy col to new jig
        String localMode = column.getMode();
        if (localMode == null) {
            boolean check = true;
        }
        if (localMode.equals("MQI") || localMode.equals("MQ")) {
            newJig.setQuadratic(column.getQuadratic().clone());
        }
        ArrayList<double[]> newInters = null;
        if (localMode.equals("MQI") || localMode.equals("MI")) {
            newInters = copyInteractions(column.getInteractions());
            newJig.setInteractions(newInters);
            //        newJig.setInterNames(copyInterNames(column.getInterNames()));
        }
        // clone interactions

        for (int j = 0; j < jigTimes; j++) {

            int firstPosition;
            int secondPosition;
            //make sure that we don't jiggle the first or last design points
            do {
                firstPosition = randomNumber.nextInt(column.getDesignColumn().length);
            } while (column.getDesignColumn()[firstPosition] == lowerRange || column.getDesignColumn()[firstPosition] == upperRange);
            //make sure that we don't jiggle the first or last design points
            do {
                //make sure that the second position is not the same as the first position or make sure that we don't jiggle the first or last design points
                secondPosition = randomNumber.nextInt(column.getDesignColumn().length);
            } while ((firstPosition == secondPosition) || (column.getDesignColumn()[secondPosition] == lowerRange || column.getDesignColumn()[secondPosition] == upperRange));
            double newRightValue = 0;
            double newLeftValue = 0;
            double firstDesignPoint = newJig.getDesignColumn()[firstPosition];
            double secondDesignPoint = newJig.getDesignColumn()[secondPosition];
            double oldInterValue;
            //determine the basis upper and lower bounds
            double firstLowerBound = basis[firstPosition] - baseLineHalfWidth;
            double firstUpperBound = basis[firstPosition] + baseLineHalfWidth;
            double secondLowerBound = basis[secondPosition] - baseLineHalfWidth;
            double secondUpperBound = basis[secondPosition] + baseLineHalfWidth;
            //     do {
            //absolute value of this gives me a value between 0 and 0.5
            // jig = Math.abs(randomNumber.nextDouble() - 0.5);
            jig = Math.abs(2 * baseLineHalfWidth * randomNumber.nextDouble() - baseLineHalfWidth);
            jig = Round(jig, 2);
            newRightValue = firstDesignPoint + jig;
            newLeftValue = secondDesignPoint - jig;

            //    } while (newRightValue > firstUpperBound || newRightValue < firstLowerBound || (newRightValue > upperRange || newRightValue < lowerRange
            //            || newLeftValue > secondUpperBound || newLeftValue < secondLowerBound) || newLeftValue > upperRange || newLeftValue < lowerRange);

            if (newRightValue > firstUpperBound || newRightValue < firstLowerBound || (newRightValue > upperRange || newRightValue < lowerRange
                    || newLeftValue > secondUpperBound || newLeftValue < secondLowerBound) || newLeftValue > upperRange || newLeftValue < lowerRange) {
                //do nothing
            } else {
                newJig.getDesignColumn()[firstPosition] = newRightValue;
                newJig.getDesignColumn()[secondPosition] = newLeftValue;


                //  newJig.getDesignColumn()[firstPosition] = newRightValue;
                //  newJig.getDesignColumn()[secondPosition] = newLeftValue;

                //should we round the jig here?

                if (localMode.equals("MQI") || localMode.equals("MQ")) {
                    newJig.getQuadratic()[firstPosition] = newRightValue * newRightValue;
                    newJig.getQuadratic()[secondPosition] = newLeftValue * newLeftValue;
                }

                if (localMode.equals("MQI") || localMode.equals("MI")) {
                    ArrayList<DesignColumn> main = getDesign().getMain();
                    //update interactions
                    //   int sizeWithDummies = countColumnsWithDummies(main);

                    for (int a = 0; a < dummyDesign.size(); a++) {// this should be the size of main

                        if (dummyDesign.size() != newInters.size()) {
                            boolean check = true;
                        }
                        double[] inter = newInters.get(a);

                        if (firstDesignPoint == 0 || newRightValue == 0) {
                            double designPosition = dummyDesign.get(a)[firstPosition];
                            inter[firstPosition] = designPosition * newRightValue;
                        } else {
                            oldInterValue = inter[firstPosition];
                            inter[firstPosition] = oldInterValue / firstDesignPoint * newRightValue;
                        }
                        if (secondDesignPoint == 0 || newLeftValue == 0) {
                            double designPosition = dummyDesign.get(a)[secondPosition];
                            inter[secondPosition] = designPosition * newLeftValue;
                        } else {
                            oldInterValue = inter[secondPosition];
                            inter[secondPosition] = oldInterValue / secondDesignPoint * newLeftValue;
                        }

                    }
                }

            }

        }

        System.arraycopy(basis, 0, newJig.getBasis(), 0, basis.length);// set the basis
        //round the jig
        for (int i = 0; i < newJig.getDesignColumn().length; i++) {
            newJig.getDesignColumn()[i] = Round(newJig.getDesignColumn()[i], 2);
        }
        // newJig.setMean(newJig.findMean(newJig.getDesignColumn()));
        newJig.setMean(column.getMean());
        newJig.setMode(column.getMode());
        return newJig;
    }

    public int countColumnsWithDummies(ArrayList<DesignColumn> main) {
        int count = 0;
        for (int i = 0; i < main.size(); i++) {
            if (main.get(i).getType().equals("categorical")) {
                ArrayList<double[]> dummies = main.get(i).getDummy();
                for (int j = 0; j < dummies.size(); j++) {
                    count += 1;
                }
            } else {
                count += 1;
            }
        }
        return count;
    }

    /**
     * round double to the specified decimal place
     * @param Rval
     * @param Rpl
     * @return
     */
    public double Round(double Rval, int Rpl) {
        double p = (double) Math.pow(10, Rpl);
        Rval = Rval * p;
        double tmp = Math.round(Rval);
        return (double) tmp / p;
    }

    public void backwardJigglePass(double baseLineHalfWidth) {
        int designSize = getDesign().getMain().size();
        for (int i = designSize - 1; i >= 0; i--) {
            //  for (int i = 0; i < designSize; i++) {
            DesignColumn tempColumn = getDesign().getMain().get(i);
            //check this later
            if (tempColumn.getType().equals("continuous") && !tempColumn.isStartCol()) { //if column is continuous or if it is not a column from the Start Design'
                String localMode = tempColumn.getMode();
                setMode(localMode);//this sets the GA mode
                getDesign().getMain().remove(i);
                //  DesignColumn tempQuad = new DesignColumn(tempColumn.getLevels(), false, tempColumn.getRandomNumber(), tempColumn.getType(), tempColumn.getDiscreteLevels());
                //    if (getHighestMode().equals("MQ") || getHighestMode().equals("MQI")) {
                //        tempQuad = getDesign().getQuadratics().get(i);
                //       getDesign().getQuadratics().remove(i);
                //    }
                //make a temp copy of the interactions that will return if we don't find a better column
                ArrayList<DesignColumn> tempInteractions = new ArrayList<DesignColumn>();
                if (localMode.equals("MQI") || localMode.equals("MI")) {
                    for (int j = 0; j < getDesign().getInteractions().size(); j++) {
                        tempInteractions.add(getDesign().getInteractions().get(j));
                    }
                }

                createQuadsAndInters(getDesign().getMain());
                // if i = zero then this was the first column in the design.  We need to create its interactions
                //   if (i == 0) {
                tempColumn.setInteractions(createInteractionArray(tempColumn));
                //    }

                //create dummyDesign pass it to create jig pop
                ArrayList<double[]> dummyDesign = addDummyToDesignForJig(getDesign());
                calculateMaxCorrelation(tempColumn);


                DesignColumn jigCol = createJigglePopulation(tempColumn, baseLineHalfWidth, dummyDesign);
                if (!CMDLine) {
                    if (showComments) {
                        System.out.println(i + " jiggled column: " + jigCol.getCorrelation() + " original column: " + tempColumn.getCorrelation());
                    }
                }
                if (jigCol.getCorrelation() < tempColumn.getCorrelation()) {

                    addNewColumn(jigCol);

                } else {
                    getDesign().getMain().add(tempColumn);
                    if (localMode.equals("MQ") || localMode.equals("MQI")) {
                        // getDesign().getQuadratics().add(tempQuad);
                        createQuadsOnly(getDesign().getMain());
                    }
                    if (localMode.equals("MQI") || localMode.equals("MI")) {
                        getDesign().setInteractions(tempInteractions);
                    }
                }
                tempColumn = null;
                //   tempQuad = null;
                tempInteractions = null;
            }
        }
    }

    public double[] translateColumn(double[] array, double mean) {
        double[] newArray = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i] + mean;
        }
        return newArray;
    }

    public DesignColumn createJigglePopulation(DesignColumn bestCol, double baseLineHalfWidth, ArrayList<double[]> dummyDesign) {
        String localMode = bestCol.getMode();
        for (int i = 0; i < getJigglePopNumber(); i++) {
            DesignColumn jigCol = jiggleColumn(bestCol, baseLineHalfWidth, dummyDesign);

            if (localMode.equals("MQI") || localMode.equals("MI")) {
                ArrayList<double[]> interactions = createInteractionArray(jigCol);
                jigCol.setInteractions(interactions);
            }
            getJigglePopulation().getPop()[i] = jigCol;
        }
        calculatePopulationCorrelation(getJigglePopulation());
        // Collections.sort(getJigglePopulation().getPop(), new InverseCorrComparator());
        Arrays.sort(getJigglePopulation().getPop(), new InverseCorrComparator());
        //perform jiggle generations
        int count = 0;
        double corr = 0;
        double minCorr = 9999;
        for (int i = 0; i < jiggleGeneratons; i++) {

            createNextJiggleGeneration(baseLineHalfWidth, dummyDesign);
            count += 1;
            //System.out.println(count);
            calculatePopulationCorrelation(getJigglePopulation());
            Arrays.sort(getJigglePopulation().getPop(), new InverseCorrComparator());
            corr = getJigglePopulation().getPop()[0].getCorrelation();
            if (corr < minCorr) {
                minCorr = corr;
                count = 0;
            } else {
                if (count == maxJigGenAttempts) {
                    break;
                }

            }

        }
        if (getJigglePopulation().getPop()[0].getCorrelation() < bestCol.getCorrelation()) {
            getPopulation().getPop()[0] = getJigglePopulation().getPop()[0];
        }
        return getJigglePopulation().getPop()[0];
    }

    public void generatePoolSet(int columnNumber) {
        // ArrayList<DesignColumn> poolSet = new ArrayList<DesignColumn>();
        DesignColumn[] poolSet = new DesignColumn[poolSetSize];

        for (int i = 0; i < getPoolSetSize(); i++) {
            //DesignColumn newColumn = new DesignColumn(levels, false, getRandomNumber(), getColumnType(), getDiscreteLevels());
            // double[] newArray = new double[levels];
            DesignColumn tempCol = population.getPop()[columnNumber];
           
            DesignColumn newColumn;
            if (tempCol.getType().equals("categorical")) {
                newColumn = swapCat(tempCol);
            } else {
                newColumn = swap(tempCol);
            }
            //calculate and assign the max correlation to the new column
            calculateMaxCorrelation(newColumn);
            //    poolSet.add(newColumn);
            poolSet[i] = newColumn;

            // if (!bestOfPoolSet) {
            //      getPoolCollection().add(newColumn);
            //  }

        }
        //   if (bestOfPoolSet) {
        //   Collections.sort(poolSet, new InverseCorrComparator());
        Arrays.sort(poolSet, new InverseCorrComparator());

        //
        //   getPoolCollection().add(poolSet.get(0));
        getPoolCollection().add(poolSet[0]);
        //   }

        poolSet = null;
    }

    public void generateJigglePoolSet(int columnNumber, double baseLineHalfWidth, ArrayList<double[]> dummyDesign) {
        // ArrayList<DesignColumn> poolSet = new ArrayList<DesignColumn>();
        DesignColumn[] poolSet = new DesignColumn[poolSetSize];
        for (int i = 0; i < getPoolSetSize(); i++) {
            DesignColumn newColumn = new DesignColumn(levels, false, getRandomNumber(), getColumnType(), getDiscreteLevels());
            double[] newArray = new double[levels];
            String localMode = getJigglePopulation().getPop()[columnNumber].getMode();

            // newColumn = jiggleColumn(getJigglePopulation().getPop().get(columnNumber), baseLineHalfWidth);
            newColumn = jiggleColumn(getJigglePopulation().getPop()[columnNumber], baseLineHalfWidth, dummyDesign);
            newColumn.setMode(localMode);
            //calculate and assign the max correlation to the new column
            calculateMaxCorrelation(newColumn);
            // poolSet.add(newColumn);
            poolSet[i] = newColumn;
            //   if (!bestOfPoolSet) {
            //        getPoolCollection().add(newColumn);
            //     }

        }
        //  if (bestOfPoolSet) {
        //Collections.sort(poolSet, new InverseCorrComparator());
        Arrays.sort(poolSet, new InverseCorrComparator());
        // getPoolCollection().add(poolSet.get(0));
        getPoolCollection().add(poolSet[0]);
        //   }

        poolSet = null;
    }

    public void createNextJiggleGeneration(double baseLineHalfWidth, ArrayList<double[]> dummyDesign) {
        int bestColumns = (int) (copyPortion * popNumber);

        int mutateColumns = getJigglePopNumber() - bestColumns;
        for (int i = 0; i < bestColumns; i++) {
            // getNextJiggleGeneration().getPop().add(getJigglePopulation().getPop().get(i));
            getNextJiggleGeneration().getPop()[i] = getJigglePopulation().getPop()[i];
        }

        int bestSet = (int) (mutateColumns * 0.05);
        int randomSet = mutateColumns - bestSet;
        for (int i = 0; i < bestSet; i++) {
            int columnNumber = i;
            // int columnNumber = selectColumn();
            generateJigglePoolSet(columnNumber, baseLineHalfWidth, dummyDesign);
        }
        // int randomSet = mutateColumns;
        for (int i = 0; i < randomSet; i++) {
            int columnNumber = selectColumn();
            generateJigglePoolSet(columnNumber, baseLineHalfWidth, dummyDesign);
        }
        //sort the poolCollection and insert the best into the next generation
        Collections.sort(getPoolCollection(), new InverseCorrComparator());
        for (int j = 0; j < mutateColumns; j++) {
            //  getNextJiggleGeneration().getPop().add(getPoolCollection().get(j));
            getNextJiggleGeneration().getPop()[bestColumns + j] = getPoolCollection().get(j);
        }
        //clear out the pool collection
        getPoolCollection().clear();

        setJigglePopulation(getNextJiggleGeneration());
        //clear out next generation
        setNextJiggleGeneration(null);
        setNextJiggleGeneration(new Population(popNumber));
    }

    /**
     * create off spring for the next generation, set the population
     * equal to the next generation and clear out the next generation
     */
    public void createNextGeneration() {


        attempts += getPoolSetSize() * popNumber * (1 - copyPortion);

        int bestColumns = (int) (copyPortion * popNumber);
        int newColumns = (int) (newPortion * popNumber);
        int mutateColumns = popNumber - bestColumns - newColumns;
        for (int i = 0; i < bestColumns; i++) {
            getNextGeneration().getPop()[i] = getPopulation().getPop()[i];
        }
        //add the proportion of new columns
        for (int i = 0; i < newColumns; i++) {
            DesignColumn newColumn = new DesignColumn(levels, true, getRandomNumber(), getColumnType(), getDiscreteLevels());

            if (getMode().equals("MQI") || getMode().equals("MI")) {
                ArrayList<double[]> interactions = createInteractionArray(newColumn);
                newColumn.setInteractions(interactions);
            }
            getNextGeneration().getPop()[bestColumns + i] = newColumn;
            calculateMaxCorrelation(newColumn);
        }
        int bestSet = (int) (mutateColumns * 0.05);//force the mutation of the best set of columns
        int randomSet = mutateColumns - bestSet;
        for (int i = 0; i < bestSet; i++) {
            int columnNumber = i;
            // int columnNumber = selectColumn();
            generatePoolSet(columnNumber);
        }
        // int randomSet = mutateColumns;
        for (int i = 0; i < randomSet; i++) {
            int columnNumber = selectColumn();
            generatePoolSet(columnNumber);
        }
        //sort the poolCollection and insert the best into the next generation
        Collections.sort(getPoolCollection(), new InverseCorrComparator());
        for (int j = 0; j < mutateColumns; j++) {
            getNextGeneration().getPop()[bestColumns + newColumns + j] = getPoolCollection().get(j);
        }
        // Collections.sort(getNextGeneration().getPop(), new InverseCorrComparator());
        Arrays.sort(getNextGeneration().getPop(), new InverseCorrComparator());
        //clear out the pool collection
        getPoolCollection().clear();
        setPopulation(getNextGeneration());
        //clear out next generation
        setNextGeneration(null);
        setNextGeneration(new Population(popNumber));

    }

    /**
     * swap operator for continuous and discrete factors
     * @param array
     * @return
     */
    public DesignColumn swap(DesignColumn column) {
        int firstPosition = 0;
        int secondPosition = 0;
        double firstValue;
        double secondValue;
        double oldFirstInterValue;
        double oldSecondInterValue;
        boolean switchPositions = false;
        boolean switchOnce = false;  // true means that there was at least one switch

        DesignColumn newColumn = new DesignColumn(levels, false, getRandomNumber(), getColumnType(), getDiscreteLevels());

        double[] newArray = column.getDesignColumn().clone();
        double[] newQuadArray = null;
        ArrayList<double[]> newInters = null;
        //if quads
        if (getMode().equals("MQI") | getMode().equals("MQ")) {
            newQuadArray = column.getQuadratic().clone();
        }
        //if interactions
        if (getMode().equals("MQI") | getMode().equals("MI")) {
            newInters = copyInteractions(column.getInteractions());
//            newColumn.setInterNames(copyInterNames(column.getInterNames()));
        }

        int swapTimes = (int) (randomNumber.nextInt(newArray.length) * swapPortion) + 1;
        for (int i = 0; i < swapTimes; i++) {

            if (!column.getType().equals("continuous")) {//if this column is discrete
                if (randomNumber.nextDouble() <= .1) {
                    switchPositions = true;
                    switchOnce = true;
                }
            }

            //randomly select first level position
            firstPosition = getRandomNumber().nextInt(newArray.length);
            //randomly select the second level position
            //prevents the second random number from being the same as the first
            if (column.getType().equals("continuous")) {
                do {
                    secondPosition = getRandomNumber().nextInt(newArray.length);
                } while (firstPosition == secondPosition);
                firstValue = newArray[firstPosition];
                secondValue = newArray[secondPosition];
            } else {//if it is discrete then don't swap if the values are the same (firstValue!= secondValue)
                do {
                    secondPosition = getRandomNumber().nextInt(newArray.length);
                    firstValue = newArray[firstPosition];
                    secondValue = newArray[secondPosition];
                } while (firstPosition == secondPosition || firstValue == secondValue);
            }

//            firstValue = newArray[firstPosition];
//            secondValue = newArray[secondPosition];

            //swap positions
            newArray[firstPosition] = secondValue;
           // if (!switchPositions) {//if switch positions is not true then swap
                newArray[secondPosition] = firstValue;
           // }
            if (getMode().equals("MQI") | getMode().equals("MQ")) {
                //swap quad positions
                newQuadArray[firstPosition] = secondValue * secondValue;
                if (!switchPositions) {//if switch positions is not true then swap
                    newQuadArray[secondPosition] = firstValue * firstValue;
                }
            }
            if (getMode().equals("MQI") | getMode().equals("MI")) {
                //update interactions
                for (int a = 0; a < newInters.size(); a++) {
                    double[] inter = newInters.get(a);
                    oldFirstInterValue = inter[firstPosition];
                    oldSecondInterValue = inter[secondPosition];
                    if (firstValue == 0 || secondValue == 0) {
                        double designFirstPosition = getDesign().getMain().get(a).getDesignColumn()[firstPosition];
                        double designSecondPosition = getDesign().getMain().get(a).getDesignColumn()[secondPosition];
                        inter[firstPosition] = designFirstPosition * secondValue;
                        if (!switchPositions) {//if switch positions is not true then swap
                            inter[secondPosition] = designSecondPosition * firstValue;
                        }
                    } else {
                        inter[firstPosition] = (oldFirstInterValue / firstValue) * secondValue;
                        if (!switchPositions) {//if switch positions is not true then swap
                            inter[secondPosition] = (oldSecondInterValue / secondValue) * firstValue;
                        }
                    }
                }
            }
            switchPositions = false;
        }




        newColumn.setDesignColumn(newArray);
        if (getMode().equals("MQI") | getMode().equals("MQ")) {
            newColumn.setQuadratic(newQuadArray);
        }
        if (getMode().equals("MQI") | getMode().equals("MI")) {
            newColumn.setInteractions(newInters);
        }
        newColumn.setBasis(newArray);
        newColumn.setMean(column.getMean());
        //     if (switchOnce) {
        //         newColumn.setBalance(newColumn.checkBalance(newArray, column.getIdealLevels(), column.getDiscreteLevels()));
        //      }
        newColumn.setIdealLevels(column.getIdealLevels());
        newColumn.setType(column.getType());
        newColumn.setMode(column.getMode());
        newColumn.setDiscreteLevels(column.getDiscreteLevels());

        return newColumn;
    }

    /**
     * swap operator for categorical factors
     * @param array
     * @return
     */
    public DesignColumn swapCat(DesignColumn column) {
        int firstPosition = 0;
        int secondPosition = 0;
        double firstValue;
        double secondValue;
        double[] firstDummyValues = new double[column.getDummy().size()];
        double[] secondDummyValues = new double[column.getDummy().size()];
        boolean switchPositions = false;
        boolean switchOnce = false;

        DesignColumn newColumn = new DesignColumn(levels, false, getRandomNumber(), getColumnType(), getDiscreteLevels());

        double[] newArray = column.getDesignColumn().clone();
        //deep copy of dummy
        ArrayList<double[]> newDummy = new ArrayList<double[]>(column.getDummy().size());
        int dummySize = column.getDummy().size();
        for (int i = 0; i < dummySize; i++) {
            newDummy.add(i, column.getDummy().get(i).clone());
        }
        ArrayList<double[]> newInters = null;
        //if interactions
        if (getMode().equals("MI")) {
            newInters = copyInteractions(column.getInteractions());
            newColumn.setInterNames(copyInterNames(column.getInterNames()));
        }
        int swapTimes = (int) (randomNumber.nextInt(newArray.length) * swapPortion) + 1;
        for (int i = 0; i < swapTimes; i++) {

//            if (randomNumber.nextDouble() <= .05) {
//                switchPositions = false;
//            }

            //randomly select first level position
            firstPosition = getRandomNumber().nextInt(newArray.length);
            //randomly select the second level position
            //prevents the second random number from being the same as the first
            do {
                secondPosition = getRandomNumber().nextInt(newArray.length);
                firstValue = newArray[firstPosition];
                secondValue = newArray[secondPosition];
            } while (firstPosition == secondPosition || firstValue == secondValue);


            for (int dummyCol = 0; dummyCol < newDummy.size(); dummyCol++) {
                firstDummyValues[dummyCol] = newDummy.get(dummyCol)[firstPosition];
                secondDummyValues[dummyCol] = newDummy.get(dummyCol)[secondPosition];
            }
            //swap positions
            newArray[firstPosition] = secondValue;
            if (!switchPositions) {//if switch positions is not true then swap
                newArray[secondPosition] = firstValue;
            }
            for (int dummyCol = 0; dummyCol < column.getDummy().size(); dummyCol++) {
                newDummy.get(dummyCol)[firstPosition] = secondDummyValues[dummyCol];
                if (!switchPositions) {//if switch positions is not true then swap
                    newDummy.get(dummyCol)[secondPosition] = firstDummyValues[dummyCol];
                }
            }
            if (getMode().equals("MQI") | getMode().equals("MI")) {
                //update interactions
                updateCatInteractionsForSwap(newDummy, newInters, firstPosition, secondPosition, switchPositions);
            }
            switchPositions = false;
        }//end swapTimes

        newColumn.setDesignColumn(newArray);

        if (getMode().equals("MQI") | getMode().equals("MI")) {
            newColumn.setInteractions(newInters);
        }
        // newColumn.setBasis(newArray);
        newColumn.setMean(column.getMean());
        //      if (switchOnce) {
        //         newColumn.setBalance(newColumn.checkBalance(newArray, column.getIdealLevels(), column.getDiscreteLevels()));
        //      }
        newColumn.setIdealLevels(column.getIdealLevels());
        newColumn.setDummy(newDummy);
        newColumn.setType(column.getType());
        newColumn.setMode(column.getMode());

        return newColumn;
    }

    /**
     * used in the swapCat method. It updates the interactions of the candidate categorical dummy variables
     * @param dummies
     * @param inters
     * @param firstPosition
     * @param secondPosition
     * @return
     */
    public ArrayList<double[]> updateCatInteractionsForSwap(ArrayList<double[]> candidateDummies, ArrayList<double[]> newInters, int firstPosition, int secondPosition, boolean switchPositions) {

        int counter = 0;//counter is used to increment the newInters column since there are more columns with dummys then they are with the design number of columns
        for (int j = 0; j < getDesign().getMain().size(); j++) {
            DesignColumn designCol = getDesign().getMain().get(j);
            for (int i = 0; i < candidateDummies.size(); i++) {
                double[] candidateDummy = candidateDummies.get(i);
                if (designCol.getType().equals("categorical")) {
                    ArrayList<double[]> designDummies = designCol.getDummy();
                    for (int k = 0; k < designDummies.size(); k++) {
                        double[] col = designDummies.get(k);
                        newInters.get(counter)[firstPosition] = col[firstPosition] * candidateDummy[firstPosition];
                        if (!switchPositions) {//if switch positions is not true then swap
                            newInters.get(counter)[secondPosition] = col[secondPosition] * candidateDummy[secondPosition];
                        }
                        counter += 1;
                    }
                } else {
                    double[] col = designCol.getDesignColumn();
                    newInters.get(counter)[firstPosition] = col[firstPosition] * candidateDummy[firstPosition];
                    if (!switchPositions) {//if switch positions is not true then swap
                        newInters.get(counter)[secondPosition] = col[secondPosition] * candidateDummy[secondPosition];
                    }
                    counter += 1;
                }
            }
        }
        return newInters;
    }

    public ArrayList<double[]> copyInteractions(ArrayList<double[]> list) {
        ArrayList<double[]> newList = new ArrayList<double[]>();
        for (int i = 0; i < list.size(); i++) {
            newList.add(i, list.get(i).clone());
        }
        return newList;
    }

    public int selectColumn() {
        double randomNumber;

//        Random rand = new Random();
//        randomNumber = rand.nextDouble();

        return returnCdf(getRandomNumber().nextDouble());


    }

    /**
     * returns the column index number that is in between i - 1 and i
     * (the second column of the selectionProb[][1] - cdf).
     * @param randomNumber
     * @return
     */
    public int returnCdf(double randomNumber) {
        int columnSelected = -1;
        for (int i = 0; i < selectionProbs.length; i++) {
            if (i == 0) {
                if (randomNumber < selectionProbs[i][1]) {
                    columnSelected = i;
                    break;
                }
            } else {
                if (randomNumber >= selectionProbs[i - 1][1]
                        && randomNumber < selectionProbs[i][1]) {
                    columnSelected = i;
                    break;
                }
            }
        }
        if (columnSelected == -1) {
            System.out.println("randomNumber " + randomNumber);
            System.out.println("cdf is broke");
        }
        return columnSelected;
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
     * @return the popNumber
     */
    public int getPopNumber() {
        return popNumber;
    }

    /**
     * @param popNumber the popNumber to set
     */
    public void setPopNumber(int popNumber) {
        this.popNumber = popNumber;
    }

//    /**
//     * @return the design
//     */
//    public ArrayList<DesignColumn> getDesign() {
//        return design;
//    }
//
//    /**
//     * @param design the design to set
//     */
//    public void setDesign(ArrayList<DesignColumn> design) {
//        this.design = design;
//    }
    /**
     * @return the diagnostics
     */
    public ArrayList<GenerationDiagnostic> getDiagnostics() {
        return diagnostics;
    }

    /**
     * @param diagnostics the diagnostics to set
     */
    public void setDiagnostics(ArrayList<GenerationDiagnostic> diagnostics) {
        this.diagnostics = diagnostics;
    }

    /**
     * @return the selectionProbabilities
     */
    public double[][] getSelectionProbs() {
        return selectionProbs;
    }

    /**
     * @param selectionProbabilities the selectionProbabilities to set
     */
    public void setSelectionProbs(double[][] selectionProbabilities) {
        this.selectionProbs = selectionProbabilities;
    }

    /**
     * @return the maxCorrelation
     */
    public double getMaxConstraint() {
        return maxConstraint;
    }

    /**
     * @param maxCorrelation the maxCorrelation to set
     */
    public void setMaxConstraint(double maxConstraint) {
        this.maxConstraint = maxConstraint;
    }

//    /**
//     * @return the quadratics
//     */
//    public ArrayList<DesignColumn> getQuadratics() {
//        return quadratics;
//    }
//
//    /**
//     * @param quadratics the quadratics to set
//     */
//    public void setQuadratics(ArrayList<DesignColumn> quadratics) {
//        this.quadratics = quadratics;
//    }
    /**
     * @return the valueRho
     */
    public double getValueRho() {
        return valueRho;
    }

    /**
     * @param valueRho the valueRho to set
     */
    public void setValueRho(double valueRho) {
        this.valueRho = valueRho;
    }

    /**
     * @return the portion
     */
    public double getCopyPortion() {
        return copyPortion;
    }

    /**
     * @param portion the portion to set
     */
    public void setCopyPortion(double copyPortion) {
        this.copyPortion = copyPortion;
    }

//    /**
//     * @return the interactions
//     */
//    public ArrayList<DesignColumn> getInteractions() {
//        return interactions;
//    }
//
//    /**
//     * @param interactions the interactions to set
//     */
//    public void setInteractions(ArrayList<DesignColumn> interactions) {
//        this.interactions = interactions;
//    }
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
     * @return the maxDesignCorrelation
     */
    public double getMaxDesignCorrelation() {
        return maxDesignCorrelation;
    }

    /**
     * @param maxDesignCorrelation the maxDesignCorrelation to set
     */
    public void setMaxDesignCorrelation(double maxDesignCorrelation) {
        this.maxDesignCorrelation = maxDesignCorrelation;
    }

    /**
     * @return the attempts
     */
    public int getAttempts() {
        return attempts;
    }

    /**
     * @param attempts the attempts to set
     */
    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    /**
     * @return the maxAttempts
     */
    public int getMaxAttempts() {
        return maxAttempts;
    }

    /**
     * @param maxAttempts the maxAttempts to set
     */
    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    /**
     * @return the attemptStats
     */
    public SimpleStats getAttemptStats() {
        return attemptStats;
    }

    /**
     * @return the avgCorrelationCriteria
     */
    public double getAvgCorrelationCriteria() {
        return avgCorrelationCriteria;
    }

    /**
     * @param avgCorrelationCriteria the avgCorrelationCriteria to set
     */
    public void setAvgCorrelationCriteria(double avgCorrelationCriteria) {
        this.avgCorrelationCriteria = avgCorrelationCriteria;
    }

    /**
     * @return the randomNumber
     */
    public MTRandom getRandomNumber() {
        return randomNumber;
    }

    /**
     * @return the columnType
     */
    public String getColumnType() {
        return columnType;
    }

    /**
     * @param columnType the columnType to set
     */
    public void setColumnType(String columnType) {
        this.columnType = columnType;
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
     * @return the jigglePopNumber
     */
    public int getJigglePopNumber() {
        return jigglePopNumber;
    }

    /**
     * @param jigglePopNumber the jigglePopNumber to set
     */
    public void setJigglePopNumber(int jigglePopNumber) {
        this.jigglePopNumber = jigglePopNumber;
    }

    /**
     * @return the currentBest
     */
    public double[] getCurrentBest() {
        return currentBest;
    }

    /**
     * @param currentBest the currentBest to set
     */
    public void setCurrentBest(double[] currentBest) {
        this.currentBest = currentBest;
    }

    /**
     * @return the lowerRange
     */
    public double getLowerRange() {
        return lowerRange;
    }

    /**
     * @param lowerRange the lowerRange to set
     */
    public void setLowerRange(double lowerRange) {
        this.lowerRange = lowerRange;
    }

    /**
     * @return the upperRange
     */
    public double getUpperRange() {
        return upperRange;
    }

    /**
     * @param upperRange the upperRange to set
     */
    public void setUpperRange(double upperRange) {
        this.upperRange = upperRange;
    }

    /**
     * @return the population
     */
    public Population getPopulation() {
        return population;
    }

    /**
     * @param population the population to set
     */
    public void setPopulation(Population population) {
        this.population = population;
    }

    /**
     * @return the bestPopulation
     */
    public Population getBestPopulation() {
        return bestPopulation;
    }

    /**
     * @param bestPopulation the bestPopulation to set
     */
    public void setBestPopulation(Population bestPopulation) {
        this.bestPopulation = bestPopulation;
    }

    /**
     * @return the nextGeneration
     */
    public Population getNextGeneration() {
        return nextGeneration;
    }

    /**
     * @param nextGeneration the nextGeneration to set
     */
    public void setNextGeneration(Population nextGeneration) {
        this.nextGeneration = nextGeneration;
    }

    /**
     * @return the jigglePopulation
     */
    public Population getJigglePopulation() {
        return jigglePopulation;
    }

    /**
     * @param jigglePopulation the jigglePopulation to set
     */
    public void setJigglePopulation(Population jigglePopulation) {
        this.jigglePopulation = jigglePopulation;
    }

    /**
     * @return the nextJiggleGeneration
     */
    public Population getNextJiggleGeneration() {
        return nextJiggleGeneration;
    }

    /**
     * @param nextJiggleGeneration the nextJiggleGeneration to set
     */
    public void setNextJiggleGeneration(Population nextJiggleGeneration) {
        this.nextJiggleGeneration = nextJiggleGeneration;
    }

    /**
     * @return the poolSetSize
     */
    public int getPoolSetSize() {
        return poolSetSize;
    }

    /**
     * @param poolSetSize the poolSetSize to set
     */
    public void setPoolSetSize(int poolSetSize) {
        this.poolSetSize = poolSetSize;
    }

    /**
     * @return the poolCollection
     */
    public ArrayList<DesignColumn> getPoolCollection() {
        return poolCollection;
    }

    /**
     * @param poolCollection the poolCollection to set
     */
    public void setPoolCollection(ArrayList<DesignColumn> poolCollection) {
        this.poolCollection = poolCollection;
    }

    /**
     * @return the design
     */
    public Design getDesign() {
        return design;
    }

    /**
     * @param design the design to set
     */
    public void setDesign(Design design) {
        this.design = design;
    }

    /**
     * @return the bestColumn
     */
    public DesignColumn getBestColumn() {
        return bestColumn;
    }

    /**
     * @param bestColumn the bestColumn to set
     */
    public void setBestColumn(DesignColumn bestColumn) {
        this.bestColumn = bestColumn;
    }

    /**
     * @return the bestColCorr
     */
    public double getBestColCorr() {
        return bestColCorr;
    }

    /**
     * @param bestColCorr the bestColCorr to set
     */
    public void setBestColCorr(double bestColCorr) {
        this.bestColCorr = bestColCorr;
    }

    /**
     * @return the highestMode
     */
    public String getHighestMode() {
        return highestMode;
    }

    /**
     * @param highestMode the highestMode to set
     */
    public void setHighestMode(String highestMode) {
        this.highestMode = highestMode;
    }

    /**
     * @return the maxJigGenAttempts
     */
    public int getMaxJigGenAttempts() {
        return maxJigGenAttempts;
    }

    /**
     * @param maxJigGenAttempts the maxJigGenAttempts to set
     */
    public void setMaxJigGenAttempts(int maxJigGenAttempts) {
        this.maxJigGenAttempts = maxJigGenAttempts;
    }

    /**
     * @return the CMDLine
     */
    public boolean isCMDLine() {
        return CMDLine;
    }

    /**
     * @param CMDLine the CMDLine to set
     */
    public void setCMDLine(boolean CMDLine) {
        this.CMDLine = CMDLine;
    }
    /**
     * @return the interactions
     */
}
