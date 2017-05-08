/*
 * Copyright (C) 2016 chcan
 */
package diagramas.livre;
import controlador.Diagrama;
import desenho.preDiagrama.baseDrawer;

public class LivreDrawer extends baseDrawer {

    private static final long serialVersionUID = 8822609460359454495L;
    
    public LivreDrawer(Diagrama diagrama, String texto) {
        super(diagrama, texto);
    }

    public LivreDrawer(Diagrama diagrama) {
        super(diagrama);
    }
        
}
