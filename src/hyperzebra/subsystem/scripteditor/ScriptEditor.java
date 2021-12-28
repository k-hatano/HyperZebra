package hyperzebra.subsystem.scripteditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import hyperzebra.TTalk;
import hyperzebra.gui.PCARD;
import hyperzebra.object.OBackground;
import hyperzebra.object.OCard;
import hyperzebra.object.OObject;
import hyperzebra.subsystem.debugger.VariableWatcher;

public class ScriptEditor extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private OStack parent = null;
	static JTabbedPane tabPane = null;
	String FindStr = "";
	String ReplaceStr = "";
	// static private ScriptArea area;

	public ScriptEditor(Frame owner) {
		super();

		// frame
		setBounds(owner.getX() + owner.getWidth() / 2 - 320, owner.getY() + owner.getHeight() / 2 - 320, 640, 640);
		setTitle(PCARD.pc.intl.getDialogText("Script Editor"));
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				maybeExit();
				while (tabPane.getComponentCount() > 0) {
					tabPane.remove(tabPane.getComponent(0));
				}
				TTalk.tracemode = 0;
				TTalk.stepflag = true;
			}

			@Override
			public void windowOpened(WindowEvent e) {
				// area.checkIndentCall();
			}
		});

		// tabpane
		tabPane = new JTabbedPane();
		getContentPane().add(tabPane);

		// menu
		new SEMenu(this);
	}

	public static void openScriptEditor(Frame owner, OObject obj) {
		openScriptEditor(owner, obj, 0);
	}

	private static Frame tmp_owner;
	private static OObject tmp_obj;
	private static int tmp_line;

	public static void openScriptEditor(Frame owner, OObject obj, int line) {
		tmp_owner = owner;
		tmp_obj = obj;
		tmp_line = line;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				openScriptEditor1(tmp_owner, tmp_obj, tmp_line);
			}
		});
	}

	private static void openScriptEditor1(Frame owner, OObject obj, int line) {
		// System.out.println("openScriptEditor");

		ArrayList<String> scriptList = obj.scriptList;
		if (PCARD.pc.stack.scriptEditor == null) {
			PCARD.pc.stack.scriptEditor = new ScriptEditor(owner);
		}

		// 2重オープンチェック
		if (tabPane != null) {
			for (int i = 0; i < tabPane.getComponentCount(); i++) {
				if (!tabPane.getComponent(i).getName().equals("JScrollPane")) {
					continue;
				}
				JScrollPane scrPane = (JScrollPane) tabPane.getComponent(i);
				if (scrPane.getViewport().getComponentCount() < 1) {
					continue;
				}
				if (scrPane.isValid() == false) {
					PCARD.pc.stack.scriptEditor.dispose();
					PCARD.pc.stack.scriptEditor = new ScriptEditor(owner);
					return;// 壊れている
				}
				ScriptArea area = (ScriptArea) scrPane.getViewport().getComponent(0);
				if (area.compareObject(obj) == true) {
					if (line > 0) {
						// 指定行にジャンプ
						String[] newAry = area.getText().split("\n");
						int selStart = 0;
						int selEnd = 0;
						for (int j = 0; j < newAry.length; j++) {
							if (line == j) {
								selEnd = selStart + newAry[j].length();
								break;
							}
							selStart += newAry[j].length() + 1;
						}
						area.setSelectionStart(selStart);
						if (selEnd > 0)
							area.setSelectionEnd(selEnd);
					}

					PCARD.pc.stack.scriptEditor.setVisible(true);
					PCARD.pc.stack.scriptEditor.toFront();
					tabPane.setSelectedIndex(i);
					area.requestFocus();

					tabPane.setVisible(true);
					scrPane.setVisible(true);
					area.setVisible(true);
					return;
				}
			}
		}

		JScrollPane scrollpane = new JScrollPane();
		scrollpane.setName("JScrollPane");
		scrollpane.setBounds(0, 0, 640, 640);
		scrollpane.setPreferredSize(new Dimension(640, 640));
		scrollpane.getVerticalScrollBar().setValue(0);
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tabPane.addTab(obj.getShortName(), scrollpane);

		String text = "";
		for (int i = 0; i < scriptList.size(); i++) {
			text += scriptList.get(i);
			if (i < scriptList.size() - 1)
				text += "\n";
		}
		if (obj.objectType.equals("button") && text.length() == 0) {
			text = "on mouseUp\n  \nend mouseUp";
			line = 1;
		}

		// area
		ScriptArea area = new ScriptArea(PCARD.pc.stack.scriptEditor, text);
		area.checkIndentCall();
		area.setObject(obj);
		area.setMargin(new Insets(2, 2, 2, 2));
		area.getPreferredSize();
		area.setOpaque(false);
		area.setCaretPosition(0);
		// scrollpane.add(area);
		scrollpane.setViewportView(area);

		if (tabPane.getComponentCount() > 2) {
			for (int i = 0; i < tabPane.getComponentCount(); i++) {
				JScrollPane scrPane = (JScrollPane) tabPane.getComponent(i);
				if (scrPane.getViewport().getComponentCount() >= 1) {
					ScriptArea a = (ScriptArea) scrPane.getViewport().getComponent(0);
					if (a != null)
						tabPane.setTitleAt(i, a.object.getShortShortName());
				}
			}
		}

		if (line > 0) {
			// 指定行にジャンプ
			String[] newAry = area.getText().split("\n");
			int selStart = 0;
			int selEnd = 0;
			for (int j = 0; j < newAry.length; j++) {
				if (line == j) {
					int spacing = 0;
					while (spacing < newAry[j].length()
							&& (newAry[j].charAt(spacing) == ' ' || newAry[j].charAt(spacing) == '　'))
						spacing++;
					selStart += spacing;
					selEnd = selStart + newAry[j].length() - spacing;
					break;
				}
				selStart += newAry[j].length() + 1;
			}
			area.setSelectionStart(selStart);
			if (selEnd > 0)
				area.setSelectionEnd(selEnd);
		}

		PCARD.pc.stack.scriptEditor.toFront();

		try {
			PCARD.pc.stack.scriptEditor.setVisible(true);
		} catch (Exception e) {
			System.out.println("Error: PCARD.pc.stack.scriptEditor.setVisible(true);");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					PCARD.pc.stack.scriptEditor.setVisible(true);
				}
			});
		}

		try {
			tabPane.setSelectedComponent(scrollpane);
		} catch (Exception e) {
			System.out.println("Error: tabPane.setSelectedComponent(scrollpane);");
		}

		area.requestFocus();
	}

	static void maybeExit() {
		for (int i = 0; i < tabPane.getComponentCount(); i++) {
			if (!tabPane.getComponent(i).getName().equals("JScrollPane")) {
				continue;
			}
			JScrollPane scrPane = (JScrollPane) tabPane.getComponent(i);
			if (scrPane.getViewport().getComponentCount() == 0)
				return;
			ScriptArea area = (ScriptArea) scrPane.getViewport().getComponent(0);
			if (false == saveAlert(area)) {
				return;// クローズキャンセル
			}
		}
		PCARD.pc.stack.scriptEditor.setVisible(false);
		// PCARD.pc.stack.scriptEditor.dispose();
		// PCARD.pc.stack.scriptEditor = null;
	}

	static boolean saveAlert(ScriptArea area) {
		if (area == null || area.saved) {
			return true;
		}
		java.awt.Toolkit.getDefaultToolkit().beep();
		Object[] options = { PCARD.pc.intl.getDialogText("Save"), PCARD.pc.intl.getDialogText("Discard"),
				PCARD.pc.intl.getDialogText("Cancel") };
		int retValue = JOptionPane.showOptionDialog(PCARD.pc.stack.scriptEditor,
				PCARD.pc.intl.getDialogText("Script is not saved."), "Exit Options", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		if (retValue == JOptionPane.YES_OPTION) {
			// 保存する
			saveScript(area);
		} else if (retValue == JOptionPane.NO_OPTION) {
			// 保存しない
		} else if (retValue == JOptionPane.CANCEL_OPTION) {
			return false;
		}
		return true;
	}

	static void saveScript(ScriptArea area) {
		String allStr = "";
		try {
			allStr = area.getDocument().getText(0, area.getDocument().getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		/*
		 * OObject obj = null; //カード(bg)を開いているなら、メモリ上にあるオブジェクトの情報を書き換える
		 * if(area.ParentId==0 || area.compareParent(PCARD.pc.stack.curCard)){ obj =
		 * area.getObject(PCARD.pc.stack.curCard); } else { obj =
		 * area.getObjectOtherCard(); }
		 */
		OObject obj = area.object;

		if (obj != null) {
			obj.setScript(allStr);
			area.savedScript();
		} else {
			System.out.println("スクリプトの保存に失敗しました");
		}
	}

	public static void setTracemode() {
		if (PCARD.pc.stack.scriptEditor == null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					setTracemode();
				}
			});
			return;
		}
		JMenuBar mb = PCARD.pc.stack.scriptEditor.getJMenuBar();
		for (int i = 0; i < mb.getMenuCount(); i++) {
			if (mb.getMenu(i).getText() == PCARD.pc.intl.getText("Debug")) {
				// すでにdebugメニューがある
				return;
			}
		}

		// debugメニューを作成
		SEMenuListener listener = new SEMenuListener();
		int s = InputEvent.CTRL_DOWN_MASK;
		if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
			s = InputEvent.META_DOWN_MASK;
		}
		JMenu m = new JMenu(PCARD.pc.intl.getText("Debug"));
		mb.add(m);
		JMenuItem mi;
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Step")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7, s));
		mi.addActionListener(listener);
		m.addSeparator();

		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Trace")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, s));
		mi.addActionListener(listener);

		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Run")));
		mi.addActionListener(listener);
		m.addSeparator();

		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Variable Watcher")));
		mi.addActionListener(listener);
	}
}

