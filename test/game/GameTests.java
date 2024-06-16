package game;

import hexaround.game.GameManager;
import hexaround.game.HexAroundGameBuilder;
import hexaround.game.move.MoveResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static hexaround.game.creature.CreatureName.*;
import static hexaround.game.move.MoveResult.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTests {
    GameManager gameManager;
    @BeforeEach
    void gameMaker() {
        setGameManager("testConfigurations/FirstConfiguration.hgc");
    }
    void setGameManager(String file) {
        try {
            gameManager =
                    (GameManager) HexAroundGameBuilder.buildGameManager(file);
        } catch (IOException e) {}
    }
    void fullGame() {
        setGameManager("testConfigurations/Submission2.hgc");
        gameManager.placeCreature(BUTTERFLY, 0, 0);
        gameManager.placeCreature(BUTTERFLY, 0, 1);
        gameManager.placeCreature(DOVE, 0, -1);
        gameManager.placeCreature(TURTLE, -1, 2);
        gameManager.placeCreature(TURTLE, -1, -1);
        gameManager.placeCreature(DOVE, 0, 2);
        gameManager.placeCreature(DOVE, 0, -2);
        gameManager.placeCreature(DOVE, 1, 1);
        gameManager.placeCreature(TURTLE, 1, -2);
        gameManager.placeCreature(TURTLE, 2, 0);
    }
    @Test //T1
    void getCreatureAt_noCreature() {
        assertTrue(gameManager.getCreaturesAt(0,0).isEmpty());
    }
    @Test //T2
    void placeCreature_placeCreatureOnAnother() {
        assertEquals(OK,gameManager.placeCreature(GRASSHOPPER,0,0).moveResult());
        assertEquals(MOVE_ERROR,gameManager.placeCreature(BUTTERFLY,0,0).moveResult());
    }
    @Test //T3
    void getCreaturesAt_creaturesPlaced() {
        gameManager.placeCreature(BUTTERFLY,0,0);
        assertEquals(BUTTERFLY,gameManager.getCreaturesAt(0,0).getLast().getName());
        gameManager.placeCreature(GRASSHOPPER,0,1);
        assertEquals(GRASSHOPPER,gameManager.getCreaturesAt(0,1).getLast().getName());
    }
    @Test //T4
    void getCreaturesAt_creatureMoved() {
        assertTrue(gameManager.getCreaturesAt(0,0).isEmpty());
        gameManager.placeCreature(BUTTERFLY,0,0);
        gameManager.placeCreature(GRASSHOPPER,0,1);
        gameManager.moveCreature(BUTTERFLY,0,0,-1,1);
        assertTrue(gameManager.getCreaturesAt(0,0).isEmpty());
    }
    @Test //T5
    void moveCreature_noCreatureAtLocationAndWrongCreatureName() {
        assertEquals(MOVE_ERROR,gameManager.moveCreature(BUTTERFLY,0,0,0,1).moveResult());
        gameManager.placeCreature(GRASSHOPPER,0,0);
        gameManager.placeCreature(BUTTERFLY,0,1);
        assertEquals(MOVE_ERROR,gameManager.moveCreature(BUTTERFLY,0,0,0,1).moveResult());
    }
    @Test //T6
    void moveCreature_movingCauseDisconnect() {
        gameManager.placeCreature(BUTTERFLY,0,0);
        gameManager.placeCreature(BUTTERFLY,1,0);
        gameManager.placeCreature(GRASSHOPPER,-1,1);
        gameManager.placeCreature(GRASSHOPPER,1,1);
        MoveResponse response = gameManager.moveCreature(BUTTERFLY,0,0,0,-1);
        assertEquals(MOVE_ERROR,response.moveResult());
        assertEquals("Colony is not connected, try again",response.message());
    }
    @Test //T7
    void moveCreature_validMove() {
        gameManager.placeCreature(GRASSHOPPER,0,0);
        gameManager.placeCreature(BUTTERFLY,1,0);
        gameManager.placeCreature(BUTTERFLY,-1,1);
        gameManager.placeCreature(GRASSHOPPER,1,1);
        MoveResponse response = gameManager.moveCreature(BUTTERFLY,-1,1,0,1);
        assertEquals(OK,response.moveResult());
        assertEquals("Legal move",response.message());
    }
    @Test //T8
    void moveCreature_validMoveOverlapping() {
        gameManager.placeCreature(BUTTERFLY,0,0);
        gameManager.placeCreature(GRASSHOPPER,-1,1);
        gameManager.placeCreature(GRASSHOPPER,1,0);
        gameManager.placeCreature(BUTTERFLY,-1,2);
        MoveResponse response = gameManager.moveCreature(GRASSHOPPER,1,0,-1,2);
        assertEquals(OK,response.moveResult());
    }
    @Test //T9
    void placeCreature_checksForDisconnect() {
        gameManager.placeCreature(GRASSHOPPER,0,0);
        gameManager.placeCreature(BUTTERFLY,1,0);
        gameManager.placeCreature(GRASSHOPPER,2,0);
        MoveResponse response = gameManager.placeCreature(BUTTERFLY,3,3);
        assertEquals(MOVE_ERROR, response.moveResult());
    }
    @Test //T10
    void fullGame_firstMoves() {
        fullGame();
        MoveResponse response = gameManager.moveCreature(TURTLE, -1, -1, -1, 0);
        assertEquals(OK, response.moveResult());

        fullGame();
        response = gameManager.moveCreature(BUTTERFLY, 0, 0, 1, -1);
        assertEquals(MOVE_ERROR, response.moveResult());

        fullGame();
        response = gameManager.moveCreature(DOVE, 0, -2, 0, 3);
        assertEquals(OK, response.moveResult());

        fullGame();
        response = gameManager.moveCreature(DOVE, 0, -1, 0, 3);
        assertEquals(MOVE_ERROR, response.moveResult());
    }
    @Test //T11
    void moveCreature_WalkingDistTooLarge() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(CRAB,0,0);
        gameManager.placeCreature(CRAB,1,0);
        gameManager.placeCreature(BUTTERFLY,-1,1);
        MoveResponse response = gameManager.moveCreature(CRAB,1,0,-2,2);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.moveCreature(CRAB,1,0,0,1);
        assertEquals(OK, response.moveResult());
        gameManager.placeCreature(CRAB,-2,2);
        response = gameManager.moveCreature(CRAB,0,1,-1,0);
        assertEquals(MOVE_ERROR, response.moveResult());
    }
    @Test //T12
    void moveCreature_draggableWalk() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(BUTTERFLY,0,0);
        gameManager.placeCreature(CRAB,-1,0);
        gameManager.placeCreature(CRAB,0,1);
        gameManager.placeCreature(BUTTERFLY,1,1);
        MoveResponse response = gameManager.moveCreature(BUTTERFLY,0,0,-1,1);
        assertEquals(MOVE_ERROR, response.moveResult());
    }
    @Test //T13
    void moveCreature_flyingCreature() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(DOVE,0,0);
        gameManager.placeCreature(CRAB,-1,0);
        gameManager.placeCreature(CRAB,0,1);
        gameManager.placeCreature(BUTTERFLY,-1,-1);
        MoveResponse response = gameManager.moveCreature(DOVE,0,0,-1,1);
        assertEquals(OK, response.moveResult());
    }
    @Test //T14
    void isGameOver_RedWins() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(BUTTERFLY,0,0);
        gameManager.placeCreature(BUTTERFLY,-1,0);
        gameManager.placeCreature(CRAB,0,1);
        gameManager.placeCreature(CRAB,-1,-1);
        gameManager.placeCreature(CRAB,-1,2);
        gameManager.placeCreature(CRAB,0,-2);
        gameManager.placeCreature(CRAB,1,1);
        gameManager.placeCreature(CRAB,1,-2);
        MoveResponse response = gameManager.moveCreature(CRAB,-1,2,-1,1);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(CRAB,1,-2,0,-1);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(CRAB,1,1,1,0);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(CRAB,0,-2,1,-1);
        assertEquals(RED_WON, response.moveResult());
    }
    @Test //T15
    void isGameOver_BlueWins() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(BUTTERFLY,-1,0);
        gameManager.placeCreature(BUTTERFLY,0,0);
        gameManager.placeCreature(CRAB,-1,-1);
        gameManager.placeCreature(CRAB,0,1);
        gameManager.placeCreature(CRAB,0,-2);
        gameManager.placeCreature(CRAB,-1,2);
        gameManager.placeCreature(CRAB,1,-2);
        gameManager.placeCreature(CRAB,1,1);
        MoveResponse response = gameManager.moveCreature(CRAB,1,-2,0,-1);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(CRAB,-1,2,-1,1);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(CRAB,0,-2,1,-1);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(CRAB,1,1,1,0);
        assertEquals(BLUE_WON, response.moveResult());
    }
    @Test //T16
    void isGameOver_Draw() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(BUTTERFLY,0,0);
        gameManager.placeCreature(BUTTERFLY,-1,0);
        gameManager.placeCreature(CRAB,0,1);
        gameManager.placeCreature(CRAB,-1,-1);
        gameManager.placeCreature(CRAB,-1,2);
        gameManager.placeCreature(CRAB,0,-2);
        gameManager.placeCreature(CRAB,1,0);
        gameManager.placeCreature(CRAB,-2,0);
        gameManager.placeCreature(CRAB,2,-1);
        gameManager.placeCreature(CRAB,-2,1);
        MoveResponse response = gameManager.moveCreature(CRAB,2,-1,1,-1);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(CRAB,0,-2,0,-1);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(CRAB,-1,2,-1,1);
        assertEquals(DRAW, response.moveResult());
    }
    @Test //T17
    void placeCreature_noAdjOppColor() {
        gameManager.placeCreature(BUTTERFLY,0,0);
        gameManager.placeCreature(BUTTERFLY,0,1);
        MoveResponse response = gameManager.placeCreature(GRASSHOPPER,0,2);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.placeCreature(GRASSHOPPER,-1,1);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.placeCreature(GRASSHOPPER,-1,0);
        assertEquals(OK, response.moveResult());
    }
    @Test //T18
    void placeCreature_butterflyUnder4() {
        gameManager.placeCreature(GRASSHOPPER,0,0); //1
        gameManager.placeCreature(GRASSHOPPER,-1,0);
        gameManager.placeCreature(GRASSHOPPER,0,1); //2
        gameManager.placeCreature(GRASSHOPPER,-1,-1);
        gameManager.placeCreature(GRASSHOPPER,-1,2); //3
        gameManager.placeCreature(GRASSHOPPER,0,-2);
        MoveResponse response = gameManager.placeCreature(GRASSHOPPER,1,0);//4
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.placeCreature(BUTTERFLY,1,0);
        assertEquals(OK, response.moveResult());
    }
    @Test //T19
    void moveCreature_flyingSurrounded() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(CRAB,0,0);
        gameManager.placeCreature(DOVE,-1,0);
        gameManager.placeCreature(CRAB,0,1);
        gameManager.placeCreature(CRAB,-1,-1);
        gameManager.placeCreature(CRAB,-1,2);
        gameManager.placeCreature(CRAB,0,-2);
        gameManager.placeCreature(BUTTERFLY,1,0);
        gameManager.placeCreature(BUTTERFLY,-2,0);
        gameManager.placeCreature(CRAB,2,-1);
        gameManager.placeCreature(CRAB,-2,1);
        MoveResponse response = gameManager.moveCreature(CRAB,2,-1,1,-1);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(CRAB,0,-2,0,-1);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(CRAB,-1,2,-1,1);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(DOVE,-1,0,0,-2);
        assertEquals(MOVE_ERROR, response.moveResult());
        assertEquals("Dove cannot move", response.message());
    }
    @Test //T20
    void moveCreature_jumpingCreature() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(BUTTERFLY,0,1);
        gameManager.placeCreature(GRASSHOPPER,0,0);
        gameManager.placeCreature(GRASSHOPPER,1,1);
        MoveResponse response = gameManager.moveCreature(GRASSHOPPER,0,0,0,2);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(GRASSHOPPER,1,1,-1,1);
        assertEquals(OK, response.moveResult());
        gameManager.placeCreature(GRASSHOPPER,0,3);
        gameManager.placeCreature(GRASSHOPPER,1,0);
        gameManager.placeCreature(BUTTERFLY,-1,3);
        gameManager.placeCreature(GRASSHOPPER,-2,1);
        response = gameManager.moveCreature(GRASSHOPPER,0,3,-1,2);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.moveCreature(GRASSHOPPER,0,3,0,0);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(GRASSHOPPER,1,0,-1,2);
        assertEquals(OK, response.moveResult());
        gameManager.placeCreature(BUTTERFLY,0,3);
        gameManager.placeCreature(GRASSHOPPER,-2,2);
        response = gameManager.moveCreature(GRASSHOPPER,0,0,0,4);
        assertEquals(MOVE_ERROR, response.moveResult());
    }
    @Test //T21
    void moveCreature_jumpingSurrounded() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(CRAB,0,0);
        gameManager.placeCreature(GRASSHOPPER,-1,0);
        gameManager.placeCreature(CRAB,0,1);
        gameManager.placeCreature(CRAB,-1,-1);
        gameManager.placeCreature(CRAB,-1,2);
        gameManager.placeCreature(CRAB,0,-2);
        gameManager.placeCreature(BUTTERFLY,1,0);
        gameManager.placeCreature(BUTTERFLY,-2,0);
        gameManager.placeCreature(CRAB,2,-1);
        gameManager.placeCreature(CRAB,-2,1);
        gameManager.moveCreature(CRAB,2,-1,1,-1);
        gameManager.moveCreature(CRAB,0,-2,0,-1);
        gameManager.moveCreature(CRAB,-1,2,-1,1);
        MoveResponse response = gameManager.moveCreature(GRASSHOPPER,-1,0,1,-2);
        assertEquals(OK, response.moveResult());
    }
    @Test //T22
    void moveCreature_KamikazeRemoves() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(BUTTERFLY,0,0);
        gameManager.placeCreature(DOVE,0,1);
        MoveResponse response = gameManager.placeCreature(BUTTERFLY,0,-1);
        assertEquals(MOVE_ERROR, response.moveResult());
        gameManager.placeCreature(DOVE,0,-1);
        response = gameManager.moveCreature(DOVE,0,1,0,0);
        assertEquals(OK, response.moveResult());
        assertEquals(1,gameManager.getCreaturesAt(0,0).size());
        response = gameManager.placeCreature(GRASSHOPPER,1,-2);
        assertEquals(OK, response.moveResult());
    }
    @Test //T23
    void moveCreature_threeCreaturesOnStack() {
        gameManager.placeCreature(GRASSHOPPER,0,0);
        gameManager.placeCreature(GRASSHOPPER,0,1);
        gameManager.placeCreature(GRASSHOPPER,0,-1);
        MoveResponse response = gameManager.moveCreature(GRASSHOPPER,0,1,0,0);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(GRASSHOPPER,0,-1,0,0);
        assertEquals(MOVE_ERROR, response.moveResult());
    }
    @Test //T24
    void butterflyIsRemoved() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(CRAB,0,0);
        gameManager.placeCreature(CRAB,-1,0);
        gameManager.placeCreature(CRAB,0,1);
        gameManager.placeCreature(CRAB,-1,-1);
        gameManager.placeCreature(CRAB,-1,2);
        gameManager.placeCreature(DOVE,0,-2);
        gameManager.placeCreature(BUTTERFLY,1,0);
        gameManager.placeCreature(BUTTERFLY,-2,0);
        gameManager.placeCreature(CRAB,2,-1);
        gameManager.moveCreature(DOVE,0,-2,1,0);
        MoveResponse response = gameManager.moveCreature(CRAB,2,-1,2,0);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.placeCreature(CRAB,2,0);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.placeCreature(BUTTERFLY,2,0);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.placeCreature(BUTTERFLY,-2,3);
        assertEquals(OK, response.moveResult());
    }
    @Test //T25
    void hasProperty_SwappingDoesNotSwap() {
        setGameManager("testConfigurations/GameConfigWithSwapping.hgc");
        gameManager.placeCreature(HUMMINGBIRD,0,0);
        gameManager.placeCreature(BUTTERFLY,0,1);
        MoveResponse response = gameManager.moveCreature(HUMMINGBIRD,0,0,0,2);
        assertEquals(OK, response.moveResult());
    }
    @Test //T26
    void moveCreature_swappingCreature() {
        setGameManager("testConfigurations/GameConfigWithSwapping.hgc");
        gameManager.placeCreature(BUTTERFLY,0,0);
        gameManager.placeCreature(BUTTERFLY,0,1);
        gameManager.placeCreature(HUMMINGBIRD,0,-1);
        gameManager.placeCreature(GRASSHOPPER,0,2);
        MoveResponse response = gameManager.moveCreature(HUMMINGBIRD,0,-1,0,1);
        assertEquals(MOVE_ERROR, response.moveResult());
        assertEquals("Hummingbird cannot land at 0,1",response.message());
        response = gameManager.moveCreature(HUMMINGBIRD,0,-1,0,2);
        assertEquals(OK, response.moveResult());
        assertEquals(GRASSHOPPER,gameManager.getCreaturesAt(0,-1).getFirst().getName());
        assertEquals(HUMMINGBIRD,gameManager.getCreaturesAt(0,2).getFirst().getName());
    }
    @Test //T27
    void moveCreature_runningCreature() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(HORSE,0,0);
        gameManager.placeCreature(BUTTERFLY,0,1);
        MoveResponse response = gameManager.moveCreature(HORSE,0,0,1,0);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.moveCreature(HORSE,0,0,1,1);
        assertEquals(OK, response.moveResult());
    }
    @Test //T28
    void butterflyTrapped() {
        gameManager.placeCreature(BUTTERFLY,0,0);
        gameManager.placeCreature(BUTTERFLY,0,1);
        gameManager.placeCreature(GRASSHOPPER,-1,0);
        gameManager.placeCreature(GRASSHOPPER,1,1);
        gameManager.moveCreature(GRASSHOPPER,-1,0,-1,2);
        gameManager.placeCreature(GRASSHOPPER,2,0);
        gameManager.placeCreature(GRASSHOPPER,-2,2);
        gameManager.moveCreature(GRASSHOPPER,2,0,1,0);
        gameManager.moveCreature(GRASSHOPPER,-2,2,-1,1);
        MoveResponse response = gameManager.moveCreature(BUTTERFLY,0,1,0,2);
        assertEquals(MOVE_ERROR, response.moveResult());
    }
    @Test //T29
    void cannotPlaceWinCondition_blue() {
        gameManager.placeCreature(BUTTERFLY,0,0);
        gameManager.placeCreature(BUTTERFLY,0,-1);
        gameManager.placeCreature(GRASSHOPPER,-1,1);
        gameManager.moveCreature(BUTTERFLY,0,-1,1,-1);
        MoveResponse response = gameManager.moveCreature(GRASSHOPPER,-1,1,2,-2);
        assertEquals(BLUE_WON, response.moveResult());
    }
    @Test //T30
    void cannotPlaceWinCondition_red() {
        gameManager.placeCreature(BUTTERFLY,0,0);
        gameManager.placeCreature(BUTTERFLY,0,1);
        gameManager.moveCreature(BUTTERFLY,0,0,1,0);
        gameManager.placeCreature(GRASSHOPPER,-1,1);
        gameManager.moveCreature(BUTTERFLY,1,0,1,1);
        MoveResponse response = gameManager.moveCreature(GRASSHOPPER,-1,1,2,1);
        assertEquals(RED_WON, response.moveResult());
    }
    @Test //T31
    void gameEnd_noTurns() {
        gameManager.placeCreature(BUTTERFLY,0,0);
        gameManager.placeCreature(BUTTERFLY,0,-1);
        gameManager.placeCreature(GRASSHOPPER,-1,1);
        gameManager.moveCreature(BUTTERFLY,0,-1,1,-1);
        gameManager.moveCreature(GRASSHOPPER,-1,1,2,-2);
        MoveResponse response = gameManager.moveCreature(BUTTERFLY,1,-1,0,0);
        assertEquals(MOVE_ERROR, response.moveResult());
        assertEquals("Game is over, cannot move creature",response.message());
        response = gameManager.placeCreature(BUTTERFLY,0,0);
        assertEquals(MOVE_ERROR, response.moveResult());
        assertEquals("Game is over, cannot place creature",response.message());
    }
    @Test //T32
    void gameEnd_BlueCanMoveNoPlace() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(BUTTERFLY,1,0);
        gameManager.placeCreature(BUTTERFLY,0,1);
        gameManager.placeCreature(GRASSHOPPER,2,0);
        gameManager.placeCreature(CRAB,-1,1);
        gameManager.placeCreature(CRAB,1,-1);
        gameManager.placeCreature(DOVE,-1,2);
        gameManager.moveCreature(GRASSHOPPER,2,0,0,0);
        gameManager.moveCreature(DOVE,-1,2,1,-1);
        gameManager.moveCreature(GRASSHOPPER,0,0,0,-1);
        gameManager.placeCreature(DOVE,-1,2);
        gameManager.moveCreature(GRASSHOPPER,0,-1,0,0);
        MoveResponse response = gameManager.moveCreature(DOVE,-1,2,2,0);
        assertEquals(OK,response.moveResult());
    }
    @Test //T33
    void gameEnd_noMove() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(BUTTERFLY,0,1);
        gameManager.placeCreature(BUTTERFLY,1,0);
        gameManager.placeCreature(CRAB,-1,1);
        gameManager.placeCreature(GRASSHOPPER,2,0);
        gameManager.placeCreature(DOVE,-1,2);
        gameManager.placeCreature(CRAB,1,-1);
        gameManager.moveCreature(DOVE,-1,2,1,-1);
        gameManager.moveCreature(GRASSHOPPER,2,0,0,0);
        gameManager.placeCreature(DOVE,-1,2);
        gameManager.moveCreature(GRASSHOPPER,0,0,0,-1);
        gameManager.moveCreature(DOVE,-1,2,2,0);
        MoveResponse response = gameManager.moveCreature(GRASSHOPPER,0,-1,0,0);
        assertEquals(OK,response.moveResult());
    }
    @Test //T34
    void endGame_noButterfly_red() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(GRASSHOPPER,0,0);
        gameManager.placeCreature(BUTTERFLY,-1,1);
        gameManager.placeCreature(BUTTERFLY,1,0);
        gameManager.placeCreature(CRAB,-2,1);
        gameManager.placeCreature(GRASSHOPPER,0,-1);
        gameManager.moveCreature(CRAB,-2,1,-1,0);
        gameManager.moveCreature(GRASSHOPPER,0,0,0,1);
        gameManager.placeCreature(DOVE,-2,2);
        gameManager.moveCreature(GRASSHOPPER,0,1,0,0);
        gameManager.placeCreature(DOVE,-2,1);
        gameManager.moveCreature(GRASSHOPPER,0,0,0,1);
        gameManager.moveCreature(DOVE,-2,1,0,-1);
        gameManager.moveCreature(GRASSHOPPER,0,1,0,0);
        MoveResponse response = gameManager.moveCreature(DOVE,-2,2,1,0);
        assertEquals(RED_WON,response.moveResult());
    }
    @Test //T35
    void endGame_noButterfly_blue() {
        setGameManager("testConfigurations/OGLevel1.hgc");
        gameManager.placeCreature(BUTTERFLY,-1,1);
        gameManager.placeCreature(GRASSHOPPER,0,1);
        gameManager.placeCreature(CRAB,-2,1);
        gameManager.placeCreature(BUTTERFLY,1,0);
        gameManager.moveCreature(CRAB,-2,1,-1,0);
        gameManager.placeCreature(GRASSHOPPER,1,-1);
        gameManager.placeCreature(DOVE,-2,2);
        gameManager.moveCreature(GRASSHOPPER,0,1,0,0);
        gameManager.placeCreature(DOVE,-2,1);
        gameManager.moveCreature(GRASSHOPPER,0,0,0,1);
        gameManager.moveCreature(DOVE,-2,1,1,-1);
        gameManager.moveCreature(GRASSHOPPER,0,1,0,0);
        MoveResponse response = gameManager.moveCreature(DOVE,-2,2,1,0);
        assertEquals(BLUE_WON,response.moveResult());
    }
    @Test //T47
    void fullGameTest () {
        setGameManager("testConfigurations/FullGame.hgc");
        gameManager.placeCreature(HORSE,0,0);
        MoveResponse response = gameManager.placeCreature(RABBIT,-1,0);
        gameManager.placeCreature(GRASSHOPPER,0,1);
        assertEquals(OK,response.moveResult());
        response = gameManager.placeCreature(GRASSHOPPER,0,-1);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.placeCreature(SPIDER,-2,0);
        assertEquals(OK,response.moveResult());
        response = gameManager.moveCreature(GRASSHOPPER,0,1,-1,-1);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.moveCreature(GRASSHOPPER,0,1,-2,1);
        assertEquals(OK,response.moveResult());
        gameManager.moveCreature(SPIDER,-2,0,-2,1);
        response = gameManager.moveCreature(SPIDER,-2,0,1,0);
        assertEquals(OK,response.moveResult());
        response = gameManager.moveCreature(HUMMINGBIRD,0,0,-2,2);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.moveCreature(GRASSHOPPER,-2,1,-1,1);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.placeCreature(BUTTERFLY,-1,-1);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.placeCreature(BUTTERFLY,-2,2);
        assertEquals(OK,response.moveResult());
        response = gameManager.placeCreature(BUTTERFLY,2,-1);
        assertEquals(OK, response.moveResult());
        gameManager.placeCreature(DOVE,-3,2);
        response = gameManager.moveCreature(BUTTERFLY,2,-1,1,-1);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(DOVE,-3,2,1,-1);
        assertEquals(OK, response.moveResult());
        response = gameManager.placeCreature(GRASSHOPPER,2,-2);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.moveCreature(BUTTERFLY,1,-1,0,-1);
        assertEquals(OK, response.moveResult());
        gameManager.moveCreature(BUTTERFLY,-2,2,-1,1);
        gameManager.placeCreature(HORSE,2,0);
        response = gameManager.moveCreature(GRASSHOPPER,-2,1,0,-1);
        assertEquals(OK, response.moveResult());
        response = gameManager.placeCreature(RABBIT,1,1);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.moveCreature(HORSE,2,0,0,1);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.placeCreature(BUTTERFLY,1,1);
        assertEquals(OK, response.moveResult());
        gameManager.placeCreature(RABBIT,1,-2);
        gameManager.placeCreature(DUCK,0,2);
        response = gameManager.placeCreature(HORSE,0,-2);
        assertEquals(MOVE_ERROR, response.moveResult());
        gameManager.placeCreature(TURTLE,-2,2);
        response = gameManager.moveCreature(HORSE,2,0,2,-1);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.moveCreature(HORSE,2,0,1,-1);
        assertEquals(OK, response.moveResult());
        gameManager.moveCreature(DOVE,2,0,1,-1);
        gameManager.moveCreature(RABBIT,-1,0,0,1);
        response = gameManager.moveCreature(RABBIT,-1,0,-1,2);
        assertEquals(OK, response.moveResult());
        gameManager.moveCreature(GRASSHOPPER,0,-1,1,-1);
        response = gameManager.moveCreature(GRASSHOPPER,0,-1,0,1);
        assertEquals(OK, response.moveResult());
        gameManager.placeCreature(BUTTERFLY,2,1);
        response = gameManager.placeCreature(DOVE,2,-1);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.placeCreature(DOVE,2,0);
        assertEquals(OK, response.moveResult());
        response = gameManager.placeCreature(CRAB,0,-1);
        assertEquals(OK, response.moveResult());
        gameManager.placeCreature(HUMMINGBIRD,0,3);
        response = gameManager.moveCreature(GRASSHOPPER,0,1,0,-2);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(DOVE,2,0,0,1);
        assertEquals(OK, response.moveResult());
        response = gameManager.placeCreature(CRAB,1,-2);
        assertEquals(MOVE_ERROR, response.moveResult());
        gameManager.placeCreature(CRAB,0,-3);
        gameManager.moveCreature(DOVE,0,1,-1,3);
        gameManager.placeCreature(SPIDER,2,0);
        gameManager.moveCreature(BUTTERFLY,-1,1,-2,1);
        response = gameManager.moveCreature(HORSE,1,-1,2,0);
        assertEquals(OK, response.moveResult());
        response = gameManager.placeCreature(HUMMINGBIRD,2,-2);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.moveCreature(CRAB,0,-3,1,-2);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(SPIDER,1,0,2,-1);
        assertEquals(MOVE_ERROR, response.moveResult());
        gameManager.moveCreature(HORSE,2,0,1,1);
        response = gameManager.placeCreature(GRASSHOPPER,2,-1);
        assertEquals(OK, response.moveResult());
        gameManager.moveCreature(HORSE,0,0,-1,0);
        response = gameManager.moveCreature(CRAB,0,-1,-3,2);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(HUMMINGBIRD,0,3,1,2);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(GRASSHOPPER,0,-2,-4,2);
        assertEquals(OK, response.moveResult());
        gameManager.moveCreature(HORSE,2,0,3,-2);
        response = gameManager.moveCreature(DOVE,1,-1,-4,2);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(HORSE,3,-2,3,-2);
        assertEquals(MOVE_ERROR, response.moveResult());
        response = gameManager.moveCreature(HORSE,3,-2,1,-2);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(CRAB,3,-2,2,-2);
        assertEquals(OK, response.moveResult());
        response = gameManager.placeCreature(TURTLE,1,-3);
        assertEquals(OK, response.moveResult());
        response = gameManager.moveCreature(CRAB,2,-2,1,-2);
        assertEquals(MOVE_ERROR, response.moveResult());
        gameManager.moveCreature(CRAB,2,-2,2,0);
        response = gameManager.moveCreature(GRASSHOPPER,2,-1,2,1);
        assertEquals(BLUE_WON, response.moveResult());
    }
}