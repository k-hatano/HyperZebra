package hyperzebra.tool;

import hyperzebra.gui.ButtonGUI;
import hyperzebra.gui.PCARDFrame;

public class ButtonTool implements authtoolInterface {

	@Override
	public void end() {
		ButtonGUI.gui.removeListenerFromParts();
		PCARDFrame.pc.mainPane.removeMouseListener(ButtonGUI.gui);
		PCARDFrame.pc.mainPane.removeMouseMotionListener(ButtonGUI.gui);
		ButtonGUI.gui.target = null;

		PCARDFrame.pc.mainPane.repaint();
	}
}
