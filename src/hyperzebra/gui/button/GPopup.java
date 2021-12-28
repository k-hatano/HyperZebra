package hyperzebra.gui.button;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import hyperzebra.TTalk;
import hyperzebra.gui.GUI;
import hyperzebra.gui.PCARD;

public class GPopup implements ActionListener, PopupMenuListener {
	static MyPopup hidePopup;

	public void actionPerformed(ActionEvent e) {
		// String cmd = e.getActionCommand();
		if (TTalk.idle == true) {
			TTalk.CallMessage("mouseUp", ((MyPopup) e.getSource()).btnData);
		} else {
			GUI.clickH = ((JComponent) e.getSource()).getX() + 1;
			GUI.clickV = ((JComponent) e.getSource()).getY() + 1;
			GUI.mouseClicked();
		}

		((MyPopup) e.getSource()).btnData.setSelectedLine(((MyPopup) e.getSource()).getSelectedIndex() + 1);
	}

	public void popupMenuCanceled(PopupMenuEvent e) {
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		if (TTalk.idle == true && PCARD.editMode == 0) {
			TTalk.CallMessage("mouseDown", ((MyPopup) e.getSource()).btnData);
		} else {
			GUI.mouseClicked();
			hidePopup = ((MyPopup) e.getSource());
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					hidePopup.setPopupVisible(false);// スクリプト実行中にメニューが開いたら取り消す
				}
			});
		}
	}
}