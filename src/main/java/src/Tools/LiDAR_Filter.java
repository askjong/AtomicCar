package src.Tools;
import java.util.LinkedList;

/**
 * Class that saves five float[] arrays
 * takes the middle value of the Five arrays
 * saves the middle value in filterArray and returns it
 *
 * Author: T.I.F
 */
public class LiDAR_Filter {

    private static final double MIN_DISTANCE = 0.1;
    private static final double MAX_DISTANCE = 12;
    float[] filteredArray       = new float[360]; //creates filtered float[] array to return to UDPServerLidar
    LinkedList<float[]> FILO    = new LinkedList<float[]>();  //First In Last Out


    /**
     * @param unfilteredArray takes in one array at the time
     *                        and saves it
     *                        If the filter contains five arrays, with same length
     *                        this filter Class is ready to calculate output
     * @return Returns the filtered array back to consumer
     * <p>
     *
     */
    public  float[] filterArray(float[] unfilteredArray) {

        try {
            saveUnfilteredArray(unfilteredArray);

            if (this.FILO.size()>=5) {
                filteredArray = getMiddleValue();
            }else filteredArray = filteredArray;
           // System.out.println("Filter returned");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not get the filtered array");
        }
        return filteredArray;
    }

    /**
     * Calculate  middleValue between each index with data in the five stored ufilltered arrays.
     * @return returns the middleValue of the five unfilteredArrays
     */
    private  float[] getMiddleValue() {

        float returnValue[] = new float[360];
        float[] element1 = FILO.get(0);
        float[] element2 = FILO.get(1);
        float[] element3 = FILO.get(2);
        float[] element4 = FILO.get(3);
        float[] element5 = FILO.get(4);

        for (int i = 0; i < element1.length; i++) {
            float denominator = (float) 0.0;
            float value = (float) 0.0;

            if (element1[i] >= MIN_DISTANCE && element1[i] <= MAX_DISTANCE) {
                denominator++;
                value += element1[i];
            }
            if (element2[i] >= MIN_DISTANCE && element2[i] <= MAX_DISTANCE) {
                denominator++;
                value += element2[i];
            }
            if (element3[i] >= MIN_DISTANCE && element3[i] <= MAX_DISTANCE) {
                denominator++;
                value += element3[i];
            }
            if (element4[i] >= MIN_DISTANCE && element4[i] <= MAX_DISTANCE) {
                denominator++;
                value += element4[i];
            }
            if (element5[i] >= MIN_DISTANCE && element5[i] <= MAX_DISTANCE) {
                denominator++;
                value += element5[i];
            }

            //if statement to prevent NaN = 0 in float[]. needs to be 0.0 value
            if (value / denominator == 0) {
                returnValue[i] = (float) 0.0;
            } else returnValue[i] = value / denominator;


        }  //todo add cast return method
        return returnValue;
    }

    /**
     * @param unfilteredArray Takes in an float[] array
     *              and saves one at the time to Five global
     *              float[] arrays.
     *              This way, the class always has the Five newest distance
     *              readings from the LiDAR
     */
    public void saveUnfilteredArray(float[] unfilteredArray) {
        FILO.add(unfilteredArray);
        removeOverflowOfLIFO();
    }

    /**
     * Holds the length of FILO to 5.
     */
    private void removeOverflowOfLIFO() {
        while (FILO.size() > 5){
            FILO.removeFirst();
        }
    }

}