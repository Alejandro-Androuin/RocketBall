import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

public class WindowFrame extends JFrame implements KeyListener {

    private WindowPanel wPanel;
    public static Timer frameUpdater;

    public WindowFrame()
    {
        this.wPanel = new WindowPanel();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.add(wPanel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        addKeyListener(this);
    }

    public WindowPanel getPanel()
    {
        return this.wPanel;
    }

    public void setGame(Game g)
    {
        this.wPanel.setGame(g);
    }

    public void repaintScreen()
    {
        wPanel.repaintScreen();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //int screen = wPanel.getScreen();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int screen = wPanel.getScreen();
        switch(screen)
        {
            case 0:
                break;
            case 1:
                break;
            case 2:
            case 3:
                if(e.getKeyCode() == 27)
                {
                    wPanel.setScreen(0);
                }
                else
                {
                    Controller.keyPressed(e.getKeyCode());
                }
                break;
            case 4:
                if(e.getKeyCode() == 27)
                {
                    BotTrainingScreen.escape();
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int screen = wPanel.getScreen();
        switch(screen)
        {
            case 0:
                break;
            case 1:
                break;
            case 2:
                Controller.keyReleased(e.getKeyCode());
                break;
        }
    }
}
