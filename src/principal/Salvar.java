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
package principal;

import controlador.Editor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 *
 * @author SAA
 */
public class Salvar extends javax.swing.JDialog {

    /**
     * Creates new form Salvar
     *
     * @param parent
     * @param modal
     */
    public Salvar(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        getRootPane().registerKeyboardAction(e -> {
            //this.dispose();
            resultado = JOptionPane.CANCEL_OPTION;
            setVisible(false);

        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> {
            resultado = JOptionPane.OK_OPTION;
            setVisible(false);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().setDefaultButton(btnContinuar);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnContinuar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnSelAll = new javax.swing.JButton();
        btnUnSelAll = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        Lista = new javax.swing.JPanel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("principal/Formularios_pt_BR"); // NOI18N
        setTitle(bundle.getString("Salvar.title")); // NOI18N

        btnContinuar.setText(bundle.getString("Salvar.btnContinuar.text")); // NOI18N
        btnContinuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinuarActionPerformed(evt);
            }
        });

        btnCancelar.setText(bundle.getString("Salvar.btnCancelar.text")); // NOI18N
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnSelAll.setText(bundle.getString("Salvar.btnSelAll.text")); // NOI18N
        btnSelAll.setToolTipText(bundle.getString("Salvar.btnSelAll.toolTipText")); // NOI18N
        btnSelAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelAllActionPerformed(evt);
            }
        });

        btnUnSelAll.setText(bundle.getString("Salvar.btnUnSelAll.text")); // NOI18N
        btnUnSelAll.setActionCommand(bundle.getString("Salvar.btnUnSelAll.actionCommand")); // NOI18N
        btnUnSelAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnSelAllActionPerformed(evt);
            }
        });

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        Lista.setLayout(new javax.swing.BoxLayout(Lista, javax.swing.BoxLayout.PAGE_AXIS));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Lista, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Lista, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(btnSelAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUnSelAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(btnContinuar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar)
                .addGap(2, 2, 2))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(140, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnContinuar)
                    .addComponent(btnCancelar)
                    .addComponent(btnSelAll)
                    .addComponent(btnUnSelAll))
                .addGap(4, 4, 4))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jScrollPane2)
                    .addGap(35, 35, 35)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    int resultado = JOptionPane.CANCEL_OPTION;
    private void btnContinuarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinuarActionPerformed
        resultado = JOptionPane.OK_OPTION;
        setVisible(false);
    }//GEN-LAST:event_btnContinuarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        resultado = JOptionPane.CANCEL_OPTION;
        setVisible(false);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnSelAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelAllActionPerformed
        cheks.stream().filter(c -> c.isEnabled()).forEach(c -> c.setSelected(true));
        btnSelAll.setEnabled(false);
        btnUnSelAll.setEnabled(cheks.stream().anyMatch(c -> c.isEnabled() && c.isSelected()));
    }//GEN-LAST:event_btnSelAllActionPerformed

    private void btnUnSelAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnSelAllActionPerformed
        cheks.stream().filter(c -> c.isEnabled()).forEach(c -> c.setSelected(false));
        btnSelAll.setEnabled(cheks.stream().anyMatch(c -> c.isEnabled() && (!c.isSelected())));
        btnUnSelAll.setEnabled(false);
    }//GEN-LAST:event_btnUnSelAllActionPerformed

    public int getResultado() {
        return resultado;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Lista;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnContinuar;
    private javax.swing.JButton btnSelAll;
    private javax.swing.JButton btnUnSelAll;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

    private final ArrayList<JCheckBox> cheks = new ArrayList<>();

    public ArrayList<JCheckBox> getCheks() {
        return cheks;
    }

    public void Carregue(Editor Manager) {
        ActionListener actionListener = (ActionEvent ae) -> {
            btnSelAll.setEnabled(cheks.stream().anyMatch(c -> c.isEnabled() && (!c.isSelected())));
            btnUnSelAll.setEnabled(cheks.stream().anyMatch(c -> c.isEnabled() && c.isSelected()));
        };
        Manager.getDiagramas().stream().forEach((d) -> {
            JCheckBox c = new JCheckBox();
            c.setText(String.valueOf(cheks.size() + 1) + " - " + d.getNomeFormatado());
            c.setToolTipText(c.getText());
            c.setEnabled(d.getMudou());
            if (c.isEnabled()) {
                c.setSelected(true);
            }
            Lista.add(c);
            cheks.add(c);
            c.addActionListener(actionListener);
        });
        btnSelAll.setEnabled(cheks.stream().anyMatch(c -> c.isEnabled() && (!c.isSelected())));
        btnUnSelAll.setEnabled(cheks.stream().anyMatch(c -> c.isEnabled() && c.isSelected()));
    }

}
