package controlador;

import modelo.Astronauta;
import modelo.DispensadorOxigeno;

/**
 * Controlador del dispensador de oxígeno.
 * Orquesta el acceso al recurso crítico.
 */
public class ControladorDispensadorOxigeno {

    private final DispensadorOxigeno dispensador;

    public ControladorDispensadorOxigeno(DispensadorOxigeno dispensador) {
        this.dispensador = dispensador;
    }

    /**
     * Solicita una recarga para un astronauta.
     * El bloqueo y la sincronización están en el MODELO.
     */
    public void solicitarRecarga(Astronauta astronauta) throws InterruptedException {

        mostrarSolicitud(astronauta);

        // 🔒 Entra a la sección crítica
        dispensador.solicitarRecarga(astronauta);

        // ⏱ Uso del recurso
        Thread.sleep(astronauta.estaEnEstadoCritico() ? 800 : 1500);
        astronauta.recargar();

        // 🔓 Sale de la sección crítica
        dispensador.liberar();

        mostrarFinalizacion(astronauta);
    }

    public boolean estaOcupado() {
        return dispensador.estaOcupado();
    }

    /* ===== Logging ===== */

    private void mostrarSolicitud(Astronauta astronauta) {
        System.out.println("🟡 [DISPENSADOR] "
                + astronauta.getNombre()
                + " solicita oxígeno");
    }

    private void mostrarFinalizacion(Astronauta astronauta) {
        System.out.println("🟢 [DISPENSADOR] "
                + astronauta.getNombre()
                + " terminó recarga | O₂ = "
                + astronauta.getOxigeno() + "%");
    }
}
