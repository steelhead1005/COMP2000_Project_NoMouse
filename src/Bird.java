import java.awt.Color;
import java.awt.Graphics2D;

public class Bird extends MovingEntity {
    private double mass = 1.0;
    private double size = 10.0;
    private static final double MIN_SPEED = 3.0;

    public Bird (double x, double y) {
        super(x, y);
        this.dx = (Math.random() * 4) - 2;
        this.dy = (Math.random() * 4) - 2;
    }
    public double getHeading() {
        return Math.atan2(this.dy, this.dx);
    }
   
    public void applyForce(double forceX, double forceY) {
        this.ax += forceX / this.mass;
        this.ay += forceY / this.mass;
    }

    public void computeForces(World world) {
        world.getBehaviours().forEach(behaviour -> behaviour.calculate(this, world));
    }

    public void integrate(World world) {
        updatePosition();
        avoidWalls(world.width, world.height);
    }
    private void updatePosition() {
        // Acceleration changes velocity
        this.dx += this.ax;
        this.dy += this.ay;
        limitVelocity(4.0);
        enforceMinimumSpeed(MIN_SPEED);
        // Velocity changes position
        this.x += this.dx;
        this.y += this.dy;

        // Acceleration is reset each frame
        this.ax = 0;
        this.ay = 0;
    }
    private void enforceMinimumSpeed(double minimumSpeed) {
        double speed = Math.hypot(this.dx, this.dy);
        if (speed == 0) {
            double angle = Math.random() * 2 * Math.PI;
            this.dx = Math.cos(angle) * minimumSpeed;
            this.dy = Math.sin(angle) * minimumSpeed;
        } else if (speed < minimumSpeed) {
            this.dx = this.dx / speed * minimumSpeed;
            this.dy = this.dy / speed * minimumSpeed;
        }
    }

   private void avoidWalls(double width, double height) {
        double margin = 100.0; 
        double turnFactor = 0.2;
        
        if (this.x < margin) {
            this.ax += turnFactor;
        } else if (this.x > width - margin) {
            this.ax -= turnFactor;
        }
        
        if (this.y < margin) {
            this.ay += turnFactor;
        } else if (this.y > height - margin) {
            this.ay -= turnFactor;
        }
    }

    @Override
    public void render(Graphics2D g2d) {
        double theta = getHeading();
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);

        double[] px = { size * 0.5, -size * 0.4, -size * 0.1, -size * 0.4 };
        double[] py = { 0,          -size * 0.4,  0,           size * 0.4 };

        int[] xPoints = new int[4];
        int[] yPoints = new int[4];

        for (int i = 0; i < 4; i++) {
            xPoints[i] = (int) (this.x + (px[i] * cos - py[i] * sin));
            yPoints[i] = (int) (this.y + (px[i] * sin + py[i] * cos));
        }

        g2d.setColor(Color.WHITE);
        g2d.fillPolygon(xPoints, yPoints, 4);
    }

}
