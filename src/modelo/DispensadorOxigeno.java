package modelo;

public class DispensadorOxigeno {

    private boolean ocupado = false;

    public synchronized void solicitarRecarga(Astronauta a)
            throws InterruptedException {

        while (ocupado) {
            wait();
        }

        // entra a la sección crítica
        ocupado = true;
    }

    public synchronized void liberar() {
        ocupado = false;
        notifyAll();
    }

    public synchronized boolean estaOcupado() {
        return ocupado;
    }
}

