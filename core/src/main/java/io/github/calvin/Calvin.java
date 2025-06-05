package io.github.calvin;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Calvin extends Game 
{
    public SpriteBatch batch;
    public BitmapFont font;
    public ExtendViewport viewport;
    
    public ExtendViewport hud_viewport;
    //public BitmapFont hud_font;
    //public SpriteBatch hud_batch;

    final float HORIZONTAL_VP = 1200.0f;
    final float VERTICAL_VP = 780.0f;

    final int PIXELS_IN_METERS = 100;

    public void create()
    {
        batch = new SpriteBatch();

        font = new BitmapFont();

        viewport = new ExtendViewport(HORIZONTAL_VP / PIXELS_IN_METERS, VERTICAL_VP / PIXELS_IN_METERS);
        hud_viewport = new ExtendViewport(HORIZONTAL_VP / PIXELS_IN_METERS, VERTICAL_VP / PIXELS_IN_METERS);
        
        font.setUseIntegerPositions(false);
        font.getData().setScale(hud_viewport.getMinWorldHeight() / Gdx.graphics.getHeight());

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
