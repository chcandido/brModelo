/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.logico;

import controlador.Editor;
import controlador.apoios.TreeItem;
import controlador.inspector.InspectorProperty;
import desenho.FormaElementar;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.swing.ImageIcon;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ccandido
 */
public class Constraint implements Serializable {

    private static final long serialVersionUID = 2017041120493765643L;

    private final Tabela tabela;

    public Tabela getTabela() {
        return tabela;
    }

    public Constraint(Tabela tbl) {
        this.tabela = tbl;
        this.tabela.Add(this);
        getMotivoValidade = new String[]{
            Editor.fromConfiguracao.getValor("Inspector.obj.constraint.validacao.ok"),
            Editor.fromConfiguracao.getValor("Inspector.obj.constraint.validacao.cons_origem"),
            Editor.fromConfiguracao.getValor("Inspector.obj.constraint.validacao.qtd_cmp"),
            Editor.fromConfiguracao.getValor("Inspector.obj.constraint.validacao.tipo"),
            Editor.fromConfiguracao.getValor("Inspector.obj.constraint.validacao.rep"),
            Editor.fromConfiguracao.getValor("Inspector.obj.constraint.validacao.ligacao"),
            Editor.fromConfiguracao.getValor("Inspector.obj.constraint.validacao.ku")
        };
    }

    public Rectangle area = null;

