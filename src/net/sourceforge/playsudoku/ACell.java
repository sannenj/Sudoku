package net.sourceforge.playsudoku;

public abstract class ACell implements Cloneable {
	public abstract int getX();
	public abstract int getY();

	public abstract void reset();	
	public abstract int getPuzzleValue();
	public abstract void setPuzzleValue(int val);
	public abstract int getGridValue();
	public abstract void setGridValue(int val, boolean isGiven);
	
	public abstract void setNote(int value, boolean enable);
	public abstract void removeNotes();
	public abstract boolean getNote(int value);
	
	public abstract boolean isEditable();
	public abstract void setEditable(boolean editable);
	
	public abstract boolean isGiven();
	
   @Override
   public ACell clone() throws CloneNotSupportedException
   {
      return (ACell)super.clone();
   }
}