class seListener implements DocumentListener {
	ScriptArea area;

	seListener(ScriptArea inarea) {
		super();
		area = inarea;
	}

	public void changedUpdate(DocumentEvent e) {
		area.changedScript();
	}

	public void insertUpdate(DocumentEvent e) {
		String changeStr = area.getText().substring(e.getOffset(), e.getOffset() + e.getLength());
		if (changeStr.equals("\n")) {
			area.checkIndentCall();
		}
		if (changeStr.contains("\t")) {
			area.checkIndentCall();
			return;
		}
		area.changedScript();
		area.new checkWordsThread().start();
	}

	public void removeUpdate(DocumentEvent e) {
		area.changedScript();
		area.new checkWordsThread().start();
	}
}

class SEMenu {

	/**
	 * 
	 */
	static JMenuItem undoMenu = null;
	static JMenuItem redoMenu = null;

	public SEMenu(ScriptEditor scriptEditor) {
		ActionListener listener = null;

		listener = new SEMenuListener();

		// メニューバーの設定
		JMenuBar mb = new JMenuBar();
		scriptEditor.setJMenuBar(mb);

		JMenu m;
		JMenuItem mi;
		int s = InputEvent.CTRL_DOWN_MASK;
		if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
			s = InputEvent.META_DOWN_MASK;
		}
		int s_opt = s + InputEvent.ALT_MASK;
		int s_shift = s + InputEvent.SHIFT_MASK;

