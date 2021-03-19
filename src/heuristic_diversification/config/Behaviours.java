/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package heuristic_diversification.config;

import core.heuristic.StateHeuristic;
import heuristic_diversification.framework.ArcadeMachineHeuristic;

public enum Behaviours {

    WINNER("WinningAndScoreHeuristic", true), 
    EXPLORER("ExplorationHeuristic", true),
    CURIOUS("CuriosityHeuristic", true),
    KILLER("KillingHeuristic", true),
    COLLECTOR("CollectionHeuristic", true);

    String fileName;
    boolean enabled;

    public static final String HEURISTICS_PATH = "heuristic_diversification.heuristics.";

    Behaviours(String fileName, boolean enabled) {
        this.fileName = fileName;
        this.enabled = enabled;
    }

    public StateHeuristic getHeuristicInstance() {
        String heuristicPath = HEURISTICS_PATH + fileName;
        StateHeuristic heuristic = ArcadeMachineHeuristic.createHeuristic(heuristicPath);
        return heuristic;
    }

    public boolean isEnabled() {
        return enabled;
    }
}

