package net.sourceforge.playsudoku.generator;

import java.util.*;

public class SudokuSolver {
    public final int blockSize;
    public final int gridSize;
    private final int[][] grid;
    private final List<Integer> values;

    public SudokuSolver(int blockSize) {
        this.blockSize = blockSize;
        this.gridSize = blockSize * blockSize;
        this.grid = new int[gridSize][gridSize];
        
        // Init grid to -1 to set all values as invalid. (0 is a valid number) 
		for (int y = 0; y < gridSize; y++) {
		    for (int x = 0; x < gridSize; x++) {
		    	this.grid[y][x] = -1;
		    }
		}
        
        this.values = generateValues(gridSize);
        //this.symbols = generateSymbols(gridSize);
    }

    public int getGridSize() {
        return gridSize;
    }

    public int[][] getGrid() {
        return grid;
    }

    public List<Integer> getValues() {
        return values;
    }

    public boolean solve() {
        return solveRecursive(0, 0);
    }

    private boolean solveRecursive(int row, int col) {
        if (row == gridSize) return true;
        if (col == gridSize) return solveRecursive(row + 1, 0);
        if (grid[row][col] != -1) return solveRecursive(row, col + 1);

        List<Integer> shuffledValues = new ArrayList<>(values);
        Collections.shuffle(shuffledValues);
        for (int value : shuffledValues) {
            if (isValid(row, col, value)) {
                grid[row][col] = value;
                if (solveRecursive(row, col + 1)) return true;
                grid[row][col] = -1;
            }
        }
        return false;
    }

    public boolean isValid(int row, int col, int value) {
        for (int i = 0; i < gridSize; i++) {
            if ((value == grid[row][i]) || (value == grid[i][col])) return false;
        }

        int startRow = (row / blockSize) * blockSize;
        int startCol = (col / blockSize) * blockSize;

        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                if ((value == grid[startRow + i][startCol + j])) return false;
            }
        }

        return true;
    }

    static public List<String> generateSymbols(int size) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (size <= 9) list.add(String.valueOf(i + 1));
            else if (i <= 9) {
            	list.add(String.valueOf(i));
            }
            else
            {
            	list.add(String.valueOf((char) ('A' + i - 10)));
            }
        }
        return list;
    }

    static public List<Integer> generateValues(int size) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
           	list.add(i);
        }
        return list;
    }

    public void setGrid(int[][] source) {
        for (int i = 0; i < gridSize; i++) {
            System.arraycopy(source[i], 0, grid[i], 0, gridSize);
        }
    }
}
