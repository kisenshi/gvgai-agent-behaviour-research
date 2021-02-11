/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package heuristic_diversification.mapelites;

import java.util.ArrayList;
import java.util.Random;

import core.heuristic.StateHeuristic;
import heuristic_diversification.config.Agents;
import heuristic_diversification.config.Behaviours;
import heuristic_diversification.helper.ArcadeMachineHeuristic;
import heuristic_diversification.helper.GameStats;
import heuristic_diversification.heuristics.TeamBehavioursHeuristic;
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
    // Games
    private static final String GAMES_LIST = "examples/games_heuristic_experiments.csv";

    // Game runs data
    private static final boolean VISUALS = false;
    private static final int NUM_GAME_RUNS = 5;
    private static final String ACTION_FILE = null;
    private static final int NUM_INITIAL_CELLS = 5;

    // Map elites config
    private static final Performance PERFORMANCE_CRITERIA = Performance.FAST;
    private static final Features FEATURE_X = Features.SCORE;
    private static final Features FEATURE_Y = Features.EXPLORATION_NUMBER;
    private static final int NUM_MAPELITES_ITERATIONS = 10;

    private Elite[][] mapElites;
    private ArrayList<EliteIdx> occupiedCellsIdx;

    public class EliteIdx {
        int x;
        int y;

        EliteIdx(int featureX, int featureY) {
            this.x = featureX;
            this.y = featureY;
        }
    }

    public MapElites() {
        // 0 --> score, 1 --> n spots explored
        mapElites = new Elite[FEATURE_X.featureArraySize()][FEATURE_Y.featureArraySize()];
        occupiedCellsIdx = new ArrayList<EliteIdx>();
    }

    public Elite getRandomEliteFromMap() {
        Random randomGenerator = new Random();
        int randomId = randomGenerator.nextInt(occupiedCellsIdx.size());
        EliteIdx eliteIdx = occupiedCellsIdx.get(randomId);

        return mapElites[eliteIdx.x][eliteIdx.y];
    }

    private void evolHeuristicsWeights(Double[] heuristicsWeightList) {
        // todo(kisenshi): evol weights based on current ones
    }
    
    public void addEliteToCell(Elite elite) {
        int featureX = elite.getFeatureIdx(FEATURE_X);
        int featureY = elite.getFeatureIdx(FEATURE_Y);

        Elite currentElite = mapElites[featureX][featureY];

        if (currentElite == null) {
            System.out.println("Free cell");
            mapElites[featureX][featureY] = elite;
            occupiedCellsIdx.add(new EliteIdx(featureX, featureY));
        } else {
            System.out.println("Cell occupied, compare performances: " + elite.getPerformance(PERFORMANCE_CRITERIA) + " vs " + currentElite.getPerformance(PERFORMANCE_CRITERIA));
            // substitute the current elite only if thew new one has better performance
            if (Double.compare(elite.getPerformance(PERFORMANCE_CRITERIA), currentElite.getPerformance(PERFORMANCE_CRITERIA)) > 0) {
                System.out.println("Better elite, replace");
                mapElites[featureX][featureY] = elite;
            }
        } 

        System.out.println("Occupied cells");
        for (EliteIdx cellIdx : occupiedCellsIdx) {
            System.out.println("(" + cellIdx.x + " , " + cellIdx.y + ")");
        }
    }

    // todo(kisenshi): move to another class
    public GameStats getGameStats(String agentName, StateHeuristic heuristic, String game, String level) {
        GameStats gameStats = new GameStats();
        ArcadeMachineHeuristic.runGameAndGetStats(gameStats, game, level, VISUALS, agentName, ACTION_FILE, heuristic,
                NUM_GAME_RUNS);
        gameStats.calculateStats();
        return gameStats;
    }

    public static void main(String[] args) {
        // Initialisations needed for algorithm and running agents
        // todo(kisenshi): this does not belong here, move to another class after tests

        // Game and level to play
        String[][] games = Utils.readGames(GAMES_LIST);
        int gameIdx = 2;
        int levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
        String gameName = games[gameIdx][1];

        String game = games[gameIdx][0];
        String level = game.replace(gameName, gameName + "_lvl" + levelIdx);

        // Agent and heuristic
        String team = Behaviours.HEURISTICS_PATH + "TeamBehavioursHeuristic";
        String controller = Agents.MCTS.getAgentName();

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
        MapElites mapElites = new MapElites();

        // Initialise map: fill some cells of the map with random values
        // todo(kisenshi): randomise values
        double[] initWeights = {0.0, 0.1, 0.15, 0.25, 0.50, 0.75, 0.85, 0.90, 1};
        for (double weight : initWeights) {
            heuristicsWeightList[Behaviours.WINNER.id()] = weight;
            heuristicsWeightList[Behaviours.EXPLORER.id()] = (1 - weight);

            teamBehaviouHeuristic.setHeuristicsWeights(heuristicsWeightList);

            GameStats gameStats = mapElites.getGameStats(controller, teamBehaviouHeuristic, game, level);
            //String resultsHeuristicFile = "Stats_" + gameName + "_" + weight + ".txt";
            //gameStats.printStats(resultsHeuristicFile);

            Elite elite = new Elite(controller, heuristicsWeightList, gameStats);
            mapElites.addEliteToCell(elite);
        }

        // MAP elites algorithm
        int nIterations = 0;
        while(nIterations < NUM_MAPELITES_ITERATIONS) {
            System.out.println("MAPELites algorithm iteration " + nIterations);
            
            // get random cell elite and its weights
            Elite randomElite = mapElites.getRandomEliteFromMap();
            heuristicsWeightList = randomElite.getWeightsClone();

            // evol weights to create new "elite"
            mapElites.evolHeuristicsWeights(heuristicsWeightList);

            // set new heuristic weights in agent
            teamBehaviouHeuristic.setHeuristicsWeights(heuristicsWeightList);

            // get game stats
            GameStats gameStats = mapElites.getGameStats(controller, teamBehaviouHeuristic, game, level);

            // replace cell with new elite if better performance
            Elite newElite = new Elite(controller, heuristicsWeightList, gameStats);
            mapElites.addEliteToCell(newElite);

            nIterations++;
        }
        
        // Save MAP info --> JSON
    }
}