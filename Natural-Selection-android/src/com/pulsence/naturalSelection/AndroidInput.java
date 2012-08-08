package com.pulsence.naturalSelection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;

public class AndroidInput implements InputProcessor {
	private Input input;

	public void updateState(GameState state) {
		state.animalSelected = input.isTouched() ||  input.justTouched();
		
		if(input.isTouched(0) && input.isTouched(1)) {
			state.showWorldStats = state.showWorldStats ? false : true;
		}
		
		state.reset = input.isKeyPressed(Keys.BACK);
		if(input.isKeyPressed(Keys.MENU) ) {
			state.pause = state.pause ? false : true;
		}
	}

	public void initialize() {
		input = Gdx.input;
		input.setCatchBackKey(true);
		input.setCatchMenuKey(true);
	}

}
