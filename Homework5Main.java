package v2;

import java.util.ArrayList;
import csis4463.*;

/**
 * See comments in Homework5.java first.
 * 
 * @author Corey Wright
 */
public class Homework5Main {
	
	public static void main(String[] args) {
		// write code here to demonstrate that your 8 puzzle solver works.
		
		Homework5 test = new Homework5();
		
		//Create a 3x3 puzzle where the optimal path is of length 8
		SlidingTilePuzzle puzzle = new SlidingTilePuzzle(3, 3, 8);
		
		System.out.println("Our Puzzle is:");
		System.out.println(puzzle);
		
		ArrayList<SlidingTilePuzzle> path = test.solver(puzzle);
		
		//Path stores the start state up to the goal state, so the shortest past is the size - 1
		System.out.println(path.size() - 1);
		
		System.out.println("Solution path");
		for (SlidingTilePuzzle s : path) {
			System.out.println();
			System.out.println(s);
		}
		
	}
}