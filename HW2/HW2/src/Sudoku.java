import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class Sudoku {
	private final int NUM_VARS = 81;
	private int assignments[] = new int[NUM_VARS];
	private boolean varDomain[][] = new boolean[NUM_VARS][9];
	private Map<Integer, Set<Integer>> neighbors = new HashMap();
	private int guesses;
	
	public Sudoku(String fileName){
		makeSudokuPuzzle(fileName);
	}
	//Takes input from a file and creates a Sudoku CSP from that information
	private void makeSudokuPuzzle(String fileName){
		try{
			FileReader file = new FileReader(fileName);
			for (int a = 0; a < NUM_VARS; a++){
				char ch;
				do{
					ch = (char)file.read();
				}while ((ch == '\n') || (ch == '\r') || (ch == ' '));
				if (ch == '-'){
					assignments[a] = 0;

					for(int j = 0; j < 9; j++){
						varDomain[a][j] = true;
					}
				}
				else{
					String  s = "" + ch;
					Integer i = new Integer(s);
					assignments[a] = i.intValue();
					for (int j = 0; j < 9; j++){
						if (j == i.intValue() - 1)
							varDomain[a][j] = true;
						else
							varDomain[a][j] = false;
					}
				}
			}

			file.close();
		}
		catch(IOException e)
		{
			System.out.println("File read error: " + e);
		}
		buildNeighbors();
	}
	
	private void buildNeighbors(){
		
		for(int j = 0; j < 81; j++){
			int rowNumber = j / 9;
			int colNumber = j % 9;
			Set<Integer> neighbors = new HashSet();
			
			//Add column neighbors
			for(int i = 0; i < 9; i++){
				int index = rowNumber * 9 + i;
				neighbors.add(index);
//				if(index != j && assignments[index] > 0){
//					varDomain[j][assignments[index] - 1] = false;
//				}
			}
			
			//Add row neighbors
			for(int i = colNumber; i < 81; i += 9){
				neighbors.add(i);
//				if(i != j && assignments[i] > 0){
//					varDomain[j][assignments[i] - 1] = false;
//				}
			}

			//Check 3x3 grid consistency
			int gridRow = rowNumber - rowNumber % 3;
			int gridCol = colNumber - colNumber % 3;

			for(int i = gridRow; i < gridRow + 3; i++){
				for(int k = gridCol; k < gridCol + 3; k++){
					int index = i * 9 + k;
					neighbors.add(index);
//					if(index != j && assignments[index] > 0){
//						varDomain[j][assignments[index] - 1] = false;
//					}
				}
			}
			neighbors.remove(j);
			this.neighbors.put(j, neighbors);
		}
	}

	//Outputs the Sudoku board to the console and a file
	public void printSudoku(){
		try{
			FileWriter ofile = new FileWriter("output.txt", true);
			for (int a = 0; a < 9; a++){
				for (int b = 0; b < 9; b++){
					int c = 9*a + b;
					if (assignments[c] == 0){
						System.out.print("- ");
						ofile.write("- ");
					}
					else{
						System.out.print(assignments[c] + " ");
						ofile.write(assignments[c] + " ");
					}
				}
				System.out.println("");
				ofile.write("\r\n");
			}
			ofile.write("\r\n");
			ofile.close();
		}
		catch(IOException e){System.out.println("File read error: " + e);}
	}
	
	public void assign(int index, int assignment){
		this.assignments[index] = assignment;
		
		for(int i = 0; i < this.varDomain[index].length; i++){
			this.varDomain[index][i] = false;
		}
		this.varDomain[index][assignment - 1] = true; 
	}
	
	public void unassign(int index){
		this.assignments[index] = 0;
		
		for(int i = 0; i < this.varDomain[index].length; i++){
			this.varDomain[index][i] = true;
		}
	}
	
	public int[] getAssignments(){
		return assignments;
	}
	
	public boolean[][] getDomains(){
		return varDomain;
	}
	
	public void setAssignments(int[] assignments){
		this.assignments = assignments;
	}
	
	public void setDomains(boolean[][] domains){
		this.varDomain = domains;
	}
	
	public void incrementGuess(int index){
		for(boolean domain : varDomain[index]){
			if(domain){
				this.guesses++;
			}
		}
	}
	
	public int getGuesses(){
		return this.guesses;
	}
	
	public Map<Integer, Set<Integer>> getNeighbors(){
		return this.neighbors;
	}
}
