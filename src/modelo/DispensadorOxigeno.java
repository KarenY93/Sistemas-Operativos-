package modelo;

import java.util.concurrent.*;
import java.util.*;

/**
 * Dispensador de oxígeno con gestión completa de concurrencia.
 * Implementa exclusión mutua, sincronización y sistema de prioridades.
 */
public class DispensadorOxigeno {

    // ========== ATRIBUTOS ESENCIALES ==========
    private final String id;
    private boolean disponible = true;
    private Astronauta usuarioActual = null;
    private int totalRecargas = 0;
    private volatile boolean enFuncionamiento = true;

    // Mecanismo de sincronización PRINCIPAL
    private final Object lock = new Object();

    // Cola de espera con prioridad (menor oxígeno = mayor prioridad)
    private final PriorityBlockingQueue<Astronauta> colaEspera;

    // Estadísticas
    private final Map<String, Integer> recargasPorAstronauta = new ConcurrentHashMap<>();
    private int recargasEmergencia = 0;

    /**
     * Constructor del dispensador.
     */
    public DispensadorOxigeno(String id) {
        this.id = id;

        // Cola ordenada por prioridad (mayor prioridad = menor oxígeno primero)
        this.colaEspera = new PriorityBlockingQueue<>(10,
                (a1, a2) -> Integer.compare(a2.getPrioridad(), a1.getPrioridad())
        );

        System.out.println("Dispensador " + id + " listo para usar");
    }

    // ========== MÉTODO PRINCIPAL ==========

    /**
     * Solicita acceso al dispensador. Demuestra synchronized, wait() y notifyAll().
     * @return true si obtuvo acceso, false si timeout o error
     */
    public boolean solicitarAcceso(Astronauta astronauta) {
        if (!enFuncionamiento || !astronauta.isActivo()) {
            return false;
        }

        synchronized (lock) {
            // Verificar si ya está en proceso
            if (astronauta.getEstado() == Astronauta.Estado.RECARGANDO) {
                return false;
            }

            // Si el dispensador está ocupado, agregar a cola y ESPERAR
            if (!disponible) {
                if (!colaEspera.contains(astronauta)) {
                    colaEspera.put(astronauta);
                    System.out.println(astronauta.getNombre() +
                            " en cola (posición " + colaEspera.size() +
                            ", prioridad: " + astronauta.getPrioridad() + ")");
                }

                // ESPERA CON wait() - REQUISITO CLAVE
                long inicioEspera = System.currentTimeMillis();
                final long TIMEOUT_MS = 15000; // 15 segundos máximo

                while ((!disponible || colaEspera.peek() != astronauta) && enFuncionamiento) {
                    long tiempoRestante = TIMEOUT_MS - (System.currentTimeMillis() - inicioEspera);

                    if (tiempoRestante <= 0) {
                        colaEspera.remove(astronauta);
                        System.out.println(astronauta.getNombre() + " timeout de espera");
                        return false;
                    }

                    try {
                        lock.wait(tiempoRestante); // wait() CON timeout
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        colaEspera.remove(astronauta);
                        return false;
                    }
                }

                // Es su turno, sacar de la cola
                colaEspera.poll();
            }

            // ===== OBTENER ACCESO EXCLUSIVO =====
            disponible = false;
            usuarioActual = astronauta;
            astronauta.setEstado(Astronauta.Estado.RECARGANDO);

            System.out.println("\n>>> " + astronauta.getNombre() +
                    " ACCEDE al dispensador <<< (Prioridad: " +
                    astronauta.getPrioridad() + ")");

            // Iniciar recarga en hilo separado (no bloquear)
            new Thread(() -> realizarRecarga(astronauta),
                    "Recarga-" + astronauta.getNombre()).start();

            return true;
        }
    }

    /**
     * Realiza la recarga del astronauta.
     */
    private void realizarRecarga(Astronauta astronauta) {
        // El astronauta ejecuta su recarga
        astronauta.realizarRecarga();

        // Cuando termina, liberar el dispensador
        liberarDispensador(astronauta);
    }

    /**
     * Libera el dispensador después de una recarga.
     */
    private void liberarDispensador(Astronauta astronauta) {
        synchronized (lock) {
            // Verificar que sigue siendo el mismo astronauta
            if (usuarioActual != astronauta) {
                return;
            }

            // Actualizar estadísticas
            totalRecargas++;
            if (astronauta.getPrioridad() >= 3) { // Prioridad alta (emergencia)
                recargasEmergencia++;
            }
            recargasPorAstronauta.merge(astronauta.getNombre(), 1, Integer::sum);

            // Liberar recursos
            disponible = true;
            usuarioActual = null;

            System.out.println("<<< " + astronauta.getNombre() +
                    " libera el dispensador >>> (Total: " + totalRecargas + ")");

            // notifyAll() - REQUISITO CLAVE
            lock.notifyAll();

            // Mostrar siguiente en cola si hay
            if (!colaEspera.isEmpty()) {
                Astronauta siguiente = colaEspera.peek();
                System.out.println("   Siguiente: " + siguiente.getNombre() +
                        " (O₂: " + siguiente.getOxigeno() + "%, " +
                        "Prioridad: " + siguiente.getPrioridad() + ")");
            }
        }
    }

