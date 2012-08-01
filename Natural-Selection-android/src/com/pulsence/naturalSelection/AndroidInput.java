package com.pulsence.naturalSelection;

import com.badlogic.gdx.Input;

public class AndroidInput implements InputProcessor {

	public int updateState(Input input, int currentState) {
		if(input.isTouched()) {
			if(currentState == GameState.SHOW_WOLRD_STATS)
				return GameState.NORMAL;
			else
				return GameState.SHOW_WOLRD_STATS;
		}
			
		return GameState.NO_CHANGE;
	}

	public String getInput(Input input) {
		return null;
	}

}
