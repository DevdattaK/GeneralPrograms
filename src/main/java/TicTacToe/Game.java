package TicTacToe;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class Game {
  private final int dimension;
  private static ArrayList<Player> players;
  private static Game instance;
  private static GameBoard gameBoard;

  private Game(int dimension) {
    this.dimension = dimension;
    this.players = new ArrayList<>(dimension - 1);

    this.gameBoard = new GameBoard(dimension);
    IntStream.range(1, dimension).forEach(i -> this.players.add(new Player(i, gameBoard)));
  }


  public static Game getInstance(int dimension){
    if (instance == null)
      instance = new Game(dimension);

    return instance;
  }


  public static ArrayList<Player> getPlayers() {
    return players;
  }


  public static Player getPlayerById(int id){
    return players.stream().filter(p -> p.getPlayerId() == id).findFirst().orElseGet(() -> new Player(id, gameBoard));
  }

  public static GameBoard getGameBoard() {
    return gameBoard;
  }
}
