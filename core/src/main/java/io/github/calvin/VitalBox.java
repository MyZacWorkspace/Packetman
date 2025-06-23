package io.github.calvin;

import com.badlogic.gdx.math.Rectangle;

public class VitalBox extends Rectangle
{
    VitalBoxType vbt;
    
    public VitalBox(VitalBoxType vbt, float x, float y, float width, float height)
    {
        setVBT(vbt);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void setVBT(VitalBoxType vbt)
    {
        this.vbt = vbt;
    }
}
