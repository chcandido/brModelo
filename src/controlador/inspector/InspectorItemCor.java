/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.inspector;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author ccandido
 */
public class InspectorItemCor extends InspectorItemExtender {

    public InspectorItemCor(Inspector criador) {
        super(criador);
        setOndeEditar(criador.TipoDlg);
        setMyAction(InspectorExtenderEditor.TipoDeAcao.tpAcaoDlgCor);
    }
    
    public InspectorItemCor(){
        super();
        setMyAction(InspectorExtenderEditor.TipoDeAcao.tpAcaoDlgCor);
    }

    @Override
    protected void paintBase(Graphics2D g) {
        Rectangle r = this.getBounds();
        int esq = (int) (r.width * Criador.getDivisor());
        setArea(new Rectangle(esq -2, 0, 4, r.height - 1));

        //int dir = r.width - esq;
        if (!isSelecionado()) {
            g.setColor(Color.GRAY);
            g.drawRoundRect(0, 0, r.width - 1, r.height - 1, 10, 10);
            g.drawLine(esq, 0, esq, r.height - 1);

            g.setColor(Color.BLACK);
            
            getCorParaTexto(g);
            
            Rectangle bkp = g.getClipBounds();
            g.clipRect(0, 0, esq - 1, r.height);
            g.drawString(getTexto(), (Criador.espaco * 2) + 1, (int) (r.height * 0.72));

            g.setClip(bkp);
            int re = esq + r.height -5;
            g.setColor(CanEdit() ? Color.BLACK : Color.LIGHT_GRAY);
            g.fillRect(esq + 4, 4, r.height - 9, r.height - 9);
            try {
                Color c = util.Utilidades.StringToColor(getTransValor());//new Color(Integer.parseInt(getTransValor()));
                g.setColor(c);
            } catch (Exception e) {
            }
            Color tmpc = g.getColor();
            String bonito = Integer.toString(tmpc.getRed()) + ", " + Integer.toString(tmpc.getGreen()) + ", " +Integer.toString(tmpc.getBlue())+ ", " +Integer.toString(tmpc.getAlpha());
            
            if (CanEdit()) {
                g.fillRect(esq + 5, 5, r.height - 10, r.height -10);
            }
            
//            g.setColor(CanEdit() ? Color.BLACK : Color.LIGHT_GRAY);
//            g.drawRect(tmp + 4, 4, r.height - 9, r.height -9);
            
            g.clipRect(re, 0, esq - 1, r.height);
            
            getCorParaTexto(g);

            g.drawString(bonito, re + (Criador.espaco * 2) + 1, (int) (r.height * 0.72));

            g.setClip(bkp);

        } else {
            super.paintBase(g);
        }
    }

//    @Override
//    protected void setSelecionado(boolean selecionado) {
//        super.setSelecionado(selecionado);
//        if ((getOndeEditar() != null) && (selecionado)) {
//            InspectorExtenderEditor xe = (InspectorExtenderEditor)getOndeEditar();
//            xe.OrganizeSize();
//            xe.setAcaoTipo(InspectorExtenderEditor.TipoDeAcao.tpAcaoDlgCor);
//        }
//    }
}
