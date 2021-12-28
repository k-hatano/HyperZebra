package hyperzebra.subsystem.iconeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.TexturePaint;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
//import javax.swing.ImageIcon;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import hyperzebra.Rsrc;
import hyperzebra.gui.MyPanel;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.gui.button.CPButton;
import hyperzebra.gui.button.GradButton;
import hyperzebra.gui.button.PatButton;
import hyperzebra.gui.button.TBButton;
import hyperzebra.gui.button.TBButtonListener;
import hyperzebra.gui.button.TransButton;
import hyperzebra.gui.dialog.GDialog;
import hyperzebra.object.OCard;
import hyperzebra.subsystem.resedit.IconTypeEditor;
import hyperzebra.tool.PaintTool;
import hyperzebra.tool.SelectTool;

public class IconEditor extends PCARDFrame {
	private static final long serialVersionUID = 1L;
	IconEditor iconeditor;
	public Rsrc rsrc;
	public int rsrcid; // 現在のid。可変。
	int orgrsrcid; // 最初に開いたときに記録。不変。
	JTextField namefld;
	JTextField idfld;
	JFrame owner;
	public JScrollPane scrollpane;
	public JTextField[] textfields = new JTextField[2];
	ICDropListener droplistener = new ICDropListener(this);
	public String type;

	public IconEditor(JFrame owner, Rsrc rsrc, String type, int id) {
		this(owner, rsrc, type, id, 0, 0);
	}

