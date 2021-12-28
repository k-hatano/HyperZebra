package hyperzebra.gui.button;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import hyperzebra.TTalk;
import hyperzebra.gui.GUI;

public class GRadio implements ActionListener, MouseListener {
	public void actionPerformed(ActionEvent e) {
		// String cmd = e.getActionCommand();
		if (TTalk.idle == true) {
			TTalk.CallMessage("mouseUp", ((MyRadio) e.getSource()).btnData);
			((MyRadio) e.getSource()).btnData.check_hilite = ((MyRadio) e.getSource()).isSelected();
			((MyRadio) e.getSource()).btnData.setHilite(((MyRadio) e.getSource()).isSelected());
		} else {
			GUI.clickH = ((JComponent) e.getSource()).getX() + 1;
			GUI.clickV = ((JComponent) e.getSource()).getY() + 1;
			GUI.mouseClicked();
		}
	}

	public void mouseClicked(MouseEvent e) {
		//
	}

	public void mousePressed(MouseEvent e) {
		if (TTalk.idle == true) {
			TTalk.CallMessage("mouseDown", ((MyRadio) e.getSource()).btnData);
			GUI.mouseDowned();
		} else
			GUI.mouseDowned();
	}

	public void mouseReleased(MouseEvent e) {
		GUI.mouseUped();
	}

	public void mouseEntered(MouseEvent e) {
		if (TTalk.idle == true) {
			TTalk.CallMessage("mouseEnter", ((MyRadio) e.getSource()).btnData);
		}
	}

	public void mouseExited(MouseEvent e) {
		if (TTalk.idle == true) {
			TTalk.CallMessage("mouseLeave", ((MyRadio) e.getSource()).btnData);
		}
	}
}