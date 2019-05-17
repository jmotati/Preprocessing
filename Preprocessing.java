import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Preprocessing Project
 * <p>
 * This program is used for preprocessing data.
 *
 * @author Jitesh Motati, Lab Section 17
 * @version 03/1/19
 *
 * <p> Spring 2019 -- Preprocessing </p>
 **/

public class Preprocessing {

    private static String fileName = "survey_data.csv";
    // DO NOT REMOVE THESE LINES. THEY ARE USED FOR TESTING THE PROJECT.
    private static Double[][] cleanedData;
    private static Double[][] transformedData;
    private static Double[][] reducedData;
    private static int[][] preprocessedData;

    // THE NEXT TWO DECLARATIONS ARE A 2D ARRAY CONTAINING EVERY ROW AND COLUMN (EXCEPT THE FIRST COLUMN - DEGREE -
    // HELD WITHIN THE 1D ARRAY degreeColumn.
    private static Double[][] dataSet;
    private static String[] degreeColumn;

    public static void main(String[] args) {
        // DO NOT CHANGE THIS CALL OR THE DATA SET WILL NOT BE LOADED!
        loadData();

        // Call printData() whenever you want the data set in its current state to be printed.
        //printData();
        /* TASK 1 - DATA CLEANING
         * PUT YOUR CODE FOR TASK 1 BELOW. */
        //Initialize sum, counter, and avg
        Double sum = 0.0;
        Double counter = 0.0;
        Double average;
        //dataSet[j].length is the number of columns, dataSet.length is number of rows
        for (int col = 0; col < dataSet[0].length; col++) {
            for (int row = 0; row < dataSet.length; row++) {

                if (dataSet[row][col] != null) {
                    counter++;
                    sum += dataSet[row][col];
                }
            }
            average = sum / counter;
            for (int row = 0; row < dataSet.length; row++) {
                if (dataSet[row][col] == null) {
                    dataSet[row][col] = average;
                }
            }
            //reset counter
            sum = 0.0;
            counter = 0.0;
            //System.out.println("Col: "+col+" average: "+average);
        }
        //printData();
        /* END OF CODE FOR TASK 1 */

        // DO NOT REMOVE THIS METHOD CALL! IT IS USED FOR TESTING YOUR RESULTS FROM TASK 1.
        storeCleanedData(dataSet);

        /* TASK 2 - DATA TRANSFORMATION
         * PUT YOUR CODE FOR TASK 2 BELOW. */
        //find max value
        Double maxValue = null;
        Double minValue = null;
        for (int col = 0; col < dataSet[0].length; col++) {
            for (int row = 0; row < dataSet.length; row++) {

                if (maxValue == null) {
                    maxValue = dataSet[row][col];
                } else if (maxValue < dataSet[row][col]) {
                    maxValue = dataSet[row][col];
                }

                if (minValue == null) {
                    minValue = dataSet[row][col];
                } else if (minValue > dataSet[row][col]) {
                    minValue = dataSet[row][col];
                }

            }

            for (int row = 0; row < dataSet.length; row++) {
                dataSet[row][col] = (dataSet[row][col] - minValue) / (maxValue - minValue);
            }

            //System.out.println("Col: "+col+" min: "+minValue+" max= "+maxValue);
            maxValue = null;
            minValue = null;
        }
        // printData();



        /* END OF CODE FOR TASK 2 */
        // DO NOT REMOVE THIS METHOD CALL! IT IS USED FOR TESTING RESULTS FROM TASK 2.
        storeTransformedData(dataSet);

        /* TASK 3 - DATA REDUCTION
         * PUT YOUR CODE FOR TASK 3 BELOW. */
        System.out.print("Enter the number of bins: ");

        Scanner input = new Scanner(System.in);
        int b = input.nextInt();
        double upperRangeOfFirstBin = 1.0 / b;
        for (int col = 0; col < dataSet[0].length; col++) {
            for (int row = 0; row < dataSet.length; row++) {
                for (int k = 0; k < b; k++) {
                    if (dataSet[row][col] <= upperRangeOfFirstBin * (k + 1)) {
                        dataSet[row][col] = (double) k;
                        break;
                    }
                }
            }
        }


        //printData();
        /* END OF CODE FOR TASK 3 */
        // DO NOT REMOVE THIS METHOD CALL! IT IS USED FOR TESTING RESULTS FROM TASK 3.
        storeReducedData(dataSet);

        /* TASK 4 - LABEL ENCODING
         * PUT YOUR CODE FOR TASK 4 BELOW. */
        int[] degreeIdColumn = new int[degreeColumn.length];
        int totalAcsii = 0;
        for (int i = 0; i < degreeColumn.length; i++) {
            degreeColumn[i] = degreeColumn[i].toLowerCase();
            totalAcsii = 0;
            for (int j = 0; j < degreeColumn[i].length(); j++) {
                totalAcsii += degreeColumn[i].charAt(j);
            }
            degreeIdColumn[i] = totalAcsii;
        }
        /* END OF CODE FOR TASK 4 */

        /* TASK 5 - PUTTING IT ALL TOGETHER
         * CODE FOR TASK 5 BELOW. */
        int[][] dataSetWithDegree = new int[dataSet.length][dataSet[0].length + 1];
        for (int i = 0; i < degreeIdColumn.length; i++) {
            dataSetWithDegree[i][0] = degreeIdColumn[i];
        }

        for (int i = 0; i < dataSet.length; i++) {
            for (int j = 0; j < dataSet[i].length; j++) {
                dataSetWithDegree[i][j + 1] = dataSet[i][j].intValue();
            }

        }
        doMachineLearning(dataSetWithDegree);
        /* END OF CODE FOR TASK 5 */
    }

