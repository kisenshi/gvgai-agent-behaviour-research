package heuristic_diversification.framework;

import heuristic_diversification.config.Games;
import heuristic_diversification.heuristics.TeamBehavioursHeuristic;
import heuristic_diversification.model.GameStats;

/**
 * This class contains the methods that allow running a game with a certain
 * agent and heuristics in the current framework: GVGAI Allows obtaining
 * gameStats and make the MapElites class as transparent to the framework as
 * possible.
 */
public class TeamGameplay {

    transient private TeamBehavioursHeuristic teamBehaviouHeuristic;
    private Games gameInfo;
    private int levelId;
    private String actionFile;
    transient private boolean visuals;
    private int nGameRuns;

    public TeamGameplay(TeamBehavioursHeuristic teamBehaviouHeuristic, Games gameInfo, int levelId, String actionFile, Boolean visuals, int nGameRuns) {
        this.teamBehaviouHeuristic = teamBehaviouHeuristic;
        this.gameInfo = gameInfo;
        this.levelId = levelId;
        this.actionFile = actionFile;
        this.visuals = visuals;
        this.nGameRuns = nGameRuns;
    }

    public GameStats createStatsFromGameplay(String controller) {
        GameStats gameStats = new GameStats(gameInfo.levelNavigationSize(levelId));
        ArcadeMachineHeuristic.runGameAndGetStats(gameStats, gameInfo.game(), gameInfo.level(levelId), visuals, controller, actionFile, teamBehaviouHeuristic, nGameRuns);
        gameStats.calculateStats();
        return gameStats;
    }
}
