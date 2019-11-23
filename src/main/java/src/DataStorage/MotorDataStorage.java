package src.DataStorage;

import com.sun.jndi.toolkit.ctx.AtomicContext;

import java.util.concurrent.atomic.*;

/**
 * This class reprecent the idea of DataSorage class. it works as a storage and a common place to write/read motor data.
 * author: Finn-C.E
 */
public class MotorDataStorage {

    private AtomicInteger speedMotor1;
    private AtomicInteger speedMotor2;
    private AtomicInteger speed;
    private AtomicReference<String> command;
    private AtomicInteger encoderOne;
    private AtomicInteger encoderTwo;

    /**
     * Create a instance of the class MotorDataStorage and sets datavalues to zero, to prevent null statements.
     */
    public MotorDataStorage(){
        this.speedMotor1 = new AtomicInteger(0);
        this.speedMotor2 = new AtomicInteger(0);
        this.speed = new AtomicInteger(50);
        this.encoderOne = new AtomicInteger(0);
        this.encoderTwo = new AtomicInteger(0);
        this.command= new AtomicReference<>("");

    }

    /**
     * Gets stored speed for motor one.
     * @return int speed.
     */
    public  int getSpeedMotor1() { return speedMotor1.get(); }

    /**
     * stores speed for motor one.
     * @param int speed.
     */
    public  void setSpeedMotor1(int speed) {
        this.speedMotor1.set(speed);
    }
    /**
     * Gets stored speed for Autonom speed.
     * @return int speed.
     */
    public  int getSpeed() { return speed.get(); }

    /**
     * stores speed for AutonomSpeed.
     * @param int speed.
     */
    public  void setSpeed(int speed) {
        this.speed.set(speed);
    }
    /**
     * Gets stored speed for motor two.
     * @return int speed.
     */
    public  int getSpeedMotor2() { return speedMotor2.get(); }

    /**
     * stores speed for motor one.
     * @param int speed.
     */
    public  void setSpeedMotor2(int speed) {
        this.speedMotor2.set(speed);
    }

    /**
     * Sets speed to 0.
     */
    public void stop(){
        this.speedMotor1.set(0);
        this.speedMotor2.set(0);
    }

    /**
     * Gets stored command from gui.
     * @return string command.
     */
    public  String getCommand() { return command.get(); }

    /**
     * stores command from gui.
     * @param string command.
     */
    public   void setCommand(String command) {
        this.command.set(command);
    }

    public  void setEncoderOne(int speed) {
        this.encoderOne.set(speed);
    }

    public  void setEncoderTwo(int speed) {
        this.encoderTwo.set(speed);
    }
    public  int getEncoderTwo() { return encoderTwo.get(); }
    public  int getEncoderOne() { return encoderOne.get(); }

}
