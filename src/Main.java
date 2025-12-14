import controlador.ControladorSimulacion;
import modelo.DispensadorOxigeno;
import vista.VentanaInicial;
import vista.VentanaSimulacion;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(VentanaInicial::new);
    }
}
