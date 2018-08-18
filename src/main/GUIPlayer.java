package main;

/*
 * Created on May 1, 2005
 */

public class GUIPlayer extends Player{

	public GUIPlayer(){
		myName = "GUI Player";
	}
	
	public int ai(GameBoard gb){
		return gb.getColumn(gb.getLastClicked());
	}
}
