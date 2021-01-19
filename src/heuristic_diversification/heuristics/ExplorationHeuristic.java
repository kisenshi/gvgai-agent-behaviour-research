/**
 * Author: Cristina Guerrero
 * Date: 18th January 2021
 */

package heuristic_diversification.heuristics;

import core.game.Game;
import core.game.StateObservation;
import core.heuristic.StateHeuristic;
import ontology.Types;
import tools.Vector2d;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Maximize the exploration of the map
 */

public class ExplorationHeuristic extends StateHeuristic {
    int block_size;
    int grid_width;
    int grid_height;
    boolean[][] exploration_matrix;
    Vector2d currentPosition;
    int last_discovered_tick = 0;
    int games_lvl_navigation_sizes[] = new int[]{
        30,     //"aliens",
        9,      //"bait",
        206,    //"butterflies",
        322,    //"camelRace",
        135,    //"chase",
        184,    //"chopper",
        333,    //"crossfire",
        405,    //"digdug",
        74,     //"escape",
        79,     //"hungrybirds",
        187,    //"infection",
        243,    //"intersection",
        222,    //"lemmings",
        242,    //"missilecommand",
        15,     //"modality",
        310,    //"plaqueattack",
        266,    //"roguelike",
        189,    //"seaquest",
        121,    //"survivezombies",
        50,     //"waitforbreakfast"
};

    public ExplorationHeuristic(StateObservation stateObs) {
        // When the class is instantiated it is needed to initialise the exploration matrix
        block_size = stateObs.getBlockSize();
        Dimension grid_dimension = stateObs.getWorldDimension();

        grid_width = grid_dimension.width / block_size;
        grid_height = grid_dimension.height / block_size;

        exploration_matrix = new boolean[grid_width][grid_height];

        markNewPositionAsVisited(stateObs);
    }

    public double evaluateState(StateObservation stateObs) {
        boolean gameOver = stateObs.isGameOver();

        // For this heuristic is penalised finishing the game, either when winning or losing it
        if (gameOver){
            return HUGE_NEGATIVE;
        }

        Vector2d avatarPosition = stateObs.getAvatarPosition();
        if (isOutOfBounds(avatarPosition)){
            // If the new position is out of bounds then dont go there
            return HUGE_NEGATIVE;
        }

        if (!hasBeenBefore(avatarPosition)){
            // If it hasnt been before, it is rewarded
            return 100;
        }

        // If it has been before, it is penalised
        if (avatarPosition.equals(currentPosition)){
            // As it is tried to reward exploration, it is penalised more if it is the last position visited
            return -50;
        }

        return -25;
    }

    public void updateHeuristicInternalInformation(StateObservation stateObs) {
        // Update the exploration matrix
        Vector2d avatarPosition = stateObs.getAvatarPosition();

        if (!isOutOfBounds(avatarPosition)){
            currentPosition = avatarPosition.copy();
            if (!hasBeenBefore(avatarPosition)) {
                markNewPositionAsVisited(stateObs);
            }
        }
       
    }

    public String relevantInfoStr(StateObservation stateObs) {
        return "exploration: " + getNSpotsExplored();
    }

    public void recordDataOnFile(Game played, String fileName, int randomSeed, int recordIds[]) {
        double explored = getNSpotsExplored();

        // Data:
        // gameId controllerId randomSeed winnerId score gameTicks mapSize nExplored navigationSize percentageExplored lastDiscoveredTick
        int gameId = recordIds[0];
        int navigation_size = games_lvl_navigation_sizes[gameId];

        try {
            if(fileName != null && !fileName.equals("")) {
                writer = new BufferedWriter(new FileWriter(new File(fileName), true));
                writer.write(gameId + " " + recordIds[1] + " " + randomSeed +
                        " " + (played.getWinner() == Types.WINNER.PLAYER_WINS ? 1 : 0) +
                        " " + played.getScore() + " " + played.getGameTick() +
                        " " + getMapSize() + " " + explored + " " + navigation_size + " " + explored/navigation_size + " " + last_discovered_tick + "\n");

                //printExplorationMatrix();

                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawInScreen(Graphics2D g) {
        Color rectColor = new Color(255, 255, 255, 127);

        for (int i = 0; i < exploration_matrix.length; i++) {
            for (int j = 0; j < exploration_matrix[i].length; j++) {
                if (exploration_matrix[i][j]) {
                    g.setColor(rectColor);
                    g.fillRect(i*block_size, j*block_size, block_size, block_size);
                    g.setColor(Types.WHITE);
                    g.drawRect(i*block_size, j*block_size, block_size, block_size);
                }
            }
        }
    }

    private int getMapSize(){
        return grid_width * grid_height;
    }

    /**
     * Checks if the provided position is out of bounds the map
     * @param position
     * @return
     */
    private boolean isOutOfBounds(Vector2d position){
        int x = (int)position.x / block_size;
        int y = (int)position.y / block_size;

        if ((x < 0) || (x >= grid_width) || (y < 0) || (y >= grid_height)){
            return true;
        }

        return false;
    }

    /**
     * Marks the position as visited in the exploration_matrix
     * The position is provided as a Vector2d object so it is needed to
     * calculate the valid coordinates to be considered for the matrix
     * It would be used the block_size int set when initialised
     * @param position The position as a Vector2d object
     */
    private void markNewPositionAsVisited(StateObservation stateObs){
        Vector2d position = stateObs.getAvatarPosition();
        if (isOutOfBounds(position)){
            return;
        }

        int x = (int)position.x / block_size;
        int y = (int)position.y / block_size;

        //System.out.println("Marking ("+x+" , "+y+") as VISITED");

        exploration_matrix[x][y] = true;
        last_discovered_tick = stateObs.getGameTick();
    }

    /**
     * Checks if the position has already been visited. As it is provided as Vector2d objects,
     * it is needed to convert it to valid coordinates to be considered for the matrix
     * @param position The position to be checked, as a Vector2d objects
     * @return true or false depending if the position has already been visited or not
     */
    private boolean hasBeenBefore(Vector2d position){
        int x = (int)position.x / block_size;
        int y = (int)position.y / block_size;

        //System.out.println("Been before to ("+x+" , "+y+")? "+exploration_matrix[x][y]);

        return exploration_matrix[x][y];
    }

    /**
     * Returns the percentage of the map explored in total
     * @return
     */
    private double getNSpotsExplored(){
        double explored = 0;

        for (int i = 0; i < exploration_matrix.length; i++) {
            for (int j = 0; j < exploration_matrix[i].length; j++) {
                if (exploration_matrix[i][j]) {
                    explored ++;
                }
            }
        }

        return explored;
    }

    /**
     * DEBUGGING method
     * @throws IOException
     */
    private void printExplorationMatrix() throws IOException {
        for (int i = 0; i < exploration_matrix.length; i++) {
            for (int j = 0; j < exploration_matrix[i].length; j++) {
                if (exploration_matrix[i][j]){
                    if (writer != null){
                        writer.write(" X ");
                    } else {
                        System.out.print(" X ");
                    }

                } else {
                    if (writer != null) {
                        writer.write(" - ");
                    } else {
                        System.out.print(" - ");
                    }
                }
            }
            if (writer != null) {
                writer.write("\n");
            } else {
                System.out.println();
            }
        }
    }
}