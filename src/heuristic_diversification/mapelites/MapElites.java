/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package heuristic_diversification.mapelites;

import java.util.ArrayList;

import heuristic_diversification.framework.TeamGameplay;
import heuristic_diversification.model.GameStats;

/**
 * Contains the definition of the MAP-Elites as well as the methos required to
 * obtain the performance and features of a certain agent-heuristics
 * combination.
 * 
 * For the MAP-Elites algorithm adaptation for automatic gameplay agents, the
 * performance and features information is retrieved from the stats of the game.
 * The specifics of the elements to consider for these will depend on the
 * configuration set when running the algorithm
 */
public class MapElites {
    // Map elites config
    private Performance performanceCriteria;
    private Features featureInfoX;
    private Features featureInfoY;

    transient private TeamGameplay gameplayFramework;

    private ArrayList<EliteIdx> occupiedCellsIdx;
    private Elite[][] mapElites;

    transient private Double heuristicsWeightList[];
    transient private String controller;

    private class EliteIdx {
        int x;
        int y;

        EliteIdx(int featureX, int featureY) {
            this.x = featureX;
            this.y = featureY;
        }
    }

    public MapElites(Performance performance, Features featureX, Features featureY, TeamGameplay gameplayFramework, String controller, Double heuristicsWeightList[], int nRandomInitialisations) {
        mapElites = new Elite[featureX.featureArraySize()][featureY.featureArraySize()];
        occupiedCellsIdx = new ArrayList<EliteIdx>();
        
        this.performanceCriteria = performance;
        this.featureInfoX = featureX;
        this.featureInfoY = featureY;
        this.gameplayFramework = gameplayFramework;
        this.controller = controller;
        this.heuristicsWeightList = heuristicsWeightList;

        initialiseMap(nRandomInitialisations);
    }

    /**
     * Initialise map. Randomise nRandomInitialisations configurations (weights) and assign the resulting
     * agents to the correspondent cell in the MAP
     * @param nRandomInitialisations number of random initialisations requested
     */
    private void initialiseMap(int nRandomInitialisations) {
        // Create an elite with only one of each of the behaviours, set to 1.0 and the others to 0.0
        for (int i = 0; i < heuristicsWeightList.length; i++) {
            System.out.println("MAPELites initialisation with behaviour for weight in " + i);
            // Set all the weights to 0.0 and the current one to 1.0
            for (int j = 0; j < heuristicsWeightList.length; j++) {
                heuristicsWeightList[j] = 0.0;
            }
            heuristicsWeightList[i] = 1.0;

            // Create elite for the current behaviour
            Elite elite = createGameplayElite();
            addEliteToMap(elite);
        }

        // Random initialisations
        for (int i = 1; i < (nRandomInitialisations + 1); i++) {
            System.out.println("MAPELites initialisation iteration " + i);
            Generator.setRandomWeights(heuristicsWeightList);

            // Create elite from random values and add to map
            Elite elite = createGameplayElite();
            addEliteToMap(elite);
        }

        System.out.println("MAPElites initialised: " + getNCellsOccupied() + " cells occupied\n");
    }

    /**
     * MAP elites algorithm - iterate nIterations times
     * 1) get random elite from map
     * 2) evolve weights taking random elite as base
     * 3) create new elite (and get its stats to be able to get features and performance)
     * 4) add elite to map (assign to cell and replace elite in assigned cell if the new performance is better)
     * @param nTotalIterations number of iterations of the map elites algorithm
     */
    public void runAlgorithm(int nTotalIterations) {
        int nIterations = 0;
        while(nIterations < nTotalIterations) {
            System.out.println("MAPELites algorithm iteration " + (nIterations + 1));
            
            // get random cell elite and a copy of its weights
            Elite randomElite = getRandomEliteFromMap();
            randomElite.copyWeightsListValues(heuristicsWeightList);

            // evol weights
            evolveHeuristicsWeights(heuristicsWeightList);

            // Create new possible elite and add to map
            Elite newElite = createGameplayElite();
            addEliteToMap(newElite);

            nIterations++;
        }

        // When the algorithm is over, we need to make sure the final elites have all the data available
        processMapElitesData();
    }

