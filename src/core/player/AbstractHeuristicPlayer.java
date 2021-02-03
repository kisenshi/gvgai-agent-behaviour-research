/**
 * Author: Cristina Guerrero
 * Date: 13th January 2021
 */

package core.player;

import java.awt.Graphics2D;

import core.game.Game;
import core.game.StateObservation;
import core.heuristic.StateHeuristic;
import heuristic_diversification.helper.GameStats;

public abstract class AbstractHeuristicPlayer extends AbstractPlayer {
    protected StateHeuristic heuristic;

    public AbstractHeuristicPlayer(StateObservation stateObs, StateHeuristic heuristic) {
        this.heuristic = heuristic;
        heuristic.initHeuristicInternalInformation(stateObs);
    }

    /**
     * For some heuristics it would be helpful to print info in the screen
     * */
    public void draw(Graphics2D g) {
        heuristic.drawInScreen(g);
    }

    /**
     * When the game is finished it is needed to print some information in a file
     */
    public void recordHeuristicData(Game played, String fileName, int randomSeed, int[] recordIds) {
        heuristic.recordDataOnFile(played, fileName, randomSeed, recordIds);
    }

    /**
     * Record game stats
     */
    public void recordGameStats(Game game, GameStats gameStats) {
        heuristic.recordGameStats(game, gameStats);
    }
}