		// Fileメニュー
		m = new JMenu(PCARD.pc.intl.getText("File"));
		mb.add(m);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Close")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Save")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, s));
		mi.addActionListener(listener);
		m.addSeparator();
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Print…")));
		mi.setEnabled(false);

		// Editメニュー
		m = new JMenu(PCARD.pc.intl.getText("Edit"));
		mb.add(m);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Undo")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s));
		mi.addActionListener(listener);
		undoMenu = mi;
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Redo")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, s_shift));
		mi.addActionListener(listener);
		redoMenu = mi;
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
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Select All")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, s));
		mi.addActionListener(listener);
		m.addSeparator();
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Find")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Find Next")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Find Prev")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, s_shift));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Replace")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Replace Next")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Replace Prev")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, s_shift));
		mi.addActionListener(listener);

		// Scriptメニュー
		m = new JMenu(PCARD.pc.intl.getText("Script"));
		mb.add(m);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Edit Card Script")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, s_opt));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Edit Background Script")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, s_opt));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Edit Stack Script")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, s_opt));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Next Window")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, s));
		mi.addActionListener(listener);
		m.addSeparator();
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Comment")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARD.pc.intl.getText("Uncomment")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, s));
		mi.addActionListener(listener);

	}
}

//メニュー動作
class SEMenuListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		String in_cmd = e.getActionCommand();
		String cmd = PCARD.pc.intl.getEngText(in_cmd);
		JTabbedPane tabpane = ScriptEditor.tabPane;
		JViewport vp = (JViewport) ((JScrollPane) tabpane.getSelectedComponent()).getComponent(0);
		if (vp.getComponentCount() == 0)
			return;
		ScriptArea area = (ScriptArea) vp.getComponent(0);
		if (cmd.equals("Cut")) {
			area.cut();
		}
		if (cmd.equals("Copy")) {
			area.copy();
		}
		if (cmd.equals("Paste")) {
			area.paste();
		}
		if (cmd.equals("Select All")) {
			area.selectAll();
		}
		if (cmd.equals("Undo")) {
			area.undo();
			area.undoStatus();
		}
		if (cmd.equals("Redo")) {
			area.redo();
			area.undoStatus();
		}
		if (cmd.equals("Find")) {
			new FindDialog(area.parent);
			cmd = FindDialog.clicked;
		}
		if (cmd.equals("Find Next")) {
			String searchedStr = area.getText().substring(area.getSelectionEnd(), area.getText().length())
					.toLowerCase();
			if (searchedStr.indexOf(area.parent.FindStr.toLowerCase()) >= 0) {
				area.setSelectionStart(area.getSelectionEnd() + searchedStr.indexOf(area.parent.FindStr.toLowerCase()));
				area.setSelectionEnd(area.getSelectionStart() + area.parent.FindStr.length());
			} else {
				java.awt.Toolkit.getDefaultToolkit().beep();
			}
		}
		if (cmd.equals("Find Prev")) {
			String searchedStr = area.getText().substring(0, area.getSelectionStart()).toLowerCase();
			if (searchedStr.lastIndexOf(area.parent.FindStr.toLowerCase()) >= 0) {
				area.setSelectionStart(searchedStr.lastIndexOf(area.parent.FindStr.toLowerCase()));
				area.setSelectionEnd(area.getSelectionStart() + area.parent.FindStr.length());
			} else {
				java.awt.Toolkit.getDefaultToolkit().beep();
			}
		}
		if (cmd.equals("Replace")) {
			new ReplaceDialog(area.parent);
			cmd = ReplaceDialog.clicked;
		}
		if (cmd.equals("Replace Next")) {
			int savStart = area.getSelectionStart();
			String searchedStr = area.getText().substring(area.getSelectionStart(), area.getText().length())
					.toLowerCase();
			if (searchedStr.indexOf(area.parent.FindStr.toLowerCase()) >= 0) {
				String text = area.getText();
				text = text.substring(0,
						area.getSelectionStart() + searchedStr.indexOf(area.parent.FindStr.toLowerCase()))
						+ area.parent.ReplaceStr
						+ text.substring(area.getSelectionStart()
								+ searchedStr.indexOf(area.parent.FindStr.toLowerCase()) + area.parent.FindStr.length(),
								text.length());
				area.setText(text);

				area.setSelectionStart(savStart + searchedStr.indexOf(area.parent.FindStr.toLowerCase()));
				area.setSelectionEnd(area.getSelectionStart() + area.parent.ReplaceStr.length());
			} else {
				java.awt.Toolkit.getDefaultToolkit().beep();
			}
		}
		if (cmd.equals("Replace Prev")) {
			String searchedStr = area.getText().substring(0, area.getSelectionEnd());
			if (searchedStr.lastIndexOf(area.parent.FindStr.toLowerCase()) >= 0) {
				String text = area.getText();
				text = text.substring(0, searchedStr.lastIndexOf(area.parent.FindStr.toLowerCase()))
						+ area.parent.ReplaceStr
						+ text.substring(searchedStr.lastIndexOf(area.parent.FindStr.toLowerCase())
								+ area.parent.FindStr.length(), text.length());
				area.setText(text);

				area.setSelectionStart(searchedStr.lastIndexOf(area.parent.FindStr.toLowerCase()));
				area.setSelectionEnd(area.getSelectionStart() + area.parent.ReplaceStr.length());
			} else {
				java.awt.Toolkit.getDefaultToolkit().beep();
			}
		}
		if (cmd.equals("Replace All")) {
			area.setSelectionStart(0);
			while (true) {
				int savStart = area.getSelectionStart();
				String searchedStr = area.getText().substring(area.getSelectionStart(), area.getText().length())
						.toLowerCase();
				if (searchedStr.indexOf(area.parent.FindStr.toLowerCase()) >= 0) {
					String text = area.getText();
					text = text.substring(0,
							area.getSelectionStart() + searchedStr.indexOf(area.parent.FindStr.toLowerCase()))
							+ area.parent.ReplaceStr
							+ text.substring(
									area.getSelectionStart() + searchedStr.indexOf(area.parent.FindStr.toLowerCase())
											+ area.parent.FindStr.length(),
									text.length());
					area.setText(text);

					area.setSelectionStart(savStart + searchedStr.indexOf(area.parent.FindStr.toLowerCase()));
					area.setSelectionEnd(area.getSelectionStart() + area.parent.ReplaceStr.length());
				} else
					break;
			}
		}
		if (cmd.equals("Close")) {
			if (true == ScriptEditor.saveAlert(area)) {
				tabpane.remove(tabpane.getSelectedComponent());
				if (0 == tabpane.getComponentCount()) {
					ScriptEditor.maybeExit();
				}
			}
		}
		if (cmd.equals("Save")) {
			ScriptEditor.saveScript(area);
		}

		{
			OObject obj = area.object;

			if (cmd.equals("Edit Card Script")) {
				OCard cd = PCARD.pc.stack.curCard;
				if (obj.objectType.equals("card"))
					cd = (OCard) obj;
				else if (obj.parent != null && obj.parent.objectType.equals("card"))
					cd = (OCard) obj.parent;
				ScriptEditor.openScriptEditor(PCARD.pc.stack.pcard, cd);
			}
			if (cmd.equals("Edit Background Script")) {
				OBackground bg = PCARD.pc.stack.curCard.bg;
				if (obj.objectType.equals("background"))
					bg = (OBackground) obj;
				else if (obj.objectType.equals("card"))
					bg = ((OCard) obj).bg;
				else if (obj.parent != null && obj.parent.objectType.equals("background"))
					bg = (OBackground) obj.parent;
				ScriptEditor.openScriptEditor(PCARD.pc.stack.pcard, bg);
			}
			if (cmd.equals("Edit Stack Script")) {
				ScriptEditor.openScriptEditor(PCARD.pc.stack.pcard, PCARD.pc.stack);
			}
		}

		if (cmd.equals("Next Window")) {
			PCARD.pc.stack.pcard.toFront();
		}
		if (cmd.equals("Comment")) {
			String allScript = area.getText();
			int start = area.getSelectionStart();
			int end = area.getSelectionEnd();
			if (start == 0 && end == 0)
				return;
			if (end == 0)
				end = start;

			String newScript = allScript.substring(0, start);
			String[] scrAry = (allScript.substring(start, end)).split("\n");
			for (int i = 0; i < scrAry.length; i++) {
				int spacing = 0;
				while (spacing < scrAry[i].length()
						&& (scrAry[i].charAt(spacing) == ' ' || scrAry[i].charAt(spacing) == '　'))
					spacing++;
				scrAry[i] = scrAry[i].substring(0, spacing) + "-- " + scrAry[i].substring(spacing);

				newScript += scrAry[i];
				if (i < scrAry.length - 1)
					newScript += "\n";
			}
			newScript += allScript.substring(end);
			area.setText(newScript);
		}
		if (cmd.equals("Uncomment")) {
			String allScript = area.getText();
			int start = area.getSelectionStart();
			int end = area.getSelectionEnd();
			if (start == 0 && end == 0)
				return;
			if (end == 0)
				end = start;

			String newScript = allScript.substring(0, start);
			String[] scrAry = (allScript.substring(start, end)).split("\n");
			for (int i = 0; i < scrAry.length; i++) {
				int spacing = 0;
				while (spacing < scrAry[i].length()
						&& (scrAry[i].charAt(spacing) == ' ' || scrAry[i].charAt(spacing) == '　'))
					spacing++;
				int comment = 0;
				while (spacing < scrAry[i].length()
						&& (scrAry[i].charAt(spacing + comment) == ' ' || scrAry[i].charAt(spacing + comment) == '-'))
					comment++;
				scrAry[i] = scrAry[i].substring(0, spacing) + scrAry[i].substring(spacing + comment);

				newScript += scrAry[i];
				if (i < scrAry.length - 1)
					newScript += "\n";
			}
			newScript += allScript.substring(end);
			area.setText(newScript);
		}
		if (cmd.equals("Step")) {
			TTalk.stepflag = true;
		}
		if (cmd.equals("Trace")) {
			TTalk.tracemode = 1;
			TTalk.stepflag = true;
		}
		if (cmd.equals("Run")) {
			TTalk.tracemode = 0;
			TTalk.stepflag = true;
		}
		if (cmd.equals("Variable Watcher")) {
			VariableWatcher.watcherWindow.setVisible(true);
		}
	}
}

