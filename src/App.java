import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;
import javax.swing.*;

public class App {
    private final World world = new World(1100, 700);
    private final FlockPanel flockPanel = new FlockPanel(world);

    private final JFrame frame = new JFrame("Flock Simulation");
    private final JPanel sliderPanel = new JPanel();

    private final JSlider separation = new JSlider();
    private final JSlider alignment = new JSlider();
    private final JSlider cohesion = new JSlider();

    private final BirdBehaviour.Separation separationRule = new BirdBehaviour.Separation(0.5);
    private final BirdBehaviour.Alignment alignmentRule = new BirdBehaviour.Alignment(0.10);
    private final BirdBehaviour.Cohesion cohesionRule = new BirdBehaviour.Cohesion(0.003);
    private final Behaviour<Bird> fleeRule = BirdBehaviour.fleePredators(110, 2.0);


    //creates a slider with the corect labels
    private JPanel labelledSlider(String name, JSlider slider, int min, int max, int start, Behaviour<Bird> rule) {
    slider.setMinimum(min);
    slider.setMaximum(max);
    slider.setValue(start);

    JLabel label = new JLabel("     " + name + ": " + start);
    // keeps all the sliders lined up
    label.setPreferredSize(new Dimension(110, 20));   

    // Update the text whenever the slider moves
    slider.addChangeListener(e -> label.setText("     " + name + ": " + slider.getValue()));

    slider.setOpaque(false);

    JPanel row = new JPanel(new BorderLayout());
    row.setOpaque(false);
    row.add(label, BorderLayout.WEST);
    row.add(slider, BorderLayout.CENTER);
    row.add(toggle(rule), BorderLayout.EAST);
    return row;
}


    private JCheckBox toggle(Behaviour<Bird> rule) {
        JCheckBox box = new JCheckBox("On", true);
        box.setOpaque(false);
        box.addItemListener(e -> {
            if (box.isSelected()) world.addBehaviour(rule);
            else world.removeBehaviour(rule);
        });
        return box;
    }

    private final ScheduledExecutorService simLoop = Executors.newSingleThreadScheduledExecutor(task -> {
        Thread thread = new Thread(task, "sim-loop");
        thread.setDaemon(true);
        return thread;
    });

    // Keep analytics off both the simulation scheduler and Swing's event thread.
    private final ScheduledExecutorService analyticsLoop = Executors.newSingleThreadScheduledExecutor(task -> {
        Thread thread = new Thread(task, "predator-analytics");
        thread.setDaemon(true);
        return thread;
    });

    //RGB Color of background
    private final Color bgColor = new Color(15, 22, 38);


