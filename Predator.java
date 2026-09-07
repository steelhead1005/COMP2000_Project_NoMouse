import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

public class Predator extends MovingEntity {

    private double speed = 2.0;
    private int size = 16;

    public Predator(double x, double y) {
        super(x, y);
    }

    public void update(List<Bird> birds) {
        Bird closestBird = findClosestBird(birds);

        if (closestBird != null) {
            moveTowards(closestBird);
        }

        x += dx;
        y += dy;
    }

    private Bird findClosestBird(List<Bird> birds) {
        Bird closestBird = null;
        double closestDistance = Double.MAX_VALUE;

        for (Bird bird : birds) {
            double distance = distanceTo(bird);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestBird = bird;
            }
        }

        return closestBird;
    }

    private void moveTowards(Bird bird) {
        double differenceX = bird.getX() - x;
        double differenceY = bird.getY() - y;

        double distance = Math.sqrt(
                differenceX * differenceX
                + differenceY * differenceY
        );

        if (distance > 0) {
            dx = (differenceX / distance) * speed;
            dy = (differenceY / distance) * speed;
        }
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(Color.RED);

        g2d.fillOval(
                (int) x - size / 2,
                (int) y - size / 2,
                size,
                size
        );
    }
}