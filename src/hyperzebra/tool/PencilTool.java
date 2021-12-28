package hyperzebra.tool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.DataBuffer;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

import hyperzebra.gui.GUI;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.gui.menu.GMenuPaint;

//-------------------
//鉛筆
//-------------------
public class PencilTool implements toolInterface {
	boolean invert;
	boolean shift;
	int shiftx, shifty;

	@Override
	public String getName() {
		return "Pencil";
	}

	@Override
	public void mouseUp(int x, int y) {
		PaintTool.toCdPict();
		PaintTool.owner.mainPane.repaint();

		invert = false;
	}

	@Override
	public void mouseDown(int x, int y) {
		if (GUI.key[12] > 0) {
			PaintTool.spoit((int) x, (int) y);
			return;
		}

		shift = (GUI.key[11] > 0);
		shiftx = x;
		shifty = y;

		if (GUI.key[14] > 0) { // cmd
			if (PaintTool.owner.bit > 1) {
				PaintTool.owner.bit = 1;
			} else {
				if (PCARDFrame.pc == PaintTool.owner) {
					PaintTool.owner.bit = 8;
				} else if (PaintTool.owner.mainImg.getWidth() < 128 && PaintTool.owner.mainImg.getHeight() < 128) {
					PaintTool.owner.bit = 8;
				} else if (PaintTool.owner.mainImg.getWidth() < 320 && PaintTool.owner.mainImg.getHeight() < 320) {
					PaintTool.owner.bit = 4;
				} else {
					PaintTool.owner.bit = 4;// 2;
				}
			}
			if (PaintTool.owner.bit > 1 && PCARDFrame.pc == PaintTool.owner) {
				PaintTool.owner.bitLeft = x - (PaintTool.owner.redoBuf.getWidth() / 2) / PaintTool.owner.bit;
				if (PaintTool.owner.bitLeft < 0)
					PaintTool.owner.bitLeft = 0;
				if (PaintTool.owner.bitLeft + PaintTool.owner.redoBuf.getWidth()
						/ PaintTool.owner.bit > PaintTool.owner.redoBuf.getWidth()) {
					PaintTool.owner.bitLeft = PaintTool.owner.redoBuf.getWidth() * (PaintTool.owner.bit - 1)
							/ PaintTool.owner.bit;
				}
				PaintTool.owner.bitTop = y - (PaintTool.owner.redoBuf.getHeight() / 2) / PaintTool.owner.bit;
				if (PaintTool.owner.bitTop < 0)
					PaintTool.owner.bitTop = 0;
				if (PaintTool.owner.bitTop + PaintTool.owner.redoBuf.getHeight()
						/ PaintTool.owner.bit > PaintTool.owner.redoBuf.getHeight()) {
					PaintTool.owner.bitTop = PaintTool.owner.redoBuf.getHeight() * (PaintTool.owner.bit - 1)
							/ PaintTool.owner.bit;
				}
			}
			if (PCARDFrame.pc != PaintTool.owner) {// IconEditor
				// PaintTool.owner.mainPane.setBounds(0, 0,
				// PaintTool.owner.mainImg.getWidth()*PaintTool.owner.bit,
				// PaintTool.owner.mainImg.getHeight()*PaintTool.owner.bit);
				PaintTool.owner.mainPane
						.setPreferredSize(new Dimension(PaintTool.owner.mainImg.getWidth() * PaintTool.owner.bit,
								PaintTool.owner.mainImg.getHeight() * PaintTool.owner.bit));

				JViewport vp = (JViewport) PaintTool.owner.mainPane.getParent();
				((JScrollPane) vp.getParent()).setViewportView(PaintTool.owner.mainPane);

				Dimension size = PaintTool.owner.getSize();
				int sw = size.width - 160;
				int sh = size.height - PaintTool.owner.getInsets().top;
				int rate = PaintTool.owner.bit;
				if (sw > PaintTool.owner.mainImg.getWidth() * rate + 20)
					sw = PaintTool.owner.mainImg.getWidth() * rate + 20;
				if (sh > PaintTool.owner.mainImg.getHeight() * rate + 20)
					sh = PaintTool.owner.mainImg.getHeight() * rate + 20;

				((JScrollPane) vp.getParent()).setBounds(((size.width - 160) - sw) / 2,
						((size.height - PaintTool.owner.getInsets().top) - sh) / 2, sw, sh);
			}

			// PaintTool.owner.getRootPane().repaint();
			PaintTool.owner.mainPane.repaint();
			return;
		}

		GMenuPaint.setUndo();

		PaintTool.lastx[0] = x;
		PaintTool.lasty[0] = y;

		DataBuffer db = PaintTool.owner.getSurface().getRaster().getDataBuffer();
		int c = db.getElem(x + y * PaintTool.owner.getSurface().getWidth());
		Color col = PaintTool.owner.fore.color;
		int c2 = (col.getAlpha() << 24) + (col.getRed() << 16) + (col.getGreen() << 8) + (col.getBlue());

		if (c == c2 || c - 1 == c2) {
			invert = true;
		}

		mouseStillDown(x, y);
	}

