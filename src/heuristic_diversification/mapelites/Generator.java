/**
 * Author: Cristina Guerrero
 * Date: 12th February 2021
 */

package heuristic_diversification.mapelites;

import java.util.ArrayList;
import java.util.Random;

/**
 * Randomising and evolutionary methods
 */
public class Generator {
    
    /**
     * Returns a random element from the arraylist provided
     * @param list
     * @return random Object
     */
    public static Object getRandomElementFromArray(ArrayList<?> list) {
        int randomId = Generator.generateRandomInteger(list.size());
        return list.get(randomId);
    }

    /**
     * Set random weights in the list provided. Weights are expected to be in range [0.0, 1.0]
     * The specific values the weights can be given are specified by generateRandomWeight
     * @param weightList list to be filled with random weights
     */
    public static void setRandomWeights(Double[] weightList) {
        for (int i = 0; i < weightList.length; i++) {
            weightList[i] = Generator.generateRandomWeight();
        }
    }

    /**
     * Evolve list of weights by the evolution method of choice
     * @param weightList list of weights to be evolved
     */
    public static void evolveWeightList(Double[] weightList) {
        stochasticHillClimberMutation(weightList);
    }

    /**
     * Generates a random integer between 0 and the max value. 
     * The maximum value is excluded from the generation.
     * @param max excluded maximum value
     * @return
     */
    private static int generateRandomInteger(int max) {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(max);
    }

    /**
     * Generates a random weight. 
     * The specifics of the values given to the weights are decided by the method called.
     * @return a weight value randomnly chosen
     */
    private static Double generateRandomWeight() {
        return generateRandomTwoDecimalDouble();
    }

    /**
     * Generates a double in range [0.0, 1.0). This double can be any value in this range.
     * @return
     */
    private static Double generateRandomDouble() {
        Random randomGenerator = new Random();
        return randomGenerator.nextDouble();
    }

    /**
     * Generates a double in range [0.00, 1.00]. The double can take any 2 decimal value in this range.
     * @return
     */
    private static Double generateRandomTwoDecimalDouble() {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(101);
        return (double) randomInt / 100.0;
    }

    /**
     * Mutates the given list of weights using a simple stochastic hill climber mutation:
     * 1) Get a random position of the population to be mutated
     * 2) Generate a new random weight for it
     * @param weightList list of weights to be mutated
     */
    private static void stochasticHillClimberMutation(Double[] weightList) {
        int randomElementId = Generator.generateRandomInteger(weightList.length);
        weightList[randomElementId] = Generator.generateRandomWeight();
    }
}
