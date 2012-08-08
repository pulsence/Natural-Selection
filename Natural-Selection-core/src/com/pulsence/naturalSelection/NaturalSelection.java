package com.pulsence.naturalSelection;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.pulsence.naturalSelection.evo.Animal;
import com.pulsence.naturalSelection.evo.Block;
import com.pulsence.naturalSelection.evo.BlockType;
import com.pulsence.naturalSelection.evo.Diet;
import com.pulsence.naturalSelection.evo.World;

public class NaturalSelection implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;

	private TextureAtlas atlas;
	private Sprite ground;
	private Sprite impassableGround;
	private Sprite water;
	private Sprite vegetation;
	private Sprite herbivore;
	private Sprite omnivore;
	private Sprite carnivore;
	
	private BitmapFont font;
	
	private World world;
	private WorldStatistics stats;
	private GameState gameState;
	
	private int framesSinceInput;
	private int framesSinceClean;
	private InputProcessor inputProcessor;
	
	private int BASEBLOCKSIZE;
	private int blockWidth;
	private int blockHeight;
	private int screenWidth;
	private int screenHeight;
	
	private Stage uiStage;
	private Table gameUI;
	private Label worldStats;
	private Label animalStats;
	
	private Table pauseMenu;
	private Label pauseLabel;
	private Label animalAgeLabel;
	private TextField animalAgeField;
	
	
	public NaturalSelection (InputProcessor inputProcessor) {
		super();
		this.inputProcessor = inputProcessor;
	}
	
	@Override
	public void create() {
		inputProcessor.initialize();
		
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera();
	    camera.setToOrtho(false, screenWidth, screenHeight);

		Gdx.gl.glClearColor(34f/255f, 177f/255f, 76f/255f, 1); // Same color as the ground
	    
		batch = new SpriteBatch();
		
		atlas = new TextureAtlas(Gdx.files.internal("packed/pack.pack"));
		ground =  atlas.createSprite("ground");
		impassableGround =  atlas.createSprite("impassable");
		water =  atlas.createSprite("water");
		vegetation =  atlas.createSprite("vegetation");
		herbivore =  atlas.createSprite("herbivore");
		omnivore =  atlas.createSprite("omnivore");
		carnivore =  atlas.createSprite("carnivore");
		
		font = new BitmapFont();
		font.setColor(1, 1, 1, 1);
		font.setScale(Gdx.app.getType() == ApplicationType.Desktop ? 1f : 1.5f);
		
		BASEBLOCKSIZE = Gdx.app.getType() == ApplicationType.Desktop ? 10 : 20;
		createWorld();
		
		gameState = new GameState();
		stats = new WorldStatistics();

		// UI Stuff
        uiStage = new Stage(screenWidth, screenHeight, true, batch);
        Gdx.input.setInputProcessor(uiStage);
        
        gameUI = new Table();
        gameUI.setFillParent(true);
        gameUI.top().left();
        uiStage.addActor(gameUI);
        
        Label.LabelStyle style = new Label.LabelStyle(font, font.getColor());
        worldStats = new Label("", style);
        worldStats.visible = false;
        gameUI.add(worldStats).center().left().expandX();
        
        animalStats = new Label("", style);
        animalStats.visible = false;
        gameUI.add(animalStats).center().left();
        gameUI.row();
        setupPauseMenu();
	}
	
	private void setupPauseMenu() {
        Label.LabelStyle style = new Label.LabelStyle(font, font.getColor());
        pauseMenu = new Table();
        pauseMenu.visible = false;
        gameUI.add(pauseMenu).center();

        pauseLabel = new Label("Paused", style);
        pauseMenu.add(pauseLabel);
        pauseMenu.row();
        
        animalAgeLabel = new Label("Age", style);
        pauseMenu.add(animalAgeLabel);
        //animalAgeField = new TextField("", new TextField.TextFieldStyle(font, font.getColor(), font, font.getColor(), cursor, selection, background));
        pauseMenu.add(animalAgeField);
        
	}

	@Override
	public void dispose() {
		batch.dispose();
		
		atlas.dispose();
	}

	@Override
	public void render() {
		update();
		draw();
	}
	
	private void update() {
		if(world.animals.size() == 0) {
			createWorld();
			gameState = new GameState();
		}
		
		if(framesSinceInput == 3 || Gdx.graphics.getFramesPerSecond() < 30) {
			processInput();
			framesSinceInput = -1;
		}
		framesSinceInput++;
		
		if(gameState.animal != null && !world.animals.contains(gameState.animal)) {;
			gameState.animal = null;
		}
		
		if(gameState.reset) {
			createWorld();
			gameState = new GameState();
		}
		
		if(!gameState.pause) {
			world.step();
			stats.updateStatistics(world);
		}
		
		// This cleans up the animal list. Required until I get the grid transitions finished
		if(framesSinceClean > 9) {
			framesSinceClean = -1;
			cleanAnimals();
		}
		framesSinceClean++;
		
		if(gameState.showWorldStats)
			prepareWorldStats();
		
		if(gameState.animal != null)
			prepareAnimalStats();
		else
			animalStats.visible = false;
	}
	
	private void cleanAnimals() {
		world.animals.clear();
		for(int x = 0; x < world.getWidth(); x++) {
			for(int y = 0; y < world.getHeight(); y++) {
				Animal animal = world.grid.getBlock(x, y).animal;
				if(animal != null)
					world.animals.add(animal);
			}
		}
	}
	
	private void processInput() {
		inputProcessor.updateState(gameState);
		if(gameState.endGame) {
			Gdx.app.exit();
		}
		
		if(gameState.animalSelected) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			int x = (int)touchPos.x / blockWidth;
			int y = (int)touchPos.y / blockWidth;
			gameState.animal = world.grid.getBlock(x, y).animal;
			gameState.animalSelected = false;
		}

		animalStats.visible = gameState.animal != null;
		worldStats.visible = gameState.showWorldStats;
		
		pauseMenu.visible = gameState.pause;
		if(gameState.pause && gameState.animal != null) {
			//animalAgeField.setRange(0, gameState.animal.lifeSpan);
			animalAgeLabel.setText("Age: " + gameState.animal.age);
		}
		
	}
	
	private void prepareWorldStats() {
		StringBuilder sb = new StringBuilder("FPS:" + Gdx.graphics.getFramesPerSecond() + "\n");
		sb.append(stats);
		worldStats.setText(sb);
	}
	
	private void prepareAnimalStats() {
		animalStats.setText(gameState.animal.toString());
	}
	
	private void draw() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		renderWorld();		
		batch.end();
        uiStage.draw();
	}

	private void renderWorld() {
		Block block;
		for (int x = 0; x < world.getWidth(); x++) {
			for (int y = 0; y < world.getHeight(); y++) {
				block = world.grid.getBlock(x, y);
				if(block.animal == null) {
					if (block.blockType == BlockType.IMPASSABLE_GROUND) {
						batch.draw(impassableGround, x * blockWidth, y * blockHeight, blockWidth, blockHeight);
					} else if (block.blockType == BlockType.PASSABLE_GROUND) {
						batch.draw(ground, x * blockWidth, y * blockHeight, blockWidth, blockHeight);
					} else if (block.blockType == BlockType.VEGETATION) {
						batch.draw(vegetation, x * blockWidth, y * blockHeight, blockWidth, blockHeight);
					} else if (block.blockType == BlockType.WATER) {
						batch.draw(water, x * blockWidth, y * blockHeight, blockWidth, blockHeight);
					}
				} else {
					if(block.animal.diet == Diet.CARNIVORE) {
						batch.draw(carnivore, block.animal.x * blockWidth, block.animal.y * blockHeight, blockWidth, blockHeight);
					} else if (block.animal.diet == Diet.HERBIVORE) {
						batch.draw(herbivore, block.animal.x * blockWidth, block.animal.y * blockHeight, blockWidth, blockHeight);
					} else if (block.animal.diet == Diet.OMNIVORE) {
						batch.draw(omnivore, block.animal.x * blockWidth, block.animal.y * blockHeight, blockWidth, blockHeight);
					}
				}
			}
		}
	}
	
	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
		camera.update(true);
		blockWidth = width / world.getWidth();
		blockHeight = height / world.getHeight();
		
		screenWidth = width;
		screenHeight = height;
		
		uiStage.setViewport(width, height, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	private void createWorld() {
		int width = screenWidth / BASEBLOCKSIZE;
		int height = screenHeight / BASEBLOCKSIZE;
		
		world = new World(width, height, 13);
		world.maxPopulation = Math.min(width * height / BASEBLOCKSIZE, 1000);
		
		blockWidth = screenWidth / width;
		blockHeight = screenHeight / height;
	}
}
