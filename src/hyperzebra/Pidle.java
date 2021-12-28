package hyperzebra;

import hyperzebra.gui.GUI;
import hyperzebra.gui.PCARD;
import hyperzebra.gui.PCARDFrame;

public class Pidle extends Thread {
	@Override
	public void run() {
		this.setName("idle thread");
		while (true) {
			if (PCARDFrame.pc.stack != null && PCARDFrame.pc.stack.curCard != null && PCARDFrame.pc.tool == null) {
				if (TTalk.idle && PCARD.pc.isActive()) {
					if (System.currentTimeMillis() - TTalk.lastErrorTime > 10 * 1000) {
						TTalk.CallMessage("idle", "", PCARDFrame.pc.stack.curCard, true, false);
					}
				}
				GUI.keyEventCheck();
				// PCARD.pc.stack.pcard.requestFocusInWindow(); //これするとフィールドに文字打てなくなる
			}
			try {
				sleep(100);// 1000msecに10回
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			if (Thread.currentThread().isInterrupted()) {
				break;
			}
		}
	}
}