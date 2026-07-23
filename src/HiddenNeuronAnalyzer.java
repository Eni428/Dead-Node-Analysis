package test;

import java.io.*;
import java.util.*;

/*
 ============================================================
 HiddenNeuronAnalyzer
 ------------------------------------------------------------
 This program analyzes the behavior of hidden neurons in a
 neural network trained to classify two shapes:

    • Circle
    • Rectangle

 The goal of the program is to:

 1. Analyze neuron activations for both classes
 2. Measure how well each neuron separates the two classes
 3. Rank neurons by importance
 4. Identify neurons that can be removed (pruning)
 5. Determine the optimal number of neurons using accuracy tests

 Input data:
    - 30 Circle samples
    - 30 Rectangle samples
    - 10 hidden neurons

 Total data points per neuron = 60 activations

 Output:
    - Console analysis
    - File "NeuronAnalysisResults.txt"

 ============================================================
*/

public class HiddenNeuronAnalyzer {

  
    static class NeuronData {

        int id;                      // Neuron identifier (HID01, HID02, etc.)

        double[] circleValues;       // Activation values for 30 circle inputs

        double[] rectangleValues;    // Activation values for 30 rectangle inputs

        double separability;         // Measure of how well neuron separates classes


        // Creates storage for neuron activation values
        
        NeuronData(int id) {
            this.id = id;

            // allocate arrays for 30 samples per class
            this.circleValues = new double[30];
            this.rectangleValues = new double[30];
        }
    }


    /*
     ============================================================
     MAIN METHOD
     ============================================================

     Program execution begins here.

     Steps performed:

     1. Print current working directory
     2. Load neuron activation data
     3. Perform pruning analysis
     4. Determine optimal architecture
     5. Save results to file

    */
    public static void main(String[] args) {

        try {

            // Print program execution directory
            String currentDir = System.getProperty("user.dir");
            System.out.println("Current directory: " + currentDir);


            // Try to read neuron activations from text file
            List<NeuronData> neurons = readFromDimensionHID();


            /*
             If file data was not loaded correctly,
             fallback to hardcoded Excel data.
            */
            if (neurons.isEmpty() || neurons.get(0).circleValues[0] == 0) {

                System.out.println("No data found in DimensionHID.txt. Check if file exists.");
                System.out.println("Using data from Excel manually...");

                neurons = readFromExcelData();
            }


            // Analyze neuron importance and pruning possibilities
            performPruningAnalysis(neurons);


            // Determine optimal number of neurons
            findOptimalArchitecture(neurons);


            // Save analysis results to output file
            writeResultsToFile(neurons);

        }
        catch (Exception e) {

            // Print any unexpected errors
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }



  
    private static List<NeuronData> readFromDimensionHID() throws IOException {

        // Create list of neurons
        List<NeuronData> neurons = new ArrayList<>();

        // Create 10 neurons
        for (int i = 1; i <= 10; i++) {
            neurons.add(new NeuronData(i));
        }


        /*
         Absolute path to the activation file
        */
        File dimFile = new File(
                "C:\\Users\\User\\eclipse-workspace\\NEWTESTNET\\Circle VS Rectangle\\DimensionHID.txt"
        );


        // If file does not exist, return empty neuron list
        if (!dimFile.exists()) {
            System.out.println("DimensionHID.txt does not exist in the specified path!");
            return neurons;
        }


        System.out.println("Reading from: " + dimFile.getAbsolutePath());


        /*
         Read file line by line
        */
        try (BufferedReader br = new BufferedReader(new FileReader(dimFile))) {

            String line;

            int circleIndex = 0;
            int rectIndex = 0;


            /*
             Continue reading until we have 30 samples for each class
            */
            while ((line = br.readLine()) != null &&
                    (circleIndex < 30 || rectIndex < 30)) {

                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) continue;


                /*
                 Split line into parts

                 
                 filename 0.34 0.91 ... 0.76
                */
                String[] parts = line.split("\\s+");


                // Ensure line contains enough values
                if (parts.length < 11) continue;


                String filename = parts[0].toLowerCase();

                try {

                    // Temporary array for neuron activations
                    double[] values = new double[10];


                    // Parse neuron activation values
                    for (int i = 0; i < 10; i++) {
                        values[i] = Double.parseDouble(parts[i+1]);
                    }


                    /*
                     Determine whether sample belongs to
                     Circle or Rectangle
                    */
                    if (filename.contains("circle") ||
                        (filename.matches(".*\\d+.*") && circleIndex < 30)) {

                        // Store circle activations
                        for (int i = 0; i < 10; i++) {
                            neurons.get(i).circleValues[circleIndex] = values[i];
                        }

                        circleIndex++;

                    } else {

                        // Store rectangle activations
                        for (int i = 0; i < 10; i++) {
                            neurons.get(i).rectangleValues[rectIndex] = values[i];
                        }

                        rectIndex++;
                    }

                }
                catch (NumberFormatException e) {
                    // Ignore lines that contain invalid numbers
                }
            }

            System.out.println(
                    "Read " + circleIndex +
                    " Circle samples and " +
                    rectIndex +
                    " Rectangle samples from DimensionHID.txt"
            );
        }

        return neurons;
    }



