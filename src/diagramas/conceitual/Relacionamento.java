/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.conceitual;

import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import desenho.preAnyDiagrama.PreRelacionamento;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author ccandido
 */
public class Relacionamento extends PreRelacionamento {

    private static final long serialVersionUID = 260085365330070656L;

    public Relacionamento(Diagrama modelo) {
        super(modelo);
        nodic = false;
    }

    public Relacionamento(Diagrama modelo, String texto) {
        super(modelo, texto);
        nodic = false;
    }

    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);
        if (Tag == 123) {
            if (mudouAtributos) {
                DoMuda();
            }
        }
        if (Tag == 90816) {
            ConverteEntAss();
            DoMuda();
        }
    }

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        ArrayList<InspectorProperty> res = GP;
        res.add(InspectorProperty.PropertyFactoryApenasLeituraSN("relacionamento.autorelacionamento", isAutoRelacionamento()));
        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "relacionamento.convertereass").setTag(90816).PropertyForceDisable(isAutoRelacionamento()));
        super.CompleteGenerateProperty(GP);
        return GP;
    }

    private void ConverteEntAss() {
        Rectangle res = getBounds();
        diagramas.conceitual.Relacionamento interno = new Relacionamento(getMaster());
        interno.SetTexto(getTexto());
        interno.SetBounds(res);
        
        interno.setObservacao(getObservacao());
        interno.setTextoAdicional(getTextoAdicional());
        interno.setBackColor(getBackColor());
        interno.setForeColorWithOutRepaint(getForeColor());
        interno.setFont(getFont());
        
        int tmp = (distSelecao * 4);
        res = new Rectangle(res.x - tmp, res.y - tmp, res.width + 2 * tmp + 2, res.height + 2 * tmp + 2);

        diagramas.conceitual.EntidadeAssociativa entA = new EntidadeAssociativa(getMaster(), "EntidadeAssociativa", interno);
        entA.SetBounds(res);

        getListaDePontosLigados().stream().forEach(p -> {
            p.SetEm(interno);
        });

        entA.ReenquadreInterno();
        entA.Reenquadre();
        entA.BringToFront();
        
        getMaster().Remove(this, true);
        getMaster().setSelecionado(entA);
        
        entA.DoMuda();
    }
}
