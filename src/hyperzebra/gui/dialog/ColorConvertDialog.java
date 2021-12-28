package hyperzebra.gui.dialog;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import hyperzebra.gui.PCARD;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.gui.menu.GMenuPaint;
import hyperzebra.tool.toolSelectInterface;

public class ColorConvertDialog extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private PCARDFrame owner;
	private Color[] srcColorBest = new Color[16];
	private Color[] dstColorBest = new Color[16];
	private int near = 10;
	private BufferedImage save_bi;

	public ColorConvertDialog(PCARDFrame owner) {
		super();
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		this.owner = owner;
		getContentPane().setLayout(null);
		setTitle(PCARD.pc.intl.getDialogText("Color Convert"));

		if (owner.tool instanceof toolSelectInterface
				&& ((toolSelectInterface) owner.tool).getSelectedSurface(owner) != null) {
		} else {
			GMenuPaint.setUndo();
		}

		makeColors(30); // 類似度の初期値30で開く

		setBounds(owner.getX() + owner.getWidth() / 2 - 200, owner.getY() + owner.getHeight() / 2 - 160 + 12, 400, 320);

		setResizable(false);
		setVisible(true);
	}

	private void makeColors(int near) {
		this.near = near;

		this.getContentPane().removeAll();
		this.getContentPane().repaint();

		this.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		BufferedImage bi;
		if (owner.tool instanceof toolSelectInterface
				&& ((toolSelectInterface) owner.tool).getSelectedSurface(owner) != null) {
			toolSelectInterface tl = (toolSelectInterface) owner.tool;
			bi = tl.getSelectedSurface(owner);
			save_bi = tl.getSelectedSurface(owner);
		} else {
			bi = owner.getSurface();
		}

		if (bi == null) {
			return;
		}

		// 代表色ピックアップ
		ArrayList<Color> srcColors = new ArrayList<Color>();
		ArrayList<Integer> srcColorCount = new ArrayList<Integer>();
		int width = bi.getWidth();
		int height = bi.getHeight();
		DataBuffer db = bi.getRaster().getDataBuffer();
		int lasti = -1;

		// 含まれる色のリストを作成
		for (int v = 0; v < height; v++) {
			for (int h = 0; h < width; h++) {
				int d = db.getElem(h + v * width);
				if (((d >> 24) & 0xFF) == 0)
					continue;// 透明色は含まない
				Color c = new Color((d >> 16) & 0xFF, (d >> 8) & 0xFF, (d >> 0) & 0xFF);
				boolean isFound = false;
				if (lasti >= 0 && isNearHSV(d, srcColors.get(lasti), near)) {
					// 既に見つけた色
					isFound = true;
					srcColorCount.set(lasti, srcColorCount.get(lasti) + 1);
				} else {
					for (int i = 0; i < srcColors.size(); i++) {
						if (isNear(d, srcColors.get(i), near)) {
							isFound = true;
							srcColorCount.set(i, srcColorCount.get(i) + 1);
							lasti = i;
							break;
						}
					}
					if (!isFound) {
						if (srcColors.size() > 1024)
							break;
						srcColors.add(c);
						srcColorCount.add(1);
					}
				}
			}
		}

		// 含まれる色を上位順に並べる。このとき上位にあるものと色相の差がある程度なければいけない。
		int[] srcColorCountBest = new int[16];
		for (int j = 0; j < srcColorCountBest.length; j++) {
			srcColorCountBest[j] = 0;
			srcColorBest[j] = new Color(0, 0, 0);
			dstColorBest[j] = new Color(0, 0, 0);
		}

		for (int i = 0; i < srcColorCount.size(); i++) {
			for (int j = 0; j < srcColorCountBest.length; j++) {
				if (srcColorCountBest[j] < srcColorCount.get(i)) {
					boolean nearColorFound = false;
					for (int m = 0; m < j; m++) {
						if (isNearHSV(srcColors.get(i), srcColorBest[m], near)) {
							nearColorFound = true;
							/*
							 * srcColorBest[m] = srcColors.get(i); dstColorBest[m] = srcColors.get(i);
							 * srcColorCountBest[m] = srcColorCount.get(i);
							 */
							break;
						}
					}
					if (!nearColorFound) {
						for (int k = srcColorCountBest.length - 1; k >= j + 1; k--) {
							srcColorBest[k] = srcColorBest[k - 1];
							dstColorBest[k] = dstColorBest[k - 1];
							srcColorCountBest[k] = srcColorCountBest[k - 1];
						}
						srcColorBest[j] = srcColors.get(i);
						dstColorBest[j] = srcColors.get(i);
						srcColorCountBest[j] = srcColorCount.get(i);
					}
					break;
				}
			}
		}

		// 変更前の色ボタン
		for (int i = 0; i < 10; i++) {
			if (srcColorCountBest[i] == 0)
				break;
			JButton button = new JButton();
			BufferedImage icon_image = new BufferedImage(16, 12, BufferedImage.TYPE_INT_RGB);
			Graphics icon_g = icon_image.createGraphics();
			icon_g.setColor(srcColorBest[i]);
			icon_g.fillRect(0, 0, 16, 12);
			button.setIcon(new ImageIcon(icon_image));
			button.setName("srcColor " + i);
			button.setText("" + i);
			button.setBounds(40, 10 + 25 * i, 60, 20);
			button.setMargin(new Insets(0, 0, 0, 0));
			button.addActionListener(this);
			getContentPane().add(button);
		}

		// 変更後の色ボタン
		for (int i = 0; i < 10; i++) {
			if (srcColorCountBest[i] == 0)
				break;
			JButton button = new JButton();
			BufferedImage icon_image = new BufferedImage(16, 12, BufferedImage.TYPE_INT_RGB);
			Graphics icon_g = icon_image.createGraphics();
			icon_g.setColor(srcColorBest[i]);
			icon_g.fillRect(0, 0, 16, 12);
			button.setIcon(new ImageIcon(icon_image));
			button.setName("dstColor " + i);
			button.setText("" + i);
			button.setBounds(280, 10 + 25 * i, 60, 20);
			button.setMargin(new Insets(0, 0, 0, 0));
			button.addActionListener(this);
			getContentPane().add(button);
		}

		// 類似色判定を厳しくする、緩くするボタン
		{
			JButton button = new JButton(PCARD.pc.intl.getDialogText("Unite"));
			button.setBounds(130, 100, 100, 25);
			button.setName("Unite");
			button.addActionListener(this);
			getContentPane().add(button);
		}
		{
			JButton button = new JButton(PCARD.pc.intl.getDialogText("Divide"));
			button.setBounds(130, 150, 100, 25);
			button.setName("Divide");
			button.addActionListener(this);
			getContentPane().add(button);
		}

		// ok, cancel
		{
			JButton button = new JButton(PCARD.pc.intl.getDialogText("OK"));
			button.setBounds(200, 270, 100, 25);
			button.setName("OK");
			button.addActionListener(this);
			getContentPane().add(button);
		}
		{
			JButton button = new JButton(PCARD.pc.intl.getDialogText("Cancel"));
			button.setBounds(90, 270, 100, 25);
			button.setName("Cancel");
			button.addActionListener(this);
			getContentPane().add(button);
		}

		this.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	private final boolean isNear(int argb, Color color, int near) {
		if (Math.abs(color.getRed() - ((argb >> 16) & 0xFF)) > near) {
			return false;
		}
		if (Math.abs(color.getGreen() - ((argb >> 8) & 0xFF)) > near) {
			return false;
		}
		if (Math.abs(color.getBlue() - ((argb >> 0) & 0xFF)) > near) {
			return false;
		}
		return true;
	}

	private final boolean isNearHSV(Color color1, Color color2, int near) {
		float[] hsb1 = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
		float[] hsb2 = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);

		float d = 5 * Math.abs(hsb1[0] - hsb2[0]) * Math.max(0.0f, hsb1[1] + hsb2[1] - 0.1f)
				* Math.max(0.0f, hsb1[2] + hsb2[2] - 0.1f)
				+ 3 * Math.abs(hsb1[1] - hsb2[1]) * Math.max(0.0f, hsb1[2] + hsb2[2] - 0.1f)
				+ 2 * Math.abs(hsb1[2] - hsb2[2]);

		if ((int) (d * 30) > near)
			return false;
		return true;
	}

	private final boolean isNearHSV(int color1, Color color2, int near) {
		float[] hsb1 = Color.RGBtoHSB((color1 >> 16) & 0xFF, (color1 >> 8) & 0xFF, (color1 >> 0) & 0xFF, null);
		float[] hsb2 = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);

		float d = 5 * Math.abs(hsb1[0] - hsb2[0]) * Math.max(0.0f, hsb1[1] + hsb2[1] - 0.1f)
				* Math.max(0.0f, hsb1[2] + hsb2[2] - 0.1f)
				+ 3 * Math.abs(hsb1[1] - hsb2[1]) * Math.max(0.0f, hsb1[2] + hsb2[2] - 0.1f)
				+ 2 * Math.abs(hsb1[2] - hsb2[2]);

		if ((int) (d * 30) > near)
			return false;
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = ((JButton) e.getSource()).getName();
		String cmd = PCARD.pc.intl.getDialogEngText(name);
		if (cmd != null && cmd.equals("Cancel")) {
			owner.redoBuf = save_bi;
			this.dispose();
			owner.mainPane.repaint();
			return;
		} else if (cmd != null && cmd.equals("Divide")) {
			makeColors(near / 2 + 2);
		} else if (cmd != null && cmd.equals("Unite")) {
			makeColors((near - 2) * 2);
		}

		// キャプチャでの色選択を実現するために別スレッド
		ColorConvertThread thread = new ColorConvertThread(this, name, cmd, e);
		thread.start();
	}

	class ColorConvertThread extends Thread {
		ColorConvertDialog owner;
		String name;
		String cmd;
		ActionEvent e;

		ColorConvertThread(ColorConvertDialog owner, String name, String cmd, ActionEvent e) {
			super();
			this.owner = owner;
			this.name = name;
			this.cmd = cmd;
			this.e = e;
		}

		public void run() {

			if (name.startsWith("src")) {
				int number = Integer.valueOf(name.split(" ")[1]);
				// Color col = JColorChooser.showDialog(PaintTool.owner, "Source Color",
				// srcColorBest[number] );
				Color col = GColorDialog.getColor(null, srcColorBest[number], true);
				if (col != null) {
					srcColorBest[number] = col;
					BufferedImage icon_image = new BufferedImage(16, 12, BufferedImage.TYPE_INT_RGB);
					Graphics icon_g = icon_image.createGraphics();
					icon_g.setColor(srcColorBest[number]);
					icon_g.fillRect(0, 0, 16, 12);
					((JButton) e.getSource()).setIcon(new ImageIcon(icon_image));
				}
			} else if (name.startsWith("dst")) {
				int number = Integer.valueOf(name.split(" ")[1]);
				// Color col = JColorChooser.showDialog(PaintTool.owner, "Destination Color",
				// dstColorBest[number] );
				Color col = GColorDialog.getColor(null, dstColorBest[number], true);
				if (col != null) {
					dstColorBest[number] = col;
					BufferedImage icon_image = new BufferedImage(16, 12, BufferedImage.TYPE_INT_RGB);
					Graphics icon_g = icon_image.createGraphics();
					icon_g.setColor(dstColorBest[number]);
					icon_g.fillRect(0, 0, 16, 12);
					((JButton) e.getSource()).setIcon(new ImageIcon(icon_image));
				}
			}

			if (owner.owner.tool instanceof toolSelectInterface
					&& ((toolSelectInterface) owner.owner.tool).getSelectedSurface(owner.owner) != null) {
				// まず新しいバッファを作成してクリア
				BufferedImage bi2 = new BufferedImage(save_bi.getWidth(), save_bi.getHeight(),
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D dst_g = bi2.createGraphics();
				dst_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
				Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, save_bi.getWidth(), save_bi.getHeight());
				dst_g.fill(rect);
				dst_g = bi2.createGraphics();
				dst_g.drawImage(save_bi, 0, 0, null);

				// 操作
				DataBuffer surbuf = bi2.getRaster().getDataBuffer();
				if (cmd.equals("OK")) {
					toolSelectInterface tl = (toolSelectInterface) owner.owner.tool;
					surbuf = tl.getSelectedSurface(owner.owner).getRaster().getDataBuffer();
				}
				int width = bi2.getWidth();
				int height = bi2.getHeight();
				float[] srchsb = new float[3];
				float[] dsthsb = new float[3];
				float[] colhsb = new float[3];
				for (int v = 0; v < height; v++) {
					for (int h = 0; h < width; h++) {
						int c = surbuf.getElem(h + v * width);
						// srcColorBestに近い色相があれば色を変換
						for (int i = 0; i < srcColorBest.length; i++) {
							if (isNearHSV(c, srcColorBest[i], near + 2) && !dstColorBest[i].equals(srcColorBest[i])) {
								Color.RGBtoHSB(srcColorBest[i].getRed(), srcColorBest[i].getGreen(),
										srcColorBest[i].getBlue(), srchsb);
								Color.RGBtoHSB(dstColorBest[i].getRed(), dstColorBest[i].getGreen(),
										dstColorBest[i].getBlue(), dsthsb);
								Color.RGBtoHSB((c >> 16) & 0xFF, (c >> 8) & 0xFF, (c >> 0) & 0xFF, colhsb);
								float cols = colhsb[1] + dsthsb[1] - srchsb[1];
								if (cols < 0.0f)
									cols = 0.0f;
								else if (cols > 1.0f)
									cols = 1.0f;
								float colb = colhsb[2] + dsthsb[2] - srchsb[2];
								if (colb < 0.0f)
									colb = 0.0f;
								else if (colb > 1.0f)
									colb = 1.0f;
								float colh = colhsb[0] + dsthsb[0] - srchsb[0];
								if (srchsb[0] < 0.2) {
									colh = dsthsb[0];
								}
								Color newcolor = Color.getHSBColor(colh, cols, colb);
								int d = (c & 0xFF000000) + (newcolor.getRed() << 16) + (newcolor.getGreen() << 8)
										+ (newcolor.getBlue());
								surbuf.setElem(h + v * width, d);
								break;
							}
						}
					}
				}

				if (cmd.equals("OK")) {
					owner.dispose();// this.dispose();
					owner.owner.mainPane.repaint();
					return;
				}

				// 選択領域を新しくする
				owner.owner.redoBuf = bi2;
				owner.owner.mainPane.repaint();
			} else {
				// サーフェース全体

				// まずredoBufにコピー
				BufferedImage bi = owner.owner.redoBuf;
				if (bi == null) {
					bi = new BufferedImage(owner.owner.getSurface().getWidth(), owner.owner.getSurface().getHeight(),
							BufferedImage.TYPE_INT_ARGB);
				}
				Graphics2D dst_g = bi.createGraphics();
				dst_g.drawImage(owner.owner.getSurface(), 0, 0, null);

				// 操作(HSB空間での平行移動)
				DataBuffer surbuf = bi.getRaster().getDataBuffer();
				int width = bi.getWidth();
				int height = bi.getHeight();
				float[] srchsb = new float[3];
				float[] dsthsb = new float[3];
				float[] colhsb = new float[3];
				for (int v = 0; v < height; v++) {
					for (int h = 0; h < width; h++) {
						int c = surbuf.getElem(h + v * width);
						// srcColorBestに近い色相があれば色を変換
						for (int i = 0; i < srcColorBest.length; i++) {
							if (isNearHSV(c, srcColorBest[i], near + 2) && !dstColorBest[i].equals(srcColorBest[i])) {
								Color.RGBtoHSB(srcColorBest[i].getRed(), srcColorBest[i].getGreen(),
										srcColorBest[i].getBlue(), srchsb);
								Color.RGBtoHSB(dstColorBest[i].getRed(), dstColorBest[i].getGreen(),
										dstColorBest[i].getBlue(), dsthsb);
								Color.RGBtoHSB((c >> 16) & 0xFF, (c >> 8) & 0xFF, (c >> 0) & 0xFF, colhsb);
								float cols = colhsb[1] + dsthsb[1] - srchsb[1];
								if (cols < 0.0f)
									cols = 0.0f;
								else if (cols > 1.0f)
									cols = 1.0f;
								float colb = colhsb[2] + dsthsb[2] - srchsb[2];
								if (colb < 0.0f)
									colb = 0.0f;
								else if (colb > 1.0f)
									colb = 1.0f;
								float colh = colhsb[0] + dsthsb[0] - srchsb[0];
								if (srchsb[0] < 0.2) {
									colh = dsthsb[0];
								}
								Color newcolor = Color.getHSBColor(colh, cols, colb);
								int d = (c & 0xFF000000) + (newcolor.getRed() << 16) + (newcolor.getGreen() << 8)
										+ (newcolor.getBlue());
								surbuf.setElem(h + v * width, d);
								break;
							}
						}
					}
				}
				/*
				 * for(int v=0; v<height; v++){ //RGBでの平行移動 for(int h=0; h<width; h++){ int c =
				 * surbuf.getElem(h+v*width); int srcred = (c>>16)&0x00FF; int srcgreen =
				 * (c>>8)&0x00FF; int srcblue = (c>>0)&0x00FF; //srcColorBestに近い色相があれば色を変換
				 * for(int i=0; i<srcColorBest.length; i++){ if(isNearHSV(c, srcColorBest[i],
				 * near+2) && !dstColorBest[i].equals(srcColorBest[i])){ int dstred = srcred +
				 * dstColorBest[i].getRed() - srcColorBest[i].getRed(); int dstgreen = srcgreen
				 * + dstColorBest[i].getGreen() - srcColorBest[i].getGreen(); int dstblue =
				 * srcblue + dstColorBest[i].getBlue() - srcColorBest[i].getBlue();
				 * if(dstred>0xFF) dstred = 0xFF; else if(dstred<0) dstred = 0;
				 * if(dstgreen>0xFF) dstgreen = 0xFF; else if(dstgreen<0) dstgreen = 0;
				 * if(dstblue>0xFF) dstblue = 0xFF; else if(dstblue<0) dstblue = 0; int d =
				 * (c&0xFF000000) + ((0x00FF&dstred)<<16) + ((0x00FF&dstgreen)<<8) +
				 * (0x00FF&dstblue); surbuf.setElem(h+v*width, d); break; } } } }
				 */
				// redoBufとmainImgを入れ替え
				BufferedImage savebi = owner.owner.getSurface();

				// 選択領域を新しくする
				owner.owner.mainImg = bi;
				if (cmd.equals("OK")) {
					if (savebi != null) {
						owner.owner.redoBuf = savebi;
					}
					owner.dispose();// this.dispose();
					owner.owner.mainPane.repaint();
					return;
				}

				owner.owner.mainPane.paintImmediately(owner.owner.mainPane.getBounds());

				owner.owner.setSurface(savebi);
				if (bi != null) {
					owner.owner.redoBuf = bi;
				}
			}

		}
	}
}
