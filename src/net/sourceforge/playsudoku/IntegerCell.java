package net.sourceforge.playsudoku;

public class IntegerCell extends Cell {
	public int value;
	public int x;
	public int y;

    public static final int MASK_X =           0xF0000000;
    public static final int MASK_Y =           0x0F000000;
    public static final int MASK_PUZZLE_VAL =  0x000000F0;
    public static final int MASK_GRID_VAL =    0x0000000F;
    public static final int MASK_NOTES =       0x0001FF00;
    public static final int MASK_IS_DEFAULT =  0x00100000;
    public static final int MASK_IS_EDITABLE = 0x00200000;
    public static final int MASK_IS_HINT =     0x00400000;
    public static final int MASK_IS_DELETED =  0x00800000;
	
	IntegerCell(int value_, int x_, int y_)
	{
		if (value_ == -1)
		{
			value = MASK_IS_DELETED;			
		}
		else
		{
			value = value_;
		}
        value += x_ << 28;
        value += y_ << 24;	
		value += MASK_IS_EDITABLE;
	}
	
	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return value;
	}
	
	@Override
	public void setValue(int val) {
		// TODO Auto-generated method stub
		value = val;
		
		if (value == -1)
		{
			value += MASK_IS_DELETED;
			value &= (MASK_X + MASK_Y + MASK_IS_EDITABLE);
		}
	}
	
	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return (value & SudokuGrid.MASK_X) >>> 28;
	}
	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return (value & SudokuGrid.MASK_Y) >>> 24;
	}
}