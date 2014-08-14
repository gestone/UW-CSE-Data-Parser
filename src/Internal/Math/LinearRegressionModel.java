package Internal.Math;


/**
 * A RegressionModel that fits a straight line to a data set.
 */
public class LinearRegressionModel  {

    /** The y intercept of the straight line */
    private double a;

    /** The gradient of the line */
    private double b;

    private double[] xValues;
    private double[] yValues;

    /**
     * Construct a new LinearRegressionModel with the supplied data set
     *
     * @param x
     *          The x data points
     * @param y
     *          The y data points
     */
    public LinearRegressionModel(double[] x, double[] y) {
        a = b = 0;
        xValues = x;
        yValues = y;
    }

    /**
     * Get the coefficents of the fitted straight line
     *
     * @return An array of coefficients {intercept, gradient}
     *
     */
    public double[] getCoefficients() {
        return new double[] { a, b };
    }

    /**
     * Compute the coefficients of a straight line the best fits the data set
     *
     */
    public void compute() {

        // throws exception if regression can not be performed
        if (xValues.length < 2 | yValues.length < 2) {
            throw new IllegalArgumentException("Must have more than two values");
        }

        // get the value of the gradient using the formula b = cov[x,y] / var[x]
        b = MathUtils.covariance(xValues, yValues) / MathUtils.variance(xValues);

        // get the value of the y-intercept using the formula a = ybar + b * xbar
        a = MathUtils.mean(yValues) - b * MathUtils.mean(xValues);

        // set the computed flag to true after we have calculated the coefficients
    }

}

