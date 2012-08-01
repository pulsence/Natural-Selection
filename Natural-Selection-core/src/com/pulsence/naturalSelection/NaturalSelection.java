package com.pulsence.naturalSelection;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pulsence.naturalSelection.evo.Animal;
import com.pulsence.naturalSelection.evo.BlockType;
import com.pulsence.naturalSelection.evo.Diet;
import com.pulsence.naturalSelection.evo.World;

public class NaturalSelection implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private Texture ground;
	private Texture impassableGround;
	private Texture water;
	private Texture vegetation;
	private Texture herbivore;
	private Texture omnivore;
	private Texture carnivore;
	
	private World world;
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera();
	    camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
				
		ground = new Texture(Gdx.files.internal("textures/ground.png"));
		impassableGround = new Texture(Gdx.files.internal("textures/impassable_ground.png"));
		water = new Texture(Gdx.files.internal("textures/water.png"));
		vegetation = new Texture(Gdx.files.internal("textures/vegetation.png"));
		herbivore = new Texture(Gdx.files.internal("textures/herbivore.png"));
		omnivore = new Texture(Gdx.files.internal("textures/omnivore.png"));
		carnivore = new Texture(Gdx.files.internal("textures/carnivore.png"));
		
		world = new World((int)w/10, (int)h/10, 13);
		world.maxPopulation = Math.min((int)(w*h/10), 1000);
	}

	@Override
	public void dispose() {
		batch.dispose();
		
		ground.dispose();
		impassableGround.dispose();
		water.dispose();
		vegetation.dispose();
		herbivore.dispose();
		omnivore.dispose();
		carnivore.dispose();
	}

	@Override
	public void render() {
		world.step();
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// Render the world
		int blockWidth = Gdx.graphics.getWidth() / world.getWidth();
		int blockHeight = Gdx.graphics.getHeight() / world.getHeight();
		
		for (int x = 0; x < world.getWidth(); x++) {
			for (int y = 0; y < world.getHeight(); y++) {
				int block = world.grid.getBlock(x, y);
				if (block == BlockType.IMPASSABLE_GROUND) {
					batch.draw(impassableGround, x * blockWidth, y * blockHeight, blockWidth, blockHeight);
				} else if (block == BlockType.PASSABLE_GROUND) {
					batch.draw(ground, x * blockWidth, y * blockHeight, blockWidth, blockHeight);
				} else if (block == BlockType.VEGETATION) {
					batch.draw(vegetation, x * blockWidth, y * blockHeight, blockWidth, blockHeight);
				} else if (block == BlockType.WATER) {
					batch.draw(water, x * blockWidth, y * blockHeight, blockWidth, blockHeight);
				}
			}
		}
		
		// Render the animals
		for(Animal animal : world.animals) {
			if(animal.diet == Diet.CARNIVORE) {
				batch.draw(carnivore, animal.x * blockWidth, animal.y * blockHeight, blockWidth, blockHeight);
			} else if (animal.diet == Diet.HERBIVORE) {
				batch.draw(herbivore, animal.x * blockWidth, animal.y * blockHeight, blockWidth, blockHeight);
			} else if (animal.diet == Diet.OMNIVORE) {
				batch.draw(omnivore, animal.x * blockWidth, animal.y * blockHeight, blockWidth, blockHeight);
			}
		}
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
