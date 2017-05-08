/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;

/**
 *
 * @author Rick
 */
public class BaseControlador extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    public BaseControlador() {
        super();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() >= 2) {
            mouseDblClick(e);
        } else {
            mouseClick(e);
        }
    }

    public int ComponentIndex(Component comp)
    {
        Component[] cmp = getComponents();
        int i = -1;
        for (Component c: cmp)
        {
            i++;
            if (c == comp) return i;
        }
        return i;
    }

    public void mouseClick(MouseEvent e) {
    }

    public void mouseDblClick(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
    }
}
