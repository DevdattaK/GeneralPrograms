package TicTacToe;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

public class HumanPlayer extends Player {

  private final Scanner scanner;

  public HumanPlayer(int playerId, GameBoard gameBoard) {
    super(playerId, gameBoard);
    scanner = new Scanner(System.in);
    scanner.useDelimiter("\\n");
  }

  public void notifyWinnerIdentified() {
    //try {
    //System.in.close();
    Thread.currentThread()
          .interrupt();
    /*} catch (InterruptedException e) {
      e.printStackTrace();
    }*/
  }

  @Override
  public Optional<BoardCell> makeYourMove() {

    boolean isValidCellForMove = false;
    Optional<BoardCell> cell = Optional.empty();

    while (!isValidCellForMove) {
      System.out.println("Print your 'Row, Column' choice in format : 'Row#,Column#' : ");
      String userInput = "";

      try {
        userInput = scanner.nextLine();
      } catch (IllegalStateException e) {
        //e.printStackTrace();
      }

      String[] choice = userInput.split(",");
      int row = Integer.parseInt(choice[0]);
      int column = Integer.parseInt(choice[1]);

      cell = Optional.of(this.gameBoard.getCellWithCoordinates(row, column));

      if (cell.isPresent() && cell.get()
                                  .isNotPopulated()) {
        isValidCellForMove = true;
      }
    }

    //scanner.close();

    if (cell.isPresent()) {
      cell.get()
          .setSign(Integer.toString(this.getPlayerId()));
    }

    return cell;
  }
}
