package com.pulsence.naturalSelection;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	// cmd args
	// 0: debug mode, true or false
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Natural Selection";
		cfg.useGL20 = true;
		cfg.width = 480;
		cfg.height = 320;
		
		if(args.length > 0) {
			NaturalSelection.DEBUG = args[0].toUpperCase().equals("TRUE");
		}
		
		new LwjglApplication(new NaturalSelection(new DesktopInput()), cfg);
	}
}
