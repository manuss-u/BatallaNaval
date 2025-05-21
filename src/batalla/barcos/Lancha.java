// Lancha.java
package batalla.barcos;

import batalla.Barco;

public class Lancha extends Barco {

    @Override
    public int obtenerTamano() {
        return 1;
    }

    @Override
    public char obtenerInicial() {
        return 'L';
    }
    
    @Override
    public String obtenerNombre() {
        return "Lancha";
    }
}
