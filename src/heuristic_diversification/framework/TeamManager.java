package heuristic_diversification.framework;

import java.util.Arrays;

import heuristic_diversification.config.Behaviours;
import heuristic_diversification.heuristics.TeamBehavioursHeuristic;

public class TeamManager {
    private Behaviours enabledBehaviours[];

    public TeamManager() {
        int nEnabledHeuristics = (int) Arrays.stream(Behaviours.values()).filter(h -> h.isEnabled()).count();
        enabledBehaviours = new Behaviours[nEnabledHeuristics];

        int i=0;
        for (Behaviours behaviour : Behaviours.values()) {
            if (behaviour.isEnabled()) {
                enabledBehaviours[i] = behaviour;
                i++;
            }
        }
    }

    public static TeamBehavioursHeuristic createTeamBehaviourHeuristic(Double[] heuristicsWeightList) {
        // Team initialisation
        String team = Behaviours.HEURISTICS_PATH + "TeamBehavioursHeuristic";

        int nHeuristics = Behaviours.values().length;
        TeamMember[] heuristicsList = new TeamMember[nHeuristics];

        int i = 0;
        for (Behaviours behaviour : Behaviours.values()) {
            heuristicsList[i] = new TeamMember(behaviour);
            i++;
        }

        Class[] heuristicArgsClass = new Class[]{heuristicsList.getClass(), heuristicsWeightList.getClass()};
        Object[] constructorArgs = new Object[]{heuristicsList, heuristicsWeightList};
        TeamBehavioursHeuristic teamBehaviouHeuristic = (TeamBehavioursHeuristic) ArcadeMachineHeuristic.createHeuristicWithArgs(team, heuristicArgsClass, constructorArgs);

        return teamBehaviouHeuristic;
    }

    public static Double[] createTeamBehaviourWeightList() {
        int nEnabledHeuristics = (int) Arrays.stream(Behaviours.values()).filter(h -> h.isEnabled()).count();
        Double heuristicsWeightList[] = new Double[nEnabledHeuristics];

        return heuristicsWeightList;
    }
}
