package controlador;

import modelo.Astronauta;
import vista.VentanaSimulacion;

public class ControladorAstronauta implements Runnable {

    /* ===== Referencias MVC ===== */
    private final Astronauta astronauta; // Modelo
    private final ControladorDispensadorOxigeno controladorDispensador; // Controlador compartido
    private final VentanaSimulacion vista; // Vista

    /* ===== Parámetros de ejecución ===== */
    private final int delayMs; // Ritmo del ciclo

    /* ===== Estado del hilo ===== */
    private boolean activo;
    private boolean esperandoDispensador;

    public ControladorAstronauta(Astronauta astronauta,
                                 ControladorDispensadorOxigeno controladorDispensador,
                                 VentanaSimulacion vista,
                                 int delayMs) {
        this.astronauta = astronauta;
        this.controladorDispensador = controladorDispensador;
        this.vista = vista;
        this.delayMs = delayMs;
        this.activo = true;
        this.esperandoDispensador = false;
    }

    @Override
    public void run() {
        try {
            while (activo && !astronauta.haFalladoLaMision()) {

                /* ===== Ciclo de vida ===== */
                // El astronauta siempre consume oxígeno
                astronauta.consumirOxigeno();
                vista.actualizarAstronauta(astronauta);

                /* ===== Solicitud de recarga concurrente ===== */
                if (astronauta.necesitaRecarga() && !esperandoDispensador) {
                    esperandoDispensador = true;

                    vista.mostrarIntento(astronauta.getNombre());
                    vista.agregarACola(astronauta.getNombre());

                    // Hilo separado para no bloquear el ciclo principal
                    new Thread(() -> {
                        try {
                            controladorDispensador.solicitarRecarga(astronauta);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            vista.removerDeCola(astronauta.getNombre());
                            esperandoDispensador = false;
                        }
                    }, "Req-" + astronauta.getNombre()).start();
                }

                /* ===== Post-recuperación ===== */
                astronauta.completarRecuperacion();

                // Control de velocidad de simulación
                Thread.sleep(delayMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Limpieza visual en caso de finalización abrupta
            vista.removerDeCola(astronauta.getNombre());
        }
    }

    /* ===== Detención controlada del hilo ===== */
    public void detener() {
        activo = false;
    }
}
