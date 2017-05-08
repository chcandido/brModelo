/*
 * Copyright (C) 2015 SAA
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
package partepronta;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JToggleButton;

/**
 *
 * @author SAA
 */
public class ParteBtn extends JToggleButton {

    private static final long serialVersionUID = -8578702045669307233L;

    public ParteBtn() {
    }

    public ParteBtn(Icon icon) {
        super(icon);
    }

    public ParteBtn(Icon icon, boolean bln) {
        super(icon, bln);
    }

    public ParteBtn(String string) {
        super(string);
    }

    public ParteBtn(String string, boolean bln) {
        super(string, bln);
    }

    public ParteBtn(Action action) {
        super(action);
    }

    public ParteBtn(String string, Icon icon) {
        super(string, icon);
    }

    public ParteBtn(String string, Icon icon, boolean bln) {
        super(string, icon, bln);
    }

    public GerenciadorSubParte Parte = null;
}
