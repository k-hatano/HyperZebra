package hyperzebra.tool;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import hyperzebra.gui.GUI;
import hyperzebra.gui.MyPanel;
import hyperzebra.gui.menu.GMenuPaint;

//-------------------
//線分
//-------------------
public class LineTool implements toolInterface {
	private Rectangle srcRect;
	private Rectangle lastClipRect = new Rectangle();

	@Override
	public String getName() {
		return "Line";
	}

	private void strokeDraw(Rectangle rect) {
		float lineSize = PaintTool.lineSize;

		Rectangle clipRect = (Rectangle) rect.clone();
		if (clipRect.width < 0) {
			clipRect.width *= -1;
			clipRect.x -= clipRect.width;
		}
		if (clipRect.height < 0) {
			clipRect.height *= -1;
			clipRect.y -= clipRect.height;
		}
		clipRect.x -= (int) lineSize + 1;
		clipRect.y -= (int) lineSize + 1;
		clipRect.width += 2 * (int) lineSize + 2;
		clipRect.height += 2 * (int) lineSize + 2;

		// 裏画面を取得
		Graphics2D g2 = (Graphics2D) PaintTool.owner.redoBuf.getGraphics();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		g2.fill(clipRect.union(lastClipRect));
		g2 = (Graphics2D) PaintTool.owner.redoBuf.getGraphics();

		// 線を裏画面に書く

		// 線の種類
		g2.setColor(PaintTool.owner.fore.color);
		if (PaintTool.antialias) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		g2.setStroke(new BasicStroke(lineSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		// 線を引く
		g2.drawLine(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);

		Graphics2D g4 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();

		// 拡大表示
		if (PaintTool.owner.bit > 1) {
			AffineTransform af = new AffineTransform();
			af.translate(-((int) PaintTool.owner.bitLeft) * PaintTool.owner.bit,
					-((int) PaintTool.owner.bitTop) * PaintTool.owner.bit);
			af.scale(PaintTool.owner.bit, PaintTool.owner.bit);
			g4.transform(af);
		}

		// 透明度を反映して線分を画面に描画
		g4.setClip(clipRect.union(lastClipRect));
		g4.drawImage(PaintTool.owner.bgImg, 0, 0, PaintTool.owner.mainPane);
		g4.drawImage(PaintTool.owner.getSurface(), 0, 0, PaintTool.owner.mainPane);
		{
			g4.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, PaintTool.alpha / 100.0F));
		}
		g4.drawImage(PaintTool.owner.redoBuf, 0, 0, PaintTool.owner.mainPane);

		// 拡大表示時の枠線表示
		if (PaintTool.owner.bit > 2) {
			Graphics g5 = PaintTool.owner.mainPane.getGraphics();
			MyPanel.bordersDraw(g5, PaintTool.owner.bit, PaintTool.owner.mainPane.getWidth(),
					PaintTool.owner.mainPane.getHeight());
		}

		PaintTool.lastTime = System.currentTimeMillis();

		lastClipRect = clipRect;
	}

	private Rectangle setSelection(int x, int y) {
		if (srcRect == null)
			return new Rectangle(x, y, 0, 0);

		Rectangle r = (Rectangle) srcRect.clone();

		if (GUI.key[11] > 1) { // Shiftで45度単位
			if (Math.abs(r.x - x) < Math.abs(r.y - y) / 2) {
				x = r.x;
			} else if (Math.abs(r.x - x) < Math.abs(r.y - y)) {
				if ((r.x - x) < 0 == (r.y - y) < 0)
					y = (x - r.x) + r.y;
				else
					y = (r.x - x) + r.y;
			} else if (Math.abs(r.x - x) / 2 > Math.abs(r.y - y)) {
				y = r.y;
			} else {
				if ((r.x - x) < 0 == (r.y - y) < 0)
					x = (y - r.y) + r.x;
				else
					x = (r.y - y) + r.x;
			}
		}

		r.width = x - r.x;
		r.height = y - r.y;

		strokeDraw(r);

		return r;
	}

	@Override
	public void mouseUp(int x, int y) {
		srcRect = setSelection(x, y);

		Graphics2D g = PaintTool.owner.getSurface().createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, PaintTool.alpha / 100.0F));
		g.drawImage(PaintTool.owner.redoBuf, 0, 0, PaintTool.owner.mainPane);

		/*
		 * //サーフェースに反映 Graphics2D g3 = PaintTool.owner.getSurface().createGraphics();
		 * 
		 * //線の種類 g3.setColor(PaintTool.owner.fore.color); if(PaintTool.antialias){
		 * g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		 * RenderingHints.VALUE_ANTIALIAS_ON); }else{
		 * g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		 * RenderingHints.VALUE_ANTIALIAS_OFF); } float lineSize = PaintTool.lineSize;
		 * g3.setStroke(new BasicStroke(lineSize, BasicStroke.CAP_ROUND,
		 * BasicStroke.JOIN_ROUND));
		 * 
		 * //線を引く g3.drawLine(srcRect.x, srcRect.y, srcRect.x+srcRect.width,
		 * srcRect.y+srcRect.height);
		 */
		PaintTool.owner.mainPane.repaint();
	}

	@Override
	public void mouseDown(int x, int y) {
		{
			GMenuPaint.setUndo();

			PaintTool.setPattern(GUI.key[14] > 0);

			// 新しい選択領域を作る
			srcRect = new Rectangle(x, y, 0, 0);

			lastClipRect = new Rectangle(x, y, 0, 0);
		}
	}

	@Override
	public boolean mouseWithin(int x, int y) {
		return false;
	}

	@Override
	public boolean mouseStillDown(int x, int y) {
		{
			if (x == srcRect.x + srcRect.width && y == srcRect.y + srcRect.height) {
				return false;
			}
			setSelection(x, y);
		}
		return true;
	}

	@Override
	public void clear() {
	}

	@Override
	public void end() {
	}
}