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
import tools.com.google.gson.JsonElement;
import tools.com.google.gson.JsonObject;

public class JSONManager {

    public static void saveMapElitesGameplay(Config configData, MapElites mapElitesGameplay) {
        Gson gson = new Gson();

        JsonObject jsonObject = new JsonObject();
        JsonElement configDataJson = gson.toJsonTree(configData);
        JsonElement mapElitesJson = gson.toJsonTree(mapElitesGameplay);
        
        jsonObject.add("config", configDataJson);
        jsonObject.add("result", mapElitesJson);

        String jsonString = gson.toJson(jsonObject);
        writeToFile(jsonString, configData.resultsFileName() + ".json");
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


