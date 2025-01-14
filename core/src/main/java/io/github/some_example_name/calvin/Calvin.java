package com.badlogic.calvin;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Calvin extends Game {

    public static final int GAMEWIDTH = 800;
    public static final int GAMEHEIGHT = 400;

    SpriteBatch batch;
    BitmapFont font;

    public void create() {

        batch = new SpriteBatch();
        font = new BitmapFont();
        this.setScreen(new MainMenuScreen(this));
        

    }

    public void render() {
        super.render();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();

    }


}
