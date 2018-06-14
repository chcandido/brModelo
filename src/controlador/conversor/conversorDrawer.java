/*
 * Copyright (C) 2014 CHC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package controlador.conversor;

import controlador.BaseControlador;
import controlador.Diagrama;
import desenho.FormaElementar;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import util.BrLogger;

/**
 *
 * @author CHC
 */
public class conversorDrawer extends BaseControlador {

    public conversorDrawer() {
        doInit();
    }

    public javax.swing.ButtonGroup Grp = null;

    private void doInit() {
        setBackground(Color.WHITE);
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        Rectangle r = getBounds();
        Graphics2D g = (Graphics2D) grphcs;

        Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        if (toPaint != null) {
            g.drawImage(toPaint, r.width - toPaint.getWidth(), r.height - toPaint.getHeight(), null);
        }
        g.setPaint(Color.BLACK);

        int dist = 0;
        int w = getWidth() - dist;
        int h = getHeight() - dist;
        int L = 0;
        int T = 0;
        boolean dv = false;

        GradientPaint GP = new GradientPaint(L, T, Color.WHITE, dv ? L : L + w, dv ? T + h : T, Color.DARK_GRAY, true);
        g.setPaint(GP);

        r = new Rectangle(r.x, r.y, r.width, r.height);
        g.fill(r);
        g.setComposite(originalComposite);
    }

    private BufferedImage toPaint = null;

    private Diagrama origem = null, destino = null;

    public void setDiagramas(Diagrama origem, Diagrama destino) {
        this.origem = origem;
        this.destino = destino;
    }

    public void setObjAtivo(FormaElementar obj, BufferedImage dig_img) {
        if (obj == null) {
            toPaint = null;
        } else {
            Rectangle r = obj.getBounds();
            int x = Math.max(0, r.x -50), y = Math.max(0, r.y -50);
            int w = getWidth() /  2;
            int h = getHeight() / 2;

            BufferedImage tmp = dig_img;

            if (x + w > tmp.getWidth()) {
                w -= (x + w) - tmp.getWidth();
            }
            if (y + h > tmp.getHeight()) {
                h -= (y + h) - tmp.getHeight();
            }
            toPaint = tmp.getSubimage(x, y, w, h);
        }
        repaint();
    }

    private final ArrayList<JRadioButton> listaRD = new ArrayList<>();

    public void Escreve(conversorOpcoes Opcoes) {
        Grp = new javax.swing.ButtonGroup();
        Opcoes.Textos.forEach(s -> {
            JLabel lbl = new JLabel(s);
            lbl.setFont(new Font(lbl.getFont().getName(), Font.BOLD, lbl.getFont().getSize()));
            add(lbl);
        });
        add(new JLabel(" "));
        Opcoes.Questoes.forEach(s -> {
            JRadioButton rd = new JRadioButton(s);
            rd.setOpaque(false);
            int i = listaRD.size();
            Grp.add(rd);
            listaRD.add(rd);
            add(rd);
            if (Opcoes.Disables.indexOf(i) > -1) {
                rd.setEnabled(false);
            }
            if (i == Opcoes.opcDefault) {
                rd.setSelected(true);
            }
        });

        if (Opcoes.Observacoes.size() > 0) {
            add(new JLabel("Observação:"));
        }
        Opcoes.Observacoes.forEach(s -> {
            add(new JLabel(s));
        });
    }

    public int getSelectedIndex() {
        for (int i = 0; i < listaRD.size(); i++) {
            if (listaRD.get(i).isSelected()) {
                return i;
            }
        }
        return -1;
    }

}
