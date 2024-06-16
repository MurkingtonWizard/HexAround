package game.board.hexagon;

import hexaround.game.board.hexagon.Hexagon;
import hexaround.game.board.hexagon.Surrounding;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HexagonTests {
    @Test //T43
    void hexagonEquals() {
        Hexagon one = new Hexagon(0,0);
        Hexagon two = new Hexagon(0,0);
        assertEquals(one,two);
        assertNotEquals(two, null);
        assertNotEquals(two, Surrounding.UP);
    } //T44
    @Test
    void toStringHexagon() {
        assertEquals("(0,0)",(new Hexagon(0,0)).toString());
    } //T45
    @Test
    void getHexagonColor_noCreatures() {
        assertNull((new Hexagon(0,0).getHexagonColor()));
    }
}
