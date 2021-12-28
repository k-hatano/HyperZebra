package hyperzebra.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import hyperzebra.tool.AuthTool;
import hyperzebra.tool.BrushTool;
import hyperzebra.tool.ButtonTool;
import hyperzebra.tool.EraserTool;
import hyperzebra.tool.FieldTool;
import hyperzebra.tool.LineTool;
import hyperzebra.tool.OvalTool;
import hyperzebra.tool.PaintBucketTool;
import hyperzebra.tool.PaintTool;
import hyperzebra.tool.PencilTool;
import hyperzebra.tool.RectTool;
import hyperzebra.tool.SelectTool;
import hyperzebra.tool.SmartSelectTool;
import hyperzebra.tool.TypeTool;

public class TBCursor {
	static public void changeCursor(PCARDFrame frame) {
		if (frame.tool == null) {
			if (AuthTool.tool == null) {
				frame.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			} else if (AuthTool.tool.getClass() == ButtonTool.class) {
				frame.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			} else if (AuthTool.tool.getClass() == FieldTool.class) {
				frame.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		} else if (frame.tool.getClass() == BrushTool.class) {
			BufferedImage bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
			int i = PaintTool.brushSize;
			Graphics2D g2 = (Graphics2D) bi.getGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(new Color(0, 0, 0));
			g2.fillOval(16 - i / 2, 16 - i / 2, i, i);

			Point hotSpot = new Point(16, 16);
			String name = "brush-cursor";
			Toolkit kit = Toolkit.getDefaultToolkit();
			Cursor cr = kit.createCustomCursor(bi, hotSpot, name);
			frame.mainPane.setCursor(cr);
			if (PaintTool.owner != null && PaintTool.owner.tool != null) {
				((BrushTool) PaintTool.owner.tool).cursor = cr;
			}
		} else if (frame.tool.getClass() == PaintBucketTool.class) {
			frame.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else if (frame.tool.getClass() == SelectTool.class) {
			frame.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		} else if (frame.tool.getClass() == TypeTool.class) {
			frame.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		} else if (frame.tool.getClass() == PencilTool.class) {
			frame.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else if (frame.tool.getClass() == LineTool.class) {
			frame.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else if (frame.tool.getClass() == SmartSelectTool.class) {
			frame.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else if (frame.tool.getClass() == EraserTool.class) {
			BufferedImage bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
			int i = 16;
			Graphics2D g2 = (Graphics2D) bi.getGraphics();
			g2.setColor(Color.WHITE);
			g2.fillRect(16 - i / 2, 16 - i / 2, i, i);
			g2.setColor(Color.BLACK);
			g2.drawRect(16 - i / 2, 16 - i / 2, i, i);

			Point hotSpot = new Point(16, 16);
			String name = "eraser-cursor";
			Toolkit kit = Toolkit.getDefaultToolkit();
			Cursor cr = kit.createCustomCursor(bi, hotSpot, name);
			frame.mainPane.setCursor(cr);
			if (PaintTool.owner != null && PaintTool.owner.tool != null) {
				((EraserTool) PaintTool.owner.tool).cursor = cr;
			}
		} else if (frame.tool.getClass() == RectTool.class) {
			frame.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		} else if (frame.tool.getClass() == LineTool.class) {
			frame.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		} else if (frame.tool.getClass() == OvalTool.class) {
			frame.mainPane.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
	}
}
