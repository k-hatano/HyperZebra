package hyperzebra.tool;

import hyperzebra.gui.FieldGUI;
import hyperzebra.gui.PCARDFrame;

public class FieldTool implements authtoolInterface {

	@Override
	public void end() {
		FieldGUI.gui.removeListenerFromParts();
		PCARDFrame.pc.mainPane.removeMouseListener(FieldGUI.gui);
		PCARDFrame.pc.mainPane.removeMouseMotionListener(FieldGUI.gui);
		FieldGUI.gui.target = null;

		PCARDFrame.pc.mainPane.repaint();
	}
}
