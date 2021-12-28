package hyperzebra.subsystem.resedit;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import hyperzebra.Rsrc;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.gui.field.MultiLineCellRenderer;
import hyperzebra.object.OObject;

public class OtherTypeEditor extends ResTypeEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// IconButton[] selectedButton;

	public OtherTypeEditor(PCARDFrame pc, String type, OObject object) {
		super(pc, type, object);

		// contpane.setLayout(new FlowLayout(FlowLayout.LEFT));
		// contpane.addMouseListener(new IconBackListener());

		scrollpane.getVerticalScrollBar().setUnitIncrement(133);

		open(pcard, 0);

		// new DropTarget(this, new IconDropListener());

		toFront();
		setVisible(true);
	}

	@Override
	public void open(PCARDFrame pc, int in_scroll) {
		super.open(pc, in_scroll);

		// selectedButton = new IconButton[selectedId.length];
		scroll = in_scroll;

		int number = rsrcAry.length;
		setTitle(type + "(" + number + ")");

		// テーブルを用意
		String[][] tabledata = new String[number][3];

		for (int i = 0; i < number; i++) {
			Rsrc.rsrcClass rsrc = rsrcAry[i];
			tabledata[i][0] = Integer.toString(rsrc.id);
			tabledata[i][1] = rsrc.name;
			tabledata[i][2] = Long
					.toString(new File(pc.stack.file.getParent() + File.separatorChar + rsrc.filename).length());
		}

		String[] columnNames = { "ID", "Name", "Size" };

		JTable table = new JTable(tabledata, columnNames);
		table.setEnabled(false);
		table.setDefaultRenderer(Object.class, new MultiLineCellRenderer());

		// contpane.setPreferredSize(new Dimension(contpane.getPreferredSize().width,
		// 133*((number+3)/4)));
		scrollpane.add(table);
		scrollpane.setViewportView(table);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				scrollpane.getVerticalScrollBar().setValue(scroll);
				scrollpane.setPreferredSize(new Dimension(scrollpane.getPreferredSize().width, 1024));
				scrollpane.setVisible(false);
				scrollpane.setVisible(true);
			}
		});
	}
}
