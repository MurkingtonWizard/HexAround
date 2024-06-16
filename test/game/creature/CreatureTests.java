package game.creature;

import hexaround.config.CreatureDefinition;
import hexaround.config.GameConfiguration;
import hexaround.config.HexAroundConfigurationMaker;
import hexaround.game.GameManager;
import hexaround.game.board.Board;
import hexaround.game.creature.Creature;
import hexaround.game.creature.CreatureName;
import hexaround.game.player.PlayerName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class CreatureTests {
    static HexAroundConfigurationMaker configurationMaker;
    Board board;
    static GameConfiguration configuration;
    static HashMap<CreatureName,CreatureDefinition> creatureDefs;

    @BeforeAll
    static void setUpMaker() throws IOException {
        configurationMaker =
                new HexAroundConfigurationMaker("testConfigurations/GameConfigWithSwapping.hgc");
        configuration = configurationMaker.makeConfiguration();
        creatureDefs = new HashMap<>();
        for(CreatureDefinition def : configuration.creatures()) {
            creatureDefs.put(def.name(),def);
        }
    }
    @BeforeEach
    void setUp() {
        board = new Board();
    }
    @Test //T36
    void toStringCreature() {
        Creature creature = new Creature(PlayerName.RED,creatureDefs.get(CreatureName.BUTTERFLY));
        assertEquals("RED Butterfly", creature.toString());
    }
    @Test //T37
    void validEndHexagon_swappingCreature() {
        Creature redCrab = new Creature(PlayerName.RED,creatureDefs.get(CreatureName.CRAB));
        Creature blueHum = new Creature(PlayerName.BLUE,creatureDefs.get(CreatureName.HUMMINGBIRD));
        board.placeCreature(blueHum,0,0);
        board.placeCreature(redCrab,0,1);
        assertTrue(blueHum.validEndHexagon(0,1,board));
    }
    @Test //T38
    void validEndHexagon_noSpecialAbilities() {
        Creature redCrab = new Creature(PlayerName.RED,creatureDefs.get(CreatureName.CRAB));
        Creature blueCrab = new Creature(PlayerName.BLUE,creatureDefs.get(CreatureName.CRAB));
        board.placeCreature(blueCrab,0,0);
        board.placeCreature(redCrab,0,1);
        assertFalse(blueCrab.validEndHexagon(0,1,board));
    }
    @Test //T39
    void creatureDefinitionToString() {
        assertEquals("[Walking, Butterfly]",creatureDefs.get(CreatureName.BUTTERFLY).properties().toString());
    }
}
