import java.util.List;

public class ConnectionGene {

    private Connection connection;
    private List<Float> weights;

    private NeuronGene ng1;
    private NeuronGene ng2;

    private boolean isBias = false;

    public ConnectionGene(Connection connection, List<Float> weights, NeuronGene ng1, NeuronGene ng2)
    {
        this.connection = connection;
        this.weights = weights;
        this.ng1 = ng1;
        this.ng2 = ng2;

        if(this.ng1.getNeuron().isBias())
        {
            this.isBias = true;
        }

        this.ng2.addConnectionGene(this);
    }

    public NeuronGene getNeuronGene1()
    {
        return this.ng1;
    }

    public NeuronGene getNeuronGene2()
    {
        return this.ng2;
    }

    public int getInnovationNumber1()
    {
        return ng1.getNeuron().getInnovationNumber();
    }

    public int getInnovationNumber2()
    {
        return ng2.getNeuron().getInnovationNumber();
    }

    public List<Float> getWeights()
    {
        return this.weights;
    }

    public void adjustWeights(List<Float> vals)
    {
        for(int i = 0; i < vals.size(); i++)
        {
            this.weights.set(i, this.weights.get(i) + vals.get(i));
        }
    }

    public float getValue()
    {
        float val = 0;
        for(int i = 0; i < this.weights.size(); i++)
        {
            val += Math.pow(this.ng1.getValue(), i+1)*weights.get(i);
        }

        return val;
    }

    public boolean isBias()
    {
        return this.isBias;
    }

    public int compare(ConnectionGene cg)
    {
        if(getInnovationNumber1()<cg.getInnovationNumber1())
        {
            return -1;
        }
        else if(getInnovationNumber1()>cg.getInnovationNumber1())
        {
            return 1;
        }
        else
        {
            if(getInnovationNumber2()<cg.getInnovationNumber2())
            {
                return -1;
            }
            else if(getInnovationNumber2()>cg.getInnovationNumber2())
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }

}
