import java.util.*;

public class SudokuSolver{

	public boolean backTrackingSearch(Sudoku sudoku, boolean useMRV){
		return simpleBackTracking(sudoku, useMRV);
	}

	private boolean simpleBackTracking(Sudoku sudoku, boolean useMRV){
		int[] assignments = sudoku.getAssignments();
		boolean[][] domains = sudoku.getDomains();

		if(isComplete(assignments)){
			return true;
		}

		int unassignIndex = useMRV? getUnassignedMRV(assignments, sudoku.getNeighbors()):getUnassigned(assignments);

		sudoku.incrementGuess(unassignIndex);

		for(int i = 0; i < domains[0].length; i++){

			Set<Integer> neighbors = sudoku.getNeighbors().get(unassignIndex);

			if(domains[unassignIndex][i] && isConsistent(unassignIndex,i + 1,sudoku.getDomains(), neighbors)){
//				assignments[unassignIndex] = i + 1;
//				sudoku.setAssignments(assignments);
				sudoku.assign(unassignIndex, i + 1);

				if(simpleBackTracking(sudoku, useMRV)){
					return true;
				}
				
				sudoku.unassign(unassignIndex);
//				assignments[unassignIndex] = 0;
//				sudoku.setAssignments(assignments);
			}
		}

		return false;
	}

	/**
	 * A method to find the first unassigned variable
	 * @param assignments
	 * @return unassigned variable index, -1 if not found
	 */
	private int getUnassigned(int[] assignments){

		for(int i = 0; i < assignments.length; i++){

			if(assignments[i] == 0){
				return i;
			}
		}
		return -1;
	}

	private int getUnassignedMRV(int[] assignments, Map<Integer, Set<Integer>> allNeighbors){

		int mrv = 0;
		int UnassignedMRV = -1;

		for(int i = 0; i < assignments.length; i++){

			if(assignments[i] == 0){
				int count = 0;

				for(int neighbor : allNeighbors.get(i)){
					if(assignments[neighbor] != 0){
						count++;
					}
				}

				if(count > mrv){
					mrv = count;
					UnassignedMRV = i;
				}
			}
		}

		return UnassignedMRV;
	}
	/**
	 * A method to check if the assignment is complete
	 * @param assignments
	 * @return true if complete, false otherwise
	 */
	private boolean isComplete(int[] assignments){
		return getUnassigned(assignments) < 0;
	}

	/**
	 * A method to check if an assigned value is consistent with the constraint 
	 * @param assignmentIndex
	 * @param assignValue
	 * @param assignments
	 * @return true if consistent, false otherwise 
	 */
	private boolean isConsistent(int assignmentIndex,int assignValue, boolean[][] domains, Set<Integer> neighbors){

		for(int neighbor : neighbors){
			boolean consistent = false;

			for(int i = 0; i < domains[neighbor].length; i++){
				if(domains[neighbor][i] && assignValue != i + 1){
					consistent = true;
					break;
				}
			}
			if(!consistent){
				return false;
			}
		}
		return true;
	}

	class ACthree implements Inference{

		@Override
		public boolean inference(Sudoku sudoku) {



			return true;
		}

		private Set<Integer[]> getArcs(Sudoku sudoku){
			Set<Integer[]> arcs = new HashSet();

			return arcs;
		}
		private boolean revise(Sudoku sudoku, int[] nodes){
			int node1 = nodes[0];
			int node2 = nodes[1];

			boolean[] node1Domain = sudoku.getDomains()[node1];
			boolean[] node2Domain = sudoku.getDomains()[node2];

			for(int i = 0; i < node1Domain.length; i++){
				boolean isConsistent = false;

				for(int j = 0; j < node2Domain.length; j++){
					if(node1Domain[i] && node2Domain[j] && i != j){
						isConsistent = true;
						break;
					}
				}
				if(!isConsistent){
					node1Domain[i] = false;
					return true;
				}
			}
			return false;
		}
	}

	public static void main(String[] args) {
		Sudoku sudoku = new Sudoku("puz-100.txt");
		sudoku.printSudoku();
		SudokuSolver solver = new SudokuSolver();
		System.out.println(solver.backTrackingSearch(sudoku,false));
		System.out.println(sudoku.getGuesses());
		sudoku.printSudoku();

	}
}
