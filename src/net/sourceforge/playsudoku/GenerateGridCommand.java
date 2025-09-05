package net.sourceforge.playsudoku;

import net.sourceforge.playsudoku.GV.NumDistributuon;

public class GenerateGridCommand extends Command {

	private SudokuGridGenerator sudGenerator;
	private SudokuGrid sudGrid;
	private SudokuGrid oldGrid;
	private int openFields; 
	private NumDistributuon nD;
	
	public GenerateGridCommand(SudokuGridGenerator generator, int of, NumDistributuon nd)
	{
		// Generate a new one
		sudGenerator = generator;
		sudGrid = sudGenerator.getGrid();
		openFields = of;
		nD = nd;
	}
	
	@Override
	public void execute() {
		// Clone the current before modification grid and save it in old.
    	oldGrid = new SudokuGrid(sudGrid);
		
        sudGrid.resetGrid();
        sudGenerator.generatePuzzle(openFields, nD);		
	}

	@Override
	public boolean isReversible() {
		return true;
	}

	@Override
	public void reverseExecute() {
		sudGrid.assign(oldGrid);
	}

	
}
