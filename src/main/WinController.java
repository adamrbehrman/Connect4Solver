package main;
/*
 * WinController.java
 * Author:
 * This object's job is to decide
 * if there is a winning configuration 
 * (four in a row) on the board.
 */
public class WinController {
	GameBoard myGameBoard;
	
	public WinController(GameBoard game) {
		myGameBoard = game;
	}

	/* TODO!
	 * Checks horizontal, vertical, and diagonal directions for 
	 * four like checkers in a row. Return true is winner is 
	 * found, otherwise return false.
	 */
	public boolean hasWinner() {
		int[][] board = myGameBoard.getBoard();
		
		int rows = board.length;
		int cols = board[0].length;
		
		
		for(int i=0;i<rows;i++) {
			for(int j=0;j<cols - 3; j++) {
				if(board[i][j] == board[i][j+1]&&board[i][j]==board[i][j+3]&&
						board[i][j+1]==board[i][j+2] && board[i][j] != GameBoard.EMPTY) {
					return true;
				}
				// return true;
			}
		}
		
		for(int j=0;j<cols;j++) {
			for(int i=0;i<rows - 3; i++) {
				if(board[i][j] == board[i+1][j]&&board[i][j]==board[i+3][j]&&
						board[i+1][j]==board[i+2][j] && board[i][j] != GameBoard.EMPTY) {
					return true;
				}
				// return true;
			}
		}
		
		
		for(int i=0;i<rows-3;i++) {
			for(int j=0;j<cols - 3; j++) {
				if(board[i][j] == board[i+1][j+1]&&board[i][j]==board[i+3][j+3]&&
						board[i+1][j+1]==board[i+2][j+2] && board[i][j] != GameBoard.EMPTY) {
					return true;
				}
				// return true;
			}
		}
		
		for(int i=0;i< rows-3;i++) {
			for(int j=cols-1; j>=3; j--) {
				if(board[i][j] == board[i+1][j-1]&&board[i][j]==board[i+3][j-3]&&
						board[i+1][j-1]==board[i+2][j-2] && board[i][j] != GameBoard.EMPTY) {
					return true;
				}
				// return true;
			}
		}
		return false;
}}
