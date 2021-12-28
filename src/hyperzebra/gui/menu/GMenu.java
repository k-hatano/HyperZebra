package hyperzebra.gui.menu;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import hyperzebra.gui.PCARD;

public class GMenu {
	public JMenuBar mb = new JMenuBar();

	public static boolean changeMenuName(String menu, String item, String name) {
		JMenuItem mi = searchMenuItem(PCARD.pc.getJMenuBar(), menu, item);
		if (mi == null)
			return false;

		mi.setText(PCARD.pc.intl.getText(name));
		return true;
	}

	public static boolean changeEnabled(String menu, String item, boolean enabled) {
		JMenuItem mi = searchMenuItem(PCARD.pc.getJMenuBar(), menu, item);
		if (mi == null)
			return false;

		mi.setEnabled(enabled);
		return true;
	}

	public static boolean changeMEnabled(String menu, boolean enabled) {
		JMenu m = searchMenu(PCARD.pc.getJMenuBar(), menu);
		if (m == null)
			return false;

		m.setEnabled(enabled);
		return true;
	}

	public static boolean changeSelected(String menu, String item, boolean selected) {
		JCheckBoxMenuItem mi = (JCheckBoxMenuItem) searchMenuItem(PCARD.pc.getJMenuBar(), menu, item);
		if (mi == null)
			return false;

		mi.setSelected(selected);
		return true;
	}

	public static JMenu searchMenu(JMenuBar mb, String menu) {
		if (mb == null)
			return null;
		int count = mb.getMenuCount();
		// そのまま探す
		for (int i = 0; i < count; i++) {
			JMenu m = mb.getMenu(i);
			if (m.getText().equals(menu)) {
				return m;
			}
		}
		// 英語メニューで探す
		for (int i = 0; i < count; i++) {
			JMenu m = mb.getMenu(i);
			if (PCARD.pc.intl.getEngText(m.getText()).equals(menu)) {
				return m;
			}
		}

		return null;
	}

	public static JMenuItem searchMenuItem(JMenuBar mb, String menu, String item) {
		if (mb == null)
			return null;
		int count = mb.getMenuCount();
		// そのまま探す
		for (int i = 0; i < count; i++) {
			JMenu m = mb.getMenu(i);
			if (m.getText().equals(menu)) {
				int itemcount = m.getItemCount();
				for (int j = 0; j < itemcount; j++) {
					JMenuItem mi = m.getItem(j);
					if (mi.getText().equals(item)) {
						return mi;
					}
				}
			}
		}
		// 英語メニューで探す
		for (int i = 0; i < count; i++) {
			JMenu m = mb.getMenu(i);
			if (PCARD.pc.intl.getEngText(m.getText()).equals(menu)) {
				int itemcount = m.getItemCount();
				for (int j = 0; j < itemcount; j++) {
					JMenuItem mi = m.getItem(j);
					if (mi != null && PCARD.pc.intl.getEngText(mi.getText()).equals(item)) {
						return mi;
					}
				}
			}
		}

		return null;
	}

