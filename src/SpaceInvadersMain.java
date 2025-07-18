import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import java.util.Vector;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;


public class SpaceInvadersMain extends JPanel implements ActionListener, KeyListener, Runnable
{
    public static void main(String[] args) { new SpaceInvadersMain(); }

    SpaceShip player;
    Vector<Alien> aliens = new Vector<>();
    Vector<Bullet> bullets = new Vector<>();
    Vector<AlienBullet> alienBullets = new Vector<>();
    Timer timer;
    Image background;

    boolean rightPressed = false;
    boolean leftPressed = false;
    boolean upPressed = false;
    private long lastShotTime = 0;
    private final long SHOT_COOLDOWN = 200;

    private int score = 0;
    private long gameStartTime = 0;
    private int difficultyLevel = 1;
    private long alienSpawnInterval = 1500;
    private boolean flashPlayer = false;
    private long lastHitTime = 0;
    private static final long FLASH_DURATION = 1000; // 1 Sekunde blinken
    private float heartbeatScale = 1.0f;
    private Color scoreboardBgColor = new Color(0, 0, 0, 150);
    private Color scoreboardTextColor = new Color(0, 255, 255);
    private Font scoreboardFont = new Font("Arial", Font.BOLD, 20);
    private Font scoreboardTitleFont = new Font("Arial", Font.BOLD, 24);


    public SpaceInvadersMain()
    {
        JFrame main = new JFrame("Space Invaders");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setSize(1900, 1000);
        main.setLocationRelativeTo(null);
        main.setResizable(false);
        main.addKeyListener(this);
        main.requestFocusInWindow();

        setBackground(Color.BLACK);
        main.add(this);

        player = new SpaceShip(950, 840);

        background = Toolkit.getDefaultToolkit().getImage(getClass().getResource("pictures/background.jpg"));
        loadHeartImage();




        gameStartTime = System.currentTimeMillis();

        Thread gameloop = new Thread(this);
        gameloop.start();

        timer = new Timer((int)alienSpawnInterval, this);
        timer.start();


        main.setVisible(true);
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, this);

