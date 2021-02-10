/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package heuristic_diversification.mapelites;

import java.util.ArrayList;

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

    // Map elites
    private static final int N_FEATURES = 2;

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
        mapElites = new Elite[Features.SCORE.featureArraySize()][Features.EXPLORATION_NUMBER.featureArraySize()];
        occupiedCellsIdx = new ArrayList<EliteIdx>();
    }
    
    public void addEliteToCell(Elite elite) {
        int featureX = elite.getFeatureIdx(Features.SCORE);
        int featureY = elite.getFeatureIdx(Features.EXPLORATION_NUMBER);

        Elite currentElite = mapElites[featureX][featureY];

        if (currentElite == null) {
            System.out.println("Free cell");
            mapElites[featureX][featureY] = elite;
            occupiedCellsIdx.add(new EliteIdx(featureX, featureY));
        } else {
            System.out.println("Cell occupied, compare performances: " + elite.getPerformance(Performance.FAST) + " vs " + currentElite.getPerformance(Performance.FAST));
            // substitute the current elite only if thew new one has better performance
            if (Double.compare(elite.getPerformance(Performance.FAST), currentElite.getPerformance(Performance.FAST)) > 0) {
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
            // agent + heuristic weights
            // get game stats
            // get features buckets (index) for agent
            // compare performance for agent in MAP for those features
                // replace cell with agent if better performance
        
        // Save MAP info --> JSON
    }
}
