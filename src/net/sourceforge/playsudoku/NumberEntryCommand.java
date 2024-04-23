package net.sourceforge.playsudoku;

public class NumberEntryCommand extends Command {

	private int x;
	private int y;
	private int val;
	private int oldval;
	private SudokuGrid sudGrid;
	
	public NumberEntryCommand(SudokuGrid grid, int x_, int y_, int val_)
	{
		x = x_;
		y = y_;
		val = val_;
		oldval = grid.getRealGridVal(x,y);
		sudGrid = grid;
	}
	
	@Override
	public void reverseExecute() {
		sudGrid.setRealGridVal(oldval);
	}

	@Override
	public boolean isReversible() {
		return true;
	}

	@Override
	public boolean isReExecutable() {
		return true;
	}

	@Override
	public void execute() {
		sudGrid.setPuzzleVal(x, y, val);
	}

}
