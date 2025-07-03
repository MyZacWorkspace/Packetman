package io.github.calvin;


import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class ActionFrameData 
{

    //The hitboxes present throughout the move
    Array<Array<Rectangle>> hitboxes;
    //The hurtboxes present throughout the move
    Array<Array<Rectangle>> hurtboxes;
    //number of frames in the action
    int numFrames;
    
    public ActionFrameData(int numFrames)
    {
        hitboxes = new Array<Array<Rectangle>>();
        hurtboxes = new Array<Array<Rectangle>>();
        for (int f = 0; f < numFrames; f++) {
            hitboxes.add(new Array<Rectangle>());
            hurtboxes.add(new Array<Rectangle>());
        }
    }
    
    public void appendHitbox(int frameNumber, float x_offset, float y_offset, float width, float height)
    {
        hitboxes.get(frameNumber).add(new Rectangle(x_offset, y_offset, width, height));
    }

    public void appendHurtbox(int frameNumber, float x_offset, float y_offset, float width, float height)
    {
        hitboxes.get(frameNumber).add(new Rectangle(x_offset, y_offset, width, height));
    }
    
    //We would never want to remove hit/hurt boxes since framedata is supposed to stay there.

    //When retrieving hitbox/hurtbox information, we only want the ones on the currently active frame

    public Array<Rectangle> getHitboxes(int currentFrameNumber)
    {
        return hitboxes.get(currentFrameNumber);
    }

    public Array<Rectangle> getHurtboxes(int currentFrameNumber)
    {
        return hurtboxes.get(currentFrameNumber);
    }

}
