package hyperzebra.gui.field;

import java.awt.Graphics;

import javax.swing.JScrollPane;

import hyperzebra.gui.FieldGUI;
import hyperzebra.gui.PCARD;
import hyperzebra.object.OField;
import hyperzebra.tool.AuthTool;
import hyperzebra.tool.PaintTool;

public class MyScrollPane extends JScrollPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public OField fldData = null;

	public MyScrollPane() {
		super();
		this.setDoubleBuffered(PCARD.useDoubleBuffer);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics paneg = PCARD.pc.mainPane.getGraphics();
		if (PCARD.pc.bit > 1)
			return;
		if (PaintTool.editBackground && fldData.parent.objectType.equals("card"))
			return;
		if (!isVisible() || fldData.card != PCARD.pc.stack.curCard || PCARD.lockedScreen && paneg == g) {
			/*
			 * for(int i=0; i<getComponentCount(); i++){ getComponent(i).setVisible(false);
			 * }
			 */
			return;
		}
		/*
		 * for(int i=0; i<getComponentCount(); i++){ if(getComponent(i).getClass() ==
		 * JScrollPane.ScrollBar.class) continue;
		 * //setVisible(true)をやったときに書き換え命令が発生するのでこっそりやりたい Class<JScrollPane.ScrollBar> c
		 * = JScrollPane.ScrollBar.class; Field m; try { m =
		 * c.getDeclaredField("visible"); m.setAccessible(true); m.set(getComponent(i),
		 * true); // .visible = true; } catch (Exception e) { e.printStackTrace(); } }
		 */
		super.paintComponent(g);
		if (AuthTool.tool != null && FieldGUI.gui.target == this) {
			FieldGUI.drawSelectBorder(this);
		}
	}
}