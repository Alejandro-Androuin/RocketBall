import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {

    /* INPUT NEURONS
    1. Ball x
    2. Ball y
    3. Magnitude of To Ball Vector scaled between (roughly) 0 and 1
    4. Unit Vector from self x to ball x
    5. Unit Vector from self y to ball y
    6. Magnitude of Separation Vector scaled between (roughly) 0 and 1
    7. Unit Separation Vector x
    8. Unit Separation Vector y
    9. Magnitude of To Ball Velocity scaled between (roughly) 0 and 1
    10. Unit ballXVel
    11. Unit ballYVel
    12. ball isOnTeam

    13. Rocket x
    14. Rocket y
    15. Magnitude of Rocket Velocity scaled between (roughly) 0 and 1
    16. Rocket XVel
    17. Rocket YVel
    18. Rocket xRotation
    19. Rocket yRotation

    teamSize-1 more team Rockets and teamSize more enemy Rockets. All include
    1. Rocket x
    2. Rocket y
    3. Magnitude of To Rocket Vector scaled between (roughly) 0 and 1
    4. Vector from self x to rocket x
    5. Vector from self y to rocket y
    6. Magnitude of Separation Vector scaled between (roughly) 0 and 1
    7. Unit Separation Vector x
    8. Unit Separation Vector y
    9. Magnitude of Rocket Velocity scaled between (roughly) 0 and 1
    10. Rocket XVel
    11. Rocket YVel
    12. Rocket xRotation
    13. Rocket yRotation

    1 Bias Neuron
    1. Bias

    */

    /* OUTPUT NEURONS
    1. targetXRotation
    2. targetYRotation
    3. boost
    */

    public static int tpe = 10;

    public static int numInputNeurons = 0;
    public static int outputNeurons = 3;

    private List<Rocket> rockets;
    private Ball ball;

    private List<List<NeuronGene>> layeredNeuronGenes = new ArrayList<>();
    private List<NeuronGene> neuronGenes = new ArrayList<>();
    private List<ConnectionGene> connectionGenes = new ArrayList<>();

    private boolean tiltC = false;
    private boolean tiltCC = false;
    private boolean boost = false;

    private float fitness = 0;

    private float mutateConnection = (float)0.02;
    private float mutateConnectionVary = (float)0.5;

    private float mutateAddConnection = (float)0.05;
    private float mutateAddNeuron = (float)0.02;

    public static float cDisjoint = (float)20;
    public static float cWeight = 8;

    public NeuralNetwork()
    {
        generateBlankNetwork(true);
    }

    public NeuralNetwork(NeuralNetwork nn)
    {
        List<List<NeuronGene>> layeredNeurons = nn.getLayeredNeuronGenes();
        List<ConnectionGene> cgs = nn.getConnectionGenes();

        for(int i = 0; i < layeredNeurons.size(); i++)
        {
            for(int j = 0; j < layeredNeurons.get(i).size(); j++)
            {
                Neuron n = layeredNeurons.get(i).get(j).getNeuron();
                boolean isInput = false;
                if(i == 0)
                {
                    isInput = true;
                }
                int layer = i;

                NeuronGene ng = new NeuronGene(n, isInput, layer);
                insertNeuronGene(ng);
            }
        }

        for(int i = 0; i < cgs.size(); i++)
        {
            NeuronGene ng1 = findNeuronGene(cgs.get(i).getNeuronGene1().getNeuron());
            NeuronGene ng2 = findNeuronGene(cgs.get(i).getNeuronGene2().getNeuron());

            Connection c = Connection.getConnection(ng1.getNeuron(), ng2.getNeuron());
            ConnectionGene cg = new ConnectionGene(c, new ArrayList<>(cgs.get(i).getWeights()), ng1, ng2);

            insertConnectionGene(cg);
        }
    }

    //PRE REQ: n1 is more fit than n2
    public NeuralNetwork(NeuralNetwork n1, NeuralNetwork n2)
    {
        //First add all neuron genes
        List<List<NeuronGene>> layeredNeurons = n1.getLayeredNeuronGenes();
        List<ConnectionGene> cg1 = n1.getConnectionGenes();
        List<ConnectionGene> cg2 = n2.getConnectionGenes();

        for(int i = 0; i < layeredNeurons.size(); i++)
        {
            for(int j = 0; j < layeredNeurons.get(i).size(); j++)
            {
                Neuron n = layeredNeurons.get(i).get(j).getNeuron();
                boolean isInput = false;
                if(i == 0)
                {
                    isInput = true;
                }
                int layer = i;

                NeuronGene ng = new NeuronGene(n, isInput, layer);
                insertNeuronGene(ng);
            }
        }

        int indexcg1 = 0;
        int indexcg2 = 0;

        //next add all connection genes
        while(indexcg1 < cg1.size() || indexcg2 < cg2.size())
        {
            if(indexcg1 >= cg1.size())
            {
                //All that remain are genes from less fit parent. break
                break;
            }
            else if(indexcg2 >= cg2.size())
            {
                //Add from cg1
                NeuronGene ng1 = findNeuronGene(cg1.get(indexcg1).getNeuronGene1().getNeuron());
                NeuronGene ng2 = findNeuronGene(cg1.get(indexcg1).getNeuronGene2().getNeuron());

                Connection c = Connection.getConnection(ng1.getNeuron(), ng2.getNeuron());
                ConnectionGene cg = new ConnectionGene(c, new ArrayList<>(cg1.get(indexcg1).getWeights()), ng1, ng2);

                insertConnectionGene(cg);

                indexcg1++;

                continue;
            }

            int compare = cg1.get(indexcg1).compare(cg2.get(indexcg2));

            if(compare == -1)
            {
                //Add from cg1
                NeuronGene ng1 = findNeuronGene(cg1.get(indexcg1).getNeuronGene1().getNeuron());
                NeuronGene ng2 = findNeuronGene(cg1.get(indexcg1).getNeuronGene2().getNeuron());

                Connection c = Connection.getConnection(ng1.getNeuron(), ng2.getNeuron());
                ConnectionGene cg = new ConnectionGene(c, new ArrayList<>(cg1.get(indexcg1).getWeights()), ng1, ng2);

                insertConnectionGene(cg);

                indexcg1++;
            }
            else if(compare == 1)
            {
                indexcg2++;
            }
            else if(compare == 0)
            {
                if(Math.random() < 0.5)
                {
                    //Add from cg1
                    NeuronGene ng1 = findNeuronGene(cg1.get(indexcg1).getNeuronGene1().getNeuron());
                    NeuronGene ng2 = findNeuronGene(cg1.get(indexcg1).getNeuronGene2().getNeuron());

                    Connection c = Connection.getConnection(ng1.getNeuron(), ng2.getNeuron());
                    ConnectionGene cg = new ConnectionGene(c,  new ArrayList<>(cg1.get(indexcg1).getWeights()), ng1, ng2);

                    insertConnectionGene(cg);
                }
                else
                {
                    //Add from cg2
                    NeuronGene ng1 = findNeuronGene(cg2.get(indexcg2).getNeuronGene1().getNeuron());
                    NeuronGene ng2 = findNeuronGene(cg2.get(indexcg2).getNeuronGene2().getNeuron());

                    Connection c = Connection.getConnection(ng1.getNeuron(), ng2.getNeuron());
                    ConnectionGene cg = new ConnectionGene(c,  new ArrayList<>(cg2.get(indexcg2).getWeights()), ng1, ng2);

                    insertConnectionGene(cg);
                }

                indexcg1++;
                indexcg2++;
            }
        }

        this.mutate();
    }

    public void generateBlankNetwork(boolean createConnections)
    {
        layeredNeuronGenes = new ArrayList<>();
        layeredNeuronGenes.add(new ArrayList<>());

        for(int i = 0; i < NeuralNetwork.numInputNeurons; i++)
        {
            Neuron n = Neuron.getInputNeuron(i);
            NeuronGene ng = new NeuronGene(n, true, 0);
            insertNeuronGene(ng);
        }

        layeredNeuronGenes.add(new ArrayList<>());
        for(int i = 0; i < NeuralNetwork.outputNeurons; i++)
        {
            Neuron n = Neuron.getOutputNeuron(i);
            NeuronGene ng = new NeuronGene(n, false, 1);
            insertNeuronGene(ng);
        }

        if(createConnections)
        {
            for(int i = 0; i < this.layeredNeuronGenes.get(1).size(); i++)
            {
                for(int j = 0; j < this.layeredNeuronGenes.get(0).size(); j++)
                {
                    Connection c = Connection.getConnection(this.layeredNeuronGenes.get(0).get(j).getNeuron(), this.layeredNeuronGenes.get(1).get(i).getNeuron());
                    if(c == null)
                    {
                        c = new Connection(this.layeredNeuronGenes.get(0).get(j).getNeuron(), this.layeredNeuronGenes.get(1).get(i).getNeuron());
                    }
                    List<Float> weights = new ArrayList<>();
                    for(int k = 0; k < Connection.power; k++)
                    {
                        weights.add((float) ((Math.random()*2)-1));
                    }
                    ConnectionGene cg = new ConnectionGene(c, weights, this.layeredNeuronGenes.get(0).get(j), this.layeredNeuronGenes.get(1).get(i));
                    insertConnectionGene(cg);
                }
            }
        }
    }

    public void setUpInputs(GameState gs, Bot bot)//Call once at beginning of game
    {
        this.ball = gs.getBall();

        this.rockets = new ArrayList<>();

        List<Rocket> rockets = gs.getRockets();

        for(int i = 0; i < rockets.size(); i++)
        {
            Bot b = rockets.get(i).getBot();
            if(b!=null)
            {
                if(b.equals(bot))
                {
                    this.rockets.add(rockets.get(i));
                    break;
                }
            }
        }

        //Should always be true
        if(this.rockets.size() == 1)
        {
            boolean isRed = this.rockets.get(0).isRed();

            for(int i = 0; i < rockets.size(); i++)
            {
                if(!rockets.get(i).equals(this.rockets.get(0)))
                {
                    if(rockets.get(i).isRed() == isRed)
                    {
                        this.rockets.add(rockets.get(i));
                    }
                }
            }

            for(int i = 0; i < rockets.size(); i++)
            {
                if(rockets.get(i).isRed() != isRed)
                {
                    this.rockets.add(rockets.get(i));
                }
            }
        }
        else
        {
            System.out.println("ERROR, bot does not exist in gamestate");
        }
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

    public void setFitness(float f)
    {
        this.fitness = f;
    }

    public float getFitness()
    {
        return this.fitness;
    }

    public void calculateOutput(GameState gs, boolean kickoff)
    {
        if(gs.getTicksPassed() % tpe == 0 || kickoff)
        {
            //System.out.println("Calculating for " + this.rockets.get(0).getBot().getName());
            boolean isRed = this.rockets.get(0).isRed();
            int swapX = 1; //is 1 if on red, -1 if on blue

            if(!isRed)
            {
                swapX = -1;
            }

            this.layeredNeuronGenes.get(0).get(0).setValue((swapX*this.ball.getX()/gs.getXMax())+((swapX-1)*(-0.5f)));
            this.layeredNeuronGenes.get(0).get(1).setValue(this.ball.getY()/gs.getYMax());
            Vector vecToBall = new Vector(swapX*(this.ball.getX()-this.rockets.get(0).getX()), this.ball.getY()-this.rockets.get(0).getY());
            this.layeredNeuronGenes.get(0).get(2).setValue((float)(vecToBall.getMagnitude()/Math.sqrt(Math.pow(gs.getXMax(), 2) +  Math.pow(gs.getYMax(), 2))));
            vecToBall.convertToUnitVector();
            this.layeredNeuronGenes.get(0).get(3).setValue(vecToBall.getX());
            this.layeredNeuronGenes.get(0).get(4).setValue(vecToBall.getY());
            //this.layeredNeuronGenes.get(0).get(2).setValue(swapX*(this.ball.getX()-this.rockets.get(0).getX())/gs.getXMax());
            //this.layeredNeuronGenes.get(0).get(3).setValue((this.ball.getY()-this.rockets.get(0).getY())/gs.getYMax());


            Vector sepVec = new Vector(this.rockets.get(0).getVelocityVector());
            sepVec.subtractVector(this.ball.getVelocityVector());
            sepVec.multiplyX(swapX);
            this.layeredNeuronGenes.get(0).get(5).setValue(sepVec.getMagnitude()/(2*((Rocket.boostStrength/(Game.airRes))+Rocket.boostStrength)));
            sepVec.convertToUnitVector();
            this.layeredNeuronGenes.get(0).get(6).setValue(sepVec.getX());
            this.layeredNeuronGenes.get(0).get(7).setValue(sepVec.getY());


            Vector ballVelVec = new Vector(swapX*this.ball.getXVel(), this.ball.getYVel());
            this.layeredNeuronGenes.get(0).get(8).setValue(ballVelVec.getMagnitude()/((Rocket.boostStrength/(Game.airRes))+Rocket.boostStrength));
            ballVelVec.convertToUnitVector();
            this.layeredNeuronGenes.get(0).get(9).setValue(ballVelVec.getX());
            this.layeredNeuronGenes.get(0).get(10).setValue(ballVelVec.getY());
            //this.layeredNeuronGenes.get(0).get(5).setValue(doubleSigmoid(swapX*this.ball.getXVel()/(Rocket.boostStrength/(Game.airRes*2))));
            //this.layeredNeuronGenes.get(0).get(6).setValue(doubleSigmoid(this.ball.getYVel()/(Rocket.boostStrength/(Game.airRes*2))));

            if(this.ball.isRed() == isRed)
            {
                this.layeredNeuronGenes.get(0).get(11).setValue(1);
            }
            else
            {
                this.layeredNeuronGenes.get(0).get(11).setValue(0);
            }

            this.layeredNeuronGenes.get(0).get(12).setValue((swapX*this.rockets.get(0).getX()/gs.getXMax())+((swapX-1)*(-0.5f)));
            this.layeredNeuronGenes.get(0).get(13).setValue(this.rockets.get(0).getY()/gs.getYMax());

            Vector rocketVelVec = new Vector(swapX*this.rockets.get(0).getVelocityVector().getX(), this.rockets.get(0).getVelocityVector().getY());
            this.layeredNeuronGenes.get(0).get(14).setValue(rocketVelVec.getMagnitude()/((Rocket.boostStrength/(Game.airRes))+Rocket.boostStrength));
            rocketVelVec.convertToUnitVector();
            this.layeredNeuronGenes.get(0).get(15).setValue(rocketVelVec.getX());
            this.layeredNeuronGenes.get(0).get(16).setValue(rocketVelVec.getY());
            //this.layeredNeuronGenes.get(0).get(12).setValue(doubleSigmoid(swapX*this.rockets.get(0).getVelocityVector().getX()/(Rocket.boostStrength/(Game.airRes*2))));
            //this.layeredNeuronGenes.get(0).get(13).setValue(doubleSigmoid(this.rockets.get(0).getVelocityVector().getY()/(Rocket.boostStrength/(Game.airRes*2))));
            float degree = this.rockets.get(0).getDegree();
            float rad = (float) Math.toRadians(degree);
            float x = (float) Math.sin(rad);
            float y = (float) Math.cos(rad);
            this.layeredNeuronGenes.get(0).get(17).setValue(swapX*x);
            this.layeredNeuronGenes.get(0).get(18).setValue(y);

            for(int i = 1; i < this.rockets.size(); i++)
            {
                this.layeredNeuronGenes.get(0).get(19+(13*(i-1))).setValue((swapX*this.rockets.get(i).getX()/gs.getXMax())+((swapX-1)*(-0.5f)));
                this.layeredNeuronGenes.get(0).get(20+(13*(i-1))).setValue(this.rockets.get(i).getY()/gs.getYMax());

                Vector vecToRocket = new Vector(swapX*(this.rockets.get(i).getX()-this.rockets.get(0).getX()), this.rockets.get(i).getY()-this.rockets.get(0).getY());
                this.layeredNeuronGenes.get(0).get(21+(13*(i-1))).setValue((float)(vecToRocket.getMagnitude()/Math.sqrt(Math.pow(gs.getXMax(), 2) +  Math.pow(gs.getYMax(), 2))));
                vecToRocket.convertToUnitVector();
                this.layeredNeuronGenes.get(0).get(22+(13*(i-1))).setValue(vecToRocket.getX());
                this.layeredNeuronGenes.get(0).get(23+(13*(i-1))).setValue(vecToRocket.getY());

                Vector sepVecRocket = new Vector(this.rockets.get(0).getVelocityVector());
                sepVecRocket.subtractVector(this.rockets.get(i).getVelocityVector());
                sepVecRocket.multiplyX(swapX);
                this.layeredNeuronGenes.get(0).get(24+(13*(i-1))).setValue(sepVecRocket.getMagnitude()/(2*((Rocket.boostStrength/(Game.airRes))+Rocket.boostStrength)));
                sepVec.convertToUnitVector();
                this.layeredNeuronGenes.get(0).get(25+(13*(i-1))).setValue(sepVec.getX());
                this.layeredNeuronGenes.get(0).get(26+(13*(i-1))).setValue(sepVec.getY());

                Vector velVec = new Vector(swapX*this.rockets.get(i).getVelocityVector().getX(), this.rockets.get(i).getVelocityVector().getY());
                this.layeredNeuronGenes.get(0).get(27+(13*(i-1))).setValue(velVec.getMagnitude()/((Rocket.boostStrength/(Game.airRes))+Rocket.boostStrength));
                velVec.convertToUnitVector();
                this.layeredNeuronGenes.get(0).get(28+(13*(i-1))).setValue(velVec.getX());
                this.layeredNeuronGenes.get(0).get(29+(13*(i-1))).setValue(velVec.getY());

                degree = this.rockets.get(i).getDegree();
                rad = (float) Math.toRadians(degree);
                x = (float) Math.sin(rad);
                y = (float) Math.cos(rad);
                this.layeredNeuronGenes.get(0).get(30+(13*(i-1))).setValue(swapX*x);
                this.layeredNeuronGenes.get(0).get(31+(13*(i-1))).setValue(y);
            }

            this.layeredNeuronGenes.get(0).get(NeuralNetwork.numInputNeurons-1).setValue(1);

            for(int i = 1; i < this.layeredNeuronGenes.size(); i++)
            {
                for(int j = 0; j < this.layeredNeuronGenes.get(i).size(); j++)
                {
                    this.layeredNeuronGenes.get(i).get(j).calculateValue();
                }
            }

            float targetX;
            if(this.rockets.get(0).isRed())
            {
                targetX = this.layeredNeuronGenes.get(this.layeredNeuronGenes.size()-1).get(0).getValue();
            }
            else
            {
                targetX = -this.layeredNeuronGenes.get(this.layeredNeuronGenes.size()-1).get(0).getValue();
            }

            float targetY = this.layeredNeuronGenes.get(this.layeredNeuronGenes.size()-1).get(1).getValue();

            Vector v = new Vector(targetX, targetY);
            float angle = v.getAngle();
            this.rockets.get(0).setTargetAngle((short)angle);

            if(angle > this.rockets.get(0).getDegree() + 180)
            {
                this.tiltC = false;
                this.tiltCC = true;
            }
            else if(angle > this.rockets.get(0).getDegree())
            {
                this.tiltC = true;
                this.tiltCC = false;
            }
            else if(angle < this.rockets.get(0).getDegree()-180)
            {
                this.tiltC = true;
                this.tiltCC = false;
            }
            else if(angle < this.rockets.get(0).getDegree())
            {
                this.tiltC = false;
                this.tiltCC = true;
            }
            else
            {
                this.tiltC = false;
                this.tiltCC = false;
            }

            if(this.layeredNeuronGenes.get(this.layeredNeuronGenes.size()-1).get(2).getValue() > 0)
            {
                this.boost = true;
            }
            else
            {
                this.boost = false;
            }
        }

        float angle = this.rockets.get(0).getTargetVector().getAngle();
        this.rockets.get(0).setTargetAngle((short)angle);

        if(angle > this.rockets.get(0).getDegree() + 180)
        {
            this.tiltC = false;
            this.tiltCC = true;
        }
        else if(angle > this.rockets.get(0).getDegree())
        {
            this.tiltC = true;
            this.tiltCC = false;
        }
        else if(angle < this.rockets.get(0).getDegree()-180)
        {
            this.tiltC = true;
            this.tiltCC = false;
        }
        else if(angle < this.rockets.get(0).getDegree())
        {
            this.tiltC = false;
            this.tiltCC = true;
        }
        else
        {
            this.tiltC = false;
            this.tiltCC = false;
        }

        if(this.layeredNeuronGenes.get(this.layeredNeuronGenes.size()-1).get(2).getValue() > 0)
        {
            this.boost = true;
        }
        else
        {
            this.boost = false;
        }
    }

    public float doubleSigmoid(float val)
    {
        return (float)(2/(1+Math.pow(Math.E, -val)))-1;
    }

    public List<ConnectionGene> getConnectionGenes()
    {
        return this.connectionGenes;
    }

    public List<NeuronGene> getNeuronGenes()
    {
        return this.neuronGenes;
    }

    public List<List<NeuronGene>> getLayeredNeuronGenes()
    {
        return this.layeredNeuronGenes;
    }

    public void mutate()
    {
        //1st mutate connection genes weights
        for(int i = 0; i < this.connectionGenes.size(); i++)
        {
            if(Math.random() < mutateConnection)
            {
                List<Float> vals = new ArrayList<>();
                for(int j = 0; j < Connection.power; j++)
                {
                    vals.add((float) ((Math.random()*(mutateConnectionVary*2))-mutateConnectionVary));
                }
                this.connectionGenes.get(i).adjustWeights(vals);
            }
        }

        //2nd mutate add connection
        if(Math.random() < mutateAddConnection)
        {
            //Add a random connection. First make a list of neurons excluding inputs
            List<NeuronGene> ngs = new ArrayList<>();
            for(int i = 1; i < this.layeredNeuronGenes.size(); i++)
            {
                for(int j = 0; j < this.layeredNeuronGenes.get(i).size(); j++)
                {
                    ngs.add(this.layeredNeuronGenes.get(i).get(j));
                }
            }

            NeuronGene toNeuron = ngs.get((int)(Math.random()*ngs.size()));

            List<NeuronGene> fromNeurons = new ArrayList<>();
            for(int i = 0; i < toNeuron.getLayer(); i++)
            {
                for(int j = 0; j < this.layeredNeuronGenes.get(i).size(); j++)
                {
                    fromNeurons.add(this.layeredNeuronGenes.get(i).get(j));
                }
            }

            NeuronGene fromNeuron = fromNeurons.get((int)(Math.random()*fromNeurons.size()));

            if(!toNeuron.hasConnection(fromNeuron))
            {
                Connection connection = Connection.getConnection(fromNeuron.getNeuron(), toNeuron.getNeuron());
                if(connection == null)
                {
                    connection = new Connection(fromNeuron.getNeuron(), toNeuron.getNeuron());
                }

                List<Float> weights = new ArrayList<>();
                for(int i = 0; i < Connection.power; i++)
                {
                    weights.add((float)((Math.random()*2)-1));
                }

                ConnectionGene cg = new ConnectionGene(connection, weights, fromNeuron, toNeuron);

                insertConnectionGene(cg);
            }
            else
            {
                //Connection already exists and the mutation fails
            }
        }

        //3rd mutate add neuron
        if(Math.random() < mutateAddNeuron)
        {
            if(this.connectionGenes.size() > 0)
            {
                ConnectionGene seperateConnection = this.connectionGenes.get((int)(Math.random()*this.connectionGenes.size()));

                if(!seperateConnection.isBias())
                {
                    //First find the layer the neuron should be placed
                    NeuronGene ng1 = seperateConnection.getNeuronGene1();
                    NeuronGene ng2 = seperateConnection.getNeuronGene2();

                    int layer = ng1.getLayer()+1;

                    if(ng2.getLayer()-ng1.getLayer() == 1)
                    {
                        createNewLayer(layer);
                    }

                    //Then add new Neuron
                    Neuron n = new Neuron(false, false);
                    NeuronGene newNeuron = new NeuronGene(n, false, layer);

                    insertNeuronGene(newNeuron);

                    //Then add its connections
                    Connection c1 = Connection.getConnection(ng1.getNeuron(), newNeuron.getNeuron());
                    Connection c2 = Connection.getConnection(newNeuron.getNeuron(), ng2.getNeuron());

                    if(c1 == null)
                    {
                        c1 = new Connection(ng1.getNeuron(), newNeuron.getNeuron());
                    }

                    if(c2 == null)
                    {
                        c2 = new Connection(newNeuron.getNeuron(), ng2.getNeuron());
                    }

                    List<Float> weights1 = new ArrayList<>();
                    List<Float> weights2 = new ArrayList<>();

                    for(int i = 0; i < Connection.power; i++) {
                        if (i == 0)
                        {
                            weights1.add(1f);
                        }
                        else
                        {
                            weights1.add(0f);
                        }
                        weights2.add(seperateConnection.getWeights().get(i));
                    }

                    ConnectionGene cg1 = new ConnectionGene(c1, weights1, ng1, newNeuron);
                    ConnectionGene cg2 = new ConnectionGene(c2, weights2, newNeuron, ng2);

                    insertConnectionGene(cg1);
                    insertConnectionGene(cg2);

                    //Then add Bias
                    if(!this.layeredNeuronGenes.get(0).get(this.layeredNeuronGenes.get(0).size()-1).equals(ng1))
                    {
                        Connection c3 = Connection.getConnection(this.layeredNeuronGenes.get(0).get(this.layeredNeuronGenes.get(0).size()-1).getNeuron(), newNeuron.getNeuron());

                        if(c3 == null)
                        {
                            c3 = new Connection(this.layeredNeuronGenes.get(0).get(this.layeredNeuronGenes.get(0).size()-1).getNeuron(), newNeuron.getNeuron());
                        }

                        List<Float> weights3 = new ArrayList<>();

                        for(int i = 0; i < Connection.power; i++) {
                            if (i == 0)
                            {
                                weights3.add((float) ((Math.random()*2)-1));
                            }
                            else
                            {
                                weights3.add(0f);
                            }
                        }

                        ConnectionGene cg3 = new ConnectionGene(c3, weights3, this.layeredNeuronGenes.get(0).get(this.layeredNeuronGenes.get(0).size()-1), newNeuron);

                        insertConnectionGene(cg3);
                    }

                    removeConnectionGene(seperateConnection);
                }
                else
                {
                    //Attempting to separate bias connection, so mutation fails
                }
            }
            else
            {
                //No connections are present so mutation fails
            }
        }


    }

    public NeuronGene findNeuronGene(Neuron n)
    {
        if(this.neuronGenes.size() == 0)
        {
            System.out.print("ERROR, ATTEMPTING TO FIND NEURON GENE WHEN SIZE IS 0");
            return null;
        }

        int low = 0;
        int high = this.neuronGenes.size()-1;

        while(low != high)
        {
            int mid = ((high-low)/2)+low;

            int compare = n.getInnovationNumber()-this.neuronGenes.get(mid).getNeuron().getInnovationNumber();

            if(compare < 0)
            {
                high = mid;
            }
            else if(compare > 0)
            {
                low = mid+1;
            }
            else
            {
                return this.neuronGenes.get(mid);
            }
        }

        if(n.getInnovationNumber() == this.neuronGenes.get(low).getNeuron().getInnovationNumber())
        {
            return this.neuronGenes.get(low);
        }
        else
        {
            System.out.print("ERROR, ATTEMPTING TO FIND NEURON GENE THAT DOESNT EXIST");
            return null;
        }
    }

    public float compare(NeuralNetwork n)
    {
        List<ConnectionGene> connections = n.getConnectionGenes();

        //Compare genes
        int thisIterator = 0;
        int thatIterator = 0;

        int numDisjoint = 0;
        int numJoint = 0;
        float weightDifference = 0;

        while(thisIterator<this.connectionGenes.size() || thatIterator<connections.size())
        {
            if(thisIterator>=this.connectionGenes.size())
            {
                thatIterator++;
                numDisjoint++;
            }
            else if(thatIterator>=connections.size())
            {
                thisIterator++;
                numDisjoint++;
            }
            else
            {
                ConnectionGene cg1 = this.connectionGenes.get(thisIterator);
                ConnectionGene cg2 = connections.get(thatIterator);

                int c = cg1.compare(cg2);

                if(c == 1)
                {
                    thatIterator++;
                    numDisjoint++;
                }
                else if(c == -1)
                {
                    thisIterator++;
                    numDisjoint++;
                }
                else if(c == 0)
                {
                    for(int i = 0; i < Connection.power; i++)
                    {
                        weightDifference+=Math.abs(cg1.getWeights().get(i)-cg2.getWeights().get(i));
                    }
                    thisIterator++;
                    thatIterator++;
                    numJoint++;
                }
            }
        }

        float compare = 0;
        int N = Math.max(this.connectionGenes.size(), connections.size());

        if(N == 0)
        {
            N = 1;
        }
        if(numJoint == 0)
        {
            numJoint = 1;
        }

        compare+=(cDisjoint*numDisjoint)/N;
        compare+=(cWeight*weightDifference)/numJoint;

        return compare;
    }

    public void insertConnectionGene(ConnectionGene cg)
    {
        if(this.connectionGenes.size() == 0)
        {
            this.connectionGenes.add(cg);
            return;
        }

        int low = 0;
        int high = this.connectionGenes.size() - 1;
        while(low != high)
        {
            int mid = ((high-low)/2)+low;

            int compare = cg.compare(this.connectionGenes.get(mid));

            if(compare == -1)
            {
                high = mid;
            }
            else if(compare == 0)
            {
                 System.out.println("ERROR, ATTEMPTING TO INSERT CONNECTIONGENE THAT ALREADY EXISTS");
                 return;
            }
            else if(compare == 1)
            {
                low = mid+1;
            }
        }

        this.connectionGenes.add(low, cg);
    }

    public void removeConnectionGene(ConnectionGene cg)
    {
        cg.getNeuronGene2().removeConnectionGene(cg);
        this.connectionGenes.remove(cg);
    }

    public void insertNeuronGene(NeuronGene ng)
    {

        if(ng.getLayer()<this.layeredNeuronGenes.size())
        {
            this.layeredNeuronGenes.get(ng.getLayer()).add(ng);
        }
        else if(ng.getLayer()<this.layeredNeuronGenes.size()+1)
        {
            this.layeredNeuronGenes.add(new ArrayList<>());
            this.layeredNeuronGenes.get(this.layeredNeuronGenes.size()-1).add(ng);
        }
        else
        {
            System.out.println("ERROR, ATTEMPTING TO INSERT NEURONGENE INTO OUT OF BOUNDS LAYER");
            return;
        }

        if(this.neuronGenes.size() == 0)
        {
            this.neuronGenes.add(ng);
            return;
        }

        int low = 0;
        int high = this.neuronGenes.size() - 1;
        while(low != high)
        {
            int mid = ((high-low)/2)+low;

            int compare = ng.getNeuron().getInnovationNumber()-this.neuronGenes.get(mid).getNeuron().getInnovationNumber();

            if(compare < 0)
            {
                high = mid;
            }
            else if(compare > 0)
            {
                low = mid+1;
            }
            else
            {
                System.out.println("ERROR, ATTEMPTING TO INSERT NEURONGENE THAT ALREADY EXISTS");
                return;
            }
        }

        if(this.neuronGenes.get(this.neuronGenes.size() - 1).getNeuron().getInnovationNumber() < ng.getNeuron().getInnovationNumber())
        {
            this.neuronGenes.add(ng);
        }
        else
        {
            this.neuronGenes.add(low, ng);
        }
    }

    public void deleteNeuronGene(NeuronGene ng)
    {
        //First go to neurons in layers higher than this and delete all connection genes that contain this neuron
        for(int i = ng.getLayer(); i < this.layeredNeuronGenes.size(); i++)
        {
            for(int j = 0; j < this.layeredNeuronGenes.get(i).size(); j++)
            {
                ConnectionGene cg = this.layeredNeuronGenes.get(i).get(j).getConnection(ng);

                if(cg!=null)
                {
                    this.connectionGenes.remove(cg);
                    this.layeredNeuronGenes.get(i).get(j).removeConnection(ng);
                }
            }
        }

        //Next delete all connections within this neuron
        List<ConnectionGene> cgs = ng.getConnections();

        for(int i = 0; i < cgs.size(); i++)
        {
            this.connectionGenes.remove(cgs.get(i));
        }

        //If deleting this neuron results in an empty layer, delete that layer
        if(this.layeredNeuronGenes.get(ng.getLayer()).size() == 1)
        {
            deleteLayer(ng.getLayer());
        }
        else
        {
            this.layeredNeuronGenes.get(ng.getLayer()).remove(ng);
        }

        this.neuronGenes.remove(ng);

    }

    //Creates a new empty layer at specified layer. All neurons at this layer or higher will shift to the next layer
    public void createNewLayer(int layer)
    {
        if(layer == 0)
        {
            System.out.println("ERROR, ATTEMPTING TO SHIFT INPUT NEURONS OVER 1 LAYER");
        }
        else
        {
            for(int i = layer; i < this.layeredNeuronGenes.size(); i++)
            {
                for(int j = 0; j < this.layeredNeuronGenes.get(i).size(); j++)
                {
                    this.layeredNeuronGenes.get(i).get(j).setLayer(this.layeredNeuronGenes.get(i).get(j).getLayer()+1);
                }
            }

            this.layeredNeuronGenes.add(layer, new ArrayList<>());
        }
    }

    public void deleteLayer(int layer)
    {
        this.layeredNeuronGenes.remove(layer);

        for(int i = layer; i < this.layeredNeuronGenes.size(); i++)
        {
            for(int j = 0; j < this.layeredNeuronGenes.get(i).size(); j++)
            {
                this.layeredNeuronGenes.get(i).get(j).setLayer(this.layeredNeuronGenes.get(i).get(j).getLayer()-1);
            }
        }
    }

    public static NeuralNetwork breedNetworks(NeuralNetwork n1, NeuralNetwork n2)
    {
        if(n1.getFitness() > n2.getFitness())
        {
            return new NeuralNetwork(n1, n2);
        }
        else
        {
            return new NeuralNetwork(n2, n1);
        }
    }

    public void printNetwork()
    {
        for(int i = 0; i < this.layeredNeuronGenes.size(); i++)
        {
            System.out.println("LAYER " + i);
            for(int j = 0; j < this.layeredNeuronGenes.get(i).size(); j++)
            {
                this.layeredNeuronGenes.get(i).get(j).print();
            }
        }
    }



}
