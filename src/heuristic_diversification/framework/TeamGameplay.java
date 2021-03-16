/**
 * Author: Cristina Guerrero
 * Date: 16th February 2021
 */

package heuristic_diversification.framework;

import java.util.HashMap;

import heuristic_diversification.config.Games;
import heuristic_diversification.heuristics.TeamBehavioursHeuristic;
import heuristic_diversification.mapelites.Config.FrameworkConfig;
import heuristic_diversification.mapelites.Features;
import heuristic_diversification.mapelites.Performance;
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

    public HashMap<Integer, String> getSpriteDetails() {
        Games gameInfo = frameworkConfig.game;

        HashMap<Integer, String> spriteDetails = ArcadeMachineHeuristic.getGameStypesInfo(gameInfo.game());
        return spriteDetails;
    }

    public GameStats createStatsFromGameplay(String controller) {
        Games gameInfo = frameworkConfig.game;
        int levelId = frameworkConfig.level;
    
        GameStats gameStats = new GameStats(gameInfo.levelNavigationSize(levelId));
        ArcadeMachineHeuristic.runGameAndGetStats(gameStats, gameInfo.game(), gameInfo.level(levelId), frameworkConfig.visuals, controller, frameworkConfig.actionFile(), teamBehaviouHeuristic, frameworkConfig.nGameRuns);
        gameStats.calculateStats();
        return gameStats;
    }

    public GameStats createMapEliteStatsFromGameplay(String controller, Performance performance, Features[] features) {
        Games gameInfo = frameworkConfig.game;
        int levelId = frameworkConfig.level;
    
        GameStats gameStats = new GameStats(gameInfo.levelNavigationSize(levelId));
        ArcadeMachineHeuristic.runGameAndGetStats(gameStats, gameInfo.game(), gameInfo.level(levelId), frameworkConfig.visuals, controller, frameworkConfig.actionFile(), teamBehaviouHeuristic, frameworkConfig.nGameRuns);
        
        // For the map elited algorithm iteration, only the stats of the performance and features fields are needed so we only calculate those for now
        performance.calculatePerfomanceStat(gameStats);
        for (Features feature : features) {
            feature.calculateFeatureStat(gameStats);
        }

        return gameStats;
    }
}