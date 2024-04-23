/* Grid created on 30.01.2006 */
package net.sourceforge.playsudoku;

import java.util.ArrayList;
import java.util.Random;

//import net.sourceforge.playsudoku.io.SerGrid;

public class SudokuGrid {
    
    private ACell[][] grid;
    public static final int MASK_IS_DELETED =  0x00800000;
    
    private ArrayList<SudokuObserver> observers;
    private boolean hasChanged;
    private int dimension;
    private boolean[][] vertical;
    private boolean[][] horizontal;
    private boolean[][] square;
    
    private Random r;
    
    public SudokuGrid (GridBuilder gb) {
    	
        grid = gb.CreateGrid();
        dimension = grid.length;
        	
        observers = new ArrayList<SudokuObserver>(4);
        hasChanged = false;
        
        vertical = new int[dimension];
        horizontal = new int[dimension];
        square = new int[dimension];
        
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
                try {
					grid[j][i] = another.grid[j][i].clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        } 

        for (int i = 0; i < 9; i++) 
        {
            for (int j = 0; j < 9; j++) 
            {
        		setChanged();   
                notifyObservers(grid[j][i]);
            }
        }
    }  
    
    
    
    public ACell getCell(int x, int y) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
    public int getDimension()
    {
    	return dimension;
    }
    
    private void checkCoords(int x, int y)
    {
        if((y < 0) || (y >= dimension) || (x < 0) || (x >= dimension)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
    }
    
    public int getRealGridVal(int x, int y) {
    	checkCoords(x, y);
        
        return grid[x][y].getValue();
    }

    public void setCell(ACell cell) {
    	int x = cell.getX();
    	int y = cell.getY();
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
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
        else
        {
        	tmp |= (val << 4);
        	tmp &= ~MASK_IS_DELETED;        
        }
        
        grid[x][y].setValue(tmp);
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void setEditable(int x, int y, boolean b) {
        grid[x][y].setEditable(b);
    	grid[x][y].setValue(tmp);
    	
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
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < 9; j++) {
            	vertical[i][j] = horizontal[i][j] = square[i][j] = false;
            }
        }
        
        int gridSize = (int) Math.round(Math.sqrt(dimension));
        
        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                int trueVal1 = -1;
                int trueVal2 = -1;
                if(checkPuzzle) {
                    trueVal1 = getVal(j,i);
                    trueVal2 = getVal(i,j);
                } else {
                    trueVal1 = getGridVal(j,i);
                    trueVal2 = getGridVal(i,j);
                }

                //Square adr in n
                m = j / gridSize;
                n = i / gridSize;
                n = gridSize * m + n;
                
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
        	int checkmask = 0;
        	for (int k = 0; k < dimension; k++) {
               checkmask |= 1 << k;
        	}
        	
            for (int k = 0; k < dimension; k++) {
                if(vertical[k] == checkmask && 
                   horizontal[k] == checkmask && 
                   square[k] == checkmask) {
                    continue;
                } else {
                    return false;
                }
            }
        } 
        return true; 
    }
    
    public int[] getAvailabeValuesField(int x, int y, boolean sortIt) {
        checkCoords(x, y);
        
        checkGrid(false, true, false);
        
        int gridSize = (int) Math.round(Math.sqrt(dimension));

        int n = gridSize*(x/gridSize)+(y/gridSize);

        int k = 0;
        boolean[] b = new boolean[dimension];
        
        for(int i = 0; i < dimension; i++) {
            
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
        for(int i = 0; i < dimension; i++) {
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
    
    protected GeneratorMove getFirstMove() {
        return getNextMove(-1,0);
    }
    
    protected GeneratorMove getNextMove(int x, int y) {
        do { //No default Fields
            if(x + 1 >= dimension) { //y mod dimension;
                if(y + 1 >= dimension) {
                    return null;
                }
                x = 0;
                y += 1;
            } else {
                x += 1;
            }
        } while(isDefault(x,y));

        int[] moves = getAvailabeValuesField(x,y, false);
        if(moves.length > 0) {
            return new GeneratorMove(x,y,moves,0);
        }
        return null;
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
        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                sb.append("|"); sb.append(getGridVal(j,i));
            }
            sb.append("|\n");
        }
        return sb.toString();
    }   
}
