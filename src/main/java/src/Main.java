package src;

import src.Servers.TCPServer;
import src.Servers.UDPServerCam;
import src.Servers.UDPServerLidar;
import src.Servers.UDPServerScatter;
import src.Tools.Terminal;

/**
 * Authors: F-C. E., A.S.S & T.I.F
 * Group: 15 sanntid
 * <p>
 * purpous:
 * this group prodject is ment to create a selfdriving car that can take the use of LiDAR to drive around.
 * <p>
 * code:
 * this code builds objects and starts threads to run them.
 */
class Main {

    private final static int PYTHON_PORT = 8000;
    private final static int GUI_PORT = 8001;
    private final static int CAM_PORT = 9000;
    private final static int LIDAR_PORT = 9001;
    private final static int MAP_PORT = 9002;




    public static void main(String[] args) {

        initialize();

    }

    /**
     * Initialize all servers and a instance of the communication center.
     */
    public static void initialize(){

        //initLiDAR();
        UDPServerCam camServer = new UDPServerCam(CAM_PORT);
        UDPServerScatter scatterServer = new UDPServerScatter(MAP_PORT);
        TCPServer serverRoboClaw = new TCPServer(PYTHON_PORT);
        TCPServer serverGUI = new TCPServer(GUI_PORT);
        UDPServerLidar udpLidar = new UDPServerLidar(LIDAR_PORT);
        COMCenter COM = new COMCenter(serverRoboClaw, serverGUI, udpLidar, scatterServer);
        COM.start();
    }


    /**
     * This method is to start up the LiDAR clients needed on the JETSONS
     * internal storage.
     */
    private static void initLiDAR() {
        try {
            Terminal.doCommand("killall rplidarNode");
            Thread.sleep(5000);
            Terminal.doCommand("killall python");
            Thread.sleep(5000);
            Terminal.doCommand("killall roslaunch");
            Thread.sleep(10000);
            System.out.println("Starting ROS");
            Terminal.doScript("/home/gruppe15/Desktop/initRPLiDAR.sh");
            System.out.println("ROS started");
            Thread.sleep(7000);
            System.out.println("Starting UDP");
            Terminal.doScript("/home/gruppe15/Desktop/initUDPClientRPLiDAR.sh");
            System.out.println("UDP started");
            Thread.sleep(7000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}