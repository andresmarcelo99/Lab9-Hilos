package paqueteria.gui;

import paqueteria.Simulacion;
import paqueteria.concurrencia.Estadisticas;
import paqueteria.estructuras.ListaEnlazada;
import paqueteria.hilos.HiloClasificador;
import paqueteria.hilos.HiloEmpaquetador;
import paqueteria.hilos.HiloRepartidor;
import paqueteria.modelo.Paquete;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class VentanaPrincipal extends JFrame {

    private static final int REFRESCO_MS = 300;
    private static final Color FONDO = new Color(0xF2F5F4);
    private static final Color BORDE = new Color(0xD0D6D4);

    private final Simulacion simulacion = new Simulacion();

    private final JButton iniciar = new JButton("INICIAR");
    private final JButton pausar = new JButton("PAUSAR");
    private final JButton reanudar = new JButton("REANUDAR");
    private final JButton detener = new JButton("DETENER");
    private final JButton reiniciar = new JButton("REINICIAR");
    private final JLabel resumen = new JLabel();

    private final PanelZona panelRecepcion;
    private final PanelZona panelAlmacen;
    private final PanelZona panelClasificacion;
    private final PanelZona panelEntregados;
    private final PanelZona[] panelesExpedicion;

    private final JLabel[] etiquetasClasificadores = new JLabel[3];
    private final JLabel[] etiquetasEmpaquetadores = new JLabel[2];
    private final TarjetaRepartidor[] tarjetas;

    private final JTextArea log = new JTextArea();
    private String ultimoLog = "";

    public VentanaPrincipal() {
        super("Centro Logistico - Sistema de Paqueteria");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 880);
        setLocationRelativeTo(null);

        panelRecepcion = new PanelZona(simulacion.getRecepcion());
        panelAlmacen = new PanelZona(simulacion.getAlmacen());
        panelClasificacion = new PanelZona(simulacion.getClasificacion());
        panelEntregados = new PanelZona(simulacion.getEntregados());
        panelesExpedicion = new PanelZona[Simulacion.RUTAS.length];
        tarjetas = new TarjetaRepartidor[Simulacion.RUTAS.length];

        JPanel raiz = new JPanel(new BorderLayout(8, 8));
        raiz.setBackground(FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        raiz.add(barraControles(), BorderLayout.NORTH);
        raiz.add(cuerpo(), BorderLayout.CENTER);
        raiz.add(panelRegistro(), BorderLayout.SOUTH);
        setContentPane(raiz);

        conectarBotones();
        new Timer(REFRESCO_MS, e -> refrescar()).start();
        refrescar();
    }

    private JPanel barraControles() {
        JPanel botones = new JPanel();
        botones.setOpaque(false);
        botones.setLayout(new BoxLayout(botones, BoxLayout.X_AXIS));
        for (JButton boton : new JButton[]{iniciar, pausar, reanudar, detener, reiniciar}) {
            botones.add(boton);
            botones.add(Box.createHorizontalStrut(6));
        }
        botones.add(Box.createHorizontalGlue());
        resumen.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        botones.add(resumen);

        JLabel titulo = new JLabel("CENTRO LOGISTICO");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.add(titulo, BorderLayout.NORTH);
        barra.add(botones, BorderLayout.CENTER);
        return barra;
    }

    private JPanel cuerpo() {
        JPanel filaEtapas = new JPanel(new GridLayout(1, 3, 8, 0));
        filaEtapas.setOpaque(false);
        filaEtapas.add(panelRecepcion);
        filaEtapas.add(panelAlmacen);
        filaEtapas.add(panelClasificacion);

        JPanel filaExpedicion = new JPanel(new GridLayout(1, Simulacion.RUTAS.length + 1, 8, 0));
        filaExpedicion.setOpaque(false);
        for (int i = 0; i < Simulacion.RUTAS.length; i++) {
            panelesExpedicion[i] = new PanelZona(simulacion.getExpedicion(i));
            filaExpedicion.add(panelesExpedicion[i]);
        }
        filaExpedicion.add(panelEntregados);

        JPanel filaRepartidores = new JPanel(new GridLayout(1, Simulacion.RUTAS.length, 8, 0));
        filaRepartidores.setOpaque(false);
        for (int i = 0; i < tarjetas.length; i++) {
            tarjetas[i] = new TarjetaRepartidor();
            filaRepartidores.add(tarjetas[i]);
        }

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.add(filaEtapas);
        cuerpo.add(Box.createVerticalStrut(8));
        cuerpo.add(panelTrabajadores());
        cuerpo.add(Box.createVerticalStrut(8));
        cuerpo.add(filaExpedicion);
        cuerpo.add(Box.createVerticalStrut(8));
        cuerpo.add(filaRepartidores);
        return cuerpo;
    }

    private JPanel panelTrabajadores() {
        JPanel clasificadores = enmarcar("CLASIFICADORES", new GridLayout(1, 3, 8, 0));
        for (int i = 0; i < etiquetasClasificadores.length; i++) {
            etiquetasClasificadores[i] = etiquetaTrabajador();
            clasificadores.add(etiquetasClasificadores[i]);
        }

        JPanel empaquetadores = enmarcar("EMPAQUETADORES", new GridLayout(1, 2, 8, 0));
        for (int i = 0; i < etiquetasEmpaquetadores.length; i++) {
            etiquetasEmpaquetadores[i] = etiquetaTrabajador();
            empaquetadores.add(etiquetasEmpaquetadores[i]);
        }

        JPanel fila = new JPanel(new GridLayout(1, 2, 8, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        fila.add(clasificadores);
        fila.add(empaquetadores);
        return fila;
    }

    private JLabel etiquetaTrabajador() {
        JLabel etiqueta = new JLabel();
        etiqueta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        etiqueta.setHorizontalAlignment(JLabel.CENTER);
        return etiqueta;
    }

    private JPanel enmarcar(String titulo, java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDE), titulo),
                BorderFactory.createEmptyBorder(4, 8, 8, 8)));
        return panel;
    }

    private JScrollPane panelRegistro() {
        log.setEditable(false);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        JScrollPane scroll = new JScrollPane(log);
        scroll.setPreferredSize(new Dimension(0, 160));
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDE), "REGISTRO DEL SISTEMA"));
        return scroll;
    }

    private void conectarBotones() {
        // Las acciones de control pueden bloquear (join de los hilos), asi que
        // no pueden ejecutarse en el EDT: congelarian la ventana.
        iniciar.addActionListener(e -> enSegundoPlano(simulacion::iniciar));
        pausar.addActionListener(e -> enSegundoPlano(simulacion::pausar));
        reanudar.addActionListener(e -> enSegundoPlano(simulacion::reanudar));
        detener.addActionListener(e -> enSegundoPlano(simulacion::detener));
        reiniciar.addActionListener(e -> enSegundoPlano(simulacion::reiniciar));
    }

    private void enSegundoPlano(Runnable accion) {
        new Thread(accion, "control-gui").start();
    }

    private void refrescar() {
        boolean activa = simulacion.getControl().estaActiva();
        boolean pausada = simulacion.getControl().estaPausada();
        iniciar.setEnabled(!activa);
        pausar.setEnabled(activa && !pausada);
        reanudar.setEnabled(activa && pausada);
        detener.setEnabled(activa);

        panelRecepcion.actualizar();
        panelAlmacen.actualizar();
        panelClasificacion.actualizar();
        panelEntregados.actualizar();
        for (PanelZona panel : panelesExpedicion) {
            panel.actualizar();
        }

        refrescarTrabajadores();
        refrescarRepartidores();
        refrescarResumen();
        refrescarLog();
    }

    private void refrescarTrabajadores() {
        HiloClasificador[] clasificadores = simulacion.getClasificadores();
        for (int i = 0; i < etiquetasClasificadores.length; i++) {
            String texto = "Clasificador-" + (i + 1) + ": libre";
            if (i < clasificadores.length && clasificadores[i].getPaqueteActual() != null) {
                texto = "Clasificador-" + (i + 1) + " -> " + clasificadores[i].getPaqueteActual();
            }
            etiquetasClasificadores[i].setText(texto);
        }

        HiloEmpaquetador[] empaquetadores = simulacion.getEmpaquetadores();
        for (int i = 0; i < etiquetasEmpaquetadores.length; i++) {
            String texto = "Empaquetador-" + (i + 1) + ": libre";
            if (i < empaquetadores.length && empaquetadores[i].getPaqueteActual() != null) {
                texto = "Empaquetador-" + (i + 1) + " -> " + empaquetadores[i].getPaqueteActual();
            }
            etiquetasEmpaquetadores[i].setText(texto);
        }
    }

    private void refrescarRepartidores() {
        HiloRepartidor[] repartidores = simulacion.getRepartidores();
        for (int i = 0; i < tarjetas.length; i++) {
            if (i < repartidores.length) {
                tarjetas[i].actualizar(repartidores[i]);
            } else {
                tarjetas[i].vaciar(i + 1, Simulacion.RUTAS[i]);
            }
        }
    }

    private void refrescarResumen() {
        Estadisticas e = simulacion.getEstadisticas();
        resumen.setText(String.format("generados %d   entregados %d   devueltos %d   en proceso %d   medio %.1f s",
                e.getGenerados(), e.getEntregados(), e.getDevueltos(), e.enProceso(), e.tiempoPromedioSegundos()));
    }

    private void refrescarLog() {
        StringBuilder texto = new StringBuilder();
        for (String linea : simulacion.getRegistro().copiaLineas()) {
            texto.append(linea).append('\n');
        }
        String nuevo = texto.toString();
        if (!nuevo.equals(ultimoLog)) {
            ultimoLog = nuevo;
            log.setText(nuevo);
            log.setCaretPosition(log.getDocument().getLength());
        }
    }

    private static class TarjetaRepartidor extends JPanel {

        private final JLabel titulo = new JLabel();
        private final JLabel estado = new JLabel();
        private final JProgressBar carga = new JProgressBar();
        private final JLabel contenido = new JLabel();

        TarjetaRepartidor() {
            setLayout(new BorderLayout(0, 4));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDE),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)));

            titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 12f));
            estado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
            carga.setStringPainted(true);
            carga.setPreferredSize(new Dimension(0, 16));
            contenido.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
            contenido.setVerticalAlignment(JLabel.TOP);

            JPanel cabecera = new JPanel(new BorderLayout(0, 3));
            cabecera.setOpaque(false);
            cabecera.add(titulo, BorderLayout.NORTH);
            cabecera.add(estado, BorderLayout.CENTER);
            cabecera.add(carga, BorderLayout.SOUTH);

            add(cabecera, BorderLayout.NORTH);
            add(contenido, BorderLayout.CENTER);
        }

        void actualizar(HiloRepartidor repartidor) {
            titulo.setText("Repartidor-" + repartidor.getIdRepartidor() + "  " + repartidor.getRuta());
            estado.setText(repartidor.getEstadoRepartidor() + "  entregas: " + repartidor.getEntregasRealizadas());
            carga.setMaximum(repartidor.getCapacidad());
            carga.setValue(repartidor.ocupacionCarga());
            carga.setString(repartidor.ocupacionCarga() + " / " + repartidor.getCapacidad()
                    + (repartidor.ocupacionCarga() == repartidor.getCapacidad() ? "  LLENO" : ""));

            StringBuilder html = new StringBuilder("<html>");
            ListaEnlazada<Paquete> cargados = repartidor.copiaCarga();
            for (Paquete paquete : cargados) {
                html.append("<span style='color:").append(PanelZona.color(paquete.getPrioridad()))
                    .append("'>&#9632;</span> ").append(paquete.getCodigo()).append("<br>");
            }
            contenido.setText(html.append("</html>").toString());
        }

        void vaciar(int id, String ruta) {
            titulo.setText("Repartidor-" + id + "  " + ruta);
            estado.setText("DISPONIBLE  entregas: 0");
            carga.setValue(0);
            carga.setString("0 / 0");
            contenido.setText("");
        }
    }
}
