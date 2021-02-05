package heuristic_diversification.config;

public enum Agents {

    OSLA(0, "sampleonesteplookahead.Agent"),
    MCTS(1, "sampleMCTS.Agent");

    int id;
    String fileName;

    private static final String CONTROLLERS_PATH = "heuristic_diversification.controllers.";

    Agents(int id, String fileName) {
        this.id = id;
        this.fileName = fileName;
    }

    public String getAgentName() {
        return CONTROLLERS_PATH + fileName;
    }

    public int id() {
        return id;
    }
    
}
