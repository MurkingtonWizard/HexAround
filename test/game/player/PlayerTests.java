package game.player;

import hexaround.config.CreatureDefinition;
import hexaround.config.GameConfiguration;
import hexaround.config.HexAroundConfigurationMaker;
import hexaround.config.PlayerConfiguration;
import hexaround.game.board.Board;
import hexaround.game.creature.CreatureName;
import hexaround.game.player.Player;
import hexaround.game.player.PlayerName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class PlayerTests {
    static HexAroundConfigurationMaker configurationMaker;
    Board board;
    static GameConfiguration configuration;
    static HashMap<PlayerName, PlayerConfiguration> playerConfig;

    @BeforeAll
    static void setUpMaker() throws IOException {
        configurationMaker =
                new HexAroundConfigurationMaker("testConfigurations/FirstConfiguration.hgc");
        configuration = configurationMaker.makeConfiguration();
        playerConfig = new HashMap<>();
        for(PlayerConfiguration def : configuration.players()) {
            playerConfig.put(def.Player(),def);
        }
    }
    @BeforeEach
    void setUp() {
    }
    @Test //T46
    void canPlace_CreatureDNE() {
        Player blue = new Player(playerConfig.get(PlayerName.BLUE));
        assertFalse(blue.canPlace(CreatureName.DOVE));
    }
}
