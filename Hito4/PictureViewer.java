package Hito4;

import org.jdesktop.swingx.JXDatePicker;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureViewer extends JFrame {
    private JComboBox<Photographer> photographerComboBox;
    private JList<Picture> pictureList;
    private JLabel pictureLabel;
    private JXDatePicker datePicker;
    private DefaultListModel<Picture> pictureListModel;

    public PictureViewer() {
        setTitle("Picture Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 2));

        JPanel photographerPanel = new JPanel();
        photographerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel photographerLabel = new JLabel("Photographer:");
        photographerComboBox = new JComboBox<>();

        photographerPanel.add(photographerLabel);
        photographerPanel.add(photographerComboBox);

        JPanel datePickerPanel = new JPanel();
        datePickerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel dateLabel = new JLabel("Fotos antes de:");
        datePicker = new JXDatePicker();

        datePickerPanel.add(dateLabel);
        datePickerPanel.add(datePicker);

        pictureListModel = new DefaultListModel<>();
        pictureList = new JList<>(pictureListModel);
        pictureLabel = new JLabel();

        add(photographerPanel);
        add(datePickerPanel);
        add(new JScrollPane(pictureList));
        add(pictureLabel);

        loadPhotographers();

        photographerComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPictures();
            }
        });

        datePicker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPictures();
            }
        });

        pictureList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    displayPicture();
                }
            }
        });
    }

    private void loadPhotographers() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Photographers")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("PhotographerId");
                String name = resultSet.getString("Name");
                boolean awarded = resultSet.getBoolean("Awarded");
                photographerComboBox.addItem(new Photographer(id, name, awarded));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPictures() {
        pictureListModel.clear();
        Photographer selectedPhotographer = (Photographer) photographerComboBox.getSelectedItem();
        if (selectedPhotographer == null) return;

        Date selectedDate = datePicker.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateCondition = selectedDate != null ? " AND Date <= '" + dateFormat.format(selectedDate) + "'" : "";

        String query = "SELECT * FROM Pictures WHERE PhotographerId = " + selectedPhotographer.getPhotographerId() + dateCondition;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("PictureId");
                String title = resultSet.getString("Title");
                Date date = resultSet.getDate("Date");
                String file = resultSet.getString("File");
                int visits = resultSet.getInt("Visits");
                int photographerId = resultSet.getInt("PhotographerId");
                pictureListModel.addElement(new Picture(id, title, date, file, visits, photographerId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayPicture() {
        Picture selectedPicture = pictureList.getSelectedValue();
        if (selectedPicture == null) return;

        ImageIcon imageIcon = new ImageIcon(selectedPicture.getFile());
        pictureLabel.setIcon(imageIcon);
        incrementVisits(selectedPicture);
    }

    private void incrementVisits(Picture picture) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE Pictures SET Visits = Visits + 1 WHERE PictureId = ?")) {

            statement.setInt(1, picture.getPictureId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PictureViewer().setVisible(true);
            }
        });
    }
}
