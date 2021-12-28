package hyperzebra.tool;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;

import hyperzebra.gui.GUI;
import hyperzebra.gui.MyPanel;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.gui.dialog.PaintBlendDialog;
import hyperzebra.gui.menu.GMenuPaint;

//-------------------
//投げなわ
//-------------------
public class LassoTool implements toolSelectInterface {
	// srcbits:選択した範囲
	// movePoint:現在の浮き出しの移動分
	// redoBuf:選択した範囲の浮き出し(0,0,width,height)
	// move:trueなら浮き出しあり

	ArrayList<Point> srcPoints = new ArrayList<Point>();
	public BufferedImage srcbits;
	public Point movePoint;
	public boolean move = false;
	boolean shift;
	int shiftx, shifty;

	@Override
	public String getName() {
		return "Lasso";
	}

	private void strokeDraw() {
		if (move == false) {
			strokeDraw(srcPoints);
			return;
		}
		strokebitsDraw();
	}

	private void strokebitsDraw() {
		if (srcbits == null)
			return;

		Graphics2D g3 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();

		if (PaintTool.owner.bit > 1) {
			AffineTransform af = new AffineTransform();
			af.translate(-((int) PaintTool.owner.bitLeft) * PaintTool.owner.bit + PaintTool.owner.bit / 2,
					-((int) PaintTool.owner.bitTop) * PaintTool.owner.bit + PaintTool.owner.bit / 2);
			af.scale(PaintTool.owner.bit, PaintTool.owner.bit);
			g3.transform(af);
		}

		DataBuffer srcbuf = srcbits.getRaster().getDataBuffer();
		int width = srcbits.getWidth();
		int height = srcbits.getHeight();
		int i = ((int) System.currentTimeMillis() / 100) % 8;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int c = srcbuf.getElem(0, x + y * width);
				if ((c & 0xFF000000) != 0) {
					// 浮き出し範囲に含まれていて、周囲4つのどれかが範囲外の場合
					int c1 = 0x00FFFFFF;
					if (x >= 1)
						c1 = srcbuf.getElem(0, x - 1 + y * width);
					int c2 = 0x00FFFFFF;
					if (x < width - 1)
						c2 = srcbuf.getElem(0, x + 1 + y * width);
					int c3 = 0x00FFFFFF;
					if (y >= 1)
						c3 = srcbuf.getElem(0, x + (y - 1) * width);
					int c4 = 0x00FFFFFF;
					if (y < height - 1)
						c4 = srcbuf.getElem(0, x + (y + 1) * width);
					if ((c1 & 0xFF000000) == 0 || (c2 & 0xFF000000) == 0 || (c3 & 0xFF000000) == 0
							|| (c4 & 0xFF000000) == 0) {
						if ((x + y + i) % 4 != 0)
							continue;
						if ((x + y + i) % 8 < 4)
							g3.setColor(Color.WHITE);
						else
							g3.setColor(Color.BLACK);
						g3.drawLine(x + movePoint.x, y + movePoint.y, x + movePoint.x, y + movePoint.y);
					}
				}
			}
		}
	}

	private void strokeDraw(ArrayList<Point> list) {
		Graphics2D g3 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();

		if (PaintTool.owner.bit > 1) {
			AffineTransform af = new AffineTransform();
			af.translate(-((int) PaintTool.owner.bitLeft) * 8 + PaintTool.owner.bit / 2,
					-((int) PaintTool.owner.bitTop) * 8 + PaintTool.owner.bit / 2);
			af.scale(PaintTool.owner.bit, PaintTool.owner.bit);
			g3.transform(af);
		}

		BasicStroke bs = new BasicStroke(0.1f);
		float dash[] = { 4.0f, 2.0f };
		int i = (((int) System.currentTimeMillis() / 100) & 0xFFFF) % 6;
		BasicStroke bs2 = new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, i);
		for (int j = 0; j < list.size() - 1; j++) {
			Point p1 = list.get(j);
			Point p2 = list.get(j + 1);

			g3.setStroke(bs);
			g3.setColor(Color.WHITE);
			g3.drawLine(p1.x, p1.y, p2.x, p2.y);
			g3.setStroke(bs2);
			g3.setColor(Color.BLACK);
			g3.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
	}

	private void makesrcbits() {
		int len = srcPoints.size();
		int[] xPoints = new int[len];
		int[] yPoints = new int[len];
		for (int j = 0; j < len; j++) {
			Point p = srcPoints.get(j);
			xPoints[j] = p.x;
			yPoints[j] = p.y;
		}

		// マスク用バッファを用意する
		BufferedImage srcsrcbits = new BufferedImage(PaintTool.owner.mainImg.getWidth(),
				PaintTool.owner.mainImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) srcsrcbits.getGraphics();
		g.setColor(new Color(255, 255, 255));
		// g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		g.fillRect(0, 0, srcsrcbits.getWidth(), srcsrcbits.getHeight());

		// 多角形を描く
		g = (Graphics2D) srcsrcbits.getGraphics();
		g.setColor(Color.BLACK);
		g.fillPolygon(xPoints, yPoints, srcPoints.size());

		// その形状に含まれるsurface()をsrcsrcbitsに描画
		DataBuffer mskbuf = srcsrcbits.getRaster().getDataBuffer();
		DataBuffer imgbuf = PaintTool.owner.getSurface().getRaster().getDataBuffer();
		int width = srcsrcbits.getWidth();
		int height = srcsrcbits.getHeight();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (0xFFFFFFFF != mskbuf.getElem(x + y * width)) {
					int v = imgbuf.getElem(x + y * width);
					if ((v & 0xFF000000) == 0)
						v = 0xFFFFFFFF;
					mskbuf.setElem(x + y * width, v);
				}
			}
		}

		//
		srcbits = new BufferedImage(PaintTool.owner.mainImg.getWidth(), PaintTool.owner.mainImg.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D srcg = (Graphics2D) srcbits.getGraphics();
		srcg.setColor(new Color(255, 255, 255));
		srcg.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		srcg.fillRect(0, 0, srcbits.getWidth(), srcbits.getHeight());
		srcg = (Graphics2D) srcbits.getGraphics();
		srcg.drawImage(srcsrcbits, 0, 0, null);

		// 周辺の白色を透明化
		pointList = new ArrayList<Point>();

		for (int x = 0; x < width; x++) {
			seedfillH(srcsrcbits, srcbits, x, 0, 0xFFFFFFFF, 0x00FFFFFF);
			seedfillH(srcsrcbits, srcbits, x, height - 1, 0xFFFFFFFF, 0x00FFFFFF);
		}
		for (int y = 0; y < height; y++) {
			seedfillH(srcsrcbits, srcbits, 0, y, 0xFFFFFFFF, 0x00FFFFFF);
			seedfillH(srcsrcbits, srcbits, width - 1, y, 0xFFFFFFFF, 0x00FFFFFF);
		}

		while (pointList.size() > 0) {
			Point p = pointList.get(0);
			pointList.remove(0);
			seedfillH(srcsrcbits, srcbits, p.x, p.y, 0xFFFFFFFF, 0x00FFFFFF);
		}

		// クリア
		srcPoints = new ArrayList<Point>();
	}

	ArrayList<Point> pointList;

	private void seedfillH(BufferedImage surface, BufferedImage newSurface, int px, int py, int srcColor,
			int newColor) {
		DataBuffer buffer = surface.getRaster().getDataBuffer();
		DataBuffer newBuffer = newSurface.getRaster().getDataBuffer();
		int width = surface.getWidth();
		int height = surface.getHeight();

		// 左を調べる
		int lx;
		for (lx = 0; px + lx >= 0; lx--) {
			// System.out.println("<srcColor:"+srcColor);
			// System.out.println("<getElem("+(px+lx)+","+py+"):"+buffer.getElem(0,
			// px+lx+py*width));
			if (buffer.getElem(0, px + lx + py * width) != srcColor) {
				break;
			}
		}
		lx++;

		// 右を調べる
		int rx;
		for (rx = 0; px + rx < width; rx++) {
			// System.out.println(">srcColor:"+srcColor);
			// System.out.println(">getElem("+(px+rx)+","+py+"):"+buffer.getElem(0,
			// px+lx+py*width));
			if (buffer.getElem(0, px + rx + py * width) != srcColor) {
				break;
			}
		}
		rx--;

		// そのラインを塗る
		for (int x = px + lx; x <= px + rx; x++) {
			if (buffer.getElem(0, x + py * width) == srcColor) {
				newBuffer.setElem(0, x + py * width, newColor);
			}
		}

		// 上のラインを探す
		if (py - 1 >= 0) {
			for (int x = px + lx; x <= px + rx; x++) {
				// 右端を探す
				if (buffer.getElem(0, x + (py - 1) * width) == srcColor) {
					if (x == px + rx || buffer.getElem(0, (x + 1) + (py - 1) * width) != srcColor) {
						if (newBuffer.getElem(0, x + (py - 1) * width) != newColor) {
							// 未登録なので登録する
							pointList.add(new Point(x, py - 1));
							newBuffer.setElem(0, x + (py - 1) * width, newColor);
						}
					}
				}
			}
		}

		// 下のラインを探す
		if (py + 1 < height) {
			for (int x = px + lx; x < px + rx; x++) {
				// 右端を探す
				if (buffer.getElem(0, x + (py + 1) * width) == srcColor) {
					if (x + 1 == px + rx || buffer.getElem(0, (x + 1) + (py + 1) * width) != srcColor) {
						if (newBuffer.getElem(0, x + (py + 1) * width) != newColor) {
							// 未登録なので登録する
							pointList.add(new Point(x, py + 1));
							newBuffer.setElem(0, x + (py + 1) * width, newColor);
						}
					}
				}
			}
		}
	}

	private void setSelection(int x, int y) {
		// PaintTool.owner.mainPane.paintImmediately(new
		// Rectangle(0,0,PaintTool.owner.mainPane.getWidth(),PaintTool.owner.mainPane.getHeight()));

		if (srcPoints.size() == 0 || srcPoints.get(srcPoints.size() - 1).x != x
				|| srcPoints.get(srcPoints.size() - 1).y != y) {
			srcPoints.add(new Point(x, y));
		}

		strokeDraw();

		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
		}

		strokeDraw();
	}

	void viewSelection() {
		PaintTool.owner.mainPane.repaint();

		// 選択領域の破線を表示
		strokeDraw();
	}

	@Override
	public void mouseUp(int x, int y) {
		if (move == false) {
			GMenuPaint.setUndo();

			makesrcbits();

			movePoint = new Point(0, 0);
			move = true;

			// 一時バッファに選択領域を移動
			Graphics2D g = (Graphics2D) PaintTool.owner.redoBuf.getGraphics();
			g.setColor(new Color(255, 255, 255));
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			g.fillRect(0, 0, PaintTool.owner.redoBuf.getWidth(), PaintTool.owner.redoBuf.getHeight());
			g.setComposite(AlphaComposite.Src);

			DataBuffer mskbuf = srcbits.getRaster().getDataBuffer();
			DataBuffer movbuf = PaintTool.owner.redoBuf.getRaster().getDataBuffer();
			DataBuffer surbuf = PaintTool.owner.getSurface().getRaster().getDataBuffer();
			int width = srcbits.getWidth();
			int height = srcbits.getHeight();
			for (int v = 0; v < height; v++) {
				for (int h = 0; h < width; h++) {
					int c = mskbuf.getElem(0, h + v * width);
					if ((c & 0xFF000000) != 0) {
						movbuf.setElem(h + v * width, surbuf.getElem(0, h + v * width));
						// 移動した部分を透明にする
						if (!PaintTool.editBackground) {
							surbuf.setElem(h + v * width, 0x00FFFFFF);
						} else {
							surbuf.setElem(h + v * width, 0xFFFFFFFF);
						}
					}
				}
			}

			// 表画面に反映
			PaintTool.owner.mainPane.repaint();

			viewSelection();
		} else {
			mouseStillDown(x, y);
		}
	}

	@Override
	public void mouseDown(int x, int y) {
		if (move == true && srcbits != null) {
			DataBuffer mskbuf = srcbits.getRaster().getDataBuffer();
			if ((x - movePoint.x) >= 0 && (x - movePoint.x) < srcbits.getWidth() && (y - movePoint.y) >= 0
					&& (y - movePoint.y) < srcbits.getHeight()) {
				// 選択範囲の移動開始
				shift = (GUI.key[11] > 0);
				shiftx = x;
				shifty = y;

				int c = mskbuf.getElem(0, (x - movePoint.x) + (y - movePoint.y) * srcbits.getWidth());
				if ((c & 0xFF000000) != 0) {
					if (GUI.key[12] > 0) {
						Graphics2D g = (Graphics2D) PaintTool.owner.getSurface().getGraphics();
						g.drawImage(PaintTool.owner.redoBuf, movePoint.x, movePoint.y, null);
					}
					return;
				}
			}
		}

		{
			// 浮き出し領域を実際の領域に描画
			GMenuPaint.setUndo();
			end();

			// 新しい選択領域を作る
			clear();

			srcPoints.add(new Point(x, y));
		}
	}

	@Override
	public boolean mouseWithin(int x, int y) {

		if (GUI.key[20] > 0 || GUI.key[21] > 0) { // BACKSPACE or DEL
			clear();
			PaintTool.owner.mainPane.repaint();
			return false;
		}

		strokeDraw();
		if (move == true && srcbits != null) {
			DataBuffer mskbuf = srcbits.getRaster().getDataBuffer();
			if ((x - movePoint.x) >= 0 && (x - movePoint.x) < srcbits.getWidth() && (y - movePoint.y) >= 0
					&& (y - movePoint.y) < srcbits.getHeight()) {
				int c = mskbuf.getElem(0, (x - movePoint.x) + (y - movePoint.y) * srcbits.getWidth());
				if ((c & 0xFF000000) != 0) {
					PaintTool.owner.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					return false;
				}
			}
		}

		PaintTool.owner.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		return false;
	}

	@Override
	public boolean mouseStillDown(int x, int y) {
		if (move == true) {
			// 選択範囲の移動

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

			movePoint.x += x - PaintTool.lastx[0];
			movePoint.y += y - PaintTool.lasty[0];

			Rectangle rect = new Rectangle(movePoint.x, movePoint.y, x - (int) PaintTool.lastx[0],
					y - (int) PaintTool.lastx[0]);
			if (rect.width < 0) {
				rect.x += rect.width;
				rect.width = -rect.width;
			}
			if (rect.height < 0) {
				rect.y += rect.height;
				rect.height = -rect.height;
			}

			if (PaintTool.owner.bit > 1) {
				PaintTool.owner.mainPane.repaint((rect.x - (int) PaintTool.owner.bitLeft) * PaintTool.owner.bit,
						(rect.y - (int) PaintTool.owner.bitTop) * PaintTool.owner.bit,
						(rect.width) * PaintTool.owner.bit, (rect.height) * PaintTool.owner.bit);
			} else {
				PaintTool.owner.mainPane.repaint(rect);
			}

			viewSelection();
		} else {
			// 選択範囲を広げて行く
			setSelection(x, y);
		}
		return true;
	}

	@Override
	public void clear() {
		srcPoints = new ArrayList<Point>();
		srcbits = null;
		move = false;
	}

	@Override
	public void end() {
		srcbits = null;
		if (move) {
			Graphics2D g = (Graphics2D) PaintTool.owner.getSurface().getGraphics();
			BufferedImage newimg = MyPanel.makeBlendImage(PaintTool.owner.redoBuf);
			g.drawImage(newimg, movePoint.x, movePoint.y, null);

			// 回転した場合はバッファの大きさが変わっているので新しくする
			PaintTool.owner.redoBuf = new BufferedImage(PaintTool.owner.mainImg.getWidth(),
					PaintTool.owner.mainImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
		}

		PaintTool.owner.blendMode = 0;
		PaintTool.owner.blendLevel = 100;
		if (PaintBlendDialog.dialog != null) {
			PaintBlendDialog.dialog.dispose();
		}

		PaintTool.owner.mainPane.repaint();
	}

	@Override
	public BufferedImage getSelectedSurface(PCARDFrame owner) {
		if (move) {
			return owner.redoBuf;
		}
		return null;
	}

	@Override
	public Rectangle getSelectedRect() {
		return makeSelectedRect(srcbits);
	}

	public static Rectangle makeSelectedRect(BufferedImage in_srcbits) {
		int width = PaintTool.owner.redoBuf.getWidth();
		int height = PaintTool.owner.redoBuf.getHeight();
		int left = width;
		int top = height;
		int right = 0;
		int bottom = 0;

		DataBuffer mskbuf = in_srcbits.getRaster().getDataBuffer();
		for (int v = 0; v < height; v++) {
			for (int h = 0; h < width; h++) {
				int c = mskbuf.getElem(0, h + v * width);
				if ((c & 0xFF000000) != 0) {
					if (left > h)
						left = h;
					if (right < h)
						right = h;
					if (top > v)
						top = v;
					if (bottom < v)
						bottom = v;
				}
			}
		}

		Rectangle srcRect = new Rectangle(left, top, right - left + 1, bottom - top + 1);

		return srcRect;
	}

	@Override
	public Rectangle getMoveRect() {
		Rectangle rect = makeSelectedRect(srcbits);
		rect.x += movePoint.x;
		rect.y += movePoint.y;
		return rect;
	}

	@Override
	public boolean isMove() {
		return move;
	}
}