class FindDialog extends JDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static String clicked = "";
	JTextField area;

	FindDialog(ScriptEditor owner) {
		super(owner, true);
		setTitle(PCARD.pc.intl.getDialogText("Find String"));
		getContentPane().setLayout(new BorderLayout());

		// パネルを追加する
		JPanel topPanel = new JPanel();
		// topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		getContentPane().add("North", topPanel);
		JPanel btmPanel = new JPanel();
		btmPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add("South", btmPanel);

		String text = owner.FindStr;
		area = new JTextField(text);
		area.setPreferredSize(new Dimension(380, 28));
		// area.setSize(380, 20);
		// area.setMargin(new Insets(16,16,16,16));
		// area.setLineWrap(true);
		// area.setBorder(new LineBorder(Color.BLACK));
		topPanel.add(area);

		JButton btn1 = new JButton(PCARD.pc.intl.getDialogText("Find Prev"));
		btn1.addActionListener(this);
		btmPanel.add(btn1);

		JButton btn2 = new JButton(PCARD.pc.intl.getDialogText("Find Next"));
		btn2.addActionListener(this);
		btmPanel.add(btn2);
		getRootPane().setDefaultButton(btn2);

		setBounds(owner.getX() + owner.getWidth() / 2 - 200,
				owner.getY() + owner.getHeight() / 2 - area.getPreferredSize().height / 2, 400,
				area.getPreferredSize().height + 80);
		setResizable(false);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		clicked = e.getActionCommand();
		((ScriptEditor) getOwner()).FindStr = area.getText();
		this.dispose();
	}
}

