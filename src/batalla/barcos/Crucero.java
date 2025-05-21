// Crucero.java
package batalla.barcos;

import batalla.Barco;

public class Crucero extends Barco {

    @Override
    public int obtenerTamano() {
        return 3;
    }

    @Override
    public char obtenerInicial() {
        return 'C';
    }
    
    @Override
    public String obtenerNombre() {
        return "Crucero";
    }
}
