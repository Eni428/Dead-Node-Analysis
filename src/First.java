
package test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.*;
import java.util.*;

public class First {

    static class LearningSet {
        int T;
        int INP;
        int HID;
        int OUT_T;
    }

    static LearningSet set = new LearningSet();
    static String[] classNames = new String[10];

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("INSERT TEST DIRECTORY:");
        String path = scanner.nextLine();

        System.out.println("INSERT FULL PATH OF WEIGHT1 FILE:");
        String weight1Path = scanner.nextLine();

        System.out.println("INSERT FULL PATH OF WEIGHT2 FILE:");
        String weight2Path = scanner.nextLine();

        System.out.println("INSERT FULL PATH OF BIGGEST FILE:");
        String biggestPath = scanner.nextLine();

        System.out.println("INSERT FULL PATH OF SMALLEST FILE:");
        String smallestPath = scanner.nextLine();

        if (setLearningVariables(path) == 1) {
            System.out.println("TEST SET STORED");
        }

        System.out.println("PLEASE WAIT LOADING...");

        double[] inputLayer =
                firstLayerConnectionsTaker(set.HID,
                        set.INP,
                        weight1Path);

        double[] hiddenLayer =
                secondLayerConnectionsTaker(set.HID,
                        set.OUT_T,
                        weight2Path);

        double[] upper = biggestRangeTaker(set.INP,
                biggestPath);

        double[] lower = smallestRangeTaker(set.INP,
                smallestPath);

        for (int j = 1; j <= set.HID; j++) {
            int idx = (set.INP + 1) * (j - 1);
            inputLayer[idx] = -inputLayer[idx];
        }

        for (int j = 1; j <= set.OUT_T; j++) {
            int idx = (set.HID + 1) * (j - 1);
            hiddenLayer[idx] = -hiddenLayer[idx];
        }

        System.out.println("READY TO CLASSIFY press enter");
        waitForEnter();

