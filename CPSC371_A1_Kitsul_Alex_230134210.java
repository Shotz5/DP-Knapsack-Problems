import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

import javax.swing.plaf.basic.BasicBorders.MarginBorder;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public class CPSC371_A1_Kitsul_Alex_230134210 {
    public static Knapsack knapsack;
    public static ArrayList<Item> items = new ArrayList<>();

    public static void main(String args[]) throws IOException {
        // Get the file name to be used in the execution
        Scanner scanner = new Scanner(System.in);
        Scanner file = new Scanner(System.in);
        System.out.print("Enter the type of Knapsack Problem you'd like: 1 = 0-1 Problem, 2 = General Problem, 3 = With Constraints: ");
        boolean shouldLoop = true;
        String problem = "";
        while(!problem.equals("1") && !problem.equals("2") && !problem.equals("3")) {
            problem = scanner.nextLine();
            if (!problem.equals("1") && !problem.equals("2") && !problem.equals("3")) {
                System.out.println("Invalid number, try again: ");
            }
        }
        scanner = new Scanner(System.in);

        if (problem.equals("1")) {
            System.out.println("0-1 Knapsack Problem");
        } else if (problem.equals("2")) {
            System.out.println("General Knapsack Problem");
        } else if (problem.equals("3")) {
            System.out.println("0-1 Knapsack Problem with Constraints");
        }

        System.out.print("Enter name of file used for input: ");
        shouldLoop = true;
        String fileName = "";
        while (shouldLoop) {
            fileName = scanner.nextLine();
            try {
                file = new Scanner(new File(fileName));
                shouldLoop = false;
                scanner.close();
            } catch (Exception e) {
                System.out.print("File not found! Try again: ");
            }
        }

        System.out.println("Processing...");

        // Make our knapsack with the limited capacity specified
        knapsack = new Knapsack(Integer.parseInt(file.nextLine()));
        // Used to make sure IDs increment by 1, not totally necessary but easy to make sure that there are no duplicate IDs
        int previousId = 0;

        // Iterate through the file and get all the data
        while(file.hasNextLine()) {
            // First item is the ID, with some logic to make sure it increments by 1
            int id = file.nextInt();
            if (previousId + 1 != id) {
                System.out.println("IDs must increment by 1 and the first Id must be > 0, please check your input file and try again.");
                return;
            }
            previousId = id;

            // Next are weight and price, no need to check any logic
            int weight = file.nextInt();
            int price = file.nextInt();

            // Create the "item" and add it to our list of items
            Item newItem = new Item(id, weight, price);
            items.add(newItem);
        }

        // If invalid input, return
        if (items.size() <= 0 || knapsack.getCapacity() <= 0) {
            System.out.println("Number of items or capacity is <= 0, please make sure proper data is given!");
            return;
        }

        if (problem.equals("1")) {
            zeroOneKnapsackProblem();
        } else if (problem.equals("2")) {
            unboundedKnapsackProblem();
        } else if (problem.equals("3")) {
            zeroOneWithConstaint();
        }

        scanner.close();
        file.close();
    }    

    // BEGIN 0-1 Knapsack Problem
    public static void zeroOneKnapsackProblem() throws IOException {
        // Get the amount of items and capacity and store it in its own value for ease
        int numItems = items.size();
        int capacity = knapsack.getCapacity();

        // Our 2D matric which represents the Dynamic Programming table
        int matrice[][] = new int[numItems + 1][capacity + 1];

        // Set all the values in the left column to 0
        for (int i = 0; i <= capacity; i++) {
            matrice[0][i] = 0;
        }

        // Iterate through each item and calculate the column value using the max value
        for (int i = 1; i <= numItems; i++) {
            for (int j = 0; j <= capacity; j++) {
                if (items.get(i - 1).getWeight() > j) {
                    matrice[i][j] = matrice[i - 1][j];
                } else {
                    matrice[i][j] = Math.max(matrice[i - 1][j], (matrice[i - 1][j - items.get(i - 1).getWeight()] + items.get(i - 1).getPrice()));
                }
            }
        }

        // This value is the best value in the bottom right of the table
        int result = matrice[numItems][capacity];

        // Used for the backtracking to find the best combination of items
        int tempResult = result;
        int tempCapacity = capacity;

        // Logic to find best items
        for (int i = numItems - 1; i >= 0 && tempResult > 0; i--) {
            if (result != matrice[i][capacity]) {
                Item knapsackItem = items.get(i);
                knapsack.addItem(knapsackItem);

                tempResult -= knapsackItem.getPrice();
                tempCapacity -= knapsackItem.getWeight();
            }
        }

        // Output to Dynamic file and console
        outputToDynamic(result, matrice);
    }

    public static void unboundedKnapsackProblem() throws IOException {
        // Get the amount of items and capacity and store it in its own value for ease
        int numItems = items.size();
        int capacity = knapsack.getCapacity();

        // 2D Array with one row is made so I don't have to recode the DynamicTable generator for linear arrays
        int array[][] = new int[1][capacity + 1];

        // Needs to add empty sets for each column to be populated by the for loop later
        ArrayList<ArrayList<Item>> arrayList = new ArrayList<>();
        
        for (int i = 0; i <= capacity; i++) {
            ArrayList<Item> dummyItem = new ArrayList<>();
            arrayList.add(dummyItem);
        }

        int lastValue = 0;
        // Logic that writes the best possible value to the array
        for (int i = 0; i <= capacity; i++) {
            for (int j = 0; j < numItems; j++) {
                if(items.get(j).getWeight() <= i) {
                    array[0][i] = Math.max(array[0][i], (array[0][i - items.get(j).getWeight()] + items.get(j).getPrice()));
                    // Build lists of best possible item combinations by capacity, if the maximum number is not repeated
                    if (array[0][i] > lastValue) {
                        ArrayList<Item> selectedItem = arrayList.get(i);
                        selectedItem.clear();
                        selectedItem.add(items.get(j));

                        for (int k = 0; k < arrayList.get(i - items.get(j).getWeight()).size(); k++) {
                            selectedItem.add(arrayList.get(i - items.get(j).getWeight()).get(k));
                        }
                    }
                    lastValue = array[0][i];
                }
            }
        }

        // Add the selected items for the last column in the Dynamic Table to the knapsack
        for (int i = 0; i < arrayList.get(arrayList.size() - 1).size(); i++) {
            knapsack.addItem(arrayList.get(arrayList.size() - 1).get(i));
        }

        // Store result in a variable to be passed
        int result = array[0][capacity];

        // Output to Dynamic file and console
        outputToDynamic(result, array);
    }

    public static void zeroOneWithConstaint() throws IOException {
        // Get the amount of items and capacity and store it in its own value for ease
        int numItems = items.size();
        int capacity = knapsack.getCapacity();

        // Our 2D matrix which represents the Dynamic Programming table
        int matrix[][] = new int[numItems + 1][capacity + 1];

        // Set all the values in the left column to 0
        for (int i = 0; i <= capacity; i++) {
            matrix[0][i] = 0;
        }

        // Iterate through each item and calculate the column value
        for (int i = 1; i <= numItems; i++) {
            for (int j = 0; j <= capacity; j++) {
                if (items.get(i - 1).getWeight() > j) {
                    matrix[i][j] = matrix[i - 1][j];
                } else {
                    matrix[i][j] = Math.max(matrix[i - 1][j], (matrix[i - 1][j - items.get(i - 1).getWeight()] + items.get(i - 1).getPrice()));
                }
            }
        }

        // Set values needed before they are modified by the result check
        int totalWeight = 0;
        int totalPrice = 0;
        int result = matrix[numItems][capacity];

        // The result check which makes sure that the weight is even and the price is odd
        while (totalWeight % 2 == 0 || totalPrice % 2 == 1 || result == 0) {
            // If this isn't the first run through, move left by one column and reset knapsack
            if (totalWeight != 0) {
                numItems--;
                knapsack.clear();
            }

            // Set result to the new value
            result = matrix[numItems][capacity];

            // Set modifiable values for the logic
            int tempResult = result;
            int tempCapacity = capacity;

            // Logic to find best items
            ArrayList<Integer> itemlist = new ArrayList<>();
            for (int i = numItems; i > 0 && result > 0; i--) {
                if (result != matrix[i - 1][capacity]) {
                    Item knapsackItem = items.get(i - 1);
                    knapsack.addItem(knapsackItem);

                    tempResult -= knapsackItem.getPrice();
                    tempCapacity -= knapsackItem.getWeight();
                }
            }

            // Sum the weight of the newly filled knapsack
            for (int i = 0; i < knapsack.getSize(); i++) {
                totalWeight += knapsack.getItem(i).getWeight();
            }

            // Sum the price of the newly filled knapsack
            for (int i = 0; i < knapsack.getSize(); i++) {
                totalPrice += knapsack.getItem(i).getPrice();
            }
        }

        outputToDynamic(result, matrix);
    }

    // Outputs the matrix to DynamicTable.txt and the results to console.
    public static void outputToDynamic(int result, int matrix[][]) throws IOException {
        System.out.println("Done!");

        System.out.println("Result:");
        System.out.println("================================================");
        System.out.println("Total Value: " + result);

        if (result == 0) {
            System.out.println("No valid result found, no IDs able to be printed.");
        } else {
            System.out.print("Item ID List: ");
            for (int i = 0; i < knapsack.getSize(); i++) {
                System.out.print(knapsack.getItem(i).getItemID() + " ");
            }
            System.out.println();
        }
        System.out.println("================================================");

        // Output to DynamicTable.txt
        File outputFile = new File("DynamicTable.txt");
        if (outputFile.createNewFile()) {
            System.out.println("Outputting DynamicTable.txt...");
        } else {
            System.out.println("Outputting DynamicTable.txt... (overwriting)");
        }

        PrintStream tableOutput = new PrintStream(new FileOutputStream(new File("DynamicTable.txt")));

        // Print the table to DynamicTable.txt
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                tableOutput.printf("%4d ", matrix[i][j]);
            }
            tableOutput.println();
        }

        System.out.println("Done!");
        System.out.println("End of Processing.");
    }
}