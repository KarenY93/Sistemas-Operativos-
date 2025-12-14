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
                    // Solo agrega a la cola
                    vista.mostrarIntento(astronauta.getNombre());
                    vista.agregarACola(astronauta.getNombre());

                    // Esta llamada ahora será MÁS RÁPIDA
                    controladorDispensador.solicitarRecarga(astronauta);

                    // Se remueve de la cola cuando termina
                    vista.removerDeCola(astronauta.getNombre());
                }

                astronauta.completarRecuperacion();
                Thread.sleep(delayMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(astronauta.getNombre() + " interrumpido");
        } finally {
            // Asegurarse de remover de la cola si estaba esperando
            vista.removerDeCola(astronauta.getNombre());
        }
    }

    public void detener() {
        activo = false;
    }
}