package hyperzebra.gui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import hyperzebra.object.OField;

public class GUI_fldDocument implements DocumentListener {
	OField fld;

	public GUI_fldDocument(OField fld) {
		this.fld = fld;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		fld.setTextByInputarea(fld.getMyTextArea().getText());
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		changedUpdate(e);

	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		changedUpdate(e);

	}
}