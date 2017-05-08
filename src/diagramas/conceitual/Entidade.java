/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.conceitual;

import controlador.Diagrama;
import desenho.preAnyDiagrama.PreEntidade;

/**
 *
 * @author ccandido
 */
public class Entidade extends PreEntidade {

    private static final long serialVersionUID = -7107006599560243972L;

    public Entidade(Diagrama modelo) {
        super(modelo);
        nodic = false;
    }

    public Entidade(Diagrama modelo, String texto) {
        super(modelo, texto);
        nodic = false;
    }

    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);
        if (Tag == CONST_DO_ORGATTR) {
            if (mudouAtributos) {
                DoMuda();
            }
        }
    }

}
