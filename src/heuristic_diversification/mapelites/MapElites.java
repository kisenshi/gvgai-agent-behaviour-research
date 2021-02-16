/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package heuristic_diversification.mapelites;

import java.util.ArrayList;

import core.heuristic.StateHeuristic;
import heuristic_diversification.config.Agents;
import heuristic_diversification.config.Behaviours;
import heuristic_diversification.config.Games;
import heuristic_diversification.framework.ArcadeMachineHeuristic;
import heuristic_diversification.heuristics.TeamBehavioursHeuristic;
import heuristic_diversification.model.GameStats;
import tools.Utils;

/**
 * Contains the definition of the MAP-Elites as well as the methos required to
 * obtain the performance and features of a certain agent-heuristics
 * combination.
 * 
 * For the MAP-Elites algorithm adaptation for automatic gameplay agents, the
 * performance and features information is retrieved from the stats of the game.
 * The specifics of the elements to consider for these will depend on the
 * configuration set when running the algorithm
 */
public class MapElites {
    // TODO(kisenshi): generalise and make it configurable
    // Game and agent
    private static final Games GAME = Games.BUTTERFLIES;
    private static final int LEVEL = 0;
    private static final Agents AGENT = Agents.MCTS;

    // Game runs data
    private static final boolean VISUALS = true;
    private static final String ACTION_FILE = null;
    private static final int NUM_GAME_RUNS = 10;

    // Map elites config
    private static final Performance PERFORMANCE_CRITERIA = Performance.FAST;
    private static final Features FEATURE_X = Features.SCORE;
    private static final Features FEATURE_Y = Features.EXPLORATION_NUMBER;
    private static final int NUM_INITIAL_CELLS = 5;
    private static final int NUM_MAPELITES_ITERATIONS = 50;

    private Elite[][] mapElites;
    private ArrayList<EliteIdx> occupiedCellsIdx;
    private Double heuristicsWeightList[];

    private TeamBehavioursHeuristic teamBehaviouHeuristic;
    private String controller;
    private String game;
    private String level;

    private class EliteIdx {
        int x;
        int y;

        EliteIdx(int featureX, int featureY) {
            this.x = featureX;
            this.y = featureY;
        }
    }

    public MapElites(TeamBehavioursHeuristic teamBehaviouHeuristic, Double heuristicsWeightList[], String controller, String game, String level) {
        mapElites = new Elite[FEATURE_X.featureArraySize()][FEATURE_Y.featureArraySize()];
        occupiedCellsIdx = new ArrayList<EliteIdx>();
        
        this.heuristicsWeightList = heuristicsWeightList;
        this.teamBehaviouHeuristic = teamBehaviouHeuristic;
        this.controller = controller;
        this.game = game;
        this.level = level;
    }

    /**
     * Initialise map. Fill nCells of the map with elites; initialised with random weights
     * @param nCells number of cells to fill in the map for initialisation
     */
    public void initialiseMap(int nCells) {
        int nCellsInitialised = 0;
        while (nCellsInitialised < nCells) {
            Generator.setRandomWeights(heuristicsWeightList);

            // Create elite from random values and add to map
            Elite elite = getGameplayElite();
            addEliteToMap(elite);

            nCellsInitialised = getNCellsOccupied();
        }

        System.out.println("MAP Elites initialised");
    }

    /**
     * MAP elites algorithm - iterate nIterations times
     * 1) get random elite from map
     * 2) evolve weights taking random elite as base
     * 3) create new elite (and get its stats to be able to get features and performance)
     * 4) add elite to map (assign to cell and replace elite in assigned cell if the new performance is better)
     * @param nIterations number of iterations of the map elites algorithm
     */
    public void runAlgorithm(int nTotalIterations) {
        int nIterations = 0;
        while(nIterations < nTotalIterations) {
            System.out.println("MAPELites algorithm iteration " + nIterations);
            
            // get random cell elite and a copy of its weights
            Elite randomElite = getRandomEliteFromMap();
            heuristicsWeightList = randomElite.getWeightListCopy();

            // evol weights
            evolveHeuristicsWeights(heuristicsWeightList);

            // Create new possible elite and add to map
            Elite newElite = getGameplayElite();
            addEliteToMap(newElite);

            nIterations++;
        }
    }

