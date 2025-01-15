import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Game {

    public static float xStandard = 4000; //width
    public static float yStandard = 2025; //height

    public static float gravity = (float) 0.2; //0.2
    public static float airRes = (float) 0.01; //0.01

    public static float wallBounce = (float) 0.8;

    private Timer frameUpdater;
    public static int tps = 60;

    private float gameLength = 60; //in seconds

    private GameState gameState;

    private boolean runInRealTime = false;
    private boolean cancel = false;
    private boolean gameOver = false;

    private List<Bot> team1Bots;
    private List<Player> team1Players;

    private List<Bot> team2Bots;
    private List<Player> team2Players;

    private byte teamSize = 0;

    private List<Rocket> rockets = new ArrayList<>(); //In order team1Bots -> team1Players -> team2Bots -> team2Players

    private boolean redWins = false;
    private boolean blueWins = false;

    private List<Bot> allBots = new ArrayList<>();

    private boolean elite = false;
    private boolean pro = false;

    public Game(List<Bot> team1Bots, List<Player> team1Players, List<Bot> team2Bots, List<Player> team2Players)
    {
        //team1Bots.size() + team1Players.size() must equal team2Bots.size() + team2Players.size()
        //bots must also contain networks that are compatible for the team size

        this.teamSize = (byte) (team1Bots.size() + team1Players.size());

        this.team1Bots = new ArrayList(team1Bots);
        this.team1Players = new ArrayList(team1Players);

        this.team2Bots = new ArrayList(team2Bots);
        this.team2Players = new ArrayList(team2Players);

        short id = 0;

        this.allBots.addAll(team1Bots);
        this.allBots.addAll(team2Bots);

        for(int i = 0; i < team1Bots.size(); i++)
        {
            Cord c = new Cord(0,0);
            this.rockets.add(new Rocket(c, (short) 0, team1Bots.get(i), true, id));
            id++;
        }

        for(int i = 0; i < team1Players.size(); i++)
        {
            Cord c = new Cord(0,0);
            this.rockets.add(new Rocket(c, (short) 0, team1Players.get(i), true, id));
            id++;
        }

        for(int i = 0; i < team2Bots.size(); i++)
        {
            Cord c = new Cord(0,0);
            this.rockets.add(new Rocket(c, (short) 0, team2Bots.get(i), false, id));
            id++;
        }

        for(int i = 0; i < team2Players.size(); i++)
        {
            Cord c = new Cord(0,0);
            this.rockets.add(new Rocket(c, (short) 0, team2Players.get(i), false, id));
            id++;
        }

        this.gameState = new GameState(this, this.rockets);
    }

    public void executeGameTick(boolean ignoreCountDown)
    {
        this.gameState.executeGameTick(ignoreCountDown);
    }

    public GameState getGameState()
    {
        return this.gameState;
    }

    public boolean redWin()
    {
        return this.redWins;
    }

    public boolean blueWin()
    {
        return this.blueWins;
    }

    public void cancel()
    {
        if(this.frameUpdater != null)
        {
            this.frameUpdater.cancel();
            this.cancel = true;
        }
    }

    public boolean cancelled()
    {
        return this.cancel;
    }

    public boolean gameOver()
    {
        return this.gameOver;
    }

    public void endGame()
    {
        this.gameState.endGame();
        this.gameOver = true;

        if(this.frameUpdater != null)
        {
            this.frameUpdater.cancel();
        }

        determineWinner();
    }

    public void determineWinner()
    {
        if(!this.redWins && !this.blueWins)
        {
            if(this.gameState.gameOver())
            {
                if(this.gameState.getRedScore() > this.gameState.getBlueScore())
                {
                    this.redWins = true;
                }
                else if(this.gameState.getRedScore() < this.gameState.getBlueScore())
                {
                    this.blueWins = true;
                }
                else
                {
                    if(this.gameState.getRedBallTouches() < this.gameState.getBlueBallTouches())
                    {
                        this.blueWins = true;
                    }
                    else if(this.gameState.getRedBallTouches() > this.gameState.getBlueBallTouches())
                    {
                        this.redWins = true;
                    }
                    else
                    {
                        if(Math.random() > 0.5)
                        {
                            this.redWins = true;
                        }
                        else
                        {
                            this.blueWins = true;
                        }
                    }
                }
            }
        }
    }

    public List<Bot> getAllBots()
    {
        return this.allBots;
    }

    public boolean isElite()
    {
        return this.elite;
    }

    public void elite(List<TrainingBot> bots)
    {
        for(int i = 0; i < this.allBots.size(); i++)
        {
            for(int j = 0; j < bots.size(); j++)
            {
                if(this.allBots.get(i).equals(bots.get(j).getBot()))
                {
                    this.elite = true;
                    return;
                }
            }
        }
    }

    public boolean isPro()
    {
        return this.pro;
    }

    public void pro()
    {
        this.pro = true;
    }

    public float getGameLength()
    {
        return this.gameLength;
    }

    public float getTimeRemaining()
    {
        return this.gameLength - ((float)this.gameState.getTicksPassed())/tps;
    }

    public int getCountDownTicks()
    {
        return this.gameState.getCountDownTicks();
    }

    public void runGameInstantly()
    {
        while(!this.gameOver)
        {
            executeGameTick(true);
        }
    }

    public void runGameRealTime()
    {
        this.frameUpdater = new Timer();
        this.runInRealTime = true;

        this.frameUpdater.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                executeGameTick(false);
            }
        }, 0L, (long)(1000/tps));
    }





}
