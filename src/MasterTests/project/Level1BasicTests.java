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

import static MasterTests.project.TestHelpers.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class Level1BasicTests extends BaseTestMaster {
    private static boolean firstTest = true;

    private final int TEST_POINTS = 50;
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
        manager.placeCreature(CreatureName.BUTTERFLY, 0, 0);    // B
        manager.placeCreature(CreatureName.BUTTERFLY, 0, 1);    // R
        manager.placeCreature(CreatureName.DOVE, 0, -1);        // B
        manager.placeCreature(CreatureName.CRAB, -1, 2);        // R
        manager.placeCreature(CreatureName.CRAB, -1, -1);       // B
        manager.placeCreature(CreatureName.DOVE, 0, 2);         // R
        manager.placeCreature(CreatureName.DOVE, 0, -2);        // B
        manager.placeCreature(CreatureName.DOVE, 1, 1);         // R
        manager.placeCreature(CreatureName.CRAB, 1, -2);        // B
        manager.placeCreature(CreatureName.SPIDER, 2, 0);       // R
        manager.placeCreature(CreatureName.HUMMINGBIRD, -2, -1);   // B
        manager.placeCreature(CreatureName.SPIDER, 0, 3);       // R
    }

    public Level1BasicTests() {
        if (firstTest) {
            testReporter.startNewTestGroup("Level 1 basic", TEST_POINTS);
        }
        firstTest = false;
        testResultsDirectory = System.getenv("PWD") + "/";
        testResultsFile = "TestOutput.txt";
    }

    /* **************************** Placement tests **************************** */
    /**
     * @param testName
     * @param testValue
     * @param expected
     * @param requests
     */
    @ParameterizedTest
    @MethodSource("placementSupplier")
    void placementTest(String testName, int testValue, MoveResult expected,
                          Request... requests) {
        startTest("Placement: " + testName, testValue);
        MoveResponse lastResponse = null;
        for (Request pr : requests) {
            lastResponse = processRequest(manager, pr);
        }
        assertEquals(expected, lastResponse.moveResult());
        markTestPassed();
    }

    static Stream<Arguments> placementSupplier() {
        return Stream.of(
            arguments("BUTTERFLY (0, 0)", 3, MoveResult.OK, new Request[] {
                new Request(CreatureName.BUTTERFLY, 0, 0)
            }),
            arguments("DOVE (-1, 0)", 3, MoveResult.OK, new Request[] {
                new Request(CreatureName.BUTTERFLY, 0, 0),
                new Request(CreatureName.DOVE, -1, 0)
            }),
            arguments("2nd Blue piece CRAB(1, 0)", 3, MoveResult.OK, new Request[] {
                new Request(CreatureName.BUTTERFLY, 0, 0),
                new Request(CreatureName.DOVE, -1, 0),
                new Request(CreatureName.CRAB, 1, 0)
            }),
            arguments("Try to place piece next to other color", 3, MoveResult.MOVE_ERROR, new Request[] {
                new Request(CreatureName.BUTTERFLY, 0, 0),
                new Request(CreatureName.DOVE, -1, 0),
                new Request(CreatureName.CRAB, -2, 0)
            }),
            arguments("Try to place piece next to both colors", 3, MoveResult.MOVE_ERROR, new Request[] {
                new Request(CreatureName.BUTTERFLY, 0, 0),
                new Request(CreatureName.DOVE, -1, 0),
                new Request(CreatureName.CRAB, 0, -1)
            }),
            arguments("Try to place two pieces on (0, 0)", 3, MoveResult.MOVE_ERROR, new Request[] {
                new Request(CreatureName.BUTTERFLY, 0, 0),
                new Request(CreatureName.DOVE, 0, 0)
            }),
            arguments("Try to place two same player pieces on a hex",
                3, MoveResult.MOVE_ERROR, new Request[] {
                    new Request(CreatureName.BUTTERFLY, 0, 0),
                    new Request(CreatureName.BUTTERFLY, 0, 1),
                    new Request(CreatureName.DOVE, 0, -1),
                    new Request(CreatureName.DOVE, 0, 2),
                    new Request(CreatureName.CRAB, 0, -1)
            }),
            arguments("Try to place second piece away from first", 3, MoveResult.MOVE_ERROR, new Request[] {
                new Request(CreatureName.BUTTERFLY, 0, 0),
                new Request(CreatureName.DOVE, 1, 2)
            }),
            arguments("Try to place piece not near any other (0, 0)", 3, MoveResult.MOVE_ERROR, new Request[] {
                new Request(CreatureName.BUTTERFLY, 0, 0),
                new Request(CreatureName.DOVE, 0, 1),
                new Request(CreatureName.CRAB, 3, 3)
            }),
            arguments("Place in legal surrounded hex", 3, MoveResult.OK, new Request[] {
                new Request(CreatureName.BUTTERFLY, 0, 0), //B
                new Request(CreatureName.BUTTERFLY, 0, 1),
                new Request(CreatureName.CRAB, -1, 0), //B
                new Request(CreatureName.CRAB, -1, 2),
                new Request(CreatureName.DOVE, 1, -1), //B
                new Request(CreatureName.DOVE, 0, 1),
                new Request(CreatureName.HUMMINGBIRD, -1, -1), //B
                new Request(CreatureName.DOVE, 0, 3),
                new Request(CreatureName.DOVE, 1, -2), //B
                new Request(CreatureName.SPIDER, 0, 4),
                new Request(CreatureName.HUMMINGBIRD, 0, -2), //B
                new Request(CreatureName.CRAB, 0, 5),
                new Request(CreatureName.CRAB, 0, -1)

            })
        );
    }


    /* **************************** Movement tests **************************** */
    // Include win conditions
    @ParameterizedTest
    @MethodSource("movementSupplier")
    void movementTest(String testName, int testValue, MoveResult expected,
                       Request... requests) {
        setupBoard();
        startTest("Movement: " + testName, testValue);
        MoveResponse lastResponse = null;
        for (Request pr : requests) {
            lastResponse = processRequest(manager, pr);
        }
        assertEquals(expected, lastResponse.moveResult());
        markTestPassed();
    }

    static Stream<Arguments> movementSupplier() {
        return Stream.of(
            arguments("CRAB (1, -2)->(1, 0)", 3, MoveResult.OK, new Request[] {
                new Request(CreatureName.CRAB, 1, -2, 1, 0)
            }),
            arguments("HUMMINGBIRD (-2, -1)->(1, -1)", 3, MoveResult.OK, new Request[] {
                new Request(CreatureName.HUMMINGBIRD, -2, -1, 1, -1)
            }),
            arguments("CRAB (1, -2)->(3, -1)", 2, MoveResult.OK, new Request[] {
                new Request(CreatureName.CRAB, 1, -2, 3, -1)
            }),
            arguments("DOVE (0, -2)->(2, 1)", 3, MoveResult.OK, new Request[] {
                new Request(CreatureName.DOVE, 0, -2, 2, 1)
            }),
            arguments("Blue wins on Red Move", 3, MoveResult.BLUE_WON, new Request[] {
                new Request(CreatureName.HUMMINGBIRD, -2, -1, -1, 1),
                new Request(CreatureName.SPIDER, 2, 0, 1, 0)
            }),
            arguments("Red wins on Red Move", 3, MoveResult.RED_WON, new Request[] {
                new Request(CreatureName.HUMMINGBIRD, -2, -1, -1, 0),
                new Request(CreatureName.SPIDER, 2, 0, 1, 0),
                new Request(CreatureName.CRAB, 1, -2, 1, -1),
                new Request(CreatureName.CRAB, -1, 2, -1, 1)
            })
        );
    }
}
