/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principal;

import controlador.Acao;
import controlador.Configuer;
import controlador.Controler;
import controlador.Controler.menuComandos;
import controlador.Diagrama;
import controlador.Editor;
import controlador.ISuperControler;
import controlador.apoios.TreeItem;
import desenho.formas.Forma;
import helper.FormHelp;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import partepronta.FormPartes;
import util.TratadorDeImagens;

/**
 *
 * @author ccandido
 */
public class FramePrincipal extends javax.swing.JFrame implements ISuperControler {

    /**
     * Creates new form FramePrincipal
     */
    public FramePrincipal() {
        initComponents();
        exitMenuItem.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                FramePrincipal.this.setVisible(false);
//                System.exit(0);
                Fechador(false);
            }
        });

        if (Editor.fromConfiguracao.hasValor("cfg.location.salvar") && Boolean.valueOf(Editor.fromConfiguracao.getValor("cfg.location.salvar"))) {
            try {
                int x = Integer.valueOf(Editor.fromConfiguracao.getValor("cfg.location.x"));
                int y = Integer.valueOf(Editor.fromConfiguracao.getValor("cfg.location.y"));
                setLocation(x, y);
            } catch (Exception e) {
                setLocationRelativeTo(null);
            }
        } else {
            setLocationRelativeTo(null);
        }
        DoInit();
        setTitle("brModelo");
    }

    private void DoInit() {
        Manager.AjusteTamanho();
        Manager.setFramePrincipal(this);
        //Manager.PopuleBarraModelo(toolModelos);
        Manager.PopuleBarra(MenuDiagrama);
        Manager.PopuleBarra(BarraDeBotoes);
        //Manager.PopuleBarra(botoes);
        //JMenu diagramas = (JMenu)menuBar.add(new JMenu(Editor.fromConfiguracao.getValor("Controler.interface.menu.menuListaDiagramas.texto")), 3);
        //diagramas.setMnemonic(Editor.fromConfiguracao.getValor("Controler.interface.menu.menuListaDiagramas.mtecla").charAt(0));
        Manager.PopuleMenus(MenuEditar, fileMenu, masterPopUp);

        JMenu nv = (JMenu) fileMenu.getItem(0);
        for (int i = 0; i < nv.getItemCount(); i++) {
            JButton b = new javax.swing.JButton(nv.getItem(i).getAction());
            b.setFocusable(false);
            b.setHideActionText(true);
            b.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            b.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            String s = nv.getItem(i).getAction().getValue(javax.swing.AbstractAction.NAME).toString();
            b.setToolTipText(s);
            s = nv.getItem(i).getName();
            b.setName(s);
            //jToolBar1.add(b, i + 5);
            barraDiags.add(b, i);
        }
        JMenuItem fechatudo = new JMenuItem(Editor.fromConfiguracao.getValor("Controler.interface.menu.closeall"));
        fechatudo.setToolTipText(Editor.fromConfiguracao.getValor(Editor.fromConfiguracao.getValor("Controler.interface.menu.closeall.dica")));
        fechatudo.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Fechador(true);
            }
        });
        fileMenu.add(fechatudo, 3);
        Manager.getBox().setComponentPopupMenu(masterPopUp);

        Manager.setDicas(inspectorDicas2);
        Manager.setInspectorEditor(inspector1);
        Manager.setEditorConfig(inspector2);
        Manager.setParente(Scroller);
        Manager.setShowDiagramas(ListadorDeDiagramas);
        Manager.setLabelZoom(lblZoom);
        Manager.setLblStatus(lblStatus);
        util.BrLogger.setStatus(statusMessageLabel);
        try {
            Image img = Configuer.getImageFromResource("Controler.interface.Icone");
            img = TratadorDeImagens.makeColorTransparent(img, Color.white);
            this.setIconImage(img);
        } finally {
        }

        ScrollerBarraDeBotoes.getHorizontalScrollBar().setBlockIncrement(100);
        ScrollerBarraDeBotoes.getVerticalScrollBar().setBlockIncrement(100);
        ScrollerBarraDeBotoes.getHorizontalScrollBar().setUnitIncrement(10);
        ScrollerBarraDeBotoes.getVerticalScrollBar().setUnitIncrement(10);

        btnAbrir.setAction(fileMenu.getItem(1).getAction());
        btnFechar.setAction(fileMenu.getItem(2).getAction());

        for (Acao a : Manager.getControler().ListaDeAcoesEditaveis) {
            String k = a.getValue(Action.ACTION_COMMAND_KEY).toString();
            menuComandos mc = menuComandos.valueOf(k);
            switch (mc) {
                case cmdSelAnt:
                    btnAnterior.setAction(a);
                    break;
                case cmdSelProx:
                    btnProximo.setAction(a);
                    break;
                case cmdSendToBack:
                    btnSendToBack.setAction(a);
                    break;
                case cmdBringToFront:
                    btnBringToFront.setAction(a);
                    break;
                case cmdUndo:
                    btnDesfazer.setAction(a);
                    break;
                case cmdRendo:
                    btnRefazer.setAction(a);
                    break;
                case cmdMicroAjuste0:
                    b0.setAction(a);
                    break;
                case cmdMicroAjuste1:
                    b1.setAction(a);
                    break;
                case cmdMicroAjuste2:
                    b2.setAction(a);
                    break;
                case cmdMicroAjuste3:
                    b3.setAction(a);
                    break;

                case cmdCopyFormat:
                    b4.setAction(a);
                    break;
                case cmdPasteFormat:
                    b5.setAction(a);
                    break;
                case cmdDimPastLeft:
                    b6.setAction(a);
                    break;
                case cmdDimPastTop:
                    b7.setAction(a);
                    break;
                case cmdDimPastRight:
                    b8.setAction(a);
                    break;
                case cmdDimPastBottom:
                    b9.setAction(a);
                    break;
                case cmdDimPastWidth:
                    b10.setAction(a);
                    break;
                case cmdDimPastHeight:
                    b11.setAction(a);
                    break;

                case cmdDimAlignH:
                    b12.setAction(a);
                    break;
                case cmdDimAlignV:
                    b13.setAction(a);
                    break;

                case cmdSave:
                    btnSalvar.setAction(a);
                    break;
            }
        }

        TreeItensDiagrama.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                    Object value, boolean selected, boolean expanded,
                    boolean isLeaf, int row, boolean focused) {
                Component c = super.getTreeCellRendererComponent(tree, value,
                        selected, expanded, isLeaf, row, focused);
                if (value instanceof TreeItem) {
                    int id = ((TreeItem) value).getId();
                    if (id == 0) {
                        setIcon(Manager.getControler().ImagemDeDiagrama.get(Manager.diagramaAtual.getTipo().name()));
                    } else {
                        ImageIcon img = Manager.getControler().getImagem(((TreeItem) value).getExtraInfo());
                        if (img != null) {
                            setIcon(img);
                        }
                    }
                }
                return c;
            }
        });

        controladorImpressao = new fmImpressao(this, true);
        Manager.AtualizeTreeNavegacao();
        Manager.diagramaAtual.populeComandos(Manager.getControler().BarraMenu.getItem(0));
        Manager.CarregueConfig();

        //---- Recentes
        JMenu m = new JMenu(Editor.fromConfiguracao.getValor("Inspector.obj.cfg.recentes"));
        m.setToolTipText(Editor.fromConfiguracao.getValor("Inspector.dica.cfg.recentes"));
        fileMenu.add(m, 2);
        Manager.setMenuRecente(m);
        JMenuItem jmi = new JMenuItem(Editor.fromConfiguracao.getValor("Inspector.obj.cfg.recentes.limbar"));
        jmi.setToolTipText(Editor.fromConfiguracao.getValor("Inspector.dica.cfg.recentes.limpar"));
        jmi.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                Manager.setRecentes(new ArrayList<>());
                JMenuItem mm = Manager.getMenuRecente().getItem(Manager.getMenuRecente().getItemCount() - 1);
                Manager.getMenuRecente().removeAll();
                Manager.getMenuRecente().addSeparator();
                Manager.getMenuRecente().add(mm);
                Manager.reloadMenuRecentes();
            }
        });
        m.addSeparator();
        m.add(jmi);
        ArrayList<String> lst = new ArrayList<>();
        if (Editor.fromConfiguracao.hasValor("cfg.recentes")) {
            lst.addAll(Arrays.asList(Editor.fromConfiguracao.getValor("cfg.recentes").split(";")));
        }

        Manager.setRecentes(lst);
        Manager.reloadMenuRecentes();
        m.setEnabled(lst.size() > 0);
        //End.

        btnPrint.setToolTipText(Editor.fromConfiguracao.getValor("Controler.comandos.print.descricao"));

        if (Manager.LoadAutoSave()) {
            util.Dialogos.ShowMessageInform(this, Editor.fromConfiguracao.getValor("Inspector.obj.msg.autosalvar"));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        masterPopUp = new javax.swing.JPopupMenu();
        SplitMaster = new javax.swing.JSplitPane();
        panSplitInspectors = new javax.swing.JPanel();
        SplitInspector = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        barraDiags = new javax.swing.JToolBar();
        jSeparator10 = new javax.swing.JSeparator();
        TabInspector = new javax.swing.JTabbedPane();
        inspector1 = new controlador.inspector.Inspector();
        jScrollPane1 = new javax.swing.JScrollPane();
        TreeItensDiagrama = new javax.swing.JTree();
        inspector2 = new controlador.inspector.Inspector();
        inspectorDicas2 = new controlador.inspector.InspectorDicas();
        SplitDiagramas = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        barraBotoes = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnAbrir = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnFechar = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btnDesfazer = new javax.swing.JButton();
        btnRefazer = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnAnterior = new javax.swing.JButton();
        btnProximo = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnBringToFront = new javax.swing.JButton();
        btnSendToBack = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        b0 = new javax.swing.JButton();
        b1 = new javax.swing.JButton();
        b2 = new javax.swing.JButton();
        b3 = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnMenos = new javax.swing.JButton();
        lblZoom = new javax.swing.JLabel();
        btnMais = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        b4 = new javax.swing.JButton();
        b5 = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        b6 = new javax.swing.JButton();
        b7 = new javax.swing.JButton();
        b8 = new javax.swing.JButton();
        b9 = new javax.swing.JButton();
        b10 = new javax.swing.JButton();
        b11 = new javax.swing.JButton();
        b12 = new javax.swing.JButton();
        b13 = new javax.swing.JButton();
        ListadorDeDiagramas = new controlador.Mostrador();
        Scroller = new javax.swing.JScrollPane();
        Manager = new controlador.Editor();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        ScrollerBarraDeBotoes = new javax.swing.JScrollPane();
        BarraDeBotoes = new javax.swing.JPanel();
        statusPanel = new javax.swing.JPanel();
        statusMessageLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblShowAllMSG = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        MenuEditar = new javax.swing.JMenu();
        MenuDiagrama = new javax.swing.JMenu();
        menuCMD = new javax.swing.JMenu();
        MenuPartes = new javax.swing.JMenu();
        MenuVer = new javax.swing.JMenuItem();
        MenuAdicionar = new javax.swing.JMenuItem();
        MenuSalvarRepo = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        menuAjuda = new javax.swing.JMenuItem();
        menuVerAtualizacao = new javax.swing.JMenuItem();
        menuSobre = new javax.swing.JMenuItem();

        masterPopUp.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                masterPopUpPopupMenuWillBecomeVisible(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        SplitMaster.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        SplitMaster.setDividerLocation(251);
        SplitMaster.setResizeWeight(0.1);

        panSplitInspectors.setBackground(new java.awt.Color(204, 204, 204));

        SplitInspector.setBorder(null);
        SplitInspector.setDividerLocation(550);
        SplitInspector.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        SplitInspector.setResizeWeight(1.0);
        SplitInspector.setDoubleBuffered(true);

        barraDiags.setBorder(null);
        barraDiags.setFloatable(false);
        barraDiags.setRollover(true);

        TabInspector.setAlignmentX(0.0F);
        TabInspector.setAlignmentY(0.0F);
        TabInspector.setMinimumSize(new java.awt.Dimension(150, 200));
        TabInspector.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TabInspectorStateChanged(evt);
            }
        });

        inspector1.setBorder(null);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("principal/Formularios_pt_BR"); // NOI18N
        TabInspector.addTab(bundle.getString("FramePrincipal.inspector1.TabConstraints.tabTitle"), null, inspector1, bundle.getString("FramePrincipal.inspector1.TabConstraints.tabToolTip")); // NOI18N

        jScrollPane1.setBorder(null);

        TreeItensDiagrama.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TreeItensDiagramaMouseClicked(evt);
            }
        });
        TreeItensDiagrama.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                TreeItensDiagramaValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(TreeItensDiagrama);

        TabInspector.addTab(bundle.getString("FramePrincipal.jScrollPane1.TabConstraints.tabTitle"), jScrollPane1); // NOI18N

        inspector2.setBorder(null);
        TabInspector.addTab(bundle.getString("FramePrincipal.inspector2.TabConstraints.tabTitle"), inspector2); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(barraDiags, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
            .addComponent(jSeparator10)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(TabInspector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(barraDiags, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(31, 31, 31)
                    .addComponent(TabInspector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
        );

        SplitInspector.setLeftComponent(jPanel3);

        inspectorDicas2.setBackground(new java.awt.Color(240, 240, 255));
        inspectorDicas2.setMinimumSize(new java.awt.Dimension(50, 50));

        javax.swing.GroupLayout inspectorDicas2Layout = new javax.swing.GroupLayout(inspectorDicas2);
        inspectorDicas2.setLayout(inspectorDicas2Layout);
        inspectorDicas2Layout.setHorizontalGroup(
            inspectorDicas2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 243, Short.MAX_VALUE)
        );
        inspectorDicas2Layout.setVerticalGroup(
            inspectorDicas2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 92, Short.MAX_VALUE)
        );

        SplitInspector.setRightComponent(inspectorDicas2);

        javax.swing.GroupLayout panSplitInspectorsLayout = new javax.swing.GroupLayout(panSplitInspectors);
        panSplitInspectors.setLayout(panSplitInspectorsLayout);
        panSplitInspectorsLayout.setHorizontalGroup(
            panSplitInspectorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SplitInspector)
        );
        panSplitInspectorsLayout.setVerticalGroup(
            panSplitInspectorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SplitInspector)
        );

        SplitInspector.getAccessibleContext().setAccessibleName(bundle.getString("FramePrincipal.SplitInspector.AccessibleContext.accessibleName")); // NOI18N

        SplitMaster.setLeftComponent(panSplitInspectors);

        SplitDiagramas.setDividerLocation(915);
        SplitDiagramas.setResizeWeight(1.0);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 51));
        jPanel2.setLayout(new java.awt.GridLayout(2, 0));

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnAbrir.setText(bundle.getString("FramePrincipal.jButton2.text")); // NOI18N
        btnAbrir.setActionCommand(bundle.getString("FramePrincipal.btnAbrir.actionCommand")); // NOI18N
        btnAbrir.setFocusable(false);
        btnAbrir.setHideActionText(true);
        btnAbrir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbrir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnAbrir);
        btnAbrir.getAccessibleContext().setAccessibleName(bundle.getString("FramePrincipal.btnAbrir.AccessibleContext.accessibleName")); // NOI18N

        btnSalvar.setText(bundle.getString("FramePrincipal.btnSalvar.text_1")); // NOI18N
        btnSalvar.setFocusable(false);
        btnSalvar.setHideActionText(true);
        btnSalvar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSalvar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnSalvar);

        btnFechar.setText(bundle.getString("FramePrincipal.btnFechar.text")); // NOI18N
        btnFechar.setFocusable(false);
        btnFechar.setHideActionText(true);
        btnFechar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFechar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnFechar);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/menu_imprimir.png"))); // NOI18N
        btnPrint.setText(bundle.getString("FramePrincipal.btnPrint.text")); // NOI18N
        btnPrint.setFocusable(false);
        btnPrint.setHideActionText(true);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);
        jToolBar1.add(jSeparator5);

        btnDesfazer.setText(bundle.getString("FramePrincipal.btnDesfazer.text")); // NOI18N
        btnDesfazer.setFocusable(false);
        btnDesfazer.setHideActionText(true);
        btnDesfazer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDesfazer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnDesfazer);

        btnRefazer.setText(bundle.getString("FramePrincipal.btnRefazer.text")); // NOI18N
        btnRefazer.setFocusable(false);
        btnRefazer.setHideActionText(true);
        btnRefazer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefazer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnRefazer);
        jToolBar1.add(jSeparator3);

        btnAnterior.setText(bundle.getString("FramePrincipal.btnAnterior.text")); // NOI18N
        btnAnterior.setFocusable(false);
        btnAnterior.setHideActionText(true);
        btnAnterior.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAnterior.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnAnterior);

        btnProximo.setText(bundle.getString("FramePrincipal.btnProximo.text")); // NOI18N
        btnProximo.setFocusable(false);
        btnProximo.setHideActionText(true);
        btnProximo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnProximo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnProximo);
        jToolBar1.add(jSeparator4);

        btnBringToFront.setText(bundle.getString("FramePrincipal.btnBringToFront.text")); // NOI18N
        btnBringToFront.setFocusable(false);
        btnBringToFront.setHideActionText(true);
        btnBringToFront.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBringToFront.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnBringToFront);

        btnSendToBack.setText(bundle.getString("FramePrincipal.btnSendToBack.text")); // NOI18N
        btnSendToBack.setFocusable(false);
        btnSendToBack.setHideActionText(true);
        btnSendToBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSendToBack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnSendToBack);
        jToolBar1.add(jSeparator2);

        b0.setText(bundle.getString("FramePrincipal.b0.text")); // NOI18N
        b0.setFocusable(false);
        b0.setHideActionText(true);
        b0.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        b0.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(b0);

        b1.setText(bundle.getString("FramePrincipal.b1.text")); // NOI18N
        b1.setFocusable(false);
        b1.setHideActionText(true);
        b1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        b1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(b1);

        b2.setText(bundle.getString("FramePrincipal.b2.text")); // NOI18N
        b2.setFocusable(false);
        b2.setHideActionText(true);
        b2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        b2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(b2);

        b3.setText(bundle.getString("FramePrincipal.b3.text")); // NOI18N
        b3.setFocusable(false);
        b3.setHideActionText(true);
        b3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        b3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(b3);
        jToolBar1.add(jSeparator6);

        btnMenos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/zoommenos.png"))); // NOI18N
        btnMenos.setIconTextGap(2);
        btnMenos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenosActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMenos);

        lblZoom.setText(bundle.getString("FramePrincipal.lblZoom.text")); // NOI18N
        jToolBar1.add(lblZoom);

        btnMais.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/zoom.png"))); // NOI18N
        btnMais.setIconTextGap(2);
        btnMais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaisActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMais);
        jToolBar1.add(jSeparator7);

        b4.setText(bundle.getString("FramePrincipal.b4.text")); // NOI18N
        b4.setFocusable(false);
        b4.setHideActionText(true);
        b4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        b4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(b4);

        b5.setText(bundle.getString("FramePrincipal.b5.text")); // NOI18N
        b5.setFocusable(false);
        b5.setHideActionText(true);
        b5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        b5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(b5);
        jToolBar1.add(jSeparator9);

        b6.setText(bundle.getString("FramePrincipal.b6.text")); // NOI18N
        b6.setFocusable(false);
        b6.setHideActionText(true);
        b6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        b6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(b6);

        b7.setText(bundle.getString("FramePrincipal.b7.text")); // NOI18N
        b7.setFocusable(false);
        b7.setHideActionText(true);
        b7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        b7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(b7);

        b8.setText(bundle.getString("FramePrincipal.b8.text")); // NOI18N
        b8.setFocusable(false);
        b8.setHideActionText(true);
        b8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        b8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(b8);

        b9.setText(bundle.getString("FramePrincipal.b9.text")); // NOI18N
        b9.setFocusable(false);
        b9.setHideActionText(true);
        b9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        b9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(b9);

        b10.setText(bundle.getString("FramePrincipal.b10.text")); // NOI18N
        b10.setFocusable(false);
        b10.setHideActionText(true);
        b10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        b10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(b10);

        b11.setText(bundle.getString("FramePrincipal.b11.text")); // NOI18N
        b11.setFocusable(false);
        b11.setHideActionText(true);
        b11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        b11.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(b11);

        b12.setText(bundle.getString("FramePrincipal.b12.text")); // NOI18N
        b12.setFocusable(false);
        b12.setHideActionText(true);
        b12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        b12.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(b12);

        b13.setText(bundle.getString("FramePrincipal.b13.text")); // NOI18N
        b13.setFocusable(false);
        b13.setHideActionText(true);
        b13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        b13.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(b13);

        javax.swing.GroupLayout barraBotoesLayout = new javax.swing.GroupLayout(barraBotoes);
        barraBotoes.setLayout(barraBotoesLayout);
        barraBotoesLayout.setHorizontalGroup(
            barraBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 914, Short.MAX_VALUE)
        );
        barraBotoesLayout.setVerticalGroup(
            barraBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel2.add(barraBotoes);

        javax.swing.GroupLayout ListadorDeDiagramasLayout = new javax.swing.GroupLayout(ListadorDeDiagramas);
        ListadorDeDiagramas.setLayout(ListadorDeDiagramasLayout);
        ListadorDeDiagramasLayout.setHorizontalGroup(
            ListadorDeDiagramasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 914, Short.MAX_VALUE)
        );
        ListadorDeDiagramasLayout.setVerticalGroup(
            ListadorDeDiagramasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        jPanel2.add(ListadorDeDiagramas);

        Scroller.setBorder(null);
        Scroller.setDoubleBuffered(true);

        Manager.setBackground(new java.awt.Color(255, 255, 255));
        Scroller.setViewportView(Manager);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Scroller, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Scroller, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE))
        );

        SplitDiagramas.setLeftComponent(jPanel1);

        jTabbedPane1.setToolTipText(bundle.getString("FramePrincipal.ScrollerBarraDeBotoes.TabConstraints.tabTitle")); // NOI18N

        ScrollerBarraDeBotoes.setBorder(null);
        ScrollerBarraDeBotoes.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ScrollerBarraDeBotoes.setToolTipText(bundle.getString("FramePrincipal.ScrollerBarraDeBotoes.TabConstraints.tabTitle")); // NOI18N
        ScrollerBarraDeBotoes.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        BarraDeBotoes.setMinimumSize(new java.awt.Dimension(70, 0));
        BarraDeBotoes.setLayout(new java.awt.GridLayout(15, 1));
        ScrollerBarraDeBotoes.setViewportView(BarraDeBotoes);

        jTabbedPane1.addTab(getBarraBotoesTexto(), ScrollerBarraDeBotoes);

        SplitDiagramas.setRightComponent(jTabbedPane1);
        jTabbedPane1.getAccessibleContext().setAccessibleName(getBarraBotoesTexto());

        SplitMaster.setRightComponent(SplitDiagramas);

        statusPanel.setPreferredSize(new java.awt.Dimension(54, 30));

        statusMessageLabel.setText(bundle.getString("FramePrincipal.statusMessageLabel.text")); // NOI18N
        statusMessageLabel.setToolTipText(bundle.getString("FramePrincipal.statusMessageLabel.toolTipText")); // NOI18N
        statusMessageLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        statusMessageLabel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jSeparator1.setAlignmentX(0.0F);
        jSeparator1.setAlignmentY(0.0F);
        jSeparator1.setDoubleBuffered(true);

        lblShowAllMSG.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblShowAllMSG.setText(bundle.getString("FramePrincipal.lblShowAllMSG.text")); // NOI18N
        lblShowAllMSG.setToolTipText(bundle.getString("FramePrincipal.lblShowAllMSG.toolTipText")); // NOI18N
        lblShowAllMSG.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblShowAllMSG.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblShowAllMSG.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblShowAllMSGMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblShowAllMSGMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblShowAllMSGMouseExited(evt);
            }
        });

        lblStatus.setBackground(new java.awt.Color(204, 204, 204));
        lblStatus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblStatus.setText(bundle.getString("FramePrincipal.lblStatus.text")); // NOI18N
        lblStatus.setToolTipText(bundle.getString("FramePrincipal.lblStatus.toolTipText")); // NOI18N
        lblStatus.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblStatus.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(lblShowAllMSG, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusMessageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jSeparator1)
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(statusMessageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblShowAllMSG, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                .addGap(3, 3, 3))
        );

        lblStatus.getAccessibleContext().setAccessibleName(bundle.getString("FramePrincipal.lblStatus.AccessibleContext.accessibleName")); // NOI18N
        lblStatus.getAccessibleContext().setAccessibleDescription(bundle.getString("FramePrincipal.lblStatus.AccessibleContext.accessibleDescription")); // NOI18N

        fileMenu.setText(bundle.getString("FramePrincipal.fileMenu.text")); // NOI18N

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        exitMenuItem.setMnemonic('r');
        exitMenuItem.setText(bundle.getString("FramePrincipal.exitMenuItem.text")); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        MenuEditar.setText(bundle.getString("FramePrincipal.MenuEditar.text")); // NOI18N
        menuBar.add(MenuEditar);

        MenuDiagrama.setText(bundle.getString("FramePrincipal.MenuDiagrama.text")); // NOI18N

        menuCMD.setText(bundle.getString("FramePrincipal.menuCMD.text")); // NOI18N
        menuCMD.setEnabled(false);
        MenuDiagrama.add(menuCMD);

        menuBar.add(MenuDiagrama);

        MenuPartes.setText(bundle.getString("FramePrincipal.MenuPartes.text")); // NOI18N
        MenuPartes.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                MenuPartesMenuSelected(evt);
            }
        });

        MenuVer.setText(bundle.getString("FramePrincipal.MenuVer.text")); // NOI18N
        MenuVer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuVerActionPerformed(evt);
            }
        });
        MenuPartes.add(MenuVer);

        MenuAdicionar.setText(bundle.getString("FramePrincipal.MenuAdicionar.text")); // NOI18N
        MenuAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuAdicionarActionPerformed(evt);
            }
        });
        MenuPartes.add(MenuAdicionar);

        MenuSalvarRepo.setText(bundle.getString("FramePrincipal.MenuSalvarRepo.text")); // NOI18N
        MenuSalvarRepo.setEnabled(false);
        MenuSalvarRepo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuSalvarRepoActionPerformed(evt);
            }
        });
        MenuPartes.add(MenuSalvarRepo);

        menuBar.add(MenuPartes);

        helpMenu.setText(bundle.getString("FramePrincipal.helpMenu.text")); // NOI18N

        menuAjuda.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        menuAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/ajuda.png"))); // NOI18N
        menuAjuda.setMnemonic('j');
        menuAjuda.setText(bundle.getString("FramePrincipal.menuAjuda.text")); // NOI18N
        menuAjuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAjudaActionPerformed(evt);
            }
        });
        helpMenu.add(menuAjuda);

        menuVerAtualizacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/download.png"))); // NOI18N
        menuVerAtualizacao.setText(bundle.getString("FramePrincipal.menuVerAtualizacao.text")); // NOI18N
        menuVerAtualizacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuVerAtualizacaoActionPerformed(evt);
            }
        });
        helpMenu.add(menuVerAtualizacao);

        menuSobre.setText(bundle.getString("FramePrincipal.menuSobre.text")); // NOI18N
        menuSobre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSobreActionPerformed(evt);
            }
        });
        helpMenu.add(menuSobre);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1240, Short.MAX_VALUE)
            .addComponent(SplitMaster)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(SplitMaster)
                .addGap(2, 2, 2)
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMenosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenosActionPerformed
        Manager.ZoomMenos();
    }//GEN-LAST:event_btnMenosActionPerformed

    private void btnMaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaisActionPerformed
        Manager.ZoomMais();
    }//GEN-LAST:event_btnMaisActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        DoComandoExterno(Controler.menuComandos.cmdPrint);
    }

    boolean mostarAreaImpressao = false;
    //refaser o componete impressor já que ele não será destruído, ou apenas acultar o frame!

    fmImpressao controladorImpressao;

    private boolean noTree = false;

    @Override
    public void DoComandoExterno(Controler.menuComandos c) {
        if (c == Controler.menuComandos.cmdPrint) {
            fmImpressao fm = controladorImpressao;
            fm.rdMostarAI.setSelected(mostarAreaImpressao);
            fm.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            fm.setLocationRelativeTo(this);
            fm.setDiagrama(Manager.diagramaAtual);
            fm.setVisible(true);
            Point res = fm.getTamanhoAreaImpressao();
            mostarAreaImpressao = fm.rdMostarAI.isSelected();
            Manager.setMostrarAreaImpressao(mostarAreaImpressao, res.x, res.y);
        }
        if (c == menuComandos.cmdTreeNavegador) {
            TreeItensDiagrama.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            TreeItensDiagrama.setModel(new DefaultTreeModel(Manager.diagramaAtual.getTreeNavegacao()));
            if (formPartes != null && formPartes.isVisible()) {
                formPartes.Popule(Manager.diagramaAtual.getTipo());
            }
        }
        if ((TabInspector.getSelectedIndex() == 1) && (c == menuComandos.cmdTreeNavegador || c == menuComandos.cmdTreeSelect)) {
            DefaultTreeModel df = (DefaultTreeModel) TreeItensDiagrama.getModel();
            if (df.getRoot() instanceof TreeItem) {
                TreeItem root = (TreeItem) df.getRoot();
                TreePath pt = new TreePath(root);
                if (Manager.diagramaAtual.getSelecionado() == null || !(Manager.diagramaAtual.getSelecionado() instanceof Forma)) {
                    noTree = true;
                    TreeItensDiagrama.setSelectionPath(pt);
                    noTree = false;
                } else {
                    boolean done = false;
                    int rp = Manager.diagramaAtual.getSelecionado().getID();
                    for (int i = 0; i < root.getChildCount(); i++) {
                        TreeItem item = (TreeItem) root.getChildAt(i);
                        if (item.getId() == rp) {
                            if (TreeItensDiagrama.getLastSelectedPathComponent() != null) {
                                int v = ((TreeItem) TreeItensDiagrama.getLastSelectedPathComponent()).getId();
                                if (v != rp) {
                                    noTree = true;
                                    TreeItensDiagrama.setSelectionPath(pt.pathByAddingChild(item));
                                    noTree = false;
                                }
                            } else {
                                noTree = true;
                                TreeItensDiagrama.setSelectionPath(pt.pathByAddingChild(item));
                                noTree = false;
                            }
                            done = true;
                            break;
                        }
                    }
                    if (!done) {
                        noTree = true;
                        TreeItensDiagrama.setSelectionPath(pt);
                        noTree = false;
                    }
                }
            }
        }
    }//GEN-LAST:event_btnPrintActionPerformed

    private void TreeItensDiagramaValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_TreeItensDiagramaValueChanged
        if (noTree) {
            return;
        }
        if (TreeItensDiagrama.getLastSelectedPathComponent() != null) {
            TreeItem ti = ((TreeItem) TreeItensDiagrama.getLastSelectedPathComponent());
            int v = ti.getId();
            if (v > 0) {
                Manager.diagramaAtual.SelecioneByID(v, false);
            }
            if (ti.getLevel() == 2 && Manager.diagramaAtual.getSelecionado() != null) {
                TreeItem tir = (TreeItem) ti.getParent();
                Manager.diagramaAtual.getSelecionado().DoSubItemSel(tir.getIndex(ti));
            }
        }
    }//GEN-LAST:event_TreeItensDiagramaValueChanged

    private void TreeItensDiagramaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TreeItensDiagramaMouseClicked
        if (noTree) {
            return;
        }
        if (evt.getClickCount() == 2) {
            if (TreeItensDiagrama.getLastSelectedPathComponent() != null) {
                int v = ((TreeItem) TreeItensDiagrama.getLastSelectedPathComponent()).getId();
                if (v > 0) {
                    Manager.diagramaAtual.SelecioneByID(v, true);
                }
            }
        }
    }//GEN-LAST:event_TreeItensDiagramaMouseClicked

    private void masterPopUpPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_masterPopUpPopupMenuWillBecomeVisible
        // TODO add your handling code here:
    }//GEN-LAST:event_masterPopUpPopupMenuWillBecomeVisible

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Fechador(false);
    }//GEN-LAST:event_formWindowClosing

    private void TabInspectorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TabInspectorStateChanged
        JTabbedPane sourceTabbedPane = (JTabbedPane) evt.getSource();
        int index = sourceTabbedPane.getSelectedIndex();
        if (index == 1) {
            DoComandoExterno(Controler.menuComandos.cmdTreeNavegador);
            Manager.setTextoDica(null, "");
        } else {
            if (Manager != null) {
                if (index == 0) {
                    inspector1.PerformDica();
                } else {
                    inspector2.PerformDica();
                }
            }
        }
    }//GEN-LAST:event_TabInspectorStateChanged

    private void lblShowAllMSGMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblShowAllMSGMouseEntered
        lblShowAllMSG.setFont(new Font(lblShowAllMSG.getFont().getName(), Font.BOLD, lblShowAllMSG.getFont().getSize()));
        lblShowAllMSG.setBackground(Color.yellow);
    }//GEN-LAST:event_lblShowAllMSGMouseEntered

    private void lblShowAllMSGMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblShowAllMSGMouseExited
        lblShowAllMSG.setFont(new Font(lblShowAllMSG.getFont().getName(), Font.PLAIN, lblShowAllMSG.getFont().getSize()));
        lblShowAllMSG.setBackground(new Color(240, 240, 240, 0));
    }//GEN-LAST:event_lblShowAllMSGMouseExited

    private void lblShowAllMSGMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblShowAllMSGMouseClicked
        FormaLogs fm = new FormaLogs(this, true);
        fm.setLocationRelativeTo(this);
        fm.setVisible(true);
