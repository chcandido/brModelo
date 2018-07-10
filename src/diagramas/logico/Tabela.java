/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.logico;

import controlador.Diagrama;
import controlador.Editor;
import controlador.apoios.TreeItem;
import controlador.inspector.InspectorProperty;
import desenho.ElementarEvento;
import desenho.FormaElementar;
import desenho.formas.Forma;
import desenho.linhas.PontoDeLinha;
import desenho.preDiagrama.baseDrawerFromForma;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import principal.Aplicacao;
import util.Constantes;
import util.DesenhadorDeTexto;

/**
 *
 * @author ccandido
 */
public class Tabela extends baseDrawerFromForma {

    private static final long serialVersionUID = -8275314691624584199L;

    public Tabela(Diagrama modelo, String texto) {
        super(modelo, texto);
        Inicie();
    }

    public Tabela(Diagrama modelo) {
        super(modelo);
        Inicie();
    }

    private void Inicie() {
        //setDelimite(false);
        setAlfa(0.25f);
        setGradiente(true);
        //setGradienteStartColor(new Color(204, 204, 255));
        showOrgDiag = true;
        INI_ORGDIAG = roundrect / 2;
//        getAncorasCode().add(CODE_EDT_CMP);
//        getAncorasCode().add(CODE_EDT_CMP_TP);
//        getAncorasCode().add(CODE_DDL);
//        getAncorasCode().add(CODE_SOBE);
//        getAncorasCode().add(CODE_DESCE);
//        getAncorasCode().add(CODE_DEL_CMP_CONST);
    }

    @Override
    public ArrayList<Integer> getAncorasCode() {
        ArrayList<Integer> res = super.getAncorasCode();
        Integer[] ancorasCode = new Integer[]{CODE_EDT_CMP, CODE_EDT_CMP_TP, CODE_DDL, CODE_SOBE, CODE_DESCE, CODE_DEL_CMP_CONST};
        res.addAll(Arrays.asList(ancorasCode));
        return res;
    }

    private ArrayList<Campo> Campos = new ArrayList<>();

    public final ArrayList<Campo> getCampos() {
        return Campos;
    }
    private Campo campoSelecionado = null;

    public Campo getCampoSelecionado() {
        return campoSelecionado;
    }

    public void setCampoSelecionado(Campo selecionado) {
        if (this.campoSelecionado == selecionado) {
            return;
        }
        if (this.campoSelecionado != null) {
            this.campoSelecionado.setSelecionado(false);
        }
        this.campoSelecionado = selecionado;
        if (this.campoSelecionado != null) {
            this.campoSelecionado.setSelecionado(true);
            setConstraintSelecionado(null);
        }
        if (getMaster().getSelecionado() == this) {
            getMaster().PerformInspector();
        }
        InvalidateArea();
    }

    public int getCampoSelectedIndex() {
        return Campos.indexOf(getCampoSelecionado());
    }

    private ArrayList<Constraint> Constraints = new ArrayList<>();

    public final ArrayList<Constraint> getConstraints() {
        return Constraints;
    }
    private Constraint constraintSelecionado = null;

    public Constraint getConstraintSelecionado() {
        return constraintSelecionado;
    }

    public void setConstraintSelecionado(Constraint selecionado) {
        if (this.constraintSelecionado == selecionado) {
            return;
        }
        if (this.constraintSelecionado != null) {
            this.constraintSelecionado.setSelecionado(false);
//            if (this.constraintSelecionado.getLigacao() != null && this. constraintSelecionado.roqued) {
//                this.constraintSelecionado.getLigacao().PerformRoqued(false);
//            }
        }
        this.constraintSelecionado = selecionado;
        if (this.constraintSelecionado != null) {
            this.constraintSelecionado.setSelecionado(true);
            setCampoSelecionado(null);
//            if (this.constraintSelecionado.getLigacao() != null) {
//                this.constraintSelecionado.getLigacao().PerformRoqued(true);
//            }
        }
        if (getMaster().getSelecionado() == this) {
            getMaster().PerformInspector();
        }
        InvalidateArea();
    }

    public int getConstraintSelectedIndex() {
        return Constraints.indexOf(getConstraintSelecionado());
    }

    public void setCampoSelectedIndex(int selectedIndex) {
        if (selectedIndex > -1 && selectedIndex < Campos.size()) {
            setCampoSelecionado(Campos.get(selectedIndex));
        } else {
            setCampoSelecionado(null);
        }
    }

    public void setConstraintSelectedIndex(int selectedIndex) {
        if (selectedIndex > -1 && selectedIndex < Constraints.size()) {
            setConstraintSelecionado(Constraints.get(selectedIndex));
        } else {
            setConstraintSelecionado(null);
        }
    }

    public ArrayList<Tabela> getListaDeTabelasLigadas() {
        ArrayList<Tabela> res = new ArrayList<>();
        res.add(this);
        getListaDeFormasLigadas().stream().filter((fe) -> (fe instanceof Tabela)).map(fe -> (Tabela) fe).forEach((fe) -> {
            res.add(fe);
        });
        return res;
    }

    public ArrayList<String> getStrListaDeTabelas(ArrayList<Tabela> lst) {
        ArrayList<String> res = new ArrayList<>();
        lst.stream().forEach((fe) -> {
            res.add(fe.getTexto());
        });
        return res;
    }

    public ArrayList<String> getListaDeCampos() {
        ArrayList<String> res = new ArrayList<>();
        Campos.stream().forEach((fe) -> {
            res.add((fe.isKey() ? "(*) " : "") + fe.getTexto());
        });
        return res;
    }

    protected final String COMM_EDT_CMPS = "edt_campos";
    protected final String COMM_EDT_CMPS_TP = "edt_campos_tipo";
    private final int EDITOR_CAMPOS = 200317;
    private final int EDITOR_CAMPOS_TP = 180717;
    private final int PRINT_DDL = 310317;
    private final int DESCE_CONSTAN = 110417;
    private final int SOBE_CONSTAN = -110417;
    private final int DESCE_CAMPO = 1;
    private final int SOBE_CAMPO = -1;

