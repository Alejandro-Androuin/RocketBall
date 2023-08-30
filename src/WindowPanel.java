import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WindowPanel extends JPanel{

    public static Images images = new Images();

    private Game game = null;

    public static int width = 1925;
    public static int height = 1025;

    public static int xDistBorder = 40;
    public static int yDistBorder = 40;

    private boolean repainting = true;

    public static int screen = 0;

    //0 = Main Menu
    //1 = Saves Menu
    //2 = Live Game
    //3 = Create Game Menu
    //4 = Bot Training

    public static boolean debug = true;

    List<Button> buttons;

    public WindowPanel()
    {
        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(null);
        this.setBackground(Color.black);
        this.setVisible(true);

        this.buttons = new ArrayList<>();

        addAllButtons();
        addButtons();
    }

    public int getScreen()
    {
        return this.screen;
    }

    public void setScreen(int screen)
    {
        switch(this.screen)
        {
            case 0:
                break;
            case 1:
                break;
            case 2:
                this.game.cancel();
                this.game = null;
                break;
            case 3:
                PlayGame.removeButtons();
                break;
            case 4:
                BotTrainingScreen.removeButtons();
        }

        removeButtons();
        this.screen = screen;
        addButtons();

        switch(this.screen)
        {
            case 0:
                break;
            case 1:
                break;
            case 2:
                this.game.runGameRealTime();
                break;
            case 3:
                PlayGame.setPanel(this);
                PlayGame.configure();
                break;
            case 4:
                BotTrainingScreen.setPanel(this);
                break;
        }

        repaintScreen();
    }

    public void repaintScreen()
    {
        this.repainting = true;
        repaint();
    }

    @Override
    public void paint(Graphics g)
    {
        if(!this.repainting)
        {
            //Do nothing
        }
        else
        {
            super.paint(g);
            Graphics2D g2d = (Graphics2D) g;

            switch(this.screen)
            {
                case 0:
                    drawMainMenu(g2d);
                    break;
                case 1:

                    break;
                case 2:
                    drawGame(g2d);
                    break;
                case 3:
                    PlayGame.print(g2d);
                    break;
                case 4:
                    BotTrainingScreen.print(g2d);
                    break;
            }

            this.repainting = false;
        }
    }

    public void setGame(Game g)
    {
        this.game = g;
    }

    public Game getGame()
    {
        return this.game;
    }

    private void drawMainMenu(Graphics2D g2d)
    {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Tahoma", Font.BOLD, 100));
        String menuName = "Rocket Ball";
        int blueWidth = (int)g2d.getFontMetrics().getStringBounds(menuName, g2d).getWidth();
        g2d.drawString(menuName, this.width/2-blueWidth/2, this.height/8);

        this.images.drawBall(g2d, this.width/2, this.height/4, 70);

        this.images.drawRocket(g2d, 5*this.width/12, 9*this.height/32, 55, 60, true);
        this.images.drawRocket(g2d, 7*this.width/12, 9*this.height/32, 55, -60, false);


    }

    public void drawGame(Graphics2D g2d)
    {
        if(this.game != null)
        {
            drawScore(g2d);
            drawTimeLeft(g2d);
            drawNameTags(g2d);

            GameState gs = this.game.getGameState();

            List<Rocket> rockets = gs.getRockets();
            Ball ball = gs.getBall();

            for(int i = 0; i < rockets.size(); i++)
            {
                this.images.drawRocket(g2d, rockets.get(i));
            }
            this.images.drawBall(g2d, ball);

            drawGameBorder(g2d);

            if(this.debug)
            {
                drawHitboxes(g2d);
            }

            if(this.game.getCountDownTicks() != 0)
            {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Times New Roman", Font.BOLD, 150));
                String seconds = String.valueOf(this.game.getCountDownTicks()/Game.tps+1);
                int secondsWidth = (int)g2d.getFontMetrics().getStringBounds(seconds, g2d).getWidth();
                int secondsHeight = (int)g2d.getFontMetrics().getStringBounds(seconds, g2d).getHeight();
                g2d.drawString(seconds, this.width/2 - secondsWidth/2, this.height/2-secondsHeight/2);
            }

            if(this.game.gameOver())
            {
                Color gameOverColor;
                if(this.game.redWin())
                {
                    gameOverColor = Color.RED;
                }
                else
                {
                    gameOverColor = Color.BLUE;
                }
                Font f = new Font("Tahoma", Font.BOLD, 200);
                drawText(g2d, gameOverColor, f, "Game Over", this.width/2, 5*this.height/14);
                drawText(g2d, Color.WHITE, f.deriveFont(80F), "Final Score", this.width/2, this.height/2);
                drawText(g2d, Color.RED, f.deriveFont(90F), String.valueOf(this.game.getGameState().getRedScore()), (int)(this.width*0.44), (int)(this.height*0.6));
                drawText(g2d, Color.BLUE, f.deriveFont(90F), String.valueOf(this.game.getGameState().getBlueScore()), (int)(this.width*0.56), (int)(this.height*0.6));
                drawText(g2d, Color.WHITE, f.deriveFont(90F), "-", this.width/2, (int)(this.height*0.6));
            }

        }
        else
        {

        }
    }

    private void drawGameBorder(Graphics2D g2d)
    {
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(5));

        g2d.drawLine(xDistBorder, yDistBorder, width- xDistBorder, yDistBorder);

        g2d.setColor(Color.BLUE);
        g2d.drawLine(width- xDistBorder, yDistBorder, width- xDistBorder, height- yDistBorder);

        g2d.setColor(Color.WHITE);
        g2d.drawLine(width- xDistBorder, height- yDistBorder, xDistBorder, height- yDistBorder);

        g2d.setColor(Color.RED);
        g2d.drawLine(xDistBorder, height- yDistBorder, xDistBorder, yDistBorder);
    }

    private void drawHitboxes(Graphics2D g2d)
    {
        if(this.game != null)
        {
            GameState gs = this.game.getGameState();

            g2d.setStroke(new BasicStroke(3));

            List<Rocket> rockets = this.game.getGameState().getRockets();
            g2d.setColor(Color.GREEN);

            for(int i = 0; i < rockets.size(); i++)
            {
                Cord rocketCord = new Cord(rockets.get(i).getCord().getX(), rockets.get(i).getCord().getY());
                float hitboxRadius = rockets.get(i).getHitboxRadius();

                rocketCord.addX(-hitboxRadius);
                rocketCord.addY(hitboxRadius);

                Pixel rocketPixel = rocketCord.getPixel(gs);

                g2d.drawOval(rocketPixel.getX(), rocketPixel.getY(), (int)Cord.convertXLengthToPixel(hitboxRadius*2, gs), (int)Cord.convertYLengthToPixel(hitboxRadius*2, gs));

                //DELETE LATER
                int length = 50;
                Cord rocketCord2 = new Cord(rockets.get(i).getCord().getX(), rockets.get(i).getCord().getY());
                Pixel rp2 = rocketCord2.getPixel(gs);
                g2d.drawLine((int) rp2.getX(), (int) rp2.getY(), (int) (rp2.getX() + (rockets.get(i).getTargetVector().getX())*length), (int) (rp2.getY() - (rockets.get(i).getTargetVector().getY())*length));

            }

            if(gs.getBall().isRed())
            {
                g2d.setColor(Color.RED);
            }
            else
            {
                g2d.setColor(Color.BLUE);
            }

            Cord ballCord = new Cord(this.game.getGameState().getBall().getX(), this.game.getGameState().getBall().getY());
            short hitboxRadius = this.game.getGameState().getBall().getHitboxRadius();

            ballCord.addX(-hitboxRadius);
            ballCord.addY(hitboxRadius);

            Pixel ballPixel = ballCord.getPixel(gs);

            g2d.drawOval(ballPixel.getX(), ballPixel.getY(), (int)Cord.convertXLengthToPixel(hitboxRadius*2, gs), (int)Cord.convertYLengthToPixel(hitboxRadius*2, gs));
        }
        else
        {
            //Do nothing
        }
    }

    private void drawScore(Graphics2D g2d)
    {
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Times New Roman", Font.PLAIN, 50));
        String redScore = String.valueOf(this.game.getGameState().getRedScore());
        int redWidth = (int)g2d.getFontMetrics().getStringBounds(redScore, g2d).getWidth();
        g2d.drawString(redScore, this.width/3 - redWidth/2, this.height/12);

        g2d.setColor(Color.BLUE);
        g2d.setFont(new Font("Times New Roman", Font.PLAIN, 50));
        String blueScore = String.valueOf(this.game.getGameState().getBlueScore());
        int blueWidth = (int)g2d.getFontMetrics().getStringBounds(blueScore, g2d).getWidth();
        g2d.drawString(String.valueOf(this.game.getGameState().getBlueScore()), 2*this.width/3-blueWidth/2, this.height/12);
    }

    private void drawTimeLeft(Graphics2D g2d)
    {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Times New Roman", Font.PLAIN, 50));
        String timeLeft = (int) (this.game.getTimeRemaining()) + "." + (int) (100 * (this.game.getTimeRemaining() - (int) (this.game.getTimeRemaining())));
        int timeWidth = (int)g2d.getFontMetrics().getStringBounds(timeLeft, g2d).getWidth();
        g2d.drawString(timeLeft, this.width/2-timeWidth/2, this.height/12);
    }

    private void drawNameTags(Graphics2D g2d)
    {
        GameState gs = this.game.getGameState();
        List<Rocket> rockets = gs.getRockets();

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Times New Roman", Font.PLAIN, 20));

        for(int i = 0; i < rockets.size(); i++)
        {
            String name = rockets.get(i).getName();
            int nameWidth = (int)g2d.getFontMetrics().getStringBounds(name, g2d).getWidth();
            int nameHeight = (int)g2d.getFontMetrics().getStringBounds(name, g2d).getHeight();
            g2d.drawString(name, rockets.get(i).getCord().getPixel(gs).getX()-nameWidth/2, rockets.get(i).getCord().getPixel(gs).getY()-Cord.convertYLengthToPixel(Rocket.hRadius, gs)-nameHeight/2);
        }
    }

    private void removeButtons()
    {
        for(int i = 0; i < this.buttons.size(); i++)
        {
            if(this.buttons.get(i).getScreen() == this.screen)
            {
                this.buttons.get(i).getButton().setVisible(false);
            }
        }
    }

    private void addButtons()
    {
        for(int i = 0; i < this.buttons.size(); i++)
        {
            if(this.buttons.get(i).getScreen() == this.screen)
            {
                this.buttons.get(i).getButton().setVisible(true);
            }
        }
    }

    private void addAllButtons()
    {
        JButton button;

        //For buttons on main menu
        int bw = 500;
        int bh = 50;
        int ts = 20;
        double placementOfFirstButton = 0.4;

        //Saves button main menu
        button = new JButton();
        button.setBounds(this.width/2-bw/2, (int)(this.height*placementOfFirstButton - bh/2), bw, bh);
        button.addActionListener(

                (e) -> {

                }
        );

        button.setText("Saves");
        button.setHorizontalAlignment(JButton.CENTER);
        button.setVerticalAlignment(JButton.CENTER);
        button.setFont(new Font("Tahoma", Font.BOLD, ts));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFocusable(false);

        this.buttons.add(new Button(button, 0));

        //Play Game button main menu
        button = new JButton();
        button.setBounds(this.width/2-bw/2, (int)(this.height*placementOfFirstButton - bh/2 + bh*1.1*1), bw, bh);
        button.addActionListener(

                (e) -> {
                    setScreen(3);
                }
        );

        button.setText("Create Game");
        button.setHorizontalAlignment(JButton.CENTER);
        button.setVerticalAlignment(JButton.CENTER);
        button.setFont(new Font("Tahoma", Font.BOLD, ts));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFocusable(false);

        this.buttons.add(new Button(button, 0));

        //Bot Training button Main Menu
        button = new JButton();
        button.setBounds(this.width/2-bw/2, (int)(this.height*placementOfFirstButton - bh/2 + bh*1.1*2), bw, bh);
        button.addActionListener(

                (e) -> {
                    setScreen(4);
                }
        );

        button.setText("Bot Training");
        button.setHorizontalAlignment(JButton.CENTER);
        button.setVerticalAlignment(JButton.CENTER);
        button.setFont(new Font("Tahoma", Font.BOLD, ts));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFocusable(false);

        this.buttons.add(new Button(button, 0));

        PlayGame.setPanel(this);
        PlayGame.addAllButtons();

        BotTrainingScreen.setPanel(this);
        BotTrainingScreen.addAllButtons();
        BotTrainingScreen.addAllGraphs();

        for(int i = 0; i < this.buttons.size(); i++)
        {
            this.buttons.get(i).getButton().setVisible(false);
            this.add(this.buttons.get(i).getButton());
        }
    }

    public static void drawText(Graphics2D g2d, Color c, Font f, String s, int cx, int cy)
    {
        g2d.setColor(c);
        g2d.setFont(f);
        int sWidth = (int)g2d.getFontMetrics().getStringBounds(s, g2d).getWidth();
        int sHeight = 2*(int)g2d.getFontMetrics().getStringBounds(s, g2d).getHeight()/3;
        g2d.drawString(s, cx-sWidth/2, cy+sHeight/2);
    }
}