//        if (BrLogger.MSGs.isEmpty()) {
//            JOptionPane.showMessageDialog(this,
//                    Editor.fromConfiguracao.getValor("Controler.MSG_STATUS_NO_ERROR"),
//                    Editor.fromConfiguracao.getValor("Controler.MSG_STATUS_TITLE"),
//                    JOptionPane.INFORMATION_MESSAGE);
//        } else if (JOptionPane.showConfirmDialog(this,
//                Editor.fromConfiguracao.getValor("Controler.MSG_STATUS_TITLE") + ":\n"
//                + BrLogger.MSGs + "\n\n" + Editor.fromConfiguracao.getValor("Controler.MSG_STATUS_CLEAN"),
//                Editor.fromConfiguracao.getValor("Controler.MSG_STATUS_TITLE"),
//                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//            BrLogger.Clean();
//        }
    }//GEN-LAST:event_lblShowAllMSGMouseClicked

    private void menuSobreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSobreActionPerformed
        CarregarFormAjuda();
//        if (!CarregarFormAjuda()) {
//            return;
//        }
        FrameSobre fs = new FrameSobre(this, true);
        fs.setLocationRelativeTo(this);
        fs.Inicie(formAjuda.AjudaMng);
        fs.setVisible(true);
    }//GEN-LAST:event_menuSobreActionPerformed

    FormPartes formPartes = null;

    private void MenuAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuAdicionarActionPerformed
        if (!CarregarFormPartes()) {
            return;
        }
        formPartes.Popule(Manager.diagramaAtual.getTipo());
        String txt = util.Dialogos.ShowDlgInputText(this.getRootPane(), "");
        if (!txt.isEmpty()) {
            formPartes.NovoBotao(txt, Manager.diagramaAtual);
        } else {
            return;
        }
        formPartes.setVisible(true);
    }//GEN-LAST:event_MenuAdicionarActionPerformed

    private void MenuVerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuVerActionPerformed
        if (!CarregarFormPartes()) {
            return;
        }
        formPartes.Popule(Manager.diagramaAtual.getTipo());
        formPartes.setVisible(true);
    }//GEN-LAST:event_MenuVerActionPerformed

    private void MenuPartesMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_MenuPartesMenuSelected
        MenuAdicionar.setEnabled(Manager.diagramaAtual.TemSelecionado());
    }//GEN-LAST:event_MenuPartesMenuSelected

    FormHelp formAjuda = null;
    private void menuAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAjudaActionPerformed
        if (!CarregarFormAjuda()) {
            util.Dialogos.ShowMessageInform(this, Editor.fromConfiguracao.getValor("Controler.MSG_HELP_NOT_FOUND"));
        }
        formAjuda.setVisible(true);
    }//GEN-LAST:event_menuAjudaActionPerformed

    private void MenuSalvarRepoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuSalvarRepoActionPerformed
        if (formPartes != null && formPartes.Partes.isMudou()) {
            formPartes.Salva();
        }
    }//GEN-LAST:event_MenuSalvarRepoActionPerformed

    private void menuVerAtualizacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuVerAtualizacaoActionPerformed
        FormAtualizar fm = new FormAtualizar();
        fm.setLocationRelativeTo(this);
        fm.Inicie(Aplicacao.VERSAO_A + "." + Aplicacao.VERSAO_B + "." + Aplicacao.VERSAO_C);
        fm.setVisible(true);
    }//GEN-LAST:event_menuVerAtualizacaoActionPerformed

    public void ReloadHelp() {
        formAjuda = null;
    }

    private boolean CarregarFormPartes() {
        if (formPartes == null) {
            formPartes = new FormPartes();
            formPartes.externalSalvar = MenuSalvarRepo;
            //formPartes.Mananger = Manager;
            formPartes.setLocationRelativeTo(this);
            if (!formPartes.LoadData()) {
                formPartes.dispose();
                formPartes = null;
                return false;
            }
        }
        return true;
    }

    private boolean CarregarFormAjuda() {
        if (formAjuda == null) {
            formAjuda = new FormHelp();
            formAjuda.fmp = this;
            formAjuda.setLocationRelativeTo(this);
            if (!formAjuda.LoadData()) {
                return false;
            }
        }
        return true;
    }

    public void Fechador(boolean sofecha) throws HeadlessException {
        if (formPartes != null && formPartes.Partes.isMudou() && (!sofecha)) {
            if (util.Dialogos.ShowMessageConfirm(this.getRootPane(), Editor.fromConfiguracao.getValor("Controler.MSG_SAVE_TEMPLATE")) == JOptionPane.YES_OPTION) {
                formPartes.Salva();
            }
        }

        Salvar fm = new Salvar(this, true);
        fm.Carregue(Manager);
        fm.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        fm.setLocationRelativeTo(this);
        if (sofecha && Manager.getDiagramas().size() == 1 && (!Manager.diagramaAtual.getMudou())) {
            Manager.FechaDiagrama(0);
            return;
        } else {
            fm.setVisible(true);
        }
        if (fm.resultado == JOptionPane.CANCEL_OPTION) {
            return;
        }
        int i = 0;
        for (Diagrama d : Manager.getDiagramas()) {
            if (d.getMudou()) {
                if (fm.getCheks().get(i).isSelected()) {
                    if (!d.Salvar(d.getArquivo())) {
                        return;
                    }
                }
            }
            i++;
        }
        if (sofecha) {
            Manager.FecharTudo();
        }

        String tmp = "";
        tmp = Manager.getRecentes().stream().map(s -> s + ";").reduce(tmp, String::concat);
        if (tmp.trim().length() > 2) {
            tmp = tmp.substring(0, tmp.length() - 1);
        } else {
            tmp = "";
        }
        Editor.fromConfiguracao.setValor("cfg.location.x", String.valueOf(getLocation().x));
        Editor.fromConfiguracao.setValor("cfg.location.y", String.valueOf(getLocation().y));
        Editor.fromConfiguracao.setValor("cfg.recentes", tmp);
        if (!sofecha) {
            if (formAjuda != null && formAjuda.AjudaMng.isMudou()) {
                if (util.Dialogos.ShowMessageConfirm(this.getRootPane(), Editor.fromConfiguracao.getValor("Controler.MSG_SAVE_HELP")) == JOptionPane.YES_OPTION) {
                    formAjuda.Salva();
                }
            }
            Manager.EndAutoSave();
            System.exit(0);
        }
//                Editor.fromConfiguracao.getValor("Controler.MSG_CLOSE_TITLE"),
//                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//        if (i == JOptionPane.YES_OPTION) {
//            //Utilidade.configFile.clear();
//            System.exit(0);
//        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BarraDeBotoes;
    private controlador.Mostrador ListadorDeDiagramas;
    private controlador.Editor Manager;
    private javax.swing.JMenuItem MenuAdicionar;
    private javax.swing.JMenu MenuDiagrama;
    private javax.swing.JMenu MenuEditar;
    private javax.swing.JMenu MenuPartes;
    private javax.swing.JMenuItem MenuSalvarRepo;
    private javax.swing.JMenuItem MenuVer;
    private javax.swing.JScrollPane Scroller;
    private javax.swing.JScrollPane ScrollerBarraDeBotoes;
    private javax.swing.JSplitPane SplitDiagramas;
    private javax.swing.JSplitPane SplitInspector;
    private javax.swing.JSplitPane SplitMaster;
    private javax.swing.JTabbedPane TabInspector;
    private javax.swing.JTree TreeItensDiagrama;
    private javax.swing.JButton b0;
    private javax.swing.JButton b1;
    private javax.swing.JButton b10;
    private javax.swing.JButton b11;
    private javax.swing.JButton b12;
    private javax.swing.JButton b13;
    private javax.swing.JButton b2;
    private javax.swing.JButton b3;
    private javax.swing.JButton b4;
    private javax.swing.JButton b5;
    private javax.swing.JButton b6;
    private javax.swing.JButton b7;
    private javax.swing.JButton b8;
    private javax.swing.JButton b9;
    private javax.swing.JPanel barraBotoes;
    private javax.swing.JToolBar barraDiags;
    private javax.swing.JButton btnAbrir;
    private javax.swing.JButton btnAnterior;
    private javax.swing.JButton btnBringToFront;
    private javax.swing.JButton btnDesfazer;
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnMais;
    private javax.swing.JButton btnMenos;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnProximo;
    private javax.swing.JButton btnRefazer;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSendToBack;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private controlador.inspector.Inspector inspector1;
    private controlador.inspector.Inspector inspector2;
    private controlador.inspector.InspectorDicas inspectorDicas2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblShowAllMSG;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblZoom;
    private javax.swing.JPopupMenu masterPopUp;
    private javax.swing.JMenuItem menuAjuda;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuCMD;
    private javax.swing.JMenuItem menuSobre;
    private javax.swing.JMenuItem menuVerAtualizacao;
    private javax.swing.JPanel panSplitInspectors;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void Super_Esperando() {
    }

    @Override
    public void Super_Pronto() {
    }

    public Editor getEditor() {
        return Manager;
    }

    public String getBarraBotoesTexto() {
        if (util.OS.isUnix()) {
            return "   ";
        }
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("principal/Formularios_pt_BR"); // NOI18N
        return bundle.getString("FramePrincipal.ScrollerBarraDeBotoes.TabConstraints.tabTitle");
    }
}
