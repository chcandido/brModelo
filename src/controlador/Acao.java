package controlador;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import util.TratadorDeImagens;

/**
 *
 * @author ccandido
 */
public class Acao extends AbstractAction {

    private final Editor editor;
    private final Image icone;
    public boolean normal = true;

    public Acao(Editor editor, String texto, String ico, String descricao, String command) {
        super(texto);
        this.editor = editor;

        ImageIcon ic = null;
        try {
            ic = Configuer.getImageIconFromResource(ico);
        } catch(Exception e) {
            util.BrLogger.Logger("ERROR_CREATE_ACAO", ico + "_" + e.getMessage());
        }
        if (ic != null) {
            icone = makeColorTransparent(ic.getImage(), Color.WHITE);
            ic = reescale(icone);
            //alimenta a lista de imagens para outros fins
            String key = ico.substring("diagrama.".length(), ico.length() -4);
            if (!editor.getControler().ImagemDeDiagrama.containsKey(key)) {
                editor.getControler().ImagemDeDiagrama.put(key, ic);
            }
            //fim.
            //Icones dos diagramas de tamanho diferenciados.
            if (ico.startsWith("Controler.interface.Diagrama.Icone.")) {
                this.putValue(Action.LARGE_ICON_KEY, ic);
            } else {
                this.putValue(Action.LARGE_ICON_KEY, new ImageIcon(icone));
            }
            this.putValue(Action.SMALL_ICON, ic);
        } else {
            icone = null;
        }
        this.putValue(Action.SHORT_DESCRIPTION, Editor.fromConfiguracao.getValor(descricao));
        this.putValue(Action.NAME, texto);
        this.putValue(Action.ACTION_COMMAND_KEY, command);
    }
    
    public void Renomeie(String desc) {
        this.putValue(Action.NAME, Editor.fromConfiguracao.getValor(desc));
//        this.putValue(Action.SHORT_DESCRIPTION, Editor.fromConfiguracao.getValor(desc));
    }

    private Image makeColorTransparent(Image im, final Color color) {
        return TratadorDeImagens.makeColorTransparent(im, color);
    }

    private ImageIcon reescale(Image img) {
        Image newimg = img.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }
    public int IDX = 0;

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (normal) {
            editor.DoAction(ev);
        } else {
            editor.DoActionExtender(ev);
        }
    }
}