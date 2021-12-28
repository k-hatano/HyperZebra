package hyperzebra.gui.field;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
	private static final long serialVersionUID = -8300422827510636123L;
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	public MultiLineCellRenderer() {
		setLineWrap(true);
		setWrapStyleWord(true);
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (isSelected) {
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			super.setForeground(table.getForeground());
			Color bgcolor = table.getBackground();
			if (row % 2 == 1)
				bgcolor = new Color(Math.max(0, bgcolor.getRed() - 30), Math.max(0, bgcolor.getGreen() - 20),
						bgcolor.getBlue());
			super.setBackground(bgcolor);
		}

		setFont(table.getFont());

		if (hasFocus) {
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
		} else {
			setBorder(noFocusBorder);
		}

		setText((value == null) ? "" : value.toString());

		this.setBounds(0, 0, 128, 128);
		int h = this.getPreferredSize().height;
		table.setRowHeight(row, h);

		return this;
	}
}