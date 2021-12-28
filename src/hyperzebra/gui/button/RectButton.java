package hyperzebra.gui.button;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import hyperzebra.gui.PCARD;
import hyperzebra.object.OButton;
import hyperzebra.tool.PaintTool;

public class RectButton extends RoundedCornerButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RectButton(OButton btn, String text) {
		super(btn, text);
		setFocusPainted(false);
		initShape();
	}

	protected void initShape() {
		if (!getBounds().equals(base)) {
			base = getBounds();
			shape = new Rectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1);
			border = new Rectangle2D.Float(focusstroke, focusstroke, getWidth() - 1 - focusstroke * 2,
					getHeight() - 1 - focusstroke * 2);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (!isVisible())
			return;
		if (PCARD.pc.stack.curCard == null
				|| btnData.card != PCARD.pc.stack.curCard && btnData.card != PCARD.pc.stack.curCard.bg)
			return;
		if (PaintTool.editBackground && btnData.parent.objectType.equals("card"))
			return;
		if (PCARD.pc.bit > 1)
			return;
		Graphics paneg = PCARD.pc.mainPane.getGraphics();
		if (PCARD.lockedScreen && paneg == g)
			return;
		Graphics2D g2 = (Graphics2D) g;
		// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_OFF);
		if (btnData.style == 1) { // 透明
			if (autoHilite && getHilite()) {
				initShape();
				g2.setXORMode(Color.WHITE);
				g2.fill(shape);
			} else {
				// g2.setXORMode(Color.WHITE);
			}
		}
		if (btnData.style == 2) { // 不透明
			initShape();
			if (autoHilite && getHilite()) {
				g2.setXORMode(Color.WHITE);
				g2.fill(shape);
				g2.setColor(getBackground());
			} else {
				g2.setColor(getBackground());
				g2.fill(shape);
			}
		}
		if (btnData.style == 3 || btnData.style == 4) { // 長方形 シャドウ
			initShape();
			if (getHilite()) {
				// g2.setColor(Color.WHITE);
				// g2.fill(shape);
				// g2.setColor(getBackground());
			} else {
				// g2.setColor(getBackground());
				// g2.fill(shape);
			}
		}
		// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_OFF);
		//// g2.setColor(getBackground());
		super.paintComponent(g2);
	}

	@Override
	protected void paintBorder(Graphics g) {
		if (!isVisible() || PCARD.lockedScreen)
			return;
		if (PCARD.pc.bit > 1)
			return;
		if (PaintTool.editBackground && btnData.parent.objectType.equals("card"))
			return;
		if (PCARD.pc.stack.curCard == null
				|| btnData.card != PCARD.pc.stack.curCard && btnData.card != PCARD.pc.stack.curCard.bg)
			return;
		if (btnData.style == 4) {
			g.setColor(btnData.color);
			Rectangle b = getBounds();
			g.drawLine(0, 0, b.width - 2, 0);
			g.drawLine(0, 0, 0, b.height - 2);
			g.drawLine(b.width - 1, 2, b.width - 1, b.height);
			g.drawLine(b.width - 2, 1, b.width - 2, b.height);
			g.drawLine(2, b.height - 1, b.width, b.height - 1);
			g.drawLine(1, b.height - 2, b.width, b.height - 2);
		} else {
			super.paintBorder(g);
		}
	}
}