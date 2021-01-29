package heuristic_diversification.helper;

import core.heuristic.StateHeuristic;
import tracks.ArcadeMachine;

public enum Behaviours {

    WINNER(0, "WinningAndScoreHeuristic"), 
    EXPLORER(1, "ExplorationHeuristic");

    int id;
    String fileName;

    private static final String HEURISTICS_PATH = "heuristic_diversification.heuristics.";

    Behaviours(int id, String fileName) {
        this.id = id;
        this.fileName = fileName;
    }

    public StateHeuristic getHeuristicInstance(){
        String heuristicPath = HEURISTICS_PATH + fileName;
        StateHeuristic heuristic = ArcadeMachine.createHeuristic(heuristicPath);
        return heuristic;
    }

    public int id(){
        return id;
    }
}

