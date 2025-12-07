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
    private Array<TextureAtlas.AtlasRegion> walk;
    private Animation animationWalk;

    private final float ANIMATION_FRAME_SPEED = 0.09f;

    //For cameraScroll
    Vector2 initialPosition = null;
    Vector2 recentPosition = null;

    //For jumping logic
    JumpStates jumpRecog = JumpStates.NONE;
    boolean isAllowedToJump = false;

    // Jump Action
    private Array<TextureAtlas.AtlasRegion> jumpFrames;
    private Animation jumpAnimation;
    boolean isJumpAnimationActive;
    float jumpAnimationTime;

    //Control faster movement
    float speedAccel = 1.0f;
    boolean isRunning = false;
    float originalWalkSpeed;
    //Boolean for animation modification

    //See if input is right or left (could never be both at the same time!)
    boolean isInputRight = false;
    boolean isInputLeft = false;
    //Whether character is currently facing right or not
    boolean isFacingRight = true;
    //Value will stay fixed for the duration of the current action, like punching
    boolean isActionFacingRight = true;
    //Whether they are airborne
    boolean isAirborne = false;

    int currentFrameNumber;

    public PlayerSprite(float x, float y)
    {
        atlas = new TextureAtlas(Gdx.files.internal("sprites/packetman.atlas"));

        walk = atlas.findRegions("walk");

        //Set the boundaries for 'cutting' frames for the animation
        setBounds(x, y, walk.get(0).getRegionWidth(), walk.get(0).getRegionHeight());
        setScale(1 / 25.0f);

        //Set the initial region of this character
        setRegion(walk.get(0));

        //Walk Animation
        animationWalk = new Animation<TextureAtlas.AtlasRegion>(ANIMATION_FRAME_SPEED, walk);
        animationWalk.setPlayMode(Animation.PlayMode.LOOP);
        
        //Jumping Animation
        isJumpAnimationActive = false;
        jumpFrames = atlas.findRegions("jump");
        jumpAnimation = new Animation<TextureAtlas.AtlasRegion>(0.09f, jumpFrames);
        jumpAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        jumpAnimationTime = 0.0f;

        //Frame Data
        /*
        punch = new ActionFrameData(jumpFrames.size);
        punch.appendHitbox(4, 0.2f, 0.1f, 1.0f, 0.5f);
        */
    }

    public void update(float totalElapsedTime, float delta)
    {
        TextureRegion currentAtlasRegion = null;

        //System.out.println("isActionFacingRight: " + isActionFacingRight);
        if (isJumpAnimationActive) {

            //FIXME
            if (isActionFacingRight) //This must be always evaluating to false! So it keeps flipping
            {
                currentAtlasRegion = new TextureRegion(
                        (TextureAtlas.AtlasRegion) jumpAnimation.getKeyFrame(jumpAnimationTime));
            } else //not initially right, then always use the x-flipped frame (left)
            { //the issue is that it flips every time when facing left is true, causing flips back and forth
                currentAtlasRegion = new TextureRegion(
                        (TextureAtlas.AtlasRegion) jumpAnimation.getKeyFrame(jumpAnimationTime));
                currentAtlasRegion.flip(true, false);
            }

            setRegion(currentAtlasRegion);
            currentFrameNumber = jumpAnimation.getKeyFrameIndex(jumpAnimationTime);
            jumpAnimationTime += delta;

            if (jumpAnimation.isAnimationFinished(jumpAnimationTime)) {
                isJumpAnimationActive = false;
                jumpAnimationTime = 0.0f;
                if (isInputRight)
                    setRegion((TextureAtlas.AtlasRegion) walk.get(0));
                else if (isInputLeft) {
                    currentAtlasRegion = new TextureRegion((TextureAtlas.AtlasRegion) walk.get(0));
                    currentAtlasRegion.flip(true, false);
                    setRegion(currentAtlasRegion);
                }
            }

        } else if (jumpRecog == JumpStates.TWO_F) {
            if (isInputRight) {
                setRegion((TextureAtlas.AtlasRegion) animationWalk.getKeyFrame(totalElapsedTime));
                isFacingRight = true;
            } else if (isInputLeft) {
                isFacingRight = false;
                currentAtlasRegion = new TextureRegion(
                        (TextureAtlas.AtlasRegion) animationWalk.getKeyFrame(totalElapsedTime));
                currentAtlasRegion.flip(true, false);
                setRegion(currentAtlasRegion);
            }
        }

    }
	
    public void setInitialPosition(Vector2 initialPosition) {
        this.initialPosition = initialPosition;
    }

    public Vector2 getInitialPosition() {
        return this.initialPosition;
    }

    public void setRecentPosition(Vector2 recentPosition) {
        this.recentPosition = recentPosition;
    }

    public Vector2 getRecentPosition() {
        return this.recentPosition;
    }

	public Vector2 getPositionV2()
	{
		return new Vector2(getX(), getY());
	}
}
