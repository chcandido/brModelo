/*
 * Aplicacao.java
 */

/*
Copyright do brModelo
============================================================================

Copyright (c) 2019 SIS4.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

============================================================================
 */

package principal;

import controlador.Diagrama;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The main class of the application.
 */
public class Aplicacao {

    public static FramePrincipal fmPrincipal;
    public static final String VERSAO_A = "3";
    public static final String VERSAO_B = "3";
    public static final String VERSAO_C = "1";
    public static final String VERSAO_DATA = "Setembro de 2020";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        initLookAndFeel();
        JFrame.setDefaultLookAndFeelDecorated(true);

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                fmPrincipal = new FramePrincipal();
                fmPrincipal.setVisible(true);
            }
        });
    }
    
    //Versao 3.2
    public static Diagrama getDiagramaSelecionado() {
        if (fmPrincipal != null) {
            return fmPrincipal.getEditor().diagramaAtual;
        }
        return null;
    }

    private static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            util.BrLogger.Logger("ERROR_APP_LOAD_UI", ex.getMessage());
        }

    }

    //Apagar
//    public static Aplicacao getApplication() {
//        return Application.getInstance(Aplicacao.class);
//    }
}
