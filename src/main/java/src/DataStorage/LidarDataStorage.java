package src.DataStorage;
/**
 * This class reprecent the idea of DataSorage class. it works as a storage and a common place to write/read LiDar data.
 * author: Finn-C.E
 */
public class LidarDataStorage{

    private volatile float[] lidarArray;
    private volatile boolean newArray = false;

    public void LidarDataStorage(){
    }

    /**
     * Method to retrieve Lidar Array from the dataStorage.
     * @return LidarArray.
     */
    public  float[] getArray() {
        this.newArray = false;
        return lidarArray;
    }

    /**
     *  method to store the LiDAR array in the dataStorage.
     * @param newList, the new LiDAR array.
     */
    public  void setArray(float[] newList) {
        this.lidarArray = newList;
        this.newArray = true;
    }

    /**
     * Checks for new Array
     * @return newArray, Boolean.
     */
    public Boolean isNewArray(){
        return this.newArray;
    }
}
