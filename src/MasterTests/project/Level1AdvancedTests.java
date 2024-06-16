/*
 * Copyright (c) 2023. Gary F. Pollice
 *
 * This files was developed for personal or educational purposes. All rights reserved.
 *
 *  You may use this software for any purpose except as follows:
 *  1) You may not submit this file without modification for any educational assignment
 *      unless it was provided to you as part of starting code that does not require modification.
 *  2) You may not remove this copyright, even if you have modified this file.
 */

package MasterTests.project;

import hexaround.game.*;
import hexaround.game.creature.*;
import hexaround.game.move.*;
import org.junit.jupiter.api.*;
import MasterTests.util.*;


import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Level1AdvancedTests extends BaseTestMaster {
    private static boolean firstTest = true;

    private final int TEST_POINTS = 10;
    private IHexAroundGameManager manager;
    @AfterAll
    static void testBreakdown() {
        firstTest = true;
    }

    @BeforeEach
    public void setup () {
        try {
            manager = HexAroundGameBuilder.buildGameManager(
                "src/MasterTests/MasterConfigurations/Level1.hgc");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupBoard() {
        manager.placeCreature(CreatureName.BUTTERFLY, 0, 0);
        manager.placeCreature(CreatureName.BUTTERFLY, 0, 1);
        manager.placeCreature(CreatureName.DOVE, 0, -1);
        manager.placeCreature(CreatureName.CRAB, -1, 2);
        manager.placeCreature(CreatureName.CRAB, -1, -1);
        manager.placeCreature(CreatureName.DOVE, 0, 2);
        manager.placeCreature(CreatureName.DOVE, 0, -2);
        manager.placeCreature(CreatureName.DOVE, 1, 1);
        manager.placeCreature(CreatureName.CRAB, 1, -2);
        manager.placeCreature(CreatureName.SPIDER, 2, 0);
        manager.placeCreature(CreatureName.HUMMINGBIRD, -2, -1);
        manager.placeCreature(CreatureName.SPIDER, 0, 3);
    }

    public Level1AdvancedTests() {
        if (firstTest) {
            testReporter.startNewTestGroup("Level 1 advanced", TEST_POINTS);
        }
        firstTest = false;
        testResultsDirectory = System.getenv("PWD") + "/";
        testResultsFile = "TestOutput.txt";
    }

    @Test
    void placePieceNotInGame() {
        startTest("Place non-game creature", 3);
        MoveResponse response = manager.placeCreature(CreatureName.GRASSHOPPER, 0, 0);
        System.out.println(response.message());
        assertEquals(MoveResult.MOVE_ERROR, response.moveResult());
        markTestPassed();
    }

    @Test
    void samePlayerMovesAfterError() {
        setupBoard();
        startTest("Same player moves after making an error", 3);
        MoveResponse response = manager.placeCreature(CreatureName.DUCK, 0, 5);
        System.out.println(response.message());
        assertEquals(MoveResult.MOVE_ERROR, response.moveResult());
        response = manager.placeCreature(CreatureName.HUMMINGBIRD, 0, -3);
        assertEquals(MoveResult.OK, response.moveResult());
        markTestPassed();
    }

    @Test
    void placePieceWhenNoMoreInInventory() {
        setupBoard();
        startTest("Try to place player when none are left", 3);
        MoveResponse response = manager.placeCreature(CreatureName.HUMMINGBIRD, 0, -3);
        response = manager.placeCreature(CreatureName.SPIDER, -2, 1);
        assertEquals(MoveResult.MOVE_ERROR, response.moveResult());
        markTestPassed();
    }

    @Test
    void wrongPlayer() {
        setupBoard();
        startTest("Wrong player's piece", 1);
        MoveResponse response = manager.moveCreature(CreatureName.CRAB, -1, 2, -1, 3);
        assertEquals(MoveResult.MOVE_ERROR, response.moveResult());
        markTestPassed();
    }
}
