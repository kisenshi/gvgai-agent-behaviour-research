/**
 * Author: Cristina Guerrero
 * Date: 23rd February 2021
 */

package heuristic_diversification.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import heuristic_diversification.mapelites.Config;
import heuristic_diversification.mapelites.MapElites;
import tools.com.google.gson.Gson;
import tools.com.google.gson.GsonBuilder;
import tools.com.google.gson.JsonElement;
import tools.com.google.gson.JsonObject;

public class JSONManager {

    public static void saveMapElitesGameplay(Config configData, MapElites mapElitesGameplay, GameInfo gameInfo) {
        FeaturesInfo featureXInfo = new FeaturesInfo(configData.getMapElitesConfig().featureX);
        FeaturesInfo featureYInfo = new FeaturesInfo(configData.getMapElitesConfig().featureY);
        
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        JsonObject jsonFeaturesObject = new JsonObject();
        JsonElement featureXInfoJson = gson.toJsonTree(featureXInfo);
        JsonElement featureYInfoJson = gson.toJsonTree(featureYInfo);

        jsonFeaturesObject.add(configData.getMapElitesConfig().featureX.name(), featureXInfoJson);
        jsonFeaturesObject.add(configData.getMapElitesConfig().featureY.name(), featureYInfoJson);
        JsonElement faturesInfoJson = gson.toJsonTree(jsonFeaturesObject);


        JsonObject jsonObject = new JsonObject();
        JsonElement configDataJson = gson.toJsonTree(configData);
        JsonElement gameInfoJson = gson.toJsonTree(gameInfo);
        JsonElement mapElitesJson = gson.toJsonTree(mapElitesGameplay);
        
        jsonObject.add("config", configDataJson);
        jsonObject.add("gameInfo", gameInfoJson);
        jsonObject.add("featuresDetails", faturesInfoJson);
        jsonObject.add("result", mapElitesJson);

        String jsonString = gson.toJson(jsonObject);
        writeToFile(jsonString, configData.resultsFileName() + ".json");
    }

    public static void prinObjectAsJson(Object object, String fileName) {
        Gson gson = new Gson();

        JsonElement jsonObject = gson.toJsonTree(object);
        String jsonString = gson.toJson(jsonObject);
        writeToFile(jsonString, fileName + ".json");
    }

    private static void writeToFile(String content, String fileName) {
        BufferedWriter writer;
        try {
            if (fileName != null && !fileName.equals("")) {
                writer = new BufferedWriter(new FileWriter(new File(fileName), true));
                writer.write(content);
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


