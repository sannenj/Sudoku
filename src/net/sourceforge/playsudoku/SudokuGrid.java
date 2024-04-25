/* Grid created on 30.01.2006 */
package net.sourceforge.playsudoku;

import java.util.ArrayList;
import java.util.Random;

import net.sourceforge.playsudoku.io.SerGrid;

public class SudokuGrid {
   
    private Cell[][] grid;
    
    private ArrayList<SudokuObserver> observers;
    private boolean hasChanged;
    private int dimension;
    private int[] vertical;
    private int[] horizontal;
    private int[] square;
    
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
    
    public int getRealGridVal(int x, int y) {
    	checkCoords(x, y);
        
        return grid[x][y].getValue();
    }
    
    public static int getX(int cell) {
        return (cell & IntegerCell.MASK_X) >>> 28;
    }
    
    public static int getY(int cell) {
        return (cell & IntegerCell.MASK_Y) >>> 24;
    }
    
    public int getGridVal(int x, int y) {
    	checkCoords(x, y);
        
        return ((grid[x][y].getValue() & IntegerCell.MASK_IS_DELETED) != 0) ? -1 : grid[x][y].getValue() & IntegerCell.MASK_GRID_VAL;
    }
    
    // Used from GuiCell
    public static int getGridVal(int SudokuRealGridVal) {
        
        return ((SudokuRealGridVal & IntegerCell.MASK_IS_DELETED) != 0) ? -1 : (SudokuRealGridVal & IntegerCell.MASK_GRID_VAL);
    }
    
    public int getPuzzleVal(int x, int y) {
    	checkCoords(x, y);
        
        return ((grid[x][y].getValue() & IntegerCell.MASK_IS_DELETED) != 0) ? -1 : ((grid[x][y].getValue() & IntegerCell.MASK_PUZZLE_VAL) >>> 4);
    }
    
