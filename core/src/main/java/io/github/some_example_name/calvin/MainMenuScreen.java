package com.badlogic.calvin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MainMenuScreen implements Screen {

    final Calvin game;
    OrthographicCamera camera;

    public MainMenuScreen (final Calvin gam) {

        game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Calvin.GAMEWIDTH, Calvin.GAMEHEIGHT);

        game.font.getData().setScale(1.5f);

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Display a single keypress menu.
        game.batch.begin();

        game.font.draw(game.batch, "Calvin the Capybara", 200, 350);
        game.font.draw(game.batch, "Press Space to begin!", (float) Calvin.GAMEWIDTH / 4, (float) Calvin.GAMEHEIGHT / 2);

        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game, this));
            dispose();
        }

    }
    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }
    @Override
    public void hide() {

    }
    @Override
    public void pause() {

    }
    @Override
    public void resume() {

    }
    @Override
    public void dispose() {

    }

}
