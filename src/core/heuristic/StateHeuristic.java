/**
 * Author: Cristina Guerrero
 * Date: 13th January 2021
 */

package core.heuristic;

import core.game.Game;
import core.game.StateObservation;

import java.awt.*;
import java.io.BufferedWriter;

public abstract class StateHeuristic {
    protected static final double HUGE_NEGATIVE = -10000.0;
    protected static final double LESS_HUGE_NEGATIVE = -5000.0;
    protected static final double HUGE_POSITIVE =  10000.0;
    protected BufferedWriter writer;

    protected double heuristicMax = HUGE_NEGATIVE;
    protected double heuristicMin = HUGE_POSITIVE;
    protected int nMinHeuristicUpdates = 0;
    protected int nMaxHeuristicUpdates = 0;

    abstract public void initHeuristicInternalInformation(StateObservation stateObs);

    abstract public void updateHeuristicInternalInformation(StateObservation stateObs);

    abstract public double evaluateState(StateObservation stateObs);

    abstract public void restartFutureStateData();

    abstract public void updateFutureStateData(StateObservation stateObs);

    abstract public String relevantInfoStr(StateObservation stateObs);

    abstract public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds);

    abstract public void drawInScreen(Graphics2D g);

    public double normaliseHeuristic(double h){
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
