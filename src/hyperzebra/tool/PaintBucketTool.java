package hyperzebra.tool;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;

import hyperzebra.gui.GUI;
import hyperzebra.gui.menu.GMenuPaint;

//-------------------
//バケツ
//-------------------
public class PaintBucketTool implements toolInterface {
	ArrayList<Point> pointList;

	@Override
	public String getName() {
		return "PaintBucket";
	}

	public static void gradfill(BufferedImage img, Color color1, Color color2, double angle) {
		// imgの不透明な部分のangle補正した領域を取得
		DataBuffer buffer = img.getRaster().getDataBuffer();
		Point topPoint = null;
		Point bottomPoint = null;
		int width = img.getWidth();
		int height = img.getHeight();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if ((0xFF000000 & buffer.getElem(0, x + y * width)) != 0) {
					// System.out.println((0xFF000000&buffer.getElem(0, x+y*width))+"
					// "+(buffer.getElem(0, x+y*width)));
					if (topPoint == null) {
						topPoint = new Point(x, y);
						bottomPoint = new Point(x, y);
					}
					if (x * Math.sin(angle) + y * Math.cos(angle) < topPoint.x * Math.sin(angle)
							+ topPoint.y * Math.cos(angle)) {
						topPoint.x = x;
						topPoint.y = y;
					}
					if (x * Math.sin(angle) + y * Math.cos(angle) > bottomPoint.x * Math.sin(angle)
							+ bottomPoint.y * Math.cos(angle)) {
						bottomPoint.x = x;
						bottomPoint.y = y;
					}
				}
			}
		}

		// 不透明な部分をグラデーション塗り
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// System.out.println(buffer.getElem(0, x+y*width));
				if ((0xFF000000 & buffer.getElem(0, x + y * width)) != 0) {
					int c = 0;
					double percent;
					percent = (x * Math.sin(angle) + y * Math.cos(angle)
							- (topPoint.x * Math.sin(angle) + topPoint.y * Math.cos(angle)))
							/ (bottomPoint.x * Math.sin(angle) + bottomPoint.y * Math.cos(angle)
									- (topPoint.x * Math.sin(angle) + topPoint.y * Math.cos(angle)));
					c = ((int) (color1.getRed() * percent + color2.getRed() * (1.0 - percent))) << 16;
					c += ((int) (color1.getGreen() * percent + color2.getGreen() * (1.0 - percent))) << 8;
					c += ((int) (color1.getBlue() * percent + color2.getBlue() * (1.0 - percent)));
					buffer.setElem(0, x + y * width, 0xFF000000 + c);
				}
			}
		}
	}

	private void seedfillH(BufferedImage surface, BufferedImage newSurface, int px, int py, int srcColor,
			int newColor) {
		DataBuffer buffer = surface.getRaster().getDataBuffer();
		DataBuffer newBuffer = newSurface.getRaster().getDataBuffer();
		int width = surface.getWidth();
		int height = surface.getHeight();

		// 左を調べる
		int lx;
		for (lx = -1; px + lx >= 0; lx--) {
			// System.out.println("<srcColor:"+srcColor);
			// System.out.println("<getElem("+(px+lx)+","+py+"):"+buffer.getElem(0,
			// px+lx+py*width));
			if ((0x00FFFFFF & buffer.getElem(0, px + lx + py * width)) != (0x00FFFFFF & srcColor)) {
				break;
			}
		}
		lx++;

		// 右を調べる
		int rx;
		for (rx = 1; px + rx < width; rx++) {
			// System.out.println(">srcColor:"+srcColor);
			// System.out.println(">getElem("+(px+rx)+","+py+"):"+buffer.getElem(0,
			// px+lx+py*width));
			if ((0x00FFFFFF & buffer.getElem(0, px + rx + py * width)) != (0x00FFFFFF & srcColor)) {
				break;
			}
		}
		rx--;

		// そのラインを塗る
		for (int x = px + lx; x <= px + rx; x++) {
			newBuffer.setElem(0, x + py * width, newColor);
		}

		// 上のラインを探す
		if (py - 1 >= 0) {
			for (int x = px + lx; x <= px + rx; x++) {
				// 右端を探す
				if ((0x00FFFFFF & buffer.getElem(0, x + (py - 1) * width)) == (0x00FFFFFF & srcColor)) {
					if (x == px + rx || (0x00FFFFFF & buffer.getElem(0, (x + 1) + (py - 1) * width)) != (0x00FFFFFF
							& srcColor)) {
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
				if ((0x00FFFFFF & buffer.getElem(0, x + (py + 1) * width)) == (0x00FFFFFF & srcColor)) {
					if (x + 1 == px + rx || (0x00FFFFFF & buffer.getElem(0, (x + 1) + (py + 1) * width)) != (0x00FFFFFF
							& srcColor)) {
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

	private void seedfill(BufferedImage surface, BufferedImage newSurface, int px, int py) {
		DataBuffer buffer = surface.getRaster().getDataBuffer();
		int width = surface.getWidth();
		int height = surface.getHeight();
		int srcColor = buffer.getElem(0, px + py * width);
		// Color c = PaintTool.owner.stack.toolbar.fore.color;
		int newColor = 0xFF000000/* +(c.getRed()<<16)+(c.getGreen()<<8)+c.getBlue() */;

		// リストをリセット
		pointList = new ArrayList<Point>();

		// 裏画面を透明にする
		Graphics2D g = (Graphics2D) newSurface.getGraphics();
		g.setColor(new Color(255, 255, 255));
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, width, height);
		g.fill(rect);

		// サーフェースの透明部分を#FFFFFFにする
		DataBuffer dbuffer = surface.getRaster().getDataBuffer();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if ((dbuffer.getElem(x + y * width) & -0xFF000000) == 0) {
					dbuffer.setElem(x + y * width, 0x00FFFFFF);
				}
			}
		}

		seedfillH(surface, newSurface, px, py, srcColor, newColor);

		while (pointList.size() > 0) {
			Point p = pointList.get(0);
			pointList.remove(0);
			seedfillH(surface, newSurface, p.x, p.y, srcColor, newColor);
		}

		// パターンを適用
		PaintTool.setPattern(false);
		DataBuffer fillbuf = newSurface.getRaster().getDataBuffer();
		DataBuffer patbuf = PaintTool.pat.getRaster().getDataBuffer();
		int patW = PaintTool.pat.getWidth();
		int patH = PaintTool.pat.getHeight();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int c1 = fillbuf.getElem(0, x + y * width);
				if ((c1 & 0xFF000000) != 0) {
					int c2 = patbuf.getElem(x % patW + (y % patH) * patW);
					// c2 = (c2&0x00FFFFFF) +
					// ((((c1&0xFF000000)>>24)*((c2&0xFF000000)>>24))/0xFF)<<24;
					fillbuf.setElem(0, x + y * width, c2);
				}
			}
		}

		// グラデーションを適用(パターンとの併用は不可)
		if (PaintTool.owner.grad.use) {
			gradfill(newSurface, PaintTool.owner.grad.color1, PaintTool.owner.grad.color2, PaintTool.owner.grad.angle);
		}

		// サーフェースに反映する
		Graphics2D g2 = (Graphics2D) surface.getGraphics();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, PaintTool.alpha / 100.0F));
		g2.drawImage(newSurface, 0, 0, PaintTool.owner.mainPane);

		// 表画面に反映する
		Graphics2D g3 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();
		g3.drawImage(PaintTool.owner.bgImg, 0, 0, PaintTool.owner.mainPane);
		if (!PaintTool.editBackground) {
			g3.drawImage(PaintTool.owner.mainImg, 0, 0, PaintTool.owner.mainPane);
		}
	}

	@Override
	public void mouseUp(int x, int y) {
		if (GUI.key[12] > 1) {
			PaintTool.spoit(x, y);
			return;
		}

		GMenuPaint.setUndo();

		seedfill(PaintTool.owner.getSurface(), PaintTool.owner.redoBuf, x, y);

		PaintTool.toCdPict();
		PaintTool.owner.mainPane.repaint();
	}

	@Override
	public void mouseDown(int x, int y) {
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
		return true;
	}

	@Override
	public void clear() {

	}

	@Override
	public void end() {

	}
}