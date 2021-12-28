package hyperzebra.gui.button;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import hyperzebra.gui.ButtonGUI;
import hyperzebra.gui.FieldGUI;
import hyperzebra.gui.GUI;
import hyperzebra.gui.PCARD;
import hyperzebra.gui.TBCursor;
import hyperzebra.gui.paintGUI;
import hyperzebra.gui.dialog.AuthDialog;
import hyperzebra.gui.dialog.GFontDialog;
import hyperzebra.gui.menu.GMenu;
import hyperzebra.gui.menu.GMenuPaint;
import hyperzebra.object.OCard;
import hyperzebra.tool.AuthTool;
import hyperzebra.tool.BrushTool;
import hyperzebra.tool.ButtonTool;
import hyperzebra.tool.EraserTool;
import hyperzebra.tool.FieldTool;
import hyperzebra.tool.LassoTool;
import hyperzebra.tool.LineTool;
import hyperzebra.tool.OvalTool;
import hyperzebra.tool.PaintBucketTool;
import hyperzebra.tool.PaintTool;
import hyperzebra.tool.PencilTool;
import hyperzebra.tool.RectTool;
import hyperzebra.tool.SelectTool;
import hyperzebra.tool.SmartSelectTool;
import hyperzebra.tool.TypeTool;

public class TBButtonListener implements ActionListener, MouseListener {
	static String lastcmd = "Browse";

