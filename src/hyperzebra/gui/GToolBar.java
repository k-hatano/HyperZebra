package hyperzebra.gui;

import java.awt.Color;
//import java.awt.Dimension;
import java.awt.Point;
//import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

//import javax.swing.JButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
//import javax.swing.JToolBar;
import javax.swing.JWindow;
//import javax.swing.plaf.basic.BasicToolBarUI;

import hyperzebra.gui.button.CPButton;
import hyperzebra.gui.button.GradButton;
import hyperzebra.gui.button.PatButton;
import hyperzebra.gui.button.TBButton;
import hyperzebra.gui.button.TBButtonListener;
import hyperzebra.gui.button.TransButton;
import hyperzebra.gui.menu.GMenu;

public class GToolBar {
	toolBarListener toolBarListener = new toolBarListener();
	// JToolBar tb;
	public JWindow tb;
	// CPButton fore;
	// CPButton back;
	// GradButton grad;
	// PatButton pat;
	public TBButton selectToolButton;
	public boolean gvisible;

	public GToolBar(PCARDFrame owner) {
		// JPanel panel = new JPanel();
		tb = new JWindow();

		// tb.setBounds(PCARD.pc.getX()-72,PCARD.pc.getY(),72,200);
		tb.setLayout(null);
		tb.setAlwaysOnTop(true);
		tb.addMouseMotionListener(toolBarListener);

		// panel.setLayout(null);
		// tb.setPreferredSize(new Dimension(80,120));
		// tb.setSize(new Dimension(80,80));
		// tb.add(panel);
		// panel.setMinimumSize(new Dimension(80,120));
		// panel.setMaximumSize(new Dimension(80,120));

		TBButton btn;
		ButtonGroup grp = new ButtonGroup();
		TBButtonListener listener = new TBButtonListener();

		JButton jbtn;
		tb.add(jbtn = new JButton());
		jbtn.setName("Close");
		jbtn.setBounds(4, 2, 12, 12);
		jbtn.setIcon(new ImageIcon("./resource/tb_close.png"));
		jbtn.setRolloverIcon(new ImageIcon("./resource/tb_close2.png"));
		jbtn.setContentAreaFilled(false);
		jbtn.setBorderPainted(false);
		jbtn.addActionListener(listener);

		tb.add(jbtn = new JButton());
		jbtn.setName("Bar");
		jbtn.setBounds(0, 0, 78, 16);
		jbtn.setIcon(new ImageIcon("./resource/tb_bar.png"));
		jbtn.setPressedIcon(new ImageIcon("./resource/tb_bar.png"));
		jbtn.setContentAreaFilled(false);
		jbtn.setBorderPainted(false);
		jbtn.addMouseMotionListener(toolBarListener);

		tb.add(btn = new TBButton(PCARD.pc.intl.getToolText("Browse"), 0, 0));
		grp.add(btn);
		btn.addActionListener(listener);
		btn.addMouseListener(listener);
		btn.setSelected(true);

		tb.add(btn = new TBButton(PCARD.pc.intl.getToolText("Button"), 0, 1));
		grp.add(btn);
		btn.addActionListener(listener);
		btn.addMouseListener(listener);

		tb.add(btn = new TBButton(PCARD.pc.intl.getToolText("Field"), 0, 2));
		grp.add(btn);
		btn.addActionListener(listener);
		btn.addMouseListener(listener);

		tb.add(btn = new TBButton(PCARD.pc.intl.getToolText("Select"), 1, 0));
		grp.add(btn);
		btn.addActionListener(listener);
		btn.addMouseListener(listener);
		selectToolButton = btn;

		tb.add(btn = new TBButton(PCARD.pc.intl.getToolText("Lasso"), 1, 1));
		grp.add(btn);
		btn.addActionListener(listener);
		btn.addMouseListener(listener);
		tb.add(btn = new TBButton(PCARD.pc.intl.getToolText("MagicWand"), 1, 2));
		grp.add(btn);
		btn.addActionListener(listener);
		btn.addMouseListener(listener);

		tb.add(btn = new TBButton(PCARD.pc.intl.getToolText("Pencil"), 2, 0));
		grp.add(btn);
		btn.addActionListener(listener);
		btn.addMouseListener(listener);

		tb.add(btn = new TBButton(PCARD.pc.intl.getToolText("Brush"), 2, 1));
		grp.add(btn);
		btn.addActionListener(listener);
		btn.addMouseListener(listener);

		tb.add(btn = new TBButton(PCARD.pc.intl.getToolText("Eraser"), 2, 2));
		grp.add(btn);
		btn.addActionListener(listener);
		btn.addMouseListener(listener);

		tb.add(btn = new TBButton(PCARD.pc.intl.getToolText("Line"), 3, 0));
		grp.add(btn);
		btn.addActionListener(listener);
		btn.addMouseListener(listener);

		// tb.add(new TBButton(PCARD.pc.intl.getToolText("SprayCan"),3,0));
		tb.add(btn = new TBButton(PCARD.pc.intl.getToolText("Rect"), 3, 1));
		grp.add(btn);
		btn.addActionListener(listener);
		btn.addMouseListener(listener);

		// tb.add(new TBButton(PCARD.pc.intl.getToolText("RoundRect"),3,2));
		tb.add(btn = new TBButton(PCARD.pc.intl.getToolText("Oval"), 3, 2));
		grp.add(btn);
		btn.addActionListener(listener);
		btn.addMouseListener(listener);

		tb.add(btn = new TBButton(PCARD.pc.intl.getToolText("PaintBucket"), 4, 0));
		grp.add(btn);
		btn.addActionListener(listener);
		btn.addMouseListener(listener);

		// tb.add(new TBButton(PCARD.pc.intl.getToolText("Curve"),4,2));
		tb.add(btn = new TBButton(PCARD.pc.intl.getToolText("Type"), 4, 1));
		grp.add(btn);
		btn.addActionListener(listener);
		btn.addMouseListener(listener);

		// tb.add(new TBButton(PCARD.pc.intl.getToolText("Polygon"),5,1));
		// tb.add(new TBButton(PCARD.pc.intl.getToolText("FreePolygon"),5,2));
		// tb.add(new TBButton(PCARD.pc.intl.getToolText("Spoit"),6,0));
		// tb.addSeparator();
		owner.fore = new CPButton(Color.BLACK, 5, 0, false);
		tb.add(owner.fore);
		owner.back = new CPButton(Color.WHITE, 5, 1, true);
		tb.add(owner.back);
		owner.pat = new PatButton(11, 6, 0);
		tb.add(owner.pat);
		owner.grad = new GradButton(Color.BLACK, Color.WHITE, 6, 1);
		tb.add(owner.grad);
		tb.add(owner.trans = new TransButton(PCARD.pc.intl.getToolText("Transparency"), 6, 2));
		owner.trans.addActionListener(listener);
		owner.trans.addMouseListener(listener);

		// panel.setPreferredSize(new Dimension(80,80));
		// panel.setSize(new Dimension(80,80));

		tb.addComponentListener(toolBarListener);

		// tb.pack();
		tb.setVisible(false);
		// owner.add(tb, BorderLayout.WEST);
	}

