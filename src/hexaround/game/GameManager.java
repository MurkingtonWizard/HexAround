package hexaround.game;

import hexaround.config.CreatureDefinition;
import hexaround.config.PlayerConfiguration;
import hexaround.game.creature.*;
import hexaround.game.board.Board;
import hexaround.game.player.*;
import hexaround.game.move.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import static hexaround.game.creature.CreatureProperty.*;
public class GameManager implements IHexAroundGameManager {
    private final Board board = new Board();
    private HashMap<CreatureName, CreatureDefinition> creatureDefs = null;
    private HashMap<PlayerName, Player> players = null;
    private Player turn;
    private boolean gameOver = false;

    /**
     * This is the default constructor, and the only constructor
     * that you can use. The builder creates an instance using
     * this connector. You should add getters and setters as
     * necessary for any instance variables that you create and
     * will be filled in by the builder.
     */
    public GameManager() {}

    /**
     * Stores the creature definitions
     * @param creatureDefs Collection of Creature Definitions
     */
    public void setCreatureDefs(Collection<CreatureDefinition> creatureDefs) {
        this.creatureDefs = new HashMap<>();
        for (CreatureDefinition curr:creatureDefs) {
            this.creatureDefs.put(curr.name(),curr);
        }
    }

    /**
     * Stores the player configurations
     * @param playerConfigs Collection of Player Configurations
     */
    public void setPlayerConfigs(Collection<PlayerConfiguration> playerConfigs) {
        this.players = new HashMap<>();
        for (PlayerConfiguration curr:playerConfigs) {
            this.players.put(curr.Player(),new Player(curr));
        }
        turn = players.get(PlayerName.BLUE);
    }

    /**
     * Gets all creatures at (x,y)
     * @param x x position
     * @param y y position
     * @return LinkedList of all Creatures
     */
    public LinkedList<Creature> getCreaturesAt(int x, int y) {
        return board.getCreaturesAt(x,y);
    }

    /**
     * Places creature on the board on Hexagon (x,y)
     * @param creature CreatureName to be placed
     * @param x x position
     * @param y y position
     * @return a response, or null
     */
    @Override
    public MoveResponse placeCreature(CreatureName creature, int x, int y) {
        if(gameOver)
            return new MoveResponse(MoveResult.MOVE_ERROR,"Game is over, cannot place creature");
        if(!turn.canPlace(creature))
            return new MoveResponse(MoveResult.MOVE_ERROR, turn.getName()+" cannot place a "+creature);
        if(isOccupied(x,y))
            return new MoveResponse(MoveResult.MOVE_ERROR, "("+x+","+y+") is occupied");
        if(!validPlacement(x,y))
            return new MoveResponse(MoveResult.MOVE_ERROR, "Invalid placement");
        Creature createdCreature = new Creature(turn.getName(), creatureDefs.get(creature));
        board.placeCreature(createdCreature,x,y);
        turn.recordPlacement(creature);
        turn.takeTurn();
        return endTurn();
    }

    /**
     * Moves creature from (fromX,fromY) to (toX,toY)
     * @param creature CreatureName to be moved
     * @param fromX initial x
     * @param fromY initial y
     * @param toX final x
     * @param toY final y
     * @return MoveResponse with a MoveResult
     */
    @Override
    public MoveResponse moveCreature(CreatureName creature, int fromX, int fromY, int toX, int toY) {
        if(gameOver)
            return new MoveResponse(MoveResult.MOVE_ERROR,"Game is over, cannot move creature");
        if(turn.needsButterfly())
            return new MoveResponse(MoveResult.MOVE_ERROR, turn.getName()+" has no butterfly");
        LinkedList<Creature> creaturesAt = board.getCreaturesAt(fromX,fromY);
        if(creaturesAt.isEmpty())
            return new MoveResponse(MoveResult.MOVE_ERROR,"No creature at "+fromX+","+fromY);
        Creature creatureAt = getCreatureOf(creaturesAt,turn.getName());
        if(creatureAt == null)
            return new MoveResponse(MoveResult.MOVE_ERROR, turn.getName()+" does not have a creature at "+fromX+","+fromY);
        if(!creatureAt.getName().equals(creature))
            return new MoveResponse(MoveResult.MOVE_ERROR,"Creature at "+fromX+","+fromY+" is not "+creature);
        if(creatureAt.hasProperty(FLYING) && board.isSurrounded(fromX, fromY))
            return new MoveResponse(MoveResult.MOVE_ERROR, creature+" cannot move");
        if(!creatureAt.validEndHexagon(toX,toY,board))
            return new MoveResponse(MoveResult.MOVE_ERROR,creature+" cannot land at "+toX+","+toY);
        if(board.willDisconnect(fromX,fromY,toX,toY))
            return new MoveResponse(MoveResult.MOVE_ERROR,"Colony is not connected, try again");
        if(!validMove(creatureAt,fromX, fromY, toX, toY))
            return new MoveResponse(MoveResult.MOVE_ERROR);
        board.moveCreature(creatureAt,fromX,fromY,toX,toY);
        if(creatureAt.hasProperty(KAMIKAZE)) handleKamikaze(toX,toY);
        if(creatureAt.hasProperty(SWAPPING)) handleSwapping(fromX,fromY,toX,toY);
        turn.takeTurn();
        return endTurn();
    }

