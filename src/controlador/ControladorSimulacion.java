package controlador;

import java.util.ArrayList;
import java.util.List;
import modelo.Astronauta;
import modelo.DispensadorOxigeno;
import vista.VentanaSimulacion;

public class ControladorSimulacion {

    /* ===== Vista principal ===== */
    private final VentanaSimulacion vista;

    /* ===== Gestión de hilos ===== */
    private final List<ControladorAstronauta> procesos;
    private final List<Thread> hilos;

    /* ===== Controlador compartido ===== */
    private ControladorDispensadorOxigeno controladorDispensador;

    public ControladorSimulacion(VentanaSimulacion vista) {
        this.vista = vista;
        this.procesos = new ArrayList<>();
        this.hilos = new ArrayList<>();
        conectarEventos();
    }

    /* ===== Enlace Vista → Controlador ===== */
    private void conectarEventos() {
        vista.setStartListener(e -> iniciarSimulacion());
        vista.setStopListener(e -> detenerSimulacion());
        vista.setResetListener(e -> resetearSimulacion());
    }

    /* ===== Inicio de la simulación ===== */
    private void iniciarSimulacion() {
        vista.habilitarControles(false);
        vista.limpiarCola();
        vista.resetearDispensador();

        int cantidad = vista.getCantidadAstronautas();
        int velocidad = vista.getVelocidadMs();

        procesos.clear();
        hilos.clear();

        // Dispensador único compartido por todos los astronautas
        DispensadorOxigeno dispensador = new DispensadorOxigeno();
        controladorDispensador = new ControladorDispensadorOxigeno(dispensador);
        controladorDispensador.setVista(vista);

        for (int i = 1; i <= cantidad; i++) {
            Astronauta a = new Astronauta("Astronauta " + i, 100);

            ControladorAstronauta proceso =
                    new ControladorAstronauta(a, controladorDispensador, vista, velocidad);

            Thread hilo = new Thread(proceso, a.getNombre());

            procesos.add(proceso);
            hilos.add(hilo);

            hilo.start();
        }
    }

    /* ===== Detención controlada ===== */
    private void detenerSimulacion() {

        // Señal de parada lógica
        for (ControladorAstronauta p : procesos) {
            p.detener();
        }

        // Interrupción de hilos
        for (Thread t : hilos) {
            t.interrupt();
        }

        // Espera breve para cierre ordenado
        for (Thread t : hilos) {
            try {
                t.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        procesos.clear();
        hilos.clear();

        vista.habilitarControles(true);
        vista.actualizarEstadoDispensador(false);
    }

    /* ===== Reinicio completo ===== */
    private void resetearSimulacion() {
        detenerSimulacion();
        vista.limpiarLog();
        vista.limpiarCola();
        vista.limpiarTablaAstronautas();
        vista.resetearDispensador();
    }
}
