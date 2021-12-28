package hyperzebra.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JLabel;

import hyperzebra.object.OCardBase;
import hyperzebra.tool.PaintTool;

public class MyLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static MyLabel cdlabel = null;
	static MyLabel bglabel = null;
	public OCardBase cd;

	public static MyLabel getMyCdLabel(OCardBase in_cd) {
		if (cdlabel == null)
			cdlabel = new MyLabel();
		cdlabel.cd = in_cd;
		cdlabel.setDoubleBuffered(PCARD.useDoubleBuffer);
		return cdlabel;
	}

	public static MyLabel getMyBgLabel(OCardBase in_cd) {
		if (bglabel == null)
			bglabel = new MyLabel();
		bglabel.cd = in_cd;
		bglabel.setDoubleBuffered(PCARD.useDoubleBuffer);
		return bglabel;
	}

	// @SuppressWarnings("restriction")
	@Override
	protected void paintComponent(Graphics g) {
		if (PCARD.pc.tool != null)
			return;
		if (cd.objectType.equals("card") && PaintTool.editBackground)
			return;
		// Graphics mainpaneg = PCARD.pc.mainPane.getGraphics();
		// Graphics paneg = PCARD.pc.getContentPane().getGraphics();
		// Field f = null;
		try {
			// f = sun.java2d.SunGraphics2D.class.getDeclaredField("surfaceData");
			// f.setAccessible(true);
		} catch (SecurityException e) {
			e.printStackTrace();
			// } catch (NoSuchFieldException e) {
			// e.printStackTrace();
		}
		try {
			// if(PCARD.lockedScreen&&(f.get(paneg)==f.get(g)||f.get(mainpaneg)==f.get(g)))
			// return;//g.drawImage(VEffect.oldoff,0,0,PCARD.pc.mainPane);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// } catch (IllegalAccessException e) {
			// e.printStackTrace();
		}

		if (!cd.showPict || cd.pict == null) {
			if (cd.objectType.equals("background")) {
				g.setColor(Color.WHITE);
				Rectangle r = g.getClipBounds();
				g.fillRect(r.x, r.y, r.width, r.height);
			}
			return;
		}
		if (cd.objectType.equals("card") && cd != PCARD.pc.stack.curCard)
			return;

		g.drawImage(cd.pict, 0, 0, PCARD.pc.mainPane);

		// addColor用
		/*
		 * if(PCARD.pc.stack.addColor != null){ Graphics2D g2 = (Graphics2D) g;
		 * g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		 * g2.drawImage(PCARD.pc.stack.addColor,0,0,null); }
		 */
	}
}