    private boolean isOccupied(int x, int y) {
        return !getCreaturesAt(x,y).isEmpty();
    }
    private Creature getCreatureOf(LinkedList<Creature> creatures, PlayerName playerName) {
        for(Creature curr : creatures) {
            if(curr.getPlayer().equals(playerName)) return curr;
        }
        return null;
    }
    private boolean validPlacement(int x, int y) {
        return board.connectedPlacement(x,y) && board.adjColor(x,y,turn.getName());
    }
    private void switchTurn() {
        if (turn.getName() == PlayerName.RED) {
            turn = players.get(PlayerName.BLUE);
        } else {
            turn = players.get(PlayerName.RED);
        }
    }
    private boolean playerTrapped(PlayerName playerName) {
        return board.isSurrounded(board.getButterfly(playerName));
    }
    private void handleSwapping(int fromX, int fromY, int toX, int toY) {
        LinkedList<Creature> creatures = board.getCreaturesAt(toX,toY);
        if(creatures.size() >= 2) {
            Creature toBeMoved = creatures.get(creatures.size()-2);
            board.removeCreature(toX, toY, toBeMoved);
            board.moveCreature(toBeMoved, toX, toY, fromX, fromY);
        }
    }
    private void handleKamikaze(int x, int y) {
        LinkedList<Creature> creatures = board.getCreaturesAt(x,y);
        if(creatures.size() == 2) {
            Creature toBeRemoved = creatures.getFirst();
            PlayerName player = toBeRemoved.getPlayer();
            board.removeCreature(x,y,toBeRemoved);
            players.get(player).recordRemoval(toBeRemoved.getName());
        }
    }
    private MoveResponse endTurn() {
        MoveResult result;
        if((result = isGameOver()) != MoveResult.OK) {
            return new MoveResponse(result);
        } else {
            switchTurn();
            return new MoveResponse(MoveResult.OK, "Legal move");
        }
    }
    private MoveResult isGameOver() {
        boolean redTrapped = playerTrapped(PlayerName.RED);
        boolean blueTrapped = playerTrapped(PlayerName.BLUE);
        if(redTrapped || blueTrapped) {
            return getWinner(redTrapped,blueTrapped);
        }
        boolean blueCannotPlace = !board.canPlace(PlayerName.BLUE);
        if(blueCannotPlace && players.get(PlayerName.BLUE).needsButterfly())
            return getWinner(false,true);
        if(blueCannotPlace && !board.canMove(PlayerName.BLUE)) {
            return getWinner(false,true);
        }
        boolean redCannotPlace = !board.canPlace(PlayerName.RED);
        if(redCannotPlace && players.get(PlayerName.RED).needsButterfly())
            return getWinner(true,false);
        if(redCannotPlace && !board.canMove(PlayerName.RED)) {
            return getWinner(true,false);
        }
        return MoveResult.OK;
    }
    private MoveResult getWinner(boolean redLost, boolean blueLost) {
        gameOver = true;
        if (redLost && blueLost)
            return MoveResult.DRAW;
        if (redLost)
            return MoveResult.BLUE_WON;
        return MoveResult.RED_WON;
    }
    private boolean validMove(Creature creature, int fromX, int fromY, int toX, int toY) {
        if(creature.movementInfo.straightLine()) {
            int distance = straightPathDistance(fromX,fromY,toX,toY);
            return distance != -1 && distance <= creature.getMaxDistance();
        }
        LinkedList<Integer> distances = board.getDistances(fromX,fromY,toX,toY,new LinkedList<>(),
                creature.movementInfo,creature.getMaxDistance());
        if(creature.movementInfo.exactDist())
            return distances.contains(creature.getMaxDistance());
        int minDist = getMinValue(distances);
        if(minDist == creature.getMaxDistance()) return true;
        return minDist != -1;

    }
    private int getMinValue(LinkedList<Integer> integers) {
        if(integers.isEmpty()) return -1;
        int min = integers.get(0);
        for(int curr : integers) {
            if(curr < min) min = curr;
        }
        return min;
    }
    private int straightPathDistance(int fromX, int fromY, int toX, int toY) {
        if(fromX == toX) return Math.abs(fromY - toY);
        if(fromY == toY) return Math.abs(fromX - toX);
        if(fromX - toX == toY - fromY) return Math.abs(fromX - toX);
        else return -1;
    }
}