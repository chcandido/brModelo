/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import controlador.Configuer;
import controlador.Editor;
import controlador.Diagrama;
import controlador.editores.JListItemParaItemLegenda;
import desenho.formas.Legenda;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 *
 * @author ccandido
 */
public class PainelSelecaoCor extends AbstractColorChooserPanel {

    private static final long serialVersionUID = 1124478972421994619L;

    ArrayList<Legenda.ItemDeLegenda> itens = new ArrayList<>();

    PainelSelecaoCor(Diagrama diagrama) {
        diagrama.getListaDeItens().stream().
                filter((fe) -> (fe instanceof Legenda && ((Legenda)fe).canShowEditor())).map(fe -> (Legenda)fe).forEach((fe) -> {
                    fe.getItens().forEach((it) -> {
                itens.add(it);
            });
        });
    }

    JList lst = null;

    @Override
    public void buildChooser() {
        setLayout(new BorderLayout());// GridLayout(0, 1));

        if (!itens.isEmpty()) {
            JScrollPane jsp = new javax.swing.JScrollPane();
            lst = new JList(itens.toArray(new Legenda.ItemDeLegenda[]{}));
            add(jsp, BorderLayout.EAST);
            jsp.add(lst);
            jsp.setViewportView(lst);
            lst.setModel(new javax.swing.AbstractListModel() {

                @Override
                public int getSize() {
                    return itens.size();
                }

                @Override
                public Object getElementAt(int i) {
                    return itens.get(i);
                }
            });
            lst.addListSelectionListener( e -> {
                if (e == null || lst.getSelectedIndex() < 0) {
                    return;
                }
                
                Legenda.ItemDeLegenda r = itens.get(lst.getSelectedIndex());
                
                getColorSelectionModel().setSelectedColor(r.getCor());
            });
            lst.setCellRenderer(new JListItemParaItemLegenda(false));
        }
    }

    @Override
    public void updateChooser() {
    }

    @Override
    public String getDisplayName() {
        return Editor.fromConfiguracao.getValor("Controler.dlg.chooser_color.legendas");
    }

    @Override
    public Icon getSmallDisplayIcon() {
//        return null;
        return TratadorDeImagens.loadFromResource("Controler.interface.Icone", true);
    }

    @Override
    public Icon getLargeDisplayIcon() {
//        return null;
        Image img = Configuer.getImageFromResource("Controler.interface.Icone");
        img = TratadorDeImagens.makeColorTransparent(img, Color.white);
        return new ImageIcon(img);
    }
}
