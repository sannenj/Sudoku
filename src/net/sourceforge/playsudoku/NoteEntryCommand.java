package net.sourceforge.playsudoku;

public class NoteEntryCommand extends Command {

	private int x;
	private int y;
	private int val;
	private int oldval;
	private SudokuGrid sudGrid;
	
	public NoteEntryCommand(SudokuGrid grid, int x_, int y_, int val_)
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
		if (val == 0)
            sudGrid.deleteAllNotes(x, y);
        else sudGrid.setNote(x, y, val);
	}
}
