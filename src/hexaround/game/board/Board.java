package hexaround.game.board;

import hexaround.game.board.hexagon.Hexagon;
import hexaround.game.creature.Creature;
import hexaround.game.creature.CreatureMovementInfo;
import hexaround.game.creature.CreatureName;
import hexaround.game.player.PlayerName;

import java.util.LinkedList;

import static java.lang.Math.min;

public class Board {

    private final LinkedList<Hexagon> hexagons;

    /**
     * Constructor, initializes hexagons
     */
    public Board() {
        hexagons = new LinkedList<>();
    }

    /**
     * Places creature on a new Hexagon (x,y)
     * @param creature Creature being placed
     * @param x x position
     * @param y y position
     */
    public void placeCreature(Creature creature, int x, int y) {
        addHexagon(creature,x,y);
    }

    /**
     * Gets all creatures on Hexagon (x,y)
     * @param x x position
     * @param y y position
     * @return LinkedList of all creatures at (x,y)
     */
    public LinkedList<Creature> getCreaturesAt(int x, int y) {
        Hexagon hex = getHexagon(x,y);
        if(hex == null) return new LinkedList<>();
        return hex.getCreatures();
    }

    /**
     * Moves creature from one Hexagon to another,
     *      assumes that creature is at (fromX,fromY) and that (toX,toY) is a valid end position
     * @param creature creature being moved
     * @param fromX initial x
     * @param fromY initial y
     * @param toX final x
     * @param toY final y
     */
    public void moveCreature(Creature creature, int fromX, int fromY, int toX, int toY) {
        Hexagon from = getHexagon(fromX, fromY);
        from.removeCreature(creature);
        LinkedList<Creature> creatures = (LinkedList) from.getCreatures().clone();
        if(creatures.isEmpty()) hexagons.remove(from);
        Hexagon to = getHexagon(toX,toY);
        if(to == null) {
            hexagons.add(new Hexagon(creature,toX,toY));
        } else to.addCreature(creature);
    }

    /**
     * Gets a list of all possible path lengths from (fromX,fromY) to (toX,toY) at or under maxDist
     * @param fromX initial x
     * @param fromY initial y
     * @param toX final x
     * @param toY final y
     * @param visited LinkedList to keep track of visited hexagons
     * @param movementInfo Information of creatures movement
     * @param maxDist the maximum distance that a path can be
     * @return LinkedList of all path lengths
     */
    public LinkedList<Integer> getDistances(int fromX, int fromY, int toX, int toY, LinkedList<Hexagon> visited, CreatureMovementInfo movementInfo, int maxDist) {
        Hexagon startPos = getHexagon(fromX,fromY);
        Hexagon endPos = getHexagon(toX,toY);
        if(endPos == null) endPos = new Hexagon(toX,toY);
        LinkedList<Integer> allMinDistances = new LinkedList<>();
        dfsWithGoal(startPos,endPos, 0, Integer.MAX_VALUE, visited,getEmptyPath(endPos),movementInfo,maxDist,allMinDistances);
        return allMinDistances;
    }

    /**
     * Checks that an x,y is connected to the rest of the board
     * @param x x position
     * @param y y position
     * @return true if the x,y is connected to the rest of the board, false otherwise
     */
    public boolean connectedPlacement(int x, int y) {
        if(hexagons.isEmpty()) return true;
        for (Hexagon hexagon : hexagons) {
            LinkedList<Hexagon> adjList = hexagon.getAdjListForHexagon();
            for (Hexagon adj : adjList) {
                if (adj.equals(x, y)) return true;
            }
        }
        return false;
    }

    /**
     * Checks that moving from (fromX,fromY) to (toX,toY) does not disconnect the board
     * @param fromX initial x
     * @param fromY initial y
     * @param toX final x
     * @param toY final y
     * @return true if the board will disconnect, otherwise false
     */
    public boolean willDisconnect(int fromX, int fromY, int toX, int toY) {
        Hexagon from = getHexagon(fromX,fromY);
        Hexagon to = getHexagon(toX,toY);
        if(to == null)
            to = new Hexagon(toX, toY);
        return willDisconnect(from,to,null);
    }
    private boolean willDisconnect(Hexagon from, Hexagon to, Hexagon origin) {
        LinkedList<Hexagon> hexagons = (LinkedList) this.hexagons.clone();
        if(origin != null) hexagons.remove(origin);
        if(from.getCreatures().size() == 1) hexagons.remove(from);
        if(!hexagons.contains(to)) hexagons.add(to);
        return !isConnected(hexagons);
    }

