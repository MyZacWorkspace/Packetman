package com.badlogic.entity;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


public class PlayerSprite extends Sprite {
    private TextureAtlas playerAtlas;
    private float frameSpeed1;

    public Move walkRight;
    public Move walkLeft;
    public Move jump;
    public CloseAttack hiPunchRight;
    public CloseAttack hiPunchLeft;
    public Move loKick;
    public RangedAttack waterAtkRight;
    public RangedAttack waterAtkLeft;
    public Move crouch;

    public int hitPoints = 12;
    public boolean isHurt = false;
    public float hurtTimer = 0.0f;

    public String currentMoveState = "";
    public int currentMoveFrame = 0;

    public boolean facingRight = true;

    public float timeSinceWater = 4f;

    //Spawn an animated player at position (x , y)
    public PlayerSprite() {
        //Get the atlas file
        playerAtlas = new TextureAtlas(Gdx.files.internal("character/calvin/calvin.atlas"));
        frameSpeed1 = 0.2f;
        setMoves();
        initSounds();

        setBounds(0, 0, walkLeft.getFrames().get(0).getRegionWidth(),
                walkLeft.getFrames().get(0).getRegionHeight());
        setRegion(walkRight.getFrames().get(0));
    }

    private void setMoves() {
        Array<TextureAtlas.AtlasRegion> temp;
        //Walking
        walkRight = new Move("walk_right", frameSpeed1, playerAtlas.findRegions("walk_right"),
                Gdx.audio.newSound(Gdx.files.internal("audio/sfx/calvin/Footstep_Right_Stone.ogg")));
        temp = playerAtlas.findRegions("walk_right");
        walkRight.setAnimation(Animation.PlayMode.LOOP);
        for (TextureAtlas.AtlasRegion tar : temp) {
            tar.flip(true, false);
        }
        walkLeft = new Move("walk_left", frameSpeed1, temp,
                Gdx.audio.newSound(Gdx.files.internal("audio/sfx/calvin/Footstep_Right_Stone.ogg")));
        walkLeft.setAnimation(Animation.PlayMode.LOOP);
        temp = null;
        //Jump
        jump = new Move("jump", Gdx.audio.newSound(Gdx.files.internal("audio/sfx/calvin/Jump.wav")));

        temp = null;

        //The punch attack
        hiPunchRight = new CloseAttack("hi_punch", 1/7f, playerAtlas.findRegions("hi_punch"),
                Gdx.audio.newSound(Gdx.files.internal("audio/sfx/calvin/hit-140236.mp3")), 4, 7);
        hiPunchRight.setAnimation(Animation.PlayMode.NORMAL);

        temp = playerAtlas.findRegions("hi_punch");
        for(TextureAtlas.AtlasRegion tar :temp)
        {
            tar.flip(true, false);
        }
        hiPunchLeft = new CloseAttack("hi_punch", 1/7f, temp,
                Gdx.audio.newSound(Gdx.files.internal("audio/sfx/calvin/hit-140236.mp3")), 4, 7);
        hiPunchLeft.setAnimation(Animation.PlayMode.NORMAL);

        temp = null;
        waterAtkRight = new RangedAttack("water_atk" , 1/8f, playerAtlas.findRegions("water_atk"),
                    Gdx.audio.newSound(Gdx.files.internal("audio/sfx/calvin/drop.wav")), 0.9f, "character/calvin/water.atlas");
        waterAtkRight.setAnimation(Animation.PlayMode.NORMAL);
        temp = playerAtlas.findRegions("water_atk");
        for(TextureAtlas.AtlasRegion tar : temp)
        {
            tar.flip(true, false);
        }
        waterAtkLeft = new RangedAttack("water_atk" , 1/8f, temp,
                    Gdx.audio.newSound(Gdx.files.internal("audio/sfx/calvin/drop.wav")), 0.9f, "character/calvin/water.atlas");
        waterAtkLeft.setAnimation(Animation.PlayMode.NORMAL);
        
        loKick = new CloseAttack("lo_kick", frameSpeed1, playerAtlas.findRegions("lo_kick"),
                Gdx.audio.newSound(Gdx.files.internal("audio/sfx/calvin/hit-140236.mp3")) , 3, 6);
    }

    private void initSounds()
    {
        walkRight.setSoundID(walkRight.getSoundEffect().loop());
    }

