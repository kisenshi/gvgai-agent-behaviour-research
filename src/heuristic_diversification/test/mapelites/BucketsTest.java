package heuristic_diversification.test.mapelites;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import heuristic_diversification.mapelites.Buckets;

public class BucketsTest {
    
    @Test
    public void testgetMapIdx() {
        Integer maxValue; 
        Integer minValue; 
        Integer bucketSize;

        // Check buckets of size 10
        minValue = 0;
        maxValue = 100;
        bucketSize = 10;

        // <= min, >=max
        assertEquals(Buckets.getMapIdx(0.0, minValue, maxValue, bucketSize), 0);
        assertEquals(Buckets.getMapIdx(-50.0, minValue, maxValue, bucketSize), 0);
        assertEquals(Buckets.getMapIdx(100.0, minValue, maxValue, bucketSize), 11);
        assertEquals(Buckets.getMapIdx(200.0, minValue, maxValue, bucketSize), 11);

        // Range (0, 10] --> bucket 1
        assertEquals(Buckets.getMapIdx(0.1, minValue, maxValue, bucketSize), 1);
        assertEquals(Buckets.getMapIdx(5.0, minValue, maxValue, bucketSize), 1);
        assertEquals(Buckets.getMapIdx(5.1, minValue, maxValue, bucketSize), 1);
        assertEquals(Buckets.getMapIdx(9.9, minValue, maxValue, bucketSize), 1);

        // Others
        assertEquals(Buckets.getMapIdx(10.0, minValue, maxValue, bucketSize), 2);
        assertEquals(Buckets.getMapIdx(47.0, minValue, maxValue, bucketSize), 5);
        assertEquals(Buckets.getMapIdx(99.0, minValue, maxValue, bucketSize), 10);

        // Check buckets of size 25
        minValue = 0;
        maxValue = 100;
        bucketSize = 25;

        assertEquals(Buckets.getMapIdx(0.1, minValue, maxValue, bucketSize), 1);
        assertEquals(Buckets.getMapIdx(10.0, minValue, maxValue, bucketSize), 1);
        assertEquals(Buckets.getMapIdx(47.0, minValue, maxValue, bucketSize), 2);
        assertEquals(Buckets.getMapIdx(99.0, minValue, maxValue, bucketSize), 4);

        assertEquals(Buckets.getMapIdx(100.0, minValue, maxValue, bucketSize), 5);
        assertEquals(Buckets.getMapIdx(101.0, minValue, maxValue, bucketSize), 5);

        // Check buckets of size 1
        minValue = 0;
        maxValue = 100;
        bucketSize = 1;

        assertEquals(Buckets.getMapIdx(0.1, minValue, maxValue, bucketSize), 1);
        assertEquals(Buckets.getMapIdx(9.9, minValue, maxValue, bucketSize), 10);
        assertEquals(Buckets.getMapIdx(10.0, minValue, maxValue, bucketSize), 11);
        assertEquals(Buckets.getMapIdx(47.0, minValue, maxValue, bucketSize), 48);
        assertEquals(Buckets.getMapIdx(99.0, minValue, maxValue, bucketSize), 100);

        assertEquals(Buckets.getMapIdx(100.0, minValue, maxValue, bucketSize), 101);

        // Check maxValue, not a 10 divisor
        minValue = 0;
        maxValue = 33;
        bucketSize = 10;

        assertEquals(Buckets.getMapIdx(10.0, minValue, maxValue, bucketSize), 2);
        assertEquals(Buckets.getMapIdx(21.0, minValue, maxValue, bucketSize), 3);
        assertEquals(Buckets.getMapIdx(30.0, minValue, maxValue, bucketSize), 4);
        assertEquals(Buckets.getMapIdx(32.0, minValue, maxValue, bucketSize), 4);
        assertEquals(Buckets.getMapIdx(33.0, minValue, maxValue, bucketSize), 4);
        assertEquals(Buckets.getMapIdx(50.0, minValue, maxValue, bucketSize), 4);

         // Check bucketSize not a maxValue divisor
         minValue = 0;
         maxValue = 50;
         bucketSize = 7;

         assertEquals(Buckets.getMapIdx(0.0, minValue, maxValue, bucketSize), 0);
         assertEquals(Buckets.getMapIdx(0.5, minValue, maxValue, bucketSize), 1);
         assertEquals(Buckets.getMapIdx(7.0, minValue, maxValue, bucketSize), 2);
         assertEquals(Buckets.getMapIdx(29.0, minValue, maxValue, bucketSize), 5);
         assertEquals(Buckets.getMapIdx(49.0, minValue, maxValue, bucketSize), 8);
         assertEquals(Buckets.getMapIdx(50.0, minValue, maxValue, bucketSize), 8);
         assertEquals(Buckets.getMapIdx(100.0, minValue, maxValue, bucketSize), 8);

        // Check minValue being positive non 0
        minValue = 7;
        maxValue = 100;
        bucketSize = 10;

        assertEquals(Buckets.getMapIdx(7.0, minValue, maxValue, bucketSize), 0);
        assertEquals(Buckets.getMapIdx(-50.0, minValue, maxValue, bucketSize), 0);
        assertEquals(Buckets.getMapIdx(7.1, minValue, maxValue, bucketSize), 1);
        assertEquals(Buckets.getMapIdx(8.0, minValue, maxValue, bucketSize), 1);
        assertEquals(Buckets.getMapIdx(100.0, minValue, maxValue, bucketSize), 10);
        assertEquals(Buckets.getMapIdx(200.0, minValue, maxValue, bucketSize), 10);

        // Check minValue being negative non 0
        minValue = -10;
        maxValue = 100;
        bucketSize = 10;

        // <= min, >=max
        assertEquals(Buckets.getMapIdx(-10.0, minValue, maxValue, bucketSize), 0);
        assertEquals(Buckets.getMapIdx(-50.0, minValue, maxValue, bucketSize), 0);
        assertEquals(Buckets.getMapIdx(100.0, minValue, maxValue, bucketSize), 12);
        assertEquals(Buckets.getMapIdx(200.0, minValue, maxValue, bucketSize), 12);

        assertEquals(Buckets.getMapIdx(-9.9, minValue, maxValue, bucketSize), 1);
        assertEquals(Buckets.getMapIdx(-0.1, minValue, maxValue, bucketSize), 1);
        assertEquals(Buckets.getMapIdx(0.0, minValue, maxValue, bucketSize), 2);
        assertEquals(Buckets.getMapIdx(5.0, minValue, maxValue, bucketSize), 2);
        assertEquals(Buckets.getMapIdx(10.0, minValue, maxValue, bucketSize), 3);
        assertEquals(Buckets.getMapIdx(47.0, minValue, maxValue, bucketSize), 6);
        assertEquals(Buckets.getMapIdx(99.0, minValue, maxValue, bucketSize), 11);

        // Check more negatives values
        minValue = -10;
        maxValue = 100;
        bucketSize = 5;

        assertEquals(Buckets.getMapIdx(-10.0, minValue, maxValue, bucketSize), 0);
        assertEquals(Buckets.getMapIdx(-9.9, minValue, maxValue, bucketSize), 1);
        assertEquals(Buckets.getMapIdx(-5.1, minValue, maxValue, bucketSize), 1);
        assertEquals(Buckets.getMapIdx(-5.0, minValue, maxValue, bucketSize), 2);
        assertEquals(Buckets.getMapIdx(-4.9, minValue, maxValue, bucketSize), 2);
        assertEquals(Buckets.getMapIdx(-0.1, minValue, maxValue, bucketSize), 2);
        assertEquals(Buckets.getMapIdx(0.0, minValue, maxValue, bucketSize), 3);
    }
}
