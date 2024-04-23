/* Grid created on 30.01.2006 */
package net.sourceforge.playsudoku;

import java.util.ArrayList;
import java.util.Random;

//import net.sourceforge.playsudoku.io.SerGrid;

public class SudokuGrid {
    
    private ACell[][] grid;
    
    private ArrayList<SudokuObserver> observers;
    private boolean hasChanged;

    private boolean[][] vertical;
    private boolean[][] horizontal;
    private boolean[][] square;
    
    private Random r;
    
    public SudokuGrid () {
        grid = new Cell[9][9];
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
            	grid[j][i] = new Cell(j, i, 10);
            }
        }
        
        observers = new ArrayList<SudokuObserver>(4);
        hasChanged = false;
        
        vertical = new boolean[9][9];
        horizontal = new boolean[9][9];
        square = new boolean[9][9];
        
        this.r = new Random();
    }

    public SudokuGrid (SudokuGrid another) {
    	this.assign(another);
    }
    
    public void assign (SudokuGrid another) {
      
        observers = new ArrayList<SudokuObserver>();
       	observers.addAll(another.observers);
        
        hasChanged = another.hasChanged;
        
        vertical = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
            	vertical[i][j] = another.vertical[i][j];
            }
        }        

        horizontal = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
            	horizontal[i][j] = another.horizontal[i][j];
            }
        }        

        square = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
            	square[i][j] = another.square[i][j];
            }
        }        

        this.r = another.r;
        
        grid = new Cell[9][9];
        for (int i = 0; i < 9; i++) 
        {
            for (int j = 0; j < 9; j++) 
            {
            	if (grid[j][i] != another.grid[j][i]) setChanged();
                try {
					grid[j][i] = another.grid[j][i].clone();
	                notifyObservers(grid[j][i]);
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }        
    }  
    
    
    
    public ACell getCell(int x, int y) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        return grid[x][y];
    }

    public ACell setCell(ACell cell) {
    	int x = cell.getX();
    	int y = cell.getY();
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        return grid[x][y] = cell;
    }

    public int getGridVal(int x, int y) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        return grid[x][y].getGridValue();
    }
    
    public int getPuzzleVal(int x, int y) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
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
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        if(note < 1 || note > 9) {
            throw new IllegalArgumentException("Illegal note position. Note position must be form 1 to 9(inclusive).");
        }
        
        return grid[x][y].getNote(note);
    }
    
    public void setDefault(int x, int y, boolean isDefault)
    {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        grid[x][y].setGridValue(grid[x][y].getGridValue(), isDefault);
    }
    
    public boolean isDefault(int x, int y) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        return grid[x][y].isGiven();
    }
    
    public boolean isEditable(int x, int y) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        return grid[x][y].isEditable();
    }
    
    public void setGridVal(int x, int y, int val, boolean isDefault) { 
        if(!isEditable(x,y)) return;
        if(val < 0 || val > 9 ) {
            throw new IllegalArgumentException("Cell value is illegal.");
        }
        
        grid[x][y].setGridValue(val, isDefault);
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void setPuzzleVal(int x, int y, int val) { 
        if(!isEditable(x,y)) return;
        if(val < 0 || val > 9 ) {
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
        if(note < 1 || note > 9) {
            throw new IllegalArgumentException("Illegal Note. Note must be form 1 to 9(inclusive).");
        }
        grid[x][y].setNote(note, true);
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void deleteNote(int x, int y, int note) {
        if(!isEditable(x,y)) return;
        if(note < 1 || note > 9) {
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
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                resetCell(j,i,false);
            }
        }
    }
    
    public void resetGrid() {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                resetCell(j,i,true);
            }
        }
    }
    
    public boolean isGridValid() {
        return checkGrid(false, false, false);
    }
    
    public boolean isGridSolved() {
        return checkGrid(true, false, false);
    }

    public boolean isPuzzleSolved() {
        
        return checkGrid(true, false, true);
    }
    
    public boolean isPuzzleValid() {
        return checkGrid(false, false, true);
    }
    
    private boolean checkGrid(boolean toBeSolvedToo, boolean usedForFindAvMoves, boolean checkPuzzle) {
        int m = 0 , n = 0;
        
        //reset Arrays
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
            	vertical[i][j] = horizontal[i][j] = square[i][j] = false;
            }
        }
        
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                int trueVal1 = 0;
                int trueVal2 = 0;
                if(checkPuzzle) {
                    trueVal1 = getVal(j,i);
                    trueVal2 = getVal(i,j);
                } else {
                    trueVal1 = getGridVal(j,i);
                    trueVal2 = getGridVal(i,j);
                }

                //Square adr in n
                m = j / 3;
                n = i / 3;
                n = 3*m + n;
                
                int k1 = 0, k2 = 0;
                if(trueVal1 != 0) {
                    k1 = trueVal1 - 1;
                    
                    if((horizontal[i][k1] || square[n][k1]) &&
                       !usedForFindAvMoves) 
                    {
                        return false;
                    }

                    square[n][k1] = true; 
                    horizontal[i][k1] = true; 
                }
                
                if(trueVal2 != 0) {
                    k2 =  trueVal2 - 1;
                    
                    if(vertical[i][k2] &&
                       !usedForFindAvMoves) 
                    {
                        return false;
                    }
                    
                    vertical[i][k2] = true; 
                } 
            }
        }
        
        if(toBeSolvedToo) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
	                if(vertical[i][j] && 
	                   horizontal[i][j] && 
	                   square[i][j]) {
	                    continue;
	                } else {
	                    return false;
	                }
                }
            }
        } 
        return true; 
    }
    
    public int[] getAvailabeValuesField(int x, int y, boolean sortIt) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        checkGrid(false, true, false);
        
        int n = 3*(x/3)+(y/3);

        int k = 0;
        boolean[] b = new boolean[9];
        
        for(int i = 0; i < 9; i++) {
            
            if( (vertical[x][i] == false) && 
                (horizontal[y][i] == false) && 
                (square[n][i] == false) ) 
            {
                b[i] = true;
                k++;
            }
        }

        int[] result = new int[k];
        
        //SORT RESULT
        k = 0;
        for(int i = 0; i < 9; i++) {
            if(b[i]) {
                result[k] = i+1;
                k++;
            }
        }
        b = null;
        if(!sortIt) {
            randomizeArray(result);
        }
        return result;
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
    
    private void randomizeArray(int[] a) {
        int tmp = 0; int rV = 0;
        for(int i = 0; i < a.length; i++) {
            rV = r.nextInt(a.length-i);
            tmp = a[a.length-i-1];
            a[a.length-i-1] = a[rV];
            a[rV] = tmp;
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                sb.append("|"); sb.append(getGridVal(j,i));
            }
            sb.append("|\n");
        }
        return sb.toString();
    }   
}
