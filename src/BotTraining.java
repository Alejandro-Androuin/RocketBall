import java.util.ArrayList;
import java.util.List;

public class BotTraining {

    public static float incluenceOnFitness = (float)0.2; //fitness = wins*(1-val)+(wins-avgtm8Wins)(val)+(avgoppWins-wins)(val)

    private static int maxWins = 5;
    private static float percentElite = (float).1;
    private static int proWins = 3;

    private static float killPercent = (float)0.5;

    private static float threshold = 20;
    private static float thresholdIncrement = (float)0.1;

    private static int numSpecies = 8;

    private static int numGenerationsAtSpecies = 0;
    private static int numGenerationsPurge = 20;

    public static boolean training = false;
    public static boolean endTraining = false;

    public static List<List<TrainingBot>> trainingBots = new ArrayList<>();
    private static List<List<NeuralNetwork>> species = new ArrayList<>();
    //First list corresponds to species 1, species 2, so on
    //First item on second list is the neural network corresponding to the species (not a member, rather is used to define the species). The rest of the items are members of that species

    public static int teamSize = 2;

    private static int generation = 0;

    public static void newTraining()
    {
        trainingBots = new ArrayList<>();
        generation = 0;

        Neuron.createInputOutputs(teamSize);

        int totalBots = (int)(teamSize*Math.pow(2, maxWins));
        List<String> names = Names.getUniqueNames(totalBots);

        for(int i = 0; i < totalBots; i++)
        {
            if(trainingBots.size() < 1)
            {
                trainingBots.add(new ArrayList<>());
            }
            else if(trainingBots.get(trainingBots.size()-1).size() == teamSize)
            {
                trainingBots.add(new ArrayList<>());
            }

            String name = names.get(i);
            name += "_G" + generation;

            Bot b = new Bot(name);
            NeuralNetwork n;
            if(trainingBots.get(trainingBots.size()-1).size() != 0)
            {
                n = new NeuralNetwork(trainingBots.get(trainingBots.size()-1).get(0).getBot().getNetwork());
            }
            else
            {
                n = new NeuralNetwork();
            }
            b.setNetwork(n);
            trainingBots.get(trainingBots.size()-1).add(new TrainingBot(b));
        }
    }

    //returns true if successful, false otherwise
    public static boolean startTraining()
    {
        if(training)
        {
            return false;
        }
        else
        {
            Thread botTraining = new Thread(() -> trainBots());
            botTraining.start();
            return true;
        }
    }

    public static void endTraining()
    {
        endTraining = true;
    }

    public static void trainBots()
    {
        if(!training)
        {
            training = true;

            while(!endTraining)
            {
                System.out.println("This is generation " + generation);
                executeMatchups(); //Fitness of all training bots are calculated here
                System.out.println("Matchups done");
                speciate(); //Will form the species list, separating all networks into different species
                System.out.println("Speciation done");
                //printAllSpecies();
                killOrganisms(); //kills a percentage of networks in each species
                System.out.println("Killing done");
                breedNetworks(); //Breeds neural networks together in each species and creates a new list of training bots
                System.out.println("Breeding done");

                System.out.println("Number of Species: " + species.size() + " threshold " + threshold);
                float averageNeurons = 0;
                for(int i = 0; i < trainingBots.size(); i++)
                {
                    averageNeurons+=trainingBots.get(i).get(0).getBot().getNetwork().getNeuronGenes().size();
                }
                averageNeurons/=trainingBots.size();
                System.out.println("Average Neurons: " + averageNeurons);
                float averageConnections = 0;
                for(int i = 0; i < trainingBots.size(); i++)
                {
                    averageConnections+=trainingBots.get(i).get(0).getBot().getNetwork().getConnectionGenes().size();
                }
                averageConnections/=trainingBots.size();
                System.out.println("Average Connections: " + averageConnections);
            }

            training = false;
            endTraining = false;

            BotTrainingScreen.training = false;
        }
    }

