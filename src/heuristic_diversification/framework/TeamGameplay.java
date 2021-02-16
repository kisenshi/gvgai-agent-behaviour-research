package heuristic_diversification.framework;

import heuristic_diversification.heuristics.TeamBehavioursHeuristic;
import heuristic_diversification.model.GameStats;

/**
 * This class contains the methods that allow running a game with a certain
 * agent and heuristics in the current framework: GVGAI Allows obtaining
 * gameStats and make the MapElites class as transparent to the framework as
 * possible.
 */
public class TeamGameplay {

    private TeamBehavioursHeuristic teamBehaviouHeuristic;
    private String game;
    private String level;
    private String actionFile;
    private boolean visuals;
    private int nGameRuns;

    public TeamGameplay(TeamBehavioursHeuristic teamBehaviouHeuristic, String game, String level, String actionFile, Boolean visuals, int nGameRuns) {
        this.teamBehaviouHeuristic = teamBehaviouHeuristic;
        this.game = game;
        this.level = level;
        this.actionFile = actionFile;
        this.visuals = visuals;
        this.nGameRuns = nGameRuns;
    }

    public GameStats createStatsFromGameplay(String controller) {
        GameStats gameStats = new GameStats();
        ArcadeMachineHeuristic.runGameAndGetStats(gameStats, game, level, visuals, controller, actionFile, teamBehaviouHeuristic, nGameRuns);
        gameStats.calculateStats();
        return gameStats;
    }
    
}
