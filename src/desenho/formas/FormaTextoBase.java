/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.formas;

import controlador.Diagrama;
import desenho.linhas.Linha;
import desenho.linhas.PontoDeLinha;
import java.awt.Graphics2D;
import util.DesenhadorDeTexto;

/**
 *
 * @author ccandido
 */
public class FormaTextoBase extends Forma {

    private static final long serialVersionUID = 5374766439117842226L;

    public FormaTextoBase(Diagrama modelo) {
        super(modelo);
    }

    public FormaTextoBase(Diagrama modelo, String texto) {
        super(modelo, texto);
    }

    public enum AlinhamentoTexto {
        alCentro, alEsquerda, alDireita
    }

    private AlinhamentoTexto Alinhamento = AlinhamentoTexto.alCentro;

    private boolean centrarVertical = false;
    
    public boolean isCentrarVertical() {
        return centrarVertical;
    }

    public void setCentrarVertical(boolean centrar) {
        if (centrar != centrarVertical) {
            centrarVertical = centrar;
            DesenhadorDeTexto edt = getTextoFormatado();
            edt.setCentrarTextoVertical(centrar);
            InvalidateArea();
        }
    }

    public AlinhamentoTexto getAlinhamento() {
        return Alinhamento;
    }

    public void setAlinhamentoByInt(int Alinhamento) {
        try {
            setAlinhamento(AlinhamentoTexto.values()[Alinhamento]);
        } catch (Exception e) {
        }
    }

    public void setAlinhamento(AlinhamentoTexto Alinhamento) {
        if (this.Alinhamento != Alinhamento) {
            this.Alinhamento = Alinhamento;
            DesenhadorDeTexto edt = getTextoFormatado();
            switch (Alinhamento) {
                case alCentro:
                    edt.setCentrarTextoHorizontal(true);
                    break;
                case alDireita:
                    edt.setAlinharDireita(true);
                    break;
                case alEsquerda:
                    edt.setAlinharEsquerda(true);
                    break;
            }
            InvalidateArea();
        }
    }

    public void ReSetAlinhamento(DesenhadorDeTexto edt) {
        if (edt == null || Alinhamento == null) return;
        switch (Alinhamento) {
            case alCentro:
                edt.setCentrarTextoHorizontal(true);
                break;
            case alDireita:
                edt.setAlinharDireita(true);
                break;
            case alEsquerda:
                edt.setAlinharEsquerda(true);
                break;
        }
        edt.LimitarAreaDePintura = true;
        edt.setCentrarTextoVertical(isCentrarVertical());
    }

    @Override
    public void DoPaint(Graphics2D g) {
        g.setPaint(this.getForeColor());
        super.DoPaint(g);
    }

    /**
     * Usado para dizer como o artefato se comporta: se true: como uma caixa de dezenho que não pode ser ligada a nada. Se false: como uma caixa de dezenho que poderá ser ligada.
     */
    private boolean simplesDezenho = true;

    public boolean isSimplesDezenho() {
        return simplesDezenho;
    }

    public void setSimplesDezenho(boolean simplesDezenho) {
        this.simplesDezenho = simplesDezenho;
    }

    @Override
    protected void DoPaintDoks(Graphics2D g) {
        if (!simplesDezenho) {
            super.DoPaintDoks(g);
        }
    }

    @Override
    public boolean CanLiga(PontoDeLinha aThis) {
        if (!simplesDezenho) {
            return super.CanLiga(aThis);
        }
        return false;
    }

    @Override
    public DesenhadorDeTexto getTextoFormatado() {
        DesenhadorDeTexto edt = super.getTextoFormatado();
        ReSetAlinhamento(edt);
        return edt;
    }
    
    
}
