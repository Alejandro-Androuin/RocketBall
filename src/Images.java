import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Images {

    private BufferedImage rocketRedImage = null;
    private BufferedImage rocketBlueImage = null;
    private double rocketWidthP = 0.0035;
    private double rocketHeightP = 0.0038;

    private BufferedImage flameImage = null;
    private double flameWidthP = 0.0028;
    private double flameHeightP = 0.0038;
    private double pixDistUnderRocket = 0.35;

    private BufferedImage ballImage = null;
    private double ballWidthP = 0.0028;
    private double ballHeightP = 0.0028;

    public Images()
    {
        generateImages();
    }

    public void generateImages()
    {
        try {
            this.rocketRedImage = ImageIO.read(new File("rocket_red.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.rocketBlueImage = ImageIO.read(new File("rocket_blue.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.flameImage = ImageIO.read(new File("flame.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.ballImage = ImageIO.read(new File("ball.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawRocket(Graphics2D g2d, Rocket rocket)
    {
        double rad = Math.toRadians(rocket.getDegree());
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);

        GameState gs = rocket.getGameState();

        float x = rocket.getCord().getPixel(gs).getX();
        float y = rocket.getCord().getPixel(gs).getY();

        if(rocket.boost())
        {
            int boostx = (int) (x-(this.pixDistUnderRocket*Cord.convertXLengthToPixel(Rocket.hRadius, gs)*sin));
            int boosty = (int) (y+(this.pixDistUnderRocket*Cord.convertYLengthToPixel(Rocket.hRadius, gs)*cos));
            AffineTransform at = AffineTransform.getTranslateInstance(boostx-this.flameImage.getWidth()*this.flameWidthP*Cord.convertXLengthToPixel(Rocket.hRadius, gs)/2,boosty - this.flameImage.getHeight()*this.flameHeightP*Cord.convertYLengthToPixel(Rocket.hRadius, gs)/2);
            at.rotate(rad, this.flameImage.getWidth()*this.flameWidthP*Cord.convertXLengthToPixel(Rocket.hRadius, gs)/2, this.flameImage.getHeight()*this.flameHeightP*Cord.convertYLengthToPixel(Rocket.hRadius, gs)/2);
            at.scale(this.flameWidthP*Cord.convertXLengthToPixel(Rocket.hRadius, gs),this.flameHeightP*Cord.convertYLengthToPixel(Rocket.hRadius, gs));

            g2d.drawImage(this.flameImage, at, null);
        }

        if (rocket.isRed())
        {
            AffineTransform at = AffineTransform.getTranslateInstance(x-this.rocketRedImage.getWidth()*this.rocketWidthP*Cord.convertXLengthToPixel(Rocket.hRadius, gs)/2,y - this.rocketRedImage.getHeight()*this.rocketHeightP*Cord.convertYLengthToPixel(Rocket.hRadius, gs)/2);
            at.rotate(rad, this.rocketRedImage.getWidth()*this.rocketWidthP*Cord.convertXLengthToPixel(Rocket.hRadius, gs)/2, this.rocketRedImage.getHeight()*this.rocketHeightP*Cord.convertYLengthToPixel(Rocket.hRadius, gs)/2);
            at.scale(this.rocketWidthP*Cord.convertXLengthToPixel(Rocket.hRadius, gs),this.rocketHeightP*Cord.convertYLengthToPixel(Rocket.hRadius, gs));

            g2d.drawImage(this.rocketRedImage, at, null);
        }
        else
        {
            AffineTransform at = AffineTransform.getTranslateInstance(x-this.rocketBlueImage.getWidth()*this.rocketWidthP*Cord.convertXLengthToPixel(Rocket.hRadius, gs)/2,y - this.rocketBlueImage.getHeight()*this.rocketHeightP*Cord.convertYLengthToPixel(Rocket.hRadius, gs)/2);
            at.rotate(rad, this.rocketBlueImage.getWidth()*this.rocketWidthP*Cord.convertXLengthToPixel(Rocket.hRadius, gs)/2, this.rocketBlueImage.getHeight()*this.rocketHeightP*Cord.convertYLengthToPixel(Rocket.hRadius, gs)/2);
            at.scale(this.rocketWidthP*Cord.convertXLengthToPixel(Rocket.hRadius, gs),this.rocketHeightP*Cord.convertYLengthToPixel(Rocket.hRadius, gs));

            g2d.drawImage(this.rocketBlueImage, at, null);
        }
    }

    public void drawRocket(Graphics2D g2d, int x, int y, int radius, int degree, boolean isRed)
    {
        double rad = Math.toRadians(degree);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);

        int boostx = (int) (x-(this.pixDistUnderRocket*radius*sin));
        int boosty = (int) (y+(this.pixDistUnderRocket*radius*cos));
        AffineTransform at = AffineTransform.getTranslateInstance(boostx-this.flameImage.getWidth()*this.flameWidthP*radius/2,boosty - this.flameImage.getHeight()*this.flameHeightP*radius/2);
        at.rotate(rad, this.flameImage.getWidth()*this.flameWidthP*radius/2, this.flameImage.getHeight()*this.flameHeightP*radius/2);
        at.scale(this.flameWidthP*radius,this.flameHeightP*radius);
        g2d.drawImage(this.flameImage, at, null);

        if (isRed)
        {
            at = AffineTransform.getTranslateInstance(x-this.rocketRedImage.getWidth()*this.rocketWidthP*radius/2,y - this.rocketRedImage.getHeight()*this.rocketHeightP*radius/2);
            at.rotate(rad, this.rocketRedImage.getWidth()*this.rocketWidthP*radius/2, this.rocketRedImage.getHeight()*this.rocketHeightP*radius/2);
            at.scale(this.rocketWidthP*radius,this.rocketHeightP*radius);

            g2d.drawImage(this.rocketRedImage, at, null);
        }
        else
        {
            at = AffineTransform.getTranslateInstance(x-this.rocketBlueImage.getWidth()*this.rocketWidthP*radius/2,y - this.rocketBlueImage.getHeight()*this.rocketHeightP*radius/2);
            at.rotate(rad, this.rocketBlueImage.getWidth()*this.rocketWidthP*radius/2, this.rocketBlueImage.getHeight()*this.rocketHeightP*radius/2);
            at.scale(this.rocketWidthP*radius,this.rocketHeightP*radius);

            g2d.drawImage(this.rocketBlueImage, at, null);
        }
    }

    public void drawBall(Graphics2D g2d, Ball b)
    {
        GameState gs = b.getGameState();

        AffineTransform at = AffineTransform.getTranslateInstance(b.getCord().getPixel(gs).getX()-this.ballImage.getWidth()*this.ballWidthP*Cord.convertXLengthToPixel(Ball.hitBoxRadius, gs)/2,b.getCord().getPixel(gs).getY() - this.ballImage.getHeight()*this.ballHeightP*Cord.convertYLengthToPixel(Ball.hitBoxRadius, gs)/2);
        at.scale(this.ballWidthP*Cord.convertXLengthToPixel(Ball.hitBoxRadius, gs),this.ballHeightP*Cord.convertYLengthToPixel(Ball.hitBoxRadius, gs));

        g2d.drawImage(this.ballImage, at, null);
    }

    public void drawBall(Graphics2D g2d, int x, int y, int radius)
    {
        AffineTransform at = AffineTransform.getTranslateInstance(x-this.ballImage.getWidth()*this.ballWidthP*radius/2,y - this.ballImage.getHeight()*this.ballHeightP*radius/2);
        at.scale(this.ballWidthP*radius,this.ballHeightP*radius);

        g2d.drawImage(this.ballImage, at, null);
    }

}
