import controlador.ControladorDispensadorOxigeno;
import modelo.Astronauta;
import modelo.DispensadorOxigeno;

public class Main {
    public static void main(String[] args) {

        // prueba



        Astronauta a1 = new Astronauta("A1", 0);
        Astronauta a2 = new Astronauta("A2", 10);

        ControladorDispensadorOxigeno cd =
                new ControladorDispensadorOxigeno(new DispensadorOxigeno());

        new Thread(() -> {
            try {
                cd.solicitarRecarga(a1);
            } catch (InterruptedException e) {
            }
        }).start();

        new Thread(() -> {
            try {
                cd.solicitarRecarga(a2);
            } catch (InterruptedException e) {
            }
        }).start();

    }
}