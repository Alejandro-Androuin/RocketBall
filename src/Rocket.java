import java.util.ArrayList;
import java.util.List;

public class Rocket extends Entity{

    private Bot bot = null;
    private Player player = null;

    private GameState gs;

    private short id;

    private Cord cord;
    private short degree;
    private short targetAngle = 0;
    private boolean isRed;

    private Vector velVec = new Vector(0,0);

    public static float boostStrength = (float) 0.5;
    private short degreeTurn = 3;
    private int mass = 20;

    public static short hRadius = 100;

    public Rocket(Cord c, short degree, Bot bot, boolean isRed, short id)
    {
        this.cord = c;
        this.degree = degree;
        this.bot = bot;
        this.isRed = isRed;
        this.id = id;
    }

    public Rocket(Cord c, short degree, Player player, boolean isRed, short id)
    {
        this.cord = c;
        this.degree = degree;
        this.player = player;
        this.isRed = isRed;
        this.id = id;
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

    public void setVelocity(Vector v)
    {
        this.velVec = v;
    }

    public void setTargetAngle(short angle)
    {
        this.targetAngle = angle;
    }

    public Vector getTargetVector()
    {
        return Vector.getUnitVector(this.targetAngle);
    }

    public float getMass()
    {
        return this.mass;
    }

    public Cord getCord()
    {
        return this.cord;
    }

    public short getDegree()
    {
        return this.degree;
    }

    public short getId()
    {
        return this.id;
    }

    public void setGameState(GameState gs)
    {
        this.gs = gs;
        if(this.bot != null)
        {
            this.bot.newGameState(gs);
        }
    }

    public GameState getGameState()
    {
        return this.gs;
    }

    public Bot getBot()
    {
        return this.bot;
    }

    public boolean isRed()
    {
        return this.isRed;
    }

    public String getName()
    {
        if(this.player != null)
        {
            return this.player.getName();
        }
        else
        {
            return this.bot.getName();
        }
    }

    public boolean boost()
    {
        if(this.bot!=null)
        {
            return this.bot.boost();
        }
        else
        {
            return this.player.boost();
        }
    }

    public void calculateBotMoves(GameState gs, boolean kickoff)
    {
        if(this.bot != null)
        {
            this.bot.calculateMove(gs, kickoff);
        }
    }

    public void executeMove()
    {
        if(this.bot != null)
        {
            executeBotMove();
        }
        else
        {
            executePlayerMove();
        }
    }

    public void executePlayerMove()
    {
        if(player.tiltC())
        {
            degree+=degreeTurn;
            if(degree>180)
            {
                degree-=360;
            }
        }
        if(player.tiltCC())
        {
            degree-=degreeTurn;
            if(degree<=-180)
            {
                degree+=360;
            }
        }
        if(player.boost())
        {
            float rad = (float) Math.toRadians(degree);
            float sin = (float) Math.sin(rad);
            float cos = (float) Math.cos(rad);

            this.velVec.incrementX(sin*boostStrength);
            this.velVec.incrementY(cos*boostStrength);
        }

        this.velVec.incrementY(-Game.gravity);
        this.velVec.multiplyX(1-Game.airRes);
        this.velVec.multiplyY(1-Game.airRes);

        move(1);
    }

    public void executeBotMove()
    {
        if(bot.tiltC())
        {
            degree+=degreeTurn;
            if(degree>180)
            {
                degree-=360;
            }
        }
        if(bot.tiltCC())
        {
            degree-=degreeTurn;
            if(degree<=-180)
            {
                degree+=360;
            }
        }
        if(bot.boost())
        {
            float rad = (float) Math.toRadians(degree);
            float sin = (float) Math.sin(rad);
            float cos = (float) Math.cos(rad);

            this.velVec.incrementX(sin*boostStrength);
            this.velVec.incrementY(cos*boostStrength);
        }

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

    public short getHitboxRadius()
    {
        return this.hRadius;
    }

    @Override
    public List<WallCollision> getWallCollisions()
    {
        List<WallCollision> collisions = new ArrayList<>();

        //Top Wall
        if(this.cord.getY()+this.hRadius>this.gs.getYMax()  & this.velVec.getY()>0)
        {
            float percent = 1-(this.cord.getY()+this.hRadius-this.gs.getYMax())/this.velVec.getY();
            collisions.add(new WallCollision(this, new Vector(this.velVec.getX(),-this.velVec.getY()*Game.wallBounce-1), percent, false, false));
        }

        //Right Wall
        if(this.cord.getX()+this.hRadius>this.gs.getXMax() & this.velVec.getX()>0)
        {
            float percent = 1-(this.cord.getX()+this.hRadius-this.gs.getXMax())/this.velVec.getX();
            collisions.add(new WallCollision(this, new Vector(-this.velVec.getX()*Game.wallBounce-1,this.velVec.getY()), percent, false, true));
        }

        //Bottom Wall
        if(this.cord.getY()-this.hRadius<0 & this.velVec.getY()<0)
        {
            float percent = 1-(this.cord.getY()-this.hRadius)/this.velVec.getY();
            collisions.add(new WallCollision(this, new Vector(this.velVec.getX(),-this.velVec.getY()*Game.wallBounce+1), percent, false, false));
        }

        //Left Wall
        if(this.cord.getX()-this.hRadius<0 & this.velVec.getX()<0)
        {
            float percent = 1-(this.cord.getX()-this.hRadius)/this.velVec.getX();
            collisions.add(new WallCollision(this, new Vector(-this.velVec.getX()*Game.wallBounce+1,this.velVec.getY()), percent, true, false));
        }

        return collisions;
    }

    public Collision calculateCollision(Entity e)
    {
        if(e.getCord().getDistance(this.cord)>e.getHitboxRadius()+this.hRadius)
        {
            return null;
        }

        Vector sepVector = new Vector(this.velVec);
        sepVector.subtractVector(e.getVelocityVector());

        Vector towardse = new Vector(e.getCord().getX()-this.cord.getX(), e.getCord().getY()-this.cord.getY());

        if(towardse.dotProduct(sepVector)<0)
        {
            return null;
        }

        float a = (float)Math.pow(sepVector.getX(),2) + (float)Math.pow(sepVector.getY(),2);
        float b = 2*sepVector.getX()*(this.cord.getX()-e.getCord().getX())+2*sepVector.getY()*(this.cord.getY()-e.getCord().getY());
        float c = (float)Math.pow(this.cord.getX()-e.getCord().getX(),2) + (float)Math.pow(this.cord.getY()-e.getCord().getY(),2) - (float)Math.pow(this.hRadius+e.getHitboxRadius(),2);

        float d = (float)Math.pow(b,2)-4*a*c;

        float percent = 0;

        if(d >= 0)
        {
            float p1 = (float)(b+Math.sqrt(d))/(2*a);
            float p2 = (float)(b-Math.sqrt(d))/(2*a);

            float max = Math.max(p1,p2);

            //Solution should always be the max value
            percent = 1-max;

        }
        else
        {
            return null;
        }

        return new Collision(this, e, percent);
    }

    public void reset(Cord c)
    {
        this.cord = c;
        this.velVec.setX(0);
        this.velVec.setY(0);
        this.degree = 0;
    }

    public float getAngle(Entity e)
    {
        Cord c = e.getCord();

        return this.cord.getAngle(c);
    }

    public boolean equals(Entity e)
    {
        if(e instanceof Rocket)
        {
            Rocket r = (Rocket)e;

            if(this.id==r.getId())
            {
                return true;
            }
        }

        return false;
    }

}
