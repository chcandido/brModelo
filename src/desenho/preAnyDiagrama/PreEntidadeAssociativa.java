/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.preAnyDiagrama;

import controlador.Diagrama;
import desenho.Elementar;
import diagramas.conceitual.Relacionamento;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import util.Constantes;
import util.DesenhadorDeTexto;

/**
 *
 * @author ccandido
 */
public class PreEntidadeAssociativa extends PreEntidade {

    private static final long serialVersionUID = 7744242295858549314L;

    // <editor-fold defaultstate="collapsed" desc="Criação">
    public PreEntidadeAssociativa(Diagrama modelo) {
        super(modelo);
        Inicie();
    }

    public PreEntidadeAssociativa(Diagrama modelo, String texto) {
        super(modelo, texto);
        Inicie();
    }

    public PreEntidadeAssociativa(Diagrama modelo, String texto, PreRelacionamento fr) {
        super(modelo, texto);
        interno = fr;
        Inicie();
    }
    private PreRelacionamento interno;

    public PreRelacionamento getInterno() {
        return interno;
    }

    public void setInterno(PreRelacionamento interno) {
        this.interno = interno;
    }

    private void Inicie() {
        if (interno == null) {
            interno = new Relacionamento(getMaster(), getTexto());
        }
        interno.setPrincipal(this);
        interno.setSelecionavel(false);
        getMaster().Remove(interno, true);
        getSubItens().add(interno);
        //interno.reSetBounds();
        reSetBounds();
        

    }

    @Override
    public void SetTexto(String Texto) {
        super.SetTexto(Texto);
//        DesenhadorDeTexto txtd = getTextoFormatado();
//        txtd.setAlinharEsquerda(true);
//        txtd.setCentrarTextoVertical(false);
    }

    @Override
    public DesenhadorDeTexto getTextoFormatado() {
        DesenhadorDeTexto txtd = super.getTextoFormatado(); //To change body of generated methods, choose Tools | Templates.
        txtd.setAlinharEsquerda(true);
        txtd.setCentrarTextoVertical(false);
        txtd.CorretorPosicao = new Point(2,2);
        return txtd;
    }

    
    // </editor-fold>
    
    public void ReenquadreInterno() {
        if (interno == null) {
            return;
        }
        Rectangle res = getBounds();
        //res.grow(-distSelecao * 4, -distSelecao * 4);
        
        int tmp = -(distSelecao * 4);
        res = new Rectangle(res.x - tmp, res.y - tmp, res.width + 2 * tmp - 2, res.height + 2 * tmp - 2);
        interno.SetBounds(res.x, res.y, res.width, res.height);
        
        //interno.SetBounds(res.x, res.y, res.width - 2, res.height - 2);
        interno.Reposicione();
    }

    @Override
    public void BringToFront() {
        super.BringToFront();
        if (interno != null) {
            interno.BringToFront();
        }
    }

    @Override
    public void SendToBack() {
        if (interno != null) {
            interno.SendToBack();
        }
        super.SendToBack();
    }

    @Override
    public void ReciveFormaResize(Rectangle ret) {
        super.ReciveFormaResize(ret);
        if (interno != null) {
            interno.ReciveFormaResize(ret);
            Rectangle res = getBounds();
            //res.grow(-distSelecao * 4, -distSelecao * 4);
            int tmp = (distSelecao * 4);
            res = new Rectangle(res.x - tmp, res.y - tmp, res.width + 2*tmp - 2, res.height + 2*tmp - 2);
            if (!interno.getBounds().equals(res)) {
                ReenquadreInterno();
            }
        }
    }

    @Override
    public void DoMove(int movX, int movY) {
        super.DoMove(movX, movY);
        if (interno != null) {
            interno.DoMove(movX, movY);
        }
    }

    @Override
    public boolean isComposto() {
        return (interno != null);
    }

    @Override
    public Elementar ProcessaComposicao(Point pt) {
        if ((interno != null) && interno.IsMe(pt)) {
            return interno;
        }
        return this;
    }
    private transient double z = 0.0;

    @Override
    public void PinteTexto(Graphics2D g) {
        //no caso de mudança no zoom, um novo TextoFormatado deve ser criado.
        if (getMaster().getZoom() != z) {
            setTextoFormatado(null);
            z = getMaster().getZoom();
        }
        super.PinteTexto(g);
    }

    @Override
    public boolean Reenquadre() {
        int ALeft = getLeft(), ATop = getTop();
        //super.Reenquadre();
        //if (ALeft != getLeft() || ATop != getTop()) {
        if (super.Reenquadre()) {
            if (interno != null) {
                ReenquadreInterno();
                interno.SendNotificacao(new Point(getLeft() - ALeft, getTop() - ATop), Constantes.Operacao.opReenquadre);
            }
            return true;
        }
        return false;
    }

    @Override
    public void Reposicione() {
        super.Reposicione();
        if (interno != null) {
            interno.Reposicione();
        }
    }

    @Override
    public Elementar getPrimeiroSubComponente() {
        return interno;
    }

    @Override
    public Elementar getPrincipal() {
        return ProcessaComposicao();
    }

    @Override
    public void EscrevaTexto(ArrayList<String> txts) {
        super.EscrevaTexto(txts);
        if (interno != null) txts.add(interno.getTexto());
    }

    @Override
    public void setDisablePainted(boolean disablePainted) {
        super.setDisablePainted(disablePainted);
        if (interno != null) {
            interno.setDisablePainted(disablePainted);
        }
    }
}
