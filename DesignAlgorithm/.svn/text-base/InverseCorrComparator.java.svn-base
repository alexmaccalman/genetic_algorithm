
package DesignAlgorithm;

import java.util.Comparator;

/**
 *
 * @author maccalman
 */
public class InverseCorrComparator implements Comparator<DesignColumn>{

    public int compare(DesignColumn column1, DesignColumn column2){
        if(column1.getInverseCorrelation() < column2.getInverseCorrelation()){
            return 1;
        }
        if(column1.getInverseCorrelation() > column2.getInverseCorrelation()){
            return -1;
        }
        return 0;
    }
}
