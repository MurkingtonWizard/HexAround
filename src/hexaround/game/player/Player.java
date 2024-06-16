package hexaround.game.player;

import hexaround.config.PlayerConfiguration;
import hexaround.game.creature.CreatureName;

import java.util.HashMap;

public class Player {
    private final PlayerConfiguration config;
    private final HashMap<CreatureName,Integer> creatureCount;
    private int turnCount;

    /**
     * Constructor for player, creates a hashmap to keep track of count of creatures place by type
     * @param config PlayerConfiguration of this player
     */
    public Player(PlayerConfiguration config) {
        this.config = config;
        this.creatureCount = new HashMap<>();
        for(CreatureName name : config.creatures().keySet()) {
            creatureCount.put(name,0);
        }
        turnCount = 0;
    }

    /**
     * Getter for the PlayerName in config
     * @return PlayerName
     */
    public PlayerName getName() {
        return config.Player();
    }

    /**
     * Determines if a player is able to place creature based on max number
     * @param creature creature to be placed
     * @return true if player can place another creature of CreatureName, otherwise false
     */
    public boolean canPlace(CreatureName creature) {
        if(needsButterfly() && creature != CreatureName.BUTTERFLY) {
            return false;
        }
        Integer count = creatureCount.get(creature);
        Integer maxCreature = config.creatures().get(creature);
        return maxCreature != null &&
                count < maxCreature;
    }

    /**
     * Records that a player has place a creature of CreatureName
     * @param creature creature that was placed
     */
    public void recordPlacement(CreatureName creature) {
        int count = creatureCount.get(creature);
        creatureCount.replace(creature,count+1);
    }

    /**
     * Records that a player's creature has been removed from the board
     * @param creature creature that was removed
     */
    public void recordRemoval(CreatureName creature) {
        int count = creatureCount.get(creature);
        creatureCount.replace(creature,count-1);
    }

    /**
     * Increments the turn counter
     */
    public void takeTurn() {
        turnCount++;
    }

    /**
     * Determines if a player needs to place a butterfly
     * @return true if the player needs to place a butterfly, otherwise false
     */
    public boolean needsButterfly() {
        return turnCount >= 3 && creatureCount.get(CreatureName.BUTTERFLY) == 0;
    }
}
