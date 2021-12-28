package hyperzebra.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
//import java.awt.event.WindowEvent;
//import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

//import javax.swing.JMenu;
//import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import hyperzebra.PictureFile;
import hyperzebra.Pidle;
import hyperzebra.TTalk;
import hyperzebra.gui.field.MyTextArea;
import hyperzebra.object.OButton;
import hyperzebra.object.OField;
import hyperzebra.object.OObject;
import hyperzebra.subsystem.scripteditor.ScriptEditor;
import hyperzebra.tool.AuthTool;
import hyperzebra.tool.ButtonTool;
import hyperzebra.tool.FieldTool;
import hyperzebra.tool.SelectTool;

//マウス、キーボードを使うものをここに集めています。

public class GUI implements MouseListener, MouseMotionListener, KeyListener {
	public static GUI gui = new GUI();

	// マウス
	public static int mouseH = 0;
	public static int mouseV = 0;
	public static int clickH = 0;
	public static int clickV = 0;
	public static boolean mouseClicked = false;
	public static boolean mouseDown = false;
	public static String clickLine = "";
	public static OField clickField;

	// キー
	public static int[] key = new int[256];

	public static void mouseClicked() {
		mouseClicked = true;
		mouseDown = false;

		if (TTalk.talk != null && !TTalk.talk.isAlive()) {
			TTalk.talk = new TTalk();
			TTalk.talk.start();

			if (!PCARD.pc.pidle.isAlive()) {
				PCARD.pc.pidle = new Pidle();
				PCARD.pc.pidle.start();
			}
		}
	}

	public static void mouseDowned() {
		mouseDown = true;
	}

	public static void mouseUped() {
		mouseDown = false;
	}

	public void addMouseListener(PCARD pc) {
		pc.addMouseListener(this);
		pc.addMouseMotionListener(this);
	}

	void removeMouseListener(PCARD pc) {
		pc.removeMouseListener(this);
		pc.removeMouseMotionListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		if (PCARD.pc.stack == null || PCARD.pc.stack.curCard == null)
			return;
		TTalk.CallMessage("mouseUp", PCARD.pc.stack.curCard);
		clickH = e.getX();
		clickV = e.getY();
		mouseClicked();
	}

	static MyTextArea lastFocusArea;

