package com.stellar.relay;

import static com.stellar.relay.GUI.score;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
	enum Difficulty {
		EASY(0.5f),
		MEDIUM(1f),
		HARD(2.0f);

		public final float multiplier;

		Difficulty(float multiplier) {
			this.multiplier = multiplier;
		}
	}

	enum GameState {
		SPLASH,
		TUTORIAL,
		FREE_PLAY,
		GAME_OVER
	}

	public static final boolean DEBUG = true;

	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private FitViewport viewport;

	private Controller controller_left;

	private GUI gui;

	private float messageSpawnTimer = 0;

	public static Difficulty difficulty = Difficulty.MEDIUM;

	@Override
	public void create() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		controller_left = new Controller(100, 100, Player.LEFT);
		gui = GUI.getInstance();
		viewport = new FitViewport(Gdx.graphics.getWidth() / 10f, Gdx.graphics.getHeight() / 10f);

		for (int i = 0; i < 2; i++) {
			Planet.spawnNewPlanet();
		}

		for (int i = 0; i < 2; i++) {
			Satellite.spawnNewSatellite();
		}

		Message.spawnNewMessage();

		System.out.println("Satellites: " + Satellite.satellites.size());
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true); // true centers the camera
	}

	@Override
	public void render() {
		input();
		draw();
		logic();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	private void input() {
		controller_left.input();
	}

	private void draw() {
		ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
		Planet.drawAll(batch, shapeRenderer);
		Message.drawAll(batch, shapeRenderer);
		Path.drawAll(shapeRenderer);
		Satellite.drawAll(batch, shapeRenderer);

		batch.begin();
		controller_left.draw(batch);
		gui.draw(batch);
		batch.end();

		viewport.apply();
	}

	private void logic() {
		if (score < 300) {
			if (Message.messages.isEmpty()) {
				Planet.spawnNewPlanet();
				for (int i = 0; i < 2 * Math.log(Planet.planets.size()); i++) {
					Satellite.spawnNewSatellite();
				}

				for (int i = 0; i < 2 * Math.log(Planet.planets.size()); i++) {
					Message.spawnNewMessage();
				}

				messageSpawnTimer = (float) (Math.random() * 10f);
			}
		} else if (messageSpawnTimer <= 0 || Message.messages.isEmpty()) {
			messageSpawnTimer = (float) (Math.random() * 10f);

			if (Math.random() > 0.2 * Math.sqrt(Planet.planets.size())) Planet.spawnNewPlanet();
			for (int i = 0; i < Math.log(Planet.planets.size()); i++) {
				Satellite.spawnNewSatellite();
			}

			for (int i = 0; i < 2 * Math.log(Planet.planets.size()); i++) {
				Message.spawnNewMessage();
			}
		}
	}

	public static void gameOver() {
		throw new RuntimeException("Game Over! Final Score: " + score);
	}
}
