package main;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class AIPlayer extends Player {
	public AIPlayer() {
		myName = "Behrman";
	}

	public AIPlayer(String s) {
		myName = s;
	}

	private ArrayList<ArrayList<Integer>> getPos(GameBoard gb) {
		ArrayList<ArrayList<Integer>> pos = new ArrayList<ArrayList<Integer>>();
		for (int col = 0; col < gb.getBoard()[0].length; col++) {
			pos.add(new ArrayList<Integer>());
			if (gb.isLegalMove(col)) {
				for (int row = 0; row < gb.getBoard().length; row++) {
					if (gb.getBoard()[row][col] == GameBoard.EMPTY) {
						pos.get(col).add(row);
					}
				}
			}
		}
		return pos;
	}

	@SuppressWarnings("unchecked")
	public int ai(GameBoard gb) {
		System.out.println();
		ArrayList<ArrayList<Integer>> pos = getPos(gb);
		int otherPersonColor = 1;
		if (this.myColor == 1) {
			otherPersonColor = 2;
		}
		// check the board for immediate moves
		int move = -1;
		try {
			move = getBestMove("immediateMoves.txt", -1, gb, otherPersonColor, this.myColor, this.myColor);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (move == -1) {
			while (!gb.isLegalMove(move)) {
				move = new Random().nextInt(gb.getBoard()[0].length);
			}
		}
		return move;
	}

	// needs a file
	private ArrayList<Integer> checkPositionsGivenChipPlayed(ArrayList<ArrayList<Integer>> pos, GameBoard gb,
			int chipColor, int otherColor, int myColor) throws FileNotFoundException {
		ArrayList<Integer> moves = new ArrayList<Integer>();
		int prevCol = 0;
		int prevRow = 0;
		for (int col = 0; col < pos.size(); col++) {
			if (!pos.get(col).isEmpty()) {
				int row = pos.get(col).get(pos.get(col).size() - 1);

				gb.getBoard()[row][col] = chipColor;
				// plays chip
				pos = getPos(gb);

				try {
					moves.add(getBestMove("", col, gb, chipColor, otherColor, myColor));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				prevCol = col;
				prevRow = row;
				gb.getBoard()[prevRow][prevCol] = GameBoard.EMPTY;
			}
		}
		return moves;
	}

	private int getBestMove(String fileName, int col, GameBoard gb, int chipColor, int otherColor, int myColor)
			throws FileNotFoundException {
		char myColorChar = Integer.toString(myColor).charAt(0);
		File f = new File(fileName);
		Map<Character, Character[]> key = setupKey(f);
		ArrayList<Position> allPositions = setupPositions(f);
		char sameColor = 'X';
		char chipPlayed = Integer.toString(chipColor).charAt(0);
		char chipNotPlayed = Integer.toString(otherColor).charAt(0);

		char[][] map = new char[gb.getBoard().length][gb.getBoard()[0].length];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = Integer.toString(gb.getBoard()[i][j]).charAt(0);
			}
		}
		// init and setup a char[][] map of the spots on the board

		// printCurrentMap(col, map, chipPlayed);
		// prints the map with all spots

		// setupAndPrintPlayerMap(map, chipPlayed);
		// prints only the chips of the player whose chip was just played

		key.put(sameColor, new Character[] { chipPlayed });
		// makes that chip color the value of 'X'

		Object[] playerPlayedPositions = getPositionsOnBoard(allPositions, map, key, sameColor);
		// get the positions on the board using that chip color as 'X'

		Object[] playerPlayedMoves = getPossibleMoves(playerPlayedPositions);
		// get the possible moves from the positions

		int m1 = analyzePositions(playerPlayedMoves, chipPlayed, myColorChar);

		// setupAndPrintPlayerMap(map, chipNotPlayed);
		// prints only the chips of the player who has yet to play

		key.put(sameColor, new Character[] { chipNotPlayed });
		// makes that chip color the value of 'X'

		Object[] playerToPlayPositions = getPositionsOnBoard(allPositions, map, key, sameColor);
		// get the positions on the board using that chip color as 'X'

		Object[] playerToPlayMoves = getPossibleMoves(playerToPlayPositions);
		// get the possible moves from the positions

		int m2 = analyzePositions(playerToPlayMoves, chipNotPlayed, myColorChar);

		if (m1 == m2) {
			if (m1 != -1) {
				return m1;
			} else {
				return -1;
			}
		} else {
			if (m1 == -1) {
				return m2;
			} else if (m2 == -1) {
				return m1;
			} else {
				return -1;
			}
		}
	}

	private Map<Character, Character[]> setupKey(File f) throws FileNotFoundException {
		Map<Character, Character[]> key = new HashMap<Character, Character[]>();
		String s = "";
		boolean stop = false;
		Scanner scan = new Scanner(f);
		s = scan.nextLine();
		Character sameColor = s.charAt(0);
		key.put(sameColor, new Character[] { '*' });
		while (!stop) {
			s = scan.nextLine();
			if (!s.contains(":")) {
				stop = true;
			} else {
				String[] x = s.split(":");
				char position = x[0].charAt(0);
				String[] map = x[1].split(",");
				Character[] keys = new Character[map.length];
				for (int i = 0; i < map.length; i++) {
					keys[i] = map[i].charAt(0);
				}
				key.put(position, keys);
			}
		}
		scan.close();
		return key;
	}

	private ArrayList<Position> setupPositions(File f) throws FileNotFoundException {
		ArrayList<Position> allPositions = new ArrayList<Position>();
		ArrayList<String> currentPosition = new ArrayList<String>();
		int priorLevel = -1;
		Point playPos = new Point(-1, -1);
		Scanner scan = new Scanner(f);
		String s = "*";
		boolean good = true;
		while (!s.equals("")) {
			s = scan.nextLine();
		}
		while (scan.hasNextLine()) {
			s = scan.nextLine();
			if (s.startsWith("*")) {
				priorLevel = s.length() - 1;
				Position p = new Position(currentPosition, priorLevel, playPos, good);
				good = true;
				allPositions.add(p);
				currentPosition.clear();
			} else if (s.contains(",")) {
				String[] temp = s.split(",");
				if (temp[0].contains("!")) {
					good = false;
					temp[0] = temp[0].charAt(temp[0].length() - 1) + "";
				}
				playPos = new Point(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
			} else {
				currentPosition.add(s);
			}
		}
		scan.close();
		Collections.sort(allPositions);
		return allPositions;
	}

	private int analyzePositions(Object[] allMoves, char chipPlayedColor, char myColorChar) {
		int move = 0;
		ArrayList<Integer> goodMoves = (ArrayList<Integer>) allMoves[0];
		ArrayList<Integer> badMoves = (ArrayList<Integer>) allMoves[1];
		// good moves based off of mine, bad moves based off of other
//		if (chipPlayedColor == myColorChar) {
//			System.out.println("Good moves for me based on my positions: ");
//			for (int i = 0; i < goodMoves.size(); i++) {
//				System.out.print(goodMoves.get(i) + ", ");
//			}
//			System.out.println();
//			System.out.println("Bad moves for me based on their positions: ");
//			for (int i = 0; i < badMoves.size(); i++) {
//				System.out.print(badMoves.get(i) + ", ");
//			}
//		} else {
//			System.out.println("Good moves for them based on their positions: ");
//			for (int i = 0; i < goodMoves.size(); i++) {
//				System.out.print(goodMoves.get(i) + ", ");
//			}
//			System.out.println();
//			System.out.println("Bad moves for them based on my positions: ");
//			for (int i = 0; i < badMoves.size(); i++) {
//				System.out.print(badMoves.get(i) + ", ");
//			}
//		}
//		System.out.println();

		if (goodMoves.isEmpty()) {
			return -1;
		} else {
			move = goodMoves.get(new Random().nextInt(goodMoves.size()));
		}

		return move;
	}

	private Object[] getPositionsOnBoard(ArrayList<Position> allPositions, char[][] map,
			Map<Character, Character[]> key, char sameColor) {
		ArrayList<Point> bottomLeftCorners = new ArrayList<Point>();
		ArrayList<Position> onBoard = new ArrayList<Position>();
		Object[] arrays = new Object[] { onBoard, bottomLeftCorners };
		int indexR = 0;
		int indexC = 0;
		boolean stop;
		boolean match;
		boolean row1 = false;
		boolean firstRun = false;
		if (!allPositions.isEmpty()) {
			for (int i = 0; i < allPositions.size(); i++) {
				// go through all possible positions that remain
				String[] pos = allPositions.get(i).map;
				row1 = false;
				String test = "";
				for (int j = 0; j < pos[0].length(); j++) {
					test += "n";
				}
				if (test.equals(pos[pos.length - 1])) {
					row1 = true;
				}
				// System.out.println(allPositions.get(i).toString());
				// get the position's String[]
				for (int r = map.length - 1; r > -1; r--) {
					// go through all the rows in map
					if (row1) {
						if (firstRun) {
							r = map.length - 1;
							firstRun = false;
							row1 = false;
						}
					}
					for (int c = 0; c < map[r].length; c++) {
						// go through all the columns in map
						indexR = pos.length - 1;
						if (row1 && r == map.length - 1) {
							indexR--;
							firstRun = true;
						}
						indexC = 0;
						int rMod = 0;
						int cMod = 0;
						stop = false;
						match = false;
						do {
							// System.out.print("("+indexR+","+indexC+")");
							if (indexR < map.length && indexC < map[r].length && indexR > -1 && indexC > -1) {
								// System.out.println("Inside bounds.");
								// if the index is inside the map...
								if ((c + cMod) > map[r + rMod].length - 1) {
									if (indexC > pos[indexR].length() - 1) {
										indexC = 0;
										cMod = 0;
										rMod--;
										indexR--;
										if ((r + rMod) < 0) {
											stop = true;
											if (indexR < 0) {
												// match
												// System.out.println("Match!!!!!!");
												match = true;
											}
										} else {
											if (indexR < 0) {
												// match
												// System.out.println("Match!!!!!!");
												match = true;
												stop = true;
											}
										}
									} else {
										stop = true;
									}
								} else if (indexC > pos[indexR].length() - 1) {
									indexC = 0;
									cMod = 0;
									rMod--;
									indexR--;
									if ((r + rMod) < 0) {
										stop = true;
										if (indexR < 0) {
											// match
											// System.out.println("Match!!!!!!");
											match = true;
										}
									} else {
										if (indexR < 0) {
											// match
											// System.out.println("Match!!!!!!");
											match = true;
											stop = true;
										}
									}
								}
							}

							if (!stop) {
								// System.out.print("Map[" + (r + rMod) + "," + (c + cMod) + "] = ("
								// + map[r + rMod][c + cMod] + " =? " + pos[indexR].charAt(indexC) + "), ");
								char currentMapChar = map[r + rMod][c + cMod];
								char currentPositionChar = pos[indexR].charAt(indexC);

								if (interpret(currentMapChar, currentPositionChar, key)) {
									cMod++;
									indexC++;
								} else {
									stop = true;
								}
							} else {
								stop = true;
							}

						} while (!stop);
						if (match) {
							onBoard.add(allPositions.get(i));
							bottomLeftCorners.add(new Point(r, c));
						}
						// System.out.println();
					}
				}
			}
		}
		return arrays;
	}

	private Object[] getPossibleMoves(Object[] chipPositions) {
		ArrayList<Position> onBoard = (ArrayList<Position>) chipPositions[0];
		ArrayList<Point> bottomLeftCorner = (ArrayList<Point>) chipPositions[1];

		ArrayList<Integer> goodMoves = new ArrayList<Integer>();
		ArrayList<Integer> badMoves = new ArrayList<Integer>();
		Object[] allMoves = { goodMoves, badMoves };
		for (int i = 0; i < onBoard.size(); i++) {
			String[] pos = onBoard.get(i).map;
			// for (int j = 0; j < pos.length; j++) {
			// System.out.println(onBoard.get(i).toString());
			// }
			Point p = onBoard.get(i).playPos;
			bottomLeftCorner.get(i).translate(p.x, p.y);
			if (onBoard.get(i).good) {
				// good move
				goodMoves.add((int) bottomLeftCorner.get(i).getY());
			} else {
				// bad move
				badMoves.add((int) bottomLeftCorner.get(i).getY());
			}
		}
		return allMoves;
	}

	private void setupAndPrintPlayerMap(char[][] map, char chipColor) {
		char[][] mapToPrint = new char[map.length][map[0].length];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j] != chipColor && map[i][j] != '0') {
					mapToPrint[i][j] = '0';
				} else {
					mapToPrint[i][j] = map[i][j];
				}
			}
		}

		System.out.println("Player " + chipColor + "'s map:");
		for (int i = 0; i < mapToPrint.length; i++) {
			for (int j = 0; j < mapToPrint[i].length; j++) {
				System.out.print(mapToPrint[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}

	private void printCurrentMap(int col, char[][] map, char chipPlayedColor) {
		if (col == -1) {
			System.out.println("Current map:");
		} else {
			System.out.println("If player " + chipPlayedColor + " plays in col: " + col);
		}

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}

	private boolean interpret(char mapChar, char posChar, Map<Character, Character[]> key) {
		boolean match = false;
		Character[] x = key.get(posChar);
		for (int i = 0; i < x.length; i++) {
			if (x[i] == mapChar) {
				match = true;
			}
		}
		return match;
	}
}

class Position implements Comparable<Position> {

	String[] map;
	int priority;
	Point playPos;
	boolean good;

	public Position(ArrayList<String> map, int p, Point pp, boolean g) {
		this.map = map.toArray(new String[map.size()]);
		priority = p;
		playPos = pp;
		good = g;
	}

	@Override
	public int compareTo(Position p1) {
		return (priority - p1.priority);
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < map.length; i++) {
			s += "\n" + map[i];
		}
		if (good) {
			s += "\nMake play at: ";
		} else {
			s += "\nDon't make play at: ";
		}
		return "\nScenario: " + s + playPos.toString();
	}
}