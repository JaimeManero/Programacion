package Hito3;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class Hito3 extends JFrame implements ActionListener {
    private JPanel panel;
    private JLabel etiquetaImagen;
    private JComboBox combo;
    private JCheckBox checkBox;
    private JTextField campoTexto;
    private JButton botonGuardar;
    private ImageIcon[] ImageIcon;

    public Hito3(){
        super("Cargar Imagen");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(900,500));

        this.setLayout(null);
        panel = (JPanel) this.getContentPane();
        addWindowListener( new CerrarMensaje() );

        JPasswordField campoContraseña = new JPasswordField();
        Object[] mensaje = {"Introducir Contraseña:", campoContraseña};

        int opcion = JOptionPane.showConfirmDialog(null, mensaje, "Solicitud de Contraseña", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            char[] contraseña = campoContraseña.getPassword();
            String contraseñaIngresada = new String(contraseña);

            if (contraseñaIngresada.equals("damocles")) {
                System.out.println("Contraseña Ingresada: " + contraseñaIngresada);
            }
            else{
                System.out.println("Contraseña Incorrecta");
                System.exit(0);
            }
        }
        else {
            System.out.println("Operación Cancelada.");
            System.exit(0);
        }

        String [] imagenes = {"casa.jpg", "monte.jpg", "tienda.jpg"};
        combo = new JComboBox<>(imagenes);

        combo.setSelectedIndex(0);
        combo.setBounds(20, 20, 200, 45);

        panel.add(combo);

        ImageIcon = new ImageIcon[]{
                new ImageIcon(new ImageIcon("ImagenesHito3/casa.jpg").getImage().getScaledInstance(250, 250, Image.SCALE_DEFAULT)),
                new ImageIcon(new ImageIcon("ImagenesHito3/monte.jpg").getImage().getScaledInstance(250, 250, Image.SCALE_DEFAULT)),
                new ImageIcon(new ImageIcon("ImagenesHito3/tienda.jpg").getImage().getScaledInstance(250, 250, Image.SCALE_DEFAULT))
        };

        etiquetaImagen = new JLabel(imagenes[0]);
        etiquetaImagen.setBounds(20, 100, 200, 200);
        panel.add(etiquetaImagen);

        ComboListener listenerCombo = new ComboListener(combo, panel, etiquetaImagen, ImageIcon);
        combo.addActionListener(listenerCombo);

        checkBox = new JCheckBox("Guardar tu comentario", true);
        checkBox.setBounds(50, 300, 200,50);
        checkBox.isSelected();
        panel.add(checkBox);

        campoTexto = new JTextField(10);
        campoTexto.setBounds(310, 300, 250, 40);
        panel.add(campoTexto);

        botonGuardar = new JButton("GUARDAR");
        botonGuardar.setBounds(200, 400, 100, 40);
        panel.add(botonGuardar);

        botonGuardar.addActionListener(this);

        this.setVisible(true);
        this.pack();
    }
    public class CerrarMensaje extends WindowAdapter {
        public void windowClosing( WindowEvent e ) {
            JOptionPane.showMessageDialog
                    (null, "Adiós","Entrada",JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new Hito3();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String nombreArchivo = "Buffer_Files/hito3.txt";
        String nombreImagen = etiquetaImagen.getName();

        if (checkBox.isSelected()){
            BufferedWriter bw;
            String nuevaLinea = "\n";
            try {
                bw = new BufferedWriter(new FileWriter(nombreArchivo, true));

                bw.append(nuevaLinea);
                bw.append(nombreImagen).append(" ").append(campoTexto.getText());

                if (bw!=null){
                    bw.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            JOptionPane.showMessageDialog
                    (null, "¡Tu comentario ha sido guardado exitosamente!","Entrada",JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog
                    (null, "¡Tu comentario no será guardado!","Alerta",JOptionPane.WARNING_MESSAGE);
        }
    }

}
