package heuristic_diversification.framework;

import core.heuristic.StateHeuristic;
import heuristic_diversification.config.Behaviours;

public class TeamMember {
    public final StateHeuristic heuristic;
    private boolean enabled;
    
    public TeamMember(Behaviours behaviour) {
        this.heuristic = behaviour.getHeuristicInstance();
        this.enabled = behaviour.isEnabled();
    }

    public boolean isEnabled() {
        return enabled;
    }
    
}
