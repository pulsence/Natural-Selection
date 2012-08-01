package com.pulsence.naturalSelection;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;

public class DesktopInput implements InputProcessor {

	public int updateState(Input input, int currentState) {
		if(input.isTouched()) {
			if(currentState == GameState.SHOW_WOLRD_STATS)
				return GameState.NORMAL;
			else
				return GameState.SHOW_WOLRD_STATS;
		}
		
		if(input.isKeyPressed(Keys.P) || input.isKeyPressed(Keys.SPACE)) {
			if(currentState == GameState.PAUSED)
				return GameState.NORMAL;
			else
				return GameState.PAUSED;
		}
		
		if(input.isKeyPressed(Keys.ESCAPE))
			return GameState.END;
		
		return GameState.NO_CHANGE;
	}

	public String getInput(Input input) {
		return null;
	}

}
