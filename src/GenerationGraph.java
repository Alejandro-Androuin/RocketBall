import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GenerationGraph {

    private String title;
    /* ALL TITLE NAME
    Average Goals
    Average Ball Touches
     */

    private String xHeading;
    private Pixel topLeft;
    private Pixel bottomRight;

    private Pixel graphTopLeft;
    private Pixel graphBottomRight;


    private List<Cord> dataPoints = new ArrayList<>();
    private List<Cord> elitePoints = new ArrayList<>();
    private int maxDataPoints = 10;

    private List<Integer> xTickers = new ArrayList<>();
    private int numXTickers = 5;
    private int numYTickers = 4;

    private int xTickerLength = 20;
    private int xTickerSpace = 30;
    private int xTickerSize = 20;

    private int yTickerLength = 20;
    private int yTickerSpace = 30;
    private int yTickerSize = 20;

    private int counter = 1;
    private int maxCounter = 1;
    private Cord calculateCord = null;

    private float highestPoint = 0;

    private static final int titleSize = 30;
    private static final float percentDown = 0.07f;
    private static final int spaceForTitle = 60;

    private static final int spaceForLeftHeading = 70;

    private static final int spaceForBottomHeading = 70;

    private static final int spaceForRight = 20;

    private static final int numberSize = 10;
    private static final int spaceHighestPoint = 20;

    public GenerationGraph(String title, String xHeading, Pixel topLeft, Pixel bottomRight)
    {
        this.title = title;
        this.xHeading = xHeading;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;

        this.graphTopLeft = new Pixel((short) (this.topLeft.getX()+spaceForLeftHeading), (short) (this.topLeft.getY()+spaceForTitle+spaceHighestPoint));
        this.graphBottomRight = new Pixel((short) (this.bottomRight.getX() - spaceForRight), (short) (this.bottomRight.getY()-spaceForBottomHeading));
    }

    public void insertData(List<Game> games, int generation)
    {
        float dataValue = 0;
        float eliteDataValue = 0;
        int eliteCount = 0;

        if(this.title.equalsIgnoreCase("Average Goals"))
        {
            for(int i = 0; i < games.size(); i++)
            {
                dataValue  += games.get(i).getGameState().getRedScore() + games.get(i).getGameState().getBlueScore();

                if(games.get(i).isPro())
                {
                    eliteDataValue += games.get(i).getGameState().getRedScore() + games.get(i).getGameState().getBlueScore();
                    eliteCount++;
                }
            }

            dataValue /= games.size();
            eliteDataValue /= eliteCount;

            /*
            if(this.dataPoints.size() < 2 || generation-this.dataPoints.get(this.dataPoints.size()-1).getX() == this.dataPoints.get(this.dataPoints.size()-1).getX() - this.dataPoints.get(this.dataPoints.size()-2).getX())
            {
                float totalGoals = 0;

                for(int i = 0; i < games.size(); i++)
                {
                    totalGoals += games.get(i).getGameState().getRedScore() + games.get(i).getGameState().getBlueScore();
                }

                float averageGoals = totalGoals/games.size();

                this.dataPoints.add(new Cord(generation, averageGoals));
            }
             */
        }
        else if(this.title.equalsIgnoreCase("Average Ball Touches"))
        {
            for(int i = 0; i < games.size(); i++)
            {
                dataValue += games.get(i).getGameState().getRedBallTouches() + games.get(i).getGameState().getBlueBallTouches();

                if(games.get(i).isPro())
                {
                    eliteDataValue += games.get(i).getGameState().getRedBallTouches() + games.get(i).getGameState().getBlueBallTouches();
                    eliteCount++;
                }
            }

            dataValue /= games.size();
            eliteDataValue /= eliteCount;
        }
        else if(this.title.equalsIgnoreCase("Average Rocket Velocity"))
        {
            for(int i = 0; i < games.size(); i++)
            {
                dataValue += games.get(i).getGameState().getAverageRocketVelocity();

                if(games.get(i).isPro())
                {
                    eliteDataValue += games.get(i).getGameState().getAverageRocketVelocity();
                    eliteCount++;
                }
            }

            dataValue /= games.size();
            eliteDataValue /= eliteCount;
        }
        else if(this.title.equalsIgnoreCase("Average Ball Velocity"))
        {
            for(int i = 0; i < games.size(); i++)
            {
                dataValue += games.get(i).getGameState().getAverageBallVelocity();

                if(games.get(i).isPro())
                {
                    eliteDataValue += games.get(i).getGameState().getAverageBallVelocity();
                    eliteCount++;
                }
            }

            dataValue /= games.size();
            eliteDataValue /= eliteCount;
        }

        /*
        if(this.dataPoints.size() < 2 || this.maxCounter == 1)
        {
            this.dataPoints.add(new Cord(generation, dataValue));
        }
        else if(this.counter % this.maxCounter == 0)
        {

        }
        else
        {
            if(this.counter == 1)
            {
                this.calculateCord = new Cord(this.dataPoints.get(this.dataPoints.size()-1).getX()+this.maxCounter, dataValue);
            }
            else
            {
                float calcVal = this.calculateCord.getY();
                float weight = (float) (this.counter - 1.0)/(this.counter);

                //multiply by weight
                calcVal *= weight;

                //Add new value
                calcVal += dataValue*(1-weight);

                //Set the value
                this.calculateCord.setY(calcVal);
            }

            float calcVal = this.dataPoints.get(this.dataPoints.size()-1).getY();
            float weight = (float) ((this.maxCounter + this.counter - 1.0)/(this.maxCounter + this.counter));

            //multiply by weight
            calcVal *= weight;

            //Add new value
            calcVal += dataValue*(1-weight);

            //Set the value
            this.dataPoints.get(this.dataPoints.size()-1).setX(calcVal);

            counter++;
        }

         */

        this.dataPoints.add(new Cord(generation, dataValue));
        this.elitePoints.add(new Cord(generation, eliteDataValue));

        if((this.xTickers.size() < numXTickers))
        {
            this.xTickers.add(generation);
        }
        else if(generation >= ((xTickers.size()+1) * xTickers.get(xTickers.size()-1))/xTickers.size())
        {
            //Update x Tickers
            for(int i = 0; i < xTickers.size(); i++)
            {
                xTickers.set(i, i*generation/(xTickers.size()-1));
            }
        }

        calculateHighestPoint();

        /*
        Size == 13
        original 0 1 2 3 4 5 6 7 8 9 10 11 12
        remove 1 3 5 7 9 11
        remaining 0 2 4 6 8 10 12
        remove 2 6 10
        remaining 0 4 8 12
        if(this.dataPoints.size() > maxDataPoints)
        {
            for(int i = 1; i < this.dataPoints.size(); i++)
            {
                this.dataPoints.remove(i);
                System.out.println("removing " + (2*i-1));
            }
        }
        */
    }

    public void printGraph(Graphics2D g2d)
    {
        //Draw graph

        g2d.setColor(Color.BLUE);
        for(int i = 1; i < this.elitePoints.size(); i++)
        {
            Pixel from = convertPointToPixel(this.elitePoints.get(i-1));
            Pixel to = convertPointToPixel(this.elitePoints.get(i));
            g2d.drawLine(from.getX(), from.getY(), to.getX(), to.getY());
        }

        if(BotTrainingScreen.training)
        {
            g2d.setColor(Color.GREEN);
        }
        else
        {
            g2d.setColor(Color.RED);
        }

        for(int i = 1; i < this.dataPoints.size(); i++)
        {
            Pixel from = convertPointToPixel(this.dataPoints.get(i-1));
            Pixel to = convertPointToPixel(this.dataPoints.get(i));
            g2d.drawLine(from.getX(), from.getY(), to.getX(), to.getY());
        }

        //Draw border
        g2d.setColor(Color.WHITE);
        g2d.drawLine(topLeft.getX(), topLeft.getY(), bottomRight.getX(), topLeft.getY());
        g2d.drawLine(bottomRight.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY());
        g2d.drawLine(bottomRight.getX(), bottomRight.getY(), topLeft.getX(), bottomRight.getY());
        g2d.drawLine(topLeft.getX(), bottomRight.getY(), topLeft.getX(), topLeft.getY());

        //Draw title
        WindowPanel.drawText(g2d, Color.WHITE, new Font("Tahoma", Font.BOLD, titleSize), this.title, ((bottomRight.getX()-topLeft.getX())/2) + topLeft.getX(), topLeft.getY() + (int)((bottomRight.getY() - topLeft.getY())*percentDown));

        //Draw title bar
        g2d.drawLine(topLeft.getX(), topLeft.getY() + spaceForTitle, bottomRight.getX(), topLeft.getY() + spaceForTitle);

        //Draw left bar and bottom bar
        g2d.drawLine(topLeft.getX()+spaceForLeftHeading, topLeft.getY() + spaceForTitle, topLeft.getX()+spaceForLeftHeading, bottomRight.getY() - spaceForBottomHeading);
        g2d.drawLine(topLeft.getX()+spaceForLeftHeading, bottomRight.getY() - spaceForBottomHeading, bottomRight.getX(), bottomRight.getY() - spaceForBottomHeading);

        //Draw Tickers
        drawYTickers(g2d);
        drawXTickers(g2d);
    }

    public void drawYTickers(Graphics2D g2d)
    {
        if(this.dataPoints.size() > 1)
        {
            //Draw y tickers
            for(int i = 0; i < this.numYTickers; i++)
            {
                g2d.setColor(Color.WHITE);

                int y = (int) ((i/(this.numYTickers-1.0))*this.highestPoint);

                Cord c = new Cord(0, y);
                Pixel p = convertPointToPixel(c);

                g2d.drawLine(p.getX()-(this.yTickerLength/2), p.getY(), p.getX()+(this.yTickerLength/2), p.getY());

                if(BotTrainingScreen.training)
                {
                    WindowPanel.drawText(g2d, Color.GREEN, new Font("Tahoma", Font.BOLD, this.yTickerSize), String.valueOf(y), p.getX()-this.yTickerSpace, p.getY());
                }
                else
                {
                    WindowPanel.drawText(g2d, Color.RED, new Font("Tahoma", Font.BOLD, this.yTickerSize), String.valueOf(y), p.getX()-this.yTickerSpace, p.getY());
                }
            }
        }
    }

    public void drawXTickers(Graphics2D g2d)
    {
        if(this.dataPoints.size() > 1)
        {
            /*
            int generation = (int) this.dataPoints.get(this.dataPoints.size()-1).getX();

            //Draw x tickers
            if(this.xTickers.size() < this.numXTickers)
            {
                this.xTickers.add((int) this.dataPoints.get(this.dataPoints.size()-1).getX());
            }

            if(this.xTickers.size() == this.numXTickers)
            {
                if((this.xTickers.get(this.xTickers.size()-1) - this.xTickers.get(0))/this.xTickers.size() < this.dataPoints.get(this.dataPoints.size()-1).getX() - this.xTickers.get(this.xTickers.size()-1))
                {
                    this.xTickers = new ArrayList<>();
                    System.out.println("Here2!");
                    for(int i = 0; i < this.numXTickers; i++)
                    {
                        this.xTickers.add((i*generation)/(this.numXTickers-1));
                        System.out.println("Adding " + this.xTickers.get(this.xTickers.size()-1));
                    }
                }
            }

            */

            //Draw x tickers
            for(int i = 0; i < this.xTickers.size(); i++)
            {
                g2d.setColor(Color.WHITE);

                Cord c = new Cord(this.xTickers.get(i), 0);
                Pixel p = convertPointToPixel(c);

                g2d.drawLine(p.getX(), p.getY()-(this.xTickerLength/2), p.getX(), p.getY()+(this.xTickerLength/2));

                if(BotTrainingScreen.training)
                {
                    WindowPanel.drawText(g2d, Color.GREEN, new Font("Tahoma", Font.BOLD, this.xTickerSize), String.valueOf(this.xTickers.get(i)), p.getX(), p.getY() + this.xTickerSpace);
                }
                else
                {
                    WindowPanel.drawText(g2d, Color.RED, new Font("Tahoma", Font.BOLD, this.xTickerSize), String.valueOf(this.xTickers.get(i)), p.getX(), p.getY() + this.xTickerSpace);
                }
            }
        }
    }

    public void calculateHighestPoint()
    {
        this.highestPoint = Float.MIN_VALUE;
        for(int i = 0; i < this.dataPoints.size(); i++)
        {
            if(this.dataPoints.get(i).getY() > this.highestPoint)
            {
                this.highestPoint = this.dataPoints.get(i).getY();
            }
        }

        for(int i = 0; i < this.elitePoints.size(); i++)
        {
            if(this.elitePoints.get(i).getY() > this.highestPoint)
            {
                this.highestPoint = this.elitePoints.get(i).getY();
            }
        }
    }

    public short convertXToPixel(int x)
    {
        return (short)(this.graphTopLeft.getX()+((x/(this.dataPoints.get(this.dataPoints.size()-1).getX()))*(this.graphBottomRight.getX()-this.graphTopLeft.getX())));
    }

    public short convertYToPixel(int y)
    {
        return (short) (this.graphBottomRight.getY()-((y/this.highestPoint)*(this.graphBottomRight.getY()-this.graphTopLeft.getY())));
    }

    public Pixel convertPointToPixel(Cord c)
    {
        short x = (short) (this.graphTopLeft.getX()+((c.getX()/(this.dataPoints.get(this.dataPoints.size()-1).getX()))*(this.graphBottomRight.getX()-this.graphTopLeft.getX())));
        short y = (short) (this.graphBottomRight.getY()-((c.getY()/this.highestPoint)*(this.graphBottomRight.getY()-this.graphTopLeft.getY())));

        return new Pixel(x,y);
    }

}
