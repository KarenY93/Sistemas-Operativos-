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

        dispensador.solicitarRecarga(astronauta);

        mostrarFinalizacion(astronauta);
    }

    public boolean estaOcupado() {
        return dispensador.estaOcupado();
    }

    public int astronautasEnEspera() {
        return dispensador.getCantidadEnEspera();
    }

    /* ===== Métodos de apoyo (logging / vista) ===== */

    private void mostrarSolicitud(Astronauta astronauta) {
        System.out.println("🟡 [DISPENSADOR] "
                + astronauta.getNombre()
                + " solicita oxígeno | Estado: "
                + astronauta.getEstado());
    }

    private void mostrarFinalizacion(Astronauta astronauta) {
        System.out.println("🟢 [DISPENSADOR] "
                + astronauta.getNombre()
                + " finalizó recarga | O₂ = "
                + astronauta.getOxigeno() + "%");
    }
}
