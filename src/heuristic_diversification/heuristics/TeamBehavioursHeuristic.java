package heuristic_diversification.heuristics;

import java.awt.Graphics2D;

import core.game.Game;
import core.game.StateObservation;
import core.heuristic.StateHeuristic;
import heuristic_diversification.framework.TeamMember;
import heuristic_diversification.model.GameStats;

public class TeamBehavioursHeuristic extends StateHeuristic {
    private TeamMember[] mHeuristics;
    private Double[] mHeuristicsWeights;
    private KnowledgeHeuristic knowledgeHeuristic;

    public TeamBehavioursHeuristic(TeamMember[] heuristics, Double[] weights) {
        // The number of heuristics and their weights can be different, but the weights are assigned in the same order
        // to the enabled ones
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

        for (TeamMember teamMember : mHeuristics) {
            teamMember.heuristic.initHeuristicInternalInformation(stateObs);
        }
    }

    @Override
    public void updateHeuristicInternalInformation(StateObservation stateObs) {
        knowledgeHeuristic.updateHeuristicInternalInformation(stateObs);

        for (TeamMember teamMember : mHeuristics) {
            teamMember.heuristic.updateHeuristicInternalInformation(stateObs);
        }
    }

    @Override
    public double evaluateState(StateObservation stateObs) {
        double finalH = 0;
        
        if(DEBUG) {
            System.out.println("Evaluation...");
        }

        int weightIndex = 0;
        for (TeamMember teamMember : mHeuristics) {
            // Only take those that are enabled to obtain the final heuristic
            if (teamMember.isEnabled()) {
                double h = teamMember.heuristic.evaluateState(stateObs);
                double hNorm = teamMember.heuristic.normaliseHeuristic(h);

                // add the heuristic value based on its weight to the final result
                double hWeight = mHeuristicsWeights[weightIndex];
                finalH += (hNorm * hWeight);

                if (DEBUG) {
                    System.out.println(h + " --> " + hNorm + " w: " + hWeight + " --> " + (hNorm * hWeight) + " --> finalH: " + finalH);
                }

                weightIndex++;
            }
        }

        return finalH;
    }

    @Override
    public void restartFutureStateData(StateObservation stateObs) {
        knowledgeHeuristic.restartFutureStateData(stateObs);

        for (TeamMember teamMember : mHeuristics) {
            // The future state data is only necessary for evaluation, so if the team member is disabled
            // the future state won't be necessary.
            if (teamMember.isEnabled()) {
                teamMember.heuristic.restartFutureStateData(stateObs);
            }
        }
    }

    @Override
    public void updateFutureStateData(StateObservation stateObs) {
        knowledgeHeuristic.updateFutureStateData(stateObs);

        for (TeamMember teamMember : mHeuristics) {
            // The future state data is only necessary for evaluation, so if the team member is disabled
            // the future state won't be necessary.
            if (teamMember.isEnabled()) {
                teamMember.heuristic.updateFutureStateData(stateObs);
            }
        }
    }

    @Override
    public String relevantInfoStr(StateObservation stateObs) {
        String s = "";
        for (TeamMember teamMember : mHeuristics) {
            // The relevantInfoStr is helpful for future data debug, so if the team member is disabled
            // it won't be necessary.
            if (teamMember.isEnabled()) {
                s = s.concat("; "+teamMember.heuristic.relevantInfoStr(stateObs));
            }
        }
        return s;
    }

    @Override
    public void recordGameStats(Game game, GameStats gameStats) {
        gameStats.addGeneralData(game.getGameTick());
        for (TeamMember teamMember : mHeuristics) {
            teamMember.heuristic.recordGameStats(game, gameStats);
        }
    }

    @Override
    public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds) {
        for (TeamMember teamMember : mHeuristics) {
            teamMember.heuristic.recordDataOnFile(played, fileName, randomSeed, recordIds);
        }
    }

    @Override
    public void drawInScreen(Graphics2D g) {
        for (TeamMember teamMember : mHeuristics) {
            teamMember.heuristic.drawInScreen(g);
        }
    }
}
