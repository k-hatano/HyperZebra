package hyperzebra.gui.button;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import hyperzebra.gui.PCARD;
import hyperzebra.gui.TBCursor;
import hyperzebra.tool.PaintTool;

public class TBButton extends JToggleButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JPopupMenu popup = new JPopupMenu(); // ポップアップメニューを生成
	// Color color;

	public TBButton(String in_text, int y, int x) {
		super();
		String text = PCARD.pc.intl.getToolEngText(in_text);
		setName(text);
		this.setBounds(x * 26, y * 28 + 16, 26, 26);
		setMargin(new Insets(0, 0, 0, 0));
		this.setFocusable(false);

		if (PCARD.pc.intl.getToolText("Brush").equals(in_text)) {
			int[] size = { 1, 2, 3, 4, 6, 8, 16, 32 };
			for (int i = 0; i < size.length; i++) {
				addPopupMenuItem("Brush " + size[i], new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PaintTool.brushSize = Integer
								.valueOf(((JCheckBoxMenuItem) (e.getSource())).getText().split(" ")[1]);
						TBCursor.changeCursor(PCARD.pc);

						for (int i = 0; i < popup.getComponentCount(); i++) {
							if (popup.getComponent(i) instanceof JCheckBoxMenuItem) {
								JCheckBoxMenuItem item = (JCheckBoxMenuItem) popup.getComponent(i);
								item.setSelected(false);
							}
						}
						((JCheckBoxMenuItem) (e.getSource())).setSelected(true);
					}
				}, (size[i] == PaintTool.brushSize));
			}
		}

		if (PCARD.pc.intl.getToolText("Line").equals(in_text)) {
			float[] size = { 0, 0.5f, 1, 1.5f, 2, 3, 4, 5, 6, 8, 10, 12, 16 };
			for (int i = 0; i < size.length; i++) {
				addPopupMenuItem("Line " + size[i], new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PaintTool.lineSize = Float
								.valueOf(((JCheckBoxMenuItem) (e.getSource())).getText().split(" ")[1]);
						TBCursor.changeCursor(PCARD.pc);

						for (int i = 0; i < popup.getComponentCount(); i++) {
							if (popup.getComponent(i) instanceof JCheckBoxMenuItem) {
								JCheckBoxMenuItem item = (JCheckBoxMenuItem) popup.getComponent(i);
								item.setSelected(false);
							}
						}
						((JCheckBoxMenuItem) (e.getSource())).setSelected(true);
					}
				}, (size[i] == PaintTool.lineSize));
			}
		}

		if (PCARD.pc.intl.getToolText("Rect").equals(in_text) || PCARD.pc.intl.getToolText("Oval").equals(in_text)) {
			addPopupMenuItem(PCARD.pc.intl.getToolText("Fill"), new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					PaintTool.owner.fill = true;

					for (int i = 0; i < popup.getComponentCount(); i++) {
						if (popup.getComponent(i) instanceof JCheckBoxMenuItem) {
							JCheckBoxMenuItem item = (JCheckBoxMenuItem) popup.getComponent(i);
							item.setSelected(false);
						}
					}
					((JCheckBoxMenuItem) (e.getSource())).setSelected(true);
				}
			}, PaintTool.owner.fill);
			addPopupMenuItem(PCARD.pc.intl.getToolText("Don't Fill"), new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					PaintTool.owner.fill = false;

					for (int i = 0; i < popup.getComponentCount(); i++) {
						if (popup.getComponent(i) instanceof JCheckBoxMenuItem) {
							JCheckBoxMenuItem item = (JCheckBoxMenuItem) popup.getComponent(i);
							item.setSelected(false);
						}
					}
					((JCheckBoxMenuItem) (e.getSource())).setSelected(true);
				}
			}, !PaintTool.owner.fill);
		}

		if (PCARD.pc.intl.getToolText("MagicWand").equals(in_text)) {
			int[] size = { 0, 1, 2, 3, 5, 7, 10, 15, 20, 25 };
			for (int i = 0; i < size.length; i++) {
				addPopupMenuItem("Color " + size[i] + " %", new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PaintTool.smartSelectPercent = Integer
								.valueOf(((JCheckBoxMenuItem) (e.getSource())).getText().split(" ")[1]);
						TBCursor.changeCursor(PCARD.pc);

						for (int i = 0; i < popup.getComponentCount(); i++) {
							if (popup.getComponent(i) instanceof JCheckBoxMenuItem) {
								JCheckBoxMenuItem item = (JCheckBoxMenuItem) popup.getComponent(i);
								item.setSelected(false);
							}
						}
						((JCheckBoxMenuItem) (e.getSource())).setSelected(true);
					}
				}, (size[i] == PaintTool.smartSelectPercent));
			}
		}

		try {
			BufferedImage bi = javax.imageio.ImageIO.read(new File("./resource/tb_button1.png"));
			BufferedImage bi2 = javax.imageio.ImageIO
					.read(new File("./resource/tb_" + PCARD.pc.intl.getToolEngText(text) + ".png"));
			bi.createGraphics().drawImage(bi2, 0, 0, this);
			ImageIcon icon = new ImageIcon(bi);
			setIcon(icon);

			bi = javax.imageio.ImageIO.read(new File("./resource/tb_button2.png"));
			bi.createGraphics().drawImage(bi2, 0, 0, this);
			icon = new ImageIcon(bi);
			setSelectedIcon(icon);

			setContentAreaFilled(false);
			setBorderPainted(false);
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	// メニュー項目を追加
	private JMenuItem addPopupMenuItem(String name, ActionListener al, boolean check) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(name);
		item.addActionListener(al);
		item.setSelected(check);
		// item.setBounds(0,0,getPreferredSize().width, getPreferredSize().height);
		popup.add(item);
		return item;
	}
}
