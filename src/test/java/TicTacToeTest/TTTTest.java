package TicTacToeTest;

import TicTacToe.BoardCell;
import TicTacToe.GameBoard;
import TicTacToe.GameBoard.tCellOrientation;
import TicTacToe.WinningCellCalculator;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
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
  void whenGivenTwoCells_thenUseComparatorToFindPotentialWinningChoice(){
    List<String> values = Stream.of(1, 0, 0, 2, 0, 1, 1, 0, 0, 0, 1, 2, 2, 0, 1, 2).map(i -> i == 0 ? "" : Integer.toString(i)).collect(Collectors.toList());

    for(int i = 0; i < gameBoard.getBoard().size(); i++){
      gameBoard.getBoard().get(i).setSign(values.get(i));
    }

    gameBoard.display();

    BoardCell c1 = gameBoard.getBoard().get(5);
    BoardCell c2 = gameBoard.getBoard().get(14);

    Map<tCellOrientation, List<BoardCell>> c1Neighbours = gameBoard.getCellsOnAxisOf(c1);
    Map<tCellOrientation, List<BoardCell>> c2Neighbours = gameBoard.getCellsOnAxisOf(c2);

    /*System.out.println(c1 + "C1 neighbours");
    mapDisplayer.accept(c1Neighbours);

    System.out.println(c2 + "C2 neighbours");
    mapDisplayer.accept(c2Neighbours);*/

    WinningCellCalculator winningCellCalculator = new WinningCellCalculator(1, gameBoard);
    int result = winningCellCalculator.getWinningChoiceComparator().compare(c1, c2);
    assertEquals(-1, result);
  }

  @Test
  void whenGivenBoardCells_HeapifyTheBoard_thenGetWinningCell(){
    List<String> values = Stream.of(1, 0, 0, 2, 0, 1, 1, 0, 0, 0, 1, 2, 2, 0, 1, 2).map(i -> i == 0 ? "" : Integer.toString(i)).collect(Collectors.toList());

    for(int i = 0; i < gameBoard.getBoard().size(); i++){
      gameBoard.getBoard().get(i).setSign(values.get(i));
    }

    gameBoard.display();
    WinningCellCalculator winningCellCalculator = new WinningCellCalculator(1, gameBoard);

    winningCellCalculator.updateCellsToRemovePopulated();
    System.out.println("Remaining winner : " + Arrays.stream(winningCellCalculator.getCells()).map(c -> c.toString()).collect(Collectors.joining(" | ")));

    BoardCell winningCell = winningCellCalculator.extractTopWinningChoice(false);
    assertEquals(gameBoard.getBoard().get(2), winningCell);
  }
}