    /*
     ============================================================
     LOAD DATA FROM EXCEL (HARDCODED)
     ============================================================

     If the text file cannot be read, this function loads
     activation values manually extracted from Excel.

     The data contains:

         10 neurons
         30 circle samples
         30 rectangle samples
    */
    private static List<NeuronData> readFromExcelData() {

        List<NeuronData> neurons = new ArrayList<>();

        // Create neurons
        for (int i = 1; i <= 10; i++) {
            neurons.add(new NeuronData(i));
        }


        /*
         Hardcoded activation values extracted from Excel
        */
        double[][] circleValues = {
            /* values omitted here for readability in explanation */
        };

        double[][] rectangleValues = {
            /* values omitted here for readability */
        };


        /*
         Copy circle values into neuron objects
        */
        for (int neuronIdx = 0; neuronIdx < 10; neuronIdx++) {

            for (int sampleIdx = 0; sampleIdx < 30; sampleIdx++) {

                neurons.get(neuronIdx).circleValues[sampleIdx] =
                        circleValues[neuronIdx][sampleIdx];
            }
        }


        /*
         Copy rectangle values into neuron objects
        */
        for (int neuronIdx = 0; neuronIdx < 10; neuronIdx++) {

            for (int sampleIdx = 0; sampleIdx < 30; sampleIdx++) {

                neurons.get(neuronIdx).rectangleValues[sampleIdx] =
                        rectangleValues[neuronIdx][sampleIdx];
            }
        }


        System.out.println("Set 30 Circle samples and 30 Rectangle samples from Excel");

        return neurons;
    }



    /*
     ============================================================
     PRUNING ANALYSIS
     ============================================================

     Determines which neurons are useful for classification.

     For each neuron we calculate:

        • Mean activation for Circle
        • Mean activation for Rectangle
        • Standard deviation for both classes
        • Separability score

     Separability formula:

        |meanCircle - meanRectangle|
        --------------------------------
        stdCircle + stdRectangle

     Higher value → neuron better distinguishes classes
    */
    private static void performPruningAnalysis(List<NeuronData> neurons) {

        System.out.println("\n=========================================");
        System.out.println("=== PRUNING ANALYSIS ===");
        System.out.println("=========================================\n");


        // Compute statistics for each neuron
        for (NeuronData neuron : neurons) {

            double circleMean = calculateMean(neuron.circleValues);
            double circleStd = calculateStd(neuron.circleValues, circleMean);

            double rectMean = calculateMean(neuron.rectangleValues);
            double rectStd = calculateStd(neuron.rectangleValues, rectMean);


            // Compute separability score the mean difference divided by the sum of standard deviations
            double meanDiff = Math.abs(circleMean - rectMean);

            double separability =
                    meanDiff / (circleStd + rectStd + 0.0001);

            neuron.separability = separability;


            // Print neuron statistics
            System.out.printf("Neuron HID%02d:\n", neuron.id);
            System.out.printf("  Circle:    mean = %.4f, std = %.4f\n", circleMean, circleStd);
            System.out.printf("  Rectangle: mean = %.4f, std = %.4f\n", rectMean, rectStd);
            System.out.printf("  Mean difference = %.4f\n", meanDiff);
            System.out.printf("  Separability = %.4f\n", separability);
            System.out.println();
        }


        /*
         Rank neurons by importance (highest separability first)
        */
        neurons.sort((a, b) -> Double.compare(b.separability, a.separability));


        System.out.println("\n=== NEURONS RANKED BY IMPORTANCE ===");

        for (int i = 0; i < neurons.size(); i++) {

            NeuronData n = neurons.get(i);

            System.out.printf(
                    "%2d. HID%02d (Separability = %.4f)\n",
                    i+1, n.id, n.separability
            );
        }


        /*
         Determine neurons that could be pruned
        */
        System.out.println("\n=== RECOMMENDED NEURONS FOR PRUNING ===");

        double sumSep = 0;

        for (NeuronData n : neurons)
            sumSep += n.separability;

        double avgSep = sumSep / neurons.size();

        System.out.printf("Average separability: %.4f\n", avgSep);


        // Print pruning recommendation
        for (NeuronData neuron : neurons) {

            if (neuron.separability < avgSep * 0.5) {

                System.out.printf(
                        "❌ HID%02d - RECOMMENDED FOR PRUNING\n",
                        neuron.id
                );

            }
            else if (neuron.separability < avgSep * 0.7) {

                System.out.printf(
                        "⚠️ HID%02d - MAY BE PRUNED\n",
                        neuron.id
                );

            }
            else {

                System.out.printf(
                        "✅ HID%02d - KEEP\n",
                        neuron.id
                );
            }
        }
    }



