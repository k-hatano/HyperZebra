package hyperzebra.subsystem.resedit;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import hyperzebra.Rsrc;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.gui.menu.GMenuPaint;
import hyperzebra.object.OCard;
import hyperzebra.subsystem.iconeditor.IconEditor;

public class REMenu {
	/**
	 * 
	 */

	public REMenu(JFrame owner) {
		ActionListener listener = null;

		listener = new REMenuListener(owner);

		// メニューバーの設定
		JMenuBar mb = new JMenuBar();
		owner.setJMenuBar(mb);

		JMenu m;
		JMenuItem mi;
		int s = InputEvent.CTRL_DOWN_MASK;
		if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
			s = InputEvent.META_DOWN_MASK;
		}

		// Fileメニュー
		m = new JMenu(PCARDFrame.pc.intl.getText("File"));
		mb.add(m);

		m.add(mi = new JMenuItem(PCARDFrame.pc.intl.getText("New Item")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARDFrame.pc.intl.getText("Open")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARDFrame.pc.intl.getText("View File")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, s));
		mi.addActionListener(listener);
		String os = System.getProperty("os.name");
		String ver = System.getProperty("os.version");
		if (os != null && os.startsWith("Mac OS X") && !ver.startsWith("10.5")) {
		} else {
			mi.setEnabled(false);
		}
		m.add(mi = new JMenuItem(PCARDFrame.pc.intl.getText("Close")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, s));
		mi.addActionListener(listener);

		// Editメニュー
		m = new JMenu(PCARDFrame.pc.intl.getText("Edit"));
		mb.add(m);
		m.add(mi = new JMenuItem(PCARDFrame.pc.intl.getText("Cut")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARDFrame.pc.intl.getText("Copy")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARDFrame.pc.intl.getText("Paste")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, s));
		mi.addActionListener(listener);
		m.addSeparator();
		m.add(mi = new JMenuItem(PCARDFrame.pc.intl.getText("Delete")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, s));
		mi.addActionListener(listener);
		m.add(mi = new JMenuItem(PCARDFrame.pc.intl.getText("Select All")));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, s));
		mi.addActionListener(listener);
	}
}

//リソース編集のメニュー動作
class REMenuListener implements ActionListener {
	ResTypeEditor editor;

