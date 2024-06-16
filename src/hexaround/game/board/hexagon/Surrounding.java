package hexaround.game.board.hexagon;

public enum Surrounding {
    UP(0,1),
    UP_RIGHT(1,0),
    DOWN_RIGHT(1,-1),
    DOWN(0,-1),
    DOWN_LEFT(-1,0),
    UP_LEFT(-1,1);

    public final int x;
    public final int y;

    Surrounding(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
