package vista;

import javax.swing.*;
import java.awt.*;

public class VentanaInicial extends JFrame {

    private final Font MAIN_FONT = new Font("Century Gothic", Font.BOLD, 32);
    private final Color BTN_COLOR = new Color(80, 150, 255);

    public VentanaInicial() {
        super("Simulación - Bienvenido");
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        getContentPane().setBackground(Color.WHITE);

        // Layout central
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 20, 20, 20);
        c.gridx = 0;
        c.gridy = 0;

        JLabel title = new JLabel("Simulador de Dispensador de Oxígeno");
        title.setFont(MAIN_FONT.deriveFont(Font.BOLD, 48f));
        title.setForeground(Color.DARK_GRAY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, c);

        c.gridy = 1;
        JLabel subtitle = new JLabel("Proyecto de Sistemas Operativos");
        subtitle.setFont(MAIN_FONT.deriveFont(Font.BOLD, 22f));
        subtitle.setForeground(Color.GRAY);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(subtitle, c);

        c.gridy = 2;
        JButton btnComenzar = new JButton("COMENZAR");
        btnComenzar.setFont(MAIN_FONT.deriveFont(Font.BOLD, 28f));
        btnComenzar.setBackground(BTN_COLOR);
        btnComenzar.setForeground(Color.WHITE);
        btnComenzar.setFocusPainted(false);
        btnComenzar.setPreferredSize(new Dimension(300, 80));
        panel.add(btnComenzar, c);


        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);


        btnComenzar.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new VentanaSimulacion());
            dispose();
        });


        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        SwingUtilities.invokeLater(() -> setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH));
    }
}
