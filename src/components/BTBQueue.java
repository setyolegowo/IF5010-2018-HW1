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
     * @param queueSize Queue size.
     */
    public BTBQueue(int queueSize) {
        maxSize = queueSize;
        list = Collections.synchronizedList(new ArrayList<BTBItem>(maxSize));
        position = maxSize - 1;
    }

    private void incrementPosition() {
        position = (position + 1) % maxSize;
    }

    public boolean offer(String instruction, String predictionAddress) {
        BTBItem item = new BTBItem(instruction, predictionAddress);
        return offer(item);
    }

    public boolean offer(BTBItem item) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) {
                break;
            }

            if (item.instruction == list.get(i).instruction) {
                throw new IllegalArgumentException("You cannot offer same instruction");
            }
        }

        // Offered item is not in buffer, add or overwrite.
        incrementPosition();
        if (position < list.size()) {
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
        return lookUp(instruction) != null;
    }

    /**
     * Insert to BTB when the instruction is not in buffer.
     */
    public boolean pushToBTB(String instruction, String predictionAddress) {
        return offer(instruction, predictionAddress);
    }

    private BTBItem lookUp(String instruction) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) {
                return null;
            }

            if (list.get(i).instruction == instruction) {
                return list.get(i);
            }
        }

        return null;
    }
}
