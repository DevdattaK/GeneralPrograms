package TicTacToe;

import TicTacToe.GameBoard.tCellOrientation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/*
this will be a priority queue built using heap.
 */
public class WinningCellCalculator {

  private BoardCell[] cells;
  private int playerId;
  private final GameBoard board;
  private final Comparator<BoardCell> winningChoiceComparator;
  private int heapSize;

  public WinningCellCalculator(int playerId, GameBoard board) {
    this.playerId = playerId;
    this.cells = board.getBoard()
                      .stream()
                      .toArray(BoardCell[]::new);
    this.heapSize = cells.length;
    this.board = board;

    Function<List<BoardCell>, Integer> getCount = (list) -> {
      int result = (int) list.stream()
                             .filter(cell -> (!cell.isNotPopulated()) && (cell.getSign()
                                                                              .equals(Integer.toString(this.playerId))))
                             .count();
      return result;
    };

    Predicate<List<BoardCell>> isNotBlocked = (list) -> {
      boolean result = list.stream()
                           .noneMatch(e -> (!e.isNotPopulated()) && !e.getSign()
                                                                      .equals(Integer.toString(this.playerId)));
      return result;
    };

    BiFunction<Function<List<BoardCell>, Integer>, Map<tCellOrientation, List<BoardCell>>, Integer> getEffectiveCount = (counter, map) -> {
      int effectiveCount = -1;
      effectiveCount = map.keySet()
                          .stream()
                          .filter(
                              k -> isNotBlocked.test(
                                  map.get(
                                      k)))
                          .map(
                              k -> map.get(
                                  k))
                          .mapToInt(
                              counter::apply)
                          .max()
                          .orElse(
                              0);

      return effectiveCount;
    };

    winningChoiceComparator = (c1, c2) -> {
      Map<tCellOrientation, List<BoardCell>> neighbourMapC1 = board.getCellsOnAxisOf(c1);
      Map<tCellOrientation, List<BoardCell>> neighbourMapC2 = board.getCellsOnAxisOf(c2);

      return getEffectiveCount.apply(getCount, neighbourMapC1)
                              .compareTo(getEffectiveCount.apply(getCount, neighbourMapC2));
    };
  }

  public Comparator<BoardCell> getWinningChoiceComparator() {
    return winningChoiceComparator;
  }

  //heapify
  public void heapify(int atIndex, int heapSize) {
    int left = atIndex * 2 + 1;
    int right = left + 1;
    int largeIndex = -1;

    int comparisonResult;

    if (left < heapSize && (comparisonResult = winningChoiceComparator.compare(this.cells[left], this.cells[atIndex])) > 0) {
      largeIndex = left;
    } else {
      largeIndex = atIndex;
    }

    if (right < heapSize && (comparisonResult = winningChoiceComparator.compare(this.cells[right], this.cells[largeIndex])) > 0) {
      largeIndex = right;
    }

    if (largeIndex != atIndex) {
      this.swap(largeIndex, atIndex);
      heapify(largeIndex, heapSize);
    }
  }

  //swap 'cells' elements at given indexes
  public void swap(int cell1Index, int cell2Index) {
    BoardCell tempCell = cells[cell1Index];
    cells[cell1Index] = cells[cell2Index];
    cells[cell2Index] = tempCell;
  }

  //buildHeap.
  public void buildHeap() {
    this.heapSize = cells.length;
    for (int i = heapSize / 2 - 1; i >= 0; i--) {
      this.heapify(i, heapSize);
    }
  }

  //extractTopContender
  public BoardCell extractTopWinningChoice(boolean shouldRemove) {
    buildHeap();

    BoardCell result = cells[0];

    if (shouldRemove) {
      cells[0] = cells[heapSize - 1];
      this.heapSize--;
    }

    return result;
  }

  public BoardCell[] getCells() {
    return cells;
  }

  //utility method for testing. Ideally, should be removed, when its populated by opponent.
  public void updateCellsToRemovePopulated() {
    BoardCell[] updatedcells = Arrays.stream(this.cells)
                                     .filter(c -> c.isNotPopulated())
                                     .toArray(BoardCell[]::new);
    this.cells = updatedcells;
  }

  public void removeCell(BoardCell cell) {
    this.cells = Arrays.stream(cells)
                       .filter(c -> c != cell)
                       .toArray(BoardCell[]::new);
  }

  public void removeAllCells(List<BoardCell> cellsToBeRemoved) {
    this.cells = Arrays.stream(this.cells)
                       .filter(cell -> !cellsToBeRemoved.contains(cell))
                       .toArray(BoardCell[]::new);
  }
}
