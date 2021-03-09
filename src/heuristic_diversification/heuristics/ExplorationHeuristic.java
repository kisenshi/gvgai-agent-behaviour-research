/**
 * Author: Cristina Guerrero
 * Date: 18th January 2021
 */

package heuristic_diversification.heuristics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import core.game.Game;
import core.game.StateObservation;
import core.heuristic.StateHeuristic;
import heuristic_diversification.framework.MapDimensionsManager;
import heuristic_diversification.model.GameStats;
import ontology.Types;
import tools.Vector2d;

/**
 * Maximize the physical exploration of the map, divided on tiles
 */
public class ExplorationHeuristic extends StateHeuristic {
    private static int COLOURS[][] = { 
            { 0, 0, 255 }, // blue
            { 0, 255, 0 }, // green
            { 255, 255, 0 }, // yellow
            { 255, 0, 0 }, // red
    };
    private MapDimensionsManager mapDimensions;
    private int[][] mExplorationMatrix;
    private HashMap<String, Integer> mFutureExploredPositions;
    private int mMaxExplorationMatrixValue;

    private int mLastDiscoveryTick = 0;

    public ExplorationHeuristic() {
        mFutureExploredPositions = new HashMap<String, Integer>();
    }

    @Override
    public void initHeuristicInternalInformation(StateObservation stateObs) {
        super.initHeuristicInternalInformation(stateObs);

        // Initialise the exploration matrix with the information given in the initial
        // state
        mapDimensions = new MapDimensionsManager(stateObs);

        mExplorationMatrix = new int[mapDimensions.gridHeight()][mapDimensions.gridWith()];
        mMaxExplorationMatrixValue = 0;

        visitCurrentPosition(stateObs);
    }

    @Override
    public void updateHeuristicInternalInformation(StateObservation stateObs) {
        super.updateHeuristicInternalInformation(stateObs);

        // Update the exploration matrix
        Vector2d avatarPosition = stateObs.getAvatarPosition();

        if (!mapDimensions.isOutOfBounds(avatarPosition)) {
            visitCurrentPosition(stateObs);
        }
        return;
    }

    @Override
    public void restartFutureStateData(StateObservation stateObs) {
        mFutureExploredPositions.clear();

        // Restart future data
        super.restartFutureStateData(stateObs);

        return;
    }

    @Override
    public void updateFutureStateData(StateObservation stateObs) {
        Vector2d avatarPosition = stateObs.getAvatarPosition();
        if (!stateObs.isAvatarAlive() || stateObs.isGameOver() || mapDimensions.isOutOfBounds(avatarPosition)) {
            return;
        }

        String avatarPositionKey = mapDimensions.getPositionKey(avatarPosition);

        if (!mFutureExploredPositions.containsKey(avatarPositionKey)) {
            mFutureExploredPositions.put(avatarPositionKey, 1);
        } else {
            // A position has been visited more than once
            int oldValue = mFutureExploredPositions.get(avatarPositionKey);
            mFutureExploredPositions.put(avatarPositionKey, oldValue + 1);
        }

        // Update future data
        super.updateFutureStateData(stateObs);

        return;
    }

    private double getHighHeuristicValue() {
        if (mMaxExplorationMatrixValue == 0 || mMaxFutureStates == 0) {
            return 100;
        }

        return mMaxFutureStates * mMaxExplorationMatrixValue;
    }

    @Override
    public double evaluateState(StateObservation stateObs) {
        double h = 0;

        // For this heuristic is penalised finishing the game, either when winning or
        // losing it
        boolean gameOver = stateObs.isGameOver();
        if (gameOver) {
            // Return a negative value that it is in range of the possible values in the
            // heuristic, to reduce the range between
            // the lowest and highest values of the heuristic
            h = (-1) * getHighHeuristicValue();

            if(DEBUG) {
                System.out.println("GAME OVER maxLength " + mMaxFutureStates + " maxValue " + mMaxExplorationMatrixValue + " h " + h);
            }
        }

        for (Map.Entry<String, Integer> visitedPosition : mFutureExploredPositions.entrySet()) {
            int x = mapDimensions.getXFromKey(visitedPosition.getKey());
            int y = mapDimensions.getYFromKey(visitedPosition.getKey());

            if(DEBUG) {
                System.out.println("visited " + visitedPosition.getKey() + " times: " + visitedPosition.getValue() + " + " + getNumberVisits(x, y));
            }

            if (!hasBeenVisited(x, y)) {
                // Reward highly each non-visited position encountered
                h += getHighHeuristicValue();
            } else {
                // If the agent has already visited a position, reward those they have been the
                // less number of times
                int nVisits = visitedPosition.getValue() * getNumberVisits(x, y);
                h += (-1 * nVisits);
            }
        }

        while (mNFutureStates < mMaxFutureStates) {
            h += (-1 * mMaxExplorationMatrixValue);
            mNFutureStates += 1;
            if(DEBUG) {
                System.out.println("Included " + mNFutureStates + " future state (max: "+ mMaxFutureStates + ")");
            }
        }

        return h;
    }

    @Override
    public String relevantInfoStr(StateObservation stateObs) {
        return mNFutureStates + " future states (max: " + mMaxFutureStates +"). Exploration: " + getNSpotsExplored();
    }


    @Override
    public void recordGameStats(Game game, GameStats gameStats) {
        gameStats.addExplorerFinalData(getNSpotsExplored(), mExplorationMatrix, mLastDiscoveryTick);
    }

