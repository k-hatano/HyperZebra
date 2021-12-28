package hyperzebra.object;

import hyperzebra.gui.PCARD;

public class OPicture extends OObject {
	OCardBase parent;

	// メイン
	public OPicture(OCardBase prt) {
		objectType = "picture";
		parent = prt;
	}

	@Override
	public boolean getVisible() {
		return parent.showPict;
	}

	@Override
	public void setVisible(boolean in) {
		parent.showPict = in;
		if (PCARD.lockedScreen == false) {
			parent.stack.pcard.repaint();
		}
	}
}
