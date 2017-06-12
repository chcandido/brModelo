/*
 * Copyright (C) 2016 chcan
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
package helper;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 *
 * @author chcan
 */
public class LabelNav extends JLabel {

    private static final long serialVersionUID = -3186762247569710132L;

    public LabelNav(String string, Icon icon, int i) {
        super(string, icon, i);
    }

    public LabelNav(String string, int i) {
        super(string, i);
    }

    public LabelNav(String string) {
        super(string);
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                super.mouseEntered(me); 
                setForeground(Color.GRAY);
            }

            @Override
            public void mouseExited(MouseEvent me) {
                super.mouseExited(me);
                setForeground(Color.BLACK);
            }

        });
    }

    public LabelNav(Icon icon, int i) {
        super(icon, i);
    }

    public LabelNav(Icon icon) {
        super(icon);
    }

    public LabelNav() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id = -1;

    public static String textoFormatado(String texto) {
        String res = texto;
        if (texto.length() > 11) {
            res = texto.substring(0, 8) + "...";
        }
        return res;
    }
    
    public void LikeLink() {
        Font font = getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        setFont(font.deriveFont(attributes));
    }
}
