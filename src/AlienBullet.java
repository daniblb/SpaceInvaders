import java.awt.*;

public class AlienBullet extends SpaceObj {
    private static final Image bulletImage = Toolkit.getDefaultToolkit()
            .getImage(AlienBullet.class.getResource("pictures/bulletAlien.png"));

    public AlienBullet(int spawnX, int spawnY) {
        super(spawnX, spawnY);
        velocityY = 6.0f;
        width = 8;
        height = 15;
        image = bulletImage;
    }

    @Override
    public void move() {
        y += velocityY;
    }
}