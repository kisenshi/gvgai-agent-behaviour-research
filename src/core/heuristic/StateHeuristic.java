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

    abstract public void initHeuristicInternalInformation(StateObservation stateObs);

    abstract public void updateHeuristicInternalInformation(StateObservation stateObs);

    abstract public double evaluateState(StateObservation stateObs);

    abstract public void restartFutureStateData();

    abstract public void updateFutureStateData(StateObservation stateObs);

    abstract public String relevantInfoStr(StateObservation stateObs);

    abstract public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds);

    abstract public void drawInScreen(Graphics2D g);
}
