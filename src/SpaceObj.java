import java.awt.*;
import java.awt.image.ImageObserver;

public abstract class SpaceObj {
    protected final Rectangle bounds;
    protected Image image;
    protected float velocityX;
    protected float velocityY;
    protected float maxSpeed;
    protected float acceleration = 0.5f;
    protected float deceleration = 0.3f;
    protected int direction;

    protected static final int UP = 1;
    protected static final int RIGHT = 2;
    protected static final int DOWN = 3;
    protected static final int LEFT = 4;

    public SpaceObj(int spawnX, int spawnY, int width, int height) {
        this.bounds = new Rectangle(spawnX, spawnY, width, height);
    }

    public void draw(Graphics g, Component c) {
        if (image != null) {
            g.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, c);
        }
    }

    public void move(float deltaTime) {
        switch (direction) {
            case UP -> {
                velocityY = approach(-maxSpeed, velocityY, acceleration * deltaTime);
                velocityX = approach(0, velocityX, deceleration * deltaTime);
            }
            case RIGHT -> {
                velocityX = approach(maxSpeed, velocityX, acceleration * deltaTime);
                velocityY = approach(0, velocityY, deceleration * deltaTime);
            }
            case DOWN -> {
                velocityY = approach(maxSpeed, velocityY, acceleration * deltaTime);
                velocityX = approach(0, velocityX, deceleration * deltaTime);
            }
            case LEFT -> {
                velocityX = approach(-maxSpeed, velocityX, acceleration * deltaTime);
                velocityY = approach(0, velocityY, deceleration * deltaTime);
            }
            default -> {
                velocityX = approach(0, velocityX, deceleration * deltaTime);
                velocityY = approach(0, velocityY, deceleration * deltaTime);
            }
        }

        bounds.x += Math.round(velocityX * deltaTime);
        bounds.y += Math.round(velocityY * deltaTime);
    }

    protected float approach(float target, float current, float delta) {
        if (current < target) return Math.min(current + delta, target);
        if (current > target) return Math.max(current - delta, target);
        return current;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}