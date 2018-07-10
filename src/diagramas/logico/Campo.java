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
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ccandido
 */
public class Campo implements Serializable {

    private static final long serialVersionUID = 2099779589753765643L;

    private final Tabela tabela;

    public Tabela getTabela() {
        return tabela;
    }

    public Campo(Tabela tbl) {
        this.tabela = tbl;
        this.tabela.Add(this);
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

        ImageIcon img = null;
        if (isFkey()) {
            if (getCampoOrigem() == null) {
                g.drawRoundRect(r.x - 1 + f, r.y + 4 - 1, imgl + 1, imgl + 1, 4, 4);
            }
            if (isKey()) {
                img = Editor.fromControler().ImagemDeDiagrama.get(getTabela().imgkfk);
            } else if (isUnique()) {
                img = Editor.fromControler().ImagemDeDiagrama.get(getTabela().imgunfk);
            } else {
                img = Editor.fromControler().ImagemDeDiagrama.get(getTabela().imgfk);
            }
        } else if (isKey()) {
            img = Editor.fromControler().ImagemDeDiagrama.get(getTabela().imgk);
        } else if (isUnique()) {
            img = Editor.fromControler().ImagemDeDiagrama.get(getTabela().imgun);
        }

        if (img != null) {
            if (getTabela().isDisablePainted()) {
                img = new ImageIcon(util.TratadorDeImagens.dye(img, getTabela().getForeColor()));
            }
            g.drawImage(img.getImage(), r.x + f, r.y + 4, imgl, imgl, null);
        }
        g.clipRect(r.x, r.y, r.width, r.height);
        g.setColor(getTabela().getForeColor());
        g.drawString(GetTexto(), r.x + f + imgl + 2, r.y + altura / 2 + f);
        g.setClip(bkp);
        g.setPaint(bkpp);
    }

    protected String GetTexto() {
        return getTexto() + (getTipo().isEmpty() ? "" : ": " + getTipo());
    }

    public boolean isMe(Point p) {
        if (area == null) {
            return false;
        }
        return area.contains(p);
    }
    //<editor-fold defaultstate="collapsed" desc="Campos">
    private String texto = "";

    public Campo getCampoOrigem() {
        Constraint cons = getTabela().getPresentAsFK(this);
        if (cons == null) {
            return null;
        }

        int idx = cons.getCamposDeDestino().indexOf(this);
        if (idx > -1) {
            return cons.getCamposDeOrigem().get(idx);
        }
        return null;
    }

//    public void setCampoOrigem(Campo campoOrigem) {
//        if (this.campoOrigem != campoOrigem) {
//            if (campoOrigem != null) {
//                if (getTabelaOrigem() != null && getTabelaOrigem().getCampos().indexOf(campoOrigem) > -1) {
//                    this.campoOrigem = campoOrigem;
//                } else {
//                    this.campoOrigem = null;
//                }
//            } else {
//                this.campoOrigem = null;
//                //getTabela().DoMuda();
//            }
//            Repaint();
//            RefreshPosNovoTexto(MSG_CAMPO_ORIGEM_RECIVE);
//        }
//    }
    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getDicionario() {
        return dicionario;
    }

    public void setDicionario(String dicionario) {
        this.dicionario = dicionario;
    }

    public boolean isFkey() {
        return fkey;
    }

    public void setFkey(boolean fkey) {
        if (this.fkey != fkey) {
            this.fkey = fkey;
            getTabela().ProcesseIrFK(this);
            Repaint();
        }
    }

    public void SetFkey(boolean fkey) {
        this.fkey = fkey;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        if (this.key != key) {
            this.key = key;
            getTabela().ProcesseIrKey(this);
            Repaint();
        }
    }

