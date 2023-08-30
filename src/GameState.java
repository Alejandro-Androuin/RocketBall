import java.util.ArrayList;
import java.util.List;

public class GameState {

    private Game game;

    private float xMax = Game.xStandard;
    private float yMax = Game.yStandard;

    private float startX = (float) 0.2;

    //public static Cord centerCord = new Cord(xMax/2, yMax/2);

    private List<Entity> entities = new ArrayList<>();
    private List<Rocket> rockets; //In order team1Bots -> team1Players -> team2Bots -> team2Players
    private Ball ball;
    private int ticksPassed = 0;
    private int overTimeTicks = Game.tps*30;
    private int countDownTicks;

    private short redScore = 0;
    private short blueScore = 0;

    private int redBallTouches = 0;
    private int blueBallTouches = 0;

    private int totalBallVelocity = 0;
    private int totalRocketVelocity = 0;

    private boolean gameOver = false;

    public GameState(Game game, List<Rocket> rockets)
    {
        this.game = game;

        this.xMax*=Math.pow(rockets.size()/2.0, 1/4.0);
        this.yMax*=Math.pow(rockets.size()/2.0, 1/4.0);

        this.rockets = rockets;
        this.ball = new Ball(new Cord(this.xMax/2, this.yMax/2));

        for(int i = 0; i < rockets.size(); i++)
        {
            this.rockets.get(i).setGameState(this);
        }

        this.ball.setGameState(this);

        this.entities.addAll(this.rockets);
        this.entities.add(this.ball);

        this.countDownTicks = 3*Game.tps; //3 seconds of countdown

        for(int i = 0; i < this.rockets.size(); i++) {
            Bot b = this.rockets.get(i).getBot();
            if(b != null)
            {
                if(b.getNetwork()!=null)
                {
                    b.getNetwork().setUpInputs(this, b);
                }
            }
        }

        setUpKickOff();
    }

    public void setUpKickOff()
    {
        this.ball.reset();

        List<Cord> startCords = new ArrayList<>(); //First half will contain cords for team1, second half with contain cords for team2

        //First team
        for(int i = 0; i < rockets.size()/2; i++)
        {
            startCords.add(new Cord(xMax*startX, (float) ((i+1.0)/(rockets.size()/2+1)*yMax)));
        }

        //Second team
        for(int i = 0; i < rockets.size()/2; i++)
        {
            startCords.add(new Cord(xMax*(1-startX), (float) ((i+1.0)/(rockets.size()/2+1)*yMax)));
        }

        //First team
        for(int i = 0; i < rockets.size()/2; i++)
        {
            int randIndex = (int) (Math.random() * (rockets.size() / 2 - i));
            this.rockets.get(i).reset(startCords.get(randIndex));
            startCords.remove(randIndex);
        }

        //Second team
        for(int i = 0; i < rockets.size()/2; i++)
        {
            int randIndex = (int) (Math.random() * (rockets.size() / 2 - i));
            this.rockets.get(i+rockets.size()/2).reset(startCords.get(randIndex));
            startCords.remove(randIndex);
        }

        this.countDownTicks = 3*Game.tps; //3 seconds of countdown

        for(int i = 0; i < this.rockets.size(); i++)
        {
            this.rockets.get(i).calculateBotMoves(this, true);
        }
    }

    public List<Rocket> getRockets()
    {
        return this.rockets;
    }

    public Ball getBall()
    {
        return this.ball;
    }

    public float getXMax()
    {
        return this.xMax;
    }

    public float getYMax()
    {
        return this.yMax;
    }

    public short getRedScore()
    {
        return this.redScore;
    }

    public short getBlueScore()
    {
        return this.blueScore;
    }

    public int getTicksPassed()
    {
        return this.ticksPassed;
    }

    public int getCountDownTicks()
    {
        return this.countDownTicks;
    }

    public boolean gameOver()
    {
        return this.gameOver;
    }

    public void endGame()
    {
        this.gameOver = true;
    }

    public int getRedBallTouches()
    {
        return this.redBallTouches;
    }

    public int getBlueBallTouches()
    {
        return this.blueBallTouches;
    }

    public float getAverageBallVelocity()
    {
        return (this.totalBallVelocity*1f)/this.ticksPassed;
    }

    public float getAverageRocketVelocity()
    {
        return (this.totalRocketVelocity*1f)/this.ticksPassed;
    }

