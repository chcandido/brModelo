/*
 * Copyright (C) 2017 chcan
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
package desenho;

import controlador.Diagrama;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 *
 * @author chcan
 */
public class Ancorador extends FormaElementar{
    
    private static final long serialVersionUID = 7623942990410762612L;
    
    public Ancorador(Diagrama master) {
        Inicie(master);
    }
    
    private void Inicie(Diagrama master) {
        setMaster(master);
        if (getMaster() != null) {
            InitializeSubItens(master);
        }
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)); 
        setVisible(false);
    }

    //<editor-fold defaultstate="collapsed" desc="Mouse">
    public void setOverNow(Rectangle overNow) {
        if (this.overRNow == overNow) {
            return;
        }
        if (this.overRNow != null && this.overRNow.equals(overNow)) {
            return;
        }
        //pinto o antigo
        if (this.overRNow != null) {
            InvalidateArea(this.overRNow);
        }
        this.overRNow = overNow;
        //pinto o novo
        if (this.overRNow != null) {
            InvalidateArea(this.overRNow);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (getMaster() == null) {
            return;
        }
        if (getMaster().getCursor().getType() != this.getCursor().getType()) {
            getMaster().setCursor(new Cursor(this.getCursor().getType()));
        }
        Point p = e.getPoint();
        ChecarOver(p);
    }

    private void ChecarOver(Point p) {
        if (areas.isEmpty()) {
            return;
        }
        for (Rectangle r : areas) {
            if (r.contains(p)) {
                setOverNow(r);
                return;
            }
        }
        setOverNow(null);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (areas.isEmpty()) {
            return;
        }
        for (int i = 0; i < areas.size(); i++) {
            Rectangle r = areas.get(i);
            if (r.contains(e.getPoint())) {
                ProcessClick(getAncorasCode().get(i));
                break;
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        
    }
    
    @Override
    public void mouseDblClicked(MouseEvent e) {
        
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        
    }
  
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    public boolean isMyEvent(MouseEvent e){
        if (e == null || getMaster().getComando() != null) return  false;
        return IsMe(e.getPoint());
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        setOverNow(null);
    }
    //</editor-fold>

    protected Shape Regiao = null;

    public Shape getRegiao() {
        if (Regiao == null) {
            Regiao = new Rectangle(getLeft(), getTop(), getWidth(), getHeight());
        }
        return Regiao;
    }

    public void setRegiao(Shape regiao) {
        Regiao = regiao;
    }
    
    @Override
    public void ReSized() {
        DestruaRegiao();
        super.ReSized();
    }

    protected void DestruaRegiao() {
        setRegiao(null);
    }
    
    @Override
    public boolean IsMe(Point p) {
        if (super.IsMe(p)) {
            return getRegiao().contains(p);
        }
        return false;
    }
    
    @Override
    public boolean IntersectPath(Rectangle recsel) {
        return false;
    }

    //# Usados para identificar a ação a ser desenhada no Ancorador e executada no objeto.
    //# Adicionar imagem em Controler "public final void Construir()"
    public static final int CODE_ANCORAR = 0;
    public static final int CODE_DEL = 1;
    public static final int CODE_ORG_AT = 2;
    
    public void Posicione(FormaElementar selecionado) {
        if (selecionado == null || !getMaster().getEditor().isAncorador()) {
            if (isVisible()) {
                setVisible(false);
            }
            return;
        }
        final int espaco = 8;
        SetVisible(true);
        
        int L = selecionado.getLeft() - getWidth() - espaco; 
        int T = selecionado.getTop(); 
        if (L < 0) {
            L = selecionado.getLeftWidth() + espaco;
        }
        if ((T + getHeight() + espaco) > getMaster().getHeight()) {
            T = getMaster().getHeight() - (getHeight() + espaco);
        }
        
        int H = Construa(selecionado);
        SetBounds(L, T, getWidth(), H);
    }
    
    private final ArrayList<Rectangle> areas = new ArrayList<>();
    
    public int Construa(FormaElementar sel) {
        getAncorasCode().clear();
        int larg = getWidth();
        int y = getTop();
        final int sx = 2;

        for (Integer c : sel.getAncorasCode()) {
            y += larg + sx;
            getAncorasCode().add(c);
        }
        return y -sx;
    }

    @Override
    public void DoPaint(Graphics2D g) {
        if (!isVisible() || getMaster().getSelecionado() == null) {
            return;
        }
        g.setFont(getFont());

        areas.clear();
        int larg = getWidth();
        int x = getLeft();
        int y = getTop();

        final int sx = 2;

        for (Integer c : getAncorasCode()) {
            areas.add(new Rectangle(x, y, larg, larg + sx));
            y += larg + sx;
        }

        if (areas.isEmpty()) {
            return;
        }
        g.setColor(new Color(204, 204, 255));
        int i = 0;
        for (Integer c : getAncorasCode()) {
//            g.setColor(Color.gray);
            Rectangle r = areas.get(i);

            g.setColor(Color.white);
            g.fillRect(r.x, r.y + 1, r.width-1, r.height-4);
            g.setColor(Color.gray);

            String whatDraw = getMaster().getSelecionado().WhatDrawOnAcorador(c);
            g.drawImage(
                    getMaster().getEditor().getControler().ImagemDeDiagrama.get(whatDraw).getImage(),
                    r.x + 1, r.y + 1, null);

            if (overRNow != null && overRNow.equals(r)) {
                g.setColor(Color.lightGray);
                g.drawRect(r.x, r.y + 1, r.width-1, r.height -4);

                g.setColor(Color.darkGray);
                g.drawLine(r.x + 2, r.y + r.height -2, r.x + r.width-1, r.y + r.height -2);
                g.drawLine(r.x + r.width-1, r.y + 4, r.x + r.width-1, r.y + r.height -2);
            }
            i++;
        }
    }

    /**
     * Is mouseover now?
     */
    private transient Rectangle overRNow = null;

    public Rectangle getOverNow() {
        return overRNow;
    }

    private void ProcessClick(Integer c) {
        if (getMaster().getSelecionado() == null) {
            if (isVisible()) {
                setVisible(false);
            }
            return;
        }
        setOverNow(null);
        InvalidateArea();
        getMaster().getSelecionado().runAncorasCode(c);
        Posicione(getMaster().getSelecionado());
    }

    private ArrayList<Integer> ancorasCode = null;
   
    @Override
    public ArrayList<Integer> getAncorasCode() {
        if (ancorasCode == null) {
            ancorasCode = new ArrayList<>();
        }
        return ancorasCode;
    }
}
