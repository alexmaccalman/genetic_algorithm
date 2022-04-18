package DesignAlgorithm;

import java.util.Random;
import DesignAlgorithm.GeneticAlgorithm.*;
import java.util.ArrayList;
import static DesignAlgorithm.Correlation.*;
import static DesignAlgorithm.ML2.*;
import static DesignAlgorithm.WriteOut.*;
import DesignAlgorithm.SimpleStats.*;
//import net.goui.util.MTRandom;
import DesignAlgorithm.MTRandom;

/**
 *
 * @author maccalman
 */
public class MainMethod {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
  
        boolean CMDLine = false;
        boolean useValueFunction = false;
        Random randomSeed = new Random();
        int seed = randomSeed.nextInt(10000000);
        //int seed = 3507950;//1856643;//9650332;//1199684  4365370
        MTRandom randomNumber = new MTRandom(seed);
        double maxConstraint = Double.parseDouble(args[17]);//0.05;
        boolean showComments = Boolean.parseBoolean(args[3]);//true;
        //mainOnly: M or mainQuad: MQ, mainInter: MI, or mainQuadInter: MQI
        int levels = Integer.parseInt(args[0]);//17;
        int populationNumber = Integer.parseInt(args[6]);//120;
        int jigglePopNumber = populationNumber;//must be the same as population number
        double copyPortion = Double.parseDouble(args[7]);//.1;
        double newPortion = 0; // % of population that are newly generated columns
        double swapPortion = Double.parseDouble(args[11]);//0.1;
        int firstTrials = Integer.parseInt(args[10]);//7;
        int firstGenerations = Integer.parseInt(args[4]);//75;
        int poolSetSize = Integer.parseInt(args[12]);//100;
        int secondGenerations = Integer.parseInt(args[5]);//200;
        int maxGenAttempts = Integer.parseInt(args[13]);//20;
        int columnAttempts = Integer.parseInt(args[15]);//3;


        double valueRho = -.5;
        int jiggleGenerations = Integer.parseInt(args[9]);//100;
        double jigglePortion = Double.parseDouble(args[14]);//0.1;
        double baseLineHalfWidth = Double.parseDouble(args[8]);//0.5;
        int maxJigGenAttempts = 20;
        int numberOfBackPasses = Integer.parseInt(args[16]);//3;

        boolean startWithDesign = Boolean.parseBoolean(args[1]);//true;
        boolean backwardJiggleOnly = false;
        boolean backwardJigOn = Boolean.parseBoolean(args[2]);//true;

        String highestMode = null;
        boolean containsCategorical = false;
        String type = null;// = "continuous";
        int discreteLevels = 0;// = 3;
        String mode = null;// = "MQI";
        int numFactors = 0;

    //    if (CMDLine) {


        //    highestMode = "MQI";
        //    containsCategorical = false;
        //    type = "continuous";
        //    discreteLevels = 3;
       //     mode = "MQI";

       //     levels = Integer.parseInt(args[0]);
       //     numFactors = Integer.parseInt(args[1]) - 1;
           // discreteLevels = Integer.parseInt(args[2]);
           // firstTrials = Integer.parseInt(args[2]);

            //first experiments
//            levels = Integer.parseInt(args[0]);
//            populationNumber = Integer.parseInt(args[1]);
//            jigglePopNumber = populationNumber;//must be the same as population number
//            copyPortion = Double.parseDouble(args[2]);
//            newPortion = 0; // % of population that are newly generated columns
//            swapPortion = Double.parseDouble(args[3]);
//            firstTrials = Integer.parseInt(args[4]);
//            firstGenerations = Integer.parseInt(args[5]);
//            poolSetSize = Integer.parseInt(args[6]);
//            secondGenerations = Integer.parseInt(args[7]);
//            valueRho = Double.parseDouble(args[8]);
            //second experiment
//            levels = Integer.parseInt(args[0]);
//            columnAttempts = Integer.parseInt(args[1]);
//            maxGenAttempts = Integer.parseInt(args[2]);

