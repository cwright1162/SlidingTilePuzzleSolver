package v2;

import csis4463.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * For this assignment, you will need the puzzle.jar file and its documentation (docs.zip) 
 * from homework 4, and the Examples.java will be useful as well.
 *
 * In Java, implement any search algorithm that we saw in class to solve sliding tile
 * puzzles, such as the 8 puzzle, 15 puzzle, etc.
 *
 * You must not change the name, parameters, or return type of the solver method.
 * You are free to add as many helper methods as you find useful.
 *
 * You are free to use the MinHeapPQ class that is in the puzzle.jar (see its documentation).
 * It supports changing the priority of elements that are in the PQ, whereas the PQ implementation
 * in the Java API does not support that operation.
 *
 * Grading:
 * Your grade will be in the interval [0, 100] if you implement an algorithm that is guaranteed
 * to find the optimal path.  Otherwise, if you implement an algorithm that is not
 * guaranteed to find the optimal path (e.g., DFS), then your grade will be in the interval [0, 85]
 * (i.e., you lose 15 points for the non-optimal algorithms).
 *
 * If your code doesn't compile, then your grade will be in the interval [0, 60] depending upon the severity
 * of the syntax errors.  i.e., make sure your code compiles (you lose at least 40 points if it doesn't.
 *
 * After completing the solver method, write code to demonstrate that it works in the Homework5Main class.
 *
 * @author Corey Wright
 *
 */
public class Homework5 {

	/**
	 * Solves sliding tile puzzles with the algorithm of your choice.
	 * Utilizes A* with the Manhattan Distance Heuristic to guarantee an 
	 * optimal path
	 *
	 * @return A path from the start state to the goal state.
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<SlidingTilePuzzle> solver(SlidingTilePuzzle start) {

		//Hold the path from start state to goal state
		ArrayList<SlidingTilePuzzle> path = new ArrayList<SlidingTilePuzzle>();

		//Start A*
		//Initialize Empty PQ
		MinHeapPQ<SlidingTilePuzzle> pq = new MinHeapPQ<SlidingTilePuzzle>();

		//Set of previously visited (State,f,Backpointer) triples and begins empty
		HashMap<SlidingTilePuzzle, Triplet<SlidingTilePuzzle, Integer, SlidingTilePuzzle>> v = 
				new HashMap<SlidingTilePuzzle, Triplet<SlidingTilePuzzle, Integer, SlidingTilePuzzle>>();

		//Add start to PQ
		pq.offer(start, manhattanDistance(start));

		//Update v with priority f(s) = g(s) + h(s)
		v.put(start, new Triplet<SlidingTilePuzzle, Integer, SlidingTilePuzzle>(start, 0 + manhattanDistance(start), null));

		boolean running = true;

		//Check if PQ is empty
		do {
			if(pq.isEmpty()) {
				//No Solution
				running = false;
				System.out.println("No Solution");
			}
			else {

				//Remove lowest priority from PQ and call it n
				int nPriority = pq.peekPriority();
				SlidingTilePuzzle n = pq.poll();

				if(n.isGoalState()) {
					//Success
					//Calculate the path and store it

					running = false;
					System.out.println("Goal State Found!");
				
					SlidingTilePuzzle current = n;
					ArrayList<SlidingTilePuzzle> bpath = new ArrayList<SlidingTilePuzzle>();
					
					//Follow the path back
					while(!current.equals(start)) {
						
						//Store the predecessor that we get from v
						bpath.add(v.get(current).getThird());
						//Update current State
						current = v.get(current).getThird();
						
					}
					
					for(int i = bpath.size() - 1; i >= 0; i--) {
						
						path.add(bpath.get(i));
						
					}
					
					path.add(n);

				}
				else {

					//Expand n for each n' successor(n)
					ArrayList<SlidingTilePuzzle> successors = n.getSuccessors();

					for(SlidingTilePuzzle nPrime : successors) {

						//F' = g(n') + h(n') = g(n') + cost(n, n') + h(n)
						//If n' not seen before, or n' previously expanded with
						//f(n')>f’, or n' currently in PQ with f(n')>f’
						int gn = nPriority - manhattanDistance(n);
						int gnPrime = gn + 1;
						int fnPrime = gnPrime + manhattanDistance(nPrime);

						if(!v.containsKey(nPrime) || (v.containsKey(nPrime) && v.get(nPrime).getSecond() > fnPrime) 
								|| (pq.inPQ(nPrime) && pq.getPriority(nPrime) > fnPrime)) {
							
							pq.offer(nPrime, fnPrime);
						
							if(v.containsKey(nPrime) && v.get(nPrime).getSecond() > fnPrime) {
								
								v.replace(nPrime, new Triplet(nPrime, fnPrime, n));
								
							}
							else {
								
								v.put(nPrime, new Triplet(nPrime, fnPrime, n));
								
							}
						}
					}
				}
			}
		}
		while (running);

		return path;
	}

	/**
	 * Helper method to calculate Manhattan Distance Heuristic
	 * @param state Current State of the Puzzle
	 * @return h - The Heuristic Value for the Manhattan Distance
	 */
	public Integer manhattanDistance(SlidingTilePuzzle state) {

		int h = 0;				//Store heuristic value to be returned
		int expected = 0;		//Store the tile value that should be in that spot
		SlidingTilePuzzle goal = getGoalState(state.numRows(), state.numColumns());		//Store an instance of the goal puzzle with the same dimensions.
		//I couldn't think of another way to do this simply, I hope it's not cheaty.
		//Go through every row in the Puzzle
		for(int row = 0; row < state.numRows(); row++) {

			//Go through every column in the Puzzle
			for(int col = 0; col < state.numColumns(); col++) {

				//Store the tilevalue in our puzzle
				int tileValue = state.getTile(row, col);
				expected++;

				//If the tile value isn't zero (not in the puzzle) 
				//or does not equal that of the goal state (e.g. 4 is in the correct spot for 4),
				//Do math to calculate how far away that tile is from it's correct spot
				if(tileValue != 0 && tileValue != expected) {

					h += Math.abs(row - getRow(goal, tileValue)) 
							+ Math.abs(col - getCol(goal, tileValue));	
				}	
			}
		}

		return h;
	}

	/**
	 * Returns a copy of the goal state for a specific size puzzle
	 * @param row How many rows the puzzle has
	 * @param col How many columns the puzzle has
	 * @return SlidingTilePuzzle - The goal state
	 */
	public SlidingTilePuzzle getGoalState(int row, int col) {

		return new SlidingTilePuzzle(row, col, 0);

	}

	/**
	 * To be used only by the method that calculates Manhattan Distance.
	 * 
	 * @param goal Goal State of the puzzle
	 * @param tileValue Value of the tile being searched for
	 * @return Integer The row that holds the value
	 */
	private int getRow(SlidingTilePuzzle goal, int tileValue) {

		return (tileValue - 1) % goal.numRows();

	}

	/**
	 * To be used only by the method that calculates Manhattan Distance.
	 * 
	 * @param goal Goal State of the puzzle
	 * @param tileValue Value of the tile being searched for
	 * @return Integer The column that holds the value
	 */
	private int getCol(SlidingTilePuzzle goal, int tileValue) {

		return (tileValue - 1) / goal.numColumns();

	}

	public boolean isSuccessor(SlidingTilePuzzle n, SlidingTilePuzzle nPrime) {

		boolean isSuccessor = false;

		for(SlidingTilePuzzle p : n.getSuccessors()) {

			if(p.equals(nPrime)) {
				isSuccessor = true;
			}
		}

		return isSuccessor;
	}

	@SuppressWarnings("rawtypes")
	public Triplet getTriplet(ArrayList<Triplet<SlidingTilePuzzle, Integer, SlidingTilePuzzle>> v, SlidingTilePuzzle puzzle) {

		Triplet t = null;

		for(Triplet tri : v) {

			if(tri.getfirst().equals(puzzle)) {
				t = tri;
			}

		}

		return t;
	}
}

class Triplet<F, S, T> {

	private F first;
	private S second;
	private T third;

	public Triplet(F first, S second, T third) {

		this.first = first;
		this.second = second;
		this.third = third;

	}

	public F getfirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}

	public T getThird() {
		return third;
	}

	/**
	 * @param first the first to set
	 */
	public void setFirst(F first) {
		this.first = first;
	}

	/**
	 * @param second the second to set
	 */
	public void setSecond(S second) {
		this.second = second;
	}

	/**
	 * @param third the third to set
	 */
	public void setThird(T third) {
		this.third = third;
	}
}