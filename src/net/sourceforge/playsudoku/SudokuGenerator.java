/* GridGenerator created on 30.01.2006 */
package net.sourceforge.playsudoku;

import java.util.EmptyStackException;
import java.util.Random;
import java.util.Stack;

import net.sourceforge.playsudoku.GV.NumDistributuon;

public class SudokuGenerator {

    private SudokuGrid grid;
    private Stack<GeneratorMove> st;
    private Random random;
    
    public SudokuGenerator() {
        this(new SudokuGrid(new SquareGridBuilder(4)));
    } 
    
    public SudokuGenerator(SudokuGrid grid) {
        this.grid = grid;
        st = new Stack<GeneratorMove>();
        random = new Random();
    }

    public SudokuGrid getGrid() {
        return this.grid;
    }

    protected GeneratorMove getFirstMove() {
        return getNextMove(-1,0);
    }
    
    protected GeneratorMove getNextMove(int x, int y) {
        do { //No default Fields
            if(x + 1 > 8) { //y mod 9;
                if(y + 1 > 8) {
                    return null;
                }
                x = 0;
                y += 1;
            } else {
                x += 1;
            }
        } while(grid.isDefault(x,y));

        int[] moves = grid.getAvailabeValuesField(x,y, false);
        if(moves.length > 0) {
            return new GeneratorMove(x,y,moves,0);
        }
        return null;
    }
    
    
    //RETURN BOOLEAN ??? TODO
    public boolean solveGrid() {
        st.clear();
        GeneratorMove m = getFirstMove();
        if(m != null) {
            grid.setGridVal(m.getX(),m.getY(),m.getVal(), false);
            st.push(m);
//            System.out.println(m);
//            System.out.println(st.size());
            return solveGridIterative(m);
        }
        return true;
    }

    private boolean solveGridIterative(GeneratorMove m) {
        int x = m.getX(); 
        int y = m.getY();
        if(!grid.isGridValid()) {
            return false;
        }

        GeneratorMove next = grid.getNextMove(x,y);
        while(!grid.isGridSolved()) {

            if(next != null) {
                grid.setGridVal(next.getX(),next.getY(),next.getVal(), false);
                st.push(next);
//                System.out.println(next);
//                System.out.println(st.size());
                y = next.getY();
                x = next.getX();
                
                // Drill deeper
                next = grid.getNextMove(x,y);
            } else {
                try{
                	// Puzzle is not solved and we have now valid moves left for the next step
                	
                	// Roll back the invalid next step.
                    next = st.pop();
                    
                    // We are now back at the current step.
                    if (next.setNextMove() == false) {
                    	// Mark earlier chosen cell value as invalid if the current step is now also depleted.
                        grid.setGridVal(next.getX(),next.getY(),-1);
                        next = null;
                    }
                } catch (EmptyStackException e) {
                    return false;
                } 
            }
        } 
        return true;
    }
    
    public boolean solvePuzzle() {
        
        if(!grid.isGridSolved()) {
            if(!solveGrid()) return false;
        }
        showSolution();
        return true;
    }
    
    private void showSolution() {
        for(int i = 0; i < grid.getDimension(); i++) {
            for(int j = 0; j < grid.getDimension(); j++) {
                grid.setEditable(j,i,true);
                grid.deleteAllNotes(j,i);
                //grid.setPuzzleVal(j,i,-1);
                grid.setPuzzleVal(j,i,grid.getGridVal(j,i));
                grid.setEditable(j,i,false);
            }
        }
    }
    
    public void generateGrid() {
        generateFirstMoves();
        solveGrid();
        for(int i = 0; i < grid.getDimension(); i++) {
            grid.setEditable(0,i,true);
            grid.setDefault(0,i,false);
        }
    }
    
    private void generateFirstMoves() {
        boolean[] b = new boolean[grid.getDimension()];
        for(int i = 0; i < grid.getDimension(); i++) {
            int val;
            do {
                val = random.nextInt(grid.getDimension());
            } while(b[val]);
            grid.setGridVal(0,i,val+1, true);
            b[val] = true;
        }
        b = null;
    }

    public void generatePuzzle(int openFields, NumDistributuon nD) {
        generateGrid();

        int dimension = grid.getDimension();
        int fieldsToFill = Math.min(dimension*dimension, openFields);
        
        int[] count = new int[grid.getDimension()];
        int min = fieldsToFill / grid.getDimension();
        int x = 0, y = 0, sqAdr = 0;

        int k = 0;
        int gridSize = (int) Math.round(Math.sqrt(grid.getDimension()));

        for (int j = 0; j < fieldsToFill;) {
            k++;
            y = random.nextInt(grid.getDimension());
            x = random.nextInt(grid.getDimension());
            if(grid.isDefault(x,y)) continue;
            
            sqAdr = gridSize*(x/gridSize)+(y/gridSize);
            
            switch (nD) {
            case random: break;
            
            case evenlyFilledSquare:
                if(count[sqAdr] >= min &&
                        !allHaveMinCount(count, min)) continue;
                count[sqAdr]++;
                break;
                
            case evenlyDistributedNumbers:
                if(count[grid.getGridVal(x,y)] >= min 
                        && !allHaveMinCount(count, min)) continue;
                count[grid.getGridVal(x,y)]++;
                break;
            }
            grid.setDefault(x,y,true);            
            j++;
        }
        grid.clearNonDefaultCells();
        count = null;
    }
    
    private boolean allHaveMinCount(int[] count,int min) {
        for (int i = 0; i < count.length; i++) {
            if(count[i] < min) return false;
        }
        return true;
    }
}
