/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package heuristic_diversification.config;

public enum Agents {

    OSLA("sampleonesteplookahead.Agent"),
    MCTS("sampleMCTS.Agent");

    String fileName;

    private static final String CONTROLLERS_PATH = "heuristic_diversification.controllers.";

    Agents(String fileName) {
        this.fileName = fileName;
    }

    public String getAgentFileName() {
        return CONTROLLERS_PATH + fileName;
    }
}
