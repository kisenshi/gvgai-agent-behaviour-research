/**
 * Author: Cristina Guerrero
 * Date: 16th February 2021
 */

package heuristic_diversification;

import heuristic_diversification.framework.TeamGameplay;
import heuristic_diversification.framework.TeamManager;
import heuristic_diversification.heuristics.TeamBehavioursHeuristic;
import heuristic_diversification.mapelites.Config;
import heuristic_diversification.mapelites.Config.FrameworkConfig;
import heuristic_diversification.mapelites.Config.MapElitesConfig;
import heuristic_diversification.mapelites.MapElites;
import heuristic_diversification.model.GameInfo;
import heuristic_diversification.model.JSONManager;

public class MapElitesGameplay {
    public static void main(String[] args) {
        // Expect config file name as argument
        if (args.length != 1) {
            System.out.println("Error: Please provide config file name as argument");
            System.exit(1);
        }
        String configFile = args[0];

        // Read config file and initialisations needed for the algorithm and running agents
        Config configData = new Config(configFile);
        FrameworkConfig fwConfig = configData.getFrameworkConfig();
        MapElitesConfig mapElitesConfig = configData.getMapElitesConfig();

        // Team set up
        Double heuristicsWeightList[] = TeamManager.createTeamBehaviourWeightList();
        TeamBehavioursHeuristic teamBehaviouHeuristic = TeamManager.createTeamBehaviourHeuristic(heuristicsWeightList);
 
        TeamGameplay gameplayFramework = new TeamGameplay(teamBehaviouHeuristic, fwConfig);

        // MAP elites adaptation

        // Initialise MAP
        MapElites mapElites = new MapElites(mapElitesConfig.performanceCriteria, mapElitesConfig.featureX, mapElitesConfig.featureY, gameplayFramework, fwConfig.agent.getAgentFileName(), heuristicsWeightList, mapElitesConfig.nRandomInitialisations);

        // MAP elites algorithm
        mapElites.runAlgorithm(mapElitesConfig.nMapElitesIterations);

        // Log results
        String statsResultsFileName = configData.resultsFileName() + "_Stats";
        mapElites.printMapElitesInfo(statsResultsFileName);
        
        // Generate JSON
        GameInfo gameInfo = new GameInfo(gameplayFramework);
        JSONManager.saveMapElitesGameplay(configData, mapElites, gameInfo);
    }
}
