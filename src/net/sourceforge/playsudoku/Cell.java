package net.sourceforge.playsudoku;

public class Cell extends ACell {
	private int puzzleValue;
	private int gridValue;
	private int x;
	private int y;
	private boolean[] notes;
	private boolean editable;
	private boolean given;
	private int range;
	
	Cell(int x_, int y_, int range_)
	{
        x = x_;
        y = y_;
		range = range_;
        
        reset();
	}
	
	@Override
	public int getX() {
		return x;
	}
	@Override
	public int getY() {
		return y;
	}

	@Override
	public void reset()
	{
		removeNotes();
		
		puzzleValue = 0;
		gridValue = 0;
        editable = true;
        given = false;
	}
	
	@Override
	public int getPuzzleValue() {
		return puzzleValue;
	}

	@Override
	public void setPuzzleValue(int val) {
		puzzleValue = val;
	}

	@Override
	public int getGridValue() {
		return gridValue;
	}

	@Override
	public void setGridValue(int val, boolean isGiven) {
		gridValue = val;
		given = isGiven;
		if (isGiven)
		{
			editable = false;
		}
	}

	@Override
	public void setNote(int value, boolean enable) {
		notes[value] = enable;
	}

	@Override
	public void removeNotes()
	{
		notes = new boolean[range];
	}
	
	@Override
	public boolean getNote(int value) {
		return notes[value];
	}

	@Override
	public boolean isEditable() {
		return editable;
	}

	@Override
	public void setEditable(boolean editable_) {
		editable = editable_;
	}
	
	@Override
	public boolean isGiven() {
		return given;
	}
}