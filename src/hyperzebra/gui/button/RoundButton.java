package hyperzebra.gui.button;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import hyperzebra.gui.ButtonGUI;
import hyperzebra.gui.PCARD;
import hyperzebra.object.OButton;
import hyperzebra.tool.AuthTool;
import hyperzebra.tool.PaintTool;

public class RoundButton extends RoundedCornerButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RoundButton(OButton btn, String text) {
		super(btn, text);
		setFocusPainted(false);
		initShape();
	}

	protected void initShape() {
		if (!getBounds().equals(base)) {
			base = getBounds();
			shape = new Ellipse2D.Float(0, 0, getWidth() - 1, getHeight() - 1);
			border = new Ellipse2D.Float(focusstroke, focusstroke, getWidth() - 1 - focusstroke * 2,
					getHeight() - 1 - focusstroke * 2);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (!isVisible())
			return;
		if (btnData.card != PCARD.pc.stack.curCard && btnData.card != PCARD.pc.stack.curCard.bg)
			return;
		if (PCARD.pc.bit > 1)
			return;
		if (PaintTool.editBackground && btnData.parent.objectType.equals("card"))
			return;
		Graphics paneg = PCARD.pc.mainPane.getGraphics();
		if (PCARD.lockedScreen && paneg == g)
			return;
		initShape();
		Graphics2D g2 = (Graphics2D) g;
		// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_OFF);
		if (getHilite()) {
			g.setXORMode(Color.WHITE);
			g2.fill(shape);
			g2.setColor(getBackground());
		} else {
		}
		// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_OFF);
		super.paintComponent(g2);

		// super.paintComponent(g2);
		if (AuthTool.tool != null && ButtonGUI.gui.target == this) {
			ButtonGUI.drawSelectBorder(this);
		}
	}

	@Override
	protected void paintBorder(Graphics g) {
	}
}
