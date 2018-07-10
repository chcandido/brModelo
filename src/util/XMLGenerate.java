/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import controlador.Diagrama;
import controlador.Editor;
import desenho.Elementar;
import desenho.FormaElementar;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author ccandido
 */
public class XMLGenerate {
    
    public XMLGenerate() {
        super();
    }
    
    public static Document GeraDocument() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            return document;
        } catch (ParserConfigurationException ex) {
            util.BrLogger.Logger("ERROR_XML_DOC", ex.getMessage());
        }
        return null;
    }
    
    public static Document LoadDocument(File arq) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(arq);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD_FILE", ex.getMessage());
        }
        return null;
    }
    
    public static Document LoadDocument(String inS) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(new InputSource(new StringReader(inS)));
            doc.getDocumentElement().normalize();
            return doc;
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD_FROM_STR", ex.getMessage());
        }
        return null;
    }
    
    public static StringWriter GeraXMLtoSaveFrom(Diagrama atual, boolean apenasSelecao) {
        Document doc = GeraDocument();
        Element rootElement = doc.createElement(Diagrama.nodePrincipal);
        rootElement.setAttribute("TIPO", atual.getTipo().name());
        rootElement.setAttribute("ID", String.valueOf(atual.getID()));
        rootElement.setAttribute("UniversalUnicID", atual.getUniversalUnicID());
        doc.appendChild(rootElement);
        if (apenasSelecao) {
            if (atual.getItensSelecionados().size() > 0) {
                rootElement.setAttribute("FIRST_SEL", String.valueOf(atual.getItensSelecionados().get(0).getID()));
            }
            //# 13/07/2017: inclusão do impedimento de gerar xml parcial com cor disabilitada.
            ArrayList<Elementar> sel_dis = new ArrayList<>();
            atual.getItensSelecionados().stream().filter(item -> item.isDisablePainted()).forEach(item -> {
                item.setDisablePainted(false);
                sel_dis.add(item);
            });
            CarregueItens(doc, rootElement, atual.getItensSelecionados());
            sel_dis.stream().forEach(item -> item.setDisablePainted(true));
        } else {
            CarregueItens(doc, rootElement, atual.getListaDeItens());
        }
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter out = new StringWriter();
            StreamResult result = new StreamResult(out);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            return out;
        } catch (TransformerException ex) {
            util.BrLogger.Logger("ERROR_XML_DOC", ex.getMessage());
        }
        
        return null;
    }
    
    public static String GeraXMLFrom(Diagrama atual, boolean apenasSelecao) {
        StringWriter res = GeraXMLtoSaveFrom(atual, apenasSelecao);
        if (res != null) {
            return res.getBuffer().toString();
        } else {
            return "";
        }
    }
    
    public static void CarregueItens(Document doc, Element root, ArrayList<FormaElementar> itens) {
        for (FormaElementar it : itens) {
            //não serializa cardinalidade.
            if (!it.getIsLoadedFromXML()) {
                continue;
            }
            try {
                it.ToXlm(doc, root);
            } catch (Exception e) {
                util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD_XML_ITENS",
                        Editor.getClassTexto(it)
                        + " (ID: " + String.valueOf(it.getID()) + ") ",
                        e.getMessage());
            }
        }
    }
    
    public static Element ValorInteger(Document doc, String prop, int valor) {
        Element res = doc.createElement(prop);
        res.setAttribute("Valor", Integer.toString(valor));
        return res;
    }
    
    public static Element ValorColor(Document doc, String prop, Color valor) {
        return ValorString(doc, prop, util.Utilidades.ColorToString(valor));
    }
    
    public static Element ValorLegenda(Document doc, String leg, String legN, Color valor, String valorN, int tag, String tagnN) {
        Element res = doc.createElement("ItemLegenda");
        res.setAttribute(legN, leg);
        res.setAttribute(valorN, util.Utilidades.ColorToString(valor));
        res.setAttribute(tagnN, Integer.toString(tag));
        return res;
    }
    
    public static Element ValorBoolean(Document doc, String prop, boolean valor) {
        Element res = doc.createElement(prop);
        res.setAttribute("Valor", Boolean.toString(valor));
        return res;
    }
    
    public static Element ValorPoint(Document doc, String prop, Point valor) {
        Element res = doc.createElement(prop);
        res.setAttribute("Left", Integer.toString(valor.x));
        res.setAttribute("Top", Integer.toString(valor.y));
        return res;
    }
    
    public static Element ValorRect(Document doc, String prop, Rectangle valor) {
        Element res = doc.createElement(prop);
        res.setAttribute("Left", Integer.toString(valor.x));
        res.setAttribute("Top", Integer.toString(valor.y));
        res.setAttribute("Width", Integer.toString(valor.width));
        res.setAttribute("Height", Integer.toString(valor.height));
        return res;
    }
    
    public static Element ValorString(Document doc, String prop, String valor) {
        Element res = doc.createElement(prop);
        res.setAttribute("Valor", valor);
        return res;
    }
    
    public static Element ValorRefFormElementar(Document doc, String prop, FormaElementar valor) {
        Element res = doc.createElement(prop);
        AtributoRefFormElementar(res, "ID", valor);
        return res;
    }
    
    public static void AtributoRefFormElementar(Element res, String attr, FormaElementar valor) {
        if (valor == null) {
            res.setAttribute(attr, "-1");
        } else {
            res.setAttribute(attr, String.valueOf(valor.getID()));
        }
    }

