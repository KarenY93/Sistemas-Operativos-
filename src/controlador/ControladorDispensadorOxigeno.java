package controlador;

import modelo.Astronauta;
import modelo.DispensadorOxigeno;
import vista.VentanaSimulacion;

public class ControladorDispensadorOxigeno {
    private final DispensadorOxigeno dispensador;
    private VentanaSimulacion vista;

    public ControladorDispensadorOxigeno(DispensadorOxigeno dispensador) {
        this.dispensador = dispensador;
    }

    // Método para establecer la vista (necesario para actualizaciones)
    public void setVista(VentanaSimulacion vista) {
        this.vista = vista;
    }

    public void solicitarRecarga(Astronauta astronauta) throws InterruptedException {
        mostrarSolicitud(astronauta);

        // Entra a la sección crítica (ESTE MÉTODO BLOQUEA SI ESTÁ OCUPADO)
        dispensador.solicitarRecarga(astronauta);

        // NOTIFICAR A LA VISTA QUE EL DISPENSADOR ESTÁ OCUPADO
        if (vista != null) {
            vista.mostrarAcceso(astronauta.getNombre());
            vista.actualizarEstadoDispensador(true);
        }

        // Uso del recurso
        Thread.sleep(astronauta.estaEnEstadoCritico() ? 800 : 1500);
        astronauta.recargar();

        // NOTIFICAR A LA VISTA ANTES DE LIBERAR
        if (vista != null) {
            vista.mostrarSalida(astronauta.getNombre());
        }

        // Sale de la sección crítica
        dispensador.liberar();

        // NOTIFICAR A LA VISTA QUE EL DISPENSADOR ESTÁ LIBRE
        if (vista != null) {
            vista.actualizarEstadoDispensador(false);
        }

        mostrarFinalizacion(astronauta);
    }

    public boolean estaOcupado() {
        return dispensador.estaOcupado();
    }

    /* ===== Logging ===== */
    private void mostrarSolicitud(Astronauta astronauta) {
        System.out.println("🟡 [DISPENSADOR] " + astronauta.getNombre() + " solicita oxígeno");
    }

    private void mostrarFinalizacion(Astronauta astronauta) {
        System.out.println("🟢 [DISPENSADOR] " + astronauta.getNombre() +
                " terminó recarga | O₂ = " + astronauta.getOxigeno() + "%");
    }
}