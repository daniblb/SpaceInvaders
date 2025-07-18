import java.awt.*;

public class Bullet extends SpaceObj
{
    public Bullet(int spawnX, int spawnY)
    {
        super(spawnX, spawnY);
        speed = 15;
        maxSpeed = 15.0f;
        velocityY = -maxSpeed;
        width = 10;
        height = 20;
        direction = SpaceObj.up;

        image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("pictures/bullet.png"));
    }

    public void move()
    {
        super.move();
    }
}
