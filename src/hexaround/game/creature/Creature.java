package hexaround.game.creature;

import hexaround.config.CreatureDefinition;
import hexaround.game.board.Board;
import hexaround.game.player.PlayerName;

import java.util.LinkedList;

public class Creature {
    private final PlayerName player;
    private final CreatureDefinition definition;
    public final CreatureMovementInfo movementInfo;

    /**
     * Creature Constructor, determines if the creature...
     *      can move over occupied spaces
     *      is required to be draggable
     *      is required to keep the colony connected each step of the move
     *      is required to go exactly their max distance
     * @param player PlayerName of the player that owns this creature
     * @param definition The CreatureDefinition of this creature
     */
    public Creature(PlayerName player, CreatureDefinition definition) {
        this.player = player;
        this.definition = definition;
        boolean allowOccupied = hasProperty(CreatureProperty.INTRUDING) ||
                !hasProperty(CreatureProperty.WALKING) &&
                !hasProperty(CreatureProperty.RUNNING);
        boolean draggable = !hasProperty(CreatureProperty.INTRUDING) &&
                (hasProperty(CreatureProperty.WALKING) || hasProperty(CreatureProperty.RUNNING));
        boolean stepping = !hasProperty(CreatureProperty.FLYING);
        boolean exactDist = hasProperty(CreatureProperty.RUNNING);
        boolean straightLine = hasProperty(CreatureProperty.JUMPING);
        movementInfo = new CreatureMovementInfo(allowOccupied,draggable,stepping,exactDist,straightLine);
    }

    /**
     * Getter for Creature's CreatureName
     * @return CreatureName
     */
    public CreatureName getName() {
        return definition.name();
    }

    /**
     * Getter for PlayerName that owns the Creature
     * @return PlayerName
     */
    public PlayerName getPlayer() { return player; }

    /**
     * toString for Creature
     * @return 'PlayerName CreatureName'
     */
    @Override
    public String toString() {
        return player.name() + " " + getName().toString();
    }

    /**
     * Determines if the creature has a given property
     * @param property CreatureProperty
     * @return true if creature has the given property, otherwise false
     */
    public boolean hasProperty(CreatureProperty property) {
        return definition.properties().contains(property);
    }

    /**
     * Determines if the creature can land on (toX,toY) given creature properties
     * @param toX final x
     * @param toY final y
     * @param board the board being played on
     * @return true if the creature's end position can be (toX,toY), otherwise false
     */
    public boolean validEndHexagon(int toX, int toY, Board board) {
        LinkedList<Creature> creaturesAt = board.getCreaturesAt(toX,toY);
        if(creaturesAt.isEmpty()) return true;
        if(creaturesAt.size() == 2 && !hasProperty(CreatureProperty.SWAPPING)) return false;
        if(hasProperty(CreatureProperty.SWAPPING) && creaturesAt.getFirst().getName()==CreatureName.BUTTERFLY) {
            return false;
        }
        return (hasProperty(CreatureProperty.INTRUDING) ||
                hasProperty(CreatureProperty.KAMIKAZE) ||
                hasProperty(CreatureProperty.SWAPPING));
    }

    /**
     * Getter for creature's max distance
     * @return max distance of creature
     */
    public int getMaxDistance() {
        return definition.maxDistance();
    }
}
