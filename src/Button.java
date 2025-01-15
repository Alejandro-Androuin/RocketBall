import javax.swing.*;

public class Button {

    private JButton button;
    private int screen;

    public Button(JButton button, int screen)
    {
        this.button = button;
        this.screen = screen;
    }

    public JButton getButton()
    {
        return this.button;
    }

    public int getScreen()
    {
        return this.screen;
    }

}
