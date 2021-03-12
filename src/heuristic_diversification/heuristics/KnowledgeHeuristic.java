/**
 * Author: Cristina Guerrero
 * Date: 26th February 2021
 */

package heuristic_diversification.heuristics;

import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.TreeSet;

import core.game.Event;
import core.game.Game;
import core.game.StateObservation;
import core.heuristic.StateHeuristic;
import heuristic_diversification.model.GameStats;

public class KnowledgeHeuristic extends StateHeuristic {
    protected static SpritesData mSpritesData;
    protected boolean updateSpritesData;

    public KnowledgeHeuristic() {
        this.updateSpritesData = false;
    }

    public void setUpdateSpritesData() {
        this.updateSpritesData = true;
    }

    @Override
    public void initHeuristicInternalInformation(StateObservation stateObs) {
        super.initHeuristicInternalInformation(stateObs);

        // Initialisations related to the KnowledgeHeuristic
        if (updateSpritesData) {
            mSpritesData = new SpritesData(stateObs);
        }
    }

    @Override
    public void updateHeuristicInternalInformation(StateObservation stateObs) {
        super.updateHeuristicInternalInformation(stateObs);

        if (updateSpritesData) {
            acknowledgeSprites(mSpritesData, stateObs);
            updateInteractionHistory(mSpritesData, stateObs);

            if (DEBUG) {
                mSpritesData.printUpdatedSpritesData(stateObs.getGameTick() - 1);
            }
        }
    }

    @Override
    public void restartFutureStateData(StateObservation stateObs) {
        super.restartFutureStateData(stateObs);
    }

    @Override
    public void updateFutureStateData(StateObservation stateObs) {
       super.updateFutureStateData(stateObs);
    }

    @Override
    public double evaluateState(StateObservation stateObs) {
        double h = 0;
        return h;
    }

    @Override
    public String relevantInfoStr(StateObservation stateObs) {
        return "string with info";
    }

    @Override
    public void recordGameStats(Game game, GameStats gameStats) {
        // TODO Auto-generated method stub
    }

    protected void acknowledgeSprites(SpritesData spritesData, StateObservation stateObs) {
        int gameTick = stateObs.getGameTick();

        // GAME SPRITES

        // NPC sprites: getNPCPositions
        spritesData.updateGameSprites(stateObs.getNPCPositions(), gameTick);

        // Fixed sprites: getImmovablePositions
        spritesData.updateGameSprites(stateObs.getImmovablePositions(), gameTick);

        // Movable sprites: getMovablePositions
        spritesData.updateGameSprites(stateObs.getMovablePositions(), gameTick);

        // Resources sprites: getResourcesPositions
        spritesData.updateGameSprites(stateObs.getResourcesPositions(), gameTick);
        
        // Portal sprites: getPortalsPositions
        spritesData.updateGameSprites(stateObs.getPortalsPositions(), gameTick);
       
        // PLAYER SPRITES

        // From avatar sprites: getFromAvatarSpritesPositions
        spritesData.updatePlayerSprites(stateObs.getFromAvatarSpritesPositions(), gameTick);
    }

    protected void updateInteractionHistory(SpritesData spritesData, StateObservation stateObs) {
        TreeSet<Event> eventsHistory = stateObs.getEventsHistory();
    
        // We are interested on events that just occured, so they happen in the previous gameTick
        int gameTick = stateObs.getGameTick() - 1;

        // We are only interested in the events that occurred in the current state
        Iterator<Event> eventsIterator = eventsHistory.descendingSet().iterator();
        while (eventsIterator.hasNext()){
            Event event = (Event) eventsIterator.next();
            if (event.gameStep != gameTick){
                // the next events are from previous states so no need to keep iterating
                break;
            }
            //System.out.println("Event: "+gameTick);
            if (event.fromAvatar) {
                // the interaction is indirect: it is a hit
                //System.out.println("HIT");
                spritesData.updateHitHistory(event, gameTick);
            } else {
                // the avatar collides with a sprite: it is a collision
                //System.out.println("COLLISION");
                spritesData.updateCollisionHistory(event, gameTick);
            }
        }
    }

    @Override
    public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds) {
        // No need to record the data
        return;
    }

    @Override
    public void drawInScreen(Graphics2D g) {
         // No need to draw anything on screen
         return;
    }
}
