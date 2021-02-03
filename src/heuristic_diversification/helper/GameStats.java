/**
 * Author: Cristina Guerrero
 * Date: 1st February 2021
 */

package heuristic_diversification.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class GameStats {
    private ArrayList<Integer> gameOverTick;

    // winner
    private ArrayList<Integer> win; // 1 win, 0 lose
    private double winAvg;

    // record breaker
    private ArrayList<Double> score;
    private ArrayList<Integer> lastScoreChangeTick;
    private ArrayList<Integer> lastPositiveScoreChangeTick;
    private double scoreAvg;

    // explorer
    private int mapSize;
    private ArrayList<Integer> nExplored;
    private ArrayList<int[][]> heatMapExplorationMatrix;
    private ArrayList<Integer> lastNewExplorationTick;
    private double nExploredAvg;

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

    public void addGeneralData(int gameOverTick) {
        this.gameOverTick.add(gameOverTick);
    }

    public void addWinnerData(int win) {
        this.win.add(win);
    }

    public void addRecordBreakerData(double score, int lastScoreChange, int lastPositiveScoreChange) {
        this.score.add(score);
        this.lastScoreChangeTick.add(lastScoreChange);
        this.lastPositiveScoreChangeTick.add(lastPositiveScoreChange);
    }

    public void setMapSize(int mapSize) {
        this.mapSize = mapSize;
    }

    public void addExplorerFinalData(int nExplored, int[][] explorationMatrix, int lastNewExploration) {
        this.nExplored.add(nExplored);
        this.lastNewExplorationTick.add(lastNewExploration);

        int[][] matrixCopy = Arrays.stream(explorationMatrix).map(int[]::clone).toArray(int[][]::new);
        this.heatMapExplorationMatrix.add(matrixCopy);
    }

    private void printWinnerStats(BufferedWriter writer) throws IOException {
        writer.write("== Winner ==\nwins: ");
        for (int i = 0; i < win.size(); i++) {
            writer.write(win.get(i).toString() + " ");
        }
        writer.write("\n");
    }

    private void printRecordBreakerStats(BufferedWriter writer) throws IOException {
        writer.write("== Record breaker==\n");
        for (int i = 0; i < score.size(); i++) {
            writer.write(score.get(i).toString() + " ");
            writer.write(lastScoreChangeTick.get(i).toString() + " ");
            writer.write(lastPositiveScoreChangeTick.get(i).toString() + " ");
            writer.write("\n");
        }
    }

    private void printExplorerStats(BufferedWriter writer) throws IOException {
        writer.write("== Explorer ==\n");
        for (int i = 0; i < score.size(); i++) {
            writer.write(nExplored.get(i).toString() + " ");
            writer.write(lastNewExplorationTick.get(i).toString() + " ");
            writer.write("\n");
        }
    }

    public void printStats(String fileName) {
        BufferedWriter writer;
        try {
            if (fileName != null && !fileName.equals("")) {
                writer = new BufferedWriter(new FileWriter(new File(fileName), true));
                printWinnerStats(writer);
                printRecordBreakerStats(writer);
                printExplorerStats(writer);
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void calculateStats() {
        SummaryStatistics stats = new SummaryStatistics();

        // win
        System.out.println("Win");
        for (int winValue : win) {
            stats.addValue((double) winValue);
        }
        winAvg = stats.getMean();
        System.out.println(stats.toString());
        stats.clear();

        // score
        System.out.println("Score");
        for (double scoreValue : score) {
            stats.addValue(scoreValue);
        }

        scoreAvg = stats.getMean();
        System.out.println(stats.toString());
        stats.clear();

        // exploration
        System.out.println("Exploration");
        for (double nExploredValue : nExplored) {
            stats.addValue(nExploredValue);
        }

        nExploredAvg = stats.getMean();
        System.out.println(stats.toString());
        stats.clear();
    }

    // killer
    // collector
    // scholar
    // risk analyst
    // novelty explorer
}
