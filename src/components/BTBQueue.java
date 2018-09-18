/**
 * BTBQueue class file.
 * @author Setyo Legowo <setyolegowo@users.noreply.github.com>
 * @since Sept 14, 2018
 */

package id.ac.itb.if5010.hw1.components;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.lang.IllegalStateException;
import java.lang.IllegalArgumentException;

/**
 * Assume using 2,1 corelating branch prediction.
 */
public class BTBQueue
{
    /**
     * Buffer storage.
     */
    private List<BTBItem> list;

    /**
     * Latest position.
     */
    private int position;

    /**
     * Size of BTB.
     */
    public final int maxSize;

    /**
     * Bit length of low significant bit for BTB index.
     */
    private final int instructionBitSize;

    /**
     * Masking bit for instruction to get instruction index.
     */
    private final long instructionBitMasker;

    /**
     * Statistic overwrite;
     */
    private int statisticOverwrite;

    /**
     * Statistic for total hit.
     */
    private int statisticHit;

    /**
     * Statistic for miss rate.
     */
    private int statisticMiss;

    /**
     * @param queueSize Queue size.
     */
    public BTBQueue(int queueSize) {
        maxSize = queueSize;
        instructionBitSize = prepareInstructionBitSize();
        if (instructionBitSize > 0) {
            instructionBitMasker = ((long) Math.pow(2.0, (double) instructionBitSize)) - 1;
        } else {
            instructionBitMasker = 0;
        }
        list = Collections.synchronizedList(new ArrayList<BTBItem>(maxSize));
        position = maxSize - 1;
        resetStatistic();
    }

    private int prepareInstructionBitSize() {
        int tmp;
        switch(maxSize) {
            case 128:
                tmp = 8;
                break;
            case 64:
                tmp = 6;
                break;
            case 32:
                tmp = 5;
                break;
            case 16:
                tmp = 4;
                break;
            case 8:
                tmp = 3;
                break;
            case 4:
                tmp = 2;
                break;
            default:
                throw new IllegalArgumentException("Queue Size is not in map");
        }
        return tmp;
    }

    private String getInstructionIndex(String originalInstruction) {
        if (instructionBitSize <= 0) {
            return null;
        }

        long instructionHex = Long.decode(originalInstruction);
        return Long.toHexString(instructionHex & instructionBitMasker);
    }

    private void incrementPosition() {
        position = (position + 1) % maxSize;
    }

    public boolean offer(String instruction, String predictionAddress) {
        String _instruction = getInstructionIndex(instruction);
        if (_instruction == null) {
            return false;
        }

        BTBItem item = new BTBItem(_instruction, predictionAddress);
        return offer(item);
    }

    public boolean offer(BTBItem item) {
        // Offered item is not in buffer, add or overwrite.
        incrementPosition();
        if (position < list.size()) {
            if (list.get(position) != null) {
                statisticOverwrite++;
            }

            list.set(position, item);
        } else {
            list.add(item);
        }
        list.get(position).resetPrediction();

        return true;
    }

    public BTBItem element() {
        if (list.get(position) == null) {
            throw new NoSuchElementException();
        }
        return list.get(position);
    }

    public BTBItem peek() {
        return list.get(position);
    }

    /**
     * Whether the instruction is in buffer.
     */
    public boolean isHit(String instruction) {
        boolean retval = lookUp(instruction) != null;
        if (retval) {
            statisticHit++;
        }  else {
            statisticMiss++;
        }
        return retval;
    }

    /**
     * Insert to BTB when the instruction is not in buffer.
     */
    public boolean pushToBTB(String instruction, String predictionAddress) {
        return offer(instruction, predictionAddress);
    }

    /**
     * @return True if the instruction is inside BTB, else otherwise.
     */
    public boolean delete(String instruction) {
        String _instruction = getInstructionIndex(instruction);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) {
                continue;
            }

            if (list.get(i).instruction.equals(_instruction)) {
                list.set(i, null);
                return true;
            }
        }

        return false;
    }

    public BTBItem lookUp(String instruction) {
        String _instruction = getInstructionIndex(instruction);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) {
                continue;
            }

            if (list.get(i).instruction.equals(_instruction)) {
                return list.get(i);
            }
        }

        return null;
    }

    public boolean isEmptyNext() {
        return list.size() < maxSize;
    }

    public void resetStatistic() {
        statisticOverwrite = statisticHit = statisticMiss = 0;
    }

    public int getTotalOverwrite() {
        return statisticOverwrite;
    }

    public float getMissRate() {
        return ((float) statisticMiss/(statisticHit + statisticMiss));
    }

    public int getTotalMiss() {
        return statisticMiss;
    }

    public float getHitRate() {
        return ((float) statisticHit/(statisticHit + statisticMiss));
    }

    public int getTotalHit() {
        return statisticHit;
    }
}