	REMenuListener(JFrame owner) {
		super();
		this.editor = (ResTypeEditor) owner;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String in_cmd = e.getActionCommand();
		String cmd = PCARDFrame.pc.intl.getEngText(in_cmd);

		if (cmd.equals("Close")) {
			editor.dispose();
			return;
		}

		if (editor.pcard.stack == null) {
			return;
		}

		if (cmd.equals("New Item")) {
			String newFileName = "";
			String name = "";
			int rsrcid = 0;
			rsrcid = editor.pcard.stack.rsrc.getNewResourceId(editor.type);
			if (editor.type.equals("icon")) {
				newFileName = "ICON_" + rsrcid + ".png";
			} else if (editor.type.equals("cicn")) {
				newFileName = "cicn_" + rsrcid + ".png";
			} else if (editor.type.equals("picture")) {
				newFileName = "PICT_" + rsrcid + ".png";
			} else if (editor.type.equals("cursor")) {
				newFileName = "CURS_" + rsrcid + ".png";
			} else {
				System.out.println("unknown resource type.");
				newFileName = "dummy";
				name = "dummy";
			}
			// ファイルを作成
			if (editor.pcard.stack.file == null)
				return;
			String newFilePath = editor.pcard.stack.file.getParent() + File.separatorChar + newFileName;
			File newFile = new File(newFilePath);
			if (editor.type.equals("icon") || editor.type.equals("cicn") || editor.type.equals("picture")
					|| editor.type.equals("cursor")) {
				BufferedImage bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
				try {
					ImageIO.write(bi, "png", newFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				System.out.println("unknown resource type..");
			}

			// リソースに追加
			editor.pcard.stack.rsrc.addResource(rsrcid, editor.type, name, newFileName);
			editor.selectedId = new int[1];
			editor.selectedId[0] = rsrcid;
			// 開き直す
			editor.open(editor.pcard, 0);
		} else if (cmd.equals("Open")) {
			if (editor.selectedId[0] != 0) {
				// if(editor.type.equals("icon")){
				new IconEditor(editor, editor.pcard.stack.rsrc, editor.type, editor.selectedId[0]);
				// }
			}
		} else if (cmd.equals("Select All")) {
			int number = editor.rsrcAry.length;
			((IconTypeEditor) editor).selectedId = new int[number];
			((IconTypeEditor) editor).selectedButton = new IconTypeEditor.IconButton[number];
			for (int i = 0; i < number; i++) {
				Rsrc.rsrcClass rsrc = editor.rsrcAry[i];
				if (editor.type.equals("icon")) {
					// ボタンを探す
					for (int j = 0; j < editor.contpane.getComponentCount(); j++) {
						JPanel panel = (JPanel) editor.contpane.getComponent(i);
						IconTypeEditor.IconButton iconbutton = (IconTypeEditor.IconButton) panel.getComponent(0);
						if (Integer.valueOf(iconbutton.getName()) == rsrc.id) {
							((IconTypeEditor) editor).selectedButton[i] = iconbutton;
							editor.selectedId[i] = rsrc.id;
							iconbutton.setBorder(IconTypeEditor.getSelectedBorder(128, 128, 192));
							break;
						}
					}
				}
			}
		} else if (cmd.equals("View File")) {
			if (editor.selectedId[0] != 0) {
				String parentPath = editor.pcard.stack.file.getParent();
				String filename = "";
				for (int i = 0; i < editor.selectedId.length; i++) {
					if (filename.length() > 0)
						filename += ":";
					filename += parentPath + File.separatorChar
							+ editor.pcard.stack.rsrc.getFileName1(editor.selectedId[i], editor.type);
				}
				/*
				 * boolean isBundle = false; String attrStr = ""; if(new
				 * File("/Developer/Tools/GetFileInfo").exists()){ try { //バンドルかどうか
				 * ProcessBuilder pb = new ProcessBuilder("/Developer/Tools/GetFileInfo",
				 * getConvertPath(parentPath)); Process p = pb.start(); p.waitFor(); InputStream
				 * is = p.getInputStream(); BufferedReader br = new BufferedReader(new
				 * InputStreamReader(is)); for (;;) { String line = br.readLine(); if (line ==
				 * null) break; if(line.startsWith("attributes: ")){
				 * if(line.substring("attributes: ".length()).contains("b")){ isBundle = true;
				 * attrStr = line.substring("attributes: ".length()); } break; } } } catch
				 * (IOException e1) { e1.printStackTrace(); } catch (InterruptedException e1) {
				 * e1.printStackTrace(); } }
				 */

				try {
					/*
					 * if(isBundle){ String notBundleAttr; int index = attrStr.indexOf("b");
					 * notBundleAttr = attrStr.substring(0,index) + attrStr.substring(index);
					 * ProcessBuilder pb1 = new ProcessBuilder("/Developer/Tools/SetFile", "-a",
					 * notBundleAttr, "\""+parentPath+"\""); pb1.start(); }
					 */
					// "-R"はOSX10.6以上 バンドルの中はあらかじめ開いておかないと表示できない
					ProcessBuilder pb = new ProcessBuilder("open", "-R", filename);
					pb.start();
					/*
					 * if(isBundle){ ProcessBuilder pb1 = new
					 * ProcessBuilder("/Developer/Tools/SetFile", "-a", attrStr,
					 * "\""+parentPath+"\""); pb1.start(); }
					 */
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else if (cmd.equals("Cut") || cmd.equals("Copy")) {
			if (editor.selectedId.length > 0) {
				// ファイルをコピー
				for (int i = 0; i < editor.selectedId.length; i++) {
					Rsrc.rsrcClass rsrc;
					rsrc = editor.pcard.stack.rsrc.getResource1(editor.selectedId[i], editor.type);

					String FilePath = editor.pcard.stack.file.getParent() + File.separatorChar + rsrc.filename;
					String destFilePath = "resource_trash" + File.separatorChar + rsrc.filename;
					try {
						FileChannel srcChannel = null;
						FileChannel destChannel = null;
						try {
							srcChannel = new FileInputStream(FilePath).getChannel();
							destChannel = new FileOutputStream(destFilePath).getChannel();
							srcChannel.transferTo(0, srcChannel.size(), destChannel);
						} finally {
							srcChannel.close();
							destChannel.close();
						}
					} catch (FileNotFoundException e2) {
						e2.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
				}

				// XMLテキストを取得
				XMLOutputFactory factory = XMLOutputFactory.newInstance();
				StringWriter stringWriter = new StringWriter();
				try {
					XMLStreamWriter writer = factory.createXMLStreamWriter(stringWriter);

					writer.writeStartElement("resourceclips");
					writer.writeCharacters("\n\t");

					for (int i = 0; i < editor.selectedId.length; i++) {
						Rsrc.rsrcClass rsrc;
						rsrc = editor.pcard.stack.rsrc.getResource1(editor.selectedId[i], editor.type);
						rsrc.writeXMLOneRsrc(writer);
					}

					writer.writeEndElement();

					writer.close();

					// クリップボードにコピー(XML)
					{
						Toolkit kit = Toolkit.getDefaultToolkit();
						Clipboard clip = kit.getSystemClipboard();

						StringSelection ss = new StringSelection(stringWriter.toString());
						clip.setContents(ss, ss);
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		} else if (cmd.equals("Paste")) {
			Toolkit kit = Toolkit.getDefaultToolkit();
			Clipboard clip = kit.getSystemClipboard();

			if (clip.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
				String str = null;
				try {
					str = (String) clip.getData(DataFlavor.stringFlavor);
					StringSelection ss = new StringSelection(str);
					clip.setContents(ss, ss);
				} catch (UnsupportedFlavorException e2) {
					e2.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				if (str != null && str.startsWith("<resourceclips>")) {
					XMLInputFactory factory = XMLInputFactory.newInstance();
					XMLStreamReader reader = null;
					try {
						reader = factory.createXMLStreamReader(new ByteArrayInputStream(str.getBytes()));
					} catch (Exception e2) {
						e2.printStackTrace();
					}

					editor.selectedId = new int[] { 0 };
					for (int i = 0; i < 9999; i++) {
						try {
							int origId = 0;
							String typeStr = "", nameStr = "", fnameStr = "", leftStr = "0", topStr = "0";
							Rsrc.OptionInfo info = null;
							boolean isComplete = false;
							while (reader.hasNext()) {
								try {
									int eventType = reader.next();
									if (eventType == XMLStreamConstants.START_ELEMENT) {
										String elm = reader.getLocalName();
										if (elm.equals("id")) {
											origId = Integer.valueOf(reader.getElementText());
										} else if (elm.equals("type")) {
											typeStr = reader.getElementText();
										} else if (elm.equals("name")) {
											nameStr = reader.getElementText();
										} else if (elm.equals("file")) {
											fnameStr = reader.getElementText();
										} else if (elm.equals("left")) {
											leftStr = reader.getElementText();
										} else if (elm.equals("top")) {
											topStr = reader.getElementText();
										} else if (elm.equals("hotspot")) {
										} else if (elm.equals("fontinfo")) {
											info = editor.pcard.stack.rsrc.new FontInfo();
											reader = editor.pcard.stack.rsrc.readFontInfoXML(reader,
													(Rsrc.FontInfo) info);
										} else if (elm.equals("resourceclips")) {
										} else if (elm.equals("media")) {
										} else {
											System.out.println("Local Name: " + reader.getLocalName());
											System.out.println("Element Text: " + reader.getElementText());
										}
									}
									if (eventType == XMLStreamConstants.END_ELEMENT) {
										String elm = reader.getLocalName();
										if (elm.equals("media")) {
											isComplete = true;
											break;
										} else if (elm.equals("resourceclips")) {
											isComplete = false;
											break;
										}
									}
								} catch (Exception ex) {
									System.err.println(ex.getMessage());
									break;
								}
							}
							if (isComplete) {
								// リソースを追加
								int id = editor.pcard.stack.rsrc.getNewResourceId(typeStr, origId);

								// リソースのファイルもコピーする
								String srcFilePath = "resource_trash" + File.separatorChar + fnameStr;
								String newFileName = "dummy";
								if (new File(srcFilePath).exists()) {
									// ファイルをコピー
									String ext = "";
									if (fnameStr.lastIndexOf(".") >= 0) {
										ext = fnameStr.substring(fnameStr.lastIndexOf("."));
									}
									String typePrefix;
									if (typeStr.equals("icon")) {
										typePrefix = "ICON_";
									} else if (typeStr.equals("cicn")) {
										typePrefix = "cicn_";
									} else if (typeStr.equals("picture")) {
										typePrefix = "PICT_";
									} else if (typeStr.equals("cursor")) {
										typePrefix = "CURS_";
									} else {
										typePrefix = typeStr + "_";
									}
									newFileName = typePrefix + id + ext;
									String newFilePath = editor.pcard.stack.file.getParent() + File.separatorChar
											+ newFileName;
									FileChannel srcChannel = null;
									FileChannel destChannel = null;
									try {
										srcChannel = new FileInputStream(srcFilePath).getChannel();
										destChannel = new FileOutputStream(newFilePath).getChannel();
										srcChannel.transferTo(0, srcChannel.size(), destChannel);
									} finally {
										srcChannel.close();
										destChannel.close();
									}
								} else {
									// ファイルがない
								}

								// リソースに追加
								editor.pcard.stack.rsrc.addResource(id, typeStr, nameStr, newFileName, leftStr, topStr,
										info);

								int[] oldSelId = editor.selectedId;
								editor.selectedId = new int[oldSelId.length + 1];
								System.arraycopy(oldSelId, 0, editor.selectedId, 0, oldSelId.length);
								editor.selectedId[editor.selectedId.length - 1] = id;
							}
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					} // end of for

				}
			} // stringFlavor
			else if (editor.type.equals("icon")) {
				Image img = GMenuPaint.getClipboardImage();
				if (img != null) {
					// 画像の場合

					String newFileName = "dummy";
					String name = "dummy";
					int rsrcid = 0;
					if (editor.type.equals("icon")) {
						rsrcid = editor.pcard.stack.rsrc.getNewResourceId("icon");
						newFileName = "ICON_" + rsrcid + ".png";
						name = "";
					}
					// ファイルを作成
					String newFilePath = editor.pcard.stack.file.getParent() + File.separatorChar + newFileName;
					File newFile = new File(newFilePath);
					if (editor.type.equals("icon")) {
						try {
							ImageIO.write((RenderedImage) img, "png", newFile);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}

					// リソースに追加
					editor.pcard.stack.rsrc.addResource(rsrcid, "icon", name, newFileName);
					editor.selectedId = new int[1];
					editor.selectedId[0] = rsrcid;
				}
			}

			editor.scroll = editor.scrollpane.getVerticalScrollBar().getValue();
			// 開き直す
			editor.open(editor.pcard, 0);
			OCard.reloadCurrentCard();
		}

		if (cmd.equals("Delete") || cmd.equals("Cut")) {
			// 削除
			int number = editor.selectedId.length;
			for (int i = 0; i < number; i++) {
				editor.pcard.stack.rsrc.deleteResource(editor.type, editor.selectedId[i]);
			}
			// 選択解除
			if (editor.type.equals("icon")) {
				((IconTypeEditor) editor).selectedButton = null;
			}
			editor.selectedId = new int[] { 0 };
			editor.scroll = editor.scrollpane.getVerticalScrollBar().getValue();
			// 開き直す
			editor.open(editor.pcard, 0);
			OCard.reloadCurrentCard();
		}
	}

}