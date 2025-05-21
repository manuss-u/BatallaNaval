// Submarino.java
package batalla.barcos;

import batalla.Barco;

public class Submarino extends Barco {

    @Override
    public int obtenerTamano() {
        return 3;
    }

    @Override
    public char obtenerInicial() {
        return 'S';
    }
    
    @Override
    public String obtenerNombre() {
        return "Submarino";
    }
}
