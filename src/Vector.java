public class Vector {

    private float x;
    private float y;

    public Vector(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector(Vector v)
    {
        this.x = v.getX();
        this.y = v.getY();
    }

    public float getX()
    {
        return this.x;
    }

    public float getY()
    {
        return this.y;
    }

    public void setX(float x)
    {
        this.x=x;
    }

    public void setY(float y)
    {
        this.y=y;
    }

    public float getMagnitude()
    {
        return (float)(Math.sqrt(Math.pow(this.x,2)+Math.pow(this.y,2)));
    }

    public void incrementX(float x)
    {
        this.x+=x;
    }

    public void incrementY(float y)
    {
        this.y+=y;
    }

    public void multiplyX(float x)
    {
        this.x*=x;
    }

    public void multiplyY(float y)
    {
        this.y*=y;
    }

    public void multiply(float s)
    {
        this.x*=s;
        this.y*=s;
    }

    public void divide(float s)
    {
        this.x/=s;
        this.y/=s;
    }

    public void addVector(Vector v)
    {
        this.x+=v.getX();
        this.y+=v.getY();
    }

    public void subtractVector(Vector v)
    {
        this.x-=v.getX();
        this.y-=v.getY();
    }

    public void multiplyVector(Vector v)
    {
        this.x*=v.getX();
        this.y*=v.getY();
    }

    public void negate()
    {
        this.x*=-1;
        this.y*=-1;
    }

    public float getAngle()
    {
        if(this.y == 0)
        {
            if(this.x == 0)
            {
                return 0;
            }
            else if(this.x>0)
            {
                return 90;
            }
            else
            {
                return -90;
            }
        }
        else
        {
            float f = (float)Math.toDegrees(Math.atan(this.x/this.y));

            if(this.y>0)
            {
                return f;
            }
            else
            {
                if(f > 0)
                {
                    f-= 180;
                }
                else
                {
                    f+= 180;
                }

                return f;
            }
        }
    }

    public static Vector getUnitVector(short angle)
    {
        while(!(angle > -180 & angle <= 180))
        {
            if(!(angle > -180))
            {
                angle+=360;
            }
            else
            {
                angle-=360;
            }
        }

        if(angle == 90)
        {
            return new Vector(1, 0);
        }
        else if(angle == -90)
        {
            return new Vector(-1, 0);
        }

        double x = Math.sin(Math.toRadians(angle));
        double y = Math.cos(Math.toRadians(angle));

        return new Vector((float) x, (float) y);
    }

    public float dotProduct(Vector v)
    {
        return this.x*v.getX()+this.y*v.getY();
    }

    public void convertToUnitVector()
    {
        float mag = getMagnitude();

        if(mag!=0)
        {
            this.x/=mag;
            this.y/=mag;
        }
    }

    public Vector getCNormalVector()
    {
        float x = this.y;
        float y = -this.x;

        return new Vector(x,y);
    }

    public Vector getCCNormalVector()
    {
        float x = -this.y;
        float y = this.x;

        return new Vector(x,y);
    }

    public void print()
    {
        System.out.println("x is " + this.x + " y is " + this.y);
    }



}
