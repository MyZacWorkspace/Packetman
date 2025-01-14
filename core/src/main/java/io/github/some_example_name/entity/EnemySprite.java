package com.badlogic.entity;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;



public class EnemySprite extends Sprite
{

    private TextureAtlas playerAtlas;
    private float frameSpeed1;

    public Move walkRight;
    public Move walkLeft;
    public CloseAttack hiBiteMoveRight;
    public CloseAttack hiBiteMoveLeft;
    public Move loTail;
    public RangedAttack laserAtkRight;
    public RangedAttack laserAtkLeft;

    public int hitPoints = 6;
    public boolean isHurt = false;
    public float hurtTimer = 0.0f;

    public boolean facingRight;
    public boolean alreadyFlipped;

    public String currentMoveState = "";
    public int currentMoveFrame = 0;

    public float lastLaser = 0.0f;

    TextureAtlas.AtlasRegion prevFrame;
    //Spawn an animated player at position (x , y)
    public EnemySprite(String enemyPath) 
    {
        //Get the atlas file
        playerAtlas = new TextureAtlas(Gdx.files.internal((enemyPath)));
        frameSpeed1 = 0.2f;
        setMoves();
        initSounds();
        
        setBounds(0,0, walkLeft.getFrames().get(0).getRegionWidth(),
                        walkLeft.getFrames().get(0).getRegionHeight());
        setRegion(walkLeft.getFrames().get(0));
    }

    private void setMoves()
    {
        Array <TextureAtlas.AtlasRegion> temp;
       
        walkRight = new Move("walk_right" , frameSpeed1, playerAtlas.findRegions("walk_right") , 
                        Gdx.audio.newSound(Gdx.files.internal("audio/sfx/enemy/metal_01_b.wav")));
        temp = playerAtlas.findRegions("walk_right");
        for(TextureAtlas.AtlasRegion tar : temp)
        {
            tar.flip(true, false);
        }
        walkLeft = new Move("walk_left" , frameSpeed1, temp, 
                        Gdx.audio.newSound(Gdx.files.internal("audio/sfx/enemy/metal_01_b.wav")));
        temp = null;
        
        //Attack
        //FIXME get ASSETS
        hiBiteMoveRight = new CloseAttack("hi_bite", 1/9f, playerAtlas.findRegions("hi_bite"), 
                Gdx.audio.newSound(Gdx.files.internal("audio/sfx/enemy/metal-sound-fighting-game-87507.mp3")), 2, 4);
        hiBiteMoveRight.setAnimation(Animation.PlayMode.NORMAL);
        temp = playerAtlas.findRegions("hi_bite");
        for(TextureAtlas.AtlasRegion tar : temp)
        {
            tar.flip(true, false);
        }
        hiBiteMoveLeft = new CloseAttack("hi_bite", 1/9f, temp, 
        Gdx.audio.newSound(Gdx.files.internal("audio/sfx/enemy/metal-sound-fighting-game-87507.mp3")), 2, 4);
        hiBiteMoveLeft.setAnimation(Animation.PlayMode.NORMAL);
        
        temp = null;
        laserAtkRight = new RangedAttack("laser_eye" , 1/3f, playerAtlas.findRegions("laser_eye"), 
                    Gdx.audio.newSound(Gdx.files.internal("audio/sfx/enemy/laserShot.wav")), 1.5f, "character/enemy/laser.atlas");
        laserAtkRight.setAnimation(Animation.PlayMode.NORMAL);
        laserAtkRight.setTime(0.0f);
        temp = playerAtlas.findRegions("laser_eye");
        for(TextureAtlas.AtlasRegion tar : temp)
        {
            tar.flip(true, false);
        }
        laserAtkLeft = new RangedAttack("laser_eye" , 1/3f, temp, 
        Gdx.audio.newSound(Gdx.files.internal("audio/sfx/enemy/laserShot.wav")), 1.5f, "character/enemy/laser.atlas");
        laserAtkLeft.setAnimation(Animation.PlayMode.NORMAL);
        /*
        loTail = new CloseAttack("lo_tail", frameSpeed1, playerAtlas.findRegions("lo_tail"), 
                Gdx.audio.newSound(Gdx.files.internal("")) , 1, 3);
                */
    }
    private void initSounds()
    {
        //walkRight.setSoundID(walkRight.getSoundEffect().loop());
        //Lizard will probably not walk
    }


