import java.util.List;

public class Collision {

    private Entity e1;
    private Entity e2;

    float percentage;

    public Collision(Entity e1, Entity e2, float percentage)
    {
        this.e1 = e1;
        this.e2 = e2;
        this.percentage = percentage;
    }

    public Entity getE1()
    {
        return this.e1;
    }

    public Entity getE2()
    {
        return this.e2;
    }

    public boolean contains(Entity e)
    {
        if(this.e1.equals(e) || this.e2.equals(e))
        {
            return true;
        }

        return false;
    }

    public float getPercent()
    {
        return this.percentage;
    }

    public void collide()
    {
        e1.moveBack(1-percentage);
        e2.moveBack(1-percentage);

        Vector norm1 = new Vector(this.e2.getCord().getX()-this.e1.getCord().getX(), this.e2.getCord().getY()-this.e1.getCord().getY());
        Vector norm2 = new Vector(this.e1.getCord().getX()-this.e2.getCord().getX(), this.e1.getCord().getY()-this.e2.getCord().getY());

        norm1.convertToUnitVector();
        norm2.convertToUnitVector();

        float scalarVel1 = e1.getVelocityVector().dotProduct(norm1);
        float scalarVel2 = e2.getVelocityVector().dotProduct(norm2);

        float scalarFinalVel1 = ((-2*e2.getMass()*scalarVel2)+((scalarVel1)*(e1.getMass()-e2.getMass())))/(e1.getMass()+e2.getMass());
        float scalarFinalVel2 = ((-2*e1.getMass()*scalarVel1)+((scalarVel2)*(e2.getMass()-e1.getMass())))/(e1.getMass()+e2.getMass());

        Vector tangetVel1 = new Vector(e1.getVelocityVector());
        Vector tangetVel2 = new Vector(e2.getVelocityVector());

        Vector normVel1 = new Vector(norm1);
        Vector normVel2 = new Vector(norm2);

        normVel1.multiply(scalarVel1);
        normVel2.multiply(scalarVel2);

        tangetVel1.subtractVector(normVel1);
        tangetVel2.subtractVector(normVel2);

        norm1.multiply(scalarFinalVel1);
        norm2.multiply(scalarFinalVel2);

        //now norm contains the final normal velocity. Adding the tangent velocity will get us the new velocity vector for each entity
        norm1.addVector(tangetVel1);
        norm2.addVector(tangetVel2);

        e1.setVelocity(norm1);
        e2.setVelocity(norm2);

        e1.move(1-percentage);
        e2.move(1-percentage);
    }

    //Returns the percentage of which the 2 lines intersect
    public static float percentOfCollision(Line l1, Vector v1, Line l2, Vector v2)
    {
        float percent = 1;

        Vector seperationVector = new Vector(v1);
        seperationVector.subtractVector(v2);
        seperationVector.negate();

        Cord c1Test = new Cord(l1.getCord1());
        Cord c2Test = new Cord(l1.getCord2());

        c1Test.addVector(seperationVector);
        c2Test.addVector(seperationVector);

        Line test1 = new Line(l1.getCord1(), c1Test);
        Line test2 = new Line(l1.getCord2(), c2Test);

        Cord test1Intersection = test1.intersect(l2);
        if(test1Intersection!=null)
        {
            float test = 1-(test1Intersection.getX()-l1.getCord1().getX()/(c1Test.getX()-l1.getCord1().getX()));
            if(test>0&test<1)
            {
                if(test<percent)
                {
                    percent=test;
                }
            }
        }

        Cord test2Intersection = test2.intersect(l2);
        if(test2Intersection!=null)
        {
            float test = 1-(test2Intersection.getX()-l1.getCord2().getX()/(c2Test.getX()-l1.getCord2().getX()));
            if(test>0&test<1)
            {
                if(test<percent)
                {
                    percent=test;
                }
            }
        }

        seperationVector.negate();

        Cord c3Test = new Cord(l2.getCord1());
        Cord c4Test = new Cord(l2.getCord2());

        c3Test.addVector(seperationVector);
        c4Test.addVector(seperationVector);

        Line test3 = new Line(l2.getCord1(), c1Test);
        Line test4 = new Line(l2.getCord2(), c2Test);

        Cord test3Intersection = test3.intersect(l1);
        if(test3Intersection!=null)
        {
            float test = 1-(test3Intersection.getX()-l2.getCord1().getX()/(c3Test.getX()-l2.getCord1().getX()));
            if(test>0&test<1)
            {
                if(test<percent)
                {
                    percent=test;
                }
            }
        }

        Cord test4Intersection = test4.intersect(l1);
        if(test4Intersection!=null)
        {
            float test = 1-(test4Intersection.getX()-l2.getCord2().getX()/(c4Test.getX()-l2.getCord2().getX()));
            if(test>0&test<1)
            {
                if(test<percent)
                {
                    percent=test;
                }
            }
        }

        return percent;
    }

}
