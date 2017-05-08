/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.inspector;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author ccandido
 */
public class InspectorExtenderEditor extends JPanel {

    private final JButton btn;
    private Inspector dono;
    
    public InspectorExtenderEditor(Inspector dono) {
        this();
        this.dono = dono;
        setBackground(Color.white);
        setFocusable(true);
    }
    
    public InspectorExtenderEditor() {
        super();
        //setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 1, 1));
        btn = new JButton("...");
        btn.setBounds(1,1, 20, 20);
        //btn.setLocation(1, 1);
        add(btn);//, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, -1, -1));
        validate();
        setFocusable(true);
        btn.addActionListener((ActionEvent e) -> {
            RunDlg();
        });
    }

    public final void RunDlg() {
        if (!isEnabled() || !btn.isEnabled()) return;
        if (getAcaoTipo() == TipoDeAcao.tpAcaoDlgCor) {
            setTexto(util.Dialogos.ShowDlgCor(this.getRootPane(), getTexto(), dono.getEditor().diagramaAtual));
        } else if (getAcaoTipo() == TipoDeAcao.tpAcaoDlgTexto) {
            setTexto(util.Dialogos.ShowDlgTexto(this.getRootPane(),getTexto()));
        }
        if (dono != null) dono.EndEdit(true, false);
        invalidate();
    }
    
    private String texto = "";
    
    public enum TipoDeAcao {
        tpAcaoDlgTexto,tpAcaoDlgCor, tpReadOnlyTexto, tpAcaoSelectObj, tpReadOnlyCor, tpAcaoCommand
    } 

    private TipoDeAcao acaoTipo = TipoDeAcao.tpAcaoDlgTexto;

    public TipoDeAcao getAcaoTipo() {
        return acaoTipo;
    }

    public void setAcaoTipo(TipoDeAcao acaoTipo) {
        if (this.acaoTipo != acaoTipo) {
            this.acaoTipo = acaoTipo;
            btn.setEnabled(acaoTipo != TipoDeAcao.tpReadOnlyTexto);
        }
    }
            
    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
        revalidate();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //Color bkp = g.getColor();
        Rectangle r = this.getBounds();
        g.setColor(Color.BLACK);
        int re = 0;
        String bonito = "?";
        if (getAcaoTipo() == TipoDeAcao.tpAcaoDlgCor || getAcaoTipo() == TipoDeAcao.tpReadOnlyCor) {
            g.setColor(Color.BLACK);
            g.fillRect(3, 3, r.height - 7, r.height - 7);
            try {
                Color c = util.Utilidades.StringToColor(getTexto());
                g.setColor(c);
                bonito = getTexto();
            } catch (Exception e) {
            }
            g.fillRect(4, 4, r.height - 8, r.height - 8);
            //g.setColor(Color.BLACK);
            //g.drawRect(3, 3, r.height - 7, r.height - 7);
            re = r.height - 1;
        } else {
            bonito = getTexto().replaceAll("\n", " | ");
        }
        //g.setColor(bkp);
        
        Rectangle obkp = g.getClipBounds();

        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font(this.getFont().getFontName(), Font.BOLD, getFont().getSize()));
        g.clipRect(re, 0, r.width - r.height -re - (re == 0? 4: 8), r.height);
        g.drawString(bonito, re + 2, (int) (r.height * 0.72) + 1);
        g.drawLine(0, 0, 0, getHeight());
        g.setClip(obkp);
    }

    protected void OrganizeSize() {
        java.awt.Dimension nd = new java.awt.Dimension(getHeight() -1, getHeight() - 2);
        btn.setPreferredSize(nd);
        btn.setSize(nd);
        btn.repaint();
    }
}
