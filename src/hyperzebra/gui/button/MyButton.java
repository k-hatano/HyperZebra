package hyperzebra.gui.button;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JButton;

import hyperzebra.gui.ButtonGUI;
import hyperzebra.gui.PCARD;
import hyperzebra.object.OButton;
import hyperzebra.tool.AuthTool;
import hyperzebra.tool.PaintTool;

public class MyButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean autoHilite = true;
	public boolean hilite = false;
	public OButton btnData = null;

	public MyButton(OButton btn, String text) {
		super(text);
		btnData = btn;
		this.setDoubleBuffered(PCARD.useDoubleBuffer);
	}

	public void setAutoHilite(boolean b) {
		autoHilite = b;
	}

	public void setHilite(boolean b) {
		hilite = b;
		btnData.check_hilite = b;
		getModel().setArmed(hilite);
	}

	public boolean getHilite() {
		if (autoHilite && PCARD.editMode == 0)
			return getModel().isArmed();
		return hilite;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (!isVisible() || PCARD.lockedScreen)
			return;
		if (PCARD.pc.bit > 1)
			return;
		if (PaintTool.editBackground && btnData.parent.objectType.equals("card"))
			return;
		if (PCARD.pc.stack.curCard == null
				|| btnData.card != PCARD.pc.stack.curCard && btnData.card != PCARD.pc.stack.curCard.bg)
			return;

		if (btnData.blendMode == 1) {
			((Graphics2D) g)
					.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, btnData.blendLevel / 100.0f));
		}

		if (autoHilite || hilite == getModel().isArmed()) {
			if (!btnData.showName && btnData.style >= 1 && btnData.style <= 3) {
				if (btnData.style >= 2) {
					g.setColor(btnData.bgColor);
					Rectangle r = g.getClipBounds();
					g.fillRect(r.x, r.y, r.width, r.height);
				}
				if (btnData.style == 3) {
					g.setColor(btnData.color);
					g.drawRect(btnData.left, btnData.top, btnData.width, btnData.height);
				}
				Icon ic = this.getIcon();
				if (ic != null) {
					if (btnData.getScaleIcon() && btnData.imageForScale != null) {
						g.drawImage(btnData.imageForScale, 0, 0, getWidth(), getHeight(), 0, 0,
								btnData.imageForScale.getWidth(), btnData.imageForScale.getHeight(), null);
					} else {
						int w = ic.getIconWidth();
						int h = ic.getIconHeight();
						ic.paintIcon(this, g, (btnData.width - w) / 2, (btnData.height - h) / 2);
					}
				} /*
					 * else{ BufferedImage img = btnData.iconImage; if(img!=null){
					 * //g2.setColor(getBackground()); g.drawImage(img,
					 * (btnData.width-img.getWidth())/2, (btnData.height-img.getHeight())/2, this);
					 * } }
					 */
			} else if (btnData.getScaleIcon() && btnData.imageForScale != null) {
				g.drawImage(btnData.imageForScale, 0, 0, getWidth(), getHeight(), 0, 0,
						btnData.imageForScale.getWidth(), btnData.imageForScale.getHeight(), null);
			} else {
				super.paintComponent(g);
			}
		} else if (btnData.getScaleIcon() && btnData.imageForScale != null) {
			g.drawImage(btnData.imageForScale, 0, 0, getWidth(), getHeight(), 0, 0, btnData.imageForScale.getWidth(),
					btnData.imageForScale.getHeight(), null);
		} else {
			getModel().setArmed(hilite);
			super.paintComponent(g);
		}
		if (AuthTool.tool != null && ButtonGUI.gui.target == this) {
			ButtonGUI.drawSelectBorder(this);
		}
	}
}
