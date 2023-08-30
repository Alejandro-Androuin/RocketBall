import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlayGame {

    private static WindowPanel panel;
    public static List<Button> buttons = new ArrayList<>();

    private static boolean configured = false;

    private static int minTeamSize = 0;
    private static int selectedTeamSize = 0;

    private static List<Player> redPlayers = new ArrayList<>();
    private static List<Player> bluePlayers = new ArrayList<>();

    public static void setPanel(WindowPanel w)
    {
        panel = w;
    }

    public static boolean showButtons = false;

    public static void configure()
    {
        /*
        if(!configured)
        {
            minTeamSize = (RocketBall.players.size()+1)/2;
            if(minTeamSize == 0)
            {
                minTeamSize++;
            }
            selectedTeamSize = minTeamSize;

            configured = true;
        }
        */
        if(!configured)
        {
            selectedTeamSize = BotTraining.teamSize;

            configured = true;
        }
    }

    public static void removeButtons()
    {
        if(showButtons)
        {
            for(int i = 0; i < buttons.size(); i++)
            {
                buttons.get(i).getButton().setVisible(false);
            }

            showButtons = false;
        }
    }

    public static void print(Graphics2D g2d)
    {
        Font f = new Font("Tahoma", Font.BOLD, 75);

        if(!showButtons)
        {
            for(int i = 0; i < buttons.size(); i++)
            {
                buttons.get(i).getButton().setVisible(true);
            }

            showButtons = true;
        }

        panel.drawText(g2d, Color.WHITE, f, "Create Game", panel.width/2, (int)(panel.height*0.05));

        g2d.drawLine(0, (int)(panel.getHeight()*.1), panel.getWidth(), (int)(panel.getHeight()*.1));
        g2d.drawLine(0, (int)(panel.getHeight()*.11), panel.getWidth(), (int)(panel.getHeight()*.11));

        panel.drawText(g2d, Color.WHITE, f.deriveFont(50F), "Team Count", panel.width/2, (int)(panel.height*0.15));

        panel.drawText(g2d, Color.WHITE, f.deriveFont(40F), String.valueOf(selectedTeamSize), panel.width/2, (int)(panel.height*0.22));

        addPlayersToTeams();
        int totalPlayers = redPlayers.size()+bluePlayers.size();

        int radius = (int)(panel.height*0.05);
        int spacing = (int)(radius*2);

        for(int i = 0; i < RocketBall.players.size(); i++)
        {
            int rx = (int)(panel.width/2+((totalPlayers-1)/-2.0+i)*spacing);
            int ry = (int)(panel.height*0.35);

            if(isIn(RocketBall.players.get(i), redPlayers))
            {
                WindowPanel.images.drawRocket(g2d, rx, ry, radius, 0, true);
                panel.drawText(g2d, Color.WHITE, f.deriveFont(20F), RocketBall.players.get(i).getName(), rx, ry + (int)(2*radius));
            }
            else if(isIn(RocketBall.players.get(i), bluePlayers))
            {
                WindowPanel.images.drawRocket(g2d, rx, ry, radius, 0, false);
                panel.drawText(g2d, Color.WHITE, f.deriveFont(20F), RocketBall.players.get(i).getName(), rx, ry + (int)(2*radius));
            }
            else
            {

            }
        }
    }

    public static void addPlayersToTeams()
    {
        if(redPlayers.size() + bluePlayers.size() != RocketBall.players.size() || !isIn(redPlayers, RocketBall.players)|| !isIn(bluePlayers, RocketBall.players))
        {
            redPlayers.clear();
            bluePlayers.clear();

            for(int i = 0; i < (RocketBall.players.size()+1)/2; i++)
            {
                redPlayers.add(RocketBall.players.get(i));
            }

            for(int i = (RocketBall.players.size()+1)/2; i < RocketBall.players.size(); i++)
            {
                bluePlayers.add(RocketBall.players.get(i));
            }
        }
    }

    public static void switchTeams(Player player, boolean toRed)
    {
        if(toRed)
        {
            if(isIn(player, bluePlayers))
            {
                redPlayers.add(player);
                bluePlayers.remove(player);
                if(redPlayers.size() > selectedTeamSize)
                {
                    int index = (int)(Math.random()*(redPlayers.size()-1));
                    bluePlayers.add(redPlayers.get(index));
                    redPlayers.remove(index);
                }
                RocketBall.w.repaintScreen();
            }
        }
        else
        {
            if(isIn(player, redPlayers))
            {
                redPlayers.remove(player);
                bluePlayers.add(player);
                if(bluePlayers.size() > selectedTeamSize)
                {
                    int index = (int)(Math.random()*(bluePlayers.size()-1));
                    redPlayers.add(bluePlayers.get(index));
                    bluePlayers.remove(index);
                }
                RocketBall.w.repaintScreen();
            }
        }
    }

    public static boolean isIn(List<Player> players1, List<Player> players2)
    {
        for(int i = 0; i < players1.size(); i++)
        {
            for(int j = 0; j < players2.size(); j++)
            {
                if(players1.get(i).equals(players2.get(j)))
                {
                    break;
                }
                else if(j == players2.size()-1)
                {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isIn(Player p, List<Player> players)
    {
        for(int i = 0; i < players.size(); i++)
        {
            if(players.get(i).equals(p))
            {
                return true;
            }
        }

        return false;
    }

    public static void addAllButtons()
    {
        JButton button;

        //Add Team Size
        button = new JButton();
        button.setBounds((int)(1.05*panel.width/2 + panel.width*.001), (int) (panel.height*.22 - (panel.height*.05)/2), (int)(panel.width*.03), (int)(panel.height*.05));
        button.addActionListener(

                (e) -> {
                    //selectedTeamSize++;
                    panel.repaintScreen();
                }
        );

        button.setText("+");
        button.setHorizontalAlignment(JButton.CENTER);
        button.setVerticalAlignment(JButton.CENTER);
        button.setFont(new Font("Tahoma", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFocusable(false);
        button.setVisible(false);

        buttons.add(new Button(button, 3));
        panel.add(button);

        //Subtract Team Size
        button = new JButton();
        button.setBounds((int)(0.95*panel.width/2 - panel.width*.03), (int) (panel.height*.22 - panel.height*.05/2), (int)(panel.width*.03), (int)(panel.height*.05));
        button.addActionListener(

                (e) -> {
                    if(selectedTeamSize>minTeamSize)
                    {
                        //selectedTeamSize--;
                        if(redPlayers.size() > selectedTeamSize)
                        {
                            switchTeams(redPlayers.get((int)(Math.random()*redPlayers.size())), false);
                        }
                        else if(bluePlayers.size() > selectedTeamSize)
                        {
                            switchTeams(bluePlayers.get((int)(Math.random()*bluePlayers.size())), true);
                        }
                        panel.repaintScreen();
                    }
                }
        );

        button.setText("-");
        button.setHorizontalAlignment(JButton.CENTER);
        button.setVerticalAlignment(JButton.CENTER);
        button.setFont(new Font("Tahoma", Font.BOLD, 25));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFocusable(false);
        button.setVisible(false);

        buttons.add(new Button(button, 3));
        panel.add(button);

        //Begin game
        button = new JButton();
        button.setBounds((int)((panel.width/2)-(panel.width*0.3/2)), (int)((panel.height*.85) - (panel.height*.1/2)), (int)(panel.width*0.3), (int)(panel.height*.1));
        button.addActionListener(

                /*
                (e) -> {
                    List<Bot> bots1 = new ArrayList<>();
                    List<Bot> bots2 = new ArrayList<>();

                    int counter = 0;
                    for(int i = 0; i < selectedTeamSize-redPlayers.size(); i++)
                    {
                        bots1.add(new Bot("Bot"+counter));
                        counter++;
                    }

                    for(int i = 0; i < selectedTeamSize-bluePlayers.size(); i++)
                    {
                        bots2.add(new Bot("Bot"+counter));
                        counter++;
                    }

                    Game g = new Game(bots1, redPlayers, bots2, bluePlayers);
                    panel.setGame(g);
                    panel.setScreen(2);
                }
                */

                (e) -> {
                    List<Bot> bots1 = new ArrayList<>();
                    List<Bot> bots2 = new ArrayList<>();

                    List<TrainingBot> currentBots = new ArrayList<>();
                    for(int i = 0; i < BotTraining.trainingBots.size(); i++)
                    {
                        for(int j = 0; j < BotTraining.trainingBots.get(i).size(); j++)
                        {
                            currentBots.add(BotTraining.trainingBots.get(i).get(j));
                        }
                    }

                    BotTraining.mergeSortBots(currentBots, 0, currentBots.size()-1);

                    int counter = 0;
                    for(int i = 0; i < selectedTeamSize-redPlayers.size(); i++)
                    {
                        bots1.add(currentBots.get(counter).getBot());
                        counter++;
                    }

                    for(int i = 0; i < selectedTeamSize-bluePlayers.size(); i++)
                    {
                        bots2.add(currentBots.get(counter).getBot());
                        counter++;
                    }

                    Game g = new Game(bots1, redPlayers, bots2, bluePlayers);
                    panel.setGame(g);
                    panel.setScreen(2);
                }
        );

        button.setText("Begin Game");
        button.setHorizontalAlignment(JButton.CENTER);
        button.setVerticalAlignment(JButton.CENTER);
        button.setFont(new Font("Tahoma", Font.BOLD, 25));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFocusable(false);
        button.setVisible(false);

        buttons.add(new Button(button, 3));
        panel.add(button);
    }

}
