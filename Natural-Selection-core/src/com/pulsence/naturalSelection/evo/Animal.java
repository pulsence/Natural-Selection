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
	
	public void step(World world) {
		//Die of age/starvation
		if(age >= lifeSpan || actualEnergy <= 0) {
			world.animals.remove(this);
			age++;
			return;
		}
		
		Random rand = World.random;
		
		//Have a child
		if((reproductionRate - stepSinceReproduce) * rand.nextFloat() < 2 &&
				world.animals.size() + birthCount < world.maxPopulation) {
			for(int i = 0; i < birthCount; i++) {
				world.animals.add(mutateAnimal(rand));
			}
			stepSinceReproduce = -1;
			updateCounters();
			return;
		}
		
		//Look for food
		if(actualEnergy * rand.nextDouble() < 15) {
			int closestX = 0;
			int closestY = 0;
			Animal iAnimal = null;
			int closest = Integer.MAX_VALUE;
			if(diet == Diet.HERBIVORE || diet == Diet.OMNIVORE) {
				for(int x = 0; x < world.grid.grid.length; x++) {
					for(int y = 0; y < world.grid.grid[0].length; y++) {
						int dist = distance(x, y);
						if(dist < closest) {
							closestX = x;
							closestY = y;
							closest = dist;
						}
					}
				}
			} else if (diet == Diet.CARNIVORE || diet == Diet.OMNIVORE) {
				for(Animal animal : world.animals) {
					int dist = distance(animal.x, animal.y);
					if(dist < closest) {
						closestX = x;
						closestY = y;
						iAnimal = animal;
						closest = dist;
					}
				}
			}
			
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
					if(iAnimal != null)
						world.animals.remove(iAnimal);
					break;
				}
			}
			updateCounters();
			return;
		}
		
		//Wonder aimlessly
		for(int i = 0; i < speed; i++) {
			int direction = rand.nextInt(4);
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
					world.grid.getBlock(x, y) == BlockType.VEGETATION) {
				actualEnergy += 5;
				if(actualEnergy > maxEnergy) {
					actualEnergy = maxEnergy;
				}
			}
			
		}
		
		updateCounters();
	}
	
	private boolean validMove(Grid grid, int x, int y) {
		return grid.validBlock(x, y) && grid.getBlock(x, y) != BlockType.IMPASSABLE_GROUND;
	}
	
	private void updateCounters() {
		stepSinceReproduce++;
		actualEnergy--;
		age++;
	}
	
	private Animal mutateAnimal(Random rand) {
		Animal child = new Animal();
		child.size = warpValue(size, rand);
		child.birthCount = warpValue(birthCount, rand);
		child.lifeSpan = warpValue(lifeSpan, rand);
		child.maxEnergy = warpValue(maxEnergy, rand);
		child.actualEnergy = child.maxEnergy;
		child.reproductionRate = warpValue(reproductionRate, rand);
		child.reproductionType = ReproductionType.ASEXUAL;
		child.speed = warpValue(speed, rand);
		child.strength = warpValue(strength, rand);
		child.weight = warpValue(weight, rand);
		
		if(rand.nextFloat() - 0.25f > 0.5f)
			child.diet = rand.nextInt(3);
		else
			child.diet = diet;
		
		child.x = x;
		child.y = y;
		
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
