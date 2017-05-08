/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package desenho;

import java.util.EventListener;

/**
 *
 * @author Rick
 */
public interface ElementarListener extends EventListener {
    public void SendNotificacao(int cod);
    public void ReciveNotificacao(ElementarEvento evt);
}
