package net.sourceforge.playsudoku;

public class SquareGridBuilder extends GridBuilder {

	private int size;
	private int dimension;
	
	SquareGridBuilder(int size_)
	{
		size = size_;
		dimension = size * size;
	}
	
	@Override
	public ACell[][] CreateGrid() {
		Cell[][] grid = new Cell[dimension][dimension];
        
        for (int y = 0; y < dimension; y++) {
            for (int x = 0; x < dimension; x++) {
                grid[x][y] = new Cell(x, y, dimension);
            }
        }
        
		return grid;
	}

	@Override
	public int getSize() {
		return size;
	}
	
}
