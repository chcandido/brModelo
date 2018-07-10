/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import controlador.Diagrama;
import controlador.Editor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author ccandido
 */
public class Dialogos {

    public static int ShowMessageSave(Diagrama afechar) {
        String arq = afechar.getNomeFormatado();
        return (JOptionPane.showConfirmDialog(afechar.getEditor().getParent(), Editor.fromConfiguracao.getValor("Controler.MSG_SAVE") + " " +
                arq,Editor.fromConfiguracao.getValor("Controler.MSG_SAVE_TITLE"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)); 
    }

    public static int ShowMessageConfirm(Component parente, String txtAdicional) {
        if (parente != null) parente.requestFocus();
        return (JOptionPane.showConfirmDialog(parente, Editor.fromConfiguracao.getValor("Controler.MSG_CONFIRM") + (txtAdicional.isEmpty() ? "?"  : " " + txtAdicional),
                Editor.fromConfiguracao.getValor("Controler.MSG_CONFIRM_TITLE"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)); 
    }
    public static int ShowMessageConfirm(Component parente, String msgTexto, boolean msgConfirm) {
        if (msgConfirm) {
            return ShowMessageConfirm(parente, msgTexto);
        }
        if (parente != null) parente.requestFocus();
        return (JOptionPane.showConfirmDialog(parente, (msgTexto.isEmpty() ? "?"  : " " + msgTexto),
                Editor.fromConfiguracao.getValor("Controler.MSG_CONFIRM_TITLE"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)); 
    }
    public static boolean ShowMessageConfirmYES(Component parente, String txtAdicional) {
        return (ShowMessageConfirm(parente, txtAdicional) == JOptionPane.YES_OPTION);
    }
    public static boolean ShowMessageConfirmYES(Component parente, String txtAdicional, boolean msgConfirm) {
        return (ShowMessageConfirm(parente, txtAdicional, msgConfirm) == JOptionPane.YES_OPTION);
    }
    public static void ShowMessageInform(Component parente, String texto) {
        JOptionPane.showMessageDialog(parente, texto,
                Editor.fromConfiguracao.getValor("Controler.MSG_INFORM_TITLE"),
                JOptionPane.INFORMATION_MESSAGE); 
    }

    public static void ShowMessageERROR(Component parente, String texto) {
        JOptionPane.showMessageDialog(parente, texto,
                Editor.fromConfiguracao.getValor("Controler.MSG_ERROR_TITLE"),
                JOptionPane.ERROR_MESSAGE); 
    }

    public Dialogos() {
        super();
    }

    public static String ShowDlgTexto(JComponent form, String texto) {
        DlgExecutor dlg = new DlgExecutor((Frame) form.getParent(), true);
        dlg.Texto.setText(texto);
        //dlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dlg.setLocationRelativeTo(form);
        dlg.setVisible(true);

        if (dlg.getResultado() == JOptionPane.OK_OPTION) {
            return dlg.Texto.getText();
        }
        return texto;
    }

    public static void ShowDlgTextoReadOnly(JComponent form, String texto) {
        DlgExecutor dlg = new DlgExecutor((Frame) form.getParent(), true);
        dlg.Texto.setText(texto);
        dlg.Texto.setEditable(false);
        dlg.Texto.setForeground(Color.DARK_GRAY);
        dlg.Texto.setCaretPosition(0);
        dlg.setLocationRelativeTo(form);
        dlg.btnCancelar.setVisible(false);
        dlg.setVisible(true);

//        if (dlg.getResultado() == JOptionPane.OK_OPTION) {
//            return dlg.Texto.getText();
//        }
//        return texto;
    }

    public static Color c = Color.BLACK;

    public static String ShowDlgCor(JComponent form, String textoCor, Diagrama modelo) {
        try {
            c = Utilidades.StringToColor(textoCor);
        } catch (Exception e) {
        }
        final JColorChooser jcc = new JColorChooser();
        jcc.addChooserPanel(new PainelSelecaoCor(modelo));
        jcc.setColor(c);

        JDialog dialog = JColorChooser.createDialog(form,
                Editor.fromConfiguracao.getValor("Controler.MSG_CHOOSE_COLLOR"),
                true, jcc, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        c = jcc.getColor();
                    }
                },
                null);
        dialog.setVisible(true);
        //c = jcc.showDialog(form, Editor.fromConfiguracao.getValor("Controler.MSG_CHOOSE_COLLOR"), c);
        return c == null ? textoCor : Utilidades.ColorToString(c);
    }

    public static String ShowDlgCor(JComponent form, String textoCor) {
        c = Color.BLACK;
        try {
            c = util.Utilidades.StringToColor(textoCor);
        } catch (Exception e) {
        }
        c = JColorChooser.showDialog(form, Editor.fromConfiguracao.getValor("Controler.MSG_CHOOSE_COLLOR"), c);
        return c == null ? textoCor : util.Utilidades.ColorToString(c);
    }

    public static String ShowDlgInputText(JComponent form, String textoCor) {
        String res = JOptionPane.showInputDialog(form,
                Editor.fromConfiguracao.getValor("Controler.MSG_INPUT_TEXT_LABEL"), textoCor);
        //null, //Editor.fromConfiguracao.getValor("Controler.MSG_INPUT_TEXT_LABEL"), 
        //textoCor);
        return res == null ? "" : res;
    }

    public static String ShowDlgFileImg(JComponent form) {
        JFileChooser f = new JFileChooser();
        //f.setDialogTitle(Editor.fromConfiguracao.getValor("Controler.dlg.image"));
        int returnVal = f.showDialog(form, Editor.fromConfiguracao.getValor("Controler.dlg.image"));
        f.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = f.getSelectedFile();
            return file.getAbsolutePath();
        } else {
            return null;
        }
    }

