package hyperzebra.subsystem.scripteditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import hyperzebra.TTalk;
import hyperzebra.gui.PCARD;
import hyperzebra.object.OObject;

public class ScriptArea extends JTextPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	OObject object;
	//
	boolean saved = true;
	// UndoManager undo = null;
	ScriptEditor parent = null;
	// String ParentType;
	// int ParentId;
	// String ObjType;
	// int ObjId;
	seListener seListener = null;

	public ScriptArea(ScriptEditor se, String text) {
		super();
		parent = se;
		// undo = new UndoManager();
		undobuf.add(text);

		setFont(new Font(PCARD.scriptFont, 0, PCARD.scriptFontSize));

		StyleContext scontext = new StyleContext();
		DefaultStyledDocument doc = new DefaultStyledDocument(scontext);
		setDocument(doc);

		try {
			doc.insertString(0, text, scontext.getStyle(StyleContext.DEFAULT_STYLE));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		// getDocument().addUndoableEditListener(undo);
		// getDocument().addUndoableEditListener(new UndoableEditListener() {
		// public void undoableEditHappened(UndoableEditEvent e) {
		// 行われた編集(文字の追加や削除)をUndoManagerに登録
		/*
		 * if (checkThreadCnt==0) { sub.addEdit(e.getEdit()); sub.end();
		 * undo.addEdit(sub); sub = new UndoManager(); sub.setLimit(10000); } else{
		 * sub.addEdit(e.getEdit()); }
		 */

		/*
		 * http://webcache.googleusercontent.com/search?q=cache:fm8Klx9_kTAJ:gline.zapto
		 * .org/log/read.php/tech/1227234261/101-200+UndoableEditEvent+色の変更は&cd=2&hl=ja&
		 * ct=clnk&gl=jp&client=safari&source=www.google.co.jp
		 * javax.swing.undoではまったのでメモ。 例えば、文字列の置換のような削除、挿入という複数の処理を１回で元に戻したい場合は
		 * UndoManagerを入れ子にする。
		 * 
		 * UndoManagerを２つ用意して、基本はサブに追加する。一塊の処理が終わったらend()を呼んでメインに追加する。
		 * 
		 * void undoableEditHappened(UndoableEditEvent e) { sub.addEdit(e.getEdit()); if
		 * (!compound) { sub.end(); main.addEdit(sub); sub = new UndoManager(); } }
		 */
		// }
		// });

		addFocusListener(new SAFocusListener());
		seListener = new seListener(this);
		getDocument().addDocumentListener(seListener);
	}

	static boolean flag;

	void checkIndentCall() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (flag == false) {
					flag = true;
					checkIndent();
					new checkWordsThread().start();
					flag = false;
				}
			}
		});
	}

	static int checkThreadCnt = 0;

	class checkWordsThread extends Thread {
		public void run() {
			if (checkThreadCnt > 0)
				return;
			checkThreadCnt++;
			setPriority(Thread.MIN_PRIORITY);
			checkWords();
			checkThreadCnt--;
		}
	}

	void checkIndent() {
		String script = getText();

		int selectedStartLine = (script.substring(0, getSelectionStart()) + " ").split("\n").length - 1;
		int selectedEndLine = (script.substring(0, getSelectionEnd()) + " ").split("\n").length - 1;
		int selectedStartChar = getSelectionStart() - script.substring(0, getSelectionStart()).lastIndexOf("\n") - 1;
		int selectedEndChar = getSelectionEnd() - script.substring(0, getSelectionEnd()).lastIndexOf("\n") - 1;

		// タブ文字を消す
		if (script.indexOf("\t") >= 0) {
			script = script.substring(0, script.indexOf("\t"))
					+ script.substring(script.indexOf("\t") + 1, script.length());
			selectedStartChar--;
			selectedEndChar--;
		}

		// インデントを解析
		String newScript = "";
		String[] scrAry = (script + " ").split("\n");
		ArrayList<String> nestAry = new ArrayList<String>();
		int indent = 0;
		int nextIndent = 0;
		int i;
		String[] lastwords = null;
		for (i = 0; i < scrAry.length; i++) {
			int spacing = 0;
			while (spacing < scrAry[i].length()
					&& (scrAry[i].charAt(spacing) == ' ' || scrAry[i].charAt(spacing) == '　'))
				spacing++;

			ArrayList<String> stringList = new ArrayList<String>();
			ArrayList<TTalk.wordType> typeList = new ArrayList<TTalk.wordType>();
			checkWordsLine(scrAry[i], stringList, typeList, true, false);
			for (int j = 0; j < typeList.size(); j++) {
				if (typeList.get(j) == TTalk.wordType.COMMENT) {
					typeList.remove(j);
					stringList.remove(j);
					break;
				}
			}
			if (stringList.size() == 0)
				stringList.add("");
			String[] words = new String[stringList.size()];
			stringList.toArray(words);

			// String[] words = scrAry[i].substring(spacing).split(" ");
			/*
			 * for(int j=0; j<words.length; j++){ if(words[j].indexOf("--")==0){ words[j] =
			 * ""; if(j-1 >= 0) words[words.length-1] = words[j-1]; else
			 * words[words.length-1] = ""; break; } else if(words[j].indexOf("--")>=0){
			 * words[j] = words[j].substring(0,words[j].indexOf("--")); if(j-1 >= 0)
			 * words[words.length-1] = words[j-1]; else words[words.length-1] = ""; break; }
			 * }
			 */

			if (words[0].equalsIgnoreCase("on") && words.length >= 2) {
				if (indent == 0) {
					nextIndent = 1;
					if (words[1].length() > 0)
						nestAry.add("on " + words[1]);
					else if (words.length >= 3 && words[2].length() > 0)
						nestAry.add("on " + words[2]);
				} else
					break;
			}
			if (words[0].equalsIgnoreCase("function") && words.length >= 2) {
				if (indent == 0) {
					nextIndent = 1;
					if (words[1].length() > 0)
						nestAry.add("on " + words[1]);
					else if (words.length >= 3 && words[2].length() > 0)
						nestAry.add("on " + words[2]);
				} else
					break;
			}
			if (words[0].equalsIgnoreCase("repeat")) {
				if (indent >= 1) {
					nextIndent = indent + 1;
					nestAry.add("repeat");
				} else
					break;
			}
			if (words[0].equalsIgnoreCase("else")) {
				if (lastwords[0].equalsIgnoreCase("if") && !lastwords[lastwords.length - 1].equalsIgnoreCase("then")
						|| lastwords[0].equalsIgnoreCase("else") && lastwords.length >= 2
								&& lastwords[1].equalsIgnoreCase("if")) {
					//
				} else if (nestAry.get(nestAry.size() - 1).equalsIgnoreCase("then")) {
					indent -= 1;
					nextIndent -= 1;
					nestAry.remove(nestAry.size() - 1);
				}
				// else break;
			}
			if (words[words.length - 1].equalsIgnoreCase("then") || words[words.length - 1].equalsIgnoreCase("else")) {
				if (indent >= 1) {
					nextIndent = indent + 1;
					nestAry.add("then");
				} else
					break;
			}
			if (words[0].equalsIgnoreCase("end")) {
				if (words[1].equalsIgnoreCase("repeat")) {
					if (nestAry.get(nestAry.size() - 1).equalsIgnoreCase("repeat")) {
						indent -= 1;
						nextIndent -= 1;
						nestAry.remove(nestAry.size() - 1);
					} else
						break;
				} else if (words[1].equalsIgnoreCase("if")) {
					if (nestAry.get(nestAry.size() - 1).equalsIgnoreCase("then")) {
						indent -= 1;
						nextIndent -= 1;
						nestAry.remove(nestAry.size() - 1);
					} else
						break;
				} else if (words.length >= 2 && nestAry.size() >= 1
						&& nestAry.get(nestAry.size() - 1).equalsIgnoreCase("on " + words[1])) {
					indent -= 1;
					nextIndent -= 1;
					nestAry.remove(nestAry.size() - 1);
				} else if (words[1].length() == 0 && words.length >= 3
						&& nestAry.get(nestAry.size() - 1).equalsIgnoreCase("on " + words[2])) {
					indent -= 1;
					nextIndent -= 1;
					nestAry.remove(nestAry.size() - 1);
				} else
					break;
			}

			lastwords = words;

			for (int j = 0; j < indent; j++) {
				newScript += "  ";
			}
			indent = nextIndent;

			newScript += scrAry[i].substring(spacing);
			if (i < scrAry.length - 1)
				newScript += "\n";
		}

		for (; i < scrAry.length; i++) {
			int spacing = 0;
			while (spacing < scrAry[i].length()
					&& (scrAry[i].charAt(spacing) == ' ' || scrAry[i].charAt(spacing) == '　'))
				spacing++;

			for (int j = 0; j < indent; j++) {
				newScript += "  ";
			}

			newScript += scrAry[i].substring(spacing);
			if (i < scrAry.length - 1)
				newScript += "\n";
		}
		if (newScript.substring(newScript.length()).equals(" ")) {
			newScript = newScript.substring(0, newScript.length() - 1);
		}

		{
			String[] newAry = (newScript + " ").split("\n");
			int selStart = 0;
			int j = 0;
			for (; j < newAry.length; j++) {
				if (selectedStartLine == j)
					break;
				selStart += newAry[j].length() + 1;
			}
			if (j < newAry.length && newAry[j].matches("^[ ]*$")) {
				selStart += newAry[j].length();
			} else {
				selStart += selectedStartChar;
			}

			int selEnd = 0;
			for (j = 0; j < newAry.length; j++) {
				if (selectedEndLine == j)
					break;
				selEnd += newAry[j].length() + 1;
			}
			selEnd += selectedEndChar;

			// テキストを設定
			getDocument().removeDocumentListener(seListener);
			setText(newScript);
			getDocument().addDocumentListener(seListener);

			setSelectionStart(selStart);
			setSelectionEnd(selEnd);
		}
	}

	@SuppressWarnings("unchecked")
	void checkWords() {
		String text = getText();
		text = text.replace('\r', '\n');
		text = text.toLowerCase();
		String[] scriptAry = text.split("\n");

		ArrayList<String>[] stringList = new ArrayList[scriptAry.length];
		ArrayList<TTalk.wordType>[] typeList = new ArrayList[scriptAry.length];
		StringBuilder handler = new StringBuilder("");
		TreeSet<String> varSet = new TreeSet<String>();

		// 行の連結
		for (int i = scriptAry.length - 2; i >= 0; i--) {
			if (scriptAry[i].length() == 0)
				continue;
			char c = scriptAry[i].charAt(scriptAry[i].length() - 1);
			if (c == '~' || c == 'ﾂ') {
				scriptAry[i] = scriptAry[i].substring(0, scriptAry[i].length() - 1) + scriptAry[i + 1];
				scriptAry[i + 1] = "";
			}
		}

		// 全ての行を解析
		for (int i = 0; i < scriptAry.length; i++) {
			stringList[i] = new ArrayList<String>();
			typeList[i] = new ArrayList<TTalk.wordType>();
			checkWordsLine(scriptAry[i], stringList[i], typeList[i], true, true);
			checkWordsLine2(scriptAry[i], stringList[i], typeList[i], true, handler, varSet);
		}

		getDocument().removeDocumentListener(seListener);

		// 文字に色を付ける
		int start = 0;
		for (int i = 0; i < scriptAry.length; i++) {
			MutableAttributeSet attr = new SimpleAttributeSet();
			for (int j = 0; j < stringList[i].size(); j++) {
				if (typeList[i].get(j) == TTalk.wordType.STRING || typeList[i].get(j) == TTalk.wordType.CONST) {
					StyleConstants.setForeground(attr, new Color(0, 0, 16));
				} else if (typeList[i].get(j) == TTalk.wordType.COMMENT) {
					StyleConstants.setForeground(attr, new Color(0, 96, 0));
				} else if (typeList[i].get(j) == TTalk.wordType.XCMD || typeList[i].get(j) == TTalk.wordType.XFCN) {
					StyleConstants.setForeground(attr, new Color(64, 0, 128));
				} else if (typeList[i].get(j) == TTalk.wordType.CMD || typeList[i].get(j) == TTalk.wordType.CMD_SUB
						|| typeList[i].get(j) == TTalk.wordType.GLOBAL) {
					StyleConstants.setForeground(attr, new Color(64, 0, 32));
				} else if (typeList[i].get(j) == TTalk.wordType.FUNC || typeList[i].get(j) == TTalk.wordType.OF_FUNC
						|| typeList[i].get(j) == TTalk.wordType.THE_FUNC) {
					StyleConstants.setForeground(attr, new Color(16, 0, 8));
				} else if (typeList[i].get(j) == TTalk.wordType.VARIABLE) {
					StyleConstants.setForeground(attr, new Color(0, 64, 128));
				} else if (typeList[i].get(j) == TTalk.wordType.OBJECT || typeList[i].get(j) == TTalk.wordType.OF_OBJ) {
					StyleConstants.setForeground(attr, new Color(16, 0, 0));
				} else if (typeList[i].get(j) == TTalk.wordType.CHUNK
						|| typeList[i].get(j) == TTalk.wordType.OF_CHUNK) {
					StyleConstants.setForeground(attr, new Color(0, 0, 0));
				} else if (typeList[i].get(j) == TTalk.wordType.X) {
					StyleConstants.setForeground(attr, new Color(16, 16, 16));
				} else if (typeList[i].get(j) == TTalk.wordType.IF || typeList[i].get(j) == TTalk.wordType.THEN
						|| typeList[i].get(j) == TTalk.wordType.ELSE || typeList[i].get(j) == TTalk.wordType.ENDIF) {
					StyleConstants.setForeground(attr, new Color(128, 32, 0));
				} else if (typeList[i].get(j) == TTalk.wordType.ON_HAND || typeList[i].get(j) == TTalk.wordType.END_HAND
						|| typeList[i].get(j) == TTalk.wordType.ON_FUNC || typeList[i].get(j) == TTalk.wordType.EXIT
						|| typeList[i].get(j) == TTalk.wordType.PASS || typeList[i].get(j) == TTalk.wordType.RETURN) {
					StyleConstants.setForeground(attr, new Color(128, 0, 0));
				} else if (typeList[i].get(j) == TTalk.wordType.REPEAT
						|| typeList[i].get(j) == TTalk.wordType.END_REPEAT
						|| typeList[i].get(j) == TTalk.wordType.EXIT_REP
						|| typeList[i].get(j) == TTalk.wordType.NEXT_REP) {
					StyleConstants.setForeground(attr, new Color(128, 64, 0));
				} else if (typeList[i].get(j) == TTalk.wordType.PROPERTY
						|| typeList[i].get(j) == TTalk.wordType.OF_PROP) {
					StyleConstants.setForeground(attr, new Color(0, 32, 0));
				} else {
					StyleConstants.setForeground(attr, new Color(0, 0, 0));
				}
				if (start < 0)
					start = 0;
				if (start >= text.length())
					start = text.length() - 10;

				int offset = text.substring(start).indexOf(stringList[i].get(j));
				if (offset > 0 && offset <= 64)
					start += offset;
				int len = stringList[i].get(j).length();
				if (start + len >= getDocument().getLength())
					len = 0;
				((DefaultStyledDocument) getDocument()).setCharacterAttributes(start, len, attr, false);
				start += len;
				// System.out.println(stringList[i].get(j)+" "+len+":"+start);
			}
		}

		getDocument().addDocumentListener(seListener);
	}

	public static void checkWordsLine(String script, ArrayList<String> stringList, ArrayList<TTalk.wordType> typeList,
			boolean isCmd, boolean isEditor) {
		StringBuilder str = new StringBuilder(16);
		boolean inFunc = false;
		ArrayList<TTalk.wordType> Brackets = new ArrayList<TTalk.wordType>();

		// 単語分割する。演算子、括弧、コメント、文字列の分別。
		for (int i = 0; i < script.length(); i++) {
			char code = script.charAt(i);
			if (code == '+' || code == '-' || code == '*' || code == '/' || code == '^' || code == '&' || code == '='
					|| code == '<' || code == '>' || code == '≠' || code == '≤' || code == '≥') {
				if (code == '-' && i > 0 && script.codePointAt(i - 1) == '-') {
					// コメント
					typeList.remove(typeList.size() - 1);
					stringList.remove(stringList.size() - 1);
					// 行の終わりまでコメント
					typeList.add(TTalk.wordType.COMMENT);
					stringList.add(script.substring(i - 1));
					break;
				}
				if (str.length() > 0) {
					if (Character.isDigit(str.charAt(0)))
						typeList.add(TTalk.wordType.STRING);
					else
						typeList.add(TTalk.wordType.X);
					stringList.add(str.toString().toLowerCase().intern());
					str.setLength(0);
				}
				typeList.add(TTalk.wordType.OPERATOR);
				stringList.add(String.valueOf((char) code).intern());
			} else if (code == '(') {
				if (str.length() > 0 || typeList.size() > 0 && typeList.get(typeList.size() - 1) == TTalk.wordType.X) {
					if (str.length() > 0) {
						if (Character.isDigit(str.charAt(0)))
							typeList.add(TTalk.wordType.STRING);
						else
							typeList.add(TTalk.wordType.X);
						stringList.add(str.toString().toLowerCase().intern());
						str.setLength(0);
					}
					String funcstr = stringList.get(stringList.size() - 1);
					if (funcstr == "cd" || funcstr == "card" || funcstr == "bg" || funcstr == "bkgnd"
							|| funcstr == "background" || funcstr == "btn" || funcstr == "button" || funcstr == "fld"
							|| funcstr == "field" || funcstr == "stack" || funcstr == "char" || funcstr == "character"
							|| funcstr == "item" || funcstr == "word" || funcstr == "line" || funcstr == "window"
							|| funcstr == "menu" || funcstr == "id" || funcstr == "or" || funcstr == "and"
							|| funcstr == "not" || funcstr == "div" || funcstr == "mod" || funcstr == "is"
							|| funcstr == "in" || funcstr == "within" || funcstr == "a" || funcstr == "an") {
						typeList.add(TTalk.wordType.LBRACKET);
					} else if (funcstr == "of" && stringList.size() >= 2 && (
					/*
					 * stringList.get(stringList.size()-2).matches("^[0-9]*$") ||
					 * stringList.get(stringList.size()-2)=="char" ||
					 * stringList.get(stringList.size()-2)=="character" ||
					 * stringList.get(stringList.size()-2)=="item" ||
					 * stringList.get(stringList.size()-2)=="word" ||
					 * stringList.get(stringList.size()-2)=="line" ||
					 */
					!TTalk.funcSet.contains(stringList.get(stringList.size() - 2)))) {
						typeList.add(TTalk.wordType.LBRACKET);
						Brackets.add(TTalk.wordType.LBRACKET);
						inFunc = false;
					} else if (isCmd && typeList.size() == 1) {
						// 左側がコマンドとして認識される場合
						typeList.add(TTalk.wordType.LBRACKET);
						Brackets.add(TTalk.wordType.LBRACKET);
						inFunc = false;
					} else {
						typeList.add(TTalk.wordType.LFUNC);
						Brackets.add(TTalk.wordType.LFUNC);
						inFunc = true;
					}
					stringList.add("(");
				} else {
					typeList.add(TTalk.wordType.LBRACKET);
					stringList.add("(");
				}
			} else if (code == ')') {
				if (str.length() > 0) {
					if (Character.isDigit(str.charAt(0)))
						typeList.add(TTalk.wordType.STRING);
					else
						typeList.add(TTalk.wordType.X);
					stringList.add(str.toString().toLowerCase().intern());
					str.setLength(0);
				}
				if (Brackets.size() == 0 || Brackets.get(Brackets.size() - 1) == TTalk.wordType.LBRACKET) {
					typeList.add(TTalk.wordType.RBRACKET);
					if (Brackets.size() >= 1)
						Brackets.remove(Brackets.size() - 1);
				} else if (Brackets.get(Brackets.size() - 1) == TTalk.wordType.LFUNC) {
					typeList.add(TTalk.wordType.RFUNC);
					if (Brackets.size() >= 1)
						Brackets.remove(Brackets.size() - 1);
				}
				if (Brackets.size() == 0 || Brackets.get(Brackets.size() - 1) == TTalk.wordType.LBRACKET) {
					inFunc = false;
				} else {
					inFunc = true;
				}
				stringList.add(")");
			} else if (code == ',') {
				if (str.length() > 0) {
					if (Character.isDigit(str.charAt(0)))
						typeList.add(TTalk.wordType.STRING);
					else
						typeList.add(TTalk.wordType.X);
					stringList.add(str.toString().toLowerCase().intern());
					str.setLength(0);
				}
				if (inFunc) {
					typeList.add(TTalk.wordType.COMMA_FUNC);// 関数の引数指定
				} else {
					typeList.add(TTalk.wordType.COMMA);// loc/rectあるいはglobal、はたまたハンドラの引数
				}
				stringList.add(",");
			} else if (code == '"') {
				if (str.length() > 0) {
					typeList.add(TTalk.wordType.X);
					stringList.add(str.toString().toLowerCase().intern());
					str.setLength(0);
				}
				i++;
				while (i < script.length()) {
					code = script.charAt(i);
					if (code == '"')
						break;
					str.append(code);
					i++;
				}
				if (isEditor) {
					typeList.add(TTalk.wordType.QUOTE);
					stringList.add("\"");
				}

				typeList.add(TTalk.wordType.STRING);
				stringList.add(str.toString());
				str.setLength(0);

				if (isEditor) {
					typeList.add(TTalk.wordType.QUOTE);
					stringList.add("\"");
				}
			} else if (code == ' ' || code == '\t') {
				if (str.length() > 0) {
					if (Character.isDigit(str.charAt(0)))
						typeList.add(TTalk.wordType.STRING);
					else
						typeList.add(TTalk.wordType.X);
					stringList.add(str.toString().toLowerCase().intern());
					str.setLength(0);
				}
			} else if (i == script.length() - 1) {
				str.append(code);
				if (str.length() > 0) {
					if (Character.isDigit(str.charAt(0)))
						typeList.add(TTalk.wordType.STRING);
					else
						typeList.add(TTalk.wordType.X);
					stringList.add(str.toString().toLowerCase().intern());
					str.setLength(0);
				}
			} else {
				str.append(code);
			}
		}
	}

	public static void checkWordsLine2(String script, ArrayList<String> stringList, ArrayList<TTalk.wordType> typeList,
			boolean isCmd, StringBuilder handler, TreeSet<String> varSet) {
		String command = "";

		// 演算子を特定
		for (int i = 0; i < typeList.size(); i++) {
			TTalk.wordType theType = typeList.get(i);

			if ((theType == TTalk.wordType.X || theType == TTalk.wordType.OPERATOR)
					&& TTalk.operatorSet.contains(stringList.get(i)/* .toLowerCase() */)) {
				if (stringList.get(i) == "div")
					typeList.set(i, TTalk.wordType.OPERATOR);
				else if (stringList.get(i) == "mod")
					typeList.set(i, TTalk.wordType.OPERATOR);
				else if (stringList.get(i) == "not")
					typeList.set(i, TTalk.wordType.OPERATOR);
				else if (stringList.get(i) == "and")
					typeList.set(i, TTalk.wordType.OPERATOR);
				else if (stringList.get(i) == "or")
					typeList.set(i, TTalk.wordType.OPERATOR);
				else if (stringList.get(i) == "contains")
					typeList.set(i, TTalk.wordType.OPERATOR);
				else if (stringList.get(i) == "within")
					typeList.set(i, TTalk.wordType.OPERATOR);
				else if (stringList.get(i) == "there") {
					if (i <= typeList.size() - 1 && stringList.get(i + 1) == "is") {
						if (i <= typeList.size() - 2
								&& (stringList.get(i + 2) == "a" || stringList.get(i + 2) == "an")) {
							typeList.set(i, TTalk.wordType.OPERATOR);
							typeList.set(i + 1, TTalk.wordType.OPERATOR_SUB);
							typeList.set(i + 2, TTalk.wordType.OPERATOR_SUB);
						} else if (i <= typeList.size() - 3 && 0 == stringList.get(i + 2).compareToIgnoreCase("not")) {
							if (0 == stringList.get(i + 3).compareToIgnoreCase("a")
									|| 0 == stringList.get(i + 3).compareToIgnoreCase("an")) {
								typeList.set(i, TTalk.wordType.OPERATOR);
								typeList.set(i + 1, TTalk.wordType.OPERATOR_SUB);
								typeList.set(i + 2, TTalk.wordType.OPERATOR_SUB);
								typeList.set(i + 3, TTalk.wordType.OPERATOR_SUB);
							}
							// else throw new xTalkException("there is notの後にa/anが必要です");
						}
						// else throw new xTalkException("there isの後にa/anが必要です");
					}
				} else if (stringList.get(i) == "is") {
					if (i < stringList.size() - 1 && (stringList.get(i + 1) == "in" || stringList.get(i + 1) == "a"
							|| stringList.get(i + 1) == "an")) {
						typeList.set(i, TTalk.wordType.OPERATOR);
						typeList.set(i + 1, TTalk.wordType.OPERATOR_SUB);
					} else if (i <= typeList.size() - 2 && stringList.get(i + 1) == "not") {
						if (0 == stringList.get(i + 2).compareToIgnoreCase("in")
								|| 0 == stringList.get(i + 2).compareToIgnoreCase("a")
								|| 0 == stringList.get(i + 2).compareToIgnoreCase("an")) {
							typeList.set(i, TTalk.wordType.OPERATOR);
							typeList.set(i + 1, TTalk.wordType.OPERATOR_SUB);
							typeList.set(i + 2, TTalk.wordType.OPERATOR_SUB);
						} else { // is not
							typeList.set(i, TTalk.wordType.OPERATOR);
							typeList.set(i + 1, TTalk.wordType.OPERATOR_SUB);
						}
					} else
						typeList.set(i, TTalk.wordType.OPERATOR);
				} else if (stringList.get(i) == "<") {
					if (i < typeList.size() - 1 && (stringList.get(i + 1) == "=" || stringList.get(i + 1) == ">")) {
						typeList.set(i, TTalk.wordType.OPERATOR);
						typeList.set(i + 1, TTalk.wordType.OPERATOR_SUB);
					}
				} else if (stringList.get(i) == ">") {
					if (i < typeList.size() - 1 && stringList.get(i + 1) == "=") {
						typeList.set(i, TTalk.wordType.OPERATOR);
						typeList.set(i + 1, TTalk.wordType.OPERATOR_SUB);
					}
				}
			}
			/*
			 * else if(theType==wordType.LFUNC) { if(i>=start+1 &&
			 * (typeAry[i-1]==wordType.OPERATOR || stringList.get(i-1)=="to"))
			 * typeAry[i]=wordType.LBRACKET; }
			 */
		}

		// 各単語の分別
		for (int i = 0; i < typeList.size(); i++) {
			TTalk.wordType type = typeList.get(i);
			if (type == TTalk.wordType.X) {
				String str = stringList.get(i);
				if (i == 0 && isCmd) {
					// 行の最初の単語
					if (str == "repeat") {
						typeList.set(i, TTalk.wordType.REPEAT);
						command = "repeat";
						continue;
					} else if (str == "if") {
						typeList.set(i, TTalk.wordType.IF);
						continue;
					} else if (str == "else") {
						typeList.set(i, TTalk.wordType.ELSE);
						continue;
					} else if (str == "end") {
						if (typeList.size() > i + 1) {
							if (stringList.get(i + 1) == "if") {
								typeList.set(i, TTalk.wordType.ENDIF);
								i++;
								typeList.set(i, TTalk.wordType.ENDIF);
								continue;
							} else if (stringList.get(i + 1) == "repeat") {
								typeList.set(i, TTalk.wordType.END_REPEAT);
								i++;
								typeList.set(i, TTalk.wordType.END_REPEAT);
								continue;
							} else if (stringList.get(i + 1).equalsIgnoreCase(handler.toString())) {
								typeList.set(i, TTalk.wordType.END_HAND);
								i++;
								typeList.set(i, TTalk.wordType.END_HAND);
								varSet.clear();
								handler.delete(0, handler.length());
								continue;
							}
						}
					} else if (str == "on") {
						if (typeList.size() > i + 1) {
							typeList.set(i, TTalk.wordType.ON_HAND);
							i++;
							typeList.set(i, TTalk.wordType.ON_HAND);
							handler.append(stringList.get(i));
							continue;
						}
					} else if (str == "function") {
						if (typeList.size() > i + 1) {
							typeList.set(i, TTalk.wordType.ON_FUNC);
							i++;
							typeList.set(i, TTalk.wordType.ON_FUNC);
							handler.append(stringList.get(i));
							continue;
						}
					}
				}
				if ((i == 0 && isCmd) || i >= 1
						&& (typeList.get(i - 1) == TTalk.wordType.THEN || typeList.get(i - 1) == TTalk.wordType.ELSE)) {
					// コマンドをかけるところの最初の単語
					if (PCARD.pc.stack.rsrc != null && PCARD.pc.stack.rsrc.getxcmdId(str, "command") > 0) {
						typeList.set(i, TTalk.wordType.XCMD);
					} else if (str == "global") {
						typeList.set(i, TTalk.wordType.GLOBAL);
						continue;
					} else if (TTalk.commandSet.contains(str)) {
						typeList.set(i, TTalk.wordType.CMD);
						command = str;
						continue;
					} else if (str == "return") {
						typeList.set(i, TTalk.wordType.RETURN);
						continue;
					} else if (str == "pass") {
						if (typeList.size() > i + 1 && stringList.get(i + 1).equalsIgnoreCase(handler.toString())) {
							typeList.set(i, TTalk.wordType.PASS);
							i++;
							typeList.set(i, TTalk.wordType.PASS);
							continue;
						}
					} else if (str == "exit") {
						if (typeList.size() > i + 1 && stringList.get(i + 1).equalsIgnoreCase(handler.toString())) {
							typeList.set(i, TTalk.wordType.EXIT);
							i++;
							typeList.set(i, TTalk.wordType.EXIT);
							continue;
						} else if (typeList.size() > i + 2 && stringList.get(i + 1).equalsIgnoreCase("to")
								&& stringList.get(i + 2).equalsIgnoreCase("hypercard")) {
							typeList.set(i, TTalk.wordType.EXIT);
							i++;
							typeList.set(i, TTalk.wordType.EXIT);
							i++;
							typeList.set(i, TTalk.wordType.EXIT);
							continue;
						} else if (typeList.size() > i + 1 && stringList.get(i + 1).equalsIgnoreCase("repeat")) {
							typeList.set(i, TTalk.wordType.EXIT_REP);
							i++;
							typeList.set(i, TTalk.wordType.EXIT_REP);
							continue;
						}
					} else if (str == "next") {
						if (typeList.size() > i + 1 && stringList.get(i + 1).equalsIgnoreCase("repeat")) {
							typeList.set(i, TTalk.wordType.NEXT_REP);
							i++;
							typeList.set(i, TTalk.wordType.NEXT_REP);
							continue;
						}
					} else if (str == "if") {
						typeList.set(i, TTalk.wordType.IF);
						continue;
					} else if (str == "else") {
						typeList.set(i, TTalk.wordType.ELSE);
						continue;
					} else if (str == "then") {
						typeList.set(i, TTalk.wordType.THEN);
						continue;
					} else {
						typeList.set(i, TTalk.wordType.USER_CMD);
					}
				} else {
					// コマンドが書けないところ
					if (str == "else") {
						typeList.set(i, TTalk.wordType.ELSE);
						continue;
					} else if (str == "then") {
						typeList.set(i, TTalk.wordType.THEN);
						continue;
					} else if (PCARD.pc.stack.rsrc != null && PCARD.pc.stack.rsrc.getxcmdId(str, "function") > 0) {
						typeList.set(i, TTalk.wordType.XFCN);
						continue;
					} else if (typeList.get(0) == TTalk.wordType.ON_HAND || typeList.get(0) == TTalk.wordType.ON_FUNC) {
						// 引数
						typeList.set(i, TTalk.wordType.VARIABLE);
						varSet.add(str);
						continue;
					} else if (str == "it"
							|| (i > 0 && stringList.get(i - 1) != "the") && (varSet != null && varSet.contains(str))) {
						typeList.set(i, TTalk.wordType.VARIABLE);
						continue;
					} else if (TTalk.constantSet.contains(str)) {
						typeList.set(i, TTalk.wordType.CONST);
						continue;
					} else if (str == "of" && i >= 1) {
						if (typeList.get(i - 1) == TTalk.wordType.OBJECT) {
							typeList.set(i, TTalk.wordType.OF_OBJ);
							continue;
						} else if (typeList.get(i - 1) == TTalk.wordType.CHUNK) {
							typeList.set(i, TTalk.wordType.OF_CHUNK);
							continue;
						} else if (typeList.get(i - 1) == TTalk.wordType.FUNC) {
							typeList.set(i, TTalk.wordType.OF_FUNC);
							continue;
						} else if (typeList.get(i - 1) == TTalk.wordType.PROPERTY) {
							typeList.set(i, TTalk.wordType.OF_PROP);
							continue;
						}
						if (i >= 2) {
							if (typeList.get(i - 2) == TTalk.wordType.OBJECT) {
								typeList.set(i, TTalk.wordType.OF_OBJ);
								continue;
							} else if (typeList.get(i - 2) == TTalk.wordType.CHUNK) {
								typeList.set(i, TTalk.wordType.OF_CHUNK);
								continue;
							}
						}
					} else if (typeList.get(0) == TTalk.wordType.GLOBAL) {
						// グローバル変数
						typeList.set(i, TTalk.wordType.VARIABLE);
						varSet.add(str);
						continue;
					} else if (i >= 1 && typeList.get(i - 1) == TTalk.wordType.REPEAT
							&& (str == "with" || str == "until" || str == "while")) {
						typeList.set(i, TTalk.wordType.REPEAT);
						continue;
					} else if (i >= 1 && typeList.get(i - 1) == TTalk.wordType.REPEAT
							&& stringList.get(i - 1) == "with") {
						// repeat変数
						typeList.set(i, TTalk.wordType.VARIABLE);
						varSet.add(str);
						continue;
					} else if (command == "put" && (str == "into" || str == "after" || str == "before")) {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "add" && str == "to") {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "subtract" && str == "from") {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "divide" && str == "by") {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "multiply" && str == "by") {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "show" && str == "at") {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "answer" && (str == "with" || str == "or")) {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "ask" && str == "with") {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "set" && str == "to") {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "send" && str == "to") {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "visual" && str == "effect") {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "play" && str == "stop") {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "pop" && str == "card") {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "push" && str == "card") {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "drag" && (str == "from" || str == "to" || str == "with")) {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "click" && (str == "at" || str == "with")) {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if ((command == "lock" || command == "unlock") && (str == "screen" || str == "with"
							|| str == "messages" || str == "recent" || str == "errordialogs")) {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "wait" && (str == "until" || str == "while")) {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "convert" && (str == "to")) {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "open" && (str == "file")) {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "read"
							&& (str == "from" || str == "file" || str == "at" || str == "for" || str == "until")) {
						typeList.set(i, TTalk.wordType.CMD_SUB);
						continue;
					} else if (command == "repeat" && (str == "to" || str == "down")) {
						typeList.set(i, TTalk.wordType.REPEAT);
						continue;
					} else if (str == "cd" || str == "card" || str == "bg" || str == "bkgnd" || str == "background"
							|| str == "btn" || str == "button" || str == "fld" || str == "field" || str == "stack"
							|| str == "window" || str == "menu" || str == "menubar" || str == "titlebar" || str == "msg"
							|| str == "message" || str == "hypercard" || str == "me"
							|| (str == "id" && i + 1 < stringList.size() && stringList.get(i + 1) != "of")) {
						typeList.set(i, TTalk.wordType.OBJECT);
						continue;
					} else if (str == "char" || str == "character" || str == "item" || str == "word" || str == "line") {
						typeList.set(i, TTalk.wordType.CHUNK);
						continue;
					} else if (i >= 1 && typeList.get(i - 1) == TTalk.wordType.CMD_SUB
							&& (command == "put" || command == "add" || command == "subtract" || command == "divide"
									|| command == "multiply")) {
						// 変数
						typeList.set(i, TTalk.wordType.VARIABLE);
						varSet.add(str);
						continue;
					} else if (TTalk.propertySet.contains(str) || str == "long" || str == "short") {
						if ((i >= 1 && stringList.get(i - 1) == "the") || (i >= 1 && stringList.get(i - 1) == "set")
								|| (i + 1 < stringList.size() && stringList.get(i + 1) == "of")) {
							typeList.set(i, TTalk.wordType.PROPERTY);
							continue;
						}
					}
					if (TTalk.funcSet.contains(str)) {
						if (str == "number" && i + 2 < stringList.size() && stringList.get(i + 1) == "of"
								&& !stringList.get(i + 2).matches("s$")) {
							// number of xxsは関数だが、そうでない場合はプロパティ
							typeList.set(i, TTalk.wordType.PROPERTY);
							continue;
						} else if (str == "number" && i - 1 > 0 && stringList.get(i - 1) == "a") {
							typeList.set(i, TTalk.wordType.X);
							continue;
						} else {
							typeList.set(i, TTalk.wordType.FUNC);
							continue;
						}
					} else if (i + 1 < stringList.size() && stringList.get(i) != "of"
							&& typeList.get(i + 1) == TTalk.wordType.LFUNC) {
						typeList.set(i, TTalk.wordType.USER_FUNC);
						continue;
					}
				}
			}
		}

		for (int i = 0; i < typeList.size(); i++) {
			TTalk.wordType type = typeList.get(i);
			if (type == TTalk.wordType.X) {
				String str = stringList.get(i);
				if ((str == "first" || str == "last" || str == "prev" || str == "previous" || str == "next")
						&& i + 1 < stringList.size() && typeList.get(i + 1) == TTalk.wordType.OBJECT) {
					typeList.set(i, TTalk.wordType.OBJECT);
				}
				if ((str == "first" || str == "last" || str == "middle" || str == "any") && i + 1 < stringList.size()
						&& typeList.get(i + 1) == TTalk.wordType.CHUNK) {
					typeList.set(i, TTalk.wordType.CHUNK);
				}
			}
		}

		for (int i = 0; i < typeList.size(); i++) {
			TTalk.wordType type = typeList.get(i);
			if (type == TTalk.wordType.X) {
				String str = stringList.get(i);
				if (str == "the" && i + 1 < stringList.size() && typeList.get(i + 1) == TTalk.wordType.FUNC
						&& (i + 2 >= stringList.size() || stringList.get(i + 2) != "of")) {
					typeList.set(i, TTalk.wordType.THE_FUNC);
				}
				if (str == "the" && i + 1 < stringList.size() && typeList.get(i + 1) == TTalk.wordType.PROPERTY) {
					typeList.set(i, TTalk.wordType.PROPERTY);
				}
				if (str == "this" && i + 1 < stringList.size() && typeList.get(i + 1) == TTalk.wordType.OBJECT) {
					typeList.set(i, TTalk.wordType.OBJECT);
				}
			}
		}
	}

	boolean compareObject(OObject obj) {
		return (obj == this.object);
	}
	/*
	 * if(ObjType.equals(obj.objectType) && ObjId == obj.id){ if(obj.parent !=
	 * null){ if(ParentType.equals(obj.parent.objectType) && ParentId ==
	 * obj.parent.id){ return true; } }else if(obj.objectType.equals("stack")){
	 * return true;//スタックごとにエディタウィンドウを持つので必ず一致 } } return false; }
	 */

	// オブジェクトの親が一致するか
	/*
	 * boolean compareParent(OObject parent){ if(ParentType!=null &&
	 * ParentType.equals("background") && parent != null &&
	 * parent.objectType.equals("card")){ OCard cd = (OCard)parent; if(ParentId ==
	 * cd.bg.id){ return true; } } else { if(ParentType!=null &&
	 * ParentType.equals(parent.objectType) && ParentId == parent.id){ return true;
	 * } } return false; }
	 */

	void setObject(OObject obj) {
		this.object = obj;
	}
	/*
	 * ObjType = obj.objectType; ObjId = obj.id; if(obj.parent != null){ ParentType
	 * = obj.parent.objectType; ParentId = obj.parent.id; } }
	 */

	/*
	 * OObject getObject(OCard parent){ if(ObjType.equals("stack")) return
	 * parent.stack; if(ObjType.equals("background") && ObjId == parent.bgid) return
	 * parent.bg; if(ObjType.equals("card") && ObjId == parent.id) return parent;
	 * if(ParentType.equals("bg")){ OBackground bg = parent.bg; if(bg == null){ bg =
	 * PCARD.pc.stack.GetBackgroundbyId(ParentId); } if(ObjType.equals("field") &&
	 * ParentId == parent.bgid) return bg.GetFldbyId(ObjId);
	 * if(ObjType.equals("button") && ParentId == parent.bgid) return
	 * bg.GetBtnbyId(ObjId); }else{ if(ObjType.equals("field") && ParentId ==
	 * parent.id) return parent.GetFldbyId(ObjId); if(ObjType.equals("button") &&
	 * ParentId == parent.id) return parent.GetBtnbyId(ObjId); } return null; }
	 */

	/*
	 * OObject getObjectOtherCard(){ if(ParentType.equals("card")){ OCard card =
	 * PCARD.pc.stack.GetCardbyId(ParentId); return getObject(card); } else { OCard
	 * card = PCARD.pc.stack.GetCardofBackgroundbyId(ParentId); return
	 * getObject(card); } }
	 */

	void changedScript() {
		saved = false;
		parent.getRootPane().putClientProperty("windowModified", Boolean.TRUE);
		if (!getText().equals(undobuf.get(undobuf.size() - 1))) {
			undobuf.add(getText());
			if (undobuf.size() >= 2) {
				// 単語単位でundobufに登録する
				String str1 = undobuf.get(undobuf.size() - 2);
				String str2 = undobuf.get(undobuf.size() - 1);
				boolean wordflag = false;
				int i1 = 0, i2 = 0;
				for (int i = 0; i + i1 < str1.length() && i + i2 < str2.length(); i++) {
					if (str1.charAt(i + i1) == str2.charAt(i + i2))
						continue;
					if (/*
						 * str1.charAt(i+i1)=='\n' || str1.charAt(i+i1)==' ' ||
						 */
					str2.charAt(i + i2) == '\n' || str2.charAt(i + i2) == ' ') {
						wordflag = true;
					} else {
						if (str1.length() - i1 > str2.length())
							i1++;
						if (str1.length() < str2.length() - i2)
							i2++;
						if (str1.length() - i1 > str2.length() || str1.length() < str2.length() - i2)
							i--; // 無限ループ回避
					}
				}
				if (wordflag == false) {
					undobuf.remove(undobuf.size() - 2);
				}
			}
			if (undobuf.size() > 32) {
				undobuf.remove(0);
			}
			redobuf.clear();
			setCanUndo();
		}
	}

	void savedScript() {
		saved = true;
		parent.getRootPane().putClientProperty("windowModified", Boolean.FALSE);
	}

	void setCanUndo() {
		SEMenu.undoMenu.setEnabled(true);
	}

	ArrayList<String> undobuf = new ArrayList<String>();
	ArrayList<String> redobuf = new ArrayList<String>();

	void undoStatus() {
		// SEMenu.undoMenu.setEnabled(undo.canUndo());
		// SEMenu.redoMenu.setEnabled(undo.canRedo());
		SEMenu.undoMenu.setEnabled(undobuf.size() > 1);
		SEMenu.redoMenu.setEnabled(redobuf.size() > 0);
	}

	void undo() {
		if (undobuf.size() > 1) {
			String str1 = getText();
			String str2 = undobuf.get(undobuf.size() - 2);
			int pos = getCaretPosition() + str2.length() - str1.length();
			redobuf.add(str1);
			getDocument().removeDocumentListener(seListener);
			setText(str2);
			undobuf.remove(undobuf.size() - 1);
			getDocument().addDocumentListener(seListener);
			undoStatus();
			new checkWordsThread().start();
			if (pos >= 0 && pos < getText().length()) {
				setCaretPosition(pos);
			}
		}
	}

	void redo() {
		if (redobuf.size() > 0) {
			int pos = getCaretPosition();
			undobuf.add(getText());
			getDocument().removeDocumentListener(seListener);
			setText(redobuf.get(redobuf.size() - 1));
			redobuf.remove(redobuf.size() - 1);
			getDocument().addDocumentListener(seListener);
			undoStatus();
			new checkWordsThread().start();
			if (pos < getText().length()) {
				setCaretPosition(pos);
			}
		}
	}

	class SAFocusListener implements FocusListener {
		public void focusGained(FocusEvent e) {
			if (saved)
				savedScript();
			else
				changedScript();
			undoStatus();
		}

		public void focusLost(FocusEvent e) {
		}
	}
}
