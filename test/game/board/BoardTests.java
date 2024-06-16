package game.board;

import hexaround.config.CreatureDefinition;
import hexaround.game.board.Board;
import hexaround.game.creature.Creature;
import hexaround.game.creature.CreatureName;
import hexaround.game.creature.CreatureProperty;
import hexaround.game.player.PlayerName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardTests {
    Board board;
    @BeforeEach
    void setUp() {
        board = new Board();
    }
    @Test //T40
    void connectedPlacement() {
        board.placeCreature(new Creature(null,new CreatureDefinition(CreatureName.BUTTERFLY,1,new LinkedList<>())),0,0);
        assertTrue(board.connectedPlacement(0,1));
        assertFalse(board.connectedPlacement(0,2));
    }
    @Test //T41
    void canMove_noHexagons() {
        assertFalse(board.canMove(PlayerName.RED));
    }
    @Test //T42
    void canMove_running() {
        Creature blue = new Creature(PlayerName.BLUE,new CreatureDefinition(CreatureName.BUTTERFLY,1,new LinkedList<>()));
        LinkedList<CreatureProperty> running = new LinkedList<>();
        running.add(CreatureProperty.RUNNING);
        Creature red = new Creature(PlayerName.RED,new CreatureDefinition(CreatureName.DOVE,1,running));
        board.placeCreature(blue,0,1);
        board.placeCreature(blue,1,0);
        board.placeCreature(blue,1,-1);
        board.placeCreature(blue,0,-1);
        board.placeCreature(red,0,0);
        assertTrue(board.canMove(PlayerName.RED));
    }
}