	@Override
	public boolean mouseWithin(int x, int y) {
		if (GUI.key[12] > 0) {
			PaintTool.owner.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		} else {
			PaintTool.owner.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		return true;
	}

	@Override
	public boolean mouseStillDown(int x, int y) {
		if (GUI.key[12] > 0) { // opt spoit
			return true;
		}
		if (GUI.key[14] > 0) { // cmd zoom
			return true;
		}

		if (shift && ((x - shiftx != 0) || (y - shifty != 0))) {
			if ((shiftx == -1) || (shifty != -1) && (Math.abs(x - shiftx) > Math.abs(y - shifty))) {
				shiftx = -1;
				y = shifty;
				PaintTool.lasty[0] = (int) shifty;
			} else if ((shifty == -1) || (shiftx != -1)) {
				shifty = -1;
				x = shiftx;
				PaintTool.lastx[0] = (int) shiftx;
			}
		}

		// 裏画面を取得
		Graphics2D g2;
		g2 = (Graphics2D) PaintTool.owner.getSurface().getGraphics();

		// 線を裏画面に書く
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		float brushSize = 1;
		g2.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		if (this.invert) {
			g2.setColor(PaintTool.owner.back.color);
		} else {
			g2.setColor(PaintTool.owner.fore.color);
		}
		Line2D.Double line = new Line2D.Double(PaintTool.lastx[0], PaintTool.lasty[0], x, y);
		g2.draw(line);

		// クリップサイズを計算
		int left = 0, right = 0, top = 0, bottom = 0;
		if (x < PaintTool.lastx[0]) {
			left = (int) (x - brushSize / 2 - 1);
			right = (int) (PaintTool.lastx[0] + brushSize / 2 + 1);
		} else {
			left = (int) (PaintTool.lastx[0] - brushSize / 2 - 1);
			right = (int) (x + brushSize / 2 + 1);
		}
		if (y < PaintTool.lasty[0]) {
			top = (int) (y - brushSize / 2 - 1);
			bottom = (int) (PaintTool.lasty[0] + brushSize / 2 + 1);
		} else {
			top = (int) (PaintTool.lasty[0] - brushSize / 2 - 1);
			bottom = (int) (y + brushSize / 2 + 1);
		}

		// 背景画面を描画
		Graphics2D g3;
		g3 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();
		g3.setClip(new Rectangle(left, top, right - left, bottom - top));
		if (!PaintTool.editBackground) {
			// g3.setColor(new Color(255,128,128));
			// g3.fillRect(0,0,640,480);
			// g3.drawImage(PaintTool.owner.bgImg, 0, 0, PaintTool.owner.mainPane);
		}

		Graphics2D g4 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();
		g4.setClip(new Rectangle(left, top, right - left, bottom - top));

		// 線分の画面を描画
		// g4.drawImage(PaintTool.owner.getSurface(), 0, 0, PaintTool.owner.mainPane);
		if (PaintTool.owner.bit > 1)
			PaintTool.owner.mainPane.repaint((left - (int) PaintTool.owner.bitLeft) * PaintTool.owner.bit,
					(top - (int) PaintTool.owner.bitTop) * PaintTool.owner.bit, (right - left) * PaintTool.owner.bit,
					(bottom - top) * PaintTool.owner.bit);
		else
			PaintTool.owner.mainPane.repaint(left, top, right - left, bottom - top);

		return true;
	}

	@Override
	public void clear() {

	}

	@Override
	public void end() {
		// TabletManager.getDefaultManager().removeTabletListener(PaintTool.owner.mainPane,
		// PaintTool.tabletListener);
	}
}
