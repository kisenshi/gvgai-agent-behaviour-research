package heuristic_diversification.helper;

import java.util.ArrayList;
import java.util.Arrays;

public class GameStats {
    private ArrayList<Integer> gameOverTick;

    // winner
    private ArrayList<Integer> win; // 1 win, 0 lose

    // record breaker
    private ArrayList<Double> score;
    private ArrayList<Integer> lastScoreChangeTick;
    private ArrayList<Integer> lastPositiveScoreChangeTick;

    // explorer
    private int mapSize;
    private ArrayList<Integer> nExplored;
    private ArrayList<int[][]> heatMapExplorationMatrix;
    private ArrayList<Integer> lastNewExplorationTick;

    public GameStats() {
        gameOverTick = new ArrayList<Integer>();   

        // winner
        win = new ArrayList<Integer>();

        // record breaker
        score = new ArrayList<Double>();
        lastScoreChangeTick = new ArrayList<Integer>();
        lastPositiveScoreChangeTick = new ArrayList<Integer>();

        // explorer
        mapSize = 0;
        nExplored = new ArrayList<Integer>();
        heatMapExplorationMatrix = new ArrayList<int[][]>();
        lastNewExplorationTick = new ArrayList<Integer>();
    }

    public void addGeneralData(int gameOverTick){
        this.gameOverTick.add(gameOverTick);
    }

    public void addWinnerData(int win){
        this.win.add(win);
    }

    public void addRecordBreakerData(double score, int lastScoreChange, int lastPositiveScoreChange){
        this.score.add(score);
        this.lastScoreChangeTick.add(lastScoreChange);
        this.lastPositiveScoreChangeTick.add(lastPositiveScoreChange);
    }

    public void setMapSize(int mapSize){
        this.mapSize = mapSize;
    }

    public void addExplorerFinalData(int nExplored, int[][] explorationMatrix, int lastNewExploration){
        this.nExplored.add(nExplored);
        this.lastNewExplorationTick.add(lastNewExploration);

        int[][] matrixCopy = Arrays.stream(explorationMatrix).map(int[]::clone).toArray(int[][]::new);
        this.heatMapExplorationMatrix.add(matrixCopy);
    }

    // killer
    // collector
    // scholar
    // risk analyst
    // novelty explorer
}
