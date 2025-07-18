import java.awt.*;

public class AlienBullet extends SpaceObj {

    public AlienBullet(int spawnX, int spawnY) {
        super(spawnX, spawnY);

        velocityY = 6.0f;
        width = 8;
        height = 15;

        image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("pictures/bulletAlien.png"));
    }

    @Override
    public void move() {
        y += velocityY;
    }
}
