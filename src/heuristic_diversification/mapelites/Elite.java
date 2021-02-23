/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package heuristic_diversification.mapelites;

import heuristic_diversification.model.GameStats;

/**
 * Class containing the information stored in each cell of the MAP-Elites
 * 
 * Returns the feature id and performance of the elite using its stats
 */
public class Elite {
    String agentName;
    Double heuristicsWeightList[];
    double performance;
    Double[] featureValues; 
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

    public void copyWeightsListValues(Double[] weightsList) {
        for (int i = 0; i < heuristicsWeightList.length; i++) {
            weightsList[i] = heuristicsWeightList[i];
        }
    }

    public void setDataForSerialisation(Performance performance, Features[] features) {
        this.performance = performance.getPerformanceValue(gameStats);
        this.featureValues = new Double[features.length];
        for (int i = 0; i < features.length; i++) {
            this.featureValues[i] = features[i].getFeatureValue(gameStats);
        }
    }

    public String printWeights() {
        return "[" + heuristicsWeightList[0] + ", " + heuristicsWeightList[1] + "]";
    }

    public void printInfo(String statsResultsFileName) {
        System.out.println("Agent: " + agentName);
        System.out.println("Weights: " + printWeights());
        System.out.println("Gametick: " + gameStats.gameOverTickStats.toString());
        System.out.println("Wins: " + gameStats.winStats.toString());
        System.out.println("Score: " + gameStats.scoreStats.toString());
        System.out.println("nExplored: " + gameStats.nExploredStats.toString());

        if (statsResultsFileName != null) {
            String resultsHeuristicFile = statsResultsFileName + "_" + heuristicsWeightList[0] + "_" + heuristicsWeightList[1] + ".txt";
            gameStats.printStats(resultsHeuristicFile);
        }
    }
}
