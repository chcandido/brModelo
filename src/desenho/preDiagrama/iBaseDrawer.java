/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.preDiagrama;

import java.util.ArrayList;

/**
 *
 * @author ccandido
 */
public interface iBaseDrawer {

    baseDrawerItem AddItem();

    String FormateUnidadeMedida(int valor);

    int getH();

    ArrayList<baseDrawerItem> getItens();

    int getL();

    int getT();

    int getW();
    
    void InvalidateArea();
    
    void DoMuda();
}
