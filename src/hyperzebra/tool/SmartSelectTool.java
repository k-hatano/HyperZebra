package hyperzebra.tool;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import hyperzebra.gui.GUI;
import hyperzebra.gui.MyPanel;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.gui.TBCursor;
import hyperzebra.gui.dialog.PaintBlendDialog;
import hyperzebra.gui.menu.GMenuPaint;

//-------------------
//スマート選択
//-------------------
public class SmartSelectTool implements toolSelectInterface {
	// srcbits:選択した範囲
	// movePoint:現在の浮き出しの移動分
	// redoBuf:選択した範囲の浮き出し(0,0,width,height)
	// move:trueなら浮き出しあり

	// cmdを押しながら選択範囲外クリックなら選択範囲を増やす

	ArrayList<Point> pointList; // seedFill用
	ArrayList<Point> srcPoints = new ArrayList<Point>();
	public BufferedImage srcbits;
	public Point movePoint;
	Rectangle tmpRect;
	public boolean move = false;
	boolean shift;
	int shiftx, shifty;

	@Override
	public String getName() {
		return "MagicWand";
	}

	private void borderDraw() {
		if (srcbits == null)
			return;

		/*
		 * //透明度を反映して線分を画面に描画 Graphics2D g4 = (Graphics2D)
		 * PaintTool.owner.redoBuf.getGraphics(); g4.drawImage(PaintTool.owner.bgImg, 0,
		 * 0, PaintTool.owner.mainPane); g4.drawImage(PaintTool.owner.getSurface(), 0,
		 * 0, PaintTool.owner.mainPane); { g4.setComposite(
		 * AlphaComposite.getInstance(AlphaComposite.SRC_OVER,PaintTool.alpha/100.0F) );
		 * } g4.drawImage(srcbits, movePoint.x, movePoint.y, PaintTool.owner.mainPane);
		 * 
		 * //選択範囲の元画像を描画 Graphics2D g6 = (Graphics2D)
		 * PaintTool.owner.mainPane.getGraphics();
		 * 
		 * //拡大表示 if(PaintTool.owner.bit>1){ AffineTransform af = new AffineTransform();
		 * af.translate(-((int)PaintTool.owner.bitLeft)*PaintTool.owner.bit,
		 * -((int)PaintTool.owner.bitTop)*PaintTool.owner.bit);
		 * af.scale(PaintTool.owner.bit, PaintTool.owner.bit); g6.transform(af); }
		 * 
		 * g6.drawImage(PaintTool.owner.redoBuf, 0, 0, PaintTool.owner.mainPane);
		 * 
		 * //拡大表示時の枠線表示 if(PaintTool.owner.bit>2){ Graphics g5 =
		 * PaintTool.owner.mainPane.getGraphics(); MyPanel.bordersDraw(g5,
		 * PaintTool.owner.bit, PaintTool.owner.mainPane.getWidth(),
		 * PaintTool.owner.mainPane.getHeight()); }
		 */

		// 背景および浮き出し部分を描画
		// PaintTool.owner.mainPane.repaint();

		// 破線描画は画面にダイレクトに
		Graphics2D g3 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();

		if (PaintTool.owner.bit > 1) {
			AffineTransform af = new AffineTransform();
			af.translate(-((int) PaintTool.owner.bitLeft) * PaintTool.owner.bit + PaintTool.owner.bit / 2,
					-((int) PaintTool.owner.bitTop) * PaintTool.owner.bit + PaintTool.owner.bit / 2);
			af.scale(PaintTool.owner.bit, PaintTool.owner.bit);
			g3.transform(af);
		}

		// 選択の破線を描画
		if (srcbits == null)
			return;
		WritableRaster raster = srcbits.getRaster();
		if (raster == null)
			return;
		DataBuffer srcbuf = raster.getDataBuffer();
		int width = srcbits.getWidth();
		int height = srcbits.getHeight();
		int i = ((int) System.currentTimeMillis() / 200) % 8;
		// Color color = Color.WHITE;
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
						if ((x + y + i) % 4 == 0) {
							if ((x + y + i) % 8 == 0)
								g3.setColor(Color.WHITE);
							else
								g3.setColor(Color.BLACK);
							g3.drawLine(x + movePoint.x, y + movePoint.y, x + movePoint.x, y + movePoint.y);
						} else {
							/*
							 * if(!isNear(c,color,3)){ color = new Color((c>>16)&0xFF, (c>>8)&0xFF,
							 * (c>>0)&0xFF); } g3.setColor(color);
							 */
						}
					} else {
						/*
						 * if(!isNear(c,color,3)){ color = new Color((c>>16)&0xFF, (c>>8)&0xFF,
						 * (c>>0)&0xFF); } g3.setColor(color);
						 */
					}
					// g3.drawLine(x+movePoint.x, y+movePoint.y, x+movePoint.x, y+movePoint.y);
				}
			}
		}
	}

	private final boolean isNear(int argb, Color color, int near) {
		if (Math.abs(color.getRed() - ((argb >> 16) & 0xFF)) > near) {
			return false;
		}
		if (Math.abs(color.getGreen() - ((argb >> 8) & 0xFF)) > near) {
			return false;
		}
		if (Math.abs(color.getBlue() - ((argb >> 0) & 0xFF)) > near) {
			return false;
		}
		if (Math.abs(color.getAlpha() - ((argb >> 24) & 0xFF)) > near) {
			return false;
		}
		return true;
	}

	private void seedfillH(BufferedImage surface, BufferedImage newSurface, int px, int py,
			ArrayList<Color> srcColors) {
		DataBuffer buffer = surface.getRaster().getDataBuffer();
		DataBuffer newBuffer = newSurface.getRaster().getDataBuffer();
		int width = surface.getWidth();
		int height = surface.getHeight();
		int near = PaintTool.smartSelectPercent * 256 / 100;

		// 左を調べる
		int lx;
		for (lx = 0; px + lx >= 0; lx--) {
			// System.out.println("<srcColor:"+srcColor);
			// System.out.println("<getElem("+(px+lx)+","+py+"):"+buffer.getElem(0,
			// px+lx+py*width));
			boolean isMatch = false;
			for (int i = 0; i < srcColors.size(); i++) {
				if (isNear(buffer.getElem(0, px + lx + py * width), srcColors.get(i), near)) {
					isMatch = true;
					break;
				}
			}
			if (!isMatch)
				break;
		}
		lx++;

		// 右を調べる
		int rx;
		for (rx = 0; px + rx < width; rx++) {
			// System.out.println(">srcColor:"+srcColor);
			// System.out.println(">getElem("+(px+rx)+","+py+"):"+buffer.getElem(0,
			// px+lx+py*width));
			boolean isMatch = false;
			for (int i = 0; i < srcColors.size(); i++) {
				if (isNear(buffer.getElem(0, px + rx + py * width), srcColors.get(i), near)) {
					isMatch = true;
					break;
				}
			}
			if (!isMatch)
				break;
		}
		rx--;

		// そのラインを塗る
		for (int x = px + lx; x <= px + rx; x++) {
			int c = buffer.getElem(0, x + py * width);
			if (c == 0)
				c = 1;
			newBuffer.setElem(0, x + py * width, c);
		}

		// 上のラインを探す
		if (py - 1 >= 0) {
			for (int x = px + lx; x <= px + rx; x++) {
				// 右端を探す
				boolean isMatch = false;
				for (int i = 0; i < srcColors.size(); i++) {
					if (isNear(buffer.getElem(0, x + (py - 1) * width), srcColors.get(i), near)) {
						isMatch = true;
						break;
					}
				}
				if (isMatch) {
					boolean isMatch2 = false;
					if (x < px + rx) {
						for (int i = 0; i < srcColors.size(); i++) {
							if (isNear(buffer.getElem(0, (x + 1) + (py - 1) * width), srcColors.get(i), near)) {
								isMatch2 = true;
								break;
							}
						}
					}
					if (x == px + rx || !isMatch2) {
						if (newBuffer.getElem(0, x + (py - 1) * width) == 0x00000000) {
							// 未登録なので登録する
							pointList.add(new Point(x, py - 1));
							int c = buffer.getElem(0, x + (py - 1) * width);
							if (c == 0)
								c = 1;
							newBuffer.setElem(0, x + (py - 1) * width, c);
						}
					}
				}
			}
		}

		// 下のラインを探す
		if (py + 1 < height) {
			for (int x = px + lx; x <= px + rx; x++) {
				// 右端を探す
				boolean isMatch = false;
				for (int i = 0; i < srcColors.size(); i++) {
					if (isNear(buffer.getElem(0, x + (py + 1) * width), srcColors.get(i), near)) {
						isMatch = true;
						break;
					}
				}
				if (isMatch) {
					boolean isMatch2 = false;
					if (x < px + rx) {
						for (int i = 0; i < srcColors.size(); i++) {
							if (isNear(buffer.getElem(0, (x + 1) + (py + 1) * width), srcColors.get(i), near)) {
								isMatch2 = true;
								break;
							}
						}
					}
					if (x == px + rx || !isMatch2) {
						if (newBuffer.getElem(0, x + (py + 1) * width) == 0x00000000) {
							// 未登録なので登録する
							pointList.add(new Point(x, py + 1));
							int c = buffer.getElem(0, x + (py + 1) * width);
							if (c == 0)
								c = 1;
							newBuffer.setElem(0, x + (py + 1) * width, c);
						}
					}
				}
			}
		}
	}

	private void makesrcbits() {
		boolean allScreen = false;
		if (GUI.key[12] > 0) { // opt
			allScreen = true;
		}

		// 時間がかかるのでカーソルを時計に
		PaintTool.owner.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		// 含まれる色を列挙
		ArrayList<Color> srcColors = new ArrayList<Color>();
		int width = PaintTool.owner.getSurface().getWidth();
		int near = PaintTool.smartSelectPercent * 256 / 100;
		if (srcPoints.size() > 512) {
			near += 3;
		}
		for (int j = 0; j < srcPoints.size(); j++) {
			int d = PaintTool.owner.getSurface().getRaster().getDataBuffer()
					.getElem(srcPoints.get(j).x + srcPoints.get(j).y * width);
			Color c = new Color((d >> 16) & 0xFF, (d >> 8) & 0xFF, (d >> 0) & 0xFF, (d >> 24) & 0xFF);
			boolean isFound = false;
			for (int i = srcColors.size() - 1; i >= 0; i--) {
				if (isNear(d, srcColors.get(i), near / 2)) {
					isFound = true;
					break;
				}
			}
			if (!isFound)
				srcColors.add(c);
		}

		// 塗りつぶし候補リストをリセット
		pointList = new ArrayList<Point>();

		if (allScreen) {
			// すべての類似色を検索
			DataBuffer buffer = PaintTool.owner.getSurface().getRaster().getDataBuffer();
			DataBuffer newBuffer = srcbits.getRaster().getDataBuffer();
			int width1 = PaintTool.owner.getSurface().getWidth();
			int height1 = PaintTool.owner.getSurface().getHeight();
			int near1 = PaintTool.smartSelectPercent * 256 / 100;
			// Color color = Color.WHITE;
			int lasti = 0;
			for (int y = 0; y < height1; y++) {
				for (int x = 0; x < width1; x++) {
					boolean isMatch = false;
					if (isNear(buffer.getElem((x) + y * width), srcColors.get(lasti), near1)) {
						isMatch = true;
					} else {
						for (int i = 0; i < srcColors.size(); i++) {
							if (isNear(buffer.getElem((x) + y * width), srcColors.get(i), near1)) {
								isMatch = true;
								lasti = i;
								break;
							}
						}
					}
					if (isMatch) {
						int c = buffer.getElem(0, x + y * width);
						newBuffer.setElem(0, x + y * width, c);
					}
				}
			}
		} else {
			// 隣り合った類似色を検索
			for (int j = 0; j < srcPoints.size(); j++) {
				seedfillH(PaintTool.owner.getSurface(), srcbits, srcPoints.get(j).x, srcPoints.get(j).y, srcColors);

				while (pointList.size() > 0) {
					Point p = pointList.get(0);
					pointList.remove(0);
					seedfillH(PaintTool.owner.getSurface(), srcbits, p.x, p.y, srcColors);
				}
			}
		}

		// 色リストはもういらないのでクリア
		srcPoints = new ArrayList<Point>();

		// カーソルを戻す
		TBCursor.changeCursor(PaintTool.owner);
	}

	private void addSelection(int x, int y) {
		boolean isFound = false;
		if (x < 0 || y < 0 || x >= PaintTool.owner.mainPane.getWidth() || y >= PaintTool.owner.mainPane.getHeight()) {
			return;
		}
		for (int i = 0; i < srcPoints.size(); i++) {
			if (x == srcPoints.get(i).x && y == srcPoints.get(i).y) {
				isFound = true;
			}

		}
		if (!isFound) {
			srcPoints.add(new Point(x, y));

			Graphics2D g3 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();
			if (PaintTool.owner.bit > 1) {
				AffineTransform af = new AffineTransform();
				af.translate(-((int) PaintTool.owner.bitLeft) * PaintTool.owner.bit + PaintTool.owner.bit / 2,
						-((int) PaintTool.owner.bitTop) * PaintTool.owner.bit + PaintTool.owner.bit / 2);
				af.scale(PaintTool.owner.bit, PaintTool.owner.bit);
				g3.transform(af);
			}
			g3.setColor(Color.BLACK);
			g3.drawLine(x, y, x, y);
		}
	}

	void viewSelection() {
		// PaintTool.owner.mainPane.repaint();

		// 選択領域を表示
		borderDraw();
	}

	@Override
	public void mouseUp(int x, int y) {
		if (move == false && srcbits != null) {
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
		if (move == true) {
			DataBuffer mskbuf = srcbits.getRaster().getDataBuffer();
			if ((x - movePoint.x) >= 0 && (x - movePoint.x) < PaintTool.owner.redoBuf.getWidth()
					&& (y - movePoint.y) >= 0 && (y - movePoint.y) < PaintTool.owner.redoBuf.getHeight()) {
				// 選択範囲の移動開始
				shift = (GUI.key[11] > 0);
				shiftx = x;
				shifty = y;

				int c = mskbuf.getElem(0, (x - movePoint.x) + (y - movePoint.y) * PaintTool.owner.redoBuf.getWidth());
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
			if (GUI.key[14] > 1 && srcbits != null && srcbits.getWidth() > 1) {// cmd
				this_end();
				move = false;
				tmpRect = null;
			} else if (move) {
				// 浮き出し領域を実際の領域に描画
				GMenuPaint.setUndo();
				this_end();

				// 新しい選択領域を作る
				clear();
			} else {
				// 新しい選択領域を作る
				clear();

				// マスク用バッファを用意する
				srcbits = new BufferedImage(PaintTool.owner.mainImg.getWidth(), PaintTool.owner.mainImg.getHeight(),
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = (Graphics2D) srcbits.getGraphics();
				g.setColor(new Color(255, 255, 255));
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
				g.fillRect(0, 0, srcbits.getWidth(), srcbits.getHeight());
			}

			addSelection(x, y);
		}
	}

	@Override
	public boolean mouseWithin(int x, int y) {

		if (GUI.key[20] > 0 || GUI.key[21] > 0) { // BACKSPACE or DEL
			clear();
			PaintTool.owner.mainPane.repaint();
			return false;
		}

		boolean isWithin = false;
		if (move == true && srcbits != null) {
			DataBuffer mskbuf = srcbits.getRaster().getDataBuffer();
			if ((x - movePoint.x) >= 0 && (x - movePoint.x) < srcbits.getWidth() && (y - movePoint.y) >= 0
					&& (y - movePoint.y) < srcbits.getHeight()) {
				int c = mskbuf.getElem(0, (x - movePoint.x) + (y - movePoint.y) * srcbits.getWidth());
				if ((c & 0xFF000000) != 0) {
					PaintTool.owner.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					isWithin = true;
				}
			}
		}

		if (!isWithin) {
			PaintTool.owner.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}

		viewSelection();

		return false;
	}

	@Override
	public boolean mouseStillDown(int x, int y) {
		if (move == true) {
			// 選択範囲の移動

			if (shift && ((x - shiftx != 0) || (y - shifty != 0))) {
				if ((shiftx == -1) || (shifty != -1) && (Math.abs(x - shiftx) - 1 > Math.abs(y - shifty))) {
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

			/*
			 * if(tmpRect==null){ tmpRect = getSelectedRect(); } Rectangle rect = new
			 * Rectangle(movePoint.x-(int)Math.abs(x - PaintTool.lastx[0])-1,
			 * movePoint.y-(int)Math.abs(y - PaintTool.lasty[0])-1, (int)Math.abs(x -
			 * PaintTool.lastx[0])*2+2, (int)Math.abs(y - PaintTool.lasty[0])*3+2);
			 * rect.width+=tmpRect.width; rect.height+=tmpRect.height;
			 * 
			 * if(PaintTool.owner.bit>1){ PaintTool.owner.mainPane.repaint(
			 * (rect.x-(int)PaintTool.owner.bitLeft)*PaintTool.owner.bit,
			 * (rect.y-(int)PaintTool.owner.bitTop)*PaintTool.owner.bit,
			 * (rect.width)*PaintTool.owner.bit, (rect.height)*PaintTool.owner.bit); }else{
			 * PaintTool.owner.mainPane.repaint(rect); }
			 */
			PaintTool.owner.mainPane.repaint();

			viewSelection();
		} else if (srcbits != null) {
			// 選択色を広げて行く
			for (int i = 0; i < 8; i++) {
				addSelection((int) ((PaintTool.lastx[0] * i + x * (8 - i)) / 8),
						(int) ((PaintTool.lasty[0] * i + y * (8 - i)) / 8));
			}
			// addSelection(x,y);
		}
		return true;
	}

	@Override
	public void clear() {
		srcbits = null;// new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		srcPoints = new ArrayList<Point>();
		move = false;
		tmpRect = null;
	}

	@Override
	public void end() {
		this_end();
		srcbits = null;
	}

	private void this_end() {
		if (move) {
			Graphics2D g = (Graphics2D) PaintTool.owner.getSurface().getGraphics();
			BufferedImage newimg = MyPanel.makeBlendImage(PaintTool.owner.redoBuf);
			g.drawImage(newimg, movePoint.x, movePoint.y, null);

			// 回転した場合はバッファの大きさが変わっているので新しくする
			PaintTool.owner.redoBuf = new BufferedImage(PaintTool.owner.mainImg.getWidth(),
					PaintTool.owner.mainImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
			move = false;
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
			return /* srcbits;//PaintTool. */owner.redoBuf;
		}
		return null;
	}

	@Override
	public Rectangle getSelectedRect() {
		return LassoTool.makeSelectedRect(srcbits);
	}

	@Override
	public Rectangle getMoveRect() {
		Rectangle rect = LassoTool.makeSelectedRect(srcbits);
		rect.x += movePoint.x;
		rect.y += movePoint.y;
		return rect;
	}

	@Override
	public boolean isMove() {
		return move;
	}
}
