/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principal;

import controlador.Diagrama;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author ccandido
 */
public class fmImpressao extends javax.swing.JDialog {

    private final java.awt.Frame frame;

    /**
     * Creates new form fmImpressa
     *
     * @param parent
     * @param modal
     */
    public fmImpressao(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        frame = parent;
        initComponents();
        prn = prnView.getImpressora();
        SpinnerNumberModel model = new SpinnerNumberModel(3, 1, 20, 1);
        spinC.setModel(model);
        model = new SpinnerNumberModel(2, 1, 20, 1);
        spinL.setModel(model);
    }

    public void setDiagrama(Diagrama d) {
        prnView.setDiagrama(d);
        AtualizePaginas();
    }

    util.PrintControler prn;

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        rdMostarAI = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        lblTlPg = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        rdProporcional = new javax.swing.JCheckBox();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        lblLinhas = new javax.swing.JLabel();
        spinL = new javax.swing.JSpinner();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        spinC = new javax.swing.JSpinner();
        jToolBar2 = new javax.swing.JToolBar();
        btnPreview = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnPrint = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnCfgImprimir = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        panBase = new javax.swing.JPanel();
        prnView = new controlador.Impressor();
        jPanel3 = new javax.swing.JPanel();
        btnSair = new javax.swing.JButton();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("principal/Formularios_pt_BR"); // NOI18N
        rdMostarAI.setText(bundle.getString("fmImpressao.rdMostarAI.text")); // NOI18N
        rdMostarAI.setFocusable(false);
        rdMostarAI.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        rdMostarAI.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(rdMostarAI);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 171, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 26, Short.MAX_VALUE)
        );

        jToolBar1.add(jPanel1);

        lblTlPg.setText(bundle.getString("fmImpressao.lblTlPg.text")); // NOI18N
        jToolBar1.add(lblTlPg);
        jToolBar1.add(jSeparator3);

        rdProporcional.setText(bundle.getString("fmImpressao.rdProporcional.text")); // NOI18N
        rdProporcional.setFocusable(false);
        rdProporcional.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        rdProporcional.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rdProporcional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdProporcionalActionPerformed(evt);
            }
        });
        jToolBar1.add(rdProporcional);
        jToolBar1.add(jSeparator5);

        lblLinhas.setText(bundle.getString("fmImpressao.lblLinhas.text")); // NOI18N
        jToolBar1.add(lblLinhas);
        lblLinhas.getAccessibleContext().setAccessibleName(bundle.getString("fmImpressao.lblLinhas.AccessibleContext.accessibleName")); // NOI18N

        spinL.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinLStateChanged(evt);
            }
        });
        jToolBar1.add(spinL);
        jToolBar1.add(jSeparator4);

        jLabel2.setText(bundle.getString("fmImpressao.jLabel2.text")); // NOI18N
        jToolBar1.add(jLabel2);
        jLabel2.getAccessibleContext().setAccessibleName(bundle.getString("fmImpressao.jLabel2.AccessibleContext.accessibleName")); // NOI18N

        spinC.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinCStateChanged(evt);
            }
        });
        jToolBar1.add(spinC);

        jToolBar2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        btnPreview.setText(bundle.getString("fmImpressao.btnPreview.text")); // NOI18N
        btnPreview.setFocusable(false);
        btnPreview.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPreview.setName(""); // NOI18N
        btnPreview.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviewActionPerformed(evt);
            }
        });
        jToolBar2.add(btnPreview);
        jToolBar2.add(jSeparator1);

        btnPrint.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnPrint.setText(bundle.getString("fmImpressao.btnPrint.text")); // NOI18N
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar2.add(btnPrint);
        jToolBar2.add(jSeparator2);

        btnCfgImprimir.setText(bundle.getString("fmImpressao.btnCfgImprimir.text")); // NOI18N
        btnCfgImprimir.setFocusable(false);
        btnCfgImprimir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCfgImprimir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCfgImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCfgImprimirActionPerformed(evt);
            }
        });
        jToolBar2.add(btnCfgImprimir);

        panBase.setBackground(new java.awt.Color(151, 151, 151));

        javax.swing.GroupLayout prnViewLayout = new javax.swing.GroupLayout(prnView);
        prnView.setLayout(prnViewLayout);
        prnViewLayout.setHorizontalGroup(
            prnViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 512, Short.MAX_VALUE)
        );
        prnViewLayout.setVerticalGroup(
            prnViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 512, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panBaseLayout = new javax.swing.GroupLayout(panBase);
        panBase.setLayout(panBaseLayout);
        panBaseLayout.setHorizontalGroup(
            panBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(prnView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panBaseLayout.setVerticalGroup(
            panBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(prnView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jScrollPane1.setViewportView(panBase);

        btnSair.setText(bundle.getString("fmImpressao.btnSair.text")); // NOI18N
        btnSair.setFocusable(false);
        btnSair.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSair.setMargin(new java.awt.Insets(2, 30, 2, 30));
        btnSair.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(508, Short.MAX_VALUE)
                .addComponent(btnSair)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(btnSair)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jScrollPane1))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                .addGap(36, 36, 36))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 398, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 402, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCfgImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCfgImprimirActionPerformed
        prnView.iniceImpressao();
        prn.printSetup();
        prnView.finalizeImpressao();
        prnView.CalculePagina();
        AtualizePaginas();
        prnView.repaint();
    }//GEN-LAST:event_btnCfgImprimirActionPerformed

// Se a configuração da página não estiver de acordo com a impressora da erro na impressão. Por essa razão, removo!
//    private void btnPageSetupActionPerformed(java.awt.event.ActionEvent evt) {                                             
//        prn.pageSetup();
//        prnView.CalculePagina();
//        AtualizePaginas();
//        prnView.repaint();
//    }                                            

    private boolean stopEv = false;
    private void spinCStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinCStateChanged
        if (!stopEv && spinC.getValue() != null) {
            int v;
            try {
                v = Integer.parseInt(spinC.getValue().toString());
                prnView.setColunas(v);
                AtualizePaginas();
            } finally {

            }
        }

    }//GEN-LAST:event_spinCStateChanged

    private void spinLStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinLStateChanged
        if (!stopEv && spinL.getValue() != null) {
            int v;
            try {
                v = Integer.parseInt(spinL.getValue().toString());
                prnView.setLinhas(v);
                AtualizePaginas();
            } finally {

            }
        }
    }//GEN-LAST:event_spinLStateChanged

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
        fmImpressaoPreview fm = new fmImpressaoPreview(this.frame, true);
        fm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fm.setLocationRelativeTo(this);
        fm.Inicie(prnView);
        fm.setVisible(true);
        prnView.finalizeImpressao();
    }//GEN-LAST:event_btnPreviewActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnSairActionPerformed

    private void rdProporcionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdProporcionalActionPerformed
        boolean s = !rdProporcional.isSelected();
        spinC.setEnabled(s);
        spinL.setEnabled(s);
        prnView.setNaoConsiderarLinhasColunas(!s);
        AtualizePaginas();
    }//GEN-LAST:event_rdProporcionalActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        prnView.iniceImpressao();
        prnView.Impressora.print();
        prnView.finalizeImpressao();
        
        prnView.CalculePagina();
        AtualizePaginas();
        prnView.repaint();

    }//GEN-LAST:event_btnPrintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCfgImprimir;
    private javax.swing.JButton btnPreview;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSair;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lblLinhas;
    private javax.swing.JLabel lblTlPg;
    private javax.swing.JPanel panBase;
    public controlador.Impressor prnView;
    public javax.swing.JCheckBox rdMostarAI;
    private javax.swing.JCheckBox rdProporcional;
    private javax.swing.JSpinner spinC;
    private javax.swing.JSpinner spinL;
    // End of variables declaration//GEN-END:variables

    private void AtualizePaginas() {
        stopEv = true;
        spinL.setValue(prnView.getLinhas());
        spinC.setValue(prnView.getColunas());
        stopEv = false;
        lblTlPg.setText(java.util.ResourceBundle.getBundle("principal/Formularios_pt_BR").getString("fmImpressao.str.tlpg") + " " + String.valueOf(prnView.getQtdPagina()) + " [" + String.valueOf(prnView.getLinhas() * prnView.getColunas()) + "]  ");
    }

    public Point getTamanhoAreaImpressao() {
        return prnView.getAreaImpressao();
    }

