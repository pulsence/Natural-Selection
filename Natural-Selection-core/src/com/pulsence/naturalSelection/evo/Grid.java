package com.pulsence.naturalSelection.evo;

public class Grid {
	public int[][] grid;
	
	public Grid(int size) {
		this(size, size);
	}
	
	public Grid(int width, int height) {
		if(width < 1 || height < 1)
			throw new IllegalArgumentException("Width and Height must be great than 0");
		grid = new int [width][height];
	}
	
	public void setBlock(int value, int x, int y) {
		if(!validBlock(x, y))
			throw new IllegalArgumentException("X and Y must be on the grid");
		grid[x][y] = value;
	}
	
	public int getBlock(int x, int y) {
		if(!validBlock(x, y))
			return BlockType.IMPASSABLE_GROUND;
		return 	grid[x][y];
	}
	
	public boolean validBlock(int x, int y) {
		return (x > -1 && x < grid.length) && (y > -1 && y < grid[0].length);
	}
	
	public int getWidth() {
		return grid.length;
	}
	
	public int getHeight() {
		return grid[0].length;
	}
}
