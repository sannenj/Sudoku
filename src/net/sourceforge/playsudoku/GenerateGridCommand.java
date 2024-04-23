package net.sourceforge.playsudoku;

import net.sourceforge.playsudoku.GV.NumDistributuon;

public class GenerateGridCommand extends Command {

	private SudokuGenerator sudGenerator;
	private SudokuGrid sudGrid;
	private SudokuGrid oldGrid;
	private int openFields; 
	private NumDistributuon nD;
	
	public GenerateGridCommand(SudokuGenerator generator, SudokuGrid grid, int of, NumDistributuon nd)
	{
		sudGrid = grid;
		oldGrid = new SudokuGrid(grid);
		sudGenerator = generator;
		openFields = of;
		nD = nd;
	}
	
	@Override
	public void execute() {
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
		
		// Hack to force gui update of new grid.
		//sudGrid.clearNonDefaultCells();
	}

	
	
}
