package hyperzebra.subsystem.iconeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import hyperzebra.PictureFile;
import hyperzebra.Rsrc;
import hyperzebra.gui.PCARD;
import hyperzebra.gui.TBCursor;
import hyperzebra.gui.dialog.GDialog;
import hyperzebra.tool.PaintTool;
import hyperzebra.tool.SelectTool;

public class HotSpotDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	JTextField xField;
	JTextField yField;
	IconEditor editor;
	JButton defaultButton;

	HotSpotDialog(IconEditor owner) {
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

			JLabel label = new JLabel(PCARD.pc.intl.getDialogText("X:"));
			label.setPreferredSize(new Dimension(64, label.getPreferredSize().height));
			panel.add(label);

			Point hotSpot = owner.rsrc.getHotSpotAll(owner.rsrcid);
			JTextField area1 = new JTextField("" + hotSpot.x);
			area1.setPreferredSize(new Dimension(64, area1.getPreferredSize().height));
			panel.add(area1);
			xField = area1;
			topPanel.add(panel);
		}
		{
			JPanel panel = new JPanel();
			// panel.setPreferredSize(new Dimension(320,32));

			JLabel label = new JLabel(PCARD.pc.intl.getDialogText("Y:"));
			label.setPreferredSize(new Dimension(64, label.getPreferredSize().height));
			panel.add(label);
			Point hotSpot = owner.rsrc.getHotSpotAll(owner.rsrcid);
			JTextField area2 = new JTextField("" + hotSpot.y);
			area2.setPreferredSize(new Dimension(64, area2.getPreferredSize().height));
			panel.add(area2);
			yField = area2;
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
			int x = 0;
			int y = 0;
			try {
				x = Integer.valueOf(xField.getText());
				y = Integer.valueOf(yField.getText());
			} catch (Exception e2) {

			}
			if (x <= 0 || y <= 0 || x >= 32 || y >= 32) {
				new GDialog(editor, PCARD.pc.intl.getDialogText("Illegal point."), null, "OK", null, null);
			} else {
				Rsrc.rsrcClass r = editor.rsrc.getResourceAll(editor.rsrcid, editor.type);
				r.hotsporleft = x;
				r.hotsportop = y;
			}
		}
		this.dispose();
	}
}

class ICDropListener extends DropTargetAdapter {
	IconEditor owner;

	ICDropListener(IconEditor owner) {
		this.owner = owner;
	}

	public void drop(DropTargetDropEvent e) {
		try {
			Transferable transfer = e.getTransferable();
			if (transfer.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				@SuppressWarnings("unchecked")
				List<File> fileList = (List<File>) (transfer.getTransferData(DataFlavor.javaFileListFlavor));
				String path = fileList.get(0).toString();
				BufferedImage bi = null;
				try {
					bi = PictureFile.loadPbm(path);
					if (bi == null) {
						bi = javax.imageio.ImageIO.read(new File(path));
					}
					if (bi == null) {
						bi = PictureFile.loadPICT(path);
					}
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				if (bi != null) {
					if (owner.tool != null) {
						PaintTool.owner = owner;
						owner.tool.end();
					}
					owner.tool = new SelectTool();
					TBCursor.changeCursor(owner);

					owner.redoBuf = bi;
					((SelectTool) owner.tool).move = true;
					((SelectTool) owner.tool).srcRect = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());
					((SelectTool) owner.tool).moveRect = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());
					owner.mainPane.repaint();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
