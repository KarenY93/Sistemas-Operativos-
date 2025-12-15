package modelo;

public class DispensadorOxigeno {
    private boolean ocupado = false;
    private String usuarioActual = null;

    public synchronized void solicitarRecarga(Astronauta a) throws InterruptedException {
        while (ocupado) {
            System.out.println("⏳ " + a.getNombre() + " esperando. Usuario actual: " + usuarioActual);
            wait();
        }

        // entra a la sección crítica
        ocupado = true;
        usuarioActual = a.getNombre();
        System.out.println("✅ " + a.getNombre() + " accedió al dispensador");
    }

    public synchronized void liberar() {
        System.out.println("🚪 " + usuarioActual + " liberó el dispensador");
        ocupado = false;
        usuarioActual = null;
        notifyAll();
    }

    public synchronized boolean estaOcupado() {
        return ocupado;
    }
}
