// JugadorMaquina.java
package batalla.jugadores;

import batalla.*;
import java.awt.Point;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;

/**
 * Jugador controlado por la máquina (IA simple).
 */
public class JugadorMaquina extends Jugador {

    private Random rnd = new Random();

    public JugadorMaquina(String nombre, Tablero tableroPropio, Tablero tableroOponente, JButton[][] botonesOponente) {
        super(nombre, tableroPropio, tableroOponente, botonesOponente);
    }

    @Override
    public void colocarFlota(List<Barco> flota) {
        for (Barco b : flota) {
            boolean colocado = false;
            while (!colocado) {
                Orientacion ori = rnd.nextBoolean() ? Orientacion.HORIZONTAL : Orientacion.VERTICAL;
                int r = rnd.nextInt(tableroPropio.obtenerTamano());
                int c = rnd.nextInt(tableroPropio.obtenerTamano());
                b.ponerOrientacion(ori);
                b.ponerPosicion(r, c);
                colocado = tableroPropio.ubicarBarco(b);
            }
        }
    }

    @Override
    public ResultadoDisparo disparar(List<Point> puntosDisponibles) {
        if (puntosDisponibles.isEmpty()) {
            return ResultadoDisparo.FALLO;
        }
        int idx = rnd.nextInt(puntosDisponibles.size());
        Point p = puntosDisponibles.remove(idx);
        ResultadoDisparo res = tableroOponente.recibirDisparo(p.x, p.y);
        JButton btn = botonesOponente[p.x][p.y];
        switch (res) {
            case FALLO -> btn.setBackground(ColoresBatallaNaval.FALLO);
            case GOLPEADO -> btn.setBackground(ColoresBatallaNaval.ACIERTO);
            default -> btn.setBackground(ColoresBatallaNaval.HUNDIDO);
        }
        return res;
    }
}
