/**
 * Author: Cristina Guerrero
 * Date: 13th January 2021
 */

package heuristic_diversification;

import tools.Utils;
import tracks.ArcadeMachine;

import java.util.Random;

import core.heuristic.StateHeuristic;

public class testDemo {
    public static void main(String[] args) {

        // PATHS
        String controllersPath = "heuristic_diversification.controllers.";
        String heuristicsPath = "heuristic_diversification.heuristics.";

		// Available controllers:
		String sampleOneStepController = "heuristic_diversification.controllers.sampleonesteplookahead.Agent";
		String sampleMCTS = "heuristic_diversification.controllers.sampleMCTS.Agent";

		//Load available games
        String gamesTest =  "examples/games_heuristic_experiments.csv";
		String[][] games = Utils.readGames(gamesTest);

		//Game settings
		boolean visuals = true;
		int seed = new Random().nextInt();

		String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
						// + levelIdx + "_" + seed + ".txt";
						// where to record the actions
						// executed. null if not to save.

		// 1. This starts a game, in a level, played by a human.
		//ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);

		//String heuristicName = "WinningAndScoreHeuristic";
		String heuristicName = "ExplorationHeuristic";
		String heuristicInfo = heuristicsPath + heuristicName;

		String controllerName = "sampleMCTS";
		//String controllerName = "sampleonesteplookahead";
		String controller = controllersPath + controllerName + ".Agent";

		String actionFile = null;

		StateHeuristic heuristic = ArcadeMachine.createHeuristic(heuristicInfo);

		if(true){
		// Game and level to play
		int gameIdx = 13;
		int levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];
		String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);

			String resultsHeuristicFile = "Test" + heuristicName + "_" + gameName + ".txt";

			// As the data is appended at the end of the file, it is needed to store the game and controllers id
			int[] recordIds = new int[]{
				gameIdx,
				0,
			};

			ArcadeMachine.runOneGameUsingHeuristic(game, level1, visuals, controller, actionFile, seed, 0, heuristic, resultsHeuristicFile, recordIds);
		} else {
			int n_games = 20;
			for (int gameId = 0; gameId < n_games; gameId++) {
				seed = new Random().nextInt();

				levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
				gameName = games[gameId][1];
				game = games[gameId][0];
				level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);

				String resultsHeuristicFile = "Test" + heuristicName + "_" + gameName + ".txt";

				// As the data is appended at the end of the file, it is needed to store the game and controllers id
				int[] recordIds = new int[]{
						gameId,
						0,
				};

				// 2. This plays a game in a level by the controller.
				ArcadeMachine.runOneGameUsingHeuristic(game, level1, visuals, controller, actionFile, seed, 0, heuristic, resultsHeuristicFile, recordIds);
			}
		}

		
		// As the data is appended at the end of the file, it is needed to store the game and controllers id
		//int[] recordIds = new int[]{
		//	gameIdx,
		//	0,
		//};

		// 2. This plays a game in a level by the controller.
		//StateHeuristic heuristic = ArcadeMachine.createHeuristic(heuristicInfo);
        //ArcadeMachine.runOneGameUsingHeuristic(game, level1, visuals, controller, actionFile, seed, 0, heuristic, resultsHeuristicFile, recordIds);
		//ArcadeMachine.runOneGameUsingHeuristic(game, level1, visuals, controller, actionFile, seed, 0, heuristic, resultsHeuristicFile, recordIds);

		// 3. This replays a game from an action file previously recorded
	//	 String readActionsFile = recordActionsFile;
	//	 ArcadeMachine.replayGame(game, level1, visuals, readActionsFile);

		// 4. This plays a single game, in N levels, M times :
//		String level2 = new String(game).replace(gameName, gameName + "_lvl" + 1);
//		int M = 10;
//		for(int i=0; i<games.length; i++){
//			game = games[i][0];
//			gameName = games[i][1];
//			level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
//			ArcadeMachine.runGames(game, new String[]{level1}, M, sampleMCTSController, null);
//		}

		//5. This plays N games, in the first L levels, M times each. Actions to file optional (set saveActions to true).
//		int N = games.length, L = 2, M = 1;
//		boolean saveActions = false;
//		String[] levels = new String[L];
//		String[] actionFiles = new String[L*M];
//		for(int i = 0; i < N; ++i)
//		{
//			int actionIdx = 0;
//			game = games[i][0];
//			gameName = games[i][1];
//			for(int j = 0; j < L; ++j){
//				levels[j] = game.replace(gameName, gameName + "_lvl" + j);
//				if(saveActions) for(int k = 0; k < M; ++k)
//				actionFiles[actionIdx++] = "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
//			}
//			ArcadeMachine.runGames(game, levels, M, sampleRHEAController, saveActions? actionFiles:null);
//		}
    }
}
