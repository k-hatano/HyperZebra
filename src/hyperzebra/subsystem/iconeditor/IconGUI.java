package hyperzebra.subsystem.iconeditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

import hyperzebra.gui.GUI;
import hyperzebra.gui.PCARD;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.gui.TBCursor;
import hyperzebra.tool.BrushTool;
import hyperzebra.tool.EraserTool;
import hyperzebra.tool.LineTool;
import hyperzebra.tool.OvalTool;
import hyperzebra.tool.PaintBucketTool;
import hyperzebra.tool.PaintTool;
import hyperzebra.tool.PencilTool;
import hyperzebra.tool.RectTool;
import hyperzebra.tool.SelectTool;
import hyperzebra.tool.SmartSelectTool;
import hyperzebra.tool.TypeTool;

public class IconGUI implements MouseListener, MouseMotionListener {
	IconEditor owner;
	int clickH;
	int clickV;
	boolean right = false;

	public IconGUI(IconEditor owner) {
		this.owner = owner;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		right = (javax.swing.SwingUtilities.isRightMouseButton(e));
		int x = e.getX();
		int y = e.getY();
		PaintTool.mouseDown(x, y);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		PaintTool.mouseUp(x, y);
		PaintTool.lastTime = 0;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (owner.textfields == null)
			return;

		for (int i = 0; i < owner.textfields.length; i++) {
			if (owner.textfields[i] != null) {
				owner.textfields[i].setFocusable(false);
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		PaintTool.mouseStillDown(x, y);
		PaintTool.lastTime = System.currentTimeMillis();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		PaintTool.mouseWithin(x, y);
		PaintTool.lastTime = System.currentTimeMillis();
	}
}

class IEKeyListener implements KeyListener {

	@Override
	public void keyPressed(KeyEvent e) {
		GUI.gui.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		GUI.gui.keyReleased(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		GUI.gui.keyTyped(e);
	}
}

class IEActionListener implements ActionListener {
	IconEditor owner;

	IEActionListener(IconEditor frame) {
		owner = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String in_cmd = ((JComponent) e.getSource()).getName();
		String cmd = PCARD.pc.intl.getToolText(in_cmd);
		ChangeTool(cmd);
	}

	public boolean ChangeTool(String cmd) {
		// 前のツールの終了処理
		if (owner.tool != null) {
			owner.tool.end();
		}

		if (PCARD.pc.intl.getToolText("Select").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Select").equalsIgnoreCase(cmd)) {
			owner.tool = new SelectTool();
			TBCursor.changeCursor(owner);
		} else if (PCARD.pc.intl.getToolText("Lasso").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Lasso").equalsIgnoreCase(cmd)) {
			owner.tool = new LineTool();
			TBCursor.changeCursor(owner);
		} else if (PCARD.pc.intl.getToolText("MagicWand").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("MagicWand").equalsIgnoreCase(cmd)) {
			owner.tool = new SmartSelectTool();
			TBCursor.changeCursor(owner);
		} else if (PCARDFrame.pc.intl.getToolText("Brush").equalsIgnoreCase(cmd)
				|| PCARDFrame.pc.intl.getToolEngText("Brush").equalsIgnoreCase(cmd)) {
			owner.tool = new BrushTool();
			TBCursor.changeCursor(owner);
		} else if (PCARD.pc.intl.getToolText("PaintBucket").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("PaintBucket").equalsIgnoreCase(cmd)) {
			owner.tool = new PaintBucketTool();
			TBCursor.changeCursor(owner);
		} else if (PCARD.pc.intl.getToolText("Pencil").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Pencil").equalsIgnoreCase(cmd)) {
			owner.tool = new PencilTool();
			TBCursor.changeCursor(owner);
		} else if (PCARD.pc.intl.getToolText("Eraser").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Eraser").equalsIgnoreCase(cmd)) {
			owner.tool = new EraserTool();
			TBCursor.changeCursor(owner);
		} else if (PCARD.pc.intl.getToolText("Line").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Line").equalsIgnoreCase(cmd)) {
			owner.tool = new LineTool();
			TBCursor.changeCursor(owner);
		} else if (PCARD.pc.intl.getToolText("Rect").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Rect").equalsIgnoreCase(cmd)) {
			owner.tool = new RectTool();
			TBCursor.changeCursor(owner);
		} else if (PCARD.pc.intl.getToolText("Oval").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Oval").equalsIgnoreCase(cmd)) {
			owner.tool = new OvalTool();
			TBCursor.changeCursor(owner);
		} else if (PCARD.pc.intl.getToolText("Type").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Type").equalsIgnoreCase(cmd)) {
			owner.tool = new TypeTool();
			TBCursor.changeCursor(owner);
		}
		/*
		 * else if(PCARD.pc.intl.getToolText("OK").equalsIgnoreCase(cmd) ||
		 * PCARD.pc.intl.getToolEngText("OK").equalsIgnoreCase(cmd)){ Rsrc.rsrcClass
		 * iconres = owner.rsrc.getResource(owner.rsrcid, "icon");
		 * 
		 * iconres.name = owner.namefld.getText(); int id = 0; try{ id =
		 * Integer.valueOf(owner.idfld.getText()); }catch(Exception e2){ } if(id!=0 &&
		 * iconres.id != id){ owner.rsrc.deleteResource("icon", iconres.id); iconres.id
		 * = id; } }
		 */
		else {
			return false;// 見つからない
		}

		return true;
	}
}
