package hyperzebra.object;

public class OBgButtonData {

	OCardBase card;
	OButton btn;
	boolean check_hilite = false; // ハイライト情報をカードごとに持つ
	int id;

	public OBgButtonData(OCardBase cd, int btnId) {
		card = cd;

		id = btnId;
	}

	public OBgButtonData(OCardBase cd, String data, int btnId) {
		card = cd;

		readButtonData(data);
		id = btnId;
	}

	public void readButtonData(String indata) {
		String[] data = indata.split("\n");
		boolean hilite = false;

		for (int line = 0; line < data.length; line++) {
			String str = data[line];

			if (str.length() >= 2 && str.charAt(0) == '#' && str.charAt(1) != '#') {
				String istr;

				istr = "#hilite:";
				if (str.startsWith(istr)) {
					String tmpstr = str.substring(istr.length());
					hilite = (tmpstr.compareTo("true") == 0);
					check_hilite = hilite;
				}
			}
		}
	}
}