    // ========== MÉTODOS ADICIONALES (PARA DEMOSTRAR CONOCIMIENTO) ==========

    /**
     * Método que usa Semaphore - para mostrar conocimiento adicional.
     */
    public boolean usarConSemaforo(Astronauta astronauta) {
        Semaphore semaforo = new Semaphore(1, true); // Un permiso, fair

        try {
            // Intentar adquirir el semáforo
            if (!semaforo.tryAcquire(5, TimeUnit.SECONDS)) {
                System.out.println(astronauta.getNombre() + " no pudo adquirir semáforo");
                return false;
            }

            // Realizar recarga
            astronauta.realizarRecarga();

            // Liberar
            synchronized (this) {
                totalRecargas++;
            }
            semaforo.release();

            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Pone el dispensador en modo "no disponible" (simula mantenimiento).
     */
    public void iniciarMantenimiento() {
        synchronized (lock) {
            disponible = false;
            colaEspera.clear();
            System.out.println("Dispensador en MANTENIMIENTO - Cola limpiada");
            lock.notifyAll(); // Despertar a todos
        }
    }

    /**
     * Reactiva el dispensador después de mantenimiento.
     */
    public void finalizarMantenimiento() {
        synchronized (lock) {
            disponible = true;
            System.out.println("Dispensador ACTIVO nuevamente");
            lock.notifyAll();
        }
    }

    /**
     * Detiene completamente el dispensador.
     */
    public void detener() {
        enFuncionamiento = false;

        synchronized (lock) {
            disponible = true;
            colaEspera.clear();
            usuarioActual = null;
            lock.notifyAll(); // Despertar a todos los que esperan
        }

        System.out.println("Dispensador " + id + " DETENIDO");
    }

    // ========== GETTERS PARA LA VISTA ==========

    public String getId() {
        return id;
    }

    public boolean isDisponible() {
        synchronized (lock) {
            return disponible;
        }
    }

    public Astronauta getUsuarioActual() {
        synchronized (lock) {
            return usuarioActual;
        }
    }

    public int getTotalRecargas() {
        return totalRecargas;
    }

    public int getRecargasEmergencia() {
        return recargasEmergencia;
    }

    public int getTamanoCola() {
        synchronized (lock) {
            return colaEspera.size();
        }
    }

    public List<Astronauta> getColaEspera() {
        synchronized (lock) {
            return new ArrayList<>(colaEspera);
        }
    }

    public Map<String, Integer> getEstadisticas() {
        return new HashMap<>(recargasPorAstronauta);
    }

    /**
     * Información completa del estado actual.
     */
    public String getInfoCompleta() {
        synchronized (lock) {
            StringBuilder info = new StringBuilder();
            info.append("=== DISPENSADOR ").append(id).append(" ===\n");
            info.append("Estado: ").append(disponible ? "DISPONIBLE" : "OCUPADO").append("\n");

            if (!disponible && usuarioActual != null) {
                info.append("Usuario actual: ").append(usuarioActual.getNombre())
                        .append(" (O₂: ").append(usuarioActual.getOxigeno()).append("%)\n");
            }

            info.append("Recargas totales: ").append(totalRecargas).append("\n");
            info.append("Recargas de emergencia: ").append(recargasEmergencia).append("\n");
            info.append("En cola de espera: ").append(colaEspera.size()).append(" astronautas\n");

            if (!colaEspera.isEmpty()) {
                info.append("Próximos en cola:\n");
                int i = 0;
                for (Astronauta a : colaEspera) {
                    if (i++ >= 5) {
                        info.append("   ... y ").append(colaEspera.size() - 5).append(" más\n");
                        break;
                    }
                    info.append(String.format("   %d. %s (O₂: %d%%, Prioridad: %d)\n",
                            i, a.getNombre(), a.getOxigeno(), a.getPrioridad()));
                }
            }

            return info.toString();
        }
    }

    @Override
    public String toString() {
        synchronized (lock) {
            if (!enFuncionamiento) {
                return "Dispensador " + id + " - DETENIDO";
            }

            String estado = disponible ? "🟢 DISPONIBLE" : "🔴 OCUPADO";
            if (disponible && !colaEspera.isEmpty()) {
                estado += " (Cola: " + colaEspera.size() + ")";
            }

            return String.format("%s | Recargas: %d | Emergencias: %d",
                    estado, totalRecargas, recargasEmergencia);
        }
    }
}