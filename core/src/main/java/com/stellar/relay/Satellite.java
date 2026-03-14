package com.stellar.relay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.ArrayList;

public class Satellite {
	enum State {
		NONE,
		TRANSMITTING
	}

	private Sprite sprite;
	private State state = State.NONE;
	private final Planet orbitingPlanet;

	private float angle;
	private float distance;
	private float distanceOscillationPeriod = 0;
	// Random orbit speed between 10 and 40 degrees per second
	private float orbitSpeed = (float) (Math.random() * 30 + 10) * (Math.random() > 0.5 ? 1 : -1);
	// Random oscilation speed between -0.5 and 0.5 radians per second
	private float oscilationSpeed = (float) (Math.random() * 0.75 + 0.25);
	public static ArrayList<Satellite> satellites = new ArrayList<>();

	public Satellite(float angle, float distance, Planet orbitingPlanet) {
		this.orbitingPlanet = orbitingPlanet;
		this.angle = angle;
		this.distance = distance;

		sprite = new Sprite(new Texture("PLACEHOLDER_Satellite.png"));
		sprite.setSize(35, 35);
		sprite.setOriginCenter();
		updatePosition();
		sprite.setRotation((float) (Math.random() * 360)); // Random initial rotation

		orbitingPlanet.incrementSatelliteCount();
		satellites.add(this);
	}

	private void updatePosition() {
		float x = orbitingPlanet.getCX() + distance * (float) Math.cos(Math.toRadians(angle)) - 10;
		float y = orbitingPlanet.getCY() + distance * (float) Math.sin(Math.toRadians(angle)) - 10;
		sprite.setPosition(x, y);
	}

	private void draw(Batch batch) {
		sprite.draw(batch);

		// Update position for orbiting
		angle += orbitSpeed * Gdx.graphics.getDeltaTime(); // Orbit speed (degrees)
		distanceOscillationPeriod += oscilationSpeed * Gdx.graphics.getDeltaTime();
		distance += (float) (Math.sin(distanceOscillationPeriod) * 0.5f) / 20f;

		updatePosition();
	}

	public static void drawAll(Batch batch) {
		satellites.forEach(satellite -> satellite.draw(batch));
	}

	public static void spawnNewSatellite(Planet planet) {
		float angle = (float) (Math.random() * 360);
		float distance =
				planet.getWidth() / 2
						+ 4
						+ (float) (Math.random() * 5); // Random distance from the planet's surface
		new Satellite(angle, distance, planet);
	}
}
