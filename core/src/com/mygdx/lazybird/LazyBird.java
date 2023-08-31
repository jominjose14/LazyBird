package com.mygdx.lazybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class LazyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
//	ShapeRenderer shapeRenderer;
	Texture gameover;

	Texture[] birds;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	Circle birdCircle;
	int score = 0;
	int highscore = 0;
	int scoringTube = 0;
	BitmapFont font;

	int gameState = 0;
	float gravity = 1.3f;

	Texture topTube;
	Texture bottomTube;
	float gap = 620;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("background.jpg");
		gameover = new Texture("gameover.jpg");
//		shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		birdCircle = new Circle();
		birds = new Texture[15];
		for(int i=0;i<7;i++) {
			birds[i] = new Texture("up.png");
		}
		for(int i=7;i<14;i++) {
			birds[i] = new Texture("down.png");
		}
		birds[14] = new Texture("ice.png");

		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 0.8f;
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		startGame();
	}

	public void startGame() {
		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / (2*3);
		tubeVelocity = 4;

		for(int i = 0; i < numberOfTubes; i++) {
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth() - topTube.getWidth() / 2 + i * distanceBetweenTubes;
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	public void vibrate() {
		Gdx.input.vibrate(100);
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1) {

			if(tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
				score++;
				if(score % 5 == 0) {
					tubeVelocity += 0.5;
				}
//				Gdx.app.log("Score", String.valueOf(score));
				if(scoringTube < numberOfTubes - 1) {
					scoringTube++;
				} else {
					scoringTube = 0;
				}
			}

			if(Gdx.input.justTouched()) {
				velocity = -24;
			}

			for(int i = 0; i < numberOfTubes; i++) {
				if(tubeX[i] < -topTube.getWidth()) {
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				} else {
					tubeX[i] -= tubeVelocity;
				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}

			birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / (2*3), birds[flapState].getHeight() / (2*3));

//			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//			shapeRenderer.setColor(Color.RED);
//			shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

			for(int i = 0; i < numberOfTubes; i++) {
//				shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
//				shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

				if(Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
					gameState = 2;
					vibrate();
					velocity = -40;
				}

			}

//			shapeRenderer.end();

			if(birdY > 0) {
				velocity += gravity;
				birdY -= velocity;
			} else {
				gameState = 2;
				vibrate();
				velocity = -40;
			}
		} else if(gameState == 0){
			font.getData().setScale(17);
			font.draw(batch, "Lazy", Gdx.graphics.getWidth() / 3.6f, Gdx.graphics.getHeight() / 2 + 1.6f * birds[0].getHeight());
			font.draw(batch, "Bird", Gdx.graphics.getWidth() / 3.6f, Gdx.graphics.getHeight() / 2 + birds[0].getHeight());
			font.getData().setScale(4);
			font.draw(batch, "I won't fly", Gdx.graphics.getWidth() / 2 - 300, Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2);
			font.draw(batch, "unless you push me ;|", Gdx.graphics.getWidth() / 2 - 300, Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2 - 70);
			font.getData().setScale(10);

			if(Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if(gameState == 2){
			velocity += gravity;
			birdY -= velocity;
			batch.draw(gameover, 0, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2, Gdx.graphics.getWidth(), gameover.getHeight() * 1.5f);
			font.getData().setScale(3);
			font.draw(batch, "Touch to restart", Gdx.graphics.getWidth() / 2 - 155, Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2 - 50);
			font.getData().setScale(10);
			if(Gdx.input.justTouched()) {
				if(Gdx.input.isTouched()) {
					gameState = 0;
					flapState = 0;
					startGame();
					highscore = Math.max(highscore, score);
					score = 0;
					scoringTube = 0;
					velocity = 0;
				}
			}
		}

		flapState = (flapState+1)%14;

		if(gameState == 2)
			flapState = 14;

		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / (2*3), birdY, birds[flapState].getWidth() / 3, birds[flapState].getHeight() / 3);
		font.draw(batch, String.valueOf(score), 100, 200);
		int offset = (highscore/10)%10 > 0 ? 80 : 0;
		font.draw(batch, String.valueOf(highscore), 900 - offset, 200);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