	public IconEditor(JFrame owner, Rsrc rsrc, String type, int id, int width, int height) {
		this.rsrc = rsrc;
		this.owner = owner;
		this.type = type;
		rsrcid = id;
		orgrsrcid = id;
		iconeditor = this;
		tool = new SelectTool();

		bitLeft = 0.0f;
		bitTop = 0.0f;

		setTitle(this.rsrc.getFileName1(this.rsrcid, type));

		int w = 640;
		int h = 480;
		// frame
		getContentPane().setLayout(new BorderLayout());

		PaintTool.owner = pc;

		// menu
		new IEMenu(this);

		System.gc();

		// 画像を読み込む
		BufferedImage bi = null;
		BufferedImage srcimg = rsrc.getImage(id, type);
		if (srcimg == null) {
			if (width == 0 || height == 0) {
				width = 32;
				height = 32;
			}
			bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		} else {
			if (width == 0 || height == 0) {
				width = srcimg.getWidth();
				height = srcimg.getHeight();
			}
			bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			bi.getGraphics().drawImage(srcimg, 0, 0, null);
		}

		if (width < 128 && height < 128) {
			bit = 8;
		} else if (width < 320 && height < 320) {
			bit = 4;
		} else if (width < 640 && height < 640) {
			bit = 1;
		} else {
			bit = 1;
		}

		w = Math.max(w, width * bit + 180);
		h = Math.max(h, height * bit + 45);
		setBounds(owner.getX() + owner.getWidth() / 2 - w / 2, owner.getY() + owner.getHeight() / 2 - h / 2, w, h);

		{
			// ペイント用バッファ
			mainImg = bi;
			bgImg = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g = (Graphics2D) bgImg.getGraphics();
			BufferedImage txtr = new BufferedImage(4, 4, BufferedImage.TYPE_INT_BGR);
			txtr.getRaster().getDataBuffer().setElem(0, 0xFFFFFF);
			txtr.getRaster().getDataBuffer().setElem(1, 0xFFFFFF);
			txtr.getRaster().getDataBuffer().setElem(2, 0xFFDDDD);
			txtr.getRaster().getDataBuffer().setElem(3, 0xFFDDDD);
			txtr.getRaster().getDataBuffer().setElem(4, 0xFFFFFF);
			txtr.getRaster().getDataBuffer().setElem(5, 0xFFFFFF);
			txtr.getRaster().getDataBuffer().setElem(6, 0xFFDDDD);
			txtr.getRaster().getDataBuffer().setElem(7, 0xFFDDDD);
			txtr.getRaster().getDataBuffer().setElem(8, 0xFFDDDD);
			txtr.getRaster().getDataBuffer().setElem(9, 0xFFDDDD);
			txtr.getRaster().getDataBuffer().setElem(10, 0xFFFFFF);
			txtr.getRaster().getDataBuffer().setElem(11, 0xFFFFFF);
			txtr.getRaster().getDataBuffer().setElem(12, 0xFFDDDD);
			txtr.getRaster().getDataBuffer().setElem(13, 0xFFDDDD);
			txtr.getRaster().getDataBuffer().setElem(14, 0xFFFFFF);
			txtr.getRaster().getDataBuffer().setElem(15, 0xFFFFFF);
			Rectangle2D r = new Rectangle2D.Double(0, 0, 4, 4);
			g.setPaint(new TexturePaint(txtr, r));
			g.fillRect(0, 0, bgImg.getWidth(), bgImg.getHeight());
			// System.out.println("max:"+Runtime.getRuntime().maxMemory());
			// System.out.println("total:"+Runtime.getRuntime().totalMemory());
			if (Runtime.getRuntime().maxMemory()
					- (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) > 2 * 4 * bi.getWidth()
							* bi.getHeight()) {
				undoBuf = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
				redoBuf = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
			} else {
				new GDialog(this, "Out of Memory Error.", null, "OK", null, null);
			}
		}

		// leftside
		{
			JPanel leftPanel = new JPanel();
			leftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			leftPanel.setBounds(0, 0, 160, h);
			leftPanel.setPreferredSize(new Dimension(160, h));
			this.add("West", leftPanel);

			JPanel toolPanel = new JPanel();
			toolPanel.setLayout(null/* new GridLayout(14,1) */);
			toolPanel.setPreferredSize(new Dimension(78, 190));
			leftPanel.add(toolPanel);

			TBButton jbtn;
			ButtonGroup grp = new ButtonGroup();
			ActionListener listener = new IEActionListener(this);
			TBButtonListener listener2 = new TBButtonListener();

			jbtn = new TBButton(PCARDFrame.pc.intl.getToolText("Select"), 0, 0);
			grp.add(jbtn);
			jbtn.setSelected(true);
			jbtn.setFocusable(false);
			jbtn.addActionListener(listener);
			jbtn.addMouseListener(listener2);
			toolPanel.add(jbtn);

			jbtn = new TBButton(PCARDFrame.pc.intl.getToolText("Lasso"), 0, 1);
			grp.add(jbtn);
			jbtn.setFocusable(false);
			jbtn.addActionListener(listener);
			jbtn.addMouseListener(listener2);
			toolPanel.add(jbtn);

			jbtn = new TBButton(PCARDFrame.pc.intl.getToolText("MagicWand"), 0, 2);
			grp.add(jbtn);
			jbtn.setFocusable(false);
			jbtn.addActionListener(listener);
			jbtn.addMouseListener(listener2);
			toolPanel.add(jbtn);

			jbtn = new TBButton(PCARDFrame.pc.intl.getToolText("Pencil"), 1, 0);
			grp.add(jbtn);
			jbtn.setFocusable(false);
			jbtn.addActionListener(listener);
			jbtn.addMouseListener(listener2);
			toolPanel.add(jbtn);

			jbtn = new TBButton(PCARDFrame.pc.intl.getToolText("Brush"), 1, 1);
			grp.add(jbtn);
			jbtn.setFocusable(false);
			jbtn.addActionListener(listener);
			jbtn.addMouseListener(listener2);
			toolPanel.add(jbtn);

			jbtn = new TBButton(PCARDFrame.pc.intl.getToolText("Eraser"), 1, 2);
			grp.add(jbtn);
			jbtn.setFocusable(false);
			jbtn.addActionListener(listener);
			jbtn.addMouseListener(listener2);
			toolPanel.add(jbtn);

			jbtn = new TBButton(PCARDFrame.pc.intl.getToolText("Line"), 2, 0);
			grp.add(jbtn);
			jbtn.setFocusable(false);
			jbtn.addActionListener(listener);
			jbtn.addMouseListener(listener2);
			toolPanel.add(jbtn);

			jbtn = new TBButton(PCARDFrame.pc.intl.getToolText("Rect"), 2, 1);
			grp.add(jbtn);
			jbtn.setFocusable(false);
			jbtn.addActionListener(listener);
			jbtn.addMouseListener(listener2);
			toolPanel.add(jbtn);

			jbtn = new TBButton(PCARDFrame.pc.intl.getToolText("Oval"), 2, 2);
			grp.add(jbtn);
			jbtn.setFocusable(false);
			jbtn.addActionListener(listener);
			jbtn.addMouseListener(listener2);
			toolPanel.add(jbtn);

			jbtn = new TBButton(PCARDFrame.pc.intl.getToolText("PaintBucket"), 3, 0);
			grp.add(jbtn);
			jbtn.setFocusable(false);
			jbtn.addActionListener(listener);
			jbtn.addMouseListener(listener2);
			toolPanel.add(jbtn);

			jbtn = new TBButton(PCARDFrame.pc.intl.getToolText("Type"), 3, 1);
			grp.add(jbtn);
			jbtn.setFocusable(false);
			jbtn.addActionListener(listener);
			jbtn.addMouseListener(listener2);
			toolPanel.add(jbtn);

			this.fore = new CPButton(Color.BLACK, 4, 0, false);
			this.fore.setFocusable(false);
			toolPanel.add(this.fore);
			this.back = new CPButton(Color.WHITE, 4, 1, true);
			this.back.setFocusable(false);
			toolPanel.add(this.back);
			this.pat = new PatButton(11, 5, 0);
			this.pat.setFocusable(false);
			toolPanel.add(this.pat);
			this.grad = new GradButton(Color.BLACK, Color.WHITE, 5, 1);
			this.grad.setFocusable(false);
			toolPanel.add(this.grad);

			this.trans = new TransButton(PCARDFrame.pc.intl.getToolText("Transparency"), 5, 2);
			this.trans.setFocusable(false);
			// trans.addActionListener(listener);
			this.trans.addMouseListener(listener2);
			toolPanel.add(this.trans);

			leftPanel.add(new MyLabelPanel("name", "Name:", rsrc.getName1(id, type), 160, 120));
			leftPanel.add(new MyLabelPanel("id", "ID:", "" + id, 160, 80));

			/*
			 * JPanel savePanel = new JPanel(); leftPanel.add(savePanel); jbtn = new
			 * JButton(PCARD.pc.intl.getToolText("Save")); jbtn.addActionListener(listener);
			 * savePanel.add(jbtn);
			 */
		}

		toFront();
		setVisible(true);

		// scroll area
		scrollpane = new JScrollPane();
		{
			JPanel panel = new JPanel();
			panel.setLayout(null);
			this.add(panel);

			scrollpane.setName("JScrollPane");
			int sw = w - 160;
			int sh = h - getInsets().top;
			if (sw > bi.getWidth() * bit + 20)
				sw = bi.getWidth() * bit + 20;
			if (sh > bi.getHeight() * bit + 20)
				sh = bi.getHeight() * bit + 20;

			scrollpane.setBounds(((w - 160) - sw) / 2, ((h - getInsets().top) - sh) / 2, sw, sh);
			// scrollpane.setPreferredSize(new Dimension(sw, sh));
			scrollpane.getVerticalScrollBar().setValue(0);
			scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			panel.add("Center", scrollpane);
		}

		// mainpane
		{
			mainPane = new MyPanel(this);
			mainPane.setLayout(null);
			mainPane.setPreferredSize(new Dimension(bi.getWidth() * bit, bi.getHeight() * bit));
			mainPane.setBounds(0, 0, bi.getWidth() * bit, bi.getHeight() * bit);
			mainPane.setOpaque(false); // 隅の画像が消えないのを抑止。
			scrollpane.setViewportView(mainPane);

			IconGUI gui = new IconGUI(this);
			mainPane.addMouseListener(gui);
			mainPane.addMouseMotionListener(gui);
			/* DropTarget drop = */new DropTarget(mainPane, droplistener);
		}

		setVisible(true);

		addWindowListener(new IEWindowListener(this));
		addComponentListener(new IEComponentListener(scrollpane));
		addKeyListener(new IEKeyListener());

		System.gc();
	}

