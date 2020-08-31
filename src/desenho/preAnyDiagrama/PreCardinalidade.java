/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.preAnyDiagrama;

import controlador.Editor;
import controlador.Diagrama;
import controlador.apoios.TreeItem;
import controlador.inspector.InspectorProperty;
import desenho.formas.FormaTextoBase;
import desenho.linhas.Linha;
import desenho.linhas.PontoDeLinha;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import util.DesenhadorDeTexto;

/**
 *
 * @author ccandido
 */
public class PreCardinalidade extends FormaTextoBase {

    private static final long serialVersionUID = 7310412510571815118L;

    public PreCardinalidade(Diagrama modelo) {
        super(modelo);
        AceitaAjusteAutmatico = false;
    }

    public PreCardinalidade(Diagrama modelo, String texto) {
        super(modelo, texto);
        AceitaAjusteAutmatico = false;
    }

    public void Posicione() {
        if (isMovimentacaoManual() || (Fixo == null) || isSelecionado() || !Fixo.isEstaLigado()) {
            return;
        }
        DirectPosicione();
    }

    private void DirectPosicione() {
        int lado = Fixo.getLado();
        int x = 0, y = 0;
        //# Mudança: com os comentários abaixo, o autoposicionamento da CARD se dará sempre: à direita e acima da linha evitando a sobreposição.
        //PontoDeLinha outraPonta = Fixo.getDono().getOutraPonta(Fixo);
        int corr = 4;
        switch (lado) {
            case 0:
            case 2:
                if (lado == 0) {
                    x = Fixo.getLeft() - getWidth() - 2 * distSelecao;
                } else {
                    x = Fixo.getLeft() + Fixo.getWidth() + 2 * distSelecao;
                }
                //if (outraPonta.getTop() >= Fixo.getTop()) {
                    y = Fixo.getTop() - getHeight() - distSelecao + corr;
                //} else {
                //    y = Fixo.getTop() + Fixo.getHeight() + distSelecao - corr;
                //}
                break;
            case 1:
            case 3:
                if (lado == 1) {
                    y = Fixo.getTop() - getHeight() - 2 * distSelecao;
                } else {
                    y = Fixo.getTop() + Fixo.getHeight() + 2 * distSelecao;
                }
                //if (outraPonta.getLeft() >= Fixo.getLeft()) {
                    x = Fixo.getLeft() - getWidth() - distSelecao + corr;
                //} else {
                //    x = Fixo.getLeft() + Fixo.getWidth() + distSelecao - corr;
                //}
                break;
        }
        setLocation(x, y);
    }

