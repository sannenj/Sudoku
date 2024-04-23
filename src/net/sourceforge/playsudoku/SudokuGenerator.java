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
        this(new SudokuGrid());
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

        while(!grid.isGridSolved()) {

            GeneratorMove next = getNextMove(x,y);
            if(next != null) {
                grid.setGridVal(next.getX(),next.getY(),next.getVal(), false);
                st.push(next);
                y = next.getY();
                x = next.getX();
            } else {
                try{
                    next = st.pop();
                    while(next.setNextMove() == false) {
                        grid.setGridVal(next.getX(),next.getY(),0, false);
                        next = st.pop(); 
                    }
                    grid.setGridVal(next.getX(),next.getY(),next.getVal(), false);
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
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                grid.setEditable(j,i,true);
                grid.deleteAllNotes(j,i);
                grid.setPuzzleVal(j,i,0);
                grid.setPuzzleVal(j,i,grid.getGridVal(j,i));
                grid.setEditable(j,i,false);
            }
        }
    }
    
    public void generateGrid() {
        generateFirst9Moves();
        solveGrid();
        for(int i = 0; i < 9; i++) {
            grid.setEditable(0,i,true);
            grid.setDefault(0,i,false);
        }
    }
    
    private void generateFirst9Moves() {
        boolean[] b = new boolean[9];
        for(int i = 0; i < 9; i++) {
            int val;
            do {
                val = random.nextInt(9);
            } while(b[val]);
            grid.setGridVal(0,i,val+1, true);
            b[val] = true;
        }
        b = null;
    }

    public void generatePuzzle(int openFields, NumDistributuon nD) {
        generateGrid();

        int[] count = new int[9];
        int min = openFields / 9;
        int x = 0, y = 0, sqAdr = 0;

        int k = 0;
        for (int j = 0; j < openFields;) {
            k++;
            y = random.nextInt(9);
            x = random.nextInt(9);
            if(grid.isDefault(x,y)) continue;
            
            sqAdr = 3*(x/3)+(y/3);
            
            switch (nD) {
            case random: break;
            
            case evenlyFilled3x3Square3:
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
