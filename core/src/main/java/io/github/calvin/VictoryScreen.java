package io.github.calvin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class VictoryScreen implements Screen, ControllerListener
{
    final Calvin game;
	
	Controller firstController;

    BitmapFont title;
    BitmapFont subtitle;

    TextureAtlas playerAtlas;
    Sprite playerSprite;

    float time;

    public VictoryScreen(final Calvin game, Controller control, float time)
    {
        this.game = game;
        this.time = time;
        //System.out.println(game.viewport.getCamera().position);
        //System.out.println(game.hud_viewport.getCamera().position);
        //System.out.println("450 / pix in metres: " + 450 / game.PIXELS_IN_METERS);
		firstController = control;

        try{
            firstController.addListener(this);
        }
        catch (NullPointerException npe)
        {
            System.err.println("No controller");
        }

        title = new BitmapFont();
        title.setColor(Color.GREEN);
        title.setUseIntegerPositions(false);
        float titleScale = game.hud_viewport.getMinWorldHeight()/Gdx.graphics.getHeight();
        //System.out.println("titleScale: " + titleScale); 
        //0.0156
        title.getData().setScale(titleScale + 0.05f);

        subtitle = new BitmapFont();
        subtitle.setColor(Color.CYAN);
        subtitle.setUseIntegerPositions(false);
        float subtitleScale = game.hud_viewport.getMinWorldHeight()/Gdx.graphics.getHeight();
        //System.out.println("titleScale: " + titleScale); 
        //0.0156
        subtitle.getData().setScale(subtitleScale + 0.02f);

        playerAtlas = new TextureAtlas(Gdx.files.internal("sprites/packetman.atlas"));
        playerSprite = new Sprite(playerAtlas.findRegions("jump").get(2));
        
        playerSprite.setBounds(5.0f, 5.0f, 2.0f ,1.0f);
    }

    @Override
    public void render(float delta)
    {
        ScreenUtils.clear(Color.BLACK);
        
        game.batch.begin();

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        
        title.draw(game.batch, "Victory!", 1.0f, 5.0f);
        subtitle.draw(game.batch, "Score: " + ((int)Math.ceil((1 / time)*10000)), 1.0f, 4.0f); 
        playerSprite.draw(game.batch);
        game.font.draw(game.batch, "Press R to Return to the Main Menu", 1f,
                1f);

        game.batch.end();

       
        if(Gdx.input.isKeyPressed(Input.Keys.R))
        {
            //System.out.println("GAME START");
            try{
            firstController.removeListener(this);
            }
            catch(NullPointerException npe)
            {}
            game.setScreen(new LevelScreen(game, firstController));
        }
    }

    //Controller Support
    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {

        controller = firstController;

        if (controller.getButton(controller.getMapping().buttonR1))
        {
            controller.removeListener(this);
            game.setScreen(new MainMenuScreen(game, firstController));
            
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