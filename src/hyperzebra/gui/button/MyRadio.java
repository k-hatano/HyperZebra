package hyperzebra.gui.button;

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JRadioButton;

import hyperzebra.gui.ButtonGUI;
import hyperzebra.gui.PCARD;
import hyperzebra.object.OButton;
import hyperzebra.tool.AuthTool;
import hyperzebra.tool.PaintTool;

public class MyRadio extends JRadioButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public OButton btnData = null;

	public MyRadio(OButton btn, String text) {
		super(text);
		btnData = btn;
		setMargin(new Insets(0, 0, 0, 0));
		this.setDoubleBuffered(PCARD.useDoubleBuffer);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (!isVisible())
			return;
		if (btnData == null || PCARD.pc.stack.curCard == null
				|| btnData.card != PCARD.pc.stack.curCard && btnData.card != PCARD.pc.stack.curCard.bg)
			return;
		if (PCARD.pc.bit > 1)
			return;
		if (PaintTool.editBackground && btnData.parent.objectType.equals("card"))
			return;
		Graphics paneg = PCARD.pc.mainPane.getGraphics();
		if (PCARD.lockedScreen && paneg == g)
			return;
		super.paintComponent(g);
		if (AuthTool.tool != null && ButtonGUI.gui.target == this) {
			ButtonGUI.drawSelectBorder(this);
		}
	}
}
