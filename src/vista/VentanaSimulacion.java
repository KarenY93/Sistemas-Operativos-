package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class VentanaSimulacion extends JFrame {

    private final Font UI_FONT = new Font("Century Gothic", Font.BOLD, 14);
    private final Color BTN_COLOR = new Color(80, 150, 255);

    private final JTextArea areaLog;
    private final JButton btnIniciar;
    private final JButton btnDetener;
    private final JButton btnReset;
    private final JSpinner spinnerCantidad;
    private final JSlider sliderVelocidad;
    private final JLabel lblDispensador;
    private final DefaultListModel<String> colaModel;
    private final JList<String> listaCola;

    public VentanaSimulacion() {
        super("Simulador de Dispensador de Oxígeno");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(Color.WHITE);

        // Panel superior: controles
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panelControles.setBackground(Color.WHITE);

        JLabel lblAstr = new JLabel("Astronautas:");
        lblAstr.setFont(UI_FONT);
        panelControles.add(lblAstr);

        spinnerCantidad = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        spinnerCantidad.setFont(UI_FONT);
        panelControles.add(spinnerCantidad);

        JLabel lblVel = new JLabel("Velocidad:");
        lblVel.setFont(UI_FONT);
        panelControles.add(lblVel);

        sliderVelocidad = new JSlider(0, 2000, 1000); // ms de delay
        sliderVelocidad.setPreferredSize(new Dimension(150, 20));
        panelControles.add(sliderVelocidad);

        btnIniciar = new JButton("Iniciar");
        btnDetener = new JButton("Detener");
        btnReset = new JButton("Reset");

        styleButton(btnIniciar);
        styleButton(btnDetener);
        styleButton(btnReset);

        panelControles.add(btnIniciar);
        panelControles.add(btnDetener);
        panelControles.add(btnReset);

        // Panel central: cola y log
        JPanel centro = new JPanel(new GridLayout(1, 2, 8, 8));
        centro.setBackground(Color.WHITE);

        // Cola de espera
        colaModel = new DefaultListModel<>();
        listaCola = new JList<>(colaModel);
        listaCola.setFont(UI_FONT);
        JPanel panelCola = new JPanel(new BorderLayout());
        panelCola.setBorder(BorderFactory.createTitledBorder("Cola de espera"));
        panelCola.setBackground(Color.WHITE);
        panelCola.add(new JScrollPane(listaCola), BorderLayout.CENTER);

        // Log
        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(UI_FONT);
        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(BorderFactory.createTitledBorder("Log"));
        panelLog.setBackground(Color.WHITE);
        panelLog.add(new JScrollPane(areaLog), BorderLayout.CENTER);

        centro.add(panelCola);
        centro.add(panelLog);

        // Panel inferior: estado del dispensador
        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        inferior.setBackground(Color.WHITE);
        inferior.add(new JLabel("Dispensador:"));

        lblDispensador = new JLabel("LIBRE");
        lblDispensador.setOpaque(true);
        lblDispensador.setBackground(Color.GREEN);
        lblDispensador.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        lblDispensador.setFont(UI_FONT);
        inferior.add(lblDispensador);

        // Layout del frame
        getContentPane().setLayout(new BorderLayout(8, 8));
        getContentPane().add(panelControles, BorderLayout.NORTH);
        getContentPane().add(centro, BorderLayout.CENTER);
        getContentPane().add(inferior, BorderLayout.SOUTH);

        // Por defecto
        btnDetener.setEnabled(false);


        setVisible(true);
    }

    private void styleButton(JButton b) {
        b.setBackground(BTN_COLOR);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Century Gothic", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(110, 36));
    }

    //Métodos para que el controlador conecte listeners

    public void setStartListener(ActionListener l) {
        btnIniciar.addActionListener(l);
    }

    public void setStopListener(ActionListener l) {
        btnDetener.addActionListener(l);
    }

    public void setResetListener(ActionListener l) {
        btnReset.addActionListener(l);
    }

    // Métodos públicos para actualizar la vista

    public int getCantidadAstronautas() {
        return (Integer) spinnerCantidad.getValue();
    }

    // velocidad en ms (delay)
    public int getVelocidadMs() {
        return sliderVelocidad.getValue();
    }

    public void mostrarIntento(String nombre) {
        SwingUtilities.invokeLater(() ->
                areaLog.append("[ESPERA] " + nombre + " espera...\n")
        );
    }

    public void mostrarAcceso(String nombre) {
        SwingUtilities.invokeLater(() ->
                areaLog.append("[ACCESO] " + nombre + " está usando el dispensador.\n")
        );
    }

    public void mostrarSalida(String nombre) {
        SwingUtilities.invokeLater(() ->
                areaLog.append("[SALIDA] " + nombre + " terminó.\n")
        );
    }

    public void agregarACola(String nombre) {
        SwingUtilities.invokeLater(() -> colaModel.addElement(nombre));
    }

    public void removerDeCola(String nombre) {
        SwingUtilities.invokeLater(() -> colaModel.removeElement(nombre));
    }

    public void actualizarEstadoDispensador(boolean ocupado) {
        SwingUtilities.invokeLater(() -> {
            lblDispensador.setText(ocupado ? "OCUPADO" : "LIBRE");
            lblDispensador.setBackground(ocupado ? Color.RED : Color.GREEN);
        });
    }


    public void limpiarLog() {
        SwingUtilities.invokeLater(() -> areaLog.setText(""));
    }

    public void limpiarCola() {
        SwingUtilities.invokeLater(() -> colaModel.clear());
    }

    public void resetearDispensador() {
        SwingUtilities.invokeLater(() -> {
            lblDispensador.setText("LIBRE");
            lblDispensador.setBackground(Color.GREEN);
        });
    }


    public void habilitarControles(boolean iniciarEnabled) {
        SwingUtilities.invokeLater(() -> {
            btnIniciar.setEnabled(iniciarEnabled);
            btnDetener.setEnabled(!iniciarEnabled);
            spinnerCantidad.setEnabled(iniciarEnabled);
        });
    }

}