//    public static Element AtributosPonto(Element res, PontoDeLinha valor) {
//        res.setAttribute("Left", Integer.toString(valor.getLeft()));
//        res.setAttribute("Top", Integer.toString(valor.getTop()));
//        //res.setAttribute("IsTopOrBotton", Boolean.toString(valor.IsTopOrBotton)); // primeiro e o último.
//        AtributoRefFormElementar(res, "Em", valor.getEm());
//        return res;
//    }
//
    public static Element ValorText(Document doc, String prop, String valor) {
        Element res = doc.createElement(prop);
        res.setTextContent(valor);
        return res;
    }
    
    public static Element ValorFonte(Document doc, Font fonte) {
        Element res = doc.createElement("Fonte");
        res.setAttribute("Nome", fonte.getName());
        res.setAttribute("Estilo", String.valueOf(fonte.getStyle()));
        res.setAttribute("Tamanho", String.valueOf(fonte.getSize()));
        return res;
    }
    
    public static Element FindByNodeName(Element pai, String prop) {
        NodeList lst = pai.getElementsByTagName(prop);
//        if (lst.getLength() == 0) {
//            return null;
//        }
        //não quero aqueles que pertençam a subitens,
        for (int i = 0; i < lst.getLength(); i++) {
            Element e = (Element) lst.item(i);
            if (e.getParentNode() == pai) {
                return e;
            }
        }
//        return (Element) lst.item(0);
        return null;
    }
    
    public static Font getValorFonte(Element pai) {
        Element res = FindByNodeName(pai, "Fonte");
        if (res != null) {
            String fn = res.getAttribute("Nome");
            int st = Integer.valueOf(res.getAttribute("Estilo"));
            int tam = Integer.valueOf(res.getAttribute("Tamanho"));
            return new Font(fn, st, tam);
        }
        return null;
    }
    
    public static Rectangle getValorRectFrom(Element pai, String prop) {
        Element res = FindByNodeName(pai, prop);
        if (res == null) {
            return null;
        }
        int l = Integer.valueOf(res.getAttribute("Left"));
        int t = Integer.valueOf(res.getAttribute("Top"));
        int w = Integer.valueOf(res.getAttribute("Width"));
        int h = Integer.valueOf(res.getAttribute("Height"));
        return new Rectangle(l, t, w, h);
    }
    
    public static Color getValorColorFrom(Element pai, String prop) {
        Element ac = FindByNodeName(pai, prop);
        if (ac == null) {
            return null;
        }
        String tmp = GetValorString(ac);
        return util.Utilidades.StringToColor(tmp);// new Color(Integer.valueOf(tmp));
    }
    
    public static String getValorStringFrom(Element pai, String prop) {
        Element ac = FindByNodeName(pai, prop);
        if (ac == null) {
            return null;
        }
        return GetValorString(ac);
    }
    
    public static String getValorTextoFrom(Element pai, String prop) {
        Element ac = FindByNodeName(pai, prop);
        if (ac == null) {
            return null;
        }
        return ac.getTextContent();
    }
    
    public static String GetValorString(Element pr) {
        return pr.getAttribute("Valor");
    }
    
    public static String GetValorString(Element pr, String ppr) {
        return pr.getAttribute(ppr);
    }
    
    public static boolean getValorBooleanFrom(Element pai, String prop) {
        Element ac = FindByNodeName(pai, prop);
        if (ac == null) {
            return false;
        }
        String tmp = GetValorString(ac);
        return Boolean.parseBoolean(tmp);
    }
    
    public static int getValorIntegerFrom(Element pai, String prop) {
        Element ac = FindByNodeName(pai, prop);
        if (ac == null) {
            return -1;
        }
        String tmp = GetValorString(ac);
        return Integer.valueOf(tmp);
    }
    
    public static Point getValorPointFrom(Element pai, String prop) {
        Element pr = FindByNodeName(pai, prop);
        int x = Integer.valueOf(pr.getAttribute("Left"));
        int y = Integer.valueOf(pr.getAttribute("Top"));
        return new Point(x, y);
    }
    
    public static Point getValorPoint(Element pr) {
        int x = Integer.valueOf(pr.getAttribute("Left"));
        int y = Integer.valueOf(pr.getAttribute("Top"));
        return new Point(x, y);
    }

    /**
     * Dado um ID, procura no mapa qual formElementar foi originado deste ID. No caso de "colando" o ID do FormElementar será diferente. Isso não importa!
     *
     * @param oID
     * @param mapa
     * @return
     */
    public static FormaElementar FindWhoHasID(String oID, HashMap<Element, FormaElementar> mapa) {
        if ("".equals(oID) || "-1".equals(oID)) {
            return null;
        }
        for (Element x : mapa.keySet()) {
            String tmp = x.getAttribute("ID");
            if (tmp.equals(oID)) {
                return mapa.get(x);
            }
        }
        for (Element x : mapa.keySet()) {
            
            NodeList nodeLst = x.getChildNodes();
            
            int i = -1;
            for (int s = 0; s < nodeLst.getLength(); s++) {
                Node fstNode = nodeLst.item(s);
                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) fstNode;
                    if (e.hasAttribute("ID")) {
                        i++;
                        String tmp = e.getAttribute("ID");
                        if (tmp.equals(oID)) {
                            return mapa.get(x).getSub(i);
                        }
                    }
                }
            }
        }
        return null;
    }
}
