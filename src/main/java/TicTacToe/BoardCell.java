package TicTacToe;

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

  public boolean isDiagonalCell(){
    return row == column;
  }

  public int getRow(){
    return row;
  }

  public int getColumn(){
    return column;
  }

  public boolean isNotPopulated(){
    return sign == null || sign == "";
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
