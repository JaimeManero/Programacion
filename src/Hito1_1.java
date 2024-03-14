import javax.swing.*;
import java.awt.*;

public class Hito1_1 {
    public static void main(String[] args) {
        JFrame frame = new JFrame("BorderLayout Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        Container container = frame.getContentPane();
        container.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new FlowLayout());

        JCheckBox checkBox1 = new JCheckBox("Katniss");
        JCheckBox checkBox2 = new JCheckBox("Peeta");
        northPanel.add(checkBox1);
        northPanel.add(checkBox2);

        container.add(northPanel, BorderLayout.NORTH);

        JPanel eastPanel = new JPanel();
        eastPanel.setPreferredSize(new Dimension(250, 0));

        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));

        JRadioButton radioButton1 = new JRadioButton("OPT 1");
        JRadioButton radioButton2 = new JRadioButton("OPT 2");
        JRadioButton radioButton3 = new JRadioButton("OPT 3");

        ButtonGroup group = new ButtonGroup();
        group.add(radioButton1);
        group.add(radioButton2);
        group.add(radioButton3);

        eastPanel.add(radioButton1);
        eastPanel.add(radioButton2);
        eastPanel.add(radioButton3);

        radioButton1.setSelected(true);

        container.add(eastPanel, BorderLayout.EAST);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
        southPanel.setPreferredSize(new Dimension(0, 50));

        southPanel.add(new JButton("But 1"));
        southPanel.add(new JButton("But 2"));

        container.add(southPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 2));

        ImageIcon icon = new ImageIcon("img/sergi.jpg");

        JLabel label1 = new JLabel(icon);
        JLabel label2 = new JLabel(icon);
        JLabel label3 = new JLabel(icon);
        JLabel label4 = new JLabel(icon);

        centerPanel.add(label1);
        centerPanel.add(label2);
        centerPanel.add(label3);
        centerPanel.add(label4);

        container.add(centerPanel, BorderLayout.CENTER);

        frame.setVisible(true);

    }
}
