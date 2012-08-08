package com.pulsence.naturalSelection.evo;

import java.util.Random;

public class Animal {
	public int size;
	public int speed;
	public int weight;
	public int reproductionRate;
	public int birthCount;
	public int reproductionType;
	public int maxEnergy;
	public int diet;
	public int strength;
	public int lifeSpan;
	
	public int x;
	public int y;
	
	public int age = 0;
	public int actualEnergy;
	private int stepSinceReproduce = 0;
	
	public boolean alive = true;
	
	public void step(World world) {
		//Die of age/starvation
		if(age >= lifeSpan || actualEnergy <= 0) {
			world.animals.remove(this);
			world.grid.getBlock(x, y).animal = null;
			alive = true;
			return;
		}
		
		//Have a child
		if((reproductionRate - stepSinceReproduce) * World.random.nextFloat() < 2 &&
				world.animals.size() + birthCount < world.maxPopulation) {
			for(int i = 0; i < birthCount; i++) {
				Animal child = mutateAnimal(World.random, world.grid);
				if(child != null) {
					world.animals.add(child);
					world.grid.getBlock(child.x, child.y).animal = child;
				}
			}
			stepSinceReproduce = -1;
			updateCounters();
			return;
		}
		
		//Look for food
		if(actualEnergy * World.random.nextDouble() < 15) {
			int closestX = 0;
			int closestY = 0;
			Block block;
			int closest = Integer.MAX_VALUE;
			
			for(int x = this.x - speed; x <= this.x + speed; x++) {
				for(int y = this.y - speed; y <= this.y + speed; y++) {
					block = world.grid.getBlock(x, y);
					if( (block.blockType == BlockType.VEGETATION && diet == Diet.HERBIVORE) ||
						(block.animal != null && diet == Diet.CARNIVORE) ||
						((block.blockType == BlockType.VEGETATION || block.animal != null) && diet == Diet.OMNIVORE)) {
						int dist = distance(x, y);
						if(dist < closest) {
							closestX = x;
							closestY = y;
							closest = dist;
						}
					}
				}
			}
			
			world.grid.getBlock(x, y).animal = null;			
			for(int i = 0; i < speed; i++) {
				if(closestY < y && validMove(world.grid, x, y - 1)) { //Move down
					y--;
				} else if (closestY > y && validMove(world.grid, x, y + 1)) { //Move up
					y++;
				} else if (closestX < x && validMove(world.grid, x, x - 1)) { //Move right
					x--;
				} else if (closestX > x && validMove(world.grid, x, x + 1)) { //Move left
					x++;
				} else if (x == closestX && y == closestY) {
					actualEnergy += 5;
					if(actualEnergy > maxEnergy) {
						actualEnergy = maxEnergy;
					}
					
					block = world.grid.getBlock(x, y);
					if(block.animal != null && diet != Diet.HERBIVORE) {
						world.animals.remove(block.animal);
						block.animal.alive = false;
						block.animal = null;
					}
					break;
				}
			}
			world.grid.getBlock(x, y).animal = this;
			updateCounters();
			return;
		}
		
		//Wonder aimlessly
		world.grid.getBlock(x, y).animal = null;	
		for(int i = 0; i < speed; i++) {
			int direction = World.random.nextInt(4);
			if(direction == 0 && validMove(world.grid, x, y - 1)) { //Move down
				y--;
			} else if (direction == 1 && validMove(world.grid, x, y + 1)) { //Move up
				y++;
			} else if (direction == 2 && validMove(world.grid, x, x - 1)) { //Move right
				x--;
			} else if (direction == 3 && validMove(world.grid, x, x + 1)) { //Move left
				x++;
			}
			
			if((diet == Diet.HERBIVORE || diet == Diet.OMNIVORE) &&
					world.grid.getBlock(x, y).blockType == BlockType.VEGETATION) {
				actualEnergy += 5;
				if(actualEnergy > maxEnergy) {
					actualEnergy = maxEnergy;
				}
			}
		}
		world.grid.getBlock(x, y).animal = this;
		
		updateCounters();
	}
	
	private boolean validMove(Grid grid, int x, int y) {
		Block block = grid.getBlock(x, y);
		return block.blockType != BlockType.IMPASSABLE_GROUND || 
			   block.animal != null;
	}
	
	private void updateCounters() {
		stepSinceReproduce++;
		actualEnergy--;
		age++;
	}
	
	private Animal mutateAnimal(Random rand, Grid grid) {
		Animal child = new Animal();
		
		// initially make the child a copy of the parent
		child.birthCount = this.birthCount;
		child.diet = this.diet;
		child.lifeSpan = this.lifeSpan;
		child.maxEnergy = this.maxEnergy;
		child.reproductionRate = this.reproductionRate;
		child.reproductionType = this.reproductionType;
		child.size = this.size;
		child.speed = this.speed;
		child.strength = this.strength;
		child.weight = this.weight;
		
		// now mutate a single trait
		int trait = rand.nextInt(11);
		switch (trait) {
			case 0 : child.size = warpValue(size, rand);
					 break;
			case 1 : // Do nothing to the animal
					 break;
			case 2 : child.birthCount = warpValue(birthCount, rand);
				 	 break;
			case 3 : child.lifeSpan = warpValue(lifeSpan, rand);
				 	 break;
			case 4 : child.maxEnergy = warpValue(maxEnergy, rand);
					 child.actualEnergy = child.maxEnergy;
				 	 break;
			case 5 : child.reproductionRate = warpValue(reproductionRate, rand);
				 	 break;
			case 6 : child.reproductionType = ReproductionType.ASEXUAL; // this is a filler until I decide how I am going to handle sexual repro
				 	 break;
			case 7 : child.speed = warpValue(speed, rand);
				 	 break;
			case 8 : child.strength = warpValue(strength, rand);
					 break;
			case 9 : child.weight = warpValue(weight, rand);
				 	 break;
			case 10 : child.diet = rand.nextInt(3);
				 	 break;
		}
		
		child.x = -1;
		child.y = -1;
		for(int tx = x - 1; tx < x + 2; tx++) {
			for(int ty = y - 1; ty < y + 2; ty++) {
				if(validMove(grid, tx, ty)) {
					child.x = tx;
					child.y = ty;
					break;
				}
			}
		}
		
		if(child.x == -1) // This means that there is no room around the parent to place the child
			child = null;
		
		return child;
	}
	
	private int warpValue(int value, Random rand) {
		return value + (int)Math.round(value * (rand.nextDouble() - 0.5));
	}
	
	private int distance(int gridX, int gridY) {
		double xx = Math.pow((x - gridX), 2);
		double yy = Math.pow((y - gridY), 2);
		double distance = Math.sqrt(xx + yy);
		return (int) Math.round(distance);
	}
}
