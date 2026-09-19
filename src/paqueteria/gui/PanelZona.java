package paqueteria.gui;

import paqueteria.concurrencia.ZonaLogistica;
import paqueteria.estructuras.ListaEnlazada;
import paqueteria.modelo.Paquete;
import paqueteria.modelo.Prioridad;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class PanelZona extends JPanel {

    private final ZonaLogistica zona;
    private final JProgressBar barra = new JProgressBar();
    private final JLabel contenido = new JLabel();
    private final JLabel titulo;

    public PanelZona(ZonaLogistica zona) {
        this.zona = zona;
        setLayout(new BorderLayout(0, 4));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD0D6D4)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        setBackground(Color.WHITE);

        titulo = new JLabel(zona.getNombre());
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 12f));

        JPanel cabecera = new JPanel(new BorderLayout(0, 3));
        cabecera.setOpaque(false);
        cabecera.add(titulo, BorderLayout.NORTH);

        if (zona.getCapacidad() != ZonaLogistica.SIN_LIMITE) {
            barra.setMaximum(zona.getCapacidad());
            barra.setStringPainted(true);
            barra.setPreferredSize(new Dimension(0, 16));
            cabecera.add(barra, BorderLayout.CENTER);
        }
        add(cabecera, BorderLayout.NORTH);

        contenido.setVerticalAlignment(JLabel.TOP);
        JScrollPane scroll = new JScrollPane(contenido,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);
    }

    public void actualizar() {
        ListaEnlazada<Paquete> paquetes = zona.copiaContenido();

        if (zona.getCapacidad() == ZonaLogistica.SIN_LIMITE) {
            titulo.setText(zona.getNombre() + "   " + paquetes.tamano());
        } else {
            barra.setValue(paquetes.tamano());
            barra.setString(paquetes.tamano() + " / " + zona.getCapacidad());
        }
        contenido.setText(comoHtml(paquetes));
    }

    private String comoHtml(ListaEnlazada<Paquete> paquetes) {
        if (paquetes.estaVacia()) {
            return "<html><span style='color:#9AA3A0;font-size:10px'>vacia</span></html>";
        }
        StringBuilder html = new StringBuilder("<html><body style='font-family:monospaced;font-size:10px'>");
        for (Paquete paquete : paquetes) {
            html.append("<span style='color:").append(color(paquete.getPrioridad())).append("'>&#9632;</span> ")
                .append(paquete.getCodigo()).append("<br>");
        }
        return html.append("</body></html>").toString();
    }

    static String color(Prioridad prioridad) {
        switch (prioridad) {
            case URGENTE:
                return "#C62828";
            case ALTA:
                return "#EF6C00";
            case NORMAL:
                return "#B28704";
            default:
                return "#2E7D32";
        }
    }
}