    public void executeGameTick(boolean ignoreCountDown)
    {
        if(ignoreCountDown || this.countDownTicks == 0)
        {
            for(int i = 0; i < this.rockets.size(); i++)
            {
                this.rockets.get(i).calculateBotMoves(this, false);
            }

            for(int i = 0; i < this.rockets.size(); i++)
            {
                this.rockets.get(i).executeMove();
            }

            this.ball.executeGameTick();

            //Get Velocity Statistic
            for(int i = 0; i < this.rockets.size(); i++)
            {
                this.totalRocketVelocity += this.rockets.get(i).getVelocityVector().getMagnitude();
            }

            this.totalBallVelocity += this.ball.getVelocityVector().getMagnitude();

            enactCollisions();

            if(this.game.getGameLength()*60-this.ticksPassed < 1)
            {
                if(this.overTimeTicks < 1)
                {
                    this.game.endGame();
                    return;
                }
                else
                {
                    this.overTimeTicks--;
                    this.ticksPassed++;
                }
            }
            else
            {
                this.ticksPassed++;
            }
        }
        else
        {
            this.countDownTicks--;
        }
    }

    public void enactCollisions()
    {
        List<WallCollision> wallCollisions = new ArrayList<>();
        List<Collision> collisions = new ArrayList<>();

        for(int i = 0; i < entities.size(); i++)
        {
            List<WallCollision> w = entities.get(i).getWallCollisions();

            wallCollisions.addAll(w);
        }

        for(int i = 0; i < entities.size(); i++)
        {
            for(int j = i+1; j < entities.size(); j++)
            {
                Collision c = entities.get(i).calculateCollision(entities.get(j));
                if(c!=null)
                {
                    collisions.add(c);
                }
            }
        }

        while(wallCollisions.size() > 0 || collisions.size() > 0)
        {
            float bPercentWall = 1;
            int bIndexWall = -1;
            float bPercentCollide = 1;
            int bIndexCollide = -1;

            for(int i = 0; i < wallCollisions.size(); i++)
            {
                if(bIndexWall == -1)
                {
                    bIndexWall = 0;
                }
                if(wallCollisions.get(i).getPercent()<bPercentWall)
                {
                    bPercentWall = wallCollisions.get(i).getPercent();
                    bIndexWall = i;
                }
            }

            for(int i = 0; i < collisions.size(); i++)
            {
                if(bIndexCollide == -1)
                {
                    bIndexCollide = 0;
                }
                if(collisions.get(i).getPercent()<bPercentCollide)
                {
                    bPercentCollide = collisions.get(i).getPercent();
                    bIndexCollide = i;
                }
            }

            if(bIndexWall == -1)
            {
                Collision c = collisions.get(bIndexCollide);

                Entity e1 = collisions.get(bIndexCollide).getE1();
                Entity e2 = collisions.get(bIndexCollide).getE2();

                if(e1 instanceof Ball)
                {
                    Ball b = (Ball)e1;
                    Rocket r = (Rocket)e2;

                    b.setRed(r.isRed());

                    if(b.isRed())
                    {
                        this.redBallTouches++;
                    }
                    else
                    {
                        this.blueBallTouches++;
                    }
                }
                else if(e2 instanceof Ball)
                {
                    Ball b = (Ball)e2;
                    Rocket r = (Rocket)e1;

                    b.setRed(r.isRed());

                    if(b.isRed())
                    {
                        this.redBallTouches++;
                    }
                    else
                    {
                        this.blueBallTouches++;
                    }
                }

                c.collide();

                removeCollisions(wallCollisions, collisions, e1, e2);
                addCollisions(wallCollisions, collisions, e1, e2);
            }
            else if(bIndexCollide == -1)
            {
                WallCollision c = wallCollisions.get(bIndexWall);

                Entity e = wallCollisions.get(bIndexWall).getEntity();

                if(e instanceof Ball)
                {
                    Ball b = (Ball)e;

                    if(c.isRed() && !b.isRed())
                    {
                        this.blueScore++;
                        if(this.game.getGameLength()*60-this.ticksPassed < 1)
                        {
                            this.game.endGame();
                            return;
                        }
                        setUpKickOff();
                        return;
                    }
                    else if(c.isBlue() && b.isRed())
                    {
                        this.redScore++;
                        if(this.game.getGameLength()*60-this.ticksPassed < 1)
                        {
                            this.game.endGame();
                            return;
                        }
                        setUpKickOff();
                        return;
                    }
                    else
                    {
                        if(this.game.getGameLength()*60-this.ticksPassed < 1)
                        {
                            this.game.endGame();
                            return;
                        }
                        c.collide();
                    }
                }
                else
                {
                    c.collide();
                }

                removeCollisions(wallCollisions, collisions, e, null);
                addCollisions(wallCollisions, collisions, e, null);
            }
            else
            {
                if(bPercentCollide < bPercentWall)
                {
                    Collision c = collisions.get(bIndexCollide);

                    Entity e1 = collisions.get(bIndexCollide).getE1();
                    Entity e2 = collisions.get(bIndexCollide).getE2();

                    if(e1 instanceof Ball)
                    {
                        Ball b = (Ball)e1;
                        Rocket r = (Rocket)e2;

                        b.setRed(r.isRed());

                        if(b.isRed())
                        {
                            this.redBallTouches++;
                        }
                        else
                        {
                            this.blueBallTouches++;
                        }
                    }
                    else if(e2 instanceof Ball)
                    {
                        Ball b = (Ball)e2;
                        Rocket r = (Rocket)e1;

                        b.setRed(r.isRed());

                        if(b.isRed())
                        {
                            this.redBallTouches++;
                        }
                        else
                        {
                            this.blueBallTouches++;
                        }
                    }

                    c.collide();

                    removeCollisions(wallCollisions, collisions, e1, e2);
                    addCollisions(wallCollisions, collisions, e1, e2);
                }
                else
                {
                    WallCollision c = wallCollisions.get(bIndexWall);

                    Entity e = wallCollisions.get(bIndexWall).getEntity();

                    if(e instanceof Ball)
                    {
                        Ball b = (Ball)e;

                        if(c.isRed() && !b.isRed())
                        {
                            this.blueScore++;
                            setUpKickOff();
                            return;
                        }
                        else if(c.isBlue() && b.isRed())
                        {
                            this.redScore++;
                            setUpKickOff();
                            return;
                        }
                    }

                    c.collide();

                    removeCollisions(wallCollisions, collisions, e, null);
                    addCollisions(wallCollisions, collisions, e, null);
                }
            }
        }
    }

