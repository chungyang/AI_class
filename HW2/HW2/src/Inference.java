
public interface Inference {
	
	/**
	 * A method to apply inference on given sudoku
	 * @param sudoku
	 * @return True if the inference on sudoku is true, false otherwise
	 */
	public boolean infer(Sudoku sudoku);
	
	/**
	 * Reset its internal state
	 */
	public void reset();

}
