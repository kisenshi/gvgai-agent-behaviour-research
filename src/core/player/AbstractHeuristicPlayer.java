/**
 * Author: Cristina Guerrero
 * Date: 13th January 2021
 */

package core.player;

import core.game.Game;
import core.game.StateObservation;
import core.heuristic.StateHeuristic;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class AbstractHeuristicPlayer extends AbstractPlayer {

    private String heuristicName = "YouForgotToSetTheHeuristic";
    protected StateHeuristic heuristic;

    public AbstractHeuristicPlayer(StateObservation stateObs, String heuristicName) {
        this.heuristicName = heuristicName;
        this.heuristic = createPlayerHeuristic(stateObs);
    }

    protected StateHeuristic createPlayerHeuristic(StateObservation stateObs) {
        StateHeuristic heuristic = null;
        try {
            Class<? extends StateHeuristic> heuristicClass = Class.forName(heuristicName)
                    .asSubclass(StateHeuristic.class);

            Class[] heuristicArgsClass = new Class[] { StateObservation.class };
            Object[] constructorArgs = new Object[] { stateObs };

            Constructor heuristicArgsConstructor = heuristicClass.getConstructor(heuristicArgsClass);

            heuristic = (StateHeuristic) heuristicArgsConstructor.newInstance(constructorArgs);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.err.println("Constructor " + heuristicName + "() not found :");
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.err.println("Class " + heuristicName + " not found :");
            e.printStackTrace();
            System.exit(1);
        } catch (InstantiationException e) {
            System.err.println("Exception instantiating " + heuristicName + ":");
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalAccessException e) {
            System.err.println("Illegal access exception when instantiating " + heuristicName + ":");
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException e) {
            System.err.println("Exception calling the constructor " + heuristicName + "():");
            e.printStackTrace();
            System.exit(1);
        }

        return heuristic;
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
}