    protected final String COMM_RI = "tabela.constraint";
    protected final String COMM_RI_PK = "constraint.key";
    public final String COMM_RI_FK = "constraint.fkey";
    protected final String COMM_RI_UN = "constraint.unique";

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {

        ArrayList<InspectorProperty> res = GP;
        if (getCampoSelecionado() == null && getConstraintSelecionado() == null) {
            res.add(InspectorProperty.PropertyFactorySeparador("tabela.exibicao"));
            res.add(InspectorProperty.PropertyFactorySN("tabela.autosize", "setAutosize", isAutosize()));
            res.add(InspectorProperty.PropertyFactorySN("tabela.showinplain", "setShowInPlain", isShowInPlain()));
            res.add(InspectorProperty.PropertyFactorySN("tabela.showddl", "setShowDDL", isShowDDL()));
            res.add(InspectorProperty.PropertyFactorySN("constraints.mostrar", "setMostrarConstraints", isMostrarConstraints()).AddCondicaoForTrue(new String[]{"setPlainIR"}));
            res.add(InspectorProperty.PropertyFactorySN("tabela.ir.plain", "setPlainIR", isPlainIR()));
            res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "tabela.printddl").setTag(PRINT_DDL));
        }

        if (getCampoSelecionado() != null) {
            Campo sel = getCampoSelecionado();
            res.clear();
            sel.CompleteGenerateProperty(res);
        } else if (getConstraintSelecionado() != null) {
            res.clear();
            getConstraintSelecionado().CompleteGenerateProperty(res);
        }

        if (getCampos().size() > 1 && getConstraintSelecionado() == null) {
            res.add(InspectorProperty.PropertyFactorySeparador("tabela.campos", true));
            int i = 11;
            for (Campo c : getCampos()) {
                if (!c.isSelecionado()) {
                    res.add(InspectorProperty.PropertyFactoryCommandPlain(nomeComandos.cmdDoAnyThing.name(), "tabela.campo", "[" + c.getTexto() + "]").setTag(i++));
                } else {
                    i++;
                }
            }
        }

        if (getConstraints().size() > 1 && getCampoSelecionado() == null) {
            res.add(InspectorProperty.PropertyFactorySeparador("tabela.constraint", true));
            int i = 1001;
            for (Constraint c : getConstraints()) {
                if (!c.isSelecionado()) {
                    res.add(InspectorProperty.PropertyFactoryCommandPlain(nomeComandos.cmdDoAnyThing.name(), "tabela.constraint", "["
                            + c.getNomeFormatado() + "]").setTag(i++));
                } else {
                    i++;
                }
            }
        }

        if (getCampoSelecionado() == null && getConstraintSelecionado() == null) {
            super.CompleteGenerateProperty(GP);
        }
