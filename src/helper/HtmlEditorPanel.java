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
package helper;

import java.io.IOException;
import java.net.URL;
import javax.swing.JEditorPane;

/**
 *
 * @author SAA
 */
public class HtmlEditorPanel extends JEditorPane{

    private static final long serialVersionUID = -4269826601003356216L;

    public HtmlEditorPanel() {
    }

    public HtmlEditorPanel(URL url) throws IOException {
        super(url);
    }

    public HtmlEditorPanel(String string) throws IOException {
        super(string);
    }

    public HtmlEditorPanel(String string, String string1) {
        super(string, string1);
    }
    
    
}
