package io.github.calvin;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class AnimatedSprite extends Sprite{

    private TextureAtlas atlas;
    private Array<Sprite> defaultSprites;
    private Animation<Sprite> defaultAnimation;
    
    float xSpeed;
    float ySpeed;
    private float ANIMATION_FRAME_SPEED;

    public AnimatedSprite(String atlasPath, float xSpeed, float ySpeed, float animationSpeed, float rescale, float xpos, float ypos)
    {
        atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        defaultSprites = atlas.createSprites("default");

        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;

        if (xSpeed > 0.0f) {
            if (ySpeed < 0.0f) {
                for (Sprite sp : defaultSprites) {
                    sp.rotate90(true);
                }
            }
            if (ySpeed > 0.0f) {
                for (Sprite sp : defaultSprites) {
                    sp.rotate90(false);
                }
            }

        }

        if (xSpeed < 0.0f) {
            for (Sprite sp : defaultSprites) {
                sp.flip(true, false);
            }
            if (ySpeed < 0.0f) {
                for (Sprite sp : defaultSprites) {
                    sp.rotate90(false);
                }
            }
            if (ySpeed > 0.0f) {
                for (Sprite sp : defaultSprites) {
                    sp.rotate90(true);
                }
            }

        }

        //This is how the sprite should be changed depending on the direction it is headed!

        ANIMATION_FRAME_SPEED = animationSpeed;
        defaultAnimation = new Animation<Sprite>(ANIMATION_FRAME_SPEED, defaultSprites);
        defaultAnimation.setPlayMode(Animation.PlayMode.LOOP);

        setBounds(xpos, ypos, defaultSprites.get(0).getRegionWidth(), defaultSprites.get(0).getRegionHeight());
        setScale(1 / rescale);
        setRegion(defaultSprites.get(0));
    }
    
    public void update(float totalElapsedTime)
    {
        setRegion((Sprite)defaultAnimation.getKeyFrame(totalElapsedTime));
    }
}
