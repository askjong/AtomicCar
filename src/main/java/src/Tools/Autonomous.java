package src.Tools;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

/**
 * @author Andreas S. and Finn-C.E
 * @version 2.0
 * @since 17.11.2019, 21:48
 * Code:
 * Autonomous algorithm
 *
 * Purpose:
 * This class is to calculate motor speed with Lidar data as input.
 */
public class Autonomous {
    // Distances in meters
    private static final double CAR_WIDTH = 0.52; // distnace from center to the end of the cars width.
    private static final double CAR_LENGTH = 0.37; // distance from center to the end of the cars length.
    private static final double MIN_DISTANCE = 0.32; // distance from the diagonal end of the viacle to the center.
    private static final double DISTANCE_TO_WALL = 0.03; // distance from car width to the width of the field of view.
    private static final double SAFE_DISTANCE = 1.0; //1.0 // distance from the car length to the length of the field of view.

    private int setSpeed = 0;
    private int fucked = 0;
    private boolean isReversing = false;

    public int[] drive(float[] lidarData) {
        double[] data = convertFloatsToDoubles(lidarData);

        int[] speed = new int[2];

        int degWidth = (int) (Math.toDegrees(Math.asin(((CAR_WIDTH / 2) + DISTANCE_TO_WALL) / (SAFE_DISTANCE + CAR_LENGTH / 2))));

        double[] rightField = new double[90];
        double[] leftField = new double[90];
        //Get green right field
        System.arraycopy(data, 0, leftField, 0, 90);
        //Get green left field
        System.arraycopy(data, 270, rightField, 0, 90);
        ArrayUtils.reverse(rightField);

        List<RecCoordinate> obstaclesLeft = obstaclesAhead(leftField, -1);
        List<RecCoordinate> obstaclesRight = obstaclesAhead(rightField, 1);

        if ((obstaclesLeft.size() == 0) && (obstaclesRight.size() == 0)) {
            speed[0] = this.setSpeed;
            speed[1] = this.setSpeed;
            fucked = 0;
            isReversing = false;

        }else if (fucked >= 3) {
            speed[0] = 30;
            speed[1] = -30;

        }else if (safeToTurn(data)) {
            if (obstaclesRight.size() > obstaclesLeft.size()) {
                // Drive left
                RecCoordinate result = nearestObject(obstaclesRight);
                speed = turningFactor(this.setSpeed, result);
            } else {
                // Drive right
                RecCoordinate result = nearestObject(obstaclesLeft);
                speed = turningFactor(this.setSpeed, result);
            }
            isReversing = false;

        } else if (safeToGoReverse(data, degWidth)) {
            speed[0] = -this.setSpeed / 2;
            speed[1] = -this.setSpeed / 2;
            if (!isReversing) {
                fucked += 1;
                isReversing = true;
            }

        }
        return speed;
    }

    /**
     * Calculate the speed needed  on each motor so the car can  turn away from a point in the field of view.
     * @param setSpeed , speed of the viacle.
     * @param coordinate, point of which to turn from.
     * @return output, speed on each motor.
     */
    private int[] turningFactor(int setSpeed, RecCoordinate coordinate) {
        // A value from -1 to 1
        double turningDirection = 0;
        if (coordinate.getX() < 0) {
            turningDirection = 1;
        } else {
            turningDirection = -1;
        }
        // A value from 0 to 100
        double turningMagnitude = (SAFE_DISTANCE - Math.pow(coordinate.getY(), 1)) * 100;


        double turningFactor = Math.abs(turningMagnitude * turningDirection);
        double speedBoost = (100 - turningFactor) * (setSpeed) / (100);
        double speedController = turningMagnitude * turningDirection;

        int[] output = new int[2];
        output[0] = (int) (speedController + speedBoost);
        output[1] = (int) (-speedController + speedBoost);
        return output;
    }

    /**
     * Finds the closest point in the field of view.
     * @param list, of cordinates.
     * @return result, the closest point.
     */
    private static RecCoordinate nearestObject(List<RecCoordinate> list) {

        RecCoordinate result = new RecCoordinate(9999999, 9999999);

        for (RecCoordinate recCoordinate : list) {
            if (recCoordinate.getY() < result.getY()) {
                result = recCoordinate;
            }
        }
        return result;
    }

    /**
     * A utillity method to convert float array to double array.
     * @param input, float array
     * @return output,double array
     */
    private double[] convertFloatsToDoubles(float[] input) {
        if (input == null) {
            return null; // Or throw an exception - your choice
        }
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }


    /**
     * Makes a list of all blocked points inn the field of view.
     * @param data, double array of data.
     * @param displacement, 1 if positive x-axis and -1 for negative x-axis.
     * @return
     */
    private List obstaclesAhead(double[] data, int displacement) {
        double[] lidarData = new double[90];

        List<RecCoordinate> obstacles = new ArrayList<>();

        System.arraycopy(data, 0, lidarData, 0, 90);

        for (int i = 0; i < lidarData.length; i++) {
            double x = lidarData[i] * Math.sin(Math.toRadians(displacement * i));
            double y = lidarData[i] * Math.cos(Math.toRadians(i));

            if ((y >= SAFE_DISTANCE) || (Math.abs(x) >= (DISTANCE_TO_WALL + (CAR_WIDTH / 2))) || Double.isNaN(lidarData[i])) {

            } else {
                obstacles.add(new RecCoordinate(x, y));
            }
        }
        return obstacles;
    }

    /**
     * Controlls if its safe for the viacle to reverse.
     * @param data, double array of LiDAR data.
     * @param degWidth, angle inn degrees to distribute field of view from south.
     * @return safe, boolean value of which its safe to reverse or not.
     */
    private boolean safeToGoReverse(double[] data, int degWidth) {
        double[] lidarData = new double[degWidth * 2];
        System.arraycopy(data, 180 - degWidth, lidarData, 0, degWidth * 2);
        boolean safe = DoubleStream.of(lidarData).allMatch(var -> var > MIN_DISTANCE || Double.isNaN(var));

        return safe;
    }

    /**
     * Controlls if its safe to turn in a 360 degrees field of view.
     * @param data, LiDAR data.
     * @return SafeToTurn, boolean value on which its safe to turn.
     */|
    private boolean safeToTurn(double[] data) {
        boolean safeToTurn = DoubleStream.of(data).allMatch(var -> var > MIN_DISTANCE || Double.isNaN(var));
        return safeToTurn;
    }

    /**
     * Sets base speed of the viacle.
     *
     * @param setSpeed , base speed.
     */
    public void setSpeed(int setSpeed) {
        this.setSpeed = setSpeed;
    }
}