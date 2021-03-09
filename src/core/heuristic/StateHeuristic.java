/**
 * Author: Cristina Guerrero
 * Date: 13th January 2021
 */

package core.heuristic;

import java.awt.Graphics2D;
import java.io.BufferedWriter;

import core.game.Game;
import core.game.StateObservation;
import heuristic_diversification.model.GameStats;

public abstract class StateHeuristic {
    public static final boolean DEBUG = false;

    protected static final double HUGE_NEGATIVE = -10000.0;
    protected static final double LESS_HUGE_NEGATIVE = -5000.0;
    protected static final double HUGE_POSITIVE =  10000.0;
    
    protected BufferedWriter writer;
    protected double heuristicMax;
    protected double heuristicMin;
    protected int nMinHeuristicUpdates;
    protected int nMaxHeuristicUpdates;

    protected int mNFutureStates;
    protected int mMaxFutureStates;

    public void initHeuristicInternalInformation(StateObservation stateObs) {
        // Initialise max and min values of heuristic
        heuristicMax = HUGE_NEGATIVE;
        heuristicMin = HUGE_POSITIVE;
        nMaxHeuristicUpdates = 0;
        nMinHeuristicUpdates = 0;

        // Future data independent from the heuristic
        mNFutureStates = 0;
        mMaxFutureStates = 0;
    }

    public void updateHeuristicInternalInformation(StateObservation stateObs) {
        // Start fresh for future calculations
        nMaxHeuristicUpdates = 0;
        nMinHeuristicUpdates = 0;
    }

    public void restartFutureStateData() {
        mNFutureStates = 0;
    }

    public void updateFutureStateData(StateObservation stateObs) {
        // Keep track of the maximum number of future states predicted as well as its
        // maximum
        mNFutureStates += 1;
        if (mNFutureStates > mMaxFutureStates) {
            mMaxFutureStates = mNFutureStates;
        }
    }

    abstract public double evaluateState(StateObservation stateObs);

    abstract public String relevantInfoStr(StateObservation stateObs);

    abstract public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds);

    abstract public void drawInScreen(Graphics2D g);

    abstract public void recordGameStats(Game game, GameStats gameStats);

    public double normaliseHeuristic(double h){
        if (DEBUG){
            System.out.println("hMax: "+ heuristicMax + " hMin: " + heuristicMin + " nMaxUpdates: " + nMaxHeuristicUpdates + " nMinUpdates: " + nMinHeuristicUpdates);
        }
        
        if ((h > heuristicMax) && (h < heuristicMin)){
            // This is the case for the first iteration
            heuristicMax = heuristicMin = h;
            return 0.5;
        }

        if(h > heuristicMax){
            heuristicMax = h;
            nMaxHeuristicUpdates += 1;
            return 1 + (0.01*(nMaxHeuristicUpdates - 1));
        } 
        if (h < heuristicMin){
            heuristicMin = h;
            nMinHeuristicUpdates += 1;
            return 0 - (0.01*(nMinHeuristicUpdates - 1));
        }
        
        if(Double.compare(heuristicMax, heuristicMin) == 0){
            return 0.5;
        }

       return (h - heuristicMin)  /  (heuristicMax - heuristicMin);
    }
}
