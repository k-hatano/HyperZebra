package hyperzebra.tool;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import hyperzebra.gui.PCARD;
import hyperzebra.gui.field.MyTextArea;
import hyperzebra.gui.menu.GMenuPaint;
import hyperzebra.object.OField;

//-------------------
//文字
//-------------------
public class TypeTool implements toolInterface {
	public MyTextArea area;
	Rectangle typeRect;
	boolean type = false;

	@Override
	public String getName() {
		return "Type";
	}

	void viewRect() {
		// 一時バッファの部分を描画
		Graphics2D g4 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();
		g4.drawImage(PaintTool.owner.redoBuf, typeRect.x, typeRect.y, typeRect.x + typeRect.width,
				typeRect.y + typeRect.height, 0, 0, typeRect.width, typeRect.height, PaintTool.owner.mainPane);

		// 選択領域を表示
		// strokeDraw();
	}

	@Override
	public void mouseUp(int x, int y) {
		if (type == false) {
			GMenuPaint.setUndo();

			// setRect(x,y);

			type = true;

			// 一時バッファをクリア
			Graphics2D g = (Graphics2D) PaintTool.owner.redoBuf.getGraphics();
			g.setColor(new Color(255, 255, 255));
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			g.fillRect(0, 0, PaintTool.owner.redoBuf.getWidth(), PaintTool.owner.redoBuf.getHeight());

			Rectangle rect = (Rectangle) typeRect.clone();
			if (PaintTool.owner.bit > 1) {
				rect.x = (int) (rect.x - PaintTool.owner.bitLeft) * PaintTool.owner.bit;
				rect.y = (int) (rect.y - PaintTool.owner.bitTop) * PaintTool.owner.bit;
				rect.width *= PaintTool.owner.bit;
				rect.height *= PaintTool.owner.bit;
			}

			area = new MyTextArea("");
			area.fldData = new OField(null, 0);
			area.setBounds(rect);
			area.setOpaque(false);
			area.fldData.textFont = PCARD.pc.textFont;
			area.fldData.textSize = Math.min(512, PCARD.pc.textSize * PaintTool.owner.bit);
			area.fldData.textStyle = PCARD.pc.textStyle;
			// area.fldData.textAlign = PCARD.pc.textAlign;
			area.setFont(new Font(PCARD.pc.textFont, PCARD.pc.textStyle, area.fldData.textSize));
			area.setForeground(PaintTool.owner.fore.color);
			area.getDocument().addDocumentListener(new TypeToolListener());

			PaintTool.owner.mainPane.add(area);
			area.requestFocus();

			viewRect();
		} else {
			mouseStillDown(x, y);
		}
	}

	@Override
	public void mouseDown(int x, int y) {
		if (type == true && typeRect.contains(new Point(x, y))) {

		} else {
			// 浮き出し領域を実際の領域に描画
			GMenuPaint.setUndo();
			end();

			// 新しい選択領域を作る
			typeRect = new Rectangle(x, y, 1, PCARD.pc.textSize + 5);
			type = false;
		}
	}

	@Override
	public boolean mouseWithin(int x, int y) {
		// strokeDraw();
		return false;
	}

	@Override
	public boolean mouseStillDown(int x, int y) {
		if (type == true) {
			// 移動
			// typeRect.x += x - PaintTool.lastx[0];
			// typeRect.y += y - PaintTool.lasty[0];

			// 表画面に反映
			Graphics2D g3 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();
			g3.drawImage(PaintTool.owner.bgImg, 0, 0, PaintTool.owner.mainPane);
			if (!PaintTool.editBackground) {
				g3.drawImage(PaintTool.owner.mainImg, 0, 0, PaintTool.owner.mainPane);
			}

			viewRect();
		} else {
			/*
			 * if(x==typeRect.x+typeRect.width&&y==typeRect.y+typeRect.height){ return
			 * false; } setRect(x,y);
			 */
		}
		return true;
	}

	@Override
	public void clear() {
		type = false;
		typeRect = null;
		if (area != null) {
			PaintTool.owner.mainPane.remove(area);
			area = null;
		}
	}

	@Override
	public void end() {
		if (type) {
			// textareaを裏画面に描画
			Graphics2D redog = PaintTool.owner.redoBuf.createGraphics();
			area.setCaretColor(new Color(0, 0, 0, 0));

			if (PaintTool.owner.bit > 1) {
				area.fldData.textSize = PCARD.pc.textSize;
				area.setFont(new Font(PCARD.pc.textFont, PCARD.pc.textStyle, area.fldData.textSize));
				Rectangle rect = area.getBounds();
				rect.x = (int) (rect.x / PaintTool.owner.bit + PaintTool.owner.bitLeft);
				rect.y = (int) (rect.y / PaintTool.owner.bit + PaintTool.owner.bitTop);
				area.setBounds(rect);
			}
			area.fldData.width += 2;
			area.fldData.style = 1;
			area.fldData.useMyDraw = true;
			area.fldData.color = PaintTool.owner.fore.color;
			area.paint(redog);

			Graphics2D g = PaintTool.owner.getSurface().createGraphics();
			// 透明度
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, PaintTool.alpha / 100.0F));
			// サーフェースに反映
			g.drawImage(PaintTool.owner.redoBuf, typeRect.x, typeRect.y, null);

		}
		if (area != null) {
			PaintTool.owner.mainPane.remove(area);
			PaintTool.owner.mainPane.repaint();
			area = null;
		}
	}

	class TypeToolListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) {
			FontMetrics fo = TypeTool.this.area.getFontMetrics(TypeTool.this.area.getFont());
			int width = fo.stringWidth(TypeTool.this.area.getText());
			Rectangle r = TypeTool.this.area.getBounds();
			r.width = width;
			r.height = TypeTool.this.area.getLineCount() * TypeTool.this.area.getLineHeight() + 5;
			area.setBounds(r);
			area.fldData.width = r.width + 1;
			area.fldData.height = r.height;
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}
	}
}