class ReplaceDialog extends JDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static String clicked = "";
	JTextField area1;
	JTextField area2;

	ReplaceDialog(ScriptEditor owner) {
		super(owner, true);
		setTitle(PCARD.pc.intl.getDialogText("Replace String"));
		getContentPane().setLayout(new BorderLayout());

		// パネルを追加する
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		topPanel.setPreferredSize(new Dimension(380, 60));
		getContentPane().add("North", topPanel);
		JPanel btmPanel = new JPanel();
		btmPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add("South", btmPanel);

		String text1 = owner.FindStr;
		area1 = new JTextField(text1);
		area1.setPreferredSize(new Dimension(380, 28));
		// area1.setMargin(new Insets(8,16,8,16));
		// area1.setLineWrap(true);
		// area1.setBorder(new LineBorder(Color.BLACK));
		topPanel.add(area1);

		String text2 = owner.ReplaceStr;
		area2 = new JTextField(text2);
		area2.setPreferredSize(new Dimension(380, 28));
		// area2.setMargin(new Insets(8,16,8,16));
		// area2.setLineWrap(true);
		// area2.setBorder(new LineBorder(Color.BLACK));
		area2.transferFocus();
		topPanel.add(area2);
		topPanel.add(area1);

		// タブキーによる移動
		Set<KeyStroke> strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB")));
		area1.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, strokes);
		strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB")));
		area2.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, strokes);

		JButton btn3 = new JButton(PCARD.pc.intl.getDialogText("Replace All"));
		btn3.addActionListener(this);
		btmPanel.add(btn3);

		JButton btn1 = new JButton(PCARD.pc.intl.getDialogText("Replace Prev"));
		btn1.addActionListener(this);
		btmPanel.add(btn1);

		JButton btn2 = new JButton(PCARD.pc.intl.getDialogText("Replace Next"));
		btn2.addActionListener(this);
		btmPanel.add(btn2);
		getRootPane().setDefaultButton(btn2);

		setBounds(owner.getX() + owner.getWidth() / 2 - 200,
				owner.getY() + owner.getHeight() / 2 - area1.getPreferredSize().height, 400,
				area1.getPreferredSize().height * 2 + 110);
		setResizable(false);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		clicked = e.getActionCommand();
		((ScriptEditor) getOwner()).FindStr = area1.getText();
		((ScriptEditor) getOwner()).ReplaceStr = area2.getText();
		this.dispose();
	}
}