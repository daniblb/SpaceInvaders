import java.awt.*;

public class SpaceShip extends SpaceObj
{
    int ammo;
    int lives;

    public SpaceShip(int spawnX, int spawnY)
    {
        super(spawnX, spawnY);

        ammo = 50;
        lives = 5;
        speed = 10;
        maxSpeed = 10.0f;
        acceleration = 0.8f;
        deceleration = 1.2f; 
        width = 50;
        height = 50;

        image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("pictures/player.png"));
    }
    public void move()
    {
        super.move();
        if (x < 15)
            x = 15;
        if (x > 1810)
            x = 1810;
    }
}
