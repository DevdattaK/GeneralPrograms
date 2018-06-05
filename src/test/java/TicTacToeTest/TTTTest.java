package TicTacToeTest;

import TicTacToe.BoardCell;
import TicTacToe.Game;
import TicTacToe.GameBoard;
import TicTacToe.GameBoard.tCellOrientation;
import TicTacToe.HumanPlayer;
import TicTacToe.Player;
import TicTacToe.Referee;
import TicTacToe.WinningCellCalculator;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TTTTest {

  private GameBoard gameBoard;
  private Consumer<Map<tCellOrientation, List<BoardCell>>> mapDisplayer = (map) -> {
    map.keySet()
       .stream()
       .forEach(key -> System.out.println(key + ": " + map.get(key)));
  };

  @BeforeEach
  void setUp() throws Exception {
    gameBoard = new GameBoard(4);

  }

  @Test
  void whenGivenBoardCell_thenGetMapOfElementsOnCellAxis() throws Exception {
    BoardCell cell = gameBoard.getBoard()
                              .get(6);

    Map<tCellOrientation, List<BoardCell>> cellsOnAxis = gameBoard.getCellsOnAxisOf(cell);

    mapDisplayer.accept(cellsOnAxis);
  }

  @Test
  void whenGivenTwoCells_thenUseComparatorToFindPotentialWinningChoice() {
    List<String> values = Stream.of(1, 0, 0, 2, 0, 1, 1, 0, 0, 0, 1, 2, 2, 0, 1, 2)
                                .map(i -> i == 0 ? "" : Integer.toString(i))
                                .collect(Collectors.toList());

    for (int i = 0; i < gameBoard.getBoard()
                                 .size(); i++) {
      gameBoard.getBoard()
               .get(i)
               .setSign(values.get(i));
    }

    gameBoard.display();

    BoardCell c1 = gameBoard.getBoard()
                            .get(5);
    BoardCell c2 = gameBoard.getBoard()
                            .get(14);

    Map<tCellOrientation, List<BoardCell>> c1Neighbours = gameBoard.getCellsOnAxisOf(c1);
    Map<tCellOrientation, List<BoardCell>> c2Neighbours = gameBoard.getCellsOnAxisOf(c2);

    /*System.out.println(c1 + "C1 neighbours");
    mapDisplayer.accept(c1Neighbours);

    System.out.println(c2 + "C2 neighbours");
    mapDisplayer.accept(c2Neighbours);*/

    WinningCellCalculator winningCellCalculator = new WinningCellCalculator(1, gameBoard);
    int result = winningCellCalculator.getWinningChoiceComparator()
                                      .compare(c1, c2);
    assertEquals(-1, result);
  }

  @Test
  void whenGivenBoardCells_HeapifyTheBoard_thenGetWinningCell() {
    List<String> values = Stream.of(1, 0, 0, 2, 0, 1, 1, 0, 0, 0, 1, 2, 2, 0, 1, 2)
                                .map(i -> i == 0 ? "" : Integer.toString(i))
                                .collect(Collectors.toList());

    for (int i = 0; i < gameBoard.getBoard()
                                 .size(); i++) {
      gameBoard.getBoard()
               .get(i)
               .setSign(values.get(i));
    }

    gameBoard.display();
    WinningCellCalculator winningCellCalculator = new WinningCellCalculator(1, gameBoard);

    winningCellCalculator.updateCellsToRemovePopulated();
    System.out.println("Remaining winner : " + Arrays.stream(winningCellCalculator.getCells())
                                                     .map(c -> c.toString())
                                                     .collect(Collectors.joining(" | ")));

    BoardCell winningCell = winningCellCalculator.extractTopWinningChoice(false);
    assertEquals(gameBoard.getBoard()
                          .get(2), winningCell);
  }

  @Test
  void whenGameBoardIsSet_PlayerMakesMove_thenGameBoardAndOpponentsUpdate() throws Exception {
    List<String> values = Stream.of(0, 1, 0, 0, 1, 2, 1, 0, 2)
                                .map(i -> i == 0 ? "" : Integer.toString(i))
                                .collect(Collectors.toList());

    Game game = Game.getInstance(3);

    GameBoard board = Game.getGameBoard();

    /*for(int i = 0; i < board.getBoard().size(); i++){
      board.getBoard().get(i).setSign(values.get(i));
    }*/
    board.display();

    Game.getPlayers()
        .stream()
        .forEach(player -> player.getWinningCellCalculator()
                                 .updateCellsToRemovePopulated());

    Player p = Game.getPlayerById(1);
    p.getWinningCellCalculator()
     .updateCellsToRemovePopulated();
    Optional<BoardCell> cell = p.makeYourMove();

    //assertEquals(board.getBoard().get(7), cell);
    Referee referee = Referee.getInstance(board);

    //releasing lock, after finishing the turn.
    p.startYourTurn();

    Referee.notifyTurnCompleteFor(p, cell.get());

    System.out.println(
        "Winner Identified ? : " + Referee.isIsWinnerIdentified() + " Winner : " + (Referee.isIsWinnerIdentified() ? Referee.getWinnerPlayerId()
            : " NONE "));
  }

  @Test
  void whenOpponentIsAboutToWin_ThenPickOpponentsBestBet() throws Exception {
    List<String> values = Stream.of(2, 1, 1, 0, 1, 2, 0, 0, 2)
                                .map(i -> i == 0 ? "" : Integer.toString(i))
                                .collect(Collectors.toList());

    Game game = Game.getInstance(3);

    GameBoard board = Game.getGameBoard();

    Referee referee = Referee.getInstance(board);

    for (int i = 0; i < board.getBoard()
                             .size(); i++) {
      board.getBoard()
           .get(i)
           .setSign(values.get(i));
    }
    board.display();

    Game.getPlayers()
        .stream()
        .forEach(player -> player.getWinningCellCalculator()
                                 .updateCellsToRemovePopulated());

    Player p = Game.getPlayerById(2);

    Optional<BoardCell> cell = p.makeYourMove();

    assertEquals(Game.getGameBoard()
                     .getBoard()
                     .get(6), cell);
  }

  @Test
  void whenWinnerExist_thenIdentifyWinner() throws Exception {
    //Diagonal2   : List<String> values = Stream.of(2, 1, 1, 0, 1, 2, 0, 0, 2).map(i -> i == 0 ? "" : Integer.toString(i)).collect(Collectors.toList());
    //Diagonal1   : List<String> values = Stream.of(0, 1, 0, 0, 0, 2, 1, 0, 2).map(i -> i == 0 ? "" : Integer.toString(i)).collect(Collectors.toList());
    //ROW         : List<String> values = Stream.of(2, 0, 1, 1, 1, 2, 2, 0, 2).map(i -> i == 0 ? "" : Integer.toString(i)).collect(Collectors.toList());
    //COL         :
    List<String> values = Stream.of(2, 0, 0, 0, 1, 2, 1, 0, 2)
                                .map(i -> i == 0 ? "" : Integer.toString(i))
                                .collect(Collectors.toList());

    Game game = Game.getInstance(3);

    GameBoard board = Game.getGameBoard();

    Referee referee = Referee.getInstance(board);

    for (int i = 0; i < board.getBoard()
                             .size(); i++) {
      board.getBoard()
           .get(i)
           .setSign(values.get(i));
    }
    board.display();

    Game.getPlayers()
        .stream()
        .forEach(player -> player.getWinningCellCalculator()
                                 .updateCellsToRemovePopulated());

    Player p = Game.getPlayerById(2);

    Optional<BoardCell> cell = p.makeYourMove();

    assertEquals(Game.getGameBoard()
                     .getBoard()
                     .get(2), cell);

    Referee.updateWinnerStatus();

    assertTrue(Referee.isIsWinnerIdentified());
    board.display();
  }


  @Test
  void whenBoardIsPopulated_HumanPlayerMakesMove_thenGetHumanSelectedBoardCell() throws Exception {
    Game game = Game.getInstance(3);
    GameBoard board = Game.getGameBoard();
    HumanPlayer user = new HumanPlayer(1, board);
    Game.getPlayers()
        .add(1, user);

    Referee referee = Referee.getInstance(board);

    Optional<BoardCell> cell = user.makeYourMove();

    Optional<BoardCell> cell1 = user.makeYourMove();

    assertNotEquals(cell, cell1);
  }

  @Test
  void testArrayTest() throws Exception {
    Integer[] intArray = Stream.of(1, 2, 3, 4, 5)
                               .toArray(Integer[]::new);

    intArray = Arrays.stream(intArray)
                     .filter(i -> i % 2 != 0)
                     .toArray(Integer[]::new);

    Arrays.stream(intArray)
          .forEach(e -> System.out.print(" " + e));
  }

  @Test
  void whenRequestedToUpdateCurrentPlayer_TheUpdatedCurrentPlayerOfTheGame() throws Exception {
    Game game = Game.getInstance(3);
    GameBoard board = Game.getGameBoard();
    Referee referee = Referee.getInstance(board);

    referee.setCurrentTurnOfPlayer();
    //System.out.println("Current Player : " + referee.getCurrentTurnOfPlayer());
    assertEquals(1, referee.getCurrentTurnOfPlayer()
                           .getPlayerId());

    referee.setCurrentTurnOfPlayer();
    //System.out.println("Current Player : " + referee.getCurrentTurnOfPlayer());
    assertEquals(2, referee.getCurrentTurnOfPlayer()
                           .getPlayerId());
  }

  @Test
  void gameTest() throws InterruptedException {
    Game game = Game.getInstance(3);
    GameBoard board = Game.getGameBoard();
    HumanPlayer user = new HumanPlayer(1, board);
    Game.getPlayers()
        .remove(0);
    Game.getPlayers()
        .add(0, user);
    Referee referee = Referee.getInstance(board);

    ExecutorService service = Executors.newFixedThreadPool(board.getDimension());

    service.submit(referee);
    service.submit(Game.getPlayerById(1));
    service.submit(Game.getPlayerById(2));

    service.shutdown();

    service.awaitTermination(2, TimeUnit.MINUTES);
  }

  @Test
  void whenGivenEntity_ThenTestIfItIsBlocked() throws Exception {
    List<BoardCell> entity = IntStream.range(0, 3)
                                      .mapToObj(i -> new BoardCell(i, i))
                                      .collect(Collectors.toList());
    entity.stream()
          .forEach(c -> c.setSign(Integer.toString(c.getRow())));

    Game game = Game.getInstance(3);

    GameBoard board = Game.getGameBoard();

    Referee referee = Referee.getInstance(board);

    assertTrue(Referee.isEntityBlocked(entity));

    entity.stream()
          .forEach(c -> c.setSign("1"));
    assertFalse(Referee.isEntityBlocked(entity));
  }

  @Test
  void scannerTest() throws Exception {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Enter row,col : ");

    String userInput = scanner.next();

    System.out.println(userInput);
  }

  @Test
  void whenUserDoesNotWantToPlayAgainstComputer_thenLetBotsPlayWithEachOtherInSimulation() throws Exception {
    Game.letTheGameBegin();
  }
}
