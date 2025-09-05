package net.sourceforge.playsudoku.generator;

public class SolutionCounter {
    private final SudokuSolver solver;
    private final int maxSolutions;
    private int count = 0;

    public SolutionCounter(SudokuSolver solver, int maxSolutions) {
        this.solver = solver;
        this.maxSolutions = maxSolutions;
    }

    public int count() {
        countSolutions(0, 0);
        return count;
    }

    private void countSolutions(int row, int col) {
        if (count >= maxSolutions) return;
        if (row == solver.gridSize) {
            count++;
            return;
        }
        if (col == solver.gridSize) {
            countSolutions(row + 1, 0);
            return;
        }

        int[][] grid = solver.getGrid();
        if (grid[row][col] != -1) {
            countSolutions(row, col + 1);
            return;
        }

        int nr_values = solver.getValues().size();
        for (int value = 0; value < nr_values; value++) {
            if (solver.isValid(row, col, value)) {
                grid[row][col] = value;
                countSolutions(row, col + 1);
                grid[row][col] = -1;
            }
        }
    }
}