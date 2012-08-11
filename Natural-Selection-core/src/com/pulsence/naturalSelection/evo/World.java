package com.pulsence.naturalSelection.evo;

import java.util.ArrayList;
import java.util.Random;

public class World {
	public Grid grid;
	public ArrayList<Animal> animals;
	public long age = 0;
	public int maxPopulation = 10;
	
	private static int INITIAL_SIZE = 10;
	private static int INITIAL_ANIMALS = 10;
	public static Random random;
	
	public int width;
	public int height;
	
	public World() {
		this(INITIAL_SIZE, INITIAL_ANIMALS);
	}
	
	/**
	 * 
	 * @param size
	 * @param initialAnimals
	 */
	public World(int size, int initialAnimals) {
		this(size, size, initialAnimals);
	}
	
	/**
	 * 
	 * @param width
	 * @param height
	 * @param initialAnimals
	 */
	public World(int width, int height, int initialAnimals) {
		grid = createRandomGrid(width, height);
		animals = new ArrayList<Animal>();
		for(int i = 0; i < initialAnimals; i++) {
			animals.add(createRandomAnimal(width, height, grid));
		}
		
		this.width = grid.getWidth();
		this.height = grid.getHeight();
	}
	
	public void step() {
		age++;
		for(int i = 0; i < animals.size(); i++) {
			animals.get(i).step(this);
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public static Grid createRandomGrid(int width, int height) {
		setUpRandom();
		
		Grid iGrid = new Grid(width, height);
		
//		for(int x = 0; x < width; x++) {
//			for(int y = 0; y < height; y++) {
//				Block block = new Block();
//				block.blockType = random.nextInt(4);
//				block.animal = null;
//				iGrid.setBlock(block, x, y);
//			}
//		}
		
		// Create all ground
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				Block block = new Block();
				block.blockType = BlockType.PASSABLE_GROUND;
				block.animal = null;
				iGrid.setBlock(block, x, y);
			}
		}
		
		int centerX;
		int centerY;
		int radius;
		
		// Create mountains
		int mountains = random.nextInt(10);
		for(int i = 0; i < mountains; i++) {
			centerX = random.nextInt(width);
			centerY = random.nextInt(height);
			radius = random.nextInt(Math.min(width, height) / 2);
			loadGrid(iGrid, centerX, centerY, radius, BlockType.IMPASSABLE_GROUND);
		}
		
		// Create lakes
		int lakes = random.nextInt(10);
		for(int i = 0; i < lakes; i++) {
			centerX = random.nextInt(width);
			centerY = random.nextInt(height);
			radius = random.nextInt(Math.min(width, height) / 2);
			loadGrid(iGrid, centerX, centerY, radius, BlockType.WATER);
		}
		
		// Create plains
		int plains = random.nextInt(10);
		for(int i = 0; i < plains; i++) {
			centerX = random.nextInt(width);
			centerY = random.nextInt(height);
			radius = random.nextInt(Math.min(width, height) / 2);
			loadGrid(iGrid, centerX, centerY, radius, BlockType.VEGETATION);
		}
		
		return iGrid;
	}
	
	private static void loadGrid(Grid grid, int centerX, int centerY, int radius, int blockType) {
		for(int x = centerX - radius; x < centerX + radius; x++) {
			for(int y = centerY - radius; y < centerY + radius; y++) {
				if(grid.validBlock(x, y) && random.nextInt(5) > 2) {
					Block block = new Block();
					block.blockType = blockType;
					block.animal = null;
					grid.setBlock(block, x, y);
				}
			}
		}
	}
	
	public static Animal createRandomAnimal(int worldWidth, int worldHeight, Grid grid) {
		setUpRandom();
		
		Animal animal = new Animal();
		animal.birthCount = random.nextInt(6);
		animal.diet = random.nextInt(3);
		animal.maxEnergy = random.nextInt(100);
		animal.actualEnergy = animal.maxEnergy;
		animal.lifeSpan = random.nextInt(50);
		animal.reproductionRate = random.nextInt(100);
		animal.reproductionType = ReproductionType.ASEXUAL;
		animal.size = random.nextInt(4);
		animal.speed = random.nextInt(Math.min(worldWidth, worldHeight));
		animal.strength = random.nextInt(100);
		animal.weight = random.nextInt(100);
		
		int x = random.nextInt(worldWidth);
		int y = random.nextInt(worldHeight);
		// Attempt to make sure that the animal is not place on
		// impassable ground
		if (!Animal.validMove(grid, animal, x, y)) {
			Block block = new Block();
			block.blockType = BlockType.PASSABLE_GROUND;
			block.animal = animal;
			grid.setBlock(block, x, y);
		}
		
		animal.x = x;
		animal.y = y;
		return animal;
	}
	
	private static void setUpRandom() {
		if(random == null);
			random = new Random();
	}
}
