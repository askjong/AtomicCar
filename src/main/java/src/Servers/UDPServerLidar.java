package src.Servers;


import src.Tools.LiDAR_Filter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This class is to communicate with the "Lidar"(Python Client) to retrive the raw Lidar array data.
 */

public class UDPServerLidar extends Thread {
    private DatagramSocket serverSocket;
    private static int port;
    private volatile float[] list;
    private LiDAR_Filter liDAR_filter;
    private volatile boolean flag;

    /**
     * create a instance of the class UDPServerLidar.
     *
     * @param port, int value to the connection port.
     */
    public UDPServerLidar(int port) {
        this.liDAR_filter = new LiDAR_Filter();
        this.port = port;
        list = new float[360];
        flag = true;
    }

    /**
     * Reads incoming data and filters it.
     */
    public void run() {
        while (true) {
            try {
                System.out.println("UDP Lidar Server started");
                serverSocket = new DatagramSocket(port);

                while (true) {
                    byte[] receiveData = new byte[1440];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);


                    try {
                        this.list = liDAR_filter.filterArray(byteToFloat(receivePacket.getData()));
                        flag = true;
                    } catch (NumberFormatException e) {
                        System.out.println("got null");
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("UDP server crashed");
                serverSocket.close();
            } catch (NullPointerException e) {
                e.printStackTrace();
                System.out.println("UDP server crashed");
                serverSocket.close();
            }
        }

    }


    /**
     * Gets the prosessed Lidar data list.
     * @return list
     */
    public  float[] getList() {
        flag = false;
        return this.list;
    }

    /**
     * Checks if its a new available list
     * @return flag
     */
    public boolean isNewData(){
        return flag;
    }

    /**
     * A utillity method to convert byte array to float array.
     * @param input, byte array
     * @return ret,float array
     */
    public static float[] byteToFloat(byte[] input) {
        float[] ret = new float[input.length / 4];
        for (int x = 0; x < input.length; x += 4) {
            ret[x / 4] = ByteBuffer.wrap(input, x, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        }
        return ret;
    }

    /**
     * A utillity method to convert float array to byte array.
     * @param input, float array
     * @return ret,byte array
     */
    public static byte[] floatToByte(float[] input) {
        byte[] ret = new byte[input.length * 4];
        for (int x = 0; x < input.length; x++) {
            ByteBuffer.wrap(ret, x * 4, 4).order(ByteOrder.LITTLE_ENDIAN).putFloat(input[x]);
        }
        return ret;
    }


}
