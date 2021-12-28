package hyperzebra.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.border.LineBorder;

import hyperzebra.gui.dialog.AuthDialog;
import hyperzebra.gui.dialog.GDialog;
import hyperzebra.gui.field.MyScrollPane;
import hyperzebra.gui.field.MyTextArea;
import hyperzebra.object.OCard;
import hyperzebra.object.OCardBase;
import hyperzebra.object.OField;
import hyperzebra.tool.PaintTool;

public class FieldGUI implements MouseListener, MouseMotionListener {
	public static FieldGUI gui = new FieldGUI();
	int startx;
	int starty;
	int offsetx;
	int offsety;
	int lastx;
	int lasty;
	boolean sizeChange;
	boolean sizeleft;
	boolean sizetop;
	public Component target;
	public OField tgtOFld;
	boolean newField;
	MyFieldDropListener droplistener = new MyFieldDropListener();
	boolean isMouseDown;
	LineBorder lineBorder = new LineBorder(Color.BLACK);

	public void addListenerToParts() {
		for (int i = 0; i < PCARD.pc.stack.curCard.fldList.size(); i++) {
			OField fld = PCARD.pc.stack.curCard.fldList.get(i);

			if (fld.scrollPane != null) {
				PCARDFrame.pc.mainPane.remove(fld.scrollPane);
				fld.scrollPane = null;
				fld.fld = new MyTextArea(fld.getText());
				fld.fld.fldData = fld;
				fld.fld.setBounds(fld.left, fld.top, fld.width, fld.height);
				fld.fld.setVisible(true);
				PCARDFrame.pc.mainPane.add(fld.fld, 0);
			}

			fld.addListener(this);
			fld.addMotionListener(this);

			if (fld.fld != null) {
				fld.drop = new DropTarget(fld.getComponent(), droplistener);
				fld.fld.setFocusable(false);
				fld.fld.setEnabled(false);
				if (fld.style == 1 || fld.style == 2) {
					fld.fld.setBorder(lineBorder);
				}
			}
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.bg.fldList.size(); i++) {
			OField fld = PCARD.pc.stack.curCard.bg.fldList.get(i);

			if (fld.scrollPane != null) {
				PCARDFrame.pc.mainPane.remove(fld.scrollPane);
				fld.scrollPane = null;
				fld.fld = new MyTextArea(fld.getText());
				fld.fld.fldData = fld;
				fld.fld.setBounds(fld.left, fld.top, fld.width, fld.height);
				fld.fld.setVisible(true);
				PCARDFrame.pc.mainPane.add(fld.fld, 0);
			}

			fld.addListener(this);
			fld.addMotionListener(this);

			if (fld.fld != null) {
				fld.drop = new DropTarget(fld.fld, droplistener);
				fld.fld.setFocusable(false);
				fld.fld.setEnabled(false);
				if (fld.style == 1 || fld.style == 2) {
					fld.fld.setBorder(lineBorder);
				}
			}
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.btnList.size(); i++) {
			PCARD.pc.stack.curCard.btnList.get(i).addListener(this);
			PCARD.pc.stack.curCard.btnList.get(i).addMotionListener(this);
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.bg.btnList.size(); i++) {
			PCARD.pc.stack.curCard.bg.btnList.get(i).addListener(this);
			PCARD.pc.stack.curCard.bg.btnList.get(i).addMotionListener(this);
		}
	}

