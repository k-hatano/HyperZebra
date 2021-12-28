package hyperzebra.gui.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import hyperzebra.gui.PCARD;
import hyperzebra.gui.PCARDFrame;

public class PaintBlendDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private PCARDFrame owner;
	public static PaintBlendDialog dialog;

	public static void showPaintBlendDialog(PCARDFrame owner) {
		if (dialog != null && dialog.owner == owner) {
			dialog.SetDialogContents();
			dialog.setBounds(owner.getX() + owner.getWidth() / 2 - 150, owner.getY() + 20, 300, 150);
			dialog.setVisible(true);
			return;
		}

		if (dialog != null) {
			dialog.dispose();
		}

		dialog = new PaintBlendDialog(owner);
	}

	private PaintBlendDialog(PCARDFrame owner) {
		super(owner, false);
		this.owner = owner;
		getContentPane().setLayout(new BorderLayout());
		setTitle(PCARD.pc.intl.getDialogText("Blending Mode"));

		SetDialogContents();

		setBounds(owner.getX() + owner.getWidth() / 2 - 150, owner.getY() + 20, 300, 150);

		setResizable(false);
		setVisible(true);
	}

	private void SetDialogContents() {
		this.getContentPane().removeAll();

		JPanel mainpanel = new JPanel();
		getContentPane().add("Center", mainpanel);

		{
			JPanel aqpanel = new JPanel();
			Border aquaBorder = UIManager.getBorder("TitledBorder.aquaVariant");
			if (aquaBorder == null) {
				aquaBorder = new EtchedBorder();
			}
			aqpanel.setBorder(aquaBorder);
			getContentPane().add("Center", aqpanel);

			JPanel panel = new JPanel();
			panel.setOpaque(false);
			aqpanel.add(panel);

			String[] value = new String[] { PCARD.pc.intl.getDialogText("Copy"), PCARD.pc.intl.getDialogText("Blend"), // 指定の強さでアルファブレンド
					PCARD.pc.intl.getDialogText("Add"), // (指定の強さで加算)発光
					PCARD.pc.intl.getDialogText("Subtract"), // (指定の強さで減算)焼きこみ
					PCARD.pc.intl.getDialogText("Multiply"), // 乗算
					PCARD.pc.intl.getDialogText("Screen"), // 指定の輝度から引いた色で掛け算
					PCARD.pc.intl.getDialogText("Darken"), // 暗い色を残す
					PCARD.pc.intl.getDialogText("Lighten"), // 明るい色を残す
					PCARD.pc.intl.getDialogText("Difference"), // 差の絶対値
					PCARD.pc.intl.getDialogText("Hue"), // 色相
					PCARD.pc.intl.getDialogText("Color"), // 色相と彩度
					PCARD.pc.intl.getDialogText("Saturation"), // 彩度
					PCARD.pc.intl.getDialogText("Luminosity"), // 輝度
					PCARD.pc.intl.getDialogText("Alpha Channel") // 輝度をアルファチャンネルに
			};
			JComboBox combo = new JComboBox(value);
			combo.setName("Mode");
			combo.setSelectedIndex(owner.blendMode);
			combo.setMaximumRowCount(32);
			combo.addActionListener(this);
			panel.add(combo);

			panel = new JPanel();
			panel.setOpaque(false);
			aqpanel.add(panel);

			value = new String[21];
			for (int i = 0; i < value.length; i++) {
				value[i] = i * 5 + "%";
			}
			combo = new JComboBox(value);
			combo.setName("Level");
			combo.setSelectedIndex(owner.blendLevel / 5);
			combo.setMaximumRowCount(32);
			combo.addActionListener(this);
			panel.add(combo);
		}

		this.getContentPane().repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = ((JComponent) e.getSource()).getName();
		String cmd = PCARD.pc.intl.getDialogEngText(name);

		if (cmd == null)
			return;

		if (cmd != null && cmd.equals("Mode")) {
			String str = (String) ((JComboBox) e.getSource()).getSelectedItem();
			String selectedMode = PCARD.pc.intl.getDialogEngText(str);
			owner.blendMode = owner.mainPane.getBlendMode(selectedMode);
		}
		if (cmd != null && cmd.equals("Level")) {
			int index = ((JComboBox) e.getSource()).getSelectedIndex();
			owner.blendLevel = index * 5;
		}

		owner.mainPane.repaint();
	}
}