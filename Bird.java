import java.awt.Graphics2D;

public class Bird extends MovingEntity {
     
    private double mass = 1.0;

    private double ax = 0;
    private double ay = 0;

    public Bird (double x, double y) {
        super(x, y);
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
    public void update() {
        // add and apply force calculations i.e. seperation, cohesion, alignment
        updatePosition(); 
    }
    private void updatePosition() {
        // Acceleration changes velocity
        this.dx += this.ax;
        this.dy += this.ay;

        // Velocity changes position
        this.x += this.dx;
        this.y += this.dy;

        // Acceleration is reset each frame
        this.ax = 0;
        this.ay = 0;
    }

    @Override
    public void render(Graphics2D g2d) {
    // drawing logic
    }

}
