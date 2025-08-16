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
	//Animation
	private final float ANIMATION_FRAME_SPEED = 0.09f;
    private Animation animationWalk;
	//Function
	private float distanceFromPlayer;
	private final float THRESHOLD_DIST = 3.0f;
	//Action Logical
	boolean isFacingRight;
	
    public EnemySprite(String enemyPath, float x, float y) 
    {
        //Get the atlas file
        atlas = new TextureAtlas(Gdx.files.internal((enemyPath)));
		walk = atlas.findRegions("walk");
		
		animationWalk = new Animation<TextureAtlas.AtlasRegion>(ANIMATION_FRAME_SPEED, walk);
		animationWalk.setPlayMode(Animation.PlayMode.LOOP);

        //Set bounds: position and size
		setBounds(x, y, ((float)walk.get(0).getRegionWidth())/Calvin.PIXELS_IN_METERS * scale, 
						((float)walk.get(0).getRegionHeight())/Calvin.PIXELS_IN_METERS * scale);
		
		//Set initial frame
		setRegion(walk.get(0));
    }

    public void update(float totalElapsedTime)
    {
		if(distanceFromPlayer > THRESHOLD_DIST)
		{
			setRegion((TextureAtlas.AtlasRegion) animationWalk.getKeyFrame(totalElapsedTime));
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
}
    