//    private void CarregarMargens() {
//        Rectangle mar = prnView.Impressora.getMargensMM();
//        txtTop.setText(String.valueOf(mar.y));
//        txtLeft.setText(String.valueOf(mar.x));
//        txtRigth.setText(String.valueOf(mar.width));
//        txtBotton.setText(String.valueOf(mar.height));
//        checkTxtEditor(txtTop, false);
//        checkTxtEditor(txtLeft, false);
//        checkTxtEditor(txtBotton, false);
//        checkTxtEditor(txtRigth, false);
//    }
//
//    private void checkTxtEditor(javax.swing.JTextField txt, boolean sn) {
//        if (sn) {
//            txt.setBackground(Color.yellow);
//        } else {
//            txt.setBackground(Color.white);
//        }
//    }
//
//    private void AlterarMargens() {
//        int left = 0, top = 0, ri = 0, bo = 0;
//        boolean err = false;
//        Integer r = convInt(txtTop.getText());
//        if (r == null) {
//            err = true;
//            checkTxtEditor(txtTop, true);
//        } else {
//            top = r.intValue();
//            checkTxtEditor(txtTop, false);
//        }
//        r = convInt(txtLeft.getText());
//        if (r == null) {
//            err = true;
//            checkTxtEditor(txtLeft, true);
//        } else {
//            left = r.intValue();
//            checkTxtEditor(txtLeft, false);
//        }
//        r = convInt(txtBotton.getText());
//        if (r == null) {
//            err = true;
//            checkTxtEditor(txtBotton, true);
//        } else {
//            bo = r.intValue();
//            checkTxtEditor(txtBotton, false);
//        }
//        r = convInt(txtRigth.getText());
//        if (r == null) {
//            err = true;
//            checkTxtEditor(txtRigth, true);
//        } else {
//            ri = r.intValue();
//            checkTxtEditor(txtRigth, false);
//        }
//        
//        if (!err) {
//            prnView.Impressora.setMargensMM(left, top, ri, bo);
//        }
//    }
//
//    private Integer convInt(String vl) {
//        try {
//            int res = Integer.parseInt(vl);
//            return new Integer(res);
//        } catch (NumberFormatException e) {
//            return null;
//        }
//    }
}
