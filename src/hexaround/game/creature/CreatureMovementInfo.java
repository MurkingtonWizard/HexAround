package hexaround.game.creature;

public record CreatureMovementInfo(boolean allowOccupied, boolean draggable, boolean stepping, boolean exactDist,
                                   boolean straightLine) {
}
