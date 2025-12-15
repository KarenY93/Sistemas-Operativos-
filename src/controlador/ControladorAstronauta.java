package controlador;

import modelo.Astronauta;
import vista.VentanaSimulacion;

public class ControladorAstronauta implements Runnable {

    private final Astronauta astronauta;
    private final ControladorDispensadorOxigeno controladorDispensador;
    private final VentanaSimulacion vista;
    private final int delayMs;

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

                // 1️⃣ Ciclo normal (SIEMPRE consume oxígeno)
                astronauta.consumirOxigeno();
                vista.actualizarAstronauta(astronauta);

                // 2️⃣ Solicitar dispensador SIN bloquear el ciclo
                if (astronauta.necesitaRecarga() && !esperandoDispensador) {
                    esperandoDispensador = true;

                    vista.mostrarIntento(astronauta.getNombre());
                    vista.agregarACola(astronauta.getNombre());

                    // 🔹 Solicitud concurrente (sin espera artificial)
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

                // 3️⃣ Recuperación post-recarga
                astronauta.completarRecuperacion();

                // 4️⃣ Ritmo del ciclo
                Thread.sleep(delayMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            vista.removerDeCola(astronauta.getNombre());
        }
    }

    public void detener() {
        activo = false;
    }
}
