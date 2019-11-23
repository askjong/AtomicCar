package src.Servers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * Create a array lidardata broadcasting Server with the UDP protocol.
 * author: Finn-C.E and Andreas S
 */
public class UDPServerScatter extends Thread {
    private InetAddress clientIPAddress;
    private int clientPort;
    private DatagramSocket serverSocket;
    private static int port;
    private float[] list;
    private boolean broadcast;
    private boolean run;

    /**
     * Create a instance of UDPServerScatter
     * @param port
     */
    public UDPServerScatter(int port) {
        this.port = port;
        list = new float[360];
        run = true;
        broadcast = false;
    }

    /**
     * Stars up server at socket port. reads inncomming massage.
     */
    public void run() {
        while (run) {
            try {
                System.out.println("UDP Scatter Server started");
                this.serverSocket = new DatagramSocket(this.port);

                while (true) {
                    byte[] receiveData = new byte[1440];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    this.serverSocket.receive(receivePacket);
                    String sentence = new String(receivePacket.getData());
                    this.clientIPAddress = receivePacket.getAddress();
                    this.clientPort = receivePacket.getPort();
                    if (sentence.trim().equalsIgnoreCase("Start")) {
                        this.broadcast = true;
                        System.out.println("Broadcasting scatter to gui");
                    } else if (sentence.trim().equalsIgnoreCase("Stop")) {
                        this.broadcast = false;
                        System.out.println("GUI broadcast scatter ended");
                    } else if (sentence.trim().equalsIgnoreCase("exit")) {
                        System.out.println("Scatterserver down.....");
                        this.run = false;

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("UDP server crashed");
                this.serverSocket.close();
            }catch (NullPointerException e) {
                e.printStackTrace();
                System.out.println("UDP server crashed");
                this.serverSocket.close();
            }
        }

    }


    /**
     * Sends a float array to Client.
     * @param floatdata, float Array.
     */
    public void outToClient(float[] floatdata) {
        if((floatdata != null)&&this.broadcast){
            try {
                byte[] data = floatToByte(floatdata);
                //System.out.println(Arrays.toString(toFloatArray(data)));
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, this.clientIPAddress, this.clientPort);
                this.serverSocket.send(sendPacket);
            } catch (IOException ex) {
                ex.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
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

