// Destructor.java
package batalla.barcos;

import batalla.Barco;

public class Destructor extends Barco {

    @Override
    public int obtenerTamano() {
        return 2;
    }

    @Override
    public char obtenerInicial() {
        return 'D';
    }
    
    @Override
    public String obtenerNombre() {
        return "Destructor";
    }
}
