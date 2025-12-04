import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class TestApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JPanel content = new JPanel(new BorderLayout());
        content.add(new JLabel("Label"), BorderLayout.NORTH);
        content.add(new JButton("Button"), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(content);
        EventQueue.invokeLater(
                () -> {
                    frame.pack();
                    frame.setVisible(true);
                });
    }
}
