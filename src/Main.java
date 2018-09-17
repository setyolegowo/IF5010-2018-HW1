package id.ac.itb.if5010.hw1;

import id.ac.itb.if5010.hw1.components.*;
import java.util.*;

public class Main {
    private static final String HISTORY_FILENAME = "history.txt";

    // attribute
    private static boolean warmStart = false;
    private static int btbSize = 4;
    private static int entry, hit, miss, correct, incorrect, overwrite, value;
    private static FileHandler fileHandler;
    private static BTBQueue btbQueue;

    public static void main(String[] args) {

        // read configuration
        parseConfiguration(args);

        // init file handler and btb queue
        fileHandler = new FileHandler(HISTORY_FILENAME);
        btbQueue = new BTBQueue(btbSize);

        // run algorithm
        int[] result = runPrediction();

        if (warmStart) {
            fileHandler.reset();
            result = runPrediction();
        }

        System.out.println("\nSummary : ");
        System.out.println(String.format("Total entry : %d", result[0]));
        System.out.println(String.format("Total BTB hit : %d (%.2f%%)", result[1], result[1] * 100d / result[0]));
        System.out.println(String.format("Total BTB miss : %d (%.2f%%)", result[2], result[2] * 100d / result[0]));
        System.out.println(String.format("Total correct prediction : %d (%.2f%%)", result[3], result[3] * 100d / result[0]));
        System.out.println(String.format("Total incorrect prediction : %d (%.2f%%)", result[4], result[4] * 100d / result[0]));
        System.out.println(String.format("Total BTB overwrite : %d", result[5]));
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

            count++;
        }

        System.out.println(String.format("Starting program with %d BTB size and %s", btbSize, warmStart ? "warm start" : "cold start"));
    }

    private static int[] runPrediction() {

        Instruction instruction;
        Map<String, Integer> dictionary;

        entry = hit = miss = correct = incorrect = overwrite = 0;
        dictionary = new HashMap<>();

        while ((instruction = fileHandler.next()) != null) {
            System.out.print(instruction.getInstruction());
            entry++;

            if (dictionary.containsKey(instruction.getInstruction())) {
                value = dictionary.get(instruction.getInstruction());
                dictionary.put(instruction.getInstruction(), value + 1);
            }
            else {
                dictionary.put(instruction.getInstruction(), 1);
            }

            if (btbQueue.isHit(instruction.getInstruction())) {
                System.out.print("\tHit");
                hit++;

                BTBItem btbItem = btbQueue.lookUp(instruction.getInstruction());

                if (!btbItem.isTaken() ^ instruction.getIsTaken()) {
                    System.out.print(String.format("\t%s\t%s\tCorrect", instruction.getIsTaken() ? "T" : "NT", btbItem.isTaken() ? "T" : "NT"));
                    correct++;
                }
                else {
                    System.out.print(String.format("\t%s\t%s\tIncorrect", instruction.getIsTaken() ? "T" : "NT", btbItem.isTaken() ? "T" : "NT"));
                    incorrect++;

                    if (instruction.getIsTaken()) {
                        btbItem.taken();
                    }
                    else {
                        btbItem.notTaken();
                    }
                }
            }
            else {
                System.out.print("\tMiss");
                miss++;

                if (instruction.getIsTaken()) {
                    System.out.print("\tT\t\tIncorrect");
                    incorrect++;

                    if (!btbQueue.isEmptyNext()) {
                        overwrite++;
                    }

                    btbQueue.pushToBTB(instruction.getInstruction(), instruction.getBranchTarget());
                }
                else {
                    System.out.print("\tNT\t\tCorrect");
                    correct++;
                }
            }

            System.out.println();
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

        System.out.println(String.format("Common instruction : %s (%d occurences)", maxkey, maxvalue));

        return new int[] { entry, hit, miss, correct, incorrect, overwrite };
    }
}
