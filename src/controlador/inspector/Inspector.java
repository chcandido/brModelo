/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.inspector;

import controlador.Editor;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 *
 * @author ccandido
 */
public class Inspector extends JScrollPane {

    //<editor-fold defaultstate="collapsed" desc="Base do componente">
    public Inspector() {
        super();
        Init();
    }

    private void Init() {
        box = new JPanel();
        this.add(box);
        box.setSize(300, 800);
        setViewportView(box);
        box.setLayout(null);
//----        box.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        getHorizontalScrollBar().setBlockIncrement(100);
        getVerticalScrollBar().setBlockIncrement(100);
        getHorizontalScrollBar().setUnitIncrement(10);
        getVerticalScrollBar().setUnitIncrement(10);

        addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                DoResize();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });

        setFocusable(true);

        ActionListener al_down = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                SelectNext(false);
            }
        };
        ActionListener al_up = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                SelectNext(true);
            }
        };

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
        this.registerKeyboardAction(al_down, stroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        this.registerKeyboardAction(al_down, stroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
        this.registerKeyboardAction(al_up, stroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
        this.registerKeyboardAction(al_up, stroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        InitEditores();

    }

    private void InitEditores() {
        TipoMenu.setVisible(false);
        TipoTexto.setVisible(false);
        TipoSN.setVisible(false);
        TipoMenu.setEditable(true);
        TipoDlg.setVisible(false);

        TipoMenu.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    EndEdit(true, false);
                }
            }
        });

        TipoSN.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                //if (e.getStateChange() == ItemEvent.SELECTED) {
                EndEdit(true, false);
                //}
            }
        });

        TipoTexto.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    EndEdit(false, false);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    EndEdit(true, false);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        TipoTexto.setBorder(null);
    }

    public void DoResize() {
        int scrollw = getVerticalScrollBar().getWidth() + 1;
        int tam = getWidth() - (2 * espaco) - scrollw;
        java.awt.Dimension d = new java.awt.Dimension(tam, altura);
        for (InspectorItemBase item : Itens) {
            item.setPreferredSize(d);
            item.setSize(d);
            item.repaint();
        }
        //validate();
        //EndEdit(true, true);
    }
    private JPanel box;

    public JPanel getBox() {
        return box;
    }
    private ArrayList<InspectorItemBase> Itens = new ArrayList<>();

    public ArrayList<InspectorItemBase> getItens() {
        return Itens;
    }
    public final int espaco = 1;
    public int altura = 20;
    protected final JComboBox TipoMenu = new JComboBox();
    protected final JCheckBox TipoSN = new JCheckBox();
    protected final InspectorExtenderEditor TipoDlg = new InspectorExtenderEditor(this);
    protected final JTextField TipoTexto = new JTextField();
    private Editor editor = null;

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    //</editor-fold>
    public InspectorItemBase Add(InspectorProperty pprt) {
        if (pprt.tipo == InspectorProperty.TipoDeProperty.tpNothing) {
            return null;
        }
        int p = Itens.size() * (altura + espaco);
        InspectorItemBase item = InspectorItemBase.SuperFactory(this, pprt);
        int scrollw = getVerticalScrollBar().getWidth() + 1;
//---        item.setPreferredSize(new java.awt.Dimension(getWidth() - (2 * espaco) - scrollw, altura));

        //item.setLocation(espaco, p + 2);
        item.setBounds(espaco, p + 2, getWidth() - (2 * espaco) - scrollw, altura);
        this.box.add(item);//----, new org.netbeans.lib.awtextra.AbsoluteConstraints(espaco, p + 2, -1, -1));
        Itens.add(item);
        box.validate();
        return item;
    }

    public InspectorItemBase AddSeparador(String caption) {
        return Add(InspectorProperty.getPropertySeparador(caption));
    }

    //<editor-fold defaultstate="collapsed" desc="Seleção">
    private InspectorItemBase selecionado = null;

    public InspectorItemBase getSelecionado() {
        return selecionado;
    }

    private void setSelecionado(InspectorItemBase selecionado) {
        if (this.selecionado != selecionado) {
            if (this.selecionado != null) {
                this.selecionado.setSelecionado(false);
            }
            this.selecionado = selecionado;
            if (this.selecionado != null) {
                this.selecionado.setSelecionado(true);
            }
        }
        if (getDicas() == null) {
            if (this.selecionado != null) {
                editor.setTextoDica(this, selecionado.getPropriedade().dica);
            } else {
                editor.setTextoDica(this, "");
            }
        } else {
            if (this.selecionado != null) {
                SetTextoDica(selecionado.getPropriedade().dica);
            } else {
                SetTextoDica("");
            }
        }
    }

    private InspectorDicas dicas = null;

    public void SetTextoDica(String txt) {
        if (getDicas() != null) {
            getDicas().setTexto(txt);
        }
    }

    public void PerformSelect(InspectorItemBase aThis) {
        CarregueValor(aThis);
        setSelecionado(aThis);
    }

    public void PerformDica() {
        if (getDicas() != null) {
            if (this.selecionado != null) {
                SetTextoDica(selecionado.getPropriedade().dica);
            } else {
                SetTextoDica("");
            }
        } else {
            if (editor != null) {
                if (this.selecionado != null) {
                    editor.setTextoDica(this, selecionado.getPropriedade().dica);
                } else {
                    editor.setTextoDica(this, "");
                }
            }
        }
    }

    /**
     * Evita loop infinito
     */
    //private int saltos = 0;
    private void SelectNext(boolean sobe) {
        if (Itens.isEmpty()) {
            return;
        }
        int p = Itens.indexOf(selecionado);
        if (sobe) {
            p--;
            if (p < 0) {
                p = Itens.size() - 1;
            }

            InspectorItemBase ib = Itens.get(p);
            while ((ib instanceof InspectorItemSeparador) || !ib.CanEdit() || !ib.isVisible()) {
                p--;
                if (p < 0) {
                    return;
                }
                ib = Itens.get(p);
            }
            PerformSelect(Itens.get(p));

        } else {
            p++;
            if (p > Itens.size() - 1) {
                p = 0;
            }

            InspectorItemBase ib = Itens.get(p);
            while ((ib instanceof InspectorItemSeparador) || !ib.CanEdit() || !ib.isVisible()) {
                p++;
                if (p > Itens.size() - 1) {
                    return;
                }
                ib = Itens.get(p);
            }
            PerformSelect(Itens.get(p));

        }

//        PerformSelect(Itens.get(p));
//        if (selecionado instanceof InspectorItemSeparador || (!selecionado.CanEdit())) {
//            saltos++;
//            if (saltos > Itens.size()) {
//                saltos = 0;
//                return;
//            }
//            SelectNext(sobe);
//        } else {
//            saltos = 0;
//        }
    }
    //</editor-fold>

    /**
     * Evita que ao carregar JCombobox o evento SELECTED entre (outros) ocorra!
     */
    private boolean stopEdicao = false;

    /**
     * Ocorre quando termina-se de editar um valor de propriedade (neste caso em um InspectorItemBase)
     *
     * @param validar o valor deve ser aceito?
     * @param sair sair da edição?
     */
    public void EndEdit(final boolean validar, final boolean sair) {
        if (stopEdicao) {
            return;
        }
        if (!validar) {
            if (sair) {
                setSelecionado(null);
            } else {
                CarregueValor(selecionado);
            }
            //return;
        } else {
            if (selecionado == null) {
                return;
            }
            String txt = "";
            if (selecionado.getOndeEditar() == TipoMenu) {
                txt = Integer.toString(TipoMenu.getSelectedIndex());
            } else if (selecionado.getOndeEditar() == TipoDlg) {

                if (selecionado.getPropriedade().tipo == InspectorProperty.TipoDeProperty.tpSelecObject) {
                    if (editor.SelectItemByID(Integer.valueOf(selecionado.getPropriedade().property))) {
                        return;
                    }
                    setSelecionado(null);
                    return;
                }

                if (selecionado.getPropriedade().tipo == InspectorProperty.TipoDeProperty.tpCommand) {
                    if (editor.ProcesseCmdFromInspector(this, selecionado.getPropriedade().property)) {
                        return;
                    }
                    setSelecionado(null);
                    return;
                }

                txt = TipoDlg.getTexto();
            } else if (selecionado.getOndeEditar() == TipoSN) {
                txt = Boolean.toString(TipoSN.isSelected());
            } else {
                txt = TipoTexto.getText();
            }
            if (!txt.equals(selecionado.getValor())) {
                if (!editor.AceitaEdicao(this, selecionado.getPropriedade(), txt)) {
                    selecionado.setFalhou(true);
                }
            }

            if (sair) {
                setSelecionado(null);
            } else {
                CarregueValor(selecionado);
            }
        }
    }

    /**
     * Carrega o valor da propriedade no editor
     *
     * @param aThis InspectorItemBase a carregar
     */
    private void CarregueValor(InspectorItemBase aThis) {
        stopEdicao = true;
        if (aThis instanceof InspectorItemMenu) {
            TipoMenu.removeAllItems();
            for (String v : aThis.getPropriedade().opcoesMenu) {
                TipoMenu.addItem(v);
            }
            TipoMenu.setSelectedIndex(Integer.parseInt(aThis.getValor()));
        } else if ((aThis instanceof InspectorItemExtender)) {
            TipoDlg.setTexto(aThis.getTransValor());
        } else if (aThis instanceof InspectorItemSN) {
            TipoSN.setSelected(Boolean.parseBoolean(aThis.getValor()));
            TipoSN.setText(aThis.getTransValor());
        } else if (aThis instanceof InspectorItemTexto) {
            TipoTexto.setText(aThis.getTransValor());
        }
        stopEdicao = false;
    }

    public void Clear() {
        for (InspectorItemBase item : Itens) {
            this.box.remove(item);
        }
        Itens.clear();
        box.validate();
        RePosicionar();
        repaint();
    }

    /**
     * ArrayList contendo as últimas propriedades carregadas.
     */
    private ArrayList<InspectorProperty> gerado = null;

    /**
     * Apaga o valor de "gerado" de forma que o próximo PerformInspector carregue os itens. É usado no caso de se clicar no próprio objeto e o clique mudar uma condição de status do objeto que deve ser mostrada no inspector: exemplo: clique na legenda (no item da legenda).
     */
    public void ForceFullOnCarregue() {
        gerado.clear();
    }

    public void Carrege(ArrayList<InspectorProperty> conjPropriedades) {
        //saltos = 0;
        boolean eq = false;
        if (gerado != null && gerado.size() == conjPropriedades.size()) {
            eq = true;
            for (int i = 0; i < gerado.size(); i++) {
                if (!gerado.get(i).tipo.equals(conjPropriedades.get(i).tipo)) {
                    eq = false;
                    break;
                }
            }
        }
        boolean novo = false;
        if (eq) {
            for (int i = 0; i < gerado.size(); i++) {
                InspectorItemBase it = Itens.get(i);
                it.setPropriedade(conjPropriedades.get(i));
                it.repaint();
            }
            if (selecionado != null) {
                CarregueValor(selecionado);
            }
        } else {
            Clear();
            conjPropriedades.stream().forEach((ipp) -> {
                Add(ipp);
            });
            InspectorItemBase tmp = AddSeparador("");
            ((InspectorItemSeparador) tmp).endOFF = true;
            tmp.setBackground(new Color(240, 240, 240));
            //requestFocus(); // Não atrair o focus! 16/08/2014
            novo = true;
        }
        gerado = conjPropriedades;
        RefreshAllCanEdit();
        if (novo) {
            Itens.stream().filter(pp -> pp instanceof InspectorItemSeparador).map(pp -> (InspectorItemSeparador) pp).forEach(pp -> {
                if (pp.getPropriedade().opcional.equals("-")) {
                    pp.setEstado('+');
                    HideShow(pp, '+');
                }
            });
        }
        RePosicionar();
        //repaint();
    }

    private ArrayList<InspectorItemBase> getListItensForProperty(ArrayList<String> pprs) {
        ArrayList<InspectorItemBase> res = new ArrayList<>();
        pprs.stream().forEach((v) -> {
            getItens().stream().filter((it) -> (it.getPropriedade().isMe(v))).forEach((it) -> {
                res.add(it);
            });
        });
        return res;
    }

    /**
     * Dado um Item (sel), verifica se ele é um agrupador, ou seja, se ele agrupa itens que<br/>
     * serão des/habilitados conforme seu valor atual. Primeiro, pega-se a relação do itens habilitáveis e os habilita.<br/>
     * Depois a dos não habilitáveis e os desabilita.<br/>
     *
     * @param sel
     * @param valor
     */
    public void MakeCanEditGrupo(InspectorItemBase sel) {
        InspectorProperty insp = sel.getPropriedade();
        if (insp.agrupada == null) {
            return;
        }
        String valor = sel.getValor();

        MakeCanEdit(getListItensForProperty(insp.QuaisCanEditIf(valor)), true);
        MakeCanEdit(getListItensForProperty(insp.QuaisCanEditNotIf(valor)), false);

        repaint();
    }

    /**
     * Seta o valor de CanEdit dos itens
     *
     * @param toMake
     * @param ena
     */
    private void MakeCanEdit(ArrayList<InspectorItemBase> toMake, boolean ena) {
        if (ena) {
            toMake.stream().forEach((it) -> {
                it.setCanEdit(true);
            });
            toMake.stream().forEach((it) -> {
                it.RefreshGrupoCanEdit();
            });
        } else {
            toMake.stream().forEach((it) -> {
                it.setCanEdit(false);
            });
        }
    }

    public void RefreshAllCanEdit() {
        //tem mesmo que ficar habilitado?
        for (InspectorItemBase it : getItens()) {
            it.RefreshGrupoCanEdit();
        }
        revalidate();
        //repaint();
    }

    /**
     * @return the dicas
     */
    public InspectorDicas getDicas() {
        return dicas;
    }

    /**
     * @param dicas the dicas to set
     */
    public void setDicas(InspectorDicas dicas) {
        this.dicas = dicas;
    }

    public void HideShow(InspectorItemSeparador item, char estado) {
        int i = getItens().indexOf(item);
        if ('+' == estado) {
            for (int j = i + 1; j < gerado.size(); j++) {
                if (gerado.get(j).tipo == InspectorProperty.TipoDeProperty.tpSeparador) {
                    RePosicionar();
                    return;
                }
                getItens().get(j).setVisible(false);
            }
        } else {
            for (int j = i + 1; j < gerado.size(); j++) {
                if (gerado.get(j).tipo == InspectorProperty.TipoDeProperty.tpSeparador) {
                    RePosicionar();
                    return;
                }
                getItens().get(j).setVisible(true);
            }
        }
        RePosicionar();
    }

    public void RePosicionar() {
        box.removeAll();
        int tl = 0;
        int scrollw = getVerticalScrollBar().getWidth() + 1;
        for (InspectorItemBase item : getItens()) {
            if (!item.isVisible()) {
                continue;
            }
            int p = tl * (altura + espaco);
            tl++;
            item.setPreferredSize(new java.awt.Dimension(getWidth() - (2 * espaco) - scrollw, altura));
            this.box.add(item);//---, new org.netbeans.lib.awtextra.AbsoluteConstraints(espaco, p + 2, -1, -1));
            item.setLocation(espaco, p + 2);
        }
//        box.validate();
//        this.validate();
        box.setSize(box.getSize().width, tl * (altura + espaco));
        box.setPreferredSize(box.getSize());
        //DoResize();
    }

    public InspectorItemBase FindByProperty(String pprt) {
        for (InspectorItemBase it : getItens()) {
            if (it.getPropriedade().isMe(pprt)) {
                return it;
            }
        }
        return null;
    }

    @Override
    public void paint(Graphics grphcs) {
        super.paint(grphcs); //To change body of generated methods, choose Tools | Templates.
        Graphics2D g = (Graphics2D) grphcs;
        int f = g.getFontMetrics().getHeight();
        /**
         * Corrige a altura do inspector no caso de tamanho de fonte diferente no SO.
         */
        if (altura != f + 6) {
            altura = f + 6;
        }
    }

    private double divisor = 0.5;

    public double getDivisor() {
        return divisor;
    }

    public void setDivisor(double divisor) {
        this.divisor = divisor;
        DoResize();
    }

}
