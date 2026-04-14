import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SpaceInvadersMain extends JPanel implements Runnable {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SpaceInvadersMain::new);
    }

    private SpaceShip player;

    private final List<Alien> aliens = new CopyOnWriteArrayList<>();
    private final List<Bullet> bullets = new CopyOnWriteArrayList<>();
    private final List<AlienBullet> alienBullets = new CopyOnWriteArrayList<>();

    private boolean rightPressed, leftPressed, shootPressed;

    private long lastShotTime;
    private static final long SHOT_COOLDOWN = 200;

    private long lastUpdateTime;

    private Image background;
    private Image heartImage;

    private int score = 0;

    public SpaceInvadersMain() {
        JFrame frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1900, 1000);
        frame.setResizable(false);
        frame.add(this);

        setFocusable(true);
        setBackground(Color.BLACK);

        initInput();
        loadResources();

        player = new SpaceShip(950, 840);

        frame.setVisible(true);
        requestFocus();

        new Thread(this).start();
    }

    private void initInput() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleInput(e, true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleInput(e, false);
            }
        });
    }

    private void handleInput(KeyEvent e, boolean pressed) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT -> rightPressed = pressed;
            case KeyEvent.VK_LEFT -> leftPressed = pressed;
            case KeyEvent.VK_UP -> shootPressed = pressed;
        }
    }

    private void updatePlayerDirection() {
        if (rightPressed && !leftPressed)
            player.setDirection(SpaceObj.Direction.RIGHT);
        else if (leftPressed && !rightPressed)
            player.setDirection(SpaceObj.Direction.LEFT);
        else
            player.setDirection(SpaceObj.Direction.NONE);
    }

    private void loadResources() {
        background = loadImage("/pictures/background.jpg");
        heartImage = loadImage("/pictures/heart.png");
    }

    private Image loadImage(String path) {
        var url = getClass().getResource(path);
        return url != null ? new ImageIcon(url).getImage() : null;
    }

    @Override
    public void run() {
        lastUpdateTime = System.nanoTime();

        while (true) {
            long now = System.nanoTime();
            float delta = (now - lastUpdateTime) / 1_000_000_000f;
            lastUpdateTime = now;

            update(delta);
            repaint();

            try {
                Thread.sleep(16);
            } catch (InterruptedException ignored) {}
        }
    }

    private void update(float delta) {
        updatePlayerDirection();
        player.move(delta);

        handleShooting();
        updateBullets(delta);
    }

    private void handleShooting() {
        if (shootPressed &&
                System.currentTimeMillis() - lastShotTime > SHOT_COOLDOWN &&
                player.getAmmo() > 0) {

            Rectangle p = player.getBounds();
            bullets.add(new Bullet(p.x + p.width / 2, p.y));

            lastShotTime = System.currentTimeMillis();
            player.addAmmo(-1);
        }
    }

    private void updateBullets(float delta) {
        for (Bullet b : bullets) {
            b.move(delta);

            for (Alien a : aliens) {
                if (b.getBounds().intersects(a.getBounds())) {
                    aliens.remove(a);
                    bullets.remove(b);
                    score += 100;
                    player.addAmmo(2);
                    return;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(background, 0, 0, this);

        player.draw(g, this);

        aliens.forEach(a -> a.draw(g, this));
        bullets.forEach(b -> b.draw(g, this));

        drawUI(g);
    }

    private void drawUI(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        g.drawString("Score: " + score, 20, 30);
        g.drawString("Ammo: " + player.getAmmo(), 20, 60);

        for (int i = 0; i < player.getLives(); i++) {
            g.drawImage(heartImage, 20 + i * 35, 100, 30, 30, this);
        }
    }
}