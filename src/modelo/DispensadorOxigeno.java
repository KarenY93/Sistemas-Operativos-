package modelo;

public class DispensadorOxigeno {

    /* ===== Estado del recurso compartido ===== */
    private boolean ocupado = false;
    private String usuarioActual = null;

    /* ===== Solicitud de acceso (sección crítica) ===== */
    public synchronized void solicitarRecarga(Astronauta a) throws InterruptedException {

        // Espera activa mientras el dispensador esté ocupado
        while (ocupado) {
            System.out.println("⏳ " + a.getNombre() +
                    " esperando. Usuario actual: " + usuarioActual);
            wait();
        }

        // Toma del recurso
        ocupado = true;
        usuarioActual = a.getNombre();
        System.out.println("✅ " + a.getNombre() + " accedió al dispensador");
    }

    /* ===== Liberación del recurso ===== */
    public synchronized void liberar() {

        // Libera el dispensador y despierta a los hilos en espera
        System.out.println("🚪 " + usuarioActual + " liberó el dispensador");
        ocupado = false;
        usuarioActual = null;
        notifyAll();
    }

    /* ===== Consulta de estado ===== */
    public synchronized boolean estaOcupado() {
        return ocupado;
    }
}
