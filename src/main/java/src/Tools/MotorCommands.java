package src.Tools;

/**
 * This class purouse is to build a template string before sending it to Roboclaw.
 * author: Finn-C.E
 */
public class MotorCommands{

    public MotorCommands(){

    }

    /**
     * Make the command tamplate to request for encoder data from motorcontroller.
     * @return returns the String with template.
     */
    public String sendEncoderRequest(){
        //System.out.println("Sending Encoder Request to phyton");
        String payload = "getEncoderData";
        return payload;
    }
    /**
     * Prints "Sending 'stop' command"
     * @return String payload with "stop" command
     */
    public String stop() {
        String payload = "stop";
        return payload;
    }

    /**Makes the command template to set speed motor one and two at the motor controller.
     * @param motor1 integer with commanded motor1 speed
     * @param motor2 integer with commanded motor2 speed
     */
        public String RoboclawFormat(int motor1, int motor2){
        String payload = ",setspeedmotorone:" + motor1 + " ,setspeedmotortwo:" + motor2;
        return(payload);

    }


}
