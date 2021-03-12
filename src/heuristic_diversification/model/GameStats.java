/**
 * Author: Cristina Guerrero
 * Date: 1st February 2021
 */

package heuristic_diversification.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class GameStats {
    transient private ArrayList<Integer> gameOverTick;
    public StatisticalSummaryValues gameOverTickStats;

    // winner
    transient private ArrayList<Integer> win; // 1 win, 0 lose
    public StatisticalSummaryValues winStats;

    // record breaker
    transient private ArrayList<Double> score;
    transient private ArrayList<Integer> lastScoreChangeTick;
    transient private ArrayList<Integer> lastPositiveScoreChangeTick;
    public StatisticalSummaryValues scoreStats;

    // explorer
    private int mapSize;
    transient private ArrayList<Integer> nExplored;
    transient private ArrayList<int[][]> heatMapExplorationMatrix;
    transient private ArrayList<Integer> lastNewExplorationTick;
    public StatisticalSummaryValues nExploredStats;
    public StatisticalSummaryValues percentageExploredStats;

    public GameStats(int mapSize) {
        this.mapSize = mapSize;

        gameOverTick = new ArrayList<Integer>();

        // winner
        win = new ArrayList<Integer>();

        // record breaker
        score = new ArrayList<Double>();
        lastScoreChangeTick = new ArrayList<Integer>();
        lastPositiveScoreChangeTick = new ArrayList<Integer>();

        // explorer
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

    public void addExplorerFinalData(int nExplored, int[][] explorationMatrix, int lastNewExploration) {
        this.nExplored.add(nExplored);
        this.lastNewExplorationTick.add(lastNewExploration);

        int[][] matrixCopy = Arrays.stream(explorationMatrix).map(int[]::clone).toArray(int[][]::new);
        this.heatMapExplorationMatrix.add(matrixCopy);
    }

    private void printGeneralStats(BufferedWriter writer) throws IOException {
        writer.write("== Game ==\n");
        for (int i = 0; i < gameOverTick.size(); i++) {
            writer.write(gameOverTick.get(i).toString() + " ");
        }
        writer.write("\n");
    }

    private void printWinnerStats(BufferedWriter writer) throws IOException {
        writer.write("== Winner ==\n");
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
        for (int i = 0; i < nExplored.size(); i++) {
            writer.write(nExplored.get(i).toString() + " ");
            writer.write(lastNewExplorationTick.get(i).toString() + " ");
            writer.write("\n");
        }
        writer.write("== Exploration Matrix ==\n");
        for (int i = 0; i < heatMapExplorationMatrix.size(); i++) {
            for (int y = 0; y < heatMapExplorationMatrix.get(i).length; y++) {
                for (int x = 0; x < heatMapExplorationMatrix.get(i)[y].length; x++) {
                    int nVisits = heatMapExplorationMatrix.get(i)[y][x];
                    if (nVisits < 10) {
                        writer.write(nVisits + "   ");
                    } else if (nVisits < 100) {
                        writer.write(nVisits + "  ");
                    } else {
                        writer.write(nVisits + " ");
                    }
                }
                writer.write("\n");
            }
            writer.write("\n\n");
        }
    }

    public void printStats(String fileName) {
        BufferedWriter writer;
        try {
            if (fileName != null && !fileName.equals("")) {
                writer = new BufferedWriter(new FileWriter(new File(fileName), true));
                printGeneralStats(writer);
                printWinnerStats(writer);
                printRecordBreakerStats(writer);
                printExplorerStats(writer);
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Allow calculating only the stats for the field provided. This method is helpful in cases where
     * only one stat value is necessary to avoid spending time calculating all of them.
     * @param statName name of the variable of the stat without the trailing "Stat" at the end.
     */
    public void calculateFieldStats(String statName) {
        try {
            StatisticalSummaryValues stats = null;
            Field dataList;

            // not all field stats are calculated the same way, we include the exceptions
            switch(statName) {
                case "percentageExplored":
                    // stats calculated with percentage from nExplored
                    stats = calculatePercentageStatsFromIntegerList(nExplored, mapSize);
                    break;
                case "score":
                    // type ArrayList<Double>
                    dataList = this.getClass().getDeclaredField(statName);
                    ArrayList<Double> doubleDataList = (ArrayList<Double>) dataList.get(this);
                    stats = calculateStatsFromDoubleList(doubleDataList);
                    break;
                default:
                    // most common stat type ArrayList<Integer> not percentage
                    dataList = this.getClass().getDeclaredField(statName);
                    ArrayList<Integer> integerDataList = (ArrayList<Integer>) dataList.get(this);
                    stats = calculateStatsFromIntegerList(integerDataList);
                    break;
            }

            Field statsField = this.getClass().getDeclaredField(statName + "Stats");
            statsField.set(this, stats); 
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        
    }

    public void calculateStats() {
        // game ticks
        System.out.println("Game ticks");
        gameOverTickStats = calculateStatsFromIntegerList(gameOverTick);

        // win
        System.out.println("Win");
        winStats = calculateStatsFromIntegerList(win);

        // score
        System.out.println("Score");
        scoreStats = calculateStatsFromDoubleList(score);

        // exploration
        System.out.println("Exploration");
        nExploredStats = calculateStatsFromIntegerList(nExplored);

        // exploration percentage
        if (mapSize > 0) {
            System.out.println("Exploration percentage");
            percentageExploredStats = calculatePercentageStatsFromIntegerList(nExplored, mapSize);
        }
    private StatisticalSummaryValues calculateStatsFromIntegerList(ArrayList<Integer> integerDataList) {
        SummaryStatistics stats = new SummaryStatistics();
        for (Integer data : integerDataList) {
            stats.addValue((double) data);
        }

        StatisticalSummaryValues statsVariable = (StatisticalSummaryValues) stats.getSummary();
        System.out.println(statsVariable.toString());

        return statsVariable;
    }

    private StatisticalSummaryValues calculatePercentageStatsFromIntegerList(ArrayList<Integer> integerDataList, int total) {
        SummaryStatistics stats = new SummaryStatistics();
        for (double data : integerDataList) {
            double percentageData = data / total;
            stats.addValue(percentageData);
        }

        StatisticalSummaryValues statsVariable = (StatisticalSummaryValues) stats.getSummary();
        System.out.println(statsVariable.toString());

        return statsVariable;
    }

    private StatisticalSummaryValues calculateStatsFromDoubleList(ArrayList<Double> doubleDataList) {
        SummaryStatistics stats = new SummaryStatistics();
        for (Double data : doubleDataList) {
            stats.addValue(data);
        }

        StatisticalSummaryValues statsVariable = (StatisticalSummaryValues) stats.getSummary();
        System.out.println(statsVariable.toString());

        return statsVariable;
    }

    // killer
    // collector
    // scholar
    // risk analyst
    // novelty explorer
}
