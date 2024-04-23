package net.sourceforge.playsudoku;

public abstract class Command {
	
	abstract public void execute();

	public void reverseExecute()
	{
		
	}

	public boolean isReversible()
	{
		return false;
	}
	
	public boolean isReExecutable()
	{
		return false;
	}	
}
