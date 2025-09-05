/* Grid created on 30.01.2006 */
package net.sourceforge.playsudoku;

import java.util.ArrayList;
import java.util.Random;

//import net.sourceforge.playsudoku.io.SerGrid;

public class SudokuGrid {
    
    private ACell[][] grid;
    
    private ArrayList<SudokuObserver> observers;
    private boolean hasChanged;
    private int dimension;
    private int size;
    
    private Random r;
    
    public SudokuGrid (GridBuilder gb) {
    	
        grid = gb.CreateGrid();
        size = gb.getSize();
        dimension = grid.length;
        	
        observers = new ArrayList<SudokuObserver>(3);
        hasChanged = false;
        
        this.r = new Random();
    }

    public SudokuGrid (SudokuGrid another) {
    	this.assign(another);
    }
    
    public int getSize() {
    	return size;
    }

    public void assign (SudokuGrid another) {
      
       	this.dimension = another.dimension;
       	this.size = another.size;
        this.hasChanged = another.hasChanged;
        this.r = another.r;

        this.observers = new ArrayList<SudokuObserver>();
        this.observers.addAll(another.observers);
        
        grid = new Cell[dimension][dimension];
        for (int i = 0; i < dimension; i++) 
        {
            for (int j = 0; j < dimension; j++) 
            {
                try {
                	grid[j][i] = another.grid[j][i].clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        } 

        for (int i = 0; i < dimension; i++) 
        {
            for (int j = 0; j < dimension; j++) 
            {
        		setChanged();   
                notifyObservers(grid[j][i]);
            }
        }
    }  
    
    
    
    public ACell getCell(int x, int y) {
        if((y < 0) || (y >= dimension) || (x < 0) || (x >= dimension)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        return grid[x][y];
    }
    
    public int getDimension()
    {
    	return dimension;
    }
    
    private void checkCoords(int x, int y)
    {
        if((y < 0) || (y >= dimension) || (x < 0) || (x >= dimension)) {
            throw new IllegalArgumentException("Invalid cell address. x:" + x + ", y:" + y);
        }
    }
    
    public void setCell(ACell cell) {
    	int x = cell.getX();
    	int y = cell.getY();
    	checkCoords(x, y);
        
        grid[x][y] = cell;

        setChanged();
        notifyObservers(grid[x][y]);      
    }

    public int getGridVal(int x, int y) {
    	checkCoords(x, y);
        
        return grid[x][y].getGridValue();
    }
    
    public int getPuzzleVal(int x, int y) {
    	checkCoords(x, y);
        
        return grid[x][y].getPuzzleValue();
    }
    
    /**
     * 
     * @return gridVal if isDefault = true else puzzleVal
     */
    public int getVal(int x, int y) {
        if(isDefault(x,y)) {
            return getGridVal(x,y);
        } else {
            return getPuzzleVal(x,y);
        }
    }
    
    public boolean getNote(int x, int y, int note) {
    	checkCoords(x, y);
        if(note < 1 || note > dimension) {
            throw new IllegalArgumentException("Illegal note position. Note position must be form 1 to " + dimension + " (inclusive).");
        }
        
        return grid[x][y].getNote(note);
    }
    
    public void setDefault(int x, int y, boolean isDefault)
    {
    	checkCoords(x, y);
        
        grid[x][y].setGridValue(grid[x][y].getGridValue(), isDefault);
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public boolean isDefault(int x, int y) {
    	checkCoords(x, y);
        
        return grid[x][y].isGiven();
    }
    
    public boolean isEditable(int x, int y) {
    	checkCoords(x, y);
        
        return grid[x][y].isEditable();
    }
    
    public void setGridVal(int x, int y, int val, boolean isDefault) { 
        if(!isEditable(x,y)) return;
        if(val < -1 || val > dimension ) {
            throw new IllegalArgumentException("Cell value is illegal.");
        }
        
        grid[x][y].setGridValue(val, isDefault);
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void setPuzzleVal(int x, int y, int val) { 
        if(!isEditable(x,y)) return;
        if(val < -1 || val > dimension ) {
            throw new IllegalArgumentException("Cell value is illegal.");
        }
        
        grid[x][y].setPuzzleValue(val);
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void setEditable(int x, int y, boolean b) {
        grid[x][y].setEditable(b);
    	
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void setNote(int x, int y, int note) {
        if(!isEditable(x,y)) return;
        if(note < 1 || note > dimension) {
            throw new IllegalArgumentException("Illegal Note. Note must be form 0 to " + dimension + " (inclusive).");
        }
        grid[x][y].setNote(note, true);
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void deleteNote(int x, int y, int note) {
        if(!isEditable(x,y)) return;
        if(note < 1 || note > dimension) {
            throw new IllegalArgumentException("Illegal note position. Note position must be form 1 to 9(inclusive).");
        }
        grid[x][y].setNote(note, false);
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void deleteAllNotes(int x, int y) {
        if(!isEditable(x,y)) return;
        
        grid[x][y].removeNotes();
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void resetCell(int x, int y, boolean resetDefaultCellsToo) {
    	if (!grid[x][y].isGiven() || resetDefaultCellsToo)
    	{
            grid[x][y].reset();
    	}
    	
    	setChanged();
        notifyObservers(grid[x][y]);
    }

    public void clearNonDefaultCells() {
        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                resetCell(j,i,false);
            }
        }
    }
    
    public void resetGrid() {
        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                resetCell(j,i,true);
            }
        }
    }

    public boolean isPuzzleSolved() {
        
        return checkGrid();
    }
    
    private boolean checkGrid() {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (getGridVal(j,i) != getPuzzleVal(j,i)) return false;
            }
        }

        return true; 
    }
    
    //OBSERVERS MANEGMANT
    public void addObserver(SudokuObserver so) {
        observers.add(so);
    }
    
    public boolean hasChanged() {
        return hasChanged;
    }
    
    public void setChanged() {
        hasChanged = true;

    }
    
    public void notifyObservers(ACell cell) {
        if(hasChanged) {
            for (SudokuObserver so : observers) {
                so.updateCellChange(cell);
            }
            hasChanged = false;
        }
    }
    
//    public SerGrid getSerGrid() {
//        SerGrid sg = new SerGrid();
//        int countDiff = 0;
//        for(int i = 0; i < 9; i++) {
//            for(int j = 0; j < 9; j++) {
//                sg.grid[j][i] = grid[j][i];
//                if(isDefault(grid[j][i])) {
//                    countDiff++;
//                }
//            }
//        }
//        sg.difficulty = countDiff;
//        return sg; 
//    }
    
//    public void setSerGrid(SerGrid sg) {
//        for(int i = 0; i < 9; i++) {
//            for(int j = 0; j < 9; j++) {
//                grid[j][i] = sg.grid[j][i] ;
//                setChanged();
//                notifyObservers(grid[j][i]);
//            }
//        }
//    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                sb.append("|"); sb.append(getGridVal(j,i));
            }
            sb.append("|\n");
        }
        return sb.toString();
    }   
}
