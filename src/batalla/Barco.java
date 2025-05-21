// Barco.java
package batalla;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Clase abstracta para un barco.
 */
public abstract class Barco {

    protected List<Point> posiciones = new ArrayList<>();
    protected Set<Point> impactos = new HashSet<>();

    public Barco() {
    }

    public abstract int obtenerTamano();

    public abstract char obtenerInicial();
    
    public abstract String obtenerNombre();

    public void ponerOrientacion(Orientacion ori) {
        this.orientacion = ori;
    }

    public List<Point> obtenerPosiciones() {
        return posiciones;
    }

    public void ponerPosicion(int row, int col) {
        posiciones.clear();
        for (int i = 0; i < obtenerTamano(); i++) {
            int r = row + (orientacion == Orientacion.VERTICAL ? i : 0);
            int c = col + (orientacion == Orientacion.HORIZONTAL ? i : 0);
            posiciones.add(new Point(r, c));
        }
    }

    public boolean ocupado(int r, int c) {
        return posiciones.contains(new Point(r, c));
    }

    public ResultadoDisparo impactar(int r, int c) {
        Point p = new Point(r, c);
        if (!ocupado(r, c) || impactos.contains(p)) {
            return ResultadoDisparo.FALLO;
        }
        impactos.add(p);
        return estaHundido() ? ResultadoDisparo.HUNDIDO : ResultadoDisparo.GOLPEADO;
    }

    public boolean estaHundido() {
        return impactos.size() == obtenerTamano();
    }

    private Orientacion orientacion;
}
