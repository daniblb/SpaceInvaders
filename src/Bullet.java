import java.awt.*;
import java.net.URL;

public class Bullet extends SpaceObj {

    public Bullet(int spawnX, int spawnY) {
        super(spawnX, spawnY, 10, 20);
        maxSpeed = 15.0f;
        direction = UP;
        velocityY = -maxSpeed;

        URL imageUrl = getClass().getResource("pictures/bullet.png");
        if (imageUrl != null) {
            image = Toolkit.getDefaultToolkit().getImage(imageUrl);
        }
    }

    @Override
    public void move(float deltaTime) {
        super.move(deltaTime);
    }
}