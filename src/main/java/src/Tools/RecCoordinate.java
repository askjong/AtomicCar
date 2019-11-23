package src.Tools;

/**
 * @author Andreas
 * @version X.X
 * @since 13.11.2019, 10:15
 */
public class RecCoordinate {
    private double x;
    private double y;


    public RecCoordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets x from recCordinate
     *
     * @return value of x
     */
    public double getX() {
        return this.x;
    }

    /**
     * Gets y from recCordinate
     *
     * @return value of y
     */
    public double getY() {
        return this.y;
    }
}
