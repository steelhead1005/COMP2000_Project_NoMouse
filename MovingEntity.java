public abstract class MovingEntity extends Entity {
    protected double dx, dy;
    protected double ax, ay;
    protected MovingEntity(double x, double y) {
        super(x, y);
    }
}
