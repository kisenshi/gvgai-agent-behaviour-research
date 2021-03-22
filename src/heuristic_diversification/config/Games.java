/**
 * Author: Cristina Guerrero
 * Date: 16th February 2021
 */

package heuristic_diversification.config;

public enum Games {

    BUTTERFLIES(0, "butterflies", new int[]{ 206 }),
    ZELDA(1, "zelda", new int[]{ 64, 126 }),
    DIGDUG(2, "digdug", new int[]{ 405 });

    int id;
    String gameName;
    int[] lvlsNavigationSize;

    private static final String GAMES_PATH = "examples/experiments/";

    Games (int id, String gameName, int[] lvlsNavigationSize) {
        this.id = id;
        this.gameName = gameName;
        this.lvlsNavigationSize = lvlsNavigationSize;
    }

    public String game() {
        return GAMES_PATH + gameName + ".txt";
    }

    public String level(int levelIdx) {
        return GAMES_PATH + gameName + "_lvl" + levelIdx + ".txt";
    }

    public String getGameName() {
        return gameName;
    }

    public int levelNavigationSize(int levelIdx) {
        if (lvlsNavigationSize.length < (levelIdx + 1)){
            return 0;
        }
        return lvlsNavigationSize[levelIdx];
    }
    
}
