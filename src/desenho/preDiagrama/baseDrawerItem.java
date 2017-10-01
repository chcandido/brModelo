/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.preDiagrama;

import controlador.apoios.IObjetoPintavel;
import controlador.apoios.ProcessadorExprSimples;
import desenho.preAnyDiagrama.PreTexto;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ccandido
 */
public class baseDrawerItem implements Serializable, IObjetoPintavel {

    private static final long serialVersionUID = 7929621219268376814L;

    private final iBaseDrawer dono;
    public static final boolean VERTICAL = true;

    public baseDrawerItem(iBaseDrawer dono) {
        super();
        this.dono = dono;
    }

    public baseDrawerItem(iBaseDrawer dono, tipoDrawer tp) {
        super();
        this.dono = dono;
        this.tipo = tp;
    }

    private transient boolean outroPintor = false;

    @Override
    public void setOutroPintor(boolean op) {
        outroPintor = op;
    }

    @Override
    public boolean isOutroPintor() {
        return outroPintor;
    }

    /**
     * @return the retangulo
     */
    public String getRetangulo() {
        return retangulo;
    }

    /**
     * @param retangulo the retangulo to set
     */
    public void setRetangulo(String retangulo) {
        this.retangulo = retangulo;
    }

    /**
     * @return the curva
     */
    public String getCurva() {
        return curva;
    }

    /**
     * @param curva the curva to set
     */
    public void setCurva(String curva) {
        this.curva = curva;
    }

    /**
     * @return the elipse
     */
    public String getElipse() {
        return elipse;
    }

    /**
     * @param elipse the elipse to set
     */
    public void setElipse(String elipse) {
        this.elipse = elipse;
    }

    /**
     * @return the arco
     */
    public String getArco() {
        return arco;
    }

    /**
     * @param arco the arco to set
     */
    public void setArco(String arco) {
        this.arco = arco;
    }

    /**
     * @return the direcaogradiente
     */
    public int getDirecaogradiente() {
        return direcaogradiente;
    }

    /**
     * @param direcaogradiente the direcaogradiente to set
     */
    public void setDirecaogradiente(int direcaogradiente) {
        this.direcaogradiente = direcaogradiente;
    }

    public boolean LoadFromXML(Element me, boolean colando) {
        SetTipo(Integer.valueOf(util.XMLGenerate.GetValorString(me)));
        setCor(util.XMLGenerate.getValorColorFrom(me, "Cor"));
        setGradienteEndColor(util.XMLGenerate.getValorColorFrom(me, "GradienteEndColor"));
        setGradienteStartColor(util.XMLGenerate.getValorColorFrom(me, "GradienteStartColor"));

        setInvertido(util.XMLGenerate.getValorBooleanFrom(me, "Invertido"));
        setFill(util.XMLGenerate.getValorBooleanFrom(me, "Fill"));
        setRecivePaint(util.XMLGenerate.getValorBooleanFrom(me, "RecivePaint"));
        setGradiente(util.XMLGenerate.getValorBooleanFrom(me, "Gradiente"));
        setVertical(util.XMLGenerate.getValorBooleanFrom(me, "Vertical"));

        setPath(util.XMLGenerate.getValorStringFrom(me, "Path"));
        setRetangulo(util.XMLGenerate.getValorStringFrom(me, "Retangulo"));
        setCurva(util.XMLGenerate.getValorStringFrom(me, "Curva"));
        setElipse(util.XMLGenerate.getValorStringFrom(me, "Elipse"));
        setArco(util.XMLGenerate.getValorStringFrom(me, "Arco"));
        setPosiImagem(util.XMLGenerate.getValorStringFrom(me, "PosiImagem"));

        setDirecaogradiente(util.XMLGenerate.getValorIntegerFrom(me, "Direcaogradiente"));

        Rectangle r = util.XMLGenerate.getValorRectFrom(me, "Bounds");
        setLeft(r.x);
        setTop(r.y);
        setWidth(r.width);
        setHeight(r.height);

        String tmp = util.XMLGenerate.getValorTextoFrom(me, "Image");
        if (tmp != null) {
            byteImage = util.Utilidades.HexadecimalToByteArr(tmp);
        }
        return true;
    }

