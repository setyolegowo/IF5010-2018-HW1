package id.ac.itb.if5010.hw1;

import id.ac.itb.if5010.hw1.components.*;
import java.util.*;

public class Main {

    // attribute
    private static boolean warmStart = false;
    private static int btbSize = 4;
    private static String historyFilename = "history.txt";
    private static boolean verbose = false;
    private static int entry, hit, miss, correct, incorrect, overwrite, value;
    private static FileHandler fileHandler;
    private static BTBQueue btbQueue;
    private static boolean[] predictionTable;
    private static int globalHistory = 0;

    public static void main(String[] args) {

        // read configuration
        parseConfiguration(args);

        // init file handler, btb queue, and predictionTable
        fileHandler = new FileHandler(historyFilename);
        btbQueue = new BTBQueue(btbSize);
        predictionTable = new boolean[4];

        // run algorithm
        int[] result = runPrediction();

        if (warmStart) {
            fileHandler.reset();
            btbQueue.resetStatistic();
            result = runPrediction();
        }

        System.out.println("\nSummary : ");
        System.out.println(String.format("Total entry : %d", result[0]));
        System.out.println(String.format("Total BTB hit : %d (%.2f%%)", btbQueue.getTotalHit(), btbQueue.getHitRate() * 100));
        System.out.println(String.format("Total BTB miss : %d (%.2f%%)", btbQueue.getTotalMiss(), btbQueue.getMissRate() * 100));
        System.out.println(String.format("Total correct prediction : %d (%.2f%%)", result[1], result[1] * 100d / result[0]));
        System.out.println(String.format("Total incorrect prediction : %d (%.2f%%)", result[2], result[2] * 100d / result[0]));
        System.out.println(String.format("Total BTB overwrite : %d\n\n", btbQueue.getTotalOverwrite()));
    }

    private static void parseConfiguration(String[] args) {
        int count = 0;

        while (count < args.length) {
            if (args[count].equals("-btbsize")) {
                count++;
                btbSize = Integer.parseInt(args[count]);
            }
            else if (args[count].equals("-warmstart")) {
                warmStart = true;
            }
            else if (args[count].equals("-historyfile")) {
                count++;
                historyFilename = args[count];
            }
            else if (args[count].equals("-verbose")) {
                verbose = true;
            }

            count++;
        }

        System.out.println(String.format("Starting program with %d BTB size and %s", btbSize, warmStart ? "warm start" : "cold start"));
    }

    private static int[] runPrediction() {

        Instruction instruction;
        Map<String, Integer> dictionary;

        entry = correct = incorrect = 0;
        dictionary = new HashMap<>();

        while ((instruction = fileHandler.next()) != null) {

            entry++;

            if (dictionary.containsKey(instruction.getInstruction())) {
                value = dictionary.get(instruction.getInstruction());
                dictionary.put(instruction.getInstruction(), value + 1);
            }
            else {
                dictionary.put(instruction.getInstruction(), 1);
            }

            // get prediction result
            boolean prediction = predictionTable[globalHistory];
            boolean actual = instruction.getIsTaken();
            boolean hit = btbQueue.isHit(instruction.getInstruction());
            boolean isCorrect = prediction == actual;

            if (verbose) {
                System.out.print(instruction.getInstruction());
                System.out.print(prediction ? "\tT" : "\tNT");
                System.out.print(actual ? "\tT" : "\tNT");
                System.out.print(hit ? "\tHit" : "\tMiss");
                System.out.println(isCorrect ? "\tCorrect" : "\tIncorrect");
            }

            if (isCorrect) {
                correct++;
            }
            else {
                incorrect++;
            }

            if (hit) {
                if (!actual) {
                    btbQueue.delete(instruction.getInstruction());
                }
            }
            else {
                if (actual) {
                    btbQueue.pushToBTB(instruction.getInstruction(), instruction.getBranchTarget());
                }
            }

            predictionTable[globalHistory] = instruction.getIsTaken();
            globalHistory = ((globalHistory << 1) & 3) + (instruction.getIsTaken() ? 1 : 0);
        }

        int maxvalue = 0;
        Iterator iterator = dictionary.entrySet().iterator();
        String maxkey = "";

        while (iterator.hasNext()) {

            Map.Entry pair = (Map.Entry)iterator.next();

            if ((int)pair.getValue() > maxvalue) {
                maxvalue = (int)pair.getValue();
                maxkey = (String)pair.getKey();
            }
        }

        System.out.println(String.format("\nCommon instruction : %s (%d occurences)", maxkey, maxvalue));

        return new int[] { entry, correct, incorrect };
    }
}
