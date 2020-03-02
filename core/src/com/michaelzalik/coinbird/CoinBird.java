package com.michaelzalik.coinbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] bird;
	Texture deadBird;

	int birdState = 0;
	int pause = 0;
	int manY = 0;
	Rectangle manRectangle;

	float gravity = 0.3f;
	float velocity = 0;

	ArrayList<Integer> coinX = new ArrayList<Integer>();
	ArrayList<Integer> coinY = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<>();
	Texture coin;
	int coinCount;

	ArrayList<Integer> bombX = new ArrayList<Integer>();
	ArrayList<Integer> bombY = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<>();
	Texture bomb;
	int bombCount;

	Random random;
	int gameState;

	int score = 0;
	BitmapFont font;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");

		bird = new Texture[6];
		bird[0] = new Texture ("frame-1.png");
		bird[1] = new Texture ("frame-2.png");
		bird[2] = new Texture ("frame-3.png");
		bird[3] = new Texture ("frame-4.png");
		bird[4] = new Texture ("frame-5.png");
		bird[5] = new Texture ("frame-6.png");
		deadBird = new Texture ("deadBird.png");

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");

		random = new Random();

		manY = Gdx.graphics.getHeight() / 2;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

	}

	public void makeCoin() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinY.add((int)height);
		coinX.add(Gdx.graphics.getWidth());
	}

	public void makeBomb() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombY.add((int)height);
		bombX.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {
			//Coins
			if (coinCount < 100) {
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}

			coinRectangles.clear();

			for (int i = 0; i < coinX.size(); i++) {
				batch.draw(coin, coinX.get(i), coinY.get(i));
				coinX.set(i, coinX.get(i) - 4);
				coinRectangles.add(new Rectangle(coinX.get(i), coinY.get(i), coin.getWidth(), coin.getHeight()));
			}

			//Bombs
			if (bombCount < 250) {
				bombCount++;
			} else {
				bombCount = 0;
				makeBomb();
			}

			bombRectangles.clear();
			for (int i = 0; i < bombX.size(); i++) {
				batch.draw(bomb, bombX.get(i), bombY.get(i));
				bombX.set(i, bombX.get(i) - 8);
				bombRectangles.add(new Rectangle(bombX.get(i), bombY.get(i), bomb.getWidth(), bomb.getHeight()));
			}
			if (Gdx.input.justTouched()) {
				velocity = -10;
			}

			if (pause < 8) {
				pause++;
			} else {
				pause = 0;
				if (birdState < 3) {
					birdState++;
				} else {
					birdState = 0;
				}
			}

			velocity += gravity;
			manY -= velocity;

			if (manY <= 0) {
				manY = 0;
			}
		} else if (gameState == 0) {
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 2) {
			if (Gdx.input.justTouched()) {
				manY = Gdx.graphics.getHeight() / 2;
				gameState = 1;
				score = 0;
				velocity = 0;

				coinX.clear();
				coinY.clear();
				coinRectangles.clear();
				coinCount = 0;

				bombX.clear();
				bombY.clear();
				bombRectangles.clear();
				bombCount = 0;
			}
		}
		if (gameState == 2) {
			batch.draw(deadBird, Gdx.graphics.getWidth() / 2 - bird[birdState].getWidth() / 2, manY);
		} else {
			batch.draw(bird[birdState], Gdx.graphics.getWidth() / 2 - bird[birdState].getWidth() / 2, manY);
			manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - bird[birdState].getWidth() / 2, manY, bird[birdState].getWidth(), bird[birdState].getHeight());
		}
		for (int i = 0; i < coinRectangles.size(); i++) {
			if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
				score++;

				coinRectangles.remove(i);
				coinX.remove(i);
				coinY.remove(i);
				break;
			}
		}

		for (int i = 0; i < bombRectangles.size(); i++) {
			if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {
				Gdx.app.log("Bomb!", "Collision!");
				gameState = 2;
			}
		}
		font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}

