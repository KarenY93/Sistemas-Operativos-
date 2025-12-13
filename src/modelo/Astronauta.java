package modelo;

import java.util.Random;

/**
 * Representa un astronauta que consume oxígeno y necesita recargas periódicas.
 * Implementa Runnable para ejecutarse como hilo independiente.
 * Demuestra: hilos, estados sincronizados, consumo de recurso.
 */
public class Astronauta implements Runnable {

    public enum Estado {
        NORMAL,          // Operando normalmente
        ESPERANDO,       // Esperando turno en el dispensador
        RECARGANDO,      // Usando el dispensador
        EMERGENCIA,      // Oxígeno crítico (< 10%)
        TERMINADO        // Misión finalizada
    }

    private final String nombre;
    private int oxigeno;            // 0-100%
    private volatile Estado estado;
    private volatile boolean activo;
    private final DispensadorOxigeno dispensador;
    private final Random random;
    private Thread hiloPropio;

    // Constantes de simulación
    private static final int CONSUMO_MIN = 1;
    private static final int CONSUMO_MAX = 4;
    private static final int UMBRAL_RECARGA = 30;
    private static final int UMBRAL_EMERGENCIA = 10;

    /**
     * Constructor del astronauta.
     * @param nombre Identificador del astronauta
     * @param oxigenoInicial Nivel inicial de oxígeno (0-100)
     * @param dispensador Referencia al dispensador compartido
     */
    public Astronauta(String nombre, int oxigenoInicial, DispensadorOxigeno dispensador) {
        this.nombre = nombre;
        this.oxigeno = Math.max(0, Math.min(100, oxigenoInicial));
        this.dispensador = dispensador;
        this.estado = Estado.NORMAL;
        this.activo = true;
        this.random = new Random();
    }

    /**
     * Inicia el hilo del astronauta.
     */
    public void iniciarMision() {
        if (hiloPropio == null || !hiloPropio.isAlive()) {
            hiloPropio = new Thread(this, "Astronauta-" + nombre);
            hiloPropio.start();
            System.out.println(nombre + " inicia misión (O₂: " + oxigeno + "%)");
        }
    }

    @Override
    public void run() {
        try {
            while (activo && oxigeno > 0) {
                // Ciclo de vida del astronauta
                cicloVida();

                // Pequeña pausa entre ciclos (variable)
                Thread.sleep(1500 + random.nextInt(1000));
            }
        } catch (InterruptedException e) {
            System.out.println(nombre + " interrumpido durante misión");
            Thread.currentThread().interrupt();
        } finally {
            estado = Estado.TERMINADO;
            if (oxigeno <= 0) {
                System.out.println("☠️ " + nombre + " ha muerto por falta de oxígeno");
            } else {
                System.out.println("✓ " + nombre + " finaliza misión exitosamente");
            }
        }
    }

    /**
     * Un ciclo de vida del astronauta: consume oxígeno y toma decisiones.
     */
    private void cicloVida() {
        // 1. Consumir oxígeno (solo si no está recargando)
        if (estado != Estado.RECARGANDO) {
            int consumo = CONSUMO_MIN + random.nextInt(CONSUMO_MAX);
            oxigeno = Math.max(0, oxigeno - consumo);

            // 2. Actualizar estado según nivel
            if (oxigeno < UMBRAL_EMERGENCIA && estado != Estado.EMERGENCIA) {
                estado = Estado.EMERGENCIA;
                System.out.println("¡EMERGENCIA! " + nombre + " tiene solo " + oxigeno + "% de O₂");
            }

            // 3. Decidir si necesita recarga
            if (oxigeno < UMBRAL_RECARGA && estado != Estado.ESPERANDO && estado != Estado.RECARGANDO) {
                estado = Estado.ESPERANDO;
                System.out.println(nombre + " solicita recarga (O₂: " + oxigeno + "%)");

                // Solicitar acceso al dispensador
                boolean acceso = dispensador.solicitarAcceso(this);

                if (!acceso) {
                    // No obtuvo acceso inmediato, volver a NORMAL
                    estado = Estado.NORMAL;
                }
                // Si obtuvo acceso, el dispensador cambiará su estado a RECARGANDO
            }
        }
    }

    /**
     * Realiza la recarga de oxígeno. Este método es llamado por el dispensador.
     */
    public void realizarRecarga() {
        if (estado == Estado.RECARGANDO || estado == Estado.TERMINADO) {
            return; // Ya está recargando o terminó
        }

        estado = Estado.RECARGANDO;
        System.out.println("⚡ " + nombre + " INICIA recarga (de " + oxigeno + "% a 100%)");

        try {
            // Tiempo de recarga proporcional a la urgencia
            int tiempoRecarga = calcularTiempoRecarga();
            Thread.sleep(tiempoRecarga);

            // Recargar al máximo
            oxigeno = 100;
            estado = Estado.NORMAL;

            System.out.println(nombre + " COMPLETA recarga (100% O₂) en " + tiempoRecarga + "ms");

        } catch (InterruptedException e) {
            System.out.println("Recarga de " + nombre + " interrumpida");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Calcula tiempo de recarga basado en nivel de oxígeno.
     * @return Tiempo en milisegundos
     */
    private int calcularTiempoRecarga() {
        if (oxigeno < 15) return 800;   // Recarga rápida para emergencias
        if (oxigeno < 25) return 1200;  // Recarga media
        return 1800;                    // Recarga normal
    }

    /**
     * Detiene al astronauta de manera segura.
     */
    public void detener() {
        activo = false;
        if (hiloPropio != null) {
            hiloPropio.interrupt();
        }
    }

    // ========== GETTERS Y SETTERS ==========

    public String getNombre() {
        return nombre;
    }

    public int getOxigeno() {
        return oxigeno;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean necesitaRecarga() {
        return oxigeno < UMBRAL_RECARGA && estado != Estado.RECARGANDO && estado != Estado.TERMINADO;
    }

    public int getPrioridad() {
        // Prioridad inversa: menor oxígeno = mayor prioridad (1-4)
        if (oxigeno < 10) return 4;      // Máxima prioridad
        if (oxigeno < 20) return 3;      // Alta prioridad
        if (oxigeno < 30) return 2;      // Media prioridad
        return 1;                        // Baja prioridad
    }

    @Override
    public String toString() {
        String iconoPrioridad;
        switch (getPrioridad()) {
            case 4: iconoPrioridad = "🔴"; break;
            case 3: iconoPrioridad = "🟠"; break;
            case 2: iconoPrioridad = "🟡"; break;
            default: iconoPrioridad = "🟢";
        }

        return String.format("%s %-6s | O₂: %3d%% | Estado: %-12s | Prioridad: %d",
                iconoPrioridad, nombre, oxigeno, estado, getPrioridad());
    }
}