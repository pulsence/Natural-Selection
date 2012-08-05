package com.pulsence.naturalSelection.evo;

public class Diet {
	public static int OMNIVORE = 0;
	public static int CARNIVORE = 1;
	public static int HERBIVORE = 2;
	
	public static String getDiet(int diet) {
		if(diet == OMNIVORE)
			return "omnivore";
		else if(diet == CARNIVORE)
			return "carnivore";
		else
			return "herbivore";
	}
}