    public static void executeMatchups()
    {
        for(int i = 0; i < trainingBots.size(); i++)
        {
            for(int j = 0; j < trainingBots.get(i).size(); j++)
            {
                trainingBots.get(i).get(j).reset();
            }
        }

        List<Game> games = new ArrayList<>();

        for(int i = 0; i < maxWins; i++)
        {
            shuffle2(trainingBots);
            for(int j = maxWins-1; j > -1; j--)
            {
                List<TrainingBot> matchup = new ArrayList<>();
                for(int k = 0; k < trainingBots.size(); k++)
                {
                    if(trainingBots.get(k).get(0).getWins() == j)
                    {
                        matchup.addAll(trainingBots.get(k));
                    }

                    if(matchup.size() == teamSize*2)
                    {
                        List<TrainingBot> redTeam = new ArrayList<>();
                        List<TrainingBot> blueTeam = new ArrayList<>();

                        for(int l = 0; l < matchup.size(); l++)
                        {
                            if(l<matchup.size()/2)
                            {
                                redTeam.add(matchup.get(l));
                            }
                            else
                            {
                                blueTeam.add(matchup.get(l));
                            }
                        }

                        List<Bot> redTeamBots = new ArrayList<>();
                        List<Bot> blueTeamBots = new ArrayList<>();

                        /*
                        for(int l = 0; l < redTeam.size(); l++)
                        {
                            redTeamBots.add(redTeam.get(l).getBot());
                        }
                        for(int l = 0; l < blueTeam.size(); l++)
                        {
                            blueTeamBots.add(blueTeam.get(l).getBot());
                        }
                         */

                        for(int l = 0; l < teamSize; l++)
                        {
                            redTeamBots.add(redTeam.get(l).getBot());
                        }
                        for(int l = 0; l < teamSize; l++)
                        {
                            blueTeamBots.add(blueTeam.get(l).getBot());
                        }

                        Game g = new Game(redTeamBots, new ArrayList<>(), blueTeamBots, new ArrayList<>());
                        g.runGameInstantly();

                        games.add(g);

                        if(j >= proWins)
                        {
                            g.pro();
                        }

                        if(g.redWin())
                        {
                            for(int l = 0; l < redTeam.size(); l++)
                            {
                                redTeam.get(l).win();
                                for(int m = 0; m < redTeam.size(); m++)
                                {
                                    redTeam.get(l).addTeammate(redTeam.get(m));
                                }

                                for(int m = 0; m < blueTeam.size(); m++)
                                {
                                    redTeam.get(l).addOpponent(blueTeam.get(m));
                                }
                            }
                        }
                        else
                        {
                            for(int l = 0; l < blueTeam.size(); l++)
                            {
                                blueTeam.get(l).win();
                                for(int m = 0; m < blueTeam.size(); m++)
                                {
                                    blueTeam.get(l).addTeammate(blueTeam.get(m));
                                }

                                for(int m = 0; m < redTeam.size(); m++)
                                {
                                    blueTeam.get(l).addOpponent(redTeam.get(m));
                                }
                            }

                        }

                        for(int l = 0; l < redTeam.size(); l++)
                        {
                            redTeam.get(l).storeGame(redTeam, blueTeam);
                        }
                        for(int l = 0; l < blueTeam.size(); l++)
                        {
                            blueTeam.get(l).storeGame(blueTeam, redTeam);
                        }

                        matchup = new ArrayList<>();
                    }
                }
            }
        }

        for(int i = 0; i < trainingBots.size(); i++)
        {
            trainingBots.get(i).get(0).calculateFitness();
        }

        mergeSortBots2(trainingBots, 0, trainingBots.size()-1);

        for(int i = 0; i < BotTrainingScreen.graphs.size(); i++)
        {
            BotTrainingScreen.graphs.get(i).insertData(games, generation);
        }
    }

    public static void speciate()
    {
        resetSpecies();

        for(int i = 0; i < trainingBots.size(); i++)
        {
            int s = -1;
            float compatible = Float.MAX_VALUE;

            for(int j = 0; j < species.size(); j++)
            {
                float compare = trainingBots.get(i).get(0).getBot().getNetwork().compare(species.get(j).get(0));
                if(compare  < threshold & compare < compatible)
                {
                    s = j;
                    compatible = compare;
                }
            }

            //Was not assigned to an existing species, so create a new one
            if(s == -1)
            {
                species.add(new ArrayList<>());
                //Add it twice. First is used for comparing and the second will represent this network as a member of the species
                species.get(species.size()-1).add(trainingBots.get(i).get(0).getBot().getNetwork());
                species.get(species.size()-1).add(trainingBots.get(i).get(0).getBot().getNetwork());
            }
            else
            {
                species.get(s).add(trainingBots.get(i).get(0).getBot().getNetwork());
            }

        }
    }

