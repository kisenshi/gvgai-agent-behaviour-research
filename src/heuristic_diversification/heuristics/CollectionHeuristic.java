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
 * Maximize the collection of resources.
 */
public class CollectionHeuristic extends KnowledgeHeuristic {
    private static final boolean DEBUG_COLLECTOR = false;
    
    private ArrayList<Integer> mResourceSprites;
    private ArrayList<Integer> mFutureResourceSprites;
    private SpritesData mFutureInteractions;

    @Override
    public void initHeuristicInternalInformation(StateObservation stateObs) {
        super.initHeuristicInternalInformation(stateObs);

        // Collection needs to keep track of the sprites that correspond to resources.
        mResourceSprites = new ArrayList<Integer>();
    }

    @Override
    public void updateHeuristicInternalInformation(StateObservation stateObs) {
        super.updateHeuristicInternalInformation(stateObs);

        // Collection needs to keep track of the sprites that correspond to resources.
        mSpritesData.addNewSpritesToList(stateObs.getResourcesPositions(), mResourceSprites);
    }

    @Override
    public void restartFutureStateData(StateObservation stateObs) {
        mFutureInteractions = new SpritesData(stateObs);
        mFutureResourceSprites = new ArrayList<Integer>(mResourceSprites);

        // Restart future data
        super.restartFutureStateData(stateObs);

        return;
    }

    @Override
    public void updateFutureStateData(StateObservation stateObs) {
        // keep track of future Resource sprites
        mSpritesData.addNewSpritesToList(stateObs.getResourcesPositions(), mFutureResourceSprites);

        // update the future interaction history with only the ones that involve resource sprites (collection)
        updateInteractionHistoryOfSpritesInList(mFutureInteractions, stateObs, mFutureResourceSprites);

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
            h = (-1);

            if(DEBUG) {
                System.out.println("GAME OVER maxLength " + mMaxFutureStates + " h " + h);
            }
        }

        int nItemsCollected = mFutureInteractions.nCollisionsToSprites(mFutureResourceSprites);
        h += nItemsCollected;

        if(DEBUG) {
            int randomSeed = new Random().nextInt();
            System.out.println(randomSeed + " futureResources: " + mFutureResourceSprites.toString() + " ItemsCollected: " + nItemsCollected);
            
            if (DEBUG_COLLECTOR) {
                mFutureInteractions.printDebugSpritesData("debug_eval_" + stateObs.getGameTick() + "_" + randomSeed);
            }
        }

        return h;
    }

    @Override
    public String relevantInfoStr(StateObservation stateObs) {
        ArrayList<Integer> spritesCollected = mFutureInteractions.spritesFromListCollisions(mFutureResourceSprites);
        int nItemsCollected = mFutureInteractions.nCollisionsToSprites(mFutureResourceSprites);
        int lastCollection = mFutureInteractions.getLastCollisionToSprites(mFutureResourceSprites);

        return "Future collector data --> resources: " + mFutureResourceSprites.toString() + " spritesCollected: " + spritesCollected.toString() + " nItemsCollected: " + nItemsCollected + " lastCollection: " + lastCollection;
    }

    @Override
    public void recordGameStats(Game game, GameStats gameStats) {
        ArrayList<Integer> spritesCollected = mSpritesData.spritesFromListCollisions(mResourceSprites);
        int nItemsCollected = mSpritesData.nCollisionsToSprites(mResourceSprites);
        int lastCollection = mSpritesData.getLastCollisionToSprites(mResourceSprites);

        gameStats.addCollectorFinalData(spritesCollected, nItemsCollected, lastCollection);
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

