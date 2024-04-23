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

    //RETURN BOOLEAN ??? TODO
    public boolean solveGrid() {
        st.clear();
        GeneratorMove m = grid.getFirstMove();
        if(m != null) {
            grid.setGridVal(m.getX(),m.getY(),m.getVal());
            st.push(m);
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
        int count = 0;
        
        while(!grid.isGridSolved()) {
        	if (count++ > 10000000) {
        		return false;
        	}
            GeneratorMove next = grid.getNextMove(x,y);
            if(next != null) {
                grid.setGridVal(next.getX(),next.getY(),next.getVal());
                st.push(next);
                y = next.getY();
                x = next.getX();
            } else {
                try{
                    next = st.pop();
                    while(next.setNextMove() == false) {
                        grid.setGridVal(next.getX(),next.getY(),0);
                        next = st.pop(); 
                    }
                    grid.setGridVal(next.getX(),next.getY(),next.getVal());
                    st.push(next);
                    y = next.getY();
                    x = next.getX();
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
                grid.setPuzzleVal(j,i,0);
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
            grid.setGridVal(0,i,val);
            grid.setDefault(0,i,true);
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
                if(count[grid.getGridVal(x,y)-1] >= min 
                        && !allHaveMinCount(count, min)) continue;
                count[grid.getGridVal(x,y)-1]++;
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
