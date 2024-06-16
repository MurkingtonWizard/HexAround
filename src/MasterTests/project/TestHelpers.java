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

import static MasterTests.project.TestHelpers.REQUEST_TYPE.MOVE;
import static MasterTests.project.TestHelpers.REQUEST_TYPE.PLACE;
import static MasterTests.project.TestHelpers.REQUEST_TYPE.*;

public class TestHelpers {
    public enum REQUEST_TYPE {PLACE, MOVE};

    /**
     * Request record
     * @param type
     * @param creature
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public static record Request(REQUEST_TYPE type, CreatureName creature,
                                 int x1, int y1, int x2, int y2) {
        public Request(CreatureName creature, int x, int y) {
            this(PLACE, creature, x, y, 0, 0);
        }

        public Request(CreatureName creature, int x1, int y1, int x2, int y2) {
            this(MOVE, creature, x1, y1, x2, y2);
        }
    }

    public static MoveResponse processRequest(IHexAroundGameManager manager,
                                              Request rq) {
        MoveResponse gs = null;
        if (rq.type == MOVE) {
            gs = manager.moveCreature(rq.creature(), rq.x1(), rq.y1(), rq.x2(), rq.y2());
        } else {
            gs = manager.placeCreature(rq.creature(), rq.x1(), rq.y1());
        }
        return gs;
    }
}
