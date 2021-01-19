package heuristic_diversification.controllers.sampleMCTS;

import java.util.ArrayList;
import java.util.Random;

import core.game.StateObservation;
import core.heuristic.StateHeuristic;
import core.player.AbstractHeuristicPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is an implementation of MCTS UCT
 */
public class Agent extends AbstractHeuristicPlayer {

    public int num_actions;
    public Types.ACTIONS[] actions;

    protected SingleMCTSPlayer mctsPlayer;

    /**
     * Public constructor with state observation and time due.
     * @param stateObs state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     * @param heuristic Instance of the heuristic to use for evaluation.
     */
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer, StateHeuristic heuristic)
    {
        super(stateObs, heuristic); 

        //Get the actions in a static array.
        ArrayList<Types.ACTIONS> act = stateObs.getAvailableActions();
        actions = new Types.ACTIONS[act.size()];
        for(int i = 0; i < actions.length; ++i)
        {
            actions[i] = act.get(i);
        }
        num_actions = actions.length;

        //Create the player.

        mctsPlayer = getPlayer(stateObs, elapsedTimer);
    }

    public SingleMCTSPlayer getPlayer(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        return new SingleMCTSPlayer(new Random(), num_actions, actions, heuristic);
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        //Set the state observation object as the new root of the tree.
        mctsPlayer.init(stateObs);

        //Determine the action using MCTS...
        int action = mctsPlayer.run(elapsedTimer);

        //... and return it.
        return actions[action];
    }

}
