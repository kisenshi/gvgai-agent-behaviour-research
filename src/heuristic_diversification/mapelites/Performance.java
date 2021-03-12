/**
 * Author: Cristina Guerrero
 * Date: 10th February 2021
 */

package heuristic_diversification.mapelites;

import java.lang.reflect.Field;

import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues;

import heuristic_diversification.model.GameStats;

public enum Performance {
    FAST("gameOverTick", true),
    SLOW("gameOverTick", false);

    String statName;
    boolean invertedStat; // If true, a lower value is better

    Performance(String statName, boolean invertedStat) {
        this.statName = statName;
        this.invertedStat = invertedStat;
    }

    public void calculatePerfomanceStat(GameStats gameStats) {
        gameStats.calculateFieldStats(statName);
    }
 
    public double getPerformanceValue(GameStats gameStats) {
        double performance = 0.0;

        try {

            Field statsField;
            statsField = GameStats.class.getField(statName + "Stats");
            StatisticalSummaryValues stats =  (StatisticalSummaryValues) statsField.get(gameStats);
            performance = stats.getMean();

        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) { 
            System.err.println("Exception retrieving " + statName + "Stats in gameStats");
            e.printStackTrace();
            System.exit(1);
        }

        if (invertedStat) {
            return (-1) * performance;
        }

        return performance;
    }
}
