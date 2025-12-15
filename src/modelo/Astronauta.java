package modelo;

import java.util.Random;

public class Astronauta {

    /* ===== Estados ===== */
    public enum Estado {
        NORMAL,
        EMERGENCIA,
        RECUPERACION,
        TERMINADO
    }

    /* ===== Atributos ===== */
    private final String nombre;
    private int oxigeno;
    private Estado estado;
    private boolean activo;

    private int ciclosVividos;
    private int fatiga;

    /* ===== Reglas de dominio ===== */
    private static final int CONSUMO_BASE_MIN = 1;
    private static final int CONSUMO_BASE_MAX = 4;

    private static final int UMBRAL_RECARGA = 30;
    private static final int UMBRAL_EMERGENCIA = 10;

    private static final int FATIGA_MAX = 100;

    /* ===== Dinámica de fatiga ===== */
    private static final int CICLOS_POR_FATIGA = 4;
    private static final int FATIGA_POR_CICLO = 2;
    private static final int PROB_RECUPERACION = 20;

    private final Random random = new Random();

    /* ===== Constructor ===== */
    public Astronauta(String nombre, int oxigenoInicial) {
        this.nombre = nombre;
        this.oxigeno = Math.max(0, Math.min(100, oxigenoInicial));
        this.estado = Estado.NORMAL;
        this.activo = true;
        this.ciclosVividos = 0;
        this.fatiga = 0;
    }

    /* ===== Ciclo de vida ===== */
    public void consumirOxigeno() {
        if (!activo || estado == Estado.TERMINADO) return;

        ciclosVividos++;

        aumentarFatiga();
        recuperarFatigaAleatoria();

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

    /* ===== Reglas internas ===== */
    private int calcularConsumo() {
        int base = CONSUMO_BASE_MIN + random.nextInt(CONSUMO_BASE_MAX);

        if (estado == Estado.EMERGENCIA) {
            base += 2;
        }

        base += fatiga / 30;
        return base;
    }

    private void aumentarFatiga() {
        if (ciclosVividos % CICLOS_POR_FATIGA == 0) {
            fatiga = Math.min(FATIGA_MAX, fatiga + FATIGA_POR_CICLO);
        }
    }

    private void recuperarFatigaAleatoria() {
        if (fatiga > 0 && random.nextInt(PROB_RECUPERACION) == 0) {
            fatiga--;
        }
    }

    /* ===== Recarga ===== */
    public void recargar() {
        if (estado == Estado.TERMINADO) return;

        oxigeno = 100;
        fatiga = Math.max(0, fatiga - 30);
        estado = Estado.RECUPERACION;
    }

    public void completarRecuperacion() {
        if (estado == Estado.RECUPERACION) {
            estado = Estado.NORMAL;
        }
    }

    /* ===== Consultas ===== */
    public boolean necesitaRecarga() {
        return oxigeno < UMBRAL_RECARGA && estado != Estado.TERMINADO;
    }

    public boolean estaEnEstadoCritico() {
        return estado == Estado.EMERGENCIA;
    }

    public boolean haFalladoLaMision() {
        return estado == Estado.TERMINADO;
    }

    /* ===== Getters ===== */
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

    /* ===== Debug ===== */
    @Override
    public String toString() {
        return nombre +
                " | O₂: " + oxigeno + "%" +
                " | Fatiga: " + fatiga +
                " | Estado: " + estado +
                " | Ciclos: " + ciclosVividos;
    }
}
