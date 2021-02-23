package heuristic_diversification;

import core.heuristic.StateHeuristic;
import heuristic_diversification.config.Behaviours;
import heuristic_diversification.framework.ArcadeMachineHeuristic;
import heuristic_diversification.framework.TeamGameplay;
import heuristic_diversification.heuristics.TeamBehavioursHeuristic;
import heuristic_diversification.mapelites.Config;
import heuristic_diversification.mapelites.Config.FrameworkConfig;
import heuristic_diversification.mapelites.Config.MapElitesConfig;
import heuristic_diversification.mapelites.MapElites;
import heuristic_diversification.model.JSONManager;

public class MapElitesGameplay {
    public static void main(String[] args) {
        // Initialisations needed for algorithm and running agents
        Config configData = new Config("configFileTestQuick.txt");
        FrameworkConfig fwConfig = configData.getFrameworkConfig();
        MapElitesConfig mapElitesConfig = configData.getMapElitesConfig();

        // Team initialisation
        String team = Behaviours.HEURISTICS_PATH + "TeamBehavioursHeuristic";

        int nHeuristics = Behaviours.values().length;
        StateHeuristic heuristicsList[] = new StateHeuristic[nHeuristics];
        Double heuristicsWeightList[] = new Double[nHeuristics];

        for (Behaviours info : Behaviours.values()) {
            heuristicsList[info.id()] = info.getHeuristicInstance();
        }

        Class[] heuristicArgsClass = new Class[]{heuristicsList.getClass(), heuristicsWeightList.getClass()};
        Object[] constructorArgs = new Object[]{heuristicsList, heuristicsWeightList};
        TeamBehavioursHeuristic teamBehaviouHeuristic = (TeamBehavioursHeuristic) ArcadeMachineHeuristic.createHeuristicWithArgs(team, heuristicArgsClass, constructorArgs);

        TeamGameplay gameplayFramework = new TeamGameplay(teamBehaviouHeuristic, fwConfig);

        // MAP elites adaptation

        // Initialise MAP
        MapElites mapElites = new MapElites(mapElitesConfig.performanceCriteria, mapElitesConfig.featureX, mapElitesConfig.featureY, gameplayFramework, fwConfig.agent.getAgentFileName(), heuristicsWeightList, mapElitesConfig.nInitialCells);

        // MAP elites algorithm
        mapElites.runAlgorithm(mapElitesConfig.nMapElitesIterations);

        // Print results
        mapElites.printMapElitesInfo();
        
        JSONManager.saveMapElitesGameplay(configData, mapElites);
    }
}
