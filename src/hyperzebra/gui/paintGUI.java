package hyperzebra.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import hyperzebra.tool.PaintTool;

public class paintGUI implements MouseListener, MouseMotionListener/* , WindowListener */
{
	public static paintGUI gui = new paintGUI();
	static int clickH;
	static int clickV;
	public static boolean right = false;
	MyPaintDropListener droplistener = new MyPaintDropListener();
	DropTarget drop = new DropTarget();

	public void addListenerToParts() {
		for (int i = 0; i < PCARD.pc.stack.curCard.btnList.size(); i++) {
			PCARD.pc.stack.curCard.btnList.get(i).addListener(this);
			PCARD.pc.stack.curCard.btnList.get(i).addMotionListener(this);
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.bg.btnList.size(); i++) {
			PCARD.pc.stack.curCard.bg.btnList.get(i).addListener(this);
			PCARD.pc.stack.curCard.bg.btnList.get(i).addMotionListener(this);
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.fldList.size(); i++) {
			PCARD.pc.stack.curCard.fldList.get(i).addListener(this);
			PCARD.pc.stack.curCard.fldList.get(i).addMotionListener(this);
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.bg.fldList.size(); i++) {
			PCARD.pc.stack.curCard.bg.fldList.get(i).addListener(this);
			PCARD.pc.stack.curCard.bg.fldList.get(i).addMotionListener(this);
		}
		drop = new DropTarget(PCARD.pc.mainPane, droplistener);
	}

	public void removeListenerFromParts() {
		for (int i = 0; i < PCARD.pc.stack.curCard.btnList.size(); i++) {
			PCARD.pc.stack.curCard.btnList.get(i).removeListener(this);
			PCARD.pc.stack.curCard.btnList.get(i).removeMotionListener(this);
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.bg.btnList.size(); i++) {
			PCARD.pc.stack.curCard.bg.btnList.get(i).removeListener(this);
			PCARD.pc.stack.curCard.bg.btnList.get(i).removeMotionListener(this);
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.fldList.size(); i++) {
			PCARD.pc.stack.curCard.fldList.get(i).removeListener(this);
			PCARD.pc.stack.curCard.fldList.get(i).removeMotionListener(this);
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.bg.fldList.size(); i++) {
			PCARD.pc.stack.curCard.bg.fldList.get(i).removeListener(this);
			PCARD.pc.stack.curCard.bg.fldList.get(i).removeMotionListener(this);
		}
		drop.removeDropTargetListener(paintGUI.gui.droplistener);
		drop = null;
	}

	public void mouseClicked(MouseEvent e) {
		/*
		 * clickH = e.getX()-PPaint.ppaint.toolbar.tb.getWidth(); clickV =
		 * e.getY()-PPaint.ppaint.getInsets().top; PaintTool.mouseUp(clickH, clickV);
		 * PaintTool.lastTime = 0;
		 */
	}

	public void mousePressed(MouseEvent e) {
		// mouseDown = true;
		right = (javax.swing.SwingUtilities.isRightMouseButton(e));
		Rectangle r = ((Component) e.getSource()).getBounds();
		int x = e.getX() + r.x/*-PCARD.pc.toolbar.getTWidth()*/;
		int y = e.getY() + r.y/*-PCARD.pc.toolbar.getTHeight()*//*-PCARD.pc.getInsets().top*/;
		if ((Component) e.getSource() == PCARD.pc.getRootPane()) {
			x -= PCARD.pc.toolbar.getTWidth();
			y -= PCARD.pc.toolbar.getTHeight() + PCARD.pc.getInsets().top + PCARD.pc.getJMenuBar().getHeight();
		}
		PaintTool.mouseDown(x, y);
	}

	public void mouseReleased(MouseEvent e) {
		// mouseDown = false;
		Rectangle r = ((Component) e.getSource()).getBounds();
		int x = e.getX() + r.x/*-PCARD.pc.toolbar.getTWidth()*/;
		int y = e.getY() + r.y/*-PCARD.pc.toolbar.getTHeight()*//*-PCARD.pc.getInsets().top*/;
		if ((Component) e.getSource() == PCARD.pc.getRootPane()) {
			x -= PCARD.pc.toolbar.getTWidth();
			y -= PCARD.pc.toolbar.getTHeight() + PCARD.pc.getInsets().top + PCARD.pc.getJMenuBar().getHeight();
		}
		PaintTool.mouseUp(x, y);
		// PaintTool.lastTime = 0;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		Rectangle r = ((Component) e.getSource()).getBounds();
		int x = e.getX() + r.x/*-PCARD.pc.toolbar.getTWidth()*/;
		int y = e.getY() + r.y/*-PCARD.pc.toolbar.getTHeight()*//*-PCARD.pc.getInsets().top*/;
		if ((Component) e.getSource() == PCARD.pc.getRootPane()) {
			x -= PCARD.pc.toolbar.getTWidth();
			y -= PCARD.pc.toolbar.getTHeight() + PCARD.pc.getInsets().top + PCARD.pc.getJMenuBar().getHeight();
		}
		PaintTool.mouseStillDown(x, y);
		// PaintTool.lastTime = System.currentTimeMillis();
	}

	public void mouseMoved(MouseEvent e) {
		Rectangle r = ((Component) e.getSource()).getBounds();
		int x = e.getX() + r.x/*-PCARD.pc.toolbar.getTWidth()*/;
		int y = e.getY() + r.y/*-PCARD.pc.toolbar.getTHeight()*//*-PCARD.pc.getInsets().top*/;
		if ((Component) e.getSource() == PCARD.pc.getRootPane()) {
			x -= PCARD.pc.toolbar.getTWidth();
			y -= PCARD.pc.toolbar.getTHeight() + PCARD.pc.getInsets().top + PCARD.pc.getJMenuBar().getHeight();
		}
		PaintTool.mouseWithin(x, y);
		// PaintTool.lastTime = System.currentTimeMillis();
	}

	/*
	 * public void windowActivated(WindowEvent arg0) {
	 * //なぜフォーカスがツールバーに移るとmenuがdisabledになったままになる？？ JMenuBar mb =
	 * PCARD.pc.getJMenuBar(); int count = mb.getMenuCount(); for(int i=0; i<count;
	 * i++){ JMenu m = mb.getMenu(i); m.setEnabled(true); }
	 * 
	 * PaintTool.owner = PCARD.pc; } public void windowClosed(WindowEvent arg0) { }
	 * public void windowClosing(WindowEvent arg0) { } public void
	 * windowDeactivated(WindowEvent arg0) { } public void
	 * windowDeiconified(WindowEvent arg0) { } public void
	 * windowIconified(WindowEvent arg0) { } public void windowOpened(WindowEvent
	 * arg0) { }
	 */

}