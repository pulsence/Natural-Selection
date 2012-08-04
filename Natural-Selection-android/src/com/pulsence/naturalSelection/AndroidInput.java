package com.pulsence.naturalSelection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;

public class AndroidInput implements InputProcessor {
	private Input input;

	public void updateState(GameState state) {
		if(input.isTouched() ||  input.justTouched()) {
			state.animalSelected = state.animalSelected ? false : true;
		}
		
		if(input.isTouched(0) && input.isTouched(1)) {
			state.showWorldStats = state.showWorldStats ? false : true;
		}
		
		if(input.isKeyPressed(Keys.BACK)) {
			state.reset = true;
		}
	}

	public void initialize() {
		input = Gdx.input;
		input.setCatchBackKey(true);
	}

}
