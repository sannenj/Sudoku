package net.sourceforge.playsudoku;

public class CommandHandler {
	private java.util.Stack<Command> undoStack;
	private java.util.Stack<Command> redoStack;
	private Observer observer;
	
	public CommandHandler(Observer obs)
	{
		undoStack = new java.util.Stack<Command>();
		redoStack = new java.util.Stack<Command>();
		
		observer = obs;
	}
	
	public int getUndoStackSize()
	{
		return undoStack.size();
	}

	public int getRedoStackSize()
	{
		return redoStack.size();
	}
	
	public void clearUndoStack()
	{
		undoStack.clear();
		observer.update();
	}

	public void clearRedoStack()
	{
		redoStack.clear();
		observer.update();
	}	
	
	public void reset()
	{
		undoStack.clear();
		redoStack.clear();
		observer.update();
	}
	
	public void doCommand(Command command)
	{
		command.execute();
		
		if (command.isReversible())
		{
			redoStack.clear();
		    undoStack.push(command);
			observer.update();
		}
	}
	
	public void undo()
	{
		if (!undoStack.empty())
		{
			Command command = undoStack.pop();
			
			if (command.isReExecutable())
			{
				redoStack.push(command);
			}
			else
			{
				// If we cannot redo the operation, then anything
				// that we were about to redo is impossible too.
				redoStack.clear();
			}
			
			command.reverseExecute();
		    observer.update();
		}
	}
	
	public void redo()
	{
		if (!redoStack.empty())
		{
			Command command = redoStack.pop();
			command.execute();
			undoStack.push(command);
		    observer.update();
		}		
	}	
}
