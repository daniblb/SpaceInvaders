import java.awt.*;

public abstract class SpaceObj extends Rectangle
{
    Image image;
    int speed;
    int direction;
    float velocityX = 0;
    float velocityY = 0;
    float maxSpeed;
    float acceleration = 0.5f;
    float deceleration = 0.3f;

    final static int up = 1;
    final static int right = 2;
    final static int down = 3;
    final static int left = 4;

    public SpaceObj(int spawnX, int spawnY)
    {
        x = spawnX;
        y = spawnY;
        // Standardwerte für width und height, können in Unterklassen überschrieben werden
        width = 50;
        height = 50;
    }
    public void draw(Graphics g, Component c)
    {
        g.drawImage(image, x, y, c);
    }
    public void move ()
    {
        switch (direction)
        {
            case up:
                velocityY = approach(-maxSpeed, velocityY, acceleration);
                velocityX = approach(0, velocityX, deceleration);
                break;
            case right:
                velocityX = approach(maxSpeed, velocityX, acceleration);
                velocityY = approach(0, velocityY, deceleration);
                break;
            case down:
                velocityY = approach(maxSpeed, velocityY, acceleration);
                velocityX = approach(0, velocityX, deceleration);
                break;
            case left:
                velocityX = approach(-maxSpeed, velocityX, acceleration);
                velocityY = approach(0, velocityY, deceleration);
                break;
            default:
                velocityX = approach(0, velocityX, deceleration);
                velocityY = approach(0, velocityY, deceleration);
                break;
        }

        x += Math.round(velocityX);
        y += Math.round(velocityY);
    }
    protected float approach(float target, float current, float delta) {
        if (current < target) {
            current += delta;
            if (current > target) return target;
        } else if (current > target) {
            current -= delta;
            if (current < target) return target;
        }
        return current;
    }
}
