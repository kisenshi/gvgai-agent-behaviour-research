/**
 * Author: Cristina Guerrero
 * Date: 28th January 2021
 */

package heuristic_diversification;

import tools.Utils;
import tracks.ArcadeMachine;

import java.util.Random;
import java.util.ArrayList;

import core.heuristic.StateHeuristic;
import heuristic_diversification.heuristics.ExplorationHeuristic;

public class mapElites {

    public static void main(String[] args) {
        // PATHS
        String controllersPath = "heuristic_diversification.controllers.";
        String heuristicsPath = "heuristic_diversification.heuristics.";

		// Available controllers:
		String sampleOneStepController = controllersPath + "sampleonesteplookahead.Agent";
        String sampleMCTS = controllersPath + "sampleMCTS.Agent";

        // Available heuristics
        String winningAndScore = heuristicsPath + "WinningAndScoreHeuristic";
        String exploration = heuristicsPath + "ExplorationHeuristic";
        String team = heuristicsPath + "TeamBehavioursHeuristic";
        
        //Available games
        //String gamesList =  "examples/games_map_elites.csv";
        String gamesList =  "examples/games_heuristic_experiments.csv";
        String[][] games = Utils.readGames(gamesList);
        
        String controller = sampleMCTS;

        StateHeuristic winningAndScoreHeuristic = ArcadeMachine.createHeuristic(winningAndScore);
        StateHeuristic explorationHeuristic = ArcadeMachine.createHeuristic(exploration);

        ArrayList<StateHeuristic> heuristicsList = new ArrayList<StateHeuristic>();
        heuristicsList.add(winningAndScoreHeuristic);
        heuristicsList.add(explorationHeuristic);

        ArrayList<Double> heuristicsWeightList = new ArrayList<Double>();
        heuristicsWeightList.add(0.35);
        heuristicsWeightList.add(0.65);

        Class[] heuristicArgsClass = new Class[] { ArrayList.class, ArrayList.class };
        Object[] constructorArgs = new Object[] { heuristicsList, heuristicsWeightList};
        StateHeuristic teamBehaviouHeuristic = ArcadeMachine.createHeuristicWithArgs(team, heuristicArgsClass, constructorArgs);

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
        
        if (false){
            String resultsHeuristicFile = "TestTeamBehaviourHeuristic_" + gameName + ".txt";
            int[] recordIds = new int[]{
                gameIdx,
                0,
            };

            ArcadeMachine.runOneGameUsingHeuristic(game, level1, visuals, controller, actionFile, seed, 0, teamBehaviouHeuristic, resultsHeuristicFile, recordIds);
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
            ArcadeMachine.runOneGameUsingHeuristic(game, level1, visuals, controller, actionFile, seed, 0, teamBehaviouHeuristic, resultsHeuristicFile, recordIds);
        }
        }
    }
}
