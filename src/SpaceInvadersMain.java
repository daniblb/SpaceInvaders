import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

public class SpaceInvadersMain extends JPanel implements Runnable {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SpaceInvadersMain::new);
    }

    private SpaceShip player;

    private final java.util.List<Alien> aliens = new ArrayList<>();
    private final java.util.List<Bullet> bullets = new ArrayList<>();
    private final java.util.List<AlienBullet> alienBullets = new ArrayList<>();

    private Timer alienTimer;

    private Image background;
    private Image heartImage;

    private boolean rightPressed = false;
    private boolean leftPressed = false;
    private boolean upPressed = false;

    private long lastShotTime = 0;
    private static final long SHOT_COOLDOWN = 200;

    private long gameStartTime = 0;
    private int difficultyLevel = 1;
    private long alienSpawnInterval = 1500;

    private boolean flashPlayer = false;
    private long lastHitTime = 0;
    private static final long FLASH_DURATION = 1000;

    private float heartbeatScale = 1.0f;

    private int score = 0;

    public SpaceInvadersMain() {
        JFrame frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1900, 1000);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        setFocusable(true);
        setBackground(Color.BLACK);
        frame.add(this);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { handleKeyPress(e, true); }
            @Override
            public void keyReleased(KeyEvent e) { handleKeyPress(e, false); }
        });

        frame.setVisible(true);
        requestFocusInWindow();

        player = new SpaceShip(950, 840);

        background = loadImage("/pictures/background.jpg");
        heartImage = loadImage("/pictures/heart.png");

        gameStartTime = System.currentTimeMillis();

        new Thread(this).start();

        alienTimer = new Timer((int) alienSpawnInterval, e -> spawnAlien());
        alienTimer.start();
    }

    private Image loadImage(String path) {
        return new ImageIcon(getClass().getResource(path)).getImage();
    }

    private void handleKeyPress(KeyEvent e, boolean pressed) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT -> rightPressed = pressed;
            case KeyEvent.VK_LEFT -> leftPressed = pressed;
            case KeyEvent.VK_UP -> upPressed = pressed;
        }
        updatePlayerDirection();
    }

    private void updatePlayerDirection() {
        if (rightPressed && !leftPressed) player.direction = SpaceObj.right;
        else if (leftPressed && !rightPressed) player.direction = SpaceObj.left;
        else player.direction = 0;
    }

    private void spawnAlien() {
        Alien a = new Alien(10, 10);
        a.maxSpeed = 4.0f + (difficultyLevel - 1) * 1.5f;
        a.velocityX = a.direction == SpaceObj.right ? a.maxSpeed : -a.maxSpeed;

        synchronized (aliens) {
            aliens.add(a);
        }

        alienTimer.setDelay((int) alienSpawnInterval);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(background, 0, 0, this);

        if (flashPlayer && System.currentTimeMillis() - lastHitTime < FLASH_DURATION) {
            if ((System.currentTimeMillis() / 100) % 2 == 0) {
                player.draw(g, this);
            }
        } else {
            player.draw(g, this);
            flashPlayer = false;
        }

        synchronized (aliens) {
            aliens.forEach(a -> a.draw(g, this));
        }

        bullets.forEach(b -> b.draw(g, this));
        alienBullets.forEach(b -> b.draw(g, this));

        drawScoreboard((Graphics2D) g);
    }

    private void drawScoreboard(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 20, 20);
        g.drawString("Ammo: " + player.getAmmo(), 20, 40);
        g.drawString("Lives: " + player.getLives(), 20, 60);
    }

    private void updateDifficulty() {
        int newLevel = 1 + score / 1000;

        if (newLevel > difficultyLevel) {
            difficultyLevel = newLevel;
            alienSpawnInterval = Math.max(400, 1500 - (difficultyLevel - 1) * 250);
        }
    }

    @Override
    public void run() {
        while (true) {

            repaint();
            player.move();

            // Schießen
            if (upPressed && System.currentTimeMillis() - lastShotTime > SHOT_COOLDOWN && player.getAmmo() > 0) {
                bullets.add(new Bullet(player.x + player.width / 2 - 5, player.y - 20));
                lastShotTime = System.currentTimeMillis();
                player.setAmmo(player.getAmmo() - 1);
            }

            synchronized (aliens) {
                for (Alien a : aliens) {
                    a.move();
                    if (a.canShoot()) {
                        alienBullets.add(new AlienBullet(a.x + a.width / 2, a.y));
                    }
                }
            }

            // Alien Bullets
            Iterator<AlienBullet> abIter = alienBullets.iterator();
            while (abIter.hasNext()) {
                AlienBullet b = abIter.next();
                b.move();

                if (b.intersects(player)) {
                    abIter.remove();
                    player.setLives(player.getLives() - 1);

                    flashPlayer = true;
                    lastHitTime = System.currentTimeMillis();

                    if (player.getLives() <= 0) {
                        System.exit(0);
                    }
                } else if (b.y > getHeight()) {
                    abIter.remove();
                }
            }

            // Player Bullets
            Iterator<Bullet> bIter = bullets.iterator();
            while (bIter.hasNext()) {
                Bullet bullet = bIter.next();
                bullet.move();

                boolean removed = false;

                synchronized (aliens) {
                    Iterator<Alien> aIter = aliens.iterator();
                    while (aIter.hasNext()) {
                        Alien alien = aIter.next();

                        if (bullet.intersects(alien)) {
                            aIter.remove();
                            bIter.remove();

                            score += 100;
                            player.setAmmo(player.getAmmo() + 2);

                            updateDifficulty();
                            removed = true;
                            break;
                        }
                    }
                }

                if (!removed && bullet.y < 0) {
                    bIter.remove();
                }
            }

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException ignored) {}
        }
    }
}