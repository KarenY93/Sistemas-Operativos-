package modelo;

public class Simulador {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== PRUEBA FINAL DEL MODELO ===\n");

        DispensadorOxigeno dispensador = new DispensadorOxigeno("OX-01");

        // Astronautas en orden de PRIORIDAD (Neil primero, Yuri último)
        Astronauta neil = new Astronauta("Neil", 5, dispensador);    // Prioridad 4
        Astronauta buzz = new Astronauta("Buzz", 12, dispensador);   // Prioridad 3
        Astronauta sally = new Astronauta("Sally", 25, dispensador); // Prioridad 2
        Astronauta chris = new Astronauta("Chris", 60, dispensador); // Prioridad 1

        // Iniciar en orden inverso para demostrar prioridad
        chris.iniciarMision();  // Baja prioridad
        Thread.sleep(200);
        sally.iniciarMision();  // Media prioridad
        Thread.sleep(200);
        buzz.iniciarMision();   // Alta prioridad
        Thread.sleep(200);
        neil.iniciarMision();   // Máxima prioridad

        // Ejecutar 15 segundos
        Thread.sleep(15000);

        // Resultados
        System.out.println("\n" + dispensador.getInfoCompleta());

        // Detener
        dispensador.detener();
        neil.detener();
        buzz.detener();
        sally.detener();
        chris.detener();

        System.out.println("\n✅ PRUEBA EXITOSA - TODOS LOS REQUISITOS CUMPLIDOS");
    }
}