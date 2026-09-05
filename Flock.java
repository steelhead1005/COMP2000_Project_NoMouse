import java.util.ArrayList;
import java.util.List;

public class Flock {
    private List<Bird> birds;

    public Flock() {
        this.birds = new ArrayList<>();
    }

    public void addBird(Bird b) {
        birds.add(b);
    }

    public int countBirds(){
        return birds.size();
    }

    public void resetFlock(){
        birds.clear();
    }
}

