public class Player {

    private String name;

    boolean tiltC = false;
    boolean tiltCC = false;
    boolean boost = false;

    public Player(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void tiltC(boolean b)
    {
        this.tiltC = b;
    }

    public void tiltCC(boolean b)
    {
        this.tiltCC = b;
    }

    public void boost(boolean b)
    {
        this.boost = b;
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

    public void activateControl(int num)
    {
        switch(num)
        {
            case 0:
                tiltC(true);
                if(WindowPanel.screen == 3)
                {
                    PlayGame.switchTeams(this, false);
                }
                break;
            case 1:
                tiltCC(true);
                if(WindowPanel.screen == 3)
                {
                    PlayGame.switchTeams(this, true);
                }
                break;
            case 2:
                boost(true);
                break;
        }
    }

    public void removeControl(int num)
    {
        switch(num)
        {
            case 0:
                tiltC(false);
                break;
            case 1:
                tiltCC(false);
                break;
            case 2:
                boost(false);
                break;
        }
    }

    public void resetControls()
    {
        this.tiltC = false;
        this.tiltCC = false;
        this.boost = false;
    }

}
