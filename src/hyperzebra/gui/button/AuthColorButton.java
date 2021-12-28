package hyperzebra.gui.button;

import java.awt.Color;

import hyperzebra.object.OButton;
import hyperzebra.object.OCard;
import hyperzebra.object.OField;
import hyperzebra.object.OObject;

public class AuthColorButton extends CPButton {
	private static final long serialVersionUID = 3564756897317769905L;

	int type;
	OObject obj;

	public AuthColorButton(Color in_color, OObject obj, int type) {
		super(in_color, 0, 0, false);
		this.obj = obj;
		this.type = type;
	}

	@Override
	public void makeIcon(Color col) {
		super.makeIcon(col);

		if (obj.getClass() == OButton.class) {
			if (type == 0)
				((OButton) obj).setColor(col);
			if (type == 1)
				((OButton) obj).setBgColor(col);
		} else if (obj.getClass() == OField.class) {
			if (type == 0)
				((OField) obj).setColor(col);
			if (type == 1)
				((OField) obj).setBgColor(col);
		}
		OCard.reloadCurrentCard();
	}
}
