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
import hexaround.game.move.MoveResponse;
import hexaround.game.move.MoveResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import MasterTests.util.*;

import java.io.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static MasterTests.project.TestHelpers.processRequest;

public class Level2Tests extends BaseTestMaster {
    private static boolean firstTest = true;

    private final int TEST_POINTS = 25;
    private IHexAroundGameManager manager;
    @AfterAll
    static void testBreakdown() {
        firstTest = true;
    }

    @BeforeEach
    public void setup () {
        try {
            manager = HexAroundGameBuilder.buildGameManager(
                "src/MasterTests/MasterConfigurations/Level2.hgc");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Level2Tests() {
        if (firstTest) {
            testReporter.startNewTestGroup("Level 2", TEST_POINTS);
        }
        firstTest = false;
        testResultsDirectory = System.getenv("PWD") + "/";
        testResultsFile = "TestOutput.txt";
    }

    @Test
    void blueButterflyOnBoard() {
        boardSetup1();
        startTest("Blue butterfly on by 4th", 3);
        MoveResponse response = manager.placeCreature(CreatureName.BUTTERFLY, 1, 1);
        assertEquals(MoveResult.OK, response.moveResult());
        markTestPassed();
    }

    @Test
    void blueButterflyMissesOut() {
        boardSetup1();
        startTest("Blue butterfly not on by 4th", 2);
        MoveResponse response = manager.placeCreature(CreatureName.DUCK, 1, 1);
        if (response.moveResult() == MoveResult.OK){
            // catch it next move
            manager.placeCreature(CreatureName.BUTTERFLY, 0, -3);   // Red BUTTERFLY
            response = manager.placeCreature(CreatureName.BUTTERFLY, -1, 1);
        }
        assertEquals(MoveResult.MOVE_ERROR, response.moveResult());
        markTestPassed();
    }

    @ParameterizedTest
    @MethodSource("jumpingSupplier")
    void jumpingTest(String testName, int testValue, MoveResult expected,
                     TestHelpers.Request... requests) {
        boardSetup1();
        startTest("Jumping: " + testName, testValue);
        MoveResponse lastResponse = null;
        for (TestHelpers.Request pr : requests) {
            lastResponse = processRequest(manager, pr);
        }
        assertEquals(expected, lastResponse.moveResult());
        markTestPassed();
    }

    static Stream<Arguments> jumpingSupplier() {
        return Stream.of(
            arguments("Grasshopper (0, 1)->(0, 3)", 3, MoveResult.OK, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.GRASSHOPPER, 0, 1, 0, -3)
            }),
            arguments("Grasshopper (0, 1)->(2, -1)", 2, MoveResult.OK, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.GRASSHOPPER, 0, 1, 2, -1)
            }),
            arguments("Grasshopper to non-empty", 2, MoveResult.MOVE_ERROR, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.GRASSHOPPER, 0, 1, 0, -2)
            }),
            arguments("Grasshopper non-linear", 3, MoveResult.MOVE_ERROR, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.GRASSHOPPER, 0, 1, 1, -2)
            }),
            arguments("Grasshopper too far", 2, MoveResult.MOVE_ERROR, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.BUTTERFLY, -1, 1),
                new TestHelpers.Request(CreatureName.BUTTERFLY, 0, -3),
                new TestHelpers.Request(CreatureName.GRASSHOPPER, 0, 1, 1, -4)
            })
        );
    }

    @Test
    void dragabilityTest() {
        boardSetup1();
        startTest("Dragability", 2);
        manager.placeCreature(CreatureName.BUTTERFLY, -1, 1);
        manager.placeCreature(CreatureName.BUTTERFLY, 0, -3);
        MoveResponse response = manager.moveCreature(CreatureName.CRAB,
            0, 0, 1, -1);
        assertEquals(MoveResult.MOVE_ERROR, response.moveResult());
    }

    @ParameterizedTest
    @MethodSource("invalidMoveSupplier")
    void invalidMoveTest(String testName, int testValue,
                         TestHelpers.Request... requests) {
        boardSetup2();
        startTest("Invalid: " + testName, testValue);
        MoveResponse lastResponse = null;
        for (TestHelpers.Request pr : requests) {
            lastResponse = processRequest(manager, pr);
        }
        // All are expected to fail
        assertEquals(MoveResult.MOVE_ERROR, lastResponse.moveResult());
        markTestPassed();
    }

    static Stream<Arguments> invalidMoveSupplier() {
        return Stream.of(
            arguments("Spider too far", 1, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.SPIDER, 1, 0, 1, 2)
            }),
            arguments("Butterfly intrudes", 3, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.BUTTERFLY, 1, 1, 0, 1)
            })
        );
    }

    @Test
    void kamikazeTest() {
        boardSetup2();
        startTest("Kamikaze", 2);
        manager.placeCreature(CreatureName.HUMMINGBIRD, -1, 1);
        manager.placeCreature(CreatureName.CRAB, 1, -2);
        MoveResponse response = manager.moveCreature(CreatureName.HUMMINGBIRD, -1, 1, 0, -2);
        assertEquals(MoveResult.OK, response.moveResult());
        response = manager.moveCreature(CreatureName.CRAB, 0, -2, -1, -2);
        assertEquals(MoveResult.MOVE_ERROR, response.moveResult());
        markTestPassed();
    }

    /* ***************************** Setups *****************************/
    private void boardSetup1() {
        manager.placeCreature(CreatureName.CRAB, 0, 0);             // B
        manager.placeCreature(CreatureName.DUCK, 0, -1);            // R
        manager.placeCreature(CreatureName.GRASSHOPPER, 0, 1);      // B
        manager.placeCreature(CreatureName.HUMMINGBIRD, -1, -1);    // R
        manager.placeCreature(CreatureName.SPIDER, 1, 0);           // B
        manager.placeCreature(CreatureName.CRAB, 0, -2);            // R

    }

    private void boardSetup2() {
        boardSetup1();
        manager.placeCreature(CreatureName.BUTTERFLY, -1, 1);       // B
        manager.placeCreature(CreatureName.BUTTERFLY, 0, -3);       // R
    }
}
