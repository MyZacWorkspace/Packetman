package com.badlogic.entity;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.audio.Sound;

public class CloseAttack extends Move
{

    private int start; //The first frame the attack is active
    private int end; //The last frame the attack is active
    private float time = 0.0f;

    //The implied pseudocode condition is that end is always greater than start

    public CloseAttack(String name, float frameSpeed, Array <TextureAtlas.AtlasRegion> frames, Sound soundEffect, int start, int end)
    {
        super(name, frameSpeed, frames, soundEffect);
        setStart(start);
        setEnd(end);
    }

    //Mutators
    public void setStart(int start)
    {
        this.start = start;
    }
    public void setEnd(int end)
    {
        this.end = end;
    }

    //Accessors
    public int getStart()
    {
        return this.start;
    }
    public int getEnd()
    {
        return this.end;
    }

    //Other methods
    
    //Gets the hit duration of the move
    public int getHitDuration()
    {
        return this.end - this.start;
    }

    //Get the total duration of the move in frames
    //FIXME 
    //Get the current frame of animation
    /*
    public int getDuration()
    {

    }
    */

    //Returns whether the current frame of player is within the active period of this attack move
    public boolean isActive(int currentFrame)
    {
        return currentFrame >= start && currentFrame <= end;
    }

    public float getTime()
    {
        return this.time;
    }
    public void incrementTime(float time)
    {
        this.time += time;
    }
    public void setTime(float time)
    {
        this.time = time;
    }

}
