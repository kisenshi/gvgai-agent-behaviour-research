/**
 * Author: Cristina Guerrero
 * Date: 13th January 2021
 */

package heuristic_diversification.heuristics;

import java.awt.Graphics2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import core.game.Game;
import core.game.StateObservation;
import core.heuristic.StateHeuristic;
import heuristic_diversification.model.GameStats;
import ontology.Types;

/**
 * Maximize the score difference if the player has not win yet
 */
public class WinningAndScoreHeuristic extends StateHeuristic {
    private double mCurrentScore;
    private int mLastScoreChangeTick;
    private int mLastPositiveScoreChangeTick;

    @Override
    public void initHeuristicInternalInformation(StateObservation stateObs) {
        super.initHeuristicInternalInformation(stateObs);

        // Store the score from the game initial state
        mCurrentScore = stateObs.getGameScore();
        mLastScoreChangeTick = stateObs.getGameTick();
        mLastPositiveScoreChangeTick = stateObs.getGameTick();
    }

    private double getHighHeuristicValue() {
        return 1000;
    }

    @Override
    public double evaluateState(StateObservation stateObs) {
        double h = 0;

        boolean gameOver = stateObs.isGameOver();
        Types.WINNER win = stateObs.getGameWinner();
        double newScore = stateObs.getGameScore();

        if (gameOver && win == Types.WINNER.PLAYER_LOSES) {
            h = (-1) * getHighHeuristicValue();

            if (DEBUG) {
                System.out.print("LOSE " + h);
            }
        }

        if (gameOver && win == Types.WINNER.PLAYER_WINS) {
            h = getHighHeuristicValue();

            if (DEBUG) {
                System.out.print("WIN "+ h);
            }
        }

        // Return the score change
        h += (newScore - mCurrentScore);

        if (DEBUG) {
            System.out.println(" Diffscore: " + (newScore - mCurrentScore));
        }

        return h;
    }

    @Override
    public void updateHeuristicInternalInformation(StateObservation stateObs) {
        super.updateHeuristicInternalInformation(stateObs);

        double newScore = stateObs.getGameScore();
        if(Double.compare(newScore, mCurrentScore) != 0){
            mLastScoreChangeTick = stateObs.getGameTick();

            if(Double.compare(newScore, mCurrentScore) > 0){
                mLastPositiveScoreChangeTick = stateObs.getGameTick();
            }
        }
        
        // Store the current score in the game
        mCurrentScore = newScore;
    }

    @Override
    public void recordGameStats(Game game, GameStats gameStats) {
        int isWinner = (game.getWinner() == Types.WINNER.PLAYER_WINS ? 1 : 0);
        gameStats.addWinnerData(isWinner);
        gameStats.addRecordBreakerData(game.getScore(), mLastScoreChangeTick, mLastPositiveScoreChangeTick);
    }

    @Override
    public String relevantInfoStr(StateObservation stateObs) {
        return "score: " + stateObs.getGameScore();
    }

    @Override
    public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds) {
        // Data:
        // gameId controllerId randomSeed winnerId score gameTicks
        try {
            if (fileName != null && !fileName.equals("")) {
                writer = new BufferedWriter(new FileWriter(new File(fileName), true));
                writer.write(recordIds[0] + " " + recordIds[1] + " " + randomSeed + " "
                        + (played.getWinner() == Types.WINNER.PLAYER_WINS ? 1 : 0) + " " + played.getScore() + " "
                        + played.getGameTick() + "\n");

                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawInScreen(Graphics2D g) {
        // No need to draw anything on screen
        return;
    }
}
