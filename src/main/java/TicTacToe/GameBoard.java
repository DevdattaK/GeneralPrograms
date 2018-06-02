package TicTacToe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameBoard {

  private final ArrayList<BoardCell> board;
  private final int dimension;

  public enum tCellOrientation {ROW, COLUMN, DIAGONAL1, DIAGONAL2}

  //private Map<tCellOrientation, List<BoardCell>> neighboursOnAxis;

  public GameBoard(int dimension) {
    this.dimension = dimension;
    board = new ArrayList<>();

    Consumer<Integer> boardInitializer = (d) -> {
      for (int i = 0; i < d; i++) {
        for (int j = 0; j < d; j++) {
          board.add(new BoardCell(i, j));
        }
      }
    };

    boardInitializer.accept(dimension);

  }

  public ArrayList<BoardCell> getBoard() {
    return board;
  }

  private Map<tCellOrientation, List<BoardCell>> createEmptyNeighboursOnAxis(){
    Map<tCellOrientation, List<BoardCell>> neighboursOnAxis;

    neighboursOnAxis = new HashMap<>();
    neighboursOnAxis.put(tCellOrientation.ROW, new ArrayList<>(dimension));
    neighboursOnAxis.put(tCellOrientation.COLUMN, new ArrayList<>(dimension));
    neighboursOnAxis.put(tCellOrientation.DIAGONAL1,
        new ArrayList<>(dimension));
    neighboursOnAxis.put(tCellOrientation.DIAGONAL2,
        new ArrayList<>(dimension));

    return neighboursOnAxis;
  }

  public Map<tCellOrientation, List<BoardCell>> getCellsOnAxisOf(
      BoardCell cell) {

    int row = -1, col = -1;

    Map<tCellOrientation, List<BoardCell>> neighboursOnAxis = createEmptyNeighboursOnAxis();

    //rows on cell's axis
    neighboursOnAxis.put(tCellOrientation.ROW,
        Stream.iterate(dimension * cell.getRow(), i -> i + 1).limit(dimension)
              .map(board::get)
              //.filter(e -> e != cell)
              .collect(Collectors.toList()));

    //columns on cell's axis
    neighboursOnAxis.put(tCellOrientation.COLUMN,
        Stream.iterate(cell.getColumn(), i -> i + dimension)
              .limit(dimension)
              .map(board::get)
              //.filter(e -> e != cell)
              .collect(Collectors.toList()));

    if (cell.isDiagonalCell()) {
      neighboursOnAxis.get(tCellOrientation.DIAGONAL1)
                      .addAll(Stream.iterate(0, i -> i + dimension + 1)
                                    .limit(dimension)
                                    .map(board::get)
                                    //.filter(e -> e != cell)
                                    .collect(Collectors.toList()));

      //this cell is at the center of the board, which is of odd-dimension. Thus, cell is part of two diagonals
      if (cell.getRow() == dimension - 1) {
        neighboursOnAxis.get(tCellOrientation.DIAGONAL2)
                        .addAll(Stream.iterate(dimension - 1,
                            i -> i + dimension - 1)
                                      .limit(dimension)
                                      .map(board::get)
                                      //.filter(e -> e != cell)
                                      .collect(Collectors.toList()));
      }
    }

    return neighboursOnAxis;
  }

  private void cleanMap(Map<tCellOrientation, List<BoardCell>> neighboursOnAxis) {
    neighboursOnAxis.keySet()
                    .stream()
                    .forEach(key -> neighboursOnAxis.get(key)
                                                    .clear());
  }

  public void display(){
    for(int i = 0; i < board.size(); i++){
      System.out.print("\t" + board.get(i).getSign());
      if ((i + 1) % dimension == 0){
        System.out.println(" ");
      }
    }
  }
}
