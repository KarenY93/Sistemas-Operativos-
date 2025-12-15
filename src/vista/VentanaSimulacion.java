package vista;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Astronauta;

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

    // Nuevos componentes para la tabla
    private final JTable tablaAstronautas;
    private final DefaultTableModel modeloAstronautas;
    private final Map<String, Object[]> datosAstronautas; // Para mantener datos sin reordenar

    public VentanaSimulacion() {
        super("Simulador de Dispensador de Oxígeno");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(Color.WHITE);

        datosAstronautas = new HashMap<>();

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

        // Panel central: tabla, cola y log (3 columnas)
        JPanel centro = new JPanel(new GridLayout(1, 3, 10, 10));
        centro.setBackground(Color.WHITE);

        // 1. Tabla de astronautas con columnas más anchas
        String[] columnas = {"Astronauta", "Oxígeno (%)", "Estado", "Fatiga (%)", "Prioridad"};
        modeloAstronautas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };

        tablaAstronautas = new JTable(modeloAstronautas);
        tablaAstronautas.setFont(UI_FONT);
        tablaAstronautas.setRowHeight(35); // Más alto para mejor visualización

        // Configurar anchos de columna
        tablaAstronautas.getColumnModel().getColumn(0).setPreferredWidth(120); // Astronauta
        tablaAstronautas.getColumnModel().getColumn(1).setPreferredWidth(100); // Oxígeno (%)
        tablaAstronautas.getColumnModel().getColumn(2).setPreferredWidth(130); // Estado
        tablaAstronautas.getColumnModel().getColumn(3).setPreferredWidth(100); // Fatiga (%)
        tablaAstronautas.getColumnModel().getColumn(4).setPreferredWidth(80);  // Prioridad

        tablaAstronautas.getTableHeader().setFont(UI_FONT.deriveFont(Font.BOLD, 14));
        tablaAstronautas.getTableHeader().setBackground(new Color(240, 240, 240));
        tablaAstronautas.getTableHeader().setForeground(Color.DARK_GRAY);

        // Configurar renderizador para colores según nivel de oxígeno
        tablaAstronautas.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                // Color de fondo alternado para filas
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                }

                // Fuente más grande para mejor legibilidad
                ((JLabel) c).setFont(UI_FONT.deriveFont(Font.PLAIN, 14));

                // Colores según los valores
                switch (column) {
                    case 1 -> {
                        // Columna de Oxígeno
                        try {
                            String oxStr = value.toString().replace("%", "").trim();
                            int oxigeno = Integer.parseInt(oxStr);
                            
                            // Hacer el texto más visible
                            if (oxigeno < 10) {
                                c.setBackground(new Color(255, 220, 220)); // Rojo claro
                                c.setForeground(Color.RED);
                                ((JLabel) c).setFont(UI_FONT.deriveFont(Font.BOLD, 15));
                            } else if (oxigeno < 30) {
                                c.setBackground(new Color(255, 255, 220)); // Amarillo claro
                                c.setForeground(new Color(200, 100, 0)); // Naranja oscuro
                                ((JLabel) c).setFont(UI_FONT.deriveFont(Font.BOLD, 14));
                            } else {
                                c.setForeground(new Color(0, 150, 0)); // Verde más brillante
                            }
                        } catch (NumberFormatException e) {
                            // No hacer nada si no es número
                        }
                    }
                    case 2 -> {
                        // Columna de Estado
                        String estado = value.toString();
                        switch (estado.toUpperCase()) {
                            case "EMERGENCIA" -> {
                                c.setForeground(Color.RED);
                                ((JLabel) c).setFont(UI_FONT.deriveFont(Font.BOLD, 14));
                        }
                            case "RECUPERACION" -> c.setForeground(new Color(0, 120, 220));
                            case "TERMINADO" -> c.setForeground(Color.DARK_GRAY);
                            case "RECARGANDO" -> {
                                c.setForeground(new Color(0, 180, 0));
                                ((JLabel) c).setFont(UI_FONT.deriveFont(Font.BOLD, 14));
                        }
                            default -> c.setForeground(Color.BLACK);
                        }
                    }
                    case 3 -> {
                        // Columna de Fatiga
                        try {
                            String fatStr = value.toString().replace("%", "").trim();
                            int fatiga = Integer.parseInt(fatStr);
                            
                            if (fatiga > 80) {
                                c.setForeground(new Color(150, 75, 0)); // Marrón
                                ((JLabel) c).setFont(UI_FONT.deriveFont(Font.BOLD, 14));
                            } else if (fatiga > 50) {
                                c.setForeground(new Color(200, 100, 0)); // Naranja
                            }
                        } catch (NumberFormatException e) {
                            // Ignorar
                        }
                    }
                    case 4 -> {
                        // Columna de Prioridad
                        try {
                            int prioridad = Integer.parseInt(value.toString());
                            switch (prioridad) {
                                case 4 -> {
                                    c.setForeground(Color.RED);
                                    ((JLabel) c).setFont(UI_FONT.deriveFont(Font.BOLD, 14));
                                }
                                case 3 -> c.setForeground(Color.ORANGE);
                                case 2 -> c.setForeground(new Color(255, 180, 0));
                                default -> c.setForeground(Color.BLACK);
                            }
                        } catch (NumberFormatException e) {
                            // Ignorar
                        }
                    }
                    default -> c.setForeground(Color.BLACK);
                }

                // Centrar texto en todas las celdas excepto la primera
                if (column == 0) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                } else {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                }

                return c;
            }
        });

        // Centrar encabezados
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBackground(new Color(70, 130, 180));
                label.setForeground(Color.WHITE);
                label.setFont(UI_FONT.deriveFont(Font.BOLD, 14));
                label.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
                return label;
            }
        };

        for (int i = 0; i < tablaAstronautas.getColumnCount(); i++) {
            tablaAstronautas.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        JScrollPane scrollTabla = new JScrollPane(tablaAstronautas);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "ESTADO DE ASTRONAUTAS",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Century Gothic", Font.BOLD, 16),
                new Color(70, 130, 180)
        ));
        scrollTabla.getViewport().setBackground(Color.WHITE);
        centro.add(scrollTabla);

        // 2. Cola de espera
        colaModel = new DefaultListModel<>();
        listaCola = new JList<>(colaModel);
        listaCola.setFont(UI_FONT);
        listaCola.setSelectionBackground(new Color(200, 220, 255));
        JPanel panelCola = new JPanel(new BorderLayout());
        panelCola.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 100), 2),
                "COLA DE ESPERA",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Century Gothic", Font.BOLD, 16),
                new Color(100, 150, 100)
        ));
        panelCola.setBackground(Color.WHITE);
        panelCola.add(new JScrollPane(listaCola), BorderLayout.CENTER);
        centro.add(panelCola);

        // 3. Log
        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(UI_FONT);
        areaLog.setBackground(new Color(250, 250, 250));
        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 100, 100), 2),
                "REGISTRO DE EVENTOS",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Century Gothic", Font.BOLD, 16),
                new Color(150, 100, 100)
        ));
        panelLog.setBackground(Color.WHITE);
        panelLog.add(new JScrollPane(areaLog), BorderLayout.CENTER);
        centro.add(panelLog);

        // Panel inferior: estado del dispensador
        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        inferior.setBackground(Color.WHITE);

        JLabel lblEstado = new JLabel("Estado del Dispensador:");
        lblEstado.setFont(new Font("Century Gothic", Font.BOLD, 14));
        inferior.add(lblEstado);

        lblDispensador = new JLabel("LIBRE");
        lblDispensador.setOpaque(true);
        lblDispensador.setBackground(Color.GREEN);
        lblDispensador.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        lblDispensador.setFont(new Font("Century Gothic", Font.BOLD, 18));
        lblDispensador.setForeground(Color.BLACK);
        inferior.add(lblDispensador);

        // Espaciador
        inferior.add(Box.createHorizontalStrut(50));

        // Panel de estadísticas
        JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelStats.setBackground(Color.WHITE);

        inferior.add(panelStats);

        // Layout del frame
        getContentPane().setLayout(new BorderLayout(15, 15));
        getContentPane().add(panelControles, BorderLayout.NORTH);
        getContentPane().add(centro, BorderLayout.CENTER);
        getContentPane().add(inferior, BorderLayout.SOUTH);

        // Por defecto
        btnDetener.setEnabled(false);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void styleButton(JButton b) {
        setButtonBaseColor(b, BTN_COLOR);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Century Gothic", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 140, 255), 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (b.isEnabled()) {
                    b.setBackground(new Color(100, 170, 255));
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Color base = (Color) b.getClientProperty("baseColor");
                if (base == null) {
                    base = BTN_COLOR;
                }
                b.setBackground(base);
            }
        });
    }

    private void setButtonBaseColor(JButton button, Color color) {
        button.putClientProperty("baseColor", color);
        button.setBackground(color);
    }

    // ... (métodos setStartListener, setStopListener, setResetListener igual)

    public int getCantidadAstronautas() {
        return (Integer) spinnerCantidad.getValue();
    }

    public int getVelocidadMs() {
        return sliderVelocidad.getValue();
    }

    public void mostrarIntento(String nombre) {
        SwingUtilities.invokeLater(() -> {
            areaLog.append("[ESPERA] " + nombre + " espera...\n");
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        });
    }

    public void mostrarAcceso(String nombre) {
        SwingUtilities.invokeLater(() -> {
            areaLog.append("[ACCESO] " + nombre + " está usando el dispensador.\n");
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        });
    }

    public void mostrarSalida(String nombre) {
        SwingUtilities.invokeLater(() -> {
            areaLog.append("[SALIDA] " + nombre + " terminó.\n");
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        });
    }

    public void agregarACola(String nombre) {
        SwingUtilities.invokeLater(() -> {
            colaModel.addElement(nombre);
            listaCola.setSelectedIndex(colaModel.size() - 1);
            listaCola.ensureIndexIsVisible(colaModel.size() - 1);
        });
    }

    public void removerDeCola(String nombre) {
        SwingUtilities.invokeLater(() -> {
            int index = colaModel.indexOf(nombre);
            if (index != -1) {
                colaModel.remove(index);
            }
        });
    }

    public void actualizarEstadoDispensador(boolean ocupado) {
        SwingUtilities.invokeLater(() -> {
            if (ocupado) {
                lblDispensador.setText("OCUPADO");
                lblDispensador.setBackground(new Color(255, 80, 80));
                lblDispensador.setForeground(Color.WHITE);
            } else {
                lblDispensador.setText("LIBRE");
                lblDispensador.setBackground(new Color(80, 220, 80));
                lblDispensador.setForeground(Color.BLACK);
            }
        });
    }

    // Métodos para la tabla de astronautas - SIN REORDENAMIENTO AUTOMÁTICO
    public void setStartListener(ActionListener l) {
        btnIniciar.addActionListener(l);
    }

    public void setStopListener(ActionListener l) {
        btnDetener.addActionListener(l);
    }

    public void setResetListener(ActionListener l) {
        btnReset.addActionListener(l);
    }

    public void actualizarEstadoAstronauta(String nombre, int oxigeno, String estado, int fatiga, int prioridad) {
        SwingUtilities.invokeLater(() -> {
            // Guardar los datos en el mapa (mantiene el orden de inserción)
            Object[] filaDatos = {nombre, oxigeno + "%", estado, fatiga + "%", prioridad};
            datosAstronautas.put(nombre, filaDatos);

            // Reconstruir la tabla manteniendo el orden original (por número de astronauta)
            modeloAstronautas.setRowCount(0);

            // Ordenar por nombre (Astronauta 1, Astronauta 2, etc.)
            datosAstronautas.entrySet().stream()
                    .sorted((e1, e2) -> {
                        // Extraer números de los nombres
                        String num1 = e1.getKey().replaceAll("\\D+", "");
                        String num2 = e2.getKey().replaceAll("\\D+", "");
                        try {
                            int n1 = Integer.parseInt(num1.isEmpty() ? "0" : num1);
                            int n2 = Integer.parseInt(num2.isEmpty() ? "0" : num2);
                            return Integer.compare(n1, n2);
                        } catch (NumberFormatException ex) {
                            return e1.getKey().compareTo(e2.getKey());
                        }
                    })
                    .forEach(entry -> modeloAstronautas.addRow(entry.getValue()));
        });
    }

    public void eliminarAstronauta(String nombre) {
        SwingUtilities.invokeLater(() -> {
            datosAstronautas.remove(nombre);

            // Reconstruir tabla
            modeloAstronautas.setRowCount(0);
            datosAstronautas.entrySet().stream()
                    .sorted((e1, e2) -> {
                        String num1 = e1.getKey().replaceAll("\\D+", "");
                        String num2 = e2.getKey().replaceAll("\\D+", "");
                        try {
                            int n1 = Integer.parseInt(num1.isEmpty() ? "0" : num1);
                            int n2 = Integer.parseInt(num2.isEmpty() ? "0" : num2);
                            return Integer.compare(n1, n2);
                        } catch (NumberFormatException ex) {
                            return e1.getKey().compareTo(e2.getKey());
                        }
                    })
                    .forEach(entry -> modeloAstronautas.addRow(entry.getValue()));
        });
    }

    public void limpiarLog() {
        SwingUtilities.invokeLater(() -> areaLog.setText(""));
    }

    public void limpiarCola() {
        SwingUtilities.invokeLater(() -> colaModel.clear());
    }

    public void limpiarTablaAstronautas() {
        SwingUtilities.invokeLater(() -> {
            datosAstronautas.clear();
            modeloAstronautas.setRowCount(0);
        });
    }

    public void resetearDispensador() {
        SwingUtilities.invokeLater(() -> {
            lblDispensador.setText("LIBRE");
            lblDispensador.setBackground(new Color(80, 220, 80));
            lblDispensador.setForeground(Color.BLACK);
        });
    }

    public void habilitarControles(boolean iniciarEnabled) {
        SwingUtilities.invokeLater(() -> {
            btnIniciar.setEnabled(iniciarEnabled);
            btnDetener.setEnabled(!iniciarEnabled);
            spinnerCantidad.setEnabled(iniciarEnabled);
            sliderVelocidad.setEnabled(iniciarEnabled);

            if (iniciarEnabled) {
                setButtonBaseColor(btnIniciar, BTN_COLOR);
                btnIniciar.setForeground(Color.WHITE);
                setButtonBaseColor(btnDetener, Color.LIGHT_GRAY);
                btnDetener.setForeground(Color.DARK_GRAY);
            } else {
                setButtonBaseColor(btnIniciar, Color.LIGHT_GRAY);
                btnIniciar.setForeground(Color.DARK_GRAY);
                setButtonBaseColor(btnDetener, new Color(220, 80, 80));
                btnDetener.setForeground(Color.WHITE);
            }
        });
    }

    public void actualizarAstronauta(Astronauta astronauta) {
        if (astronauta == null) {
            return;
        }

        int prioridad = calcularPrioridad(astronauta);
        String estado = formatearEstado(astronauta.getEstado());
        actualizarEstadoAstronauta(
                astronauta.getNombre(),
                astronauta.getOxigeno(),
                estado,
                astronauta.getFatiga(),
                prioridad
        );
    }

    private int calcularPrioridad(Astronauta astronauta) {
        if (!astronauta.isActivo() || astronauta.haFalladoLaMision()) {
            return 0;
        }

        if (astronauta.estaEnEstadoCritico()) {
            return 4;
        }

        if (astronauta.necesitaRecarga()) {
            return 3;
        }

        if (astronauta.getFatiga() > 70) {
            return 2;
        }

        return 1;
    }

    private String formatearEstado(Astronauta.Estado estado) {
        if (estado == null) {
            return "DESCONOCIDO";
        }

        return switch (estado) {
            case EMERGENCIA -> "EMERGENCIA";
            case RECUPERACION -> "RECUPERACION";
            case TERMINADO -> "TERMINADO";
            default -> "NORMAL";
        };
    }
}