    /**
     * Method printData Pretty-prints table made up of values from dataSet and
     * degreeColumn.
     * <p>
     * Usage: Simply call printData() in your code whenever you would like the
     * table to be printed.
     * <p>
     */
    private static void printData() {
        String[] headers = {"Age", "CS180_Grade", "GPA", "Credit_Hours", "Months_Until_Employment"};
        System.out.printf("| %-40s |", "Degree");
        for (String header : headers) {
            System.out.printf(" %-23s |", header);
        }
        System.out.println();
        for (int i = 0; i < 174; i++) {
            System.out.print("_");
        }
        System.out.println();
        for (int i = 0; i < dataSet.length; i++) {
            System.out.printf("| %-40s |", degreeColumn[i]);
            for (int j = 0; j < dataSet[i].length; j++) {
                System.out.printf(" %-23s |", dataSet[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * Method loadData Loads the file fileName from disk
     */
    private static void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            ArrayList<Double[]> data = new ArrayList<>(); // ArrayList used for data sets of different length.
            ArrayList<String> stringColumn = new ArrayList<>();

            String line = br.readLine(); // Ignore Headers
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",", -1);
                stringColumn.add(row[0]);
                Double[] restOfRow = new Double[row.length - 1];
                for (int i = 1; i < row.length; i++) {
                    try {
                        restOfRow[i - 1] = Double.parseDouble(row[i]);
                    } catch (NumberFormatException e) { // Missing Value
                        restOfRow[i - 1] = null;
                    }
                }
                data.add(restOfRow);
            }
            dataSet = new Double[data.size()][data.get(0).length];
            for (int i = 0; i < data.size(); i++) {
                dataSet[i] = data.get(i);
            }
            degreeColumn = new String[stringColumn.size()];
            for (int i = 0; i < stringColumn.size(); i++) {
                degreeColumn[i] = stringColumn.get(i);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error Loading File. Ensure survey_data.csv "
                    + "is located within the same folder as this file.");
        } catch (IOException e) {
            System.out.println("Error While Parsing Data From CSV:");
            e.printStackTrace();
        }
    }

    /**
     *
     * @param preprocessedDataSet A 2D array of preprocessed data.
     */
    private static void doMachineLearning(int[][] preprocessedDataSet) {
        System.out.println("The Machine is Learning...");
        System.out.println();
        String[] headers = {"Degree", "Age", "CS180_Grade", "GPA", "Credit_Hours", "Months_Until_Employment"};
        System.out.print("|");
        for (String header : headers) {
            System.out.printf(" %-23s |", header);
        }
        System.out.println();
        for (int i = 0; i < 157; i++) {
            System.out.print("_");
        }
        System.out.println();
        for (int[] row : preprocessedDataSet) {
            for (int value : row) {
                System.out.printf(" %-23s |", value);
            }
            System.out.println();
        }
        System.out.println("The Machine has Learned!");
        Preprocessing.preprocessedData = preprocessedDataSet;
    }

    /**
     * Method storeCleanedData Stores the provided data set in the 2D array
     * cleanedData. Used for testing.
     *
     * @param cleanedDataSet The data set to store.
     */
    private static void storeCleanedData(Double[][] cleanedDataSet) {
        cleanedData = new Double[cleanedDataSet.length][cleanedDataSet[0].length];
        for (int i = 0; i < cleanedDataSet.length; i++) {
            if (cleanedDataSet[i].length >= 0) {
                System.arraycopy(cleanedDataSet[i], 0, cleanedData[i], 0,
                        cleanedDataSet[i].length);
            }
        }
    }

    /**
     * Method storeReducedData Stores the provided data set in the 2D array
     * reducedData. Used for testing.
     *
     * @param reducedDataSet The data set to store.
     */
    private static void storeReducedData(Double[][] reducedDataSet) {
        reducedData = new Double[reducedDataSet.length][reducedDataSet[0].length];
        for (int i = 0; i < reducedDataSet.length; i++) {
            if (reducedDataSet[i].length >= 0) {
                System.arraycopy(reducedDataSet[i], 0, reducedData[i], 0,
                        reducedDataSet[i].length);
            }
        }
    }

    /**
     * Method storeTransformedData Stores the provided data set in the 2D array
     * transformedData. Used for testing.
     *
     * @param transformedDataSet The data set to store.
     */
    private static void storeTransformedData(Double[][] transformedDataSet) {
        transformedData = new Double[transformedDataSet.length][transformedDataSet[0].length];
        for (int i = 0; i < transformedDataSet.length; i++) {
            if (transformedDataSet[i].length >= 0) {
                System.arraycopy(transformedDataSet[i], 0, transformedData[i], 0,
                        transformedDataSet[i].length);
            }
        }
    }
}