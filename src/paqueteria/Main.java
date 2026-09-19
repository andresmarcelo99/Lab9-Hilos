package paqueteria;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.Font;

public class Main {

    public static void main(String[] args) {
        // Swing solo permite construir componentes desde el EDT.
        SwingUtilities.invokeLater(Main::crearVentana);
    }

    private static void crearVentana() {
        JFrame ventana = new JFrame("Centro Logistico - Sistema de Paqueteria");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(1200, 800);
        ventana.setLocationRelativeTo(null);

        JLabel marcador = new JLabel("Centro Logistico", SwingConstants.CENTER);
        marcador.setFont(marcador.getFont().deriveFont(Font.BOLD, 28f));
        ventana.add(marcador);

        ventana.setVisible(true);
    }
}
