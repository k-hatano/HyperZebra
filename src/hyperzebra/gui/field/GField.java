package hyperzebra.gui.field;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import hyperzebra.TTalk;
import hyperzebra.gui.GUI;
import hyperzebra.gui.PCARD;
import hyperzebra.object.OField;

public class GField implements MouseListener, MouseMotionListener {
	@Override
	public void mouseClicked(MouseEvent e) {
		OField fld = ((MyTextArea) e.getSource()).fldData;
		if (TTalk.idle == true) {
			TTalk.CallMessage("mouseUp", fld);
			if (e.getClickCount() == 2)
				TTalk.CallMessage("mouseDoubleClick", fld);
		} else {
			GUI.clickH = e.getX();
			GUI.clickV = e.getY();
			GUI.mouseClicked();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (TTalk.idle == true) {
			OField fld = ((MyTextArea) e.getSource()).fldData;
			if (fld.enabled) {
				if (fld.textHeight < fld.textSize) {
					fld.textHeight = fld.textSize;
				}
				int line = (e.getY() + fld.scroll + fld.textHeight - 1) / fld.textHeight;
				int cnt = 0;
				int i;
				for (i = 0; i < fld.getText().length(); i++) {
					if (fld.getText().charAt(i) == '\n' || i == fld.getText().length() - 1) {
						cnt++;
						if (cnt == line) {
							if (fld.autoSelect && fld.dontWrap) {
								fld.fld.setSelectionStart(i);
								fld.selectedLine = line;
								fld.selectedStart = i + 1;
								PCARD.pc.mainPane.paintImmediately(fld.left, fld.top, fld.width, fld.height);
							}
							GUI.clickLine = "line " + (line) + " of " + fld.getShortName();
							GUI.clickField = fld;
							// System.out.println("clickline:"+GUI.clickLine);

						}
						if (cnt == line + 1) {
							break;
						}
					}
				}
				if (fld.autoSelect && fld.dontWrap) {
					if (fld.selectedLine == line) {
						fld.selectedEnd = i;
						fld.fld.setSelectionEnd(i);
					}
				}
			}
			TTalk.CallMessage("mouseDown", fld);
			GUI.mouseDowned();
		} else
			GUI.mouseDowned();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		GUI.mouseUped();
		GUI.mouseClicked();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (TTalk.idle == true) {
			TTalk.CallMessage("mouseEnter", ((MyTextArea) e.getSource()).fldData);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (TTalk.idle == true) {
			TTalk.CallMessage("mouseLeave", ((MyTextArea) e.getSource()).fldData);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		TTalk.CallMessage("mouseStillDown", ((MyTextArea) e.getSource()).fldData);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		TTalk.CallMessage("mouseWithin", ((MyTextArea) e.getSource()).fldData);
	}
}
