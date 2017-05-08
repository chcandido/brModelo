/*
 * Aplicacao.java
 */

package principal;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The main class of the application.
 */
public class Aplicacao {

    public static FramePrincipal fmPrincipal;

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
