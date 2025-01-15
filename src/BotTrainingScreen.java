import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BotTrainingScreen {

    private static WindowPanel panel;
    public static List<Button> buttons = new ArrayList<>();

    public static List<GenerationGraph> graphs = new ArrayList<>();

    public static void setPanel(WindowPanel w)
    {
        panel = w;
    }

    public static boolean showButtons = false;

    public static boolean training = false;

    public static int screen = 0;
    //0 is main screen
    //1 is game screen

    public static void escape()
    {
        if(screen == 0)
        {
            removeButtons();
            panel.setScreen(0);
        }
        else if(screen == 1)
        {
            setScreen(0);
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

    public static void setScreen(int s)
    {
        if(screen == 0)
        {
            removeButtons();
        }
        else if(screen == 1)
        {
            panel.getGame().cancel();
            panel.setGame(null);
        }

        screen = s;

        if(screen == 0)
        {

        }
        else if(screen == 1)
        {

        }
    }

    public static void print(Graphics2D g2d)
    {
        if(screen == 0)
        {
            Font f = new Font("Tahoma", Font.BOLD, 25);
            if(!showButtons)
            {
                for(int i = 0; i < buttons.size(); i++)
                {
                    buttons.get(i).getButton().setVisible(true);
                }

                showButtons = true;
            }

            if(BotTrainingScreen.training)
            {
                panel.drawText(g2d, Color.GREEN, f, "Training: On", (int) (panel.width*0.1), (int)(panel.height*0.04));
            }
            else
            {
                panel.drawText(g2d, Color.RED, f, "Training: Off", (int) (panel.width*0.1), (int)(panel.height*0.04));
            }

            for(int i = 0; i < graphs.size(); i++)
            {
                graphs.get(i).printGraph(g2d);
            }
        }
        else if(screen == 1)
        {
            panel.drawGame(g2d);
        }


        //panel.drawText(g2d, Color.WHITE, f, "Create Game", panel.width/2, (int)(panel.height*0.05));

        //g2d.drawLine(0, (int)(panel.getHeight()*.1), panel.getWidth(), (int)(panel.getHeight()*.1));
        //g2d.drawLine(0, (int)(panel.getHeight()*.11), panel.getWidth(), (int)(panel.getHeight()*.11));

        //panel.drawText(g2d, Color.WHITE, f.deriveFont(50F), "Team Count", panel.width/2, (int)(panel.height*0.15));
    }

    public static void addAllButtons()
    {
        JButton button;

        int bw = 250;
        int bh = 50;
        int ts = 20;
        double placementOfFirstButtonY = 0.1;
        double placementOfFirstButtonX = 0.1;

        //Add toggle train bots button
        button = new JButton();
        button.setBounds((int) (WindowPanel.width*placementOfFirstButtonX - bw/2 + bw*1.1*0), (int)(WindowPanel.height*placementOfFirstButtonY - bh/2 + bh*1.1*0), bw, bh);
        button.addActionListener(

                (e) -> {
                    if(BotTrainingScreen.training)
                    {
                        BotTraining.endTraining();
                    }
                    else
                    {
                        BotTrainingScreen.training = BotTraining.startTraining();
                    }
                }
        );

        button.setText("Toggle Training");
        button.setHorizontalAlignment(JButton.CENTER);
        button.setVerticalAlignment(JButton.CENTER);
        button.setFont(new Font("Tahoma", Font.BOLD, ts));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFocusable(false);
        button.setVisible(false);

        buttons.add(new Button(button, 4));
        panel.add(button);

        //Add Watch Game Button
        button = new JButton();
        button.setBounds((int) (WindowPanel.width*placementOfFirstButtonX - bw/2 + bw*1.1*0), (int)(WindowPanel.height*placementOfFirstButtonY - bh/2 + bh*1.1*1), bw, bh);
        button.addActionListener(

                (e) -> {
                    if(!training)
                    {
                        if(BotTraining.trainingBots.size() > 2)
                        {
                            List<Bot> redTeam = new ArrayList<>();
                            List<Bot> blueTeam = new ArrayList<>();
                            for(int i = 0; i < BotTraining.teamSize; i++)
                            {
                                redTeam.add(BotTraining.trainingBots.get(0).get(i).getBot());
                                blueTeam.add(BotTraining.trainingBots.get(1).get(i).getBot());
                            }

                            Game g = new Game(redTeam, new ArrayList<>(), blueTeam, new ArrayList<>());
                            panel.setGame(g);
                            setScreen(1);
                            g.runGameRealTime();
                        }
                    }
                }
        );

        button.setText("Watch Game");
        button.setHorizontalAlignment(JButton.CENTER);
        button.setVerticalAlignment(JButton.CENTER);
        button.setFont(new Font("Tahoma", Font.BOLD, ts));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFocusable(false);
        button.setVisible(false);

        buttons.add(new Button(button, 4));
        panel.add(button);
    }

    public static void addAllGraphs()
    {
        graphs.add(new GenerationGraph("Average Goals", "Generation", new Pixel((short) 500, (short) 75), new Pixel((short) 900, (short) 500)));
        graphs.add(new GenerationGraph("Average Ball Touches", "Generation" , new Pixel((short) 950, (short) 75), new Pixel((short) 1350, (short) 500)));
        graphs.add(new GenerationGraph("Average Rocket Velocity", "Generation" , new Pixel((short) 500, (short) 550), new Pixel((short) 900, (short) 975)));
        graphs.add(new GenerationGraph("Average Ball Velocity", "Generation" , new Pixel((short) 950, (short) 550), new Pixel((short) 1350, (short) 975)));
    }

}
