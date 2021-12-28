package hyperzebra.gui.button;

import java.awt.Color;
import java.awt.Graphics;
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

import hyperzebra.gui.PCARD;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.gui.dialog.GColorDialog;
import hyperzebra.tool.PaintTool;

public class CPButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static CPButtonListener listener = new CPButtonListener();
	JPopupMenu popup = new JPopupMenu(); // ポップアップメニューを生成
	public Color color;

	public CPButton(Color in_color, int y, int x, boolean isback) {
		super("");
		this.setFocusable(false);
		BufferedImage bi = new BufferedImage(16, 12, BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.createGraphics();
		g.setColor(in_color);
		g.fillRect(0, 0, 16, 12);
		this.setIcon(new ImageIcon(bi));
		this.color = in_color;
		// this.setForeground(color);
		// this.setBackground(color);
		// this.setBounds((y*3+x)*24,0/*y*20*/,24,20);
		this.setBounds(x * 26, y * 28 + 16, 26, 26);
		setMargin(new Insets(0, 0, 0, 0));
		this.addActionListener(listener);
		this.addMouseListener(listener);

		if (isback) {
			addPopupMenuItem(PCARDFrame.pc.intl.getToolText("Transparency"), new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Color c = PaintTool.owner.back.color;
					int alpha = c.getAlpha() == 0 ? 0xFF : 0x00;
					PaintTool.owner.back.color = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
					PaintTool.owner.back.makeIcon(PaintTool.owner.back.color);
				}
			});
		}
	}

	// メニュー項目を追加
	private JMenuItem addPopupMenuItem(String name, ActionListener al) {
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(al);
		popup.add(item);
		return item;
	}

	public void makeIcon(Color col) {
		this.color = col;
		BufferedImage bi = new BufferedImage(16, 12, BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 16, 12);
		g.setColor(Color.GRAY);
		g.drawRect(0, 0, 8, 12);
		g.drawRect(0, 0, 16, 6);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, 16, 12);
		g.setColor(col);
		g.fillRect(0, 0, 16, 12);
		this.setIcon(new ImageIcon(bi));
	}

	// paintComponentをオーバーライドすると表示が崩れる
	/*
	 * @Override protected void paintComponent(Graphics g) { }
	 */
}

class CPButtonListener implements ActionListener, MouseListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		CPButton btn = (CPButton) e.getSource();
		/* if(e.getActionCommand().equals("color1")) */ {
			// Color col = JColor.showDialog(PaintTool.owner, "Color", btn.color );
			/*
			 * Color col = GColorDialog.getColor(PaintTool.owner, btn.color, new
			 * Point(btn.getX(), btn.getY()), true);
			 * 
			 * if(col != null){ btn.makeIcon(col); }
			 */
			new CPButtonThread(btn, e).start();
		}
	}

	class CPButtonThread extends Thread {
		CPButton btn;
		ActionEvent e;

		CPButtonThread(CPButton btn, ActionEvent e) {
			super();
			this.btn = btn;
			this.e = e;
		}

		public void run() {
			Color col = null;
			if (btn.getClass() == CPButton.class) {
				col = GColorDialog.getColor(PaintTool.owner, btn.color, true);
			} else if (btn.getClass() == AuthColorButton.class) {
				col = GColorDialog.getColor(PCARD.pc, btn.color, true);
			}

			if (col != null) {
				btn.makeIcon(col);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (javax.swing.SwingUtilities.isRightMouseButton(arg0)) {
			((CPButton) arg0.getSource()).popup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
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