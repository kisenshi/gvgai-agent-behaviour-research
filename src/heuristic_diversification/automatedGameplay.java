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

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.Arrays;

public class automatedGameplay {
    // PATHS
    private static String CONTROLLERS_PATH = "heuristic_diversification.controllers.";
    private static String HEURISTICS_PATH = "heuristic_diversification.heuristics.";

    private static String AGENT = "MCTS";

    public class AutomatedGameplayConfig {
        private String experimentId;
        private String game;
        private int level;
        private String[] behaviours;
        private Double[] weights;

        public String getExperimentId() { return this.experimentId; }

        public Games getGame(){
            return Games.valueOf(this.game);
        }

        public int getLevel() {
            return this.level;
        }

        public String[] getEnabledBehaviours() {
            return this.behaviours;
        }

        public Double[] getEnabledBehavioursWeightList() {
            return this.weights;
        }

        public String printConfigInfo() {
            String text = "Automated gameplay of "+this.getGame()+" level "+this.getLevel() + ". ";
            if (this.experimentId != null) {
                text += "\nData from experiment "  + this.getExperimentId();
            }
            text += "\nBehaviours: " + Arrays.toString(this.getEnabledBehaviours());
            text += "\nWeights: " + Arrays.toString(this.getEnabledBehavioursWeightList());
            text += "\n";
            return  text;
        }
    }

    public static void main(String[] args) {
        // Expect json file name as argument
        if (args.length != 1) {
            System.out.println("Automated gameplay error: Please provide json config file name as argument");
            System.exit(1);
        }

        String configJsonFile = args[0];
        Gson gson = new Gson();

        String jsonPath = String.valueOf(Paths.get(configJsonFile));
        System.out.println(jsonPath);
        try (Reader reader = new FileReader(jsonPath)) {
            AutomatedGameplayConfig gameplayConfig = gson.fromJson(reader,AutomatedGameplayConfig.class);
            System.out.println(gameplayConfig.printConfigInfo());

            /*String game = "DIGDUG";
            String[] enabledBehaviours = {"WINNER", "EXPLORER", "CURIOUS", "KILLER", "COLLECTOR"};
            Double[] enabledBehavioursWeightList = {0.5, 0.02, 0.5, 1.0, 0.0};*/

            // Enable behaviours requested
            for (String behaviourString : gameplayConfig.getEnabledBehaviours()) {
                Behaviours behaviour = Behaviours.valueOf(behaviourString);
                behaviour.enableBehaviour();
            }

            System.out.println("Behaviours set for the agent: ");
            for (Behaviours b : Behaviours.values()) {
                System.out.println(b +" ->"+ b.isEnabled());
            }
            System.out.println();

            // Game and framework set up
            boolean visuals = true;
            boolean saveActionFile = false;

            Config configData = new Config(gameplayConfig.getGame(), gameplayConfig.getLevel(), Agents.valueOf(AGENT), 1, visuals, saveActionFile);
            FrameworkConfig fwConfig = configData.getFrameworkConfig();

            // Team set up
            TeamBehavioursHeuristic teamBehaviouHeuristic = TeamManager.createTeamBehaviourHeuristic(gameplayConfig.getEnabledBehavioursWeightList());
            TeamGameplay gameplayFramework = new TeamGameplay(teamBehaviouHeuristic, fwConfig);

            // Automated gameplay
            System.out.println("Running automated gameplay...\n");
            gameplayFramework.createStatsFromGameplay(fwConfig.agent.getAgentFileName());
            System.out.println("Automated gameplay finished");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