    public void Paint(int x, int y, int altura, Graphics2D g) {
        int f = 2 * getTabela().distSelecao;
        int imgl = 16;

        Rectangle r = new Rectangle(getTabela().getLeft() + x, getTabela().getTop() + y, getTabela().getWidth() - x - 1, altura);
        area = r;
        if (r.y + altura > getTabela().getTopHeight()) {
            return;
        }

        float alfa = 1f - getTabela().getAlfa();// 0.2f;
        Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));
        Paint bkpp = g.getPaint();
        g.setColor(getTabela().getMaster().getBackground()); //# Não: isDisablePainted()? disabledColor : 
        g.fill(area);
        if (isSelecionado()) {
            if (getTabela().isGradiente()) {
                g.setColor(getTabela().getGradienteStartColor());
            } else {
                g.setColor(getTabela().getForeColor());
            }
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getTabela().getAlfa()));
            g.fillRect(r.x, r.y, r.width + 1, r.height);
        }
        g.setPaint(bkpp);
        g.setComposite(originalComposite);

        Rectangle bkp = g.getClipBounds();

        ImageIcon img;
        switch (getTipo()) {
            case tpPK:
                img = Editor.fromControler().ImagemDeDiagrama.get("diagrama.Constraint_PK.img");
                break;
            case tpFK:
                img = Editor.fromControler().ImagemDeDiagrama.get("diagrama.Constraint_FK.img");
                break;
            default:
                img = Editor.fromControler().ImagemDeDiagrama.get("diagrama.Constraint_UN.img");
        }
        if (!isValidado()) {
            g.drawRoundRect(r.x - 1 + f, r.y + 4 - 1, imgl + 1, imgl + 1, 4, 4);
        }
        if (roqued) {
            Stroke bkps = g.getStroke();
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1, 2}, 0));
            //g.drawRoundRect(r.x - 1 + f + 2, r.y + f - 1 + 2, imgl + 1 - 4, imgl + 1 - 4, 4, 4);
            g.drawRoundRect(r.x - 1 + f, r.y + 4 - 1, imgl + 1, imgl + 1, 4, 4);
            g.setStroke(bkps);
        }

        if (getTabela().isDisablePainted()) {
            img = new ImageIcon(util.TratadorDeImagens.dye(img, getTabela().getForeColor()));
        }
        g.drawImage(img.getImage(), r.x + f, r.y + 4, imgl, imgl, null);

        g.clipRect(r.x, r.y, r.width, r.height);
        g.setColor(getTabela().getForeColor());
        String tx = getNomeFormatado();
        g.drawString(tx, r.x + f + imgl + 2, r.y + altura / 2 + f);
        g.setClip(bkp);
        g.setPaint(bkpp);
    }

    public void Paint(int x, int y, Graphics2D g) {
        int f = 3;
        int imgl = 16;

        Rectangle r = new Rectangle(getTabela().getLeft() + x, getTabela().getTop() + y, getTabela().cmpAltura, getTabela().cmpAltura);
        area = r;

        float alfa = 1f - getTabela().getAlfa();// 0.2f;
        Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));
        Paint bkpp = g.getPaint();
        g.setColor(getTabela().getMaster().getBackground());
        g.fill(area);
        if (isSelecionado()) {
            if (getTabela().isGradiente()) {
                g.setColor(getTabela().getGradienteStartColor());
            } else {
                g.setColor(getTabela().getForeColor());
            }
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getTabela().getAlfa()));
            g.fillRect(r.x, r.y, r.width + 1, r.height);
        }
        g.setPaint(bkpp);
        g.setComposite(originalComposite);

        Rectangle bkp = g.getClipBounds();

        ImageIcon img;
        switch (getTipo()) {
            case tpPK:
                img = Editor.fromControler().ImagemDeDiagrama.get("diagrama.Constraint_PK.img");
                break;
            case tpFK:
                img = Editor.fromControler().ImagemDeDiagrama.get("diagrama.Constraint_FK.img");
                break;
            default:
                img = Editor.fromControler().ImagemDeDiagrama.get("diagrama.Constraint_UN.img");
        }
        if (!isValidado()) {
            g.drawRoundRect(r.x - 1 + f, r.y + f - 1, imgl + 1, imgl + 1, 4, 4);
        }
        if (roqued) {
            Stroke bkps = g.getStroke();
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1, 2}, 0));
            g.drawRoundRect(r.x - 1 + f, r.y + 4 - 1, imgl + 1, imgl + 1, 4, 4);
            g.setStroke(bkps);
        }
        if (getTabela().isDisablePainted()) {
            img = new ImageIcon(util.TratadorDeImagens.dye(img, getTabela().getForeColor()));
        }
        g.drawImage(img.getImage(), r.x + f, r.y + f, imgl, imgl, null);
        g.clipRect(r.x, r.y, r.width, r.height);
        g.setColor(getTabela().getForeColor());
        g.setClip(bkp);
        g.setPaint(bkpp);
    }

    protected transient boolean roqued = false;

    public boolean isMe(Point p) {
        if (area == null) {
            return false;
        }
        return area.contains(p);
    }

    public String getCamposStr(List<Campo> lst) {
        if (lst == null || lst.isEmpty()) {
            return "()";
        }
        String cmpsD = "";
        cmpsD = lst.stream().map((cx) -> (cx == null ? "[]" : cx.getTexto().trim().isEmpty() ? "?" : cx.getTexto()) + ", ").reduce(cmpsD, String::concat);
        return "(" + cmpsD.substring(0, cmpsD.length() - 2) + ")";
    }

    public String getCamposStrCheck(List<Campo> lst) {
        if (lst == null || lst.isEmpty()) {
            return "()";
        }
        String cmpsD = "";
        cmpsD = lst.stream().map((cx) -> (cx == null ? "[]"
                : (cx.getTexto().trim().isEmpty() ? "?"
                : cx.getTexto())
                + (getOrigem(cx) == null ? "???" : ""))
                + ", ")
                .reduce(cmpsD, String::concat);
        return "(" + cmpsD.substring(0, cmpsD.length() - 2) + ")";
    }

    private final int V_MOTIVO_OK = 0;
    private final int V_MOTIVO_CONS_ORIGEM = 1;
    private final int V_MOTIVO_QTD_CMP = 2;
    private final int V_MOTIVO_TIPO = 3;
    private final int V_MOTIVO_CAMPO_REP = 4;
    private final int V_MOTIVO_NAO_LIGADO = 5;
    private final int V_MOTIVO_KEY_AND_UNIQUE = 6;

    private String[] getMotivoValidade = null;

    /**
     * Qual motivo de não estar válido
     */
    private int motivoValidade = 0;

    public boolean isAutoRelacionamento() {
        return (getTabela() != null && getTabela() == getTabelaDeOrigem());
    }

    public void Valide() {
        motivoValidade = V_MOTIVO_OK;
        if (getTipo() != CONSTRAINT_TIPO.tpFK) {
            if (getCamposDeOrigem().size() == 1) {
                Campo cx = getCamposDeOrigem().get(0);
                if (cx.isKey() && cx.isUnique()) {
                    motivoValidade = V_MOTIVO_KEY_AND_UNIQUE;
                    setValidado(false);
                    return;
                }
            }
            setValidado(true);
            return;
        }
        if (getLigacao() == null) {
            //# auto relacionamento.
            if (getTabela() != getTabelaDeOrigem()) {
                setValidado(false);
                motivoValidade = V_MOTIVO_NAO_LIGADO;
                return;
            }
        }
        if (getConstraintOrigem() == null) {
            setValidado(false);
            motivoValidade = V_MOTIVO_CONS_ORIGEM;
            return;
        }
        if (getConstraintOrigem().getCamposDeOrigem().size() != getCamposDeOrigem().size()) {
            setValidado(false);
            motivoValidade = V_MOTIVO_QTD_CMP;
            return;
        }
        boolean sn = true;
        for (int i = 0; i < getCamposDeOrigem().size(); i++) {
            sn = getCamposDeOrigem().get(i) != null && getCamposDeOrigem().get(i).getTipo().equals(getCamposDeDestino().get(i).getTipo());
            if (!sn) {
                motivoValidade = V_MOTIVO_TIPO;
                break;
            }
        }
        if (sn) {
            HashSet<Campo> teste = new HashSet<>();
            int tl = 0;
            for (Campo c : getCamposDeOrigem()) {
                if (c != null) {
                    tl++;
                    teste.add(c);
                    if (tl != teste.size()) { // não inseriu: duplicado.
                        sn = false;
                        motivoValidade = V_MOTIVO_CAMPO_REP;
                        break;
                    }
                }
            }
        }
        setValidado(sn);
    }

    protected void MostreSeParaExibicao(TreeItem root) {
        String img;
        switch (getTipo()) {
            case tpPK:
                img = "Constraint_PK";
                break;
            case tpFK:
                img = "Constraint_FK";
                break;
            default:
                img = "Constraint_UN";
        }
        root.add(new TreeItem(((isNomeada() && !getNome().isEmpty())? getNome() : Editor.fromConfiguracao.getValor("diagrama.Constraint.nome")), getTabela().getID(), "diagrama." + img + ".img"));
    }

    public enum CONSTRAINT_TIPO {
        tpPK, tpUNIQUE, tpFK
    }

    //<editor-fold defaultstate="collapsed" desc="Propriedades">
    private boolean nomeada = false;
    private String nome = "";

    private final ArrayList<Campo> camposDeOrigem = new ArrayList<>();
    private final ArrayList<Campo> camposDeDestino = new ArrayList<>();

    private CONSTRAINT_TIPO tipo = CONSTRAINT_TIPO.tpPK;
    //private HashMap<Campo, Campo> listaDeCamposKV = new HashMap<>();
    private Constraint constraintOrigem = null;

    public Constraint getConstraintOrigem() {
        return constraintOrigem;
    }

    public void LigacaoDireta(Constraint constraintOrigem, LogicoLinha ligacao) {
        if ((constraintOrigem == null) || (constraintOrigem.getTipo() == CONSTRAINT_TIPO.tpFK) || (this.constraintOrigem != null) || (getTipo() != CONSTRAINT_TIPO.tpFK)) {
            setConstraintOrigem(constraintOrigem);
        } else {
            this.constraintOrigem = constraintOrigem;
            Tabela ori = this.constraintOrigem.getTabela();
            getTabela().PerformLigacao(ori, true);
            int tl = camposDeOrigem.size() - 1;
            while (tl > -1) {
                if (camposDeOrigem.get(tl) != null && camposDeOrigem.get(tl).getTabela() != ori) {
                    camposDeOrigem.remove(tl);
                    camposDeOrigem.add(tl, null);
                }
                tl--;
            }
            this.ligacao = ligacao;
        }
    }

    public void setConstraintOrigem(Constraint constraintOrigem) {
        if (this.constraintOrigem != constraintOrigem) {
            if (this.constraintOrigem != null) {
                Tabela ori = this.constraintOrigem.getTabela();
                getTabela().PerformLigacao(ori, false);
            }
            if (getTipo() == CONSTRAINT_TIPO.tpFK) {
                if (constraintOrigem != null) {
                    if (constraintOrigem.getTipo() == CONSTRAINT_TIPO.tpFK) {
                        constraintOrigem = null;
                    }
                }
                this.constraintOrigem = constraintOrigem;
                if (this.constraintOrigem != null) {
                    Tabela ori = this.constraintOrigem.getTabela();
                    getTabela().PerformLigacao(ori, true);
                }

                //# Remove os campos da antiga origem.
                int tl = camposDeOrigem.size();
                camposDeOrigem.clear();
                for (int i = 0; i < tl; i++) {
                    camposDeOrigem.add(null);
                }
                if (!getTabela().getMaster().isCarregando && !novalide) {
                    Valide();
                }
            }
        }
    }

    public CONSTRAINT_TIPO getTipo() {
        return tipo;
    }

    public void setTipo(CONSTRAINT_TIPO tipo) {
        if (this.tipo.equals(tipo)) {
            return;
        }
        this.tipo = tipo;
        if (tipo == CONSTRAINT_TIPO.tpFK) {
            setValidado(false);
            motivoValidade = V_MOTIVO_CONS_ORIGEM;
        }
        //InvalidateArea();
    }

    public void SetTipo(int tpForInspector) {
        try {
            setTipo(CONSTRAINT_TIPO.values()[tpForInspector]);
        } catch (Exception e) {
        }
    }

    public boolean isNomeada() {
        return nomeada;
    }

    public void setNomeada(boolean nomeada) {
        if (this.nomeada == nomeada) {
            return;
        }
        this.nomeada = nomeada;
        Repaint();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (this.nome.equals(nome)) {
            return;
        }
        this.nome = nome;
        Repaint();
    }

    public String getNomeFormatado() {
        return (isNomeada() && !getNome().isEmpty()) ? getNome() : getTipoStr();
    }

