package DesignAlgorithm;

//import net.goui.util.MTRandom;
import DesignAlgorithm.MTRandom;
import java.util.Random;
import java.util.ArrayList;
import static DesignAlgorithm.Correlation.*;

/**
 *
 * @author maccalman
 */
public class Design {

    private ArrayList<DesignColumn> main; //k
    private ArrayList<DesignColumn> quadratics; //2k
    private ArrayList<DesignColumn> interactions; //k(k-1)/2
    private int designSize; // k
    private DesignColumn finalColumn;

    public Design(DesignColumn firstColumn, boolean startWithDesign, MTRandom randomNumber, String highestMode) {
        //later initialize these to the specified column number
        main = new ArrayList<DesignColumn>();
        quadratics = new ArrayList<DesignColumn>();
        interactions = new ArrayList<DesignColumn>();

        if (startWithDesign) {
            initializeWithStartDesign(randomNumber, highestMode);
        } else {
            initialize(firstColumn);
        }
    }

    public void initialize(DesignColumn firstColumn) {

        //create first column
        //DesignColumn firstDesignColumn = new DesignColumn(getLevels(), true, getRandomNumber(), getColumnType(), getDiscreteLevels());

        main.add(firstColumn);

        //if checking against quadratics, add the quatradic term to the quadratic arrayList

        DesignColumn quad = new DesignColumn(firstColumn.getLevels(), false, firstColumn.getRandomNumber(), firstColumn.getType(), firstColumn.getDiscreteLevels());
        //grab the column from the population and set its quadratic
        //to the new column just created
        double[] quadCol = new double[firstColumn.getLevels()];
        //squares the first design column
        if (!firstColumn.getType().equals("categorical")) {
            quadCol = firstColumn.createQuadratic();
            //sets the double[] design column of the newly created design
            // (quadratic) column object. Unless it is a categorical variable
            quad.setDesignColumn(quadCol);
            //add the new quadratic to the quadratic array list
            quadratics.add(quad);
        }
    }

