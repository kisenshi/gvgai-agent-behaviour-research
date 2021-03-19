/**
 * Author: Cristina Guerrero
 * Date: 28th January 2021
 */

package heuristic_diversification;

import java.util.Random;

import heuristic_diversification.framework.ArcadeMachineHeuristic;
import heuristic_diversification.framework.TeamManager;
import heuristic_diversification.heuristics.TeamBehavioursHeuristic;
import heuristic_diversification.model.GameStats;
import tools.Utils;

public class mapElitesTest {

    public static void main(String[] args) {
        // PATHS
        String controllersPath = "heuristic_diversification.controllers.";
        String heuristicsPath = "heuristic_diversification.heuristics.";

		// Available controllers:
		String sampleOneStepController = controllersPath + "sampleonesteplookahead.Agent";
        String sampleMCTS = controllersPath + "sampleMCTS.Agent";

        // Main heuristic
        String team = heuristicsPath + "TeamBehavioursHeuristic";
        
        //Available games
        //String gamesList =  "examples/games_map_elites.csv";
        String gamesList =  "examples/games_heuristic_experiments.csv";
        String[][] games = Utils.readGames(gamesList);
        
        String controller = sampleMCTS;

        // Team set up
        Double heuristicsWeightList[] = TeamManager.createTeamBehaviourWeightList();
        TeamBehavioursHeuristic teamBehaviouHeuristic = TeamManager.createTeamBehaviourHeuristic(heuristicsWeightList);

        // Weights set up
        heuristicsWeightList[0] = 0.10;  // Behaviours.WINNER
        heuristicsWeightList[1] = 0.10;  // Behaviours.EXPLORER
        heuristicsWeightList[2] = 0.20; // Behaviours.CURIOUS
        heuristicsWeightList[3] = 0.30;  // Behaviours.KILLER
        heuristicsWeightList[4] = 0.30; // Behaviours.COLLECTOR

        //Game settings
		boolean visuals = true;
        int seed = new Random().nextInt();
        
        // Log files
        String recordActionsFile = null;
        String actionFile = null;

        // Game and level to play
		int gameIdx = 0;
		int levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];
        String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
        
        boolean oneGame = true;
        if (oneGame){
            String resultsHeuristicFile = "Stats_" + gameName + "_10.txt";
            int[] recordIds = new int[]{
                gameIdx,
                0,
            };

            //ArcadeMachineHeuristic.runOneGameUsingHeuristic(game, level1, visuals, controller, actionFile, seed, teamBehaviouHeuristic, resultsHeuristicFile, recordIds);
            GameStats gameStats = new GameStats(levelIdx);
            ArcadeMachineHeuristic.runGameAndGetStats(gameStats, game, level1, visuals, controller, actionFile, teamBehaviouHeuristic, 5);
            gameStats.calculateStats();
            gameStats.printStats(resultsHeuristicFile);
        } else {
            int n_games = 20;
            for (int gameId = 0; gameId < n_games; gameId++) {
                seed = new Random().nextInt();

                levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
                gameName = games[gameId][1];
                game = games[gameId][0];
                level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);

                String resultsHeuristicFile = "TestTeamBehaviourHeuristic_" + gameName + ".txt";

                // As the data is appended at the end of the file, it is needed to store the game and controllers id
                int[] recordIds = new int[]{
                        gameId,
                        0,
                };

                // 2. This plays a game in a level by the controller.
                ArcadeMachineHeuristic.runOneGameUsingHeuristic(game, level1, visuals, controller, actionFile, seed, teamBehaviouHeuristic, resultsHeuristicFile, recordIds);
            }
        }
    }
}
