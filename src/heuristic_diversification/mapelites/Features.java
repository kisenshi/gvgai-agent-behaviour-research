/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package heuristic_diversification.mapelites;

import java.lang.reflect.Field;

import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues;

import heuristic_diversification.model.GameStats;

public enum Features {
    WINS("win", 0, 100, 10, true), 
    SCORE("score", 0, 70, 10, false), 
    EXPLORATION_PERCENTAGE("percentageExplored", 0, 100, 10, true),
    EXPLORATION_NUMBER("nExplored", 0, 206, 10, false),
    DISCOVERY("nSpritesDiscovered", 0, 20, 1, false),
    SPRITES_INTERACTION("nUniqueSpriteInteractions", 0, 20, 1, false),
    CURIOSITY("nCuriosityInteractions", 0, 400, 20, false),
    INTERACTIONS("nTotalInteractions", 0, 1000, 50, false),
    COLLISIONS("nTotalCollisions", 0, 1000, 50, false),
    HITS("nTotalHits", 0, 1000, 50, false),
    KILLS("nTotalKills", 0, 27, 3, false),
    ITEMS("nTotalItemsCollected", 0, 27, 3, false);

    String statName;
    Integer minValue;
    Integer maxValue;
    Integer bucketSize;
    boolean percentage;

    Features(String statName, Integer min, Integer max, Integer bucketSize, boolean percentage) {
        this.statName = statName;
        this.minValue = min;
        this.maxValue = max;
        this.bucketSize = bucketSize;
        this.percentage = percentage;
    }

    public Integer getFeatureMinValue() {
        return minValue;
    }

    public Integer getFeatureMaxValue() {
        return maxValue;
    }

    public Integer getFeatureBucketSize() {
        return bucketSize;
    }

    public boolean isPercentageFeature() {
        return percentage;
    }

    public Integer featureArraySize() {
        return Buckets.getMapNBuckets(minValue, maxValue, bucketSize);
    }

    public void calculateFeatureStat(GameStats gameStats) {
        gameStats.calculateFieldStats(statName);
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

    public String[] featureArrayInfo() {
        return Buckets.getMapRangesInfo(minValue, maxValue, bucketSize);
    }
}