	boolean quicksave() {
		if (this.rsrc == null)
			return true;

		// this.tool.end();

		/*
		 * String filepath = this.rsrc.ownerstack.file.getParent()
		 * +File.separatorChar+"_"+this.rsrc.getFileName(this.rsrcid, "icon"); try {
		 * ImageIO.write(this.mainImg, "png", new File(filepath)); } catch (IOException
		 * e1) { e1.printStackTrace(); }
		 */

		return true;
	}

	public boolean save() {
		if (this.rsrc == null)
			return true;

		this.tool.end();

		int id = 0;
		try {
			id = Integer.valueOf(this.idfld.getText());
		} catch (Exception e2) {
		}

		// 名前とidを反映
		{
			Rsrc.rsrcClass iconres;

			if (this.orgrsrcid == id) {
				iconres = this.rsrc.getResource1(this.orgrsrcid, type);
			} else {
				String prefix;
				if (type.equals("icon")) {
					prefix = "ICON_";
				} else if (type.equals("cicn")) {
					prefix = "cicn_";
				} else if (type.equals("picture")) {
					prefix = "PICT_";
				} else if (type.equals("cursor")) {
					prefix = "CURS_";
				} else {
					prefix = "dummy_";
				}
				iconres = this.rsrc.new rsrcClass(0, type, "", prefix + id + ".png", "0", "0", null);
			}

			{
				iconres.name = this.namefld.getText();
				if (id != 0 && iconres.id != id) {
					if (iconres.id != 0) {
						this.rsrc.deleteResource(type, iconres.id);
					}
					iconres.id = id;
					this.rsrc.addResource(iconres);
				}
			}
		}

		String fileName = this.rsrc.getFileName1(id, type);
		String ext = "";
		if (fileName != null && fileName.lastIndexOf(".") >= 0) {
			ext = fileName.substring(fileName.lastIndexOf("."));
			if (!ext.equals(".png")) {
				fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".png";
				Rsrc.rsrcClass res = this.rsrc.getResource1(id, type);
				res.filename = fileName;
			}
		}
		String filepath = fileName;
		if (this.rsrc.ownerstack.file != null) {
			filepath = this.rsrc.ownerstack.file.getParent() + File.separatorChar + fileName;
		}

		if (this.mainImg != null) {
			try {
				ImageIO.write(this.mainImg, "png", new File(filepath));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if (owner.getClass() == IconTypeEditor.class) {
			IconTypeEditor typeeditor = (IconTypeEditor) owner;
			/*
			 * int id = 0; try{ id = Integer.valueOf(this.idfld.getText()); }catch(Exception
			 * e2){ }
			 */
			if (this.orgrsrcid != id) {
				(typeeditor).selectedButton = null;
				typeeditor.selectedId = new int[1];
				typeeditor.selectedId[0] = id;
				int scroll = typeeditor.scrollpane.getVerticalScrollBar().getValue();
				// 開き直す
				typeeditor.open(typeeditor.pcard, scroll);
			} else {
				typeeditor.updateContent(this.rsrcid);
			}
		}

		OCard.reloadCurrentCard();

		return true;
	}

	class MyLabelPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		MyLabelPanel(String name, String labelStr, String value, int width, int fldWidth) {
			super();
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

			JLabel label = new JLabel(PCARDFrame.pc.intl.getDialogText(labelStr));
			add(label);
			JTextField jfld = new JTextField(value);
			jfld.setMargin(new Insets(0, 0, 0, 0));
			jfld.setPreferredSize(new Dimension(fldWidth, jfld.getPreferredSize().height));
			jfld.setName(name);

			// textfieldに不要なフォーカスを取られないようにする
			jfld.setFocusable(false);
			for (int i = 0; i < textfields.length; i++) {
				if (textfields[i] == null) {
					textfields[i] = jfld;
					break;
				}
			}
			jfld.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					((JTextField) e.getSource()).setFocusable(true);
				}
			});

