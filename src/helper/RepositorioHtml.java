/*
 * Copyright (C) 2016 chcan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package helper;

import principal.Aplicacao;

/**
 *
 * @author chcan
 */
public class RepositorioHtml {
    public static String generateBaseHtml(String empty) {
        String res = "<html>\n"
                   + "  <head>\n"
                   + "    <title>Ajuda</title>\n"
                   + "  </head>\n"
                   + "<body>\n";
        res += empty + "</body>\n"
                + "</html>";
        return res;
    }
    public static String getVersao() {
        return Aplicacao.VERSAO_A + "." + Aplicacao.VERSAO_A + "." + Aplicacao.VERSAO_A;
    }
    public static String generateSobre() {
        String res = "<html>\n<head>\n<title>Ajuda</title>\n</head>\n<body>\n";
                res += "<center><h1>BrModelo " + getVersao() + " </h1>\nCarlos Henrique Cândido<br>\nOrientador: Dr. Ronaldo dos Santos Mello<br>\n" + Aplicacao.VERSAO_DATA + "<br/><br/>"
                        + "Baixe o arquivo de ajuda em http://www.sis4.com/brModelo/Ajuda.html</center>\n</body>\n</html>";
        return res;
    }

    public static String generateHtml() {
        String res = "<html>\n"
                   + "  <head>\n"
                   + "    <title>Ajuda</title>\n";
        res +=       "  </head>\n"
                   + "<body>\n";
        res +=     "<!--Edit here-->\n"
                 + "</body>\n"
                + "</html>";
        return res;
    }

//    public static String generateImg(String img, int w, int h){
//        String res = "<center>\n<img style=\"width:" + String.valueOf(w) + "px;height:" + String.valueOf(h) + "px;\" src=\"data:image/png;base64,"
//                + img + "\"/>\n</center>";
//        return res;
//    }
//    
//    public static String InserirImagem(String html, String img, int pEdt, int w, int h) {
//        if (html == null || html.isEmpty()) {
//            html = generateHtml();
//        }
//        html = html.substring(0, pEdt) + generateImg(img, w, h) + html.substring(pEdt);
//        return html;
//    }
}