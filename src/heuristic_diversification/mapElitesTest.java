/**
 * Author: Cristina Guerrero
 * Date: 28th January 2021
 */

package heuristic_diversification;

import java.util.Random;

import core.heuristic.StateHeuristic;
import heuristic_diversification.helper.ArcadeMachineHeuristic;
import heuristic_diversification.config.Behaviours;
import heuristic_diversification.helper.GameStats;
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

        int nHeuristics = Behaviours.values().length;
        StateHeuristic heuristicsList[] = new StateHeuristic[nHeuristics];
        Double heuristicsWeightList[] = new Double[nHeuristics];

        for (Behaviours info : Behaviours.values()) {
            heuristicsList[info.id()] = info.getHeuristicInstance();
        }

        heuristicsWeightList[Behaviours.WINNER.id()] = 0.15;
        heuristicsWeightList[Behaviours.EXPLORER.id()] = 0.85;
    
        Class[] heuristicArgsClass = new Class[] { heuristicsList.getClass(), heuristicsWeightList.getClass() };
        Object[] constructorArgs = new Object[] { heuristicsList, heuristicsWeightList};
        StateHeuristic teamBehaviouHeuristic = ArcadeMachineHeuristic.createHeuristicWithArgs(team, heuristicArgsClass, constructorArgs);

        //Game settings
		boolean visuals = true;
        int seed = new Random().nextInt();
        
        // Log files
        String recordActionsFile = null;
        String actionFile = null;

        // Game and level to play
		int gameIdx = 2;
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
            GameStats gameStats = new GameStats();
            ArcadeMachineHeuristic.runGameAndGetStats(gameStats, game, level1, visuals, controller, actionFile, teamBehaviouHeuristic, 10);
            gameStats.printStats(resultsHeuristicFile);
            gameStats.calculateStats();
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
