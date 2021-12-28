package hyperzebra.gui.button;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import hyperzebra.PictureFile;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.tool.PaintTool;

public class PatButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static PatButtonListener listener = new PatButtonListener();
	JPopupMenu popup = new JPopupMenu(); // ポップアップメニューを生成
	public int pattern;
	public BufferedImage[] patterns = new BufferedImage[40];

	public PatButton(int in_pattern, int y, int x) {
		super(/* "Pattern" */);
		this.setFocusable(false);
		this.pattern = in_pattern;
		// this.setBounds((y*3+x)*24,0/*y*20*/,24,20);
		this.setBounds(x * 26, y * 28 + 16, 26, 26);
		setMargin(new Insets(0, 0, 0, 0));
		// this.addActionListener(listener);
		this.addMouseListener(listener);

		for (int i = 0; i < patterns.length; i++) {
			if (PCARDFrame.pc.stack.Pattern[i] != null) {
				try {
					patterns[i] = ImageIO.read(new File(PCARDFrame.pc.stack.file.getParent() + File.separatorChar
							+ PCARDFrame.pc.stack.Pattern[i]));
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (patterns[i] == null && PCARDFrame.pc.stack.Pattern[i].length() > 0) {
					patterns[i] = PictureFile.loadPbm(
							PCARDFrame.pc.stack.file.getParent() + File.separatorChar + PCARDFrame.pc.stack.Pattern[i]);
				}
				if (patterns[i] == null && PCARDFrame.pc.stack.Pattern[i].length() > 0) {
					patterns[i] = PictureFile.loadPbm("resource" + File.separatorChar + "PAT_" + i + ".pbm");
				}
				if (patterns[i] == null) {
					patterns[i] = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
					Graphics g = patterns[i].getGraphics();
					g.setColor(Color.BLACK);
					g.fillRect(0, 0, 16, 16);
				}
				if (patterns[i].getWidth() < 16 || patterns[i].getHeight() < 16) {
					BufferedImage pat = patterns[i];
					int width = pat.getWidth();
					int height = pat.getHeight();
					if (width < 16)
						width *= 2;
					if (height < 16)
						height *= 2;
					patterns[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
					patterns[i].getGraphics().drawImage(pat, 0, 0, PCARDFrame.pc);
					patterns[i].getGraphics().drawImage(pat, 0, pat.getHeight(), PCARDFrame.pc);
					patterns[i].getGraphics().drawImage(pat, pat.getWidth(), 0, PCARDFrame.pc);
					patterns[i].getGraphics().drawImage(pat, pat.getWidth(), pat.getHeight(), PCARDFrame.pc);
				}
			}
		}

		if (patterns[pattern] != null) {
			setIcon(new ImageIcon(patterns[pattern]));
		}

		{
			for (int i = 0; i < patterns.length; i++) {
				ImageIcon icon = null;
				if (patterns[i] != null) {
					icon = new ImageIcon(patterns[i]);
				} else {
					// パターンを./resource/から取ってくる
					String fname = "PAT_" + (i + 1) + ".png";

					File ifile = new File("." + File.separatorChar + "resource" + File.separatorChar + fname);
					try {
						patterns[i] = ImageIO.read(ifile);
						icon = new ImageIcon(patterns[i]);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				JCheckBoxMenuItem item = addPopupMenuItem(PCARDFrame.pc.intl.getToolText("Pattern") + " " + i, icon,
						new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								PaintTool.owner.pat.pattern = Integer
										.valueOf(((JMenuItem) (e.getSource())).getText().split(" ")[1]);
								BufferedImage patbi = PaintTool.owner.pat.patterns[PaintTool.owner.pat.pattern];
								if (patbi != null) {
									PaintTool.owner.pat.setIcon(new ImageIcon(patbi));
								}

								for (int i = 0; i < PaintTool.owner.pat.popup.getComponentCount(); i++) {
									Component c = PaintTool.owner.pat.popup.getComponent(i);
									if (c.getClass() == JCheckBoxMenuItem.class) {
										((JCheckBoxMenuItem) c).setSelected(false);
									}
								}
								((JCheckBoxMenuItem) (e.getSource())).setSelected(true);
							}
						});
				if (i == pattern) {
					item.setSelected(true);
				}
			}
		}
	}

	// メニュー項目を追加
	private JCheckBoxMenuItem addPopupMenuItem(String name, ImageIcon icon, ActionListener al) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(name, icon);
		item.addActionListener(al);
		popup.add(item);
		return item;
	}
}

class PatButtonListener implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// String cmd = ((PatButton)arg0.getSource()).getText();
		// if(PCARDFrame.pc.intl.getToolText("Pattern").equals(cmd.split(" ")[0])){
		((PatButton) arg0.getSource()).popup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
		// }
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
