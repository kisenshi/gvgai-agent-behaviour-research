/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package heuristic_diversification.mapelites;

import java.lang.reflect.Field;

import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues;

import heuristic_diversification.model.GameStats;

public enum Features {
    WINS(0, 100, 10, "win"), 
    SCORE(0, 100, 10, "score"), 
    EXPLORATION_PERCENTAGE(0, 100, 10, "nExplored"),
    EXPLORATION_NUMBER(0, 206, 10, "nExplored");

    Integer minValue;
    Integer maxValue;
    Integer bucketSize;
    String statName;

    Features(Integer min, Integer max, Integer bucketSize, String statName) {
        this.minValue = min;
        this.maxValue = max;
        this.bucketSize = bucketSize;
        this.statName = statName;
    }

    public Integer featureArraySize() {
        // The size of the array of the feature is the max id plus one
        return Buckets.getMaxIdx(minValue, maxValue, bucketSize) + 1;
    }

    public int getBucketIdx(GameStats gameStats) {

        int idx = 0; 

        try {

            Field statsField;
            statsField = GameStats.class.getField(statName + "Stats");
            StatisticalSummaryValues stats =  (StatisticalSummaryValues) statsField.get(gameStats);
            idx = Buckets.getMapIdx(stats.getMean(), minValue, maxValue, bucketSize);

        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) { 
            System.err.println("Exception retrieving " + statName + "Stats in gameStats");
            e.printStackTrace();
            System.exit(1);
        }

        return idx;
    }
}
