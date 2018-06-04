package TicTacToe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;

/*
    represent computer.
 */
public class Player implements Runnable {

  private final WinningCellCalculator winningCellCalculator;
  private final int playerId;
  private final Semaphore turnLock;
  protected final GameBoard gameBoard;

  public Player(int playerId, GameBoard gameBoard) {
    this.playerId = playerId;
    this.winningCellCalculator = new WinningCellCalculator(playerId, gameBoard);
    this.turnLock = new Semaphore(1);
    this.gameBoard = gameBoard;
  }

  public WinningCellCalculator getWinningCellCalculator() {
    return winningCellCalculator;
  }

  //make every player an observer of every other player.
  public void notifyCellPopulated(BoardCell cell) {
    //remove this cell, if its part of your 'WinningCellCalculator.cells' list.
    this.winningCellCalculator.removeCell(cell);
  }

  public int getPlayerId() {
    return playerId;
  }

  //with this release, thread would unlock.
  public void startYourTurn() {
    turnLock.release();
  }

  public void releaseLock(){
    turnLock.release();
  }

  public void acquireLock() throws InterruptedException {
    turnLock.acquire();
  }


  @Override
  public void run() {
    BoardCell cell;

    while (!Referee.isIsWinnerIdentified()) {
      try {
        System.out.println(this + " is trying to acquire lock.");
        this.acquireLock();
        System.out.println("Player Lock acquired by " + this);

        cell = this.makeYourMove();

        System.out.println(this + " populated " + cell);

        System.out.println(this + " is releasing lock.");
        this.releaseLock();

        Referee.notifyTurnCompleteFor(this, cell);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    System.out.println(this + "exiting the game...");
  }

  //get the top-cell for your opponent and block it, if it has more chances to win for opponent. Otherwise, maximize your chance.
  public BoardCell makeYourMove() {
    Map<Player, BoardCell> opponentsTopWinningChoices = this.getOpponentsWinningChoices();
    Optional<BoardCell> opponentTargetCell = Optional.empty();

    Optional<BoardCell> targetCell = this.winningCellCalculator.extractTopWinningChoice(false) == null ? Optional.empty()
        : Optional.of(this.winningCellCalculator.extractTopWinningChoice(false));

    //if player can't win by making move on this cell, then try to see if opponent is winning by making his/her next move.
    if (!targetCell.isPresent() || (targetCell.isPresent() && !targetCell.get()
                                                                         .isFinalWinningCellForPlayer(this, gameBoard))) {
      opponentTargetCell = opponentsTopWinningChoices.keySet()
                                                     .stream()
                                                     .filter(k -> opponentsTopWinningChoices.get(k)
                                                                                            .isFinalWinningCellForPlayer(k, gameBoard))
                                                     .map(opponentsTopWinningChoices::get)
                                                     .findFirst();

      //if opponent has a next target, block it with your mark.
      if(opponentTargetCell.isPresent()){
        targetCell = opponentTargetCell;
      }
    }

    if (targetCell.isPresent()) {
      targetCell.get()
                .setSign(Integer.toString(this.playerId));
    }

    return targetCell.get();
  }

  private Map<Player, BoardCell> getOpponentsWinningChoices() {
    Map<Player, BoardCell> opponentsWinningChoices = new HashMap<>();

    Game.getPlayers()
        .stream()
        .filter(p -> p != this)
        .forEach(player -> opponentsWinningChoices.putIfAbsent(player, player.winningCellCalculator.extractTopWinningChoice(false)));

    return opponentsWinningChoices;
  }

  @Override
  public String toString() {
    return "Player " + playerId;
  }
}
