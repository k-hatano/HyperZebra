package hyperzebra.tool;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import hyperzebra.gui.GUI;
import hyperzebra.gui.MyPanel;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.gui.dialog.PaintBlendDialog;
import hyperzebra.gui.menu.GMenuPaint;

//-------------------
//選択
//-------------------
public class SelectTool implements toolSelectInterface {
	// srcRect:選択した範囲
	// moveRect:現在の浮き出しの位置
	// redoBuf:選択した範囲の浮き出し(0,0,width,height)
	// move:trueなら浮き出しあり

	public Rectangle srcRect;
	public Rectangle moveRect;
	public boolean move = false;
	public boolean shift;
	public int shiftx, shifty;

	@Override
	public String getName() {
		return "Select";
	}

	private void strokeDraw() {
		Rectangle rect = moveRect;
		if (rect == null)
			rect = srcRect;
		if (rect == null)
			return;
		strokeDraw(rect);
	}

	private void strokeDraw(Rectangle rect) {
		rect = (Rectangle) rect.clone();

		if (PaintTool.owner.bit > 1) {
			rect.x = (rect.x - (int) PaintTool.owner.bitLeft) * PaintTool.owner.bit;
			rect.y = (rect.y - (int) PaintTool.owner.bitTop) * PaintTool.owner.bit;
			rect.width *= PaintTool.owner.bit;
			rect.height *= PaintTool.owner.bit;
		}

		rect.width--;
		rect.height--;

		Graphics2D g3 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();
		g3.setColor(Color.WHITE);
		g3.draw(rect);
		float dash[] = { 4.0f, 2.0f };
		int i = (((int) System.currentTimeMillis() / 100) & 0xFFFF) % 6;
		g3.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, i));
		g3.setColor(Color.BLACK);
		g3.draw(rect);
	}

	private Rectangle setSelection(int x, int y) {
		if (srcRect == null)
			return null;

		// Graphics2D g3 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();
		PaintTool.owner.mainPane.paintImmediately(
				new Rectangle(0, 0, PaintTool.owner.mainPane.getWidth(), PaintTool.owner.mainPane.getHeight()));

		Rectangle r = (Rectangle) srcRect.clone();
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

		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
		}

		return r;
	}

	public void viewSelection() {
		// 一時バッファの部分を描画
		// Graphics2D g4 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();
		// g4.drawImage(PaintTool.owner.redoBuf, moveRect.x, moveRect.y,
		// moveRect.x+moveRect.width, moveRect.y+moveRect.height,
		// 0, 0, moveRect.width, moveRect.height, PaintTool.owner.mainPane);
		// MyPanel.mainPaneDraw(g4, PaintTool.owner.redoBuf, moveRect.x, moveRect.y,
		// moveRect.width, moveRect.height);
		PaintTool.owner.mainPane.repaint();

		// 選択領域の破線を表示
		strokeDraw();
	}

	@Override
	public void mouseUp(int x, int y) {
		if (move == false && srcRect != null) {
			GMenuPaint.setUndo();

			srcRect = setSelection(x, y);

			moveRect = (Rectangle) srcRect.clone();
			move = true;

			// 一時バッファに選択領域を移動
			Graphics2D g = (Graphics2D) PaintTool.owner.redoBuf.getGraphics();
			g.setColor(new Color(255, 255, 255));
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			g.fillRect(0, 0, PaintTool.owner.redoBuf.getWidth(), PaintTool.owner.redoBuf.getHeight());
			g.setComposite(AlphaComposite.Src);
			g.drawImage(PaintTool.owner.getSurface(), 0, 0, srcRect.width, srcRect.height, srcRect.x, srcRect.y,
					srcRect.x + srcRect.width, srcRect.y + srcRect.height, PaintTool.owner.mainPane);

			// 移動した部分を透明にする
			Graphics2D g2 = (Graphics2D) PaintTool.owner.getSurface().getGraphics();
			g2.setColor(new Color(255, 255, 255));
			if (!PaintTool.editBackground) {
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			}
			g2.fillRect(srcRect.x, srcRect.y, srcRect.width, srcRect.height);

			// 表画面に反映
			/*
			 * Graphics2D g3 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();
			 * g3.setColor(new Color(255,255,255)); g3.fillRect(0, 0,
			 * PaintTool.owner.mainPane.getWidth(), PaintTool.owner.mainPane.getHeight());
			 * g3.drawImage(PaintTool.owner.bgImg, 0, 0, PaintTool.owner.mainPane);
			 * if(!PaintTool.editBackground){ g3.drawImage(PaintTool.owner.mainImg, 0, 0,
			 * PaintTool.owner.mainPane); }
			 */
			if (PaintTool.owner.bit > 1) {
				PaintTool.owner.mainPane.repaint();
			} else {
				PaintTool.owner.mainPane.repaint(moveRect);
			}

			viewSelection();
		} else {
			mouseStillDown(x, y);
		}
	}

	@Override
	public void mouseDown(int x, int y) {
		if (move == true && moveRect.contains(new Point(x, y))) {
			// 選択範囲の移動開始
			shift = (GUI.key[11] > 0);
			shiftx = x;
			shifty = y;

			if (GUI.key[12] > 0) {
				Graphics2D g = (Graphics2D) PaintTool.owner.getSurface().getGraphics();
				g.drawImage(PaintTool.owner.redoBuf, moveRect.x, moveRect.y, moveRect.x + moveRect.width,
						moveRect.y + moveRect.height, 0, 0, srcRect.width, srcRect.height, PaintTool.owner.mainPane);
			}
		} else {
			// 浮き出し領域を実際の領域に描画
			GMenuPaint.setUndo();
			end();

			// 新しい選択領域を作る
			srcRect = new Rectangle(x, y, 0, 0);

			moveRect = null;
			move = false;
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
		if (move == true && moveRect.contains(new Point(x, y))) {
			PaintTool.owner.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else {
			PaintTool.owner.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}

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

			moveRect.x += x - PaintTool.lastx[0];
			moveRect.y += y - PaintTool.lasty[0];

			// 表画面に反映
			/*
			 * Graphics2D g3 = (Graphics2D) PaintTool.owner.mainPane.getGraphics();
			 * g3.drawImage(PaintTool.owner.bgImg, 0, 0, PaintTool.owner.mainPane);
			 * if(!PaintTool.editBackground){ g3.drawImage(PaintTool.owner.mainImg, 0, 0,
			 * PaintTool.owner.mainPane); }
			 */
			if (PaintTool.owner.bit > 1) {
				PaintTool.owner.mainPane.repaint((moveRect.x - (int) PaintTool.owner.bitLeft) * PaintTool.owner.bit,
						(moveRect.y - (int) PaintTool.owner.bitTop) * PaintTool.owner.bit,
						(moveRect.width) * PaintTool.owner.bit, (moveRect.height) * PaintTool.owner.bit);
			} else {
				PaintTool.owner.mainPane.repaint(moveRect);
			}

			viewSelection();

			// ポインタがウィンドウ外へ出たら
			/*
			 * PointerInfo pointerInfo = MouseInfo.getPointerInfo(); Point p =
			 * pointerInfo.getLocation(); Rectangle r = new
			 * Rectangle(PaintTool.owner.getLocationOnScreen().x,
			 * PaintTool.owner.getLocationOnScreen().y, PaintTool.owner.getWidth(),
			 * PaintTool.owner.getHeight()); if(!r.contains(p) && move==true ){
			 * //ファイルへのドラッグアンドドロップ //ドラッグ元を作成する DragSource dragSource = new DragSource();
			 * DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(
			 * PaintTool.owner.mainPane, DnDConstants.ACTION_COPY_OR_MOVE, dselection); }
			 */
		} else {
			if (srcRect != null && x == srcRect.x + srcRect.width && y == srcRect.y + srcRect.height) {
				return false;
			}
			setSelection(x, y);
		}
		return true;
	}

	@Override
	public void clear() {
		move = false;
		moveRect = null;
		srcRect = null;
	}

	@Override
	public void end() {
		if (move) {
			Graphics2D g = (Graphics2D) PaintTool.owner.getSurface().getGraphics();
			BufferedImage newimg = MyPanel.makeBlendImage(PaintTool.owner.redoBuf);
			g.drawImage(newimg, moveRect.x, moveRect.y, moveRect.x + moveRect.width, moveRect.y + moveRect.height, 0, 0,
					srcRect.width, srcRect.height, null);

			// ペーストした場合はバッファの大きさが変わっているので新しくする
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
		if (move && srcRect.width > 0) {
			return owner.redoBuf;
		}
		return null;
	}

	@Override
	public Rectangle getSelectedRect() {
		return srcRect;
	}

	@Override
	public Rectangle getMoveRect() {
		Rectangle rect = (Rectangle) moveRect.clone();
		rect.x += srcRect.x;
		rect.y += srcRect.y;
		return rect;
	}

	@Override
	public boolean isMove() {
		return move;
	}
}

/*
 * class DragSelection implements Transferable, DragGestureListener {
 * 
 * @Override public void dragGestureRecognized(DragGestureEvent e) { //
 * Copy/Moveのアクションならドラッグを開始する
 * if((e.getDragAction()|DnDConstants.ACTION_COPY_OR_MOVE)!= 0) { try{
 * e.startDrag(DragSource.DefaultCopyDrop, this, null); }catch(Exception e1){
 * e1.printStackTrace(); } } }
 * 
 * @Override public Object getTransferData(DataFlavor e) { ArrayList<File>
 * filelist = new ArrayList<File>(); File file = new File("tmp.png"); try {
 * if(!ImageIO.write(PaintTool.owner.redoBuf, "PNG", file)){
 * System.out.println("Image output error"); } else{ filelist.add(file); } }
 * catch (IOException e1) { e1.printStackTrace(); } return filelist; }
 * 
 * @Override public DataFlavor[] getTransferDataFlavors() { return new
 * DataFlavor[] {DataFlavor.javaFileListFlavor}; }
 * 
 * @Override public boolean isDataFlavorSupported(DataFlavor flavor) { return
 * flavor.equals(DataFlavor.javaFileListFlavor); } }
 */
