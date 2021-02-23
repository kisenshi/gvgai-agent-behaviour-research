package heuristic_diversification.framework;

import heuristic_diversification.config.Games;
import heuristic_diversification.heuristics.TeamBehavioursHeuristic;
import heuristic_diversification.mapelites.Config.FrameworkConfig;
import heuristic_diversification.model.GameStats;

/**
 * This class contains the methods that allow running a game with a certain
 * agent and heuristics in the current framework: GVGAI Allows obtaining
 * gameStats and make the MapElites class as transparent to the framework as
 * possible.
 */
public class TeamGameplay {

    private TeamBehavioursHeuristic teamBehaviouHeuristic;
    FrameworkConfig frameworkConfig;

    public TeamGameplay(TeamBehavioursHeuristic teamBehaviouHeuristic, FrameworkConfig frameworkConfig) {
        this.teamBehaviouHeuristic = teamBehaviouHeuristic;
        this.frameworkConfig = frameworkConfig;
    }

    public GameStats createStatsFromGameplay(String controller) {
        Games gameInfo = frameworkConfig.game;
        int levelId = frameworkConfig.level;
    
        GameStats gameStats = new GameStats(gameInfo.levelNavigationSize(levelId));
        ArcadeMachineHeuristic.runGameAndGetStats(gameStats, gameInfo.game(), gameInfo.level(levelId), frameworkConfig.visuals, controller, frameworkConfig.actionFile(), teamBehaviouHeuristic, frameworkConfig.nGameRuns);
        gameStats.calculateStats();
        return gameStats;
    }
}