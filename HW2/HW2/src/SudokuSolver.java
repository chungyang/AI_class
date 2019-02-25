import java.util.*;

public class SudokuSolver{

	private List<Inference> inferences = new LinkedList();

	public boolean backTrackingSearch(Sudoku sudoku, boolean useMRV){
		if(this.inferences != null){
			for(Inference inference : inferences){
				inference.reset();
			}
		}
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

			if(domains[unassignIndex][i] && isConsistent(unassignIndex,i + 1, domains, neighbors)){

				domains =  deepCopy(sudoku.getDomains());
				sudoku.assign(unassignIndex, i + 1);
				boolean validInferences = true;
				
				for(Inference inference : inferences){
					validInferences &= inference.infer(sudoku);
				}
				
				if(validInferences && simpleBackTracking(sudoku, useMRV)){
					return true;
				}
				
				sudoku.setDomains(domains);
				sudoku.unassign(unassignIndex);
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
	private static boolean isConsistent(int assignmentIndex,int assignValue, boolean[][] domains, Set<Integer> neighbors){

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

	private boolean[][] deepCopy(boolean[][] original){
		int rowLength = original.length;
		int colLength = original[0].length;

		boolean[][] copy = new boolean[rowLength][colLength];

		for(int i = 0; i < rowLength; i++){
			for(int j = 0; j < colLength; j++){
				copy[i][j] = original[i][j];
			}
		}

		return copy;
	}
	public void addInference(Inference inference){
		this.inferences.add(inference);
	}

	static class ACthree implements Inference{
		private List<int[]> arcs;

		@Override
		public boolean infer(Sudoku sudoku) {

			if(arcs == null){
				arcs = new LinkedList();
				arcs.addAll(getArcs(sudoku));
			}

			while(!arcs.isEmpty()){
				int[] nodes = arcs.remove(0);

				if(revise(sudoku, nodes)){

					List<Integer> domainValues = sudoku.getDomainValues(nodes[0]);

					if(sudoku.getDomainValues(nodes[0]).size() == 0){
						return false;
					}
					for(int neighbor : sudoku.getNeighbors().get(nodes[0])){
						if(neighbor != nodes[1]){
							arcs.add(new int[]{neighbor, nodes[0]});
						}
					}
				}
			}

			return true;
		}

		private Set<int[]> getArcs(Sudoku sudoku){

			Set<int[]> arcs = new HashSet();

			for(Map.Entry<Integer, Set<Integer>> entry : sudoku.getNeighbors().entrySet()){
				int node = entry.getKey();

				for(int neighbor : entry.getValue()){
					int[] arc = new int[]{node, neighbor};
					arcs.add(arc);

				}
			}
			return arcs;
		}

		private boolean revise(Sudoku sudoku, int[] nodes){

			Set<Integer> neighbor = new HashSet<Integer>();
			neighbor.add(nodes[1]);

			for(int x : sudoku.getDomainValues(nodes[0])){
				if(!isConsistent(nodes[0], x, sudoku.getDomains(), neighbor)){
					sudoku.deleteFromDomain(nodes[0], x);
					return true;
				}
			}

			return false;
		}

		@Override
		public void reset() {
			this.arcs = null;
		}
	}

	public static void main(String[] args) {
		String[] puzzles = new String[]{"001","002","010","015","025","026","048","051","062","076","081","082","090",
				"095","099","100"};

		SudokuSolver solver = new SudokuSolver();
		ACthree ac3 = new ACthree();
		solver.addInference(ac3);

		for(String puzzle : puzzles){
			System.out.println("puz-"+ puzzle + ".txt");
			Sudoku sudoku1 = new Sudoku("puz-" + puzzle + ".txt");
			Sudoku sudoku2 = new Sudoku("puz-" + puzzle + ".txt");
			

	
			System.out.println(solver.backTrackingSearch(sudoku1,true));
			System.out.println("");
			sudoku1.printSudoku();
			System.out.println("");
			System.out.println(solver.backTrackingSearch(sudoku2,false));
			sudoku2.printSudoku();
			System.out.println("");
			System.out.println(sudoku1.getGuesses() + " & " + sudoku2.getGuesses());

		}
	}
}
