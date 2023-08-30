import java.util.ArrayList;
import java.util.List;

public class Cord {

    private float x;
    private float y;

    public Cord(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Cord(Cord cord)
    {
        this.x = cord.getX();
        this.y = cord.getY();
    }

    public float getX()
    {
        return this.x;
    }

    public float getY()
    {
        return this.y;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public void addX(float x)
    {
        this.x+=x;
    }

    public void addY(float y)
    {
        this.y+=y;
    }

    public void addVector(Vector v)
    {
        this.x+=v.getX();
        this.y+=v.getY();
    }

    public float getDistance(Cord c)
    {
        return (float)(Math.sqrt(Math.pow(this.x-c.getX(),2) + Math.pow(this.y-c.getY(), 2)));
    }

    public Pixel getPixel(GameState gs)
    {
        short x = (short)(((this.x/gs.getXMax()))*(WindowPanel.width-WindowPanel.xDistBorder*2)+WindowPanel.xDistBorder);
        short y = (short)(((gs.getYMax()-this.y)/gs.getYMax())*(WindowPanel.height-WindowPanel.yDistBorder*2)+WindowPanel.yDistBorder);
        return new Pixel(x,y);
    }

    public float getAngle(Cord c)
    {
        Vector v = new Vector(c.getX()-this.x, c.getY()-this.y);
        return v.getAngle();
    }

    public static float convertXLengthToPixel(float x, GameState gs)
    {
        return (short)((x/gs.getXMax())*(WindowPanel.width-WindowPanel.xDistBorder*2));
    }

    public static float convertYLengthToPixel(float y, GameState gs)
    {
        return (short)(((y)/gs.getYMax())*(WindowPanel.height-WindowPanel.yDistBorder*2));
    }

    public boolean equals(Cord c)
    {
        if(this.x==c.getX() & this.y==c.getY())
        {
            return true;
        }

        return false;
    }



}
