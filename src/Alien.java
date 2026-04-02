import java.awt.*;
import java.util.Random;

public class Alien extends SpaceObj {
    private boolean canShoot;
    private long lastShotTime = 0;
    private static final long SHOT_INTERVAL = 2000; // 2 Sekunden
    private static final Random random = new Random();

    public Alien(int spawnX, int spawnY) {
        super(spawnX, spawnY);
        speed = 4;
        maxSpeed = 4.0f;
        velocityX = maxSpeed;

        direction = SpaceObj.right;

        canShoot = random.nextInt(100) < 10;

        image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("pictures/alien.png"));
    }

    public void move() {
        super.move();
        if (x > 1800) {
            direction = SpaceObj.left;
            y += 100;
            velocityX = -maxSpeed;
        }
        if (x < 10) {
            direction = SpaceObj.right;
            y += 100;
            velocityX = maxSpeed;
        }
    }

    public boolean canShoot() {
        if (!canShoot) return false;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime > SHOT_INTERVAL) {
            lastShotTime = currentTime;
            return true;
        }
        return false;
    }

    public void markShot() {
        lastShotTime = System.currentTimeMillis();
    }
}