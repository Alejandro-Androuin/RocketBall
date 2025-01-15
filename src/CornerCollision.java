public class CornerCollision {

    private Cord corner;

    private Line l1;
    private Line l2;
    private Line l3;
    private Line l4;

    public CornerCollision(Cord corner, Line l1, Line l2, Line l3, Line l4) //l1 and l2 are the lines that make up the corner. l1 intersects l3 and l2 intersects l4. l3 and l4 may be the same line
    {
        this.corner = corner;

        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
        this.l4 = l4;
    }

    public boolean collideSameLine()
    {
        return l3.equals(l4);
    }

    public Line getLine1()
    {
        return this.l1;
    }

    public Line getLine2()
    {
        return this.l2;
    }

    public Line getLine3()
    {
        return this.l3;
    }

    public Line getLine4()
    {
        return this.l4;
    }

    public float getMaxPercentLine3Intersection(Vector vel1, Vector vel2)
    {
        float p1 = Collision.percentOfCollision(this.l1, vel1, this.l3, vel2);
        float p2 = Collision.percentOfCollision(this.l2, vel1, this.l3, vel2);

        return Math.max(p1,p2);
    }

    public float getMaxPercentLine4Intersection(Vector vel1, Vector vel2)
    {
        float p1 = Collision.percentOfCollision(this.l1, vel1, this.l4, vel2);
        float p2 = Collision.percentOfCollision(this.l2, vel1, this.l4, vel2);

        return Math.max(p1,p2);
    }

    public float getPercentOfCollision(Vector vel1, Vector vel2)
    {
        float p1 = Collision.percentOfCollision(this.l1, vel1, this.l3, vel2);
        float p2 = Collision.percentOfCollision(this.l2, vel1, this.l4, vel2);

        return Math.max(p1,p2);
    }

    public Line getLineInCommon(CornerCollision c)
    {
        if(this.l1.equals(c.getLine1()))
        {
            return this.l1;
        }
        else if(this.l1.equals(c.getLine2()))
        {
            return this.l1;
        }
        else if(this.l2.equals(c.getLine1()))
        {
            return this.l2;
        }
        else if(this.l2.equals(c.getLine2()))
        {
            return this.l2;
        }

        return null;
    }

}
