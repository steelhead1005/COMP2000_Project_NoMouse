/** A fast hunter that pursues the nearest bird anywhere in the world. */
public class ChaserPredator extends Predator {
    public ChaserPredator(double x, double y) {
        super(x, y, 4.1, 20.0);
    }

    @Override
    protected double getDetectionRadius() {
        return Double.POSITIVE_INFINITY;
    }
}
