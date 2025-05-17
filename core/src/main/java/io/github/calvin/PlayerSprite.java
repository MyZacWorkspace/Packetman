package io.github.calvin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


public class PlayerSprite extends Sprite
{
    private TextureAtlas atlas;
    private Array<TextureAtlas.AtlasRegion> walkRight;
    private Array<TextureAtlas.AtlasRegion> walkLeft;
    private Animation walkRightAnimation;
    private Animation walkLeftAnimation;

    private final float ANIMATION_FRAME_SPEED = 0.2f;

    public PlayerSprite(float x, float y)
    {
        atlas = new TextureAtlas(Gdx.files.internal("sprites/calvin.atlas"));

        walkRight = atlas.findRegions("walk_right");
        
        //Construct a walk left region by just flipping the walk right region
        walkLeft = atlas.findRegions("walk_right");

        for (TextureAtlas.AtlasRegion tar : walkLeft)
        {
            tar.flip(true, false);
        }

        //Set the boundaries for 'cutting' frames for the animation
        //System.out.println( "Width: " + walkRight.get(0).getRegionWidth() + " Height: " + walkRight.get(0).getRegionHeight());
        setBounds(x, y, walkRight.get(0).getRegionWidth(), walkRight.get(0).getRegionHeight());
        setScale(1/25.0f);

        //Set the initial region of this character
        setRegion(walkRight.get(0));

        //Construct Animations
        walkRightAnimation = new Animation<TextureAtlas.AtlasRegion>(ANIMATION_FRAME_SPEED, walkRight);
        walkLeftAnimation = new Animation<TextureAtlas.AtlasRegion>(ANIMATION_FRAME_SPEED, walkLeft);
        walkRightAnimation.setPlayMode(Animation.PlayMode.LOOP);
        walkLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }
    
    public void update(float totalElapsedTime, float delta, int movementState)
    {
        switch (movementState) {
            case 1:
                setRegion((TextureAtlas.AtlasRegion) walkRightAnimation.getKeyFrame(totalElapsedTime));
                break;
            case 2:
                setRegion((TextureAtlas.AtlasRegion) walkLeftAnimation.getKeyFrame(totalElapsedTime));
            default:
        }
    }
    //1 - Walking Right
    //2 - Walking Left
}
