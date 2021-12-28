package hyperzebra.gui.dialog;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import hyperzebra.gui.PCARD;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.tool.LassoTool;
import hyperzebra.tool.SelectTool;
import hyperzebra.tool.SmartSelectTool;
import hyperzebra.tool.toolSelectInterface;

public class PaintScaleDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private PCARDFrame owner;
	private AffineTransform save_af;
	private JTextField widthfield;
	private JTextField heightfield;
	private JCheckBox keepRatio;

	public PaintScaleDialog(PCARDFrame owner) {
		super(owner, true);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		this.owner = owner;
		save_af = owner.selectaf;
		getContentPane().setLayout(new BorderLayout());
		setTitle(PCARD.pc.intl.getDialogText("Scale Selection"));

		SetDialogContents();

		setBounds(owner.getX() + owner.getWidth() / 2 - 100, owner.getY() + owner.getHeight() / 2 - 100 - 20, 200, 200);

		setResizable(false);
		setVisible(true);
	}

	private void SetDialogContents() {
		this.getContentPane().removeAll();

		JPanel mainpanel = new JPanel();
		mainpanel.setLayout(new GridLayout(3, 1));
		getContentPane().add("Center", mainpanel);

		toolSelectInterface tl = (toolSelectInterface) owner.tool;
		Rectangle rect = tl.getSelectedRect();

		{
			JPanel panel = new JPanel();
			mainpanel.add(panel);

			JLabel label = new JLabel(PCARD.pc.intl.getDialogText("Width:"));
			label.setPreferredSize(new Dimension(64, label.getPreferredSize().height));
			panel.add(label);

			widthfield = new JTextField("" + rect.width);
			widthfield.setName("width");
			widthfield.setPreferredSize(new Dimension(64, widthfield.getPreferredSize().height));
			widthfield.getDocument().addDocumentListener(new DocListener(widthfield));
			panel.add(widthfield);
		}
		{
			JPanel panel = new JPanel();
			mainpanel.add(panel);

			JLabel label = new JLabel(PCARD.pc.intl.getDialogText("Height:"));
			label.setPreferredSize(new Dimension(64, label.getPreferredSize().height));
			panel.add(label);

			heightfield = new JTextField("" + rect.height);
			heightfield.setName("height");
			heightfield.setPreferredSize(new Dimension(64, heightfield.getPreferredSize().height));
			heightfield.getDocument().addDocumentListener(new DocListener(heightfield));
			panel.add(heightfield);
		}
		{
			keepRatio = new JCheckBox(PCARD.pc.intl.getDialogText("Keep aspect ratio"));
			keepRatio.setName("Keep aspect ratio");
			keepRatio.setSelected(true);
			mainpanel.add(keepRatio);
		}

		JPanel okpanel = new JPanel();
		getContentPane().add("South", okpanel);

		// ok, cancel
		{
			JButton button = new JButton(PCARD.pc.intl.getDialogText("Cancel"));
			button.setName("Cancel");
			button.addActionListener(this);
			okpanel.add(button);
		}
		{
			JButton button = new JButton(PCARD.pc.intl.getDialogText("OK"));
			button.setName("OK");
			button.addActionListener(this);
			okpanel.add(button);
		}

		this.getContentPane().repaint();
		// this.setVisible(true);
	}

	static boolean flag = false;

	class DocListener implements DocumentListener {
		JTextField jfield;

		public DocListener(JTextField fld) {
			jfield = fld;
		}

		public void changedUpdate(DocumentEvent e) {
			toolSelectInterface tl = (toolSelectInterface) owner.tool;
			Rectangle rect = tl.getSelectedRect();

			String widthstr = widthfield.getText();
			if (!widthstr.matches("^[0-9]{1,5}$"))
				return;
			String heightstr = heightfield.getText();
			if (!heightstr.matches("^[0-9]{1,5}$"))
				return;
			int newwidth = Integer.valueOf(widthstr);
			int newheight = Integer.valueOf(heightstr);
			if (newwidth == 0 && newheight == 0)
				return;

			if (flag)
				return;
			flag = true;

			if (jfield.getName().equals("width")) {
				if (keepRatio.isSelected()) {
					heightfield.setText("" + newwidth * rect.height / rect.width);
				}
			} else if (jfield.getName().equals("height")) {
				if (keepRatio.isSelected()) {
					widthfield.setText("" + newheight * rect.width / rect.height);
				}
			}

			flag = false;

			widthstr = widthfield.getText();
			heightstr = heightfield.getText();
			newwidth = Integer.valueOf(widthstr);
			newheight = Integer.valueOf(heightstr);
			if (newwidth == 0 || newheight == 0)
				return;

			double xrate, yrate;
			if (keepRatio.isSelected()) {
				if (newwidth > newheight) {
					xrate = (double) newwidth / rect.width;
					yrate = xrate;
				} else {
					yrate = (double) newheight / rect.height;
					xrate = yrate;
				}
			} else {
				xrate = (double) newwidth / rect.width;
				yrate = (double) newheight / rect.height;
			}

			AffineTransform af = new AffineTransform();
			af.scale(xrate, yrate);
			Rectangle moverect = tl.getMoveRect();
			af.translate(moverect.x / xrate - moverect.x, moverect.y / yrate - moverect.y);
			owner.selectaf = af;
			owner.mainPane.repaint();
		}

		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = ((JComponent) e.getSource()).getName();
		String cmd = PCARD.pc.intl.getDialogEngText(name);

		if (cmd == null)
			return;

		if (cmd.equals("Cancel")) {
			owner.selectaf = save_af;
			this.dispose();
			owner.mainPane.repaint();
			return;
		} else if (cmd.equals("OK")) {
			// 拡大縮小を反映
			String widthstr = widthfield.getText();
			if (!widthstr.matches("^[0-9]{1,5}$"))
				return;
			String heightstr = heightfield.getText();
			if (!heightstr.matches("^[0-9]{1,5}$"))
				return;
			int newwidth = Integer.valueOf(widthstr);
			int newheight = Integer.valueOf(heightstr);
			if (newwidth == 0 || newheight == 0)
				return;

			toolSelectInterface tl = (toolSelectInterface) owner.tool;
			if (tl.getClass() == SelectTool.class) {
				Image img = owner.redoBuf.getScaledInstance(newwidth, newheight, Image.SCALE_SMOOTH);
				BufferedImage newimg = new BufferedImage(newwidth, newheight, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = (Graphics2D) newimg.getGraphics();
				g2.drawImage(img, 0, 0, newwidth, newheight, null);

				owner.redoBuf = newimg;

				((SelectTool) tl).srcRect.width = newwidth;
				((SelectTool) tl).srcRect.height = newheight;
				((SelectTool) tl).moveRect.width = newwidth;
				((SelectTool) tl).moveRect.height = newheight;
				if (((SelectTool) tl).moveRect.x < 0)
					((SelectTool) tl).moveRect.x = 0;
				if (((SelectTool) tl).moveRect.y < 0)
					((SelectTool) tl).moveRect.y = 0;
			} else {
				if (tl.getClass() == LassoTool.class) {
					Rectangle rect = tl.getSelectedRect();
					int newwidth2 = owner.redoBuf.getWidth() * newwidth / rect.width;
					int newheight2 = owner.redoBuf.getHeight() * newheight / rect.height;

					Image selimg = owner.redoBuf.getScaledInstance(newwidth2, newheight2, Image.SCALE_SMOOTH);
					BufferedImage newimg = new BufferedImage(newwidth, newheight, BufferedImage.TYPE_INT_ARGB);
					Graphics2D newg = newimg.createGraphics();
					newg.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					newg.fillRect(0, 0, newimg.getWidth(), newimg.getHeight());
					newg = newimg.createGraphics();
					newg.drawImage(selimg, 0, 0, newwidth, newheight, rect.x * newwidth / rect.width,
							rect.y * newheight / rect.height, rect.x * newwidth / rect.width + newwidth,
							rect.y * newheight / rect.height + newheight, null);

					owner.redoBuf = newimg;

					Image mskimg = ((LassoTool) tl).srcbits.getScaledInstance(newwidth2, newheight2,
							Image.SCALE_SMOOTH);
					BufferedImage newimg2 = new BufferedImage(newwidth, newheight, BufferedImage.TYPE_INT_ARGB);
					Graphics2D newg2 = newimg2.createGraphics();
					newg2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					newg2.fillRect(0, 0, newimg2.getWidth(), newimg2.getHeight());
					newg2 = newimg2.createGraphics();
					newg2.drawImage(mskimg, 0, 0, newwidth, newheight, rect.x * newwidth / rect.width,
							rect.y * newheight / rect.height, rect.x * newwidth / rect.width + newwidth,
							rect.y * newheight / rect.height + newheight, null);

					((LassoTool) tl).srcbits = newimg2;

					((LassoTool) tl).movePoint.x += rect.x;
					((LassoTool) tl).movePoint.y += rect.y;
				} else if (tl.getClass() == SmartSelectTool.class) {
					Rectangle rect = tl.getSelectedRect();
					int newwidth2 = owner.redoBuf.getWidth() * newwidth / rect.width;
					int newheight2 = owner.redoBuf.getHeight() * newheight / rect.height;

					Image selimg = owner.redoBuf.getScaledInstance(newwidth2, newheight2, Image.SCALE_SMOOTH);
					BufferedImage newimg = new BufferedImage(newwidth, newheight, BufferedImage.TYPE_INT_ARGB);
					Graphics2D newg = newimg.createGraphics();
					newg.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					newg.fillRect(0, 0, newimg.getWidth(), newimg.getHeight());
					newg = newimg.createGraphics();
					newg.drawImage(selimg, 0, 0, newwidth, newheight, rect.x * newwidth / rect.width,
							rect.y * newheight / rect.height, rect.x * newwidth / rect.width + newwidth,
							rect.y * newheight / rect.height + newheight, null);

					owner.redoBuf = newimg;

					Image mskimg = ((SmartSelectTool) tl).srcbits.getScaledInstance(newwidth2, newheight2,
							Image.SCALE_SMOOTH);
					BufferedImage newimg2 = new BufferedImage(newwidth, newheight, BufferedImage.TYPE_INT_ARGB);
					Graphics2D newg2 = newimg2.createGraphics();
					newg2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					newg2.fillRect(0, 0, newimg2.getWidth(), newimg2.getHeight());
					newg2 = newimg2.createGraphics();
					newg2.drawImage(mskimg, 0, 0, newwidth, newheight, rect.x * newwidth / rect.width,
							rect.y * newheight / rect.height, rect.x * newwidth / rect.width + newwidth,
							rect.y * newheight / rect.height + newheight, null);

					((SmartSelectTool) tl).srcbits = newimg2;

					((SmartSelectTool) tl).movePoint.x += rect.x;
					((SmartSelectTool) tl).movePoint.y += rect.y;
				}

			}

			owner.selectaf = save_af;
			this.dispose();
			owner.mainPane.repaint();
			return;
		}
	}
}