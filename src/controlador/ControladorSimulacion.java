package controlador;

import modelo.Astronauta;
import modelo.DispensadorOxigeno;
import vista.VentanaSimulacion;

import java.util.ArrayList;
import java.util.List;

public class ControladorSimulacion {

    private final VentanaSimulacion vista;
    private DispensadorOxigeno dispensador;

    private final List<ControladorAstronauta> procesos;
    private final List<Thread> hilos;

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

        int cantidad = vista.getCantidadAstronautas();
        int velocidad = vista.getVelocidadMs();

        dispensador = new DispensadorOxigeno();

        procesos.clear();
        hilos.clear();

        DispensadorOxigeno dispensador = new DispensadorOxigeno();
        ControladorDispensadorOxigeno controladorDispensador =
                new ControladorDispensadorOxigeno(dispensador);

        for (int i = 1; i <= cantidad; i++) {
            Astronauta a = new Astronauta("Astronauta " + i, 100);

            ControladorAstronauta proceso =
                    new ControladorAstronauta(a, controladorDispensador, vista, velocidad);

            Thread hilo = new Thread(proceso, a.getNombre());
            hilo.start();
        }

    }

    private void detenerSimulacion() {
        for (ControladorAstronauta p : procesos) {
            p.detener();
        }

        for (Thread t : hilos) {
            t.interrupt();
        }

        vista.habilitarControles(true);
    }

    private void resetearSimulacion() {
        //limpieza visual
        detenerSimulacion();

        vista.limpiarLog();
        vista.limpiarCola();
        vista.resetearDispensador();
    }

}
