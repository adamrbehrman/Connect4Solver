package main;

/*
 * Created on Dec 4, 2004
 */

public abstract class Player {
	// This tells the color for the player.
	// It will be either GameBoard.RED or GameBoard.BLACK
	public int myColor;
	
	//This is the name of the Player
	public String myName;
	
	// Number of games won in the round.
	public int myScore = 0;
	
	// Get the score
	public int getScore(){
		return myScore;
	}
	
	// Won one!  Increment the score 
	public void incrementScore(){
		myScore = myScore+1;
	}
	
	// Starting new round, reset score.
	public void resetScore(){
		myScore = 0;
	}
	
	// Setter for the checker color
	public void setColor(int color){
		myColor = color;
	}
	
	public String getName(){
		return myName;
	}
	
	// The students will write this! 
	public abstract int ai(GameBoard gb);
}
