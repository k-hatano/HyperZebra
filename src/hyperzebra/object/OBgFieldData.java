package hyperzebra.object;

import java.util.ArrayList;

import hyperzebra.type.styleClass;

public class OBgFieldData {

	OCardBase card;
	OField fld;
	public String text;
	public int id;
	ArrayList<styleClass> styleList;

	public OBgFieldData(OCardBase cd, String data, int fldId) {
		card = cd;
		text = "";

		readFieldData(data);
		id = fldId;
	}

	public OBgFieldData(OCardBase cd, int fldId) {
		card = cd;
		text = "";

		id = fldId;
	}

	public void readFieldData(String indata) {
		String[] data = indata.split("\n");
		int isText = 0;

		for (int line = 0; line < data.length; line++) {
			String str = data[line];

			if (str.length() >= 2 && str.charAt(0) == '#' && str.charAt(1) != '#') {
				isText = 0;
				String istr;

				istr = "#text:";
				if (str.startsWith(istr)) {
					str = str.substring(istr.length());
					isText = 1;
				}
			}

			if (isText >= 1) {
				if (isText == 2)
					text += "\r\n";
				text += str;
				isText = 2;
			}
		}
	}
}