    public void SetKey(boolean key) {
        this.key = key;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean isSeparador() {
        return separador;
    }

    public void setSeparador(boolean separador) {
        this.separador = separador;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        if (!this.texto.equals(texto)) {
            this.texto = texto;
            Repaint();
            getTabela().RefreshPosNovoTexto();
        }
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        if (!this.tipo.equals(tipo)) {
            this.tipo = tipo;
            getTabela().NotifiqueIR(null, Tabela.MSG_CMP_CHANGE_TIPO, this);
            Repaint();
        }
    }
    private String tipo = "";
    private String complemento = "";
    private String observacao = "";
    private String dicionario = "";
    private boolean separador = false;
    private boolean key = false;
    private boolean fkey = false;
    private boolean selecionado = false;

    /**
     * DDL UNIQUE (para uso na integridade referencial)
     */
    private boolean Unique = false;

    public boolean isUnique() {
        return Unique;
    }

    public void setUnique(boolean Unique) {
        if (this.Unique == Unique) {
            return;
        }
        this.Unique = Unique;
        getTabela().ProcesseIrUnique(this);
        Repaint();
    }

    public void SetUnique(boolean Unique) {
        this.Unique = Unique;
    }

    public void SetUnique(boolean isadd, Constraint constr) {
        if (isadd) {
            this.Unique = true; // # já deve ser true!
            getTabela().ProcesseIrUnique(this, constr, isadd);
            return;
        } else {
            getTabela().ProcesseIrUnique(this, constr, isadd);
            List<Constraint> lst = getTabela().getPresentAsUN(this);
            this.Unique = (!lst.isEmpty());
        }
        Repaint();
    }

    public boolean isSelecionado() {
        return selecionado && getTabela().isSelecionado();
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public Tabela getTabelaOrigem() {
        Campo c = getCampoOrigem();
        if (c != null) {
            return c.getTabela();
        }
        return null;
    }

    //</editor-fold>
    /**
     * verifica se a tabela está selecionada para dar o efeito de seleção
     */
    public boolean SuperSelecionado() {
        return (tabela.isSelecionado() && isSelecionado());
    }

    public void ToXmlValores(Document doc, Element root) {
        Element me = doc.createElement("Campo");
        if (isSeparador()) {
            me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Separador", isSeparador()));
        } else {
            me.appendChild(util.XMLGenerate.ValorText(doc, "Texto", getTexto()));
            me.appendChild(util.XMLGenerate.ValorText(doc, "Tipo", getTipo()));
            me.appendChild(util.XMLGenerate.ValorText(doc, "Complemento", getComplemento()));
            me.appendChild(util.XMLGenerate.ValorText(doc, "Dicionario", getDicionario()));
            me.appendChild(util.XMLGenerate.ValorText(doc, "Observacao", getObservacao()));

            me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Key", isKey()));
            me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Fkey", isFkey()));

            me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Unique", isUnique()));
        }
        root.appendChild(me);
    }

    public void LoadFromXML(Element me, boolean colando) {
        String tmp = util.XMLGenerate.getValorTextoFrom(me, "Texto");
        if (tmp != null) {
            setTexto(tmp);
            setObservacao(util.XMLGenerate.getValorTextoFrom(me, "Observacao"));
            setDicionario(util.XMLGenerate.getValorTextoFrom(me, "Dicionario"));

            setTipo(util.XMLGenerate.getValorTextoFrom(me, "Tipo"));
            setComplemento(util.XMLGenerate.getValorTextoFrom(me, "Complemento"));
            setKey(util.XMLGenerate.getValorBooleanFrom(me, "Key"));
            setFkey(util.XMLGenerate.getValorBooleanFrom(me, "Fkey"));
            setUnique(util.XMLGenerate.getValorBooleanFrom(me, "Unique"));
        } else {
            setSeparador(true);
        }
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
        return (getTabela().getCampos().indexOf(this) == 0);
    }

    public boolean isLast() {
        int tmp = getTabela().getCampos().indexOf(this) + 1;
        return (tmp == getTabela().getCampos().size());
    }

    /**
     * Está validamente ligado/relacionado à uma tabela.
     *
     * @return
     */
    public boolean isLinkedToTable() {
        Tabela tb = getTabelaOrigem();
        return tb != null && getTabela().getListaDeTabelasLigadas().indexOf(tb) > -1;
    }

    public int getIndexOnTable() {
        return getTabela().getCampos().indexOf(this);
    }

    private final int DESCE_CAMPO = 1;
    private final int SOBE_CAMPO = -1;

    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> res) {

        res.add(InspectorProperty.PropertyFactorySeparador("campo.selecionado"));
        String relaName = "Campo";

        res.add(InspectorProperty.PropertyFactoryTexto("nome", relaName + ".setTexto", getTexto()));
        res.add(InspectorProperty.PropertyFactoryTexto("campo.tipo", relaName + ".setTipo", getTipo()));
        res.add(InspectorProperty.PropertyFactoryTexto("campo.complemento", relaName + ".setComplemento", getComplemento()));
        res.add(InspectorProperty.PropertyFactoryTextoL("dicionario", relaName + ".setDicionario", getDicionario()));
        res.add(InspectorProperty.PropertyFactoryTextoL("observacao", relaName + ".setObservacao", getObservacao()));
        res.add(InspectorProperty.PropertyFactorySN("campo.key", relaName + ".setKey", isKey()));
        res.add(InspectorProperty.PropertyFactorySN("campo.unique", relaName + ".setUnique", isUnique()));
        res.add(InspectorProperty.PropertyFactorySN("campo.fkey", relaName + ".setFkey", isFkey()).AddCondicaoForTrue(
                new String[]{relaName + ".setTabelaOrigem", relaName + ".setCampoOrigem",
                    relaName + ".setDdlOnUpdate", relaName + ".setDdlOnDelete"}));

        if (isFkey()) {
            String txt = getTabelaOrigem() == null ? "[]" : getTabelaOrigem().getTexto();
            res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), "campo.tabelaorigem", txt).setTag(Constraint.TAG_COMMAND_FK + 10));

            txt = getCampoOrigem() == null ? "()" : getCampoOrigem().getTexto();
            res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), "campo.campoorigem", txt).setTag(Constraint.TAG_COMMAND_FK + 10));
        } else {
            res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("campo.tabelaorigem", "[]").PropertyForceDisable(true));
            res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("campo.campoorigem", "()").PropertyForceDisable(true));
        }

        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdExcluirSubItem.name()));

        if (getTabela().getCampos().size() > 1) {
            res.add(InspectorProperty.PropertyFactorySeparador("tabela.campos.posicao", false));
            if (!isFirst()) {
                res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), "tabela.campos.sobe").setTag(SOBE_CAMPO));
            }
            if (!isLast()) {
                res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), "tabela.campos.desce").setTag(DESCE_CAMPO));
            }
        }
        return res;
    }

    protected transient boolean roqued = false;

    protected void MostreSeParaExibicao(TreeItem root) {
        root.add(new TreeItem(getTexto() + ": " + getTipo(), getTabela().getID(), this.getClass().getSimpleName()));
    }
}