    private int getNCellsOccupied() {
        return occupiedCellsIdx.size();
    }

    private Elite getRandomEliteFromMap() {
        EliteIdx eliteIdx = (EliteIdx) Generator.getRandomElementFromArray(occupiedCellsIdx);
        return mapElites[eliteIdx.x][eliteIdx.y];
    }

    private void evolveHeuristicsWeights(Double[] heuristicsWeightList) {
        Generator.evolveWeightList(heuristicsWeightList);
    }
    
    private Elite getGameplayElite() {
        // Get the resulting game stats for current controller and weights
        GameStats gameStats = new GameStats();
        ArcadeMachineHeuristic.runGameAndGetStats(gameStats, game, level, VISUALS, controller, ACTION_FILE, teamBehaviouHeuristic, NUM_GAME_RUNS);
        gameStats.calculateStats();

        // Create elite with information and results
        return new Elite(controller, heuristicsWeightList, gameStats);
    }

    private void addEliteToMap(Elite elite) {
        int featureX = elite.getFeatureIdx(FEATURE_X);
        int featureY = elite.getFeatureIdx(FEATURE_Y);

        Elite currentElite = mapElites[featureX][featureY];

        System.out.println("New elite " + elite.printWeights() + " --> Cell ("+ featureX + ", " + featureY + ")");
        if (currentElite == null) {
            mapElites[featureX][featureY] = elite;
            occupiedCellsIdx.add(new EliteIdx(featureX, featureY));
        } else {
            System.out.println("Cell occupied by elite w/ weights: " + currentElite.printWeights()+ ". Performances: " + elite.getPerformance(PERFORMANCE_CRITERIA) + " vs " + currentElite.getPerformance(PERFORMANCE_CRITERIA));
            // substitute the current elite only if thew new one has better performance
            if (Double.compare(elite.getPerformance(PERFORMANCE_CRITERIA), currentElite.getPerformance(PERFORMANCE_CRITERIA)) > 0) {
                System.out.println("New elite has better performance; replace");
                mapElites[featureX][featureY] = elite;
            }
        }
    }

    public void printMapElitesInfo() {
        System.out.println("MAP Elites cells: ");
        for (int x = 0; x < mapElites.length; x++) {
            for (int y = 0; y < mapElites[x].length; y++) {
                if (mapElites[x][y] != null) {
                    System.out.print(" X ");
                } else {
                    System.out.print(" - ");
                }
            }
            System.out.println("\n");
        }
        System.out.println("MAP Elites cells info: ");
        for (int x = 0; x < mapElites.length; x++) {
            for (int y = 0; y < mapElites[x].length; y++) {
                if (mapElites[x][y] != null) {
                    System.out.println("-----------------------");
                    System.out.println("(" + x + ", " + y + ")");
                    Elite elite = mapElites[x][y];
                    elite.printInfo();
                }
            }
        }
    }

    public static void main(String[] args) {
        // Initialisations needed for algorithm and running agents
        // todo(kisenshi): this does not belong here, move to another class after tests

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

        // MAP elites adaptation

        // Initialise MAP 
        MapElites mapElites = new MapElites(teamBehaviouHeuristic, heuristicsWeightList, AGENT.getAgentName(), GAME.game(), GAME.level(LEVEL));
        mapElites.initialiseMap(NUM_INITIAL_CELLS);

        // MAP elites algorithm
        mapElites.runAlgorithm(NUM_MAPELITES_ITERATIONS);

        // Print results
        mapElites.printMapElitesInfo();
        
        // Save MAP info --> JSON
    }
}
