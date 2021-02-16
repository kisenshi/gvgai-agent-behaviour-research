/**
 * Author: Cristina Guerrero
 * Date: 1st February 2021
 */

package heuristic_diversification.framework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import core.competition.CompetitionParameters;
import core.game.Game;
import core.game.StateObservation;
import core.game.StateObservationMulti;
import core.heuristic.StateHeuristic;
import core.player.AbstractHeuristicPlayer;
import core.player.AbstractMultiPlayer;
import core.player.Player;
import core.vgdl.VGDLFactory;
import core.vgdl.VGDLParser;
import core.vgdl.VGDLRegistry;
import heuristic_diversification.model.GameStats;
import tools.ElapsedCpuTimer;
import tracks.ArcadeMachine;

/**
 * HEURISTIC DIVERSIFICATION EXTENSION
 * New methods created to be able to run games choosing the heuristic to run as parameter
 * 
 * Note: Only single player supported
 */
public class ArcadeMachineHeuristic {
    public static final boolean VERBOSE = false;

    /**
     * createPlayer adapted for the modifications related to heuristic diversification
     * This modifications only cover single player.
     * 
     * @param playerName
     *            name of the agent to create. It must be of the type
     *            "<agentPackage>.Agent".
     * @param actionFile
     *            filename of the file where the actions of this player, for
     *            this game, should be recorded.
     * @param stateObs
     *            Initial state of the game to be played by the agent.
     * @param randomSeed
     *            Seed for the sampleRandom generator of the game to be played.
     * @param heuristic
     *            Heuristic object to plug into the player
     * @return the player, created and initialized, ready to start playing the
     *         game.
     */
    public static AbstractHeuristicPlayer createHeuristicPlayer(String playerName, String actionFile, StateObservation stateObs,
	                                                            int randomSeed, StateHeuristic heuristic)  {
        AbstractHeuristicPlayer player = null;

        try {
            // create the controller.
            player = (AbstractHeuristicPlayer) createHeuristicController(playerName, 0, stateObs, heuristic);
            if (player != null)
                player.setup(actionFile, randomSeed, false);
            // else System.out.println("No controller created.");

        } catch (Exception e) {
            // This probably happens because controller took too much time to be
            // created.
            e.printStackTrace();
            System.exit(1);
        }

        // System.out.println("Created player.");

        return player;
    }

    /**
     * 
     * createController adapted for the modifications related to heuristic diversification
     * This modifications only cover single player.
     * 
     * @param playerName
     *            Name of the controller to instantiate.
     * @param stateObs
     *            Initial state of the game to be played by the agent.
     * @param heuristic
     *           Instance of the heuristic to plug in the player
     * @return the player if it could be created, null otherwise.
     */

    protected static Player createHeuristicController(String playerName, int playerID, StateObservation stateObs, StateHeuristic heuristic)
	    throws RuntimeException {
        Player player = null;
        try {

            // Determine the time due for the controller creation.
            ElapsedCpuTimer ect = new ElapsedCpuTimer();
            ect.setMaxTimeMillis(CompetitionParameters.INITIALIZATION_TIME);

            if (stateObs.getNoPlayers() < 2) { // single player
				// Get the class and the constructor with arguments
				// (StateObservation, long).
				Class<? extends AbstractHeuristicPlayer> controllerClass = Class.forName(playerName)
                    .asSubclass(AbstractHeuristicPlayer.class);
                    
				Class[] gameArgClass = new Class[] { StateObservation.class, ElapsedCpuTimer.class, StateHeuristic.class };
				Constructor controllerArgsConstructor = controllerClass.getConstructor(gameArgClass);

				// Call the constructor with the appropriate parameters.
				Object[] constructorArgs = new Object[] { stateObs, ect.copy(), heuristic };

				player = (AbstractHeuristicPlayer) controllerArgsConstructor.newInstance(constructorArgs);
				player.setPlayerID(playerID);

            } else { // multi player - heuristic not supported, regular player is created
				// Get the class and the constructor with arguments
				// (StateObservation, long, int).
				Class<? extends AbstractMultiPlayer> controllerClass = Class.forName(playerName)
					.asSubclass(AbstractMultiPlayer.class);
				Class[] gameArgClass = new Class[] { StateObservationMulti.class, ElapsedCpuTimer.class, int.class };
				Constructor controllerArgsConstructor = controllerClass.getConstructor(gameArgClass);

				// Call the constructor with the appropriate parameters.
				Object[] constructorArgs = new Object[] { (StateObservationMulti) stateObs.copy(), ect.copy(), playerID };

				player = (AbstractMultiPlayer) controllerArgsConstructor.newInstance(constructorArgs);
				player.setPlayerID(playerID);
            }

            // Check if we returned on time, and act in consequence.
            long timeTaken = ect.elapsedMillis();
            if (CompetitionParameters.TIME_CONSTRAINED && ect.exceededMaxTime()) {
				long exceeded = -ect.remainingTimeMillis();
				System.out.println("Controller initialization time out (" + exceeded + ").");

				return null;
            } else {
				if (VERBOSE)
					System.out.println("Controller initialization time: " + timeTaken + " ms.");
            }

            // This code can throw many exceptions (no time related):

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.err.println("Constructor " + playerName + "(StateObservation,long,String) not found in controller class:");
            System.exit(1);

        } catch (ClassNotFoundException e) {
            System.err.println("Class " + playerName + " not found for the controller:");
            e.printStackTrace();
            System.exit(1);

        } catch (InstantiationException e) {
            System.err.println("Exception instantiating " + playerName + ":");
            e.printStackTrace();
            System.exit(1);

        } catch (IllegalAccessException e) {
            System.err.println("Illegal access exception when instantiating " + playerName + ":");
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException e) {
            System.err.println("Exception calling the constructor " + playerName + "(StateObservation,long,String):");
            e.printStackTrace();
            System.exit(1);
        }

        // System.out.println("Controller created. " + player.getPlayerID());

        return player;
    }

