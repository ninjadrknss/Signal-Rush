package com.stellar.relay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public class Message {
	public static final int MAX_TIMEOUT = 2000;
	public static final float speed = 0.1f * 20;

	enum State {
		AWAITING,
		IN_TRANSIT,
		DELIVERED
	}

	private final Planet source;
	private final Planet destination;
	public final Path path;

	// TODO: implement different creature types for the planet inhabitants
	private final int creature = 0;
	private float progress; // 0 to 1
	private float life;

	private State state = State.AWAITING;

	public static final ArrayList<Message> messages = new ArrayList<>();

	private final Sprite awaitingSprite;
	private final Sprite inTransitSprite;

	public Message(Planet source, Planet destination) {
		this.source = source;
		this.destination = destination;
		this.progress = 0;
		this.life = MAX_TIMEOUT; // in seconds, TODO tweak this value for better gameplay

		path = new Path(this, source, destination);
		path.add(source);

		source.message = this; // Link the message to the source planet

		awaitingSprite = new Sprite(new Texture("PLACEHOLDER_planetMessage.png"));
		awaitingSprite.setPosition(
				source.getCX() + 20, source.getCY() + 20); // Position on top right of the source planet
		awaitingSprite.setSize(30, 30);

		inTransitSprite = new Sprite(new Texture("PLACEHOLDER_sendingMessage.png"));
		inTransitSprite.setPosition(-100, -100); // Start off-screen
		inTransitSprite.setSize(30, 30);
		inTransitSprite.setOrigin(inTransitSprite.getWidth() / 2, inTransitSprite.getHeight() / 2);

		messages.add(this);
	}

	private void draw(Batch batch, ShapeRenderer shapeRenderer) {
		if (state == State.AWAITING) {
			batch.begin();
			awaitingSprite.draw(batch);
			batch.end();

			life -= Gdx.graphics.getDeltaTime();

			if (life <= 0) Main.gameOver();
		} else if (state == State.IN_TRANSIT) {
			progress += speed * Gdx.graphics.getDeltaTime();

			if (progress >= 1) {
				progress = 1;
				state = State.DELIVERED;
				path.delete();
				GUI.score += 100; // Award points for successful delivery
				if (Main.DEBUG) System.out.println("Message delivered to " + destination);
				// TODO: add some visual effect for delivery, like a burst or confetti?
			}

			Vector2 position = path.positionAlongPath(progress);
			inTransitSprite.setPosition(
					position.x - inTransitSprite.getOriginX(), position.y - inTransitSprite.getOriginY());

			batch.begin();
			inTransitSprite.draw(batch);
			batch.end();
		} else {
			System.out.println("Should be deleted lol");
		}
	}

	public void start() {
		if (state == State.AWAITING && path.isComplete()) {
			state = State.IN_TRANSIT;
			source.message = null; // Clear the message from the source planet
			inTransitSprite.setPosition(source.getCX(), source.getCY()); // Start at the source planet
			progress = 0;
		}
	}

	public static void drawAll(Batch batch, ShapeRenderer shapeRenderer) {
		messages.forEach(message -> message.draw(batch, shapeRenderer));

		messages.removeAll(
				messages.stream().filter(message -> message.state == State.DELIVERED).toList());
	}

	// TODO: check that its actually possible to create a path between the source and destination
	// planets before spawning the message
	private static boolean possiblePathExists(Planet source, Planet destination) {
		return true;
	}

	public static void spawnNewMessage() {
		Planet source = null, destination = null;

		while (source == null || !possiblePathExists(source, destination)) {
			source = Planet.planets.get((int) (Math.random() * Planet.planets.size()));
			while (source.message != null) { // Ensure the source planet doesn't already have a message
				source = Planet.planets.get((int) (Math.random() * Planet.planets.size()));
			}

			destination = Planet.planets.get((int) (Math.random() * Planet.planets.size()));

			while (destination == source) { // Ensure the destination is different from the source
				destination = Planet.planets.get((int) (Math.random() * Planet.planets.size()));
			}
		}

		new Message(source, destination);
	}
}
