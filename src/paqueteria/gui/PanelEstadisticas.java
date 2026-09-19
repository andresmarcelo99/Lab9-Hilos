package paqueteria.gui;

import paqueteria.concurrencia.Estadisticas;
import paqueteria.hilos.HiloRepartidor;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Frame;

public class PanelEstadisticas extends JDialog {

    private final JLabel contenido = new JLabel();

    public PanelEstadisticas(Frame padre) {
        super(padre, "Estadisticas", false);
        contenido.setVerticalAlignment(JLabel.TOP);
        contenido.setOpaque(true);
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        setContentPane(contenido);
        setSize(340, 340);
        setLocationRelativeTo(padre);
    }

    public void actualizar(Estadisticas estadisticas, HiloRepartidor[] repartidores) {
        StringBuilder html = new StringBuilder("<html><body style='font-family:monospaced;font-size:12px'>");
        html.append("<b>ESTADISTICAS</b><br><br>");
        fila(html, "Paquetes generados", estadisticas.getGenerados());
        fila(html, "Entregados", estadisticas.getEntregados());
        fila(html, "Devueltos", estadisticas.getDevueltos());
        fila(html, "En proceso", estadisticas.enProceso());
        html.append("<br>Tiempo promedio: <b>")
            .append(String.format("%.1f", estadisticas.tiempoPromedioSegundos()))
            .append(" s</b><br><br>");

        int[] porRepartidor = estadisticas.copiaEntregasPorRepartidor();
        for (int i = 0; i < porRepartidor.length; i++) {
            String ruta = i < repartidores.length ? repartidores[i].getRuta() : "";
            fila(html, "Repartidor-" + (i + 1) + " " + ruta, porRepartidor[i]);
        }
        contenido.setText(html.append("</body></html>").toString());
    }

    private void fila(StringBuilder html, String etiqueta, int valor) {
        html.append(etiqueta).append(": <b>").append(valor).append("</b><br>");
    }
}
