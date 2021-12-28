package hyperzebra.tool;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import hyperzebra.gui.GUI;
import hyperzebra.gui.PCARD;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.gui.paintGUI;
import hyperzebra.subsystem.iconeditor.IconEditor;

/*
import cello.jtablet.TabletManager;
import cello.jtablet.event.TabletEvent;
import cello.jtablet.event.TabletListener;
import cello.jtablet.installer.JTabletExtension;
*/

public class PaintTool {
	// static toolInterface tool = null;
	static boolean mouse;
	public static float lastx[] = new float[2];
	public static float lasty[] = new float[2];
	public static long lastTime;
	static boolean canTablet = false;
	// static MyTabletListener tabletListener;
	public static PCARDFrame owner;

	// ペイントプロパティ
	public static int brushSize = 3; // TODO staticでないようにする
	public static float lineSize = 1;
	public static int alpha = 100;
	public static boolean antialias = false;
	public static boolean editBackground = false;
	public static BufferedImage pat;
	public boolean fill;
	public static int smartSelectPercent = 5; // 5%
	// static boolean bit = false; //拡大表示
	// static float bitLeft = 0;
	// static float bitTop = 0;

	public static void tabletinit() {
		/*
		 * if (JTabletExtension.checkCompatibility(PaintTool.owner, "1.2.0")) {
		 * canTablet = true; if(tabletListener==null){ tabletListener = new
		 * MyTabletListener(); } }
		 */

		if (!PCARD.pc.paidle.isAlive()) {
			PCARD.pc.paidle = new PaintIdle();
			PCARD.pc.paidle.start();
		}
	}

	public static void mouseUp(int x, int y) {
		if (owner.bit > 1) {
			// 端数切り捨て
			PaintTool.owner.bitLeft = (float) (int) PaintTool.owner.bitLeft;
			PaintTool.owner.bitTop = (float) (int) PaintTool.owner.bitTop;

			if (owner.getClass() == IconEditor.class) {
				// スクロールバーを使う
				((IconEditor) owner).scrollpane.getHorizontalScrollBar()
						.setValue(((IconEditor) owner).scrollpane.getHorizontalScrollBar().getValue()
								+ (int) PaintTool.owner.bitLeft * PaintTool.owner.bit);
				((IconEditor) owner).scrollpane.getVerticalScrollBar()
						.setValue(((IconEditor) owner).scrollpane.getVerticalScrollBar().getValue()
								+ (int) PaintTool.owner.bitTop * PaintTool.owner.bit);
				PaintTool.owner.bitLeft = 0;
				PaintTool.owner.bitTop = 0;
			}

			if (owner.tool.getClass() == EraserTool.class || owner.tool.getClass() == SelectTool.class) {
				x = (int) (((float) x + owner.bit / 2) / owner.bit) + (int) owner.bitLeft;
				y = (int) (((float) y + owner.bit / 2) / owner.bit) + (int) owner.bitTop;
			} else {
				x = x / owner.bit + (int) owner.bitLeft;
				y = y / owner.bit + (int) owner.bitTop;
			}

		}
		if (GUI.key[' '] > 0) { // space
			return;
		}
		if (owner.tool != null) {
			owner.tool.mouseUp(x, y);
			lastx[1] = lastx[0];
			lasty[1] = lasty[0];
			lastx[0] = x;
			lasty[0] = y;
			mouse = false;
		}
	}

	public static void mouseDown(int x, int y) {
		/*
		 * if(GUI.key[12]>0 && owner.bit>1 && owner.tool!=null&&owner.tool.getClass() ==
		 * PencilTool.class) { lastx[0] = x; lasty[0] = y; return; }
		 */
		if (GUI.key[' '] > 0) { // space
			lastx[0] = x;
			lasty[0] = y;
			return;
		}
		if (owner.bit > 1) {
			if (owner.tool.getClass() == EraserTool.class || owner.tool.getClass() == SelectTool.class) {
				x = (int) (((float) x + owner.bit / 2) / owner.bit) + (int) owner.bitLeft;
				y = (int) (((float) y + owner.bit / 2) / owner.bit) + (int) owner.bitTop;
			} else {
				x = x / owner.bit + (int) owner.bitLeft;
				y = y / owner.bit + (int) owner.bitTop;
			}
		}
		if (owner.tool != null) {
			owner.tool.mouseDown(x, y);
			lastx[1] = lastx[0];
			lasty[1] = lasty[0];
			lastx[0] = x;
			lasty[0] = y;
			mouse = true;
		}
	}

