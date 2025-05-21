// Portaaviones.java
package batalla.barcos;

import batalla.Barco;

public class Portaaviones extends Barco {

    @Override
    public int obtenerTamano() {
        return 5;
    }

    @Override
    public char obtenerInicial() {
        return 'P';
    }
    
    @Override
    public String obtenerNombre() {
        return "Portaaviones";
    }
}
