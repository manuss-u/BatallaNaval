// JugadorHumano.java
package batalla.jugadores;

import batalla.*;
import java.awt.Point;
import java.util.List;
import javax.swing.JButton;

/**
 * Jugador controlado por interacción del usuario.
 */
public class JugadorHumano extends Jugador {

    private List<Barco> flota;

    public JugadorHumano(String nombre, Tablero tableroPropio, Tablero tableroOponente, JButton[][] botonesOponente) {
        super(nombre, tableroPropio, tableroOponente, botonesOponente);
    }

    @Override
    public void colocarFlota(List<Barco> flota) {
        // La colocación se maneja mediante listeners en la UI.
        this.flota = flota;
    }

    @Override
    public ResultadoDisparo disparar(List<Point> puntosDisponibles) {
        return ResultadoDisparo.FALLO;
    }
}