	public static void mouseWithin(int x, int y) {
		/*
		 * if(GUI.key[12]>0 && owner.bit>1 && owner.tool!=null&&owner.tool.getClass() ==
		 * PencilTool.class) { return; }
		 */
		if (GUI.key[' '] > 0) { // space
			if (GUI.key[' '] > 0) {
				PaintTool.owner.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			return;
		}
		if (owner.bit > 1) {
			x = x / owner.bit + (int) owner.bitLeft;
			y = y / owner.bit + (int) owner.bitTop;
		}
		if (owner.tool != null && owner.tool.mouseWithin(x, y)) {
			lastx[1] = lastx[0];
			lasty[1] = lasty[0];
			lastx[0] = x;
			lasty[0] = y;
		}
	}

	public static void mouseStillDown(int x, int y) {
		// if(GUI.key[12]>0 && owner.bit>1 && owner.tool!=null&&owner.tool.getClass() ==
		// PencilTool.class) {
		if (GUI.key[' '] > 0 && owner.bit > 1 && owner.tool != null) {
			PaintTool.owner.bitLeft += (PaintTool.lastx[0] - x) / PaintTool.owner.bit;
			PaintTool.owner.bitTop += (PaintTool.lasty[0] - y) / PaintTool.owner.bit;
			lastx[0] = x;
			lasty[0] = y;

			if (owner.getClass() == IconEditor.class) {
				// スクロールバーを使う
				if (PaintTool.owner.bitLeft
						+ ((IconEditor) owner).scrollpane.getHorizontalScrollBar().getValue() / owner.bit < 0) {
					PaintTool.owner.bitLeft = -((IconEditor) owner).scrollpane.getHorizontalScrollBar().getValue()
							/ owner.bit;
				}
				if (PaintTool.owner.bitTop
						+ ((IconEditor) owner).scrollpane.getVerticalScrollBar().getValue() / owner.bit < 0) {
					PaintTool.owner.bitTop = -((IconEditor) owner).scrollpane.getVerticalScrollBar().getValue()
							/ owner.bit;
				}
				if (PaintTool.owner.bitLeft
						+ PaintTool.owner.redoBuf.getWidth() / PaintTool.owner.bit > PaintTool.owner.redoBuf.getWidth()
								- ((IconEditor) owner).scrollpane.getHorizontalScrollBar().getValue() / owner.bit) {
					PaintTool.owner.bitLeft = PaintTool.owner.redoBuf.getWidth() * (PaintTool.owner.bit - 1)
							/ PaintTool.owner.bit
							- ((IconEditor) owner).scrollpane.getHorizontalScrollBar().getValue() / owner.bit;
				}
				if (PaintTool.owner.bitTop + PaintTool.owner.redoBuf.getHeight()
						/ PaintTool.owner.bit > PaintTool.owner.redoBuf.getHeight()
								- ((IconEditor) owner).scrollpane.getVerticalScrollBar().getValue() / owner.bit) {
					PaintTool.owner.bitTop = PaintTool.owner.redoBuf.getHeight() * (PaintTool.owner.bit - 1)
							/ PaintTool.owner.bit
							- ((IconEditor) owner).scrollpane.getVerticalScrollBar().getValue() / owner.bit;
				}
			} else {
				if (PaintTool.owner.bitLeft < 0)
					PaintTool.owner.bitLeft = 0;
				if (PaintTool.owner.bitTop < 0)
					PaintTool.owner.bitTop = 0;
				if (PaintTool.owner.bitLeft + PaintTool.owner.redoBuf.getWidth()
						/ PaintTool.owner.bit > PaintTool.owner.redoBuf.getWidth()) {
					PaintTool.owner.bitLeft = PaintTool.owner.redoBuf.getWidth() * (PaintTool.owner.bit - 1)
							/ PaintTool.owner.bit;
				}
				if (PaintTool.owner.bitTop + PaintTool.owner.redoBuf.getHeight()
						/ PaintTool.owner.bit > PaintTool.owner.redoBuf.getHeight()) {
					PaintTool.owner.bitTop = PaintTool.owner.redoBuf.getHeight() * (PaintTool.owner.bit - 1)
							/ PaintTool.owner.bit;
				}
			}

			PaintTool.owner.mainPane.repaint();
			return;
		}
		if (owner.bit > 1) {
			if (owner.tool.getClass() == EraserTool.class || owner.tool.getClass() == SelectTool.class) {
				x = (int) (((float) x + owner.bit / 2) / owner.bit) + (int) owner.bitLeft;
				y = (int) (((float) y + owner.bit / 2) / owner.bit) + (int) owner.bitTop;
			} else {
				x = x / owner.bit + (int) owner.bitLeft;
				y = y / owner.bit + (int) owner.bitTop;
			}
		}
		if (owner.tool != null && owner.tool.mouseStillDown(x, y)) {
			lastx[1] = lastx[0];
			lasty[1] = lasty[0];
			lastx[0] = x;
			lasty[0] = y;
		}
	}

	static public void setPattern(boolean invert) {
		BufferedImage patorg = PaintTool.owner.pat.patterns[PaintTool.owner.pat.pattern];
		if (patorg == null) {
			// ない場合
			PaintTool.pat = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
			Graphics g1 = PaintTool.pat.getGraphics();
			g1.setColor(PaintTool.owner.fore.color);
			g1.fillRect(0, 0, 8, 8);
			return;
		}
		PaintTool.pat = new BufferedImage(patorg.getWidth(), patorg.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g1 = PaintTool.pat.getGraphics();
		g1.drawImage(patorg, 0, 0, null);
		DataBuffer db = PaintTool.pat.getRaster().getDataBuffer();
		for (int h = 0; h < patorg.getHeight(); h++) {
			for (int w = 0; w < patorg.getWidth(); w++) {
				int c = db.getElem(0, h * patorg.getWidth() + w);
				int r = (c >> 16) & 0xFF;
				int g = (c >> 8) & 0xFF;
				int b = (c >> 0) & 0xFF;
				Color foreColor = PaintTool.owner.fore.color;
				Color backColor = PaintTool.owner.back.color;
				if (invert) {
					foreColor = PaintTool.owner.back.color;
					backColor = PaintTool.owner.fore.color;
				}
				int fr = foreColor.getRed();
				int fg = foreColor.getGreen();
				int fb = foreColor.getBlue();
				int fa = foreColor.getAlpha();
				int br = backColor.getRed();
				int bg = backColor.getGreen();
				int bb = backColor.getBlue();
				int ba = backColor.getAlpha();
				db.setElem(h * patorg.getWidth() + w,
						(((r * ba + (0xFF - r) * fa) / 0xFF) << 24) + (((r * br + (0xFF - r) * fr) / 0xFF) << 16)
								+ (((g * bg + (0xFF - g) * fg) / 0xFF) << 8) + ((b * bb + (0xFF - b) * fb) / 0xFF));
			}
		}
	}

	static public void toCdPict() {
		if (PaintTool.owner != PCARDFrame.pc)
			return;
		if (PCARD.pc.stack.curCard == null || PaintTool.owner.mainImg == null)
			return;

		// Cardピクチャが全部透明かどうかを判定
		DataBuffer db = PaintTool.owner.mainImg.getRaster().getDataBuffer();
		int width = PaintTool.owner.mainImg.getWidth();
		boolean isTransparent = true;
		for (int h = 0; h < PaintTool.owner.mainImg.getHeight(); h++) {
			for (int w = 0; w < width; w++) {
				int c = db.getElem(0, h * width + w);
				if ((c & 0xFF000000) != 0) {
					isTransparent = false;
					break;
				}
			}
		}

		// Cardピクチャ
		if (isTransparent) {
			PaintTool.owner.stack.curCard.pict = null;
			PaintTool.owner.stack.curCard.bitmapName = null;
		} else {
			if (PaintTool.owner.stack.curCard.pict == null) {
				PaintTool.owner.stack.curCard.pict = new BufferedImage(PaintTool.owner.mainImg.getWidth(),
						PaintTool.owner.mainImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
			}
			if (PaintTool.owner.stack.curCard.pict.getWidth() != PaintTool.owner.stack.width
					|| PaintTool.owner.stack.curCard.pict.getHeight() != PaintTool.owner.stack.height) {
				// サイズ変更
				PaintTool.owner.stack.curCard.pict = new BufferedImage(PaintTool.owner.stack.width,
						PaintTool.owner.stack.height, BufferedImage.TYPE_INT_ARGB);
			}
			Graphics2D g = (Graphics2D) PaintTool.owner.stack.curCard.pict.getGraphics();
			g.setColor(new Color(255, 255, 255));
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			g.fillRect(0, 0, PaintTool.owner.stack.width, PaintTool.owner.stack.height);
			g = (Graphics2D) PaintTool.owner.stack.curCard.pict.getGraphics();
			g.drawImage(PaintTool.owner.mainImg, 0, 0, PaintTool.owner);
		}

		// Bgピクチャが全部透明かどうかを判定
		DataBuffer bgdb = PaintTool.owner.bgImg.getRaster().getDataBuffer();
		int bgwidth = PaintTool.owner.bgImg.getWidth();
		boolean isWhite = true;
		for (int h = 0; h < PaintTool.owner.bgImg.getHeight(); h++) {
			for (int w = 0; w < bgwidth; w++) {
				int c = bgdb.getElem(0, h * bgwidth + w);
				if ((c & 0x00FFFFFF) != 0x00FFFFFF) {
					isWhite = false;
					break;
				}
			}
		}

		// Bgピクチャ
		if (isWhite) {
			PaintTool.owner.stack.curCard.bg.pict = null;
			PaintTool.owner.stack.curCard.bg.bitmapName = null;
		} else {
			if (PaintTool.owner.stack.curCard.bg.pict == null) {
				PaintTool.owner.stack.curCard.bg.pict = new BufferedImage(PaintTool.owner.mainImg.getWidth(),
						PaintTool.owner.mainImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
			}
			if (PaintTool.owner.stack.curCard.bg.pict.getWidth() != PaintTool.owner.stack.width
					|| PaintTool.owner.stack.curCard.bg.pict.getHeight() != PaintTool.owner.stack.height) {
				// サイズ変更
				PaintTool.owner.stack.curCard.bg.pict = new BufferedImage(PaintTool.owner.stack.width,
						PaintTool.owner.stack.height, BufferedImage.TYPE_INT_ARGB);
			}
			Graphics2D g2 = (Graphics2D) PaintTool.owner.stack.curCard.bg.pict.getGraphics();
			g2.setColor(new Color(255, 255, 255));
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			g2.fillRect(0, 0, PaintTool.owner.stack.width, PaintTool.owner.stack.height);
			g2 = (Graphics2D) PaintTool.owner.stack.curCard.bg.pict.getGraphics();
			g2.drawImage(PaintTool.owner.bgImg, 0, 0, PaintTool.owner);
		}
	}

	static public void saveCdPictures() {
		// カードピクチャにコピー
		PaintTool.toCdPict();

		// ファイル保存 (card)
		if (PCARDFrame.pc.stack.curCard != null && PCARDFrame.pc.stack.curCard.pict != null) {
			if (PCARDFrame.pc.stack.curCard.bitmapName == null || PCARDFrame.pc.stack.curCard.bitmapName.length() == 0
					|| !PCARDFrame.pc.stack.curCard.bitmapName.matches(".*\\.png")) {
				PCARDFrame.pc.stack.curCard.bitmapName = "BMAP_" + PCARDFrame.pc.stack.curCard.id + ".png";
				PCARDFrame.pc.stack.curCard.changed = true;
			}
			File file = new File(
					PCARDFrame.pc.stack.file.getParent() + File.separatorChar + PCARDFrame.pc.stack.curCard.bitmapName);
			try {
				ImageIO.write(PCARDFrame.pc.stack.curCard.pict, "PNG", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// ファイル保存 (bg)
		if (PCARDFrame.pc.stack.curCard != null && PCARDFrame.pc.stack.curCard.bg != null
				&& PCARDFrame.pc.stack.curCard.bg.pict != null) {
			if (PCARDFrame.pc.stack.curCard.bg.bitmapName == null
					|| PCARDFrame.pc.stack.curCard.bg.bitmapName.length() == 0
					|| !PCARDFrame.pc.stack.curCard.bg.bitmapName.matches(".*\\.png")) {
				PCARDFrame.pc.stack.curCard.bg.bitmapName = "BMAP_" + PCARDFrame.pc.stack.curCard.bg.id + ".png";
				PCARDFrame.pc.stack.curCard.bg.changed = true;
			}
			File file2 = new File(PCARDFrame.pc.stack.file.getParent() + File.separatorChar
					+ PCARDFrame.pc.stack.curCard.bg.bitmapName);
			try {
				ImageIO.write(PCARDFrame.pc.stack.curCard.bg.pict, "PNG", file2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void spoit(int x, int y) {
		DataBuffer db = PaintTool.owner.getSurface().getRaster().getDataBuffer();
		int c = db.getElem(x + y * PaintTool.owner.getSurface().getWidth());
		Color col = new Color((c >> 16) & 0xFF, (c >> 8) & 0xFF, (c) & 0xFF);
		if (paintGUI.right) {
			PaintTool.owner.back.color = col;
			PaintTool.owner.back.makeIcon(col);
		} else {
			PaintTool.owner.fore.color = col;
			PaintTool.owner.fore.makeIcon(col);
		}
	}
}

/*
 * class MyTabletListener implements TabletListener { toolTabletInterface tool;
 * boolean in_stroke = false;
 * 
 * @Override public void cursorDragged(TabletEvent event) {
 * if(event.getPressure() == 0.0) { in_stroke = false; return; } boolean eraser
 * = (event.getDevice().getType() == cello.jtablet.TabletDevice.Type.ERASER);
 * 
 * if(!in_stroke){ tool.penDown(event.getFloatX(), event.getFloatY(),
 * event.getPressure(), eraser); in_stroke = true; }
 * tool.penStillDown(event.getFloatX(), event.getFloatY(), event.getPressure(),
 * eraser); PaintTool.lastx[1] = PaintTool.lastx[0]; PaintTool.lasty[1] =
 * PaintTool.lasty[0]; PaintTool.lastx[0] = event.getFloatX();
 * PaintTool.lasty[0] = event.getFloatY(); }
 * 
 * @Override public void cursorEntered(TabletEvent arg0) { }
 * 
 * @Override public void cursorExited(TabletEvent arg0) { }
 * 
 * @Override public void cursorGestured(TabletEvent arg0) { }
 * 
 * @Override public void cursorMoved(TabletEvent arg0) { }
 * 
 * @Override public void cursorPressed(TabletEvent arg0) { }
 * 
 * @Override public void cursorReleased(TabletEvent event) { if(in_stroke){
 * boolean eraser = (event.getDevice().getType() ==
 * cello.jtablet.TabletDevice.Type.ERASER); tool.penUp(event.getFloatX(),
 * event.getFloatY(), event.getPressure(), eraser); in_stroke = false; } }
 * 
 * @Override public void cursorScrolled(TabletEvent arg0) { }
 * 
 * @Override public void levelChanged(TabletEvent arg0) { } }
 */