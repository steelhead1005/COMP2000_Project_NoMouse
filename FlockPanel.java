import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class FlockPanel extends JPanel {

    private final World world;

    public FlockPanel(World world) {
        this.world = world;
        setBackground(Color.GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // Draw all birds.
        for (Bird bird : world.getBirds()) {
            bird.render(g2d);
        }

        // Draw all obstacles.
        for (Obstacle obstacle : world.getObstacles()) {
            obstacle.render(g2d);
        }

        // Draw all predators.
        for (Predator predator : world.getPredators()) {
            predator.render(g2d);
        }
    }
}