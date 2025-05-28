package io.github.calvin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class MainMenuScreen implements Screen
{
    final Calvin game;

    public MainMenuScreen(final Calvin game)
    {
        this.game = game;
        //System.out.println(game.viewport.getCamera().position);
        //System.out.println(game.hud_viewport.getCamera().position);
    }

    @Override
    public void render(float delta)
    {
        ScreenUtils.clear(Color.BLACK);
        
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        
        game.batch.begin();
        
        game.font.draw(game.batch, "Calvin the Capybara :)" , 100/game.PIXELS_IN_METERS, 450/game.PIXELS_IN_METERS);
        game.font.draw(game.batch, "Click anywhere to begin!", 100 / game.PIXELS_IN_METERS,
                100 / game.PIXELS_IN_METERS);

        game.batch.end();

        game.batch.begin();

        game.hud_viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.font.draw(game.batch, "It's a cool game", 50 / game.PIXELS_IN_METERS, 600 / game.PIXELS_IN_METERS);

        game.batch.end();
        
        /*
        game.hud_viewport.apply();
        game.hud_batch.setProjectionMatrix(game.hud_viewport.getCamera().combined);

        game.hud_batch.begin();

        game.hud_font.draw(game.hud_batch, "Calvin the Capybara :)", 100 / game.PIXELS_IN_METERS, 450 / game.PIXELS_IN_METERS);
        game.hud_font.draw(game.hud_batch, "Click anywhere to begin!", 100 / game.PIXELS_IN_METERS,
                100 / game.PIXELS_IN_METERS);
        game.hud_batch.end();
        */
        if(Gdx.input.isKeyPressed(Input.Keys.Z))
        {
            System.out.println("GAME START");
            game.setScreen(new LevelScreen(game));
        }
    }

    @Override
    public void resize(int width, int height)
    {
        game.viewport.update(width, height, true);
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