        if (flashPlayer) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastHitTime > FLASH_DURATION) {
                flashPlayer = false;
            } else {
                if ((currentTime / 100) % 2 == 0) {
                    player.draw(g, this);
                }
            }
        } else {
            player.draw(g, this);
        }

        Enumeration<Alien> e = aliens.elements();
        while (e.hasMoreElements())
        {
            e.nextElement().draw(g, this);
        }

        Enumeration<Bullet> b = bullets.elements();
        while (b.hasMoreElements())
        {
            b.nextElement().draw(g, this);
        }

        Enumeration<AlienBullet> ab = alienBullets.elements();
        while (ab.hasMoreElements())
        {
            ab.nextElement().draw(g, this);
        }

        drawScoreboard(g);
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        Alien newAlien = new Alien(10, 10);

        newAlien.maxSpeed = 4.0f + (difficultyLevel - 1) * 1.5f;
        newAlien.velocityX = newAlien.maxSpeed;

        aliens.add(newAlien);

        timer.setDelay((int)alienSpawnInterval);
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = true;
            updatePlayerDirection();
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = true;
            updatePlayerDirection();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = false;
            updatePlayerDirection();
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = false;
            updatePlayerDirection();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upPressed = false;
        }
    }

    private void updatePlayerDirection() {
        if (rightPressed && !leftPressed) {
            player.direction = SpaceObj.right;
        } else if (leftPressed && !rightPressed) {
            player.direction = SpaceObj.left;
        } else {
            player.direction = 0;
        }
    }

    private void updateDifficulty() {
        int newLevel = 1 + (score / 1000);

        if (newLevel > difficultyLevel) {
            difficultyLevel = newLevel;

            alienSpawnInterval = Math.max(400, 1500 - (difficultyLevel - 1) * 250);

            for (Alien alien : aliens) {
                alien.maxSpeed = 4.0f + (difficultyLevel - 1) * 1.5f;
                if (alien.direction == SpaceObj.right) {
                    alien.velocityX = alien.maxSpeed;
                } else {
                    alien.velocityX = -alien.maxSpeed;
                }
            }
        }
    }

        private Image heartImage;

        private void loadHeartImage() {
            if (heartImage == null) {
                heartImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("pictures/heart.png"));
            }
        }

        private void drawHeart(Graphics2D g, int x, int y, int size) {
            if (heartImage == null) {
                heartImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("pictures/heart.png"));
            }

                int drawSize = size;
                if (g.getComposite().equals(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f))) {
                    drawSize = (int)(size * heartbeatScale);
                    int offset = (size - drawSize) / 2;
                    g.drawImage(heartImage, x + offset, y + offset, drawSize, drawSize, null);
                } else {
                    g.drawImage(heartImage, x, y, size, size, null);
                }
            }

    private void drawScoreboard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        long currentTime = System.currentTimeMillis();
        long elapsedTimeMillis = currentTime - gameStartTime;
        int seconds = (int) (elapsedTimeMillis / 1000) % 60;
        int minutes = (int) ((elapsedTimeMillis / (1000 * 60)) % 60);

        int panelWidth = 350;
        int panelHeight = 400;
        int panelX = getWidth() - panelWidth - 20;
        int panelY = getHeight() - panelHeight - 20;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(scoreboardBgColor);
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        g2d.setColor(new Color(0, 200, 255));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        g2d.setFont(scoreboardTitleFont);
        g2d.setColor(new Color(255, 255, 100));
        g2d.drawString("SPACE INVADERS", panelX + 50, panelY + 35);

        g2d.setFont(scoreboardFont);
        g2d.setColor(scoreboardTextColor);
        g2d.drawString("Punktestand: " + score, panelX + 30, panelY + 70);
        g2d.drawString(String.format("Spielzeit: %02d:%02d", minutes, seconds), panelX + 30, panelY + 100);
        g2d.drawString("Munition: " + player.ammo, panelX + 30, panelY + 130);
        g2d.drawString("Level: " + difficultyLevel, panelX + 30, panelY + 160);
        g2d.drawString("Alien-Geschwindigkeit: " + String.format("%.1f", (4.0f + (difficultyLevel - 1) * 1.5f)), panelX + 30, panelY + 190);
        g2d.drawString("Spawn-Intervall: " + String.format("%.1f", alienSpawnInterval / 1000.0f) + " s", panelX + 30, panelY + 220);


        int shootProbability = Math.min(90, 10 + (score / 1000) * 10);
        if (shootProbability < 30) {
            g2d.setColor(new Color(0, 255, 0)); // Grün
        } else if (shootProbability < 60) {
            g2d.setColor(new Color(255, 255, 0)); // Gelb
        } else {
            g2d.setColor(new Color(255, 50, 50)); // Rot
        }
        g2d.drawString("Alien-Probability: " + shootProbability + "%", panelX + 30, panelY + 250);
        g2d.setFont(new Font("Arial", Font.ITALIC, 13));
        g2d.setColor(new Color(200, 200, 200));
        g2d.setFont(scoreboardFont);
        g2d.setColor(scoreboardTextColor);

        g2d.setColor(new Color(255, 50, 50));
        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        FontMetrics fm = g2d.getFontMetrics();
        String lebenText = "Leben";
        int textWidth = fm.stringWidth(lebenText);
        g2d.drawString(lebenText, panelX + (panelWidth - textWidth) / 2, panelY + 290);
        g2d.setFont(scoreboardFont);

        int heartSize = 28;
        int heartSpacing = 8;

        int totalHeartsWidth = 5 * (heartSize + heartSpacing) - heartSpacing;
        int startX = panelX + (panelWidth - totalHeartsWidth) / 2;
        int startY = panelY + 305;

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
        for (int i = 0; i < 5; i++) {
            g2d.drawImage(heartImage, startX + i * (heartSize + heartSpacing), startY, heartSize, heartSize, null);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        for (int i = 0; i < player.lives; i++) {
            int pulseSize = (int)(heartSize * heartbeatScale);
            int pulseOffset = (heartSize - pulseSize) / 2;
            g2d.drawImage(heartImage,
                           startX + i * (heartSize + heartSpacing) + pulseOffset,
                           startY + pulseOffset,
                           pulseSize, pulseSize, null);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        int maxAmmo = 50;
        int barWidth = 130;
        int barHeight = 15;
        int barX = panelX + 30;
        int barY = panelY + 145;

        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRoundRect(barX, barY, barWidth, barHeight, 5, 5);

        float ammoRatio = (float) player.ammo / maxAmmo;
        int currentBarWidth = (int) (barWidth * ammoRatio);

        if (ammoRatio > 0.6f) {
            g2d.setColor(new Color(0, 255, 0));
        } else if (ammoRatio > 0.3f) {
            g2d.setColor(new Color(255, 255, 0));
        } else {
            g2d.setColor(new Color(255, 0, 0));
        }

        g2d.fillRoundRect(barX, barY, currentBarWidth, barHeight, 5, 5);
    }

    @Override
    public void run()
    {
        while(true)
        {
            double pulse = Math.sin(System.currentTimeMillis() / 250.0) * 0.08 + 1.0;
            heartbeatScale = (float)pulse;

            repaint();
            player.move();

            if (upPressed) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastShotTime > SHOT_COOLDOWN && player.ammo > 0) {
                    bullets.add(new Bullet(player.x + player.width/2 - 5, player.y - 20));
                    lastShotTime = currentTime;
                    player.ammo--;
                }
            }

            Enumeration<Alien> z = aliens.elements();
            while (z.hasMoreElements()) {
                Alien a = (Alien) z.nextElement();
                a.move();

                if (a.canShoot()) {
                    alienBullets.add(new AlienBullet(a.x + a.width/2 - 4, a.y + a.height));
                    a.markShot();
                }
            }

            for (int i = 0; i < alienBullets.size(); i++) {
                AlienBullet bullet = alienBullets.get(i);
                bullet.move();

                if (bullet.intersects(player)) {
                    alienBullets.remove(i);
                    i--;
                    player.lives--;

                    flashPlayer = true;
                    lastHitTime = System.currentTimeMillis();

                    if (player.lives <= 0) {

                        player.lives = 0;
                        System.exit(0);
                    }
                    continue;
                }

                if (bullet.y > getHeight()) {
                    alienBullets.remove(i);
                    i--;
                }
            }

            for (int i = 0; i < bullets.size(); i++) {
                Bullet bullet = bullets.get(i);
                bullet.move();

                boolean bulletRemoved = false;
                for (int j = 0; j < aliens.size(); j++) {
                    Alien alien = aliens.get(j);
                    if (bullet.intersects(alien)) {
                        aliens.remove(j);
                        bullets.remove(i);
                        i--;
                        score += 100;
                        player.ammo += 2;

                        updateDifficulty();
                        bulletRemoved = true;
                        break;
                    }
                }

                if (!bulletRemoved && bullet.y < 0) {
                    bullets.remove(i);
                    i--;
                }
            }

            try
            {
                Thread.sleep(20);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }

    }
}