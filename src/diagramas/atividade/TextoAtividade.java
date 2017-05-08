/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.atividade;

import controlador.Diagrama;
import controlador.Editor;
import controlador.inspector.InspectorProperty;
import desenho.preAnyDiagrama.PreTextoApenso;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 *
 * @author ccandido
 */
public class TextoAtividade extends PreTextoApenso {

    private static final long serialVersionUID = 1364161501841228409L;

    public TextoAtividade(Diagrama modelo) {
        super(modelo);
    }

    public TextoAtividade(Diagrama modelo, String texto) {
        super(modelo, texto);
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();

        InspectorProperty tmp = InspectorProperty.FindByProperty(res, "setTexto");
        tmp.ReSetCaptionFromConfig("texto");
        tmp.tipo = InspectorProperty.TipoDeProperty.tpTextoLongo;

        res.add(InspectorProperty.PropertyFactoryMenu("texto.alinhamento", "setAlinhamentoByInt", getAlinhamento().ordinal(), Editor.fromConfiguracao.getLstTextoAlin()));
        res.add(InspectorProperty.PropertyFactorySN("texto.alinhamento.v", "setCentrarVertical", isCentrarVertical()));

        tmp = InspectorProperty.FindByProperty(res, "setForeColor");
        tmp.ReSetCaptionFromConfig("texto.forecolor");

        tmp = InspectorProperty.FindByProperty(res, "setTextoAdicional");
        res.remove(tmp);

        res.add(InspectorProperty.PropertyFactorySN("texto.autosize", "setAutosize", isAutosize()));

        res.add(InspectorProperty.PropertyFactorySeparador("texto.atreladoalinha"));

        res.add(InspectorProperty.PropertyFactorySN("texto.movimentacaomanual", "setMovimentacaoManual", isMovimentacaoManual()));

        return res;
    }

    private transient double z = 0.0;
    @Override
    public void PinteTexto(Graphics2D g) {
        //no caso de mudança no zoom, um novo TextoFormatado deve ser criado.
        if (getMaster().getZoom() != z) {
            setTextoFormatado(null);
            z = getMaster().getZoom();
        }
        getTextoFormatado().PinteTexto(g, getForeColor(), getArea(), "[" + getTexto() + "]");
    }
    
    @Override
    public boolean isAlinhavel() {
        return false;
    }

 }
