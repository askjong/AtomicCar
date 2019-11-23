package src.Servers;

import src.Tools.Camera;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Create a Image broadcasting Server with the UDP protocol.
 * author: Finn-C.E
 */

public class UDPServerCam implements Runnable {
    private InetAddress clientIPAddress;
    private int clientPort;
    private DatagramSocket serverSocket;
    private static int port;
    private Thread thread;
    private Camera cam;
    private boolean run;
    private boolean broadcast;

    /**
     * Create a instance of UDPServerCam and stars the thread.
     * @param port
     */
    public UDPServerCam(int port) {
        this.cam = new Camera(this);
        run = true;
        broadcast = false;
        this.port = port;
        this.thread = new Thread(this);
        thread.start();
    }

    /**
     * Stars up server at socket port. reads inncomming massage.
     */
    public void run() {
        while (run) {
            try {
                System.out.println("UDP Cam Server started");
                serverSocket = new DatagramSocket(port);
                while (true) {
                    byte[] receiveData = new byte[10000];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    String sentence = new String(receivePacket.getData());
                    clientIPAddress = receivePacket.getAddress();
                    clientPort = receivePacket.getPort();
                    System.out.println("Cam: "+sentence);
                    if (sentence.trim().equalsIgnoreCase("Start")) {
                        cam.startCamera();
                        broadcast = true;
                        System.out.println("Broadcasting image");
                    } else if (sentence.trim().equalsIgnoreCase("stop")) {
                        broadcast = false;
                        System.out.println("Stops Broadcasting image");
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("UDP server crashed");
            }catch (Exception e){
                e.toString();
            }
        }
    }

    /**
     * Sends image to client.
     * @param image, image BufferedImage.
     * @throws IOException
     */
    public void outToClient( BufferedImage image) {
        try {

            if(broadcast) {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);

                byte[] imBuffer = baos.toByteArray();
                //System.out.println(imBuffer.length);
                DatagramPacket packet = new DatagramPacket(
                        imBuffer,
                        imBuffer.length,
                        this.clientIPAddress,
                        this.clientPort
                );
                this.serverSocket.send(packet);

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }


}
