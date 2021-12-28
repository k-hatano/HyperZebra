package hyperzebra.gui.field;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

public class MyLabel3 extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage bi;
	public float scale;

	public MyLabel3(String fname, BufferedImage bi) {
		super();
		this.bi = bi;
		this.scale = 1f;
		// this.setIcon(new ImageIcon(bi));
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (bi == null) {
			g.setColor(Color.WHITE);
			Rectangle r = g.getClipBounds();
			g.fillRect(r.x, r.y, r.width, r.height);
			return;
		}
		g.drawImage(bi, 0, 0, (int) (bi.getWidth() * scale), (int) (bi.getHeight() * scale), this);
	}
}