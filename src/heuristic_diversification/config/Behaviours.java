package heuristic_diversification.config;

import core.heuristic.StateHeuristic;
import heuristic_diversification.framework.ArcadeMachineHeuristic;

public enum Behaviours {

    WINNER(0, "WinningAndScoreHeuristic"), 
    EXPLORER(1, "ExplorationHeuristic");

    int id;
    String fileName;

    public static final String HEURISTICS_PATH = "heuristic_diversification.heuristics.";

    Behaviours(int id, String fileName) {
        this.id = id;
        this.fileName = fileName;
    }

    public StateHeuristic getHeuristicInstance() {
        String heuristicPath = HEURISTICS_PATH + fileName;
        StateHeuristic heuristic = ArcadeMachineHeuristic.createHeuristic(heuristicPath);
        return heuristic;
    }

    public int id() {
        return id;
    }
}

