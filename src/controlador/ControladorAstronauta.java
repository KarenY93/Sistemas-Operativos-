package controlador;

import modelo.Astronauta;
import modelo.DispensadorOxigeno;

/**
 * Controlador del Astronauta.
 * Representa un PROCESO (hilo) que compite por el dispensador de oxígeno.
 */
public class ControladorAstronauta implements Runnable {

    private final Astronauta astronauta;
    private final DispensadorOxigeno dispensador;
    private boolean activo;

    // Tiempo base entre ciclos (simula planificación del SO)
    private static final int TIEMPO_CICLO = 1000;

    public ControladorAstronauta(Astronauta astronauta,
                                 DispensadorOxigeno dispensador) {
        this.astronauta = astronauta;
        this.dispensador = dispensador;
        this.activo = true;
    }

    @Override
    public void run() {

        try {
            while (activo && !astronauta.haFalladoLaMision()) {

                // 1️⃣ Ejecuta un ciclo de vida
                astronauta.consumirOxigeno();
                mostrarEstado();

                // 2️⃣ Solicita oxígeno si es necesario (sección crítica)
                if (astronauta.necesitaRecarga()) {
                    System.out.println("🔴 " + astronauta.getNombre()
                            + " solicita acceso al dispensador");

                    dispensador.solicitarRecarga(astronauta);

                    System.out.println("🟢 " + astronauta.getNombre()
                            + " terminó la recarga");
                }

                // 3️⃣ Finaliza recuperación
                astronauta.completarRecuperacion();

                // 4️⃣ Espera (simula quantum de CPU)
                Thread.sleep(TIEMPO_CICLO);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("☠️ Proceso finalizado: " + astronauta.getNombre());
    }

    private void mostrarEstado() {
        System.out.println(astronauta.toString());
    }

    public void detener() {
        activo = false;
    }
}
