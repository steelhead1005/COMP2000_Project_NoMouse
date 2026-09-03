public abstract class Entity {
    protected double x, y;
    protected double dx, dy;

    public Entity(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public abstract void update();
    public abstract void render();

    public double distanceTo(Entity other) {
        double diffX = this.x - other.x;
        double diffY = this.y - other.y;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }
}
