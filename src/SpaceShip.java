import java.awt.Image;
import javax.swing.ImageIcon;

public class SpaceShip extends SpaceObj {

    private static final int MIN_X = 15;
    private static final int MAX_X = 1810;
    private static final int DEFAULT_AMMO = 50;
    private static final int DEFAULT_LIVES = 5;

    private int ammo;
    private int lives;

    public SpaceShip(int spawnX, int spawnY) {
        super(spawnX, spawnY, 50, 50);

        this.ammo = DEFAULT_AMMO;
        this.lives = DEFAULT_LIVES;

        this.maxSpeed = 10.0f;
        this.acceleration = 0.8f;
        this.deceleration = 1.2f;

        this.image = loadImage("/pictures/player.png");
    }

    private Image loadImage(String path) {
        var url = getClass().getResource(path);
        return url != null ? new ImageIcon(url).getImage() : null;
    }

    @Override
    public void move(float deltaTime) {
        super.move(deltaTime);
        clampPosition();
    }

    private void clampPosition() {
        if (bounds.x < MIN_X) bounds.x = MIN_X;
        if (bounds.x > MAX_X) bounds.x = MAX_X;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = Math.max(0, ammo);
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = Math.max(0, lives);
    }
}