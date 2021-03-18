/**
 * Author: Cristina Guerrero
 * Date: 18th March 2021
 */

package heuristic_diversification.heuristics;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import core.game.Game;
import core.game.StateObservation;
import heuristic_diversification.model.GameStats;

/**
 * Maximize the killing of NPCs.
 */
public class KillingHeuristic extends KnowledgeHeuristic {
    private static final boolean DEBUG_KILLER = false;

    private ArrayList<Integer> mNPCSprites;
    private ArrayList<Integer> mFutureNPCSprites;
    private SpritesData mFutureInteractions;

    @Override
    public void initHeuristicInternalInformation(StateObservation stateObs) {
        super.initHeuristicInternalInformation(stateObs);

        // Killing needs to keep track of the sprites that correspond to NPCs.
        mNPCSprites = new ArrayList<Integer>();
    }

    @Override
    public void updateHeuristicInternalInformation(StateObservation stateObs) {
        super.updateHeuristicInternalInformation(stateObs);
        
        // Killing needs to keep track of the sprites that correspond to NPCs.
        mSpritesData.addNewSpritesToList(stateObs.getNPCPositions(), mNPCSprites);
    }

    @Override
    public void restartFutureStateData(StateObservation stateObs) {
        mFutureInteractions = new SpritesData(stateObs);
        mFutureNPCSprites = new ArrayList<Integer>(mNPCSprites);

        // Restart future data
        super.restartFutureStateData(stateObs);

        return;
    }

    @Override
    public void updateFutureStateData(StateObservation stateObs) {
        // keep track of future NPC sprites
        mSpritesData.addNewSpritesToList(stateObs.getNPCPositions(), mFutureNPCSprites);

        // update the future interaction history with only the ones that involve NPC sprites (killing)
        updateInteractionHistoryOfSpritesInList(mFutureInteractions, stateObs, mFutureNPCSprites);
        
        // Update future data
        super.updateFutureStateData(stateObs);

        return;
    }

    @Override
    public double evaluateState(StateObservation stateObs) {
        double h = 0;
        
        // For this heuristic is penalised finishing the gamem either winning or losing it
        boolean gameOver = stateObs.isGameOver();
        if (gameOver) {
            // Return a negative value that it is in range of the possible values in the
            // heuristic, to reduce the range between
            // the lowest and highest values of the heuristic
            h = (-1) * mMaxFutureStates;

            if(DEBUG) {
                System.out.println("GAME OVER maxLength " + mMaxFutureStates + " h " + h);
            }
        }

        int nKills = mFutureInteractions.nHitsToSprites(mFutureNPCSprites);
        h += nKills;

        if(DEBUG) {
            int randomSeed = new Random().nextInt();
            System.out.println(randomSeed + " futureNPCs: " + mFutureNPCSprites.toString() + " Kills: " + nKills);
            
            if (DEBUG_KILLER) {
                mFutureInteractions.printDebugSpritesData("debug_eval_" + stateObs.getGameTick() + "_" + randomSeed);
            }
        }

        return h;
    }

    @Override
    public String relevantInfoStr(StateObservation stateObs) {
        ArrayList<Integer> spritesKilled = mSpritesData.spritesFromListHits(mNPCSprites);
        int nKills = mSpritesData.nHitsToSprites(mNPCSprites);
        int lastKill = mSpritesData.getLastHitToSprites(mNPCSprites);
        return "NPCS: " + mNPCSprites.toString() + " spritesKilled: " + spritesKilled.toString() + " nKills: " + nKills + " lastKill: " + lastKill;
    }

    @Override
    public void recordGameStats(Game game, GameStats gameStats) {
        ArrayList<Integer> spritesKilled = mSpritesData.spritesFromListHits(mNPCSprites);
        int nKills = mSpritesData.nHitsToSprites(mNPCSprites);
        int lastKill = mSpritesData.getLastHitToSprites(mNPCSprites);

        gameStats.addKillerFinalData(spritesKilled, nKills, lastKill);
    }

    @Override
    public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds) {
        // We dont implement this method for this heuristic
    }

    @Override
    public void drawInScreen(Graphics2D g) {
        // No need to draw anything on screen
    }
}