    private static String dir = "";
    public static File ShowDlgSaveDiagrama(JComponent form, Diagrama diag) {
        JFileChooser f = new JFileChooser();
        //f.setDialogTitle(Editor.fromConfiguracao.getValor("Controler.dlg.modelo.salvar"));
        f.setFileSelectionMode(JFileChooser.FILES_ONLY);

        FileFilter filter = new FileNameExtensionFilter("BrModelo(bin)", Arquivo.brM3);
        FileFilter filter2 = new FileNameExtensionFilter("BrModelo(xml)", Arquivo.xml);
        f.addChoosableFileFilter(filter);
        f.addChoosableFileFilter(filter2);
        f.setAcceptAllFileFilterUsed(false);
        f.setFileFilter(filter);
        if (dir.isEmpty()) dir = System.getProperty("user.dir");
        f.setCurrentDirectory(new File(dir + "."));
        f.setDialogTitle(Editor.fromConfiguracao.getValor("Controler.MSG_SAVE_TITLE") + " " + diag.getNomeFormatado());
        if (!diag.getNome().isEmpty()){ 
            f.setSelectedFile(new File(diag.getNome()));
        }
        //f.setApproveButtonText(Editor.fromConfiguracao.getValor("Controler.dlg.modelo.salvar"));
        int returnVal = f.showSaveDialog(form);
        //int returnVal = f.showDialog(form, Editor.fromConfiguracao.getValor("Controler.dlg.modelo.salvar"));

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = f.getSelectedFile();
            String ext = Arquivo.getExtension(file);
            if (ext == null) {
                ext = "";
            }
            String arq = file.getAbsolutePath();
            dir = f.getCurrentDirectory().getAbsolutePath();
            if (f.getFileFilter().equals(filter) && !Arquivo.brM3.toUpperCase().equals(ext.toUpperCase())) {
                return new File(arq + "." + Arquivo.brM3);
            }
            if (f.getFileFilter().equals(filter2) && !Arquivo.xml.toUpperCase().equals(ext.toUpperCase())) {
                return new File(arq + "." + Arquivo.xml);
            }
            return file;
        } else {
            return null;
        }
    }

    public static File ShowDlgSaveAsImg(JComponent form, Diagrama diag) {
        JFileChooser f = new JFileChooser();
        //f.setDialogTitle(Editor.fromConfiguracao.getValor("Controler.dlg.modelo.salvar"));
        f.setFileSelectionMode(JFileChooser.FILES_ONLY);

        FileFilter filter = new FileNameExtensionFilter("Imagem (png)", Arquivo.png);
        FileFilter filter2 = new FileNameExtensionFilter("Imagem (bmp)", Arquivo.bmp);
        f.addChoosableFileFilter(filter);
        f.addChoosableFileFilter(filter2);
        f.setAcceptAllFileFilterUsed(false);
        f.setFileFilter(filter);
        if (dir.isEmpty()) dir = System.getProperty("user.dir");
        f.setCurrentDirectory(new File(dir));
        f.setDialogTitle(Editor.fromConfiguracao.getValor("Controler.MSG_EPRT_TITLE"));
        if (!diag.getNome().isEmpty()){ 
            f.setSelectedFile(new File(diag.getNome()));
        }
        //f.setApproveButtonText(Editor.fromConfiguracao.getValor("Controler.dlg.modelo.salvar"));
        int returnVal = f.showSaveDialog(form);
        //int returnVal = f.showDialog(form, Editor.fromConfiguracao.getValor("Controler.dlg.modelo.salvar"));

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = f.getSelectedFile();
            String ext = Arquivo.getExtension(file);
            if (ext == null) {
                ext = "";
            }
            dir = file.getAbsolutePath();
            if (f.getFileFilter().equals(filter) && !Arquivo.png.toUpperCase().equals(ext.toUpperCase())) {
                return new File(file.getAbsolutePath() + "." + Arquivo.png);
            }
            if (f.getFileFilter().equals(filter2) && !Arquivo.bmp.toUpperCase().equals(ext.toUpperCase())) {
                return new File(file.getAbsolutePath() + "." + Arquivo.bmp);
            }
            return file;
        } else {
            return null;
        }
    }

    public static File ShowDlgSaveAsAny(JComponent form, String ar) {
        JFileChooser f = new JFileChooser();
        //f.setDialogTitle(Editor.fromConfiguracao.getValor("Controler.dlg.modelo.salvar"));
        f.setFileSelectionMode(JFileChooser.FILES_ONLY);

        //FileFilter filter = new FileNameExtensionFilter("Arquivo texto (txt)", Arquivo.png);
        //FileFilter filter2 = new FileNameExtensionFilter("Imagem (bmp)", Arquivo.bmp);
        //f.addChoosableFileFilter(filter);
        //f.addChoosableFileFilter(filter2);
        f.setAcceptAllFileFilterUsed(true);
        //f.setFileFilter(filter);
        if (dir.isEmpty()) dir = System.getProperty("user.dir");
        f.setCurrentDirectory(new File(dir));
        f.setDialogTitle(Editor.fromConfiguracao.getValor("Controler.MSG_EPRT_TITLE"));
        if (!ar.isEmpty()){ 
            f.setSelectedFile(new File(ar));
        }
        //f.setApproveButtonText(Editor.fromConfiguracao.getValor("Controler.dlg.modelo.salvar"));
        int returnVal = f.showSaveDialog(form);
        //int returnVal = f.showDialog(form, Editor.fromConfiguracao.getValor("Controler.dlg.modelo.salvar"));

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = f.getSelectedFile();
//            String ext = Arquivo.getExtension(file);
//            if (ext == null) {
//                ext = "";
//            }
            dir = file.getAbsolutePath();

            return file;
        } else {
            return null;
        }
    }

    /**
     * param preDir = pode ser diretório ou arquivo (não importa)
     * @param preDir
     * @param master
     * @return 
     */
    public static File ShowDlgLoadDiagrama(String preDir, Editor master) {
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.FILES_ONLY);

        f.setFileFilter(new FileNameExtensionFilter("BrModelo", Arquivo.brM3, Arquivo.xml));
        f.addChoosableFileFilter(new FileNameExtensionFilter("BrModelo(bin)", Arquivo.brM3));
        f.addChoosableFileFilter(new FileNameExtensionFilter("BrModelo(xml)", Arquivo.xml));
        f.setAcceptAllFileFilterUsed(true);
        
        if (preDir == null || "".equals(preDir)) {
            f.setCurrentDirectory(new File(System.getProperty("user.dir")));
        } else {
            File f2 = new File(preDir);
            if (f2.isDirectory()) {
                f.setCurrentDirectory(f2);
            } else {
                f.setCurrentDirectory(new File(f2.getPath()));
            }
        }

        //f.setApproveButtonText(Editor.fromConfiguracao.getValor("Controler.dlg.modelo.abrir"));
        int returnVal = f.showOpenDialog((Component) master.getFramePrincipal());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = f.getSelectedFile();
            if (!file.exists()) {
                return null;
            }
            return file;
        } else {
            return null;
        }
    }

    public static JFontChooser JFC = new JFontChooser();
    public static Font ShowDlgFont(JComponent form, Font selected){
        JFontChooser fc = JFC;
        fc.setSelectedFont(selected);
        if (fc.showDialog(form) == JFontChooser.OK_OPTION) {
            fc.makeLastRegistred();
            return fc.getSelectedFont();
        }
        return null;
    }
}
