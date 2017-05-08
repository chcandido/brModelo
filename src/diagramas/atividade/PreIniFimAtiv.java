/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.atividade;

import controlador.Diagrama;
import desenho.formas.Forma;
import desenho.formas.FormaCircular;
import desenho.linhas.Linha;
import java.awt.Graphics2D;

/**
 *
 * @author ccandido
 */
public class PreIniFimAtiv extends FormaCircular {

    private static final long serialVersionUID = -499379874848514932L;

    public PreIniFimAtiv(Diagrama modelo) {
        super(modelo);
        editFonte = false;
        showOrgDiag = true;
    }

    public PreIniFimAtiv(Diagrama modelo, String texto) {
        super(modelo, texto);
        editFonte = false;
        showOrgDiag = true;
    }

//    @Override
//    public boolean CanLiga(Forma forma, Linha lin) {
//        if (forma == null) {
//            return true;
//        }
//        if (super.CanLiga(forma, lin)) {
//            if (forma instanceof PreAtributo) {
//                return forma.CanLiga(this, lin);
//            }
//            if (forma instanceof PreEntidade) {
//                ArrayList<Forma> lst = getListaDeFormasLigadasNaoExclusiva(PreEntidade.class);
//                if (AutoRelacionamento(lst) || getPrincipal() == forma) {
//                    return false;
//                }
//                if (isSubComponente()) { //entidade ass.
//                    if (lst.indexOf(forma) > -1) {
//                        return false; //NÃO PODE SER AUTO-REL.
//                    }
//                } else {
//                    if (lst.indexOf(forma) > -1 && lst.size() > 1) {
//                        return false; //já está ligado e possui outra ligação, por isso não pode ser alto-rel.
//                    }
//                }
//                return true;
//            }
//        }
//        return false;
//    }
    @Override
    public boolean CanLiga(Forma forma, Linha lin) {
        return true;
//        if (forma == null) {
//            return true;
//        }
//        if (super.CanLiga(forma, lin) && (!(forma instanceof PreEntidade))) {
//            return forma.CanLiga(this, lin);
//        }
//        return false;
    }

    @Override
    public void PinteTexto(Graphics2D g) {

    }

    @Override
    public void OrganizeDiagrama() {
        OrganizeFluxo();
    }

}