	public GMenu(PCARD pcard, int mode) {
		ActionListener listener = null;

		if (mode == 0) {
			listener = new GMenuBrowse();
		} else if (mode == 1) {
			listener = new GMenuPaint();
		}

		JMenu m;
		JMenuItem mi;
		JCheckBoxMenuItem cb;
		int s = InputEvent.CTRL_DOWN_MASK;
		int s_shi = InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK;
		if (isMacOSX()) {
			s = InputEvent.META_DOWN_MASK;
			s_shi = InputEvent.META_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK;
		}

		if (mode == 0) {
			// Fileメニュー
			m = new JMenu(PCARD.pc.intl.getText("File"));
			mb.add(m);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("New Stack…")));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Open Stack…")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, s));
			mi.addActionListener(listener);
			{
				JMenu subm = new JMenu(PCARD.pc.intl.getText("Open Recent Stack"));
				m.add(subm);
			}
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Close Stack")));
			mi.setEnabled(false);
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, s));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Save a Copy…")));
			mi.setEnabled(false);
			mi.addActionListener(listener);
			m.addSeparator();
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Compact Stack")));
			mi.setEnabled(false);
			mi.setEnabled(false);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Protect Stack…")));
			mi.setEnabled(false);
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Delete Stack…")));
			mi.setEnabled(false);
			mi.setEnabled(false);
			m.addSeparator();
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Print…")));
			mi.setEnabled(false);

			if (!isMacOSX()) {
				m.addSeparator();
				m.add(mi = new JMenuItem(PCARD.pc.intl.getText("About " + PCARD.AppName + "…")));
				mi.addActionListener(listener);

				m.addSeparator();
				m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Quit")));
				mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, s));
				mi.addActionListener(listener);
			}

			// Editメニュー
			m = new JMenu(PCARD.pc.intl.getText("Edit"));
			mb.add(m);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Undo")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s));
			mi.addActionListener(listener);
			m.addSeparator();
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Cut")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, s));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Copy")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, s));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Paste")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, s));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Delete")));
			mi.addActionListener(listener);
			m.addSeparator();
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("New Card")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, s));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Delete Card")));
			mi.addActionListener(listener);
			// m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Cut
			// Card")));mi.setEnabled(false);mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Copy Card")));
			mi.setEnabled(false);
			mi.addActionListener(listener);
			m.addSeparator();
			m.add(cb = new JCheckBoxMenuItem(PCARD.pc.intl.getText("Background")));
			cb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, s));
			cb.addActionListener(listener);
			m.addSeparator();
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Icon…")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, s));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Sound…")));
			mi.setEnabled(false);
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Resource…")));
			mi.addActionListener(listener);
		}

		if (mode == 1) {
			// Fileメニュー
			m = new JMenu(PCARD.pc.intl.getText("File"));
			mb.add(m);
			m.setEnabled(false);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Import Paint…")));
			mi.setEnabled(false);
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Export Paint…")));
			mi.setEnabled(false);
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, s)); */mi.addActionListener(listener);
			// m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Save as
			// ppm…")));/*mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
			// s));*/mi.addActionListener(listener);

			// Editメニュー
			m = new JMenu(PCARD.pc.intl.getText("Edit"));
			mb.add(m);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Undo Paint")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Redo Paint")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s_shi));
			mi.addActionListener(listener);
			m.addSeparator();
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Cut Picture")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, s));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Copy Picture")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, s));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Paste Picture")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, s));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Clear Selection")));
			mi.addActionListener(listener);
			m.addSeparator();
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("New Card")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, s));
			mi.addActionListener(listener);
			m.addSeparator();
			m.add(cb = new JCheckBoxMenuItem(PCARD.pc.intl.getText("Background")));
			cb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, s));
			cb.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Icon…")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, s));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Sound…")));
			mi.setEnabled(false);
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Resource…")));
			mi.setEnabled(false);
			mi.addActionListener(listener);
		}

		// Goメニュー
		m = new JMenu(PCARD.pc.intl.getText("Go"));
		mb.add(m);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Back")));
		mi.setEnabled(false);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DEAD_TILDE, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Home")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Help")));
		mi.setEnabled(false);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Recent")));
		mi.setEnabled(false);
		m.addSeparator();
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("First")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Prev")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Next")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Last")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, s));
		mi.addActionListener(listener);
		m.addSeparator();
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Find…")));
		mi.setEnabled(false);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Message")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Next Window")));
		mi.setEnabled(false);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, s));
		mi.addActionListener(listener);

		// Toolメニュー
		m = new JMenu(PCARD.pc.intl.getText("Tools"));
		mb.add(m);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Show ToolBar")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, s));
		mi.addActionListener(listener);
		m.addSeparator();
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Browse")));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Button")));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Field")));
		mi.addActionListener(listener);
		m.addSeparator();
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Select")));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Lasso")));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("MagicWand")));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Pencil")));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Brush")));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Eraser")));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Line")));
		mi.addActionListener(listener);
		// m.add(mi = new
		// JMenuItem(PCARD.pc.intl.getText("SprayCan")));mi.setEnabled(false);mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Rect")));
		mi.addActionListener(listener);
		// m.add(mi = new
		// JMenuItem(PCARD.pc.intl.getText("RoundRect")));mi.setEnabled(false);mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("PaintBucket")));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Oval")));
		mi.addActionListener(listener);
		// m.add(mi = new
		// JMenuItem(PCARD.pc.intl.getText("Curve")));mi.setEnabled(false);mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Type")));
		mi.addActionListener(listener);
		// m.add(mi = new
		// JMenuItem(PCARD.pc.intl.getText("Polygon")));mi.setEnabled(false);mi.addActionListener(listener);
		// m.add(mi = new
		// JMenuItem(PCARD.pc.intl.getText("FreePolygon")));mi.setEnabled(false);mi.addActionListener(listener);
		// m.add(mi = new
		// JMenuItem(PCARD.pc.intl.getText("Spoit")));mi.setEnabled(false);mi.addActionListener(listener);

		// 0.ブラウズモード
		if (mode == 0) {
			// Objectsメニュー
			m = new JMenu(PCARD.pc.intl.getText("Objects"));
			mb.add(m);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Button Info…")));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Field Info…")));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Card Info…")));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Background Info…")));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Stack Info…")));
			mi.addActionListener(listener);
			m.addSeparator();
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Bring Closer")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, s));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Send Farther")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, s));
			mi.addActionListener(listener);
			m.addSeparator();
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("New Button")));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("New Field")));
			mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("New Background")));
			mi.addActionListener(listener);

		}

		if (mode == 1) {
			// Paintメニュー
			m = new JMenu(PCARD.pc.intl.getText("Paint"));
			mb.add(m);
			// m.add(mi = new
			// JMenuItem(PCARD.pc.intl.getText("Select")));mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
			// s));mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Select All")));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, s));
			mi.addActionListener(listener);
			m.add(cb = new JCheckBoxMenuItem(PCARD.pc.intl.getText("FatBits")));
			cb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, s));
			cb.addActionListener(listener);
			m.add(cb = new JCheckBoxMenuItem(PCARD.pc.intl.getText("Antialias")));
			cb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, s));
			cb.addActionListener(listener);
			m.addSeparator();
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Reverse Selection")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Expand Selection")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			// m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Add to Protect
			// Area")));/*mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
			// s));*/mi.addActionListener(listener);
			// m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Discard Protect
			// Area")));/*mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
			// s));*/mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Color Convert…")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			// m.add(mi = new
			// JMenuItem(PCARD.pc.intl.getText("Emboss…")));/*mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
			// s));*/mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Filter…")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Blending Mode…")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Scale Selection…")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.addSeparator();
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Fill")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Invert")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Pickup")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Darken")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Lighten")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Rotate Left")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Rotate Right")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Flip Horizontal")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Flip Vertical")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, s)); */mi.addActionListener(listener);
			m.addSeparator();
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Opaque")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Transparent")));
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.addSeparator();
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Keep")));
			mi.setEnabled(false);
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Revert")));
			mi.setEnabled(false);
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.addSeparator();
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Rotate")));
			mi.setEnabled(false);
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Distort")));
			mi.setEnabled(false);
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Stretch")));
			mi.setEnabled(false);
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Perspective")));
			mi.setEnabled(false);
			/* mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s)); */mi.addActionListener(listener);

		}
	}

	public static boolean isMacOSX() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac os x");
	}

	public static void menuUpdate(JMenuBar srcMenuBar) {
		JMenuBar menubar = PCARD.pc.getJMenuBar();
		menubar.removeAll();
		for (int i = 0; i < srcMenuBar.getComponentCount(); i++) {
			Component c = srcMenuBar.getComponent(i);
			if (c.getClass() == JMenu.class) {
				JMenu menu = (JMenu) c;
				JMenu newMenu = new JMenu(menu.getText());
				for (int j = 0; j < menu.getPopupMenu().getComponentCount(); j++) {
					Component c2 = menu.getPopupMenu().getComponent(j);
					if (c2.getClass() == JMenuItem.class) {
						JMenuItem mi = (JMenuItem) c2;
						JMenuItem newmi = new JMenuItem(mi.getText());
						newmi.setEnabled(c2.isEnabled());
						ActionListener[] listeners = mi.getActionListeners();
						if (listeners.length > 0) {
							newmi.addActionListener(listeners[0]);
						}
						newmi.setAccelerator(mi.getAccelerator());
						newMenu.add(newmi);
					} else if (c2.getClass() == JCheckBoxMenuItem.class) {
						JCheckBoxMenuItem mi = (JCheckBoxMenuItem) c2;
						JCheckBoxMenuItem newmi = new JCheckBoxMenuItem(mi.getText());
						newmi.setEnabled(mi.isEnabled());
						newmi.setSelected(mi.isSelected());
						ActionListener[] listeners = mi.getActionListeners();
						if (listeners.length > 0) {
							newmi.addActionListener(listeners[0]);
						}
						newmi.setAccelerator(mi.getAccelerator());
						newMenu.add(newmi);
					} else if (c2.getClass() == JPopupMenu.Separator.class) {
						newMenu.addSeparator();
					} else if (c2.getClass() == JMenu.class) {
						JMenu menu2 = (JMenu) c2;
						JMenu newMenu2 = new JMenu(menu2.getText());
						for (int k = 0; k < menu2.getPopupMenu().getComponentCount(); k++) {
							Component c3 = menu2.getPopupMenu().getComponent(k);
							if (c3.getClass() == JMenuItem.class) {
								JMenuItem mi = (JMenuItem) c3;
								JMenuItem newmi = new JMenuItem(mi.getText());
								newmi.setEnabled(c3.isEnabled());
								ActionListener[] listeners = mi.getActionListeners();
								if (listeners.length > 0) {
									newmi.addActionListener(listeners[0]);
								}
								newmi.setAccelerator(mi.getAccelerator());
								newMenu2.add(newmi);
							}
						}
						newMenu.add(newMenu2);
					}
				}
				menubar.add(newMenu);
			}
		}
		BuildRecentMenu((JMenu) menubar.getComponent(0));
	}

	static String[] recentsMenuName = new String[10];
	static String[] recentsMenuPath = new String[10];

	public static void BuildRecentMenu(JMenu m) {
		GMenuBrowse listener = new GMenuBrowse();

		JMenu subm = null;
		for (int i = 0; i < m.getPopupMenu().getComponentCount(); i++) {
			if (m.getPopupMenu().getComponent(i).getClass() == JMenu.class) {
				subm = (JMenu) m.getPopupMenu().getComponent(i);
				subm.removeAll();
			}
		}
		if (subm == null)
			return;

		// 最近使ったスタック
		File recentFile = new File("resource_trash" + File.separatorChar + "recent.txt");
		if (recentFile.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(recentFile);
				int length = (int) recentFile.length();
				byte[] b = new byte[length];
				fis.read(b);
				String[] recents = new String(b).split("\n");
				for (int i = 0; i < 10 && i < recents.length; i++) {
					if (new File(recents[i]).exists()) {
						JMenuItem mi = new JMenuItem((i + 1) + " " + new File(recents[i]).getName());
						mi.addActionListener(listener);
						subm.add(mi);
						recentsMenuPath[i] = recents[i];
						recentsMenuName[i] = (i + 1) + " " + new File(recents[i]).getName();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		subm.addSeparator();
		JMenuItem mi = new JMenuItem(PCARD.pc.intl.getText("Clear This Menu"));
		mi.addActionListener(listener);
		subm.add(mi);
	}
}

/*
 * class MyMenu extends JMenu { private static final long serialVersionUID =
 * 5858305584938822448L;
 * 
 * public MyMenu(String text) { super(text); TearoffThread thread = new
 * TearoffThread(this.getPopupMenu()); thread.start(); }
 * 
 * }
 * 
 * 
 * class TearoffThread extends Thread { JPopupMenu popup;
 * TearoffThread(JPopupMenu popup){ this.popup = popup; }
 * 
 * public void run(){ while(!popup.isVisible()){ try { sleep(10); } catch
 * (InterruptedException e) {} } while(popup.isVisible()){ try { sleep(10); }
 * catch (InterruptedException e) {}
 * 
 * PointerInfo pointerInfo = MouseInfo.getPointerInfo(); Rectangle vrect =
 * popup.getVisibleRect(); Point p = pointerInfo.getLocation(); if(p.y>vrect.y
 * && (p.y>vrect.y+vrect.height || p.x<vrect.x || p.x>vrect.x+vrect.width)){
 * popup.setLocation(p.x, p.y); } } } }
 */
