/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package desenho.preAnyDiagrama;

import controlador.Diagrama;
import diagramas.atividade.SetaAtividade;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author ccandido
 */
public class PreTextoApenso extends PreTexto{

    private static final long serialVersionUID = 6920592210168176051L;

    public PreTextoApenso(Diagrama modelo) {
        super(modelo);
        AceitaAjusteAutmatico = false;
        //setTextoSimples(false);
    }

    public PreTextoApenso(Diagrama modelo, String texto) {
        super(modelo, texto);
        AceitaAjusteAutmatico = false;
        //setTextoSimples(false);
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);

        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Alinhamento", getAlinhamento().ordinal()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "CentrarVertical", isCentrarVertical()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Tipo", getTipo().ordinal()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Autosize", isAutosize()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "MovimentacaoManual", isMovimentacaoManual()));

        //remover dicionário do XML do objeto.
        NodeList nl = me.getElementsByTagName("Dicionario");
        if (nl != null && nl.getLength() > 0) {
            me.removeChild(nl.item(0));
        }

    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        int l = util.XMLGenerate.getValorIntegerFrom(me, "GDirecao");
        if (l != -1) {
            setGDirecao(l);
        }
        setAlinhamentoByInt(util.XMLGenerate.getValorIntegerFrom(me, "Alinhamento"));
        setCentrarVertical(util.XMLGenerate.getValorBooleanFrom(me, "CentrarVertical"));
        setTipobyInt(util.XMLGenerate.getValorIntegerFrom(me, "Tipo"));
        Color c = util.XMLGenerate.getValorColorFrom(me, "BackColor");
        if (c != null) {
            setBackColor(c);
        }
        setMovimentacaoManual(util.XMLGenerate.getValorBooleanFrom(me, "MovimentacaoManual"));
        setAutosize(util.XMLGenerate.getValorBooleanFrom(me, "Autosize"));
        return true;
    }

    // <editor-fold defaultstate="collapsed" desc="Como Tag">
    private boolean MovimentacaoManual = false;

    public boolean isMovimentacaoManual() {
        return MovimentacaoManual;
    }

    public void setMovimentacaoManual(boolean MovimentacaoManual) {
        if (this.MovimentacaoManual != MovimentacaoManual) {
            this.MovimentacaoManual = MovimentacaoManual;

            if (this.MovimentacaoManual || (LinhaMestre == null) || getMaster().IsMultSelecionado()) {
                return;
            }
            LinhaMestre.PrepareTexto();
            //Reenquadre();
            Reposicione();
        }
    }

    public void SetLinhaMestre(SetaAtividade LinhaMestre) {
        if (this.LinhaMestre == LinhaMestre) {
            return;
        }
        this.LinhaMestre = LinhaMestre;
        MovimentacaoManual = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        MovimentacaoManual = true;
    }

    private PreLigacaoSetaComApenso LinhaMestre = null;

    public PreLigacaoSetaComApenso getLinhaMestre() {
        return LinhaMestre;
    }

    public void SetLinhaMestre(PreLigacaoSetaComApenso LinhaMestre) {
        if (this.LinhaMestre == LinhaMestre) {
            return;
        }
        this.LinhaMestre =  LinhaMestre;
        MovimentacaoManual = false;
    }

    public void setLinhaMestre(PreLigacaoSetaComApenso LinhaMestre) {
        if (this.LinhaMestre == LinhaMestre) {
            return;
        }
        if (this.LinhaMestre != null) {
            this.LinhaMestre.SetTag(null);
        }
        this.LinhaMestre = LinhaMestre;
        if (this.LinhaMestre != null) {
            this.LinhaMestre.SetTag(this);
        }
        MovimentacaoManual = false;
    }

    @Override
    public void Posicione() {
        if (isMovimentacaoManual() || (LinhaMestre == null) || isSelecionado()) {
            return;
        }
        LinhaMestre.PrepareTexto();
    }

    // </editor-fold>
    
    @Override
    public boolean Destroy() {
        setLinhaMestre(null);
        return super.Destroy();
    }

    @Override
    public boolean AskToDelete() {
        if (LinhaMestre != null) {
            if (LinhaMestre.isSelecionado()) {
                return false;
            } else {
                return getMaster().Remove(LinhaMestre, false);
            }
        }
        return super.AskToDelete();
    }

    @Override
    protected void ReSizedByAutoSize() {
        super.ReSizedByAutoSize(); //To change body of generated methods, choose Tools | Templates.
        if (this.MovimentacaoManual || (LinhaMestre == null) || getMaster().IsMultSelecionado()) {
            return;
        }
        LinhaMestre.PrepareTexto();
        Reposicione();
    }

    private transient double z = 0.0;

    @Override
    public void PinteTexto(Graphics2D g) {
        //no caso de mudança no zoom, um novo TextoFormatado deve ser criado.
        if (getMaster().getZoom() != z) {
            setTextoFormatado(null);
            z = getMaster().getZoom();
        }
        getTextoFormatado().PinteTexto(g, getForeColor(), getArea(), getTexto());
    }

    /**
     * Este objeto pode ser carregdo no InfoDiagrama_LoadFromXML ? - TextoAtividade: não!
     *
     * @return falso
     */
    @Override
    public boolean getIsLoadedFromXML() {
        return false;
    }
}
