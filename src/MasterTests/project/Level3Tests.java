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
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import MasterTests.util.*;

import java.io.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static MasterTests.project.TestHelpers.processRequest;

public class Level3Tests extends BaseTestMaster {
    private static boolean firstTest = true;

    private final int TEST_POINTS = 20;
    private IHexAroundGameManager manager;
    @AfterAll
    static void testBreakdown() {
        firstTest = true;
    }

    @BeforeEach
    public void setup () {
        try {
            manager = HexAroundGameBuilder.buildGameManager(
                "src/MasterTests/MasterConfigurations/Level3.hgc");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Level3Tests() {
        if (firstTest) {
            testReporter.startNewTestGroup("Level 3", TEST_POINTS);
        }
        firstTest = false;
        testResultsDirectory = System.getenv("PWD") + "/";
        testResultsFile = "TestOutput.txt";
    }

    @ParameterizedTest
    @MethodSource("generalTestSupplier")
    void generalTest(String testName, int testValue, MoveResult expected,
    TestHelpers.Request... requests) {
        startTest(testName, testValue);
        boardSetup1();
        MoveResponse lastResponse = null;
        for (TestHelpers.Request pr : requests) {
            lastResponse = processRequest(manager, pr);
        }
        assertEquals(expected, lastResponse.moveResult());
        markTestPassed();
    }

    static Stream<Arguments> generalTestSupplier() {
        return Stream.of(
            arguments("Non-continuity T(-1,3)->(-3,1)", 3, MoveResult.MOVE_ERROR, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.TURTLE, -1, 3, -3, 1)
            }),
            arguments("Intruding to open hex T(1,0)->(-3,1)", 2, MoveResult.OK, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.TURTLE, 1, 0, -3, 1)
            }),
            arguments("Valid run H(1,1)->(1,-3)", 2, MoveResult.OK, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.HORSE, 1, 1, 1, -3)
            }),
            arguments("Invalid run H(1,1)->(1,-1)", 2, MoveResult.MOVE_ERROR, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.HORSE, 1, 1, -1, 1)
            }),
            arguments("Create stack T(1,0)->(0, 0)", 2, MoveResult.OK, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.TURTLE, 1, 0, 0, 0)
            }),
            arguments("Too many on stack", 2, MoveResult.MOVE_ERROR, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.TURTLE, 1, 0, 0, 0),
                new TestHelpers.Request(CreatureName.TURTLE,-1, -1, 0, 0)
            }),
            arguments("Drawn game", 3, MoveResult.DRAW, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.TURTLE, 1, 0, 1, -1),
                new TestHelpers.Request(CreatureName.GRASSHOPPER, -3, 0, 1, 0),
                new TestHelpers.Request(CreatureName.DUCK, -1, 1),
                new TestHelpers.Request(CreatureName.DUCK, -1, -2, 1, -2),
                new TestHelpers.Request(CreatureName.TURTLE, -1, 3, -1, 1)
            }),
            arguments("Just swap", 2, MoveResult.OK, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.DUCK, -1, 1),
                new TestHelpers.Request(CreatureName.DUCK, 1, -2, -1, 1)
            }),
            arguments("Swap and move", 2, MoveResult.OK, new TestHelpers.Request[] {
                new TestHelpers.Request(CreatureName.DUCK, -1, 1),
                new TestHelpers.Request(CreatureName.DUCK, 1, -2, -1, 1),
                new TestHelpers.Request(CreatureName.DUCK, 1, -2, 0, -3)
            })
        );
    }

    /* ***************************** Setups *****************************/
    private void boardSetup1() {
        manager.placeCreature(CreatureName.BUTTERFLY, 0, 0);            // B
        manager.placeCreature(CreatureName.BUTTERFLY, 0, -1);           // R
        manager.placeCreature(CreatureName.GRASSHOPPER, 0, 1);          // B
        manager.placeCreature(CreatureName.TURTLE, -1, -1);             // R
        manager.placeCreature(CreatureName.HORSE, 1, 1);                // B
        manager.placeCreature(CreatureName.DUCK, -1, -2);               // R
        manager.placeCreature(CreatureName.RABBIT, 0, 2);               // B
        manager.placeCreature(CreatureName.RABBIT,-2, -1);              // R
        manager.placeCreature(CreatureName.TURTLE, 1, 0);               // B
        manager.placeCreature(CreatureName.HORSE, 0, -2);               // R
        manager.placeCreature(CreatureName.TURTLE, -1, 3);              // B
        manager.placeCreature(CreatureName.GRASSHOPPER, -3, 0);          // R
    }
}
