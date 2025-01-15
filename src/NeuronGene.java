import java.util.ArrayList;
import java.util.List;

public class NeuronGene {

    private Neuron neuron;
    private boolean isInput;
    private float val = 0;
    private int layer = 0;

    private List<ConnectionGene> connectionGenes;

    public NeuronGene(Neuron neuron, boolean isInput, int layer)
    {
        this.neuron = neuron;
        this.connectionGenes = new ArrayList<>();
        this.isInput = isInput;
        this.layer = layer;
    }

    public Neuron getNeuron()
    {
        return this.neuron;
    }

    public void setLayer(int layer)
    {
        this.layer = layer;
    }

    public int getLayer()
    {
        return this.layer;
    }

    public void addConnectionGene(ConnectionGene cg)
    {
        this.connectionGenes.add(cg);
    }

    public void removeConnectionGene(ConnectionGene cg)
    {
        this.connectionGenes.remove(cg);
    }

    public void removeConnection(NeuronGene ng)
    {
        for(int i = this.connectionGenes.size()-1; i > -1; i--)
        {
            if(this.connectionGenes.get(i).getNeuronGene1().equals(ng))
            {
                this.connectionGenes.remove(i);
            }
        }
    }

    public ConnectionGene getConnection(NeuronGene ng)
    {
        for(int i = this.connectionGenes.size()-1; i > -1; i--)
        {
            if(this.connectionGenes.get(i).getNeuronGene1().equals(ng))
            {
                return this.connectionGenes.get(i);
            }
        }

        return null;
    }

    public List<ConnectionGene> getConnections()
    {
        return this.connectionGenes;
    }

    public int numConnections()
    {
        return this.connectionGenes.size();
    }

    public void setValue(float val)
    {
        if(this.isInput)
        {
            this.val = val;
        }
    }

    public float getValue()
    {
        return this.val;
    }

    public boolean hasConnection(NeuronGene ng)
    {
        for(int i = 0; i < this.connectionGenes.size(); i++)
        {
            if(ng.equals(this.connectionGenes.get(i).getNeuronGene1()))
            {
                return true;
            }
        }

        return false;
    }

    public void calculateValue()
    {
        this.val = 0;

        for(int i = 0; i < this.connectionGenes.size(); i++)
        {
            this.val += this.connectionGenes.get(i).getValue();
        }

        this.val = this.neuron.activationFunction(this.val);
    }

    public boolean equals(NeuronGene ng)
    {
        if(this.neuron.getInnovationNumber() == ng.getNeuron().getInnovationNumber())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void print()
    {
        System.out.println("Neuron " + this.neuron.getInnovationNumber() + " is in layer " + this.layer);

        for(int i = 0; i < this.connectionGenes.size(); i++)
        {
            System.out.println("Neuron " + this.connectionGenes.get(i).getInnovationNumber1() + " connects to " + this.connectionGenes.get(i).getInnovationNumber2() + " weights are ");
            for(int j = 0; j < Connection.power; j++)
            {
                System.out.println(this.connectionGenes.get(i).getWeights().get(j) + " ");
            }
        }
    }
}
