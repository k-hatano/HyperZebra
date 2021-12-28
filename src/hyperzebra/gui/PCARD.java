package hyperzebra.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import hyperzebra.HyperZebra;
import hyperzebra.International;
import hyperzebra.Pidle;
import hyperzebra.TTalk;
import hyperzebra.gui.dialog.GDialog;
import hyperzebra.gui.dialog.GMsg;
import hyperzebra.gui.menu.GMenu;
import hyperzebra.object.OStack;
import hyperzebra.object.OWindow;
import hyperzebra.subsystem.debugger.MessageWatcher;
import hyperzebra.subsystem.debugger.VariableWatcher;
import hyperzebra.tool.PaintIdle;
import hyperzebra.tool.PaintTool;
import hyperzebra.type.xTalkException;

public class PCARD extends PCARDFrame /* implements MRJOpenDocumentHandler */ {
	private static final long serialVersionUID = 1L;

	public static String AppName = "HyperZebra";

	public static String version = "3.0";
	public static String longVersion = "3.0a5";
	public GMenu menu;
	public GMenu paintMenu;
	public GMsg msg;
	public International intl;
	public String lang;
	public static boolean useDoubleBuffer = true;// バッファをオフにしてメモリ節約できる？
	public Pidle pidle;
	public PaintIdle paidle;
	DropTarget drop;
	MyDropTargetListener droplistener;
	public GToolBar toolbar;

	// プロパティ
	public static boolean lockedScreen = false;
	public static int visual = 0;
	public static int toVisual = 0; /* to black1 white2 grey3 inverse4 */
	public static int visSpd = 3; /* very fast1 fast2 normal3 slow4 veryslow5 */
	public static int editMode = 0; // 0:ブラウジング 1:アウトライン強調
	public static String scriptFont = "";
	public static int scriptFontSize = 12;
	public static int userLevel = 5;
	public String textFont = "";
	public int textSize = 12;
	public int textStyle = 0;
	// int textAlign = 0;
	public int foundIndex = 0;
	public String foundText;
	public String foundObject;

