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
        super(spawnX, spawnY);

        this.ammo = DEFAULT_AMMO;
        this.lives = DEFAULT_LIVES;

        this.speed = 10;
        this.maxSpeed = 10.0f;
        this.acceleration = 0.8f;
        this.deceleration = 1.2f;

        this.width = 50;
        this.height = 50;

        this.image = loadImage("/pictures/player.png");
    }

    private Image loadImage(String path) {
        return new ImageIcon(getClass().getResource(path)).getImage();
    }

    @Override
    public void move() {
        super.move();
        clampPosition();
    }

    private void clampPosition() {
        if (x < MIN_X) x = MIN_X;
        if (x > MAX_X) x = MAX_X;
    }

    // Getter / Setter
    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = Math.max(0, ammo); // keine negativen Werte
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = Math.max(0, lives);
    }
}