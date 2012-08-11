package com.pulsence.naturalSelection;

import com.pulsence.naturalSelection.evo.Animal;
import com.pulsence.naturalSelection.evo.Diet;
import com.pulsence.naturalSelection.evo.World;

public class WorldStatistics {
	int animals;
	float averageAge;
	int carnivores;
	int omnivores;
	int herbivores;
	long worldAge;
	
	public void updateStatistics(World world) {
		averageAge = 0;
		carnivores = 0;
		omnivores = 0;
		herbivores = 0;
		
		animals = world.animals.size();
		worldAge = world.age;
		
		int i = 0;
		Animal animal;
		for(; i < animals; i++) {
			animal = world.animals.get(i);
			averageAge += animal.age;
			if(animal.diet == Diet.CARNIVORE) {
				carnivores++;
			} else if(animal.diet == Diet.HERBIVORE) {
				herbivores++;
			} else {
				omnivores++;
			}
		}
		averageAge /= i;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Animals: " + animals + "\n");
		sb.append("Carnivores: " + carnivores + "\n").
		   append("Herbivores: " + herbivores + "\n").
		   append("Omnivores: " + omnivores + "\n").
		   append("World Age: " + worldAge + "\n").
		   append("Average Animal Age: " + averageAge + "\n");
		return sb.toString();
	}
}
