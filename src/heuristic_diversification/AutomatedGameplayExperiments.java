/**
 * Author: Cristina Guerrero
 * Date: 16 July 2021
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
import heuristic_diversification.model.GameStats;
import tools.com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.Arrays;

public class AutomatedGameplayExperiments {
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
        private Boolean visuals = false;
        private int nGameRuns = 1;
        private int nLevels = 1;

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

        public Boolean getVisuals() { return this.visuals; }

        public int getNGameRuns() { return this.nGameRuns; }

        public int getNLevels() {
            return this.nLevels;
        }

        public String getBehavioursString() {
            return Arrays.toString(this.getEnabledBehaviours());
        }

        public String getWeightsString(String separator) {
            String weights = "";
            Double[] heuristicsWeightList = this.getEnabledBehavioursWeightList();
            for (int i = 0; i < (heuristicsWeightList.length - 1); i++) {
                weights += heuristicsWeightList[i] + separator;
            }
            weights += heuristicsWeightList[heuristicsWeightList.length - 1];
            return weights;
        }

        public Integer getCurrentLvl(int lvlIterator) {
            return this.getLevel() + lvlIterator;
        }

        public String printConfigInfo(int lvlIterator) {
            String text = "Automated gameplay of "+this.getGame()+" level "+this.getCurrentLvl(lvlIterator) + ". ";
            if (this.experimentId != null) {
                text += "\nData from experiment "  + this.getExperimentId();
            }
            text += "\nBehaviours: " + this.getBehavioursString();
            text += "\nWeights: " + "[" + this.getWeightsString(", ") + "]";
            text += "\nVisuals "  + this.getVisuals();
            text += "\nNumber of game runs: "  + this.getNGameRuns();
            text += "\n";
            return  text;
        }

        public String statsFileName(int lvlIterator) {
            return "Stats_" + this.getExperimentId() + "_" + this.getGame().getGameName() + "_" + this.getCurrentLvl(lvlIterator) + "_" + this.getWeightsString("_") + ".txt";
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
        System.out.println("Config file: " + jsonPath +"\n");

        try (Reader reader = new FileReader(jsonPath)) {
            AutomatedGameplayConfig gameplayConfig = gson.fromJson(reader,AutomatedGameplayConfig.class);

            // Enable behaviours requested
            for (String behaviourString : gameplayConfig.getEnabledBehaviours()) {
                Behaviours behaviour = Behaviours.valueOf(behaviourString);
                behaviour.enableBehaviour();
            }

            System.out.println("Behaviours set for the agent: ");
            for (Behaviours b : Behaviours.values()) {
                System.out.println(b +" -> "+ b.isEnabled());
            }
            System.out.println();

            // Game and framework set up
            boolean saveActionFile = false;

            int lvlIterator = 0;
            System.out.println(gameplayConfig.printConfigInfo(lvlIterator));

            while(lvlIterator < gameplayConfig.getNLevels()) {
                Config configData = new Config(gameplayConfig.getGame(), gameplayConfig.getCurrentLvl(lvlIterator), Agents.valueOf(AGENT), gameplayConfig.getNGameRuns(), gameplayConfig.getVisuals(), saveActionFile);
                FrameworkConfig fwConfig = configData.getFrameworkConfig();

                // Team set up
                TeamBehavioursHeuristic teamBehaviouHeuristic = TeamManager.createTeamBehaviourHeuristic(gameplayConfig.getEnabledBehavioursWeightList());
                TeamGameplay gameplayFramework = new TeamGameplay(teamBehaviouHeuristic, fwConfig);

                // Automated gameplay
                System.out.println("Running automated gameplay of lvl " + gameplayConfig.getCurrentLvl(lvlIterator) + "...\n");
                GameStats gameStats = gameplayFramework.createStatsFromGameplay(fwConfig.agent.getAgentFileName());
                
                String resultsStatsFile = gameplayConfig.statsFileName(lvlIterator);
                if (resultsStatsFile != null) {
                    BufferedWriter writer;
                    try {{
                            writer = new BufferedWriter(new FileWriter(new File(resultsStatsFile), true));
                            writer.write(gameplayConfig.printConfigInfo(lvlIterator));
                            writer.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    gameStats.printStats(resultsStatsFile);
                }
                
                System.out.println("Automated gameplay of lvl "+ gameplayConfig.getCurrentLvl(lvlIterator) +"finished");
                lvlIterator++;
            }
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
