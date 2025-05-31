package io.github.calvin;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Calvin extends Game 
{
    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;
    
    public FitViewport hud_viewport;
    //public BitmapFont hud_font;
    //public SpriteBatch hud_batch;

    final int HORIZONTAL_VP = 1200;
    final int VERTICAL_VP = 780;

    final int PIXELS_IN_METERS = 100;

    public void create()
    {
        batch = new SpriteBatch();

        font = new BitmapFont();

       
        viewport = new FitViewport(HORIZONTAL_VP / PIXELS_IN_METERS, VERTICAL_VP / PIXELS_IN_METERS);
        hud_viewport = new FitViewport(HORIZONTAL_VP / PIXELS_IN_METERS, VERTICAL_VP / PIXELS_IN_METERS);
        
        font.setUseIntegerPositions(false);
        font.getData().setScale(hud_viewport.getWorldHeight() / Gdx.graphics.getHeight());

        this.setScreen(new MainMenuScreen(this));
    }

    public void render()
    {
        super.render();
    }

    public void dispose()
    {
        batch.dispose();
        font.dispose();
        //hud_batch.dispose();
        //hud_font.dispose();
    }
}
