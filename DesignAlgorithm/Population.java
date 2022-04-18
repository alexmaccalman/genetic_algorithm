
package DesignAlgorithm;

//import DesignAlgorithm.*;
import java.util.ArrayList;

/**
 *
 * @author maccalman
 */
public class Population {

  //  private ArrayList<DesignColumn> pop;
    private DesignColumn[] pop;
    private double[][] selectionProbs;
    private double popMin;
    private double popMax;
    private double popMean;
    private int popSize;
    private boolean firstCalculation;

    public Population(int popNumber){
      //  pop = new ArrayList<DesignColumn>(popNumber);
        pop = new DesignColumn[popNumber];
        this.popSize = popNumber;
        //column 1 is the probabilites (pdf)
        //column 2 is the cdf
        selectionProbs = new double[popNumber][2];
        firstCalculation = true;
    }

//    /**
//     * @return the population
//     */
//    public ArrayList<DesignColumn> getPop() {
//        return pop;
//    }
//
//    /**
//     * @param population the population to set
//     */
//    public void setPop(ArrayList<DesignColumn> population) {
//        this.pop = population;
//    }

    /**
     * @return the population
     */
    public DesignColumn[] getPop() {
        return pop;
    }

    /**
     * @param population the population to set
     */
    public void setPop(DesignColumn[] population) {
        this.pop = population;
    }

    /**
     * @return the popMin
     */
    public double getPopMin() {
        return popMin;
    }

    /**
     * @param popMin the popMin to set
     */
    public void setPopMin(double popMin) {
        this.popMin = popMin;
    }

    /**
     * @return the popMax
     */
    public double getPopMax() {
        return popMax;
    }

    /**
     * @param popMax the popMax to set
     */
    public void setPopMax(double popMax) {
        this.popMax = popMax;
    }

    /**
     * @return the popMean
     */
    public double getPopMean() {
        return popMean;
    }

    /**
     * @param popMean the popMean to set
     */
    public void setPopMean(double popMean) {
        this.popMean = popMean;
    }

    /**
     * @return the firstCalculation
     */
    public boolean isFirstCalculation() {
        return firstCalculation;
    }

    /**
     * @param firstCalculation the firstCalculation to set
     */
    public void setFirstCalculation(boolean firstCalculation) {
        this.firstCalculation = firstCalculation;
    }

    /**
     * @return the selectionProbs
     */
    public double[][] getSelectionProbs() {
        return selectionProbs;
    }

    /**
     * @param selectionProbs the selectionProbs to set
     */
    public void setSelectionProbs(double[][] selectionProbs) {
        this.selectionProbs = selectionProbs;
    }

    /**
     * @return the popSize
     */
    public int getPopSize() {
        return popSize;
    }

    /**
     * @param popSize the popSize to set
     */
    public void setPopSize(int popSize) {
        this.popSize = popSize;
    }
    

}
