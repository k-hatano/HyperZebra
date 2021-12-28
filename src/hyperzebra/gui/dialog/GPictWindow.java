package hyperzebra.gui.dialog;

import java.awt.image.BufferedImage;

import javax.swing.JDialog;

import hyperzebra.gui.PCARD;
import hyperzebra.gui.field.MyLabel3;

public class GPictWindow extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public MyLabel3 label;

	public GPictWindow(PCARD owner, String name, BufferedImage bi, String windowType, boolean visible) {
		super(owner);
		this.name = name;

		// パネルを追加する
		if (bi != null) {
			label = new MyLabel3(name, bi);
			getContentPane().add(label);

		}

		if (windowType.equalsIgnoreCase("rect") || windowType.equalsIgnoreCase("shadow")) {
			setUndecorated(true);
			setBounds(owner.getX() + owner.getWidth() / 2 - bi.getWidth() / 2,
					owner.getY() + owner.getHeight() / 2 - bi.getHeight() / 2, bi.getWidth(),
					bi.getHeight()/* +owner.getInsets().top */);
			setResizable(false);
		} else {
			setBounds(owner.getX() + owner.getWidth() / 2 - bi.getWidth() / 2,
					owner.getY() + owner.getHeight() / 2 - bi.getHeight() / 2, bi.getWidth(),
					bi.getHeight() + owner.getInsets().top);
			// setResizable(false);
		}
		setVisible(visible);
	}
}
