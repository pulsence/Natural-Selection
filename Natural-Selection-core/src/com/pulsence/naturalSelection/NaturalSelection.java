package com.pulsence.naturalSelection;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
	
	private BitmapFont font;
	
	private World world;
	private WorldStatistics stats;
	private int gameState;
	
	private int framesSinceInput;
	private InputProcessor inputProcessor;
	
	private int blockWidth;
	private int blockHeight;
	private int screenWidth;
	private int screenHeight;
	
	public NaturalSelection (InputProcessor inputProcessor) {
		super();
		this.inputProcessor = inputProcessor;
	}
	
	@Override
	public void create() {
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera();
	    camera.setToOrtho(false, screenWidth, screenHeight);
		batch = new SpriteBatch();
				
		ground = new Texture(Gdx.files.internal("textures/ground.png"));
		impassableGround = new Texture(Gdx.files.internal("textures/impassable_ground.png"));
		water = new Texture(Gdx.files.internal("textures/water.png"));
		vegetation = new Texture(Gdx.files.internal("textures/vegetation.png"));
		herbivore = new Texture(Gdx.files.internal("textures/herbivore.png"));
		omnivore = new Texture(Gdx.files.internal("textures/omnivore.png"));
		carnivore = new Texture(Gdx.files.internal("textures/carnivore.png"));
		
		font = new BitmapFont();
		
		createWorld();
		
		gameState = GameState.NORMAL;
		stats = new WorldStatistics();
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
		update();
		draw();
	}
	
	private void update() {
		if(world.animals.size() == 0)
			createWorld();
		
		if(framesSinceInput == 3) {
			processInput();
			framesSinceInput = -1;
		}
		framesSinceInput++;
		
		if(gameState != GameState.PAUSED) {
			world.step();
			stats.updateStatistics(world);
		}
	}
	
	private void processInput() {
		int newState = inputProcessor.updateState(Gdx.input, gameState);
		if(newState != GameState.NO_CHANGE) {
			if(newState == GameState.END) {
				Gdx.app.exit();
			}
			gameState = newState;
		}
	}
	
	private void draw() {
		Gdx.gl.glClearColor(34f/255f, 177f/255f, 76f/255f, 1); // Same color as the ground
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// Render the world
		renderWorld();
		
		// Render the animals
		renderAnimals();
		if(gameState == GameState.PAUSED)
			renderPause();
		
		if( gameState == GameState.SHOW_WOLRD_STATS ||
			gameState == GameState.PAUSED)
			renderWorldStates();
		
		batch.end();
	}

	private void renderWorld() {
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
	}
	
	private void renderAnimals() {
		for(Animal animal : world.animals) {
			if(animal.diet == Diet.CARNIVORE) {
				batch.draw(carnivore, animal.x * blockWidth, animal.y * blockHeight, blockWidth, blockHeight);
			} else if (animal.diet == Diet.HERBIVORE) {
				batch.draw(herbivore, animal.x * blockWidth, animal.y * blockHeight, blockWidth, blockHeight);
			} else if (animal.diet == Diet.OMNIVORE) {
				batch.draw(omnivore, animal.x * blockWidth, animal.y * blockHeight, blockWidth, blockHeight);
			}
		}
	}
	
	private void renderPause() {
		font.setColor(0, 0, 0, 1);
		font.draw(batch, "Paused", screenWidth / 2, screenHeight / 2);
	}
	
	private void renderWorldStates() {
		StringBuilder str = new StringBuilder("World Stats\n");
		str.append("FPS: " + Gdx.graphics.getFramesPerSecond() + "\n").
			append("World Age: " + stats.worldAge + "\n").
			append("Animals: " + stats.animals + "\n").
			append("Carnivores: " + stats.carnivores + "\n").
			append("Omnivores: " + stats.omnivores + "\n").
			append("Hebivores: " + stats.herbivores + "\n").
			append("Average Age: " + stats.averageAge + "\n");
		font.setColor(0, 0, 0, 1);
		font.drawMultiLine(batch, str, 5, screenHeight);
	}
	
	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
		
		blockWidth = width / world.getWidth();
		blockHeight = height / world.getHeight();
		
		screenWidth = width;
		screenHeight = height;
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	private void createWorld() {
		int width = screenWidth / 10;
		int height = screenHeight / 10;
		
		world = new World(width, height, 13);
		world.maxPopulation = Math.min(width * height / 10, 1000);
		
		blockWidth = screenWidth / width;
		blockHeight = screenHeight / height;
	}
}
