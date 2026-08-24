import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Obstacle {

    private static Random RNG = new Random();

    private double x;
    private double y;
    private double radius;
    private long spawnTimeMillis;
    private long lifespanMillis;

    public Obstacle(double x, double y, double radius, long lifespanMillis) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.spawnTimeMillis = System.currentTimeMillis();
        this.lifespanMillis = lifespanMillis;
    }

    
    public static Obstacle spawnRandom(int frameWidth, int frameHeight, long lifespanMillis, List<Obstacle> existing) {
        double minDim = Math.min(frameWidth, frameHeight);
        double minDiameter = minDim / 12.0;
        double maxDiameter = minDim / 8.0;

        int maxAttempts = 10;
        Obstacle candidate = null; //holds the new potential obstacle 

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            double diameter = minDiameter + RNG.nextDouble() * (maxDiameter - minDiameter);
            double radius = diameter / 2.0;

            // Keep the whole circle inside the frame bounds.
            double x = radius + RNG.nextDouble() * Math.max(0, frameWidth - 2 * radius);
            double y = radius + RNG.nextDouble() * Math.max(0, frameHeight - 2 * radius);

            candidate = new Obstacle(x, y, radius, lifespanMillis);
            if (existing == null || !candidate.overlapsAny(existing)) {
                return candidate;
            }
        }

        return candidate;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - spawnTimeMillis >= lifespanMillis;
    }

    public long getAgeMillis() {
        return System.currentTimeMillis() - spawnTimeMillis;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getRadius() { return radius; }

    public double distanceTo(double px, double py) {
        double dx = px - x;
        double dy = py - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** True if the point (px, py) is inside this pillar. */
    public boolean contains(double px, double py) {
        return distanceTo(px, py) <= radius;
    }

    public boolean overlaps(Obstacle other) {
        return distanceTo(other.x, other.y) < (this.radius + other.radius);
    }

    public boolean overlapsAny(List<Obstacle> others) {
        for (Obstacle o : others) {
            if (this.overlaps(o)) return true;
        }
        return false;
    }