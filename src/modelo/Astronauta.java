package modelo;

import java.util.Random;

/**
 * Modelo avanzado del astronauta.
 * Contiene SOLO reglas de dominio (sin concurrencia).
 */
public class Astronauta {

    public enum Estado {
        NORMAL,
        EMERGENCIA,
        RECUPERACION,
        TERMINADO
    }

    private final String nombre;
    private int oxigeno; // 0 - 100
    private Estado estado;
    private boolean activo;

    // Dinámica avanzada
    private int ciclosVividos;
    private int fatiga; // 0 - 100

    // Reglas de dominio
    private static final int CONSUMO_BASE_MIN = 1;
    private static final int CONSUMO_BASE_MAX = 4;
    private static final int UMBRAL_RECARGA = 30;
    private static final int UMBRAL_EMERGENCIA = 10;
    private static final int FATIGA_MAX = 100;

    private final Random random = new Random();

    public Astronauta(String nombre, int oxigenoInicial) {
        this.nombre = nombre;
        this.oxigeno = Math.max(0, Math.min(100, oxigenoInicial));
        this.estado = Estado.NORMAL;
        this.activo = true;
        this.ciclosVividos = 0;
        this.fatiga = 0;
    }

    /**
     * Simula un ciclo de vida del astronauta.
     */
    public void consumirOxigeno() {
        if (!activo || estado == Estado.TERMINADO) return;

        ciclosVividos++;
        aumentarFatiga();

        int consumo = calcularConsumo();
        oxigeno = Math.max(0, oxigeno - consumo);

        if (oxigeno < UMBRAL_EMERGENCIA) {
            estado = Estado.EMERGENCIA;
        }

        if (oxigeno <= 0) {
            estado = Estado.TERMINADO;
            activo = false;
        }
    }

    /**
     * Regla de consumo basada en estado y fatiga.
     */
    private int calcularConsumo() {
        int base = CONSUMO_BASE_MIN + random.nextInt(CONSUMO_BASE_MAX);

        if (estado == Estado.EMERGENCIA) {
            base += 2; // hiperventilación
        }

        base += fatiga / 25; // fatiga aumenta consumo

        return base;
    }

    /**
     * Aumenta la fatiga con el tiempo.
     */
    private void aumentarFatiga() {
        fatiga = Math.min(FATIGA_MAX, fatiga + 5);
    }

    /**
     * Recarga oxígeno y reduce fatiga.
     */
    public void recargar() {
        if (estado == Estado.TERMINADO) return;

        oxigeno = 100;
        fatiga = Math.max(0, fatiga - 40);
        estado = Estado.RECUPERACION;
    }

    /**
     * Se llama después de algunos ciclos tras la recarga.
     */
    public void completarRecuperacion() {
        if (estado == Estado.RECUPERACION) {
            estado = Estado.NORMAL;
        }
    }

    public boolean necesitaRecarga() {
        return oxigeno < UMBRAL_RECARGA && estado != Estado.TERMINADO;
    }

    public boolean estaEnEstadoCritico() {
        return estado == Estado.EMERGENCIA;
    }

    public int getPrioridad() {
        if (oxigeno < 10) return 4;
        if (oxigeno < 20) return 3;
        if (oxigeno < 30) return 2;
        return 1;
    }

    public boolean haFalladoLaMision() {
        return estado == Estado.TERMINADO && oxigeno <= 0;
    }

    // ===== GETTERS =====

    public String getNombre() {
        return nombre;
    }

    public int getOxigeno() {
        return oxigeno;
    }

    public Estado getEstado() {
        return estado;
    }

    public boolean isActivo() {
        return activo;
    }

    public int getFatiga() {
        return fatiga;
    }

    public int getCiclosVividos() {
        return ciclosVividos;
    }

    @Override
    public String toString() {
        return nombre +
                " | O₂: " + oxigeno + "%" +
                " | Fatiga: " + fatiga +
                " | Estado: " + estado +
                " | Ciclos: " + ciclosVividos;
    }
}