			add(jfld);
			if (name.equals("name")) {
				IconEditor.this.namefld = jfld;
			}
			if (name.equals("id")) {
				IconEditor.this.idfld = jfld;
			}

			this.setPreferredSize(new Dimension(width, jfld.getPreferredSize().height));
		}
	}
}

class IEWindowListener implements WindowListener {
	IconEditor owner;

	IEWindowListener(IconEditor owner) {
		this.owner = owner;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		if (PaintTool.owner != owner) {
			PaintTool.owner = owner;
			PaintTool.alpha = 100;
		}
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		PaintTool.owner = owner;
		owner.save();

		owner.iconeditor.end();
		owner.iconeditor = null;
		owner.rsrc = null;
		owner.namefld = null;
		owner.idfld = null;
		owner.owner = null;
		owner.textfields = null;
		owner.droplistener = null;
		owner.scrollpane = null;

		PaintTool.owner = PCARDFrame.pc;
		System.gc();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		owner.quicksave();
		PaintTool.owner = PCARDFrame.pc;
		System.gc();
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}

class IEComponentListener implements ComponentListener {
	JScrollPane scrollpane;

	IEComponentListener(JScrollPane scrollpane) {
		this.scrollpane = scrollpane;
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		IconEditor owner = (IconEditor) e.getSource();
		Dimension size = owner.getSize();
		int sw = size.width - 160;
		int sh = size.height - ((JFrame) e.getSource()).getInsets().top;
		int rate = owner.bit;
		if (sw > owner.mainImg.getWidth() * rate + 20)
			sw = owner.mainImg.getWidth() * rate + 20;
		if (sh > owner.mainImg.getHeight() * rate + 20)
			sh = owner.mainImg.getHeight() * rate + 20;

		scrollpane.setBounds(((size.width - 160) - sw) / 2, ((size.height - owner.getInsets().top) - sh) / 2, sw, sh);
		owner.setVisible(true);
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}
}
