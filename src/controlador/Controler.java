/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import util.TratadorDeImagens;

/**
 *
 * @author ccandido
 */
public class Controler {

    private final Editor editor;
    public JToggleButton BtnNothing;
    public HashMap<String, ImageIcon> ImagemDeDiagrama = new HashMap<>();

    public Controler(Editor edt) {
        this.editor = edt;
        Construir();
    }

    public HashMap<String, Cursor> Cursores = new HashMap<>();

    public Cursor MakeCursor(Comandos comando) {
        String key = comando.name().substring(3);
        if (Cursores.containsKey(key)) {
            return Cursores.get(key);
        }

        Cursor cur;
        try {
            Toolkit kit = Toolkit.getDefaultToolkit();
            Dimension dim = kit.getBestCursorSize(24, 24);
            BufferedImage buffered = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = buffered.createGraphics();
            RenderingHints renderHints
                    = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
            renderHints.put(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            renderHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.addRenderingHints(renderHints);
            g.setColor(Color.GRAY);

            GeneralPath dr
                    = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
            dr.moveTo(0, 0);
            dr.lineTo(10, 0);
            dr.lineTo(0, 10);
            g.fill(dr);

            g.setColor(Color.BLACK);
            dr = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
            dr.moveTo(2, 2);
            dr.lineTo(8, 2);
            dr.lineTo(2, 8);
            g.fill(dr);

            Image img = getImagem(key).getImage();
            g.drawImage(img, 9, 9, null);
            g.dispose();
            cur = kit.createCustomCursor(buffered, new Point(1, 1), key);
        } catch (HeadlessException | IndexOutOfBoundsException e) {
            util.BrLogger.Logger("Cursor: " + key, e.toString());
            return new Cursor(Cursor.DEFAULT_CURSOR);
        }
        Cursores.put(key, cur);
        return cur;
    }

    /**
     * Aqui existem os comandos, mas também a ordem em que aparecem nos menus.
     */
    public enum Comandos {

        cmdEntidade, cmdRelacionamento, cmdAutoRelacionamento, cmdEspecializacao,
        cmdEspecializacao_Exclusiva, cmdEspecializacao_Dupla, cmdUniao, cmdUniao_Entidades, cmdEntidadeAssociativa,
        cmdAtributo, cmdAtributo_Multivalorado, cmdLinha,
        cmdTabela, cmdCampo, cmdCampo_Key, cmdCampo_Fkey, cmdCampo_KeyFkey, cmdLogicoLinha,
        cmdInicioAtividade, cmdEstadoAtividade, cmdDecisaoAtividade, cmdFimAtividade, cmdSetaAtividade, cmdLigacaoAtividade,
        cmdTextoAtividade, cmdForkJoinAtividade,
        cmdRaiaAtividade,
        cmdFluxIniFim, cmdFluxProcesso, cmdFluxConector, cmdFluxDecisao, cmdFluxDocumento, cmdFluxVDocumentos, cmdFluxNota, cmdFluxLigacao, cmdFluxSeta,
        cmdFluxTexto,
        cmdEapProcesso, cmdEapLigacao, cmdEapBarraLigacao,
        cmdLivreRetangulo, cmdLivreRetanguloArr, cmdLivreComentario, cmdLivreTriangulo, cmdLivreLigacao, cmdLivreLigacaoSimples, cmdLivreJuncao, cmdLivreDocumento, cmdLivreVariosDocumentos,
        cmdLivreNota, cmdLivreSuperTexto, cmdLivreLosango, cmdLivreCirculo, cmdLivreDrawer,
        cmdDesenhador, cmdTexto, cmdLegenda, cmdApagar
    }

    public enum menuComandos {

        cmdUndo, cmdRendo, cmdCut, cmdCopy, cmdCopyImg, cmdPaste, cmdCopyFormat, cmdPasteFormat, cmdRealcar, cmdSelProx, cmdSelAnt, cmdSelectAll, cmdBringToFront, cmdSendToBack, cmdDel, cmdDelToSel, //menu edit
        cmdNew, cmdOpen, cmdClose, cmdPrint, cmdExport, cmdSave, cmdSaveAs, cmdSaveAll, //menu arquivo
        cmdTreeNavegador, cmdTreeSelect, //comandos diversos
        cmdMicroAjuste0, cmdMicroAjuste1, cmdMicroAjuste2, cmdMicroAjuste3, //micro ajuste.
        //cmdDimCp, 
        cmdDimPastLeft, cmdDimPastTop, cmdDimPastRight, cmdDimPastBottom, cmdDimPastWidth, cmdDimPastHeight, cmdDimAlignH, cmdDimAlignV
    }

    public enum TipoConfigAcao {

        tpBotoes, tpMenuBarra, tpAny, tpMenus
    }

    public class ConfigAcao {

        private final String texto;
        private final String ico;
        private final String descricao;
        private final String command;
        private final TipoConfigAcao tipo;

        public ConfigAcao(String texto, String ico, String descricao, String command, TipoConfigAcao tipo) {
            this.texto = texto;
            this.ico = ico;
            this.descricao = descricao;
            this.command = command;
            this.tipo = tipo;
        }
    }

    public ArrayList<ConfigAcao> Lista = new ArrayList<>();
    public ArrayList<Acao> ListaDeAcoesEditaveis = new ArrayList<>();

    public final void Construir() {
        ResourceBundle resourceMap = Configuer.getResourceMap();
        for (Comandos c : Comandos.values()) {
            String str = "diagrama." + c.toString().substring(3);
            Lista.add(new ConfigAcao(resourceMap.getString(str), str + ".img", str + ".descricao", c.toString(), TipoConfigAcao.tpAny));
        }
        for (menuComandos c : menuComandos.values()) {
            String str = "Controler.comandos." + c.toString().substring(3).toLowerCase();
            Lista.add(new ConfigAcao(Editor.fromConfiguracao.getValor(str + ".descricao"), str + ".img", str + ".descricao", c.toString(), TipoConfigAcao.tpMenus));
        }
        for (Diagrama.TipoDeDiagrama tm : Diagrama.TipoDeDiagrama.values()) {
            ImagemDeDiagrama.put(tm.name(), TratadorDeImagens.loadFromResource("Controler.interface.Diagrama.Icone." + tm.name().substring(2), true));
        }

        ImagemDeDiagrama.put(Mostrador.Img, TratadorDeImagens.loadFromResource(Mostrador.Img, true));
        ImagemDeDiagrama.put("diagrama.Campo_Key.img", TratadorDeImagens.loadFromResource("diagrama.Campo_Key.img", true));
        ImagemDeDiagrama.put("diagrama.Campo_Fkey.img", TratadorDeImagens.loadFromResource("diagrama.Campo_Fkey.img", true));
        ImagemDeDiagrama.put("diagrama.Campo_KeyFkey.img", TratadorDeImagens.loadFromResource("diagrama.Campo_KeyFkey.img", true));

        ImagemDeDiagrama.put("diagrama.ancordor.0.img", TratadorDeImagens.loadFromResource("diagrama.ancordor.0.img", true));
        ImagemDeDiagrama.put("diagrama.ancordor.1.img", TratadorDeImagens.loadFromResource("diagrama.ancordor.1.img", true));

        ImagemDeDiagrama.put("diagrama.ancordor.0.0.img", TratadorDeImagens.loadFromResource("diagrama.ancordor.0.0.img", true));
        ImagemDeDiagrama.put("diagrama.ancordor.2.img", TratadorDeImagens.loadFromResource("diagrama.ancordor.2.img", true));
        ImagemDeDiagrama.put("diagrama.ancordor.3.img", TratadorDeImagens.loadFromResource("diagrama.ancordor.3.img", true));
        ImagemDeDiagrama.put("diagrama.ancordor.4.img", TratadorDeImagens.loadFromResource("diagrama.ancordor.4.img", true));
        ImagemDeDiagrama.put("diagrama.ancordor.5.img", TratadorDeImagens.loadFromResource("diagrama.ancordor.5.img", true));
        ImagemDeDiagrama.put("diagrama.ancordor.6.img", TratadorDeImagens.loadFromResource("diagrama.ancordor.6.img", true));

        ImagemDeDiagrama.put("diagrama.ancordor.7.img", TratadorDeImagens.loadFromResource("diagrama.ancordor.7.img", true));
        ImagemDeDiagrama.put("diagrama.ancordor.8.img", TratadorDeImagens.loadFromResource("diagrama.ancordor.8.img", true));
        ImagemDeDiagrama.put("diagrama.ancordor.9.img", TratadorDeImagens.loadFromResource("diagrama.ancordor.9.img", true));
        ImagemDeDiagrama.put("diagrama.ancordor.7.0.img", TratadorDeImagens.loadFromResource("diagrama.ancordor.7.0.img", true));
        ImagemDeDiagrama.put("diagrama.ancordor.8.0.img", TratadorDeImagens.loadFromResource("diagrama.ancordor.8.0.img", true));
        ImagemDeDiagrama.put("diagrama.ancordor.9.0.img", TratadorDeImagens.loadFromResource("diagrama.ancordor.9.0.img", true));

        ImagemDeDiagrama.put("diagrama.Constraint_PK.img", TratadorDeImagens.loadFromResource("diagrama.Constraint_PK.img", true));
        ImagemDeDiagrama.put("diagrama.Constraint_FK.img", TratadorDeImagens.loadFromResource("diagrama.Constraint_FK.img", true));
        ImagemDeDiagrama.put("diagrama.Constraint_UN.img", TratadorDeImagens.loadFromResource("diagrama.Constraint_UN.img", true));
        ImagemDeDiagrama.put("diagrama.Constraint_UNFK.img", TratadorDeImagens.loadFromResource("diagrama.Constraint_UNFK.img", true));
        ImagemDeDiagrama.put("diagrama.Constraint_see.img", TratadorDeImagens.loadFromResource("diagrama.Constraint_see.img", true));

        ImagemDeDiagrama.put("Controler.interface.ajuda.icone", TratadorDeImagens.loadFromResource("Controler.interface.ajuda.icone", true));
    }

    private JComponent Barra;
    public JMenu BarraMenu;
    private final HashMap<String, AbstractButton> listaBotoes = new HashMap<>();
    private final HashMap<String, JMenuItem> listaMenus = new HashMap<>();

    public void PopuleBarra(JComponent obj) {
        ButtonGroup buttons = new ButtonGroup();
        Barra = obj;

        Acao ac = new Acao(editor, "?", "Controler.interface.BarraLateral.Nothing.img", "Controler.interface.BarraLateral.Nothing.Texto", null);
        JToggleButton btn = arrume(new JToggleButton(ac));
        buttons.add(btn);
        obj.add(btn);
        btn.setSelected(true);
        ac.IDX = -1;
        this.BtnNothing = btn;
        int i = 0;
        for (ConfigAcao ca : Lista) {
            if (ca.tipo == TipoConfigAcao.tpBotoes || ca.tipo == TipoConfigAcao.tpAny) {
                ac = new Acao(editor, ca.texto, ca.ico, ca.descricao, ca.command);
                ac.IDX = i++;
                btn = arrume(new JToggleButton(ac));
                buttons.add(btn);
                //obj.add(btn);
                listaBotoes.put(ca.command, btn);
            }
        }
        menuComandos c = menuComandos.cmdDel;
        String str = "Controler.comandos." + c.toString().substring(3).toLowerCase();
        ac = new Acao(editor, Editor.fromConfiguracao.getValor(str + ".descricao"), str + ".img", str + ".descricao", c.toString());
        ListaDeAcoesEditaveis.add(ac);
        ac.normal = false;
        JButton btn2 = new JButton(ac);
        btn2.setHideActionText(true);
        btn2.setFocusable(false);
        btn2.setPreferredSize(new Dimension(40, 40));
        obj.add(btn2);

        LayoutManager la = obj.getLayout();
        if (la instanceof GridLayout) {
            ((GridLayout) la).setRows(i + 2);
        }
    }

    private JToggleButton arrume(JToggleButton btn) {
        Dimension btnDim = new Dimension(40, 40);
        btn.setHideActionText(true);
        btn.setFocusable(false);
        btn.setPreferredSize(btnDim);
        btn.setDoubleBuffered(true);
        //btn.setMargin(new Insets(2, 5, 2, 5));
        btn.setRolloverEnabled(false);
        return btn;
    }

    public void PopuleBarra(JMenu obj) {
        int i = 0;
        for (ConfigAcao ca : Lista) {
            if (ca.tipo == TipoConfigAcao.tpMenuBarra || ca.tipo == TipoConfigAcao.tpAny) {
                Acao ac = new Acao(editor, ca.texto, ca.ico, ca.descricao, ca.command);
                ac.IDX = i++;
                JMenuItem mi = new JMenuItem(ac);
                //obj.add(mi);
                listaMenus.put(ca.command, mi);
                //ListaDeAcoes.add(ac);
            }
        }
        obj.setText(Editor.fromConfiguracao.getValor("Controler.interface.menu.menuDiagrama.texto"));
        char b = Editor.fromConfiguracao.getValor("Controler.interface.menu.menuDiagrama.mtecla").charAt(0);
        obj.setMnemonic(b);
        BarraMenu = obj;
    }

    public void PopuleMenus(JMenu menuEditar, JMenu menuArquivo, JPopupMenu popup) {
        int i = 0;
        ArrayList<String> forMEdt = new ArrayList<>();
        forMEdt.add(menuComandos.cmdUndo.toString());
        forMEdt.add(menuComandos.cmdRendo.toString());
        forMEdt.add(menuComandos.cmdCut.toString());
        forMEdt.add(menuComandos.cmdCopy.toString());
        forMEdt.add(menuComandos.cmdCopyImg.toString());
        forMEdt.add(menuComandos.cmdPaste.toString());
        forMEdt.add(menuComandos.cmdCopyFormat.toString());
        forMEdt.add(menuComandos.cmdPasteFormat.toString());
        forMEdt.add(menuComandos.cmdRealcar.toString());
        forMEdt.add(menuComandos.cmdSelProx.toString());
        forMEdt.add(menuComandos.cmdSelAnt.toString());
        forMEdt.add(menuComandos.cmdSelectAll.toString());
        forMEdt.add(menuComandos.cmdBringToFront.toString());
        forMEdt.add(menuComandos.cmdSendToBack.toString());
        forMEdt.add(menuComandos.cmdDel.toString());

        ArrayList<String> forMArq = new ArrayList<>();
        forMArq.add(menuComandos.cmdNew.toString());
        forMArq.add(menuComandos.cmdOpen.toString());
        forMArq.add(menuComandos.cmdClose.toString());
        forMArq.add(menuComandos.cmdPrint.toString());
        forMArq.add(menuComandos.cmdExport.toString());
        forMArq.add(menuComandos.cmdSave.toString());
        forMArq.add(menuComandos.cmdSaveAs.toString());
        forMArq.add(menuComandos.cmdSaveAll.toString());

        String[] btns = new String[]{menuComandos.cmdMicroAjuste0.toString(), menuComandos.cmdMicroAjuste1.toString(),
            menuComandos.cmdMicroAjuste2.toString(), menuComandos.cmdMicroAjuste3.toString(),
            //menuComandos.cmdDimCp.toString(), 
            menuComandos.cmdDimPastLeft.toString(), menuComandos.cmdDimPastTop.toString(),
            menuComandos.cmdDimPastRight.toString(), menuComandos.cmdDimPastBottom.toString(), menuComandos.cmdDimPastWidth.toString(),
            menuComandos.cmdDimPastHeight.toString(), menuComandos.cmdDimAlignH.toString(), menuComandos.cmdDimAlignV.toString()
        };
        ArrayList<String> justBtns = new ArrayList<>();
        justBtns.addAll(Arrays.asList(btns));

        for (ConfigAcao ca : Lista) {
            boolean medt = forMEdt.indexOf(ca.command) > -1;
            if (medt || forMArq.indexOf(ca.command) > -1) {
                try {
                    Acao ac = new Acao(editor, ca.texto, ca.ico, ca.descricao, ca.command);
                    ac.IDX = -1;
                    ac.normal = false;
                    String str = "Controler.comandos." + ca.command.substring(3).toLowerCase() + ".tecla";
                    KeyStroke k = KeyStroke.getKeyStroke(Editor.fromConfiguracao.getValor(str));
                    ac.putValue(Acao.ACCELERATOR_KEY, k);

                    char a = Editor.fromConfiguracao.getValor("Controler.comandos." + ca.command.substring(3).toLowerCase() + ".mtecla").charAt(0);
                    ac.putValue(Acao.MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(a));

                    JMenuItem mi = new JMenuItem(ac);
                    //mi.setAccelerator(k);
                    if (medt) {
                        i++;
                        if (i == 4 || i == 9 || i == 10 || i == 16) {
                            menuEditar.addSeparator();// add(new JSeparator());
                            popup.addSeparator();
                        }
                        menuEditar.add(mi);
                        popup.add(new JMenuItem(ac));

                        ListaDeAcoesEditaveis.add(ac);
                    } else {
                        if (menuComandos.cmdNew.toString().equals(ca.command)) {
                            JMenu men = new JMenu(ac);
                            men.setText(ca.texto);
                            //ac.putValue(Action.ACTION_COMMAND_KEY, "");

                            for (Diagrama.TipoDeDiagrama tp : Diagrama.TipoDeDiagrama.values()) {
                                String tmp = Editor.fromConfiguracao.getValor("Inspector.lst.tipodiagrama." + tp.name().substring(2).toLowerCase());
                                ac = new Acao(editor, tmp, "Controler.interface.Diagrama.Icone." + tp.name().substring(2), tmp, ca.command);
                                ac.IDX = -1;
                                ac.normal = false;

                                str = "Controler.comandos." + tp.name().substring(2).toLowerCase() + ".tecla";
                                k = KeyStroke.getKeyStroke(Editor.fromConfiguracao.getValor(str));
                                ac.putValue(Acao.ACCELERATOR_KEY, k);

                                a = Editor.fromConfiguracao.getValor("Controler.comandos." + tp.name().substring(2).toLowerCase() + ".mtecla").charAt(0);
                                ac.putValue(Acao.MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(a));
                                mi = new JMenuItem(ac);
                                mi.setName(tp.name());
                                men.add(mi);
                            }
                            menuArquivo.add(men);

                        } else {
                            menuArquivo.add(mi);
                        }
                        if (menuComandos.cmdSaveAll.toString().equals(ca.command) || menuComandos.cmdSave.toString().equals(ca.command) || menuComandos.cmdExport.toString().equals(ca.command)) {
                            ListaDeAcoesEditaveis.add(ac);
                        }

                    }
                    //} catch (Exception e) {
                } finally {
                }
            } else if (justBtns.indexOf(ca.command) > -1) {
                Acao ac = new Acao(editor, ca.texto, ca.ico, ca.descricao, ca.command);
                ac.IDX = -1;
                ac.normal = false;
                String str = "Controler.comandos." + ca.command.substring(3).toLowerCase() + ".tecla";
                KeyStroke k = KeyStroke.getKeyStroke(Editor.fromConfiguracao.getValor(str));
                ac.putValue(Acao.ACCELERATOR_KEY, k);

                char a = Editor.fromConfiguracao.getValor("Controler.comandos." + ca.command.substring(3).toLowerCase() + ".mtecla").charAt(0);
                ac.putValue(Acao.MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(a));

                ListaDeAcoesEditaveis.add(ac);
            }
        }
        JMenuItem quit = menuArquivo.getItem(0);
        //if ("Exit".equals(quit.getText())) {
        menuArquivo.remove(quit);
        javax.swing.Action ac = quit.getAction();
        KeyStroke k = KeyStroke.getKeyStroke(Editor.fromConfiguracao.getValor("Controler.interface.menu.quit.tecla"));
        ac.putValue(Acao.ACCELERATOR_KEY, k);
        ac.putValue(Acao.SHORT_DESCRIPTION, Editor.fromConfiguracao.getValor("Controler.interface.menu.quit.texto"));
        ac.putValue(Acao.NAME, Editor.fromConfiguracao.getValor("Controler.interface.menu.quit.texto"));
        char a = Editor.fromConfiguracao.getValor("Controler.interface.menu.quit.mtecla").charAt(0);
        ac.putValue(Acao.MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(a));
        menuArquivo.add(new JSeparator());
        menuArquivo.add(quit);
        //}
        menuEditar.setText(Editor.fromConfiguracao.getValor("Controler.interface.menu.menuEditar.texto"));
        char b = Editor.fromConfiguracao.getValor("Controler.interface.menu.menuEditar.mtecla").charAt(0);
        menuEditar.setMnemonic(b);
        menuArquivo.setText(Editor.fromConfiguracao.getValor("Controler.interface.menu.fileMenu.texto"));
        b = Editor.fromConfiguracao.getValor("Controler.interface.menu.fileMenu.mtecla").charAt(0);
        menuArquivo.setMnemonic(b);

        makeEnableComands();
    }

    public void SelecioneForAction(Acao ac) {
        if (Barra == null) {
            return;
        }
        for (Component c : Barra.getComponents()) {
            if (c instanceof JToggleButton) {
                JToggleButton btn = (JToggleButton) c;
                javax.swing.Action tmp = btn.getAction();
                if (tmp instanceof Acao) {
                    if (((Acao) tmp).IDX == ac.IDX) {
                        btn.setSelected(true);
                    }
                }
            }
        }
    }

    public void makeEnableComands() {
        boolean alll = editor.diagramaAtual == null;
        for (Acao ac : ListaDeAcoesEditaveis) {
            if (alll) {
                ac.setEnabled(false);
                continue;
            }
            String cmd = ac.getValue(Acao.ACTION_COMMAND_KEY).toString();

            if (cmd.equals(Comandos.cmdApagar.toString())) {
                ac.setEnabled(editor.diagramaAtual.getListaDeItens().size() > Diagrama.totalInicialDeItens);
            } else {
                menuComandos comm = menuComandos.valueOf(cmd);
                switch (comm) {
                    case cmdUndo:
                        ac.setEnabled(editor.podeDesfazer());
                        break;
                    case cmdRendo:
                        ac.setEnabled(editor.podeRefazer());
                        break;
                    case cmdCut:
                    case cmdCopy:
                    case cmdCopyImg:
                    case cmdDel:
                    case cmdCopyFormat:
                    case cmdMicroAjuste0:
                    case cmdMicroAjuste1:
                    case cmdMicroAjuste2:
                    case cmdMicroAjuste3:
                        ac.setEnabled(editor.diagramaAtual.TemSelecionado());
                        break;
                    case cmdRealcar:
                        if (!editor.diagramaAtual.isRealce()) {
                            ac.setEnabled(editor.diagramaAtual.TemSelecionado());
                            ac.Renomeie("Controler.comandos.realcar.descricao");
                        } else {
                            ac.setEnabled(true);
                            ac.Renomeie("Controler.comandos.realcar.descricao.b");
                        }
                        break;
                    case cmdSelProx:
                    case cmdSelAnt:
                    case cmdBringToFront:
                    case cmdSendToBack:
                        int tl = editor.diagramaAtual.getItensSelecionados().size();
                        ac.setEnabled(tl == 1);
                        break;
                    case cmdSelectAll:
                    case cmdExport:
                        ac.setEnabled(editor.diagramaAtual.getListaDeItens().size() > Diagrama.totalInicialDeItens);
                        break;

                    case cmdDimPastLeft:
                    case cmdDimPastTop:
                    case cmdDimPastRight:
                    case cmdDimPastBottom:
                    case cmdDimPastWidth:
                    case cmdDimPastHeight:
                    case cmdDimAlignH:
                    case cmdDimAlignV:
                        ac.setEnabled(editor.diagramaAtual.getItensSelecionados().size() > 1);
                        break;
                    case cmdPasteFormat:
                        ac.setEnabled(editor.diagramaAtual.TemSelecionado() && editor.CopiadorFormatacao.isCopiado());
                        break;
                    case cmdSaveAll:
                        ac.setEnabled(editor.getDiagramas().stream().anyMatch(d -> d.getMudou()));
                        break;
                    case cmdSave:
                        ac.setEnabled(editor.diagramaAtual.getMudou());
                        break;
                }
            }
        }
    }

    public void AjusteBarra(ArrayList<String> comm) {
        int i = 0;
        for (Comandos k : Comandos.values()) {
            String ks = k.name();
            if (comm.indexOf(ks) > -1) {
                Barra.add(listaBotoes.get(ks));
                i++;
            } else {
                Barra.remove(listaBotoes.get(ks));
            }
        }
        //A borracha
        Component bkp = Barra.getComponent(1);
        Barra.remove(bkp);
        Barra.add(bkp);

        LayoutManager la = Barra.getLayout();
        if (la instanceof GridLayout) {
            ((GridLayout) la).setRows(i + 2);
        }
        Barra.revalidate();

        //Menus
        for (Comandos k : Comandos.values()) {
            String ks = k.name();
            if (comm.indexOf(ks) > -1) {
                BarraMenu.add(listaMenus.get(ks));
            } else {
                BarraMenu.remove(listaMenus.get(ks));
            }
        }
        BarraMenu.revalidate();
    }

    /**
     * Pega uma imagem a partir da configuração.
     *
     * @param path nome da classe
     * @return Imagem
     */
    public ImageIcon getImagem(String path) {
        String caminhoCompleto = "diagrama." + path + ".img";
        if (ImagemDeDiagrama.containsKey(path)) {
            return ImagemDeDiagrama.get(path);
        }
        ImageIcon img = null;
        try {
            Image imgx = Configuer.getImageFromResource(caminhoCompleto);
            if (imgx != null) {
                imgx = imgx.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
                img = new ImageIcon(imgx);
            }
        } catch (Exception e) {
            util.BrLogger.Logger("ERROR_GET_RESOURCE_IMG", e.getMessage());
            return null;
        }
        if (img != null) {
            ImagemDeDiagrama.put(path, img);
        }
        return img;
    }

    /**
     * Pega uma imagem a partir da configuração em tamanho normal.
     *
     * @param path nome da classe
     * @return Imagem
     */
    public ImageIcon getImagemNormal(String path) {
        String caminhoCompleto = "diagrama." + path + ".img";
        if (ImagemDeDiagrama.containsKey("N" + path)) {
            return ImagemDeDiagrama.get("N" + path);
        }
        ImageIcon img = null;
        try {
            Image imgx = Configuer.getImageFromResource(caminhoCompleto);
            img = new ImageIcon(imgx);
        } catch (Exception e) {
            util.BrLogger.Logger("ERROR_GET_RESOURCE_IMG", e.getMessage());
            return null;
        }
        ImagemDeDiagrama.put("N" + path, img);
        return img;
    }
}
