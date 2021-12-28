package hyperzebra.subsystem.resedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import hyperzebra.PictureFile;
import hyperzebra.Rsrc;
import hyperzebra.gui.PCARD;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.gui.dialog.GDialog;
import hyperzebra.object.OButton;
import hyperzebra.object.OObject;
import hyperzebra.subsystem.iconeditor.IconEditor;

public class IconTypeEditor extends ResTypeEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public IconButton[] selectedButton;

	public IconTypeEditor(PCARDFrame pc, String type, OObject object) {
		super(pc, type, object);

		if (object != null) {
			selectedId[0] = ((OButton) object).icon;
		}

		contpane.setLayout(new FlowLayout(FlowLayout.LEFT));
		contpane.addMouseListener(new IconBackListener());

		scrollpane.getVerticalScrollBar().setUnitIncrement(133);

		open(pcard, 0);

		new DropTarget(this, new IconDropListener());

		toFront();
		setVisible(true);
	}

	@Override
	public void open(PCARDFrame pc, int in_scroll) {
		super.open(pc, in_scroll);
		LineBorder border = new LineBorder(Color.GRAY);
		IconButtonListener listener = new IconButtonListener();

		selectedButton = new IconButton[selectedId.length];
		scroll = in_scroll;

		int number = rsrcAry.length;
		setTitle(type + "(" + number + ")");

		for (int i = 0; i < number; i++) {
			Rsrc.rsrcClass rsrc = rsrcAry[i];
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.setPreferredSize(new Dimension(128, 128));
			panel.setBackground(new Color(219, 223, 230));

			IconButton iconlabel = new IconButton(this);
			iconlabel.setName(Integer.toString(rsrc.id));
			iconlabel.setPreferredSize(new Dimension(126, 112));
			if (object != null && !pc.stack.rsrc.rsrcIdMap.containsKey(type + rsrc.id)) {
				iconlabel.setEnabled(false);
			}
			iconlabel.setBorder(border);
			for (int j = 0; j < selectedId.length; j++) {
				if (rsrc.id == selectedId[j]) {
					selectedButton[j] = iconlabel;
					iconlabel.setBorder(new LineBorder(new Color(128, 128, 192), 3));
					if (j == 0 && in_scroll == 0) {
						scroll = 133 * (i / 4);
					}
					break;
				}
			}
			iconlabel.setHorizontalAlignment(SwingConstants.CENTER);
			iconlabel.addMouseListener(listener);
			// iconはすべて読み込めないので後で表示する
			panel.add("North", iconlabel);

			JLabel label = new JLabel(rsrc.id + " " + rsrc.name);
			label.setPreferredSize(new Dimension(128, 16));
			panel.add("South", label);

			contpane.add(panel);
		}

		contpane.setPreferredSize(new Dimension(contpane.getPreferredSize().width, 133 * ((number + 3) / 4)));

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

	public void updateContent(int id) {
		int number = rsrcAry.length;
		setTitle(type + "(" + number + ")");

		for (int i = 0; i < contpane.getComponentCount(); i++) {
			Rsrc.rsrcClass rsrc = rsrcAry[i];

			Component component = (Component) contpane.getComponent(i);
			if (component != null) {
				if (component.getClass() == JPanel.class) {
					for (int j = 0; j < ((JPanel) component).getComponentCount(); j++) {
						Component component2 = (Component) ((JPanel) component).getComponent(j);
						if (component2.getClass() == IconButton.class) {
							if (component2.getName().equals(Integer.toString(id))) {
								((IconButton) component2).setIcon(null);
								((IconButton) component2).setName(Integer.toString(rsrc.id));
								((IconButton) component2).repaint();
							}
						} else if (component2.getClass() == JLabel.class) {
							((JLabel) component2).setText(rsrc.id + " " + rsrc.name);
						}
					}
				}
			}
		}
	}

	static Border getSelectedBorder(int r, int g, int b) {
		Color color1 = new Color(r, g, b);
		Color color2 = new Color((255 + r * 3) / 4, (255 + g * 3) / 4, (255 + b * 3) / 4);
		Color color3 = new Color((255 + r) / 2, (255 + g) / 2, (255 + b) / 2);
		CompoundBorder border1 = new CompoundBorder(new LineBorder(color3), new LineBorder(color2));
		CompoundBorder border2 = new CompoundBorder(border1, new LineBorder(color1));

		return border2;
	}

	class IconButton extends JButton {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		BufferedImage bi;
		IconTypeEditor owner;

		IconButton(IconTypeEditor owner) {
			this.owner = owner;
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, 128, 128);
			if (getIcon() == null) {
				BufferedImage bi = pcard.stack.rsrc.getImage(Integer.valueOf(this.getName()), type);
				int w = 124;
				int h = 110;
				if (bi != null && (bi.getWidth() <= w && bi.getHeight() <= h)) {
					// そのままの大きさ
					// String filename =
					// pcard.stack.rsrc.getFileName(Integer.valueOf(this.getName()), "icon");
					// String path = pcard.stack.file.getParent()+File.separatorChar+filename;
					setIcon(new ImageIcon(bi));
					bi.flush();
					setAlignmentX(CENTER_ALIGNMENT);
					super.paintComponent(g);
					return;
				}
				if (bi != null && (bi.getWidth() > w || bi.getHeight() > h)) {
					// 縮小表示
					float rate = (float) w / bi.getWidth();
					if ((float) h / bi.getHeight() < rate) {
						rate = (float) h / bi.getHeight();
					}
					int nw = (int) (rate * bi.getWidth());
					int nh = (int) (rate * bi.getHeight());

					// 3倍の大きさの画像も用意
					if (bi.getWidth() > nw * 3) {
						this.bi = new BufferedImage(nw * 3, nh * 3, BufferedImage.TYPE_INT_ARGB);
						Graphics2D g1 = (Graphics2D) this.bi.getGraphics();
						g1.drawImage(bi, 0, 0, nw * 3, nh * 3, 0, 0, bi.getWidth(), bi.getHeight(), this);
					} else {
						this.bi = bi;
					}

					BufferedImage newbi = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = (Graphics2D) newbi.getGraphics();
					g2.drawImage(bi, 0, 0, nw, nh, 0, 0, bi.getWidth(), bi.getHeight(), this);
					bi.flush();
					bi = newbi;

					// 後から高画質にする
					if (updateThread == null || !updateThread.isAlive()) {
						updateThread = new lateUpdateThread();
						updateThread.setButton(this);
						updateThread.start();
					} else {
						updateThread.setButton(this);
					}
				}
				if (bi == null) {
					bi = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
					Graphics g2 = bi.getGraphics();
					g2.setColor(Color.RED);
					g2.drawLine(0, 0, 32, 32);
					g2.drawLine(32, 0, 0, 32);
				}
				setIcon(new ImageIcon(bi));
				bi.flush();
				setAlignmentX(CENTER_ALIGNMENT);
			}
			super.paintComponent(g);

			if (updateThread != null && !updateThread.isAlive() && updateList.size() > 0) {
				updateThread = new lateUpdateThread();
				updateThread.start();
			}
		}
	}

	lateUpdateThread updateThread;
	ArrayList<IconButton> updateList = new ArrayList<IconButton>();

	class lateUpdateThread extends Thread {
		void setButton(IconButton button) {
			updateList.add(button);
		}

		public void run() {
			// 高画質にする
			setPriority(MIN_PRIORITY);

			try {
				sleep(100);
			} catch (InterruptedException e) {
				this.interrupt();
			}

			for (int i = 0; i < updateList.size(); i++) {
				IconButton button = updateList.get(i);

				try {
					sleep(10);
				} catch (InterruptedException e) {
					this.interrupt();
				}

				// 画面上に見えているか？
				int top = button.owner.scrollpane.getVerticalScrollBar().getValue();
				int bottom = top + button.owner.scrollpane.getHeight();
				if (button.getParent().getBounds().y + button.getBounds().height > top
						&& button.getParent().getBounds().y < bottom) {
					BufferedImage bi = button.bi;
					int w = 124;
					int h = 110;
					float rate = (float) w / bi.getWidth();
					if ((float) h / bi.getHeight() < rate) {
						rate = (float) h / bi.getHeight();
					}
					int nw = (int) (rate * bi.getWidth());
					int nh = (int) (rate * bi.getHeight());
					// そこからきれいに縮小
					Image img = bi.getScaledInstance(nw, -1, Image.SCALE_AREA_AVERAGING);
					BufferedImage newbi = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = (Graphics2D) newbi.getGraphics();
					g2.drawImage(img, 0, 0, nw, nh, button);
					button.setIcon(new ImageIcon(newbi));
					bi.flush();
					newbi.flush();
					System.gc();

					updateList.remove(i);
					i--;
				}
			}
		}
	}

	class IconButtonListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= 2) {
				// ダブルクリック
				if (pcard.stack.rsrc.rsrcIdMap.containsKey(type + editor.selectedId[0])) {
					// 開く
					new IconEditor(editor, pcard.stack.rsrc, type, editor.selectedId[0]);
				} else {
					// このスタックのリソースでない場合はコピーを作るか尋ねる
					new GDialog(editor, PCARD.pc.intl.getDialogText("This resource is not in this stack. Make a copy?"),
							null, "Cancel", "OK", null);
					if (GDialog.clicked.equals("OK")) {
						Rsrc.rsrcClass r = pcard.stack.rsrc.getResourceAll(editor.selectedId[0], type);
						if (r != null) {
							String srcFilePath = pcard.stack.rsrc.getFilePathAll(editor.selectedId[0], type);
							String newFileName = new File(srcFilePath).getName();
							String newFilePath = editor.pcard.stack.file.getParent() + File.separatorChar + newFileName;
							FileChannel srcChannel = null;
							FileChannel destChannel = null;
							try {
								srcChannel = new FileInputStream(srcFilePath).getChannel();
								destChannel = new FileOutputStream(newFilePath).getChannel();
								srcChannel.transferTo(0, srcChannel.size(), destChannel);
							} catch (Exception e1) {
								e1.printStackTrace();
							} finally {
								try {
									srcChannel.close();
									destChannel.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
							pcard.stack.rsrc.addResource(r);
							new IconEditor(editor, pcard.stack.rsrc, type, editor.selectedId[0]);
						} else {
							System.out.println("resource not found.");
						}
					}
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			IconButton btn = (IconButton) e.getSource();
			IconTypeEditor editor = (IconTypeEditor) (btn.getRootPane().getParent());
			if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
				// 範囲選択
				int selectid = Integer.valueOf(btn.getName());
				int lastselectid = editor.selectedId[0];
				int allnumber = rsrcAry.length;
				for (int i = 0; i < allnumber; i++) {
					Rsrc.rsrcClass rsrc = rsrcAry[i];
					if ((rsrc.id <= selectid) != (rsrc.id <= lastselectid)) {
						// 一つずつ追加
						IconButton[] newSelectedButton = new IconButton[editor.selectedButton.length + 1];
						int[] newSelectedId = new int[editor.selectedButton.length + 1];
						System.arraycopy(editor.selectedButton, 0, newSelectedButton, 1, editor.selectedButton.length);
						System.arraycopy(editor.selectedId, 0, newSelectedId, 1, editor.selectedButton.length);
						// ボタンを探す
						for (int j = 0; j < contpane.getComponentCount(); j++) {
							JPanel panel = (JPanel) contpane.getComponent(i);
							IconButton iconbutton = (IconButton) panel.getComponent(0);
							if (Integer.valueOf(iconbutton.getName()) == rsrc.id) {
								newSelectedButton[0] = iconbutton;
								newSelectedId[0] = rsrc.id;
								iconbutton.setBorder(getSelectedBorder(128, 128, 192));
								break;
							}
						}
						editor.selectedButton = newSelectedButton;
						editor.selectedId = newSelectedId;
					}
				}
			} else if (((e.getModifiersEx() & InputEvent.META_DOWN_MASK) != 0)
					|| ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)) {
				// 追加選択
				IconButton[] newSelectedButton = new IconButton[editor.selectedButton.length + 1];
				int[] newSelectedId = new int[editor.selectedButton.length + 1];
				System.arraycopy(editor.selectedButton, 0, newSelectedButton, 1, editor.selectedButton.length);
				System.arraycopy(editor.selectedId, 0, newSelectedId, 1, editor.selectedButton.length);
				newSelectedButton[0] = btn;
				newSelectedId[0] = Integer.valueOf(btn.getName());
				editor.selectedButton = newSelectedButton;
				editor.selectedId = newSelectedId;
				((IconButton) (e.getSource())).setBorder(getSelectedBorder(96, 96, 128));
			} else {
				// 一つ選択
				if (editor.selectedButton != null) {
					for (int i = 0; i < editor.selectedButton.length; i++) {
						if (editor.selectedButton[i] != null) {
							editor.selectedButton[i].setBorder(new LineBorder(Color.GRAY));
						}
					}
				}
				if (editor.selectedButton == null || editor.selectedButton.length != 1) {
					editor.selectedButton = new IconButton[1];
					editor.selectedId = new int[1];
				}
				editor.selectedButton[0] = btn;
				editor.selectedId[0] = Integer.valueOf(btn.getName());
				((IconButton) (e.getSource())).setBorder(getSelectedBorder(96, 96, 128));
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			IconTypeEditor ieditor = (IconTypeEditor) editor;
			if (ieditor.selectedButton != null) {
				ieditor.selectedButton[0].setBorder(getSelectedBorder(128, 128, 192));
			}
		}

	}

	class IconBackListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			JPanel btn = (JPanel) e.getSource();
			IconTypeEditor editor = (IconTypeEditor) (btn.getRootPane().getParent());
			if (((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0)
					|| ((e.getModifiersEx() & InputEvent.META_DOWN_MASK) != 0)
					|| ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)) {

			} else {
				if (editor.selectedButton != null) {
					for (int i = 0; i < editor.selectedButton.length; i++) {
						if (editor.selectedButton[i] != null) {
							editor.selectedButton[i].setBorder(new LineBorder(Color.GRAY));
						}
					}
				}
				if (editor.selectedButton == null || editor.selectedButton.length != 1) {
					editor.selectedButton = new IconButton[1];
					editor.selectedId = new int[1];
				}
				editor.selectedButton[0] = null;
				editor.selectedId[0] = 0;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}

	class IconDropListener extends DropTargetAdapter {
		@Override
		public void drop(DropTargetDropEvent e) {
			try {
				Transferable transfer = e.getTransferable();
				if (transfer.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					@SuppressWarnings("unchecked")
					List<File> fileList = (List<File>) (transfer.getTransferData(DataFlavor.javaFileListFlavor));

					((IconTypeEditor) editor).selectedButton = null;
					editor.selectedId = new int[fileList.size()];

					for (int j = 0; j < fileList.size(); j++) {
						String path = fileList.get(j).toString();
						String ext = "";
						BufferedImage bi = null;
						try {
							ext = ".ppm";
							bi = PictureFile.loadPbm(path);
							if (bi == null) {
								if (path.lastIndexOf(".") >= 0) {
									ext = path.substring(path.lastIndexOf("."), path.length());
								} else {
									ext = "";
								}
								bi = javax.imageio.ImageIO.read(new File(path));
							}
							if (bi == null) {
								ext = ".pict";
								bi = PictureFile.loadPICT(path);
							}
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						if (bi != null) {
							String name = new File(path).getName();
							Pattern p = Pattern.compile("([0-9]{1,5})([^0-9])");
							Matcher m = p.matcher(name);
							int baseid = 1000;
							if (m.find()) {
								baseid = Integer.valueOf(m.group(1));
							}
							if (baseid < -32768 || baseid > 32767) {
								baseid = (baseid + 1000000) % 10000;
							}
							int iconid;
							String newFileName = "";
							if (pcard.stack.file != null) {
								iconid = pcard.stack.rsrc.getNewResourceId(type, baseid);

								// ファイルをコピー
								if (type.equals("icon")) {
									newFileName = "ICON_" + iconid + ext;
								} else if (type.equals("cicn")) {
									newFileName = "cicn_" + iconid + ext;
								} else if (type.equals("picture")) {
									newFileName = "PICT_" + iconid + ext;
								} else if (type.equals("cursor")) {
									newFileName = "CURS_" + iconid + ext;
								} else {
									System.out.println("unknown resource type");
								}
								String newFilePath = pcard.stack.file.getParent() + File.separatorChar + newFileName;
								FileChannel srcChannel = null;
								FileChannel destChannel = null;
								try {
									srcChannel = new FileInputStream(path).getChannel();
									destChannel = new FileOutputStream(newFilePath).getChannel();
									srcChannel.transferTo(0, srcChannel.size(), destChannel);
								} finally {
									srcChannel.close();
									destChannel.close();
								}
							} else {
								iconid = pcard.stack.rsrc.getNewResourceId(type, 1);
								newFileName = path;
							}
							// リソースに追加
							pcard.stack.rsrc.addResource(iconid, type, name, newFileName);

							editor.selectedId[j] = iconid;
						} else {
							String str = PCARD.pc.intl.getDialogText("Can't open the file '%1'.");
							str = str.replace("%1", path);
							new GDialog(editor, str, null, "OK", null, null);
						}
					}
					// 開き直す
					editor.open(pcard, 0);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
