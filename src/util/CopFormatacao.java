/*
 * Copyright (C) 2014 SAA
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
package util;

import controlador.Controler;
import controlador.inspector.InspectorProperty;
import desenho.Elementar;
import desenho.FormaElementar;
import desenho.formas.Forma;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author SAA
 */
public class CopFormatacao {

    private ArrayList<InspectorProperty> copiado = null;
    private Font fonteCopiada = null;
    private Rectangle regiaoCopiada = new Rectangle();

    public void Copiar(FormaElementar ori) {
        copiado = Resuma(ori.GenerateFullProperty());
        fonteCopiada = Elementar.CloneFont(ori.getFont());
        //regiaoCopiada = ori.getBounds();
    }

    public void Colar(ArrayList<FormaElementar> lst) {
        lst.forEach(it -> {
            Colar(it);
        });
    }

    public void Ajustar(ArrayList<FormaElementar> lst, Controler.menuComandos mm, int dimh, int dimv) {
        regiaoCopiada = lst.get(0).getBounds();
        for (int i = 1; i < lst.size(); i++) {
            if (lst.get(i) instanceof Forma && ((Forma)lst.get(i)).isAlinhavel())
                Ajustar(lst.get(i), mm, dimh, dimv);
        }
    }

    public boolean isCopiado() {
        return copiado != null;
    }

    public void Clear() {
        fonteCopiada = null;
        copiado = null;
    }

    private final String[] pprts = new String[]{
        "setGradiente", "setGradienteStartColor", "setGradienteEndColor", "setGradientePinteDetalhe", "setGradienteCorDetalhe", "setGDirecao", "setForeColor", "setAlfa", "setDashed"
    };

    private ArrayList<InspectorProperty> Resuma(ArrayList<InspectorProperty> quem) {
        ArrayList<InspectorProperty> res = new ArrayList<>();
        ArrayList<String> tmp = new ArrayList<>();
        tmp.addAll(Arrays.asList(pprts));
        quem.stream().filter(it -> tmp.indexOf(it.property) > -1).forEach(it -> {
            res.add(it);
        });
        return res;
    }

    public void Colar(FormaElementar var) {
        if (copiado == null) {
            return;
        }
        if (var instanceof Forma) {
            if (((Forma) var).editFonte) {
                var.setFont(fonteCopiada);
            }
        }
        if (copiado.isEmpty()){
            return;
        }
        ArrayList<InspectorProperty> dest = Resuma(var.GenerateFullProperty());
        ArrayList<InspectorProperty> aColar = new ArrayList<>();

        ArrayList<String> tmp = new ArrayList<>();
        dest.forEach(it -> {
            tmp.add(it.property);
        });
        copiado.stream().filter(c -> tmp.indexOf(c.property) > -1).forEach(ac -> {
            aColar.add(ac);
        });

        aColar.forEach(ac -> {
            var.getMaster().ColeFormatacao(var, ac, ac.valor_string);
        });
    }

    public void Ajustar(FormaElementar var, Controler.menuComandos cmd, int dimH, int dimV) {

        ((Forma)var).HidePontos(true);
        switch (cmd) {
            case cmdDimPastLeft:
                int x = regiaoCopiada.x - var.getLeft();
                var.DoMove(x, 0);
                //var.setLeft(regiaoCopiada.x);
                break;
            case cmdDimPastTop:
                int y = regiaoCopiada.y - var.getTop();
                var.DoMove(0, y);
                //var.setTop(regiaoCopiada.y );
                break;
            case cmdDimPastRight:
                int r = regiaoCopiada.width + regiaoCopiada.x - var.getLeftWidth();
                var.DoMove(r, 0);
                //var.setLeft(regiaoCopiada.width + regiaoCopiada.x - var.getLeftWidth());
                break;

            case cmdDimPastBottom:
                int b = regiaoCopiada.height + regiaoCopiada.y - var.getTopHeight();
                //var.setTop(regiaoCopiada.height + regiaoCopiada.y - var.getTopHeight());
                var.DoMove(0, b);
                break;
            case cmdDimPastWidth:
                ((Forma)var).ReciveFormaResize(new Rectangle(0, 0, var.getWidth()- regiaoCopiada.width, 0));
                //((Forma)var).SendNotificacao(Constantes.Operacao.opResize);
                break;
            case cmdDimPastHeight:
                ((Forma)var).ReciveFormaResize(new Rectangle(0, 0, 0, var.getHeight() - regiaoCopiada.height));
                //((Forma)var).SendNotificacao(Constantes.Operacao.opResize);
                break;

            case cmdDimAlignH:
                int hh = (dimH + regiaoCopiada.x + regiaoCopiada.width) - var.getLeft();
                int yy = regiaoCopiada.y - var.getTop() + ((regiaoCopiada.height - var.getHeight())/2);
                var.DoMove(hh, yy);
                
                regiaoCopiada = new Rectangle(var.getBounds());
                break;
                
            case cmdDimAlignV:
                int vv = (dimV + regiaoCopiada.y + regiaoCopiada.height) - var.getTop();
                int xx = regiaoCopiada.x - var.getLeft() + ((regiaoCopiada.width - var.getWidth())/2);
                var.DoMove(xx, vv);
                
                regiaoCopiada = new Rectangle(var.getBounds());
                break;

        }
        var.Reposicione();
        var.Reenquadre();
        ((Forma)var).HidePontos(false);
    }

}
