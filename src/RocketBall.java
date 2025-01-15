import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RocketBall {

    public static WindowFrame w;
    public static Timer frameUpdater;
    public static int fps = 60;

    public static List<Player> players = new ArrayList<>();

    public static void main(String agrs[]) throws IOException {
        Settings.writeDefaultSettings();

        w = new WindowFrame();
        frameUpdater = new Timer();
        runFrameUpdater();

        Names.generateNames();

        //Font f[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        //for(int i = 0; i < f.length; i++)
        //{
            //System.out.println(f[i].getName());
        //}

        Player p1 = new Player("You");
        players.add(p1);
        //players.add(p2);
        //players.add(p3);

        List<Bot> botst1 = new ArrayList<>();
        List<Bot> botst2 = new ArrayList<>();

        int teamSize = 2;
        int counter = 0;

        for(int i = 0; i < teamSize-1; i++)
        {
            if(counter == 0)
            {
                botst1.add(new Bot("Bot"));
            }
            else
            {
                botst1.add(new Bot("Bot"+i));
            }

            counter++;
        }

        for(int i = counter; i < counter+teamSize; i++)
        {
            botst2.add(new Bot("Bot"+i));
        }

        List<Bot> bots1 = new ArrayList<>();
        bots1.addAll(botst1.subList(0, teamSize-1));

        List<Player> players1 = new ArrayList<>();
        //players1.add(p1);
        //players1.add(p2);

        List<Bot> bots2 = new ArrayList<>();
        bots2.addAll(botst2.subList(0, teamSize));

        List<Player> players2 = new ArrayList<>();
        //players2.add(p2);

        List<Integer> p1Controls = new ArrayList<>();
        p1Controls.add(68);
        p1Controls.add(65);
        p1Controls.add(66);
        Controller.addPlayer(p1, p1Controls);

        List<Integer> p2Controls = new ArrayList<>();
        p2Controls.add(68);
        p2Controls.add(65);
        p2Controls.add(66);
        //Controller.addPlayer(p2, p2Controls);

        List<Integer> p3Controls = new ArrayList<>();
        p3Controls.add(68);
        p3Controls.add(65);
        p3Controls.add(66);
        //Controller.addPlayer(p3, p3Controls);

        //Game game = new Game(bots1, players1, bots2, players2);

        //w.setGame(game);

        //game.runGameRealTime();
        BotTraining.newTraining();
    }

    public static void runFrameUpdater()
    {
        frameUpdater.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                w.repaintScreen();
            }
        }, 0L, 1000/fps);
    }



}
