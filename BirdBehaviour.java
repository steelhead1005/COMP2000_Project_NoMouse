import java.util.*;

public final class BirdBehaviour {
    public static class Separation implements Behaviour<Bird> {
        public /*Maybe make vector class*/ void  calculate(Bird self, World world) {
            List<Bird> neighbors = calculateNeighbors(self, world, 25.0);
            double forceX = 0, forceY = 0;
            
            for (Bird other : neighbors) {
                double dist = self.distanceTo(other);
                if (dist > 0) {
                    forceX += (self.getX() - other.getX()) / dist; 
                    forceY += (self.getY() - other.getY()) / dist;
                }
            }
            self.applyForce(forceX * 1.5, forceY * 1.5);
        }
    }
    public static class Alignment implements Behaviour<Bird> {
        public void calculate(Bird self, World world) {
            List<Bird> neighbors = calculateNeighbors(self, world, 50.0);
            if (neighbors.isEmpty()) return;
            
            double avgDx = 0, avgDy = 0;
            for (Bird other : neighbors) {
                avgDx += other.getVelocityX();
                avgDy += other.getVelocityY();
            }
            avgDx /= neighbors.size();
            avgDy /= neighbors.size();
            
            self.applyForce((avgDx - self.getVelocityX()) * 0.05, (avgDy - self.getVelocityY()) * 0.05);
        }
    }
    public static class Cohesion implements Behaviour<Bird> {
        public void calculate(Bird self, World world) {
            List<Bird> neighbors = calculateNeighbors(self, world, 50.0);
            if (neighbors.isEmpty()) return;
            
            double centerX = 0, centerY = 0;
            for (Bird other : neighbors) {
                centerX += other.getX();
                centerY += other.getY();
            }
            centerX /= neighbors.size();
            centerY /= neighbors.size();
            
            // Steer towards center of mass
            self.applyForce((centerX - self.getX()) * 0.01, (centerY - self.getY()) * 0.01);
        }
    }
    
    public static List<Bird> calculateNeighbors(Bird self, World world, double x) {
        List<Bird> allBirds = world.getBirds();
        List<Bird> neighbors = new ArrayList<>();

        for (Bird other : allBirds) {
            if (other == self) continue;

            double d = self.distanceTo(other);

            if (d < x) {
                neighbors.add(other);
            }
        }
        return neighbors;
    }
}

