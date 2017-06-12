/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.eap;

import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import desenho.formas.Forma;
import desenho.linhas.Linha;
import desenho.linhas.PontoDeLinha;
import desenho.preDiagrama.baseDrawerFromForma;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import util.DesenhadorDeTexto;

/**
 *
 * @author ccandido
 */
public class EapProcesso extends baseDrawerFromForma {

    private static final long serialVersionUID = 7455235988018549136L;

    public EapProcesso(Diagrama diagrama) {
        super(diagrama);
        Inicie();
    }

    private void Inicie() {
        setRoundrect(0);
        setGradiente(true);
        setDelimite(false);
        setGradienteStartColor(new Color(255, 255, 255, 255));
    }

    public EapProcesso(Diagrama diagrama, String texto) {
        super(diagrama, texto);
        Inicie();
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();
        InspectorProperty tmp = InspectorProperty.FindByProperty(res, "setTexto");
        tmp.ReSetCaptionFromConfig("nometexto");
        tmp.tipo = InspectorProperty.TipoDeProperty.tpTextoLongo;
        return res;
    }

    @Override
    public DesenhadorDeTexto getTextoFormatado() {
        DesenhadorDeTexto dz = super.getTextoFormatado(); 
        dz.setCentrarTextoVertical(true);
        dz.setCentrarTextoHorizontal(true);
        dz.CorretorPosicao = new Point(0, 0);
        return dz;
    }

    @Override
    public void PosicionePonto(PontoDeLinha ponto) {
        super.PosicionePonto(ponto);
        PontoDeLinha outraponta = ponto.getDono().getOutraPonta(ponto);
        if (outraponta.getEm() instanceof EapBarraLigacao) {
            outraponta.getEm().PosicionePonto(outraponta);
        }
    }

    @Override
    public boolean CanLiga(Forma forma, Linha lin) {
        if (super.CanLiga(forma, lin)) {
            return (forma == null || ((forma instanceof EapBarraLigacao) && (forma.CanLiga(this, lin))));
        }
        return false;
    }
    
    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorColor(doc, "BackColor", getBackColor()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Gradiente", isGradiente()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteStartColor", getGradienteStartColor()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteEndColor", getGradienteEndColor()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "GDirecao", getGDirecao()));
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        int l = util.XMLGenerate.getValorIntegerFrom(me, "GDirecao");
        if (l != -1) {
            setGDirecao(l);
        }
        Color c = util.XMLGenerate.getValorColorFrom(me, "BackColor");
        if (c != null) {
            setBackColor(c);
        }

        setGradiente(util.XMLGenerate.getValorBooleanFrom(me, "Gradiente"));
        c = util.XMLGenerate.getValorColorFrom(me, "GradienteStartColor");
        if (c != null) {
            setGradienteStartColor(c);
        }
        c = util.XMLGenerate.getValorColorFrom(me, "GradienteEndColor");
        if (c != null) {
            setGradienteEndColor(c);
        }
        return true;
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

    public EapBarraLigacao getOutraLigacao(EapBarraLigacao exceto) {
        ArrayList<Forma> lst = getListaDeFormasLigadas();
        lst.remove(exceto);
        if (lst.isEmpty()) return null;
        return (EapBarraLigacao)lst.get(0);
    }

    @Override
    public void DoPaint(Graphics2D g) {
        Composite bkp = g.getComposite();
        Paint p = g.getPaint();
        super.DoPaint(g);
        g.setPaint(p);
        g.setComposite(bkp);
    }
}
