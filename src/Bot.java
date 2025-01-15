import java.util.List;

public class Bot{

    private String name;

    boolean tiltC = false;
    boolean tiltCC = false;
    boolean boost = false;

    private GameState gs;

    private Rocket r = null;

    private NeuralNetwork network = null;

    public Bot(String name)
    {
        this.name = name;
    }

    public void setNetwork(NeuralNetwork network)
    {
        this.network = network;
    }

    public NeuralNetwork getNetwork()
    {
        return this.network;
    }

    public String getName()
    {
        return this.name;
    }

    public void newGameState(GameState gs)
    {
        this.gs = gs;
        this.r = null;

        List<Rocket> rockets = this.gs.getRockets();

        for(int i = 0; i < rockets.size(); i++)
        {
            Bot b = rockets.get(i).getBot();

            if(b != null)
            {
                if(this.equals(b))
                {
                    this.r = rockets.get(i);
                    break;
                }
            }
        }
    }

    public void calculateMove(GameState gs, boolean kickoff)
    {
        if(this.network != null)
        {
            this.network.calculateOutput(gs, kickoff);
            this.tiltC = this.network.tiltC();
            this.tiltCC = this.network.tiltCC();
            this.boost = this.network.boost();
        }
        else if(this.r != null)
        {
            Ball b = gs.getBall();
            float angle = this.r.getAngle(b);

            if(angle > this.r.getDegree() + 180)
            {
                this.tiltC = false;
                this.tiltCC = true;
            }
            else if(angle > this.r.getDegree())
            {
                this.tiltC = true;
                this.tiltCC = false;
            }
            else if(angle < this.r.getDegree()-180)
            {
                this.tiltC = true;
                this.tiltCC = false;
            }
            else if(angle < this.r.getDegree())
            {
                this.tiltC = false;
                this.tiltCC = true;
            }
            else
            {
                this.tiltC = false;
                this.tiltCC = false;
            }

            this.boost = false;
        }
    }

    public void tiltC(boolean b)
    {
        this.tiltC = b;
    }

    public void tiltCC(boolean b)
    {
        this.tiltCC = b;
    }

    public void boost(boolean b)
    {
        this.boost = b;
    }

    public boolean tiltC()
    {
        return this.tiltC;
    }

    public boolean tiltCC()
    {
        return this.tiltCC;
    }

    public boolean boost()
    {
        return this.boost;
    }

    public boolean equals(Bot b)
    {
        if(b.getName().equalsIgnoreCase(this.name))
        {
            return true;
        }

        return false;
    }
}