    public static StateHeuristic createHeuristic(String heuristicName) {
        StateHeuristic heuristic = null;
        try {
            Class<? extends StateHeuristic> heuristicClass = Class.forName(heuristicName)
                    .asSubclass(StateHeuristic.class);

            Class[] heuristicArgsClass = new Class[] { };
            Object[] constructorArgs = new Object[] { };

            Constructor heuristicArgsConstructor = heuristicClass.getConstructor(heuristicArgsClass);

            heuristic = (StateHeuristic) heuristicArgsConstructor.newInstance(constructorArgs);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.err.println("Constructor " + heuristicName + "() not found :");
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.err.println("Class " + heuristicName + " not found :");
            e.printStackTrace();
            System.exit(1);
        } catch (InstantiationException e) {
            System.err.println("Exception instantiating " + heuristicName + ":");
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalAccessException e) {
            System.err.println("Illegal access exception when instantiating " + heuristicName + ":");
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException e) {
            System.err.println("Exception calling the constructor " + heuristicName + "():");
            e.printStackTrace();
            System.exit(1);
        }

        return heuristic;
    }

    public static StateHeuristic createHeuristicWithArgs(String heuristicName, Class[] heuristicArgsClass, Object[] constructorArgs) {
        StateHeuristic heuristic = null;
        try {
            Class<? extends StateHeuristic> heuristicClass = Class.forName(heuristicName)
                    .asSubclass(StateHeuristic.class);

            Constructor heuristicArgsConstructor = heuristicClass.getConstructor(heuristicArgsClass);

            heuristic = (StateHeuristic) heuristicArgsConstructor.newInstance(constructorArgs);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.err.println("Constructor " + heuristicName + "() not found :");
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.err.println("Class " + heuristicName + " not found :");
            e.printStackTrace();
            System.exit(1);
        } catch (InstantiationException e) {
            System.err.println("Exception instantiating " + heuristicName + ":");
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalAccessException e) {
            System.err.println("Illegal access exception when instantiating " + heuristicName + ":");
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException e) {
            System.err.println("Exception calling the constructor " + heuristicName + "():");
            e.printStackTrace();
            System.exit(1);
        }

        return heuristic;
    }

	/**
     * Reads and launches a game for a bot to be played with a certain heuristic. 
     * Graphics can be on or off.
     * Only single player supported.
     * 
     * @param game_file
     *            game description file.
     * @param level_file
     *            file with the level to be played.
     * @param visuals
     *            true to show the graphics, false otherwise.
     * @param agentNames
     *            names (inc. package) where the tracks are otherwise.
     *            Names separated by space.
     * @param actionFile
     *            filename of the files where the actions of these players, for
     *            this game, should be recorded.
     * @param randomSeed
     *            sampleRandom seed for the sampleRandom generator.
     * @param heuristicName
	 * 			  name (include packages) of the heuristic to be used
     * @param recordIds
     *            data to record related to heuristics 
     */