            //third experiment
//            levels = Integer.parseInt(args[0]);
//            maxJigGenAttempts = Integer.parseInt(args[1]);
//            populationNumber = Integer.parseInt(args[2]);
//            jigglePopNumber = populationNumber;//must be the same as population number
//            jiggleGenerations = Integer.parseInt(args[3]);
//            jigglePortion = Double.parseDouble(args[4]);
//            baseLineHalfWidth = Double.parseDouble(args[5]);
//            numberOfBackPasses = Integer.parseInt(args[6]);
//            poolSetSize = Integer.parseInt(args[7]);

            //  jiggleGenerations = Integer.parseInt(args[8]);
            //   jigglePortion = Double.parseDouble(args[9]);
            //   baseLineHalfWidth = Double.parseDouble(args[10]);
            //   numberOfBackPasses = Integer.parseInt(args[11]);
   //     }


//            levels = 17;//Integer.parseInt(args[0]);
//            backwardJigOn = true;//Boolean.parseBoolean(args[1]);
//            firstGenerations = 100;//Integer.parseInt(args[2]);
//            secondGenerations = 200;//Integer.parseInt(args[3]);
//            populationNumber = 100;//Integer.parseInt(args[4]);
//            copyPortion = 0.1;//Double.parseDouble(args[5]);
//            baseLineHalfWidth = 0.5;//Double.parseDouble(args[6]);
//            jiggleGenerations = 100;//Integer.parseInt(args[7]);
//            firstTrials = 3;//Integer.parseInt(args[8]);
//            swapPortion = 0.2;//Double.parseDouble(args[9]);
//            poolSetSize = 100;//Integer.parseInt(args[10]);
//            maxGenAttempts = 20;//Integer.parseInt(args[11]);
//            jigglePortion = 0.2;//Double.parseDouble(args[12]);
//            columnAttempts = 3;//Integer.parseInt(args[13]);
//            numberOfBackPasses = 3;//Integer.parseInt(args[14]);
//            maxConstraint = 0.05;//Double.parseDouble(args[15]);

            maxJigGenAttempts = maxGenAttempts;

//            System.out.println(levels);
//            System.out.println(startWithDesign);
//            System.out.println((backwardJigOn));
//            System.out.println(firstGenerations);
//            System.out.println(secondGenerations);
//            System.out.println(populationNumber);
//            System.out.println(copyPortion);
//            System.out.println(baseLineHalfWidth);
//            System.out.println(jiggleGenerations);
//            System.out.println(firstTrials);
//            System.out.println(swapPortion);
//            System.out.println(poolSetSize);
//            System.out.println(maxGenAttempts);
//            System.out.println(jigglePortion);
//            System.out.println(columnAttempts);
//            System.out.println(numberOfBackPasses);
//            System.out.println(maxConstraint);

        //must have more than 1 column attempt!
        boolean writeOutToFiles = true;

//        if (!CMDLine) {
//            if (backwardJiggleOnly == true) {//skip over the algorithm and only do the backward jig
//                System.out.println("backward Jiggle Only");
//                if (backwardJiggleOnly == true && startWithDesign == false) {
//                    System.out.println("start with design must be true if backward Jiggle Only is true");
//                }
//            }
//        }


        int maxAttempts = 4;
        double avgCorrelationCriteria = 0.07;
        boolean newPopEachTrial = true; //this should always be set to true for continuous factors.  If not, it will screw up the basis, use false only for discrete
        boolean bestOfPoolSet = true;
        //diagnosticsOn will generate diagnostics for each generation
        //this should only be on when testing one level!!!! (startLevel == endLevel)
        boolean diagnosticsOn = false;
        boolean startWithPopulation = false;




        //initializaton
        Timer timer = new Timer();
        Timer columnTimer = new Timer();
        Timer totalTime = new Timer();

        String fileName = null;
        DOEFactorArray inputs = null;

        if (!CMDLine) {
            fileName = "inputs.csv";
            inputs = new DOEFactorArray(fileName);
            type = inputs.getFactorArray()[1][1];
            //decrement the first subFactor
            int numType = Integer.parseInt(inputs.getFactorArray()[1][0]);
            if (!startWithDesign) {
                numType = numType - 1;
            }
            inputs.getFactorArray()[1][0] = Integer.toString(numType);
            discreteLevels = Integer.parseInt(inputs.getFactorArray()[1][2]);
            mode = inputs.getFactorArray()[1][3];//first mode
            highestMode = findHighestMode(inputs);
            containsCategorical = findIfCategorical(inputs);
        }

