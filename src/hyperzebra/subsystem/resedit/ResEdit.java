package hyperzebra.subsystem.resedit;

import hyperzebra.gui.PCARDFrame;
import hyperzebra.object.OObject;
import hyperzebra.object.OStack;

public class ResEdit {
	public static ResEdit nulleditor;
	public ResTypeEditor child;

	public ResEdit(PCARDFrame pc, String type, OObject object) {
		if (pc == null) {
			pc = new PCARDFrame();
			pc.stack = new OStack(pc);
		}

		// 各Typeごとに開いて選択する
		if (type.equals("icon") || type.equals("cicn") || type.equals("picture") || type.equals("cursor")) {
			child = new IconTypeEditor(pc, type, object);
		} else {
			child = new OtherTypeEditor(pc, type, object);
		}
	}
}
