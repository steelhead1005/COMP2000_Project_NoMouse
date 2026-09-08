import java.util.*;

public final class BirdBehaviour {
    public static class Separation implements Behaviour<Bird> {
        public /*Maybe make vector class*/ void  calculate(Bird self, World world) {
            List<Bird> neighbors = calculateNeighbors(self, world, 25.0);
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