//            res.add(InspectorProperty.PropertyFactorySeparador(COMM_RI));

        res.add(InspectorProperty.PropertyFactorySeparador("tabela.edtitores", true));

        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), COMM_RI_PK).setTag(Constraint.TAG_COMMAND_PK));

        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), COMM_RI_UN).setTag(Constraint.TAG_COMMAND_UN));

        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), COMM_RI_FK).setTag(Constraint.TAG_COMMAND_FK));

        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), COMM_EDT_CMPS).setTag(EDITOR_CAMPOS));
        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), COMM_EDT_CMPS_TP).setTag(EDITOR_CAMPOS_TP));
        //}
        return GP;
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Autosize", isAutosize()));

        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Gradiente", isGradiente()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteStartColor", getGradienteStartColor()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteEndColor", getGradienteEndColor()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "GDirecao", getGDirecao()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Alfa", (int) (100 * getAlfa())));

        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "ShowDDL", isShowDDL()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "ShowInPlain", isShowInPlain()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "MostrarConstraints", isMostrarConstraints()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "PlainIR", isPlainIR()));

        Element sbCampos = doc.createElement("Campos");
        getCampos().stream().forEach((c) -> {
            c.ToXmlValores(doc, sbCampos);
        });
        me.appendChild(sbCampos);
        Element sbConstraints = doc.createElement("Constraints");
        getConstraints().stream().forEach((c) -> {
            c.ToXmlValores(doc, sbConstraints);
        });
        me.appendChild(sbConstraints);
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        setAutosize(util.XMLGenerate.getValorBooleanFrom(me, "Autosize"));
        setPlainIR(util.XMLGenerate.getValorBooleanFrom(me, "PlainIR"));

        int l = util.XMLGenerate.getValorIntegerFrom(me, "GDirecao");
        if (l != -1) {
            setGDirecao(l);
        }
        l = util.XMLGenerate.getValorIntegerFrom(me, "Alfa");
        if (l != -1) {
            SetAlfa(l);
        }

        setGradiente(util.XMLGenerate.getValorBooleanFrom(me, "Gradiente"));
        Color cor = util.XMLGenerate.getValorColorFrom(me, "GradienteStartColor");
        if (cor != null) {
            setGradienteStartColor(cor);
        }
        cor = util.XMLGenerate.getValorColorFrom(me, "GradienteEndColor");
        if (cor != null) {
            setGradienteEndColor(cor);
        }
        setShowDDL(util.XMLGenerate.getValorBooleanFrom(me, "ShowDDL"));
        setShowInPlain(util.XMLGenerate.getValorBooleanFrom(me, "ShowInPlain"));
        setMostrarConstraints(util.XMLGenerate.getValorBooleanFrom(me, "MostrarConstraints"));

        NodeList ptLst = me.getElementsByTagName("Campos");
        Element pontos = (Element) ptLst.item(0);
        ptLst = pontos.getChildNodes();

        for (int i = 0; i < ptLst.getLength(); i++) {
            Campo c = new Campo(this);
            Node tmp = ptLst.item(i);
            if (tmp.getNodeType() == Node.ELEMENT_NODE) {
                c.LoadFromXML((Element) tmp, colando);
            }
        }

        ptLst = me.getElementsByTagName("Constraints");
        pontos = (Element) ptLst.item(0);
        ptLst = pontos.getChildNodes();

        for (int i = 0; i < ptLst.getLength(); i++) {
            Constraint c = new Constraint(this);
            Node tmp = ptLst.item(i);
            if (tmp.getNodeType() == Node.ELEMENT_NODE) {
                c.LoadFromXML((Element) tmp, colando);
            }
        }
        return true;
    }

    @Override
    public boolean CommitXML(Element me, HashMap<Element, FormaElementar> mapa) {
        if (!super.CommitXML(me, mapa)) {
            return false;
        }

        Element constraits = util.XMLGenerate.FindByNodeName(me, "Constraints");
        NodeList ptLst = constraits.getChildNodes();

        int j = 0;
        for (int i = 0; i < ptLst.getLength(); i++) {
            Constraint c = getConstraints().get(j);
            Node tmp = ptLst.item(i);
            if (tmp.getNodeType() == Node.ELEMENT_NODE) {
                j++;
                if (!c.CommitXML((Element) tmp, mapa)) {
                    return false;
                }
            }
        }
        needRecalc = true;
        return true;
    }

    public Campo Add(String texto) {
        Campo c = new Campo(this);
        c.setTexto(NomeieCampo(texto));
        if (isAutosize()) {
            needRecalc = true;
        }
        setCampoSelecionado(c);
        return c;
    }

    protected int cmpAltura = 22;

    final int cmpAlturaPadrao = 22;

    private boolean showInPlain = false;
    private boolean showDDL = false;

    public boolean isShowInPlain() {
        return showInPlain;
    }

    public void setShowInPlain(boolean showInPlain) {
        this.showInPlain = showInPlain;
        if (!showInPlain) {
            if (isAutosize()) {
                needRecalc = true;
            }
        }
        InvalidateArea();
    }

    public boolean isShowDDL() {
        return showDDL;
    }

    public void setShowDDL(boolean showDDL) {
        this.showDDL = showDDL;
        if (isAutosize()) {
            needRecalc = true;
        }
        InvalidateArea();
    }

    /**
     * Mostrar as IR de maneira simplificada
     */
    private boolean plainIR = true;

    public boolean isPlainIR() {
        return plainIR;
    }

    public void setPlainIR(boolean plainIR) {
        if (this.plainIR == plainIR) {
            return;
        }
        this.plainIR = plainIR;
        InvalidateArea();
    }

    private void FillCampos(Graphics2D g, Rectangle r, boolean normal) {
        Composite originalComposite = g.getComposite();
        float alfa = 1f - getAlfa();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));
        Paint bkpp = g.getPaint();
        g.setColor(getMaster().getBackground()); //# Não: isDisablePainted()? disabledColor : 
        if (!normal) {
            if (isGradiente()) {
                g.setColor(getGradienteStartColor());
            } else {
                g.setColor(getForeColor());
            }
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlfa()));
        g.fill(r);

        g.setPaint(bkpp);
        g.setComposite(originalComposite);
    }

    public void setRoqued(boolean sn, LogicoLinha linha) {
        getConstraints().stream().filter(c -> c.getLigacao() == linha && c.getTipo() == Constraint.CONSTRAINT_TIPO.tpFK).forEach(co -> {
            co.roqued = sn;
            co.getCamposDeDestino().stream().forEach(c -> c.roqued = sn);
            co.getCamposDeOrigem().stream().filter(c -> c != null).forEach(c -> c.roqued = sn);
            if (co.getTabelaDeOrigem() != null) {
                co.getConstraintOrigem().roqued = sn;
                co.getTabelaDeOrigem().repaint();
            }
            co.getTabela().repaint();
        });
    }

    @Override
    public void DoPaint(Graphics2D g) {
        Composite ori = g.getComposite();
        Paint bkp_paint = g.getPaint();
        Rectangle rgn = g.getClipBounds();
        super.DoPaint(g);
        Composite orig = g.getComposite();
        Font bkpf = g.getFont();
        g.setFont(new Font(getFont().getName(), getFont().getStyle(), getFont().getSize() - 2));
        FontMetrics fm = g.getFontMetrics();
        int y = margemTitulo + 1;
        Rectangle r = new Rectangle(getLeft() + 1, getTop() + y + 1, getWidth() - 1, getHeight() - y - piso - 2);
        g.clipRect(r.x - 1, r.y, r.width + 1, r.height + 2);

        g.setComposite(ori);

        int bkpAltura = altura;
        cmpAltura = g.getFontMetrics(g.getFont()).getHeight() + (2 * distSelecao) + 4;
        cmpAltura = Math.max(cmpAltura, cmpAlturaPadrao);

        if (!isShowInPlain()) {
            altura = 0;
            for (Campo c : getCampos()) {
                c.Paint(1, y, cmpAltura, g);
                if (c.roqued) {
                    drawRoqued(c.area, g);
                }
                y += cmpAltura;
                altura += cmpAltura;
            }
        } else {
            altura = 0;
            g.setPaint(this.getForeColor());

            FillCampos(g, r, true);

            int sp = 8;
            Rectangle rt = fm.getStringBounds(getTexto() + " {", g).getBounds();
            y += rt.height + sp;
            altura = rt.height + sp;
            g.drawString(getTexto() + " {", getLeft() + 2, y + getTop());
            int proxW = rt.width + 2;

            Rectangle rtmp = fm.getStringBounds(", ", g).getBounds();
            int larg = rtmp.width;

            int tl = getCampos().size() - 1;
            int i = 0;
            for (Campo c : getCampos()) {
                boolean s = i != tl;
                Rectangle rt2 = fm.getStringBounds(c.getTexto() + (s ? ", " : ""), g).getBounds();
                final int imgl = 16 + 4;
                int w = rt2.width + (c.roqued ? imgl : 0);
                if (!((proxW + w) <= getWidth())) {
                    y += rt2.height + sp;
                    altura += rt.height + sp;
                    proxW = 20;
                }

                c.area = new Rectangle(getLeft() + proxW,
                        getTop() + y - rt2.height + 2,
                        w + (s ? -larg : 0),
                        rt2.height + 4);

                if (c.isSelecionado()) {
                    FillCampos(g, c.area, false);
                }
                if (c.roqued) {
                    drawRoqued(c.area, g);
                }
                g.drawString(c.getTexto() + (s ? ", " : ""), getLeft() + proxW, y + getTop());

                if (c.isKey()) {
                    g.drawLine(c.area.x, c.area.y, c.area.x + c.area.width - 2, c.area.y);
                }
                if (c.isFkey()) {
                    g.drawLine(c.area.x, c.area.y + rt2.height, c.area.x + c.area.width - 2, c.area.y + rt2.height);
                }
                proxW += w;
                i++;
            }
            rt = fm.getStringBounds("}", g).getBounds();
            if (!(proxW + rt.width < getWidth())) {
                y += rt.height + sp;
                altura += rt.height + sp;
                proxW = 20;
            }
            g.drawString("}", getLeft() + proxW, y + getTop());
            y += rt.height;
            altura += rt.height;
        }

        if (isMostrarConstraints() && !getConstraints().isEmpty()) {
            g.setPaint(getCorBorda());
            g.setComposite(orig);
            y += 2;
            g.drawLine(getLeft() + 1, getTop() + y, getLeftWidth(), getTop() + y);
            y += 2;
            altura += 4;
            g.setComposite(ori);
            if (isPlainIR()) {
                int lar = 1;
                for (Constraint c : getConstraints()) {
                    c.Paint(lar, y, g);
                    lar += cmpAltura + 2;
                }

                int lag = getWidth() - lar - 1;
                if (lag > 1) {

                    Rectangle rx = new Rectangle(getLeft() + lar, getTop() + y, lag, cmpAltura);

                    float alfa = 1f - getAlfa();// 0.2f;
                    Composite originalComposite = g.getComposite();
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));
                    Paint bkpp = g.getPaint();
                    g.setColor(getMaster().getBackground());
                    g.fill(rx);
                    g.setComposite(originalComposite);
                    g.setColor(getForeColor());
                    g.setPaint(bkpp);
                }

                y += cmpAltura;
                altura += cmpAltura;
            } else {
                for (Constraint c : getConstraints()) {
                    c.Paint(1, y, cmpAltura, g);
                    y += cmpAltura;
                    altura += cmpAltura;
                }
            }
        }
        if (bkpAltura != altura) {
            needRecalc = true;
        }

        if (isShowDDL()) {
            g.setPaint(getCorBorda());
            g.setComposite(orig);
            y += 2;
            g.drawLine(getLeft() + 1, getTop() + y, getLeftWidth(), getTop() + y);
            g.setComposite(ori);
            g.setPaint(this.getForeColor());
            int bkpADDL = alturaDDL;
            Rectangle rt = fm.getStringBounds("C", g).getBounds();
            r = new Rectangle(getLeft() + 2, getTop() + y + rt.height / 2, getWidth() - 4, getDDL_Desenhador().getMaxHeigth() + rt.height);
            ArrayList<String> ls = new ArrayList<>();
            DDLGenerate(ls, DDL_PEGAR_TUDO);
            String tmp = ls.stream().map(s -> "\n" + s).reduce("", String::concat);
            getDDL_Desenhador().setFont(g.getFont());
            getDDL_Desenhador().PinteTexto(g, getForeColor(), r, tmp);

            if (r.height != (getDDL_Desenhador().getMaxHeigth() + rt.height)) {
                r = new Rectangle(getLeft() + 2, getTop() + y + rt.height / 2, getWidth() - 4, getDDL_Desenhador().getMaxHeigth() + rt.height);
                getDDL_Desenhador().PinteTexto(g, getForeColor(), r, tmp);
            }
            alturaDDL = getDDL_Desenhador().getMaxHeigth() + piso;
            if (bkpADDL != alturaDDL) {
                needRecalc = true;
            }
        } else {
            if (alturaDDL != 0) {
                needRecalc = true;
            }
            alturaDDL = 0;
        }

        g.setFont(bkpf);
        if (needRecalc) {
            needRecalc = false;
            ReCalculaAltura();
        }
        g.setComposite(ori);
        g.setPaint(bkp_paint);
        g.setClip(rgn);
    }

    public void drawRoqued(Rectangle area, Graphics2D g) {
        ImageIcon img = Editor.fromControler().ImagemDeDiagrama.get("diagrama.Constraint_see.img");
        int imgl = 16;
        if (getWidth() > imgl) {
            if (img != null) {
                int x = (area.height - imgl) / 2;
                g.drawImage(img.getImage(), area.x + area.width - imgl - 2, area.y + x, imgl, imgl, null);

                Stroke bkps = g.getStroke();
                g.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1, 2}, 0));
                g.drawRoundRect(area.x + 1, area.y + 1, area.width - 2, area.height - 2, 4, 4);
                g.setStroke(bkps);
            }
        }
    }

    private transient DesenhadorDeTexto ddl_desenhador = null;

    private DesenhadorDeTexto getDDL_Desenhador() {
        if (ddl_desenhador != null) {
            return ddl_desenhador;
        }
        ddl_desenhador = new DesenhadorDeTexto();
        return ddl_desenhador;
    }

    public final int DDL_PEGAR_TABELAS = 0;
    public final int DDL_PEGAR_INTEGRIDADE = 1;
    public final int DDL_PEGAR_TUDO = 2;
    public final int DDL_PEGAR_INTEGRIDADE_PK_UN_NOMEADAS = 3;
    public final int DDL_PEGAR_INTEGRIDADE_FK = 4;

    public void DDLGenerate(List<String> texto, int ddl_pegar) {
        final String createTable = "CREATE TABLE ";
        final String pktxt = "PRIMARY KEY";
        final String utxt = "UNIQUE";
        final String em_branco = "    ";
        if (ddl_pegar == DDL_PEGAR_TUDO || ddl_pegar == DDL_PEGAR_TABELAS) {
            String tmp = createTable + getPrefixo() + getTexto() + " (";
            texto.add(tmp);

            int total_campos = getCampos().size() - 1;
            Constraint pk = getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpPK).findFirst().orElse(null);
            boolean nomeada = (pk != null && pk.isNomeada());
            boolean chaveSimples = (pk != null && pk.getCamposDeOrigem().size() == 1 && !nomeada);
            boolean pk_noNome_simples = (pk != null && !nomeada && !chaveSimples);

            List<Constraint> uniao_noNome_complex = getConstraints().stream()
                    .filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpUNIQUE && !c.isNomeada() && c.getCamposDeOrigem().size() > 1)
                    .collect(Collectors.toList());
            boolean u_noNomea_complex = !uniao_noNome_complex.isEmpty();

            int contador_campos = 0;
            for (Campo c : getCampos()) {
                boolean eh_ultimo_campo = contador_campos == total_campos;
                tmp = c.getTexto() + (c.getTipo().isEmpty() ? "" : " " + c.getTipo()) + (!c.getComplemento().isEmpty() ? " " + c.getComplemento() : "");
                if (c.isKey() && chaveSimples) {
                    tmp += " " + pktxt;
                }
                if (c.isUnique()) {
                    List<Constraint> ux = getPresentAsUN(c);
                    tmp = ux.stream().filter((u) -> (!u.isNomeada() && u.getCamposDeOrigem().size() == 1)).map((_item) -> " " + utxt).reduce(tmp, String::concat);
                }
                if (eh_ultimo_campo) {
                    if (pk_noNome_simples || u_noNomea_complex) {
                        tmp += ",";
                    }
                } else {
                    tmp += ",";
                }
                texto.add(em_branco + tmp);
                contador_campos++;
            }
            if (pk_noNome_simples) {
                texto.add(em_branco + pk.getDDL() + (u_noNomea_complex ? "," : ""));
            }
            if (u_noNomea_complex) {
                int total_uniao = uniao_noNome_complex.size();
                int contador_uniao = 0;
                for (Constraint c : uniao_noNome_complex) {
                    contador_uniao++;
                    texto.add(em_branco + c.getDDL() + (contador_uniao != total_uniao ? "," : ""));
                }
            }
            if (texto.size() == 1) {
                texto.add(" ");
            }
            texto.add(")" + getSepadorSql());
        }

        if (ddl_pegar == DDL_PEGAR_TUDO || ddl_pegar == DDL_PEGAR_INTEGRIDADE || ddl_pegar == DDL_PEGAR_INTEGRIDADE_PK_UN_NOMEADAS) {
            //# PK e UNIQUE
            getConstraints().stream().filter(constr -> !(constr.getTipo() == Constraint.CONSTRAINT_TIPO.tpFK) && constr.isNomeada()).forEach(constr -> {
                texto.add(" ");
                AddSubsDDL(texto, constr.getDDL(), em_branco);
            });
        }
        if (ddl_pegar == DDL_PEGAR_TUDO || ddl_pegar == DDL_PEGAR_INTEGRIDADE || ddl_pegar == DDL_PEGAR_INTEGRIDADE_FK) {
            // # Só FK
            getConstraints().stream().filter(constr -> (constr.getTipo() == Constraint.CONSTRAINT_TIPO.tpFK)).forEach(constr -> {
                texto.add(" ");
                AddSubsDDL(texto, constr.getDDL(), em_branco);
            });
        }
    }

    private void AddSubsDDL(List<String> texto, String valor, String espaco) {
        String arr[] = valor.split("\n");
        String ax = "";
        for (String a : arr) {
            texto.add(ax + a);
            ax = espaco;
        }
    }

    private int alturaDDL = 0;
    private int altura = 0;

    @Override
    public void ReciveNotificacao(ElementarEvento evt) {
        super.ReciveNotificacao(evt);
        int i = evt.getCod();
        if (i == Constantes.Operacao.opDestroy) {
            if (evt.getSender() instanceof Tabela) {
                Tabela origem = (Tabela) evt.getSender();
                getConstraints().stream().filter(c -> c.getTabelaDeOrigem() == origem).forEach(c -> {
                    c.setConstraintOrigem(null);
                });
            }
        } else {
            if (i == Constantes.Operacao.opRefresh) {
                InvalidateArea();
            }
        }
    }

    public void RemoveCampo(int idx) {
        if (idx > -1 && idx < getCampos().size()) {
            RemoveCampo(getCampos().get(idx));
        }
    }

    public void RemoveCampo(Campo campo) {
        if (campo == getCampoSelecionado()) {
            setCampoSelecionado(null);
        }
        int i = 0;
        while (i < getConstraints().size()) {
            Constraint onstr = getConstraints().get(i);
            boolean deve = (onstr.getCamposDeOrigem().indexOf(campo) > -1) || (onstr.getCamposDeDestino().indexOf(campo) > -1);
            if (deve) {
                onstr.RemoveFromOrigem(campo);
                if (!AnaliseAndRemove(onstr)) {
                    i++;
                }
            } else {
                i++;
            }
        }
        Campos.remove(campo);
        NotifiqueIR(null, Tabela.MSG_CMP_DELETE, campo);
        //SendNotificacao(campo, Constantes.Operacao.opSubDestroy);
        if (isAutosize()) {
            needRecalc = true;
            InvalidateArea();
        }
        DoMuda();
    }

    public boolean AnaliseAndRemove(Constraint constr) {
        if (constr.getCamposDeOrigem().isEmpty() && constr.getCamposDeDestino().isEmpty()) {
            InternalRemoveConstraint(constr);
            return true;
        }
        return false;
    }

    public void RemoveConstraint(Constraint constr) {
        InternalRemoveConstraint(constr);
        DoMuda();
    }

    protected void InternalRemoveConstraint(Constraint constr) {
        if (constr == getConstraintSelecionado()) {
            setConstraintSelecionado(null);
        }
        switch (constr.getTipo()) {
            case tpPK:
                constr.getCamposDeOrigem().stream().forEach(cmp -> cmp.SetKey(false));
                break;
            case tpUNIQUE:
                constr.getCamposDeOrigem().forEach(cmp -> {
                    cmp.SetUnique(getPresentAsUN(cmp).size() > 1);
                });
                break;
            case tpFK:
                if (constr.getLigacao() != null) {
                    constr.getLigacao().PerformRoqued(false);
                }
                constr.getCamposDeDestino().stream().filter(cmp -> cmp != null).forEach(cmp -> cmp.SetFkey(false));
                break;
        }
        NotifiqueIR(constr, MSG_IR_PREDELETE, null);
        constr.Clear();
        Constraints.remove(constr);
        SendNotificacao(constr, Constantes.Operacao.opSubDestroy);
        if (isAutosize()) {
            needRecalc = true;
            InvalidateArea();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (isSelecionado()) {
            SelecioneCampoConstraint(e);
        }
    }

    private void SelecioneCampoConstraint(MouseEvent e) {
        for (Campo c : getCampos()) {
            if (c.isMe(e.getPoint())) {
                setCampoSelecionado(c);
                return;
            }
        }
        setCampoSelecionado(null);
        for (Constraint c : getConstraints()) {
            if (c.isMe(e.getPoint())) {
                setConstraintSelecionado(c);
                return;
            }
        }
        setConstraintSelecionado(null);
    }

    public Campo getCampoFromPoint(Point p) {
        for (Campo c : getCampos()) {
            if (c.isMe(p)) {
                return c;
            }
        }
        return null;
    }

    public Constraint getConstraintFromPoint(Point p) {
        for (Constraint c : getConstraints()) {
            if (c.isMe(p)) {
                return c;
            }
        }
        return null;
    }

    public String NomeieCampo(String nome) {
        if (nome.equals("_")) {
            return "_";
        }
        ArrayList<Campo> campos = getCampos();
        ArrayList<String> nomes = new ArrayList<>();
        campos.stream().forEach((c) -> {
            nomes.add(c.getTexto());
        });
        int i = 0;
        String tmp = nome; // + "_1";
        while (nomes.indexOf(tmp) > -1) {
            tmp = nome + "_" + String.valueOf(++i);
        }
        return tmp;
    }

    final String imgk = "diagrama.Campo_Key.img";
    final String imgfk = "diagrama.Campo_Fkey.img";
    final String imgkfk = "diagrama.Campo_KeyFkey.img";
    final String imgunfk = "diagrama.Constraint_UNFK.img";
    final String imgun = "diagrama.Constraint_UN.img";

    public void Add(Campo aThis) {
        getCampos().add(aThis);
        if (isAutosize()) {
            needRecalc = true;
        }
    }

    public void Add(Constraint aThis) {
        getConstraints().add(aThis);
        if (isAutosize()) {
            needRecalc = true;
        }
    }

    private final int piso = (6 * distSelecao);

    private void ReCalculaAltura() {
        if (!isAutosize()) {
            return;
        }
        int topo = margemTitulo + 1;
        int tam = altura + topo + piso + alturaDDL;
        if (tam != getHeight() && tam > 50) {
            boolean sn = autosize;
            autosize = false;
            ReciveFormaResize(new Rectangle(0, 0, 0, getHeight() - tam));
            autosize = sn;
            DoRaizeReenquadreReposicione();

            calculePontos();
            SendNotificacao(Constantes.Operacao.opResize);
            if (isSelecionado()) {
                Reposicione();
            }
            repaint();
        }
    }

    private boolean autosize = true;

    public boolean isAutosize() {
        return autosize;
    }

    public void setAutosize(boolean autosize) {
        if (this.autosize == autosize) {
            return;
        }
        this.autosize = autosize;
        if (isAutosize()) {
            needRecalc = true;
        }
        InvalidateArea();
    }

    @Override
    public void ReciveFormaResize(Rectangle ret) {
        if (!isAutosize()) {
            super.ReciveFormaResize(ret);
        } else {
            Rectangle rec = new Rectangle(ret.x, ret.y, ret.width, 0);
            super.ReciveFormaResize(rec);
        }
    }

    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);

        if (Tag == Constraint.TAG_COMMAND_FK + 10) {
            Constraint co = getPresentAsFK(campoSelecionado);
            setConstraintSelecionado(co);
            ((DiagramaLogico) getMaster()).LancarEditorDeIR(Tag - 10);
            return;
        }

        if (Tag == Constraint.TAG_COMMAND_PK || Tag == Constraint.TAG_COMMAND_FK || Tag == Constraint.TAG_COMMAND_UN) {
            ((DiagramaLogico) getMaster()).LancarEditorDeIR(Tag);
            return;
        }

        if (Tag == EDITOR_CAMPOS) {
            ((DiagramaLogico) getMaster()).LancarEditorDeCampos();
            return;
        }
        if (Tag == EDITOR_CAMPOS_TP) {
            ((DiagramaLogico) getMaster()).LancarEditorDeCamposTP();
            return;
        }
        if (Tag == PRINT_DDL) {
            PrintDDL();
            return;
        }

        if ((Tag == DESCE_CONSTAN || Tag == SOBE_CONSTAN) && getConstraintSelecionado() != null) {
            int idxc = getConstraints().indexOf(getConstraintSelecionado());
            if (Tag == SOBE_CONSTAN) {
                idxc--;
            } else {
                idxc++;
            }
            getConstraints().remove(getConstraintSelecionado());
            getConstraints().add(idxc, constraintSelecionado);
            DoMuda();
            InvalidateArea();
            return;
        }

        if (Tag > 10 && Tag < 2000) {
            if (Tag > 10) {
                int i = Tag - 11;
                if (i < getCampos().size()) {
                    setCampoSelectedIndex(i);
                    return;
                }
            }
            if (Tag > 1000) {
                int i = Tag - 1001;
                if (i < getConstraints().size()) {
                    setConstraintSelectedIndex(i);
                    return;
                }
            }
        }

        if (Tag == SOBE_CAMPO || Tag == DESCE_CAMPO) {
            if (getCampoSelecionado() == null) {
                return;
            }
            int idx = getCampos().indexOf(getCampoSelecionado());
            if (Tag == SOBE_CAMPO) {
                idx--;
            } else {
                idx++;
            }
            getCampos().remove(getCampoSelecionado());
            getCampos().add(idx, campoSelecionado);
            DoMuda();
            InvalidateArea();
        }
    }

    @Override
    public void ExcluirSubItem(int idx) {
        if (getCampoSelecionado() != null) {
            RemoveCampo(getCampoSelecionado());
            return;
        }
        if (getConstraintSelecionado() != null) {
            RemoveConstraint(getConstraintSelecionado());
        }
    }

    @Override
    public void PoluleColors(ArrayList<Color> cores) {
        super.PoluleColors(cores);
        if (cores.indexOf(getGradienteStartColor()) == -1) {
            cores.add(getGradienteStartColor());
        }
        if (cores.indexOf(getGradienteEndColor()) == -1) {
            cores.add(getGradienteEndColor());
        }
    }

    private transient boolean needRecalc = false;

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (isAutosize()) {
            needRecalc = true;
        }
    }

    @Override
    public void PosicionePonto(PontoDeLinha ponto) {
        super.PosicionePonto(ponto);
        AfterPosicione(ponto);
    }

    /**
     * Deconta o espaço arredondado dos cantos do objeto. Usado em PosicionePonto e PropagueResizeParaLigacoes
     *
     * @param ponto
     */
    protected void AfterPosicione(PontoDeLinha ponto) {
        int mx = ponto.getLado();
        int L = ponto.getLeft();
        int T = ponto.getTop();
        int tmp;
        int rnd = roundrect / 2;
        boolean sn = false;

        switch (mx) {
            case 0:
            case 2:
                tmp = getTop() + rnd;
                if (T < tmp) {
                    T = tmp;
                    sn = true;
                } else {
                    tmp = getTopHeight() - rnd;
                    if (T > tmp) {
                        T = tmp;
                        sn = true;
                    }
                }
                break;
            case 1:
            case 3:
                tmp = getLeft() + rnd;
                if (L < tmp) {
                    L = tmp;
                    sn = true;
                } else {
                    tmp = getLeftWidth() - rnd;
                    if (L > tmp) {
                        L = tmp;
                        sn = true;
                    }
                }
                break;
        }
        if (sn) {
            ponto.setLocation(L, T);
        }
    }

    @Override
    protected void PropagueResizeParaLigacoes() {
        super.PropagueResizeParaLigacoes();
        getListaDePontosLigados().stream().forEach((p) -> {
            AfterPosicione(p);
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Ancorador">
    public final int CODE_EDT_CMP = 3;
    public final int CODE_EDT_CMP_TP = 5;
    public final int CODE_DDL = 6;
    public final int CODE_SOBE = 7;
    public final int CODE_DESCE = 8;
    public final int CODE_DEL_CMP_CONST = 9;

    @Override
    public void runAncorasCode(int cod) {
        super.runAncorasCode(cod);
        switch (cod) {
            case CODE_EDT_CMP:
                DoAnyThing(EDITOR_CAMPOS);
                break;
            case CODE_EDT_CMP_TP:
                DoAnyThing(EDITOR_CAMPOS_TP);
                break;
            case CODE_DDL:
                DoAnyThing(PRINT_DDL);
                break;
            case CODE_DEL_CMP_CONST:
                ExcluirSubItem(0);
                break;
            case CODE_DESCE:
                if (getCampoSelecionado() != null) {
                    if (!getCampoSelecionado().isLast()) {
                        DoAnyThing(DESCE_CAMPO);
                    }
                } else if (getConstraintSelecionado() != null) {
                    if (!getConstraintSelecionado().isLast()) {
                        DoAnyThing(DESCE_CONSTAN);
                    }
                }
                break;
            case CODE_SOBE:
                if (getCampoSelecionado() != null) {
                    if (!getCampoSelecionado().isFirst()) {
                        DoAnyThing(SOBE_CAMPO);
                    }
                } else if (getConstraintSelecionado() != null) {
                    if (!getConstraintSelecionado().isFirst()) {
                        DoAnyThing(SOBE_CONSTAN);
                    }
                }
                break;
        }
    }

    @Override
    public String WhatDrawOnAcorador(Integer c) {
        String res = "";
        boolean any = getCampoSelecionado() != null || getConstraintSelecionado() != null;
        switch (c) {
            case CODE_EDT_CMP:
                res = "diagrama.ancordor.3.img";
                break;
            case CODE_EDT_CMP_TP:
                res = "diagrama.ancordor.5.img";
                break;
            case CODE_DDL:
                res = "diagrama.ancordor.6.img";
                break;
            case CODE_SOBE:
                if (((getCampoSelecionado() != null) && (!getCampoSelecionado().isFirst())) || ((getConstraintSelecionado() != null) && (!getConstraintSelecionado().isFirst()))) {
                    res = "diagrama.ancordor.7.img";
                } else {
                    res = "diagrama.ancordor.7.0.img";
                }
                break;
            case CODE_DESCE:
                if (((getCampoSelecionado() != null) && (!getCampoSelecionado().isLast())) || ((getConstraintSelecionado() != null) && (!getConstraintSelecionado().isLast()))) {
                    res = "diagrama.ancordor.8.img";
                } else {
                    res = "diagrama.ancordor.8.0.img";
                }
                break;
            case CODE_DEL_CMP_CONST:
                res = any ? "diagrama.ancordor.9.img" : "diagrama.ancordor.9.0.img";
                break;
        }
        if (!"".equals(res)) {
            return res;
        }
        return super.WhatDrawOnAcorador(c);
    }
    //</editor-fold>

    public void PrintDDL() {
        ArrayList<String> ddl = new ArrayList<>();
        DDLGenerate(ddl, DDL_PEGAR_TUDO);
        String tmp = "/* brModelo: */\n";
        util.Dialogos.ShowDlgTextoReadOnly(Aplicacao.fmPrincipal.getRootPane(), ddl.stream().map(s -> "\n" + s).reduce(tmp, String::concat));
    }

    public void ProcesseIrKey(Campo cmp) {
        if (cmp == null || getMaster().isCarregando) {
            return;
        }
        direct_ProcesseIrKey(cmp);
    }

    public void direct_ProcesseIrKey(Campo cmp) {
        Constraint pk = getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpPK).findAny().orElse(null);
        if (pk == null) {
            if (cmp.isKey()) {
                pk = new Constraint(this);
                pk.setTipo(Constraint.CONSTRAINT_TIPO.tpPK);
                pk.Add(cmp, null);
            }
            return;
        }
        if (cmp.isKey() && (pk.getCamposDeOrigem().indexOf(cmp) == -1)) {
            pk.Add(cmp, null);
            NotifiqueIR(pk, MSG_IR_CHANGE_ADD_CMP, cmp);
            return;
        }
        if (!cmp.isKey() && (pk.getCamposDeOrigem().indexOf(cmp) > -1)) {
            pk.RemoveFromOrigem(cmp);
            NotifiqueIR(pk, MSG_IR_CHANGE_DEL_CMP, cmp);
            AnaliseAndRemove(pk);
        }
    }

    //public static int MSG_IR_CHANGE = 1;
    public static final int MSG_IR_CHANGE_ADD_CMP = 2;
    public static final int MSG_IR_CHANGE_DEL_CMP = 3;
    public static final int MSG_IR_PREDELETE = 4;
    public static final int MSG_CMP_DELETE = 5;
    public static final int MSG_CMP_CHANGE_TIPO = 6;

    /**
     * Notifica apenas as FK para refletir novo comportamento.
     *
     * @param cons
     * @param msg
     * @param cmp
     */
    public void NotifiqueIR(Constraint cons, int msg, Campo cmp) {
        if (getMaster().isCarregando) {
            return;
        }
        ((DiagramaLogico) getMaster()).ReciveNotifiqueIR(cons, msg, cmp);
    }

    public List<Constraint> getPresentAsUN(Campo cmp) {
        ArrayList<Constraint> lst = new ArrayList<>();
        getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpUNIQUE)
                .filter(c -> c.getCamposDeOrigem().indexOf(cmp) > -1).forEach(c -> lst.add(c));
        return lst;
    }

    public Constraint getPresentAsFK(Campo cmp) {
        return getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpFK)
                .filter(c -> c.getCamposDeDestino().indexOf(cmp) > -1).findAny().orElse(null);
    }

    public void ProcesseIrUnique(Campo cmp) {
        if (cmp == null || getMaster().isCarregando) {
            return;
        }
        Constraint pk = getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpUNIQUE).findFirst().orElse(null);
        if (pk == null) {
            if (cmp.isUnique()) {
                pk = new Constraint(this);
                pk.setTipo(Constraint.CONSTRAINT_TIPO.tpUNIQUE);
                pk.Add(cmp, null);
                InvalidateArea();
            }
            return;
        }
        if (cmp.isUnique() && (pk.getCamposDeOrigem().indexOf(cmp) == -1)) {
            pk.Add(cmp, null);
            NotifiqueIR(pk, MSG_IR_CHANGE_ADD_CMP, cmp);
            return;
        }
        if (!cmp.isUnique()) {
            List<Constraint> lst = getPresentAsUN(cmp);

            lst.stream().forEach(constr -> {
                constr.RemoveFromOrigem(cmp);
                NotifiqueIR(constr, MSG_IR_CHANGE_DEL_CMP, cmp);
                AnaliseAndRemove(constr);
            });
        }
    }

    public void ProcesseIrUnique(Campo cmp, Constraint constr, boolean isadd) {
        if (cmp == null || getMaster().isCarregando) {
            return;
        }
        Constraint pk = constr;
        if (pk == null) {
            if (isadd) {
                pk = new Constraint(this);
                pk.setTipo(Constraint.CONSTRAINT_TIPO.tpUNIQUE);
                pk.Add(cmp, null);
                InvalidateArea();
            }
            return;
        }
        if (isadd && (pk.getCamposDeOrigem().indexOf(cmp) == -1)) {
            pk.Add(cmp, null);
            NotifiqueIR(pk, MSG_IR_CHANGE_ADD_CMP, cmp);
            return;
        }
        if (!isadd && (pk.getCamposDeOrigem().indexOf(cmp) > -1)) {
            pk.RemoveFromOrigem(cmp);
            NotifiqueIR(pk, MSG_IR_CHANGE_DEL_CMP, cmp);
        }
    }

    public void ProcesseIrFK(Campo cmp) {
        if (cmp == null || getMaster().isCarregando) {
            return;
        }
        Constraint fk = getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpFK && !c.isValidado()).findFirst().orElse(null);
        if (fk == null) {
            if (cmp.isFkey()) {
                fk = new Constraint(this);
                fk.setTipo(Constraint.CONSTRAINT_TIPO.tpFK);
                fk.Add(null, cmp);
                InvalidateArea();
            }
            return;
        }
        if (cmp.isFkey() && (fk.getCamposDeOrigem().indexOf(cmp) == -1)) {
            fk.Add(null, cmp);
            //# NotifiqueIR(fk, MSG_IR_CHANGE_ADD_CMP, cmp); //# Não precisa. Fk não muda os outros!
            return;
        }
        if (!cmp.isFkey()) {
            fk.RemoveFromDestino(cmp);
            //# NotifiqueIR(fk, MSG_IR_CHANGE_DEL_CMP, cmp); //# Não precisa. Fk não muda os outros!
            AnaliseAndRemove(fk);
        }
    }

    @Override
    public void menosLigacao(PontoDeLinha aThis) {
        super.menosLigacao(aThis);
        LogicoLinha linha = (LogicoLinha) aThis.getDono();
        Desligacao(linha);

        //# outro lado.
        Forma op = linha.getOutraPonta(this);
        if (op != null && (op instanceof Tabela)) {
            ((Tabela) op).Desligacao(linha);
        }
    }

    public void Desligacao(LogicoLinha linha) {
        getConstraints().stream().filter(c -> c.getLigacao() == linha).forEach(cnsmr -> {
            cnsmr.setLigacao(null);
        });
    }

    public String getSepadorSql() {
        return ((DiagramaLogico) getMaster()).getSeparatorSQL();
    }

    //Versão 3.2!
    public String getPrefixo() {
        return ((DiagramaLogico) getMaster()).getPrefixo();
    }

    /**
     * Mostrar constraints na tabela
     */
    private boolean mostrarConstraints = true;

    public boolean isMostrarConstraints() {
        return mostrarConstraints;
    }

    public void setMostrarConstraints(boolean mostrarConstraints) {
        if (this.mostrarConstraints == mostrarConstraints) {
            return;
        }
        this.mostrarConstraints = mostrarConstraints;
        InvalidateArea();
    }

    @Override
    public void maisLigacao(PontoDeLinha aThis) {
        super.maisLigacao(aThis);
        LogicoLinha linha = (LogicoLinha) aThis.getDono();
        Forma origem = linha.getOutraPonta(this);
        if (origem != null && (origem instanceof Tabela)) {
            Tabela tab = (Tabela) origem;
            ReLiga(linha, tab);
            // # outra ponta
            tab.ReLiga(linha, this);
        }
    }

    public void ReLiga(LogicoLinha linha, Tabela origem) {
        getConstraints().stream().filter(c -> c.getLigacao() == null && c.getTabelaDeOrigem() == origem).forEach(cnsmr -> {
            cnsmr.setLigacao(linha);
        });
    }

    @Override
    public void setTexto(String Texto) {
        super.setTexto(Texto);
        RefreshPosNovoTexto();
    }

    public void RefreshPosNovoTexto() {
        SendNotificacao(Constantes.Operacao.opRefresh);
    }

    @Override
    public void mouseDblClicked(MouseEvent e) {
        super.mouseDblClicked(e);
        if (getConstraintSelecionado() != null) {
            int tg = Constraint.TAG_COMMAND_FK;
            if (getConstraintSelecionado().getTipo() == Constraint.CONSTRAINT_TIPO.tpPK) {
                tg = Constraint.TAG_COMMAND_PK;
            } else if (getConstraintSelecionado().getTipo() == Constraint.CONSTRAINT_TIPO.tpUNIQUE) {
                tg = Constraint.TAG_COMMAND_UN;
            }
            DoAnyThing(tg);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        if (constraintRoqued != null) {
            roqueFromMouse(false);
            constraintRoqued = null;
        }
    }

    private void roqueFromMouse(boolean sn) {
        if (sn == constraintRoqued.roqued) {
            return;
        }
        if (constraintRoqued.isAutoRelacionamento()) {
            constraintRoqued.roqued = sn;
            constraintRoqued.getCamposDeDestino().stream().filter(c -> c != null).forEach(c -> c.roqued = sn);
            constraintRoqued.getCamposDeOrigem().stream().filter(c -> c != null).forEach(c -> c.roqued = sn);
            InvalidateArea();
            return;
        }
        if (constraintRoqued.getLigacao() != null) {
            constraintRoqued.getLigacao().PerformRoqued(sn);
            constraintRoqued.getLigacao().SetFatorLargura(sn ? 2f : 1f);
            constraintRoqued.getLigacao().InvalidateArea();
        }
    }

    private transient Constraint constraintRoqued = null;

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        Constraint tmp = getConstraints().stream().filter(constr -> constr.getTipo() == Constraint.CONSTRAINT_TIPO.tpFK && constr.isMe(e.getPoint())).findFirst().orElse(null);
        if (tmp == constraintRoqued) {
            return;
        }
        if (constraintRoqued != null) {
            roqueFromMouse(false);
        }
        if (tmp == null) {
            constraintRoqued = null;
        } else {
            constraintRoqued = tmp;
            roqueFromMouse(true);
        }
    }

    @Override
    public boolean MostreSeParaExibicao(TreeItem root) {
        TreeItem t = new TreeItem(getTexto(), getID(), this.getClass().getSimpleName());
        getCampos().stream().forEach(c -> c.MostreSeParaExibicao(t));
        getConstraints().stream().forEach(c -> c.MostreSeParaExibicao(t));
        root.add(t);
        return true;
    }

    @Override
    public void DoSubItemSel(int index) {
        super.DoSubItemSel(index);
        if (index < getCampos().size()) {
            setCampoSelectedIndex(index);
        } else {
            index -= getCampos().size();
            setConstraintSelectedIndex(index);
        }
    }
}
