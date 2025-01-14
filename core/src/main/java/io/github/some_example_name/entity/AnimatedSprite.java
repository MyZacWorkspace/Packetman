//Adapted from Project 2, Zaccery Tarver s1338565
package com.badlogic.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

//A class for game objects like the coin that have one simple animation
public class AnimatedSprite extends Sprite
{
    private TextureAtlas myAtlas;

    private Array <TextureAtlas.AtlasRegion> idle;
    private Animation defaultAnimation;

    private boolean isRight;

    public AnimatedSprite(String path, boolean isRight)
    {
        //Get the atlas file
        myAtlas = new TextureAtlas(Gdx.files.internal((path)));

        idle = myAtlas.findRegions("idle");

        this.isRight = isRight;

        if(!isRight)
        {
            for (int r = 0; r < idle.size; r++)
            {
                idle.get(r).flip(true, false);
                //System.out.println("IS FLIPPING");
            }
        }

        defaultAnimation = new Animation(1/5f, idle);

        //System.out.println("IDLE: " + idle.size);

        //Set the boundary region of the sprite (all regions have the same dimensions)
        setBounds(0, 0, idle.get(0).getRegionWidth(), idle.get(0).getRegionHeight());

        //Set the starting region
        setRegion(idle.get(0)); //The first visual frame of walking right
    }


    //For Advancing Next Frame, based upon information retrieved from game
    public void update(float elapsedTime)
    {
        setRegion((TextureRegion) defaultAnimation.getKeyFrame(elapsedTime, true));
    }

    public boolean isRight()
    {
        return isRight;
    }
}

