package Hito3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ComboListener implements ActionListener {

    private JComboBox combo;
    private JPanel panel;
    private ImageIcon[] iconosImagen;
    private JLabel etiquetaImagen;

    public ComboListener(JComboBox combo, JPanel panel, JLabel etiquetaImagen, ImageIcon[] iconosImagen) {
        this.combo = combo;
        this.panel = panel;
        this.etiquetaImagen = etiquetaImagen;
        this.iconosImagen = iconosImagen;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int indiceSeleccionado = combo.getSelectedIndex();

        ImageIcon iconoSeleccionado = iconosImagen[indiceSeleccionado];
        etiquetaImagen.setIcon(iconoSeleccionado);
        System.out.println(etiquetaImagen.getIcon());
        panel.add(etiquetaImagen);

    }
}

