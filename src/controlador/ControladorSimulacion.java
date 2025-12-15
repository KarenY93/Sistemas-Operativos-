package controlador;

import java.util.ArrayList;
import java.util.List;
import modelo.Astronauta;
import modelo.DispensadorOxigeno;
import vista.VentanaSimulacion;

public class ControladorSimulacion {
    private final VentanaSimulacion vista;
    private final List<ControladorAstronauta> procesos;
    private final List<Thread> hilos;
    private ControladorDispensadorOxigeno controladorDispensador;

    public ControladorSimulacion(VentanaSimulacion vista) {
        this.vista = vista;
        this.procesos = new ArrayList<>();
        this.hilos = new ArrayList<>();
        conectarEventos();
    }

    private void conectarEventos() {
        vista.setStartListener(e -> iniciarSimulacion());
        vista.setStopListener(e -> detenerSimulacion());
        vista.setResetListener(e -> resetearSimulacion());
    }

    private void iniciarSimulacion() {
        vista.habilitarControles(false);
        vista.limpiarCola();
        vista.resetearDispensador();

        int cantidad = vista.getCantidadAstronautas();
        int velocidad = vista.getVelocidadMs();

        procesos.clear();
        hilos.clear();

        // Solo una instancia del dispensador
        DispensadorOxigeno dispensador = new DispensadorOxigeno();
        controladorDispensador = new ControladorDispensadorOxigeno(dispensador);
        controladorDispensador.setVista(vista); // ⭐ PASA LA VISTA AL CONTROLADOR

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

    private void detenerSimulacion() {
        // Detener primero los procesos
        for (ControladorAstronauta p : procesos) {
            p.detener();
        }

        // Interrumpir los hilos
        for (Thread t : hilos) {
            t.interrupt();
        }

        // Esperar a que todos terminen
        for (Thread t : hilos) {
            try {
                t.join(1000); // Esperar 1 segundo máximo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Limpiar
        procesos.clear();
        hilos.clear();

        vista.habilitarControles(true);
        vista.actualizarEstadoDispensador(false); // Asegurar que esté libre
    }

    private void resetearSimulacion() {
        detenerSimulacion();
        vista.limpiarLog();
        vista.limpiarCola();
        vista.limpiarTablaAstronautas();
        vista.resetearDispensador();
    }
}