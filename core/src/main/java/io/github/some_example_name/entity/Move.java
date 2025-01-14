//Zaccery Tarver
//s1338565@monmouth.edu
package com.badlogic.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class Move 
{
    private String name;
    private Animation animation;
    private float frameSpeed;
    private Array<TextureAtlas.AtlasRegion> frames;
    private Sound soundEffect = null;
    private long soundID = 0;
    private boolean isActive = false;

    //Three different constructors
    //A move with sound and an tied animation
    public Move(String name, float frameSpeed, Array<TextureAtlas.AtlasRegion> frames, Sound soundEffect)
    {
        setName(name);
        setFrameSpeed(frameSpeed);
        setFrames(frames);
        setSoundEffect(soundEffect);
    }
    //A move without sound but an animation
    public Move(String name, float frameSpeed, Array<TextureAtlas.AtlasRegion> frames)
    {
        setName(name);
        setFrameSpeed(frameSpeed);
        setFrames(frames);
    }
    //A move with sound and no animation
    public Move(String name, Sound soundEffect)
    {
        setName(name);
        setSoundEffect(soundEffect);
    }

    //Mutators
    public void setName(String name)
    {
        this.name = name;
    }
    public void setFrameSpeed(float frameSpeed)
    {
        this.frameSpeed = frameSpeed;
    }
    public void setFrames(Array<TextureAtlas.AtlasRegion> frames)
    {
        this.frames = frames;
    }
    public void setSoundEffect(Sound soundEffect)
    {
        this.soundEffect = soundEffect;
    }
    public void setActive(boolean isActive)
    {
        this.isActive = isActive;
    }
    public void setAnimation(Animation.PlayMode pm)
    {
        this.animation = new Animation(frameSpeed, frames);
        this.animation.setPlayMode(pm);
    }

    //For playback
    public void setSoundID(long soundID)
    {
        this.soundID = soundID;
    }

    //Accessors
    public String getName()
    {
        return this.name;
    }
    public float getFrameSpeed()
    {
        return this.frameSpeed;
    }
    public Array<TextureAtlas.AtlasRegion> getFrames()
    {
        return this.frames;
    }
    public Sound getSoundEffect()
    {
        return this.soundEffect;
    }
    public Animation getAnimation()
    {
        return this.animation;
    }
    public boolean isActive()
    {
        return isActive;
    }

    public long getSoundID()
    {
        return this.soundID;
    }
}
