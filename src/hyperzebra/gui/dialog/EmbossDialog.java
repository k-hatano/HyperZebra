package hyperzebra.gui.dialog;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import hyperzebra.gui.PCARD;
import hyperzebra.gui.PCARDFrame;
import hyperzebra.gui.menu.GMenuPaint;
import hyperzebra.tool.SmartSelectTool;
import hyperzebra.tool.toolSelectInterface;

public class EmbossDialog extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private PCARDFrame owner;

	public EmbossDialog(PCARDFrame owner) {
		super();
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		this.owner = owner;
		getContentPane().setLayout(new BorderLayout());
		setTitle(PCARD.pc.intl.getDialogText("Emboss"));

		SetDialogContents();

		setBounds(owner.getX() + owner.getWidth() / 2 - 240, owner.getY() + owner.getHeight() / 2 - 240 - 20, 480, 480);

		setResizable(false);
		setVisible(true);

		previewImage("");
	}

	private void SetDialogContents() {
		this.getContentPane().removeAll();

		if (!(owner.tool instanceof toolSelectInterface)
				|| ((toolSelectInterface) owner.tool).getSelectedSurface(owner) == null) {
			GMenuPaint.setUndo();

			GMenuPaint.doMenu("Select All");
		}

		JPanel mainpanel = new JPanel();
		mainpanel.setLayout(new GridLayout(2, 2));
		getContentPane().add("Center", mainpanel);

		// 厚み -on/off
		// --明るさの変化(0-128)
		// --端からの距離(0-32)
		// --方向(x,y)
		{
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(4, 1));
			Border aquaBorder = UIManager.getBorder("TitledBorder.aquaVariant");
			if (aquaBorder == null) {
				aquaBorder = new EtchedBorder();
			}
			panel.setBorder(new TitledBorder(aquaBorder, PCARD.pc.intl.getDialogText("Thickness")));
			mainpanel.add(panel);

			{
				JCheckBox chkbox = new JCheckBox(PCARD.pc.intl.getDialogText("Use"));
				chkbox.setName("Thick-Use");
				chkbox.setPreferredSize(new Dimension(180, 24));
				chkbox.setSelected(useThick);
				chkbox.addActionListener(this);
				panel.add(chkbox);
			}

			{
				String name = PCARD.pc.intl.getDialogText("Brightness ");
				String[] value = new String[] { name + "0", name + "1", name + "2", name + "3", name + "4", name + "5",
						name + "6", name + "7", name + "8" };
				JComboBox combo = new JComboBox(value);
				combo.setName("Thick-Brightness");
				combo.setSelectedIndex(thickBright);
				combo.setMaximumRowCount(16);
				combo.setEnabled(useThick);
				combo.addActionListener(this);
				panel.add(combo);
			}

			{
				String name = PCARD.pc.intl.getDialogText("Width ");
				String[] value = new String[] { name + "0", name + "1", name + "2", name + "3", name + "4", name + "5",
						name + "6", name + "7", name + "8", name + "9", name + "10" };
				JComboBox combo = new JComboBox(value);
				combo.setName("Thick-Width");
				combo.setSelectedIndex(thickWidth);
				combo.setMaximumRowCount(16);
				combo.setEnabled(useThick);
				combo.addActionListener(this);
				panel.add(combo);
			}

			JPanel arrowpanel = new JPanel();
			arrowpanel.setLayout(new BoxLayout(arrowpanel, BoxLayout.X_AXIS));
			arrowpanel.setOpaque(false);
			panel.add(arrowpanel);

			{
				JToggleButton button = new JToggleButton("◀");
				button.setName("Thick-Left");
				button.setPreferredSize(new Dimension(32, 25));
				button.setEnabled(useThick);
				button.setSelected(thickLeft);
				button.addActionListener(this);
				arrowpanel.add(button);
			}
			{
				JToggleButton button = new JToggleButton("▶");
				button.setName("Thick-Right");
				button.setPreferredSize(new Dimension(32, 25));
				button.setEnabled(useThick);
				button.setSelected(thickRight);
				button.addActionListener(this);
				arrowpanel.add(button);
			}
			{
				JToggleButton button = new JToggleButton("▲");
				button.setName("Thick-Up");
				button.setPreferredSize(new Dimension(32, 25));
				button.setEnabled(useThick);
				button.setSelected(thickUp);
				button.addActionListener(this);
				arrowpanel.add(button);
			}
			{
				JToggleButton button = new JToggleButton("▼");
				button.setName("Thick-Down");
				button.setPreferredSize(new Dimension(32, 25));
				button.setEnabled(useThick);
				button.setSelected(thickDown);
				button.addActionListener(this);
				arrowpanel.add(button);
			}
		}

		// グラデーション -on/off
		// --明るさの変化(0-128)
		// --方向(0-360)
		{
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(4, 1));
			Border aquaBorder = UIManager.getBorder("TitledBorder.aquaVariant");
			if (aquaBorder == null) {
				aquaBorder = new EtchedBorder();
			}
			panel.setBorder(new TitledBorder(aquaBorder, PCARD.pc.intl.getDialogText("Gradation")));
			mainpanel.add(panel);

			{
				JCheckBox chkbox = new JCheckBox(PCARD.pc.intl.getDialogText("Use"));
				chkbox.setName("Gradation-Use");
				chkbox.setPreferredSize(new Dimension(180, 24));
				chkbox.setSelected(useGrad);
				chkbox.addActionListener(this);
				panel.add(chkbox);
			}

			{
				String name = PCARD.pc.intl.getDialogText("Brightness ");
				String[] value = new String[] { name + "0", name + "1", name + "2", name + "3", name + "4", name + "5",
						name + "6", name + "7", name + "8" };
				JComboBox combo = new JComboBox(value);
				combo.setName("Gradation-Brightness");
				combo.setSelectedIndex(gradBright);
				combo.setMaximumRowCount(16);
				combo.setEnabled(useGrad);
				combo.addActionListener(this);
				panel.add(combo);
			}

			JPanel arrowpanel = new JPanel();
			arrowpanel.setLayout(new BoxLayout(arrowpanel, BoxLayout.X_AXIS));
			arrowpanel.setOpaque(false);
			panel.add(arrowpanel);

			{
				JButton button = new JButton("◀");
				button.setName("Gradation-Left");
				button.setPreferredSize(new Dimension(32, 25));
				button.setEnabled(useGrad);
				button.addActionListener(this);
				arrowpanel.add(button);
			}
			{
				JButton button = new JButton("▶");
				button.setName("Gradation-Right");
				button.setPreferredSize(new Dimension(32, 25));
				button.setEnabled(useGrad);
				button.addActionListener(this);
				arrowpanel.add(button);
			}
		}

		// ハイライト -on/off
		// --検出強度
		// --明るさの変化(0-128)
		// --範囲(0-16)
		// --オフセット(x,y)
		{
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(4, 1));
			Border aquaBorder = UIManager.getBorder("TitledBorder.aquaVariant");
			if (aquaBorder == null) {
				aquaBorder = new EtchedBorder();
			}
			panel.setBorder(new TitledBorder(aquaBorder, PCARD.pc.intl.getDialogText("Highlight")));
			mainpanel.add(panel);

			{
				JCheckBox chkbox = new JCheckBox(PCARD.pc.intl.getDialogText("Use"));
				chkbox.setName("Highlight-Use");
				chkbox.setPreferredSize(new Dimension(180, 24));
				chkbox.setSelected(useHighlight);
				chkbox.addActionListener(this);
				panel.add(chkbox);
			}

			{
				String name = PCARD.pc.intl.getDialogText("Brightness ");
				String[] value = new String[] { name + "0", name + "1", name + "2", name + "3", name + "4", name + "5",
						name + "6", name + "7", name + "8" };
				JComboBox combo = new JComboBox(value);
				combo.setName("Highlight-Brightness");
				combo.setSelectedIndex(highlBright);
				combo.setMaximumRowCount(16);
				combo.addActionListener(this);
				panel.add(combo);
			}

			{
				String name = PCARD.pc.intl.getDialogText("Area ");
				String[] value = new String[] { name + "0", name + "1", name + "2", name + "3", name + "4", name + "5",
						name + "6", name + "7", name + "8" };
				JComboBox combo = new JComboBox(value);
				combo.setName("Highlight-Area");
				combo.setSelectedIndex(highlArea);
				combo.setMaximumRowCount(16);
				combo.addActionListener(this);
				panel.add(combo);
			}

			JPanel arrowpanel = new JPanel();
			arrowpanel.setLayout(new BoxLayout(arrowpanel, BoxLayout.X_AXIS));
			arrowpanel.setOpaque(false);
			panel.add(arrowpanel);

			{
				JButton button = new JButton("◀");
				button.setName("Highlight-Left");
				button.setPreferredSize(new Dimension(32, 25));
				button.addActionListener(this);
				arrowpanel.add(button);
			}
			{
				JButton button = new JButton("▶");
				button.setName("Highlight-Right");
				button.setPreferredSize(new Dimension(32, 25));
				button.addActionListener(this);
				arrowpanel.add(button);
			}
			{
				JButton button = new JButton("▲");
				button.setName("Highlight-Up");
				button.setPreferredSize(new Dimension(32, 25));
				button.addActionListener(this);
				arrowpanel.add(button);
			}
			{
				JButton button = new JButton("▼");
				button.setName("Highlight-Down");
				button.setPreferredSize(new Dimension(32, 25));
				button.addActionListener(this);
				arrowpanel.add(button);
			}
		}

		// 映り込み -on/off
		// --種類 (直線/弧)
		// --明るさの変化(0-128)
		// --オフセット(x,y)
		// --傾き(0-360)
		{
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(4, 1));
			Border aquaBorder = UIManager.getBorder("TitledBorder.aquaVariant");
			if (aquaBorder == null) {
				aquaBorder = new EtchedBorder();
			}
			panel.setBorder(new TitledBorder(aquaBorder, PCARD.pc.intl.getDialogText("Reflection")));
			mainpanel.add(panel);

			{
				String[] value = new String[] { PCARD.pc.intl.getDialogText("None"),
						PCARD.pc.intl.getDialogText("Line"), PCARD.pc.intl.getDialogText("Curve"),
						PCARD.pc.intl.getDialogText("Fit") };
				JComboBox combo = new JComboBox(value);
				combo.setName("Reflection-Style");
				combo.setSelectedIndex(1);
				combo.addActionListener(this);
				panel.add(combo);
			}

			{
				String name = PCARD.pc.intl.getDialogText("Brightness ");
				String[] value = new String[] { name + "0", name + "1", name + "2", name + "3", name + "4", name + "5",
						name + "6", name + "7", name + "8" };
				JComboBox combo = new JComboBox(value);
				combo.setName("Reflection-Brightness");
				combo.setSelectedIndex(3);
				combo.setMaximumRowCount(16);
				combo.addActionListener(this);
				panel.add(combo);
			}

			{
				String name = PCARD.pc.intl.getDialogText("Angle ");
				String[] value = new String[36];
				for (int i = 0; i < 36; i++) {
					value[i] = name + Integer.toString(i * 10);
				}
				JComboBox combo = new JComboBox(value);
				combo.setName("Reflection-Angle");
				combo.setSelectedIndex(3);
				combo.setMaximumRowCount(16);
				combo.addActionListener(this);
				panel.add(combo);
			}

			JPanel arrowpanel = new JPanel();
			arrowpanel.setLayout(new BoxLayout(arrowpanel, BoxLayout.X_AXIS));
			arrowpanel.setOpaque(false);
			panel.add(arrowpanel);

			{
				JButton button = new JButton("◀");
				button.setName("Reflection-Left");
				button.setPreferredSize(new Dimension(32, 25));
				button.addActionListener(this);
				arrowpanel.add(button);
			}
			{
				JButton button = new JButton("▶");
				button.setName("Reflection-Right");
				button.setPreferredSize(new Dimension(32, 25));
				button.addActionListener(this);
				arrowpanel.add(button);
			}
			{
				JButton button = new JButton("▲");
				button.setName("Reflection-Up");
				button.setPreferredSize(new Dimension(32, 25));
				button.addActionListener(this);
				arrowpanel.add(button);
			}
			{
				JButton button = new JButton("▼");
				button.setName("Reflection-Down");
				button.setPreferredSize(new Dimension(32, 25));
				button.addActionListener(this);
				arrowpanel.add(button);
			}
		}

		// 影は別のダイアログで。

		JPanel okpanel = new JPanel();
		getContentPane().add("South", okpanel);

		// ok, cancel
		{
			JButton button = new JButton(PCARD.pc.intl.getDialogText("Cancel"));
			button.setBounds(90, 270, 100, 25);
			button.setName("Cancel");
			button.addActionListener(this);
			okpanel.add(button);
		}
		{
			JButton button = new JButton(PCARD.pc.intl.getDialogText("OK"));
			button.setBounds(200, 270, 100, 25);
			button.setName("OK");
			button.addActionListener(this);
			okpanel.add(button);
		}

		this.getContentPane().repaint();
		this.setVisible(true);
	}

	private BufferedImage saveImage;

	private boolean useThick = true;
	private int thickBright = 3;
	private int thickWidth = 2;
	private boolean thickUp = true;
	private boolean thickDown = true;
	private boolean thickLeft = true;
	private boolean thickRight = true;

	private boolean useGrad = true;
	private int gradBright = 3;
	private float gradAngle = 0.0f;

	private boolean useHighlight = true;
	private int highlBright = 3;
	private int highlArea = 3;
	private Point highlOffset = new Point(0, 0);

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = ((JComponent) e.getSource()).getName();
		String cmd = PCARD.pc.intl.getDialogEngText(name);

		if (cmd == null)
			return;

		if (cmd.equals("Cancel")) {
			if (saveImage != null) {
				owner.redoBuf = saveImage;
				owner.mainPane.repaint();
			}
			this.dispose();
			owner.mainPane.repaint();
			return;
		} else if (cmd.equals("Thick-Use")) {
			useThick = ((JCheckBox) e.getSource()).isSelected();
			SetDialogContents();
		} else if (cmd.equals("Thick-Brightness")) {
			thickBright = ((JComboBox) e.getSource()).getSelectedIndex();
			SetDialogContents();
		} else if (cmd.equals("Thick-Width")) {
			thickWidth = ((JComboBox) e.getSource()).getSelectedIndex();
			SetDialogContents();
		} else if (cmd.equals("Thick-Up")) {
			thickUp = ((JToggleButton) e.getSource()).isSelected();
		} else if (cmd.equals("Thick-Down")) {
			thickDown = ((JToggleButton) e.getSource()).isSelected();
		} else if (cmd.equals("Thick-Left")) {
			thickLeft = ((JToggleButton) e.getSource()).isSelected();
		} else if (cmd.equals("Thick-Right")) {
			thickRight = ((JToggleButton) e.getSource()).isSelected();
		} else if (cmd.equals("Gradation-Use")) {
			useGrad = ((JCheckBox) e.getSource()).isSelected();
			SetDialogContents();
		} else if (cmd.equals("Gradation-Brightness")) {
			gradBright = ((JComboBox) e.getSource()).getSelectedIndex();
			SetDialogContents();
		} else if (cmd.equals("Gradation-Left")) {
			gradAngle += Math.PI / 18.0;
		} else if (cmd.equals("Gradation-Right")) {
			gradAngle -= Math.PI / 18.0;
		} else if (cmd.equals("Highlight-Use")) {
			useHighlight = ((JCheckBox) e.getSource()).isSelected();
			SetDialogContents();
		} else if (cmd.equals("Highlight-Brightness")) {
			highlBright = ((JComboBox) e.getSource()).getSelectedIndex();
			SetDialogContents();
		} else if (cmd.equals("Highlight-Area")) {
			highlArea = ((JComboBox) e.getSource()).getSelectedIndex();
			SetDialogContents();
		} else if (cmd.equals("Highlight-Up")) {
			highlOffset.y--;
		} else if (cmd.equals("Highlight-Down")) {
			highlOffset.y++;
		} else if (cmd.equals("Highlight-Left")) {
			highlOffset.x--;
		} else if (cmd.equals("Highlight-Right")) {
			highlOffset.x++;
		}

		this.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		previewImage(cmd);
	}

	private void previewImage(String cmd) {
		toolSelectInterface tl = (toolSelectInterface) owner.tool;
		BufferedImage srcimg = saveImage;
		if (saveImage == null) {
			srcimg = tl.getSelectedSurface(owner);
			saveImage = srcimg;
		}
		Rectangle srcRect = tl.getSelectedRect();

		BufferedImage newimg = new BufferedImage(srcimg.getWidth(), srcimg.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D newg = newimg.createGraphics();
		newg.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		newg.fillRect(0, 0, newimg.getWidth(), newimg.getHeight());

		DataBuffer srcdb = srcimg.getRaster().getDataBuffer();
		int width = srcimg.getWidth();
		// int height = srcimg.getHeight();

		/*
		 * if(useThick){ //Thickness //輪郭に近い部分は暗く、離れたところは明るい int realThickWidth = new
		 * int[]{1,2,3,4,6,8,10,14,20,30,50}[thickWidth]; for(int i=0; i<realThickWidth;
		 * i++){ for(int y=srcRect.y; y<srcRect.y+srcRect.height; y++){ for(int
		 * x=srcRect.x; x<srcRect.x+srcRect.width; x++){ boolean src =
		 * (srcdb.getElem(x+y*width)&0xFF000000)!=0; if(src && i==0){ //1回目は輪郭部分を塗る
		 * boolean srcup = false; if((y-1)>=srcRect.y) srcup =
		 * (srcdb.getElem(x+(y-1)*width)&0xFF000000)!=0; boolean srcdown = false;
		 * if((y+1)<srcRect.y+srcRect.height) srcdown =
		 * (srcdb.getElem(x+(y+1)*width)&0xFF000000)!=0; boolean srcleft = false;
		 * if((x-1)>=srcRect.x) srcleft = (srcdb.getElem(x-1+y*width)&0xFF000000)!=0;
		 * boolean srcright = false; if((x+1)<srcRect.x+srcRect.width) srcright =
		 * (srcdb.getElem(x+1+y*width)&0xFF000000)!=0;
		 * 
		 * if(!srcup&thickDown || !srcdown&thickUp || !srcleft&thickRight ||
		 * !srcright&thickLeft){ int v = srcdb.getElem(x+y*width); int red =
		 * (v>>16)&0xFF; int green = (v>>8)&0xFF; int blue = (v>>0)&0xFF; float[] hsb =
		 * Color.RGBtoHSB(red, green, blue, null); hsb[2] -=
		 * (double)thickBright*(realThickWidth-i)/realThickWidth/20.0f; if(hsb[2]<0.0f)
		 * hsb[2] = 0.0f; Color col = Color.getHSBColor(hsb[0], hsb[1], hsb[2]); int c =
		 * (0xFF000000&v)+(col.getRed()<<16)+(col.getGreen()<<8)+(col.getBlue());
		 * newdb.setElem(x+y*width, c); } } else if(src){ //2回目以降はすでに塗った部分のすぐ隣を順次塗っていく
		 * if((0xFF000000&newdb.getElem(x+y*width))==0){ boolean dstup = false;
		 * if((y-1)>=srcRect.y) dstup = (newdb.getElem(x+(y-1)*width)&0xFF000000)!=0;
		 * boolean dstdown = false; if((y+1)<srcRect.y+srcRect.height) dstdown =
		 * (newdb.getElem(x+(y+1)*width)&0xFF000000)!=0; boolean dstleft = false;
		 * if((x-1)>=srcRect.x) dstleft = (newdb.getElem(x-1+y*width)&0xFF000000)!=0;
		 * boolean dstright = false; if((x+1)<srcRect.x+srcRect.width) dstright =
		 * (newdb.getElem(x+1+y*width)&0xFF000000)!=0;
		 * 
		 * //幅 :←外側,内側→ //width1:-10*thickBright
		 * //width3:-10*thickBright,-5*thickBright,-0
		 * 
		 * if(dstup&thickDown || dstdown&thickUp || dstleft&thickRight ||
		 * dstright&thickLeft){ newdb.setElem(x+y*width, 0x00adbeef); } } } } } for(int
		 * y=srcRect.y; y<srcRect.y+srcRect.height; y++){ for(int x=srcRect.x;
		 * x<srcRect.x+srcRect.width; x++){ if(newdb.getElem(x+y*width)==0x00adbeef){
		 * int v = srcdb.getElem(x+y*width); int red = (v>>16)&0xFF; int green =
		 * (v>>8)&0xFF; int blue = (v>>0)&0xFF;
		 * 
		 * float[] hsb = Color.RGBtoHSB(red, green, blue, null); hsb[2] -=
		 * (double)thickBright*(realThickWidth-i)/realThickWidth/20.0f; if(hsb[2]<0.0f)
		 * hsb[2] = 0.0f; Color col = Color.getHSBColor(hsb[0], hsb[1], hsb[2]); int c =
		 * (0xFF000000&v)+(col.getRed()<<16)+(col.getGreen()<<8)+(col.getBlue());
		 * newdb.setElem(x+y*width, c); } } } } }
		 */

		/*
		 * ぼかしでどうにかできないか？ if(useThick){ //Thickness //輪郭に近い部分は暗く、離れたところは明るい
		 * 
		 * //輪郭を取得 DataBuffer newdb = newimg.getRaster().getDataBuffer(); for(int
		 * y=srcRect.y; y<srcRect.y+srcRect.height; y++){ for(int x=srcRect.x;
		 * x<srcRect.x+srcRect.width; x++){ boolean src =
		 * (srcdb.getElem(x+y*width)&0xFF000000)!=0; if(src){ //輪郭部分を塗る boolean srcup =
		 * false; if((y-1)>=srcRect.y) srcup =
		 * (srcdb.getElem(x+(y-1)*width)&0xFF000000)!=0; boolean srcdown = false;
		 * if((y+1)<srcRect.y+srcRect.height) srcdown =
		 * (srcdb.getElem(x+(y+1)*width)&0xFF000000)!=0; boolean srcleft = false;
		 * if((x-1)>=srcRect.x) srcleft = (srcdb.getElem(x-1+y*width)&0xFF000000)!=0;
		 * boolean srcright = false; if((x+1)<srcRect.x+srcRect.width) srcright =
		 * (srcdb.getElem(x+1+y*width)&0xFF000000)!=0;
		 * 
		 * if(!srcup&thickDown || !srcdown&thickUp || !srcleft&thickRight ||
		 * !srcright&thickLeft){ newdb.setElem(x+y*width, 0xFF000000); } } } }
		 * 
		 * //ぼかし効果 int realThickWidth = new
		 * int[]{1,2,3,4,5,7,10,15,20,30,50}[thickWidth]; for(int i=0;
		 * i<realThickWidth/5; i++){ //平滑化(濃いめ) final float[] operator={ 0.00f, 0.02f,
		 * 0.05f, 0.05f, 0.05f, 0.02f, 0.00f, 0.02f, 0.05f, 0.05f, 0.07f, 0.05f, 0.05f,
		 * 0.02f, 0.05f, 0.05f, 0.07f, 0.08f, 0.07f, 0.05f, 0.05f, 0.05f, 0.07f, 0.08f,
		 * 0.10f, 0.08f, 0.07f, 0.05f, 0.05f, 0.05f, 0.07f, 0.08f, 0.07f, 0.05f, 0.05f,
		 * 0.02f, 0.05f, 0.05f, 0.07f, 0.05f, 0.05f, 0.02f, 0.00f, 0.02f, 0.05f, 0.05f,
		 * 0.05f, 0.02f, 0.00f, }; if(!thickUp){ for(int y=0; y<2; y++){ for(int x=0;
		 * x<7; x++){ operator[7*y+x] = 0.0f; } } } if(!thickDown){ for(int y=5; y<7;
		 * y++){ for(int x=0; x<7; x++){ operator[7*y+x] = 0.0f; } } } if(!thickLeft){
		 * for(int y=0; y<7; y++){ for(int x=0; x<2; x++){ operator[7*y+x] = 0.0f; } } }
		 * if(!thickRight){ for(int y=0; y<7; y++){ for(int x=5; x<7; x++){
		 * operator[7*y+x] = 0.0f; } } } Kernel blur=new Kernel(7,7,operator);
		 * ConvolveOp convop=new ConvolveOp(blur,ConvolveOp.EDGE_NO_OP,null);
		 * newimg=convop.filter(newimg,null); } for(int i=0; i<realThickWidth%5; i++){
		 * //平滑化(濃いめ) final float[] operator={ 0.04f, 0.06f, 0.04f, 0.06f, 0.90f, 0.06f,
		 * 0.04f, 0.06f, 0.04f }; Kernel blur=new Kernel(3,3,operator); ConvolveOp
		 * convop=new ConvolveOp(blur,ConvolveOp.EDGE_NO_OP,null);
		 * newimg=convop.filter(newimg,null); }
		 * 
		 * //内部シャドウを塗る newdb = newimg.getRaster().getDataBuffer(); for(int
		 * y=srcRect.y-realThickWidth; y<srcRect.y+srcRect.height+realThickWidth; y++){
		 * for(int x=srcRect.x-realThickWidth; x<srcRect.x+srcRect.width+realThickWidth;
		 * x++){ if(x<0 || y<0 ||x>=width||y>=height) continue; boolean src =
		 * (srcdb.getElem(x+y*width)&0xFF000000)!=0; if(src){ int newc =
		 * newdb.getElem(x+y*width); if((0xFF000000&newc)==0){ newdb.setElem(x+y*width,
		 * srcdb.getElem(x+y*width)); } else{ int v = srcdb.getElem(x+y*width); int red
		 * = (v>>16)&0xFF; int green = (v>>8)&0xFF; int blue = (v>>0)&0xFF;
		 * 
		 * float[] hsb = Color.RGBtoHSB(red, green, blue, null); hsb[2] +=
		 * (float)(thickBright-3.5f)*((newc>>24)&0xFF)/realThickWidth/300.0f;
		 * if(hsb[2]>1.0f) hsb[2] = 1.0f; else if(hsb[2]<0.0f) hsb[2] = 0.0f; Color col
		 * = Color.getHSBColor(hsb[0], hsb[1], hsb[2]); int c =
		 * (0xFF000000&v)+(col.getRed()<<16)+(col.getGreen()<<8)+(col.getBlue());
		 * newdb.setElem(x+y*width, c); } } else{ newdb.setElem(x+y*width, 0x00000000);
		 * } } } }
		 */

		if (useThick) {
			// Thickness
			// 輪郭に近い部分は暗く、離れたところは明るい
			DataBuffer newdb = newimg.getRaster().getDataBuffer();

			int realThickWidth = new int[] { 1, 2, 3, 4, 6, 8, 10, 14, 20, 30, 50 }[thickWidth];
			for (int i = 0; i < realThickWidth; i++) {
				for (int y = srcRect.y; y < srcRect.y + srcRect.height; y++) {
					for (int x = srcRect.x; x < srcRect.x + srcRect.width; x++) {
						boolean src = (srcdb.getElem(x + y * width) & 0xFF000000) != 0;
						if (src && i == 0) {
							// 1回目は輪郭部分を塗る
							boolean srcup = false;
							if ((y - 1) >= srcRect.y)
								srcup = (srcdb.getElem(x + (y - 1) * width) & 0xFF000000) != 0;
							boolean srcdown = false;
							if ((y + 1) < srcRect.y + srcRect.height)
								srcdown = (srcdb.getElem(x + (y + 1) * width) & 0xFF000000) != 0;
							boolean srcleft = false;
							if ((x - 1) >= srcRect.x)
								srcleft = (srcdb.getElem(x - 1 + y * width) & 0xFF000000) != 0;
							boolean srcright = false;
							if ((x + 1) < srcRect.x + srcRect.width)
								srcright = (srcdb.getElem(x + 1 + y * width) & 0xFF000000) != 0;

							if (!srcup & thickDown || !srcdown & thickUp || !srcleft & thickRight
									|| !srcright & thickLeft) {
								int v = srcdb.getElem(x + y * width);
								int red = (v >> 16) & 0xFF;
								int green = (v >> 8) & 0xFF;
								int blue = (v >> 0) & 0xFF;
								float[] hsb = Color.RGBtoHSB(red, green, blue, null);
								hsb[2] -= (double) thickBright * (realThickWidth - i) / realThickWidth / 20.0f;
								if (hsb[2] < 0.0f)
									hsb[2] = 0.0f;
								Color col = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
								int c = (0xFF000000 & v) + (col.getRed() << 16) + (col.getGreen() << 8)
										+ (col.getBlue());
								newdb.setElem(x + y * width, c);
							}
						} else if (src) {
							// 2回目以降はすでに塗った部分のすぐ隣を順次塗っていく
							if ((0xFF000000 & newdb.getElem(x + y * width)) == 0) {
								boolean dstup = false;
								if ((y - 1) >= srcRect.y)
									dstup = (newdb.getElem(x + (y - 1) * width) & 0xFF000000) != 0;
								boolean dstdown = false;
								if ((y + 1) < srcRect.y + srcRect.height)
									dstdown = (newdb.getElem(x + (y + 1) * width) & 0xFF000000) != 0;
								boolean dstleft = false;
								if ((x - 1) >= srcRect.x)
									dstleft = (newdb.getElem(x - 1 + y * width) & 0xFF000000) != 0;
								boolean dstright = false;
								if ((x + 1) < srcRect.x + srcRect.width)
									dstright = (newdb.getElem(x + 1 + y * width) & 0xFF000000) != 0;

								// 幅 :←外側,内側→
								// width1:-10*thickBright
								// width3:-10*thickBright,-5*thickBright,-0

								if (dstup & thickDown || dstdown & thickUp || dstleft & thickRight
										|| dstright & thickLeft) {
									newdb.setElem(x + y * width, 0x00adbeef);
								} else if (i % 3 == 0) {
									// 斜め方向の隣り合ったピクセルも調べる
									boolean dstul = false;
									if ((y - 1) >= srcRect.y && (x - 1) >= srcRect.x)
										dstul = (newdb.getElem(x - 1 + (y - 1) * width) & 0xFF000000) != 0;
									boolean dstur = false;
									if ((y - 1) >= srcRect.y && (x + 1) < srcRect.x + srcRect.width)
										dstur = (newdb.getElem(x + 1 + (y - 1) * width) & 0xFF000000) != 0;
									boolean dstdl = false;
									if ((y + 1) < srcRect.y + srcRect.height && (x - 1) >= srcRect.x)
										dstdl = (newdb.getElem(x - 1 + (y + 1) * width) & 0xFF000000) != 0;
									boolean dstdr = false;
									if ((y + 1) < srcRect.y + srcRect.height && (x + 1) < srcRect.x + srcRect.width)
										dstdr = (newdb.getElem(x + 1 + (y + 1) * width) & 0xFF000000) != 0;

									if (dstul & (thickDown || thickRight) || dstur & (thickDown || thickLeft)
											|| dstdl & (thickUp || thickRight) || dstdr & (thickUp || thickLeft)) {
										newdb.setElem(x + y * width, 0x00adbeef);
									}
								}

							}
						}
					}
				}
				for (int y = srcRect.y; y < srcRect.y + srcRect.height; y++) {
					for (int x = srcRect.x; x < srcRect.x + srcRect.width; x++) {
						if (newdb.getElem(x + y * width) == 0x00adbeef) {
							int v = srcdb.getElem(x + y * width);
							int red = (v >> 16) & 0xFF;
							int green = (v >> 8) & 0xFF;
							int blue = (v >> 0) & 0xFF;

							float[] hsb = Color.RGBtoHSB(red, green, blue, null);
							hsb[2] -= (double) thickBright * (realThickWidth - i) / realThickWidth / 20.0f;
							hsb[1] += 2 * (double) thickBright * (realThickWidth - i) / realThickWidth / 20.0f;
							if (hsb[2] < 0.0f)
								hsb[2] = 0.0f;
							if (hsb[1] > 1.0f)
								hsb[1] = 1.0f;
							Color col = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
							int c = (0xFF000000 & v) + (col.getRed() << 16) + (col.getGreen() << 8) + (col.getBlue());
							newdb.setElem(x + y * width, c);
						}
					}
				}
			}
		}

		// 塗り残しを塗る
		DataBuffer newdb = newimg.getRaster().getDataBuffer();
		for (int y = srcRect.y; y < srcRect.y + srcRect.height; y++) {
			for (int x = srcRect.x; x < srcRect.x + srcRect.width; x++) {
				int v = srcdb.getElem(x + y * width);
				if ((v & 0xff000000) != 0) {
					if ((newdb.getElem(x + y * width) & 0xFF000000) == 0) {
						newdb.setElem(x + y * width, v);
					}
				}
			}
		}

		if (useGrad) {
			// Gradation
			// 一方向への単純なグラデーション

			Point topPoint = null;
			Point bottomPoint = null;
			for (int y = srcRect.y; y < srcRect.y + srcRect.height; y++) {
				for (int x = srcRect.x; x < srcRect.x + srcRect.width; x++) {
					if ((0xFF000000 & srcdb.getElem(0, x + y * width)) != 0) {
						if (topPoint == null) {
							topPoint = new Point(x, y);
							bottomPoint = new Point(x, y);
						}
						if (x * Math.sin(gradAngle) + y * Math.cos(gradAngle) < topPoint.x * Math.sin(gradAngle)
								+ topPoint.y * Math.cos(gradAngle)) {
							topPoint.x = x;
							topPoint.y = y;
						}
						if (x * Math.sin(gradAngle) + y * Math.cos(gradAngle) > bottomPoint.x * Math.sin(gradAngle)
								+ bottomPoint.y * Math.cos(gradAngle)) {
							bottomPoint.x = x;
							bottomPoint.y = y;
						}
					}
				}
			}

			newdb = newimg.getRaster().getDataBuffer();
			for (int y = srcRect.y; y < srcRect.y + srcRect.height; y++) {
				for (int x = srcRect.x; x < srcRect.x + srcRect.width; x++) {
					int newc = newdb.getElem(x + y * width);
					if ((newc & 0xFF000000) != 0) {
						int c = 0;
						double percent;
						percent = (x * Math.sin(gradAngle) + y * Math.cos(gradAngle)
								- (topPoint.x * Math.sin(gradAngle) + topPoint.y * Math.cos(gradAngle)))
								/ (bottomPoint.x * Math.sin(gradAngle) + bottomPoint.y * Math.cos(gradAngle)
										- (topPoint.x * Math.sin(gradAngle) + topPoint.y * Math.cos(gradAngle)));
						percent = 1.0 - (1.0 - percent) / (10 - gradBright);
						if (percent > 1.0)
							percent = 1.0;
						c = ((int) (((newc >> 16) & 0xFF) * percent + 255 * (1.0 - percent))) << 16;
						c += ((int) (((newc >> 8) & 0xFF) * percent + 255 * (1.0 - percent))) << 8;
						c += ((int) (((newc >> 0) & 0xFF) * percent + 255 * (1.0 - percent)));
						newdb.setElem(0, x + y * width, 0xFF000000 + c);
					}
				}
			}
		}

		if (useHighlight) {
			// Highlight
			// てかり

			Point topPoint = null;
			Point bottomPoint = null;
			for (int y = srcRect.y; y < srcRect.y + srcRect.height; y++) {
				for (int x = srcRect.x; x < srcRect.x + srcRect.width; x++) {
					if ((0xFF000000 & srcdb.getElem(0, x + y * width)) != 0) {
						if (topPoint == null) {
							topPoint = new Point(x, y);
							bottomPoint = new Point(x, y);
						}
						if (x * Math.sin(gradAngle) + y * Math.cos(gradAngle) < topPoint.x * Math.sin(gradAngle)
								+ topPoint.y * Math.cos(gradAngle)) {
							topPoint.x = x;
							topPoint.y = y;
						}
						if (x * Math.sin(gradAngle) + y * Math.cos(gradAngle) > bottomPoint.x * Math.sin(gradAngle)
								+ bottomPoint.y * Math.cos(gradAngle)) {
							bottomPoint.x = x;
							bottomPoint.y = y;
						}
					}
				}
			}

			newdb = newimg.getRaster().getDataBuffer();
			for (int y = srcRect.y; y < srcRect.y + srcRect.height; y++) {
				for (int x = srcRect.x; x < srcRect.x + srcRect.width; x++) {
					int newc = newdb.getElem(x + y * width);
					if ((newc & 0xFF000000) != 0) {
						int c = 0;
						double percent;
						percent = (x * Math.sin(gradAngle) + y * Math.cos(gradAngle)
								- (topPoint.x * Math.sin(gradAngle) + topPoint.y * Math.cos(gradAngle)))
								/ (bottomPoint.x * Math.sin(gradAngle) + bottomPoint.y * Math.cos(gradAngle)
										- (topPoint.x * Math.sin(gradAngle) + topPoint.y * Math.cos(gradAngle)));
						percent = 1.0 - (1.0 - percent) / (10 - gradBright);
						if (percent > 1.0)
							percent = 1.0;
						c = ((int) (((newc >> 16) & 0xFF) * percent + 255 * (1.0 - percent))) << 16;
						c += ((int) (((newc >> 8) & 0xFF) * percent + 255 * (1.0 - percent))) << 8;
						c += ((int) (((newc >> 0) & 0xFF) * percent + 255 * (1.0 - percent)));
						newdb.setElem(0, x + y * width, 0xFF000000 + c);
					}
				}
			}
		}

		// 選択領域を新しくする
		owner.redoBuf = newimg;
		owner.mainPane.repaint();

		if (cmd.equals("OK")) {
			if (tl.getClass() == SmartSelectTool.class) {
				((SmartSelectTool) tl).srcbits = newimg;
			}
			this.dispose();
			owner.mainPane.repaint();
			return;
		}

		this.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

	}

}
