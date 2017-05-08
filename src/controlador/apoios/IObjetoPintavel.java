/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.apoios;

import java.awt.Graphics2D;

/**
 *
 * @author ccandido
 */
public interface IObjetoPintavel {
    void DoPaint(Graphics2D g);
    boolean isOutroPintor();
    void setOutroPintor(boolean op);
}
