/**
 * Author: Cristina Guerrero
 * Date: 10th February 2021
 */

package heuristic_diversification.mapelites;

public class Buckets {
    
    public static int getMapIdx(Double value, Integer minValue, Integer maxValue, Integer bucketSize) {
        //System.out.println("getMapIdx: " + value + " " + minValue + " "+ maxValue + " "+ bucketSize + " ");
        if (value <= minValue){
            return 0;
        }

        if ((maxValue != null) && (value >= maxValue)) {
            return getMaxIdx(minValue, maxValue, bucketSize);
        }

        return getBucketIdx(value, minValue, bucketSize);
    }

    public static int getBucketIdx(Double value, Integer minValue, Integer bucketSize) {
        return (int) (((value - minValue) / bucketSize) + 1);
    }

    public static int getMaxIdx(Integer minValue, Integer maxValue, Integer bucketSize) {
        return getBucketIdx((double) maxValue, minValue, bucketSize);
    }
}
