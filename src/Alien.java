import java.awt.*;
import java.util.Random;

public class Alien extends SpaceObj {
    private boolean canShoot;
    private long lastShotTime = 0;
    private static final long SHOT_INTERVAL = 2000; // 2 Sekunden
    private static final Random random = new Random();

    public Alien(int spawnX, int spawnY) {
        super(spawnX, spawnY, 50, 50);  // Standardgröße für Aliens
        maxSpeed = 4.0f;
        velocityX = maxSpeed;
        direction = RIGHT;

        canShoot = random.nextInt(100) < 10;

        var imageUrl = getClass().getResource("pictures/alien.png");
        if (imageUrl != null) {
            image = Toolkit.getDefaultToolkit().getImage(imageUrl);
        }
    }

    @Override
    public void move(float deltaTime) {
        super.move(deltaTime);
        
        if (bounds.x > 1800) {
            direction = LEFT;
            bounds.y += 100;
            velocityX = -maxSpeed;
        }
        if (bounds.x < 10) {
            direction = RIGHT;
            bounds.y += 100;
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