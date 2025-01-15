import java.util.ArrayList;
import java.util.List;

public class Neuron {

    public static List<Neuron> allNeurons = new ArrayList<>(); //Sorted by innovation number

    private boolean isBias;

    private int innovationNumber;
    private String acticationFunction = "";
    /*
    "Sigmoid"
    "DoubleSigmoid"
    "reLU"
     */

    public static int chanceOfSigmoid = 5; //1/(1+Math.pow(Math.E, -val))
    public static int chanceOfDoubleSigmoid = 5; //2/(1+Math.pow(Math.E, -val))-1
    public static int chanceOfreLU = 2; //Math.max(0, val) capped at 1

    public Neuron(boolean isOutput, boolean isBias)
    {
        this.innovationNumber = allNeurons.size();
        if(isOutput)
        {
            this.acticationFunction = "DoubleSigmoid";
        }
        else
        {
            setRandomActivationFunction();
            this.acticationFunction = "DoubleSigmoid";
        }
        this.isBias = isBias;
        allNeurons.add(this);
    }

    public void setRandomActivationFunction()
    {
        List<Integer> functions = new ArrayList<>();
        functions.add(chanceOfSigmoid);
        functions.add(chanceOfDoubleSigmoid);
        functions.add(chanceOfreLU);

        int max = 0;
        for(int i = 0; i < functions.size(); i++)
        {
            max += functions.get(i);
        }

        int randomIndex = (int)(Math.random()*max);

        for(int i = 0; i < functions.size(); i++)
        {
            if(randomIndex < functions.get(i))
            {
                if(i==0)
                {
                    this.acticationFunction = "Sigmoid";
                    break;
                }
                else if(i==1)
                {
                    this.acticationFunction = "DoubleSigmoid";
                    break;
                }
                else if(i==2)
                {
                    this.acticationFunction = "reLU";
                }
            }
            else
            {
                randomIndex-=functions.get(i);
            }
        }

    }

    public int getInnovationNumber()
    {
        return this.innovationNumber;
    }

    public void setInnovationNumber(int i)
    {
        this.innovationNumber = i;
    }

    public boolean isBias()
    {
        return this.isBias;
    }

    public float activationFunction(float val)
    {
        if(this.acticationFunction.equalsIgnoreCase("Sigmoid"))
        {
            return (float)(1/(1+Math.pow(Math.E, -val)));
        }
        else if(this.acticationFunction.equalsIgnoreCase("DoubleSigmoid"))
        {
            return (float)(2/(1+Math.pow(Math.E, -val))-1);
        }
        else if(this.acticationFunction.equalsIgnoreCase("reLU"))
        {
            if(Math.max(0, val)>1)
            {
                return 1;
            }
            else
            {
                return Math.max(0, val);
            }
        }
        else
        {
            return val;
        }
    }

    public static int getInsertionIndex(Neuron n)
    {
        int low = 0;
        int high = allNeurons.size();
        int mid = -1;

        while(low < high)
        {
            mid = (low+high)/2;

            if(allNeurons.get(mid).getInnovationNumber() > n.getInnovationNumber())
            {
                high = mid;
            }
            else if(allNeurons.get(mid).getInnovationNumber() < n.getInnovationNumber())
            {
                low = mid+1;
            }
            else
            {
                return -1;
            }
        }

        if(allNeurons.size() == 0)
        {
            return 0;
        }

        return mid;
    }

    public static Neuron getInputNeuron(int number)
    {
        return allNeurons.get(number);
    }

    public static Neuron getOutputNeuron(int number)
    {
        return allNeurons.get(NeuralNetwork.numInputNeurons + number);
    }

    public static void createInputOutputs(int teamSize) //CALL BEFORE CONSTRUCTING FIRST NETWORKS
    {
        allNeurons = new ArrayList<>();

        NeuralNetwork.numInputNeurons =  12 + 7 + (2*teamSize-1)*13 + 1;
        for(int i = 0; i < NeuralNetwork.numInputNeurons; i++)
        {
            Neuron n;

            if(i == NeuralNetwork.numInputNeurons-1)
            {
                n = new Neuron(false, true);
            }
            else
            {
                n = new Neuron(false, false);
            }

            insertNeuron(n);
        }

        for(int i = 0; i < NeuralNetwork.outputNeurons; i++)
        {
            Neuron n = new Neuron(true, false);
            insertNeuron(n);
        }
    }

    public static void insertNeuron(Neuron n)
    {
        int index = getInsertionIndex(n);
        if(index!=-1)
        {
            allNeurons.add(index, n);
        }
    }

    public static void removeNeuron(Neuron n)
    {
        boolean removed = false;
        for(int i = 0; i < allNeurons.size(); i++)
        {
            if(allNeurons.get(i).equals(n))
            {
                allNeurons.remove(i);
                removed = true;
                break;
            }
        }

        if(removed)
        {
            correctInnovationNumbers();
        }
    }

    public static void correctInnovationNumbers()
    {
        for(int i = 0; i < allNeurons.size(); i++)
        {
            allNeurons.get(i).setInnovationNumber(i);
        }
    }

    public boolean equals(Neuron n)
    {
        if(this.innovationNumber == n.getInnovationNumber())
        {
            return true;
        }

        return false;
    }



}
