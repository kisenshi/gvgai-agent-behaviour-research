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
import java.util.HashMap;
import java.util.Map;

/**
 * Maximize the exploration of the map
 */

public class ExplorationHeuristic extends StateHeuristic {
    private static int sGamesLvlNavigationSizes[] = new int[]{
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
    private static int COLOURS[][] = {
        {0,0,255},      // blue
        {0,255,0},      // green
        {255,255,0},    // yellow
        {255,0,0},      // red
    };
    private int mBlockSize;
    private int mGridWidth;
    private int mGridHeight;
    private int[][] mExplorationMatrix;
    private HashMap<String, Integer> mFutureExploredPositions;
    private int mMaxExplorationMatrixValue;

    private int mLastDiscoveryTick = 0;

    public ExplorationHeuristic() {
        mFutureExploredPositions = new HashMap<String, Integer>();
    }
    
    @Override
    public void initHeuristicInternalInformation(StateObservation stateObs){
        // Initialise the exploration matrix with the information given in the initial state
        mBlockSize = stateObs.getBlockSize();
        Dimension gridDimension = stateObs.getWorldDimension();

        mGridWidth = gridDimension.width / mBlockSize;
        mGridHeight = gridDimension.height / mBlockSize;

        mExplorationMatrix = new int[mGridHeight][mGridWidth];
        mMaxExplorationMatrixValue = 0;

        visitCurrentPosition(stateObs);
    }

    @Override
    public void updateHeuristicInternalInformation(StateObservation stateObs) {
        // Update the exploration matrix
        Vector2d avatarPosition = stateObs.getAvatarPosition();

        if (!isOutOfBounds(avatarPosition)){
            visitCurrentPosition(stateObs);
        }
        return;
    }

    @Override
    public void restartFutureStateData() {
        mFutureExploredPositions.clear();
        return;
    }

    @Override
    public void updateFutureStateData(StateObservation stateObs) {
        if (!stateObs.isAvatarAlive() || stateObs.isGameOver()){
            return;
        }

        String avatarPositionKey = getPositionKey(stateObs.getAvatarPosition());

        if (!mFutureExploredPositions.containsKey(avatarPositionKey)){
            mFutureExploredPositions.put(avatarPositionKey, 1);
        } else {
            // A position has been visited more than once
            int oldValue = mFutureExploredPositions.get(avatarPositionKey);
            mFutureExploredPositions.put(avatarPositionKey, oldValue+1);
        }
        return;
    }

    @Override
    public double evaluateState(StateObservation stateObs) {
        // For this heuristic is penalised finishing the game, either when winning or losing it
        boolean gameOver = stateObs.isGameOver();
        if (gameOver){
            return HUGE_NEGATIVE;
        }

        double h = 0;
        for (Map.Entry<String,Integer> visitedPosition : mFutureExploredPositions.entrySet()) {
            int x = getX(visitedPosition.getKey());
            int y = getY(visitedPosition.getKey());


            if(!hasBeenVisited(x, y)){
                // Reward highly each non-visited position encountered
                h += 100;
            } else {
                // If the agent has already visited a position, reward those they have been the less number of times
                int nVisits = visitedPosition.getValue() + getNumberVisits(x, y);
                h += (-1 * nVisits);
            }
        }

        return h;
    }

    @Override
    public String relevantInfoStr(StateObservation stateObs) {
        return "exploration: " + getNSpotsExplored();
    }

    @Override
    public void recordDataOnFile(Game played, String fileName, int randomSeed, int recordIds[]) {
        double explored = getNSpotsExplored();

        // Data:
        // gameId controllerId randomSeed winnerId score gameTicks mapSize nExplored navigationSize percentageExplored lastDiscoveredTick
        int gameId = recordIds[0];
        int navigationSize = sGamesLvlNavigationSizes[gameId];

        try {
            if(fileName != null && !fileName.equals("")) {
                writer = new BufferedWriter(new FileWriter(new File(fileName), true));
                writer.write(gameId + " " + recordIds[1] + " " + randomSeed +
                        " " + (played.getWinner() == Types.WINNER.PLAYER_WINS ? 1 : 0) +
                        " " + played.getScore() + " " + played.getGameTick() +
                        " " + getMapSize() + " " + explored + " " + navigationSize + " " + explored/navigationSize + " " + mLastDiscoveryTick + "\n");

                //printExplorationMatrix();

                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawInScreen(Graphics2D g) {
        for (int y = 0; y < mExplorationMatrix.length; y++) {
            for (int x = 0; x < mExplorationMatrix[y].length; x++) {
                if (hasBeenVisited(x, y)) {
                    g.setColor(heatMapColour(getNumberVisits(x, y)));
                    g.fillRect(x*mBlockSize, y*mBlockSize, mBlockSize, mBlockSize);
                    g.setColor(Types.WHITE);
                    g.drawRect(x*mBlockSize, y*mBlockSize, mBlockSize, mBlockSize);
                }
            }
        }
    }

    /**
     * Calculate the colour of the heatmap for the position, given the number of times it has been visited
     * The algorithm is based on: http://www.andrewnoske.com/wiki/Code_-_heatmaps_and_color_gradients
     * The range of the minimum and maximum for the heatmap is dynamic, taking as reference the maximum value encountered in the
     * exploration matrix. 
     * Colours go blue (less visited) --> green --> yellow --> red (most visited)
     */
    private Color heatMapColour(int positionValue){
        // The range of possible colours goes between [1, mMaxExplorationMatrixValue]; normalise positionValue
        double mMinExplorationMatrixValue = 0.0;
        double positionValueNorm = (positionValue - mMinExplorationMatrixValue) / (mMaxExplorationMatrixValue - mMinExplorationMatrixValue); 

        // 4 colour used for the heatmap: blue, green, yellow, red   
        int colourIndex1; 
        int colourIndex2;
        double colourFraction = 0;

        if (positionValueNorm <= 0) {
            colourIndex1 = colourIndex2 = 0;
        } else if (positionValueNorm >= 1) {
            colourIndex1 = colourIndex2 = COLOURS.length - 1;
        } else {
            positionValueNorm *= (COLOURS.length - 1);
            colourIndex1 = (int)Math.floor(positionValueNorm);
            colourIndex2 = colourIndex1 + 1;
            colourFraction = positionValueNorm - colourIndex1;
        }
        int r_rgb = (int)((COLOURS[colourIndex2][0] - COLOURS[colourIndex1][0])*colourFraction + COLOURS[colourIndex1][0]);
        int g_rgb = (int)((COLOURS[colourIndex2][1] - COLOURS[colourIndex1][1])*colourFraction + COLOURS[colourIndex1][1]);
        int b_rgb = (int)((COLOURS[colourIndex2][2] - COLOURS[colourIndex1][2])*colourFraction + COLOURS[colourIndex1][2]);

        return new Color(r_rgb, g_rgb, b_rgb, 127);
    }

    /**
     * @return Size of the map 
     */
    private int getMapSize(){
        return mGridWidth * mGridHeight;
    }

    /**
     * Generate the key formed by the coordinates for the mFutureExploredPositions hashmap
     * @return String in the form "x y"
     */
    private String getPositionKey(Vector2d position){
        int x = (int)position.x / mBlockSize;
        int y = (int)position.y / mBlockSize;

        return x + " " + y;
    }

    /**
     * Get the x coordinate from the key used for the mFutureExploredPositions hashmap
     * @return the x coordinate
     */
    private int getX(String key){
        String[] coordinates = key.split(" +");
        return Integer.parseInt(coordinates[0]);
    }

    /**
     * Get the y coordinate from the key used for the mFutureExploredPositions hashmap
     * @return the y coordinate
     */
    private int getY(String key){
        String[] coordinates = key.split(" +");
        return Integer.parseInt(coordinates[1]);
    }

    /**
     * Checks if the provided position is out of bounds the map
     * @param position
     * @return
     */
    private boolean isOutOfBounds(Vector2d position){
        int x = (int)position.x / mBlockSize;
        int y = (int)position.y / mBlockSize;

        if ((x < 0) || (x >= mGridWidth) || (y < 0) || (y >= mGridHeight)){
            return true;
        }

        return false;
    }

    /**
     * Mark the current position as visited in the exploratory matrix
     * @param stateObs The current state to get the position and the game tick
     */
    private void visitCurrentPosition(StateObservation stateObs){
        Vector2d position = stateObs.getAvatarPosition();
        if (isOutOfBounds(position)){
            return;
        }

        int x = (int)position.x / mBlockSize;
        int y = (int)position.y / mBlockSize;

        if(!hasBeenVisited(x, y)){
            mLastDiscoveryTick = stateObs.getGameTick();
        }
        
        mExplorationMatrix[y][x] += 1;
        if (getNumberVisits(x, y) > mMaxExplorationMatrixValue) {
            mMaxExplorationMatrixValue = getNumberVisits(x, y);
        }
    }

    /**
     * Check if the position given by the (x,y) coordinates have been visited at least once.
     * @param x coordinate x
     * @param y coordinate y
     * @return true or false depending if the position has already been visited or not
     */
    private boolean hasBeenVisited(int x, int y){
        return getNumberVisits(x, y) > 0;
    }

    /**
     * @param x coordinate x
     * @param y coordinate y
     * @return Number of times a certain position has been visited by the player
     */
    private int getNumberVisits(int x, int y){
        return mExplorationMatrix[y][x];
    }

    /**
     * @return Number of different unique positions visited by the player
     */
    private double getNSpotsExplored(){
        double explored = 0;

        for (int y = 0; y < mExplorationMatrix.length; y++) {
            for (int x = 0; x < mExplorationMatrix[y].length; x++) {
                if (hasBeenVisited(x, y)) {
                    explored ++;
                }
            }
        }

        return explored;
    }

    /**
     * Debug method: Print the exploratory matrix marking the positions of the map visited by the player
     * @throws IOException
     */
    private void printExplorationMatrix() throws IOException {
        for (int y = 0; y < mExplorationMatrix.length; y++) {
            for (int x = 0; x < mExplorationMatrix[y].length; x++) {
                int nVisits = getNumberVisits(x, y);
                if (nVisits < 10) {
                    if (writer != null){
                        writer.write(getNumberVisits(x, y) + "   ");
                    } else {
                        System.out.print(getNumberVisits(x, y) + "   ");
                    }
                } else if(nVisits < 100) {
                    if (writer != null){
                        writer.write(getNumberVisits(x, y) + "  ");
                    } else {
                        System.out.print(getNumberVisits(x, y) + "  ");
                    }
                } else {
                    if (writer != null){
                        writer.write(getNumberVisits(x, y) + " ");
                    } else {
                        System.out.print(getNumberVisits(x, y) + " ");
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