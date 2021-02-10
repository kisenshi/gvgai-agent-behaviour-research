/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package heuristic_diversification.mapelites;

import heuristic_diversification.helper.GameStats;

/**
 * Class containing the information stored in each cell of the MAP-Elites
 * 
 * Returns the feature id and performance of the elite using its stats
 */
public class Elite {
    String agentName;
    Double heuristicsWeightList[];
    GameStats gameStats;

    public Elite(String agentName, final Double weightList[], GameStats gameStats) {
        this.agentName = agentName;
        this.heuristicsWeightList = weightList.clone();
        this.gameStats = gameStats;
    }

    public int getFeatureIdx(Features feature) {
        return feature.getBucketIdx(gameStats);
    }

    public double getPerformance(Performance performance) {
        return performance.getPerformanceValue(gameStats);
    }
}