    /**
     * Make all the data of the elites available: calculate all stats and set the data needed for serialisation.
     * While the algorithm is running, not all gameStats data is being calculated so it is needed to got through
     * all the occupied cells in the map to process all the data of the elite so it is available when their
     * information is printed or serialised.
     */
    public void processMapElitesData() {
        for (EliteIdx eliteIdx : occupiedCellsIdx) {
            Elite elite = mapElites[eliteIdx.x][eliteIdx.y];
            elite.calculateAllStats();
            elite.setDataForSerialisation(performanceCriteria, new Features[]{featureInfoX, featureInfoY});
        }
    }

    private int getNCellsOccupied() {
        return occupiedCellsIdx.size();
    }

    private Elite getRandomEliteFromMap() {
        EliteIdx eliteIdx = (EliteIdx) Generator.getRandomElementFromArray(occupiedCellsIdx);
        return mapElites[eliteIdx.x][eliteIdx.y];
    }

    private void evolveHeuristicsWeights(Double[] heuristicsWeightList) {
        Generator.evolveWeightList(heuristicsWeightList);
    }
    
    private Elite createGameplayElite() {
        // Get the game stats needed for the calculations for the map for current controller and weights. 
        // During the iteration of the algorithm, only the stats involved in performance and features are needed
        GameStats gameStats = gameplayFramework.createMapEliteStatsFromGameplay(controller, performanceCriteria, new Features[]{featureInfoX, featureInfoY});

        // Create elite with information and results
        return new Elite(controller, heuristicsWeightList, gameStats);
    }

    private void addEliteToMap(Elite elite) {
        int featureX = elite.getFeatureIdx(featureInfoX);
        int featureY = elite.getFeatureIdx(featureInfoY);

        Elite currentElite = mapElites[featureX][featureY];

        System.out.println("New elite w/ weights: " + elite.printWeights() + " --> Cell ("+ featureX + ", " + featureY + ")");
        if (currentElite == null) {
            elite.setDataForSerialisation(performanceCriteria, new Features[]{featureInfoX, featureInfoY});
            mapElites[featureX][featureY] = elite;
            occupiedCellsIdx.add(new EliteIdx(featureX, featureY));
        } else {
            System.out.println("Cell occupied by elite w/ weights: " + currentElite.printWeights()+ ". Performances: " + elite.getPerformance(performanceCriteria) + " vs " + currentElite.getPerformance(performanceCriteria));
            // substitute the current elite only if thew new one has better performance
            if (Double.compare(elite.getPerformance(performanceCriteria), currentElite.getPerformance(performanceCriteria)) > 0) {
                System.out.println("New elite has better performance; replace");
                elite.setDataForSerialisation(performanceCriteria, new Features[]{featureInfoX, featureInfoY});
                mapElites[featureX][featureY] = elite;
            }
        }
        System.out.println("\n");
    }

    public void printMapElitesInfo(String statsResultsFileName) {
        System.out.println("MAP Elites cells X: "+ featureInfoX.name()+ " (vertical); Y: " + featureInfoY.name() + " (horizontal)\n");
        for (int x = 0; x < mapElites.length; x++) {
            for (int y = 0; y < mapElites[x].length; y++) {
                if (mapElites[x][y] != null) {
                    System.out.print(" X ");
                } else {
                    System.out.print(" - ");
                }
            }
            System.out.println("\n");
        }
        System.out.println("Elites info: ");
        for (int x = 0; x < mapElites.length; x++) {
            for (int y = 0; y < mapElites[x].length; y++) {
                if (mapElites[x][y] != null) {
                    System.out.println("-----------------------");
                    System.out.println("(" + x + ", " + y + ")");
                    Elite elite = mapElites[x][y];
                    elite.printInfo(statsResultsFileName);
                }
            }
        }
    }
}
