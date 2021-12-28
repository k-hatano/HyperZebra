package hyperzebra.tool;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.DataBuffer;

import hyperzebra.gui.GUI;
import hyperzebra.gui.MyPanel;
import hyperzebra.gui.menu.GMenuPaint;

//-------------------
//消しゴム
//-------------------
public class EraserTool implements toolTabletInterface {
	public Cursor cursor;
	boolean shift;
	float shiftx, shifty;

	public EraserTool() {
		PaintTool.tabletinit();

		if (PaintTool.canTablet) {
			// TabletManager.getDefaultManager().addTabletListener(PaintTool.owner.mainPane,
			// PaintTool.tabletListener);
			// PaintTool.tabletListener.tool = this;
		}
	}

	@Override
	public String getName() {
		return "Eraser";
	}

	@Override
	public void mouseUp(int x, int y) {
		/*
		 * if(PaintTool.tabletListener.in_stroke){ return; }
		 */
		penUp(x, y, 0.5f, false);
	}

	@Override
	public void penUp(float x, float y, float pressure, boolean eraser) {

		// サーフェースに書き込む
		if (true) { // eraser
			DataBuffer srcdb = PaintTool.owner.redoBuf.getRaster().getDataBuffer();
			DataBuffer tgtdb = PaintTool.owner.getSurface().getRaster().getDataBuffer();
			int width = PaintTool.owner.getSurface().getWidth();
			for (int h = 0; h < PaintTool.owner.getSurface().getHeight(); h++) {
				for (int w = 0; w < width; w++) {
					int src = srcdb.getElem(0, h * width + w);
					int tgt = tgtdb.getElem(0, h * width + w);
					int c = tgt;
					if ((src & 0xFF000000) != 0) {
						// int a = (c&0xFF000000)>>24;
						// a = (a * (0xFF-((src&0xFF000000)>>24)))/0xFF;
						// c = (a<<24) + tgt&0x00FFFFFF;
						if (PaintTool.editBackground) {
							c = 0xFFFFFFFF;
						} else {
							c = 0x00FFFFFF;
						}
					}
					tgtdb.setElem(0, h * width + w, c);
				}
			}
		}

		PaintTool.toCdPict();
		PaintTool.owner.mainPane.repaint();
	}

	@Override
	public void mouseDown(int x, int y) {
		/*
		 * if(PaintTool.tabletListener.in_stroke){ return; }
		 */
		penDown(x, y + 0.5f, 0.5f, false);
	}

	@Override
	public void penDown(float x, float y, float pressure, boolean eraser) {
		GMenuPaint.setUndo();
		shift = (GUI.key[11] > 0);
		shiftx = x;
		shifty = y;

		if (true) { // eraser
			Graphics2D g2 = (Graphics2D) PaintTool.owner.redoBuf.getGraphics();
			g2.setColor(new Color(255, 255, 255));
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			g2.fillRect(0, 0, PaintTool.owner.redoBuf.getWidth(), PaintTool.owner.redoBuf.getHeight());
		}

		PaintTool.lastx[0] = x;
		PaintTool.lasty[0] = y;
		penStillDown(x, y, pressure, eraser);
	}

	@Override
	public boolean mouseWithin(int x, int y) {
		/*
		 * if(PaintTool.tabletListener.in_stroke){ return false; }
		 */
		return penWithin(x, y, 0.5f);
	}

	public boolean penWithin(float x, float y, float pressure) {
		return true;
	}

	@Override
	public boolean mouseStillDown(int x, int y) {
		/*
		 * if(PaintTool.tabletListener.in_stroke){ return false; }
		 */
		return penStillDown(x, y + 0.5f, 0.5f, false);
	}

	@Override
	public boolean penStillDown(float x, float y, float pressure, boolean eraser) {
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
		if (true) { // eraser
			g2 = (Graphics2D) PaintTool.owner.redoBuf.getGraphics();
		}

		// 線を裏画面に書く
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		float brushSize = 16.0f / PaintTool.owner.bit;
		g2.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

		if (PaintTool.editBackground) {
			g2.setColor(Color.WHITE);
		} else {
			Rectangle2D.Double r = new Rectangle2D.Double(0, 0, PaintTool.owner.bgImg.getWidth(),
					PaintTool.owner.bgImg.getHeight());
			g2.setPaint(new TexturePaint(PaintTool.owner.bgImg, r));
		}
		Line2D.Double line;
		for (int i = 1; i <= 16; i++) {
			line = new Line2D.Double((PaintTool.lastx[0] * i + x * (16 - i)) / 16,
					(PaintTool.lasty[0] * i + y * (16 - i)) / 16 - brushSize / 2,
					(PaintTool.lastx[0] * i + x * (16 - i)) / 16,
					(PaintTool.lasty[0] * i + y * (16 - i)) / 16 + brushSize / 2);
			g2.draw(line);
		}

		// クリップサイズを計算
		int left = 0, right = 0, top = 0, bottom = 0;
		if (x < PaintTool.lastx[0]) {
			left = (int) (x - brushSize / 2 - 4);
			right = (int) (PaintTool.lastx[0] + brushSize / 2 + 4);
		} else {
			left = (int) (PaintTool.lastx[0] - brushSize / 2 - 4);
			right = (int) (x + brushSize / 2 + 4);
		}
		if (y < PaintTool.lasty[0]) {
			top = (int) (y - brushSize / 2 - 4);
			bottom = (int) (PaintTool.lasty[0] + brushSize / 2 + 4);
		} else {
			top = (int) (PaintTool.lasty[0] - brushSize / 2 - 4);
			bottom = (int) (y + brushSize / 2 + 4);
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

		// 透明度を反映して線分を画面に描画
		if (true) {
			if (PaintTool.owner.bit > 1) {
				AffineTransform af = new AffineTransform();
				af.translate(-((int) PaintTool.owner.bitLeft) * PaintTool.owner.bit/* +4 */,
						-((int) PaintTool.owner.bitTop) * PaintTool.owner.bit/* +4 */);
				af.scale(PaintTool.owner.bit, PaintTool.owner.bit);
				g4.transform(af);
			}

			g4.setClip(new Rectangle(left, top, right - left, bottom - top));
			g4.drawImage(PaintTool.owner.bgImg, 0, 0, PaintTool.owner.mainPane);
			g4.drawImage(PaintTool.owner.getSurface(), 0, 0, PaintTool.owner.mainPane);
			/*
			 * if(eraser){
			 * g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			 * }else{ g4.setComposite(
			 * AlphaComposite.getInstance(AlphaComposite.SRC_OVER,PaintTool.alpha/100.0F) );
			 * }
			 */
			g4.drawImage(PaintTool.owner.redoBuf, 0, 0, PaintTool.owner.mainPane);

			// 拡大表示時の枠線表示
			if (PaintTool.owner.bit > 2) {
				Graphics g5 = PaintTool.owner.mainPane.getGraphics();
				MyPanel.bordersDraw(g5, PaintTool.owner.bit, PaintTool.owner.mainPane.getWidth(),
						PaintTool.owner.mainPane.getHeight());
			}

			PaintTool.lastTime = System.currentTimeMillis();
		}

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
