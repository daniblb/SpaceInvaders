import java.awt.*;
import java.net.URL;

public class AlienBullet extends SpaceObj {

    public AlienBullet(int spawnX, int spawnY) {
        super(spawnX, spawnY, 8, 15);
        maxSpeed = 6.0f;
        velocityY = maxSpeed;
        direction = DOWN;

        URL imageUrl = getClass().getResource("pictures/bulletAlien.png");
        if (imageUrl != null) {
            image = Toolkit.getDefaultToolkit().getImage(imageUrl);
        }
    }

    @Override
    public void move(float deltaTime) {
        super.move(deltaTime);
    }
}