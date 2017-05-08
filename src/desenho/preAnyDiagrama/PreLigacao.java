/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.preAnyDiagrama;

import controlador.Editor;
import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import desenho.FormaElementar;
import desenho.formas.Forma;
import desenho.linhas.PontoDeLinha;
import desenho.linhas.SuperLinha;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author ccandido
 */
public class PreLigacao extends SuperLinha {

    private static final long serialVersionUID = 4654449532952974461L;

    // <editor-fold defaultstate="collapsed" desc="Criação">
    public PreLigacao(Diagrama diagrama) {
        super(diagrama);
        setInteligente(true);
    }

    public PreLigacao(Diagrama diagrama, PreCardinalidade aCard) {
        super(diagrama);
        setInteligente(true);
        InitLCC(aCard);
        diagrama.Remove(aCard, false);
    }

    protected final void InitLCC(PreCardinalidade aCard) {
        setCard(aCard);
        PrepareCardinalidade();
    }

    //protected boolean cardNome = false;

//    public PreLigacao(Diagrama diagrama, boolean cardNome) {
//        this(diagrama);
//        //this.cardNome = cardNome;
//    }
    // </editor-fold>
    
    private PreCardinalidade Card;

    public PreCardinalidade getCard() {
        return Card;
    }

    public void setCard(PreCardinalidade aCard) {
        if (this.Card != aCard) {
            if (this.Card != null) {
                Card.setLigadoA(null);
            }
            this.Card = aCard;
            if (this.Card != null) {
                Card.setLigadoA(this);
            }
        }
    }

    public boolean isCardVisible() {
        if (Card != null) {
            return Card.isVisible();
        } else {
            return false;
        }
    }

    public void PrepareCardinalidade() {
//        PreCardinalidade card = getCard();
//        if (card == null) {
//            return;
//        }
//        if (getPontaA() == null || getPontaB() == null) {
//            card.setVisible(false);
//            return;
//        }
//        if (!getPontaA().isEstaLigado() || !getPontaB().isEstaLigado()) {
//            card.Fixe(null);
//            return;
//        }
//
//        boolean t1 = getFormaPontaA() instanceof PreEntidade;
//        boolean t1b = getFormaPontaB() instanceof PreRelacionamento;
//        if (t1 && t1b) {
//            card.setVisible(true);
//            card.Fixe(getPontaA());
//            card.Posicione();
//            return;
//        }
//
//        boolean t2 = getFormaPontaB() instanceof PreEntidade;
//        boolean t2b = getFormaPontaA() instanceof PreRelacionamento;
//        if (t2 && t2b) {
//            card.setVisible(true);
//            card.Fixe(getPontaB());
//            card.Posicione();
//            return;
//        }
//        card.Fixe(null);
    }

    @Override
    public void reSetBounds() {
        super.reSetBounds();
        PrepareCardinalidade();
    }

    @Override
    public boolean AnexePontos() {
        boolean res = super.AnexePontos();
        if (!res) {
            PrepareCardinalidade();
        }
        return res;
    }

    @Override
    public boolean Destroy() {
        ArrayList<PontoDeLinha> pontos = getPontos();
        for (PontoDeLinha pdl : pontos) {
            pdl.Destroy();
        }
        if (Card != null) {
            //Card.setRemovido(true);
            Card.setCanBeDeleted(true);
            getMaster().Remove(Card, false);
            //mudei aqui e não testei - comentei abaixo.
            //Card.setCanBeDeleted(false); //se estiver na lista de selecionados não fará nada.
        }
        return super.Destroy();
    }

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        //GP.add(InspectorProperty.PropertyFactorySeparador("seta.titulo"));
        GP = super.CompleteGenerateProperty(GP);
        
        GP.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "linha.centre").setTag(140916));
        
        ArrayList<Forma> lst = new ArrayList<>();
        if (getPontaA().getEm() != null) {
            lst.add(getPontaA().getEm());
        }
        if (getPontaB().getEm() != null) {
            lst.add(getPontaB().getEm());
        }
        if (isCardVisible()) {
            lst.add(0, Card);
        }
        boolean ja = false;
        for (Forma f : lst) {
            InspectorProperty ipp = InspectorProperty.PropertyFactoryActionSelect(Editor.fromConfiguracao.getValor("diagrama." + Editor.getClassTexto(f) + ".nome"),
                    f.getTexto(),
                    String.valueOf(f.getID()));
            if (!ja) {
                ja = true;
                GP.add(InspectorProperty.PropertyFactorySeparador("ligacoes"));
            }
            GP.add(ipp);
        }
        return GP;
    }

   @Override
    public FormaElementar getSub(int i) {
        if (i == 0) return getCard();
        return super.getSub(i);
    }

    
    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);
        if (Tag == 140916) {
            CentralizarByEntidade();
        }
    }

    public void CentralizarByEntidade() {
        int L = -1;
        Forma A;
        PontoDeLinha P;
        if (getFormaPontaA() instanceof PreEntidade || getFormaPontaA() instanceof PreRelacionamento) {
            L = getPontaA().getLado();
            A = getFormaPontaA();
            P = getPontaA();
            Point pt = A.getPontosCalculados()[L];
            P.setCentro(pt);
        }
        if (getFormaPontaB() instanceof PreEntidade || getFormaPontaB() instanceof PreRelacionamento) {
            L = getPontaB().getLado();
            A = getFormaPontaB();
            P = getPontaB();
            Point pt = A.getPontosCalculados()[L];
            P.setCentro(pt);
        }
        if (L > -1) {
            OrganizeLinha();
            reSetBounds();
            DoMuda();
        }
    }
    
}