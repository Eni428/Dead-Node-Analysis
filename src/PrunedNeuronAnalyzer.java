package test;
import java.io.*;
import java.util.*;

public class PrunedNeuronAnalyzer {

    public static void main(String[] args) throws Exception {

        String file = "C:\\Users\\User\\eclipse-workspace\\NEWTESTNET\\src\\Square VS Rectangle\\HID = 10\\140 images\\WEIGHT2.txt";
        double threshold = 2.0;

        List<Double> weights = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while((line = br.readLine()) != null){
            weights.add(Double.parseDouble(line.trim()));
        }

        br.close();

        System.out.println("PRUNING RESULT\n");
        int hid = 1;

        for(int i = 0; i < 20; i += 2){

            double w1 = weights.get(i);
            double w2 = weights.get(i+1);

            double importance = Math.abs(w1) + Math.abs(w2);

            if(importance < threshold){
                System.out.println("HID" + hid + " -> REMOVE");
            }else{
                System.out.println("HID" + hid + " -> KEEP");
            }

            hid++;
        }}}