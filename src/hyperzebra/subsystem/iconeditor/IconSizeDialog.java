package hyperzebra.subsystem.iconeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import hyperzebra.gui.PCARD;
import hyperzebra.gui.dialog.GDialog;

public class IconSizeDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	JTextField widthField;
	JTextField heightField;
	IconEditor editor;
	JButton defaultButton;

	IconSizeDialog(IconEditor owner) {
		super(owner, true);
		editor = owner;
		getContentPane().setLayout(new BorderLayout());

		// パネルを追加する
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(2, 1));
		topPanel.setPreferredSize(new Dimension(200, 80));
		getContentPane().add("Center", topPanel);

		{
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout());
			// panel.setPreferredSize(new Dimension(320,32));

			JLabel label = new JLabel(PCARD.pc.intl.getDialogText("Width:"));
			label.setPreferredSize(new Dimension(64, label.getPreferredSize().height));
			panel.add(label);

			JTextField area1 = new JTextField("" + owner.mainImg.getWidth());
			area1.setPreferredSize(new Dimension(64, area1.getPreferredSize().height));
			panel.add(area1);
			widthField = area1;
			topPanel.add(panel);
		}
		{
			JPanel panel = new JPanel();
			// panel.setPreferredSize(new Dimension(320,32));

			JLabel label = new JLabel(PCARD.pc.intl.getDialogText("Height:"));
			label.setPreferredSize(new Dimension(64, label.getPreferredSize().height));
			panel.add(label);
			JTextField area2 = new JTextField("" + owner.mainImg.getHeight());
			area2.setPreferredSize(new Dimension(64, area2.getPreferredSize().height));
			panel.add(area2);
			heightField = area2;
			topPanel.add(panel);
		}

		// パネルを追加する
		JPanel btmPanel = new JPanel();
		getContentPane().add("South", btmPanel);

		{
			JButton btn1 = new JButton("Cancel");
			btn1.addActionListener(this);
			btmPanel.add(btn1);
		}

		{
			JButton btn2 = new JButton("OK");
			btn2.addActionListener(this);
			btmPanel.add(btn2);
			getRootPane().setDefaultButton(btn2);
			defaultButton = btn2;
		}

		setBounds(owner.getX() + owner.getWidth() / 2 - 120, owner.getY() + owner.getHeight() / 2 - 120, 240, 160);
		setUndecorated(true);// タイトルバー非表示

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (defaultButton != null)
					defaultButton.requestFocus();
			}
		});

		setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			int width = 0;
			int height = 0;
			try {
				width = Integer.valueOf(widthField.getText());
				height = Integer.valueOf(heightField.getText());
			} catch (Exception e2) {

			}
			if (width <= 0 || height <= 0 || width * height >= 5000 * 5000) {
				new GDialog(editor, PCARD.pc.intl.getDialogText("Illegal size."), null, "OK", null, null);
			} else {
				new IconEditor(editor.owner, editor.rsrc, editor.type, editor.rsrcid, width, height);
				editor.dispose();
			}
		}
		this.dispose();
	}
}
