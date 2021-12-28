package hyperzebra.gui.button;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import hyperzebra.gui.PCARDFrame;
import hyperzebra.tool.PaintBucketTool;
import hyperzebra.tool.PaintTool;

public class GradButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static GradButtonListener listener = new GradButtonListener();
	JPopupMenu popup = new JPopupMenu(); // ポップアップメニューを生成
	public Color color1;
	public Color color2;
	public double angle;
	public boolean use = false;

	public GradButton(Color color1, Color color2, int y, int x) {
		super("Gradation");
		this.setFocusable(false);
		// BufferedImage bi = new BufferedImage(16,12,BufferedImage.TYPE_INT_RGB);
		// Graphics g = bi.createGraphics();
		// g.setColor(color1);
		// g.fillRect(0,0,16,12);
		// this.setIcon(new ImageIcon(bi));
		this.setText(/* PCARDFrame.pc.intl.getToolText("Gradation")+" "+ */(this.use ? "" : "off"));
		this.color1 = color1;
		this.color2 = color2;
		this.angle = 0;
		// this.setBounds((y*3+x)*24,0/*y*20*/,24,20);
		this.setBounds(x * 26, y * 28 + 16, 26, 26);
		setMargin(new Insets(0, 0, 0, 0));
		this.addActionListener(listener);
		this.addMouseListener(listener);

		{
			int[] angles = { 0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190,
					200, 210, 220, 230, 240, 250, 260, 270, 280, 290, 300, 310, 320, 330, 340, 350 };
			for (int i = 0; i < angles.length; i++) {
				addPopupMenuItem(PCARDFrame.pc.intl.getToolText("Angle") + " " + angles[i], new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						PCARDFrame.pc.grad.angle = Math.PI / 180.0
								* Integer.valueOf(((JMenuItem) (e.getSource())).getText().split(" ")[1]);
						PCARDFrame.pc.grad.makeIcon();
					}
				});
			}
		}
	}

	// メニュー項目を追加
	private JMenuItem addPopupMenuItem(String name, ActionListener al) {
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(al);
		popup.add(item);
		return item;
	}

	public void makeIcon() {
		if (this.use) {
			this.color1 = PaintTool.owner.fore.color;
			this.color2 = PaintTool.owner.back.color;
			BufferedImage bi = new BufferedImage(16, 12, BufferedImage.TYPE_INT_ARGB);
			bi.getGraphics().fillRect(0, 0, 16, 12);
			PaintBucketTool.gradfill(bi, this.color1, this.color2, this.angle);
			this.setIcon(new ImageIcon(bi));
		} else {
			this.setIcon(null);
		}
	}

	// paintComponentをオーバーライドすると表示が崩れる
	/*
	 * @Override protected void paintComponent(Graphics g) { }
	 */
}

class GradButtonListener implements ActionListener, MouseListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		GradButton btn = (GradButton) e.getSource();
		/* if(e.getActionCommand().equals("color1")) */ {
			btn.use = !btn.use;
			btn.setText(/* PCARDFrame.pc.intl.getToolText("Gradation")+" "+ */(btn.use ? "" : "off"));
			btn.makeIcon();
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// String cmd = ((GradButton)arg0.getSource()).getText();
		if (javax.swing.SwingUtilities.isRightMouseButton(arg0)) {
			// if(PCARDFrame.pc.intl.getToolText("Gradation").equals(cmd.split(" ")[0])){
			((GradButton) arg0.getSource()).popup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			// }
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}