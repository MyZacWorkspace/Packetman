package io.github.calvin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.controllers.*;

public class MainMenuScreen implements Screen, ControllerListener
{
    final Calvin game;
	
	Controller firstController;

    public MainMenuScreen(final Calvin game, Controller control)
    {
        this.game = game;
        //System.out.println(game.viewport.getCamera().position);
        //System.out.println(game.hud_viewport.getCamera().position);
        //System.out.println("450 / pix in metres: " + 450 / game.PIXELS_IN_METERS);
		firstController = control;

        try{
        }
        catch (NullPointerException npe)
        {
            System.err.println("No controller");
        }
        firstController.addListener(this);
    }

    @Override
    public void render(float delta)
    {
        ScreenUtils.clear(Color.BLACK);
        
        game.batch.begin();

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        
        game.font.draw(game.batch, "Calvin the Capybara :)" , 1.0f, 4.5f);
        game.font.draw(game.batch, "Click anywhere to begin!", 1f,
                1f);

        game.hud_viewport.apply();
        game.batch.setProjectionMatrix(game.hud_viewport.getCamera().combined);
        game.font.draw(game.batch, "It's a cool game", 0.5f, 6.0f);

        game.batch.end();

       
        if(Gdx.input.isKeyPressed(Input.Keys.Z))
        {
            //System.out.println("GAME START");
            game.setScreen(new LevelScreen(game, firstController));
        }
    }

    //Controller Support
    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {

        controller = firstController;

        //Jump Button
        if (controller.getButton(controller.getMapping().buttonR1))
        {
            controller.removeListener(this);
            game.setScreen(new LevelScreen(game, firstController));
            
        }
        //Doesn't matter what it returns
        return true;
    }
	

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) 
    { return true; }

    @Override
    public void connected(Controller controller) {

		System.out.println("Connection ESTABLISHED!");
		try {
            controller.addListener(this);
        } catch (NullPointerException npe) {
        }
		firstController = controller;
    }

    @Override
    public void disconnected(Controller controller) {

		System.out.println("DISCONNECTED " + controller.getName());
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) 
    { return true; }


    //FIXME use this to resize viewport properly
    @Override
    public void resize(int width, int height)
    {
        game.viewport.update(width, height, true);
        game.hud_viewport.update(width, height, true);
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