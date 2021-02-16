package heuristic_diversification;

import core.heuristic.StateHeuristic;
import heuristic_diversification.config.Agents;
import heuristic_diversification.config.Behaviours;
import heuristic_diversification.config.Games;
import heuristic_diversification.framework.ArcadeMachineHeuristic;
import heuristic_diversification.framework.TeamGameplay;
import heuristic_diversification.heuristics.TeamBehavioursHeuristic;
import heuristic_diversification.mapelites.MapElites;

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

    // Map elites initialisation and iterations
    private static final int NUM_INITIAL_CELLS = 5;
    private static final int NUM_MAPELITES_ITERATIONS = 5; // 1000
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

        TeamGameplay gameplayFramework = new TeamGameplay(teamBehaviouHeuristic, GAME.game(), GAME.level(LEVEL), ACTION_FILE, VISUALS, NUM_GAME_RUNS);

        // MAP elites adaptation

        // Initialise MAP 
        MapElites mapElites = new MapElites(gameplayFramework, AGENT.getAgentName(), heuristicsWeightList);
        mapElites.initialiseMap(NUM_INITIAL_CELLS);

        // MAP elites algorithm
        mapElites.runAlgorithm(NUM_MAPELITES_ITERATIONS);

        // Print results
        mapElites.printMapElitesInfo();
        
        // Save MAP info --> JSON
    }
}