    /*
     ============================================================
     ARCHITECTURE OPTIMIZATION
     ============================================================

     This function determines how many neurons are actually needed.

     Steps:

        1. Test accuracy using 1..10 neurons
        2. Compute improvement between architectures
        3. Apply elbow method
    */
    private static void findOptimalArchitecture(List<NeuronData> neurons) {

        System.out.println("\n=========================================");
        System.out.println("=== OPTIMAL ARCHITECTURE ANALYSIS ===");
        System.out.println("=========================================\n");

        double[] accuracies = new double[11];


        /*
         Test performance with different neuron counts
        */
        for (int numNeurons = 1; numNeurons <= 10; numNeurons++) {

            double accuracy =
                    evaluateArchitecture(neurons, numNeurons);

            accuracies[numNeurons] = accuracy;

            System.out.printf(
                    "Neurons: %2d → Accuracy: %5.2f%%\n",
                    numNeurons,
                    accuracy * 100
            );
        }


        /*
         Compute improvement between architectures
        */
        double[] improvements = new double[11];
        double maxImprovement = 0;


        for (int i = 2; i <= 10; i++) {

            improvements[i] =
                    accuracies[i] - accuracies[i-1];

            if (improvements[i] > maxImprovement)
                maxImprovement = improvements[i];
        }


        /*
         Elbow threshold
        */
        double threshold = maxImprovement * 0.2;

        int optimalPoint = 10;


        for (int i = 2; i <= 10; i++) {

            if (improvements[i] < threshold) {

                optimalPoint = i - 1;
                break;
            }
        }


        System.out.println("\n🎯 OPTIMAL ARCHITECTURE: "
                + optimalPoint + " NEURONS");
    }



    /*
     ============================================================
     ARCHITECTURE EVALUATION
     ============================================================

     Tests classification performance using a subset
     of the most important neurons.

     Steps:

        1. Select top N neurons
        2. Compute average activation
        3. Compare class scores
        4. Measure accuracy
    */
    private static double evaluateArchitecture(
            List<NeuronData> neurons,
            int numNeurons) {

        List<NeuronData> sorted = new ArrayList<>(neurons);

        sorted.sort((a,b)->Double.compare(b.separability,a.separability));

        List<NeuronData> selected =
                sorted.subList(0, Math.min(numNeurons, sorted.size()));


        int correct = 0;
        int total = 60;


        for (int i = 0; i < 30; i++) {

            double circleScore = 0;
            double rectScore = 0;

            for (NeuronData n : selected) {

                circleScore += n.circleValues[i];
                rectScore += n.rectangleValues[i];
            }

            circleScore /= selected.size();
            rectScore /= selected.size();

            if (circleScore > rectScore)
                correct++;
        }


        for (int i = 0; i < 30; i++) {

            double circleScore = 0;
            double rectScore = 0;

            for (NeuronData n : selected) {

                circleScore += n.circleValues[i];
                rectScore += n.rectangleValues[i];
            }

            circleScore /= selected.size();
            rectScore /= selected.size();

            if (rectScore > circleScore)
                correct++;
        }

        return (double) correct / total;
    }



    /*
     ============================================================
     SAVE RESULTS TO FILE
     ============================================================

     Saves neuron statistics and rankings to

        NeuronAnalysisResults.txt
    */
    private static void writeResultsToFile(
            List<NeuronData> neurons) throws IOException {

        try (PrintWriter writer =
                     new PrintWriter(
                             new FileWriter(
                                     "NeuronAnalysisResults.txt"))) {

            writer.println("=== NEURON ANALYSIS RESULTS ===\n");

            for (NeuronData n : neurons) {

                writer.printf(
                        "HID%02d separability: %.4f\n",
                        n.id,
                        n.separability
                );
            }
        }

        System.out.println("\nResults saved to NeuronAnalysisResults.txt");
    }



    /*
     ------------------------------------------------------------
     Compute mean value of array
     ------------------------------------------------------------
    */
    private static double calculateMean(double[] values) {

        double sum = 0;

        for (double v : values)
            sum += v;

        return sum / values.length;
    }



    /*
     ------------------------------------------------------------
     Compute standard deviation
     ------------------------------------------------------------
    */
    private static double calculateStd(
            double[] values,
            double mean) {

        double sum = 0;

        for (double v : values)
            sum += Math.pow(v - mean, 2);

        return Math.sqrt(sum / values.length);
    }

}