        public App() {
            // Create both predator types when the simulation starts.
            try {
                world.addPredator(new ChaserPredator(200, 200));
                world.addPredator(new AmbushPredator(350, 250));
            } catch (IllegalArgumentException e) {
                System.err.println("Failed to add predator: " + e.getMessage());
            }
            world.addBehaviour(separationRule);
            world.addBehaviour(alignmentRule);
            world.addBehaviour(cohesionRule);
            world.addBehaviour(fleeRule);
            // Setting flockPanel paramaters
            flockPanel.setBackground(bgColor);
            // Setting sliderPanel paramaters
            sliderPanel.setBackground(Color.gray);
            sliderPanel.setPreferredSize(new Dimension(600, 200));

            for (int i = 0; i < 500; i++) {
                try {
                    world.addBird(new Bird(Math.random() * (int)world.width, Math.random() * (int)world.height));
                } catch (IllegalArgumentException e) {
                    
                    System.err.println("Failed to add bird " + i + ": " + e.getMessage());
                } catch (CapacityExceededException e) {
                    
                    System.err.println("Stopped adding birds: " + e.getMessage());
                    break;
                }
            }

            
            
            // Add panels to Frame and set Frame layout
            frame.setLayout(new BorderLayout());
            frame.add(flockPanel, BorderLayout.CENTER);
            frame.add(sliderPanel, BorderLayout.SOUTH);

            //Add Slider to sliderPanel

            sliderPanel.setLayout(new GridLayout(5, 1, 5, 5));   // one row per behaviour
            sliderPanel.add(labelledSlider("Separation", separation, 0, 20, 5, separationRule));
            sliderPanel.add(labelledSlider("Alignment", alignment, 0, 30, 10, alignmentRule));
            sliderPanel.add(labelledSlider("Cohesion", cohesion, 0, 20, 3, cohesionRule));

            JPanel fleeRow = new JPanel(new BorderLayout());
            fleeRow.setOpaque(false);
            JLabel fleeLabel = new JLabel("     Flee predators");
            fleeLabel.setPreferredSize(new Dimension(110, 20));
            fleeRow.add(fleeLabel, BorderLayout.WEST);
            fleeRow.add(toggle(fleeRule), BorderLayout.EAST);
            sliderPanel.add(fleeRow);

            JPanel obstacleRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            obstacleRow.setOpaque(false);
            JComboBox<World.ObstacleShape> shapeBox = new JComboBox<>(World.ObstacleShape.values());
            JButton spawnButton = new JButton("Spawn obstacle");
            JCheckBox movingBox = new JCheckBox("Moving", false);
            movingBox.setOpaque(false);
            JButton clearButton = new JButton("Clear obstacles");
            spawnButton.addActionListener(e -> {
                try {
                    world.spawnObstacle((World.ObstacleShape) shapeBox.getSelectedItem(),
                            movingBox.isSelected() ? 1.5 : 0);
                } catch (Obstacle.SpawnException ex) {
                    System.out.println(ex.getMessage());
                }
            });
            clearButton.addActionListener(e -> world.clearObstacles());
            obstacleRow.add(new JLabel("     Shape:"));
            obstacleRow.add(shapeBox);
            obstacleRow.add(spawnButton);
            obstacleRow.add(movingBox);
            obstacleRow.add(clearButton);
            sliderPanel.add(obstacleRow);

            separation.addChangeListener(e -> separationRule.setWeight(separation.getValue() / 10.0));
            alignment.addChangeListener(e  -> alignmentRule.setWeight(alignment.getValue() / 100.0));
            cohesion.addChangeListener(e  -> cohesionRule.setWeight(cohesion.getValue() / 1000.0));

            flockPanel.addComponentListener(new ComponentAdapter() {
                @Override 
                public void componentResized(ComponentEvent e) {
                    world.changeBounds(Math.max(1, flockPanel.getWidth()), Math.max(1, flockPanel.getHeight()));
                }
            });

            //Set frame size and make it visible.
            frame.setSize((int)world.width, (int)world.height + 240);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // If window closes, exit the program.
            frame.addWindowListener(
                new WindowAdapter() {
                    @Override 
                    public void
                    windowClosing(WindowEvent we) {
                        analyticsLoop.shutdownNow();
                        simLoop.shutdownNow();
                        System.exit(0);
                    }
                }
            );
           simLoop.scheduleAtFixedRate(() -> {
            try {
                world.update();
                flockPanel.repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            }, 0, 16, TimeUnit.MILLISECONDS);
            analyticsLoop.scheduleWithFixedDelay(() -> {
                try {
                    PredatorAnalytics.Snapshot snapshot = world.snapshotForAnalytics();
                    // No world lock is held while ForkJoinPool workers analyse the copies.
                    java.util.Map<PredatorAnalytics.Zone, Long> counts =
                            PredatorAnalytics.analyseParallel(snapshot);
                    SwingUtilities.invokeLater(() -> {
                        if (frame.isDisplayable()) {
                            frame.setTitle("Flock Simulation | DANGER: " + counts.get(PredatorAnalytics.Zone.DANGER)
                                    + " | WARNING: " + counts.get(PredatorAnalytics.Zone.WARNING)
                                    + " | SAFE: " + counts.get(PredatorAnalytics.Zone.SAFE));
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }, 0, 1, TimeUnit.SECONDS);
    }
    //for testing purposess will be moved later
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}
