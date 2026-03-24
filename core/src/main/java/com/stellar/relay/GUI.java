package com.stellar.relay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.*;

public class GUI {
	private final BitmapFont font24;
	private final BitmapFont font12;

	public static int score = 0;

	private static GUI instance = null;

	public static GUI getInstance() {
		if (instance == null) instance = new GUI();
		return instance;
	}

	private GUI() {
		FreeTypeFontGenerator generator =
				new FreeTypeFontGenerator(Gdx.files.internal("fonts/PixelatedEleganceRegular-ovawB.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 24;
		font24 = generator.generateFont(parameter); // font size 24 pixels
		parameter.size = 12;
		font12 = generator.generateFont(parameter); // font size 12 pixels
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
	}

	public void draw(Batch batch) {
		font24.draw(batch, "Score: %5d".formatted(score), 10, Gdx.graphics.getHeight() - 25);

		if (Main.DEBUG) {
			font12.draw(batch, "FPS: " + (int) (1 / Gdx.graphics.getDeltaTime()), 10, 100);

			font12.draw(batch, "Satellites: " + Satellite.satellites.size(), 10, 80);
			font12.draw(batch, "Planets: " + Planet.planets.size(), 10, 60);
			font12.draw(batch, "Messages: " + Message.messages.size(), 10, 40);
			font12.draw(batch, "Paths: " + Path.paths.size(), 10, 20);
		}
	}
}
