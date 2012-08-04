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
	}
	
	public World(int width, int height, ArrayList<Animal> animals) {
		this(createRandomGrid(width, height), animals);
	}
	
	public World(Grid grid, ArrayList<Animal> animals) {
		setUpRandom();
		this.grid = grid;
		this.animals = animals;
	}
	
	public void step() {
		age++;
		for(int i = 0; i < animals.size(); i++) {
			animals.get(i).step(this);
		}
	}
	
	public int getWidth() {
		return grid.getWidth();
	}
	
	public int getHeight() {
		return grid.getHeight();
	}
	
	public static Grid createRandomGrid(int width, int height) {
		setUpRandom();
		
		Grid iGrid = new Grid(width, height);
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				Block block = new Block();
				block.blockType = random.nextInt(4);
				block.animal = null;
				iGrid.setBlock(block, x, y);
			}
		}
		
		return iGrid;
	}
	
	public static Animal createRandomAnimal(int worldWidth, int worldHeight, Grid grid) {
		setUpRandom();
		
		Animal animal = new Animal();
		animal.birthCount = random.nextInt(3);
		animal.diet = random.nextInt(3);
		animal.maxEnergy = random.nextInt(100);
		animal.actualEnergy = animal.maxEnergy;
		animal.lifeSpan = random.nextInt(100);
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
		if (!tryPosition(x, y, grid)) {
			Block block = new Block();
			block.blockType = BlockType.PASSABLE_GROUND;
			block.animal = animal;
			grid.setBlock(block, x, y);
		}
		
		animal.x = x;
		animal.y = y;
		return animal;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param grid
	 * @return True if good position
	 */
	private static boolean tryPosition(int x, int y, Grid grid) {
		return grid.getBlock(x, y).blockType != BlockType.IMPASSABLE_GROUND || 
				grid.getBlock(x, y).animal != null;
	}
	
	private static void setUpRandom() {
		if(random == null);
			random = new Random();
	}
}
