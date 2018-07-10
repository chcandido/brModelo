package controlador.apoios;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import desenho.formas.Forma;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ccandido
 */
public class InfoDiagrama extends Forma {

    private static final long serialVersionUID = 4149452269833523526L;

    public InfoDiagrama(Diagrama modelo) {
        super(modelo);
        setVisible(false);
        setSelecionavel(false);
        diagramaUniversalUnicID = geraUnicDiagramaID();
        realDiagramaUniversalUnicID = diagramaUniversalUnicID;
    }

    public InfoDiagrama(Diagrama modelo, String texto) {
        super(modelo, texto);
        setVisible(false);
        diagramaUniversalUnicID = geraUnicDiagramaID();
        realDiagramaUniversalUnicID = diagramaUniversalUnicID;
    }

    private String geraUnicDiagramaID() {
        java.util.Random r = new Random();
        String res = "";
        for (int i = 0; i < 10; i++) {
            res += Integer.toHexString(r.nextInt(256)) + (i % 3 != 0 ? "-" : "");
        }
        return res.toUpperCase();
    }

    public final void ReGeraGuardaUnicDiagramaID() {
        diagramaUniversalUnicID = geraUnicDiagramaID();
    }

    @Override
    public void DoPaint(Graphics2D g) {
        g.setPaint(this.getForeColor());
        super.DoPaint(g);
        g.drawRect(getLeft(), getTop(), getWidth() - 1, getHeight() - 1);
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = new ArrayList<>();
        getMaster().BeginProperty(res);
        res.add(InspectorProperty.PropertyFactorySeparador("diagrama.versao.titulo"));
        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("diagrama.versao", getMaster().getVersao()));
        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("diagrama.nome", getMaster().getNome()));
        if (getMaster().getEditor().isMostrarIDs()) {
            res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("diagrama.unicid", getRealDiagramaUniversalUnicID()));
        }
        String arq = ("".equals(getMaster().getArquivo()) ? "" : (new File(getMaster().getArquivo())).getName());
        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("diagrama.arquivo", arq));
        res.add(InspectorProperty.PropertyFactoryTextoL("diagrama.autores", "setAutores", getAutores()));
        res.add(InspectorProperty.PropertyFactoryTextoL("diagrama.observacao", "setObservacao", getObservacao()));
        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("diagrama.tipo", getMaster().getTipoDeDiagramaFormatado()));
        res.add(InspectorProperty.PropertyFactorySeparador("dimensoes"));
        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("width", Integer.toString(getMaster().getWidth())));
        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("height", Integer.toString(getMaster().getHeight())));

        ArrayList<String> vl = new ArrayList<>();
        for (int i = 0; i < getMaster().getEditor().zoonsd.length; i++) {
            vl.add(Double.toString(getMaster().getEditor().zoonsd[i] * 100) + "%");
        }
        res.add(InspectorProperty.PropertyFactoryMenu("zoom", "setZoonInt", getZoonInt(), vl));
        //res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("zoom", Double.toString(getMaster().getZoom()*100) + "%"));
        res.add(InspectorProperty.PropertyFactorySeparador("diagrama.alinhamento"));
        res.add(InspectorProperty.PropertyFactoryNumero("diagrama.alinhamento_h", "setAlinhamento_h", getAlinhamento_h()));
        res.add(InspectorProperty.PropertyFactoryNumero("diagrama.alinhamento_v", "setAlinhamento_v", getAlinhamento_v()));

        res.add(InspectorProperty.PropertyFactorySeparador("fonte"));
        Font f = getFont();
        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("fonte.nome", f.getName()));
        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("fonte.tamanho", Integer.toString(f.getSize())));
        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("fonte.estilo", util.Utilidades.decodeFontStyle(f.getStyle())));
        if (editFonte) {
            //# res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdFonte.name()));
            res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdFonte.name(), nomeComandos.cmdFonte.name().toLowerCase(), getFont().getFontName()));
        }

        getMaster().EndProperty(res);
        return res;
    }

    public int getZoonInt() {
        int res = 4;
        double z = getMaster().getZoom();
        for (int i = 0; i < getMaster().getEditor().zoonsd.length; i++) {
            if (getMaster().getEditor().zoonsd[i] == z) {
                res = i;
            }
        }
        return res;
    }

    public void setZoonInt(int zoonInt) {
        getMaster().getEditor().setZoom(getMaster().getEditor().zoonsd[zoonInt]);
    }

    private String autores = "";

    public String getAutores() {
        return autores;
    }

    public void setAutores(String autores) {
        this.autores = autores;
    }

    /**
     * No momento em que se estiver colando o XML verifica-se se o ato de colar está acontecendo no mesmo diagrama por meio do UnicID do diagrama atual
     *
     * @return true se é o mesmo.
     */
    public boolean IsTheShame() {
        return diagramaUniversalUnicID.equals(diagramaOldUniversalUnicID);
    }

    /**
     * Verifiquei que precisava garantir que o diagramaUniversalUnicID fosse único porém, ao copiar um arquivo do modelo o diagramaUniversalUnicID se tornava duplicado. Os dois modelos poderiam vir a ser abertos pelo mesmo editor ao mesmo tempo, causando problemas. Por isso, decidi que todas as vezes que um diagrama fosse carregado, um novo diagramaUniversalUnicID seria gerado para ele. Porém, resolvi preserva aquele primeiro gerado.
     */
    private String realDiagramaUniversalUnicID = "";

    public String getRealDiagramaUniversalUnicID() {
        return realDiagramaUniversalUnicID;
    }

    private String diagramaUniversalUnicID;

    public void setDiagramaUniversalUnicID(String diagramaUniversalUnicID) {
        this.diagramaUniversalUnicID = diagramaUniversalUnicID;
    }

    /**
     * Qual era o ID anterior? Setado apenas se estiver colando.
     */
    private transient String diagramaOldUniversalUnicID = "";

    public String getDiagramaUniversalUnicID() {
        return diagramaUniversalUnicID;
    }

    public String getDiagramaOldUniversalUnicID() {
        return diagramaOldUniversalUnicID;
    }

    public void setDiagramaOldUniversalUnicID(String uid) {
        this.diagramaOldUniversalUnicID = uid;
    }

    private int alinhamento_v = 50, alinhamento_h = 60;

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        //super.InfoDiagrama_ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorString(doc, "Versao", getMaster().getVersao()));
        //me.appendChild(util.XMLGenerate.ValorString(doc, "Nome", getMaster().getNome()));
        me.appendChild(util.XMLGenerate.ValorText(doc, "Autores", getAutores()));
        me.appendChild(util.XMLGenerate.ValorText(doc, "Observacao", getObservacao()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Width", getMaster().getWidth()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Height", getMaster().getHeight()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Alinhamento_v", getAlinhamento_v()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Alinhamento_h", getAlinhamento_h()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Zoom", (int) (getMaster().getZoom() * 100)));
        me.appendChild(util.XMLGenerate.ValorFonte(doc, getFont()));
        me.appendChild(util.XMLGenerate.ValorPoint(doc, "Localizacao", getMaster().ScrPosicao));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Realce", getMaster().isRealce()));

        getMaster().InfoDiagrama_ToXmlValores(doc, me);
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        if (!colando) {
            diagramaUniversalUnicID = me.getAttribute("UniversalUnicID");
            realDiagramaUniversalUnicID = diagramaUniversalUnicID;
        }
        //setNome(util.XMLGenerate.getValorStringFrom(me, "Nome"));
        setAutores(util.XMLGenerate.getValorTextoFrom(me, "Autores"));
        setObservacao(util.XMLGenerate.getValorTextoFrom(me, "Observacao"));
        setFont(util.XMLGenerate.getValorFonte(me));
        getMaster().setZoom((double) util.XMLGenerate.getValorIntegerFrom(me, "Zoom") / 100.0);
        getMaster().ScrPosicao = util.XMLGenerate.getValorPointFrom(me, "Localizacao");
        setAlinhamento_h(util.XMLGenerate.getValorIntegerFrom(me, "Alinhamento_h"));
        setAlinhamento_v(util.XMLGenerate.getValorIntegerFrom(me, "Alinhamento_v"));
        
        getMaster().SetRealce(util.XMLGenerate.getValorBooleanFrom(me, "Realce"));
        getMaster().LoadVersao(util.XMLGenerate.getValorStringFrom(me, "Versao"));

        return getMaster().InfoDiagrama_LoadFromXML(me, colando);
    }

    @Override
    public boolean MostreSeParaExibicao(TreeItem root) {
        return false;
    }

    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);
        getMaster().DoAnyThing(Tag);
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        getMaster().setFont(font);
    }

    //Espaço de alinhamento entre os itens - comando de alinahmento
    /**
     * @return the alinhamento_v
     */
    public int getAlinhamento_v() {
        return alinhamento_v;
    }

    /**
     * @param alinhamento_v the alinhamento_v to set
     */
    public void setAlinhamento_v(int alinhamento_v) {
        this.alinhamento_v = alinhamento_v;
    }

    /**
     * @return the alinhamento_h
     */
    public int getAlinhamento_h() {
        return alinhamento_h;
    }

    /**
     * @param alinhamento_h the alinhamento_h to set
     */
    public void setAlinhamento_h(int alinhamento_h) {
        this.alinhamento_h = alinhamento_h;
    }

    public void setFromString(String str) {
        int tag = getMaster().getEditor().getInspectorEditor().getSelecionado().getTag();
        getMaster().setFromString(str, tag);
    }
    
    
}
