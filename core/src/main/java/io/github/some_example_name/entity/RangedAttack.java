//Zaccery Tarver
//s1338565
package com.badlogic.entity;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.audio.Sound;

//A ranged attack is a move that triggers a player animation but the hit itself is tied to an external projectile
public class RangedAttack extends Move
{
    private String projectile;
    private float velocity;
    private float time;

    public RangedAttack(String name, float frameSpeed, Array<TextureAtlas.AtlasRegion> frames, Sound soundEffect, float velocity, String projectile)
    {
        super(name, frameSpeed, frames, soundEffect);

        setVelocity(velocity);
        setAnimatedSpritePath(projectile);
    }

    //Mutators
    public void setVelocity(float velocity)
    {
        this.velocity = velocity;
    }

    public void setAnimatedSpritePath(String projectile)
    {
        this.projectile = projectile;
    }

    //Accessors
    public float getVelocity()
    {
        return this.velocity;
    }
    public String getAnimatedSpritePath()
    {
        return this.projectile;
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
