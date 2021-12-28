package hyperzebra.object;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

import hyperzebra.TTalk;

public class OObject {
	public OObject parent = null;
	public String objectType = "";
	public int id;
	public String name = "";
	public String text = "";
	public boolean wrapFlag;
	public ArrayList<String> scriptList;
	public TreeSet<String> handlerList;
	public ArrayList<String>[] stringList;
	public ArrayList<TTalk.wordType>[] typeList;
	public ArrayList<Integer> handlerLineList;
	public boolean visible = true;
	public boolean enabled = true;

	public int left = 0;
	public int top = 0;
	public int width = 0;
	public int height = 0;

	public void clean() {
		parent = null;
		// objectType = null;
		scriptList = null;
		stringList = null;
		typeList = null;
		handlerLineList = null;
		handlerList = null;
	}

	public int getLeft() {
		return left;
	}

	public int getTop() {
		return top;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean getVisible() {
		return visible;
	}

	public String getText() {
		return text;
	}

	public String getShortShortName() {
		String sName = "";
		if (objectType.equals("stack"))
			sName += "stack";
		if (objectType.equals("background"))
			sName += "bg";
		if (objectType.equals("card"))
			sName += "cd";
		if (objectType.equals("button"))
			sName += "btn";
		if (objectType.equals("field"))
			sName += "fld";
		if (name.equals(""))
			sName += " id " + id;
		else if (name.length() > 9)
			sName += " \"" + name.substring(0, 8) + "...\"";
		else
			sName += " \"" + name + "\"";
		return sName;
	}

	public String getShortName() {
		String sName = "";
		if (!objectType.equals("card") && parent != null) {
			if (parent.objectType.equals("card") || parent.objectType.equals("background")) {
				sName += parent.CapitalType() + " ";
			}
		}
		if (name.equals(""))
			sName += CapitalType() + " id " + id;
		else
			sName += CapitalType() + " \"" + name + "\"";
		return sName;
	}

	public String getLongName() {
		if (objectType.equals("stack")) {
			// スタックの場合は Stack "スタックのパス"になる
			String path = ((OStack) this).file.getAbsolutePath();
			for (int i = 0; i < path.length(); i++) {
				if (path.charAt(i) == File.separatorChar)
					path = path.substring(0, i) + ":" + path.substring(i + 1);
			}
			return "Stack \"" + path + "\"";
		}
		if (parent != null)
			return getShortName() + " of " + parent.getShortName();
		return getShortName();
	}

	public String CapitalType() {
		return objectType.substring(0, 1).toUpperCase() + objectType.substring(1).toLowerCase();
	}

	public void setVisible(boolean in) {
		visible = in;
		if (parent != null && (parent.getClass() == OCard.class || parent.getClass() == OBackground.class)) {
			((OCardBase) parent).changed = true;
		}
	}

	public void setText(String in) {
		text = in;
	}

	public void setTextInternal(String in) {
		text = in;
	}

	@SuppressWarnings("unchecked")
	public void setScript(String str) {
		scriptList.clear();

		String[] strArray = str.split("\n");
		for (int i = 0; i < strArray.length; i++) {
			scriptList.add(strArray[i]);
		}

		stringList = new ArrayList[scriptList.size()];
		typeList = new ArrayList[scriptList.size()];
		handlerList = null;
		handlerLineList = null;
		if (getClass() == OStack.class) {
			((OStack) this).changed = true;
		} else if ((getClass() == OCard.class || getClass() == OBackground.class)) {
			((OCardBase) this).changed = true;
		} else if (parent != null && (parent.getClass() == OCard.class || parent.getClass() == OBackground.class)) {
			((OCardBase) parent).changed = true;
		}

		wrapFlag = false;
	}
}

//------------------------------------------
