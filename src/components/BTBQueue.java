/**
 * BTBQueue class file.
 * @author Setyo Legowo <setyolegowo@users.noreply.github.com>
 * @since Sept 14, 2018
 */

package id.ac.itb.if5010.hw1.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.lang.IllegalStateException;
import java.lang.IllegalArgumentException;

/**
 *
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
        list = Collections.synchronizedList(new ArrayList<BTBItem>(maxSize));
        position = maxSize - 1;
        resetStatistic();
    }

    private void incrementPosition() {
        position = (position + 1) % maxSize;
    }

    public boolean offer(String instruction, String predictionAddress) {
        BTBItem item = new BTBItem(instruction, predictionAddress);
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
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) {
                continue;
            }

            if (list.get(i).instruction.equals(instruction)) {
                list.set(i, null);
                return true;
            }
        }

        return false;
    }

    public BTBItem lookUp(String instruction) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) {
                continue;
            }

            if (list.get(i).instruction.equals(instruction)) {
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
        return ((float)statisticMiss/(statisticHit + statisticMiss));
    }

    public int getTotalMiss() {
        return statisticMiss;
    }

    public float getHitRate() {
        return ((float)statisticHit/(statisticHit + statisticMiss));
    }

    public int getTotalHit() {
        return statisticHit;
    }
}
