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
        if (rightPressed && !leftPressed) player.direction = RIGHT;
        else if (leftPressed && !rightPressed) player.direction = LEFT;
        else player.direction = 0;
    }

    private void spawnAlien() {
        Alien a = new Alien(10, 100);  // y=100 statt 10
        a.maxSpeed = 4.0f + (difficultyLevel - 1) * 1.5f;
        a.direction = RIGHT;  // Richtung statt velocityX

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
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Score, Ammo, Time
        int elapsed = (int) ((System.currentTimeMillis() - gameStartTime) / 1000);
        int minutes = elapsed / 60;
        int seconds = elapsed % 60;

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);
        g.drawString("Ammo: " + player.getAmmo(), 20, 60);
        g.drawString(String.format("Time: %02d:%02d", minutes, seconds), 20, 90);

        // Herz-Leben mit Puls-Effekt
        for (int i = 0; i < player.getLives(); i++) {
            int heartSize = (int) (30 * heartbeatScale);
            g.drawImage(heartImage, 20 + i * (heartSize + 5), 120, heartSize, heartSize, this);
        }
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
        lastUpdateTime = System.nanoTime();
        
        while (true) {
            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0f; // Sekunden
            lastUpdateTime = currentTime;

            // 1️⃣ Puls-Effekt
            heartbeatScale = (float) (Math.sin(System.currentTimeMillis() / 250.0) * 0.08 + 1.0);

            // 2️⃣ Player Bewegung + Richtung
            updatePlayerDirection();
            player.move(deltaTime);

            // 3️⃣ Schießen
            if (upPressed && System.currentTimeMillis() - lastShotTime > SHOT_COOLDOWN && player.getAmmo() > 0) {
                Rectangle pBounds = player.getBounds();
                bullets.add(new Bullet(pBounds.x + pBounds.width / 2 - 5, pBounds.y - 20));
                lastShotTime = System.currentTimeMillis();
                player.setAmmo(player.getAmmo() - 1);
            }

            // 4️⃣ Aliens
            synchronized (aliens) {
                for (Alien a : aliens) {
                    a.move(deltaTime);
                    if (a.canShoot()) {
                        Rectangle aBounds = a.getBounds();
                        alienBullets.add(new AlienBullet(aBounds.x + aBounds.width / 2, aBounds.y + aBounds.height));
                        a.markShot();
                    }
                }
            }

            // 5️⃣ AlienBullets
            Iterator<AlienBullet> abIter = alienBullets.iterator();
            while (abIter.hasNext()) {
                AlienBullet b = abIter.next();
                b.move(deltaTime);

                if (b.getBounds().intersects(player.getBounds())) {
                    abIter.remove();
                    player.setLives(player.getLives() - 1);
                    flashPlayer = true;
                    lastHitTime = System.currentTimeMillis();

                    if (player.getLives() <= 0) {
                        System.exit(0);
                    }
                } else if (b.getBounds().y > getHeight()) {
                    abIter.remove();
                }
            }

            // 6️⃣ PlayerBullets (identisch, aber mit getBounds())
            Iterator<Bullet> bIter = bullets.iterator();
            while (bIter.hasNext()) {
                Bullet bullet = bIter.next();
                bullet.move(deltaTime);

                boolean removed = false;
                synchronized (aliens) {
                    Iterator<Alien> aIter = aliens.iterator();
                    while (aIter.hasNext()) {
                        Alien alien = aIter.next();
                        if (bullet.getBounds().intersects(alien.getBounds())) {
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

                if (!removed && bullet.getBounds().y < 0) {
                    bIter.remove();
                }
            }

            repaint();  // Neu: explizit repaint aufrufen
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException ignored) {}
        }
    }
}