/**
 * Author: Cristina Guerrero
 * Date: 16th March 2021
 */

package heuristic_diversification.model;

import java.util.HashMap;

import heuristic_diversification.framework.TeamGameplay;

public class GameInfo {
    HashMap<Integer, String> spriteDetails;

    public GameInfo(TeamGameplay gameplayFramework) {
        this.spriteDetails = gameplayFramework.getSpriteDetails();
    }
}
