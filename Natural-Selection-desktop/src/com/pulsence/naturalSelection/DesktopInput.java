package com.pulsence.naturalSelection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

public class DesktopInput implements InputProcessor {
	private Input input;

	@Override
	public void updateState(GameState state) {
		if( input.isKeyPressed(Keys.SHIFT_LEFT) ||
			input.isKeyPressed(Keys.SHIFT_RIGHT) ||
			input.isButtonPressed(Buttons.RIGHT)) {
			state.showWorldStats = state.showWorldStats ? false : true;
		}
		
		if(input.isKeyPressed(Keys.P) || input.isKeyPressed(Keys.SPACE)) {
			state.pause = state.pause ? false : true;
			state.showWorldStats = state.pause;
		}
		
		if(input.isButtonPressed(Buttons.LEFT)) {
			state.animalSelected = true;
		}
		
		if(input.isKeyPressed(Keys.ESCAPE)) {
			state.endGame = true;
		}
		
		if(input.isKeyPressed(Keys.R)) {
			state.reset = true;
		}
	}

	@Override
	public void initialize() {
		input = Gdx.input;
	}

}
