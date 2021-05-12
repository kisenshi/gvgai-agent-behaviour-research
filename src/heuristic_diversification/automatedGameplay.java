/**
 * Author: Cristina Guerrero
 * Date: 07 May 2021
 */

package heuristic_diversification;

import heuristic_diversification.config.Agents;
import heuristic_diversification.config.Behaviours;
import heuristic_diversification.config.Games;
import heuristic_diversification.framework.TeamGameplay;
import heuristic_diversification.framework.TeamManager;
import heuristic_diversification.heuristics.TeamBehavioursHeuristic;
import heuristic_diversification.mapelites.Config;
import heuristic_diversification.mapelites.Config.FrameworkConfig;

import tools.com.google.gson.Gson;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class automatedGameplay {
    // PATHS
    private static String CONTROLLERS_PATH = "heuristic_diversification.controllers.";
    private static String HEURISTICS_PATH = "heuristic_diversification.heuristics.";

    private static String AGENT = "MCTS";

    public class AutomatedGameplayConfig {
        private String game;
        private int level;
        private String[] enabledBehaviours;
        private Double[] enabledBehavioursWeightList;

       // public AutomatedGameplayConfig(){}

        public AutomatedGameplayConfig(String game, int level, String[] behaviours, Double[] weights) {
            this.game = game;
            this.level = level;
            this.enabledBehaviours = behaviours;
            this.enabledBehavioursWeightList = weights;
        }

        public Games getGame(){
            return Games.valueOf(this.game);
        }

        public int getLevel() {
            return this.level;
        }

        public String[] getBehaviours() {
            return this.enabledBehaviours;
        }

        public Double[] getWeights() {
            return this.enabledBehavioursWeightList;
        }
    }

    public static void main(String[] args) {
        // Expect json file name as argument
        if (args.length != 1) {
            System.out.println("Automated gameplay error: Please provide json config file name as argument");
            System.exit(1);
        }

        String configJsonFile = args[0];
        AutomatedGameplayConfig gameplayConfig = null;

        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(configJsonFile));
            gameplayConfig = gson.fromJson(reader,AutomatedGameplayConfig.class);
            reader.close();

            System.out.println(gameplayConfig);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        /*String game = "DIGDUG";
        String[] enabledBehaviours = {"WINNER", "EXPLORER", "CURIOUS", "KILLER", "COLLECTOR"};
        Double[] enabledBehavioursWeightList = {0.5, 0.02, 0.5, 1.0, 0.0};*/

        // Enable behaviours requested
        for (String behaviourString : gameplayConfig.getBehaviours()) {
            Behaviours behaviour = Behaviours.valueOf(behaviourString);
            behaviour.enableBehaviour();
        }
        
        for (Behaviours b : Behaviours.values()) {
            System.out.println("Behaviours set for the agent: ");
            System.out.println(b +" ->"+ b.isEnabled());
        }

        // Game and framework set up
        int level = 0;
        boolean visuals = true;
        boolean saveActionFile = false;

        Config configData = new Config(gameplayConfig.getGame(), gameplayConfig.getLevel(), Agents.valueOf(AGENT), 1, visuals, saveActionFile);
        FrameworkConfig fwConfig = configData.getFrameworkConfig();
        
        // Team set up
        TeamBehavioursHeuristic teamBehaviouHeuristic = TeamManager.createTeamBehaviourHeuristic(gameplayConfig.getWeights());
        TeamGameplay gameplayFramework = new TeamGameplay(teamBehaviouHeuristic, fwConfig);

        // Automated gameplay
        System.out.println("Running automated gameplay");
        gameplayFramework.createStatsFromGameplay(fwConfig.agent.getAgentFileName());
        System.out.println("Automated gameplay finished");
    }
}
