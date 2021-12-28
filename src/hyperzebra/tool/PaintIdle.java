package hyperzebra.tool;

import java.awt.MouseInfo;
import java.awt.PointerInfo;

import hyperzebra.gui.GUI;

public class PaintIdle extends Thread {
	@Override
	public void run() {
		this.setName("Paint idle");

		while (true) {
			if (PaintTool.owner == null || PaintTool.owner.tool == null || PaintTool.owner.isVisible() == false) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// Thread.currentThread().interrupt();
				}
				continue;
			}

			if (PaintTool.lastTime + 100 <= System.currentTimeMillis() && PaintTool.owner.bit == 1) {
				// 0.1秒以上待ったら
				if (PaintTool.mouse) {
					PaintTool.mouseStillDown((int) PaintTool.lastx[0], (int) PaintTool.lasty[0]);
				} else {
					PaintTool.mouseWithin((int) PaintTool.lastx[0], (int) PaintTool.lasty[0]);
				}
				// PaintTool.lastTime = 0;
			} else if (!PaintTool.mouse) {
				PointerInfo pointerInfo = MouseInfo.getPointerInfo();
				PaintTool.mouseWithin(
						pointerInfo.getLocation().x - PaintTool.owner.mainPane.getX()
								- PaintTool.owner.getLocationOnScreen().x,
						pointerInfo.getLocation().y - PaintTool.owner.mainPane.getY()
								- PaintTool.owner.getLocationOnScreen().y - PaintTool.owner.getInsets().top);
			}
			GUI.keyEventCheck();

			try {
				sleep(100);// 1000msecに10回
			} catch (InterruptedException e) {
				// Thread.currentThread().interrupt();
			}
			/*
			 * if (Thread.currentThread().isInterrupted()) { break; }
			 */
		}
	}
}