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
	public Cell[][] CreateGrid() {
		IntegerCell[][] grid = new IntegerCell[dimension][dimension];
        int val = -1;
        
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                grid[j][i] = new IntegerCell(val, j, i);
            }
        }
        
		return grid;
	}

}
