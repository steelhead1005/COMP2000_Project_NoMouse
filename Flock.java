import java.util.ArrayList;
import java.util.List;

public class Flock {
    private List<Bird> birds;

    public Flock() {
        this.birds = new ArrayList<>();
    }

    public void addBird(Bird b) {
        if(birds != null){
            birds.add(b);
        }
    }

    public int countBirds(){
        if(birds == null){
            return 0;
        }
        return birds.size();
    }

    public void resetFlock(){
        if(birds != null){
            birds.clear();
        }
    }
}

