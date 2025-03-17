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

    public void create()
    {
        batch = new SpriteBatch();

        font = new BitmapFont();

        //FIXME
        //May need to change in the future for uniform
        //World Scale
        viewport = new FitViewport(940,780);
        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());
    }

    public void render()
    {
        this.setScreen(new MainMenuScreen(this));
        super.render();
    }

    public void dispose()
    {
        batch.dispose();
        font.dispose();
    }
}