    public static void printAllSpecies()
    {
        for(int i = 0; i < species.size(); i++)
        {
            System.out.println("Species " + i);
            for(int j = 0; j < species.get(i).size(); j++)
            {
                species.get(i).get(j).printNetwork();
            }
        }
    }

    public static void killOrganisms()
    {
        if(numGenerationsAtSpecies > numGenerationsPurge)
        {
            System.out.println("Purging!");
            int numKill = species.size()-(numSpecies-1);

            //Calculate worst species
            List<Integer> worstSpecies = new ArrayList<>();
            List<Float> fitnesses = new ArrayList<>();

            for(int i = species.size()-1; i > -1; i--)
            {
                float speciesFitness = 0;
                for(int j = 1; j < species.get(i).size(); j++)
                {
                    speciesFitness+=species.get(i).get(j).getFitness()/(species.get(i).size()-1);
                }

                if(numKill > worstSpecies.size())
                {
                    worstSpecies.add(i);
                    fitnesses.add(speciesFitness);
                }
                else
                {
                    boolean bad = false;
                    for(int j = 0; j < fitnesses.size(); j++)
                    {
                        if(speciesFitness < fitnesses.get(j))
                        {
                            bad = true;
                            break;
                        }
                    }

                    if(bad)
                    {
                        //First remove the best fitness
                        int indexBest = 0;
                        for(int j = 1; j < fitnesses.size(); j++)
                        {
                            if(fitnesses.get(indexBest) < fitnesses.get(j))
                            {
                                indexBest = j;
                            }
                        }

                        worstSpecies.remove(indexBest);
                        fitnesses.remove(indexBest);

                        //Then add this fitness
                        worstSpecies.add(i);
                        fitnesses.add(speciesFitness);
                    }
                }
            }

            //worstspecies and fitness are now defined. delete them
            for (int i = 0; i < worstSpecies.size(); i++) {
                species.remove(species.get(worstSpecies.get(i)));
            }

            numGenerationsAtSpecies = 0;
        }

        for(int i = 0; i < species.size(); i++)
        {
            float chanceOfLowSurvival = 0;
            float chanceOfHighSurvival = 1;
            if(killPercent < 0.5)
            {
                chanceOfLowSurvival = (float) (2*(0.5-killPercent));
            }
            if(killPercent > 0.5)
            {
                chanceOfHighSurvival = (2*(1-killPercent));
            }

            int speciesPop = species.get(i).size()-1;

            for(int j = species.get(i).size()-1; j > 0; j--)
            {
                float chanceSurvival = (float) (((speciesPop-j+1.0)/(speciesPop+1))*(chanceOfHighSurvival-chanceOfLowSurvival)+chanceOfLowSurvival);
                if(Math.random()>chanceSurvival)
                {
                    species.get(i).remove(j);
                }
                else
                {

                }
            }
        }

        //Delete species with 0 members
        for(int i = species.size()-1; i > -1; i--)
        {
            if(species.get(i).size() == 1)
            {
                species.remove(i);
            }
        }

        if(species.size() < numSpecies)
        {
            threshold-=thresholdIncrement;
            numGenerationsAtSpecies = 0;
        }
        else
        {
            threshold+=thresholdIncrement;
            numGenerationsAtSpecies++;
        }
    }

