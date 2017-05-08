/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.fluxo;

import controlador.Diagrama;
import controlador.Editor;
import controlador.inspector.InspectorProperty;
import desenho.preAnyDiagrama.PreTextoApenso;
import java.awt.Dimension;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ccandido
 */
public class FluxTexto extends PreTextoApenso {

    private static final long serialVersionUID = -757285006393666224L;

    public FluxTexto(Diagrama modelo) {
        super(modelo);
        setPositivo(true);
    }

    public FluxTexto(Diagrama modelo, String texto) {
        super(modelo, texto);
        setPositivo(true);
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();

        InspectorProperty tmp = InspectorProperty.FindByProperty(res, "setTexto");
        tmp.ReSetCaptionFromConfig("texto");
        tmp.tipo = InspectorProperty.TipoDeProperty.tpApenasLeituraTexto;

        ArrayList<String> sn = new ArrayList<>();
        sn.add(Editor.fromConfiguracao.getValor("Inspector.obj.fluxtexto.positivo"));
        sn.add(Editor.fromConfiguracao.getValor("Inspector.obj.fluxtexto.negativo"));
        
        res.add(InspectorProperty.PropertyFactoryMenu("fluxtexto.condicao", "setPositivoByInt", 
                getPositivoByInt(), sn));
        
        res.add(InspectorProperty.PropertyFactoryMenu("texto.alinhamento", "setAlinhamentoByInt", getAlinhamento().ordinal(), Editor.fromConfiguracao.getLstTextoAlin()));
        res.add(InspectorProperty.PropertyFactorySN("texto.alinhamento.v", "setCentrarVertical", isCentrarVertical()));

        tmp = InspectorProperty.FindByProperty(res, "setForeColor");
        tmp.ReSetCaptionFromConfig("texto.forecolor");

        tmp = InspectorProperty.FindByProperty(res, "setTextoAdicional");
        res.remove(tmp);

        //res.add(InspectorProperty.PropertyFactorySN("texto.autosize", "setAutosize", isAutosize()));

        res.add(InspectorProperty.PropertyFactorySeparador("texto.atreladoalinha"));
        res.add(InspectorProperty.PropertyFactorySN("texto.movimentacaomanual", "setMovimentacaoManual", isMovimentacaoManual()));
        return res;
    }

    private boolean positivo = true;

    public boolean isPositivo() {
        return positivo;
    }

    public final void setPositivo(boolean positivo) {
        this.positivo = positivo;
        if (isPositivo()) {
            setTexto(Editor.fromConfiguracao.getValor("Inspector.obj.fluxtexto.positivo"));
        } else {
            setTexto(Editor.fromConfiguracao.getValor("Inspector.obj.fluxtexto.negativo"));
        }
    }
    
    public int getPositivoByInt() {
        return (isPositivo()? 0: 1);
    }

    public void setPositivoByInt(int vl) {
        setPositivo(vl == 0);
    }
    
    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Positivo", isAutosize()));
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        setPositivo(util.XMLGenerate.getValorBooleanFrom(me, "Positivo"));
        return true;
    }

    @Override
    public boolean isAlinhavel() {
        return false;
    }

}
