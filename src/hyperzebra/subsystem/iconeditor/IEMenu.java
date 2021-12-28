package hyperzebra.subsystem.iconeditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import hyperzebra.gui.PCARD;
import hyperzebra.gui.menu.GMenu;
import hyperzebra.gui.menu.GMenuPaint;

public class IEMenu {
	static JMenuItem undoMenu = null;
	static JMenuItem redoMenu = null;
	static IconEditor owner;

	public static boolean changeEnabled(String menu, String item, boolean enabled) {
		JMenuItem mi = GMenu.searchMenuItem(owner.getJMenuBar(), menu, item);
		if (mi == null)
			return false;

		mi.setEnabled(enabled);
		return true;
	}

	public IEMenu(IconEditor in_owner) {
		owner = in_owner;

		ActionListener listener = null;

		listener = new IEMenuListener(in_owner);

		// メニューバーの設定
		JMenuBar mb = new JMenuBar();
		owner.setJMenuBar(mb);

		JMenu m;
		JMenuItem mi;
		JCheckBoxMenuItem cb;
		int s = InputEvent.CTRL_DOWN_MASK;
		if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
			s = InputEvent.META_DOWN_MASK;
		}
		int s_shift = s + InputEvent.SHIFT_MASK;

		// Fileメニュー
		m = new JMenu(PCARD.pc.intl.getText("File"));
		mb.add(m);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Close")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, s));
		mi.addActionListener(listener);

		// Editメニュー
		m = new JMenu(PCARD.pc.intl.getText("Edit"));
		mb.add(m);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Undo Paint")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s));
		mi.addActionListener(listener);
		undoMenu = mi;
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Redo Paint")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s_shift));
		mi.addActionListener(listener);
		redoMenu = mi;
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
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Image Size…")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, s));
		mi.addActionListener(listener);
		if (owner.type.equals("cursor")) {
			m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Hot Spot…")));
			mi.addActionListener(listener);
		}

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
		{
			JMenu subm = new JMenu(PCARD.pc.intl.getText("Grid"));
			subm.add(mi = new JMenuItem(PCARD.pc.intl.getText("Grid Size 1")));
			mi.addActionListener(listener);
			subm.add(mi = new JMenuItem(PCARD.pc.intl.getText("Grid Size 16")));
			mi.addActionListener(listener);
			subm.add(cb = new JCheckBoxMenuItem(PCARD.pc.intl.getText("Use Grid")));
			cb.addActionListener(listener);
			cb.setSelected(IconEditor.useGrid);
			m.add(subm);
		}
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

//メニュー動作
class IEMenuListener implements ActionListener {
	IconEditor editor;

	IEMenuListener(IconEditor editor) {
		this.editor = editor;
	}

	public void actionPerformed(ActionEvent e) {
		String in_cmd = e.getActionCommand();
		String cmd = PCARD.pc.intl.getEngText(in_cmd);

		if (cmd.equalsIgnoreCase("Image Size…")) {
			editor.save();
			new IconSizeDialog(editor);
		} else if (cmd.equalsIgnoreCase("Hot Spot…")) {
			new HotSpotDialog(editor);
		} else if (cmd.equalsIgnoreCase("Close")) {
			editor.save();
			editor.dispose();
		} else {
			GMenuPaint.doMenu(cmd);
		}
	}
}