    //Elapsed Time represents the total time that has passed in game, making it useful for walking
    //But for one-time attacks, it is necessary for them to have their own timer. This will get the proper frame
    //instead of skipping to the last.
    public void update(float elapsedTime, boolean airborne)
    {
        if (timeSinceWater < 4f) {
            timeSinceWater += Gdx.graphics.getDeltaTime();
        }

        if (waterAtkRight.isActive()) {

            setRegion((TextureAtlas.AtlasRegion) waterAtkRight.getAnimation().getKeyFrame(waterAtkRight.getTime()));
            currentMoveState = "water_atk";
            waterAtkRight.incrementTime(Gdx.graphics.getDeltaTime());

            if (waterAtkRight.getAnimation().isAnimationFinished(waterAtkRight.getTime())) {
                waterAtkRight.setActive(false);
                setRegion(walkRight.getFrames().get(0));
                //System.out.println("Move finished");
                waterAtkRight.setTime(0.0f);

            }
        }
        else if(waterAtkLeft.isActive())
        {
            setRegion((TextureAtlas.AtlasRegion) waterAtkLeft.getAnimation().getKeyFrame(waterAtkLeft.getTime()));
            currentMoveState = "water_atk";
            waterAtkLeft.incrementTime(Gdx.graphics.getDeltaTime());

            if (waterAtkLeft.getAnimation().isAnimationFinished(waterAtkLeft.getTime())) {
                waterAtkLeft.setActive(false);
                setRegion(walkLeft.getFrames().get(0));
                //System.out.println("Move finished");
                waterAtkLeft.setTime(0.0f);

            }

        } else if (hiPunchRight.isActive()) {
            setRegion((TextureAtlas.AtlasRegion) hiPunchRight.getAnimation().getKeyFrame(hiPunchRight.getTime(), true));
            currentMoveFrame = hiPunchRight.getAnimation().getKeyFrameIndex(hiPunchRight.getTime());
            currentMoveState = "hi_punch";
            hiPunchRight.incrementTime(Gdx.graphics.getDeltaTime());

            if (hiPunchRight.getAnimation().isAnimationFinished(hiPunchRight.getTime())) {
                hiPunchRight.setActive(false);
                setRegion(walkRight.getFrames().get(0));
                //System.out.println("Punch done");
                hiPunchRight.setTime(0.0f);
            }
        } else if(hiPunchLeft.isActive())
        {
            setRegion((TextureAtlas.AtlasRegion) hiPunchLeft.getAnimation().getKeyFrame(hiPunchLeft.getTime(), true));
            currentMoveFrame = hiPunchLeft.getAnimation().getKeyFrameIndex(hiPunchLeft.getTime());
            currentMoveState = "hi_punch";
            hiPunchLeft.incrementTime(Gdx.graphics.getDeltaTime());

            if (hiPunchLeft.getAnimation().isAnimationFinished(hiPunchLeft.getTime())) {
                hiPunchLeft.setActive(false);
                setRegion(walkLeft.getFrames().get(0));
                System.out.println("Punch done");
                hiPunchLeft.setTime(0.0f);
            }
        }
        else {
            if (jump.isActive() && !airborne) {
                jump.getSoundEffect().play();
            }
            if (airborne) {
                // Pause Walking sound wall airborne
                walkRight.getSoundEffect().pause();
            } else if (walkRight.isActive() && !walkLeft.isActive()) {
                setRegion((TextureAtlas.AtlasRegion) walkRight.getAnimation().getKeyFrame(elapsedTime, true));
                TextureAtlas.AtlasRegion kf = (TextureAtlas.AtlasRegion) walkRight.getAnimation()
                        .getKeyFrame(elapsedTime, true);
                //Same walking sound for either direction
                walkRight.getSoundEffect().resume(walkRight.getSoundID());
                currentMoveState = "walk_right";

                facingRight = true;

            } else if (walkLeft.isActive() && !walkRight.isActive()) {
                setRegion((TextureRegion) walkLeft.getAnimation().getKeyFrame(elapsedTime, true));
                //Same walking sound for either direction
                walkRight.getSoundEffect().resume(walkRight.getSoundID());
                currentMoveState = "walk_left";
                //currentMoveFrame = walkLeft.getAnimation().getKeyFrameIndex(elapsedTime);

                facingRight = false;
            } else if (!walkLeft.isActive() && !walkRight.isActive()) {
                walkRight.getSoundEffect().pause();
            }
        }
    }
    
    public void dispose()
    {
        walkRight.getSoundEffect().dispose();
        walkLeft.getSoundEffect().dispose();
    }
}
