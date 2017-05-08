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
package controlador.apoios;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Reder para desenhar campos coloridos em JComboBox
 *
 * @author chcan
 */
public class ComboBoxSuperRender extends DefaultListCellRenderer {

    private final ListCellRenderer defaultRenderer;

    public ComboBoxSuperRender(ListCellRenderer defaultRenderer) {
        this.defaultRenderer = defaultRenderer;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        Component c = defaultRenderer.getListCellRendererComponent(list, value,
                index, isSelected, cellHasFocus);
        if (c instanceof JLabel) {
            ProcesseFonte((JLabel) c, index, isSelected);
        }
//        else {
//            c.setBackground(Color.red);
//            c = super.getListCellRendererComponent(list, value, index, isSelected,
//                    cellHasFocus);
//        }
        return c;
    }

    /**
     * Usado para redesenhar o label do do JComboBox
     */
    public void ProcesseFonte(JLabel lbl, int item_idx, boolean selcted) {
    }
}