	public void mousePressed(MouseEvent e) {
		if (PCARD.pc.stack == null || PCARD.pc.stack.curCard == null)
			return;

		// フィールドからフォーカスを取り戻す
		if (PCARD.pc.getFocusOwner() != null && PCARD.pc.getFocusOwner().getClass() == MyTextArea.class) {
			lastFocusArea = (MyTextArea) PCARD.pc.getFocusOwner();
			PCARD.pc.requestFocusInWindow();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					// 一度消してやらないと最前面に描画されたまま後始末しない
					lastFocusArea.setVisible(false);
					lastFocusArea.setVisible(true);
				}
			});
		}

		TTalk.CallMessage("mouseDown", PCARD.pc.stack.curCard);
		mouseDowned();
	}

	public void mouseReleased(MouseEvent e) {
		mouseDown = false;
		mouseUped();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		mouseH = e.getX();
		mouseV = e.getY();
	}

	public void mouseMoved(MouseEvent e) {
		mouseH = e.getX();
		mouseV = e.getY();
	}

	// Key[]について
	// 0:押されてないとき
	// 3:押された直後
	// 2:押された(3の後)
	// 1:離された直後

	public void keyPressed(KeyEvent e) {
		// System.out.println("keyPressed:"+e.getKeyCode());
		int k = e.getKeyCode();
		if (k >= 32 && k < 128) { // 数字、記号はそのまま、アルファベットは大文字のcode
			if (k > 'a' && k <= 'z')
				k -= 'a' - 'A';
			if (key[k] == 0)
				key[k] = 3;
		}

		int c = -1;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			c = 0;
			break;
		case KeyEvent.VK_DOWN:
			c = 1;
			break;
		case KeyEvent.VK_LEFT:
			c = 2;
			break;
		case KeyEvent.VK_RIGHT:
			c = 3;
			break;
		case KeyEvent.VK_ENTER:
			c = 10;
			break;
		case KeyEvent.VK_SHIFT:
			c = 11;
			break;
		case KeyEvent.VK_ALT:
			c = 12;
			break;
		case KeyEvent.VK_CONTROL:
			c = 13;
			break;
		case KeyEvent.VK_META:
			c = 14;
			break;
		case KeyEvent.VK_CAPS_LOCK:
			c = 15;
			break;
		case KeyEvent.VK_WINDOWS:
			c = 16;
			break;
		case KeyEvent.VK_BACK_SPACE:
			c = 20;
			break;
		case KeyEvent.VK_DELETE:
			c = 21;
			break;
		case KeyEvent.VK_ESCAPE:
			c = 22;
			break;
		case KeyEvent.VK_NUMPAD0:
			c = 100;
			break;
		case KeyEvent.VK_NUMPAD1:
			c = 101;
			break;
		case KeyEvent.VK_NUMPAD2:
			c = 102;
			break;
		case KeyEvent.VK_NUMPAD3:
			c = 103;
			break;
		case KeyEvent.VK_NUMPAD4:
			c = 104;
			break;
		case KeyEvent.VK_NUMPAD5:
			c = 105;
			break;
		case KeyEvent.VK_NUMPAD6:
			c = 106;
			break;
		case KeyEvent.VK_NUMPAD7:
			c = 107;
			break;
		case KeyEvent.VK_NUMPAD8:
			c = 108;
			break;
		case KeyEvent.VK_NUMPAD9:
			c = 109;
			break;
		// cが110-116は左shift,alt,ctrl,meta用
		// cが116-122は右shift,alt,ctrl,meta用
		case 107:
			c = '+';
			break;
		case 109:
			c = '-';
			break;
		}
		if (c >= 0) {
			if (key[c] == 0)
				key[c] = 3;
			if (c == 13 && !System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
				c = 14; // OSX以外ではctrlキーをcmdキーとしても認識
				if (key[c] == 0)
					key[c] = 3;
			}
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT && c >= 11 && c <= 16) {
				c += 100;
				if (key[c] == 0)
					key[c] = 3;
			} else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT && c >= 11 && c <= 16) {
				c += 106;
				if (key[c] == 0)
					key[c] = 3;
			}
		}

		if (AuthTool.tool != null) {
			if (AuthTool.tool.getClass() == ButtonTool.class) {
				ButtonGUI.gui.keyDown();
			} else if (AuthTool.tool.getClass() == FieldTool.class) {
				FieldGUI.gui.keyDown();
			}
		} else {
			String keyStr = null;
			if (c == -1 && k >= 32 && k <= 128) {
				keyStr = Character.toString((char) k);
			} else if (c == 0)
				keyStr = Character.toString('↑');
			else if (c == 1)
				keyStr = Character.toString('↓');
			else if (c == 2)
				keyStr = Character.toString('←');
			else if (c == 3)
				keyStr = Character.toString('→');
			else if (c == 20)
				keyStr = Character.toString('⌫');
			else if (c == 21)
				keyStr = Character.toString('⌦');
			else if (c == 22)
				keyStr = Character.toString('⎋');
			else if (c >= 100 && c <= 109) {
				keyStr = Character.toString((char) (c - 100 + '0'));
			}
			if (keyStr != null) {
				String message = "keyDown";
				if (key[14] > 0)
					message = "commandKeyDown";
				TTalk.CallMessage(message, keyStr, PCARD.pc.stack.curCard, false, false);
			}

		}
	}

	public void keyReleased(KeyEvent e) {
		// System.out.println("keyReleased:"+e.getKeyCode());
		int k = e.getKeyCode();
		if (k >= 32 && k < 128) {
			if (k > 'a' && k <= 'z')
				k -= 'a' - 'A';
			if (key[k] == 2)
				key[k] = 0;
			if (key[k] == 3)
				key[k] = 1;
		}

		int c = -1;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			c = 0;
			break;
		case KeyEvent.VK_DOWN:
			c = 1;
			break;
		case KeyEvent.VK_LEFT:
			c = 2;
			break;
		case KeyEvent.VK_RIGHT:
			c = 3;
			break;
		case KeyEvent.VK_ENTER:
			c = 10;
			break;
		case KeyEvent.VK_SHIFT:
			c = 11;
			break;
		case KeyEvent.VK_ALT:
			c = 12;
			break; // opt
		case KeyEvent.VK_CONTROL:
			c = 13;
			break;
		case KeyEvent.VK_META:
			c = 14;
			break; // cmd
		case KeyEvent.VK_CAPS_LOCK:
			c = 15;
			break;
		case KeyEvent.VK_WINDOWS:
			c = 16;
			break;
		case KeyEvent.VK_BACK_SPACE:
			c = 20;
			break;
		case KeyEvent.VK_DELETE:
			c = 21;
			break;
		case KeyEvent.VK_NUMPAD0:
			c = 100;
			break;
		case KeyEvent.VK_NUMPAD1:
			c = 101;
			break;
		case KeyEvent.VK_NUMPAD2:
			c = 102;
			break;
		case KeyEvent.VK_NUMPAD3:
			c = 103;
			break;
		case KeyEvent.VK_NUMPAD4:
			c = 104;
			break;
		case KeyEvent.VK_NUMPAD5:
			c = 105;
			break;
		case KeyEvent.VK_NUMPAD6:
			c = 106;
			break;
		case KeyEvent.VK_NUMPAD7:
			c = 107;
			break;
		case KeyEvent.VK_NUMPAD8:
			c = 108;
			break;
		case KeyEvent.VK_NUMPAD9:
			c = 109;
			break;
		// cが110-116は左shift,alt,ctrl,meta用
		// cが116-122は右shift,alt,ctrl,meta用
		case 107:
			c = '+';
			break;
		case 109:
			c = '-';
			break;
		}
		if (c >= 0) {
			if (key[c] == 2)
				key[c] = 0;
			if (key[c] == 3)
				key[c] = 1;
			if (c == 13 && !System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
				c = 14; // OSX以外ではctrlキーをcmdキーとしても認識
				if (key[c] == 2)
					key[c] = 0;
				if (key[c] == 3)
					key[c] = 1;
			}
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT && c >= 11 && c <= 16) {
				c += 100;
				if (key[c] == 2)
					key[c] = 0;
				if (key[c] == 3)
					key[c] = 1;
			} else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT && c >= 11 && c <= 16) {
				c += 106;
				if (key[c] == 2)
					key[c] = 0;
				if (key[c] == 3)
					key[c] = 1;
			}
		}
	}

	public void keyTyped(KeyEvent e) {
		// 文字が入力された場合に来るらしい

		/*
		 * int k = e.getKeyCode(); if(k>=32 && k<128){ if(k>'a' && k<='z') k -= 'a'-'A';
		 * key[k] = 1; }
		 */
	}

	public static void keyEventCheck() {
		boolean isOpened = false;
		// if( PCARDFrame.pc.isFocused() ){
		for (int i = 0; i < 128; i++) {
			if (key[i] == 1 || key[i] == 3) {
				if (TTalk.idle) {
					// 特殊ショートカットキー
					if (i >= 0 && i <= 3) {
						if (i == 0)
							TTalk.CallMessage("arrowKey", "up", PCARDFrame.pc.stack.curCard, true, false);
						else if (i == 1)
							TTalk.CallMessage("arrowKey", "down", PCARDFrame.pc.stack.curCard, true, false);
						else if (i == 2)
							TTalk.CallMessage("arrowKey", "left", PCARDFrame.pc.stack.curCard, true, false);
						else if (i == 3)
							TTalk.CallMessage("arrowKey", "right", PCARDFrame.pc.stack.curCard, true, false);
					}
				}
				if (key[12] == 0 && key[14] == 2 && i == '.') {
					// スクリプト強制停止
					if (false == PCARDFrame.pc.stack.cantAbort)
						TTalk.stop = true;
				}
				if (key[12] == 2 && key[14] == 2 && i == '.') {
					// スクリプトトレース
					/* if(false==PCARDFrame.pc.stack.cantAbort) */ TTalk.tracemode = 2;
					ScriptEditor.setTracemode();
					isOpened = true;
					addAllListener();
				}
				if (key[12] == 2 && key[14] == 2 && !isOpened && !PCARD.pc.stack.cantPeek) {
					isOpened = true;
					if (i == 'C' || i == '1' || i == 101) {
						ScriptEditor.openScriptEditor(PCARDFrame.pc, PCARDFrame.pc.stack.curCard);
						addAllListener();
						key[12] = 0;
						key[14] = 0;
					}
					if (i == 'B' || i == '2' || i == 102) {
						ScriptEditor.openScriptEditor(PCARDFrame.pc, PCARDFrame.pc.stack.curCard.bg);
						addAllListener();
						key[12] = 0;
						key[14] = 0;
					}
					if (i == 'S' || i == '3' || i == 103) {
						ScriptEditor.openScriptEditor(PCARDFrame.pc, PCARDFrame.pc.stack);
						addAllListener();
						key[12] = 0;
						key[14] = 0;
					}
				}
			}
			if (key[i] == 3)
				key[i] = 2;
			if (key[i] == 1)
				key[i] = 0;
		}
		// }
		// else{
		// for(int i=0; i<128; i++){
		// key[i]=0;
		// }
		// }

		// optとcmd同時押しでボタンを破線で浮かび上がらせる
		if (TTalk.idle && false == PCARDFrame.pc.stack.cantPeek)
			outlineStrokeCheck();

		if (AuthTool.tool != null && AuthTool.tool.getClass().equals(ButtonGUI.class) && ButtonGUI.gui.target != null) {
			ButtonGUI.drawSelectBorder(ButtonGUI.gui.target);
		} else if (AuthTool.tool != null && AuthTool.tool.getClass().equals(FieldGUI.class)
				&& FieldGUI.gui.target != null) {
			FieldGUI.drawSelectBorder(FieldGUI.gui.target);
		}
	}

	static final private BasicStroke STROKE_DOTTED = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
			2.0f, new float[] { 2f }, 0.0f);

	static int stroke_frag = 0;

	public static void outlineStrokeCheck() {
		if (key[12] >= 2 && key[14] >= 2) {
			Graphics g = PCARDFrame.pc.mainPane.getGraphics();
			if (key[11] >= 2 && stroke_frag != 2) {
				if (stroke_frag != 0) {
					addAllListener();
					PCARD.editMode = 0;
					stroke_frag = 0;
				} else {
					fldStroke(g);
					stroke_frag = 2;
					if (PCARD.editMode == 0) {
						PCARD.editMode = 1;
						GUI.removeAllListener();
						gui.addOutlineListener();
					}
				}
			} else if (key[11] < 2 && stroke_frag != 1) {
				if (stroke_frag != 0) {
					addAllListener();
					PCARD.editMode = 0;
					stroke_frag = 0;
				} else {
					btnStroke(PCARDFrame.pc.mainPane.getGraphics());
					stroke_frag = 1;
					if (PCARD.editMode == 0) {
						PCARD.editMode = 1;
						GUI.removeAllListener();
						gui.addOutlineListener();
					}
				}
			}
		} else if (stroke_frag >= 1) {
			PCARDFrame.pc.mainPane.repaint();
			stroke_frag = 0;
			PCARD.editMode = 0;
			addAllListener();
		}
	}

	static void noListener() {
		// PCARDFrame.pc.removeMouseListener(outlineListen);
		for (int i = 0; i < PCARDFrame.pc.stack.curCard.btnList.size(); i++) {
			PCARDFrame.pc.stack.curCard.btnList.get(i).removeListener(OButton.btnOutlineListen);
		}
		for (int i = 0; i < PCARDFrame.pc.stack.curCard.fldList.size(); i++) {
			PCARDFrame.pc.stack.curCard.fldList.get(i).removeListener(OButton.btnOutlineListen);
		}
		if (PCARDFrame.pc.stack.curCard.bg != null) {
			for (int i = 0; i < PCARDFrame.pc.stack.curCard.bg.btnList.size(); i++) {
				PCARDFrame.pc.stack.curCard.bg.btnList.get(i).removeListener(OButton.btnOutlineListen);
			}
			for (int i = 0; i < PCARDFrame.pc.stack.curCard.bg.fldList.size(); i++) {
				PCARDFrame.pc.stack.curCard.bg.fldList.get(i).removeListener(OButton.btnOutlineListen);
			}
		}
	}

	// アウトライン強調モードから通常状態に移行
	public static void addAllListener() {
		PCARDFrame.pc.mainPane.repaint();
		// PCARDFrame.pc.removeMouseListener(outlineListen);
		PCARDFrame.pc.stack.pcard.addMouseListener(gui);
		PCARDFrame.pc.mainPane.addMouseListener(gui);
		for (int i = 0; i < PCARDFrame.pc.stack.curCard.btnList.size(); i++) {
			PCARDFrame.pc.stack.curCard.btnList.get(i).removeListener(OButton.btnOutlineListen);
			PCARDFrame.pc.stack.curCard.btnList.get(i).addListener();
		}
		for (int i = 0; i < PCARDFrame.pc.stack.curCard.fldList.size(); i++) {
			PCARDFrame.pc.stack.curCard.fldList.get(i).removeListener(OField.fldOutlineListen);
			PCARDFrame.pc.stack.curCard.fldList.get(i).addListener();
		}
		if (PCARDFrame.pc.stack.curCard.bg != null) {
			for (int i = 0; i < PCARDFrame.pc.stack.curCard.bg.btnList.size(); i++) {
				PCARDFrame.pc.stack.curCard.bg.btnList.get(i).removeListener(OButton.btnOutlineListen);
				PCARDFrame.pc.stack.curCard.bg.btnList.get(i).addListener();
			}
			for (int i = 0; i < PCARDFrame.pc.stack.curCard.bg.fldList.size(); i++) {
				PCARDFrame.pc.stack.curCard.bg.fldList.get(i).removeListener(OField.fldOutlineListen);
				PCARDFrame.pc.stack.curCard.bg.fldList.get(i).addListener();
			}
		}
		PCARDFrame.pc.getJMenuBar().setEnabled(true);
	}

	// アウトライン強調モードに移行
	public static void removeAllListener() {
		PCARDFrame.pc.stack.pcard.removeMouseListener(gui);
		PCARDFrame.pc.mainPane.removeMouseListener(gui);
		for (int i = 0; i < PCARDFrame.pc.stack.curCard.btnList.size(); i++) {
			PCARDFrame.pc.stack.curCard.btnList.get(i).removeListener();
		}
		for (int i = 0; i < PCARDFrame.pc.stack.curCard.bg.btnList.size(); i++) {
			PCARDFrame.pc.stack.curCard.bg.btnList.get(i).removeListener();
		}
		for (int i = 0; i < PCARDFrame.pc.stack.curCard.fldList.size(); i++) {
			PCARDFrame.pc.stack.curCard.fldList.get(i).removeListener();
		}
		for (int i = 0; i < PCARDFrame.pc.stack.curCard.bg.fldList.size(); i++) {
			PCARDFrame.pc.stack.curCard.bg.fldList.get(i).removeListener();
		}
	}

	void addOutlineListener() {
		PCARD.pc.removeMouseListener(gui);
		// PCARD.pc.addMouseListener(outlineListen);
		for (int i = 0; i < PCARD.pc.stack.curCard.btnList.size(); i++) {
			PCARD.pc.stack.curCard.btnList.get(i).addListener(OButton.btnOutlineListen);
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.bg.btnList.size(); i++) {
			PCARD.pc.stack.curCard.bg.btnList.get(i).addListener(OButton.btnOutlineListen);
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.fldList.size(); i++) {
			PCARD.pc.stack.curCard.fldList.get(i).addListener(OField.fldOutlineListen);
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.bg.fldList.size(); i++) {
			PCARD.pc.stack.curCard.bg.fldList.get(i).addListener(OField.fldOutlineListen);
		}
		PCARD.pc.getJMenuBar().setEnabled(false);
	}

	public static void btnStroke(Graphics g) {
		if (PCARDFrame.pc.stack.curCard == null)
			return;
		Graphics2D g2 = (Graphics2D) g;
		for (int i = 0; i < PCARDFrame.pc.stack.curCard.btnList.size(); i++) {
			OObject obj = PCARDFrame.pc.stack.curCard.btnList.get(i);
			dotStroke(g2, obj);
		}
		if (PCARDFrame.pc.stack.curCard.bg != null) {
			for (int i = 0; i < PCARDFrame.pc.stack.curCard.bg.btnList.size(); i++) {
				OObject obj = PCARDFrame.pc.stack.curCard.bg.btnList.get(i);
				dotStroke(g2, obj);
			}
		}
	}

	public static void fldStroke(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		for (int i = 0; i < PCARDFrame.pc.stack.curCard.fldList.size(); i++) {
			OObject obj = PCARDFrame.pc.stack.curCard.fldList.get(i);
			dotStroke(g2, obj);
		}
		for (int i = 0; i < PCARDFrame.pc.stack.curCard.bg.fldList.size(); i++) {
			OObject obj = PCARDFrame.pc.stack.curCard.bg.fldList.get(i);
			dotStroke(g2, obj);
		}
	}

	public static void dotStroke(Graphics2D g2, OObject obj) {
		g2.setStroke(new BasicStroke(1.0f));
		g2.setPaint(Color.WHITE);
		g2.draw(new Rectangle2D.Float(obj.left, obj.top, obj.width - 1, obj.height - 1));
		g2.setPaint(Color.BLACK);
		g2.setStroke(STROKE_DOTTED);
		g2.draw(new Rectangle2D.Float(obj.left, obj.top, obj.width - 1, obj.height - 1));
	}

	// static outlineListen outlineListen = new outlineListen();
}

class MyPaintDropListener extends DropTargetAdapter {
	public void drop(DropTargetDropEvent e) {
		try {
			Transferable transfer = e.getTransferable();
			if (transfer.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				@SuppressWarnings("unchecked")
				List<File> fileList = (List<File>) (transfer.getTransferData(DataFlavor.javaFileListFlavor));
				String path = fileList.get(0).toString();
				BufferedImage bi = null;
				try {
					bi = PictureFile.loadPbm(path);
					if (bi == null) {
						bi = javax.imageio.ImageIO.read(new File(path));
					}
					if (bi == null) {
						bi = PictureFile.loadPICT(path);
					}
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				if (bi != null) {
					PCARD.pc.toolbar.selectToolButton.doClick();
					PCARD.pc.redoBuf = bi;
					((SelectTool) PCARD.pc.tool).move = true;
					((SelectTool) PCARD.pc.tool).srcRect = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());
					((SelectTool) PCARD.pc.tool).moveRect = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());
					PCARD.pc.mainPane.repaint();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
