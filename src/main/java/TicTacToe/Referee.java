package TicTacToe;

import TicTacToe.GameBoard.tCellOrientation;
import java.util.List;
import java.util.stream.Collectors;

public class Referee implements Runnable {

  private static volatile boolean isWinnerIdentified;
  private static GameBoard gameBoard;
  private static Referee instance;
  private static int winnerPlayerId = -1;
  private Player currentTurnOfPlayer;

  private Referee(GameBoard gameBoard) {
    this.gameBoard = gameBoard;

    //acquire lock of all players. So, referee decides who plays next.
    Game.getPlayers()
        .stream()
        .forEach(p -> {
          try {
            p.acquireLock();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        });
  }

  public static Referee getInstance(GameBoard gameBoard) {
    if (instance == null) {
      instance = new Referee(gameBoard);
    }

    return instance;
  }

  public static boolean isIsWinnerIdentified() {
    return isWinnerIdentified;
  }

  public static int getWinnerPlayerId() {
    return winnerPlayerId;
  }

  public void setCurrentTurnOfPlayer() {
    int nextPlayerIndex = -1;

    if (this.currentTurnOfPlayer == null) {
      this.currentTurnOfPlayer = Game.getPlayers()
                                     .get(0);
      System.out.println("Next player selected to play is first player : " + this.currentTurnOfPlayer);
    } else {
      nextPlayerIndex = this.currentTurnOfPlayer.getPlayerId() % Game.getPlayers()
                                                                     .size();

      this.currentTurnOfPlayer = Game.getPlayers()
                                     .get(nextPlayerIndex);
      System.out.println("Next player selected to play is computed player : " + this.currentTurnOfPlayer);
    }
  }

  public static void notifyTurnCompleteFor(Player player, BoardCell cell) throws InterruptedException {
    player.acquireLock();
    System.out.println("Referee acquired lock for " + player + " after its turn is over.");

    System.out.println("Current state of the board is : ");
    gameBoard.display();
    System.out.println("\n");

    //remove the cell from opponent's list, as it is populated now.
    Game.getPlayers()
        .stream()
        //.filter(p -> p != player)               //should be removed from everyones' cell list, including player who populated this cell..
        .forEach(p -> p.notifyCellPopulated(cell));

    System.out.println("Referee is updating winner status after " + player + " made its move.");
    //find winner and set fields if one exists.
    updateWinnerStatus();

    //release board control, for referee to schedule next player's turn to play game
    gameBoard.releaseBoardControl();
  }

  private static List<BoardCell> getElementsAt(int index, tCellOrientation entityType) {
    //1st row is at index (1+dimension) of boardCell list.
    index = entityType == tCellOrientation.COLUMN ? index : (index * gameBoard.getDimension());

    List<BoardCell> result = gameBoard.getCellsOnAxisOf(gameBoard.getBoard()
                                                                 .get(index))
                                      .get(entityType);

    return result;
  }

  //axis = ROW, COLUMN etc.
  private static boolean areAllCellsOnAxisFilled(tCellOrientation axis, List<BoardCell> elementsOnAxis) {

    String sign = elementsOnAxis.get(0)
                                .getSign();
    int count = (int) elementsOnAxis.stream()
                                    .filter(c -> !c.isNotPopulated() && c.getSign()
                                                                         .equals(sign))
                                    .count();

    return count == gameBoard.getDimension();
  }

  //optimize TODO
  public static void updateWinnerStatus() {
    List<BoardCell> elementsOnSameAxis = null;
    int diagonalCellIndex = -1;

    //check if any row filled.
    for (tCellOrientation dimension : gameBoard.createEmptyNeighboursOnAxis()
                                               .keySet()) {
      if (!isWinnerIdentified) {
        if (dimension == tCellOrientation.ROW || dimension == tCellOrientation.COLUMN) {
          for (int i = 0; i < gameBoard.getDimension(); i++) {
            elementsOnSameAxis = getElementsAt(i, dimension);
            if (areAllCellsOnAxisFilled(dimension, elementsOnSameAxis)) {
              winnerPlayerId = Integer.parseInt(elementsOnSameAxis.get(0)
                                                                  .getSign());
              isWinnerIdentified = true;
              break;
            }
          }
        } else {
          //There are only two diagonals, regardless of dimension. Check them individually.
          //get elements from diagonal1
          diagonalCellIndex = dimension == tCellOrientation.DIAGONAL1 ? 0 : gameBoard.getDimension() - 1;
          elementsOnSameAxis = gameBoard.getCellsOnAxisOf(gameBoard.getBoard()
                                                                   .get(diagonalCellIndex))
                                        .get(dimension);
          if (areAllCellsOnAxisFilled(dimension, elementsOnSameAxis)) {
            winnerPlayerId = Integer.parseInt(elementsOnSameAxis.get(0)
                                                                .getSign());
            isWinnerIdentified = true;
          }

        }
      } else {
        //winner is identified.
        System.out.println("Winner identified");
        break;
      }
    }

  }

  public Player getCurrentTurnOfPlayer() {
    return currentTurnOfPlayer;
  }

  @Override
  public String toString() {
    return "Referee";
  }

  @Override
  public void run() {
    while (!isWinnerIdentified) {
      try {
        System.out.println(this + " is trying to acquire board control.");
        gameBoard.acquireBoardControl();
        System.out.println(this + " acquired board control.");

        //set player who can play next.
        this.setCurrentTurnOfPlayer();

        System.out.println("Current player to take next turn is : " + this.getCurrentTurnOfPlayer());

        //releases lock of current player.
        this.currentTurnOfPlayer.startYourTurn();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    System.out.println("And the winner is : " + (winnerPlayerId > 0 ? Game.getPlayerById(winnerPlayerId)
                                                                          .toString() : "Nobody...its a draw."));
  }
}