    //For Advancing Next Frame Based Upon the State
    
    public void update(float elapsedTime, boolean airborne, float playerX, float playerY)
    {
        if (Math.abs(playerX - this.getX()) <= 1.5f) {
            currentMoveState = "hi_bite";
            //Default bite
            if(facingRight)
            {
                setRegion((TextureAtlas.AtlasRegion) hiBiteMoveRight.getAnimation().getKeyFrame(hiBiteMoveRight.getTime()));
                hiBiteMoveRight.incrementTime(Gdx.graphics.getDeltaTime());

                currentMoveFrame = hiBiteMoveRight.getAnimation().getKeyFrameIndex(hiBiteMoveRight.getTime());

                if (hiBiteMoveRight.getAnimation().isAnimationFinished(hiBiteMoveRight.getTime())) {
                    setRegion(walkRight.getFrames().get(0));
                    hiBiteMoveRight.setTime(0.0f);
                }
            }
            else
            {
                setRegion((TextureAtlas.AtlasRegion) hiBiteMoveLeft.getAnimation().getKeyFrame(hiBiteMoveLeft.getTime()));
                hiBiteMoveLeft.incrementTime(Gdx.graphics.getDeltaTime());

                currentMoveFrame = hiBiteMoveLeft.getAnimation().getKeyFrameIndex(hiBiteMoveLeft.getTime());

                if (hiBiteMoveLeft.getAnimation().isAnimationFinished(hiBiteMoveLeft.getTime())) {
                    setRegion(walkLeft.getFrames().get(0));
                    hiBiteMoveLeft.setTime(0.0f);
                }
            }
        }
        else if((laserAtkRight.isActive() || laserAtkLeft.isActive()) &&
            Math.abs(playerX - this.getX()) < 3 && Math.abs(playerX - this.getX()) > 1.5f)
        {
            if(facingRight)
            {
                setRegion((TextureAtlas.AtlasRegion) laserAtkRight.getAnimation().getKeyFrame(laserAtkRight.getTime()));
                laserAtkRight.incrementTime(Gdx.graphics.getDeltaTime());
    
                if(laserAtkRight.getAnimation().isAnimationFinished(laserAtkRight.getTime()))
                {
                    setRegion(walkRight.getFrames().get(0));
                    laserAtkRight.setTime(0.0f);
                    laserAtkRight.setActive(false);
                }
            }
            else
            {
                setRegion((TextureAtlas.AtlasRegion) laserAtkLeft.getAnimation().getKeyFrame(laserAtkLeft.getTime()));
                laserAtkLeft.incrementTime(Gdx.graphics.getDeltaTime());
    
                if(laserAtkLeft.getAnimation().isAnimationFinished(laserAtkLeft.getTime()))
                {
                    setRegion(walkLeft.getFrames().get(0));
                    laserAtkLeft.setTime(0.0f);
                    laserAtkLeft.setActive(false);
                }
            }
         
            //System.out.println("LASER TIME!");
        } else {
            if (airborne) {
                // Pause Walking sound wall airborne
                walkRight.getSoundEffect().pause();
            } else if (playerX >= this.getX()) {
                setRegion(walkRight.getFrames().get(0));
                //Same walking sound for either direction
                //walkRight.getSoundEffect().resume(walkRight.getSoundID());
                facingRight = true;

            } else if (playerX < this.getX()) {
                setRegion(walkLeft.getFrames().get(0));
                //Same walking sound for either direction
                //walkRight.getSoundEffect().resume(walkRight.getSoundID());
                facingRight = false;

            } else if (!walkLeft.isActive() && !walkRight.isActive()) {
                walkRight.getSoundEffect().pause();
            }
        }

    }
    
}
    