    public void addCollisions(List<WallCollision> wallCollisions, List<Collision> collisions, Entity e1, Entity e2)
    {
        if(e2 == null)
        {
            wallCollisions.addAll(e1.getWallCollisions());

            for(int i = 0; i < this.entities.size(); i++)
            {
                if(!this.entities.get(i).equals(e1))
                {
                    Collision c = e1.calculateCollision(this.entities.get(i));
                    if(c != null)
                    {
                        collisions.add(c);
                    }
                }
            }
        }
        else
        {
            wallCollisions.addAll(e1.getWallCollisions());
            wallCollisions.addAll(e2.getWallCollisions());

            for(int i = 0; i < this.entities.size(); i++)
            {
                if(!this.entities.get(i).equals(e1))
                {
                    Collision c = e1.calculateCollision(this.entities.get(i));
                    if(c != null)
                    {
                        collisions.add(c);
                    }
                }

                if(!this.entities.get(i).equals(e2) && !this.entities.get(i).equals(e1))
                {
                    Collision c = e2.calculateCollision(this.entities.get(i));
                    if(c != null)
                    {
                        collisions.add(c);
                    }
                }
            }
        }
    }

    public void removeCollisions(List<WallCollision> wallCollisions, List<Collision> collisions, Entity e1, Entity e2)
    {
        if(e2 == null)
        {
            for(int i = wallCollisions.size()-1; i > -1; i--)
            {
                if(wallCollisions.get(i).contains(e1))
                {
                    wallCollisions.remove(i);
                }
            }

            for(int i = collisions.size()-1; i > -1; i--)
            {
                if(collisions.get(i).contains(e1))
                {
                    collisions.remove(i);
                }
            }
        }
        else
        {
            for(int i = wallCollisions.size()-1; i > -1; i--)
            {
                if(wallCollisions.get(i).contains(e1) || wallCollisions.get(i).contains(e2))
                {
                    wallCollisions.remove(i);
                }
            }

            for(int i = collisions.size()-1; i > -1; i--)
            {
                if(collisions.get(i).contains(e1) || collisions.get(i).contains(e2))
                {
                    collisions.remove(i);
                }
            }
        }
    }

}
