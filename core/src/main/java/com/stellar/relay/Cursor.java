package com.stellar.relay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.fazecast.jSerialComm.SerialPort;

public class Cursor {
	private Player player;

	private final Sprite sprite;
	private static SerialPort picoPort;

	private byte[] buffer = new byte[14];

	public Cursor(float x, float y, Player player) {
		this.player = player;

		sprite = new Sprite(new Texture("satellite.png"));
		sprite.setSize(60, 60);
		sprite.setPosition(x, y);

		SerialPort[] ports = SerialPort.getCommPorts();

		if (ports[ports.length - 1].getSystemPortName().contains("usb")
				|| ports[ports.length - 1].getSystemPortName().contains("com")) {
			picoPort = ports[ports.length - 1];
			picoPort.setBaudRate(9600);
			picoPort.setNumDataBits(8);
			picoPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
			picoPort.setParity(SerialPort.NO_PARITY);

			picoPort.openPort();
			System.out.println("Pico found on port: " + picoPort.getSystemPortName());
		} else {
			System.out.println("Pico not found. Available ports:");
			for (int i = 0; i < ports.length; i++) {
				System.out.println(i + ": " + ports[i].getSystemPortName());
			}
		}
	}

	public void input() {
		float speed = 150.0f * Gdx.graphics.getDeltaTime();

		int numRead = picoPort.readBytes(buffer, buffer.length);
		boolean[] inputs = new boolean[6]; // Up, Down, Left, Right, button1, button2

		if (numRead > 0) {
			// Read the first line of data
			String data = new String(buffer, 0, numRead).trim().split("\n")[0];
			if (data.charAt(0) == 'P') {
				for (int i = 0; i < 6 && i < data.length() - 1; i++) {
					// The data format is expected to be "P:xxxxyyaaaabb" where each variable is '0' or '1'
					// for each button,
					// and each set of 6 buttons corresponds to a player, 4 joystick switches, 2 buttons.
					inputs[i] = data.charAt(i + 2 + (player == Player.LEFT ? 0 : 6)) == '1';
				}
			}
			System.out.println("Received data: " + data);
			buffer = new byte[256];
		}

		if (player == Player.LEFT && Gdx.input.isKeyPressed(Input.Keys.D)
				|| player == Player.RIGHT && Gdx.input.isKeyPressed(Input.Keys.RIGHT)
				|| inputs[3]) {
			sprite.translateX(speed);
		}
		if (player == Player.LEFT && Gdx.input.isKeyPressed(Input.Keys.A)
				|| player == Player.RIGHT && Gdx.input.isKeyPressed(Input.Keys.LEFT)
				|| inputs[2]) {
			sprite.translateX(-speed);
		}

		if (player == Player.LEFT && Gdx.input.isKeyPressed(Input.Keys.W)
				|| player == Player.RIGHT && Gdx.input.isKeyPressed(Input.Keys.UP)
				|| inputs[0]) {
			sprite.translateY(speed);
		}
		if (player == Player.LEFT && Gdx.input.isKeyPressed(Input.Keys.S)
				|| player == Player.RIGHT && Gdx.input.isKeyPressed(Input.Keys.DOWN)
				|| inputs[1]) {
			sprite.translateY(-speed);
		}

		if (sprite.getX() < 0) {
			sprite.setX(0);
		} else if (sprite.getX() + sprite.getWidth() > Gdx.graphics.getWidth()) {
			sprite.setX(Gdx.graphics.getWidth() - sprite.getWidth());
		}

		if (sprite.getY() < 0) {
			sprite.setY(0);
		} else if (sprite.getY() + sprite.getHeight() > Gdx.graphics.getHeight()) {
			sprite.setY(Gdx.graphics.getHeight() - sprite.getHeight());
		}
	}

	public void draw(Batch batch) {
		sprite.draw(batch);
	}
}
