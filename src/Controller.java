import java.util.ArrayList;
import java.util.List;

public class Controller {

    private static int controlsPerPlayer = 3;
    //tiltC, tiltCC, boost

    private static List<Player> players = new ArrayList<>();
    private static List<Integer> controls = new ArrayList<>();

    public static void addPlayer(Player p, List<Integer> keys)
    {
        if(keys.size() == controlsPerPlayer)
        {
            players.add(p);
            for(int i = 0; i < keys.size(); i++)
            {
                controls.add(keys.get(i));
            }
        }
        else
        {
            //If statement must be true for program to work
        }
    }

    public static void removePlayer(Player p)
    {
        for(int i = 0; i < players.size(); i++)
        {
            if(players.get(i) == p)
            {
                players.remove(i);
                int remove = controlsPerPlayer*i;
                for(int j = 0; j < controlsPerPlayer; j++)
                {
                    controls.remove(remove);
                }
            }
        }
    }

    public static void keyPressed(int key)
    {
        for(int i = 0; i < controls.size(); i++)
        {
            if(controls.get(i) == key)
            {
                Player p = players.get(i/controlsPerPlayer);
                p.activateControl(i%controlsPerPlayer);
            }
        }
    }

    public static void keyReleased(int key)
    {
        for(int i = 0; i < controls.size(); i++)
        {
            if(controls.get(i) == key)
            {
                Player p = players.get(i/controlsPerPlayer);
                p.removeControl(i%controlsPerPlayer);
            }
        }
    }



}
