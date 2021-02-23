package heuristic_diversification;

import core.heuristic.StateHeuristic;
import heuristic_diversification.config.Agents;
import heuristic_diversification.config.Behaviours;
import heuristic_diversification.config.Games;
import heuristic_diversification.framework.ArcadeMachineHeuristic;
import heuristic_diversification.framework.TeamGameplay;
import heuristic_diversification.heuristics.TeamBehavioursHeuristic;
import heuristic_diversification.mapelites.Features;
import heuristic_diversification.mapelites.MapElites;
import heuristic_diversification.mapelites.Performance;
import heuristic_diversification.model.JSONManager;

public class MapElitesGameplay {
    // Game
    private static final Games GAME = Games.BUTTERFLIES;
    private static final int LEVEL = 0;
    
    // Agent
    private static final Agents AGENT = Agents.MCTS;

    // Game runs data
    private static final boolean VISUALS = true;
    private static final String ACTION_FILE = null;
    private static final int NUM_GAME_RUNS = 5; // 100

    // MAP elites configuration
    private static final Performance PERFORMANCE_CRITERIA = Performance.FAST;
    private static final Features FEATURE_X = Features.SCORE;
    private static final Features FEATURE_Y = Features.EXPLORATION_NUMBER;

    // Map elites initialisation and iterations
    private static final int NUM_INITIAL_CELLS = 1;
    private static final int NUM_MAPELITES_ITERATIONS = 1; // 1000

    private static String resultsFileName() {
        return "MAPElitesGameplay_" + GAME.getGameName() + "_lvl" + LEVEL + "_x_" + FEATURE_X.name() + "_y_" + FEATURE_Y.name() + "_perf_" + PERFORMANCE_CRITERIA.name() + "_" + NUM_GAME_RUNS + "_" + NUM_MAPELITES_ITERATIONS;
    }
    
    public static void main(String[] args) {
        // Initialisations needed for algorithm and running agents

        // Team initialisation
        String team = Behaviours.HEURISTICS_PATH + "TeamBehavioursHeuristic";

        int nHeuristics = Behaviours.values().length;
        StateHeuristic heuristicsList[] = new StateHeuristic[nHeuristics];
        Double heuristicsWeightList[] = new Double[nHeuristics];

        for (Behaviours info : Behaviours.values()) {
            heuristicsList[info.id()] = info.getHeuristicInstance();
        }

        Class[] heuristicArgsClass = new Class[]{heuristicsList.getClass(), heuristicsWeightList.getClass()};
        Object[] constructorArgs = new Object[]{heuristicsList, heuristicsWeightList};
        TeamBehavioursHeuristic teamBehaviouHeuristic = (TeamBehavioursHeuristic) ArcadeMachineHeuristic.createHeuristicWithArgs(team, heuristicArgsClass, constructorArgs);

        TeamGameplay gameplayFramework = new TeamGameplay(teamBehaviouHeuristic, GAME, LEVEL, ACTION_FILE, VISUALS, NUM_GAME_RUNS);

        // MAP elites adaptation

        // Initialise MAP 
        MapElites mapElites = new MapElites(PERFORMANCE_CRITERIA, FEATURE_X, FEATURE_Y, gameplayFramework, AGENT.getAgentFileName(), heuristicsWeightList, NUM_INITIAL_CELLS);

        // MAP elites algorithm
        mapElites.runAlgorithm(NUM_MAPELITES_ITERATIONS);

        // Print results
        mapElites.printMapElitesInfo();
        
        JSONManager.saveMAP(mapElites, resultsFileName());
    }
}
