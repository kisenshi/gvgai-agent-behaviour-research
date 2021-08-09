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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public StatisticalSummaryValues lastScoreChangeTickStats;
    public StatisticalSummaryValues lastPositiveScoreChangeTickStats;

    // explorer
    private int mapSize;
    transient private ArrayList<Integer> nExplored;
    transient private ArrayList<int[][]> heatMapExplorationMatrix;
    transient private ArrayList<Integer> lastNewExplorationTick;
    private double[][] heatMapExplorationMatrixAvg;
    public StatisticalSummaryValues nExploredStats;
    public StatisticalSummaryValues percentageExploredStats;
    public StatisticalSummaryValues lastNewExplorationTickStats;

    // discovery
    private ArrayList<Integer> finalStypesDiscovered; // this contains the final list of sprites discovered in any of the runs
    transient private ArrayList<Integer> nSpritesDiscovered;
    transient private ArrayList<Integer> lastDiscoveryTick;
    public StatisticalSummaryValues nSpritesDiscoveredStats;
    public StatisticalSummaryValues lastDiscoveryTickStats;

    // curious
    transient private ArrayList<Integer> nUniqueSpriteInteractions;
    transient private ArrayList<Integer> nCuriosityInteractions;
    transient private ArrayList<Integer> nTotalCollisions;
    transient private ArrayList<Integer> nTotalHits;
    transient private ArrayList<Integer> lastNewCollisionTick;
    transient private ArrayList<Integer> lastNewHitTick;
    transient private ArrayList<Integer> lastCuriosityTick;
    public StatisticalSummaryValues nUniqueSpriteInteractionsStats;
    public StatisticalSummaryValues nCuriosityInteractionsStats;
    public StatisticalSummaryValues nTotalInteractionsStats;
    public StatisticalSummaryValues nTotalCollisionsStats;
    public StatisticalSummaryValues nTotalHitsStats;
    public StatisticalSummaryValues lastNewCollisionTickStats;
    public StatisticalSummaryValues lastNewHitTickStats;
    public StatisticalSummaryValues lastCuriosityTickStats;
    
    // killer
    private ArrayList<Integer> finalStypesKilled;
    transient private ArrayList<Integer> nTotalKills;
    transient private ArrayList<Integer> lastKillTick;
    public StatisticalSummaryValues nTotalKillsStats;
    public StatisticalSummaryValues lastKillTickStats;

    // collector
    private ArrayList<Integer> finalStypesCollected;
    transient private ArrayList<Integer> nTotalItemsCollected;
    transient private ArrayList<Integer> lastCollectionTick;
    public StatisticalSummaryValues nTotalItemsCollectedStats;
    public StatisticalSummaryValues lastCollectionTickStats;

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

        // discovery
        finalStypesDiscovered = new ArrayList<Integer>();
        nSpritesDiscovered = new ArrayList<Integer>();
        lastDiscoveryTick = new ArrayList<Integer>();

        // curious
        nUniqueSpriteInteractions = new ArrayList<Integer>();
        nCuriosityInteractions = new ArrayList<Integer>();
        nTotalCollisions = new ArrayList<Integer>();
        nTotalHits = new ArrayList<Integer>();
        lastNewCollisionTick = new ArrayList<Integer>();
        lastNewHitTick = new ArrayList<Integer>();
        lastCuriosityTick = new ArrayList<Integer>();

        // killer
        finalStypesKilled = new ArrayList<Integer>();
        nTotalKills = new ArrayList<Integer>();
        lastKillTick = new ArrayList<Integer>();

        // collector
        finalStypesCollected = new ArrayList<Integer>();
        nTotalItemsCollected = new ArrayList<Integer>();
        lastCollectionTick = new ArrayList<Integer>();
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

    public void addDiscoveryFinalData(ArrayList<Integer> stypesDiscovered, int nSprites, int lastSpriteDiscovery) {
        addStypesToFinalStypes(stypesDiscovered, this.finalStypesDiscovered);
        
        this.nSpritesDiscovered.add(nSprites);
        this.lastDiscoveryTick.add(lastSpriteDiscovery);
    }

    public void addCuriousFinalData(int nDistinctStypes, int nCuriosity, int nCollisions, int nHits, int lastNewCollision, int lastNewHit, int lastCuriosity) {
        this.nUniqueSpriteInteractions.add(nDistinctStypes);
        this.nCuriosityInteractions.add(nCuriosity); 
        this.nTotalCollisions.add(nCollisions);
        this.nTotalHits.add(nHits);
        this.lastNewCollisionTick.add(lastNewCollision); 
        this.lastNewHitTick.add(lastNewHit);
        this.lastCuriosityTick.add(lastCuriosity);
    }

    public void addKillerFinalData(ArrayList<Integer> stypesKilled, int nKills, int lastKill) {
        addStypesToFinalStypes(stypesKilled, finalStypesKilled);

        this.nTotalKills.add(nKills);
        this.lastKillTick.add(lastKill);
    }

    public void addCollectorFinalData(ArrayList<Integer> stypesCollected, int nCollectedItems, int lastCollection) {
        addStypesToFinalStypes(stypesCollected, finalStypesCollected);

        this.nTotalItemsCollected.add(nCollectedItems);
        this.lastCollectionTick.add(lastCollection);
    }

    private void addStypesToFinalStypes(ArrayList<Integer> stypes, ArrayList<Integer> finalStypes) {
        if (!finalStypes.equals(stypes)) {
            for (Integer stype : stypes) {
                if (!finalStypes.contains(stype)) {
                    finalStypes.add(stype);
                }
            }
        }
    }

    private void printGeneralStats(BufferedWriter writer) throws IOException {
        writer.write("== Game ==\n");
        for (int i = 0; i < gameOverTick.size(); i++) {
            writer.write(gameOverTick.get(i).toString() + " ");
        }
        writer.write("\n");
        writer.write("-- gameOverTickStats:\n");
        writer.write(gameOverTickStats.toString());
    }

    private void printWinnerStats(BufferedWriter writer) throws IOException {
        writer.write("== Winner ==\n");
        for (int i = 0; i < win.size(); i++) {
            writer.write(win.get(i).toString() + " ");
        }
        writer.write("\n");
        writer.write("-- winStats:\n");
        writer.write(winStats.toString());
    }

    private void printRecordBreakerStats(BufferedWriter writer) throws IOException {
        writer.write("== Record breaker==\n");
        for (int i = 0; i < score.size(); i++) {
            writer.write(score.get(i).toString() + " ");
            writer.write(lastScoreChangeTick.get(i).toString() + " ");
            writer.write(lastPositiveScoreChangeTick.get(i).toString() + " ");
            writer.write("\n");
        }
        writer.write("-- scoreStats:\n");
        writer.write(scoreStats.toString());
        writer.write("-- lastScoreChangeTickStats:\n");
        writer.write(lastScoreChangeTickStats.toString());
        writer.write("-- lastPositiveScoreChangeTickStats:\n");
        writer.write(lastPositiveScoreChangeTickStats.toString());
    }

    private void printExplorerStats(BufferedWriter writer) throws IOException {
        writer.write("== Explorer ==\n");
        for (int i = 0; i < nExplored.size(); i++) {
            writer.write(nExplored.get(i).toString() + " ");
            if (mapSize > 0) {
                Double percentageExplored = nExplored.get(i) * 1.0 / mapSize;
                writer.write(percentageExplored.toString() + " ");
            }
            writer.write(lastNewExplorationTick.get(i).toString() + " ");
            writer.write("\n");
        }
        writer.write("-- nExploredStats:\n");
        writer.write(nExploredStats.toString());
        writer.write("-- percentageExploredStats:\n");
        writer.write(percentageExploredStats.toString());
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
        writer.write("== Average exploration Matrix ==\n");
        if (heatMapExplorationMatrixAvg != null) {
            for (int y = 0; y < heatMapExplorationMatrixAvg.length; y++) {
                for (int x = 0; x < heatMapExplorationMatrixAvg[y].length; x++) {
                    double nVisits = heatMapExplorationMatrixAvg[y][x];
                    if (nVisits < 10) {
                        writer.write(String.format("%.2f", nVisits) + "   ");
                    } else if (nVisits < 100) {
                        writer.write(String.format("%.2f", nVisits) + "  ");
                    } else {
                        writer.write(String.format("%.2f", nVisits) + " ");
                    }
                }
                writer.write("\n");
            }
        }
    }

    private void printDiscoveryStats(BufferedWriter writer) throws IOException {
        writer.write("== Discovery ==\n");
        writer.write("stype discovered: " + finalStypesDiscovered.toString() + "\n");
        for (int i = 0; i < nSpritesDiscovered.size(); i++) {
            writer.write(nSpritesDiscovered.get(i).toString() + " ");
            writer.write(lastDiscoveryTick.get(i).toString() + " ");
            writer.write("\n");
        }
        writer.write("-- nSpritesDiscoveredStats:\n");
        writer.write(nSpritesDiscoveredStats.toString());
        writer.write("-- lastDiscoveryTickStats:\n");
        writer.write(lastDiscoveryTickStats.toString());
    }

    private void printCuriosityStats(BufferedWriter writer) throws IOException {
        writer.write("== Curious ==\n");
        for (int i = 0; i < nUniqueSpriteInteractions.size(); i++) {
            writer.write(nUniqueSpriteInteractions.get(i).toString() + " ");
            writer.write(nCuriosityInteractions.get(i).toString() + " ");
            writer.write(nTotalCollisions.get(i).toString() + " ");
            writer.write(nTotalHits.get(i).toString() + " ");
            writer.write(lastNewCollisionTick.get(i).toString() + " ");
            writer.write(lastNewHitTick.get(i).toString() + " ");
            writer.write(lastCuriosityTick.get(i).toString() + " ");
            writer.write("\n");
        }
        writer.write("-- nUniqueSpriteInteractionsStats:\n");
        writer.write(nUniqueSpriteInteractionsStats.toString());
        writer.write("-- nCuriosityInteractionsStats:\n");
        writer.write(nCuriosityInteractionsStats.toString());
        writer.write("-- nTotalCollisionsStats:\n");
        writer.write(nTotalCollisionsStats.toString());
        writer.write("-- nTotalHitsStats:\n");
        writer.write(nTotalHitsStats.toString());
        writer.write("-- lastNewCollisionTickStats:\n");
        writer.write(lastNewCollisionTickStats.toString());
        writer.write("-- lastNewHitTickStats:\n");
        writer.write(lastNewHitTickStats.toString());
        writer.write("-- lastCuriosityTickStats:\n");
        writer.write(lastCuriosityTickStats.toString());
    }

    private void printKillingStats(BufferedWriter writer) throws IOException {
        writer.write("== Killer ==\n");
        writer.write("stype killed: " + finalStypesKilled.toString() + "\n");
        for (int i = 0; i < nTotalKills.size(); i++) {
            writer.write(nTotalKills.get(i).toString() + " ");
            writer.write(lastKillTick.get(i).toString() + " ");
            writer.write("\n");
        }
        writer.write("-- nTotalKillsStats:\n");
        writer.write(nTotalKillsStats.toString());
        writer.write("-- lastKillTickStats:\n");
        writer.write(lastKillTickStats.toString());
    }

    private void printCollectionStats(BufferedWriter writer) throws IOException {
        writer.write("== Collector ==\n");
        writer.write("stype collected: " + finalStypesCollected.toString() + "\n");
        for (int i = 0; i < nTotalItemsCollected.size(); i++) {
            writer.write(nTotalItemsCollected.get(i).toString() + " ");
            writer.write(lastCollectionTick.get(i).toString() + " ");
            writer.write("\n");
        }
        writer.write("-- nTotalItemsCollectedStats:\n");
        writer.write(nTotalItemsCollectedStats.toString());
        writer.write("-- lastCollectionTickStats:\n");
        writer.write(lastCollectionTickStats.toString());
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
                printDiscoveryStats(writer);
                printCuriosityStats(writer);
                printKillingStats(writer);
                printCollectionStats(writer);
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
                case "nTotalInteractions":
                    // stats calculated as the sum of two lists: collisions + hits
                    ArrayList<Integer> bothInteractions = (ArrayList<Integer>) IntStream.range(0, nTotalCollisions.size())
                        .mapToObj(i -> nTotalCollisions.get(i) + nTotalHits.get(i))
                        .collect(Collectors.toList());
                    stats = calculateStatsFromIntegerList(bothInteractions);
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

        // last score change
        System.out.println("Tick last score change");
        lastScoreChangeTickStats = calculateStatsFromIntegerList(lastScoreChangeTick);

        // last positive score change
        System.out.println("Tick last positive score change");
        lastPositiveScoreChangeTickStats = calculateStatsFromIntegerList(lastPositiveScoreChangeTick);

        // exploration
        System.out.println("Exploration");
        nExploredStats = calculateStatsFromIntegerList(nExplored);

        // exploration percentage
        if (mapSize > 0) {
            System.out.println("Exploration percentage");
            percentageExploredStats = calculatePercentageStatsFromIntegerList(nExplored, mapSize);
        }

        // exploration heatmap average
        System.out.println("Exploration heatmap average");
        heatMapExplorationMatrixAvg = calculateAverageExplorationHeatMap();

        // last new exploration
        System.out.println("Tick last new exploration");
        lastNewExplorationTickStats = calculateStatsFromIntegerList(lastNewExplorationTick);

        // discovery
        System.out.println("Sprites discovered");
        nSpritesDiscoveredStats = calculateStatsFromIntegerList(nSpritesDiscovered);
        
        // last discovery
        System.out.println("Tick last discovery");
        lastDiscoveryTickStats = calculateStatsFromIntegerList(lastDiscoveryTick);

        // unique interactions
        System.out.println("Unique interactions");
        nUniqueSpriteInteractionsStats = calculateStatsFromIntegerList(nUniqueSpriteInteractions);

        // curiosity
        System.out.println("Curiosity interactions");
        nCuriosityInteractionsStats = calculateStatsFromIntegerList(nCuriosityInteractions);

        System.out.println("Tick last curiosity");
        lastCuriosityTickStats = calculateStatsFromIntegerList(lastCuriosityTick);
        
        // total interactions
        System.out.println("Total interactions");
        ArrayList<Integer> bothInteractions = (ArrayList<Integer>) IntStream.range(0, nTotalCollisions.size())
                                                .mapToObj(i -> nTotalCollisions.get(i) + nTotalHits.get(i))
                                                .collect(Collectors.toList());
        nTotalInteractionsStats = calculateStatsFromIntegerList(bothInteractions);

        // collisions
        System.out.println("Total collisions");
        nTotalCollisionsStats = calculateStatsFromIntegerList(nTotalCollisions);

        System.out.println("Tick last new collision");
        lastNewCollisionTickStats = calculateStatsFromIntegerList(lastNewCollisionTick);
        
        // hits
        System.out.println("Total hits");
        nTotalHitsStats = calculateStatsFromIntegerList(nTotalHits);

        System.out.println("Tick last new hit");
        lastNewHitTickStats = calculateStatsFromIntegerList(lastNewHitTick);

        // kills
        System.out.println("Total kills");
        nTotalKillsStats = calculateStatsFromIntegerList(nTotalKills);

        System.out.println("Tick last kill");
        lastKillTickStats = calculateStatsFromIntegerList(lastKillTick);

        // items collected
        System.out.println("Total items collected");
        nTotalItemsCollectedStats = calculateStatsFromIntegerList(nTotalItemsCollected);

        System.out.println("Tick last collection");
        lastCollectionTickStats = calculateStatsFromIntegerList(lastCollectionTick);
    }

    private StatisticalSummaryValues calculateStatsFromIntegerList(ArrayList<Integer> integerDataList) {
        if (integerDataList.size() == 0) {
            return null;
        }

        SummaryStatistics stats = new SummaryStatistics();
        for (Integer data : integerDataList) {
            stats.addValue((double) data);
        }

        StatisticalSummaryValues statsVariable = (StatisticalSummaryValues) stats.getSummary();
        System.out.println(statsVariable.toString());

        return statsVariable;
    }

    private StatisticalSummaryValues calculatePercentageStatsFromIntegerList(ArrayList<Integer> integerDataList, int total) {
        if ((integerDataList.size() == 0) || (total <= 0)) {
            return null;
        }

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
        if (doubleDataList.size() == 0) {
            return null;
        }

        SummaryStatistics stats = new SummaryStatistics();
        for (Double data : doubleDataList) {
            stats.addValue(data);
        }

        StatisticalSummaryValues statsVariable = (StatisticalSummaryValues) stats.getSummary();
        System.out.println(statsVariable.toString());

        return statsVariable;
    }

    private double[][] calculateAverageExplorationHeatMap() {
        if (heatMapExplorationMatrix.size() == 0) {
            return null;
        }

        // get first element to set the correct size to the matrix
        int[][] firstElement = heatMapExplorationMatrix.get(0);
        double[][] avgMatrix = new double[firstElement.length][firstElement[0].length];
    
        for (int i = 0; i < heatMapExplorationMatrix.size(); i++) {
            for (int y = 0; y < heatMapExplorationMatrix.get(i).length; y++) {
                for (int x = 0; x < heatMapExplorationMatrix.get(i)[y].length; x++) {
                    int nVisits = heatMapExplorationMatrix.get(i)[y][x];
                    avgMatrix[y][x] += (double) nVisits;
                }
            }
        }

        int nElements = heatMapExplorationMatrix.size();
        for (int y = 0; y < avgMatrix.length; y++) {
            for (int x = 0; x < avgMatrix[y].length; x++) {
                double nVisits = avgMatrix[y][x];
                avgMatrix[y][x] = (nVisits / nElements);
            }
        }

        return avgMatrix;
    }

    // risk analyst
    // novelty explorer
}