    @Override
    public void recordDataOnFile(Game played, String fileName, int randomSeed, int recordIds[]) {
        int explored = getNSpotsExplored();

        // Data:
        // gameId controllerId randomSeed winnerId score gameTicks mapSize nExplored
        // navigationSize percentageExplored lastDiscoveredTick
        int gameId = recordIds[0];

        try {
            if (fileName != null && !fileName.equals("")) {
                writer = new BufferedWriter(new FileWriter(new File(fileName), true));
                writer.write(gameId + " " + recordIds[1] + " " + randomSeed + " "
                        + (played.getWinner() == Types.WINNER.PLAYER_WINS ? 1 : 0) + " " + played.getScore() + " "
                        + played.getGameTick() + " " + mapDimensions.getMapSize() + " " + explored + " " + mLastDiscoveryTick + "\n");

                // printExplorationMatrix();

                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawInScreen(Graphics2D g) {
        int blockSize = mapDimensions.blockSize();
        for (int y = 0; y < mExplorationMatrix.length; y++) {
            for (int x = 0; x < mExplorationMatrix[y].length; x++) {
                if (hasBeenVisited(x, y)) {
                    g.setColor(heatMapColour(getNumberVisits(x, y)));
                    g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
                    g.setColor(Types.WHITE);
                    g.drawRect(x * blockSize, y * blockSize, blockSize, blockSize);
                }
            }
        }
    }

    /**
     * Calculate the colour of the heatmap for the position, given the number of
     * times it has been visited The algorithm is based on:
     * http://www.andrewnoske.com/wiki/Code_-_heatmaps_and_color_gradients The range
     * of the minimum and maximum for the heatmap is dynamic, taking as reference
     * the maximum value encountered in the exploration matrix. Colours go blue
     * (less visited) --> green --> yellow --> red (most visited)
     */
    private Color heatMapColour(int positionValue) {
        // The range of possible colours goes between [1, mMaxExplorationMatrixValue];
        // normalise positionValue
        double mMinExplorationMatrixValue = 0.0;
        double positionValueNorm = (positionValue - mMinExplorationMatrixValue)
                / (mMaxExplorationMatrixValue - mMinExplorationMatrixValue);

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
            colourIndex1 = (int) Math.floor(positionValueNorm);
            colourIndex2 = colourIndex1 + 1;
            colourFraction = positionValueNorm - colourIndex1;
        }
        int r_rgb = (int) ((COLOURS[colourIndex2][0] - COLOURS[colourIndex1][0]) * colourFraction
                + COLOURS[colourIndex1][0]);
        int g_rgb = (int) ((COLOURS[colourIndex2][1] - COLOURS[colourIndex1][1]) * colourFraction
                + COLOURS[colourIndex1][1]);
        int b_rgb = (int) ((COLOURS[colourIndex2][2] - COLOURS[colourIndex1][2]) * colourFraction
                + COLOURS[colourIndex1][2]);

        return new Color(r_rgb, g_rgb, b_rgb, 127);
    }

    /**
     * Mark the current position as visited in the exploratory matrix
     * 
     * @param stateObs The current state to get the position and the game tick
     */
    private void visitCurrentPosition(StateObservation stateObs) {
        Vector2d position = stateObs.getAvatarPosition();
        if (mapDimensions.isOutOfBounds(position)) {
            return;
        }

        int x = mapDimensions.getXFromVector(position);
        int y = mapDimensions.getYFromVector(position);

        if (!hasBeenVisited(x, y)) {
            mLastDiscoveryTick = stateObs.getGameTick();
        }

        mExplorationMatrix[y][x] += 1;
        if (getNumberVisits(x, y) > mMaxExplorationMatrixValue) {
            mMaxExplorationMatrixValue = getNumberVisits(x, y);
        }
    }

    /**
     * Check if the position given by the (x,y) coordinates have been visited at
     * least once.
     * 
     * @param x coordinate x
     * @param y coordinate y
     * @return true or false depending if the position has already been visited or
     *         not
     */
    private boolean hasBeenVisited(int x, int y) {
        return getNumberVisits(x, y) > 0;
    }

    /**
     * @param x coordinate x
     * @param y coordinate y
     * @return Number of times a certain position has been visited by the player
     */
    private int getNumberVisits(int x, int y) {
        return mExplorationMatrix[y][x];
    }

    /**
     * @return Number of different unique positions visited by the player
     */
    private int getNSpotsExplored() {
        int explored = 0;

        for (int y = 0; y < mExplorationMatrix.length; y++) {
            for (int x = 0; x < mExplorationMatrix[y].length; x++) {
                if (hasBeenVisited(x, y)) {
                    explored++;
                }
            }
        }

        return explored;
    }

    /**
     * Debug method: Print the exploratory matrix marking the positions of the map
     * visited by the player
     * 
     * @throws IOException
     */
    private void printExplorationMatrix() throws IOException {
        for (int y = 0; y < mExplorationMatrix.length; y++) {
            for (int x = 0; x < mExplorationMatrix[y].length; x++) {
                int nVisits = getNumberVisits(x, y);
                if (nVisits < 10) {
                    if (writer != null) {
                        writer.write(getNumberVisits(x, y) + "   ");
                    } else {
                        System.out.print(getNumberVisits(x, y) + "   ");
                    }
                } else if (nVisits < 100) {
                    if (writer != null) {
                        writer.write(getNumberVisits(x, y) + "  ");
                    } else {
                        System.out.print(getNumberVisits(x, y) + "  ");
                    }
                } else {
                    if (writer != null) {
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