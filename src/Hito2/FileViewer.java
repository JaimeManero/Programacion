package Hito2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class FileViewer extends JFrame {
    private JComboBox<String> comboBox;
    private JTextArea textArea;

    public FileViewer() {
        setTitle("File Viewer:Test");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        comboBox = new JComboBox<>(new String[]{"python.txt", "c.txt", "java.txt"});
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarArchivo((String) comboBox.getSelectedItem());
            }
        });

        JButton clearButton = new JButton("Borrar");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });

        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtonPanel.add(comboBox);
        leftButtonPanel.add(clearButton);

        textArea = new JTextArea();
        textArea.setEditable(false);

        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        rightPanel.add(closeButton, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftButtonPanel, rightPanel);
        splitPane.setResizeWeight(0.5);

        add(splitPane);
    }

    private void cargarArchivo(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea);
                sb.append("\n");
            }
            textArea.setText(sb.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el archivo " );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FileViewer viewer = new FileViewer();
                viewer.setVisible(true);
            }
        });
    }
}