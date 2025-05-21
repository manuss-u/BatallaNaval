// Tablero.java
package batalla;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Tablero {

    private final int tamano;
    private final List<Barco> barcos = new ArrayList<>();
    private final boolean[][] shots;

    public Tablero(int tamano) {
        this.tamano = tamano;
        shots = new boolean[tamano][tamano];
    }

    public int obtenerTamano() {
        return tamano;
    }

    public boolean ubicarBarco(Barco ship) {
        for (Point p : ship.posiciones) {
            if (p.x < 0 || p.x >= tamano || p.y < 0 || p.y >= tamano) {
                return false;
            }
            for (Barco b : barcos) {
                if (b.ocupado(p.x, p.y)) {
                    return false;
                }
            }
        }
        barcos.add(ship);
        return true;
    }

    public boolean estaOcupado(int r, int c) {
        for (Barco b : barcos) {
            if (b.ocupado(r, c)) {
                return true;
            }
        }
        return false;
    }

    public ResultadoDisparo recibirDisparo(int r, int c) {
        if (shots[r][c]) {
            return ResultadoDisparo.FALLO;
        }
        shots[r][c] = true;
        for (Barco b : barcos) {
            if (b.ocupado(r, c)) {
                return b.impactar(r, c);
            }
        }
        return ResultadoDisparo.FALLO;
    }

    public Barco obtenerBarcoPorUbicacion(int r, int c) {
        for (Barco b : barcos) {
            if (b.ocupado(r, c)) {
                return b;
            }
        }
        return null;
    }

    public boolean todosLosBarcosHundidos() {
        for (Barco b : barcos) {
            if (!b.estaHundido()) {
                return false;
            }
        }
        return true;
    }
}
