package heuristic_diversification.test.mapelites;

import static org.junit.Assert.assertArrayEquals;
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
        assertEquals(0, Buckets.getMapIdx(0.0, minValue, maxValue, bucketSize));
        assertEquals(0, Buckets.getMapIdx(0.1, minValue, maxValue, bucketSize));
        assertEquals(0, Buckets.getMapIdx(-50.0, minValue, maxValue, bucketSize));
        assertEquals(11, Buckets.getMapIdx(100.0, minValue, maxValue, bucketSize));
        assertEquals(11, Buckets.getMapIdx(200.0, minValue, maxValue, bucketSize));
        assertEquals(11, Buckets.getMapIdx(99.9, minValue, maxValue, bucketSize));

        // Range [1, 10] --> bucket 1
        assertEquals(1, Buckets.getMapIdx(5.0, minValue, maxValue, bucketSize));
        assertEquals(1, Buckets.getMapIdx(5.1, minValue, maxValue, bucketSize));
        assertEquals(1, Buckets.getMapIdx(5.9, minValue, maxValue, bucketSize));
        assertEquals(1, Buckets.getMapIdx(9.9, minValue, maxValue, bucketSize));
        assertEquals(1, Buckets.getMapIdx(10.0, minValue, maxValue, bucketSize));
        assertEquals(1, Buckets.getMapIdx(10.4, minValue, maxValue, bucketSize));

        // Others
        assertEquals(4, Buckets.getMapIdx(40.0, minValue, maxValue, bucketSize));
        assertEquals(5, Buckets.getMapIdx(47.0, minValue, maxValue, bucketSize));
        assertEquals(5, Buckets.getMapIdx(50.0, minValue, maxValue, bucketSize));
        assertEquals(10, Buckets.getMapIdx(99.0, minValue, maxValue, bucketSize));

        // Check buckets of size 25
        minValue = 0;
        maxValue = 100;
        bucketSize = 25;

        assertEquals(0, Buckets.getMapIdx(0.1, minValue, maxValue, bucketSize));
        assertEquals(1, Buckets.getMapIdx(10.0, minValue, maxValue, bucketSize));
        assertEquals(2, Buckets.getMapIdx(47.0, minValue, maxValue, bucketSize));
        assertEquals(4, Buckets.getMapIdx(99.0, minValue, maxValue, bucketSize));

        assertEquals(5, Buckets.getMapIdx(100.0, minValue, maxValue, bucketSize));
        assertEquals(5, Buckets.getMapIdx(101.0, minValue, maxValue, bucketSize));

        // Check buckets of size 1
        minValue = 0;
        maxValue = 10;
        bucketSize = 1;

        assertEquals(0, Buckets.getMapIdx(0.1, minValue, maxValue, bucketSize));
        assertEquals(1, Buckets.getMapIdx(1.0, minValue, maxValue, bucketSize));
        assertEquals(5, Buckets.getMapIdx(4.7, minValue, maxValue, bucketSize));
        assertEquals(9, Buckets.getMapIdx(9.0, minValue, maxValue, bucketSize));
        assertEquals(10, Buckets.getMapIdx(9.9, minValue, maxValue, bucketSize));
        assertEquals(10, Buckets.getMapIdx(100.0, minValue, maxValue, bucketSize));

        // Check maxValue, not a 10 divisor
        minValue = 5;
        maxValue = 33;
        bucketSize = 10;

        assertEquals(0, Buckets.getMapIdx(3.0, minValue, maxValue, bucketSize));
        assertEquals(0, Buckets.getMapIdx(5.0, minValue, maxValue, bucketSize));
        assertEquals(1, Buckets.getMapIdx(10.0, minValue, maxValue, bucketSize));
        assertEquals(2, Buckets.getMapIdx(16.0, minValue, maxValue, bucketSize));
        assertEquals(2, Buckets.getMapIdx(21.0, minValue, maxValue, bucketSize));
        assertEquals(3, Buckets.getMapIdx(29.0, minValue, maxValue, bucketSize));
        assertEquals(3, Buckets.getMapIdx(30.0, minValue, maxValue, bucketSize));
        assertEquals(3, Buckets.getMapIdx(32.0, minValue, maxValue, bucketSize));
        assertEquals(4, Buckets.getMapIdx(33.0, minValue, maxValue, bucketSize));
        assertEquals(4, Buckets.getMapIdx(50.0, minValue, maxValue, bucketSize));

        // Check bucketSize not a maxValue divisor
        minValue = 0;
        maxValue = 50;
        bucketSize = 7;

        assertEquals(0, Buckets.getMapIdx(0.0, minValue, maxValue, bucketSize));
        assertEquals(1, Buckets.getMapIdx(0.5, minValue, maxValue, bucketSize));
        assertEquals(1, Buckets.getMapIdx(7.0, minValue, maxValue, bucketSize));
        assertEquals(2, Buckets.getMapIdx(8.0, minValue, maxValue, bucketSize));
        assertEquals(5, Buckets.getMapIdx(29.0, minValue, maxValue, bucketSize));
        assertEquals(7, Buckets.getMapIdx(49.0, minValue, maxValue, bucketSize));
        assertEquals(8, Buckets.getMapIdx(50.0, minValue, maxValue, bucketSize));
        assertEquals(8, Buckets.getMapIdx(100.0, minValue, maxValue, bucketSize));

        // Check minValue being negative non 0
        minValue = -10;
        maxValue = 100;
        bucketSize = 10;

        // <= min, >=max
        assertEquals(0, Buckets.getMapIdx(-9.9, minValue, maxValue, bucketSize));
        assertEquals(0, Buckets.getMapIdx(-10.0, minValue, maxValue, bucketSize));
        assertEquals(0, Buckets.getMapIdx(-50.0, minValue, maxValue, bucketSize));
        assertEquals(12, Buckets.getMapIdx(100.0, minValue, maxValue, bucketSize));
        assertEquals(12, Buckets.getMapIdx(200.0, minValue, maxValue, bucketSize));

        assertEquals(1, Buckets.getMapIdx(-5.0, minValue, maxValue, bucketSize));
        assertEquals(1, Buckets.getMapIdx(-0.1, minValue, maxValue, bucketSize));
        assertEquals(1, Buckets.getMapIdx(0.0, minValue, maxValue, bucketSize));
        assertEquals(2, Buckets.getMapIdx(1.0, minValue, maxValue, bucketSize));
        assertEquals(2, Buckets.getMapIdx(5.0, minValue, maxValue, bucketSize));
        assertEquals(2, Buckets.getMapIdx(10.0, minValue, maxValue, bucketSize));
        assertEquals(6, Buckets.getMapIdx(47.0, minValue, maxValue, bucketSize));
        assertEquals(10, Buckets.getMapIdx(90.0, minValue, maxValue, bucketSize));
        assertEquals(11, Buckets.getMapIdx(91.0, minValue, maxValue, bucketSize));
        assertEquals(11, Buckets.getMapIdx(99.0, minValue, maxValue, bucketSize));
    }

    @Test
    public void testgetMapNBuckets() {
        Integer maxValue; 
        Integer minValue; 
        Integer bucketSize;

        minValue = 0;
        maxValue = 100;
        bucketSize = 10;
        
        assertEquals(12, Buckets.getMapNBuckets(minValue, maxValue, bucketSize));

        minValue = 0;
        maxValue = 100;
        bucketSize = 25;

        assertEquals(6, Buckets.getMapNBuckets(minValue, maxValue, bucketSize));

        minValue = 0;
        maxValue = 10;
        bucketSize = 1;

        assertEquals(11, Buckets.getMapNBuckets(minValue, maxValue, bucketSize));

        minValue = 5;
        maxValue = 33;
        bucketSize = 10;

        assertEquals(5, Buckets.getMapNBuckets(minValue, maxValue, bucketSize));

        minValue = 0;
        maxValue = 50;
        bucketSize = 7;

        assertEquals(9, Buckets.getMapNBuckets(minValue, maxValue, bucketSize));

        minValue = -10;
        maxValue = 100;
        bucketSize = 10;

        assertEquals(13, Buckets.getMapNBuckets(minValue, maxValue, bucketSize));
    }

    @Test
    public void testgetMapRangesInfo() {
        Integer maxValue; 
        Integer minValue; 
        Integer bucketSize;

        minValue = 0;
        maxValue = 100;
        bucketSize = 10;
        String[] expected = new String[] {
            "[<=0]",
            "[1 - 10]",
            "[11 - 20]",
            "[21 - 30]",
            "[31 - 40]",
            "[41 - 50]",
            "[51 - 60]",
            "[61 - 70]",
            "[71 - 80]",
            "[81 - 90]",
            "[91 - 99]",
            "[>=100]",
        };

        assertArrayEquals(expected, Buckets.getMapRangesInfo(minValue, maxValue, bucketSize));
    
        minValue = 5;
        maxValue = 33;
        bucketSize = 10;
        expected = new String[] {
            "[<=5]",
            "[6 - 15]",
            "[16 - 25]",
            "[26 - 32]",
            "[>=33]",
        };

        assertArrayEquals(expected, Buckets.getMapRangesInfo(minValue, maxValue, bucketSize));

        minValue = 0;
        maxValue = 10;
        bucketSize = 1;
        expected = new String[] {
            "[<=0]",
            "[1]",
            "[2]",
            "[3]",
            "[4]",
            "[5]",
            "[6]",
            "[7]",
            "[8]",
            "[9]",
            "[>=10]",
        };

        assertArrayEquals(expected, Buckets.getMapRangesInfo(minValue, maxValue, bucketSize));

        minValue = 0;
        maxValue = 100;
        bucketSize = 25;
        expected = new String[] {
            "[<=0]",
            "[1 - 25]",
            "[26 - 50]",
            "[51 - 75]",
            "[76 - 99]",
            "[>=100]",
        };

        assertArrayEquals(expected, Buckets.getMapRangesInfo(minValue, maxValue, bucketSize));

        minValue = 0;
        maxValue = 50;
        bucketSize = 7;
        expected = new String[] {
            "[<=0]",
            "[1 - 7]",
            "[8 - 14]",
            "[15 - 21]",
            "[22 - 28]",
            "[29 - 35]",
            "[36 - 42]",
            "[43 - 49]",
            "[>=50]",
        };

        assertArrayEquals(expected, Buckets.getMapRangesInfo(minValue, maxValue, bucketSize));
    }
}
