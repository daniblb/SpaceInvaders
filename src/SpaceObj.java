import java.awt.*;

public abstract class SpaceObj {
    protected final Rectangle bounds = new Rectangle();
    protected Image image;
    protected int speed;
    protected int direction;
    protected float velocityX = 0;
    protected float velocityY = 0;
    protected float maxSpeed;
    protected float acceleration = 0.5f;
    protected float deceleration = 0.3f;

    protected final static int UP = 1;
    protected final static int RIGHT = 2;
    protected final static int DOWN = 3;
    protected final static int LEFT = 4;

    public SpaceObj(int spawnX, int spawnY, int width, int height) {
        bounds.setBounds(spawnX, spawnY, width, height);
    }

    public void draw(Graphics g, Component c) {
        if (image != null) {
            g.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, c);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    public void move(float deltaTime) {
        switch (direction) {
            case UP:
                velocityY = approach(-maxSpeed, velocityY, acceleration * deltaTime);
                velocityX = approach(0, velocityX, deceleration * deltaTime);
                break;
            case RIGHT:
                velocityX = approach(maxSpeed, velocityX, acceleration * deltaTime);
                velocityY = approach(0, velocityY, deceleration * deltaTime);
                break;
            case DOWN:
                velocityY = approach(maxSpeed, velocityY, acceleration * deltaTime);
                velocityX = approach(0, velocityX, deceleration * deltaTime);
                break;
            case LEFT:
                velocityX = approach(-maxSpeed, velocityX, acceleration * deltaTime);
                velocityY = approach(0, velocityY, deceleration * deltaTime);
                break;
            default:
                velocityX = approach(0, velocityX, deceleration * deltaTime);
                velocityY = approach(0, velocityY, deceleration * deltaTime);
                break;
        }

        bounds.x += Math.round(velocityX * deltaTime);
        bounds.y += Math.round(velocityY * deltaTime);
    }

    protected float approach(float target, float current, float delta) {
        if (current < target) {
            current += delta;
            return Math.min(current, target);
        } else if (current > target) {
            current -= delta;
            return Math.max(current, target);
        }
        return current;
    }

    // Getter methods for bounds access
    public Rectangle getBounds() { return bounds; }
    public int getX() { return bounds.x; }
    public int getY() { return bounds.y; }
    public int getWidth() { return bounds.width; }
    public int getHeight() { return bounds.height; }

    // Setter for image and other properties
    public void setImage(Image img) { this.image = img; }
    public void setMaxSpeed(float speed) { this.maxSpeed = speed; }
    public void setDirection(int dir) { this.direction = dir; }
}