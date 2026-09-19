package paqueteria;

import paqueteria.gui.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        // Swing solo permite construir componentes desde el EDT.
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorada) {
                // se queda el aspecto por defecto
            }
            new VentanaPrincipal().setVisible(true);
        });
    }
}
