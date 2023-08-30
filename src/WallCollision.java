public class WallCollision {

    private Entity e;
    private Vector newVector;
    private float percentage;

    private boolean isRed;
    private boolean isBlue;

    public WallCollision(Entity e, Vector newVector, float percentage, boolean isRed, boolean isBlue)
    {
        this.e = e;
        this.newVector = newVector;
        this.percentage = percentage;
        this.isRed = isRed;
        this.isBlue = isBlue;
    }

    public void collide()
    {
        this.e.moveBack(1-percentage);
        this.e.setVelocity(newVector);
        this.e.move(1-percentage);
    }

    public boolean isRed()
    {
        return this.isRed;
    }

    public boolean isBlue()
    {
        return this.isBlue;
    }

    public Entity getEntity()
    {
        return this.e;
    }

    public boolean contains(Entity e)
    {
        if(this.e.equals(e))
        {
            return true;
        }

        return false;
    }

    public float getPercent()
    {
        return this.percentage;
    }

}