	public void removeListenerFromParts() {
		for (int i = 0; i < PCARD.pc.stack.curCard.fldList.size(); i++) {
			OField fld = PCARD.pc.stack.curCard.fldList.get(i);
			fld.removeListener(this);
			fld.removeMotionListener(this);
			if (fld.drop != null) {
				fld.drop.removeDropTargetListener(FieldGUI.gui.droplistener);
				fld.drop = null;
			}
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.bg.fldList.size(); i++) {
			OField fld = PCARD.pc.stack.curCard.bg.fldList.get(i);
			fld.removeListener(this);
			fld.removeMotionListener(this);
			if (fld.drop != null) {
				fld.drop.removeDropTargetListener(FieldGUI.gui.droplistener);
				fld.drop = null;
			}
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.btnList.size(); i++) {
			PCARD.pc.stack.curCard.btnList.get(i).removeListener(this);
			PCARD.pc.stack.curCard.btnList.get(i).removeMotionListener(this);
		}
		for (int i = 0; i < PCARD.pc.stack.curCard.bg.btnList.size(); i++) {
			PCARD.pc.stack.curCard.bg.btnList.get(i).removeListener(this);
			PCARD.pc.stack.curCard.bg.btnList.get(i).removeMotionListener(this);
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	@SuppressWarnings("unchecked")
	public void mousePressed(MouseEvent e) {
		Rectangle r = ((Component) e.getSource()).getBounds();
		int x = e.getX() + r.x;
		int y = e.getY() + r.y;
		if ((Component) e.getSource() == PCARD.pc.getRootPane()) {
			x -= PCARD.pc.toolbar.getTWidth();
			y -= PCARD.pc.toolbar.getTHeight() + PCARD.pc.getInsets().top + PCARD.pc.getJMenuBar().getHeight();
		}

		if (target != null) {
			target.repaint();
		}

		newField = false;
		if ((e.getModifiersEx() & InputEvent.META_DOWN_MASK) > 0
				|| (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) > 0) { // 新規フィールド
			OCardBase cdbase = PCARD.pc.stack.curCard;
			if (PaintTool.editBackground) {
				cdbase = PCARD.pc.stack.curCard.bg;
			}
			newField = true;
			int newid = 1;
			for (; newid < 32767; newid++) {
				if (cdbase.GetPartbyId(newid) == null) {
					break;
				}
			}
			OField ofld = null;
			ofld = new OField(cdbase, newid);
			if (ofld != null) {
				sizeChange = true;
				ofld.fld = new MyTextArea("");
				ofld.fld.fldData = ofld;
				((OCardBase) ofld.parent).partsList.add(ofld);
				((OCardBase) ofld.parent).fldList.add(ofld);
				target = ofld.fld;
				tgtOFld = ofld;

				// 外観変更
				{
					ofld.fld.setBounds(x, y, 2, 2);
					ofld.style = 1;
					ofld.enabled = false;
					ofld.fld.setOpaque(false);
					PCARD.pc.mainPane.add(ofld.fld, 0);
				}
			}
		} else if ((e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) > 0) {
			OCardBase cdbase = PCARD.pc.stack.curCard;
			if (PaintTool.editBackground) {
				cdbase = PCARD.pc.stack.curCard.bg;
			}
			newField = true;
			int newid = 1;
			for (; newid < 32767; newid++) {
				if (cdbase.GetPartbyId(newid) == null) {
					break;
				}
			}
			OField ofld = null;
			ofld = new OField(cdbase, newid);
			((OCardBase) ofld.parent).partsList.add(ofld);
			((OCardBase) ofld.parent).fldList.add(ofld);
			if (ofld != null) {
				OField obtn2 = null;
				Component target = (Component) e.getSource();
				if (target.getClass() == MyTextArea.class)
					obtn2 = ((MyTextArea) target).fldData;
				else if (target.getClass() == MyScrollPane.class)
					obtn2 = ((MyScrollPane) target).fldData;
				ofld.card = obtn2.card;
				ofld.color = obtn2.color;
				ofld.bgColor = obtn2.bgColor;
				ofld.left = obtn2.left;
				ofld.top = obtn2.top;
				ofld.width = obtn2.width;
				ofld.height = obtn2.height;
				ofld.style = obtn2.style;
				ofld.textAlign = obtn2.textAlign;
				ofld.textFont = obtn2.textFont;
				ofld.textHeight = obtn2.textHeight;
				ofld.textSize = obtn2.textSize;
				ofld.textStyle = obtn2.textStyle;
				ofld.autoSelect = obtn2.autoSelect;
				ofld.fixedLineHeight = obtn2.fixedLineHeight;
				ofld.autoTab = obtn2.autoTab;
				ofld.dontSearch = obtn2.dontSearch;
				ofld.dontWrap = obtn2.dontWrap;
				ofld.scroll = obtn2.scroll;
				ofld.sharedText = obtn2.sharedText;
				ofld.showLines = obtn2.showLines;
				ofld.hilite = obtn2.hilite;
				ofld.multipleLines = obtn2.multipleLines;
				ofld.wideMargins = obtn2.wideMargins;

				ofld.parent = obtn2.parent;
				ofld.name = obtn2.name;
				ofld.setText(obtn2.getText());
				ofld.scriptList = (ArrayList<String>) obtn2.scriptList.clone();
				ofld.stringList = new ArrayList[obtn2.scriptList.size()];
				ofld.typeList = new ArrayList[obtn2.scriptList.size()];
				ofld.enabled = obtn2.enabled;
				ofld.setVisible(obtn2.getVisible());

				// 外観変更
				{
					OField.buildOField(ofld);
					tgtOFld = ofld;
					this.target = ofld.getComponent();
					PCARD.pc.mainPane.add(this.target, 0);
				}
			}
		} else if (e.getSource().getClass() == MyTextArea.class || e.getSource().getClass() == MyScrollPane.class) {
			target = (Component) e.getSource();
			if (target != null) {
				if ((x < target.getX() + 8 || x > target.getX() + target.getWidth() - 8)
						&& (y < target.getY() + 8 || y > target.getY() + target.getHeight() - 8)) {
					sizeChange = true;
					sizeleft = (x < target.getX() + 8);
					sizetop = (y < target.getY() + 8);
				} else {
					sizeChange = false;
				}
			}
		} else {
			target = null;
		}
		startx = x; // 最初の位置を記憶
		starty = y;
		offsetx = e.getX(); // ポインタと部品のオフセットを記憶
		offsety = e.getY();
		lastx = x; // サイズの変更用に前回の位置を記憶
		lasty = y;

		isMouseDown = true;

		if (target != null) {
			// ドラッグ時の描画が重いのでスレッド内でぶん回してみる
			Thread p = new dragThread();
			p.start();
		}
	}

	public void mouseReleased(MouseEvent e) {
		/*
		 * Rectangle r = ((Component)e.getSource()).getBounds(); int x = e.getX()+r.x;
		 * int y = e.getY()+r.y; if((Component)e.getSource() == PCARD.pc.getRootPane()){
		 * x -= PCARD.pc.toolbar.getTWidth(); y -=
		 * PCARD.pc.toolbar.getTHeight()+PCARD.pc.getInsets().top+PCARD.pc.getJMenuBar()
		 * .getHeight(); }
		 */

		isMouseDown = false;

		if (target != null) {
			drawSelectBorder(target);

			OField ofld = null;
			if (target.getClass() == MyTextArea.class)
				ofld = ((MyTextArea) target).fldData;
			else if (target.getClass() == MyScrollPane.class)
				ofld = ((MyScrollPane) target).fldData;

			if (ofld != null) {
				// ダブルクリック判定
				if (!newField && e.getClickCount() >= 2) {
					AuthDialog.openAuthDialog(PCARD.pc, "field", ofld);
				} else {
					tgtOFld = ofld;
					ofld.left = target.getBounds().x;
					ofld.top = target.getBounds().y;
					ofld.width = target.getBounds().width;
					ofld.height = target.getBounds().height;
				}

				if (newField) {
					OCard.reloadCurrentCard();

					OCardBase cdbase = PCARD.pc.stack.curCard;
					if (PaintTool.editBackground) {
						cdbase = PCARD.pc.stack.curCard.bg;
					}
					OField newfld = cdbase.GetFldbyNum(cdbase.fldList.size());
					target = newfld.getComponent();
					drawSelectBorder(newfld.fld);
				}
			}

			if (AuthDialog.authDialog != null) {
				AuthDialog.openAuthDialog(PCARD.pc, "field", ofld);
			}
		} else {
			tgtOFld = null;
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public class dragThread extends Thread {
		public void run() {
			while (isMouseDown) {
				PointerInfo pointerInfo = MouseInfo.getPointerInfo();
				int x = pointerInfo.getLocation().x - PCARD.pc.mainPane.getX() - PCARD.pc.getLocationOnScreen().x;
				int y = pointerInfo.getLocation().y - PCARD.pc.mainPane.getY() - PCARD.pc.getLocationOnScreen().y
						- PCARD.pc.getInsets().top + PCARD.pc.getJMenuBar().getHeight();

				if (target != null) {
					Rectangle newr = target.getBounds();
					if (!sizeChange) {
						// 移動
						newr.x = x - offsetx;
						newr.y = y - offsety;
						if (GUI.key[11] > 1) { // SHIFT
							if (Math.abs(x - startx) < Math.abs(y - starty)) {
								newr.x = startx - offsetx;
							} else {
								newr.y = starty - offsety;
							}
						}
						target.setBounds(newr);
						target.setEnabled(false);
					} else {
						// サイズ変更
						if (sizeleft) {
							newr.x += x - lastx;
							newr.width -= x - lastx;
						} else {
							newr.width += x - lastx;
						}
						if (sizetop) {
							newr.height -= y - lasty;
							newr.y += y - lasty;
						} else {
							newr.height += y - lasty;
						}
						target.setBounds(newr);
						target.setEnabled(false);

						OField ofld = null;
						if (target.getClass() == MyTextArea.class)
							ofld = ((MyTextArea) target).fldData;
						else if (target.getClass() == MyScrollPane.class)
							ofld = ((MyScrollPane) target).fldData;
						ofld.width = newr.width;
					}
				}

				lasty += (y - lasty);
				lastx += (x - lastx);
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		/*
		 * Rectangle r = ((Component)e.getSource()).getBounds(); int x = e.getX()+r.x;
		 * int y = e.getY()+r.y; if((Component)e.getSource() == PCARD.pc.getRootPane()){
		 * x -= PCARD.pc.stack.toolbar.getTWidth(); y -=
		 * PCARD.pc.stack.toolbar.getTHeight()+PCARD.pc.getInsets().top; }
		 * 
		 * if(target != null){ Rectangle newr = target.getBounds(); if(!sizeChange){
		 * //移動 newr.x = x-offsetx; newr.y = y-offsety;
		 * if((e.getModifiersEx()&InputEvent.SHIFT_DOWN_MASK)>0){
		 * if(Math.abs(x-startx)<Math.abs(y-starty)){ newr.x = startx-offsetx; }else{
		 * newr.y = starty-offsety; } } target.setBounds(newr); } else{ //サイズ変更
		 * newr.width += x - lastx; newr.height += y - lasty; target.setBounds(newr); }
		 * }
		 * 
		 * lasty += (y-lasty); lastx += (x-lastx);
		 */
	}

	public void mouseMoved(MouseEvent e) {
		if (target != null)
			drawSelectBorder(target);
	}

	public void keyDown() {
		if (tgtOFld == null) {
			return;
		}

		if (GUI.key[20] > 0 || GUI.key[21] > 0) { // BACKSPACE or DELETE
			((OCardBase) tgtOFld.parent).partsList.remove(tgtOFld);
			((OCardBase) tgtOFld.parent).fldList.remove(tgtOFld);
			if (tgtOFld.getComponent() != null) {
				PCARD.pc.mainPane.remove(tgtOFld.getComponent());
			}
			target = null;
			tgtOFld = null;

			OCard.reloadCurrentCard();
		} else if (GUI.key[14] == 0 && (GUI.key[0] > 0 || GUI.key[1] > 0 || GUI.key[2] > 0 || GUI.key[3] > 0)) { // ARROW
			if (GUI.key[0] > 0) {
				tgtOFld.setTopLeft(tgtOFld.left, tgtOFld.top - 1);
			} else if (GUI.key[1] > 0) {
				tgtOFld.setTopLeft(tgtOFld.left, tgtOFld.top + 1);
			} else if (GUI.key[2] > 0) {
				tgtOFld.setTopLeft(tgtOFld.left - 1, tgtOFld.top);
			} else if (GUI.key[3] > 0) {
				tgtOFld.setTopLeft(tgtOFld.left + 1, tgtOFld.top);
			}
		} else if (GUI.key[14] > 0 && (GUI.key[0] > 0 || GUI.key[1] > 0 || GUI.key[2] > 0 || GUI.key[3] > 0)) { // ARROW
			if (GUI.key[0] > 0 && tgtOFld.height > 0) {
				tgtOFld.setRect(tgtOFld.left, tgtOFld.top, tgtOFld.left + tgtOFld.width,
						tgtOFld.top + tgtOFld.height - 1);
			} else if (GUI.key[1] > 0) {
				tgtOFld.setRect(tgtOFld.left, tgtOFld.top, tgtOFld.left + tgtOFld.width,
						tgtOFld.top + tgtOFld.height + 1);
			} else if (GUI.key[2] > 0 && tgtOFld.width > 0) {
				tgtOFld.setRect(tgtOFld.left, tgtOFld.top, tgtOFld.left + tgtOFld.width - 1,
						tgtOFld.top + tgtOFld.height);
			} else if (GUI.key[3] > 0) {
				tgtOFld.setRect(tgtOFld.left, tgtOFld.top, tgtOFld.left + tgtOFld.width + 1,
						tgtOFld.top + tgtOFld.height);
			}
		} else if (GUI.key[14] > 0 && (GUI.key['+'] > 0)) { // テンキーの+はショートカットが効かないのでここに
			OField ofld = tgtOFld;
			int i = ((OCardBase) ofld.parent).GetNumberof(ofld) - 1;
			int ip = ((OCardBase) ofld.parent).GetNumberofParts(ofld) - 1;
			ip++;
			if (ip >= ((OCardBase) ofld.parent).partsList.size())
				ip--;
			else if (((OCardBase) ofld.parent).partsList.get(ip).objectType.equals("field")) {
				i++;
			}
			((OCardBase) ofld.parent).partsList.remove(ofld);
			((OCardBase) ofld.parent).fldList.remove(ofld);
			((OCardBase) ofld.parent).partsList.add(ip, ofld);
			((OCardBase) ofld.parent).fldList.add(i, ofld);

			OCard.reloadCurrentCard();

			// ofldは生きているがtargetはいなくなるので取り直す
			FieldGUI.gui.target = ofld.getComponent();
		} else if (GUI.key[14] > 0 && (GUI.key['-'] > 0)) { // テンキーの-はショートカットが効かないのでここに
			OField ofld = tgtOFld;
			int i = ((OCardBase) ofld.parent).GetNumberof(ofld) - 1;
			int ip = ((OCardBase) ofld.parent).GetNumberofParts(ofld) - 1;
			ip--;
			if (ip < 0)
				ip = 0;
			else if (((OCardBase) ofld.parent).partsList.get(ip).objectType.equals("field")) {
				i--;
			}
			((OCardBase) ofld.parent).partsList.remove(ofld);
			((OCardBase) ofld.parent).fldList.remove(ofld);
			((OCardBase) ofld.parent).partsList.add(ip, ofld);
			((OCardBase) ofld.parent).fldList.add(i, ofld);

			OCard.reloadCurrentCard();

			// ofldは生きているがtargetはいなくなるので取り直す
			FieldGUI.gui.target = ofld.getComponent();
		}
	}

	public static void drawSelectBorder(Component target) {
		ButtonGUI.drawSelectBorder(target);
	}
}

class MyFieldDropListener extends DropTargetAdapter {
	public void drop(DropTargetDropEvent e) {
		try {
			Transferable transfer = e.getTransferable();
			if (transfer.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				/*
				 * @SuppressWarnings("unchecked") List<File> fileList = (List<File>)
				 * (transfer.getTransferData(DataFlavor.javaFileListFlavor)); String path =
				 * fileList.get(0).toString(); String ext = ""; BufferedImage bi = null; try{
				 * ext = ".ppm"; bi = PictureFile.loadPbm(path); if(bi==null){
				 * if(path.lastIndexOf(".")>=0){ ext = path.substring(path.lastIndexOf("."),
				 * path.length()); } else{ ext = ""; } bi = javax.imageio.ImageIO.read(new
				 * File(path)); } if(bi==null){ ext = ".pict"; bi = PictureFile.loadPICT(path);
				 * } } catch (IOException e2) { e2.printStackTrace(); } if(bi!=null){ String
				 * name = new File(path).getName(); Pattern p =
				 * Pattern.compile("([0-9]{1,6})([^0-9])"); Matcher m = p.matcher(name); int
				 * baseid = 1000; if(m.find()){ baseid = Integer.valueOf(m.group(1)); } int
				 * rsrcid = PCARD.pc.stack.rsrc.getNewResourceId("pictfont", baseid); //ファイルをコピー
				 * String newFileName = "PICTFONT_"+rsrcid+ext; String newFilePath =
				 * PCARD.pc.stack.file.getParent()+File.separatorChar+newFileName; FileChannel
				 * srcChannel = new FileInputStream(path).getChannel(); FileChannel destChannel
				 * = new FileOutputStream(newFilePath).getChannel(); try {
				 * srcChannel.transferTo(0, srcChannel.size(), destChannel); } finally {
				 * srcChannel.close(); destChannel.close(); } //リソースに追加
				 * PCARD.pc.stack.rsrc.addResource(rsrcid, "pictfont", name, newFileName);
				 * //フィールドのフォントに設定 Component component =
				 * ((DropTarget)e.getSource()).getComponent(); OField ofld =
				 * ((MyTextArea)component).fldData; ofld.fontPict = bi; ofld.textFont = name; }
				 * else
				 */ {
					new GDialog(PCARD.pc, PCARD.pc.intl.getDialogText("Could't open the file."), null, "OK", null,
							null);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
