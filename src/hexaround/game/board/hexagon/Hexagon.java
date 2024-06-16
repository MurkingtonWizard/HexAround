package hexaround.game.board.hexagon;

import hexaround.game.creature.Creature;
import hexaround.game.player.PlayerName;

import java.util.LinkedList;

public class Hexagon {
    private final Coordinate coordinate;
    private final LinkedList<Creature> creatures;

    /**
     * Constructor for x,y
     * @param x x position
     * @param y y position
     */
    public Hexagon(int x, int y) {
        coordinate = new Coordinate(x,y);
        creatures = new LinkedList<>();
    }

    /**
     * Constructor for x,y and adding the first creature
     * @param creature creature to add
     * @param x x position
     * @param y y position
     */
    public Hexagon(Creature creature, int x, int y) {
        this(x,y);
        addCreature(creature);
    }

    /**
     * Gets a list of empty hexagons that surround this hexagon
     * @return LinkedList of surrounding hexagons
     */
    public LinkedList<Hexagon> getAdjListForHexagon() {
        LinkedList<Hexagon> adjList = new LinkedList<>();
        for(Surrounding surround : Surrounding.values()) {
            adjList.add(new Hexagon(surround.x+coordinate.x(), surround.y+coordinate.y()));
        }
        return adjList;
    }

    /**
     * Override equals for Hexagon
     * @param obj potential hexagon
     * @return true if hexagons have same x and y
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof Hexagon other)) return false;
        return equals(other.getX(), other.getY());
    }

    /**
     * Override toString for Hexagon
     * @return '(x,y)'
     */
    @Override
    public String toString() {
        return coordinate.toString();
    }

    /**
     * Checks if the given x and y are equal to the Hexagons x and y coordinate
     * @param x x position
     * @param y y position
     * @return true if the hexagon has the same coordinates, otherwise false
     */
    public boolean equals(int x, int y) {
        return coordinate.x() == x && coordinate.y() == y;
    }

    /**
     * Adds creature to the creatures LinkedList, last item in list is at the top of the creature stack
     * @param creature Creature to add
     */
    public void addCreature(Creature creature) {
        creatures.add(creature);
    }

    /**
     * Getter for creatures LinkedList
     * @return LinkedList of creatures on this Hexagon
     */
    public LinkedList<Creature> getCreatures() {
        return creatures;
    }

    /**
     * Getter for x coordinate
     * @return x coordinate
     */
    public int getX() {
        return coordinate.x();
    }
    /**
     * Getter for y coordinate
     * @return y coordinate
     */
    public int getY() {
        return coordinate.y();
    }

    /**
     * Removes the creature on the top from the hexagon
     */
    public void removeCreature(Creature creature) {
        creatures.remove(creature);
    }

    /**
     * Gets the PlayerName of the top creature
     * @return PlayerName or null if no creatures are present
     */
    public PlayerName getHexagonColor() {
        if(creatures.isEmpty()) return null;
        return creatures.getLast().getPlayer();
    }

    /**
     * Gets creature of PlayerName
     * @param playerName PlayerName
     * @return creature of PlayerName if present, otherwise null
     */
    public Creature getCreatureOf(PlayerName playerName) {
        for(Creature creature : creatures) {
            if(creature.getPlayer() == playerName) return creature;
        }
        return null;
    }
}
