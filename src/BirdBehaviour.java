import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class BirdBehaviour {

    private BirdBehaviour() { }

    private static Collector<Bird, double[], double[]> repulsionFrom(Bird self) {
    return Collector.of(
            () -> new double[2],
            (force, other) -> {
                double dist = self.distanceTo(other);
                if (dist > 0) {
                    force[0] += (self.getX() - other.getX()) / dist;
                    force[1] += (self.getY() - other.getY()) / dist;
                }
            },
            (a, b) -> { a[0] += b[0]; a[1] += b[1]; return a; },
            Collector.Characteristics.UNORDERED);
    }

    public static class Separation implements Behaviour<Bird> {
        private volatile double weight;

        public Separation(double weight) {
            this.weight = weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        @Override
        public void calculate(Bird self, World world) {
            List<Bird> neighbors = world.getEntitiesInRange(world.getBirds(), self, 25.0);
            double[] force = neighbors.stream().collect(repulsionFrom(self));
            self.applyForce(force[0] * weight, force[1] * weight);
        }
    }

    public static class Alignment implements Behaviour<Bird> {
        private volatile double weight;

        public Alignment(double weight) {
            this.weight = weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        @Override
        public void calculate(Bird self, World world) {
            List<Bird> neighbors = world.getEntitiesInRange(world.getBirds(), self, 50.0);
            if (neighbors.isEmpty()) return;
 
            double[] avg = neighbors.stream().collect(Collectors.teeing(
                Collectors.averagingDouble(Bird::getVelocityX),
                Collectors.averagingDouble(Bird::getVelocityY),
                (avgDx, avgDy) -> new double[]{avgDx, avgDy}));

            self.applyForce((avg[0] - self.getVelocityX()) * weight, (avg[1] - self.getVelocityY()) * weight);
        }
    }

    public static class Cohesion implements Behaviour<Bird> {
        private volatile double weight;

        public Cohesion(double weight) {
            this.weight = weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        @Override
        public void calculate(Bird self, World world) {
            List<Bird> neighbors = world.getEntitiesInRange(world.getBirds(), self, 50);
            if (neighbors.isEmpty()) return;

            double[] center = neighbors.stream().collect(Collectors.teeing(
                Collectors.averagingDouble(Bird::getX),
                Collectors.averagingDouble(Bird::getY),
                (centerX, centerY) -> new double[]{centerX, centerY}));

            self.applyForce((center[0] - self.getX()) * weight, (center[1] - self.getY()) * weight);
        }
    }

    public static Behaviour<Bird> fleePredators(double range, double weight) {
        return (self, world) -> world.getPredators().stream()
                .filter(predator -> self.distanceTo(predator) < range)
                .forEach(predator -> {
                    double d = Math.max(0.001, self.distanceTo(predator));
                    double strength = (range - d) / range * weight;
                    self.applyForce((self.getX() - predator.getX()) / d * strength,
                            (self.getY() - predator.getY()) / d * strength);
                });
    }
}