    public void ToXml(Document doc, Element root) {
        Element me = util.XMLGenerate.ValorInteger(doc, getClass().getSimpleName(), tipo.ordinal());

        me.appendChild(util.XMLGenerate.ValorColor(doc, "Cor", getCor()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteEndColor", getGradienteEndColor()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteStartColor", getGradienteStartColor()));

        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Invertido", isInvertido()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Fill", isFill()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "RecivePaint", isRecivePaint()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Gradiente", isGradiente()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Vertical", isVertical()));

        me.appendChild(util.XMLGenerate.ValorString(doc, "Path", getPath()));
        me.appendChild(util.XMLGenerate.ValorString(doc, "Retangulo", getRetangulo()));
        me.appendChild(util.XMLGenerate.ValorString(doc, "Curva", getCurva()));
        me.appendChild(util.XMLGenerate.ValorString(doc, "Elipse", getElipse()));
        me.appendChild(util.XMLGenerate.ValorString(doc, "Arco", getArco()));
        me.appendChild(util.XMLGenerate.ValorString(doc, "PosiImagem", getPosiImagem()));

        me.appendChild(util.XMLGenerate.ValorRect(doc, "Bounds", new Rectangle(getLeft(), getTop(), getWidth(), getHeight())));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Direcaogradiente", getDirecaogradiente()));
        if (byteImage != null) {
            me.appendChild(util.XMLGenerate.ValorText(doc, "Image", util.Utilidades.Hexadecimal(byteImage)));
        }
        root.appendChild(me);
    }

    public void DoAnyThing(int Tag) {
        if (Tag == 1) {
            Proporcao();
            //dono.InvalidateArea();
        }
    }

    public baseDrawerItem Clone(baseDrawerItem origem) {
        this.setTipo(origem.getTipo());
        this.setCor(origem.getCor());
        this.setGradienteEndColor(origem.getGradienteEndColor());
        this.setGradienteStartColor(origem.getGradienteStartColor());

        this.setInvertido(origem.isInvertido());
        this.setFill(origem.isFill());
        this.setRecivePaint(origem.isRecivePaint());
        this.setGradiente(origem.isGradiente());
        this.setVertical(origem.isVertical());

        this.setPath(origem.getPath());
        this.setRetangulo(origem.getRetangulo());
        this.setCurva(origem.getCurva());
        this.setElipse(origem.getElipse());
        this.setArco(origem.getArco());
        this.setPosiImagem(origem.getPosiImagem());

        this.setDirecaogradiente(origem.getDirecaogradiente());

        this.setLeft(origem.getLeft());
        this.setTop(origem.getTop());
        this.setWidth(origem.getWidth());
        this.setHeight(origem.getHeight());

        this.byteImage = origem.byteImage;
        return this;
    }

    public enum tipoDrawer {

        tpRetangulo, tpElipse, tpCurva, tpArco, tpPath, tpImagem, tpMedida
    }

    public Color getCor() {
        return isDisablePainted() ? disabledColor : cor;
    }

    public void setCor(Color cor) {
        this.cor = cor;
    }

    public tipoDrawer getTipo() {
        return tipo;
    }

    public void setTipo(tipoDrawer tipo) {
        if (tipo != tipoDrawer.tpImagem && tipo != this.tipo) {
            imagem = null;
            imgres = null;
            byteImage = null;
            //Imagem é apagada se mudar o tipo!
        }
        this.tipo = tipo;
    }

    public void SetTipo(int tipo) {
        tipoDrawer inttp = tipoDrawer.tpRetangulo;

        try {
            inttp = tipoDrawer.values()[tipo];
        } catch (Exception e) {
        }
        setTipo(inttp);
    }

    private Color cor = Color.BLACK;
    private boolean invertido = false;
    private String path = "L, T, L + W - 2, T + H - 2, L, T + H - 2, L, T";
    private boolean fill = true;
    private boolean recivePaint = false;
    private String retangulo = "L,T,W-2,H-2";
    private String curva = "L, T, L + W - 2, T + H - 2, L, T + H - 2, L, T";
    private String elipse = "L, T, W - 2, H - 2";
    private String arco = "L,T,W,H,90,135,0";
    private String posiImagem = "L,T,200,200";

    //<editor-fold defaultstate="collapsed" desc="Imagem">
    private transient Image imgres = null;
    private transient BufferedImage imagem = null;

    public void DrawImagem(Graphics2D g) {
        BufferedImage img = getImagem();
        if (img == null) {
            return;
        }
        int[] pts = ArrayDePontos(getPosiImagem());
        if (pts.length != 4) {
            posiImagem = "L,T,200,200";
            imgres = null;
            pts = ArrayDePontos(getPosiImagem());
        }
        Rectangle rec = new Rectangle(pts[0], pts[1], pts[2], pts[3]);
        rec.grow(-2, -2);
        if (imgres == null) {
            imgres = img.getScaledInstance(rec.width, rec.height, Image.SCALE_SMOOTH);
        }
        g.drawImage(imgres, rec.x, rec.y, null);
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

    protected byte[] byteImage = null;

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
        try {
            File fimg = new File(arq);
            setImagem(ImageIO.read(fimg));
            Proporcao();
            dono.InvalidateArea();
        } catch (IOException iOException) {
            dono.InvalidateArea();
            util.BrLogger.Logger("ERROR_OPEN_FILE_IMG", iOException.getMessage());
            return false;
        } catch (Exception e) {
            dono.InvalidateArea();
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
        imgres = null;
        String[] dist = getPosiImagem().split(",");
//        if (dist.length < 0) {
//            setPosiImagem("L,T,200,200");
//            Proporcao();
//            return;
//        }
        int[] pts = ArrayDePontos(getPosiImagem());
        int x = (GetImgSize().y * pts[2] / GetImgSize().x);
        dist[3] = String.valueOf(x);
        setPosiImagem(util.Utilidades.ArrayToStr(dist));
    }

    //</editor-fold>
    public String getPosiImagem() {
        return posiImagem;
    }

    public void setPosiImagem(String posiImagem) {
        String[] dist = posiImagem.split(",");
        if (dist.length < 0) {
            posiImagem = "L,T,200,200";
            dist = posiImagem.split(",");
        }
        int[] pts = ArrayDePontos(posiImagem);
        dist[3] = String.valueOf(pts[3]);
        dist[2] = String.valueOf(pts[2]);
        this.posiImagem = util.Utilidades.ArrayToStr(dist);
        imgres = null;
        //dono.InvalidateArea();
    }

    public boolean isRecivePaint() {
        return recivePaint;
    }

    public void setRecivePaint(boolean recivePaint) {
        this.recivePaint = recivePaint;
    }

    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isInvertido() {
        return invertido;
    }

    public void setInvertido(boolean invertido) {
        this.invertido = invertido;
    }
    private tipoDrawer tipo = tipoDrawer.tpRetangulo;

    private int left = 0;
    private int top = 0;
    private int width = 50;
    private int height = 50;
    private boolean gradiente = false;
    private Color gradienteEndColor = new Color(204, 204, 204, 255);//Color.WHITE;
    private Color gradienteStartColor = Color.BLACK;
    private boolean vertical = VERTICAL;
    private int direcaogradiente = PreTexto.VERTICAL;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Color getGradienteStartColor() {
        return isDisablePainted()? disabledColor : gradienteStartColor;
    }

    public void setGradienteStartColor(Color gradienteStartColor) {
        this.gradienteStartColor = gradienteStartColor;
    }

    public boolean isGradiente() {
        return gradiente;
    }

    public void setGradiente(boolean gradiente) {
        this.gradiente = gradiente;
    }

    public Color getGradienteEndColor() {
        return isDisablePainted()? disabledColor : gradienteEndColor;
    }

    public void setGradienteEndColor(Color gradienteEndColor) {
        this.gradienteEndColor = gradienteEndColor;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean aDirection) {
        vertical = aDirection;
    }

    protected GradientPaint PaintGradiente(Graphics2D g, int l, int t) {
        int w = dono.getW();
        int h = dono.getH();
        int L = l;
        int T = t;

        boolean dv = getDirecaogradiente() == PreTexto.VERTICAL;
        return new GradientPaint(L, T, getGradienteStartColor(), dv ? L : L + w, dv ? T + h : T, getGradienteEndColor(), true);
    }

    @Override
    public void DoPaint(Graphics2D g) {
        int l = left;
        int t = top;

        if (!outroPintor) {
            if (getTipo() == tipoDrawer.tpMedida) {
                l = dono.getL() + left;
                t = dono.getT() + top;
            } else {
                l = dono.getL();
                t = dono.getT();
            }
        }
        if (!recivePaint) {
            if (isGradiente()) {
                g.setPaint(PaintGradiente(g, l, t));
            } else {
                g.setColor(getCor());
            }
        }

        Shape dr = null;
        boolean ok = false;
        int[] pts;
        switch (tipo) {
            case tpElipse:
                pts = ArrayDePontos(getElipse());
                if (pts.length == 4) {
                    dr = new Ellipse2D.Double(pts[0], pts[1], pts[2], pts[3]);
                }
                break;
            case tpRetangulo:
                pts = ArrayDePontos(getRetangulo());
                if (pts.length == 4) {
                    dr = new Rectangle2D.Double(pts[0], pts[1], pts[2], pts[3]);
                }
                if (pts.length == 6) {
                    dr = new RoundRectangle2D.Double(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);
                }
                break;
            case tpCurva:
                pts = ArrayDePontos(getCurva());
                if (pts.length == 8) {
                    dr = new CubicCurve2D.Double(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5], pts[6], pts[7]);
                }
                if (pts.length == 6) {
                    dr = new QuadCurve2D.Double(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5]);
                }
                break;
            case tpArco:
                //        public final static int OPEN = 0;
                //        public final static int CHORD = 1;
                //        public final static int PIE = 2;
                pts = ArrayDePontos(getArco());
                if (pts.length == 7) {
                    dr = new Arc2D.Double(pts[0], pts[1], pts[2], pts[3], pts[4], pts[5], pts[6]);
                }
                break;
            case tpImagem:
                DrawImagem(g);
                ok = true;
                break;
            case tpMedida:
                //# Foi retirado do dezenho porque está feio (DrawerEditor).)
                if (vertical == VERTICAL) {
                    medidaH(g, l, t);
                } else {
                    medidaV(g, l, t);
                }
                ok = true;
                break;
            case tpPath:
                DrawComplexPath(g, l, t);
                ok = true;
                break;
            default:
                g.drawLine(l, t, getWidth(), getHeight());
                ok = true;
                break;
        }
        if (dr == null || ok) {
            if (dr == null && !ok) {
                g.drawString("?", l + 5, t + 5);
            }
            return;
        }
        if (isFill()) {
            g.fill(dr);
        } else {
            g.draw(dr);
        }
    }

    private void medidaH(Graphics2D g, int l, int t) {
        FontMetrics fm = g.getFontMetrics();
        String vl = dono.FormateUnidadeMedida(width);
        int xini = l;
        int pre_y = t;
        int xfim = l + width;
        int yfim = t + height / 2;

        int traco = height;
        int ytraco = pre_y;// - (traco/2);

        g.drawLine(xini, ytraco, xini, ytraco + traco);
        g.drawLine(xfim, ytraco, xfim, ytraco + traco);
        g.drawLine(xini, yfim, xfim, yfim);

        xini = xini + (width - fm.stringWidth(vl)) / 2;
        int yini = invertido ? yfim + (fm.getHeight() - fm.getDescent()) : yfim - fm.getDescent();// yfim + (fm.getHeight()) / 2 - fm.getDescent();
        g.drawString(vl, xini, yini);
    }

    private void medidaV(Graphics2D g, int l, int t) {
        FontMetrics fm = g.getFontMetrics();
        String vl = dono.FormateUnidadeMedida(height);
        int traco = width;
        int xIni = l;// + (traco) / 2;
        int xFim = xIni + traco;
        int yIni = t;
        int yFim = t + height;
        int xLin = l + (width / 2);

        g.drawLine(xIni, yIni, xFim, yIni);
        g.drawLine(xIni, yFim, xFim, yFim);
        g.drawLine(xLin, yIni, xLin, yFim);

        int degrees = isInvertido() ? 90 : -90;
        int desse = isInvertido() ? 0 : fm.stringWidth(vl);
        //int centra = fm.getHeight() / 2 - fm.getDescent();
        int centra = fm.getHeight() - fm.getDescent();
        centra = isInvertido() ? -centra : centra;

        AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(degrees));
        Font f = new Font(g.getFont().getName(), Font.BOLD, g.getFont().getSize());
        Font f2 = g.getFont();
        g.setFont(f.deriveFont(at));
        yIni = yIni + (height - fm.stringWidth(vl)) / 2 + desse;
        g.drawString(vl, xLin + centra, yIni);
        g.setFont(f2);
    }

    private boolean DrawComplexPath(Graphics2D g, int l, int t) {
        String[] dist = getPath().split(",");
        int dl = dist.length;
        if (dl < 3) {
            g.drawString("?", l, t);
            return false;
        }
        if (dl % 2 > 0) {
            String[] tmp = new String[dl + 1];
            tmp[dl] = "0";
            dist = tmp;
            dl = dist.length;
        }
        int tam = dl / 2;
        int xPoints[] = new int[tam];
        int yPoints[] = new int[tam];
        try {
            int y = 0;
            for (int i = 0; i < tam; i++) {
                xPoints[i] = Expr(dist[y++].trim());
                yPoints[i] = Expr(dist[y++].trim());
//                xPoints[i] = l + Integer.valueOf(dist[y++].trim());
//                yPoints[i] = t + Integer.valueOf(dist[y++].trim());
            }
        } catch (Exception x) {
            g.drawString("?", l, t);
            return false;
        }
        if (isFill()) {
            g.fillPolygon(xPoints, yPoints, tam);
        } else {
            g.drawPolygon(xPoints, yPoints, tam);
        }
        return true;
    }

    private int[] ArrayDePontos(String str) {
        String[] dist = str.split(",");
        int dl = dist.length;
        int[] res = new int[dl];
        try {
            for (int i = 0; i < dl; i++) {
                res[i] = Expr(dist[i].trim());
            }
        } catch (Exception x) {
            return new int[]{0, 0};
        }
        return res;
    }

    private int Expr(String trim) throws Exception {
        String conv = trim.replaceAll("[w,W]", Integer.toString(dono.getW()));
        conv = conv.replaceAll("[h,H]", Integer.toString(dono.getH()));
        if (!outroPintor) {
            conv = conv.replaceAll("[l,L]", Integer.toString(dono.getL()));
            conv = conv.replaceAll("[t,T]", Integer.toString(dono.getT()));
        } else if (getTipo() == tipoDrawer.tpMedida) {
            conv = conv.replaceAll("[l,L]", Integer.toString(getLeft()));
            conv = conv.replaceAll("[t,T]", Integer.toString(getTop()));
        } else {
            conv = conv.replaceAll("[l,L]", "0");
            conv = conv.replaceAll("[t,T]", "0");
        }
        int res = 0;
        try {
            res = Integer.valueOf(conv);
        } catch (NumberFormatException e) {
            ProcessadorExprSimples pEx = new ProcessadorExprSimples();
            if (pEx.IsMathExpr(conv)) {
                //try {
                return pEx.processaExprInt(conv);
                //} catch (Exception x) {
                //    throw x;
                //}
            }
            //throw e;
        }
        return res;
    }

    protected Color disabledColor = new Color(221, 221, 221);

    /**
     * Mostra os artefatos em cor padrão ao ser disabilitado na pintura.
     */
    private boolean disablePainted = false;

    public boolean isDisablePainted() {
        return disablePainted;
    }

    public void setDisablePainted(boolean disablePainted) {
        if (this.disablePainted == disablePainted) {
            return;
        }
        this.disablePainted = disablePainted;
    }
}
