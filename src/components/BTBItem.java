/**
 * BTBItem class file.
 * @author Setyo Legowo <setyolegowo@users.noreply.github.com>
 * @since Sept 14, 2018
 */

package id.ac.itb.if5010.hw1.components;

/**
 *
 */
public class BTBItem
{
    public static final int OPTIONAL_BIT_SIZE = 2;
    private static final int PREDICTION_MAX_VALUE = (OPTIONAL_BIT_SIZE*OPTIONAL_BIT_SIZE) - 1;

    public final String instruction;

    public final String predictionAddress;

    private int predictionBits = PREDICTION_MAX_VALUE;

    public BTBItem() {
        instruction = "UNKNOWN";
        predictionAddress = "UNKNOWN";
    }

    public BTBItem(String _instruction, String _predictionAddress) {
        instruction = _instruction;
        predictionAddress = _predictionAddress;
    }

    public boolean isTaken() {
        return (predictionBits ^ OPTIONAL_BIT_SIZE) > 0;
    }

    public void taken() {
        predictionBits = ((predictionBits << 1) + 1) & PREDICTION_MAX_VALUE;
    }

    public void notTaken() {
        predictionBits = (predictionBits << 1) & PREDICTION_MAX_VALUE;
    }

    public void resetPrediction() {
        predictionBits = PREDICTION_MAX_VALUE;
    }
}
