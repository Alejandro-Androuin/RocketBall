public class Line {

    private Cord c1;
    private Cord c2;

    public Line(Cord c1, Cord c2)
    {
        this.c1 = c1;
        this.c2 = c2;
    }

    public Cord getCord1()
    {
        return this.c1;
    }

    public Cord getCord2()
    {
        return this.c2;
    }

    public Cord intersect(Line l)
    {
        if(invalidSlope()&l.invalidSlope())
        {
            if(this.c1.getX()==l.getCord1().getX())
            {
                float min = Math.min(this.c1.getY(), this.c2.getY());
                float max = Math.max(this.c1.getY(), this.c2.getY());

                float lmin = Math.min(l.getCord1().getY(), l.getCord2().getY());
                float lmax = Math.max(l.getCord1().getY(), l.getCord2().getY());

                if(lmin<max & lmax>min)
                {
                    return new Cord(this.c1.getX(), ((Math.min(max, lmax)-Math.max(min, lmin))/2)+Math.max(min, lmin));
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        else if(invalidSlope())
        {
            if(l.inDomain(this.c1.getX()))
            {
                float min = Math.min(this.c1.getY(), this.c2.getY());
                float max = Math.max(this.c1.getY(), this.c2.getY());

                float y = l.getSlope()*this.c1.getX()+l.getConstant();

                if(y<max&y>min)
                {
                    return new Cord(this.c1.getX(), y);
                }

                return null;
            }
            else
            {
                return null;
            }
        }
        else if(l.invalidSlope())
        {
            if(inDomain(l.getCord1().getX()))
            {
                float lmin = Math.min(l.getCord1().getY(), l.getCord2().getY());
                float lmax = Math.max(l.getCord1().getY(), l.getCord2().getY());

                float y = getSlope()*this.c1.getX()+getConstant();

                if(y<lmax&y>lmin)
                {
                    return new Cord(l.getCord1().getX(), y);
                }

                return null;
            }
            else
            {
                return null;
            }
        }
        else if(getSlope()!=l.getSlope())
        {
            if(domainIntersect(l))
            {
                float xIntersect = (l.getConstant()-getConstant())/(getSlope()-l.getSlope());
                if(inDomain(xIntersect) & l.inDomain(xIntersect))
                {
                    return new Cord(xIntersect, getYWithX(xIntersect));
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        else
        {
            //Lines have the same slope. If constants are equal then they intersect if and only if their domains intersect, otherwise they do not
            if(getConstant()==l.getConstant())
            {
                if(domainIntersect(l))
                {
                    float min = Math.min(this.c1.getX(), this.c2.getX());
                    float max = Math.max(this.c1.getX(), this.c2.getX());

                    float lmin = Math.min(l.getCord1().getX(), l.getCord2().getX());
                    float lmax = Math.max(l.getCord1().getX(), l.getCord2().getX());

                    float xIntersect = ((Math.min(max, lmax)-Math.max(min, lmin))/2)+Math.max(min, lmin);

                    return new Cord(xIntersect, getYWithX(xIntersect));
                }
                return null;
            }
            else
            {
                return null;
            }
        }
    }

    public float getSlope()
    {
        return (c2.getY()-c1.getY())/(c2.getX()-c1.getX());
    }

    public boolean invalidSlope()
    {
        return this.c1.getX()==this.c2.getX();
    }

    public float getConstant()
    {
        return c1.getY()-getSlope()*c1.getX();
    }

    public float getYWithX(float x)
    {
        return x*getSlope()+getConstant();
    }

    public boolean domainIntersect(Line l)
    {
        float min = Math.min(this.c1.getX(), this.c2.getX());
        float max = Math.max(this.c1.getX(), this.c2.getX());

        float lmin = Math.min(l.getCord1().getX(), l.getCord2().getX());
        float lmax = Math.max(l.getCord1().getX(), l.getCord2().getX());

        if(lmin<max & lmax>min)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean inDomain(float x)
    {
        float min = Math.min(this.c1.getX(), this.c2.getX());
        float max = Math.max(this.c1.getX(), this.c2.getX());

        if(x>min & x<max)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public float xBestDistanceFromOrigin(float xShift, float yShift)
    {
        this.c1.addX(xShift);
        this.c2.addX(xShift);

        this.c1.addY(yShift);
        this.c2.addY(yShift);

        float slope = getSlope();
        float b = this.c1.getY()-slope*this.c1.getX();

        float answer = (float)((-slope*b)/(1+Math.pow(slope, 2)))-xShift;

        this.c1.addX(-xShift);
        this.c2.addX(-xShift);

        this.c1.addY(-yShift);
        this.c2.addY(-yShift);

        return answer;
    }

    public boolean equals(Line l)
    {
        if(this.c1.equals(l.getCord1()))
        {
            if(this.c2.equals(l.getCord2()))
            {
                return true;
            }
        }

        return false;
    }

}