	public void mouseClicked(MouseEvent arg0) {
		String in_cmd = ((JComponent) arg0.getSource()).getName();
		String cmd = PCARD.pc.intl.getToolText(in_cmd);
		if (javax.swing.SwingUtilities.isRightMouseButton(arg0) || arg0.getClickCount() == 2) {
			if (PCARD.pc.intl.getToolText("Brush").equals(cmd)) {
				((TBButton) arg0.getSource()).popup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			}
			if (PCARD.pc.intl.getToolText("Rect").equals(cmd)) {
				((TBButton) arg0.getSource()).popup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			}
			if (PCARD.pc.intl.getToolText("Oval").equals(cmd)) {
				((TBButton) arg0.getSource()).popup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			}
			if (PCARD.pc.intl.getToolText("Line").equals(cmd)) {
				((TBButton) arg0.getSource()).popup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			}
			if (PCARD.pc.intl.getToolText("MagicWand").equals(cmd)) {
				((TBButton) arg0.getSource()).popup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			}
			if (PCARD.pc.intl.getToolText("Transparency").equals(cmd)) {
				((TransButton) arg0.getSource()).popup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			}
			if (PCARD.pc.intl.getToolText("Type").equals(cmd)) {
				new GFontDialog(PaintTool.owner, PCARD.pc.textFont, PCARD.pc.textSize, PCARD.pc.textStyle, -1);
				PCARD.pc.textFont = GFontDialog.selectedFont;
				PCARD.pc.textSize = GFontDialog.selectedSize * PaintTool.owner.bit;
				PCARD.pc.textStyle = GFontDialog.selectedStyle;
				// PCARD.pc.textAlign = GFontDialog.selectedAlign;
			}
		}

		if (arg0.getClickCount() == 2) {
			// ダブルクリック
			if (PCARD.pc.intl.getToolText("Eraser").equals(cmd)) {
				GMenuPaint.doMenu("Select All");
				GMenuPaint.doMenu("Clear Selection");
			}
			if (PCARD.pc.intl.getToolText("Select").equals(cmd)) {
				GMenuPaint.doMenu("Select All");
			}
			if (PCARD.pc.intl.getToolText("Lasso").equals(cmd)) {
				GMenuPaint.doMenu("choose lasso tool");
				PaintTool.mouseDown(0, 0);
				PaintTool.mouseStillDown(0, 1000);
				PaintTool.mouseStillDown(0, 10000);
				PaintTool.mouseStillDown(1000, 10000);
				PaintTool.mouseStillDown(10000, 10000);
				PaintTool.mouseStillDown(10000, 1000);
				PaintTool.mouseStillDown(10000, 0);
				PaintTool.mouseStillDown(1000, 0);
				PaintTool.mouseStillDown(0, 0);
				PaintTool.mouseUp(0, 0);
			}
			if (PCARD.pc.intl.getToolText("Pencil").equals(cmd)) {
				GMenuPaint.doMenu("FatBits");
			}
			/*
			 * if(PCARD.pc.intl.getToolText("Button").equals(cmd)){ try {
			 * GMenuBrowse.doMenu("New Button"); } catch (xTalkException e) {
			 * e.printStackTrace(); } } if(PCARD.pc.intl.getToolText("Field").equals(cmd)){
			 * try { GMenuBrowse.doMenu("New Field"); } catch (xTalkException e) {
			 * e.printStackTrace(); } }
			 */
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void actionPerformed(ActionEvent ae) {
		String in_cmd = ((JComponent) ae.getSource()).getName();
		String cmd = PCARD.pc.intl.getToolText(in_cmd);
		ChangeTool(cmd, ae);
		// if(((TBButton)ae.getSource()).getParent()==PCARD.pc.stack.toolbar.tb){
		// PCARD.pc.toFront();
		// }
		if (PCARD.pc.intl.getToolText("Close").equals(cmd)) {
			PCARD.pc.toolbar.tb.setVisible(false);
		}
	}

	static public boolean ChangeTool(String cmd, ActionEvent ae) {

		// 前のツールの終了処理
		if (PCARD.pc.tool != null) {
			PCARD.pc.tool.end();

			PaintTool.saveCdPictures();
		}
		if (AuthTool.tool != null) {
			ButtonGUI.gui.removeListenerFromParts();
			FieldGUI.gui.removeListenerFromParts();

			AuthTool.tool.end();
		}

		// ツールバーのハイライトを変更
		if (PCARD.pc.stack != null) {
			for (int i = 0; i < PCARD.pc.toolbar.tb.getContentPane().getComponentCount(); i++) {
				JComponent component = (JComponent) PCARD.pc.toolbar.tb.getContentPane().getComponent(i);
				if (component.getClass() == TBButton.class) {
					if (component.getName().equalsIgnoreCase(cmd)) {
						((TBButton) component).setSelected(true);
					} else {
						((TBButton) component).setSelected(false);
					}
				}
			}
		}

		if (PCARD.pc.intl.getToolText("Browse").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Browse").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolText("Button").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Button").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolText("Field").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Field").equalsIgnoreCase(cmd)) {
			if (PCARD.pc.tool != null) {
				// ブラウズツールにしたのでペイントモードから戻す
				PCARD.pc.tool = null;
				PCARD.pc.bit = 1;
				PCARD.pc.getRootPane().removeMouseListener(paintGUI.gui);
				PCARD.pc.getRootPane().removeMouseMotionListener(paintGUI.gui);
				paintGUI.gui.removeListenerFromParts();

				// PCARD.pc.stack.curCard.pict.getGraphics().drawImage(PCARD.pc.mainImg, 0, 0,
				// PCARD.pc);
				// PCARD.pc.stack.curCard.bg.pict.getGraphics().drawImage(PCARD.pc.bgImg, 0, 0,
				// PCARD.pc);
				PCARD.pc.mainPane.repaint();

				// PCARD.pc.paidle.interrupt();
				// PCARD.pc.pidle = new Pidle();
				// PCARD.pc.pidle.start();

				{
					// ペイント用バッファ
					PCARD.pc.mainImg = null;
					PCARD.pc.bgImg = null;
					PCARD.pc.undoBuf = null;
					PCARD.pc.redoBuf = null;
				}

				// これでは切り替わってくれない
				// PCARD.pc.setJMenuBar(PCARD.pc.menu.mb);

				GMenu.menuUpdate(PCARD.pc.menu.mb);
			}
		}

		{
			GMenu.changeSelected("Tool", PCARD.pc.intl.getToolText(lastcmd), true);
			GMenu.changeSelected("Tool", PCARD.pc.intl.getToolText(cmd), true);
			lastcmd = cmd;
		}

		if (PCARD.pc.intl.getToolText("Button").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Button").equalsIgnoreCase(cmd)) {
			AuthTool.tool = new ButtonTool();
			TBCursor.changeCursor(PCARD.pc);

			GUI.removeAllListener();
			ButtonGUI.gui.addListenerToParts();
			PCARD.pc.mainPane.addMouseListener(ButtonGUI.gui);
			PCARD.pc.mainPane.addMouseMotionListener(ButtonGUI.gui);

			// メニュー
			{
				GMenu.changeEnabled("Go", "Background", true);
				GMenu.changeEnabled("Objects", "Button Info…", true);
				GMenu.changeEnabled("Objects", "Field Info…", false);
				GMenu.changeEnabled("Objects", "Bring Closer", true);
				GMenu.changeEnabled("Objects", "Send Farther", true);

				GMenu.changeMenuName("Edit", "Cut", "Cut Button");
				GMenu.changeMenuName("Edit", "Copy", "Copy Button");
				GMenu.changeMenuName("Edit", "Paste", "Paste Button");
				GMenu.changeMenuName("Edit", "Delete", "Delete Button");
				GMenu.changeMenuName("Edit", "Cut Field", "Cut Button");
				GMenu.changeMenuName("Edit", "Copy Field", "Copy Button");
				GMenu.changeMenuName("Edit", "Paste Field", "Paste Button");
				GMenu.changeMenuName("Edit", "Delete Field", "Delete Button");
			}

			return true;
		} else if (PCARD.pc.intl.getToolText("Field").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Field").equalsIgnoreCase(cmd)) {
			AuthTool.tool = new FieldTool();
			TBCursor.changeCursor(PCARD.pc);

			GUI.removeAllListener();
			FieldGUI.gui.addListenerToParts();
			PCARD.pc.mainPane.addMouseListener(FieldGUI.gui);
			PCARD.pc.mainPane.addMouseMotionListener(FieldGUI.gui);

			// メニュー
			{
				GMenu.changeEnabled("Edit", "Background", true);
				GMenu.changeEnabled("Objects", "Button Info…", false);
				GMenu.changeEnabled("Objects", "Field Info…", true);
				GMenu.changeEnabled("Objects", "Bring Closer", true);
				GMenu.changeEnabled("Objects", "Send Farther", true);

				GMenu.changeMenuName("Edit", "Cut", "Cut Field");
				GMenu.changeMenuName("Edit", "Copy", "Copy Field");
				GMenu.changeMenuName("Edit", "Paste", "Paste Field");
				GMenu.changeMenuName("Edit", "Delete", "Delete Field");
				GMenu.changeMenuName("Edit", "Cut Button", "Cut Field");
				GMenu.changeMenuName("Edit", "Copy Button", "Copy Field");
				GMenu.changeMenuName("Edit", "Paste Button", "Paste Field");
				GMenu.changeMenuName("Edit", "Delete Button", "Delete Field");
			}

			return true;
		} else {
			// ボタン、フィールドツール以外
			if (AuthDialog.authDialog != null)
				AuthDialog.authDialog.dispose();
			AuthDialog.authDialog = null;
		}

		if (PCARD.pc.intl.getToolText("Browse").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Browse").equalsIgnoreCase(cmd)) {
			AuthTool.tool = null;
			TBCursor.changeCursor(PCARD.pc);

			// バックグラウンド編集モードを解除
			{
				PaintTool.editBackground = false;
				String titleName = PCARD.pc.stack.name;
				if (titleName.length() > 5 && titleName.substring(titleName.length() - 5).equals(".xstk")) {
					titleName = titleName.substring(0, titleName.length() - 5);
				}
				PCARD.pc.setTitle(titleName);
			}

			OCard.reloadCurrentCard();

			// メニュー
			{
				GMenu.changeEnabled("Edit", "Background", false);
				GMenu.changeSelected("Edit", "Background", false);
				GMenu.changeEnabled("Objects", "Button Info…", false);
				GMenu.changeEnabled("Objects", "Field Info…", false);
				GMenu.changeEnabled("Objects", "Bring Closer", false);
				GMenu.changeEnabled("Objects", "Send Farther", false);
			}

			return true;
		}

		//
		// ここから下はペイントツール
		//

		if (PCARD.pc.intl.getToolText("Select").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Select").equalsIgnoreCase(cmd)) {
			PCARD.pc.tool = new SelectTool();
			TBCursor.changeCursor(PCARD.pc);
		} else if (PCARD.pc.intl.getToolText("Lasso").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Lasso").equalsIgnoreCase(cmd)) {
			PCARD.pc.tool = new LassoTool();
			TBCursor.changeCursor(PCARD.pc);
		} else if (PCARD.pc.intl.getToolText("MagicWand").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("MagicWand").equalsIgnoreCase(cmd)) {
			PCARD.pc.tool = new SmartSelectTool();
			TBCursor.changeCursor(PCARD.pc);
		} else if (PCARD.pc.intl.getToolText("Brush").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Brush").equalsIgnoreCase(cmd)) {
			PCARD.pc.tool = new BrushTool();
			TBCursor.changeCursor(PCARD.pc);
		} else if (PCARD.pc.intl.getToolText("PaintBucket").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("PaintBucket").equalsIgnoreCase(cmd)
				|| "bucket".equalsIgnoreCase(cmd)) {
			PCARD.pc.tool = new PaintBucketTool();
			TBCursor.changeCursor(PCARD.pc);
		} else if (PCARD.pc.intl.getToolText("Transparency").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Transparency").equalsIgnoreCase(cmd)) {
			if (ae != null) {
				((TransButton) ae.getSource()).popup.show(((TransButton) ae.getSource()),
						/* ((TBButton)arg0.getSource()).getLocation().x */0,
						0/* ((TBButton)arg0.getSource()).getLocation().y */);
			}
		} else if (PCARD.pc.intl.getToolText("Type").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Type").equalsIgnoreCase(cmd) || "text".equalsIgnoreCase(cmd)) {
			PCARD.pc.tool = new TypeTool();
			TBCursor.changeCursor(PCARD.pc);
		} else if (PCARD.pc.intl.getToolText("Pencil").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Pencil").equalsIgnoreCase(cmd)) {
			PCARD.pc.tool = new PencilTool();
			TBCursor.changeCursor(PCARD.pc);
		} else if (PCARD.pc.intl.getToolText("Eraser").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Eraser").equalsIgnoreCase(cmd)) {
			PCARD.pc.tool = new EraserTool();
			TBCursor.changeCursor(PCARD.pc);
		} else if (PCARD.pc.intl.getToolText("Rect").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Rect").equalsIgnoreCase(cmd) || "rectangle".equalsIgnoreCase(cmd)) {
			PCARD.pc.tool = new RectTool();
			TBCursor.changeCursor(PCARD.pc);
		} else if (PCARD.pc.intl.getToolText("Line").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Line").equalsIgnoreCase(cmd)) {
			PCARD.pc.tool = new LineTool();
			TBCursor.changeCursor(PCARD.pc);
		} else if (PCARD.pc.intl.getToolText("Oval").equalsIgnoreCase(cmd)
				|| PCARD.pc.intl.getToolEngText("Oval").equalsIgnoreCase(cmd)) {
			PCARD.pc.tool = new OvalTool();
			TBCursor.changeCursor(PCARD.pc);
		} else if (cmd.equals("DummyPaint")) {
			TBCursor.changeCursor(PCARD.pc);
		} else {
			return false;// 見つからない
		}

		if (PCARD.pc.stack.curCard != null && PCARD.pc.mainImg == null) {
			{
				// ペイント用バッファ

				PCARD.pc.mainImg = new BufferedImage(PCARD.pc.stack.width, PCARD.pc.stack.height,
						BufferedImage.TYPE_INT_ARGB);

				PCARD.pc.bgImg = new BufferedImage(PCARD.pc.stack.width, PCARD.pc.stack.height,
						BufferedImage.TYPE_INT_ARGB);
				// これをRGBにするとundoBufと互換性がとれない
				Graphics g = PCARD.pc.bgImg.getGraphics();
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, PCARD.pc.stack.width, PCARD.pc.stack.height);

				PCARD.pc.undoBuf = new BufferedImage(PCARD.pc.stack.width, PCARD.pc.stack.height,
						BufferedImage.TYPE_INT_ARGB);

				PCARD.pc.redoBuf = new BufferedImage(PCARD.pc.stack.width, PCARD.pc.stack.height,
						BufferedImage.TYPE_INT_ARGB);
			}

			// カードピクチャをペイント用バッファへ移動
			PCARD.pc.mainImg.getGraphics().drawImage(PCARD.pc.stack.curCard.pict, 0, 0, PCARD.pc);
			if (PCARD.pc.stack.curCard.bg != null && PCARD.pc.stack.curCard.bg.pict != null) {
				PCARD.pc.bgImg.getGraphics().drawImage(PCARD.pc.stack.curCard.bg.pict, 0, 0, PCARD.pc);
			}
			PCARD.pc.mainPane.repaint();

			// ペイント用リスナー
			GUI.removeAllListener();
			paintGUI.gui.addListenerToParts();
			PCARD.pc.mainPane.addMouseListener(paintGUI.gui);
			PCARD.pc.mainPane.addMouseMotionListener(paintGUI.gui);

			// これでは切り替わってくれない
			// PCARD.pc.setJMenuBar(PCARD.pc.paintMenu.mb);

			GMenu.menuUpdate(PCARD.pc.paintMenu.mb);

			// PCARD.pc.pidle.interrupt();
			/*
			 * PCARD.pc.paidle = new PaintIdle(); PCARD.pc.paidle.start();
			 */

			// メニュー
			{
				GMenu.changeEnabled("Edit", "Background", true);
			}
		}

		// フィールドにフォーカスを渡さない
		if (PCARD.pc.stack.curCard != null) {
			for (int i = 0; i < PCARD.pc.stack.curCard.fldList.size(); i++) {
				PCARD.pc.stack.curCard.fldList.get(i).fld.setFocusable(false);
				PCARD.pc.stack.curCard.fldList.get(i).fld.setEditable(false);
			}
			for (int i = 0; i < PCARD.pc.stack.curCard.bg.fldList.size(); i++) {
				PCARD.pc.stack.curCard.bg.fldList.get(i).fld.setFocusable(false);
				PCARD.pc.stack.curCard.bg.fldList.get(i).fld.setEditable(false);
			}
		}

		return true;
	}
}
