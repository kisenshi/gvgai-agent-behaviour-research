/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package heuristic_diversification.mapelites;

import java.lang.reflect.Field;

import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues;

import heuristic_diversification.model.GameStats;

public enum Features {
    WINS(0, 100, 10, "win", true), 
    SCORE(0, 70, 10, "score", false), 
    EXPLORATION_PERCENTAGE(0, 100, 10, "percentageExplored", true),
    EXPLORATION_NUMBER(0, 206, 10, "nExplored", false);

    Integer minValue;
    Integer maxValue;
    Integer bucketSize;
    String statName;
    boolean percentage;

    Features(Integer min, Integer max, Integer bucketSize, String statName, boolean percentage) {
        this.minValue = min;
        this.maxValue = max;
        this.bucketSize = bucketSize;
        this.statName = statName;
    }

    public Integer featureArraySize() {
        // The size of the array of the feature is the max id plus one
        return Buckets.getMaxIdx(minValue, maxValue, bucketSize) + 1;
    }

    public double getFeatureValue(GameStats gameStats) {
        double featureValue = 0.0;

        try {

            Field statsField;
            statsField = GameStats.class.getField(statName + "Stats");
            StatisticalSummaryValues stats =  (StatisticalSummaryValues) statsField.get(gameStats);

            featureValue = stats.getMean();
            if(percentage) {
                // For stats in percentage, the result is in range [0, 1], we need to multiply by 100 to get the right bucket
                featureValue *= 100;
            }

        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) { 
            System.err.println("Exception retrieving " + statName + "Stats in gameStats");
            e.printStackTrace();
            System.exit(1);
        }

        return featureValue;
    }

    public int getBucketIdx(GameStats gameStats) {
        double featureStatsValue = getFeatureValue(gameStats);
        return Buckets.getMapIdx(featureStatsValue, minValue, maxValue, bucketSize);
    }
}
