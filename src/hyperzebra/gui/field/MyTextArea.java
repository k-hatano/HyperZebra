package hyperzebra.gui.field;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.SystemColor;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import hyperzebra.Rsrc;
import hyperzebra.gui.FieldGUI;
import hyperzebra.gui.PCARD;
import hyperzebra.object.OField;
import hyperzebra.object.OStack;
import hyperzebra.tool.AuthTool;
import hyperzebra.tool.PaintTool;

public class MyTextArea extends JTextArea {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public OField fldData = null;
	public JScrollPane pr_scrl;
	public int pr_selLine;// showLine xfcn用
	boolean smallfont;

	public MyTextArea(String str) {
		super(str);
		this.setDoubleBuffered(PCARD.useDoubleBuffer);
	}

	public int getLineHeight() {
		return getRowHeight();
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (!isVisible())
			return;
		if (PCARD.pc.bit > 1 && fldData.card != null)
			return;
		if (PaintTool.editBackground && fldData.parent != null && fldData.parent.objectType.equals("card"))
			return;
		if (fldData != null && fldData.card != null && fldData.card.objectType.equals("cd")
				&& fldData.card != PCARD.pc.stack.curCard)
			return;
		Graphics paneg = PCARD.pc.mainPane.getGraphics();
		if (PCARD.lockedScreen && paneg == g)
			return;
		// super.paintComponent(g);
		if (fldData != null) {
			// 通常フィールド
			if (fldData.fontPict2 != null) {
				if (fldData.style != 1) {
					g.setColor(fldData.bgColor);
					Rectangle r = g.getClipBounds();
					g.fillRect(r.x, r.y, r.width, r.height);
				}

				// ピクチャによる独自フォント
				int rsrcid = fldData.card.stack.rsrc.getRsrcIdAll(fldData.textFont, "font");
				rsrcid += fldData.textSize;// ベースID+テキストサイズ=ID
				Rsrc.rsrcClass fontrsrc = fldData.card.stack.rsrc.getResourceAll(rsrcid, "font");
				Rsrc.FontInfo fontinfo = (Rsrc.FontInfo) fontrsrc.optionInfo;

				if (fontinfo == null) {
					fontinfo = fldData.card.stack.rsrc.new FontInfo();
				}
				int h = 0, v = 0;
				int sz = fldData.textSize;
				char start = fontinfo.firstChar;
				char end = fontinfo.lastChar;
				for (int i = 0; i < getText().length(); i++) {
					char c = getText().charAt(i);
					if (c == '\r' || c == '\n') {
						v++;
						h = 0;
						if (c == '\r' && i + 1 < getText().length() && getText().charAt(i + 1) == '\n')
							i++;
					} else if (c >= start && c <= end) {
						// System.out.println("fontpict:h:"+h+" v:"+v+" "+fontinfo.locs[c-start]+" -
						// "+fontinfo.locs[c-start+1]);
						g.drawImage(fldData.fontPict2, sz * h, sz * v, sz * h + sz, sz * v + sz,
								fontinfo.locs[c - start], 0, fontinfo.locs[c - start + 1], 32, PCARD.pc.stack.pcard);
						h++;
					}
				}
				return;
			} else if (isFocusOwner() == false || fldData.useMyDraw) {
				if (fldData.style != 1) {
					g.setColor(fldData.bgColor);
					Rectangle r = g.getClipBounds();
					g.fillRect(r.x, r.y, r.width, r.height);
				}

				if (fldData.enabled && fldData.autoSelect && fldData.dontWrap) {
					// ライン選択
					if (fldData.selectedLine > 0) {
						g.setColor(SystemColor.textHighlight);
						g.fillRect(1, (fldData.selectedLine - 1) * fldData.textHeight, getWidth() - 1,
								fldData.textHeight);
					}
				}

				String textFont = fldData.textFont;
				int textStyle = fldData.textStyle;
				int textSize = fldData.textSize;
				if (smallfont && textSize > 6)
					textSize--;
				Color textColor = fldData.color;
				int nextStyle = 0;
				int nextTextPosition = -1;
				if (fldData.styleList != null && fldData.styleList.size() > nextStyle) {
					nextTextPosition = fldData.styleList.get(nextStyle).textPosition;
				}
				int textHeight = fldData.textHeight;
				if (textHeight < textSize)
					textHeight = textSize;
				StringBuilder readingStr = new StringBuilder();
				// int v=0;
				g.setFont(new Font(textFont, textStyle & 0x03, textSize));
				FontMetrics fo = g.getFontMetrics();
				int nihongo_i = 0;
				int lft = 0;
				int top = fo.getAscent();
				int fldWidth = fldData.width;
				if (fldData.style == 5)
					fldWidth -= 16;
				else if (fldData.style == 4)
					fldWidth -= 4;

				for (int i = 0; i <= getText().length(); i++) {
					char c = ' ';
					char nextc = ' ';
					if (i < getText().length()) {
						try {
							c = getText().charAt(i);
							if (i + 1 < getText().length()) {
								nextc = getText().charAt(i + 1);
							}
						} catch (StringIndexOutOfBoundsException e) {
						}
					} else {
						c = '\n';
					}

					if (c != '\r' && c != '\n') {
						readingStr.append(c);
					}
					boolean isStyleChange = false;
					if (nextTextPosition != -1 && nextTextPosition <= nihongo_i) {
						isStyleChange = true;
					}

					if (c <= 255) {
						nihongo_i++;
					} else {
						nihongo_i += 2;
					}

					String drawStr = readingStr.toString();
					int w = 0;
					if (fldData.dontWrap == false) {
						w = fo.stringWidth(drawStr);
					}

					int add = textSize;
					if (nextc == '\n')
						add = 0;
					if (c == '\r' || c == '\n' || w > fldWidth - add + 1 || (isStyleChange && i != 0)) { // この行を描画する

						if (!isStyleChange && w > fldWidth - add + 1) {
							// 行末処理
							{
								int len = drawStr.length();
								if (i + 1 < getText().length()) {
									char nextchar = getText().charAt(i + 1);
									if (nextchar == '.' || nextchar == ',' || nextchar == '、' || nextchar == '。') {
										len--;
									}
								}
								while (len > 0) {
									char c2 = drawStr.charAt(len - 1);
									if (c2 >= 'A' && c2 <= 'z' || c2 >= '0' && c2 <= '9' || c2 >= '!' && c2 <= '+') {
										len--;
									} else
										break;
								}
								if (len > 0) {
									drawStr = drawStr.substring(0, len);
								}
							}
						}
						readingStr.delete(0, drawStr.length());

						int drawWidth = fo.stringWidth(drawStr);

						// 右寄せかセンタリング
						if (lft == 0) {
							if (fldData.textAlign == 0) {
								lft = 0;
								if (fldData.style >= 3)
									lft = 1;
							}
							if (fldData.textAlign == 1)
								lft = (fldWidth - drawWidth) / 2;
							if (fldData.textAlign == 2)
								lft = fldWidth - drawWidth - 1;
						}

						// outline or shadow
						g.setColor(textColor);
						if ((textStyle & 8) > 0 || (textStyle & 16) > 0) {
							if (/* (textStyle&8) > 0 && */ (textStyle & 16) > 0) {
								g.drawString(drawStr, lft + 2, top + 1);
								g.drawString(drawStr, lft + 1, top + 2);
							}
							if ((textStyle & 8) > 0) {
								g.drawString(drawStr, lft - 1, top + 0);
								g.drawString(drawStr, lft + 0, top - 1);
							}
							g.drawString(drawStr, lft + 1, top + 0);
							g.drawString(drawStr, lft + 0, top + 1);
							g.setColor(fldData.bgColor);
						}

						// 文字描画
						g.drawString(drawStr, lft, top);

						// underline
						if ((textStyle & 4) > 0) {
							g.drawLine(lft, top, lft + drawWidth, top);
							if ((textStyle & 8) > 0 || (textStyle & 16) > 0) {
								g.setColor(textColor);
								g.drawLine(lft, top + 1, lft + drawWidth, top + 1);
							}
						}

						if (!isStyleChange) {
							// v++; //改行
							lft = 0;
							top += textHeight;
							/*
							 * //OS9とのフォントサイズの違いを吸収 if(PCARD.pc.stack.createdByVersion.charAt(0)=='2' &&
							 * fldData.style!=5 && !smallfont && top+textHeight>fldData.height+4){ smallfont
							 * = true; repaint(); }
							 */
						}

						if (c == '\r' && i + 1 < getText().length() && getText().charAt(i + 1) == '\n') {
							i++;
						}
					}

					if (isStyleChange) {
						// 次のスタイルを適用
						int styleId = fldData.styleList.get(nextStyle).styleId;
						for (int j = 0; j < fldData.card.stack.styleList.size(); j++) {
							OStack.styleClass styleClass = fldData.card.stack.styleList.get(j);
							if (styleClass.id == styleId) {
								if (styleClass.font != -1) {
									for (int k = 0; k < fldData.card.stack.fontList.size(); k++) {
										OStack.fontClass fontClass = fldData.card.stack.fontList.get(k);
										if (fontClass.id == styleClass.font) {
											textFont = fontClass.name;
											break;
										}
									}
								}
								if (styleClass.style != -1) {
									textStyle = styleClass.style;
								}
								if (styleClass.size != -1) {
									textSize = styleClass.size;
								}
								if (!fldData.fixedLineHeight) {
									textHeight = textSize + 1;
									if ((textStyle & 8) > 0 || (textStyle & 16) > 0) {
										textHeight += 1;
									}
								}
								// Color textColor = styleClass;
								break;
							}
						}

						// 新しいスタイルを適用
						g.setFont(new Font(textFont, textStyle & 0x03, textSize));
						fo = g.getFontMetrics();

						nextStyle++;
						if (fldData.styleList.size() > nextStyle) {
							nextTextPosition = fldData.styleList.get(nextStyle).textPosition;
						} else {
							nextTextPosition = -1;
						}
					}
				} /*
					 * else if(fldData.textAlign!=0){ //右寄せかセンタリング if(fldData.style!=1){
					 * g.setColor(Color.WHITE); g.fillRect(1,1,fldData.width-2,fldData.height-2); }
					 * g.setFont(new Font(fldData.textFont, fldData.textStyle&0x03,
					 * fldData.textSize)); FontMetrics fo = g.getFontMetrics(); int v=0; int sz =
					 * fldData.textHeight; StringBuilder str = new StringBuilder(); for(int i=0;
					 * i<getText().length(); i++){ char c = getText().charAt(i);
					 * if(c=='\r'||c=='\n'||i==getText().length()){
					 * //System.out.println("fontpict:"+h+","+v+"  "+c%8+","+c/8+""+(char)(c+32));
					 * String str2 = str.toString(); int w = fo.stringWidth(str2); int lft = 0;
					 * if(fldData.textAlign==0) lft = 2; if(fldData.textAlign==1) lft =
					 * (fldData.width-w)/2; if(fldData.textAlign==2) lft = fldData.width-w;
					 * g.setColor(Color.BLACK); g.drawString(str2,lft,fldData.textSize+v*sz);
					 * 
					 * str.delete(0,str.length()); v++;
					 * if(c=='\r'&&i+1<getText().length()&&getText().charAt(i+1)=='\n') i++; } else{
					 * str.append(c); } } }
					 */

				if (AuthTool.tool != null && FieldGUI.gui.target == this) {
					FieldGUI.drawSelectBorder(this);
				}
				return;
			}
		} else if (fldData == null) {
			// showList用
			// ライン選択
			g.setColor(Color.WHITE);
			Rectangle r = g.getClipBounds();
			g.fillRect(r.x, r.y, r.width, r.height);
			if (pr_selLine > 0) {
				// g.setColor(Color.WHITE);
				// g.setXORMode(SystemColor.textHighlight);
				g.setColor(SystemColor.textHighlight);
				g.fillRect(1, (pr_selLine - 1) * getRowHeight(), getWidth() - 1, getRowHeight());
				// g.setXORMode(Color.WHITE);
			}
		}
		g.setPaintMode();
		super.paintComponent(g);
		if (AuthTool.tool != null && FieldGUI.gui.target == this) {
			FieldGUI.drawSelectBorder(this);
		}
	}

	@Override
	protected void paintBorder(Graphics g) {
		if (!isVisible())
			return;
		if (PCARD.pc.bit > 1)
			return;
		if (PaintTool.editBackground && fldData.parent != null && fldData.parent.objectType.equals("card"))
			return;
		if (fldData != null && fldData.card != null && fldData.card.objectType.equals("cd")
				&& fldData.card != PCARD.pc.stack.curCard)
			return;
		Graphics paneg = PCARD.pc.mainPane.getGraphics();
		if (PCARD.lockedScreen && paneg == g)
			return;
		super.paintBorder(g);
	}
}