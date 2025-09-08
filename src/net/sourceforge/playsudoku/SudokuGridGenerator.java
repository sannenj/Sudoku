/* GridGenerator created on 30.01.2006 */
package net.sourceforge.playsudoku;

import net.sourceforge.playsudoku.GV.NumDistributuon;
import net.sourceforge.playsudoku.generator.*;

public class SudokuGridGenerator {

    private SudokuGrid grid;
    
    public SudokuGridGenerator() {
//        this(new SudokuGrid(new SquareGridBuilder(3), new NineByNineLevelStrategy()));
        this(new SudokuGrid(new SquareGridBuilder(4), new SixteenBySixteenLevelStrategy()));
    } 
    
    public SudokuGridGenerator(SudokuGrid grid) {
        this.grid = grid;
    }

    public SudokuGrid getGrid() {
        return this.grid;
    }
  
    public boolean solvePuzzle() {
        showSolution();
        return true;
    }
    
    private void showSolution() {
        for(int i = 0; i < grid.getDimension(); i++) {
            for(int j = 0; j < grid.getDimension(); j++) {
                grid.setEditable(j,i,true);
                grid.deleteAllNotes(j,i);
                grid.setPuzzleVal(j,i,grid.getGridVal(j,i));
                grid.setEditable(j,i,false);
            }
        }
    }
    
    public void generatePuzzle(int openFields, NumDistributuon nD) {
        int size = grid.getSize();
        int dimension = grid.getDimension();
        
        SudokuGenerator generator = new SudokuGenerator(size); 

        System.out.println("Starting generation for size " + size);

        int[][] solution;
        int[][] puzzle;
		try {
			solution = generator.generatePuzzle();

			System.out.println("Generated Solution:");
	        generator.printPuzzle(solution);

	        System.out.println("Starting clue elimination " + (openFields));
	        
			puzzle = generator.determineClues(openFields, 16);

			System.out.println("Generated Puzzle:");
	        generator.printPuzzle(puzzle);
            System.out.println();
	        
	        for (int y = 0; y < dimension; y++) {
	        	for (int x = 0; x < dimension; x++) {
	        		int puzzle_val = puzzle[x][y];
	        		int solution_val = solution[x][y];
	        		
	                grid.getCell(x, y).setGridValue(solution_val, puzzle_val != -1);
	            }
	        }
	        
		} catch (InterruptedException e) {
	        System.out.println("Exception!");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
        
        grid.clearNonDefaultCells();
    }    
}
