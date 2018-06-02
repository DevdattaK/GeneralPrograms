package TicTacToe;

public class Player implements Runnable {


  //make every player an observer of every other player.
  public void notifyCellPopulated(BoardCell cell){
    //remove this cell, if its part of your 'WinningCellCalculator.cells' list.
  }

  @Override
  public void run() {

  }
}
