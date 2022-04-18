package DesignAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author maccalman
 */
public class CrossoverTest {

    public static double[] cross(double[] parent1, double[] parent2, Random randomNumber) {
        double[] child = new double[parent1.length];

        HashMap<Double, HashSet<Double>> parent1AdjacencyMatrix = new HashMap<Double, HashSet<Double>>();
        HashMap<Double, HashSet<Double>> parent2AdjacencyMatrix = new HashMap<Double, HashSet<Double>>();
        HashMap<Double, HashSet<Double>> union = new HashMap<Double, HashSet<Double>>();

        parent1AdjacencyMatrix = createMatrix(parent1);
        parent2AdjacencyMatrix = createMatrix(parent2);


        System.out.println("parent1 column");
        printColumn(parent1);
        System.out.println("parent2 column");
        printColumn(parent2);

        System.out.println("parent1");
        printadjacent(parent1AdjacencyMatrix);
        System.out.println("parent2");
        printadjacent(parent2AdjacencyMatrix);

        System.out.println("union");
        union = createUnion(parent1, parent2, parent1AdjacencyMatrix, parent2AdjacencyMatrix);

        printUnion(union);


        createChild(union, parent1, parent2);

        //null out everything
        parent1AdjacencyMatrix = null;
        parent2AdjacencyMatrix = null;
        union = null;
        return child;
    }

    public static double[] createChild(HashMap<Double, HashSet<Double>> union, double[] parent1, double[] parent2) {
        double[] child = new double[union.size()];
        ArrayList<Double> childList = new ArrayList<Double>();
        ArrayList<Double> notInChildList = new ArrayList<Double>();

        //fill up the notInChildList arrayList
        for (int i = 0; i < union.size(); i++) {
            notInChildList.add(parent1[i]);
        }

        //randomly select a starting point from both parents
        double level;
        Random randomNumber = new Random();
        if (randomNumber.nextDouble() <= .5) {
            level = parent1[0];
        } else {
            level = parent2[0];
        }

        System.out.println("                   first level: " + level);
        //add level to childList
        childList.add(level);
        notInChildList.remove(level);

        ArrayList<Double> smallest = new ArrayList<Double>();
        int minSize = 9999;
        int size = 0;
        double smallestLevel;
        double levelCheck;
        while (childList.size() < parent1.length) {
            //remove level from all neighbor sets
            System.out.println("remove level " + level);
            for (Map.Entry<Double, HashSet<Double>> set : union.entrySet()) {
                set.getValue().remove(level);
            }
            printUnion(union);

            //if the level's HashSet neighbor list is empty then
            if (union.get(level).isEmpty()) {
                //randomly choose another level that is not in the childList
                System.out.println("randomly choose one");
                int randomInteger = 1 + (int) (randomNumber.nextDouble() * (notInChildList.size()));
                level = notInChildList.get(randomInteger - 1);
                //add level to childList
                childList.add(level);
                notInChildList.remove(level);
                System.out.println("randomly picked a notInChildList: " + level);
            } else {
                //loop through and find the smallest size HashSet within current levels neighbors

                for (Map.Entry<Double, HashSet<Double>> set : union.entrySet()) {
                    double iterator = set.getKey();
                    if (union.get(level).contains(set.getKey())) {
                        size = set.getValue().size();
                        if (size < minSize) {
                            minSize = size;
                            //set the level of the smallest size
                            levelCheck = set.getKey();
                        }
                    }
                }

                System.out.println("minSize: " + minSize);

                //add all the smallest size keys to the ArrayList named smallest
                for (Map.Entry<Double, HashSet<Double>> set : union.entrySet()) {
                    if (union.get(level).contains(set.getKey())) {
                        size = set.getValue().size();
                        if (size == minSize) {
                            smallest.add(set.getKey());
                        }
                    }

                }
                //randomly pick one of the smallest levels
                double randomDouble = randomNumber.nextDouble();
                int randomInteger = 1 + (int) (randomDouble * (smallest.size()));
                System.out.println("smallestSize: " + smallest.size());
                System.out.println("randomInteger: " + randomInteger);
                level = smallest.get(randomInteger - 1);

                childList.add(level);
                notInChildList.remove(level);
                System.out.println("                      added level: " + level);

                smallest.clear();
                minSize = 999;

            }



        }


        System.out.println("child");
        //populate childe double array
        for (int i = 0; i < childList.size(); i++) {
            child[i] = childList.get(i);
            System.out.println(child[i]);

        }









        return child;
    }

    /**
     * pass in the column and a position and return an ArralyList of the adjacent
     * values
     * @param parent
     * @param position
     * @return
     */
    public static HashSet<Double> findAdjacents(double[] parent, int position) {
        HashSet<Double> adjacent = new HashSet<Double>();

        if (position == 0) {
            adjacent.add(parent[position + 1]);
            //last position
            adjacent.add(parent[parent.length - 1]);
        } else if (position == parent.length - 1) {
            adjacent.add(parent[position - 1]);
            //first position
            adjacent.add(parent[0]);
        } else {
            adjacent.add(parent[position - 1]);
            adjacent.add(parent[position + 1]);
        }
        return adjacent;
    }

    /**
     * create a hashMap with the key as the level in each position of the parent column and the
     * value the arrayList that stores the levels in the adjacent positions
     * @param parent
     * @return
     */
    public static HashMap<Double, HashSet<Double>> createMatrix(double[] parent) {
        HashMap<Double, HashSet<Double>> adjacencyMatrix = new HashMap<Double, HashSet<Double>>();
        for (int position = 0; position < parent.length; position++) {
            HashSet<Double> adjacent = new HashSet<Double>();
            adjacent = findAdjacents(parent, position);
            adjacencyMatrix.put(parent[position], adjacent);
        }
        return adjacencyMatrix;
    }

    public static HashMap<Double, HashSet<Double>> createUnion(double[] parent1, double[] parent2,
            HashMap<Double, HashSet<Double>> parent1Matrix, HashMap<Double, HashSet<Double>> parent2Matrix) {

        HashMap<Double, HashSet<Double>> unionMatrix = new HashMap<Double, HashSet<Double>>();
        for (int position = 0; position < parent1Matrix.size(); position++) {




            //we will match the union's positions with parent1's positions
            HashSet<Double> union = new HashSet<Double>(parent1Matrix.get(parent1[position]));
            union.addAll(parent2Matrix.get(parent1[position]));



            unionMatrix.put(parent1[position], union);

        }







        return unionMatrix;
    }

    public static void printUnion(HashMap<Double, HashSet<Double>> unionMatrix) {
        for (Map.Entry<Double, HashSet<Double>> set : unionMatrix.entrySet()) {
            System.out.print(set.getKey() + ": ");
            for (Double level : set.getValue()) {
                System.out.print(level + " ");
            }
            System.out.println();
        }
    }

    public static void printadjacent(HashMap<Double, HashSet<Double>> adjacent) {
        for (Map.Entry<Double, HashSet<Double>> set : adjacent.entrySet()) {
            System.out.print(set.getKey() + ": ");
            for (Double level : set.getValue()) {
                System.out.print(level + " ");
            }
            System.out.println();
        }
    }

    public static void printColumn(double[] parent) {
        for (int i = 0; i < parent.length; i++) {
            System.out.println(parent[i]);
        }
    }

}