	public static double[] runOneGameUsingHeuristic(String game_file, String level_file, boolean visuals, String agentNames,
													String actionFile, int randomSeed, StateHeuristic heuristic, String heuristicFile,
													int[] recordIds) {
		VGDLFactory.GetInstance().init(); // This always first thing to do.
		VGDLRegistry.GetInstance().init();

		if (VERBOSE)
			System.out.println(" ** Playing game " + game_file + ", level " + level_file + " **");

		if (CompetitionParameters.OS_WIN)
		{
			System.out.println(" * WARNING: Time limitations based on WALL TIME on Windows * ");
		}

		// First, we create the game to be played..
		Game toPlay = new VGDLParser().parseGame(game_file);
		toPlay.buildLevel(level_file, randomSeed);

		// Warm the game up.
		ArcadeMachine.warmUp(toPlay, CompetitionParameters.WARMUP_TIME);

		// Create the players.
		String[] names = agentNames.split(" ");
		int no_players = toPlay.no_players;
		if (no_players > 1) {
            System.out.println("Error: Only single player supported in this mode");
            return null;
		}

		// System.out.println("Number of players: " + no_players);

		AbstractHeuristicPlayer[] players;
		players = new AbstractHeuristicPlayer[no_players];

		int playerId = 0;

        // single player
        players[playerId] = createHeuristicPlayer(names[playerId], actionFile, toPlay.getObservation(), randomSeed, heuristic);

		if (players[playerId] == null) {
			// Something went wrong in the constructor, controller
			// disqualified
			// single player
			toPlay.disqualify();

			// Get the score for the result.
			toPlay.handleResult();
			toPlay.printResult();
			return toPlay.getFullResult();
		}

		// Then, play the game.
		double[] score;
		if (visuals)
			score = toPlay.playGame(players, randomSeed, false, 0);
		else
			score = toPlay.runGame(players, randomSeed);

		// Finally, when the game is over, we need to tear the players down.
        ArcadeMachine.tearPlayerDown(toPlay, players, actionFile, randomSeed, true);
        
        // Heuristic data is recorded in the heuristicFile provided
		players[playerId].recordHeuristicData(toPlay, heuristicFile, randomSeed, recordIds);

		// This, the last thing to do in this method, always:
		toPlay.handleResult();
		toPlay.printResult();

		return toPlay.getFullResult();
    }

    public static void runGameAndGetStats(GameStats gameStats, String game_file, String level_file, boolean visuals, String agentName,
                                          String actionFile, StateHeuristic heuristic, int nGames) {
        VGDLFactory.GetInstance().init();
        VGDLRegistry.GetInstance().init();
        
        if (VERBOSE) {
            System.out.println(" ** Playing game " + game_file + ", level " + level_file + " **");
        }
        
        // Create the game to be played
        Game toPlay = new VGDLParser().parseGame(game_file);

        // Create the player
		String[] names = agentName.split(" ");
		int no_players = toPlay.no_players;
		if (no_players > 1) {
            System.out.println("Error: Only single player supported in this mode");
            return;
		}

		AbstractHeuristicPlayer[] players;
		players = new AbstractHeuristicPlayer[no_players];

		int playerId = 0;
        
        for (int i = 0; i < nGames; i++) {
            int randomSeed = new Random().nextInt();
            System.out.println(i + " with seed " + randomSeed);
            toPlay.buildLevel(level_file, randomSeed);

            // Warm the game up.
            ArcadeMachine.warmUp(toPlay, CompetitionParameters.WARMUP_TIME);
            double[] score;

            // single player
            players[playerId] = createHeuristicPlayer(names[playerId], actionFile, toPlay.getObservation(), randomSeed, heuristic);

            if (players[playerId] == null) {
                // Something went wrong in the constructor, controller disqualified
                toPlay.disqualify();

                // Get the score for the result.
                score = toPlay.handleResult();
                toPlay.printResult();
                toPlay.reset();
                continue;
            }

            // Play the game
            if (visuals) {
                score = toPlay.playGame(players, randomSeed, false, 0);
            } else {
                score = toPlay.runGame(players, randomSeed);
            }

            ArcadeMachine.tearPlayerDown(toPlay, players, actionFile, randomSeed, true);

            // Add stats of the run to the stats class
            players[playerId].recordGameStats(toPlay, gameStats);

            // This, the last thing to do in this method, always:
		    toPlay.handleResult();
		    toPlay.printResult();

            // Reset the game
            toPlay.reset();
        }
    }
}
