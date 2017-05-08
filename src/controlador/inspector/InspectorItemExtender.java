/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.inspector;

import controlador.inspector.InspectorExtenderEditor.TipoDeAcao;
import java.awt.event.MouseEvent;

/**
 *
 * @author ccandido
 */
public class InspectorItemExtender extends InspectorItemBase {

    public InspectorItemExtender(Inspector criador) {
        super(criador);
        setOndeEditar(criador.TipoDlg);
    }
    
    public InspectorItemExtender(Inspector criador, TipoDeAcao tda) {
        super(criador);
        setOndeEditar(criador.TipoDlg);
        setMyAction(tda);
    }
    
    public InspectorItemExtender(){
        super();
    }

    private InspectorExtenderEditor.TipoDeAcao myAction = InspectorExtenderEditor.TipoDeAcao.tpAcaoDlgTexto;

    public TipoDeAcao getMyAction() {
        return myAction;
    }

    public final void setMyAction(TipoDeAcao myAction) {
        this.myAction = myAction;
    }

    @Override
    public void mouseDblClick(MouseEvent e) {
        super.mouseDblClick(e);
        Criador.TipoDlg.RunDlg();
    }
    
    @Override
    protected void setSelecionado(boolean selecionado) {
        super.setSelecionado(selecionado);
        if ((getOndeEditar() != null) && (selecionado)) {
            InspectorExtenderEditor xe = (InspectorExtenderEditor)getOndeEditar();
            xe.OrganizeSize();
            xe.setAcaoTipo(getMyAction());
        }
    }
    
    public void ExternalRun() {
        Criador.TipoDlg.RunDlg();
    }
}
