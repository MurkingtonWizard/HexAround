package hexaround.game.board.hexagon;

public record Coordinate(int x, int y) {
    /**
     * Override toString for Coordinate
     * @return '(x,y)'
     */
    @Override
    public String toString() {
        return "("+x+","+y+")";
    }
}
