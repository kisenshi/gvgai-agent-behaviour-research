/**
 * Author: Cristina Guerrero
 * Date: 23rd February 2021
 */

package heuristic_diversification.mapelites;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import com.sun.org.apache.xpath.internal.operations.Bool;
import heuristic_diversification.config.Agents;
import heuristic_diversification.config.Games;

public class Config {
    private static final String EXPERIMENT_KEY = "experimentId";

    private static final String GAME_KEY = "gameName";
    private static final String LEVEL_KEY = "level";
    private static final String AGENT_KEY = "agentName";
    private static final String N_GAME_RUNS_KEY = "nGameRuns";
    private static final String VISUALS_KEY = "withVisuals";
    private static final String ACTION_FILE_KEY = "saveActionFile";

    private static final String PERFORMANCE_CRITERIA_KEY = "perfomanceCriteria";
    private static final String FEATURE_X_KEY = "featureX";
    private static final String FEATURE_Y_KEY = "featureY";
    private static final String N_MAP_INITIALISATION_KEY = "nRandomInitialisations";
    private static final String N_ALGORITHM_ITERATIONS_KEY = "nAlgorithmIterations";

    private final String experimentId; 
    private FrameworkConfig frameworkConfig;
    private MapElitesConfig mapElitesConfig;

    public class FrameworkConfig {
        public final Games game;
        public final int level;
        public final Agents agent;
        public final int nGameRuns;
        public final boolean visuals;
        private final boolean saveActionFile;

        private FrameworkConfig(Properties configProperties) {
            this.game = Games.valueOf(configProperties.getProperty(GAME_KEY));
            this.level = Integer.parseInt(configProperties.getProperty(LEVEL_KEY));
            this.agent = Agents.valueOf(configProperties.getProperty(AGENT_KEY));
            this.nGameRuns = Integer.valueOf(configProperties.getProperty(N_GAME_RUNS_KEY));
            this.visuals = Boolean.valueOf(configProperties.getProperty(VISUALS_KEY));
            this.saveActionFile = Boolean.valueOf(configProperties.getProperty(ACTION_FILE_KEY));
        }

        public FrameworkConfig(Games game, Integer level, Agents agent, Integer nGameRuns, Boolean visuals, Boolean saveActionFile) {
            this.game = game;
            this.level = level;
            this.agent = agent;
            this.nGameRuns = nGameRuns;
            this.visuals = visuals;
            this.saveActionFile = saveActionFile;
        }

        public String actionFile() {
            if (!saveActionFile) {
                return null;
            }

            return resultsFileName() + "_ActionFile";
        }
    }

    public class MapElitesConfig {
        public final Performance performanceCriteria;
        public final Features featureX;
        public final Features featureY;
        public final int nRandomInitialisations;
        public final int nMapElitesIterations;

        private MapElitesConfig(Properties configProperties) {
            this.performanceCriteria = Performance.valueOf(configProperties.getProperty(PERFORMANCE_CRITERIA_KEY));
            this.featureX = Features.valueOf(configProperties.getProperty(FEATURE_X_KEY));
            this.featureY = Features.valueOf(configProperties.getProperty(FEATURE_Y_KEY));
            this.nRandomInitialisations = Integer.parseInt(configProperties.getProperty(N_MAP_INITIALISATION_KEY));
            this.nMapElitesIterations = Integer.parseInt(configProperties.getProperty(N_ALGORITHM_ITERATIONS_KEY));
        }
    }

    public Config(Games game, Integer level, Agents agent, Integer nGameRuns, Boolean visuals, Boolean saveActionFile) {
        experimentId = "";

        frameworkConfig = new FrameworkConfig(game, level, agent, nGameRuns, visuals, saveActionFile);
    }

    public Config(String configFile) {
        Properties configProperties = new Properties();

        try {
            configProperties.load(new FileInputStream(configFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        experimentId = configProperties.getProperty(EXPERIMENT_KEY);
        frameworkConfig = new FrameworkConfig(configProperties);
        mapElitesConfig = new MapElitesConfig(configProperties);
    }

    public FrameworkConfig getFrameworkConfig() {
        return frameworkConfig;
    }

    public MapElitesConfig getMapElitesConfig() {
        return mapElitesConfig;
    }

    public  String resultsFileName() {
        return "MAPElitesGameplay_" + experimentId + "_" + frameworkConfig.game.getGameName() + "_lvl" + frameworkConfig.level + "_x_" + mapElitesConfig.featureX.name() + "_y_" + mapElitesConfig.featureY.name() + "_perf_" + mapElitesConfig.performanceCriteria.name() + "_" + frameworkConfig.nGameRuns + "_" + mapElitesConfig.nMapElitesIterations;
    }

    private static String gameValuesString() {
        String s = "";
        boolean firstElement = true;
        for (Games game : Games.values()) {
            if (firstElement) {
                firstElement = false;
            } else {
                s += "/";
            }
            s += game.name();
        }
        return s;
    }

    private static String agentValuesString() {
        String s = "";
        boolean firstElement = true;
        for (Agents agent : Agents.values()) {
            if (firstElement) {
                firstElement = false;
            } else {
                s += "/";
            }
            s += agent.name();
        }
        return s;
    }

    private static String performanceValuesString() {
        String s = "";
        boolean firstElement = true;
        for (Performance performance : Performance.values()) {
            if (firstElement) {
                firstElement = false;
            } else {
                s += "/";
            }
            s += performance.name();
        }
        return s;
    }

    private static String featureValuesString() {
        String s = "";
        boolean firstElement = true;
        for (Features feature : Features.values()) {
            if (firstElement) {
                firstElement = false;
            } else {
                s += "/";
            }
            s += feature.name();
        }
        return s;
    }

    public static void generateTemplateFile() {
        BufferedWriter writer;
        String fileName = "MapElitesGameplayConfigTemplate.txt";
        try {
            writer = new BufferedWriter(new FileWriter(new File(fileName), true));

            writer.write(EXPERIMENT_KEY + "=XXXX\n");
            writer.write(GAME_KEY + "=" + gameValuesString() + "\n");
            writer.write(LEVEL_KEY + "=0/1/2...\n");
            writer.write(AGENT_KEY + "=" + agentValuesString() + "\n");
            writer.write(N_GAME_RUNS_KEY + "=1/2/3...\n");
            writer.write(VISUALS_KEY + "=true/false\n");
            writer.write(ACTION_FILE_KEY + "=true/false\n");
            writer.write(PERFORMANCE_CRITERIA_KEY + "=" + performanceValuesString() + "\n");
            writer.write(FEATURE_X_KEY + "=" + featureValuesString() + "\n");
            writer.write(FEATURE_Y_KEY + "=" + featureValuesString() + "\n");
            writer.write(N_MAP_INITIALISATION_KEY + "=1/2/3...\n");
            writer.write(N_ALGORITHM_ITERATIONS_KEY + "=1/2/3...\n");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
