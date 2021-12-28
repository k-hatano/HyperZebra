package hyperzebra.object;

import javax.swing.JMenu;

public class OMenu extends OObject {
	public JMenu menu;

	// メイン
	public OMenu(JMenu menu) {
		objectType = "menu";
		this.menu = menu;
	}
}