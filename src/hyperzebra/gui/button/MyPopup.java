package hyperzebra.gui.button;

import java.awt.Graphics;

import javax.swing.JComboBox;

import hyperzebra.gui.ButtonGUI;
import hyperzebra.gui.PCARD;
import hyperzebra.object.OButton;
import hyperzebra.tool.AuthTool;
import hyperzebra.tool.PaintTool;

public class MyPopup extends JComboBox {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public OButton btnData = null;

	public MyPopup(OButton btn, String[] text) {
		super(text);
		btnData = btn;
		this.setDoubleBuffered(PCARD.useDoubleBuffer);
	}

	boolean flag;

	@Override
	protected void paintComponent(Graphics g) {
		if (!isVisible())
			return;
		// if(btnData.card!=PCARD.pc.stack.curCard &&
		// btnData.card!=PCARD.pc.stack.curCard.bg) return;
		if (PCARD.pc.bit > 1)
			return;
		if (PaintTool.editBackground && btnData.parent.objectType.equals("card"))
			return;
		// Graphics paneg = PCARD.pc.mainPane.getGraphics();
		// if(PCARD.lockedScreen&&paneg==g) return;
		this.validate();
		super.paintComponent(g);
		if (AuthTool.tool != null && ButtonGUI.gui.target == this) {
			ButtonGUI.drawSelectBorder(this);
		}
	}
}