    //Will bread the members of the species together to create a fresh evolved list of training bots
    public static void breedNetworks()
    {
        List<NeuralNetwork> newNetworks = new ArrayList<>();

        //First add elites to the networks
        for(int i = 0; i < trainingBots.size()*percentElite; i++)
        {
            newNetworks.add(trainingBots.get(i).get(0).getBot().getNetwork());
        }

        //Calculate sum of all species fitness
        float totalSpeciesFitness = 0;

        for(int i = 0; i < species.size(); i++)
        {
            float speciesFitness = 0;
            for(int j = 1; j < species.get(i).size(); j++)
            {
                speciesFitness+=species.get(i).get(j).getFitness()/(species.get(i).size()-1);
            }
            totalSpeciesFitness += speciesFitness;
        }

        //Now calculate the number of organisms each species should produce
        List<Integer> numReproduce = new ArrayList<>();
        int sum = 0;

        for(int i = 0; i < species.size(); i++)
        {
            float speciesFitness = 0;
            for(int j = 1; j < species.get(i).size(); j++)
            {
                speciesFitness+=species.get(i).get(j).getFitness()/(species.get(i).size()-1);
            }
            numReproduce.add(Math.round((speciesFitness/totalSpeciesFitness)*(trainingBots.size()-newNetworks.size())));
            sum+=numReproduce.get(numReproduce.size()-1);
        }

        //Ensure this list adds exactly to number of bots that must be created
        for(int i = sum; i < (trainingBots.size()-newNetworks.size()); i++)
        {
            int randomIndex = (int)(Math.random()*numReproduce.size());
            numReproduce.set(randomIndex, numReproduce.get(randomIndex)+1);
        }

        for(int i = sum; i > (trainingBots.size()-newNetworks.size()); i--)
        {
            int randomIndex = (int)(Math.random()*numReproduce.size());
            while(numReproduce.get(randomIndex) == 0)
            {
                randomIndex = (int)(Math.random()*numReproduce.size());
            }
            numReproduce.set(randomIndex, numReproduce.get(randomIndex)-1);
        }

        //Now breed members of each species together
        for(int i = 0; i < species.size(); i++)
        {
            float totalFitness = 0;
            for(int j = 1; j < species.get(i).size(); j++)
            {
                totalFitness+=species.get(i).get(j).getFitness();
            }

            for(int j = 0; j < numReproduce.get(i); j++)
            {
                NeuralNetwork n1 = null;
                NeuralNetwork n2 = null;

                float random = (float) Math.random();
                float fitCounter = 0;
                for(int k = 1; k < species.get(i).size(); k++)
                {
                    fitCounter+=species.get(i).get(k).getFitness()/totalFitness;
                    if(k == species.get(i).size()-1)
                    {
                        n1 = species.get(i).get(k);
                        break;
                    }
                    else if(random < fitCounter)
                    {
                        n1 = species.get(i).get(k);
                        break;
                    }
                }

                random = (float) Math.random();
                fitCounter = 0;
                for(int k = 1; k < species.get(i).size(); k++)
                {
                    fitCounter+=species.get(i).get(k).getFitness()/totalFitness;
                    if(k == species.get(i).size()-1)
                    {
                        n2 = species.get(i).get(k);
                        break;
                    }
                    if(random <= fitCounter)
                    {
                        n2 = species.get(i).get(k);
                        break;
                    }
                }

                NeuralNetwork n = NeuralNetwork.breedNetworks(n1,n2);
                newNetworks.add(n);
            }
        }

        //If my code is correct this will always be true
        if(newNetworks.size() == trainingBots.size())
        {
            generation++;

            List<String> names = Names.getUniqueNames(teamSize*newNetworks.size());
            trainingBots = new ArrayList<>();

            for(int i = 0; i < newNetworks.size(); i++)
            {
                trainingBots.add(new ArrayList<>());
                for(int j = 0; j < teamSize; j++)
                {
                    String name = names.get(i*teamSize+j);
                    name += "_G" + generation;

                    Bot b = new Bot(name);
                    NeuralNetwork n = new NeuralNetwork(newNetworks.get(i));
                    b.setNetwork(n);
                    trainingBots.get(trainingBots.size()-1).add(new TrainingBot(b));
                }
            }
        }
        else
        {
            System.out.println("ERROR IN BOT TRAINING BREED NETWORKS");
        }
    }

    //Removes all members of species. Only preserves the first element of every list
    public static void resetSpecies()
    {
        for(int i = 0; i < species.size(); i++)
        {
            for(int j = species.get(i).size() - 1; j > 0; j--)
            {
                species.get(i).remove(j);
            }
        }
    }

    public static void shuffle(List<TrainingBot> bots)
    {
        for(int i = 0; i < bots.size(); i++)
        {
            int swap = (int)((Math.random()*(bots.size()-i-1))+i);

            TrainingBot temp = bots.get(i);
            bots.set(i, bots.get(swap));
            bots.set(swap, temp);
        }
    }

