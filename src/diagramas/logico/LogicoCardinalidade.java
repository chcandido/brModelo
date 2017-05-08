/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.logico;

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
public class LogicoCardinalidade  extends PreCardinalidade {

    private static final long serialVersionUID = 1456264234125393022L;

    public LogicoCardinalidade(Diagrama modelo) {
        super(modelo);
        setCanBeDeleted(false);
    }
    
    public LogicoCardinalidade(Diagrama modelo, String texto) {
        super(modelo, texto);
        setCanBeDeleted(false);
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();

        String[] afetados = new String[]{"setWidth", "setHeight"};

        InspectorProperty tmp = InspectorProperty.FindByProperty(res, "setHeight");
        int p = res.indexOf(tmp) + 1;

        res.add(p, InspectorProperty.PropertyFactorySN("cardinalidade.tamanhoautmatico", "setTamanhoAutmatico", isTamanhoAutmatico()).AddCondicaoForFalse(afetados));
        res.add(p + 1, InspectorProperty.PropertyFactorySN("cardinalidade.movimentacaomanual", "setMovimentacaoManual", isMovimentacaoManual()));

        //res.add(InspectorProperty.PropertyFactorySeparador("mer"));
        ArrayList<String> strCards = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            strCards.add(CardToString(IntToCard(i)));
        }
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
        me.appendChild(util.XMLGenerate.ValorFonte(doc, getFont()));
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
        String tmp = util.XMLGenerate.getValorStringFrom(me, "Papel");
        setPapel(tmp);

        return true;
    }

    @Override
    public void setCard(TiposCard Card) {
        if (Card != this.getCard()) {
            super.setCard(Card);
            if (getLigadoA() instanceof LogicoLinha) {
                LogicoLinha lin = ((LogicoLinha)getLigadoA());
                lin.AlterCard(this);
                lin.ajusteSeta();
            }
        }
    }
    
    @Override
    public boolean isAlinhavel() {
        return false;
    }

}