        DesignColumn firstDesignColumn = new DesignColumn(levels, true, randomNumber, type, discreteLevels);
        firstDesignColumn.setMode(mode);
        Design design = new Design(firstDesignColumn, startWithDesign, randomNumber, highestMode);


        GeneticAlgorithm ga = new GeneticAlgorithm(levels, populationNumber,
                maxConstraint, valueRho, copyPortion, highestMode,
                poolSetSize, swapPortion, useValueFunction, startWithDesign,
                maxAttempts, avgCorrelationCriteria, bestOfPoolSet, startWithPopulation,
                newPortion, randomNumber, jigglePopNumber, jiggleGenerations,
                jigglePortion, design, maxJigGenAttempts, CMDLine, showComments);

        timer.resetTimer();
        columnTimer.resetTimer();
        if (!CMDLine) {
            if (showComments) {
            System.out.println("design Points: " + levels + " seed: " + seed);
            }
        }
        double previousCorr = 1.0;

        int numberOfSameAttempts = 0;
        boolean performJiggle = false;
        boolean jigglePerformed = false;
        boolean addLastColumn = true;
        int cumulateCol = 0;


        SimpleStats firstGenStats = new SimpleStats();
        SimpleStats secondGenStats = new SimpleStats();
        int inputRows = 1;

        if (!CMDLine) {
            inputRows = inputs.getNumberOfDesignPoints();
        }

        if (!backwardJiggleOnly) {
            for (int inputRow = 1; inputRow <= inputRows; inputRow++) {
                if (!CMDLine) {
                    numFactors = Integer.parseInt(inputs.getFactorArray()[inputRow][0]);
                }
                for (int subFactor = 0; subFactor < numFactors; subFactor++) {
                    cumulateCol += 1;
                    if (!CMDLine) {
                        type = inputs.getFactorArray()[inputRow][1];
                        discreteLevels = Integer.parseInt(inputs.getFactorArray()[inputRow][2]);
                        mode = inputs.getFactorArray()[inputRow][3];
                    }

                    ga.setColumnType(type);
                    ga.setDiscreteLevels(discreteLevels);
                    ga.setMode(mode);

                    boolean foundColumn = false;
                    double corrAttempt = 0;
                    double minAttempt = 9999;
                    for (int k = 1; k <= columnAttempts; k++) {
                        if (!CMDLine) {
                            if (showComments) {
                            System.out.println();
                            System.out.println(cumulateCol + " column, " + type + " factor type, " + discreteLevels + " discreteLevels, " + "mode: " + mode + ", " + k + " columnAttempt, " + "designSize: " + ga.getDesign().getMain().size());
                            }
                        }
                        double corr = 0;
                        double minCorr = 9999;
                        int countOf1stGens;
                        int countOf2ndGens;

                        ga.createNewPopulation();

                        //exploration trials
                        for (int j = 1; j <= firstTrials; j++) {

                            countOf1stGens = performGenerations(ga, firstGenerations, maxGenAttempts);
                            firstGenStats.newObs(countOf1stGens);


                            corr = ga.getPopulation().getPop()[0].getCorrelation();
                            if (corr < minCorr) {
                                minCorr = corr;
                                ga.copyPopulation(ga.getPopulation(), ga.getBestPopulation());//save the best population
                            }
                            //  System.out.println(j + " first trial corr: " + ga.getPopulation().getPop().get(0).getCorrelation());
                            if (!CMDLine) {
                                if (showComments) {
                                System.out.println(j + " exploration trial correlation: " + ga.getPopulation().getPop()[0].getCorrelation());
                                }
                            }
                            if (corr == 0.0) {
                                break;
                            }
                            ga.createNewPopulation();//maybe we can skip this after the last trial
                        } // end of first trials
                        if (!CMDLine) {
                            if (showComments) {
                            System.out.println("best from exploration generations: " + minCorr
                                    + " time elapsed: " + timer.elapsedTime() + " minutes");
                            }
                        }
                        timer.resetTimer();

                        //    ga.readInPopulation();
                        ga.copyPopulation(ga.getBestPopulation(), ga.getPopulation());//set to best population
                        //exploitation Phase


                        countOf2ndGens = performGenerations(ga, secondGenerations, maxGenAttempts);
                        secondGenStats.newObs(countOf2ndGens);
                        //replace these with same code above
//                        if (type.equals("continuous")) {
//                            continuousSecondTrials(ga, secondGenerations, timer, previousCorr,
//                                    numberOfSameAttempts, seed, totalTime, baseLineHalfWidth);
//                        }
//                        if (type.equals("discrete")) {
//                            discreteSecondTrials(ga, secondGenerations, timer, previousCorr,
//                                    numberOfSameAttempts, seed, totalTime);
//                        }

                        // bestCol = ga.getPopulation().getPop().get(0);
                        // corrAttempt = ga.getPopulation().getPop().get(0).getCorrelation();
                        corrAttempt = ga.getPopulation().getPop()[0].getCorrelation();
                        if (corrAttempt < minAttempt) {
                            minAttempt = corrAttempt;
                            // ga.setBestColumn(ga.getPopulation().getPop().get(0));
                            ga.setBestColumn(ga.getPopulation().getPop()[0]);
                            ga.setBestColCorr(minAttempt);
                        }


                        foundColumn = ga.checkColumnAddition(avgCorrelationCriteria);
                        if (foundColumn) {
                            if (!CMDLine) {
                                if (showComments) {
                               // System.out.println("added col from exploitation generations ");
                                }
                            }
                            //reset column Attempts so we more on to the next column
                            k = columnAttempts + 1;
                        } else {
                            //if the correlation is way off the maxCorrealtion, then reset the column attempt so we more one to the next column
                        }
                        if (!CMDLine) {
                            if (showComments) {
                            System.out.println("best from exploitation generations: " + ga.getBestColCorr());
                            System.out.println();
                            }
                        }
                        //          System.out.println("column attempt time elapsed: " + columnTimer.elapsedTime() + " minutes"
                        //                   + " estimated time to finish: " + columnTimer.elapsedTime() * (secondTrials - k) / 60 + " hrs");
                        columnTimer.resetTimer();
                        ga.createNewPopulation(); //may be able to skip this if this is the last number of column attempts
                    }// end of column attempts

                    //if we finished our set column attempts, add the best column so far and then perform the backward jig
                    if (!foundColumn) {
                        ga.addNewColumn(ga.getBestColumn());
                        //              backwardJig(ga, design, 2, baseLineHalfWidth);
                    }
                    //add the best column from the column attempts
                }//end of sub factor
            }//end of input row
        }
        if (backwardJigOn) {
            backwardJig(ga, design, numberOfBackPasses, baseLineHalfWidth, CMDLine, showComments);
        }

