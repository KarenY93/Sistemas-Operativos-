import javax.swing.SwingUtilities;
import vista.VentanaInicial;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(VentanaInicial::new);
    }
}
