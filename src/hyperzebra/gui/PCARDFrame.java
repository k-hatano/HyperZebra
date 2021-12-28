package hyperzebra.gui;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import hyperzebra.gui.button.CPButton;
import hyperzebra.gui.button.GradButton;
import hyperzebra.gui.button.PatButton;
import hyperzebra.gui.button.TransButton;
import hyperzebra.object.OStack;
import hyperzebra.tool.PaintTool;
import hyperzebra.tool.toolInterface;

public class PCARDFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public static PCARD pc = null;
	public static boolean useGrid = true;
	public static int gridSize = 1;
	public MyPanel mainPane;
	public OStack stack;
	public static OStack home;
	public int bit = 1;
	public float bitLeft;
	public float bitTop;
	public toolInterface tool;
	public CPButton fore;
	public CPButton back;
	public GradButton grad;
	public PatButton pat;
	public TransButton trans;
	public boolean fill;
	public AffineTransform selectaf;
	public int blendMode;
	public int blendLevel = 100;

	// ペイント
	public BufferedImage mainImg;
	public BufferedImage bgImg;
	public BufferedImage undoBuf;
	public BufferedImage redoBuf;

	public void end() {
		if (mainImg != null)
			mainImg.flush();
		mainImg = null;
		if (bgImg != null)
			bgImg.flush();
		bgImg = null;
		if (undoBuf != null)
			undoBuf.flush();
		undoBuf = null;
		if (redoBuf != null)
			redoBuf.flush();
		redoBuf = null;
		pat = null;
		fore = null;
		back = null;
		grad = null;
		stack = null;
		mainPane = null;
		tool = null;
	}

	public BufferedImage getSurface() {
		if (PaintTool.editBackground)
			return bgImg;
		return mainImg;
	}

	public void setSurface(BufferedImage bi) {
		if (PaintTool.editBackground)
			bgImg = bi;
		mainImg = bi;
	}

	public void setNewBounds() {
		Rectangle r = pc.getBounds();
		if (pc.toolbar == null || pc.toolbar.tb == null)
			return;
		pc.setBounds(r.x, r.y, pc.stack.width + pc.toolbar.getTWidth(),
				pc.stack.height + pc.toolbar.getTHeight() + pc.getInsets().top + pc.getJMenuBar().getHeight());
		if (pc.stack != null && pc.toolbar != null && pc.stack.scroll != null) {
			pc.mainPane.setBounds(pc.toolbar.getTWidth() - pc.stack.scroll.x,
					pc.toolbar.getTHeight() - pc.stack.scroll.y, pc.stack.width, pc.stack.height);
		}
	}
}
