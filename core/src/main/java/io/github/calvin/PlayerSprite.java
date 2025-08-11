package io.github.calvin;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


public class PlayerSprite extends Sprite
{
    private TextureAtlas atlas;
    private Array<TextureAtlas.AtlasRegion> walkRight;
    private Array<TextureAtlas.AtlasRegion> walkLeft;
    private Animation walkRightAnimation;
    private Animation walkLeftAnimation;

    private final float ANIMATION_FRAME_SPEED = 0.09f;

    //For cameraScroll
    Vector2 initialPosition = null;
    Vector2 recentPosition = null;

    //FIXME if you are not right then you are left, no need for two variables!
	//These two refer to controller input directions
    boolean isRight = false;
    boolean isLeft = false;
    boolean isActionFacingRight = true;
    boolean isJumping = false;
    boolean isAirborne = false;

	boolean isFacingRight = true;

    //Actions
    boolean isStandPunchActive;
    private Array<TextureAtlas.AtlasRegion> standPunch;
    private Animation standPunchAnimation;
    float standPunchAnimationTime;

    //THE PUNCH
    //Array<Array<Rectangle>> hitBoxes;

    //THE NEW PUNCH
    ActionFrameData punch;

    int currentFrameNumber;

    public PlayerSprite(float x, float y)
    {
        atlas = new TextureAtlas(Gdx.files.internal("sprites/calvinResprite.atlas"));

        walkRight = atlas.findRegions("walk");

        //Construct a walk left region by just flipping the walk right region
        walkLeft = atlas.findRegions("walk");

        for (TextureAtlas.AtlasRegion tar : walkLeft) {
            tar.flip(true, false);
        }

        //Set the boundaries for 'cutting' frames for the animation
        //System.out.println( "Width: " + walkRight.get(0).getRegionWidth() + " Height: " + walkRight.get(0).getRegionHeight());
        setBounds(x, y, walkRight.get(0).getRegionWidth(), walkRight.get(0).getRegionHeight());
        setScale(1 / 25.0f);

        //Set the initial region of this character
        setRegion(walkRight.get(0));
		//isFacingRight = true;

        //Construct Animations
        walkRightAnimation = new Animation<TextureAtlas.AtlasRegion>(ANIMATION_FRAME_SPEED, walkRight);
        walkLeftAnimation = new Animation<TextureAtlas.AtlasRegion>(ANIMATION_FRAME_SPEED, walkLeft);
        walkRightAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);
        
        //Action Animations
        isStandPunchActive = false;
        standPunch = atlas.findRegions("stand_punch");
        standPunchAnimation = new Animation<TextureAtlas.AtlasRegion>(0.04f, standPunch);
        standPunchAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        standPunchAnimationTime = 0.0f;

        //SAMPLE ATTACK FRAME DATA
        /*
        hitBoxes = new Array<Array<Rectangle>>();
        
        hitBoxes.add(new Array<Rectangle>());
        hitBoxes.add(new Array<Rectangle>());
        hitBoxes.add(new Array<Rectangle>());
        hitBoxes.add(new Array<Rectangle>());
        hitBoxes.add(new Array<Rectangle>());
        hitBoxes.add(new Array<Rectangle>());
        hitBoxes.add(new Array<Rectangle>());
        hitBoxes.add(new Array<Rectangle>());
        hitBoxes.add(new Array<Rectangle>());
        
        hitBoxes.get(4).add(new Rectangle(0.5f, 0.5f, 1.0f, 0.5f));
        */
        
        //IMPROVED IMPLEMENTATION
        punch = new ActionFrameData(standPunch.size);
        punch.appendHitbox(4, 0.2f, 0.1f, 1.0f, 0.5f);
    }

    public void setInitialPosition(Vector2 initialPosition)
    {
        this.initialPosition = initialPosition;
    }

    public Vector2 getInitialPosition()
    {
        return this.initialPosition;
    }

    public void setRecentPosition(Vector2 recentPosition)
    {
        this.recentPosition = recentPosition;
    }

    public Vector2 getRecentPosition()
    {
        return this.recentPosition;
    }
    
    public void update(float totalElapsedTime, float delta)
    {
        TextureRegion currentAtlasRegion = null;

	//System.out.println("isActionFacingRight: " + isActionFacingRight);
        
        if(isStandPunchActive)
        {

            //FIXME
            if (isActionFacingRight) //This must be always evaluating to false! So it keeps flipping
            {
                currentAtlasRegion = new TextureRegion((TextureAtlas.AtlasRegion) standPunchAnimation.getKeyFrame(standPunchAnimationTime));
            }
            else //not initially right, then always use the x-flipped frame (left)
            { //the issue is that it flips every time when facing left is true, causing flips back and forth
                currentAtlasRegion = new TextureRegion((TextureAtlas.AtlasRegion) standPunchAnimation.getKeyFrame(standPunchAnimationTime));
                currentAtlasRegion.flip(true, false);
            }

            setRegion(currentAtlasRegion);
            currentFrameNumber = standPunchAnimation.getKeyFrameIndex(standPunchAnimationTime);
            standPunchAnimationTime += delta;

            if(standPunchAnimation.isAnimationFinished(standPunchAnimationTime))
            {
                isStandPunchActive = false;
                standPunchAnimationTime = 0.0f;
                if (isRight)
                    setRegion((TextureAtlas.AtlasRegion) walkRight.get(0));
                else if (isLeft)
                    setRegion((TextureAtlas.AtlasRegion) walkLeft.get(0));
            }

        }
        else if (!isAirborne)
        {
            if (isRight)
	    {
                setRegion((TextureAtlas.AtlasRegion) walkRightAnimation.getKeyFrame(totalElapsedTime));
		isFacingRight = true;
	    }
            else if (isLeft)
	    {
                setRegion((TextureAtlas.AtlasRegion) walkLeftAnimation.getKeyFrame(totalElapsedTime));
		isFacingRight = false;
	    }
        }
        
    }
}
