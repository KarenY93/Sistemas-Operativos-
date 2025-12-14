package controlador;

import modelo.Astronauta;
import vista.VentanaSimulacion;

public class ControladorAstronauta implements Runnable {

    private final Astronauta astronauta;
    private final ControladorDispensadorOxigeno controladorDispensador;
    private final VentanaSimulacion vista;
    private final int delayMs;

    private boolean activo;

    public ControladorAstronauta(Astronauta astronauta,
                                 ControladorDispensadorOxigeno controladorDispensador,
                                 VentanaSimulacion vista,
                                 int delayMs) {

        this.astronauta = astronauta;
        this.controladorDispensador = controladorDispensador;
        this.vista = vista;
        this.delayMs = delayMs;
        this.activo = true;
    }

    @Override
    public void run() {
        try {
            while (activo && !astronauta.haFalladoLaMision()) {

                System.out.println(
                        astronauta.getNombre() + " | O2=" + astronauta.getOxigeno()
                );

                astronauta.consumirOxigeno();

                if (astronauta.necesitaRecarga()) {

                    // 1️⃣ Intenta acceder
                    vista.mostrarIntento(astronauta.getNombre());
                    vista.agregarACola(astronauta.getNombre());

                    // 2️⃣ Solicita el recurso (CONTROLADOR, no MODELO)
                    controladorDispensador.solicitarRecarga(astronauta);

                    // 3️⃣ Actualización visual
                    vista.removerDeCola(astronauta.getNombre());
                    vista.mostrarAcceso(astronauta.getNombre());
                    vista.actualizarEstadoDispensador(true);

                    vista.mostrarSalida(astronauta.getNombre());
                    vista.actualizarEstadoDispensador(false);
                }

                astronauta.completarRecuperacion();
                Thread.sleep(delayMs);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void detener() {
        activo = false;
    }
}
