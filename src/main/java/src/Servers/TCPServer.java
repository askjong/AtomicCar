package src.Servers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class makes a instanse of a TCP protocol server.
 * the use of this server is to read and write to a external client.
 * author: Finn-C.E
 */

public class TCPServer extends Thread {
    private DataOutputStream outToClient;
    private String clientString;
    private volatile boolean message;
    private int port;

    /**
     * Constructor for the class TCPServer.
     * takes in param, cleate a object of it self and starts a thread.
     * @param port
     */
    public TCPServer(int port) {
        this.port = port;
        this.clientString = "";
        this.message = false;
    }

    @Override
    /**
     * Create establishment between server and clinet.
     * this to  read/write messages from/to the client.
     */
    public void run() {

        while (true) {
            try {

                ServerSocket welcomeSocket = new ServerSocket(port);
                Socket connectionSocket = welcomeSocket.accept();

                if (connectionSocket.isConnected()) {
                    System.out.println("TCP client connected at " + connectionSocket.getRemoteSocketAddress());
                }

                while (!connectionSocket.isClosed()) {
                    try {
                        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                        clientString = inFromClient.readLine();

                        if (clientString == null) throw new NullPointerException();
                        else message = true;

                    } catch (NullPointerException npe) {
                        System.out.println("Exseption: " + npe.getMessage());
                        connectionSocket.close();
                        welcomeSocket.close();
                    } catch (SocketException se) {
                        System.out.println("Exseption: " + se.getMessage());
                        connectionSocket.close();
                        welcomeSocket.close();
                    }
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }


    /**
     * Sends message to client.
     * @param payload, message string.
     * @throws IOException
     */
    public synchronized void messageToClient(String payload) throws IOException{
        try {
            outToClient.write(payload.toLowerCase().getBytes());
        }catch (NullPointerException e){
            System.out.println(e.toString());
        }
    }

    /**
     * return last massage inn from client. sets flag to false.
     * @return, massage string.
     */
    public synchronized String getClientString() throws NullPointerException {
        if(clientString != null) {
            message = false;
            return clientString;
        }else{throw new NullPointerException();}

    }

    /**
     * Checks flag value.
     * @return boolean flag.
     */
    public  Boolean isMessage(){
        return message;
    }
}