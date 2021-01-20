package heuristic_diversification.controllers.sampleonesteplookahead;

import core.game.StateObservation;
import core.heuristic.StateHeuristic;
import core.player.AbstractHeuristicPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Utils;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractHeuristicPlayer {

    public double epsilon = 1e-6;
    public Random m_rnd;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer, StateHeuristic heuristic) {
        super(stateObs, heuristic);
        m_rnd = new Random();
    }

    /**
     *
     * Very simple one step lookahead agent.
     *
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        Types.ACTIONS bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY;

        // Update the information the heuristic contains about the game if needed
        heuristic.updateHeuristicInternalInformation(stateObs);

        for (Types.ACTIONS action : stateObs.getAvailableActions()) {
            StateObservation stCopy = stateObs.copy();
            heuristic.restartFutureStateData();
            
            stCopy.advance(action);
            heuristic.updateFutureStateData(stCopy);
            double Q = heuristic.evaluateState(stCopy);
            Q = Utils.noise(Q, this.epsilon, this.m_rnd.nextDouble());

            //System.out.println("Action:" + action + " score:" + Q);
            if (Q > maxQ) {
                maxQ = Q;
                bestAction = action;
            }
        }

        //System.out.println("======== "  + maxQ + " " + bestAction + "============");
        return bestAction;
    }
}
