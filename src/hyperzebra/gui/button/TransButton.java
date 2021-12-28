package hyperzebra.gui.button;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import hyperzebra.gui.PCARD;
import hyperzebra.gui.TBCursor;
import hyperzebra.tool.PaintTool;

public class TransButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPopupMenu popup = new JPopupMenu(); // ポップアップメニューを生成

	public TransButton(String in_text, int y, int x) {
		super();
		this.setFocusable(false);
		// this.setBounds((y*3+x)*24,0/*y*20*/,24,20);
		this.setBounds(x * 26, y * 28 + 16, 26, 26);
		this.setName(in_text);
		setMargin(new Insets(0, 0, 0, 0));

		if (PCARD.pc.intl.getToolText("Transparency").equals(in_text)) {
			int[] alpha = { 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95 };
			for (int i = 0; i < alpha.length; i++) {
				addPopupMenuItem(alpha[i] + " %", new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PaintTool.alpha = 100 - Integer.valueOf(((JMenuItem) (e.getSource())).getText().split(" ")[0]);
						TBCursor.changeCursor(PCARD.pc);

						for (int i = 0; i < popup.getComponentCount(); i++) {
							if (popup.getComponent(i) instanceof JCheckBoxMenuItem) {
								JCheckBoxMenuItem item = (JCheckBoxMenuItem) popup.getComponent(i);
								item.setSelected(false);
							}
						}
						((JCheckBoxMenuItem) (e.getSource())).setSelected(true);
						PaintTool.owner.trans.setText(100 - PaintTool.alpha + "%");
					}
				}, alpha[i] == PaintTool.alpha);
			}
		}

		setText(100 - PaintTool.alpha + "%");
		setFont(new Font("", 0, 10));
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
