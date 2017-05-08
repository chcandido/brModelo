/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principal.cli;

import controlador.Diagrama;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import util.DesenhadorDeTexto;

/**
 *
 * @author ccandido
 */
public class MasterCli extends JPanel implements KeyListener {

    public MasterCli() {
        super();
        processador = new CliDiagramaProcessador(this);
        setBackground(Color.white);

        setFocusTraversalKeysEnabled(false);
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(this);
        // <editor-fold defaultstate="collapsed" desc="Teclas">
        KeyStroke key_crtl = KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, InputEvent.CTRL_DOWN_MASK);
        KeyStroke key_crtl_up = KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, 0, true);
        KeyStroke key_shift = KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK);
        KeyStroke key_shift_up = KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, 0, true);
        KeyStroke key_alt = KeyStroke.getKeyStroke(KeyEvent.VK_ALT, InputEvent.ALT_DOWN_MASK);
        KeyStroke key_alt_up = KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, true);
        KeyStroke key_tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, true);

        final String ac_crtl = "AC_CRTL";
        final String ac_crtl_up = "AC_CRTL_UP";
        final String ac_shift = "AC_SHIFT";
        final String ac_shift_up = "AC_SHIFT_UP";
        final String ac_alt = "AC_ALT";
        final String ac_alt_up = "AC_ALT_UP";
        final String ac_tab = "TAB";

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(key_crtl, ac_crtl);
        inputMap.put(key_crtl_up, ac_crtl_up);
        inputMap.put(key_shift, ac_shift);
        inputMap.put(key_shift_up, ac_shift_up);
        inputMap.put(key_alt, ac_alt);
        inputMap.put(key_alt_up, ac_alt_up);
        inputMap.put(key_tab, ac_tab);

        Action al_crtl = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                controlDown = true;
            }
        };
        Action al_crtl_up = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                controlDown = false;
            }
        };
        Action al_shift = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                shiftDown = true;
            }
        };
        Action al_shift_up = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                shiftDown = false;
            }
        };
        Action al_alt = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                altDown = true;
            }
        };
        Action al_alt_up = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!altDown) {
                    /////bug do java ao ler o key AltGr do teclado em Windows - swing
                    controlDown = false;
                }
                altDown = false;
            }
        };
        Action al_tab = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AutoComplete();
            }
        };