        double corrForOutput = findMaxCorrelation(design.getMain(), design.getQuadratics(),
                design.getInteractions(), highestMode);
        double ML2ForOutput = calculateML2(design.getMain());

        if (!CMDLine) {
            if (writeOutToFiles) {
                writeOutToFiles(ga, highestMode, seed, containsCategorical, corrForOutput, ML2ForOutput, totalTime.elapsedTime());
            }

          //  printCorrelations(design, highestMode);

         //   System.out.println("max design corr: " + findMaxCorrelation(design.getMain(), design.getQuadratics(),
          //          design.getInteractions(), highestMode));

           // System.out.println("firstGenStats Mean: " + firstGenStats.sampleMean() + " std: " + firstGenStats.sampleStdDev() + " size: " + firstGenStats.sampleSize());
           // System.out.println("secondGenStats Mean: " + secondGenStats.sampleMean() + " std: " + secondGenStats.sampleStdDev() + " size: " + secondGenStats.sampleSize());
            if (showComments) {
            printOutput(design, totalTime.elapsedTime(), seed, highestMode, containsCategorical);
            }

          //  System.out.println("seed: " + seed);
        }

        if (CMDLine) {
            // double corrForCMDLine = findMaxCorrelation(design.getMain(), design.getQuadratics(),
            //          design.getInteractions(), highestMode);
            //  double ML2ForCMDLine = calculateML2(design.getMain());
            CMDLinePrintOutput(design.getMain().size(), corrForOutput, ML2ForOutput, args, totalTime.elapsedTime(), seed, firstGenStats, secondGenStats);
            writeOutOneFile(ga, highestMode, seed, containsCategorical, corrForOutput, ML2ForOutput, totalTime.elapsedTime());
        }
        ga = null;
    }

    public static int performGenerations(GeneticAlgorithm ga, int numberOfGenerations, int maxGenAttempts) {
        int genAttempts = 0;
        double corr = 0;
        double minGenCorr = 9999;
        int numOfGenerations = 0;
        for (int i = 1; i <= numberOfGenerations; i++) {
            ga.calculatePopulationCorrelation(ga.getPopulation());
            genAttempts += 1;
            corr = ga.getPopulation().getPop()[0].getCorrelation();
            if (corr < minGenCorr) {
                genAttempts = 0;
                minGenCorr = corr;
            } else {
                if (genAttempts == maxGenAttempts) {
                    break;
                }
            }
            //break out under a condition
            ga.createNextGeneration();//maybe we can skip this after the last generation
            numOfGenerations = i;
        }//end of generations
        // return minGenCorr;
        return numOfGenerations;
    }

    public static ArrayList<DesignColumn> addDummyToDesign(Design design) {

        ArrayList<DesignColumn> newArrayList = design.getMain();
        ArrayList<DesignColumn> removeList = new ArrayList<DesignColumn>();
        //add in dummy variables
        int categoricalIndex = 1;
        //the catIndex is used while calculating the design's correlation. We don't need to calculate correlation among a
        // a categorical variable's dummies
        for (int i = 0; i < newArrayList.size(); i++) {
            DesignColumn column = newArrayList.get(i);
            if (column.getType().equals("categorical")) {
                removeList.add(column);

                int dummySize = column.getDummy().size();
                for (int dummyCol = 0; dummyCol < dummySize; dummyCol++) {
                    // newDummy.add(dummyCol, column.getDummy().get(dummyCol).clone());
                    DesignColumn tempDummy = new DesignColumn(column.getLevels(), false, column.getRandomNumber(), "dummy", column.getDiscreteLevels());
                    tempDummy.setCatIndex(categoricalIndex);
                    tempDummy.setDesignColumn(column.getDummy().get(dummyCol).clone());
                    newArrayList.add(tempDummy);
                }
                categoricalIndex += 1;//increment the cat Index
            }
        }
        //remove categorical, find the index value in newArrayList of object in the removeList array
        for (int i = 0; i < removeList.size(); i++) {
            int indexToRemove = newArrayList.indexOf(removeList.get(i));
            newArrayList.remove(indexToRemove);
        }
        return newArrayList;
    }

    public static void backwardJig(GeneticAlgorithm ga, Design design, int numberOfBackPasses, double baseLineHalfWidth, boolean CMDLine, boolean showComments) {
        if (design.getMain().size() > 1) {
            for (int p = 0; p < numberOfBackPasses; p++) {
                if (!CMDLine) {
                    if (showComments) {
                    System.out.println(p + 1 + " jiggle pass:");
                    }
                }
                ga.backwardJigglePass(baseLineHalfWidth);
                if (!CMDLine) {
                    if (showComments) {
                    System.out.println();
                    }
                }
            }
        }
    }

    public static void continuousSecondTrials(GeneticAlgorithm ga, int secondGenerations, Timer timer,
            double previousCorr, int numberOfSameAttempts, int seed, Timer totalTime,
            double baseLineHalfWidth) {

        //exploitation phase
        double secondTrialCorr = 0;
        for (int i = 1; i <= secondGenerations; i++) {
            ga.calculatePopulationCorrelation(ga.getPopulation());

            //break out under a condition


            ga.createNextGeneration();
        }//end of generations

        //  secondTrialCorr = ga.getPopulation().getPop().get(0).getCorrelation();
        secondTrialCorr = ga.getPopulation().getPop()[0].getCorrelation();
        //  System.out.println("best from exploitation: " + secondTrialCorr
        //          + "                       time elapsed: " + timer.elapsedTime() + " minutes");
        timer.resetTimer();
    }

    public static void discreteSecondTrials(GeneticAlgorithm ga, int secondGenerations, Timer timer,
            double previousCorr, int numberOfSameAttempts, int seed, Timer totalTime) {

        double secondTrialCorr = 0;
        //     for (int j = 1; j <= secondTrials; j++) {

        for (int i = 1; i <= secondGenerations; i++) {
            ga.calculatePopulationCorrelation(ga.getPopulation());
            ga.createNextGeneration();
        }//end of generations

        //secondTrialCorr = ga.getPopulation().getPop().get(0).getCorrelation();
        secondTrialCorr = ga.getPopulation().getPop()[0].getCorrelation();
        //  System.out.println(" best from exploitation: " + secondTrialCorr);



        //    } // end of second trials
    }

    /**
     * find the highest mode MQI, MQ or M to set the the GA's mode
     * @param inputs
     * @return
     */
    public static String findHighestMode(DOEFactorArray inputs) {
        String highestMode = "M";
        String tempMode = "M";
        for (int i = 1; i <= inputs.getNumberOfDesignPoints(); i++) {
            tempMode = inputs.getFactorArray()[i][3];
            if (tempMode.equals("MQ")) {
                highestMode = "MQ";
            }
            if (tempMode.equals("MI")) {
                highestMode = "MI";
            }
            if (tempMode.equals("MQI")) {
                highestMode = "MQI";
            }
        }
        return highestMode;
    }

    /**
     * find whether there is a categorical variable in here or not
     * @param inputs
     * @return
     */
    public static boolean findIfCategorical(DOEFactorArray inputs) {
        boolean cat = false;
        String tempType = "";
        for (int i = 1; i <= inputs.getNumberOfDesignPoints(); i++) {
            tempType = inputs.getFactorArray()[i][1];
            if (tempType.equals("categorical")) {
                cat = true;
            }
        }
        return cat;
    }

    public static void printCorrelations(Design design, String highestMode) {
        if (highestMode.equals("MI")) {
            System.out.println("main with interactions");
            calculateInteractionCorrelations(design.getMain(), design.getInteractions());
            System.out.println("interactions with interactions");
            calculateIntersWithInters(design.getInteractions());
        }

        if (highestMode.equals("MQ")) {
            System.out.println("main with quadratics");
            calculateQuadraticCorrelations(design.getMain(), design.getQuadratics());
            System.out.println("quadratics with quadratics");
            calculateCorrelations(design.getQuadratics());
        }

        if (highestMode.equals("MQI")) {
            System.out.println("main with quadratics");
            calculateQuadraticCorrelations(design.getMain(), design.getQuadratics());
            System.out.println("quadratics with quadratics");
            calculateCorrelations(design.getQuadratics());
            System.out.println("interaction with quadratics");
            calculateInteractionCorrelations(design.getQuadratics(), design.getInteractions());
            System.out.println("interactions with interactions");
            calculateIntersWithInters(design.getInteractions());
            System.out.println("interactions with main");
            calculateInteractionCorrelations(design.getMain(), design.getInteractions());
        }

        System.out.println("main with main");
        calculateCorrelations(design.getMain());
        System.out.println();
    }

    public static void writeOutToFiles(GeneticAlgorithm ga, String highestMode, int seed, boolean containsCategorical, double corr, double ML2, double time) {

        ArrayList<DesignColumn> main = ga.getDesign().getMain();
        int rows = main.get(0).getDesignColumn().length;
        int designSize = main.size();
        double corrRound = Round(corr,2);
        double ML2Round = Round(ML2,2);

        ArrayList<DesignColumn> fullDesign = new ArrayList<DesignColumn>();
        fullDesign = combineArrayLists(main, ga.getDesign().getQuadratics(), ga.getDesign().getInteractions());
        int fullDesignSize = fullDesign.size();

       // String fullDesignName = "designOutput/" + highestMode + "/fullDesign/fullDesign " + rows + " levels "
       //         + designSize + " cols " + seed + ".csv";
       // clearCsvFile(fullDesignName);
       // writeHeadersToFullDesign(fullDesignName, main.size());
       // writeArrayListToCsv(fullDesign, fullDesignName);


     //   String designName = rows + " levels " + designSize + " cols " + seed + ".csv";
     //   clearCsvFile(designName);
     //   writeHeadersToDesign(designName, main.size(), main, corr, ML2, time);
     //   writeArrayListToCsv(main, designName);

        String translatedName = rows + " rows " + designSize + " cols " + corrRound + " corr " + ML2Round + " ML2 " + seed + " SEED" +".csv";
        clearCsvFile(translatedName);
        writeHeadersToTranslatedDesign(translatedName, main.size(), main);
        writeTranslatedArrayListToCsv(main, translatedName);

//        if (containsCategorical) {
//            String withDummyName = "withDummy " + rows + " levels " + designSize + " cols " + seed + ".csv";
//            clearCsvFile(withDummyName);
//
//            writeDummyArrayListWithHeadersToCsv(addDummyToDesign(ga.getDesign()), withDummyName);
//        }




    }

    public static void writeOutOneFile(GeneticAlgorithm ga, String highestMode, int seed, boolean containsCategorical, double corr, double ML2, double time) {
        ArrayList<DesignColumn> main = ga.getDesign().getMain();
        int rows = main.get(0).getDesignColumn().length;
        int designSize = main.size();

        String designName = rows + " levels " + designSize + " cols " + seed + ".csv";
        clearCsvFile(designName);
        writeHeadersToDesign(designName, main.size(), main, corr, ML2, time);
        writeArrayListToCsv(main, designName);
    }

    public static void printArrayList(ArrayList<DesignColumn> arrayList) {
        //grab the first array column to get the number of rows
        for (int row = 0; row < arrayList.get(0).getDesignColumn().length; row++) {
            for (int col = 0; col < arrayList.size(); col++) {
                double[] column = arrayList.get(col).getDesignColumn();

                System.out.print(column[row] + " ");
            }
            System.out.println();
        }

    }

    public static void printOutput(Design design, double time, int seed, String highestMode, boolean containsCategorical) {

        if (containsCategorical) {
            ArrayList<DesignColumn> designArray = addDummyToDesign(design);
            System.out.print("design size with dummy variables: " + designArray.size() + ", ");
            System.out.print("maximum correlation: " + findMaxCorrelation(designArray, design.getQuadratics(), design.getInteractions(), highestMode) + ", ");
        } else {
            System.out.print("design size: " + design.getMain().size() + ", ");
            System.out.print("maximum correlation: " + findMaxCorrelation(design.getMain(), design.getQuadratics(), design.getInteractions(), highestMode) + ", ");
        }


        System.out.print("time: " + time / 60 + ",");
        System.out.print("ML2: " + calculateML2(design.getMain()) + ",");
        System.out.print("seed: " + seed + ", ");
        System.out.println();
    }

    public static void CMDLinePrintOutput(int designSize, double maxCorr, double ML2, String[] args, double time, int seed, SimpleStats firstGens, SimpleStats secondGens) {
        //print column headers
//         System.out.print("popNum, ");
//         System.out.print("copyPortion, ");
//         System.out.print("swapPortion, ");
//         System.out.print("firstTrials, ");
//         System.out.print("firstGen, ");
//         System.out.print("poolSize, ");
//         System.out.print("secondGen, ");
//         System.out.print("rho, ");
//         System.out.print("jigGen, ");
//         System.out.print("jigPortion, ");
//         System.out.print("baseHW, ");
//         System.out.print("numBack, ");
//         System.out.print("cols, ");
//         System.out.print("maxCorr, ");
//         System.out.print("time, ");
//         System.out.print("ML2, ");
//         System.out.print("seed, ");
//

        for (int i = 0; i < args.length; i++) {
            System.out.print(args[i] + ",");
        }
        System.out.print(designSize + ",");
        System.out.print(maxCorr + ",");
        System.out.print(ML2 + ",");
        System.out.print(time + ",");
        System.out.print(seed + ",");
        System.out.print(firstGens.sampleMean() + ",");

        System.out.print(firstGens.sampleStdDev() + ",");
        System.out.print(firstGens.sampleSize() + ",");
        System.out.print(secondGens.sampleMean() + ",");
        System.out.print(secondGens.sampleStdDev() + ",");
        System.out.print(secondGens.sampleSize() + ",");
        System.out.print(secondGens.max() + ",");
        System.out.print(secondGens.min() + ",");
        System.out.println();
    }
     /**
     * round double to the specified decimal place
     * @param Rval
     * @param Rpl
     * @return
     */
    public static double Round(double Rval, int Rpl) {
        double p = (double) Math.pow(10, Rpl);
        Rval = Rval * p;
        double tmp = Math.round(Rval);
        return (double) tmp / p;
    }
}
//            //print fitness of last population
//            //all are zero when we generate a new population
//            for (int i = 0; i < ga.getPopNumber(); i++) {
//                System.out.println(ga.getPopulation().get(i).getFitness() + " ");
//            }
//print the selection spin wheels and cdf
//            for (int col = 0; col < 2; col++) {
//                for (int row = 0; row < ga.getPopNumber(); row++) {
//                    System.out.print(ga.getSelectionProbs()[row][col] + " ");
//                }
//                System.out.println();
//            }
//            String popName = "pop" + levelNumber + ".csv";
//            clearCsvFile(popName);
//            writeArrayListToCsv(ga.getPopulation(), popName);
//append to master file
//            writeToMaster(masterName, levelNumber, designSize, designName, maxConstraint);
//            if (diagnosticsOn) {
//                String diagnosticsName = "../designOutput/diagnostics" + rows + ".csv";
//                clearCsvFile(diagnosticsName);
//                writeHeadersToDiagnostics(diagnosticsName);
//                writeDiagnosticsToCsv(ga.getDiagnostics(), diagnosticsName);
//            }
//empty out ga to prepare for the next levelNumber
//            System.out.println("trial " + trials);
//            System.out.println("correlation, inverse, fitness");
//            for (int row = 0; row < ga.getPopulation().size(); row++) {
//                System.out.print(ga.getPopulation().get(row).getCorrelation() + ", ");
//                System.out.print(ga.getPopulation().get(row).getInverseCorrelation() + ", ");
//                System.out.print(ga.getPopulation().get(row).getFitness() + ", ");
//                System.out.println();
//            }
//            System.out.println();
//            printArrayList(ga.getPopulation());
//            System.out.println("design");
//            printArrayList(ga.getDesign());
//                if (previousCorr == currentCorr) {
//                    //maybe we will break out of the generation
//                    // if the first current corr is 0 then we break out right away
//                    //since previous corr is initially set to zero
//                    boolean check = true;
//                    timesTheSame += 1;
//                    if (timesTheSame > 2) {
//                        boolean check2 = true;
//                    }
//                    if (timesTheSame == 2 && sameSegment) {
//                        System.out.println("New Population");
//                        System.out.println();
//                        System.out.println();
//                        System.out.println();
//                        System.out.println();
//                        ga.createNewPopulation();
//                        newPopCount += 1;
//                        //break;
//                    }
//                }
//  System.out.println("best correlation: " + bestCorrelation);
//                System.out.println();
//                System.out.println("after new genertion");
//                for (int row = 0; row < ga.getPopulation().size(); row++) {
//                    System.out.println(ga.getPopulation().get(row).getCorrelation() + ", ");
//                }
//                    double bestCorrelation = ga.getPopulation().get(0).getCorrelation();
//                    int segment = (int) (ga.getCopyPortion() * ga.getPopNumber());
////                    if (bestCorrelation < maxConstraint && i > 3) {
////                        boolean check = true;
////                        //       break;
////                    }
//                    if (bestCorrelation == ga.getPopulation().get(segment).getCorrelation()) {
//                        boolean check = true;
//                        sameSegment = true;
//
//                         if (timesTheSame == 2 && sameSegment) {
//                              ga.createNewPopulation();
//                         }
//
//
//                        //System.out.println("BreakOut");
//                        //     break;
//                    } else {
//                        sameSegment = false;
//                    }
//
//                    if (currentCorr < 0.00000001) {
//                        break;
//                    }
//turn on when we find designs that max limit
//do this with an exsiting design
//               if(ga.getDesign().size() > 10){
//                  ga.setMode("mainOnly");
//               }
//set up master file
//        String masterName = "master.csv";
//        clearCsvFile(masterName);
//        writeHeadersToMaster(masterName);
//                        System.out.println("population");
//                        printArrayList(ga.getPopulation());
//                    if (i == generations) {
//                        for (int row = 0; row < ga.getPopulation().size(); row++) {
//                            System.out.println(ga.getPopulation().get(row).getCorrelation() + ", ");
//                        }
//
//                    }

