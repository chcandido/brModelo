/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package desenho;

import java.util.EventObject;

/**
 *
 * @author Rick
 */
public class ElementarEvento extends EventObject{
    private final Elementar sender;
    private final int cod;
    private final Elementar destinator;
    private final Object msg;

    public ElementarEvento(Elementar sender, int cod)
    {
        super(sender);
        this.sender = sender;
        this.destinator = null;
        this.cod = cod;
        this.msg = null;
    }

    public ElementarEvento(Elementar sender, Object msg, int cod)
    {
        super(sender);
        this.sender = sender;
        this.destinator = null;
        this.cod = cod;
        this.msg = msg;
    }

    public ElementarEvento(Elementar sender, Elementar destinator, int cod)
    {
        super(sender);
        this.sender = sender;
        this.destinator = destinator;
        this.cod = cod;
        this.msg = null;
    }

    /**
     * @return the sender
     */
    public Elementar getSender() {
        return sender;
    }

    /**
     * @return the cod
     */
    public int getCod() {
        return cod;
    }

    /**
     * @return the destinator
     */
    public Elementar getDestinator() {
        return destinator;
    }

    public Object getMsg() {
        return msg;
    }
}
