package heuristic_diversification.heuristics;

import java.awt.Graphics2D;

import core.game.Game;
import core.game.StateObservation;
import core.heuristic.StateHeuristic;
import heuristic_diversification.model.GameStats;

public class TeamBehavioursHeuristic extends StateHeuristic {
    private StateHeuristic[] mHeuristics;
    private Double[] mHeuristicsWeights;
    private KnowledgeHeuristic knowledgeHeuristic;

    public TeamBehavioursHeuristic(StateHeuristic[] heuristics, Double[] weights) {
        // We assume the number of heuristics and their weights are the same and
        // provided in the same order
        mHeuristics = heuristics;
        mHeuristicsWeights = weights;

        // The team heuristic is in charge of keeping the SpritesData up to date so
        // those heuristics that extend from KnowledgeHeuristic have the data available
        knowledgeHeuristic = new KnowledgeHeuristic();
        knowledgeHeuristic.setUpdateSpritesData();
    }

    @Override
    public void initHeuristicInternalInformation(StateObservation stateObs) {
        knowledgeHeuristic.initHeuristicInternalInformation(stateObs);

        for (StateHeuristic heuristic : mHeuristics) {
            heuristic.initHeuristicInternalInformation(stateObs);
        }
    }

    @Override
    public void updateHeuristicInternalInformation(StateObservation stateObs) {
        knowledgeHeuristic.updateHeuristicInternalInformation(stateObs);

        for (StateHeuristic heuristic : mHeuristics) {
            heuristic.updateHeuristicInternalInformation(stateObs);
        }
    }

    @Override
    public double evaluateState(StateObservation stateObs) {
        double finalH = 0;
        
        if(DEBUG) {
            System.out.println("Evaluation...");
        }

        if (!checkHeuristicsArraySize()) {
            // Avoid error in the iteration
            return 0;
        }

        for (int i = 0; i < mHeuristics.length; i++) {
            StateHeuristic heuristic = mHeuristics[i];
            double h = heuristic.evaluateState(stateObs);
            double hNorm = heuristic.normaliseHeuristic(h);

            // add the heuristic value based on its weight to the final result
            double hWeight = mHeuristicsWeights[i];
            finalH += (hNorm * hWeight);

            if (DEBUG) {
                System.out.println(h + " --> " + hNorm + " w: " + hWeight + " --> " + (hNorm * hWeight) + " --> finalH: " + finalH);
            }
        }

        return finalH;
    }

    @Override
    public void restartFutureStateData(StateObservation stateObs) {
        knowledgeHeuristic.restartFutureStateData(stateObs);

        for (StateHeuristic heuristic : mHeuristics) {
            heuristic.restartFutureStateData(stateObs);
        }
    }

    @Override
    public void updateFutureStateData(StateObservation stateObs) {
        knowledgeHeuristic.updateFutureStateData(stateObs);

        for (StateHeuristic heuristic : mHeuristics) {
            heuristic.updateFutureStateData(stateObs);
        }
    }

    @Override
    public String relevantInfoStr(StateObservation stateObs) {
        String s = "";
        for (StateHeuristic heuristic : mHeuristics) {
            s.concat(heuristic.relevantInfoStr(stateObs) + "\n");
        }
        return s;
    }

    @Override
    public void recordGameStats(Game game, GameStats gameStats) {
        gameStats.addGeneralData(game.getGameTick());
        for (StateHeuristic heuristic : mHeuristics) {
            heuristic.recordGameStats(game, gameStats);
        }
    }

    @Override
    public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds) {
        for (StateHeuristic heuristic : mHeuristics) {
            heuristic.recordDataOnFile(played, fileName, randomSeed, recordIds);
        }
    }

    @Override
    public void drawInScreen(Graphics2D g) {
        for (StateHeuristic heuristic : mHeuristics) {
            heuristic.drawInScreen(g);
        }
    }

    private boolean checkHeuristicsArraySize() {
        return (mHeuristics.length == mHeuristicsWeights.length);
    }
}