	public int getTWidth() {
		return 0;
		/*
		 * if(tb==null) return 0; if(!tb.isVisible()) return 0; if
		 * (((BasicToolBarUI)tb.getUI()).isFloating()) return 0; if
		 * (tb.getOrientation()==JToolBar.HORIZONTAL) return 0; Rectangle r =
		 * tb.getBounds(); return r.width;
		 */
	}

	public int getTHeight() {
		return 0;
		/*
		 * if(tb==null) return 0; if(!tb.isVisible()) return 0; if
		 * (((BasicToolBarUI)tb.getUI()).isFloating()) return 0; if
		 * (tb.getOrientation()==JToolBar.VERTICAL) return 0; Rectangle r =
		 * tb.getBounds(); return r.height;
		 */
	}

	public void activate() {
		tb.setVisible(gvisible);
	}

	public void deactivate() {
		gvisible = tb.isVisible();
		tb.setVisible(false);
	}
}

class toolBarListener implements ComponentListener, MouseMotionListener {
	public void componentMoved(ComponentEvent e) {
		// PCARD.stack.setNewBounds();
	}

	public void componentResized(ComponentEvent e) {
		// PCARD.pc.setNewBounds();
	}

	public void componentHidden(ComponentEvent e) {
		// PCARD.pc.setNewBounds();
		GMenu.changeMenuName("Tool", "Hide ToolBar", "Show ToolBar");
	}

	public void componentShown(ComponentEvent e) {
		// PCARD.pc.setNewBounds();
		GMenu.changeMenuName("Tool", "Show ToolBar", "Hide ToolBar");
	}

	boolean isDragStart;
	Point p = new Point(0, 0);

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!isDragStart) {
			isDragStart = true;
			p.x = e.getX();
			p.y = e.getY();
		} else {
			JWindow tb = PCARD.pc.toolbar.tb;
			tb.setLocation(e.getXOnScreen() - p.x, e.getYOnScreen() - p.y);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		isDragStart = false;
	}
}
