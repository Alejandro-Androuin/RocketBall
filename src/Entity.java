import java.util.List;

public abstract class Entity {

    public abstract List<WallCollision> getWallCollisions();

    public abstract Collision calculateCollision(Entity e);

    public abstract Cord getCord();

    public abstract Vector getVelocityVector();

    public abstract float getMass();

    public abstract void setVelocity(Vector v);

    public abstract void move(float percent);

    public abstract void moveBack(float percent);

    public abstract short getHitboxRadius();

    public abstract boolean equals(Entity e);

    public abstract float getAngle(Entity e);

    public abstract void setGameState(GameState gs);

}
