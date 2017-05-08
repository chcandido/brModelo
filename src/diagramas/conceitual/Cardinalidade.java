/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.conceitual;

import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import desenho.preAnyDiagrama.PreCardinalidade;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ccandido
 */
public class Cardinalidade extends PreCardinalidade {

    private static final long serialVersionUID = -9219630166211153571L;

    public Cardinalidade(Diagrama modelo) {
        super(modelo);
        setCanBeDeleted(false);
        nodic = false;
    }

    public Cardinalidade(Diagrama modelo, String texto) {
        super(modelo, texto);
        setCanBeDeleted(false);
        nodic = false;
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();
        
        String[] afetados = new String[] {"setWidth", "setHeight"};
        
        InspectorProperty tmp = InspectorProperty.FindByProperty(res, "setHeight");
        int p = res.indexOf(tmp) + 1;
        
        res.add(p, InspectorProperty.PropertyFactorySN("cardinalidade.tamanhoautmatico", "setTamanhoAutmatico", isTamanhoAutmatico()).AddCondicaoForFalse(afetados));
        res.add(p + 1, InspectorProperty.PropertyFactorySN("cardinalidade.movimentacaomanual", "setMovimentacaoManual", isMovimentacaoManual()));

        //res.add(InspectorProperty.PropertyFactorySeparador("mer"));
        ArrayList<String> strCards = new ArrayList<>();
        for(int i = 0; i < 4; i++) strCards.add(CardToString(IntToCard(i)));
        res.add(InspectorProperty.PropertyFactoryMenu("cardinalidade.card", "setCard", CardToInt(), strCards));

        return res;
        
    }
    
    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "TamanhoAutmatico", isTamanhoAutmatico()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Card", CardToInt()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "MovimentacaoManual", isMovimentacaoManual()));
        me.appendChild(util.XMLGenerate.ValorString(doc, "Papel", getPapel()));
        //me.appendChild(util.XMLGenerate.ValorRefFormElementar(doc, "LigadoA", getLigadoA()));
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        int l = util.XMLGenerate.getValorIntegerFrom(me, "Card");
        if (l != -1) {
            setCard(l);
        }
        setTamanhoAutmatico(util.XMLGenerate.getValorBooleanFrom(me, "TamanhoAutmatico"));
        setMovimentacaoManual(util.XMLGenerate.getValorBooleanFrom(me, "MovimentacaoManual"));
        setPapel(util.XMLGenerate.getValorStringFrom(me, "Papel"));

        return true;
    }
    
    @Override
    public boolean isAlinhavel() {
        return false;
    }
    
}
