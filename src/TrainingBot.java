import java.util.ArrayList;
import java.util.List;

public class TrainingBot {

    private Bot bot;
    private int wins = 0;
    private float fitness = 0;
    private boolean isElite = false;

    private List<TrainingBot> teammates = new ArrayList<>();
    private List<TrainingBot> opponents = new ArrayList<>();

    public TrainingBot(Bot bot)
    {
        this.bot = bot;
    }

    public Bot getBot()
    {
        return this.bot;
    }

    public int getWins()
    {
        return this.wins;
    }

    public void win()
    {
        this.wins++;
    }

    public void reset()
    {
        this.wins = 0;
        this.teammates = new ArrayList<>();
        this.opponents = new ArrayList<>();
    }

    public void addTeammate(TrainingBot b)
    {
        this.teammates.add(b);
    }

    public void addOpponent(TrainingBot b)
    {
        this.opponents.add(b);
    }

    public float getFitness()
    {
        return this.fitness;
    }

    public boolean isElite()
    {
        return this.isElite;
    }

    public void elite()
    {
        this.isElite = true;
    }

    public void calculateFitness()
    {
        float totalTeamWins = 0;
        float totalOpponentWins = 0;

        for(int i = 0; i < this.teammates.size(); i++)
        {
            totalTeamWins+=this.teammates.get(i).getWins();
        }

        for(int i = 0; i < this.opponents.size(); i++)
        {
            totalOpponentWins+=this.opponents.get(i).getWins();
        }

        float avgTeamWins = totalTeamWins/this.teammates.size();
        float avgOpponentWins = totalOpponentWins/this.opponents.size();

        this.fitness = (this.wins*(1-BotTraining.incluenceOnFitness) + (this.wins-avgTeamWins)*(BotTraining.incluenceOnFitness) + (avgOpponentWins - this.wins)*(BotTraining.incluenceOnFitness));

        this.bot.getNetwork().setFitness(this.fitness);
    }

    public void storeGame(List<TrainingBot> teammates, List<TrainingBot> opponents)
    {
        this.teammates.addAll(teammates);
        this.opponents.addAll(opponents);
    }

}
