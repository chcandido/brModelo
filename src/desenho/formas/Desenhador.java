/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.formas;

import controlador.Editor;
import controlador.Diagrama;
import controlador.apoios.TreeItem;
import controlador.inspector.InspectorProperty;
import desenho.linhas.PontoDeLinha;
import desenho.preDiagrama.baseDrawerItem;
import desenho.preDiagrama.iBaseDrawer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author ccandido
 */
public class Desenhador extends Forma implements iBaseDrawer {

    private static final long serialVersionUID = -6675432580840672702L;

    public Desenhador(Diagrama diagrama) {
        super(diagrama);
    }
    public final int TIPOSETA = 0;
    public final int TIPOIMG = 1;
    public final int TIPODEZ = 2;
    private int tipo = 1;
    private transient BufferedImage imgSeta = null;
    private transient BufferedImage imagem = null;
    private float alfa = 1f;
    private String imgName = "";

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        if (this.tipo != tipo) {
            if (!getMaster().isCarregando) {
                if (!util.Dialogos.ShowMessageConfirmYES(getMaster().getEditor().getRootPane(), Editor.fromConfiguracao.getValor("Inspector.obj.desenhador.msg_muada_tipo"))) {
                    return;
                }
            }
            this.tipo = tipo;
            ReajusteDesenho();
        }
    }

    public boolean isTipoSeta() {
        return (tipo == TIPOSETA);
    }

    public boolean isTipoDesenho() {
        return (tipo == TIPODEZ);
    }

    public boolean isTipoImg() {
        return (tipo == TIPOIMG);
    }

    public void setTipoSeta() {
        setTipo(TIPOSETA);
    }

    public void setTipoImg() {
        setTipo(TIPOIMG);
        //imgSeta = null;
    }

    public float getAlfa() {
        return alfa;
    }

    public void setAlfa(float alfa) {
        this.alfa = alfa;
        InvalidateArea();
    }

    public void SetAlfa(int alfa) {
        this.alfa = (float) alfa / 100;
        if (this.alfa > 1) {
            this.alfa = 0.5f;
        }
        InvalidateArea();
    }

    @Override
    public void DoPaint(Graphics2D g) {
        if (isTipoSeta()) {
            DrawSeta(g);
        } else if (isTipoImg()) {
            DrawImagem(g);
        } else {
            //# Shape bkpA = g.getClip();
            Rectangle bkpA = g.getClipBounds();
            Rectangle r = getBounds();
            g.clipRect(r.x, r.y, r.width, r.height);
            
            Color bkp = g.getColor();
            for (baseDrawerItem bi : getItens()) {
                bi.setDisablePainted(isDisablePainted());
                bi.DoPaint(g);
            }
            g.setColor(bkp);
            g.setClip(bkpA);
        }
        if (imgres == null && imgSeta == null && (!isTipoDesenho())) {
            Color bkp = g.getColor();
            g.setColor(isDisablePainted()? disabledColor : Color.LIGHT_GRAY);
            g.drawRect(getLeft(), getTop(), getWidth() - 1, getHeight() - 1);
            g.setColor(bkp);
        }
        super.DoPaint(g);
    }

    //<editor-fold defaultstate="collapsed" desc="Seta">
    private int anguloSeta = 0;
    private boolean setaPontaDireita = true;
    private boolean setaPontaEsquerda = true;
    private int setaLargura = 2;
    private int desvioX = 0;
    private int desvioY = 0;
    private Color setaCor = Color.BLACK;

    public int getSetaLargura() {
        return setaLargura;
    }

    public void setSetaLargura(int setaLargura) {
        if (this.setaLargura != setaLargura) {
            this.setaLargura = setaLargura;
            ReajusteDesenho();
        }
    }

    public Color getSetaCor() {
        return isDisablePainted()? disabledColor : setaCor;
    }

    public void setSetaCor(Color setaCor) {
        if (this.setaCor != setaCor) {
            this.setaCor = setaCor;
            ReajusteDesenho();
        }
    }

    public int getAnguloSeta() {
        return anguloSeta;
    }

    public void setAnguloSeta(int anguloSeta) {
        if (this.anguloSeta != anguloSeta) {
            this.anguloSeta = anguloSeta;
            ReajusteDesenho();
        }
    }

    public int getDesvioX() {
        return desvioX;
    }

    public void setDesvioX(int desvioX) {
        if (this.desvioX != desvioX) {
            this.desvioX = desvioX;
            ReajusteDesenho(); //força recriação
        }
    }

    public int getDesvioY() {
        return desvioY;
    }

    public void setDesvioY(int desvioy) {
        if (this.desvioY != desvioy) {
            this.desvioY = desvioy;
            ReajusteDesenho();
        }
    }

    public boolean isSetaPontaDireita() {
        return setaPontaDireita;
    }

    public void setSetaPontaDireita(boolean setaPontaDireita) {
        if (this.setaPontaDireita != setaPontaDireita) {
            this.setaPontaDireita = setaPontaDireita;
            ReajusteDesenho();
        }
    }

    public boolean isSetaPontaEsquerda() {
        return setaPontaEsquerda;
    }

    public void setSetaPontaEsquerda(boolean setaPontaEsquerda) {
        if (this.setaPontaEsquerda != setaPontaEsquerda) {
            this.setaPontaEsquerda = setaPontaEsquerda;
            ReajusteDesenho();
        }
    }

    private void DrawSeta(Graphics2D g) {
        if (imgSeta == null) {
            Rectangle recSeta = null;
            Rectangle pos = new Rectangle(0, 0, getWidth() - 4, getHeight() - 4);
            int angulo = getAnguloSeta();
            if (angulo < 0) {
                angulo *= -1;
            }
            while (angulo > 180) {
                angulo -= 180;
            }

            int x, y, y2, x2;

            double catOp = (double) pos.height / 2;
            double senORcos = Math.sin(Math.toRadians(angulo));
            double hip = catOp / senORcos;
            double catAd = Math.sqrt((hip * hip) - (catOp * catOp));

            if (catAd <= (pos.width / 2)) {
                int rec = (int) ((pos.width - (2 * catAd)) / 2);
                x = rec + pos.x;
                y = 0 + pos.y;
                y2 = 0 + pos.height + pos.y;
                x2 = -rec + pos.x + pos.width;
                if (angulo > 90) {
                    int tmp = x;
                    x = x2;
                    x2 = tmp;
                }
            } else {
                catAd = pos.width / 2;
                senORcos = Math.cos(Math.toRadians(angulo));
                hip = catAd / senORcos;
                catOp = Math.sqrt((hip * hip) - (catAd * catAd));
                int rec = (int) ((pos.height - (2 * catOp)) / 2);
                x = pos.x;
                y = rec + pos.y;
                y2 = pos.height + pos.y - rec;
                x2 = pos.x + pos.width;
                if (angulo > 90) {
                    int tmp = y;
                    y = y2;
                    y2 = tmp;
                }
            }
            recSeta = new Rectangle(x + desvioX, y + desvioY, x2 - desvioX, y2 - desvioY);

            BufferedImage bi = new BufferedImage(pos.width, pos.height, BufferedImage.TYPE_INT_ARGB);
            Graphics gg = bi.getGraphics();
            //Color bkp = g.getColor();
            gg.setColor(getSetaCor());
            drawArrow((Graphics2D) gg, recSeta.x, recSeta.y, recSeta.width, recSeta.height);
            imgSeta = bi;
            //return;
            //g.setColor(bkp);
        }
        //g.drawLine(recSeta.x, recSeta.y, recSeta.width, recSeta.height);

//        if (recSeta == null) {
//            recSeta = new Rectangle(getLeft() + 1 + desvioX, getTop() + getHeight() / 2 + desvioX, getLeft() + getWidth() - 1 - desvioX, getTop() + getHeight() / 2 - desvioY);
//        }
        //drawArrow(g, 0, getHeight() / 2, getWidth(), getHeight() / 2);
        //drawArrow(g, getLeft() + 1 + desvioX, getTop() + getHeight() / 2 + desvioX, getLeft() + getWidth() - 1 - desvioX, getTop() + getHeight() / 2 - desvioY);
        //drawArrow(g, recSeta.x, recSeta.y, recSeta.width, recSeta.height);
        g.drawImage(imgSeta, getLeft() + 2, getTop() + 2, null);
    }

    private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {

        RenderingHints renderHints
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        renderHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.addRenderingHints(renderHints);

        g.setStroke(new BasicStroke(
                getSetaLargura(),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        //g.setPaint(Color.BLACK);
        int ARR_SIZE = 3 + getSetaLargura();
        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx * dx + dy * dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        //AffineTransform bkp = g.getTransform();
        g.setTransform(at);

        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(ARR_SIZE, 0, len - ARR_SIZE, 0);

        if (isSetaPontaEsquerda()) {
            g.fillPolygon(new int[]{len, len - ARR_SIZE, len - ARR_SIZE, len},
                    new int[]{0, -ARR_SIZE, ARR_SIZE, 0}, 4);
        }
        if (isSetaPontaDireita()) {
            g.fillPolygon(new int[]{0, ARR_SIZE, ARR_SIZE, 0},
                    new int[]{0, -ARR_SIZE, ARR_SIZE, 0}, 4);
        }
    }

    //</editor-fold>
    @Override
    public void ReSized() {
        super.ReSized();
        if (isTipoSeta() && imgSeta != null) {
            if (imgSeta.getWidth() + 4 != getWidth() || imgSeta.getHeight() + 4 != getHeight()) {
                imgSeta = null;
            }
        }
        if (isTipoImg() && imgres != null) {
            if (imgres.getWidth(null) + 4 != getWidth() || imgres.getHeight(null) + 4 != getHeight()) {
                imgres = null;
            }
        }
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();

        res.add(InspectorProperty.PropertyFactorySeparador("desenhador"));
        ArrayList<String> men = new ArrayList<>();
        men.add(Editor.fromConfiguracao.getValor("Inspector.lst.desenhador.seta")); //seta == 0
        men.add(Editor.fromConfiguracao.getValor("Inspector.lst.desenhador.imagem"));
        men.add(Editor.fromConfiguracao.getValor("Inspector.lst.desenhador.desenholivre"));
        InspectorProperty txtTipo = InspectorProperty.PropertyFactoryMenu("desenhador.tipo", "setTipo", getTipo(), men);
        res.add(txtTipo);
        txtTipo.AddCondicao(new String[]{"0"}, new String[]{"setAnguloSeta", "setSetaCor", "setSetaLargura", "setDesvioX",
            "setDesvioY", "setSetaPontaDireita", "setSetaPontaEsquerda"});
        txtTipo.AddCondicao(new String[]{"1"}, new String[]{nomeComandos.cmdLoadImg.name(), "SetAlfa", "desenhador.imagem.arquivo", "desenhador.imagem.size", nomeComandos.cmdDoAnyThing.name()});

        txtTipo.AddCondicao(new String[]{"2"}, new String[]{nomeComandos.cmdCallDrawerEditor.name()});

        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdLoadImg.name()));
        res.add(InspectorProperty.PropertyFactoryNumero("diagrama.detalhe.alfa", "SetAlfa", (int) (100 * getAlfa())));
        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("desenhador.imagem.arquivo", getImgName()));

        Point p = GetImgSize();
        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("desenhador.imagem.size", "(" + String.valueOf(p.x) + " ," + String.valueOf(p.y) + ")"));
        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "desenhador.imagem.resize").setTag(1));

        res.add(InspectorProperty.PropertyFactoryNumero("seta.angulo", "setAnguloSeta", getAnguloSeta()));
        res.add(InspectorProperty.PropertyFactoryCor("seta.cor", "setSetaCor", getSetaCor()));
        res.add(InspectorProperty.PropertyFactoryNumero("seta.largura", "setSetaLargura", getSetaLargura()));
        res.add(InspectorProperty.PropertyFactoryNumero("seta.desvio.x", "setDesvioX", getDesvioX()));
        res.add(InspectorProperty.PropertyFactoryNumero("seta.desvio.y", "setDesvioY", getDesvioY()));

        res.add(InspectorProperty.PropertyFactorySN("seta.pontadireita", "setSetaPontaDireita", isSetaPontaDireita()));
        res.add(InspectorProperty.PropertyFactorySN("seta.pontaesquerda", "setSetaPontaEsquerda", isSetaPontaEsquerda()));

        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdCallDrawerEditor.name()));

        InspectorProperty tmp = InspectorProperty.FindByProperty(res, "setTexto");
        tmp.ReSetCaptionFromConfig("texto");
        tmp = InspectorProperty.FindByProperty(res, "setTextoAdicional");
        res.remove(tmp);

        return res;
    }

    /**
     * Usado para dizer como o artefato se comporta: se true: como uma caixa de dezenho que não pode ser ligada a nada. Se false: como uma caixa de dezenho que poderá ser ligada.
     */
    private boolean simplesDezenho = true;

    public boolean isSimplesDezenho() {
        return simplesDezenho;
    }

    public void setSimplesDezenho(boolean simplesDezenho) {
        this.simplesDezenho = simplesDezenho;
    }

    @Override
    protected void DoPaintDoks(Graphics2D g) {
        if (!simplesDezenho) {
            super.DoPaintDoks(g);
        }
    }

    @Override
    public boolean CanLiga(PontoDeLinha aThis) {
        if (!simplesDezenho) {
            return super.CanLiga(aThis);
        }
        return false;
    }

    private void ReajusteDesenho() {
        imgSeta = null;
        imgres = null;
        imagem = null;
        byteImage = null;
        imgName = "";
        getItens().clear();
        InvalidateArea();
    }

    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);
        Proporcao();
        InvalidateArea();
    }

    //<editor-fold defaultstate="collapsed" desc="Imagem">
    transient Image imgres = null;

    public void DrawImagem(Graphics2D g) {
        BufferedImage imgB = getImagem();
        if (imgB == null) {
            return;
        }
        Rectangle rec = getBounds();
        rec.grow(-2, -2);
        if (imgres == null) {
            imgres = imgB.getScaledInstance(rec.width, rec.height, Image.SCALE_SMOOTH);
        }

        Composite originalComposite = g.getComposite();
        if (alfa != 1f) {
            int type = AlphaComposite.SRC_OVER;
            g.setComposite(AlphaComposite.getInstance(type, alfa));
        }
        Image img = imgres;
        if (isDisablePainted()) {
            img = util.TratadorDeImagens.dye(new ImageIcon(imgres), disabledColor);
        }
        
        g.drawImage(img, rec.x, rec.y, null);
        g.setComposite(originalComposite);
    }

    public BufferedImage getImagem() {
        if (imagem == null && byteImage != null) {
            imagem = fromByteArray(byteImage);
        }
        return imagem;
    }

    public void setImagem(BufferedImage imagem) {
        byteImage = toByteArray(imagem);
        this.imagem = imagem;
    }
    private byte[] byteImage = null;

    private BufferedImage fromByteArray(byte[] imagebytes) {
        return util.TratadorDeImagens.fromByteArray(imagebytes);
    }

    private byte[] toByteArray(BufferedImage bufferedImage) {
        return util.TratadorDeImagens.toByteArray(bufferedImage);
    }

    public boolean LoadImageFromFile(String arq) {
        imagem = null;
        imgres = null;
        byteImage = null;
        imgName = "";
        setTipoImg();
        try {
            File fimg = new File(arq);
            setImgName(fimg.getName());
            setImagem(ImageIO.read(fimg));
            Proporcao();
            InvalidateArea();
        } catch (IOException iOException) {
            InvalidateArea();
            util.BrLogger.Logger("ERROR_OPEN_FILE_IMG", iOException.getMessage());
            return false;
        } catch (Exception e) {
            InvalidateArea();
            util.BrLogger.Logger("ERROR_OPEN_FILE_IMG", e.getMessage());
            return false;
        }
        if (imagem == null) {
            util.BrLogger.Logger("ERROR_OPEN_FILE_IMG", "[EMPTY IMG?]", "[]");
            return false;
        }
        return true;
    }

    public Point GetImgSize() {
        BufferedImage img = getImagem();
        Point res = new Point();
        if (img != null) {
            res = new Point(img.getWidth(), img.getHeight());
        }
        return res;
    }

    private void Proporcao() {
        if (GetImgSize().x == 0 || GetImgSize().y == 0) {
            return;
        }
        int x = (GetImgSize().y * getWidth() / GetImgSize().x);
        setHeight(x);
        Reposicione();
    }

    //</editor-fold>
    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Tipo", getTipo()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "SetaLargura", getSetaLargura()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "SetaCor", getSetaCor()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "AnguloSeta", getAnguloSeta()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "DesvioX", getDesvioX()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "DesvioY", getDesvioY()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "SetaPontaDireita", isSetaPontaDireita()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "SetaPontaEsquerda", isSetaPontaEsquerda()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "ImagemAlpha", (int) (alfa * 100)));
        me.appendChild(util.XMLGenerate.ValorString(doc, "ImagemNome", imgName));

        Element e = util.XMLGenerate.ValorInteger(doc, "DrawerItens", getItens().size());
        getItens().stream().forEach((bi) -> {
            bi.ToXml(doc, e);
        });
        me.appendChild(e);
        if (byteImage != null) {
            me.appendChild(util.XMLGenerate.ValorText(doc, "Image", util.Utilidades.Hexadecimal(byteImage)));
        }
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        setTipo(util.XMLGenerate.getValorIntegerFrom(me, "Tipo"));
        setSetaLargura(util.XMLGenerate.getValorIntegerFrom(me, "SetaLargura"));
        Color c = util.XMLGenerate.getValorColorFrom(me, "SetaCor");
        if (c != null) {
            setSetaCor(c);
        }
        setAnguloSeta(util.XMLGenerate.getValorIntegerFrom(me, "AnguloSeta"));
        setDesvioX(util.XMLGenerate.getValorIntegerFrom(me, "DesvioX"));
        setDesvioY(util.XMLGenerate.getValorIntegerFrom(me, "DesvioY"));
        setSetaPontaDireita(util.XMLGenerate.getValorBooleanFrom(me, "SetaPontaDireita"));
        setSetaPontaEsquerda(util.XMLGenerate.getValorBooleanFrom(me, "SetaPontaEsquerda"));
        SetAlfa(util.XMLGenerate.getValorIntegerFrom(me, "ImagemAlpha"));
        setImgName(util.XMLGenerate.getValorStringFrom(me, "ImagemNome"));

        String tmp = util.XMLGenerate.getValorTextoFrom(me, "Image");
        if (tmp != null) {
            byteImage = util.Utilidades.HexadecimalToByteArr(tmp);
        }

        Element inter = util.XMLGenerate.FindByNodeName(me, "DrawerItens");
        if (inter != null) {
            for (int s = 0; s < inter.getChildNodes().getLength(); s++) {
                Node fstNode = inter.getChildNodes().item(s);
                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fstElmnt = (Element) fstNode;
                    baseDrawerItem item = AddItem();
                    if (!item.LoadFromXML(fstElmnt, colando)) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }

    public String getImgName() {
        return imgName;
    }

    @Override
    public baseDrawerItem AddItem() {
        baseDrawerItem bi = new baseDrawerItem(this, baseDrawerItem.tipoDrawer.tpRetangulo);
        getItens().add(bi);
        return bi;
    }

    private ArrayList<baseDrawerItem> Itens = new ArrayList<>();

    @Override
    public ArrayList<baseDrawerItem> getItens() {
        return Itens;
    }

    public void setItens(ArrayList<baseDrawerItem> Itens) {
        this.Itens = Itens;
    }

    @Override
    public String FormateUnidadeMedida(int valor) {
        return Integer.toString(valor);
    }

    @Override
    public int getH() {
        return getHeight();
    }

    @Override
    public int getL() {
        return getLeft();
    }

    @Override
    public int getT() {
        return getTop();
    }

    @Override
    public int getW() {
        return getWidth();
    }

    @Override
    public boolean MostreSeParaExibicao(TreeItem root) {
        root.add(new TreeItem(Editor.fromConfiguracao.getValor("diagrama.Desenhador.nome"), getID(), this.getClass().getSimpleName()));
        return true;
    }

    @Override
    public void PoluleColors(ArrayList<Color> cores) {
        super.PoluleColors(cores); //To change body of generated methods, choose Tools | Templates.
        if (cores.indexOf(getSetaCor()) == -1) {
            cores.add(setaCor);
        }
    }

}
