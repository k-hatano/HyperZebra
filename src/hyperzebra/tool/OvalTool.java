package hyperzebra.tool;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import hyperzebra.gui.GUI;
import hyperzebra.gui.MyPanel;
import hyperzebra.gui.menu.GMenuPaint;

//-------------------
//楕円
//-------------------
public class OvalTool implements toolInterface {
	private Rectangle srcRect;
	private Rectangle lastClipRect = new Rectangle();

	@Override
	public String getName() {
		return "Oval";
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

		// 楕円を裏画面に書く
		if (PaintTool.antialias) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		// 線の種類
		g2.setColor(PaintTool.owner.fore.color);
		if (PaintTool.antialias) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		if (PaintTool.owner.fill) {
			// パターン
			Paint savePaint = g2.getPaint();
			Rectangle2D.Double r = new Rectangle2D.Double(0, 0, PaintTool.pat.getWidth(), PaintTool.pat.getHeight());
			g2.setPaint(new TexturePaint(PaintTool.pat, r));
			g2.fill(new Ellipse2D.Float(rect.x, rect.y, rect.width, rect.height));
			g2.setPaint(savePaint);
		}

		// 楕円を描く
		g2.setStroke(new BasicStroke(lineSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		g2.draw(new Ellipse2D.Float(rect.x, rect.y, rect.width, rect.height));

		Graphics2D g4 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();

		// 拡大表示
		if (PaintTool.owner.bit > 1) {
			AffineTransform af = new AffineTransform();
			af.translate(-((int) PaintTool.owner.bitLeft) * PaintTool.owner.bit,
					-((int) PaintTool.owner.bitTop) * PaintTool.owner.bit);
			af.scale(PaintTool.owner.bit, PaintTool.owner.bit);
			g4.transform(af);
		}

		// 透明度を反映して楕円を画面に描画
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

		lastClipRect = clipRect;

	}

	private Rectangle setSelection(int x, int y) {
		// Graphics2D g3 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();

		Rectangle r = (Rectangle) srcRect.clone();

		if (GUI.key[11] > 1) { // Shiftで正方形
			if (Math.abs(r.x - x) < Math.abs(r.y - y)) {
				if ((r.x - x) < 0 == (r.y - y) < 0)
					y = (x - r.x) + r.y;
				else
					y = (r.x - x) + r.y;
			} else {
				if ((r.x - x) < 0 == (r.y - y) < 0)
					x = (y - r.y) + r.x;
				else
					x = (r.y - y) + r.x;
			}
		}

		if (x < r.x) {
			r.width = r.x - x;
			r.x = x;
		} else {
			r.width = x - r.x;
		}
		if (y < r.y) {
			r.height = r.y - y;
			r.y = y;
		} else {
			r.height = y - r.y;
		}

		strokeDraw(r);

		return r;
	}

	@Override
	public void mouseUp(int x, int y) {
		{
			srcRect = setSelection(x, y);

			// サーフェースに反映
			Graphics2D g = PaintTool.owner.getSurface().createGraphics();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, PaintTool.alpha / 100.0F));
			g.drawImage(PaintTool.owner.redoBuf, 0, 0, PaintTool.owner.mainPane);

			PaintTool.owner.mainPane.repaint();
		}
	}

	@Override
	public void mouseDown(int x, int y) {
		{
			GMenuPaint.setUndo();

			PaintTool.setPattern(GUI.key[14] > 0);

			// 新しい選択領域を作る
			srcRect = new Rectangle(x, y, 0, 0);
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
