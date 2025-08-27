package io.github.calvin;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.math.Vector2;


public class EnemySprite extends Sprite
{
	
	private TextureAtlas atlas;
	private float scale = 5.0f;
	//Frames
	private Array<TextureAtlas.AtlasRegion> walk;
	private Array<TextureAtlas.AtlasRegion> bite;
	//Animation
	private final float ANIMATION_FRAME_SPEED = 0.09f;
	private Animation animationWalk;
	private Animation animationBite;
	//FrameData
	ActionFrameData actionBite;
	//Function
	private float distanceFromPlayer;
	private final float THRESHOLD_DIST = 2.0f;
	//Action Logical
	boolean isFacingRight;

	int frameIndex;
	
    public EnemySprite(String enemyPath, float x, float y) 
    {
        //Get the atlas file
        atlas = new TextureAtlas(Gdx.files.internal((enemyPath)));
		walk = atlas.findRegions("walk");
		
		animationWalk = new Animation<TextureAtlas.AtlasRegion>(ANIMATION_FRAME_SPEED, walk);
		animationWalk.setPlayMode(Animation.PlayMode.LOOP);

		bite = atlas.findRegions("bite");
		animationBite = new Animation<TextureAtlas.AtlasRegion>(ANIMATION_FRAME_SPEED, bite);
		animationBite.setPlayMode(Animation.PlayMode.LOOP);
		
		actionBite = new ActionFrameData(bite.size);
		actionBite.appendHitbox(6, 0.2f, 0.1f, 1.0f, 0.5f);

        //Set bounds: position and size
		setBounds(x, y, ((float)walk.get(0).getRegionWidth())/Calvin.PIXELS_IN_METERS * scale, 
						((float)walk.get(0).getRegionHeight())/Calvin.PIXELS_IN_METERS * scale);
		
		//Set initial frame
		setRegion(walk.get(0));
    }

    public void update(float totalElapsedTime, Vector2 playerPos)
    {
		TextureRegion currentRegion;
		if(distanceFromPlayer > THRESHOLD_DIST)
		{
			frameIndex =  animationWalk.getKeyFrameIndex(totalElapsedTime);
			if (playerPos.x > this.getX())
				setRegion((TextureAtlas.AtlasRegion) animationWalk.getKeyFrame(totalElapsedTime));
			else {
				currentRegion = new TextureRegion(
						(TextureAtlas.AtlasRegion) animationWalk.getKeyFrame(totalElapsedTime));
				currentRegion.flip(true, false);
				setRegion(currentRegion);
			}

		}
		else
		{
			frameIndex = animationBite.getKeyFrameIndex(totalElapsedTime);
			if (playerPos.x > this.getX())
				setRegion((TextureAtlas.AtlasRegion) animationBite.getKeyFrame(totalElapsedTime));
			else {
				currentRegion = new TextureRegion(
						(TextureAtlas.AtlasRegion) animationBite.getKeyFrame(totalElapsedTime));
				currentRegion.flip(true, false);
				setRegion(currentRegion);
			}
		}
    }
	
	public Vector2 getPositionV2()
	{
		return new Vector2(getX(), getY());
	}
	
	public void setDistanceFromPlayer(float distanceFromPlayer)
	{
		this.distanceFromPlayer = distanceFromPlayer;
	}

	public float getDistanceFromPlayer()
	{
		return this.distanceFromPlayer;
	}
}
    
