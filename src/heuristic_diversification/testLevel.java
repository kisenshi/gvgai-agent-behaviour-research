/**
 * Author: Cristina Guerrero
 * Date: 13th January 2021
 */

package heuristic_diversification;

import java.util.Random;

import heuristic_diversification.framework.ArcadeMachineHeuristic;
import heuristic_diversification.heuristics.KnowledgeHeuristic;
import heuristic_diversification.model.GameStats;
import tools.Utils;
import tracks.ArcadeMachine;

public class testLevel {
    public static void main(String[] args) {
		//Load available games
        String gamesTest =  "examples/map_elites_experiments_generalisation.csv";
		String[][] games = Utils.readGames(gamesTest);

		//Game settings
		boolean visuals = true;
		int seed = new Random().nextInt();
		
		// Game and level to play
		int gameIdx = 2;
		int levelIdx = 5; // level names from 0 to 4 (game_lvlN.txt).
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];
		String level = game.replace(gameName, gameName + "_lvl" + levelIdx);

		//ArcadeMachineHeuristic.printGameStypes(game);

		// 1. This starts a game, in a level, played by a human.
		ArcadeMachine.playOneGame(game, level, null, seed);

		System.exit(0);
	}
}
