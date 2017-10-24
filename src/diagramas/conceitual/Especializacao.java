/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.conceitual;

import controlador.Controler;
import controlador.Diagrama;
import controlador.Editor;
import controlador.inspector.InspectorProperty;
import desenho.formas.Forma;
import desenho.preAnyDiagrama.PreEntidade;
import desenho.preAnyDiagrama.PreEspecializacao;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ccandido
 */
public class Especializacao extends PreEspecializacao {

    private static final long serialVersionUID = 3595682719755602909L;

    public Especializacao(Diagrama modelo) {
        super(modelo);
        nodic = false;
        paintParcial = true;
    }

    public Especializacao(Diagrama modelo, String texto) {
        super(modelo, texto);
        nodic = false;
        paintParcial = true;
    }

    // <editor-fold defaultstate="collapsed" desc="Campos">
//    private boolean Parcial = false;
//
    @Override
    public boolean isParcial() {
        return (this.getListaDeFormasLigadas().size() > 1 && LigadaAoPontoPrincipal() != null) && super.isParcial();
    }

    public boolean isTotal() {
        return (!isParcial() &&  LigadaAoPontoPrincipal() != null);// (this.getListaDeFormasLigadas().size() > 1 && LigadaAoPontoPrincipal() != null) && (!super.isParcial());
    }
    
    public void setTotal(boolean sn) {
        setParcial(!sn);
    }

    public boolean isNaoExclusiva() {
        final PreEntidade tmp = LigadaAoPontoPrincipal();
        return this.getListaDeFormasLigadas().size() > 1 && tmp != null && (
                    //tmp.getListaDeFormasLigadas().stream().filter(f -> f instanceof Especializacao).count() > 1
                    tmp.getListaDeFormasLigadas().stream().filter(f -> f instanceof Especializacao).map(e -> (Especializacao)e).filter(e -> e.LigadaAoPontoPrincipal() == tmp).count() > 1
                );
    }

    public boolean isExclusiva() {
        final PreEntidade tmp = LigadaAoPontoPrincipal();
        return this.getListaDeFormasLigadas().size() > 1 && tmp != null && (
                    tmp.getListaDeFormasLigadas().stream().filter(f -> f instanceof Especializacao).map(e -> (Especializacao)e).filter(e -> e.LigadaAoPontoPrincipal() == tmp).count() == 1
                );
    }
    
//
//    public void setParcial(boolean Parcial) {
//        this.Parcial = Parcial;
//        if (this.Parcial) toPaintTxt = "p"; else toPaintTxt = "";
//        InvalidateArea();
//    }
    // </editor-fold>
    
    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();
        res.add(InspectorProperty.PropertyFactoryMenu("direcao", "setDirecaoFromInspector", getDirecaoForInspector(), Editor.fromConfiguracao.getLstDirecao(Controler.Comandos.cmdEspecializacao)));

        if ((!isExclusiva() && !isNaoExclusiva()) ||(!isParcial() && !isTotal())) {
            res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("especializacao.formacao", Editor.fromConfiguracao.getValor("Inspector.obj.especializacao.malformada")));
        } else {
            //res.add(InspectorProperty.PropertyFactoryApenasLeituraSN("especializacao.parcial", isParcial()));
            res.add(InspectorProperty.PropertyFactorySN("especializacao.parcial", "setParcial", isParcial()));
            //res.add(InspectorProperty.PropertyFactoryApenasLeituraSN("especializacao.total", isTotal()));
            res.add(InspectorProperty.PropertyFactorySN("especializacao.total", "setTotal", isTotal()));
            
            res.add(InspectorProperty.PropertyFactoryApenasLeituraSN("especializacao.exclusiva", isExclusiva()));
            res.add(InspectorProperty.PropertyFactoryApenasLeituraSN("especializacao.naoexclusiva", isNaoExclusiva()));
        }
        PreEntidade pe = LigadaAoPontoPrincipal();
        if (pe != null) {
            res.add(
                    InspectorProperty.PropertyFactoryActionSelect(Editor.fromConfiguracao.getValor("Inspector.obj.especializacao.apartirde"),
                    pe.getTexto(),
                    String.valueOf(pe.getID()))
                    );
        } else {
            res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("especializacao.apartirde", "{}"));
        }
        return res;
    }
    
//    @Override
//    public boolean LoadFromXML(Element me, boolean colando) {
//        return super.LoadFromXML(me, colando);
//    }
    
    @Override
    protected boolean FinderLinked(Forma quem, Forma origem) {
        ArrayList<Forma> outras = getListaDeFormasLigadas(origem);
        for (Forma f : outras) {
            if (f == quem) {
                return true;
            }
            List<Especializacao> esp = f.getListaDeFormasLigadas(Especializacao.class).stream().filter(o -> o != this).map(o -> (Especializacao)o).collect(Collectors.toList());
            
            if (esp.stream().anyMatch((f2) -> (f2.FinderLinked(quem, f)))) {
                return true;
            }
        }
        return false;
    }
    
}
