package hyperzebra.subsystem.debugger;

import java.awt.Rectangle;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import hyperzebra.TTalk;
import hyperzebra.gui.field.MultiLineCellRenderer;
import hyperzebra.object.OWindow;
import hyperzebra.type.ttalktype.MemoryData;

public class VariableWatcher extends JDialog {
	private static final long serialVersionUID = 3795782126509780013L;

	public static VariableWatcher watcherWindow;
	static private JScrollPane scrollpane;
	public static int rowSize;

	private String[] columnNames = { "Name", "Value" };

	public VariableWatcher() {
		super(/* PCARD.pc */);

		// オブジェクトをウィンドウリストに登録
		setTitle("VariableWatcher");
		new OWindow(this);
		watcherWindow = this;

		// テーブルを用意
		String[][] tabledata = { { "it", "" } };

		JTable table = new JTable(tabledata, columnNames);
		// table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setEnabled(false);
		table.setDefaultRenderer(Object.class, new MultiLineCellRenderer());

		// スクロール
		scrollpane = new JScrollPane(table);
		add(scrollpane);

		// ウィンドウ位置とサイズ設定
		setBounds(0, 0, 320, 240);
		setLocationRelativeTo(null);
	}

	public void setTable(MemoryData globalData, MemoryData memData) {
		int total = globalData.nameList.size();
		if (memData != null)
			total += memData.nameList.size();
		String[][] tabledata = new String[total][2];

		int i;
		for (i = 0; i < globalData.nameList.size(); i++) {
			tabledata[i][0] = "*" + globalData.nameList.get(i);
			tabledata[i][1] = globalData.valueList.get(i);
		}
		if (memData != null) {
			for (int j = 0; j < memData.nameList.size(); j++) {
				tabledata[i + j][0] = memData.nameList.get(j);
				tabledata[i + j][1] = memData.valueList.get(j);
			}
		}

		rowSize = TTalk.globalData.nameList.size();

		JTable table = new JTable(tabledata, columnNames);
		// table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setEnabled(false);
		table.setDefaultRenderer(Object.class, new MultiLineCellRenderer());

		Rectangle rect = scrollpane.getBounds();
		int scroll = scrollpane.getVerticalScrollBar().getValue();
		remove(scrollpane);
		scrollpane = new JScrollPane(table);
		add(scrollpane);
		scrollpane.getVerticalScrollBar().setValue(scroll);
		/*
		 * scrollpane.removeAll(); scrollpane.add(table);
		 * scrollpane.setViewportView(table);
		 */
		scrollpane.setBounds(rect);
		// repaint();
	}
}
