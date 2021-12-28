package hyperzebra.gui.button;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import hyperzebra.gui.ButtonGUI;
import hyperzebra.gui.PCARD;
import hyperzebra.object.OButton;
import hyperzebra.tool.AuthTool;
import hyperzebra.tool.ButtonTool;
import hyperzebra.tool.PaintTool;

public class RoundedCornerButton extends MyButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final float arcwidth = 16.0f;
	private static final float archeight = 16.0f;
	protected static final int focusstroke = 2;
	protected final Color fc = new Color(100, 150, 255, 200);
	// protected final Color ac = new Color(0,0,0);
	// protected final Color rc = Color.ORANGE;
	protected Shape shape;
	protected Shape border;
	protected Shape base;

	public RoundedCornerButton(OButton btn, String text) {
		super(btn, text);
		// setRolloverEnabled(true);
		setContentAreaFilled(false);
		setBackground(new Color(255, 255, 255));
		initShape();
	}

	protected void initShape() {
		if (!getBounds().equals(base)) {
			base = getBounds();
			shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arcwidth, archeight);
			border = new RoundRectangle2D.Float(focusstroke, focusstroke, getWidth() - 1 - focusstroke * 2,
					getHeight() - 1 - focusstroke * 2, arcwidth, archeight);
		}
	}
	/*
	 * private void paintFocusAndRollover(Graphics2D g2, Color color) {
	 * g2.setPaint(new GradientPaint(0, 0, color, getWidth()-1, getHeight()-1,
	 * color.brighter(), true)); g2.fill(shape); g2.setColor(getBackground());
	 * g2.fill(border); }
	 */

	@Override
	protected void paintComponent(Graphics g) {
		if (!isVisible() || PCARD.lockedScreen)
			return;
		if (PCARD.pc.bit > 1)
			return;
		if (PaintTool.editBackground && btnData.parent.objectType.equals("card"))
			return;
		if (btnData.card != PCARD.pc.stack.curCard)
			return;
		initShape();
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (getHilite()) {
			if (btnData.style != 1 && btnData.style != 7) { // 透明と楕円はXOR
				g2.setColor(Color.BLACK);
				g2.fill(shape);
			} else {
				g2.setXORMode(Color.BLACK);
				g2.fill(shape);
			}
			setForeground(btnData.bgColor);
			setBackground(btnData.color);
			// }else if(isRolloverEnabled() && getModel().isRollover()) {
			// paintFocusAndRollover(g2, rc);
			// }else if(hasFocus() && isFocusPainted()) {
			// paintFocusAndRollover(g2, fc);
		} else {
			if (btnData.style != 1 && btnData.style != 7) { // 透明と楕円は書かない
				g2.setColor(getBackground());
				g2.fill(shape);
			}
			setForeground(btnData.color);
			setBackground(btnData.bgColor);
		}
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setColor(getBackground());
		super.paintComponent(g2);
		if ((btnData.textStyle & 8) > 0) { // outline
			g2.setFont(new Font(btnData.textFont, Font.PLAIN, btnData.textSize));
			FontMetrics fo = g2.getFontMetrics();
			int w = fo.stringWidth(btnData.name);
			g2.setPaintMode();
			g2.setColor(Color.BLACK);
			g2.drawString(btnData.name, (btnData.width - w) / 2 - 1, (btnData.height + btnData.textSize) / 2 - 0);
			g2.drawString(btnData.name, (btnData.width - w) / 2 - 0, (btnData.height + btnData.textSize) / 2 - 1);
			g2.drawString(btnData.name, (btnData.width - w) / 2 + 1, (btnData.height + btnData.textSize) / 2 + 0);
			g2.drawString(btnData.name, (btnData.width - w) / 2 + 0, (btnData.height + btnData.textSize) / 2 + 1);
			g2.setColor(Color.WHITE);
			g2.drawString(btnData.name, (btnData.width - w) / 2, (btnData.height + btnData.textSize) / 2);
			g2.setColor(getBackground());
			return;
		}
		if ((btnData.textStyle & 16) > 0) { // shadow
			g2.setFont(new Font(btnData.textFont, Font.PLAIN, btnData.textSize));
			FontMetrics fo = g2.getFontMetrics();
			int w = fo.stringWidth(btnData.name);
			g2.setColor(Color.BLACK);
			g2.drawString(btnData.name, (btnData.width - w) / 2 + 0, (btnData.height + btnData.textSize) / 2 + 1);
			g2.drawString(btnData.name, (btnData.width - w) / 2 + 1, (btnData.height + btnData.textSize) / 2 + 0);
			g2.drawString(btnData.name, (btnData.width - w) / 2 + 1, (btnData.height + btnData.textSize) / 2 + 1);
			g2.setColor(Color.WHITE);
			g2.drawString(btnData.name, (btnData.width - w) / 2, (btnData.height + btnData.textSize) / 2);
			g2.setColor(getBackground());
			return;
		}
	}

	@Override
	protected void paintBorder(Graphics g) {
		if (!isVisible() || PCARD.lockedScreen)
			return;
		if (PCARD.pc.bit > 1)
			return;
		if (btnData.card != PCARD.pc.stack.curCard && btnData.card != PCARD.pc.stack.curCard.bg)
			return;
		if (isBorderPainted()) {
			initShape();
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(btnData.color);
			g2.draw(shape);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		if (AuthTool.tool != null && ButtonGUI.gui.target == this) {
			ButtonGUI.drawSelectBorder(this);
		} else if ((btnData.style == 1 || btnData.style == 2) && AuthTool.tool != null
				&& AuthTool.tool.getClass() == ButtonTool.class) {
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, btnData.width - 1, btnData.height - 1);
		}
	}

	@Override
	public boolean contains(int x, int y) {
		initShape();
		return shape.contains(x, y);
	}
}