	public static void main(final String[] args) {
		pc = new PCARD();

		if (System.getProperty("user.language").equals("ja")) {
			pc.lang = "Japanese";
		} else {
			pc.lang = "English";
		}

		// ウィンドウアイコンの設定
		try {
			Image icon = ImageIO
					.read(new File("." + File.separatorChar + "resource" + File.separatorChar + "icon.png"));
			pc.setIconImage(icon);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		new OWindow(pc, true);
		pc.rootPane.setDoubleBuffered(useDoubleBuffer);
		pc.getRootPane().setOpaque(false);
		pc.setBackground(Color.WHITE);

		pc.setLayout(new BorderLayout());
		pc.mainPane = new MyPanel(pc);
		pc.mainPane.setDoubleBuffered(useDoubleBuffer);
		// pc.setContentPane(pc.mainPane/*, BorderLayout.CENTER*/);//こうするとツールバーがおかしくなる
		pc.add(pc.mainPane, BorderLayout.CENTER);

		Cursor cr = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		pc.setCursor(cr);

		try {
			pc.intl = new International(pc.lang);
		} catch (Exception e) {

		}

		pc.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		OStack ostack = new OStack(pc);
		pc.stack = ostack;

		pc.msg = new GMsg(pc, "");
		new VariableWatcher();
		new MessageWatcher();

		pc.menu = new GMenu(pc, 0);
		pc.paintMenu = new GMenu(pc, 1);
		JMenuBar menubar = new JMenuBar();
		pc.setJMenuBar(menubar);
		GMenu.menuUpdate(PCARD.pc.menu.mb);

		TTalk.talk = new TTalk(); // TTalkが異常終了したときのためにGUI.javaにスレッド再起動処理あり
		TTalk.talk.start();

		String homepath = "./home";
		if (new File(homepath).exists()) {
			// ostack.openStackFileInThread(homepath, true);
			try {
				TTalk.doScriptforMenu("start using stack " + "\"" + homepath + "\"");
			} catch (xTalkException e1) {
				e1.printStackTrace();
			}
			home = ostack;
		}

		String path = "./home";
		if (args.length > 0) {
			path = args[0];
		}

		if (path != null && new File(path).exists()) {
			ostack.openStackFile(path, false);
		} else if (home != null) {
			home.buildStackFile(false);
		} else {
			pc.failureOpenFile(path);
		}

		HyperZebra.installMacHandler();

		// idle処理
		pc.pidle = new Pidle();
		pc.pidle.start();

		pc.paidle = new PaintIdle();
		pc.paidle.start();
	}

	public void successOpenFile() {
		PCARD.lockedScreen = true;

		if (pc.drop != null) {
			pc.drop.removeDropTargetListener(pc.droplistener);
			pc.droplistener = null;
		}
		PaintTool.owner = PCARDFrame.pc;
		PCARDFrame.pc.stack.buildStackFile(false);

		// 最近使ったスタックに追加
		if (PCARDFrame.pc.stack.path.startsWith("./home")) {
			return;
		}

		// まず読み込み
		File recentFile = new File("resource_trash" + File.separatorChar + "recent.txt");
		String[] recents = new String[1];
		if (recentFile.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(recentFile);
				int length = (int) recentFile.length();
				byte[] b = new byte[length];
				fis.read(b);
				String recentStr = new String(b);
				recentStr = " \n" + recentStr;
				recents = recentStr.split("\n");
				fis.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 最初の行に追加
		String path = PCARDFrame.pc.stack.path;
		if (new File(path).getName().equals("_stack.xml") || new File(path).getName().equals("toc.xml")) {
			path = new File(path).getParent();
		}
		recents[0] = path;

		// ファイルに書き出し
		{
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(recentFile);
				for (int i = 0; i < recents.length; i++) {
					if (i == 0 || !recents[i].equals(path)) {
						fos.write(recents[i].getBytes());
						if (i < recents.length - 1)
							fos.write("\n".getBytes());
					}
				}
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		GMenu.BuildRecentMenu((JMenu) PCARD.pc.getJMenuBar().getComponent(0));
	}

	public void failureOpenFile(String path) {
		if (pc.stack.curCard != null) {
			pc.stack.curCard.removeData();
			if (pc.stack.curCard.bg != null)
				pc.stack.curCard.bg.removeData();
		}
		pc.emptyWindow();
		if (!path.equals("./home")) {
			new GDialog(pc, PCARDFrame.pc.intl.getDialogText("Could't open the file."), null, "OK", null, null);
		}
	}

	public void emptyWindow() {
		setTitle(PCARDFrame.pc.intl.getDialogText("Drop file here"));

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		if (d != null) {
			setBounds(d.width / 2 - 400 / 2, d.height / 2 - 320 / 2 - 20, 400, 320 + 20);
		} else {
			setBounds(0, 0 - 320 - 20, 400, 320 + 20);
		}
		setVisible(true);
		// setLocationRelativeTo(null);
		droplistener = new MyDropTargetListener();
		drop = new DropTarget(pc.mainPane, droplistener);
	}

	private class MyDropTargetListener extends DropTargetAdapter {
		/*
		 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
		 */
		@Override
		public void drop(DropTargetDropEvent e) {
			try {
				Transferable transfer = e.getTransferable();
				if (transfer.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					if (pc.stack != null) {
						pc.stack.clean();
						pc.stack = new OStack(pc);
					}
					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					@SuppressWarnings("unchecked")
					List<File> fileList = (List<File>) (transfer.getTransferData(DataFlavor.javaFileListFlavor));
					for (int i = 0; i < fileList.size(); i++) {
						pc.stack.openStackFile(fileList.get(i).toString(), false);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	PCARD() {
		GUI.gui.addMouseListener(this);
		this.addKeyListener(GUI.gui);
		// this.addWindowListener(paintGUI.gui);
		this.addWindowListener(new PCARDWindowListener(this));
	}
}

class PCARDWindowListener implements WindowListener {
	PCARD owner;

	PCARDWindowListener(PCARD owner) {
		this.owner = owner;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		if (owner != null && owner.toolbar != null) {
			owner.toolbar.activate();
		}
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		if (owner != null && owner.toolbar != null) {
			owner.toolbar.deactivate();
		}
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}
