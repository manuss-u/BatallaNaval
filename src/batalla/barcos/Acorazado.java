// Acorazado.java
package batalla.barcos;

import batalla.Barco;

public class Acorazado extends Barco {

    @Override
    public int obtenerTamano() {
        return 4;
    }

    @Override
    public char obtenerInicial() {
        return 'A';
    }
    
    @Override
    public String obtenerNombre() {
        return "Acorazado";
    }
}
