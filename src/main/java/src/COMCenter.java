package src;

import src.DataStorage.LidarDataStorage;
import src.DataStorage.MotorDataStorage;
import src.Servers.TCPServer;
import src.Servers.UDPServerLidar;
import src.Servers.UDPServerScatter;
import src.Tools.Autonomous;
import src.Tools.MotorCommands;

import java.io.IOException;
import java.util.Arrays;

/**
 * This class is to connect all communication between all classes in the main code.
 * <p>
 * auther: Finn-C.E
 */
public class COMCenter {

    private MotorDataStorage motorDataStorage;
    private TCPServer roboclawServer;
    private TCPServer serverGUI;
    private UDPServerLidar udpLidarServer;
    private UDPServerScatter scatterServer;
    private MotorCommands motorCommands;
    private Autonomous autonomous;
    private LidarDataStorage lidarDataStorage;
    private Thread sendAndUpdateThread;
    private Thread storeThread;
    private volatile String controll = "";

    /**
     * Constructor for the class COMCenter.
     * <p>
     *
     * @param server    is the server instance connected to the Python Client
     * @param serverGUI is the server instance connected to the GUI Client
     * @param udpLidar  is the server instance connected to the LIDAR
     */
    public COMCenter(TCPServer server, TCPServer serverGUI, UDPServerLidar udpLidar, UDPServerScatter scatterServer) {
        this.roboclawServer = server;
        this.serverGUI = serverGUI;
        this.udpLidarServer = udpLidar;
        this.scatterServer = scatterServer;
        this.autonomous = new Autonomous();
        this.motorCommands = new MotorCommands();
        this.lidarDataStorage = new LidarDataStorage();
        this.motorDataStorage = new MotorDataStorage();
        this.storeThread = new Thread(() -> {
            storeIncomingMessage();
        });
        this.sendAndUpdateThread = new Thread(() -> {
            sendUpdatedData();
        });
    }


    /**
     * Start up server threads and two threads to handle them.
     */
    public void start() {
        this.serverGUI.start();
        this.roboclawServer.start();
        this.udpLidarServer.start();
        this.scatterServer.start();
        this.storeThread.start();
        this.sendAndUpdateThread.start();

    }

    /**
     * Read command from TCPServer object and returns an array.
     *
     * @param inputServer, Object TCPServer
     * @return String[], Commands from TCPServer
     */
    private String[] getInputCommand(TCPServer inputServer) throws NullPointerException {
        String text = inputServer.getClientString();
        String[] input = inputParser(text);
        return input;
    }

    /**
     * Reads inputfealds from each server and store them Datastorage.
     */
    private void storeIncomingMessage() {
        while (true) {
            try {
                //GUI
                if (serverGUI.isMessage()) {
                    String[] inputGUI = getInputCommand(this.serverGUI);
                    storeValidCommand(inputGUI);
                }

                //python Roboclaw
                if (roboclawServer.isMessage()) {
                    String[] inputRoboclaw = getInputCommand(this.roboclawServer);
                    storeEncoderData(inputRoboclaw);
                }

                //LidarServer
                if(udpLidarServer.isNewData()) {
                this.lidarDataStorage.setArray(this.udpLidarServer.getList());
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Splits the param into String[] at each ":"
     * eks. Speed:40 = ["speed","40"];
     *
     * @param keyword String
     * @return String[]
     */
    public static String[] inputParser(String keyword) {
        return keyword.split(":");
    }


    /**
     * this method parse incoming commands according to the commands
     * options:
     * "getencoder"
     * "stop"
     * "Speed"
     * "manualmotorcontrol"
     * "motorcontrol"
     * "Manual"
     * "Auto"
     * <p>
     * if valid command, stores it.
     *
     * @param input String[] input
     */
    private void storeValidCommand(String[] input) {
        try {

            if (input[0].trim().equalsIgnoreCase("stop")) {
                this.motorDataStorage.setCommand(input[0]);
                System.out.println("stops car");
            } else if (input[0].trim().equalsIgnoreCase("getencoder")) {
                this.motorDataStorage.setCommand(input[0]);
            } else if (input[0].equalsIgnoreCase("manualmotorcontrol") && input.length == 3) {
                this.motorDataStorage.setCommand(input[0]);
                this.motorDataStorage.setSpeedMotor1(Integer.parseInt(input[1]));
                this.motorDataStorage.setSpeedMotor2(Integer.parseInt(input[2]));
            } else if (input[0].equalsIgnoreCase("speed")) {
                this.motorDataStorage.setSpeed(Integer.parseInt(input[1]));
            } else if (input[0].equalsIgnoreCase("MOTORCONTROL")) {
                if (input[1].equalsIgnoreCase("AUTO")) {
                    this.motorDataStorage.stop();
                    this.controll = "AUTO";
                } else if (input[1].equalsIgnoreCase("MANUAL")) {
                    this.motorDataStorage.stop();
                    this.controll = "MANUAL";
                }
            }
        } catch (
                ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            System.out.println("failed to parse command");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("failed to parse command");
        }

    }

    /**
     * Stores encoderData in MotorDataStorage.
     *
     * @param input, string[].
     */
    private void storeEncoderData(String[] input) {
        if (input[0].equalsIgnoreCase("enc1") && input[2].equalsIgnoreCase("enc2")) {
            this.motorDataStorage.setEncoderOne(Integer.parseInt(input[1]));
            this.motorDataStorage.setEncoderTwo(Integer.parseInt(input[3]));
        }
    }


    /**
     * Reads data from datastorage and sends them out to classes.
     */
    private void sendUpdatedData() {
        int oldMotorSpeed1 = 0;
        int oldMotorSpeed2 = 0;

        while (true) {
            try {
                if (this.lidarDataStorage.isNewArray()) {
                    this.scatterServer.outToClient(lidarDataStorage.getArray());
                }
                switch (controll) {

                    case ("MANUAL"):
                        String data = motorDataStorage.getCommand();
                        int motorSpeed1 = this.motorDataStorage.getSpeedMotor1();
                        int motorSpeed2 = this.motorDataStorage.getSpeedMotor2();


                        if ((data.equalsIgnoreCase("manualmotorcontrol")) && ((motorSpeed1 != oldMotorSpeed1) || (motorSpeed2 != oldMotorSpeed2))) {

                            this.roboclawServer.messageToClient(this.motorCommands.RoboclawFormat(motorSpeed1, motorSpeed2));

                            oldMotorSpeed1 = motorSpeed1;
                            oldMotorSpeed2 = motorSpeed2;

                        } else if (data.equalsIgnoreCase("stop")) {
                            this.roboclawServer.messageToClient(motorCommands.stop());
                        } else if (data.equalsIgnoreCase("encoderdata")) {
                            this.roboclawServer.messageToClient(motorCommands.sendEncoderRequest());
                        }
                        break;

                    case ("AUTO"):
                        this.autonomous.setSpeed(this.motorDataStorage.getSpeed());
                        int[] newspeed = this.autonomous.drive(this.lidarDataStorage.getArray());

                        if (((newspeed[0] != oldMotorSpeed1) || (newspeed[1] != oldMotorSpeed2))) {
                            this.roboclawServer.messageToClient(this.motorCommands.RoboclawFormat(newspeed[0], newspeed[1]));
                            oldMotorSpeed1 = newspeed[0];
                            oldMotorSpeed2 = newspeed[1];
                        }
                        break;



                }

            } catch (IOException w) {
                w.printStackTrace();
                System.out.println("Exseption: " + w.getMessage());
            }
        }
    }

}