package DesignAlgorithm;
/**
 * SimpleStats objects provide basic tally and report basic descriptive
 * statistics.  These include the minimum, maximum, sample size, sample mean,
 * sample variance, and sample standard deviation of all the observations
 * which have been added to the object.
 * @author Christopher J. Nannini
 */
public class SimpleStats {

   // class constants
   // class variables
   // instance variables
   // class methods
   // constructor methods

   private double sampleMean = Double.NaN;
   private double sampleVariance = Double.NaN;
   private double sampleSize = 0.0;
   private double min = Double.POSITIVE_INFINITY;
   private double max = Double.NEGATIVE_INFINITY;

   /**
    * Initialize the new object so that the sample mean and variance are
    * undefined, and the min and max are infinity and -infinity, respectively.
    */
   public SimpleStats() {

      reset();
   }

   /**
    * Set the object state so that the sample mean and vaiance are undefined,
    * and the min and max are infinity and -infinity, respectively.
    */
   public void reset() {

      sampleMean = Double.NaN;
      sampleVariance = Double.NaN;
      sampleSize = 0.0;
      min = Double.POSITIVE_INFINITY;
      max = Double.NEGATIVE_INFINITY;;
   }

   /**
    * Update statistics using Kalman Filtering with a new data value.
    * @param x
    *  The new data value.
    */
   public void newObs(double x) {

      sampleSize = sampleSize + 1.0;

      max = Math.max(max, x);
      min = Math.min(min, x);

      if (sampleSize == 1.0) {
         sampleMean = x;
         sampleVariance = 0.0;
      }
      else {
         sampleVariance = (((sampleSize - 2)/(sampleSize - 1)) * sampleVariance) + (((x-sampleMean)*(x-sampleMean))/sampleSize);
         sampleMean = sampleMean + ((x-sampleMean)/sampleSize);
      }
   }

   /**
    * Update statistics with all the data values in an array.
    * @param x
    *  x - the array.
    */
   public void newObs(double[] x)  {

      newObs(x, (x.length-1));
   }

   /**
    * Update statistics with all the data values in an array from elements 0
    * through last.
    * @param x
    *  x - the array.
    * @param last
    *  last - the index of the last element of the array to be included.
    */
   public void newObs(double[] x, int last)  {

      newObs(x,0,last);
   }

   /**
    * Update statistics with all the data values in an array from elements
    * first through last.
    * @param x
    *  x - the array.
    * @param first
    *  first - the index of the first element of the array to be included.
    * @param last
    *  last - the index of the last element of the array to be included.
    */
   public void newObs(double[] x, int first, int last)  {

      for (int i = first; i <= last; i++) {
         newObs(x[i]);
      }
   }

   /**
    * Report the sample mean.
    * @return
    *  Returns Double.NaN if sampleSize() == 0, the sample mean otherwise.
    */
   public double sampleMean() {

      return sampleMean;
   }

   /**
    * Report the sample variance.
    * @return
    *  Returns Double.NaN if sampleSize() == 0, zero if sampleSize() == 1,
    *  the sample variance otherwise.
    */
   public double sampleVariance() {

      return sampleVariance;
   }

   /**
    * Report the sample standard deviation.
    * @return
    *  Returns sqrt(sampleVariance).
    */
   public double sampleStdDev() {

      return Math.sqrt(sampleVariance);
   }

   /**
    * Report the sample size.
    * @return
    *  Returns number of times newObs() has been invoked.
    */
   public int sampleSize() {

      return (int)sampleSize;
   }

   /**
    * Report the sample min.
    * @return
    *  Returns Double.POSTIVE_INFINITY if sampleSize() == 0, min(X1,...,Xn)
    *  otherwise.
    */
   public double min() {

      return min;
   }

   /**
    * Report the sample max.
    * @return
    *  Returns Double.NEGATIVE_INFINITY if sampleSize() == 0, min(X1,...,Xn)
    *  otherwise.
    */
   public double max() {

      return max;
   }

}