    private String papel = "";

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        if (this.papel == null ? papel != null : !this.papel.equals(papel)) {
            this.papel = papel;
            InvalidateArea();
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="Tratamento da cardinalidade">
    public enum TiposCard {

        C11, C01, C1N, C0N
    }
    private TiposCard Card = TiposCard.C0N;

    public TiposCard getCard() {
        return Card;
    }

    public void setCard(TiposCard Card) {
        if (this.Card != Card) {
            this.Card = Card;
            InvalidateArea();
        }
    }

    public void setCard(int Card) {
        try {
        setCard(TiposCard.values()[Card]);
        } catch (Exception e) {
            setCard(TiposCard.C0N);
        }
    }

    public String CardToString(TiposCard card) {
        String res = "(?,?)";
        if (card == null) {
            return res;
        }
        switch (card) {
            case C01:
                res = "(0,1)";
                break;
            case C11:
                res = "(1,1)";
                break;
            case C1N:
                res = "(1,n)";
                break;
            case C0N:
                res = "(0,n)";
                break;
        }
        return res;
    }

    public static String CardToString(int c) {
        String res = "(?,?)";
        if (c < 0 || c > TiposCard.values().length) {
            return res;
        }
        TiposCard card = TiposCard.values()[c];
        switch (card) {
            case C01:
                res = "(0,1)";
                break;
            case C11:
                res = "(1,1)";
                break;
            case C1N:
                res = "(1,n)";
                break;
            case C0N:
                res = "(0,n)";
                break;
        }
        return res;
    }

    public final String CardToString() {
        return CardToString(getCard());
    }
    
    public final String FullCard() {
        String p = getPapel();
        if (p == null || "".equals(p)) {
            return CardToString();
        } else {
            return CardToString() + " " + getPapel();
        }
    }

    public TiposCard StringToCard(String card) {
        for (TiposCard c : TiposCard.values()) {
            if (card.equals(CardToString(c))) {
                return c;
            }
        }
        return TiposCard.C0N;
    }

    public TiposCard IntToCard(int card) {
        if (card >= 0 && card < TiposCard.values().length) {
            return TiposCard.values()[card];
        }
        return TiposCard.C0N;
    }

    public int CardToInt(TiposCard card) {
        return card.ordinal();
    }

    public int CardToInt() {
        return CardToInt(getCard());
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Propriedades de comportamento">
    private boolean TamanhoAutmatico = true;
    private boolean MovimentacaoManual = false;

    public boolean isMovimentacaoManual() {
        return MovimentacaoManual;
    }

    public void setMovimentacaoManual(boolean MovimentacaoManual) {
        if (this.MovimentacaoManual != MovimentacaoManual) {
            this.MovimentacaoManual = MovimentacaoManual;

            if (this.MovimentacaoManual || (Fixo == null) || getMaster().IsMultSelecionado() || !Fixo.isEstaLigado()) {
                return;
            }
            DirectPosicione();
            Reposicione();
        }
    }

    public boolean isTamanhoAutmatico() {
        return TamanhoAutmatico;
    }

    public void setTamanhoAutmatico(boolean TamanhoAutmatico) {
        if (this.TamanhoAutmatico != TamanhoAutmatico) {
            this.TamanhoAutmatico = TamanhoAutmatico;
            InvalidateArea();
        }
    }
    // </editor-fold>

    @Override
    public void DoPaint(Graphics2D g) {
        if (!isVisible()) {
            return;
        }
        if (TamanhoAutmatico) {
            int tamLetra =  g.getFontMetrics(getFont()).stringWidth("M");
            int largura = g.getFontMetrics(getFont()).stringWidth(FullCard()) + tamLetra;//+ distSelecao * 2;
            int altura = g.getFontMetrics(getFont()).getHeight();
            if (getWidth() != largura || getHeight() != altura) {
                setStopRaize(true);
                setWidth(largura);
                setHeight(altura);
                setStopRaize(false);
                Posicione();
                if (isSelecionado()) Reposicione();
            }
        }
        super.DoPaint(g);
    }

    private transient double z = 0.0;

    @Override
    public void PinteTexto(Graphics2D g) {
        //no caso de mudança no zoom, um novo TextoFormatado deve ser criado.
        if (getMaster().getZoom() != z) {
            setTextoFormatado(null);
            z = getMaster().getZoom();
        }
        DesenhadorDeTexto txtf = getTextoFormatado();
        txtf.PinteTexto(g, getForeColor(), getArea(), FullCard());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        MovimentacaoManual = true;
    }
    
    private PontoDeLinha Fixo = null;

    public void Fixe(PontoDeLinha pt) {
        Fixo = pt;
        if (Fixo == null) {
            setVisible(false);
        }
        MovimentacaoManual = false;
    }
    
    private Linha ligadoA = null;

   
    public Linha getLigadoA() {
        return ligadoA;
    }

    public void setLigadoA(Linha ligadoA) {
        this.ligadoA = ligadoA;
    }
    
    @Override
    public boolean AskToDelete() {
            if (getLigadoA() != null) {
                if (getLigadoA().isSelecionado()) {
                    return false;
                } else {
                    return getMaster().Remove(getLigadoA(), false);
                }
            }
        return super.AskToDelete();
    }
    
    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        GP = super.CompleteGenerateProperty(GP);
        if (getLigadoA() != null) {
            GP.add(InspectorProperty.PropertyFactoryTexto("cardinalidade.papel", "setPapel", getPapel()));
            GP.add(InspectorProperty.PropertyFactorySeparador("ligacoes"));
            String tmp = Editor.fromConfiguracao.getValor("Inspector.obj.diagrama.linha.nome");
            GP.add(InspectorProperty.PropertyFactoryActionSelect(tmp, tmp, String.valueOf(getLigadoA().getID())));
        }
        return GP;
    }
    
    /**
     * Este objeto pode ser carregdo no InfoDiagrama_LoadFromXML ? - Cardinalidades: não!
     */
    @Override
    public boolean getIsLoadedFromXML() {
        return false;
    }

    @Override
    public boolean MostreSeParaExibicao(TreeItem root) {
        return false;
    }

}
