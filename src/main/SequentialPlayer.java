package main;
/**
 * @Author amandaconover
 */

public class SequentialPlayer extends Player {
	public SequentialPlayer() {
    		myName = "Sequential";
	}

public int ai(GameBoard gb) {
    int[][] board = gb.getBoard();

// Now finish it...Finish the code in the ai method 
// so that it returns the first column that is a legal move.
// You will need a loop to do this.

	for(int i = 0; i< board.length; i++){
		if (gb.isLegalMove(i)) {
			return i;
		}
	}
	return -1;
}
}