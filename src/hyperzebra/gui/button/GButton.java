package hyperzebra.gui.button;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

import hyperzebra.TTalk;
import hyperzebra.gui.GUI;
import hyperzebra.gui.PCARD;
import hyperzebra.object.OBackground;
import hyperzebra.object.OCardBase;

public class GButton implements ActionListener, MouseListener, MouseMotionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		// String cmd = e.getActionCommand();
		// MyButton btn = (MyButton)e.getSource();
		// OCardBase parent = (OCardBase)btn.btnData.parent;
		if (TTalk.idle == true/*
								 * &&parent==PCARD.pc.stack.curCard||parent.objectType.equals("background")&&
								 * ((OBackground)parent).viewCard==PCARD.pc.stack.curCard
								 */) {
			TTalk.CallMessage("mouseUp", ((MyButton) e.getSource()).btnData);
		} else {
			GUI.clickH = ((JComponent) e.getSource()).getX() + 1;
			GUI.clickV = ((JComponent) e.getSource()).getY() + 1;
			GUI.mouseClicked();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//
		if (TTalk.idle == true) {
			if (((MyButton) e.getSource()).autoHilite == false && ((MyButton) e.getSource()).btnData.enabled == true) {
				if (e.getClickCount() == 2)
					TTalk.CallMessage("mouseDoubleClick", ((MyButton) e.getSource()).btnData);
				TTalk.CallMessage("mouseUp", ((MyButton) e.getSource()).btnData);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		MyButton btn = (MyButton) e.getSource();
		OCardBase parent = (OCardBase) btn.btnData.parent;
		if (parent == PCARD.pc.stack.curCard || parent.objectType.equals("background")
				&& ((OBackground) parent).viewCard == PCARD.pc.stack.curCard) {
			if (TTalk.idle == true && ((MyButton) e.getSource()).btnData.enabled == true) {
				TTalk.CallMessage("mouseDown", ((MyButton) e.getSource()).btnData);
			}
		}
		GUI.mouseDowned();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		GUI.mouseUped();
		GUI.mouseClicked();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (TTalk.idle == true && ((MyButton) e.getSource()).btnData.enabled == true) {
			TTalk.CallMessage("mouseEnter", ((MyButton) e.getSource()).btnData);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (TTalk.idle == true && ((MyButton) e.getSource()).btnData.enabled == true) {
			TTalk.CallMessage("mouseLeave", ((MyButton) e.getSource()).btnData);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		TTalk.CallMessage("mouseStillDown", ((MyButton) e.getSource()).btnData);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		TTalk.CallMessage("mouseWithin", ((MyButton) e.getSource()).btnData);
	}
}
