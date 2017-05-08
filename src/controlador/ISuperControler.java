/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

/**
 *
 * @author ccandido
 * Interface para a janela principal do aplicativo
 */
public interface ISuperControler {
    public void Super_Esperando();
    public void Super_Pronto();
    public void DoComandoExterno(Controler.menuComandos c);
    //((FrameView)master.getFramePrincipal()).getFrame() //em uso!
}
