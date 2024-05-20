package Hito5;


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
import java.util.HashMap;

public class PictureViewer extends JFrame {
    private JComboBox<Photographer> photographerComboBox;
    private JList<Picture> pictureList;
    private JLabel pictureLabel;
    private JXDatePicker datePicker;
    private DefaultListModel<Picture> pictureListModel;
    private JButton awardButton;
    private JButton removeButton;

    public PictureViewer() {
        setTitle("Picture Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

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

        // Adding the buttons
        awardButton = new JButton("PREMIO");
        removeButton = new JButton("ELIMINAR");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(awardButton);
        buttonPanel.add(removeButton);

        add(photographerPanel);
        add(datePickerPanel);
        add(new JScrollPane(pictureList));
        add(pictureLabel);
        add(buttonPanel);

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

        awardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                awardPhotographers();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removePictures();
            }
        });
    }

    private void loadPhotographers() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Photographers")) {
            photographerComboBox.removeAllItems();
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

    private void awardPhotographers() {
        String nVisitsStr = JOptionPane.showInputDialog(this, "Ingrese el número mínimo de visitas:");
        if (nVisitsStr == null || nVisitsStr.isEmpty()) return;
        int nVisits = Integer.parseInt(nVisitsStr);

        HashMap<Integer, Integer> visitsMap = createVisitsMap();

        try (Connection connection = DatabaseConnection.getConnection()) {
            for (int photographerId : visitsMap.keySet()) {
                if (visitsMap.get(photographerId) >= nVisits) {
                    try (PreparedStatement statement = connection.prepareStatement("UPDATE Photographers SET Awarded = TRUE WHERE PhotographerId = ?")) {
                        statement.setInt(1, photographerId);
                        statement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removePictures() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Pictures WHERE Visits = 0")) {

            while (resultSet.next()) {
                int pictureId = resultSet.getInt("PictureId");
                int photographerId = resultSet.getInt("PhotographerId");
                String file = resultSet.getString("File");

                Photographer photographer = getPhotographerById(photographerId);
                if (photographer != null && !photographer.isAwarded()) {
                    int confirm = JOptionPane.showConfirmDialog(this, "¿Desea eliminar la foto: " + file + "?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Pictures WHERE PictureId = ?")) {
                            deleteStatement.setInt(1, pictureId);
                            deleteStatement.executeUpdate();
                        }
                    }
                }
            }

            // Eliminar fotógrafos sin fotos
            ResultSet photographersResultSet = statement.executeQuery("SELECT * FROM Photographers");
            while (photographersResultSet.next()) {
                int photographerId = photographersResultSet.getInt("PhotographerId");
                try (PreparedStatement countStatement = connection.prepareStatement("SELECT COUNT(*) FROM Pictures WHERE PhotographerId = ?")) {
                    countStatement.setInt(1, photographerId);
                    ResultSet countResultSet = countStatement.executeQuery();
                    if (countResultSet.next() && countResultSet.getInt(1) == 0) {
                        try (PreparedStatement deletePhotographerStatement = connection.prepareStatement("DELETE FROM Photographers WHERE PhotographerId = ?")) {
                            deletePhotographerStatement.setInt(1, photographerId);
                            deletePhotographerStatement.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Photographer getPhotographerById(int photographerId) {
        for (int i = 0; i < photographerComboBox.getItemCount(); i++) {
            Photographer photographer = photographerComboBox.getItemAt(i);
            if (photographer.getPhotographerId() == photographerId) {
                return photographer;
            }
        }
        return null;
    }

    private HashMap<Integer, Integer> createVisitsMap() {
        HashMap<Integer, Integer> visitsMap = new HashMap<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT PhotographerId, SUM(Visits) as TotalVisits FROM Pictures GROUP BY PhotographerId")) {

            while (resultSet.next()) {
                int photographerId = resultSet.getInt("PhotographerId");
                int totalVisits = resultSet.getInt("TotalVisits");
                visitsMap.put(photographerId, totalVisits);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return visitsMap;
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

