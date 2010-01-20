/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ScrollersPanel.java
 *
 * Created on 2010-01-16, 19:59:54
 */
package brick.gui;

import brick.image.TransformsChangeNotifyer;

/**
 *
 * @author Szymon
 */
public class ScrollersPanel extends javax.swing.JPanel {

	/** Creates new form ScrollersPanel */
	public ScrollersPanel() {
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        scaleScrollBar = new javax.swing.JScrollBar();
        angleScrollBar = new javax.swing.JScrollBar();
        positionScrollBar = new javax.swing.JScrollBar();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        scaleResetButton = new javax.swing.JButton();
        angleResetButton = new javax.swing.JButton();
        positionResetButton = new javax.swing.JButton();

        setBorder(null);
        setLayout(new java.awt.GridBagLayout());

        scaleScrollBar.setMaximum(1000);
        scaleScrollBar.setMinimum(-1000);
        scaleScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                anyScrollBarMoved(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipady = 43;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(scaleScrollBar, gridBagConstraints);

        angleScrollBar.setMaximum(180);
        angleScrollBar.setMinimum(-180);
        angleScrollBar.setVisibleAmount(1);
        angleScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                anyScrollBarMoved(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipady = 43;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(angleScrollBar, gridBagConstraints);

        positionScrollBar.setMaximum(1000);
        positionScrollBar.setMinimum(-1000);
        positionScrollBar.setVisibleAmount(1);
        positionScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                anyScrollBarMoved(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipady = 43;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(positionScrollBar, gridBagConstraints);

        jLabel1.setText("Skala");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        add(jLabel1, gridBagConstraints);

        jLabel2.setText("Obrót");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        add(jLabel2, gridBagConstraints);

        jLabel3.setText("Przes.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 3);
        add(jLabel3, gridBagConstraints);

        scaleResetButton.setText("1:1");
        scaleResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleResetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        add(scaleResetButton, gridBagConstraints);

        angleResetButton.setText("0");
        angleResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                angleResetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        add(angleResetButton, gridBagConstraints);

        positionResetButton.setText("0");
        positionResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                positionResetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        add(positionResetButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

	private void angleResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_angleResetButtonActionPerformed
		angleScrollBar.setValue(0);
	}//GEN-LAST:event_angleResetButtonActionPerformed

	private void scaleResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleResetButtonActionPerformed
		scaleScrollBar.setValue(0);
	}//GEN-LAST:event_scaleResetButtonActionPerformed

	private void positionResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_positionResetButtonActionPerformed
		positionScrollBar.setValue(0);
	}//GEN-LAST:event_positionResetButtonActionPerformed

	private void anyScrollBarMoved(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_anyScrollBarMoved
		if(abstractBrick != null) {
			abstractBrick.setAngle(axis, angleScrollBar.getValue());
			double scale = scaleScrollBar.getValue();
			if(scale < 0) {
				scale = 100d/(-scale + 100);
			} else if (scale == 0) {
				scale = 1;
			}else {
				scale = (scale + 100) / 100;
			}
			abstractBrick.setScale(axis, scale);
			abstractBrick.setTransform(axis, (double) positionScrollBar.getValue());
			abstractBrick.recalc();
			if(renderPanel != null) {
				renderPanel.repaint();
			}
		}
	}//GEN-LAST:event_anyScrollBarMoved

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton angleResetButton;
    private javax.swing.JScrollBar angleScrollBar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton positionResetButton;
    private javax.swing.JScrollBar positionScrollBar;
    private javax.swing.JButton scaleResetButton;
    private javax.swing.JScrollBar scaleScrollBar;
    // End of variables declaration//GEN-END:variables
	protected String title = "ASDF";
	protected int axis = TransformsChangeNotifyer.BAD;
	protected TransformsChangeNotifyer abstractBrick = null;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get the value of axis
	 *
	 * @return the value of axis
	 */
	public int getAxis() {
		return axis;
	}

	/**
	 * Set the value of axis
	 *
	 * @param axis new value of axis, one of the TransformChangeNotyfiyer.{X,Y,Z}
	 */
	public void setAxis(int axis) {
		this.axis = axis;
	}

	public TransformsChangeNotifyer getAbstractBrick() {
		return abstractBrick;
	}

	public void setAbstractBrick(TransformsChangeNotifyer abstractBrick) {
		this.abstractBrick = abstractBrick;
	}

	protected RenderPanel renderPanel = null;

	public RenderPanel getRenderPanel() {
		return renderPanel;
	}

	public void setRenderPanel(RenderPanel renderPanel) {
		this.renderPanel = renderPanel;
	}

}
