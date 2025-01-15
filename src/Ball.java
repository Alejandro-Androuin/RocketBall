import java.util.ArrayList;
import java.util.List;

public class Ball extends Entity{

    private Cord centerCord;
    private Cord cord;

    private Vector velVec = new Vector(0,0);

    public static short hitBoxRadius = 150;

    private float maxResetYVel = 40;

    private GameState gs;

    private int mass = 8;

    private boolean isRed = true;

    public Ball(Cord centerCord)
    {
        this.cord = new Cord(centerCord);
        this.centerCord = centerCord;
    }

    public float getX()
    {
        return this.cord.getX();
    }

    public float getY()
    {
        return this.cord.getY();
    }

    public Vector getVelocityVector()
    {
        return this.velVec;
    }

    public  void setVelocity(Vector v)
    {
        this.velVec = v;
    }

    public float getXVel()
    {
        return this.velVec.getX();
    }

    public float getYVel()
    {
        return this.velVec.getY();
    }

    public void setXVel(float vel)
    {
        this.velVec.setX(vel);
    }

    public void setYVel(float vel)
    {
        this.velVec.setY(vel);
    }

    public void setGameState(GameState gs)
    {
        this.gs = gs;
    }

    public GameState getGameState()
    {
        return this.gs;
    }

    public Cord getCord()
    {
        return this.cord;
    }

    public float getMass()
    {
        return this.mass;
    }

    public short getHitboxRadius()
    {
        return this.hitBoxRadius;
    }

    public boolean isRed()
    {
        return this.isRed;
    }

    public void setRed(boolean b)
    {
        this.isRed = b;
    }

    public void setCord(Cord c)
    {
        this.cord.setX(c.getX());
        this.cord.setY(c.getY());
    }

    public void executeGameTick()
    {
        this.velVec.incrementY(-Game.gravity);

        this.velVec.multiplyX(1-Game.airRes);
        this.velVec.multiplyY(1-Game.airRes);

        move(1);
    }

    public void move(float percent)
    {
        this.cord.addX(this.velVec.getX()*percent);
        this.cord.addY(this.velVec.getY()*percent);
    }

    public void moveBack(float percent)
    {
        this.cord.addX(-this.velVec.getX()*percent);
        this.cord.addY(-this.velVec.getY()*percent);
    }

    public void reset()
    {
        this.cord = new Cord(centerCord);
        this.velVec.setX(0);
        this.velVec.setY((float)(Math.random()*maxResetYVel));
    }

    @Override
    public List<WallCollision> getWallCollisions()
    {
        List<WallCollision> collisions = new ArrayList<>();

        //Top Wall
        if(this.cord.getY()+this.hitBoxRadius>this.gs.getYMax()  & this.velVec.getY()>0)
        {
            float percent = 1-(this.cord.getY()+this.hitBoxRadius-this.gs.getYMax())/this.velVec.getY();
            collisions.add(new WallCollision(this, new Vector(this.velVec.getX(),-this.velVec.getY()*Game.wallBounce-1), percent, false, false));
        }

        //Right Wall
        if(this.cord.getX()+this.hitBoxRadius>this.gs.getXMax() & this.velVec.getX()>0)
        {
            float percent = 1-(this.cord.getX()+this.hitBoxRadius-this.gs.getXMax())/this.velVec.getX();
            collisions.add(new WallCollision(this, new Vector(-this.velVec.getX()*Game.wallBounce-1,this.velVec.getY()), percent, false, true));
        }

        //Bottom Wall
        if(this.cord.getY()-this.hitBoxRadius<0 & this.velVec.getY()<0)
        {
            float percent = 1-(this.cord.getY()-this.hitBoxRadius)/this.velVec.getY();
            collisions.add(new WallCollision(this, new Vector(this.velVec.getX(),-this.velVec.getY()*Game.wallBounce+1), percent, false, false));
        }

        //Left Wall
        if(this.cord.getX()-this.hitBoxRadius<0 & this.velVec.getX()<0)
        {
            float percent = 1-(this.cord.getX()-this.hitBoxRadius)/this.velVec.getX();
            collisions.add(new WallCollision(this, new Vector(-this.velVec.getX()*Game.wallBounce+1,this.velVec.getY()), percent, true, false));
        }

        return collisions;
    }

    public Collision calculateCollision(Entity e)
    {
        if(e instanceof Rocket)
        {
            Rocket r = (Rocket)e;
            return r.calculateCollision(this);
        }
        return null;
    }

    public float getAngle(Entity e)
    {
        Cord c = e.getCord();

        return this.cord.getAngle(c);
    }

    public boolean equals(Entity e)
    {
        if(e instanceof Ball)
        {
            return true;
        }

        return false;
    }

}