//        Action actionListener = new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                System.out.println(actionEvent.toString());
//                if (isFocusOwner()) return;  //ação ocorrerá pelo keylistener.
//                setControlDown(actionEvent.getModifiers() == ActionEvent.CTRL_MASK);
//                setShiftDown(actionEvent.getModifiers() == ActionEvent.SHIFT_MASK);
//                setAltDown(actionEvent.getModifiers() == ActionEvent.ALT_MASK);
//            }
//        };
        ActionMap actionMap = getActionMap();
        actionMap.put(ac_crtl, al_crtl);
        actionMap.put(ac_crtl_up, al_crtl_up);
        actionMap.put(ac_shift, al_shift);
        actionMap.put(ac_shift_up, al_shift_up);
        actionMap.put(ac_alt, al_alt);
        actionMap.put(ac_alt_up, al_alt_up);
        actionMap.put(ac_tab, al_tab);
        setActionMap(actionMap);
        // </editor-fold>
    }

    //<editor-fold defaultstate="collapsed" desc="Teclas e campos">
    @Override
    public void keyTyped(KeyEvent e) {
        key = e.getKeyChar();
        if (key == (char) 127 || key == '\b' || key == '\n') {
            e.consume();
            return;
        }

        if (e.getModifiers() == KeyEvent.CTRL_MASK) {
            return;
        }

        palavra = insertChar(palavra, key);
        posCursor++;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isControlDown()) {
            if (processeAtalhos(e.getKeyCode())) {
                e.consume();
            }
        } else {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    doEnter(palavra);
                    e.consume();
                    break;
                case KeyEvent.VK_UP:
                    posicioneHistorico(+1);
                    e.consume();
                    break;
                case KeyEvent.VK_DOWN:
                    posicioneHistorico(-1);
                    e.consume();
                    break;
                case KeyEvent.VK_LEFT:
                    movimenteCursor(-1);
                    e.consume();
                    break;
                case KeyEvent.VK_RIGHT:
                    movimenteCursor(+1);
                    e.consume();
                    break;
//                case KeyEvent.VK_TAB:
//                    break;
                case KeyEvent.VK_DELETE:
                    apagarLetra(posCursor);
                    e.consume();
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    apagarLetra(posCursor - 1);
                    e.consume();
                    break;
            }
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    // </editor-fold>

    private char key = '.';
    private String prompt = "# ";
    private final int espaco = 5;
    private int posCursor = 0;
    private StringBuilder strs = new StringBuilder();
    private Rectangle cliRect = getBounds();
    private String palavra = prompt;
    private final DesenhadorDeTexto dzExecutado = new DesenhadorDeTexto();
    private final DesenhadorDeTexto dzCli = new DesenhadorDeTexto();
    private int alturaTexto = 0;
    private ArrayList<String> historico = new ArrayList<>();

    public ArrayList<String> getHistorico() {
        return historico;
    }

    public void setHistorico(ArrayList<String> historico) {
        this.historico = historico;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    //<editor-fold defaultstate="collapsed" desc="Status teclas">
    private boolean shiftDown = false;
    private boolean altDown = false;
    private boolean controlDown = false;

    public boolean isShiftDown() {
        return shiftDown;
    }

    public void setShiftDown(boolean shiftDown) {
        this.shiftDown = shiftDown;
    }

    public boolean isAltDown() {
        return altDown;
    }

    public boolean isControlDown() {
        return controlDown;
    }

    public void setControlDown(boolean controlDown) {
        this.controlDown = controlDown;
    }
    // </editor-fold>

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints renderHints
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2d.addRenderingHints(renderHints);

        g2d.setPaint(Color.BLACK);
        Stroke stroke = new BasicStroke(2.f,
                BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
        g2d.setStroke(stroke);

        PintarTextos(g2d);
    }

    public void Clear() {
        strs = new StringBuilder();
        strs.append(prompt);
        repaint();
    }
    
    private void PintarTextos(Graphics2D g) {
        if (strs.length() > 0) {
            dzExecutado.PinteTexto(g, Color.BLUE, new Rectangle(espaco, espaco, getWidth() - espaco * 2, getHeight() - espaco), strs.toString());
        }
        int alturaOld = dzExecutado.getMaxHeigth() + 2 * espaco;
        String txt = insertChar(palavra, '|');

        cliRect = new Rectangle(espaco, alturaOld, getWidth() - espaco * 2, getHeight());
        dzCli.PinteTexto(g, Color.yellow, cliRect, txt); //apenas para calcular a altura.
        int recuo = g.getFontMetrics().getDescent();
        int alturaCli = dzCli.getMaxHeigth() + 2 * recuo + espaco;

        cliRect = new Rectangle(espaco, alturaOld, getWidth() - espaco * 2, alturaCli);
        g.fillRect(cliRect.x, cliRect.y, cliRect.width, cliRect.height);
        int pos = alturaCli + alturaOld;
        alturaTexto = pos;
        dzCli.PinteTexto(g, Color.black, new Rectangle(cliRect.x, cliRect.y + recuo, cliRect.width, cliRect.height - 2 * recuo), txt);
        AtualizaTamanho();
    }

    private String insertChar(String apalavra, char c) {
        int tmpTxtPos = posCursor + getPrompt().length();
        apalavra = apalavra.substring(0, tmpTxtPos) + c
                + (apalavra.length() > tmpTxtPos ? apalavra.substring(tmpTxtPos, apalavra.length()) : ""); //prompt
        return apalavra;
    }

    private String insertTexto(String texto, String ainserir) {
        int tmpTxtPos = posCursor + getPrompt().length();
        texto = texto.substring(0, tmpTxtPos) + ainserir
                + (texto.length() > tmpTxtPos ? texto.substring(tmpTxtPos, texto.length()) : ""); //prompt
        return texto;
    }

    public void AtualizaTamanho() {
        int pos = alturaTexto;
        if (pos > getHeight()) {
            JViewport jvp = ((JViewport) getParent());

            setSize(jvp.getExtentSize().width, pos + 2);
            setPreferredSize(getSize());

            if (alturaTexto > jvp.getHeight()) {
                jvp.setViewPosition(new Point(espaco, getHeight()));
            }
            repaint();
        }
    }

    public boolean doEnter(String apalavra) {
        appendHistorico(apalavra);
        strs.append(apalavra).append("\n");
        String ret = getProcessador().processeComando(apalavra.substring(getPrompt().length()));
        if (!ret.isEmpty() && !ret.equals(getProcessador().msgNOTHING)) {
            strs.append(ret).append("\n");
        }
        if (getProcessador().isJustNewLine()) {
            palavra = getPrompt();
            posCursor = 0;
            cliRect = getBounds();
        }
        repaint();
        return getProcessador().isLastCmdErro();
    }

    private CliDiagramaProcessador processador = new CliDiagramaProcessador(this);

    public CliDiagramaProcessador getProcessador() {
        return processador;
    }

    public void setProcessador(CliDiagramaProcessador processador) {
        this.processador = processador;
    }

    public void doPaste() {
        String txt = Diagrama.getClipboardContents();
        if (txt.isEmpty()) {
            return;
        }
        donePaste(txt);
        AtualizaTamanho();
    }

    private void donePaste(String txt) {
        String tmp = palavra.substring(prompt.length());
        if (!tmp.isEmpty()) {
            txt = insertTexto(palavra, txt).substring(prompt.length());
        }
        String[] comms = txt.replaceAll("\r\n", "\n").split("\n");
        for (String str : comms) {
            posCursor = (prompt + str).length();
            if (!doEnter(prompt + str)) {
                doShowMsg(getProcessador().getErroMsg());
            }
        }
    }
    
    public void doPaste(String txt) {
        if (txt.isEmpty()) {
            return;
        }
        donePaste(txt);
        AtualizaTamanho();
    }

    public void doShowMsg(String msg) {
        strs.append(msg).append("\n");
        palavra = getPrompt();
        posCursor = 0;
        cliRect = getBounds();
        repaint(cliRect);
    }

    public void Cancelar() {
        doShowMsg(getProcessador().doCancel());
    }

    public boolean processeAtalhos(int keyCode) {
        boolean res = false;
        switch (keyCode) {
            case KeyEvent.VK_V:
                doPaste();
                res = true;
                break;
            case KeyEvent.VK_Q:
                Sair();
                res = true;
                break;
            case KeyEvent.VK_D:
                Cancelar();
                res = true;
                break;
        }
        return res;
    }

    private JDialog janela = null;

    public JDialog getJanela() {
        return janela;
    }

    public final void setJanela(JDialog janela) {
        this.janela = janela;
    }

    public void Sair() {
        Cancelar();
        getJanela().setVisible(false);
    }

    public void setPalavra(String str) {
        palavra = getPrompt() + str;
        posCursor = str.length();
        repaint();
    }

    private int posHist = -1;

    public void appendHistorico(String txt) {
        if (!txt.isEmpty()) {
            posHist = -1;
            if (!historico.isEmpty()) {
                if (historico.get(0).equals(txt)) {
                    return;
                }
            }
            historico.add(0, txt.substring(getPrompt().length()));
        }
    }

    private void posicioneHistorico(int i) {
        if (historico.isEmpty()) {
            return;
        }
        int tmp = posHist + i;
        if (tmp < 0 || tmp >= historico.size()) {
            setPalavra("");
            if (tmp < 0) {
                posHist = -1;
            } else {
                posHist = historico.size();
            }
            return;
        }
        posHist += i;
        String res = historico.get(posHist);
        setPalavra(res);
    }

    private void movimenteCursor(int i) {
        int tmp = posCursor + i;
        String txt = palavra.substring(prompt.length());
        if (tmp < 0 || tmp > txt.length()) {
            return;
        }
        posCursor = tmp;
        repaint();
    }

    private void AutoComplete() {
        String ac = getProcessador().DoAutoComplete(palavra.substring(prompt.length()).trim());
        if (!ac.isEmpty()) {
            setPalavra(ac);
        }
    }

    private void apagarLetra(int pos) {
        String tmp = palavra.substring(prompt.length());
        if (tmp.isEmpty() || pos > tmp.length() - 1 || pos < 0) {
            return;
        }
        int tmpTxtPos = pos + getPrompt().length();
        palavra = palavra.substring(0, tmpTxtPos)
                + (palavra.length() > tmpTxtPos + 1 ? palavra.substring(tmpTxtPos + 1, palavra.length()) : ""); //prompt
        posCursor = pos;
        repaint();
    }

}
