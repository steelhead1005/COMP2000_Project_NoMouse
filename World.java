import java.util.ArrayList;
import java.util.List;
public class World {
    private final double width, height;
    private final List<Bird> birds = new ArrayList<>();
    private final List<Obstacle> obstacles = new ArrayList<>();

    public World(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public void addBird(Bird b) {
        birds.add(b);
    }
    public void addObstacle(Obstacle o) {
        obstacles.add(o);
    }

    public List<Bird> getBirds() {
        return birds;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }
} 
