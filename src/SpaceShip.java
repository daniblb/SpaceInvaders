import javax.swing.*;
import java.awt.*;

public class SpaceShip extends SpaceObj {

    private static final int MIN_X = 15;
    private static final int MAX_X = 1810;

    private static final int DEFAULT_AMMO = 50;
    private static final int DEFAULT_LIVES = 5;

    private int ammo;
    private int lives;

    public SpaceShip(int x, int y) {
        super(x, y, 50, 50);

        this.ammo = DEFAULT_AMMO;
        this.lives = DEFAULT_LIVES;

        this.maxSpeed = 10f;
        this.acceleration = 0.8f;
        this.deceleration = 1.2f;

        this.image = loadImage("/pictures/player.png");
    }

    private Image loadImage(String path) {
        var url = getClass().getResource(path);
        if (url == null) {
            System.err.println("Image not found: " + path);
            return null;
        }
        return new ImageIcon(url).getImage();
    }

    @Override
    public void move(float deltaTime) {
        super.move(deltaTime);
        clampPosition();
    }

    private void clampPosition() {
        bounds.x = Math.max(MIN_X, Math.min(bounds.x, MAX_X));
    }

    public int getAmmo() {
        return ammo;
    }

    public void addAmmo(int amount) {
        ammo = Math.max(0, ammo + amount);
    }

    public int getLives() {
        return lives;
    }

    public void damage(int amount) {
        lives = Math.max(0, lives - amount);
    }
}