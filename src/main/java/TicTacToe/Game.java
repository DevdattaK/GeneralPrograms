package TicTacToe;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    IntStream.range(1, dimension)
             .forEach(i -> this.players.add(new Player(i, gameBoard)));
  }


  public static Game getInstance(int dimension) {
    if (instance == null) {
      instance = new Game(dimension);
    }

    return instance;
  }


  public static ArrayList<Player> getPlayers() {
    return players;
  }


  public static Player getPlayerById(int id) {
    return players.stream()
                  .filter(p -> p.getPlayerId() == id)
                  .findFirst()
                  .orElseGet(() -> new Player(id, gameBoard));
  }

  public static GameBoard getGameBoard() {
    return gameBoard;
  }

  public static void letTheGameBegin() throws InterruptedException {
    System.out.println("Enter board dimension : ");
    Scanner scanner = new Scanner(System.in);
    int dimension = Integer.parseInt(scanner.nextLine());
    //reading newline/CR
    /*//scanner.next();
    if(scanner.hasNext()){
      System.out.println("Has next.");
    }*/
    System.out.println("Do you want to play against computer? (y/n) : ");
    String userChoice = scanner.nextLine();
    //

    Game game = getInstance(dimension);

    //setup player and referee
    Referee referee = Referee.getInstance(gameBoard);

    if (userChoice.toLowerCase()
                  .equals("y")) {
      System.out.println("You are player1.");
      players.remove(0);
      players.add(0, new HumanPlayer(1, gameBoard));
    }

    ExecutorService service = Executors.newFixedThreadPool(players.size() + 1);

    service.submit(referee);

    players.stream()
           .parallel()
           .forEach(p -> service.submit(p));

    service.shutdown();

    service.awaitTermination(10, TimeUnit.MINUTES);

    scanner.close();
  }
}