    public void initializeWithStartDesign(MTRandom randomNumber, String highestMode) {

        String fileName = "Startdesign.csv";
        DOEFactorArray startDesign = new DOEFactorArray(fileName);
        int numberOfFactors = startDesign.getNumberOfFactors();
        int numberOfDesignPoints = startDesign.getNumberOfDesignPoints() - 3;
        //set levels to what is in the cvs file, this may change what is set in the main method
        //   setLevels(numberOfDesignPoints);

        for (int i = 0; i < numberOfFactors; i++) {
            double[] col = new double[numberOfDesignPoints];
            for (int j = 4; j < numberOfDesignPoints + 4; j++) {//set to 4 with type string at first row and numner of discrete levels second at second row
                col[j - 4] = Double.parseDouble(startDesign.getFactorArray()[j][i]);
            }
            String factorType = startDesign.getFactorArray()[0][i];
            int discreteLevels = Integer.parseInt(startDesign.getFactorArray()[1][i]);
            String factorMode = startDesign.getFactorArray()[2][i];
            DesignColumn designCol = new DesignColumn(numberOfDesignPoints, false, randomNumber, factorType, discreteLevels);
            designCol.setDesignColumn(col);
            designCol.setMode(factorMode);
            designCol.setStartCol(true);
            
            if (designCol.getType().equals("categorical")) {
                designCol.createDummy(designCol.getDiscreteLevels());
            }


            designCol.setMean(designCol.findMean(col));
            designCol.setDesignColumn(designCol.centerColumn(designCol.getDesignColumn()));
            if (designCol.getType().equals("continuous")) {//if continuous round it so that the lower and upper bound will match identically
                for (int k = 0; k < col.length; k++) {
                    col[k] = Round(col[k], 2);
                }
            }
         
            System.arraycopy(col, 0, designCol.getBasis(), 0, col.length);// set the basis
            getMain().add(designCol);

            if (!designCol.getType().equals("categorical")) {
                //categorical factors don't have quadratics
          //      if (highestMode.equals("MQI") || highestMode.equals("MQ")) {
                    DesignColumn quad = new DesignColumn(numberOfDesignPoints, false, randomNumber, factorType, discreteLevels);
                    double[] quadCol = new double[numberOfDesignPoints];
                    quadCol = designCol.createQuadratic();
                    quad.setDesignColumn(quadCol);
                    designCol.setQuadratic(quadCol);
                    quad.setType(factorType);
                    quad.setDiscreteLevels(discreteLevels);
                    quad.setMode(factorMode);

                
                    getQuadratics().add(quad);
           //     }
            }


       //     if (highestMode.equals("MQI") || highestMode.equals("MI")) {
                ArrayList<DesignColumn> interactionSet = createStartInteractions(designCol, getMain());
                for (int k = 0; k < interactionSet.size(); k++) {
                    //  int interactionSize = getInteractions().size();
                    DesignColumn inter = interactionSet.get(k);
                    inter.setType(factorType);
                    inter.setMode(factorMode);
                    getInteractions().add(inter);
                }
       //     }
        }
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
                DesignColumn interaction = new DesignColumn(column.getDesignColumn().length, false, column.getRandomNumber(), column.getType(), column.getDiscreteLevels());
                interaction.setDesignColumn(interactionColumn);
                tempInteractionList.add(interaction);
            }

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

//    public void backwardJigglePass(GeneticAlgorithm ga, double baseLineHalfWidth, String mode) {
//        int designSize = main.size();
//        for (int i = designSize - 1; i >= 0; i--) {
//
//            DesignColumn tempColumn = main.get(i);
//            if (tempColumn.equals("continuous")) {
//                main.remove(i);
//                DesignColumn tempQuad = new DesignColumn(tempColumn.getLevels(), false, tempColumn.getRandomNumber(), tempColumn.getType(), tempColumn.getDiscreteLevels());
//                if (mode.equals("MQ") || mode.equals("MQI")) {
//                    tempQuad = quadratics.get(i);
//                    quadratics.remove(i);
//                }
//                ArrayList<DesignColumn> tempInteractions = new ArrayList<DesignColumn>();
//                if (mode.equals("MQI")) {
//                    for (int j = 0; j < interactions.size(); j++) {
//                        int tempSize = tempInteractions.size();
//                        tempInteractions.add(interactions.get(j));
//                    }
//                }
//
//                quadratics.clear();
//                interactions.clear();
//                ga.calculateMaxCorrelation(tempColumn);
//
//                ga.createQuadsAndInters(main);
//
//                DesignColumn jigCol = ga.createJigglePopulation(tempColumn, baseLineHalfWidth);
//                System.out.println(i + " recursive jig:" + jigCol.getCorrelation() + " tempCol: " + tempColumn.getCorrelation());
//                if (jigCol.getCorrelation() < tempColumn.getCorrelation()) {
//
//                    ga.addNewColumn(jigCol);
//
//
//                } else {
//                    main.add(tempColumn);
//                    if (mode.equals("MQ") || mode.equals("MQI")) {
//                        quadratics.add(tempQuad);
//                    }
//                    if (mode.equals("MQI")) {
//                        setInteractions(tempInteractions);
//                    }
//                }
//                tempColumn = null;
//                tempQuad = null;
//                tempInteractions = null;
//            }
//
//        }
//    }
//    //before we use start with design we must tell it what type the columns are in the start design.
//     public void initializeWithStartDesign() {
//
//        String fileName = "startDesign/design.csv";
//        DOEFactorArray startDesign = new DOEFactorArray(fileName);
//        int numberOfFactors = startDesign.getNumberOfFactors();
//        int numberOfDesignPoints = startDesign.getNumberOfDesignPoints();
//        //set levels to what is in the cvs file, this may change what is set in the main method
//     //   setLevels(numberOfDesignPoints);
//
//        for (int i = 0; i < numberOfFactors; i++) {
//            double[] col = new double[numberOfDesignPoints];
//            for (int j = 1; j < numberOfDesignPoints + 1; j++) {
//                col[j - 1] = Double.parseDouble(startDesign.getFactorArray()[j][i]);
//            }
//            DesignColumn designCol = new DesignColumn(numberOfDesignPoints, false, firstColumn.getRandomNumber(), firstColumn.getType(), firstColumn.getDiscreteLevels());
//            designCol.setDesignColumn(col);
//            //this is the wrong mean, need the translated mean
//            designCol.setMean(designCol.findMean(col));
//            System.arraycopy(col, 0, designCol.getBasis(), 0, col.length);// set the basis
//            getDesign().getMain().add(designCol);
//
//            if (getMode().equals("MQI") || getMode().equals("MQ")) {
//                DesignColumn quad = new DesignColumn(numberOfDesignPoints, false, firstColumn.getRandomNumber(), firstColumn.getType(), firstColumn.getDiscreteLevels());
//                double[] quadCol = new double[numberOfDesignPoints];
//                quadCol = designCol.createQuadratic();
//                quad.setDesignColumn(quadCol);
//                getDesign().getQuadratics().add(quad);
//            }
//            if (getMode().equals("MQI")) {
//                ArrayList<DesignColumn> interactionSet = createStartInteractions(designCol, getDesign().getMain());
//                for (int k = 0; k < interactionSet.size(); k++) {
//                    //  int interactionSize = getInteractions().size();
//                    getDesign().getInteractions().add(interactionSet.get(k));
//                }
//            }
//        }
//    }
    /**
     * @return the main
     */
    public ArrayList<DesignColumn> getMain() {
        return main;
    }

    /**
     * @param main the main to set
     */
    public void setMain(ArrayList<DesignColumn> main) {
        this.main = main;
    }

    /**
     * @return the quadratics
     */
    public ArrayList<DesignColumn> getQuadratics() {
        return quadratics;
    }

    /**
     * @param quadratics the quadratics to set
     */
    public void setQuadratics(ArrayList<DesignColumn> quadratics) {
        this.quadratics = quadratics;
    }

    /**
     * @return the interactions
     */
    public ArrayList<DesignColumn> getInteractions() {
        return interactions;
    }

    /**
     * @param interactions the interactions to set
     */
    public void setInteractions(ArrayList<DesignColumn> interactions) {
        this.interactions = interactions;
    }

    /**
     * @return the designSize
     */
    public int getDesignSize() {
        return designSize;
    }

    /**
     * @param designSize the designSize to set
     */
    public void setDesignSize(int designSize) {
        this.designSize = designSize;
    }
}
