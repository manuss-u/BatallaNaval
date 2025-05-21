// Jugador.java
package batalla;

import java.awt.Point;
import java.util.List;
import javax.swing.JButton;

/**
 * Clase abstracta para un jugador de Batalla Naval.
 */
public abstract class Jugador {

    protected String nombre;
    protected Tablero tableroPropio;
    protected Tablero tableroOponente;
    protected JButton[][] botonesOponente;
    protected Orientacion orientacion = Orientacion.HORIZONTAL; // <-- estado interno

    protected Jugador(String nombre, Tablero tableroPropio, Tablero tableroOponente, JButton[][] botonesOponente) {
        this.nombre = nombre;
        this.tableroPropio = tableroPropio;
        this.tableroOponente = tableroOponente;
        this.botonesOponente = botonesOponente;
    }

    /**
     * Cambia la orientación para la siguiente colocación.
     */
    public void ponerOrientacion(Orientacion orientacion) {
        this.orientacion = orientacion;
    }

    /**
     * Devuelve la orientación actual (Horizontal o Vertical).
     */
    public Orientacion obtenerOrientacion() {
        return orientacion;
    }

    /**
     * Coloca la flota propia en el tablero.
     */
    public abstract void colocarFlota(List<Barco> flota);

    /**
     * Dispara contra el oponente (turno de juego).
     */
    public abstract ResultadoDisparo disparar(List<Point> puntosDisponibles);
}
