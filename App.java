import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
NOTES:
    going to change GridLayout to GridBagLayout
    make flockPanel 2/3 of frame
    add lables to sliders
    add use to sliders
    create bird visule
    create piller visule
    make pretty if time
 */

public class App {
    Frame frame = new Frame("Flock Simulation");
    Panel flockPanel = new Panel();
    Panel sliderPanel = new Panel();
    JSlider separation = new JSlider();
    JSlider alignment = new JSlider();
    JSlider piller = new JSlider();

        public App() {
            // Setting flockPanel paramaters
            //flockPanel.setBounds(0, 0, 200, 200);
            flockPanel.setBackground(Color.gray);
            // Setting sliderPanel paramaters
            //sliderPanel.setBounds(0, 400, 200, 200);
            sliderPanel.setBackground(Color.blue);

            
            
            // Add panels to Frame and set Frame layout
            frame.add(flockPanel);
            frame.add(sliderPanel);
            frame.setSize(400, 600);
            frame.setLayout(new GridLayout(2, 1, 10, 10));
            frame.setVisible(true);

            //Add Slider to sliderPanel
            sliderPanel.add(separation);
            sliderPanel.add(alignment);
            sliderPanel.add(piller);
            sliderPanel.setLayout(new GridLayout(3, 1, 5, 5));

                // If window closes, exit the program.
            frame.addWindowListener(
                new WindowAdapter() {
                    public void
                    windowClosing(WindowEvent we) {
                        System.exit(0);
                    }
                }
            );
    }
    //for testing purposess will be moved later
    public static void main(String[] args) {
        new App();
    }
}
