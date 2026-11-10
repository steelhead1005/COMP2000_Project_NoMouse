import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
public class World {
    public double width;
    public double height;
    private final int MAX_BIRDS = 500;
    private final List<Bird> birds = new ArrayList<>();
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Predator> predators = new ArrayList<>();
    private final List<Behaviour<Bird>> behaviours = new CopyOnWriteArrayList<>();



    public World(double width, double height) {
        if(width <=0 || height <=0){
            throw new IllegalArgumentException(
                    "World dimensions must be positive"
            );
        }
        this.width = width;
        this.height = height;
    }


    public synchronized void addBird(Bird bird) throws CapacityExceededException {
        if (birds.size() >= MAX_BIRDS) {
            throw new CapacityExceededException("World capacity reached. Cannot exceed " + MAX_BIRDS + " birds.");
        }
        if (bird == null) {
            throw new IllegalArgumentException("Cannot add a null Bird to the world.");
        }
        birds.add(bird);
    }
    public synchronized void addObstacle(Obstacle obstacle) {
        if (obstacle == null) {
            throw new IllegalArgumentException("Cannot add a null Obstacle to the world.");
        }
        obstacles.add(obstacle);
    }

    public enum ObstacleShape {
        CIRCLE("Circle"), RECTANGLE("Rectangle");

        private final String label;

        ObstacleShape(String label) { this.label = label; }

        @Override
        public String toString() { return label; }
    }

    public synchronized void spawnObstacle(ObstacleShape shape, double speed) throws Obstacle.SpawnException {
        Obstacle obstacle = switch (shape) {
            case CIRCLE -> Obstacle.spawnRandom((int) width, (int) height, Long.MAX_VALUE, obstacles);
            case RECTANGLE -> PillarObstacle.spawnRandom(width, height, obstacles);
        };
        obstacle.setColor(Obstacle.randomColor());
        if (speed > 0) {
            double angle = Math.random() * 2 * Math.PI;
            obstacle.setVelocity(Math.cos(angle) * speed, Math.sin(angle) * speed);
        }
        obstacles.add(obstacle);
    }

    public synchronized void clearObstacles() {
        obstacles.clear();
    }

    public void addBehaviour(Behaviour<Bird> behaviour) {
        if (behaviour == null) {
            throw new IllegalArgumentException("Cannot add a null Behaviour to the world.");
        }
        if (!behaviours.contains(behaviour)) {
            behaviours.add(behaviour);
        }
    }

    public void removeBehaviour(Behaviour<Bird> behaviour) {
        behaviours.remove(behaviour);
    }

    public List<Behaviour<Bird>> getBehaviours() {
        return behaviours;
    }

    public synchronized void addPredator(Predator predator){
        if (predator == null) {
            throw new IllegalArgumentException("Cannot add a null Predator to the world.");
        }
        predators.add(predator);
        
    }
    public synchronized void update() {
        obstacles.removeIf(Obstacle::isExpired);
        obstacles.forEach(obstacle -> obstacle.move(width, height));
        for (Predator predator : predators) {
            predator.update(birds, obstacles);
            resolvePillarCollisions(predator, predator.getBodyRadius());
            keepInBounds(predator);
        }
        birds.parallelStream().forEach(bird -> {
            obstacles.stream()
                    .map(obstacle -> obstacle.computeAvoidanceForce(bird.getX(), bird.getY(), 45, 2.5))
                    .forEach(force -> bird.applyForce(force[0], force[1]));
            bird.computeForces(this);
        });
        birds.parallelStream().forEach(bird -> {
            bird.integrate(this);
            resolvePillarCollisions(bird, 4);
            keepInBounds(bird);
        });
    }
    /** Copy one consistent frame while update() is excluded by this same monitor.
     * Parallel analytics must run after this method returns, outside the lock.
     */
    public synchronized PredatorAnalytics.Snapshot snapshotForAnalytics() {
        List<PredatorAnalytics.Position> birdPositions = new ArrayList<>();
        List<PredatorAnalytics.Position> predatorPositions = new ArrayList<>();
        birds.forEach(bird -> birdPositions.add(
                new PredatorAnalytics.Position(bird.getX(), bird.getY())));
        predators.forEach(predator -> predatorPositions.add(
                new PredatorAnalytics.Position(predator.getX(), predator.getY())));
        return new PredatorAnalytics.Snapshot(birdPositions, predatorPositions);
    }

    private void keepInBounds(MovingEntity entity) {
        double x = Math.max(0, Math.min(width, entity.getX()));
        double y = Math.max(0, Math.min(height, entity.getY()));
        if (x != entity.getX()) entity.dx = 0;
        if (y != entity.getY()) entity.dy = 0;
        entity.setPosition(x, y);
    }

    private void resolvePillarCollisions(MovingEntity entity, double padding) {
        obstacles.stream()
                .filter(obstacle -> obstacle instanceof PillarObstacle)
                .map(obstacle -> (PillarObstacle) obstacle)
                .forEach(pillar -> pillar.resolveCollision(entity, padding));
    }

    public <T extends Entity> List<T> getEntitiesInRange(List<T> entities, Entity center, double radius) {
        if (entities == null || center == null) {
        throw new IllegalArgumentException("Entities list and center entity cannot be null.");
        }
        if (radius < 0) {
            throw new IllegalArgumentException("Search radius cannot be negative.");
        }
        return entities.stream()
                .filter(entity -> entity != center && center.distanceTo(entity) < radius)
                .toList();
    }

    public List<Bird> getBirds() {
        return birds;
    }

    public int getBirdCount(List<Bird> list){
        return list.size();
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<Predator> getPredators(){
        return predators;
    }

    public int getPredatorCount(List<Predator> list){
        return list.size();
    }    
    public synchronized void changeBounds(double width, double height){
        this.width = width;
        this.height = height;
    }

} 
