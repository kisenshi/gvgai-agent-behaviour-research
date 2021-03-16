/**
 * Author: Cristina Guerrero
 * Date: 9th March 2021
 */


package heuristic_diversification.heuristics;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import core.game.Game;
import core.game.StateObservation;
import heuristic_diversification.model.GameStats;

/**
 * Maximize the discovery and interaction with sprites in the game, prioritising new interactions.
 * When no new interactions are possible, it prioritises interactions in new locations
 * of the game.
 */
public class CuriosityHeuristic extends KnowledgeHeuristic {
    private static final boolean DEBUG_CURIOUS = false;
    private static final int H_NEW_SPRITES_DISCOVERED       = 1000;
    private static final int H_NEW_INTERACTIONS             = 1000;
    private static final int H_NEW_CURIOSITY_INTERACTIONS   = 100;
    private static final int H_CURIOSITY_LOCATIONS          = 10;
    private static final int H_TOTAL_INTERACTIONS           = 1;

    SpritesData mFutureInteractions;
    
    @Override
    public void initHeuristicInternalInformation(StateObservation stateObs) {
        super.initHeuristicInternalInformation(stateObs);

        // Curiosity uses the information in KnowledgeHeuristic, no need to
        // do further initialisations
    }

    @Override
    public void updateHeuristicInternalInformation(StateObservation stateObs) {
        super.updateHeuristicInternalInformation(stateObs);

        // Curiosity uses the information in KnowledgeHeuristic, no need to
        // do further updates
    }

    @Override
    public void restartFutureStateData(StateObservation stateObs) {
        mFutureInteractions = new SpritesData(stateObs);

        // Restart future data
        super.restartFutureStateData(stateObs);

        return;
    }

    @Override
    public void updateFutureStateData(StateObservation stateObs) {
        // acknowledge sprites for discovery
        acknowledgeSprites(mFutureInteractions, stateObs);

        // update interaction history for interaction
        updateInteractionHistory(mFutureInteractions, stateObs);

        // Update future data
        super.updateFutureStateData(stateObs);

        return;
    }

    private double getHighHeuristicValue() {
        return H_NEW_INTERACTIONS + H_NEW_CURIOSITY_INTERACTIONS + H_CURIOSITY_LOCATIONS + H_TOTAL_INTERACTIONS;
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
            h = (-1) * getHighHeuristicValue();

            if(DEBUG) {
                System.out.println("GAME OVER maxLength " + mMaxFutureStates + " h " + h);
            }
        }

        // For the curiosity we give priority to each interaction as follows:
        // 1) New sprites discovered and new interactions
        // 2) New curiosity: interactions in a new location
        // 3) Different curiosity: Number of unique future locations
        // 4) Total interactions: Number of total future interactions
        int nNewSpritesDiscovered = mFutureInteractions.nNewSpritesDiscovered(mSpritesData);
        h += (H_NEW_SPRITES_DISCOVERED * nNewSpritesDiscovered);

        int nNewInteractions = mFutureInteractions.nNewInteractions(mSpritesData);
        h += (H_NEW_INTERACTIONS * nNewInteractions);

        int nNewCuriosityInteractions = mFutureInteractions.nNewCuriosityInteractions(mSpritesData);
        h += (H_NEW_CURIOSITY_INTERACTIONS * nNewCuriosityInteractions);

        int nTotalCuriosityInteractions = mFutureInteractions.nTotalCuriosityInteractions();
        h += (H_CURIOSITY_LOCATIONS * nTotalCuriosityInteractions);

        int nTotalInteracions = mFutureInteractions.nTotalInteractions();
        h += (H_TOTAL_INTERACTIONS * nTotalInteracions);

        if(DEBUG) {
            int randomSeed = new Random().nextInt();
            System.out.println(randomSeed + " New sprites: " + nNewSpritesDiscovered + " New interactions: " + nNewInteractions + " New curiosity interactions : " + nNewCuriosityInteractions + " total distinct curiosity: " + nTotalCuriosityInteractions + " total interactions: " + nTotalInteracions);
            
            if (DEBUG_CURIOUS) {
                mFutureInteractions.printDebugSpritesData("debug_eval_" + stateObs.getGameTick() + "_" + randomSeed);
            }
        }
        return h;
    }

    @Override
    public String relevantInfoStr(StateObservation stateObs) {
        return "";
    }

    @Override
    public void recordGameStats(Game game, GameStats gameStats) {
        // discovery
        ArrayList<Integer> spritesDiscovered = mSpritesData.getSpritesDiscovered();
        int nDifferentStypes = spritesDiscovered.size();
        int lastSpriteDiscovery = mSpritesData.getLastNewDiscovery();
        
        gameStats.addDiscoveryFinalData(spritesDiscovered, nDifferentStypes, lastSpriteDiscovery);

        // curiority
        int nDistinctStypes = mSpritesData.getNStypesInteractedWith();
        int nCollisions = mSpritesData.nCollisions();
        int nHits = mSpritesData.nHits();
        int nCuriosity = mSpritesData.nTotalCuriosityInteractions();  
        int lastNewCollision = mSpritesData.getLastNewCollision(); 
        int lastNewHit = mSpritesData.getLastNewHit();
        int lastCuriosity = mSpritesData.getLastCuriosity();

        gameStats.addCuriousFinalData(nDistinctStypes, nCuriosity, nCollisions, nHits, lastNewCollision, lastNewHit, lastCuriosity);
        
        // DEBUG
        if (DEBUG_CURIOUS) {
            int randomSeed = new Random().nextInt();
            System.out.println(randomSeed);
            mSpritesData.printDebugSpritesData(randomSeed + "_gameStats");
        }
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
