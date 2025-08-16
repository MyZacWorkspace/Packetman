package io.github.calvin;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


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
	
    public EnemySprite(String enemyPath, float x, float y) 
    {
        //Get the atlas file
        atlas = new TextureAtlas(Gdx.files.internal((enemyPath)));
		walk = atlas.findRegions("walk");

        //Set bounds: position and size
		setBounds(x, y, ((float)walk.get(0).getRegionWidth())/Calvin.PIXELS_IN_METERS * scale, 
						((float)walk.get(0).getRegionHeight())/Calvin.PIXELS_IN_METERS * scale);
		
		//Set initial frame
		setRegion(walk.get(0));
    }

    public void update(float elapsedTime, boolean airborne)
    {
        
    }
	
	public float calculateDistanceFromPlayer()
	{
		//Return the distance from player using the distance formula
		// sqrt((x2 - x1)^2 + (y2 - y1)^2)
		return 0.0f;
	}
    
}
    
