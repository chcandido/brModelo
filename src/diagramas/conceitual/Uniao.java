/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.conceitual;

import controlador.Controler;
import controlador.Editor;
import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import desenho.formas.Forma;
import desenho.preAnyDiagrama.PreEntidade;
import desenho.preAnyDiagrama.PreUniao;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ccandido
 */
public class Uniao extends PreUniao {

    private static final long serialVersionUID = 8765681402109033957L;

    public Uniao(Diagrama modelo) {
        super(modelo);
        nodic = false;
    }

    public Uniao(Diagrama modelo, String texto) {
        super(modelo, texto);
        nodic = false;
    }

    @Override
    protected boolean FinderLinked(Forma quem, Forma origem) {
        ArrayList<Forma> outras = getListaDeFormasLigadas(origem);
        for (Forma f : outras) {
            if (f == quem) {
                return true;
            }
            List<Uniao> esp = f.getListaDeFormasLigadas(Uniao.class).stream().filter(o -> o != this).map(o -> (Uniao) o).collect(Collectors.toList());
            for (Uniao f2 : esp) {
                if (f2.FinderLinked(quem, f)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();
        res.add(InspectorProperty.PropertyFactoryMenu("direcao", "setDirecaoFromInspector", getDirecaoForInspector(), Editor.fromConfiguracao.getLstDirecao(Controler.Comandos.cmdEspecializacao)));
        PreEntidade pe = LigadaAoPontoPrincipal();
        if (pe != null) {
            res.add(
                    InspectorProperty.PropertyFactoryActionSelect(Editor.fromConfiguracao.getValor("Inspector.obj.uniao.resultante"),
                            pe.getTexto(),
                            String.valueOf(pe.getID()))
            );
        } else {
            res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("uniao.resultante", "{}"));
        }
        //res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("uniao.resultante", pe == null? "": pe.getTexto()));
        return res;
    }
}
