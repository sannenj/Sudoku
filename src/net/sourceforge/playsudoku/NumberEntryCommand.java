package net.sourceforge.playsudoku;

public class NumberEntryCommand extends Command {

	private int x;
	private int y;
	private int val;
	private ACell oldval;
	private SudokuGrid sudGrid;
	
	public NumberEntryCommand(SudokuGrid grid, int x_, int y_, int val_)
	{
		x = x_;
		y = y_;
		val = val_;
		sudGrid = grid;
	}
	
	@Override
	public void reverseExecute() {
		sudGrid.setCell(oldval);
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
		try {
			oldval = sudGrid.getCell(x,y).clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sudGrid.setPuzzleVal(x, y, val);
	}

}
