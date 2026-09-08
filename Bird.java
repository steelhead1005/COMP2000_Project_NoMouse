import java.awt.Color;
import java.awt.Graphics2D;

public class Bird extends MovingEntity {
     
    private double mass = 1.0;

    private double ax = 0;
    private double ay = 0;
    
    private static final BirdBehaviour.Separation separation = new BirdBehaviour.Separation();
    private static final BirdBehaviour.Alignment alignment = new BirdBehaviour.Alignment();
    private static final BirdBehaviour.Cohesion cohesion = new BirdBehaviour.Cohesion();

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

    //@Override
    //needs bird list param
    public void update(World world) {
        separation.calculate(this, world);
        alignment.calculate(this, world);
        cohesion.calculate(this, world);
        
        updatePosition();
        wrapAround(400, 400);
    }
    private void updatePosition() {
        // Acceleration changes velocity
        this.dx += this.ax;
        this.dy += this.ay;
        limitVelocity(3.0);
        // Velocity changes position
        this.x += this.dx;
        this.y += this.dy;

        // Acceleration is reset each frame
        this.ax = 0;
        this.ay = 0;
    }
    private void wrapAround(double width, double height) {
        if (this.x > width) this.x = 0;
        else if (this.x < 0) this.x = width;
        if (this.y > height) this.y = 0;
        else if (this.y < 0) this.y = height;
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)this.x, (int)this.y, 6, 6);
    }

}