    /**
     * Determines if hexagon (x,y) is touch other hexagons of PlayerName color and not opposite color
     * @param x x position
     * @param y y position
     * @param name player color
     * @return true if (x,y) is only touch name's player color, otherwise false
     */
    public boolean adjColor(int x, int y, PlayerName name) {
        Hexagon hex = new Hexagon(x,y);
        return adjColor(hex,name);
    }
    private boolean adjColor(Hexagon hex, PlayerName name) {
        if(hexagons.size() <= 1) return true;
        LinkedList<Hexagon> adjList = hex.getAdjListForHexagon();
        for (Hexagon adj : adjList) {
            if(hexagons.contains(adj)) {
                adj = getHexagon(adj);
            }
            LinkedList<Creature> creatures = adj.getCreatures();
            if(creatures.isEmpty()) continue;
            if(creatures.getLast().getPlayer() != name) return false;
        }
        return true;
    }

    /**
     * Determines if (x,y) is surrounded
     * @param x x position
     * @param y y position
     * @return true if surrounded, otherwise false
     */
    public boolean isSurrounded(int x, int y) {
        Hexagon hexagon = getHexagon(x,y);
        return isSurrounded(hexagon);
    }
    /**
     * Determines if hexagon is surrounded
     * @param hexagon Hexagon to check
     * @return true if surrounded, otherwise false
     */
    public boolean isSurrounded(Hexagon hexagon) {
        if(hexagon == null) return false;
        LinkedList<Hexagon> adjList = hexagon.getAdjListForHexagon();
        makeDraggable(adjList);
        return adjList.isEmpty();
    }

    /**
     * Removes toBeRemoved from (x,y)
     * @param x x position
     * @param y y position
     * @param toBeRemoved creature to remove
     */
    public void removeCreature(int x, int y, Creature toBeRemoved) {
        Hexagon hexagon = getHexagon(x,y);
        hexagon.removeCreature(toBeRemoved);
    }

    /**
     * Gets the butterfly Hexagon of playerName
     * @param playerName PlayerName
     * @return Hexagon containing the butterfly
     */
    public Hexagon getButterfly(PlayerName playerName) {
        for(Hexagon curr : hexagons) {
            LinkedList<Creature> creatures = curr.getCreatures();
            for(Creature creature : creatures) {
                if(creature.getName() == CreatureName.BUTTERFLY && creature.getPlayer() == playerName) return curr;
            }
        }
        return null;
    }

    /**
     * Determines if playerName can place a creature
     * @param playerName PlayerName
     * @return true if playerName is able to place a creature somewhere
     */
    public boolean canPlace(PlayerName playerName) {
        if(hexagons.size() <= 1) return true;
        for(Hexagon curr : hexagons) {
            if(curr.getHexagonColor().equals(playerName)) {
                for(Hexagon adjListCurr : curr.getAdjListForHexagon()) {
                    if (!hexagons.contains(adjListCurr) && adjColor(adjListCurr, playerName)) return true;
                }
            }

        }
        return false;
    }

    /**
     * Determines if the player has any moves available
     * @param playerName PlayerName
     * @return true if the player has >= 1 moves available
     */
    public boolean canMove(PlayerName playerName) {
        if(hexagons.size() <= 1) return false;
        for(Hexagon curr : hexagons) {
            Creature creature;
            if((creature = curr.getCreatureOf(playerName)) != null) {
                int distance = DFSUnknownGoal(curr,0,Integer.MAX_VALUE,new LinkedList<>(),getEmptyPath(curr),
                        creature.movementInfo,creature.getMaxDistance());
                if(distance != Integer.MAX_VALUE) return true;
            }

        }
        return false;
    }

