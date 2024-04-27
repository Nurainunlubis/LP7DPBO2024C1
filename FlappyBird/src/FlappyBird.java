import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int frameWidht = 360;
    int frameHight = 640;

    Image backgroundImage;
    Image birdImage;
    Image lowerPipeImage;
    Image upperPipeImage;

    int playerStartPosX = frameWidht / 8;
    int PlayerStartPosY = frameHight / 2;
    int playerWidht = 34;
    int playerHeight = 24;

    Player player;

    int pipeStartPosX = frameWidht;
    int PipeStartPosY = 0;
    int PipeWidth = 64;
    int PipeHeight = 512;

    ArrayList<Pipe> pipes;
    Timer gameLoop;
    Timer pipesCooldown;
    int gravity = 1;

    private int score = 0;
    private JLabel scoreLabel;

    private boolean gameRunning = true;

    public FlappyBird() {
        setPreferredSize(new Dimension(frameWidht, frameHight));
        setFocusable(true);
        addKeyListener(this);

        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();

        player = new Player(playerStartPosX, PlayerStartPosY, playerWidht, playerHeight, birdImage);
        pipes = new ArrayList<Pipe>();

        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setForeground(Color.WHITE);
        add(scoreLabel);
        scoreLabel.setBounds(10, 10, 100, 20);

        pipesCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        pipesCooldown.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, frameWidht, frameHight, null);
        g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidht(), player.getHeight(), null);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
        }
    }

    public void move() {
        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0));

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipe.getVelocityX());
        }

        updateScore();

        if (player.getPosY() + player.getHeight() >= frameHight || isCollidingWithPipe()) {
            gameOver();
        }
    }

    public void placePipes() {
        int randomPipePosY = (int) (PipeStartPosY - PipeHeight / 4 - Math.random() * (PipeHeight / 2));
        int openingSpace = frameHight / 4;

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPipePosY, PipeWidth, PipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, randomPipePosY + PipeHeight + openingSpace, PipeWidth, PipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

    private void gameOver() {
        gameRunning = false;
        gameLoop.stop();
        pipesCooldown.stop();

        // Menampilkan pop-up dengan skor saat permainan berakhir
        JOptionPane.showMessageDialog(this, "Game Over! Your Score: " + score, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private void restartGame() {
        pipes.clear();
        player.setPosY(PlayerStartPosY);
        score = 0;
        scoreLabel.setText("Score: " + score);
        gameRunning = true;
        gameLoop.start();
        pipesCooldown.start();
    }

    private void updateScore() {
        ArrayList<Pipe> pipesToRemove = new ArrayList<>();

        for (Pipe pipe : pipes) {
            if (!pipe.isPassed() && pipe.getPosX() + pipe.getWidth() < player.getPosX()) {
                pipesToRemove.add(pipe);
            }
        }

        if (!pipesToRemove.isEmpty()) {
            for (Pipe pipe : pipesToRemove) {
                pipe.setPassed(true);
            }
            score++;
            scoreLabel.setText("Score: " + score);
        }
    }

    private boolean isCollidingWithPipe() {
        for (Pipe pipe : pipes) {
            if (player.getPosX() + player.getWidht() > pipe.getPosX() && player.getPosX() < pipe.getPosX() + pipe.getWidth()) {
                if (player.getPosY() < pipe.getPosY() + pipe.getHeight() && player.getPosY() + player.getHeight() > pipe.getPosY()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && gameRunning) {
            player.setVelocityY(-10);
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            if (!gameRunning) {
                restartGame();
            }
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        FlappyBird game = new FlappyBird();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
