package hyperzebra.gui.button;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import hyperzebra.TTalk;
import hyperzebra.gui.GUI;

public class GCheckBox implements ActionListener, MouseListener {
	public void actionPerformed(ActionEvent e) {
		// String cmd = e.getActionCommand();
		if (TTalk.idle == true) {
			TTalk.CallMessage("mouseUp", ((MyCheck) e.getSource()).btnData);
			((MyCheck) e.getSource()).btnData.check_hilite = ((MyCheck) e.getSource()).isSelected();
			((MyCheck) e.getSource()).btnData.setHilite(((MyCheck) e.getSource()).isSelected());
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
			TTalk.CallMessage("mouseDown", ((MyCheck) e.getSource()).btnData);
			GUI.mouseDowned();
		} else
			GUI.mouseDowned();
	}

	public void mouseReleased(MouseEvent e) {
		GUI.mouseUped();
	}

	public void mouseEntered(MouseEvent e) {
		if (TTalk.idle == true) {
			TTalk.CallMessage("mouseEnter", ((MyCheck) e.getSource()).btnData);
		}
	}

	public void mouseExited(MouseEvent e) {
		if (TTalk.idle == true) {
			TTalk.CallMessage("mouseLeave", ((MyCheck) e.getSource()).btnData);
		}
	}
}