    public static void shuffle2(List<List<TrainingBot>> bots)
    {
        for(int i = 0; i < bots.size(); i++)
        {
            int swap = (int)((Math.random()*(bots.size()-i-1))+i);

            List<TrainingBot> temp = bots.get(i);
            bots.set(i, bots.get(swap));
            bots.set(swap, temp);
        }
    }

    //Sorts the bots by fitness highest to lowest
    public static void mergeSortBots(List<TrainingBot> bots, int low, int high) {
        if (high <= low) return;

        int mid = (low+high)/2;
        mergeSortBots(bots, low, mid);
        mergeSortBots(bots, mid+1, high);
        mergeBots(bots, low, mid, high);
    }

    public static void mergeBots(List<TrainingBot> bots, int low, int mid, int high) {
        // Creating temporary subarrays
        TrainingBot leftArray[] = new TrainingBot[mid - low + 1];
        TrainingBot rightArray[] = new TrainingBot[high - mid];

        // Copying our subarrays into temporaries
        for (int i = 0; i < leftArray.length; i++)
            leftArray[i] = bots.get(low + i);
        for (int i = 0; i < rightArray.length; i++)
            rightArray[i] = bots.get(mid + i + 1);

        // Iterators containing current index of temp subarrays
        int leftIndex = 0;
        int rightIndex = 0;

        // Copying from leftArray and rightArray back into array
        for (int i = low; i < high + 1; i++) {
            // If there are still uncopied elements in R and L, copy minimum of the two
            if (leftIndex < leftArray.length && rightIndex < rightArray.length) {
                if (leftArray[leftIndex].getFitness() > rightArray[rightIndex].getFitness()) {
                    bots.set(i, leftArray[leftIndex]);
                    leftIndex++;
                }
                else{
                    bots.set(i, rightArray[rightIndex]);
                    rightIndex++;
                }
            } else if (leftIndex < leftArray.length) {
                // If all elements have been copied from rightArray, copy rest of leftArray
                bots.set(i, leftArray[leftIndex]);
                leftIndex++;
            } else if (rightIndex < rightArray.length) {
                // If all elements have been copied from leftArray, copy rest of rightArray
                bots.set(i, rightArray[rightIndex]);
                rightIndex++;
            }
        }
    }

    //Sorts the bots by fitness highest to lowest
    public static void mergeSortBots2(List<List<TrainingBot>> bots, int low, int high) {
        if (high <= low) return;

        int mid = (low+high)/2;
        mergeSortBots2(bots, low, mid);
        mergeSortBots2(bots, mid+1, high);
        mergeBots2(bots, low, mid, high);
    }

    public static void mergeBots2(List<List<TrainingBot>> bots, int low, int mid, int high) {
        // Creating temporary subarrays
        List<List<TrainingBot>> left = new ArrayList<>();
        List<List<TrainingBot>> right = new ArrayList<>();

        // Copying our subarrays into temporaries
        for (int i = 0; i < mid - low + 1; i++)
            left.add(bots.get(low + i));
        for (int i = 0; i < high - mid; i++)
            right.add(bots.get(mid + i + 1));

        // Iterators containing current index of temp subarrays
        int leftIndex = 0;
        int rightIndex = 0;

        // Copying from leftArray and rightArray back into array
        for (int i = low; i < high + 1; i++) {
            // If there are still uncopied elements in R and L, copy minimum of the two
            if (leftIndex < left.size() && rightIndex < right.size()) {
                if (left.get(leftIndex).get(0).getFitness() > right.get(rightIndex).get(0).getFitness()) {
                    bots.set(i, left.get(leftIndex));
                    leftIndex++;
                }
                else{
                    bots.set(i, right.get(rightIndex));
                    rightIndex++;
                }
            } else if (leftIndex < left.size()) {
                // If all elements have been copied from rightArray, copy rest of leftArray
                bots.set(i, left.get(leftIndex));
                leftIndex++;
            } else if ( rightIndex < right.size()) {
                // If all elements have been copied from leftArray, copy rest of rightArray
                bots.set(i, right.get(rightIndex));
                rightIndex++;
            }
        }
    }

}
