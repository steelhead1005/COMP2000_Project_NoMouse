import java.util.List;

public class BirdBehaviour {

public class UBehaviour implements Behaviour<Bird> {
    @Override
    public void calculate(Bird self, World world) {
            List<Bird> allBirds = world.getBirds();
            double perceptionRadius = 50.0;
            double separationRadius = 25.0;

            double sepX = 0, sepY = 0;
            double alignX = 0, alignY = 0;
            double cohX = 0, cohY = 0;

            int totalNeighbors = 0;
            int sepNeighbors = 0;

            for (Bird other : allBirds) {
                if (other == self) continue;

                double d = self.distanceTo(other);

                if (d > 0 && d < perceptionRadius) {
                    // Alignment: sum neighbor velocities
                    alignX += other.dx;
                    alignY += other.dy;


                    // Cohesion: sum neighbor positions
                    cohX += other.x;
                    cohY += other.y;
                    totalNeighbors++;

                    // Separation: sum vectors pointing away from close neighbors
                    if (d < separationRadius) {
                        double diffX = self.x - other.x;
                        double diffY = self.y - other.y;

                        // Weight the separation force inversely by distance
                        sepX += (diffX / d) / d;
                        sepY += (diffY / d) / d;
                        sepNeighbors++;
                    }
                }
            }

            if (totalNeighbors > 0) {
                // Alignment: (Average Velocity) - Current Velocity
                alignX = (alignX / totalNeighbors) - self.dx;
                alignY = (alignY / totalNeighbors) - self.dy;

                // Cohesion: (Vector towards Average Position) - Current Velocity
                cohX = (cohX / totalNeighbors) - self.x;
                cohY = (cohY / totalNeighbors) - self.y;

                self.applyForce(alignX * 0.05, alignY * 0.05);
                self.applyForce(cohX * 0.005, cohY * 0.005);
            }

            if (sepNeighbors > 0) {
                // Separation: Average escape vector
                sepX /= sepNeighbors;
                sepY /= sepNeighbors;
                self.applyForce(sepX * 1.5, sepY * 1.5);
            } 
        }
    }
}
