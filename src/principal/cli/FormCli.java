/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package principal.cli;

import controlador.Diagrama;
import controlador.Editor;
import diagramas.eap.DiagramaEap;
import diagramas.eap.EapCLI;
import diagramas.eap.EapFormManual;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author ccandido
 */
public class FormCli extends javax.swing.JDialog {

    public FormCli(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        masterCli1.setJanela(this);
        masterCli1.setComponentPopupMenu(jPopupMenu1);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        popColar = new javax.swing.JMenuItem();
        popCancelar = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        popSair = new javax.swing.JMenuItem();
        jPanel2 = new javax.swing.JPanel();
        Scrooler = new javax.swing.JScrollPane();
        masterCli1 = new principal.cli.MasterCli();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuSair = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menuColar = new javax.swing.JMenuItem();
        menuCancelar = new javax.swing.JMenuItem();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("principal/Formularios_pt_BR"); // NOI18N
        popColar.setText(bundle.getString("FormCli.popColar.text")); // NOI18N
        popColar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popColarActionPerformed(evt);
            }
        });
        jPopupMenu1.add(popColar);

        popCancelar.setText(bundle.getString("FormCli.popCancelar.text")); // NOI18N
        popCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popCancelarActionPerformed(evt);
            }
        });
        jPopupMenu1.add(popCancelar);
        jPopupMenu1.add(jSeparator1);

        popSair.setText(bundle.getString("FormCli.popSair.text")); // NOI18N
        popSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popSairActionPerformed(evt);
            }
        });
        jPopupMenu1.add(popSair);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("FormCli.title")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );

        masterCli1.setPreferredSize(new java.awt.Dimension(600, 50));

        javax.swing.GroupLayout masterCli1Layout = new javax.swing.GroupLayout(masterCli1);
        masterCli1.setLayout(masterCli1Layout);
        masterCli1Layout.setHorizontalGroup(
            masterCli1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 657, Short.MAX_VALUE)
        );
        masterCli1Layout.setVerticalGroup(
            masterCli1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 284, Short.MAX_VALUE)
        );

        Scrooler.setViewportView(masterCli1);

        jMenu1.setText(bundle.getString("FormCli.jMenu1.text")); // NOI18N

        menuSair.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        menuSair.setText(bundle.getString("FormCli.menuSair.text")); // NOI18N
        menuSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSairActionPerformed(evt);
            }
        });
        jMenu1.add(menuSair);

        jMenuBar1.add(jMenu1);

        jMenu2.setText(bundle.getString("FormCli.jMenu2.text")); // NOI18N

        menuColar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        menuColar.setText(bundle.getString("FormCli.menuColar.text")); // NOI18N
        menuColar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuColarActionPerformed(evt);
            }
        });
        jMenu2.add(menuColar);

        menuCancelar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        menuCancelar.setText(bundle.getString("FormCli.menuCancelar.text")); // NOI18N
        menuCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCancelarActionPerformed(evt);
            }
        });
        jMenu2.add(menuCancelar);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Scrooler, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(Scrooler, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addGap(1, 1, 1)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuColarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuColarActionPerformed
        masterCli1.doPaste();
    }//GEN-LAST:event_menuColarActionPerformed

    private void menuSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSairActionPerformed
        menuCancelarActionPerformed(evt);
        setVisible(false);
    }//GEN-LAST:event_menuSairActionPerformed

    private void menuCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCancelarActionPerformed
        masterCli1.Cancelar();
    }//GEN-LAST:event_menuCancelarActionPerformed

    private void popColarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popColarActionPerformed
        menuColarActionPerformed(evt);
    }//GEN-LAST:event_popColarActionPerformed

    private void popCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popCancelarActionPerformed
        menuCancelarActionPerformed(evt);
    }//GEN-LAST:event_popCancelarActionPerformed

    private void popSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popSairActionPerformed
        menuSairActionPerformed(evt);
    }//GEN-LAST:event_popSairActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane Scrooler;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator1;
    private principal.cli.MasterCli masterCli1;
    private javax.swing.JMenuItem menuCancelar;
    private javax.swing.JMenuItem menuColar;
    private javax.swing.JMenuItem menuSair;
    private javax.swing.JMenuItem popCancelar;
    private javax.swing.JMenuItem popColar;
    private javax.swing.JMenuItem popSair;
    // End of variables declaration//GEN-END:variables

    public MasterCli getMasterCLI() {
        return masterCli1;
    }

    public void setProcessador(CliDiagramaProcessador cli) {
        masterCli1.setProcessador(cli);
    }

    public void SetDiagrama(Diagrama diagramaAtual) {
        if (diagramaAtual instanceof DiagramaEap) {
            EapCLI tmp = new EapCLI(getMasterCLI());
            setProcessador(tmp);
            JMenuItem menu = new JMenuItem(Editor.fromConfiguracao.getValor("Controler.cli.wizard.eap.menu"));
            jMenu2.insert(menu, 0);
            final FormCli fmc = this;
            menu.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EapFormManual fm = new EapFormManual(fmc, true);
                fm.SetEapCLI(tmp);
                fm.setLocationRelativeTo(fmc);
                fm.setVisible(true);
                if (fm.getResultado() == JOptionPane.OK_OPTION) {
                    masterCli1.doPaste(fm.getValor());
                }
            }
        });
        }
        masterCli1.getProcessador().setDiag(diagramaAtual);
    }
}
