import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

public class SpaceInvadersMain extends JPanel implements Runnable {
    public static void main(String[] args) { new SpaceInvadersMain(); }

    private SpaceShip player;
    private ArrayList<Alien> aliens = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<AlienBullet> alienBullets = new ArrayList<>();
    private Timer alienTimer;
    private Image background;
    private Image heartImage;

    private boolean rightPressed = false;
    private boolean leftPressed = false;
    private boolean upPressed = false;

    private long lastShotTime = 0;
    private final long SHOT_COOLDOWN = 200;
    private long gameStartTime = 0;
    private int difficultyLevel = 1;
    private long alienSpawnInterval = 1500;

    private boolean flashPlayer = false;
    private long lastHitTime = 0;
    private final long FLASH_DURATION = 1000;
    private float heartbeatScale = 1.0f;

    private int score = 0;

    private final Color scoreboardBgColor = new Color(0, 0, 0, 150);
    private final Color scoreboardTextColor = new Color(0, 255, 255);
    private final Font scoreboardFont = new Font("Arial", Font.BOLD, 20);
    private final Font scoreboardTitleFont = new Font("Arial", Font.BOLD, 24);

    public SpaceInvadersMain() {
        JFrame main = new JFrame("Space Invaders");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setSize(1900, 1000);
        main.setLocationRelativeTo(null);
        main.setResizable(false);

        setBackground(Color.BLACK);
        main.add(this);
        main.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { handleKeyPress(e, true); }
            @Override
            public void keyReleased(KeyEvent e) { handleKeyPress(e, false); }
        });
        main.setVisible(true);

        player = new SpaceShip(950, 840);
        background = Toolkit.getDefaultToolkit().getImage(getClass().getResource("pictures/background.jpg"));
        heartImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("pictures/heart.png"));

        gameStartTime = System.currentTimeMillis();

        new Thread(this).start();

        alienTimer = new Timer((int) alienSpawnInterval, e -> spawnAlien());
        alienTimer.start();
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
        Alien newAlien = new Alien(10, 10);
        newAlien.maxSpeed = 4.0f + (difficultyLevel - 1) * 1.5f;
        newAlien.velocityX = newAlien.direction == SpaceObj.right ? newAlien.maxSpeed : -newAlien.maxSpeed;
        aliens.add(newAlien);
        alienTimer.setDelay((int) alienSpawnInterval);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, this);

        // Player blinken
        if (flashPlayer && System.currentTimeMillis() - lastHitTime < FLASH_DURATION) {
            if ((System.currentTimeMillis() / 100) % 2 == 0) player.draw(g, this);
        } else {
            player.draw(g, this);
            flashPlayer = false;
        }

        aliens.forEach(a -> a.draw(g, this));
        bullets.forEach(b -> b.draw(g, this));
        alienBullets.forEach(ab -> ab.draw(g, this));

        drawScoreboard((Graphics2D) g);
    }

    private void drawScoreboard(Graphics2D g) {
        int panelWidth = 350, panelHeight = 400;
        int panelX = getWidth() - panelWidth - 20;
        int panelY = getHeight() - panelHeight - 20;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(scoreboardBgColor);
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        g.setColor(new Color(0, 200, 255));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        g.setFont(scoreboardTitleFont);
        g.setColor(new Color(255, 255, 100));
        g.drawString("SPACE INVADERS", panelX + 50, panelY + 35);

        g.setFont(scoreboardFont);
        g.setColor(scoreboardTextColor);
        g.drawString("Punktestand: " + score, panelX + 30, panelY + 70);
        int seconds = (int) ((System.currentTimeMillis() - gameStartTime) / 1000) % 60;
        int minutes = (int) ((System.currentTimeMillis() - gameStartTime) / 1000 / 60);
        g.drawString(String.format("Spielzeit: %02d:%02d", minutes, seconds), panelX + 30, panelY + 100);
        g.drawString("Munition: " + player.ammo, panelX + 30, panelY + 130);
        g.drawString("Level: " + difficultyLevel, panelX + 30, panelY + 160);
    }

    private void updateDifficulty() {
        int newLevel = 1 + score / 1000;
        if (newLevel > difficultyLevel) {
            difficultyLevel = newLevel;
            alienSpawnInterval = Math.max(400, 1500 - (difficultyLevel - 1) * 250);
            aliens.forEach(a -> {
                a.maxSpeed = 4.0f + (difficultyLevel - 1) * 1.5f;
                a.velocityX = a.direction == SpaceObj.right ? a.maxSpeed : -a.maxSpeed;
            });
        }
    }

    @Override
    public void run() {
        while (true) {
            heartbeatScale = (float) (Math.sin(System.currentTimeMillis() / 250.0) * 0.08 + 1.0);

            repaint();
            player.move();

            if (upPressed && System.currentTimeMillis() - lastShotTime > SHOT_COOLDOWN && player.ammo > 0) {
                bullets.add(new Bullet(player.x + player.width / 2 - 5, player.y - 20));
                lastShotTime = System.currentTimeMillis();
                player.ammo--;
            }

            aliens.forEach(a -> {
                a.move();
                if (a.canShoot()) alienBullets.add(new AlienBullet(a.x + a.width / 2 - 4, a.y + a.height));
            });

            // Alien bullets collisions
            Iterator<AlienBullet> abIter = alienBullets.iterator();
            while (abIter.hasNext()) {
                AlienBullet b = abIter.next();
                b.move();
                if (b.intersects(player)) {
                    abIter.remove();
                    player.lives--;
                    flashPlayer = true;
                    lastHitTime = System.currentTimeMillis();
                    if (player.lives <= 0) System.exit(0);
                } else if (b.y > getHeight()) abIter.remove();
            }

            // Player bullets collisions
            Iterator<Bullet> bIter = bullets.iterator();
            while (bIter.hasNext()) {
                Bullet bullet = bIter.next();
                bullet.move();
                boolean removed = false;
                Iterator<Alien> aIter = aliens.iterator();
                while (aIter.hasNext()) {
                    Alien alien = aIter.next();
                    if (bullet.intersects(alien)) {
                        aIter.remove();
                        bIter.remove();
                        score += 100;
                        player.ammo += 2;
                        updateDifficulty();
                        removed = true;
                        break;
                    }
                }
                if (!removed && bullet.y < 0) bIter.remove();
            }

            try { Thread.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}