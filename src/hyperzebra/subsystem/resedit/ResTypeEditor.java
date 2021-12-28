package hyperzebra.subsystem.resedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import hyperzebra.Rsrc;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.object.OButton;
import hyperzebra.object.OObject;
import hyperzebra.object.OStack;

public class ResTypeEditor extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ResTypeEditor editor;
	String type;
	OObject object;
	public PCARDFrame pcard;
	public JScrollPane scrollpane;
	JPanel contpane;
	public Rsrc.rsrcClass[] rsrcAry;
	public int selectedId[] = { 0 };
	int scroll;

	public ResTypeEditor(PCARDFrame pc, String type, OObject object) {
		super();
		editor = this;
		this.type = type;
		this.object = object;
		this.pcard = pc;

		if (pcard.stack.rsrc == null) {
			pcard.stack.rsrc = new Rsrc(pcard.stack);
		}

		int w = 560;
		int h = 480;

		// frame
		setTitle(type);
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				maybeExit();
			}
		});

		addComponentListener(new ResComponentListener());

		// scroll
		{
			scrollpane = new JScrollPane();
			scrollpane.setName("JScrollPane");
			int paneh = h;
			if (object != null) {
				paneh = h - 60;
			}
			scrollpane.setBounds(0, 0, w, paneh);
			scrollpane.setPreferredSize(new Dimension(w, paneh));
			scrollpane.getVerticalScrollBar().setValue(0);
			scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollpane.getVerticalScrollBar().setUnitIncrement(12);
			this.add("North", scrollpane);

			contpane = new JPanel();
			// contpane.setBounds(0, 0, w-20, paneh);
			contpane.setPreferredSize(new Dimension(w - 20, paneh));
			contpane.setOpaque(true);
			contpane.setBackground(new Color(219, 223, 230));
			scrollpane.setViewportView(contpane);
		}

		// アイコン選択ダイアログのときはok buttonを表示
		if (object != null) {
			JPanel okPanel = new JPanel();
			okPanel.setLayout(new FlowLayout());

			JButton jbtn = new JButton("OK");
			jbtn.addActionListener(new ResOkButtonListener());
			okPanel.add(jbtn);
			this.add("South", okPanel);
		}

		if (pc != null) {
			setBounds(pc.getX() + pc.getWidth() / 2 - w / 2, pc.getY() + pc.getHeight() / 2 - h / 2, w, h);
		} else {
			setBounds(0, 0, w, h);
		}

		// menu
		new REMenu(this);
	}

	@SuppressWarnings("unchecked")
	public void open(PCARDFrame pc, int in_scroll) {
		while (contpane.getComponentCount() > 0) {
			contpane.remove(contpane.getComponent(0));
		}

		if (object == null) {
			// リソースをソートしてリストに保存
			int number = pc.stack.rsrc.getRsrcCount(type);

			rsrcAry = new Rsrc.rsrcClass[number];
			for (int i = 0; i < number; i++) {
				Rsrc.rsrcClass rsrc = pc.stack.rsrc.getRsrcByIndex(type, i);
				rsrcAry[i] = rsrc;
			}
			Arrays.sort(rsrcAry, new DataComparator());
		} else {
			// リソースをソートしてリストに保存
			int number = pc.stack.rsrc.getRsrcCount(type);
			int numberall = pc.stack.rsrc.getRsrcCountAll(type);

			rsrcAry = new Rsrc.rsrcClass[numberall];
			int i = 0;
			for (i = 0; i < number; i++) {
				Rsrc.rsrcClass rsrc = pc.stack.rsrc.getRsrcByIndex(type, i);
				rsrcAry[i] = rsrc;
			}
			for (int j = pc.stack.usingStacks.size() - 1; j >= 0; j--) {
				OStack rsrcstack = pc.stack.usingStacks.get(j);
				Iterator<Rsrc.rsrcClass> it = rsrcstack.rsrc.rsrcIdMap.values().iterator();
				while (it.hasNext()) {
					Rsrc.rsrcClass rsrc = it.next();
					if (rsrc.type.equals(type)) {
						String path1 = (rsrcstack.file.getParent() + File.separatorChar + rsrc.filename);
						String path2 = pc.stack.rsrc.getFilePathAll(rsrc.id, type);
						if (path1.equals(path2)) {
							rsrcAry[i] = rsrc;
							i++;
						}
					}
				}
			}
			Arrays.sort(rsrcAry, new DataComparator());
		}
	}

	@SuppressWarnings("rawtypes")
	class DataComparator implements Comparator {
		@Override
		public int compare(Object o1, Object o2) {
			return ((Rsrc.rsrcClass) o1).id - ((Rsrc.rsrcClass) o2).id;
		}
	}

	class ResComponentListener implements ComponentListener {
		@Override
		public void componentHidden(ComponentEvent e) {
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentResized(ComponentEvent e) {
			int paneh = ((Component) e.getSource()).getHeight();
			if (object != null) {
				paneh -= 60;
			}
			scrollpane.setBounds(0, 0, ((Component) e.getSource()).getWidth(), paneh);
			int n = ((Component) e.getSource()).getWidth() / 133;
			if (n == 0)
				n = 1;
			int len = 0;
			if (rsrcAry != null) {
				len = rsrcAry.length;
			}
			contpane.setPreferredSize(
					new Dimension(((Component) e.getSource()).getWidth() - 20, 133 * ((len + n - 1) / n) + 20));
			contpane.setBounds(0, 0, ((Component) e.getSource()).getWidth() - 20, 133 * ((len + n - 1) / n) + 20);
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}
	}

	class ResOkButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (type.equals("icon") && object != null) {
				((OButton) object).setIcon(selectedId[0]);
			}
			editor.dispose();
		}
	}

	void maybeExit() {
		object = null;
		pcard = null;
		scrollpane = null;
		contpane = null;
		rsrcAry = null;
		selectedId = null;

		System.gc();
		editor.dispose();

		editor = null;
	}
}
