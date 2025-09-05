package net.sourceforge.playsudoku.generator;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class SudokuGenerator {
    private final SudokuSolver solver;
    
    public SudokuGenerator(int blockSize) {
        this.solver = new SudokuSolver(blockSize);
    }

    public int[][] generatePuzzle() throws InterruptedException {
        solver.solve();
        return solver.getGrid();
    }
    
    public int[][] determineClues(int clues, int threadCount) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicReference<int[][]> result = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < threadCount; i++) {
            int[][] fullGrid = deepCopy(solver.getGrid());

            executor.submit(() -> {

                List<int[]> cells = new ArrayList<>();
                for (int r = 0; r < solver.getGridSize(); r++)
                    for (int c = 0; c < solver.getGridSize(); c++)
                        cells.add(new int[]{r, c});

                Collections.shuffle(cells, new Random()); // unique order per thread
                int[][] puzzle = deepCopy(fullGrid);

                for (int[] cell : cells) {
                    if (latch.getCount() == 0) return; // another thread won

                    int r = cell[0], c = cell[1];
                    int backup = puzzle[r][c];
                    puzzle[r][c] = -1;

                    SudokuSolver testSolver = new SudokuSolver(solver.blockSize);
                    testSolver.setGrid(deepCopy(puzzle));
                    int solutions = new SolutionCounter(testSolver, 2).count();

                    int puzzleClues = countClues(puzzle);
                    //System.out.println(" [" + Thread.currentThread().getName() + "] clues: " + puzzleClues);                    
                    
                    if (solutions != 1) puzzle[r][c] = backup;
                    if (puzzleClues <= clues) {
                        if (result.compareAndSet(null, deepCopy(puzzle))) {
                            latch.countDown(); // signal success
                            //System.out.println(" [" + Thread.currentThread().getName() + "] won! " + puzzleClues);                    
                        }
                        return;
                    }
                }
            });
        }

        latch.await(); // wait for first success
        executor.shutdownNow(); // stop other threads
        return result.get();
    }
    
    private int countClues(int[][] grid) {
        int count = 0;
        for (int[] row : grid)
            for (int val : row)
                if (val != -1) count++;
        return count;
    }

    private int[][] deepCopy(int[][] source) {
        int size = source.length;
        int[][] copy = new int[size][size];
        for (int i = 0; i < size; i++)
            System.arraycopy(source[i], 0, copy[i], 0, size);
        return copy;
    }

    public void printPuzzle(int[][] puzzle) {
        for (int[] row : puzzle) {
            for (int val : row) {
            	if (val == -1) {
            		System.out.printf("  .");		
            	}
            	else System.out.printf(" %2d", val);
            }
            System.out.println();
        }
        System.out.println();
        System.out.println(solver.getValues());
    }
}