        try (
                PrintWriter responseOut =
                        new PrintWriter(new FileWriter("RESPONSE.txt"));

                PrintWriter dimHidOut =
                        new PrintWriter(new FileWriter("DimensionHID.txt"))
        ) {

            responseOut.println("NETWORK RESPONSE:");

            for (int t = 0; t < set.T; t++) {

                String filename = takeAFilename(path, t);

                System.out.println("Processing: " + filename);

                double[] pattern =
                        patternNormaliser(filename,
                                lower,
                                upper,
                                set.INP);

                double[] hiddenActivator =
                        outputNodesProducer(inputLayer,
                                pattern,
                                set.HID,
                                set.INP);

                dimHidOut.print(filename + "\t");

                for (int d = 1; d <= set.HID; d++) {
                    dimHidOut.print(hiddenActivator[d] + "\t");
                }

                dimHidOut.println();

                double[] outputActivator =
                        outputNodesProducer(hiddenLayer,
                                hiddenActivator,
                                set.OUT_T,
                                set.HID);

                for (int j = 1; j <= set.OUT_T; j++) {

                    System.out.printf("%s = %f\n",
                            classNames[j - 1],
                            outputActivator[j]);

                    if (j == 1) {
                        responseOut.println("CHARACTERIZED AS:");
                    }

                    responseOut.printf("%s %f\n",
                            classNames[j - 1],
                            outputActivator[j]);
                }

                System.out.println("\nSOURCE FILE: " + filename);

                responseOut.println("source file: " + filename);

                System.out.println("\nPRESS ENTER to CLASSIFY a NEW INPUT");

                waitForEnter();
            }

            System.out.println("\nCLASSIFICATIONS CORRECTLY SAVED ON FILE");

        } catch (IOException e) {

            System.err.println("I/O error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("hit ENTER to quit");
        waitForEnter();
    }

    private static int setLearningVariables(String path) {

        Path dirPath = Paths.get(path);

        if (!Files.isDirectory(dirPath)) {
            System.err.println("Unable to open directory: " + path);
            System.exit(1);
        }

        List<Path> files;

        try {

            files = Files.list(dirPath)
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();

        } catch (IOException e) {

            System.err.println("Error reading directory: " + e.getMessage());
            System.exit(1);
            return 0;
        }

        if (files.size() < 1) {
            System.out.println("cannot find FIRST FILE of the list");
            System.exit(1);
        }

        Path firstFile = files.get(0);

        System.out.println("FIRST FILE OF THE LIST: "
                + firstFile.getFileName());

        int k1 = 0;

        try (DataInputStream dis =
                     new DataInputStream(
                             new FileInputStream(firstFile.toFile()))) {

            while (dis.available() > 0) {

                byte[] bytes = new byte[8];

                if (dis.read(bytes) != 8)
                    break;

                ByteBuffer.wrap(bytes)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .getDouble();

                k1++;
            }

        } catch (IOException e) {

            System.err.println("cannot open first file");
            System.exit(1);
        }

        int k2 = files.size();

        set.INP = k1;
        set.T = k2;

        System.out.println("INPUT NEURONS = " + k1);
        System.out.println("NUMBER OF PATTERNS TO BE TESTED = " + k2);

        Scanner scanner = new Scanner(System.in);

        System.out.println("INSERT number of classes to discriminate");

        int out = scanner.nextInt();
        scanner.nextLine();

        set.OUT_T = out;

        for (int s = 0; s < out; s++) {

            System.out.println("Please Insert Name of the Class to Identify");

            classNames[s] = scanner.nextLine();

            System.out.println("Class to Identify: " + classNames[s]);
        }

        System.out.println("INSERT NUMBER of HIDDEN NEURONS");

        set.HID = scanner.nextInt();
        scanner.nextLine();

        return 1;
    }

    private static double[] firstLayerConnectionsTaker(
            int p,
            int q,
            String weight1Path) {

        double[] firstLayer = new double[(p + 1) * (q + 1)];

        try (Scanner sc = new Scanner(new File(weight1Path))) {

            for (int j = 1; j <= p; j++) {

                for (int k1 = 0; k1 <= q; k1++) {

                    int idx = k1 + (q + 1) * (j - 1);

                    firstLayer[idx] = sc.nextDouble();
                }
            }

        } catch (Exception e) {

            System.err.println("CANNOT OPEN WEIGHT1 FILE");
            System.exit(1);
        }

        return firstLayer;
    }

    private static double[] secondLayerConnectionsTaker(
            int r,
            int v,
            String weight2Path) {

        double[] secondLayer = new double[(r + 1) * (v + 1)];

        try (Scanner sc = new Scanner(new File(weight2Path))) {

            for (int k1 = 0; k1 <= r; k1++) {

                for (int j = 1; j <= v; j++) {

                    int idx = (j - 1) + k1 * v;

                    secondLayer[idx] = sc.nextDouble();
                }
            }

        } catch (Exception e) {

            System.err.println("CANNOT OPEN WEIGHT2 FILE");
            System.exit(1);
        }

        return secondLayer;
    }

    private static double[] biggestRangeTaker(int r,
                                              String biggestPath) {

        double[] biggest = new double[r + 1];

        try (Scanner sc = new Scanner(new File(biggestPath))) {

            for (int j = 1; j <= r; j++) {
                biggest[j] = sc.nextDouble();
            }

        } catch (Exception e) {

            System.err.println("CANNOT OPEN BIGGEST FILE");
            System.exit(1);
        }

        return biggest;
    }

    private static double[] smallestRangeTaker(int r,
                                               String smallestPath) {

        double[] smallest = new double[r + 1];

        try (Scanner sc = new Scanner(new File(smallestPath))) {

            for (int j = 1; j <= r; j++) {
                smallest[j] = sc.nextDouble();
            }

        } catch (Exception e) {

            System.err.println("CANNOT OPEN SMALLEST FILE");
            System.exit(1);
        }

        return smallest;
    }

    private static double[] patternNormaliser(
            String filename,
            double[] lower,
            double[] upper,
            int x) {

        double[] pattern = new double[x + 1];

        try (FileInputStream fis = new FileInputStream(filename)) {

            byte[] buffer = new byte[8];

            for (int k = 1; k <= x; k++) {

                fis.read(buffer);

                double number =
                        ByteBuffer.wrap(buffer)
                                .order(ByteOrder.LITTLE_ENDIAN)
                                .getDouble();

                double boot =
                        normalise(number,
                                lower[k],
                                upper[k]);

                if (boot < 0.01)
                    pattern[k] = 0.0;
                else if (boot > 0.99)
                    pattern[k] = 1.0;
                else
                    pattern[k] = boot;
            }

        } catch (IOException e) {

            System.err.println("Cannot open file " + filename);
            System.exit(1);
        }

        return pattern;
    }

    private static double normalise(double value,
                                    double min,
                                    double max) {

        if (Double.isNaN((value - min) / (max - min))) {
            return 0.0;
        }

        return (value - min) / (max - min);
    }

    private static String takeAFilename(String currentDir,
                                        int p) {

        Path dirPath = Paths.get(currentDir);

        List<Path> files;

        try {

            files = Files.list(dirPath)
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();

        } catch (IOException e) {

            System.err.println("Unable to open directory");
            System.exit(1);
            return null;
        }

        return files.get(p).toString();
    }

    private static double sigmoid(double input) {

        return 1.0 / (1.0 + Math.exp(-input));
    }

    private static double[] outputNodesProducer(
            double[] layerWeights,
            double[] layerInputs,
            int p,
            int r) {

        double[] y = new double[p + 1];

        for (int j = 1; j <= p; j++) {

            double top = 0.0;

            for (int i = 1; i <= r; i++) {

                int idx = i + (r + 1) * (j - 1);

                top += layerWeights[idx] * layerInputs[i];
            }

            int biasIdx = (r + 1) * (j - 1);

            double thr = top - layerWeights[biasIdx];

            y[j] = sigmoid(thr);
        }

        return y;
    }

    private static void waitForEnter() {

        try {

            System.in.read();

            while (System.in.available() > 0)
                System.in.read();

        } catch (IOException e) {

        }
    
}}

