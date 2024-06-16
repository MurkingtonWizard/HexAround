# TDD Tests and refactorings

Add to the following table

| ID  | Done | Description                                                                                                                                                      |
|:---:|:----:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| T1  |  X   | getCreatureAt returns null when no creature is place                                                                                                             |
| T2  |  X   | placeCreature returns MoveResult OK when creature is placed                                                                                                      |
| R1  |  X   | Make Hexagon class for coordinate system                                                                                                                         |
| R2  |  X   | Make Board class and move responsibilities of manager                                                                                                            |
| T3  |  X   | getCreaturesAt gets all the creature on the hexagon                                                                                                              |
| T4  |  X   | getCreaturesAt, shows that creature moved                                                                                                                        |
| R3  |  X   | made helper function to get CreatureDef of CreatureName                                                                                                          |
| R4  |  X   | make data structures that contain CreatureDefs HashMap<CreatureName,CreatureDef> and for PlayerConfigs                                                           |
| R5  |  X   | Move distance calculation to Board                                                                                                                               |
| R6  |  X   | Add adjacency matrix to Board to keep track of Hexagon adjacency                                                                                                 |
| T5  |  X   | moveCreature returns Move_error when creature is not at original x,y                                                                                             |
| T6  |  X   | moveCreature checks if move will disconnect board                                                                                                                |
| T7  |  X   | moveCreature valid move occurs, move MoveResponse has OK, "Legal move"                                                                                           |
| T8  |  X   | moveCreature Move creature on top of another                                                                                                                     |
| R7  |  X   | adding checks for turns, edited tests to make sure all moves are alternating turns                                                                               |
| R8  |  X   | placeCreature (GM) now has a placement check helper                                                                                                              |
| T9  |  X   | placeCreature does not place a creature that will disconnect the colony                                                                                          |
| R9  |  X   | placeCreature checks that the player can place that creature                                                                                                     |
| T10 |  X   | uses Submission 2 tests to check valid and invalid moves                                                                                                         |
| T11 |  X   | Walking creature is not moving over creatures                                                                                                                    |
| R10 |  X   | Hexagons now generate their own adjacency lists                                                                                                                  |
| R11 |  X   | Creature is now it's own class                                                                                                                                   |
| R12 |  X   | Player is now it's own class                                                                                                                                     |
| T12 |  X   | Checks that a walking creature cannot move through a narrow space                                                                                                |
| T13 |  X   | Checks that a flying creature can move over a narrow space                                                                                                       |
| T14 |  X   | Checks for winner at move end, red wins                                                                                                                          |
| T15 |  X   | Checks for winner at move end, blue wins                                                                                                                         |
| T16 |  X   | Checks for winner at move end, draw                                                                                                                              |
| T17 |  X   | Checks that creature can only be placed next to one of it's own color                                                                                            |
| T18 |  X   | placeCreature gives error when butterfly is not placed in under 4 turns                                                                                          |
| R13 |  X   | Creature is responsible for determining if a path exists                                                                                                         |
| R14 |  X   | CreatureMovementInfo records if a creature can use occupied spaces, is draggable, steps in movement, if it travels an exact distance, travels in a straight line |
| T19 |  X   | moveCreature checks if a flying creature is surrounded and gives error if is                                                                                     |
| T20 |  X   | moveCreature jumping creature can only jump in straight lines                                                                                                    |
| T21 |  X   | moveCreature jumping creature can only jump can move when surrounded                                                                                             |
| T22 |  X   | moveCreature kamikaze creature removes creature and allows player to replace it                                                                                  |
| T23 |  X   | moveCreature cannot move 3 creatures onto a stack                                                                                                                |
| T24 |  X   | moveCreature kamikaze creature removes butterfly on turn 5, player must immediately replace it                                                                   |
| R15 |  X   | GameManager is responsible for determining if a path exists and gets CreatureMovementInfo from Creature                                                          |
| T25 |  X   | swapping creature moves normally                                                                                                                                 |
| T26 |  X   | moveCreature swapping creature swaps with another, both creatures are at new locations                                                                           |
| T27 |  X   | moveCreature running creature moves an exact distance                                                                                                            |
| T28 |  X   | Butterfly cannot move when surrounded on 5 sides                                                                                                                 |
| T29 |  X   | red cannot place creature, game over blue wins                                                                                                                   |
| T30 |  X   | blue cannot place creature, game over red wins                                                                                                                   |
| T31 |  X   | No turns can be taken after BLUE_WON or RED_WON                                                                                                                  |
| T32 |  X   | Creature can move but none can be placed                                                                                                                         |
| T33 |  X   | Red cannot place creature, but can move, so game continues                                                                                                       |
| T34 |  X   | Blue cannot place a butterfly after turn 4, red wins                                                                                                             |
| T35 |  X   | Red cannot place a butterfly after turn 4, blue wins                                                                                                             |
| T35 |  X   | Red cannot place a butterfly after turn 4, blue wins                                                                                                             |
| T36 |  X   | Tests Creature toString                                                                                                                                          |
| T37 |  X   | validEndHexagon is valid when a swapping creature targets another creature                                                                                       |
| T38 |  X   | validEndHexagon returns false when a walking creature wants to end on another creature                                                                           |
| T39 |  X   | Tests CreatureDefinition toString                                                                                                                                |
| T40 |  X   | connectedPlacement returns appropriate results                                                                                                                   |
| T41 |  X   | canMove returns false when no creatures are on the board                                                                                                         |
| T42 |  X   | canMove returns true for the exactDistance of a running creature                                                                                                 |
| T43 |  X   | Testing hexagon equals with null and a non-hexagon                                                                                                               |
| T44 |  X   | Tests Hexagon toString                                                                                                                                           |
| T45 |  X   | checks for a null response from getHexagonColor when no creatures are present                                                                                    |
| T46 |  X   | Tests that Player.canPlace returns false when attempting to place a Creature that isn't in the game                                                              |
| T47 |  X   | Tests a full game                                                                                                                                                |

## Citations

Put citations to any code snippets you used in your implementation and
where you found it.

## Design Patterns Used

If you use design patterns, identify them, where they are applied, and 
why you chose them.

- Acyclic Dependencies Principle: No packages within hexaround.game are cyclical