    private void addHexagon(int x, int y) {
        hexagons.add(new Hexagon(x,y));
    }
    private void addHexagon(Creature creature, int x, int y) {
        addHexagon(x,y);
        getHexagon(x,y).addCreature(creature);
    }
    private Hexagon getHexagon(int x, int y) {
        for(Hexagon hex:hexagons) {
            if(hex.equals(x,y)) return hex;
        }
        return null;
    }
    private Hexagon getHexagon(Hexagon hex) {
        return getHexagon(hex.getX(),hex.getY());
    }
    private int dfsWithGoal(Hexagon curr, Hexagon to, int currDistance, int minDistance, LinkedList<Hexagon> visited, LinkedList<Hexagon> empty,
                            CreatureMovementInfo movementInfo, int maxDist, LinkedList<Integer> minimums) {
        if(currDistance>maxDist) return currDistance;
        if(curr.equals(to)) {
            if(!minimums.contains(currDistance)) minimums.add(currDistance);
            return currDistance;
        }
        visited.add(curr);
        LinkedList<Hexagon> adjList = curr.getAdjListForHexagon();
        if(movementInfo.draggable()) makeDraggable(adjList);
        boolean disconnect = false;
        for(Hexagon hex : adjList) {
            if(movementInfo.stepping()) disconnect = willDisconnect(curr,hex,visited.get(0));
            boolean isValidMove = ((empty.contains(hex) && !hexagons.contains(hex))
                    || (movementInfo.allowOccupied() && !empty.contains(hex) && hexagons.contains(hex))
                    || (hex.equals(to)))
                    && !visited.contains(hex);
            if(!disconnect && isValidMove) {
                int result = dfsWithGoal(hex,to,currDistance+1,minDistance,visited,empty,movementInfo,maxDist,minimums);
                minDistance = min(minDistance,result);
            }
        }
        visited.removeLast();
        return minDistance;
    }
    private void makeDraggable(LinkedList<Hexagon> adjList) {
        LinkedList<Hexagon> adjListCopy = (LinkedList) adjList.clone();
        for(int i=0;i<adjListCopy.size();i++) {
            int prevIndex = i;
            int nextIndex = i;
            if(i==0) prevIndex = adjListCopy.size()-1;
            else prevIndex--;
            if(i==adjListCopy.size()-1) nextIndex = 0;
            else nextIndex++;
            Hexagon prev = getHexagon(adjListCopy.get(prevIndex));
            Hexagon next = getHexagon(adjListCopy.get(nextIndex));
            if(prev != null && next != null)
                adjList.remove(adjListCopy.get(i));
        }
    }
    private LinkedList<Hexagon> getEmptyPath(Hexagon origin) {
        LinkedList<Hexagon> hexagons = (LinkedList) this.hexagons.clone();
        hexagons.remove(origin);
        LinkedList<Hexagon> empty = new LinkedList<>();
        for(Hexagon curr : hexagons) {
            LinkedList<Hexagon> adjList = curr.getAdjListForHexagon();
            for(Hexagon hex : adjList) {
                if(!hexagons.contains(hex) && !empty.contains(hex)) {
                    empty.add(hex);
                }
            }
        }
        return empty;
    }
    private void DFSConnectedHexagons(int index, LinkedList<Hexagon> hexagons, LinkedList<Hexagon> visited) {
        Hexagon curr = hexagons.get(index);
        visited.add(curr);
        LinkedList<Hexagon> adjList = curr.getAdjListForHexagon();
        for(Hexagon hex : adjList) {
            if(hexagons.contains(hex) && !visited.contains(hex)) {
                DFSConnectedHexagons(hexagons.indexOf(hex), hexagons, visited);
            }
        }
    }
    private boolean isConnected(LinkedList<Hexagon> hexagons) {
        if(hexagons.size() <= 1) return true;
        LinkedList<Hexagon> visited = new LinkedList<>();
        DFSConnectedHexagons(0,hexagons,visited);
        return visited.size() == hexagons.size();
    }

    private int DFSUnknownGoal(Hexagon curr, int currDistance, int minDistance, LinkedList<Hexagon> visited, LinkedList<Hexagon> empty,
                               CreatureMovementInfo movementInfo, int maxDist) {
        if(movementInfo.exactDist() && currDistance == maxDist) return currDistance;
        LinkedList<Creature> creatures = curr.getCreatures();
        if(!movementInfo.exactDist() && creatures.isEmpty()) {
            return currDistance;
        }
        visited.add(curr);
        LinkedList<Hexagon> adjList = curr.getAdjListForHexagon();
        if(movementInfo.draggable()) makeDraggable(adjList);
        boolean disconnect = false;
        for(Hexagon hex : adjList) {
            if(movementInfo.stepping()) disconnect = willDisconnect(curr,hex,visited.get(0));
            boolean isValidMove = ((empty.contains(hex) && !hexagons.contains(hex))
                    || (movementInfo.allowOccupied() && !empty.contains(hex) && hexagons.contains(hex)))
                    && !visited.contains(hex);
            if(!disconnect && isValidMove) {
                int result = DFSUnknownGoal(hex,currDistance+1,minDistance,visited,empty,movementInfo,maxDist);
                minDistance = min(minDistance,result);
            }
        }
        visited.removeLast();
        return minDistance;
    }
}