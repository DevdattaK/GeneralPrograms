package TicTacToe;

import java.util.Scanner;

public class HumanPlayer extends Player {
  private final Scanner scanner;

  public HumanPlayer(int playerId, GameBoard gameBoard) {
    super(playerId, gameBoard);
    scanner = new Scanner(System.in);
    scanner.useDelimiter("\\n");
  }


  @Override
  public BoardCell makeYourMove() {

    boolean isValidCellForMove = false;
    BoardCell cell = null;

    while (!isValidCellForMove) {
      System.out.println("Print your 'Row, Column' choice in format : 'Row#,Column#' : ");
      String userInput = "";

     if (scanner.hasNext()) {
        userInput = scanner.nextLine();
     }

      String[] choice = userInput.split(",");
      int row = Integer.parseInt(choice[0]);
      int column = Integer.parseInt(choice[1]);

      cell = this.gameBoard.getCellWithCoordinates(row, column);

      if (cell.isNotPopulated()) {
        isValidCellForMove = true;
      }
    }

    //scanner.close();

    if (cell != null) {
      cell.setSign(Integer.toString(this.getPlayerId()));
    }

    return cell;
  }
}