    // Used from GuiCell
    public static int getPuzzleVal(int SudokuRealGridVal) {
        
        return ((SudokuRealGridVal & IntegerCell.MASK_IS_DELETED) != 0) ? -1 : ((SudokuRealGridVal & IntegerCell.MASK_PUZZLE_VAL) >>> 4);
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
    
//    public static int getVal(int realGridVal) {
//        if(isDefault(realGridVal)) {
//            return getGridVal(realGridVal);
//        } else {
//            return getPuzzleVal(realGridVal);
//        }
//    }
    
    public boolean getNote(int x, int y, int note) {
    	checkCoords(x, y);
        if(note < 1 || note > dimension) {
            throw new IllegalArgumentException("Illegal note position. Note position must be form 1 to " + dimension + " (inclusive).");
        }
        
        note = grid[x][y].getValue() & (1 << (note-1+8));
        if(note != 0) {
            return true;
        }
        return false;
    }
    
    public static boolean getNote(int realGridVal, int note) {
        note = realGridVal & (1 << (note-1+8));
        if(note != 0) {
            return true;
        }
        return false;
    }
    
    public boolean isDefault(int x, int y) {
    	checkCoords(x, y);
        
        if((grid[x][y].getValue() & IntegerCell.MASK_IS_DEFAULT) == IntegerCell.MASK_IS_DEFAULT) {
            return true;
        }
        return false;
    }
    
    public static boolean isDefault(int realGridVal) {
        if((realGridVal & IntegerCell.MASK_IS_DEFAULT) == IntegerCell.MASK_IS_DEFAULT) {
            return true;
        }
        return false;
    }
    
    public boolean isEditable(int x, int y) {
    	checkCoords(x, y);
        
        if((grid[x][y].getValue() & IntegerCell.MASK_IS_EDITABLE) == IntegerCell.MASK_IS_EDITABLE) {
            return true;
        }
        return false;
    }
    
    public static boolean isEditable(int realGridVal) {
        if((realGridVal & IntegerCell.MASK_IS_EDITABLE) == IntegerCell.MASK_IS_EDITABLE) {
            return true;
        }
        return false;
    }
    
    public boolean isHint(int x, int y) {
    	checkCoords(x, y);
        
        if((grid[x][y].getValue() & IntegerCell.MASK_IS_HINT) == IntegerCell.MASK_IS_HINT) {
            return true;
        }
        return false;
    }
    
    public static boolean isHint(int realGridVal) {
        
        if((realGridVal & IntegerCell.MASK_IS_HINT) == IntegerCell.MASK_IS_HINT) {
            return true;
        }
        return false;
    }
    
    //OBSERVERS SET CHANGE METHODS BEGIN:
    public void setRealGridVal(int realGridVal) {
        int x = SudokuGrid.getX(realGridVal);
        int y = SudokuGrid.getY(realGridVal);
        grid[x][y].setValue(realGridVal);
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void setGridVal(int x, int y, int val) { 
        if(!isEditable(x,y)) return;
        if(val < -1 || val > dimension ) {
            throw new IllegalArgumentException("Cell value is illegal.");
        }
        
        int tmp = grid[x][y].getValue();
        tmp &= ~IntegerCell.MASK_GRID_VAL;
        
        if (val == -1)
        {
        	tmp |= IntegerCell.MASK_IS_DELETED;
        	tmp &= ~IntegerCell.MASK_GRID_VAL;
        }
        else
        {
        	tmp |= val;
        	tmp &= ~IntegerCell.MASK_IS_DELETED;        
        }
        grid[x][y].setValue(tmp);
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void setPuzzleVal(int x, int y, int val) { 
        if(!isEditable(x,y)) return;
        if(val < -1 || val > dimension ) {
            throw new IllegalArgumentException("Cell value is illegal.");
        }
        
        int tmp = grid[x][y].getValue();
        tmp &= ~IntegerCell.MASK_PUZZLE_VAL;
        if (val == -1)
        {
        	tmp |= IntegerCell.MASK_IS_DELETED;        
        }
        else
        {
        	tmp |= (val << 4);
        	tmp &= ~IntegerCell.MASK_IS_DELETED;        
        }
        
        grid[x][y].setValue(tmp);
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void setDefault(int x, int y, boolean b) {
        if(!isEditable(x,y)) return;
        
        int tmp = grid[x][y].getValue();        
        if(b) {
            tmp |= IntegerCell.MASK_IS_DEFAULT;
        } else {
            tmp &= ~IntegerCell.MASK_IS_DEFAULT;
        }
        grid[x][y].setValue(tmp);
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void setEditable(int x, int y, boolean b) {
    	int tmp = grid[x][y].getValue();
    	if(b) {
            tmp |= IntegerCell.MASK_IS_EDITABLE;
        } else {
            tmp &= ~IntegerCell.MASK_IS_EDITABLE;
        }
    	grid[x][y].setValue(tmp);
    	
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void setHint(int x, int y, boolean b)  {
        if(!isEditable(x,y)) return;
        
        int tmp = grid[x][y].getValue();
        if(b) {
            tmp |= IntegerCell.MASK_IS_HINT;
        } else {
            tmp &= ~IntegerCell.MASK_IS_HINT;
        }
        grid[x][y].setValue(tmp);
        
        setChanged();
        notifyObservers(grid[x][y]);
    }

    public void setNote(int x, int y, int note) {
        if(!isEditable(x,y)) return;
        if(note < 1 || note > dimension) {
            throw new IllegalArgumentException("Illegal Note. Note must be form 0 to " + dimension + " (inclusive).");
        }
        int tmp = grid[x][y].getValue();
        tmp ^= (1 << (note-1+8));
        grid[x][y].setValue(tmp);
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
//    public void deleteNote(int x, int y, int note) {
//        if(!isEditable(x,y)) return;
//        if(note < 1 || note > dimension) {
//            throw new IllegalArgumentException("Illegal note position. Note position must be form 1 to " + dimension + " (inclusive).");
//        }
//        int tmp = grid[x][y].getValue();
//        tmp &= ~(1 << (note-1+8));
//        grid[x][y].setValue(tmp);
//        
//        setChanged();
//        notifyObservers(grid[x][y]);
//    }
    
    public void deleteAllNotes(int x, int y) {
        if(!isEditable(x,y)) return;
        
        int tmp = grid[x][y].getValue();
        tmp &= ~IntegerCell.MASK_NOTES;
        grid[x][y].setValue(tmp);
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void resetCell(int x, int y, boolean resetDefaultCellsToo) {
        
    	int tmp = grid[x][y].getValue();
    	if(resetDefaultCellsToo) {
            tmp &= IntegerCell.MASK_X + IntegerCell.MASK_Y;
            tmp |= IntegerCell.MASK_IS_DELETED;
        } else {
            tmp &= ~(IntegerCell.MASK_NOTES + IntegerCell.MASK_PUZZLE_VAL);
            if ((tmp & IntegerCell.MASK_IS_DEFAULT) == 0)
            {
            	tmp |= IntegerCell.MASK_IS_DELETED;
            }
        }
        tmp |= IntegerCell.MASK_IS_EDITABLE;
        grid[x][y].setValue(tmp);
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    //END OBSERVERS SET CHANGE METHODS

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
            vertical[i] = horizontal[i] = square[i] = 0;
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
                int k1 = 0, k2 = 0;
                if(trueVal1 != -1) {
                    k1 =  1 << (trueVal1);
                }
                if(trueVal2 != -1) {
                    k2 =  1 << (trueVal2);
                } 
                
                //Square adr in n
                m = j / gridSize;
                n = i / gridSize;
                n = gridSize * m + n;
                

                if(((vertical[i] & k2) > 0 || 
                   (horizontal[i] & k1) > 0 || 
                   (square[n] & k1) > 0) &&
                   !usedForFindAvMoves) {
                    return false;
                } else {
                    vertical[i] |= k2; square[n] |= k1; horizontal[i] |= k1; 
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
            
            if((vertical[x] & (1 << i)) == 0 && 
               (horizontal[y] & (1 << i)) == 0 && 
               (square[n] & (1 << i)) == 0) {
                b[i] = true;
                k++;
            }
        }

        int[] result = new int[k];
        
        //SORT RESULT
        k = 0;
        for(int i = 0; i < dimension; i++) {
            if(b[i]) {
                result[k] = i;
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
            if((x+1) >= dimension) { //y mod dimension;
                if((y+1) >= dimension) {
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
            return new GeneratorMove(x,y,moves);
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
    
    public void notifyObservers(Cell cell) {
        if(hasChanged) {
            for (SudokuObserver so : observers) {
                so.updateCellChange(cell);
            }
            hasChanged = false;
        }
    }
    
    public SerGrid getSerGrid() {
        SerGrid sg = new SerGrid();
        int countDiff = 0;
        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                sg.grid[j][i] = grid[j][i].getValue();
                if(isDefault(grid[j][i].getValue())) {
                    countDiff++;
                }
            }
        }
        sg.difficulty = countDiff;
        return sg; 
    }
    
    public void setSerGrid(SerGrid sg) {
        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                grid[j][i].setValue(sg.grid[j][i]);
                setChanged();
                notifyObservers(grid[j][i]);
            }
        }
    }
    
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
