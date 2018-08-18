package main;


public class RandomPlayer extends Player {

	public RandomPlayer(){
		myName = "Random Player";
	}
	
	// Returns a random, legal column to move in.
	public int ai(GameBoard gb) {
		int[][] board = gb.getBoard();
		int random = (int)(Math.random()*board[0].length);
		while(!gb.isLegalMove(random)){
			random = (int)(Math.random()*board[0].length);
		}
		return random;
	}

}
