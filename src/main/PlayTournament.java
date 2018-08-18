package main;

/*
 * PlayTournament.java
 *
 * @author
 * J. Hollingsworth and Shannon Pollard
 *
 * Simple applet that draws a connect-four game board and 
 * allows for mouse input.
 * Plays a bracket tournament best 2-of-3 for all players.
 */

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JOptionPane;
import java.util.*;

public class PlayTournament extends Applet implements MouseListener {
	// Instance Variables
	GameBoard gb;
	WinController win;
	boolean playerAsTurn;
	Player playerA;
	Player playerB;
	Player preferredNext = null;
	ArrayList<Player> players = new ArrayList<Player>();
	ArrayList<Player> winners = new ArrayList<Player>();

	/*
	 * init() Called by the applet once at the start.
	 */
	public void init() {
		// This is where you add the players to the game.

		players.add(new AIPlayer("Computer"));
		players.add(new GUIPlayer());

		setBackground(Color.gray);
		// The standard board is 6x7
		int numRows = 5;
		int numCols = 7;
		gb = new GameBoard(numRows, numCols);
		this.setSize(numCols * 60, numRows * 82);
		win = new WinController(gb);
		addMouseListener(this); // Enables the mouse
		setUpOneRound();
	}

	/*
	 * Sets up one round by finding two players to play, or Declaring the game over.
	 */
	public void setUpOneRound() {
		// if there are still 2 players in the pool,
		if (players.size() >= 2) {
			int index1 = (int) (Math.random() * players.size());
			// Make sure the preferred player plays
			if (preferredNext != null) {
				playerA = preferredNext;
				index1 = players.indexOf(preferredNext);
			}
			// find 2 distinct players
			int index2 = (int) (Math.random() * players.size());
			while (index1 == index2) {
				index2 = (int) (Math.random() * players.size());
			}
			playerA = players.get(index1);
			playerB = players.get(index2);
			preferredNext = null;
			// reset their scores and play.
			playerA.resetScore();
			playerB.resetScore();
			playerA.setColor(GameBoard.RED);
			playerB.setColor(GameBoard.BLACK);
			setUpOneGame();
		}
		// if there is only one player left to play...
		else if (players.size() == 1) {
			// And no one is the winner's bracket, they win!
			if (winners.size() == 0) {
				proclaimWinnerTournament(players.get(0));
			}
			// Otherwise, they get a free pass to the winner's bracket
			else {
				Player p = players.get(0);
				players.remove(p);
				winners.add(p);
				JOptionPane.showMessageDialog(null, p.getName() + " gets a bye to the next round.", "Bye Notice",
						JOptionPane.INFORMATION_MESSAGE);
				// The one that gets a bye must go next.
				preferredNext = p;
			}
		}
		// If there are no players left, reset the winners back to the player pool.
		if (players.size() == 0) {
			if (winners.size() > 1) {
				JOptionPane.showMessageDialog(null, "New Round!", "Connect Four Tourney",
						JOptionPane.INFORMATION_MESSAGE);
			}
			players = winners;
			winners = new ArrayList<Player>();
			setUpOneRound();
		}
	}

	/*
	 * This method sets up one game, assuming that playerA and playerB have been
	 * chosen.
	 */
	public void setUpOneGame() {
		// addMouseListener(this); // Enables the mouse
		// reset the board.
		gb.reset();
		repaint();
		// ask who should go first
		playerAsTurn = false;
		int ans = JOptionPane
				.showConfirmDialog(
						null, "Player A (red) is " + playerA.getName() + " \n" + "Player B (black) is "
								+ playerB.getName() + " \n" + "Should Player A go first?",
						"Who's First?", JOptionPane.YES_NO_OPTION);
		if (ans == JOptionPane.YES_OPTION) {
			playerAsTurn = true;
			System.out.print(playerA.getName() + "'s move ...");
		} else
			System.out.print(playerB.getName() + "'s move ...");
		// Now the mouse click will take care of playing the
		// rest of the game...
	}

	/*
	 * This method displays the tournament winner's name
	 */
	public void proclaimWinnerTournament(Player p) {
		JOptionPane.showMessageDialog(null, p.getName() + " wins the whole tournament!", "WINNER!",
				JOptionPane.WARNING_MESSAGE);
	}

	/*
	 * This method displays the Round winner's name
	 */
	public void proclaimWinnerRound(Player p) {
		JOptionPane.showMessageDialog(null, p.getName() + " wins the round!", "WINNER!", JOptionPane.WARNING_MESSAGE);
	}

	/*
	 * paint(page)
	 * 
	 * Called by repaint() to explicitly redraw the applet.
	 */
	public void paint(Graphics page) {
		gb.draw(page);
	}

	/*
	 * mouseClicked(event)
	 * 
	 * Handle a mouse click event. This signals that some player may play.
	 */
	public void mouseClicked(MouseEvent event) {
		Point clickpoint = event.getPoint(); // returns a x,y coordinate
		gb.setLastClicked(clickpoint);
		if (playerAsTurn) {
			makeMove(playerA);
		} else { // Player B's turn
			makeMove(playerB);
		}
		// Move was made ... redraw the applet
		repaint();
	}

	/*
	 * Player p is allowed to move using its AI method.
	 */
	public void makeMove(Player p) {
		// these lines were for debugging only
		// gb.printBoard();
		// System.out.println(p.myColor);
		Player other;
		GameBoard clone = gb.clone();
		int col = p.ai(clone);
		// if the drop is valid...
		if (gb.drop(col, p.myColor)) {
			// Display the move and check for a winner
			System.out.println("column " + col + ".");
			// Other player gets to go next
			if (p == playerB) {
				other = playerA;
				playerAsTurn = true;
			} else {
				other = playerB;
				playerAsTurn = false;
			}
			System.out.print(other.getName() + "'s move.");
			// Display the move and check for a winner
			checkForWinner(p);
		}
		// if the drop was invalid, the player can try again.
		else {
			System.out.println("column " + col + ": INVALID");
			System.out.println("Still " + p.getName() + "'s turn.");
		}
	}

	/*
	 * See if the last move made a winner Assumes that Player p made the last move.
	 */
	public void checkForWinner(Player p) {
		// Use the WinController to check for a winner.
		if (win.hasWinner()) {
			// if there is a winner, repaint, score, and report it.
			repaint();
			p.incrementScore();
			System.out.println(p.getName() + " wins!");
			JOptionPane.showMessageDialog(null, p.getName() + " wins!\n" + playerA.getName() + ": " + playerA.getScore()
					+ "\n" + playerB.getName() + ": " + playerB.getScore(), "WINNER!", JOptionPane.WARNING_MESSAGE);
			// removeMouseListener(this);
			// If someone has now won 2 games, they win the round.
			if (p.getScore() == 2) {
				proclaimWinnerRound(p);
				// put the winner in the winner's bracket
				players.remove(playerA);
				players.remove(playerB);
				winners.add(p);
				// Continue play by setting up the next round.
				setUpOneRound();
			}
			// If someone won the game but not the round, play another game.
			else {
				setUpOneGame();
			}
		}
	}

	// Required mouse methods
	public void mousePressed(MouseEvent event) {
	}

	public void mouseDragged(MouseEvent event) {
	}

	public void mouseReleased(MouseEvent event) {
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mouseMoved(MouseEvent event) {
	}
}
