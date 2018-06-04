package TicTacToe;

import TicTacToe.GameBoard.tCellOrientation;
import java.util.List;
import java.util.Map;

public class BoardCell {

  private final int row;
  private final int column;
  private String sign;

  public BoardCell(int row, int column) {
    this.row = row;
    this.column = column;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public String getSign() {
    return sign;
  }

  public boolean isDiagonalCell() {
    return (row == column) || (row == 0 && column == Game.getGameBoard()
                                                         .getDimension() - 1) || (row == Game.getGameBoard()
                                                                                             .getDimension() - 1 && column == 0);
  }

  public int getRow() {
    return row;
  }

  public int getColumn() {
    return column;
  }

  public boolean isNotPopulated() {
    return sign == null || sign == "";
  }

  //is this the last cell for the Player-P, to populate and thus win the game.
  public boolean isFinalWinningCellForPlayer(Player p, GameBoard gameBoard) {
    if (this.isNotPopulated()) {
      Map<tCellOrientation, List<BoardCell>> winningChoices = gameBoard.getCellsOnAxisOf(this);

      return winningChoices.keySet()
                           .stream()
                           .anyMatch(k -> winningChoices.get(k)
                                                        .stream()
                                                        .filter(c -> c != this)
                                                        .filter(c -> c.getSign() != null && c.getSign()
                                                                                             .equals(Integer.toString(p.getPlayerId())))
                                                        .count() == gameBoard.getDimension() - 1);
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return "{" +
        "row=" + row +
        ", column=" + column +
        ", sign='" + sign + '\'' +
        '}';
  }


}
