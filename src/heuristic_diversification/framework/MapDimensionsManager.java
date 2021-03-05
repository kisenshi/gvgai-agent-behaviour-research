package heuristic_diversification.framework;

import java.awt.Dimension;

/**
 * Author: Cristina Guerrero
 * Date: 4th March 2021
 */

import core.game.StateObservation;
import tools.Vector2d;

/**
 * Class that contains the methods to handle positions of game converted to grid dimensions
 * and that can be used in a matrix, representing the map as a grid.
 */
public class MapDimensionsManager {
    private int mBlockSize;
    private int mGridWidth;
    private int mGridHeight;

    public MapDimensionsManager(StateObservation stateObs) {
        mBlockSize = stateObs.getBlockSize();
        
        Dimension gridDimension = stateObs.getWorldDimension();
        mGridWidth = gridDimension.width / mBlockSize;
        mGridHeight = gridDimension.height / mBlockSize;
    }

    public int gridWith() {
        return mGridWidth;
    }

    public int gridHeight() {
        return mGridHeight;
    }

    public int blockSize() {
        return mBlockSize;
    }

    /**
     * @return Size of the map
     */
    public int getMapSize() {
        return mGridWidth * mGridHeight;
    }

    /**
     * Get the y correspondent to the matrix that represents the grid.
     * @param positionVector
     * @return
     */
    public int getXFromVector(Vector2d positionVector) {
        return (int) positionVector.x / mBlockSize;
    }

    /**
     * Get the y correspondent to the matrix that represents the grid.
     * @param positionVector
     * @return
     */
    public int getYFromVector(Vector2d positionVector) {
        return (int) positionVector.y / mBlockSize;
    }

    /**
     * Generate the key formed by the coordinates for the mFutureExploredPositions
     * hashmap
     * 
     * @return String in the form "x y"
     */
    public String getPositionKey(Vector2d position) {
        int x = getXFromVector(position);
        int y = getYFromVector(position);

        return x + " " + y;
    }

    /**
     * Get the x coordinate from the key used for the mFutureExploredPositions
     * hashmap
     * 
     * @return the x coordinate
     */
    public int getXFromKey(String positionKey) {
        String[] coordinates = positionKey.split(" +");
        return Integer.parseInt(coordinates[0]);
    }
    
    /**
     * Get the y coordinate from the key used for the mFutureExploredPositions
     * hashmap
     * 
     * @return the y coordinate
     */
    public int getYFromKey(String positionKey) {
        String[] coordinates = positionKey.split(" +");
        return Integer.parseInt(coordinates[1]);
    }

    /**
     * Checks if the provided position is out of bounds the map
     * 
     * @param position
     * @return
     */
    public boolean isOutOfBounds(Vector2d position) {
        int x = getXFromVector(position);
        int y = getYFromVector(position);

        if ((x < 0) || (x >= mGridWidth) || (y < 0) || (y >= mGridHeight)) {
            return true;
        }

        return false;
    }
}
