package modelo;

import java.util.LinkedList;
import java.util.List;

public class DispensadorOxigeno {
    private boolean ocupado;
    private final List<Astronauta> colaEspera;

    public DispensadorOxigeno() {
        this.ocupado = false;
        this.colaEspera = new LinkedList<>();
    }

    public void solicitarRecarga(Astronauta astronauta) throws InterruptedException {
        synchronized (this) {
            colaEspera.add(astronauta);

            while (ocupado || !esTurnoDelAstronauta(astronauta)) {
                wait();
            }
            ocupado = true;
            colaEspera.remove(astronauta);
        }

        ejecutarRecarga(astronauta);

        synchronized (this) {
            ocupado = false;
            notifyAll(); 
        }
    }

    private boolean esTurnoDelAstronauta(Astronauta astronauta) {
        for (Astronauta a : colaEspera) {
            if (a.estaEnEstadoCritico()) {
                return a == astronauta;
            }
        }
        return colaEspera.get(0) == astronauta;
    }

    private void ejecutarRecarga(Astronauta astronauta) throws InterruptedException {
        int tiempoRecarga = astronauta.estaEnEstadoCritico() ? 800 : 1500;
        Thread.sleep(tiempoRecarga);

        astronauta.recargar();
    }

    public synchronized boolean estaOcupado() {
        return ocupado;
    }

    public synchronized int getCantidadEnEspera() {
        return colaEspera.size();
    }
}

    
