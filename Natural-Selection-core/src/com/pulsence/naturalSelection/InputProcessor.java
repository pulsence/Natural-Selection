package com.pulsence.naturalSelection;

import com.badlogic.gdx.Input;

public interface InputProcessor {
	
	public int updateState(Input input, int currentState);
	
	public String getInput(Input input);
}
