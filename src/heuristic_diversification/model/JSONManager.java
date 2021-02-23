package heuristic_diversification.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import heuristic_diversification.mapelites.MapElites;
import tools.com.google.gson.Gson;
import tools.com.google.gson.JsonElement;

public class JSONManager {
    public static void saveMAP(MapElites mapElitesGameplay, String fileName) {
        Gson gson = new Gson();

        JsonElement jsonElement = gson.toJsonTree(mapElitesGameplay);
        String jsonString = gson.toJson(jsonElement);
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