//    public HashMap<Campo, Campo> getListaDeCamposKV() {
//        return listaDeCamposKV;
//    }
//
//    public void setListaDeCamposKV(HashMap<Campo, Campo> listaDeCamposKV) {
//        this.listaDeCamposKV = listaDeCamposKV;
//    }
    public void Add(Campo origem, Campo destino, LogicoLinha lig, Constraint orig) {
        novalide = true;
        setConstraintOrigem(orig);
        novalide = false;
        Add(origem, destino, lig);
    }

    public void Add(Campo origem, Campo destino, LogicoLinha lig) {
        novalide = true;
        setLigacao(lig);
        novalide = false;
        Add(origem, destino);
    }

    public void Add(Campo origem, Campo destino) {
        if (getTipo() != CONSTRAINT_TIPO.tpFK) {
            int idx = camposDeOrigem.indexOf(origem);
            if (idx == -1) {
                camposDeOrigem.add(origem);
                camposDeDestino.add(destino);
            }
// # não usa destino!
//            else {
//                camposDeDestino.remove(idx);
//                camposDeDestino.add(idx, destino);
//            }
        } else {
            int idx = camposDeDestino.indexOf(destino);
            if (idx == -1) {
                camposDeOrigem.add(origem);
                camposDeDestino.add(destino);
            } else {
                camposDeOrigem.remove(idx);
                camposDeOrigem.add(idx, origem);
            }
            if (!getTabela().getMaster().isCarregando && origem != null && destino != null) {
                destino.setTipo(origem.getTipo());
            }
        }
        if (!getTabela().getMaster().isCarregando && !novalide) {
            Valide();
        }
    }

    public Campo getOrigem(Campo destino) {
        int idx = camposDeDestino.indexOf(destino);
        if (idx > -1) {
            return camposDeOrigem.get(idx);
        }
        return null;
    }

    public Campo getDestino(Campo origem) {
        int idx = camposDeOrigem.indexOf(origem);
        if (idx > -1) {
            return camposDeDestino.get(idx);
        }
        return null;
    }

    public List<Campo> getCamposDeOrigem() {
        return camposDeOrigem;
    }

    public List<Campo> getCamposDeDestino() {
        return camposDeDestino;
    }

    public void RemoveFromDestino(Campo cmp) {
        int idx = camposDeDestino.indexOf(cmp);
        if (idx > -1) {
            camposDeDestino.remove(idx);
            camposDeOrigem.remove(idx);
            Valide();
        }
    }

    public void RemoveFromOrigem(Campo cmp) {
        int idx = camposDeOrigem.indexOf(cmp);
        if (idx > -1) {
            camposDeDestino.remove(idx);
            camposDeOrigem.remove(idx);
            Valide();
        }
    }

    public void Clear() {
        camposDeDestino.clear();
        camposDeOrigem.clear();
    }

    public String getDicionario() {
        return dicionario;
    }

    public void setDicionario(String dicionario) {
        this.dicionario = dicionario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    private String observacao = "";
    private String dicionario = "";
    private boolean selecionado = false;
    private boolean validado = true;
    private transient boolean novalide = false;
    private LogicoLinha ligacao = null;

    public LogicoLinha getLigacao() {
        return ligacao;
    }

    public void setLigacao(LogicoLinha ligacao) {
        if (this.ligacao != ligacao) {
            this.ligacao = ligacao;
            if (!getTabela().getMaster().isCarregando && !novalide) {
                Valide();
            }
        }
    }

    public boolean isValidado() {
        return validado;
    }

    public void setValidado(boolean validado) {
        if (this.validado != validado) {
            this.validado = validado;
            InvalidateArea();
        }
    }

    public boolean isSelecionado() {
        return selecionado && getTabela().isSelecionado();
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    protected Tabela getTabelaDeOrigem() {
        if (tipo == CONSTRAINT_TIPO.tpPK || tipo == CONSTRAINT_TIPO.tpUNIQUE) {
            return getTabela();
        }
        return (getConstraintOrigem() == null) ? null : getConstraintOrigem().getTabela();
    }

    protected Tabela getTabelaDeDestino() {
        if (tipo == CONSTRAINT_TIPO.tpPK || tipo == CONSTRAINT_TIPO.tpUNIQUE) {
            return null;
        }
        return getTabela();
    }

    //</editor-fold>
    /**
     * verifica se a tabela está selecionada para dar o efeito de seleção
     *
     * @return
     */
    public boolean SuperSelecionado() {
        return (tabela.isSelecionado() && isSelecionado());
    }

    public void ToXmlValores(Document doc, Element root) {
        Element me = doc.createElement("Constraint");
        me.appendChild(util.XMLGenerate.ValorString(doc, "Nome", getNome()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Nomeada", isNomeada()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Tipo", getTipo().ordinal()));

        me.appendChild(util.XMLGenerate.ValorText(doc, "Dicionario", getDicionario()));
        me.appendChild(util.XMLGenerate.ValorText(doc, "Observacao", getObservacao()));
        me.appendChild(util.XMLGenerate.ValorText(doc, "DdlOnUpdate", getDdlOnUpdate()));
        me.appendChild(util.XMLGenerate.ValorText(doc, "DdlOnDelete", getDdlOnDelete()));

        me.appendChild(util.XMLGenerate.ValorRefFormElementar(doc, "LigacaoParaOrigem", getLigacao()));

        Element lig = util.XMLGenerate.ValorText(doc, "ConstraintOrigem", getConstraintOrigem() == null ? "" : getConstraintOrigem().getNomeFormatado());
        lig.setAttribute("ID", GeraCodToLocalise(getConstraintOrigem()));
        ArrayList<Integer> cmps = new ArrayList<>();

        camposDeOrigem.stream().forEach(c -> cmps.add(c == null ? -1 : c.getIndexOnTable()));
        lig.setAttribute("CamposOrigem", Arrays.toString(cmps.toArray()));
        cmps.clear();
        camposDeDestino.stream().forEach(c -> cmps.add(c == null ? -1 : c.getIndexOnTable()));
        lig.setAttribute("CamposDestino", Arrays.toString(cmps.toArray()));
        me.appendChild(lig);

        root.appendChild(me);
    }

    public void LoadFromXML(Element me, boolean colando) {
        setObservacao(util.XMLGenerate.getValorTextoFrom(me, "Observacao"));
        setDicionario(util.XMLGenerate.getValorTextoFrom(me, "Dicionario"));
        setNome(util.XMLGenerate.getValorStringFrom(me, "Nome"));
        setDdlOnUpdate(util.XMLGenerate.getValorTextoFrom(me, "DdlOnUpdate"));
        setDdlOnDelete(util.XMLGenerate.getValorTextoFrom(me, "DdlOnDelete"));
        SetTipo(util.XMLGenerate.getValorIntegerFrom(me, "Tipo"));
        setNomeada(util.XMLGenerate.getValorBooleanFrom(me, "Nomeada"));
    }

    public boolean CommitXML(Element me, HashMap<Element, FormaElementar> mapa) {
        Element lig = util.XMLGenerate.FindByNodeName(me, "ConstraintOrigem");
        String idStr = lig.getAttribute("ID");
        Constraint oric = LocaliseFomCod(idStr, mapa);
        if (oric != null) {
            setConstraintOrigem(oric);
        }
        String cmpStr = lig.getAttribute("CamposOrigem");
        final Tabela ori = getTabelaDeOrigem();
        String[] origens = cmpStr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
        if (getTipo() != CONSTRAINT_TIPO.tpFK) {
            for (String origen : origens) {
                int v = util.Utilidades.TryIntStr(origen, -1);
                if (v != -1) {
                    Add(ori.getCampos().get(v), null);
                }
            }
        } else {
            cmpStr = lig.getAttribute("CamposDestino");
            String[] destinos = cmpStr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
            final Tabela dest = getTabelaDeDestino();
            if (dest != null) {
                for (int i = 0; i < origens.length; i++) {
                    int vo = util.Utilidades.TryIntStr(origens[i], -1);
                    int vd = util.Utilidades.TryIntStr(destinos[i], -1);
                    if (vd != -1) {
                        Add(vo == -1 || ori == null ? null : ori.getCampos().get(vo), dest.getCampos().get(vd));
                    }
                }
            }
        }

        lig = util.XMLGenerate.FindByNodeName(me, "LigacaoParaOrigem");
        idStr = lig.getAttribute("ID");
        if (!"-1".equals(idStr)) {
            FormaElementar liga = util.XMLGenerate.FindWhoHasID(idStr, mapa);
            if (liga instanceof LogicoLinha) {
                setLigacao((LogicoLinha) liga);
            }
        }
        Valide();
        return true;
    }

    public void Repaint() {
        if (getTabela() == null || area == null) {
            return;
        }
        getTabela().InvalidateArea();
    }

    public void InvalidateArea() {
        if (getTabela() == null || area == null) {
            return;
        }
        getTabela().InvalidateArea(area);
    }

    public boolean isFirst() {
        return (getTabela().getConstraints().indexOf(this) == 0);
    }

    public boolean isLast() {
        int tmp = getTabela().getConstraints().indexOf(this) + 1;
        return (tmp == getTabela().getConstraints().size());
    }

//    /**
//     * Está validamente ligado/relacionado à uma tabela.
//     *
//     * @return
//     */
//    public boolean isLinkedToTable() {
//        return getTabelaOrigem() != null && getTabela().getListaDeTabelasLigadas().indexOf(getTabelaOrigem()) > -1;
//    }
    public static final int TAG_COMMAND_PK = 120420170;
    public static final int TAG_COMMAND_FK = 120420171;
    public static final int TAG_COMMAND_UN = 120420172;
    private final int DESCE_CONSTAN = +110417;
    private final int SOBE_CONSTAN = -110417;

    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> res) {

        res.add(InspectorProperty.PropertyFactorySeparador("constraint.selecionado"));
        String relaName = "Constraint";

        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("constraint.tipo", getTipoStr()));
        res.add(InspectorProperty.PropertyFactorySN("constraint.nomeada", relaName + ".setNomeada", isNomeada()).AddCondicaoForTrue(new String[]{relaName + ".setNome"}));
        res.add(InspectorProperty.PropertyFactoryTexto("constraint.nome", relaName + ".setNome", getNome()));

        res.add(InspectorProperty.PropertyFactoryTextoL("dicionario", relaName + ".setDicionario", getDicionario()));
        res.add(InspectorProperty.PropertyFactoryTextoL("observacao", relaName + ".setObservacao", getObservacao()));

        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("constraint.validacao", getMotivoValidade[motivoValidade]));

        if (tipo == CONSTRAINT_TIPO.tpFK) {
            String txt = getTabelaDeOrigem() == null ? "[]" : getTabelaDeOrigem().getTexto();
            res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), "constraint.tabelaorigem", txt).setTag(Constraint.TAG_COMMAND_FK));

            txt = getConstraintOrigem() == null ? "[]" : getConstraintOrigem().getNomeFormatado();
            res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), "constraint.ir.origem", txt).setTag(Constraint.TAG_COMMAND_FK));

            res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), "constraint.campos.ir", getCamposStr(getCamposDeOrigem())).setTag(Constraint.TAG_COMMAND_FK));
            res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), "constraint.tabeladestino", getTabelaDeDestino().getTexto()).setTag(Constraint.TAG_COMMAND_FK));
            res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), "constraint.campos.ir", getCamposStr(getCamposDeDestino())).setTag(Constraint.TAG_COMMAND_FK));

            res.add(InspectorProperty.PropertyFactoryTexto("constraint.ddlonupdate", relaName + ".setDdlOnUpdate", getDdlOnUpdate()));
            res.add(InspectorProperty.PropertyFactoryTexto("constraint.ddlondelete", relaName + ".setDdlOnDelete", getDdlOnDelete()));

        } else {
            res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("constraint.campos.ir", getCamposStr(getCamposDeOrigem())));
        }
        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), "tabela.edtitores")
                .setTag(tipo == CONSTRAINT_TIPO.tpPK ? TAG_COMMAND_PK
                        : tipo == CONSTRAINT_TIPO.tpUNIQUE ? TAG_COMMAND_UN
                                : TAG_COMMAND_FK));

        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdExcluirSubItem.name()));

        if (getTabela().getConstraints().size() > 1) {
            res.add(InspectorProperty.PropertyFactorySeparador("tabela.constraint.posicao", false));
            if (!isFirst()) {
                res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), "tabela.constraint.sobe").setTag(SOBE_CONSTAN));
            }
            if (!isLast()) {
                res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), "tabela.constraint.desce").setTag(DESCE_CONSTAN));
            }
        }
        return res;
    }

    public String getTipoStr() {
        String res;
        switch (getTipo()) {
            case tpFK:
                res = Editor.fromConfiguracao.getValor("Inspector.obj.constraint.fkey");
                break;
            case tpPK:
                res = Editor.fromConfiguracao.getValor("Inspector.obj.constraint.key");
                break;
            default:
                res = Editor.fromConfiguracao.getValor("Inspector.obj.constraint.unique");
                break;
        }
        return res;
    }

    public String getDDL() {
        String txt = "";
        String sepa = getTabela().getSepadorSql();
        switch (getTipo()) {
            case tpPK:
                if (isNomeada() && !getNome().trim().isEmpty()) {
                    txt = "ALTER TABLE " + getTabela().getTexto() + " ADD CONSTRAINT " + getPrefixo() + getNome().trim() + " PRIMARY KEY " + getCamposStr(getCamposDeOrigem());
                    txt += sepa;
                } else {
                    txt = "PRIMARY KEY " + getCamposStr(getCamposDeOrigem());
                }
                break;
            case tpUNIQUE:
                if (isNomeada() && !getNome().trim().isEmpty()) {
                    txt = "ALTER TABLE "  + getPrefixo() + getTabela().getTexto() + " ADD CONSTRAINT " + getNome().trim() + " UNIQUE " + getCamposStr(getCamposDeOrigem());
                    txt += sepa;
                } else {
                    txt = "UNIQUE " + getCamposStr(getCamposDeOrigem());
                }
                break;
            case tpFK:
                String nome = (isNomeada() && !getNome().trim().isEmpty()) ?  getPrefixo() + getNome() : getPrefixo() + Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix")
                        + getTabela().getTexto() + "_" + String.valueOf(getTabela().getConstraints().indexOf(this) + 1);

                String tmpCD = getCamposStr(getCamposDeOrigem()).replaceAll("\\[\\]", "???");
                String tmpCO = getCamposStrCheck(getCamposDeDestino());
                if (getConstraintOrigem() != null) {
                    if (getConstraintOrigem().getCamposDeOrigem().size() > getCamposDeOrigem().size()) {
                        tmpCO = tmpCO.substring(0, tmpCO.length() - 1) + (getCamposDeOrigem().size() > 0 ? ", " : "") + "???)";
                    }
                    if (getConstraintOrigem().getCamposDeOrigem().size() > getCamposDeDestino().size()) {
                        tmpCD = tmpCD.substring(0, tmpCD.length() - 1) + (getCamposDeDestino().size() > 0 ? ", " : "") + "???)";
                    }
                }
                txt = "ALTER TABLE " + getPrefixo() + getTabela().getTexto() + " ADD CONSTRAINT " + nome + "\nFOREIGN KEY " + tmpCO + "\n";
                txt += "REFERENCES " + (getConstraintOrigem() == null ? "??? (???)" : getPrefixo() + getConstraintOrigem().getTabela().getTexto() + " " + tmpCD);
                if (!getDdlOnDelete().isEmpty() && !getDdlOnUpdate().isEmpty()) {
                    txt += "\nON DELETE " + getDdlOnDelete() + " ON UPDATE " + getDdlOnUpdate();
                } else if (!getDdlOnDelete().isEmpty() || !getDdlOnUpdate().isEmpty()) {
                    txt += "\n";
                    txt += !getDdlOnDelete().isEmpty() ? "ON DELETE " + getDdlOnDelete() : "";
                    txt += !getDdlOnUpdate().isEmpty() ? "ON UPDATE " + getDdlOnUpdate() : "";
                }
                txt += sepa;
                break;
        }
        return txt;
    }
    
    //Versão 3.2!
    public String getPrefixo() {
        return getTabela().getPrefixo();
    }

    public void NotifiqueIR(Constraint cons, int msg, Campo cmp) {
    }

    private String ddlOnUpdate = "";
    private String ddlOnDelete = "";

    public String getDdlOnDelete() {
        return ddlOnDelete;
    }

    public void setDdlOnDelete(String ddlOnDelete) {
        if (this.ddlOnDelete == null ? ddlOnDelete != null : !this.ddlOnDelete.equals(ddlOnDelete)) {
            this.ddlOnDelete = ddlOnDelete;
            Repaint();
        }
    }

    public String getDdlOnUpdate() {
        return ddlOnUpdate;
    }

    public void setDdlOnUpdate(String ddlOnUpdate) {
        if (this.ddlOnUpdate == null ? ddlOnUpdate != null : !this.ddlOnUpdate.equals(ddlOnUpdate)) {
            this.ddlOnUpdate = ddlOnUpdate;
            Repaint();
        }
    }

    public Constraint LocaliseFomCod(String cod, HashMap<Element, FormaElementar> mapa) {
        try {
            String cods[] = cod.split(",");
            if (cods[0].trim().equals("-1")) {
                return null;
            }
            FormaElementar e = util.XMLGenerate.FindWhoHasID(cods[0], mapa);
            if (e == null) {
                return null;
            }
            return ((Tabela) e).getConstraints().get(Integer.valueOf(cods[1].trim()));
        } catch (Exception e) {
            util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD", e.getMessage());
            return null;
        }
    }

    public String GeraCodToLocalise(Constraint ori) {
        if (ori == null) {
            return "-1,-1";
        }
        return String.valueOf(ori.getTabela().getID()) + "," + String.valueOf(ori.getTabela().getConstraints().indexOf(ori));
    }
}
