import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.Kernel;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JViewport;


//�y�C���g���̃��j���[����
class GMenuPaint implements ActionListener {
	@Override
	public void actionPerformed (ActionEvent e) {
		String cmd = e.getActionCommand();

		boolean result = doMenu(cmd);
		if(result==false){
			try {
				GMenuBrowse.doMenu(cmd);
			} catch (xTalkException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static boolean doMenu(String in_cmd)
	{
		String cmd = PCARDFrame.pc.intl.getEngText(in_cmd);
		
		if(cmd.equalsIgnoreCase("Undo Paint")){
			PaintTool.owner.tool.clear();
			//redoBuf�ɃC���[�W���ڂ�
			PaintTool.owner.redoBuf.setData(PaintTool.owner.getSurface().getData());
			//mainImg��undoBuf������
			PaintTool.owner.getSurface().setData(PaintTool.owner.undoBuf.getData());

			if(PaintTool.owner == PCARDFrame.pc){
				GMenu.changeEnabled("Edit","Undo Paint",false);
				GMenu.changeEnabled("Edit","Redo Paint",true);
			}else{
				IEMenu.changeEnabled("Edit","Undo Paint",false);
				IEMenu.changeEnabled("Edit","Redo Paint",true);
			}

			/*Graphics g = PaintTool.owner.mainPane.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0,0,PaintTool.owner.mainPane.getWidth(),PaintTool.owner.mainPane.getHeight());
			PaintTool.owner.mainPane.getGraphics().drawImage(PaintTool.owner.getSurface(), 0, 0, PaintTool.owner.mainPane);*/
			PaintTool.owner.mainPane.repaint();
		}
		else if(cmd.equalsIgnoreCase("Redo Paint")){
			setUndo();
			//mainImg��redoBuf������
			PaintTool.owner.getSurface().setData(PaintTool.owner.redoBuf.getData());

			/*Graphics g = PaintTool.owner.mainPane.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0,0,PaintTool.owner.mainPane.getWidth(),PaintTool.owner.mainPane.getHeight());
			PaintTool.owner.mainPane.getGraphics().drawImage(PaintTool.owner.getSurface(), 0, 0, PaintTool.owner.mainPane);*/
			PaintTool.owner.mainPane.repaint();
		}
		else if(cmd.equalsIgnoreCase("Antialias")){
			PaintTool.antialias = !PaintTool.antialias;
			GMenu.changeSelected("Paint","Antialias",PaintTool.antialias);
		}
		else if(cmd.equalsIgnoreCase("Save as ppm�c")){
			PaintTool.owner.tool.end();
			PaintTool.owner.tool.clear();
			PictureFile.saveAsPpm(PaintTool.owner.getSurface(), null);
		}
		else if(cmd.equalsIgnoreCase("Background")){
			if(PaintTool.owner.tool!=null){
				PaintTool.owner.tool.end();
				PaintTool.owner.tool.clear();
			}
			
			PaintTool.editBackground = !PaintTool.editBackground;
			GMenu.changeSelected("Edit","Background",PaintTool.editBackground);

			PCARDFrame.pc.mainPane.repaint();

			String titleName = PCARDFrame.pc.stack.name;
			if(titleName.substring(titleName.length()-5).equals(".xstk")){
				titleName = titleName.substring(0,titleName.length()-5);
			}
			if(PaintTool.editBackground){
				PCARDFrame.pc.setTitle("/// "+titleName+" ///");
			}else{
				PCARDFrame.pc.setTitle(titleName);
			}
		}
		else if(cmd.equalsIgnoreCase("Clear Selection")){
			PaintTool.owner.tool.clear();
			PaintTool.owner.mainPane.repaint();
		}
		else if(cmd.equalsIgnoreCase("Cut Picture")){
			if(PaintTool.owner.tool.getClass() == SelectTool.class){
				if(((SelectTool)PaintTool.owner.tool).move == true){
					setClipboardImage(PaintTool.owner.redoBuf, ((SelectTool)PaintTool.owner.tool).srcRect.width, ((SelectTool)PaintTool.owner.tool).srcRect.height);
					
					PaintTool.owner.tool.clear();
				}
				else System.out.println("Error");
			}
			else if(PaintTool.owner.tool.getClass() == LassoTool.class){
				if(((LassoTool)PaintTool.owner.tool).move == true){
					setClipboardImage(PaintTool.owner.redoBuf, PaintTool.owner.redoBuf.getWidth(), PaintTool.owner.redoBuf.getHeight());
				}
				else System.out.println("Error");
				
				PaintTool.owner.tool.clear();
			}
			else if(PaintTool.owner.tool.getClass() == SmartSelectTool.class){
				if(((SmartSelectTool)PaintTool.owner.tool).move == true){
					setClipboardImage(PaintTool.owner.redoBuf, PaintTool.owner.redoBuf.getWidth(), PaintTool.owner.redoBuf.getHeight());
				}
				else System.out.println("Error");
				
				PaintTool.owner.tool.clear();
			}
			else System.out.println("Error");
			PaintTool.owner.mainPane.repaint();
		}
		else if(cmd.equalsIgnoreCase("Copy Picture")){
			if(PaintTool.owner.tool.getClass() == SelectTool.class){
				if(((SelectTool)PaintTool.owner.tool).move == true){
					setClipboardImage(PaintTool.owner.redoBuf, ((SelectTool)PaintTool.owner.tool).srcRect.width, ((SelectTool)PaintTool.owner.tool).srcRect.height);
				}
				else System.out.println("Error");
			}
			else if(PaintTool.owner.tool.getClass() == LassoTool.class){
				if(((LassoTool)PaintTool.owner.tool).move == true){
					setClipboardImage(PaintTool.owner.redoBuf, PaintTool.owner.redoBuf.getWidth(), PaintTool.owner.redoBuf.getHeight());
				}
				else System.out.println("Error");
			}
			else if(PaintTool.owner.tool.getClass() == SmartSelectTool.class){
				if(((SmartSelectTool)PaintTool.owner.tool).move == true){
					setClipboardImage(PaintTool.owner.redoBuf, PaintTool.owner.redoBuf.getWidth(), PaintTool.owner.redoBuf.getHeight());
				}
				else System.out.println("Error");
			}
			else System.out.println("Error");
		}
		else if(cmd.equalsIgnoreCase("Paste Picture")){
			PaintTool.owner.tool.end();
			Image img = getClipboardImage();
			if(img!=null){
				int width = img.getWidth(PaintTool.owner);
				int height = img.getHeight(PaintTool.owner);
				PaintTool.owner.redoBuf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = PaintTool.owner.redoBuf.createGraphics();
				g.setColor(Color.WHITE);
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
				g.fillRect(0, 0, width, height);
				g = PaintTool.owner.redoBuf.createGraphics();
				g.drawImage(img, 0, 0, null);
				PaintTool.owner.tool = new SelectTool();
				((SelectTool)PaintTool.owner.tool).srcRect = new Rectangle(0,0,width,height);
				if(PaintTool.owner == PCARD.pc){
					((SelectTool)PaintTool.owner.tool).moveRect = new Rectangle((int)PaintTool.owner.bitLeft,(int)PaintTool.owner.bitTop,width,height);
				}
				else {
					((SelectTool)PaintTool.owner.tool).moveRect = new Rectangle(
							((IconEditor)PaintTool.owner).scrollpane.getHorizontalScrollBar().getValue()/PaintTool.owner.bit,
							((IconEditor)PaintTool.owner).scrollpane.getVerticalScrollBar().getValue()/PaintTool.owner.bit,
							width,height);
				}
				((SelectTool)PaintTool.owner.tool).move = true;
				((SelectTool)PaintTool.owner.tool).viewSelection();
			}
			PaintTool.owner.mainPane.repaint();
		}
		else if(cmd.equalsIgnoreCase("Select All")){
			PaintTool.owner.tool.end();
			Graphics2D g = PaintTool.owner.redoBuf.createGraphics();
			g.setColor(Color.WHITE);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			g.fillRect(0, 0, PaintTool.owner.mainImg.getWidth(), PaintTool.owner.mainImg.getHeight());
			g = PaintTool.owner.redoBuf.createGraphics();
			g.drawImage(PaintTool.owner.getSurface(), 0, 0, null);
			Graphics2D maing = PaintTool.owner.getSurface().createGraphics();
			maing.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			maing.fillRect(0,0,PaintTool.owner.getSurface().getWidth(), PaintTool.owner.getSurface().getHeight());
			PaintTool.owner.tool = new SelectTool();
			((SelectTool)PaintTool.owner.tool).srcRect = new Rectangle(0,0, PaintTool.owner.mainImg.getWidth(), PaintTool.owner.mainImg.getHeight());
			((SelectTool)PaintTool.owner.tool).moveRect = new Rectangle(0,0, PaintTool.owner.mainImg.getWidth(), PaintTool.owner.mainImg.getHeight());
			((SelectTool)PaintTool.owner.tool).move = true;
			((SelectTool)PaintTool.owner.tool).viewSelection();
		}
		else if(cmd.equalsIgnoreCase("FatBits")){
			if(PaintTool.owner.bit>1){
				PaintTool.owner.bit = 1;
			}
			else
			{
				if(PCARDFrame.pc == PaintTool.owner){
					PaintTool.owner.bit = 8;
					PaintTool.owner.bitLeft = PaintTool.lastx[0]-30;
					if(PaintTool.owner.bitLeft<0) PaintTool.owner.bitLeft = 0;
					PaintTool.owner.bitTop = PaintTool.lasty[0]-30;
					if(PaintTool.owner.bitTop<0) PaintTool.owner.bitTop = 0;
				}
				else if(PaintTool.owner.mainImg.getWidth()<128 && PaintTool.owner.mainImg.getHeight()<128){
					PaintTool.owner.bit = 8;
				}
				else if(PaintTool.owner.mainImg.getWidth()<320 && PaintTool.owner.mainImg.getHeight()<320){
					PaintTool.owner.bit = 4;
				}
				else{
					PaintTool.owner.bit = 4;//2;
				}
			}
			
			if(PCARDFrame.pc != PaintTool.owner){ //IconEditor
				JViewport vp = (JViewport)PaintTool.owner.mainPane.getParent();
				((JScrollPane)vp.getParent()).setViewportView(PaintTool.owner.mainPane);
				
				Dimension size = PaintTool.owner.getSize();
				int sw = size.width-160;
				int sh = size.height-PaintTool.owner.getInsets().top;
				int rate = PaintTool.owner.bit;
				if(sw > PaintTool.owner.mainImg.getWidth()*rate+20) sw = PaintTool.owner.mainImg.getWidth()*rate+20;
				if(sh > PaintTool.owner.mainImg.getHeight()*rate+20) sh = PaintTool.owner.mainImg.getHeight()*rate+20;

				PaintTool.owner.mainPane.setPreferredSize( new Dimension
						(PaintTool.owner.mainImg.getWidth()*PaintTool.owner.bit,
						PaintTool.owner.mainImg.getHeight()*PaintTool.owner.bit));
				
				((JScrollPane)vp.getParent()).setBounds(((size.width-160)-sw)/2, ((size.height-PaintTool.owner.getInsets().top)-sh)/2, sw, sh);
			}
			
			PaintTool.owner.mainPane.repaint();
			GMenu.changeSelected("Paint","FatBits",(PaintTool.owner.bit>1));
		}
		else if(cmd.equalsIgnoreCase("Flip Horizontal")){
			
			if(PaintTool.owner.tool instanceof toolSelectInterface &&
					((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
				BufferedImage bi = tl.getSelectedSurface(PaintTool.owner);
				
				//�܂��V�����o�b�t�@���쐬���ăN���A
				BufferedImage bi2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D dst_g = bi2.createGraphics();
				dst_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
				Rectangle2D.Double rect = new Rectangle2D.Double(0,0,bi.getWidth(), bi.getHeight());
				dst_g.fill(rect);
				
				//�I��̈�̍��E���]
				Graphics2D dst_g2 = bi2.createGraphics();
				AffineTransform af = new AffineTransform();
				af.scale(-1.0f, 1.0f);
				
				if(tl.getClass()==SelectTool.class){
					Rectangle srcRect = tl.getSelectedRect();

					af.translate(-srcRect.width, 0);
					dst_g2.transform(af);
					dst_g2.drawImage(bi, 0, 0, srcRect.width, srcRect.height, 0, 0, srcRect.width, srcRect.height, null);

					bi.flush();
				}
				else if(tl.getClass()==LassoTool.class){
					af.translate(-bi.getWidth(), 0);
					dst_g2.transform(af);
					dst_g2.drawImage(bi, 0, 0, null);

					//newSrcBits�N���A
					BufferedImage newbi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D newsrcbits_g = newbi.createGraphics();
					newsrcbits_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					Rectangle2D.Double nrect = new Rectangle2D.Double(0,0,bi.getWidth(), bi.getHeight());
					newsrcbits_g.fill(nrect);
					
					//srcBits�����]������
					Rectangle srcRect = tl.getSelectedRect();
					
					af = new AffineTransform();
					af.scale(-1.0f, 1.0f);
					af.translate(-newbi.getWidth(), 0);
					LassoTool lassotl = (LassoTool)tl;
					Graphics2D newsrcbits_g2 = newbi.createGraphics();
					newsrcbits_g2.transform(af);
					newsrcbits_g2.drawImage(lassotl.srcbits, 0, 0, null);
					lassotl.srcbits.flush();
					lassotl.srcbits = newbi;
					
					//
					int oldLeft = srcRect.x;
					int flipLeft = bi.getWidth()-(srcRect.x+srcRect.width);
					lassotl.movePoint.x += oldLeft-flipLeft;
				}
				else if(tl.getClass()==SmartSelectTool.class){
					af.translate(-bi.getWidth(), 0);
					dst_g2.transform(af);
					dst_g2.drawImage(bi, 0, 0, null);

					//newSrcBits�N���A
					BufferedImage newbi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D newsrcbits_g = newbi.createGraphics();
					newsrcbits_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					Rectangle2D.Double nrect = new Rectangle2D.Double(0,0,bi.getWidth(), bi.getHeight());
					newsrcbits_g.fill(nrect);
					
					//srcBits�����]������
					Rectangle srcRect = tl.getSelectedRect();
					
					af = new AffineTransform();
					af.scale(-1.0f, 1.0f);
					af.translate(-newbi.getWidth(), 0);
					SmartSelectTool smarttl = (SmartSelectTool)tl;
					Graphics2D newsrcbits_g2 = newbi.createGraphics();
					newsrcbits_g2.transform(af);
					newsrcbits_g2.drawImage(smarttl.srcbits, 0, 0, null);
					smarttl.srcbits.flush();
					smarttl.srcbits = newbi;
					
					//
					int oldLeft = srcRect.x;
					int flipLeft = bi.getWidth()-(srcRect.x+srcRect.width);
					smarttl.movePoint.x += oldLeft-flipLeft;
				}
				
				//�I��̈��V��������
				PaintTool.owner.redoBuf = bi2;
				
				PaintTool.owner.mainPane.repaint();
				
			}
			else{
				//�T�[�t�F�[�X�S�̂̍��E���]
				setUndo();
				
				//�܂�redoBuf���N���A
				BufferedImage bi = PaintTool.owner.redoBuf;
				Graphics2D dst_g = bi.createGraphics();
				dst_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
				Rectangle2D.Double rect = new Rectangle2D.Double(0,0,bi.getWidth(), bi.getHeight());
				dst_g.fill(rect);
				
				//���E���]
				Graphics2D dst_g2 = bi.createGraphics();
				AffineTransform af = new AffineTransform();
				af.scale(-1.0f, 1.0f);
				af.translate(-bi.getWidth(), 0);
				dst_g2.transform(af);
				dst_g2.drawImage(PaintTool.owner.getSurface(), 0, 0, null);
				
				//redoBuf��mainImg�����ւ�
				BufferedImage savebi = PaintTool.owner.getSurface();
				PaintTool.owner.redoBuf = savebi;
				
				//�I��̈��V��������
				PaintTool.owner.setSurface(bi);
				PaintTool.owner.mainPane.repaint();
			}
		}
		else if(cmd.equalsIgnoreCase("Flip Vertical")){
			
			if(PaintTool.owner.tool instanceof toolSelectInterface &&
					((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
				BufferedImage bi = tl.getSelectedSurface(PaintTool.owner);
				
				//�܂��V�����o�b�t�@���쐬���ăN���A
				BufferedImage bi2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D dst_g = bi2.createGraphics();
				dst_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
				Rectangle2D.Double rect = new Rectangle2D.Double(0,0,bi.getWidth(), bi.getHeight());
				dst_g.fill(rect);
				
				//�I��̈�̏㉺���]
				Graphics2D dst_g2 = bi2.createGraphics();
				AffineTransform af = new AffineTransform();
				af.scale(1.0f, -1.0f);
				
				if(tl.getClass()==SelectTool.class){
					Rectangle srcRect = tl.getSelectedRect();

					af.translate(0, -srcRect.height);
					dst_g2.transform(af);
					dst_g2.drawImage(bi, 0, 0, srcRect.width, srcRect.height, 0, 0, srcRect.width, srcRect.height, null);
				}
				else if(tl.getClass()==LassoTool.class){
					af.translate(0, -bi.getHeight());
					dst_g2.transform(af);
					dst_g2.drawImage(bi, 0, 0, null);

					//newSrcBits�N���A
					BufferedImage newbi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D newsrcbits_g = newbi.createGraphics();
					newsrcbits_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					Rectangle2D.Double nrect = new Rectangle2D.Double(0,0,bi.getWidth(), bi.getHeight());
					newsrcbits_g.fill(nrect);
					
					//srcBits�����]������
					Rectangle srcRect = tl.getSelectedRect();
					
					af = new AffineTransform();
					af.scale(1.0f, -1.0f);
					af.translate(0, -newbi.getHeight());
					LassoTool lassotl = (LassoTool)tl;
					Graphics2D newsrcbits_g2 = newbi.createGraphics();
					newsrcbits_g2.transform(af);
					newsrcbits_g2.drawImage(lassotl.srcbits, 0, 0, null);
					lassotl.srcbits.flush();
					lassotl.srcbits = newbi;
					
					//
					int oldTop = srcRect.y;
					int flipTop = bi.getHeight()-(srcRect.y+srcRect.height);
					lassotl.movePoint.y += oldTop-flipTop;
				}
				else if(tl.getClass()==SmartSelectTool.class){
					af.translate(0, -bi.getHeight());
					dst_g2.transform(af);
					dst_g2.drawImage(bi, 0, 0, null);

					//newSrcBits�N���A
					BufferedImage newbi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D newsrcbits_g = newbi.createGraphics();
					newsrcbits_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					Rectangle2D.Double nrect = new Rectangle2D.Double(0,0,bi.getWidth(), bi.getHeight());
					newsrcbits_g.fill(nrect);
					
					//srcBits�����]������
					Rectangle srcRect = tl.getSelectedRect();
					
					af = new AffineTransform();
					af.scale(1.0f, -1.0f);
					af.translate(0, -newbi.getHeight());
					SmartSelectTool smarttl = (SmartSelectTool)tl;
					Graphics2D newsrcbits_g2 = newbi.createGraphics();
					newsrcbits_g2.transform(af);
					newsrcbits_g2.drawImage(smarttl.srcbits, 0, 0, null);
					smarttl.srcbits.flush();
					smarttl.srcbits = newbi;
					
					//
					int oldTop = srcRect.y;
					int flipTop = bi.getHeight()-(srcRect.y+srcRect.height);
					smarttl.movePoint.y += oldTop-flipTop;
				}
				
				//�I��̈��V��������
				PaintTool.owner.redoBuf = bi2;
				
				PaintTool.owner.mainPane.repaint();
				
				bi.flush();
			}
			else{
				//�T�[�t�F�[�X�S�̂̏㉺���]
				setUndo();
				
				//�܂�redoBuf���N���A
				BufferedImage bi = PaintTool.owner.redoBuf;
				Graphics2D dst_g = bi.createGraphics();
				dst_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
				Rectangle2D.Double rect = new Rectangle2D.Double(0,0,bi.getWidth(), bi.getHeight());
				dst_g.fill(rect);
				
				//�㉺���]
				Graphics2D dst_g2 = bi.createGraphics();
				AffineTransform af = new AffineTransform();
				af.scale(1.0f, -1.0f);
				af.translate(0, -bi.getHeight());
				dst_g2.transform(af);
				dst_g2.drawImage(PaintTool.owner.getSurface(), 0, 0, null);
				
				//redoBuf��mainImg�����ւ�
				BufferedImage savebi = PaintTool.owner.getSurface();
				PaintTool.owner.redoBuf = savebi;
				
				//�I��̈��V��������
				PaintTool.owner.setSurface(bi);
				PaintTool.owner.mainPane.repaint();
			}
		}
		else if(cmd.equalsIgnoreCase("Color Convert�c")){
			
			new ColorConvertDialog(PaintTool.owner);
		}
		else if(cmd.equalsIgnoreCase("Rotate Left")){
			
			if(PaintTool.owner.tool instanceof toolSelectInterface &&
					((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
				BufferedImage bi = tl.getSelectedSurface(PaintTool.owner);
				
				//�܂��V�����o�b�t�@���쐬���ăN���A
				BufferedImage bi2 = new BufferedImage(bi.getHeight(), bi.getWidth(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D dst_g = bi2.createGraphics();
				dst_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
				Rectangle2D.Double rect = new Rectangle2D.Double(0,0,bi.getHeight(), bi.getWidth());
				dst_g.fill(rect);
				
				//�I��̈�̍���]
				Graphics2D dst_g2 = bi2.createGraphics();
				AffineTransform af = new AffineTransform();
				Rectangle srcRect = (Rectangle)tl.getSelectedRect().clone();
				
				if(tl.getClass()==SelectTool.class){
					af.setToRotation(Math.PI*3.0/2.0, srcRect.width/2, srcRect.width/2);
					dst_g2.transform(af);
					dst_g2.drawImage(bi, 0, 0, srcRect.width, srcRect.height, 0, 0, srcRect.width, srcRect.height, null);

					((SelectTool)tl).srcRect.width = srcRect.height;
					((SelectTool)tl).srcRect.height = srcRect.width;
					((SelectTool)tl).moveRect.width = srcRect.height;
					((SelectTool)tl).moveRect.height = srcRect.width;
					
					//���s�ړ�
					((SelectTool)tl).moveRect.x += (srcRect.width-srcRect.height)/2;
					((SelectTool)tl).moveRect.y -= (srcRect.width-srcRect.height)/2;
				}
				else if(tl.getClass()==LassoTool.class){
					Rectangle srcRect2 = tl.getSelectedRect();
					af.setToRotation(Math.PI*3.0/2.0, srcRect2.x+srcRect2.width/2, srcRect2.x+srcRect2.width/2);
					dst_g2.transform(af);
					dst_g2.drawImage(bi, 0, 0, null);

					//newSrcBits���쐬���ăN���A
					BufferedImage newSrcBits = new BufferedImage(bi.getHeight(), bi.getWidth(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D newsrcbits_g = newSrcBits.createGraphics();
					newsrcbits_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					Rectangle2D.Double nrect = new Rectangle2D.Double(0,0,newSrcBits.getHeight(), newSrcBits.getWidth());
					newsrcbits_g.fill(nrect);
					
					//srcBits����]������
					af = new AffineTransform();
					af.setToRotation(Math.PI*3.0/2.0, srcRect2.x+srcRect2.width/2, srcRect2.x+srcRect2.width/2);
					LassoTool lassotl = (LassoTool)tl;
					Graphics2D newsrcbits_g2 = newSrcBits.createGraphics();
					newsrcbits_g2.transform(af);
					newsrcbits_g2.drawImage(lassotl.srcbits, 0, 0, null);
					lassotl.srcbits.flush();
					lassotl.srcbits = newSrcBits;
					
					//���s�ړ�
					lassotl.movePoint.x += srcRect.x-srcRect.y+(srcRect.width-(srcRect.height))/2;
					lassotl.movePoint.y -= srcRect.x-srcRect.y+(srcRect.width-(srcRect.height))/2;
				}
				else if(tl.getClass()==SmartSelectTool.class){
					Rectangle srcRect2 = tl.getSelectedRect();
					af.setToRotation(Math.PI*3.0/2.0, srcRect2.x+srcRect2.width/2, srcRect2.x+srcRect2.width/2);
					dst_g2.transform(af);
					dst_g2.drawImage(bi, 0, 0, null);

					//newSrcBits���쐬���ăN���A
					BufferedImage newSrcBits = new BufferedImage(bi.getHeight(), bi.getWidth(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D newsrcbits_g = newSrcBits.createGraphics();
					newsrcbits_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					Rectangle2D.Double nrect = new Rectangle2D.Double(0,0,newSrcBits.getHeight(), newSrcBits.getWidth());
					newsrcbits_g.fill(nrect);
					
					//srcBits����]������
					af = new AffineTransform();
					af.setToRotation(Math.PI*3.0/2.0, srcRect2.x+srcRect2.width/2, srcRect2.x+srcRect2.width/2);
					SmartSelectTool smarttl = (SmartSelectTool)tl;
					Graphics2D newsrcbits_g2 = newSrcBits.createGraphics();
					newsrcbits_g2.transform(af);
					newsrcbits_g2.drawImage(smarttl.srcbits, 0, 0, null);
					smarttl.srcbits.flush();
					smarttl.srcbits = newSrcBits;
					
					//���s�ړ�
					smarttl.movePoint.x += srcRect.x-srcRect.y+(srcRect.width-(srcRect.height))/2;
					smarttl.movePoint.y -= srcRect.x-srcRect.y+(srcRect.width-(srcRect.height))/2;
				}
				
				//�I��̈��V��������
				PaintTool.owner.redoBuf = bi2;
				
				PaintTool.owner.mainPane.repaint();
				
				bi.flush();
			}
			else{
				//�T�[�t�F�[�X�S�̂̍���]
				setUndo();
				
				//�܂�redoBuf���N���A
				BufferedImage bi = PaintTool.owner.redoBuf;
				Graphics2D dst_g = bi.createGraphics();
				dst_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
				Rectangle2D.Double rect = new Rectangle2D.Double(0,0,bi.getWidth(), bi.getHeight());
				dst_g.fill(rect);
				
				//����]
				Graphics2D dst_g2 = bi.createGraphics();
				AffineTransform af = new AffineTransform();
				af.setToRotation(Math.PI*3.0/2.0, bi.getWidth()/2, bi.getHeight()/2);
				dst_g2.transform(af);
				dst_g2.drawImage(PaintTool.owner.getSurface(), 0, 0, null);
				
				//redoBuf��mainImg�����ւ�
				BufferedImage savebi = PaintTool.owner.getSurface();
				PaintTool.owner.redoBuf = savebi;
				
				//�C���[�W��V��������
				PaintTool.owner.setSurface(bi);
				PaintTool.owner.mainPane.repaint();
			}
		}
		else if(cmd.equalsIgnoreCase("Rotate Right")){
			
			if(PaintTool.owner.tool instanceof toolSelectInterface &&
					((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
				BufferedImage bi = tl.getSelectedSurface(PaintTool.owner);
				
				//�܂��V�����o�b�t�@���쐬���ăN���A
				BufferedImage bi2 = new BufferedImage(bi.getHeight(), bi.getWidth(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D dst_g = bi2.createGraphics();
				dst_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
				Rectangle2D.Double rect = new Rectangle2D.Double(0,0,bi.getHeight(), bi.getWidth());
				dst_g.fill(rect);
				
				//�I��̈�̉E��]
				Graphics2D dst_g2 = bi2.createGraphics();
				AffineTransform af = new AffineTransform();
				Rectangle srcRect = (Rectangle)tl.getSelectedRect().clone();
				
				if(tl.getClass()==SelectTool.class){
					af.setToRotation(Math.PI/2.0, srcRect.height/2, srcRect.height/2);
					dst_g2.transform(af);
					dst_g2.drawImage(bi, 0, 0, srcRect.width, srcRect.height, 0, 0, srcRect.width, srcRect.height, null);

					((SelectTool)tl).srcRect.width = srcRect.height;
					((SelectTool)tl).srcRect.height = srcRect.width;
					((SelectTool)tl).moveRect.width = srcRect.height;
					((SelectTool)tl).moveRect.height = srcRect.width;
					
					//���s�ړ�
					((SelectTool)tl).moveRect.x += (srcRect.width-srcRect.height)/2;
					((SelectTool)tl).moveRect.y -= (srcRect.width-srcRect.height)/2;
				}
				else if(tl.getClass()==LassoTool.class){
					Rectangle srcRect2 = tl.getSelectedRect();
					af.setToRotation(Math.PI/2.0, srcRect2.y+srcRect2.height/2, srcRect2.y+srcRect2.height/2);
					dst_g2.transform(af);
					dst_g2.drawImage(bi, 0, 0, null);

					//newSrcBits���쐬���ăN���A
					BufferedImage newSrcBits = new BufferedImage(bi.getHeight(), bi.getWidth(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D newsrcbits_g = newSrcBits.createGraphics();
					newsrcbits_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					Rectangle2D.Double nrect = new Rectangle2D.Double(0,0,newSrcBits.getHeight(), newSrcBits.getWidth());
					newsrcbits_g.fill(nrect);
					
					//srcBits����]������
					af = new AffineTransform();
					af.setToRotation(Math.PI/2.0, srcRect2.y+srcRect2.height/2, srcRect2.y+srcRect2.height/2);
					LassoTool lassotl = (LassoTool)tl;
					Graphics2D newsrcbits_g2 = newSrcBits.createGraphics();
					newsrcbits_g2.transform(af);
					newsrcbits_g2.drawImage(lassotl.srcbits, 0, 0, null);
					lassotl.srcbits.flush();
					lassotl.srcbits = newSrcBits;
					
					//���s�ړ�
					lassotl.movePoint.x += srcRect.x-srcRect.y+(srcRect.width-(srcRect.height))/2;
					lassotl.movePoint.y -= srcRect.x-srcRect.y+(srcRect.width-(srcRect.height))/2;
				}
				else if(tl.getClass()==SmartSelectTool.class){
					Rectangle srcRect2 = tl.getSelectedRect();
					af.setToRotation(Math.PI/2.0, srcRect2.y+srcRect2.height/2, srcRect2.y+srcRect2.height/2);
					dst_g2.transform(af);
					dst_g2.drawImage(bi, 0, 0, null);

					//newSrcBits���쐬���ăN���A
					BufferedImage newSrcBits = new BufferedImage(bi.getHeight(), bi.getWidth(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D newsrcbits_g = newSrcBits.createGraphics();
					newsrcbits_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					Rectangle2D.Double nrect = new Rectangle2D.Double(0,0,newSrcBits.getHeight(), newSrcBits.getWidth());
					newsrcbits_g.fill(nrect);
					
					//srcBits����]������
					af = new AffineTransform();
					af.setToRotation(Math.PI/2.0, srcRect2.y+srcRect2.height/2, srcRect2.y+srcRect2.height/2);
					SmartSelectTool smarttl = (SmartSelectTool)tl;
					Graphics2D newsrcbits_g2 = newSrcBits.createGraphics();
					newsrcbits_g2.transform(af);
					newsrcbits_g2.drawImage(smarttl.srcbits, 0, 0, null);
					smarttl.srcbits.flush();
					smarttl.srcbits = newSrcBits;
					
					//���s�ړ�
					smarttl.movePoint.x += srcRect.x-srcRect.y+(srcRect.width-(srcRect.height))/2;
					smarttl.movePoint.y -= srcRect.x-srcRect.y+(srcRect.width-(srcRect.height))/2;
				}
				
				//�I��̈��V��������
				PaintTool.owner.redoBuf = bi2;
				
				PaintTool.owner.mainPane.repaint();
				
				bi.flush();
			}
			else{
				//�T�[�t�F�[�X�S�̂̉E��]
				setUndo();
				
				//�܂�redoBuf���N���A
				BufferedImage bi = PaintTool.owner.redoBuf;
				Graphics2D dst_g = bi.createGraphics();
				dst_g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
				Rectangle2D.Double rect = new Rectangle2D.Double(0,0,bi.getWidth(), bi.getHeight());
				dst_g.fill(rect);
				
				//�E��]
				Graphics2D dst_g2 = bi.createGraphics();
				AffineTransform af = new AffineTransform();
				af.setToRotation(Math.PI/2.0, bi.getWidth()/2, bi.getHeight()/2);
				dst_g2.transform(af);
				dst_g2.drawImage(PaintTool.owner.getSurface(), 0, 0, null);
				
				//redoBuf��mainImg�����ւ�
				BufferedImage savebi = PaintTool.owner.getSurface();
				PaintTool.owner.redoBuf = savebi;
				
				//�C���[�W��V��������
				PaintTool.owner.setSurface(bi);
				PaintTool.owner.mainPane.repaint();
			}
		}
		else if(cmd.equalsIgnoreCase("Emboss�c")){
			
			new EmbossDialog(PaintTool.owner);
		}
		else if(cmd.equalsIgnoreCase("Blur")){
			
			if(PaintTool.owner.tool instanceof toolSelectInterface &&
					((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				//�I��͈�
				toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
				BufferedImage bi = tl.getSelectedSurface(PaintTool.owner);

				//�[�̏����������邽�߁A�摜�T�C�Y��傫������
				int biwidth = bi.getWidth();
				int biheight = bi.getHeight();
				if(PaintTool.owner.tool.getClass()==SelectTool.class){
					biwidth = ((SelectTool)PaintTool.owner.tool).getSelectedRect().width;
					biheight = ((SelectTool)PaintTool.owner.tool).getSelectedRect().height;
				}
				BufferedImage bigbi = new BufferedImage(biwidth+4, biheight+4, BufferedImage.TYPE_INT_ARGB);
				bigbi.createGraphics().drawImage(bi, 0,2,2,biheight+2, 0,0,2,biheight, null);
				bigbi.createGraphics().drawImage(bi, biwidth+2,2,biwidth+4,biheight+2, biwidth-2,0,biwidth,biheight, null);
				bigbi.createGraphics().drawImage(bi, 2,0,biwidth+2,2, 0,0,biwidth,2, null);
				bigbi.createGraphics().drawImage(bi, 2,biheight+2,biwidth+2,biheight+4, 0,biheight-2,biwidth,biheight, null);
				bigbi.createGraphics().drawImage(bi, 2,2, null);
				
				//�t�B���^�[����
				final float[] operator={
						0.00f, 0.01f, 0.02f, 0.01f, 0.00f,
						0.01f, 0.03f, 0.08f, 0.03f, 0.01f,
						0.02f, 0.08f, 0.40f, 0.08f, 0.02f,
						0.01f, 0.03f, 0.08f, 0.03f, 0.01f,
						0.00f, 0.01f, 0.02f, 0.01f, 0.00f,
					};
				Kernel blur=new Kernel(5,5,operator);
				ConvolveOp convop=new ConvolveOp(blur,ConvolveOp.EDGE_NO_OP,null);
				BufferedImage bignewimg = convop.filter(bigbi,null);

				BufferedImage newimg = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
				newimg.createGraphics().drawImage(bignewimg, -2,-2, null);
				
				//�I��̈��V��������
				PaintTool.owner.redoBuf = newimg;
				
				if(PaintTool.owner.tool.getClass()==LassoTool.class ||
					PaintTool.owner.tool.getClass()==SmartSelectTool.class){
					BufferedImage srcbits = null;
					if(PaintTool.owner.tool.getClass()==LassoTool.class){
						srcbits = ((LassoTool)PaintTool.owner.tool).srcbits;
					}
					else if(PaintTool.owner.tool.getClass()==SmartSelectTool.class){
						srcbits = ((SmartSelectTool)PaintTool.owner.tool).srcbits;
					}

					DataBuffer mskbuf = srcbits.getRaster().getDataBuffer();
					DataBuffer movbuf = PaintTool.owner.redoBuf.getRaster().getDataBuffer();
					int width = srcbits.getWidth();
					int height = srcbits.getHeight();
					for(int v=0; v<height; v++){
						for(int h=0; h<width; h++){
							int c = mskbuf.getElem(0, h+v*width);
							if((c&0xFF000000) == 0){
								//�ړ����������𓧖��ɂ���
								if(!PaintTool.editBackground){
									movbuf.setElem(h+v*width, 0x00FFFFFF);
								}else{
									movbuf.setElem(h+v*width, 0xFFFFFFFF);
								}
							}
							else{
								mskbuf.setElem(h+v*width, movbuf.getElem(h+v*width));
							}
						}
					}
				}
				
				PaintTool.owner.mainPane.repaint();
				
				bi.flush();
			}
			else{
				//�T�[�t�F�[�X�S�̂̂ڂ���
				setUndo();
				
				BufferedImage bi = PaintTool.owner.getSurface();

				//�[�̏����������邽�߁A�摜�T�C�Y��傫������
				BufferedImage bigbi = new BufferedImage(bi.getWidth()+4, bi.getHeight()+4, BufferedImage.TYPE_INT_ARGB);
				bigbi.createGraphics().drawImage(bi, 0,2,2,bi.getHeight()+2, 0,0,2,bi.getHeight(), null);
				bigbi.createGraphics().drawImage(bi, bi.getWidth()+2,2,bi.getWidth()+4,bi.getHeight()+2, bi.getWidth()-2,0,bi.getWidth(),bi.getHeight(), null);
				bigbi.createGraphics().drawImage(bi, 2,0,bi.getWidth()+2,2, 0,0,bi.getWidth(),2, null);
				bigbi.createGraphics().drawImage(bi, 2,bi.getHeight()+2,bi.getWidth()+2,bi.getHeight()+4, 0,bi.getHeight()-2,bi.getWidth(),bi.getHeight(), null);
				bigbi.createGraphics().drawImage(bi, 2,2, null);

				//�t�B���^�[����
				final float[] operator={
						0.00f, 0.01f, 0.02f, 0.01f, 0.00f,
						0.01f, 0.03f, 0.08f, 0.03f, 0.01f,
						0.02f, 0.08f, 0.40f, 0.08f, 0.02f,
						0.01f, 0.03f, 0.08f, 0.03f, 0.01f,
						0.00f, 0.01f, 0.02f, 0.01f, 0.00f,
					};
				Kernel blur=new Kernel(5,5,operator);
				ConvolveOp convop=new ConvolveOp(blur,ConvolveOp.EDGE_NO_OP,null);
				BufferedImage bignewimg = convop.filter(bigbi,null);

				BufferedImage newimg = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
				newimg.createGraphics().drawImage(bignewimg, -2,-2, null);
				
				//�C���[�W��V��������
				PaintTool.owner.setSurface(newimg);
				PaintTool.owner.mainPane.repaint();
			}
		}
		else if(cmd.equalsIgnoreCase("Sharpen")){
			
			if(PaintTool.owner.tool instanceof toolSelectInterface &&
					((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				//�I��͈�
				toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
				BufferedImage bi = tl.getSelectedSurface(PaintTool.owner);

				//�[�̏����������邽�߁A�摜�T�C�Y��傫������
				int biwidth = bi.getWidth();
				int biheight = bi.getHeight();
				if(PaintTool.owner.tool.getClass()==SelectTool.class){
					biwidth = ((SelectTool)PaintTool.owner.tool).getSelectedRect().width;
					biheight = ((SelectTool)PaintTool.owner.tool).getSelectedRect().height;
				}
				BufferedImage bigbi = new BufferedImage(biwidth+4, biheight+4, BufferedImage.TYPE_INT_ARGB);
				bigbi.createGraphics().drawImage(bi, 0,2,2,biheight+2, 0,0,2,biheight, null);
				bigbi.createGraphics().drawImage(bi, biwidth+2,2,biwidth+4,biheight+2, biwidth-2,0,biwidth,biheight, null);
				bigbi.createGraphics().drawImage(bi, 2,0,biwidth+2,2, 0,0,biwidth,2, null);
				bigbi.createGraphics().drawImage(bi, 2,biheight+2,biwidth+2,biheight+4, 0,biheight-2,biwidth,biheight, null);
				bigbi.createGraphics().drawImage(bi, 2,2, null);
				
				//�t�B���^�[����
				final float[] operator={
						-0.00f, -0.01f, -0.02f, -0.01f, -0.00f,
						-0.01f, -0.03f, -0.08f, -0.03f, -0.01f,
						-0.02f, -0.08f,  1.60f, -0.08f, -0.02f,
						-0.01f, -0.03f, -0.08f, -0.03f, -0.01f,
						-0.00f, -0.01f, -0.02f, -0.01f, -0.00f,
					};
				Kernel blur=new Kernel(5,5,operator);
				ConvolveOp convop=new ConvolveOp(blur,ConvolveOp.EDGE_NO_OP,null);
				BufferedImage bignewimg = convop.filter(bigbi,null);

				BufferedImage newimg = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
				newimg.createGraphics().drawImage(bignewimg, -2,-2, null);
				
				//�I��̈��V��������
				PaintTool.owner.redoBuf = newimg;

				if(PaintTool.owner.tool.getClass()==LassoTool.class ||
					PaintTool.owner.tool.getClass()==SmartSelectTool.class){
					BufferedImage srcbits = null;
					if(PaintTool.owner.tool.getClass()==LassoTool.class){
						srcbits = ((LassoTool)PaintTool.owner.tool).srcbits;
					}
					else if(PaintTool.owner.tool.getClass()==SmartSelectTool.class){
						srcbits = ((SmartSelectTool)PaintTool.owner.tool).srcbits;
					}

					DataBuffer mskbuf = srcbits.getRaster().getDataBuffer();
					DataBuffer movbuf = PaintTool.owner.redoBuf.getRaster().getDataBuffer();
					int width = srcbits.getWidth();
					int height = srcbits.getHeight();
					for(int v=0; v<height; v++){
						for(int h=0; h<width; h++){
							int c = mskbuf.getElem(0, h+v*width);
							if((c&0xFF000000) == 0){
								//�ړ����������𓧖��ɂ���
								if(!PaintTool.editBackground){
									movbuf.setElem(h+v*width, 0x00FFFFFF);
								}else{
									movbuf.setElem(h+v*width, 0xFFFFFFFF);
								}
							}
							else{
								mskbuf.setElem(h+v*width, movbuf.getElem(h+v*width));
							}
						}
					}
				}
				
				PaintTool.owner.mainPane.repaint();
				
				bi.flush();
			}
			else{
				//�T�[�t�F�[�X�S�̂̃V���[�v
				setUndo();
				
				BufferedImage bi = PaintTool.owner.getSurface();

				//�[�̏����������邽�߁A�摜�T�C�Y��傫������
				BufferedImage bigbi = new BufferedImage(bi.getWidth()+4, bi.getHeight()+4, BufferedImage.TYPE_INT_ARGB);
				bigbi.createGraphics().drawImage(bi, 0,2,2,bi.getHeight()+2, 0,0,2,bi.getHeight(), null);
				bigbi.createGraphics().drawImage(bi, bi.getWidth()+2,2,bi.getWidth()+4,bi.getHeight()+2, bi.getWidth()-2,0,bi.getWidth(),bi.getHeight(), null);
				bigbi.createGraphics().drawImage(bi, 2,0,bi.getWidth()+2,2, 0,0,bi.getWidth(),2, null);
				bigbi.createGraphics().drawImage(bi, 2,bi.getHeight()+2,bi.getWidth()+2,bi.getHeight()+4, 0,bi.getHeight()-2,bi.getWidth(),bi.getHeight(), null);
				bigbi.createGraphics().drawImage(bi, 2,2, null);

				//�t�B���^�[����
				final float[] operator={
						-0.00f, -0.01f, -0.02f, -0.01f, -0.00f,
						-0.01f, -0.03f, -0.08f, -0.03f, -0.01f,
						-0.02f, -0.08f,  1.60f, -0.08f, -0.02f,
						-0.01f, -0.03f, -0.08f, -0.03f, -0.01f,
						-0.00f, -0.01f, -0.02f, -0.01f, -0.00f,
					};
				Kernel blur=new Kernel(5,5,operator);
				ConvolveOp convop=new ConvolveOp(blur,ConvolveOp.EDGE_NO_OP,null);
				BufferedImage bignewimg = convop.filter(bigbi,null);

				BufferedImage newimg = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
				newimg.createGraphics().drawImage(bignewimg, -2,-2, null);
				
				//�C���[�W��V��������
				PaintTool.owner.setSurface(newimg);
				PaintTool.owner.mainPane.repaint();
			}
		}
		else if(cmd.equalsIgnoreCase("Invert")){
			
			if(PaintTool.owner.tool instanceof toolSelectInterface &&
					((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				//�I��͈�
				toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
				BufferedImage bi = tl.getSelectedSurface(PaintTool.owner);
				if(bi.hasTileWriters())
				{
					BufferedImage bi2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
					bi2.createGraphics().drawImage(bi,0,0,null);
					PaintTool.owner.redoBuf = bi2;
					bi = bi2;
				}
				
				//����
				DataBuffer db = bi.getRaster().getDataBuffer();
				int width = bi.getWidth();
				int height = bi.getHeight();
				for(int v=0; v<height; v++){
					for(int h=0; h<width; h++){
						int c = db.getElem(h+v*width);
						c = (c&0xFF000000) + (0x00FFFFFF&~(c&0x00FFFFFF));
						db.setElem(h+v*width, c);
					}
				}
				
				if(PaintTool.owner.tool.getClass()==LassoTool.class ||
					PaintTool.owner.tool.getClass()==SmartSelectTool.class){
					BufferedImage srcbits = null;
					if(PaintTool.owner.tool.getClass()==LassoTool.class){
						srcbits = ((LassoTool)PaintTool.owner.tool).srcbits;
					}
					else if(PaintTool.owner.tool.getClass()==SmartSelectTool.class){
						srcbits = ((SmartSelectTool)PaintTool.owner.tool).srcbits;
					}

					DataBuffer mskbuf = srcbits.getRaster().getDataBuffer();
					DataBuffer movbuf = PaintTool.owner.redoBuf.getRaster().getDataBuffer();
					width = srcbits.getWidth();
					height = srcbits.getHeight();
					for(int v=0; v<height; v++){
						for(int h=0; h<width; h++){
							mskbuf.setElem(h+v*width, movbuf.getElem(h+v*width));
						}
					}
				}
				
				PaintTool.owner.mainPane.repaint();
			}
			else{
				//�T�[�t�F�[�X�S�̂̐F���]
				setUndo();
				
				BufferedImage bi = PaintTool.owner.getSurface();

				//����
				DataBuffer db = bi.getRaster().getDataBuffer();
				int width = bi.getWidth();
				int height = bi.getHeight();
				for(int v=0; v<height; v++){
					for(int h=0; h<width; h++){
						int c = db.getElem(h+v*width);
						c = (c&0xFF000000) + (0x00FFFFFF&~(c&0x00FFFFFF));
						db.setElem(h+v*width, c);
					}
				}
				
				PaintTool.owner.mainPane.repaint();
			}
		}
		else if(cmd.equalsIgnoreCase("Darken")){
			
			if(PaintTool.owner.tool instanceof toolSelectInterface &&
					((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				//�I��͈͂��Â�����
				toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
				BufferedImage bi = tl.getSelectedSurface(PaintTool.owner);
				if(bi.hasTileWriters())
				{
					BufferedImage bi2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
					bi2.createGraphics().drawImage(bi,0,0,null);
					PaintTool.owner.redoBuf = bi2;
					bi = bi2;
				}
				
				//����
				DataBuffer db = bi.getRaster().getDataBuffer();
				int width = bi.getWidth();
				int height = bi.getHeight();
				for(int v=0; v<height; v++){
					for(int h=0; h<width; h++){
						int c = db.getElem(h+v*width);
						int red = (c>>16)&0xFF;
						red-=20;
						if(red<0) red = 0;
						int green = (c>>8)&0xFF;
						green-=20;
						if(green<0) green = 0;
						int blue = (c)&0xFF;
						blue-=20;
						if(blue<0) blue = 0;
						int w = (c&0xFF000000) + (red<<16) + (green<<8) + blue;
						db.setElem(h+v*width, w);
					}
				}
				
				if(PaintTool.owner.tool.getClass()==LassoTool.class ||
					PaintTool.owner.tool.getClass()==SmartSelectTool.class){
					BufferedImage srcbits = null;
					if(PaintTool.owner.tool.getClass()==LassoTool.class){
						srcbits = ((LassoTool)PaintTool.owner.tool).srcbits;
					}
					else if(PaintTool.owner.tool.getClass()==SmartSelectTool.class){
						srcbits = ((SmartSelectTool)PaintTool.owner.tool).srcbits;
					}

					DataBuffer mskbuf = srcbits.getRaster().getDataBuffer();
					DataBuffer movbuf = PaintTool.owner.redoBuf.getRaster().getDataBuffer();
					width = srcbits.getWidth();
					height = srcbits.getHeight();
					for(int v=0; v<height; v++){
						for(int h=0; h<width; h++){
							mskbuf.setElem(h+v*width, movbuf.getElem(h+v*width));
						}
					}
				}
				
				PaintTool.owner.mainPane.repaint();
			}
			else{
				//�T�[�t�F�[�X�S�̂��Â�����
				setUndo();
				
				BufferedImage bi = PaintTool.owner.getSurface();

				//����
				DataBuffer db = bi.getRaster().getDataBuffer();
				int width = bi.getWidth();
				int height = bi.getHeight();
				for(int v=0; v<height; v++){
					for(int h=0; h<width; h++){
						int c = db.getElem(h+v*width);
						int red = (c>>16)&0xFF;
						red-=20;
						if(red<0) red = 0;
						int green = (c>>8)&0xFF;
						green-=20;
						if(green<0) green = 0;
						int blue = (c)&0xFF;
						blue-=20;
						if(blue<0) blue = 0;
						int w = (c&0xFF000000) + (red<<16) + (green<<8) + blue;
						db.setElem(h+v*width, w);
					}
				}
				
				PaintTool.owner.mainPane.repaint();
			}
		}
		else if(cmd.equalsIgnoreCase("Lighten")){
			
			if(PaintTool.owner.tool instanceof toolSelectInterface &&
					((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				//�I��͈͂𖾂邭����
				toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
				BufferedImage bi = tl.getSelectedSurface(PaintTool.owner);
				if(bi.hasTileWriters())
				{
					BufferedImage bi2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
					bi2.createGraphics().drawImage(bi,0,0,null);
					PaintTool.owner.redoBuf = bi2;
					bi = bi2;
				}
				
				//����
				DataBuffer db = bi.getRaster().getDataBuffer();
				int width = bi.getWidth();
				int height = bi.getHeight();
				for(int v=0; v<height; v++){
					for(int h=0; h<width; h++){
						int c = db.getElem(h+v*width);
						int red = (c>>16)&0xFF;
						red+=20;
						if(red>255) red = 255;
						int green = (c>>8)&0xFF;
						green+=20;
						if(green>255) green = 255;
						int blue = (c)&0xFF;
						blue+=20;
						if(blue>255) blue = 255;
						int w = (c&0xFF000000) + (red<<16) + (green<<8) + blue;
						db.setElem(h+v*width, w);
					}
				}
				
				if(PaintTool.owner.tool.getClass()==LassoTool.class ||
					PaintTool.owner.tool.getClass()==SmartSelectTool.class){
					BufferedImage srcbits = null;
					if(PaintTool.owner.tool.getClass()==LassoTool.class){
						srcbits = ((LassoTool)PaintTool.owner.tool).srcbits;
					}
					else if(PaintTool.owner.tool.getClass()==SmartSelectTool.class){
						srcbits = ((SmartSelectTool)PaintTool.owner.tool).srcbits;
					}

					DataBuffer mskbuf = srcbits.getRaster().getDataBuffer();
					DataBuffer movbuf = PaintTool.owner.redoBuf.getRaster().getDataBuffer();
					width = srcbits.getWidth();
					height = srcbits.getHeight();
					for(int v=0; v<height; v++){
						for(int h=0; h<width; h++){
							mskbuf.setElem(h+v*width, movbuf.getElem(h+v*width));
						}
					}
				}
				
				PaintTool.owner.mainPane.repaint();
			}
			else{
				//�T�[�t�F�[�X�S�̂𖾂邭����
				setUndo();
				
				BufferedImage bi = PaintTool.owner.getSurface();

				//����
				DataBuffer db = bi.getRaster().getDataBuffer();
				int width = bi.getWidth();
				int height = bi.getHeight();
				for(int v=0; v<height; v++){
					for(int h=0; h<width; h++){
						int c = db.getElem(h+v*width);
						int red = (c>>16)&0xFF;
						red+=20;
						if(red>255) red = 255;
						int green = (c>>8)&0xFF;
						green+=20;
						if(green>255) green = 255;
						int blue = (c)&0xFF;
						blue+=20;
						if(blue>255) blue = 255;
						int w = (c&0xFF000000) + (red<<16) + (green<<8) + blue;
						db.setElem(h+v*width, w);
					}
				}
				
				PaintTool.owner.mainPane.repaint();
			}
		}
		else if(cmd.equalsIgnoreCase("Pickup")){
			
			if(PaintTool.owner.tool instanceof toolSelectInterface &&
					((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				//�I��͈͂����ɂ�����̂ɂ���
				toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
				BufferedImage bi;
				if(tl.getClass()==LassoTool.class){
					bi = ((LassoTool)tl).srcbits;
				}
				else if(tl.getClass()==SmartSelectTool.class){
					bi = ((SmartSelectTool)tl).srcbits;
				}
				else{
					bi = tl.getSelectedSurface(PaintTool.owner);
				}
				if(bi.hasTileWriters())
				{
					BufferedImage bi2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
					bi2.createGraphics().drawImage(bi,0,0,null);
					PaintTool.owner.redoBuf = bi2;
					bi = bi2;
				}

				Rectangle rect1 = tl.getSelectedRect();
				Rectangle rect2 = tl.getMoveRect();
				System.out.println("rect1:"+rect1);
				System.out.println("rect2:"+rect2);
				
				//����
				DataBuffer db = bi.getRaster().getDataBuffer();
				DataBuffer maindb = PaintTool.owner.getSurface().getRaster().getDataBuffer();
				int width = bi.getWidth();
				int height = bi.getHeight();
				int mainwidth = PaintTool.owner.getSurface().getWidth();
				int mainheight = PaintTool.owner.getSurface().getHeight();
				
				if(tl.getClass()==SelectTool.class){
					//��`�I���c�[��
					for(int v=0; v<rect1.height; v++){
						for(int h=0; h<rect1.width; h++){
							//if((0xFF000000&db.getElem(h+v*width))!=0){
								int nh = h + rect1.x;
								int nv = v + rect1.y;
								if(nh<0 || nh>=mainwidth || nv<0 || nv>=mainheight){
									continue;
								}
								db.setElem(h+v*width, maindb.getElem(nh+nv*mainwidth));
							//}
						}
					}
				}
				else{
					//�����ꂩ�����I���c�[��
					for(int v=0; v<height; v++){
						for(int h=0; h<width; h++){
							if((0xFF000000&db.getElem(h+v*width))!=0){
								int nh = h + rect2.x-rect1.x;
								int nv = v + rect2.y-rect1.y;
								if(nh<0 || nh>=mainwidth || nv<0 || nv>=mainheight){
									continue;
								}
								db.setElem(h+v*width, maindb.getElem(nh+nv*mainwidth));
							}
						}
					}
				}
				
				PaintTool.owner.mainPane.repaint();
			}
			else{
				//�I��͈͂�����܂���!
			}
		}
		else if(cmd.equalsIgnoreCase("Fill")){
			
			if(PaintTool.owner.tool instanceof toolSelectInterface &&
					((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				//�I��͈͂�h��Ԃ�
				toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
				BufferedImage bi = tl.getSelectedSurface(PaintTool.owner);
				if(bi.hasTileWriters())
				{
					BufferedImage bi2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
					bi2.createGraphics().drawImage(bi,0,0,null);
					PaintTool.owner.redoBuf = bi2;
					bi = bi2;
				}
				
				if(tl.getClass()==SelectTool.class){
					bi.createGraphics().fillRect(0,0,tl.getSelectedRect().width, tl.getSelectedRect().height);
				}
				
				//����

				//�p�^�[����K�p
				PaintTool.setPattern(false);
				DataBuffer fillbuf = bi.getRaster().getDataBuffer();
				DataBuffer patbuf = PaintTool.pat.getRaster().getDataBuffer();
				int patW = PaintTool.pat.getWidth();
				int patH = PaintTool.pat.getHeight();
				int width = bi.getWidth();
				int height = bi.getHeight();
				for(int y=0; y<height; y++){
					for(int x=0; x<width; x++){
						int c1 = fillbuf.getElem(0, x+y*width);
						if((c1 & 0xFF000000) != 0){
							int c2 = patbuf.getElem(x%patW + (y%patH)*patW);
							//c2 = (c2&0x00FFFFFF) + ((((c1&0xFF000000)>>24)*((c2&0xFF000000)>>24))/0xFF)<<24;
							fillbuf.setElem(0, x+y*width, c2);
						}
					}
				}
				
				//�O���f�[�V������K�p(�p�^�[���Ƃ̕��p�͕s��)
				if(PaintTool.owner.grad.use){
					PaintBucketTool.gradfill(bi, PaintTool.owner.grad.color1, PaintTool.owner.grad.color2, PaintTool.owner.grad.angle );
				}
				
				if(PaintTool.owner.tool.getClass()==LassoTool.class ||
					PaintTool.owner.tool.getClass()==SmartSelectTool.class){
					BufferedImage srcbits = null;
					if(PaintTool.owner.tool.getClass()==LassoTool.class){
						srcbits = ((LassoTool)PaintTool.owner.tool).srcbits;
					}
					else if(PaintTool.owner.tool.getClass()==SmartSelectTool.class){
						srcbits = ((SmartSelectTool)PaintTool.owner.tool).srcbits;
					}

					DataBuffer mskbuf = srcbits.getRaster().getDataBuffer();
					DataBuffer movbuf = PaintTool.owner.redoBuf.getRaster().getDataBuffer();
					width = srcbits.getWidth();
					height = srcbits.getHeight();
					for(int v=0; v<height; v++){
						for(int h=0; h<width; h++){
							mskbuf.setElem(h+v*width, movbuf.getElem(h+v*width));
						}
					}
				}
				
				PaintTool.owner.mainPane.repaint();
			}
			else{
				//�T�[�t�F�[�X�S�̂�h��Ԃ�
				setUndo();
				
				BufferedImage bi = PaintTool.owner.getSurface();
				bi.createGraphics().fillRect(0,0,bi.getWidth(),bi.getHeight());

				//����

				//�p�^�[����K�p
				PaintTool.setPattern(false);
				DataBuffer fillbuf = bi.getRaster().getDataBuffer();
				DataBuffer patbuf = PaintTool.pat.getRaster().getDataBuffer();
				int patW = PaintTool.pat.getWidth();
				int patH = PaintTool.pat.getHeight();
				int width = bi.getWidth();
				int height = bi.getHeight();
				for(int y=0; y<height; y++){
					for(int x=0; x<width; x++){
						int c1 = fillbuf.getElem(0, x+y*width);
						if((c1 & 0xFF000000) != 0){
							int c2 = patbuf.getElem(x%patW + (y%patH)*patW);
							//c2 = (c2&0x00FFFFFF) + ((((c1&0xFF000000)>>24)*((c2&0xFF000000)>>24))/0xFF)<<24;
							fillbuf.setElem(0, x+y*width, c2);
						}
					}
				}
				
				//�O���f�[�V������K�p(�p�^�[���Ƃ̕��p�͕s��)
				if(PaintTool.owner.grad.use){
					PaintBucketTool.gradfill(bi, PaintTool.owner.grad.color1, PaintTool.owner.grad.color2, PaintTool.owner.grad.angle );
				}
				
				PaintTool.owner.mainPane.repaint();
			}
		}
		else if(cmd.equalsIgnoreCase("Transparent")){
			
			if(PaintTool.owner.tool instanceof toolSelectInterface &&
					((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				//�I��͈͂̔��F�𓧖��ɂ���
				
				//����
				if(PaintTool.owner.tool.getClass()==SelectTool.class){
					toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
					BufferedImage bi = tl.getSelectedSurface(PaintTool.owner);
					if(bi.hasTileWriters())
					{
						BufferedImage bi2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
						bi2.createGraphics().drawImage(bi,0,0,null);
						PaintTool.owner.redoBuf = bi2;
						bi = bi2;
					}
					
					DataBuffer db = bi.getRaster().getDataBuffer();
					int width = bi.getWidth();
					int height = bi.getHeight();
					for(int v=0; v<height; v++){
						for(int h=0; h<width; h++){
							int c = db.getElem(h+v*width);
							if((c&0xFFFFFF)==0xFFFFFF){
								db.setElem(h+v*width, 0x00FFFFFF);
							}
						}
					}
				}
				
				if(PaintTool.owner.tool.getClass()==LassoTool.class ||
					PaintTool.owner.tool.getClass()==SmartSelectTool.class){
					/*BufferedImage srcbits = null;
					if(PaintTool.owner.tool.getClass()==LassoTool.class){
						srcbits = ((LassoTool)PaintTool.owner.tool).srcbits;
					}
					else if(PaintTool.owner.tool.getClass()==SmartSelectTool.class){
						srcbits = ((SmartSelectTool)PaintTool.owner.tool).srcbits;
					}*/

					//DataBuffer mskbuf = srcbits.getRaster().getDataBuffer();
					DataBuffer movbuf = PaintTool.owner.redoBuf.getRaster().getDataBuffer();
					int width = PaintTool.owner.redoBuf.getWidth();
					int height = PaintTool.owner.redoBuf.getHeight();
					for(int v=0; v<height; v++){
						for(int h=0; h<width; h++){
							if(movbuf.getElem(h+v*width)==0xFFFFFFFF){
								movbuf.setElem(h+v*width, 0x00FFFFFF);
							}
						}
					}
				}
				
				PaintTool.owner.mainPane.repaint();
			}
			else{
				//�I��͈͖���
			}
		}
		else if(cmd.equalsIgnoreCase("Opaque")){
			
			if(PaintTool.owner.tool instanceof toolSelectInterface &&
					((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				//�I��͈͂̓����𔒐F�ɂ���
				
				//����
				if(PaintTool.owner.tool.getClass()==SelectTool.class){
					toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
					BufferedImage bi = tl.getSelectedSurface(PaintTool.owner);
					if(bi.hasTileWriters())
					{
						BufferedImage bi2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
						bi2.createGraphics().drawImage(bi,0,0,null);
						PaintTool.owner.redoBuf = bi2;
						bi = bi2;
					}
					
					DataBuffer db = bi.getRaster().getDataBuffer();
					int width = bi.getWidth();
					int width2 = tl.getSelectedRect().width;
					int height2 = tl.getSelectedRect().height;
					if(width2>=bi.getWidth()) width2=bi.getWidth();
					if(height2>=bi.getHeight()) height2=bi.getHeight();
					for(int v=0; v<height2; v++){
						for(int h=0; h<width2; h++){
							int c = db.getElem(h+v*width);
							if((c&0xFF000000)==0){
								db.setElem(h+v*width, 0xFFFFFFFF);
							}
						}
					}
				}
				
				if(PaintTool.owner.tool.getClass()==LassoTool.class ||
					PaintTool.owner.tool.getClass()==SmartSelectTool.class){
					BufferedImage srcbits = null;
					if(PaintTool.owner.tool.getClass()==LassoTool.class){
						srcbits = ((LassoTool)PaintTool.owner.tool).srcbits;
					}
					else if(PaintTool.owner.tool.getClass()==SmartSelectTool.class){
						srcbits = ((SmartSelectTool)PaintTool.owner.tool).srcbits;
					}

					DataBuffer mskbuf = srcbits.getRaster().getDataBuffer();
					DataBuffer movbuf = PaintTool.owner.redoBuf.getRaster().getDataBuffer();
					int width = srcbits.getWidth();
					int height = srcbits.getHeight();
					for(int v=0; v<height; v++){
						for(int h=0; h<width; h++){
							if((mskbuf.getElem(h+v*width)&0xFF000000)!=0 &&
								(movbuf.getElem(h+v*width)&0xFF000000)==0){
								movbuf.setElem(h+v*width, 0xFFFFFFFF);
							}
						}
					}
				}
				
				PaintTool.owner.mainPane.repaint();
			}
			else{
				//�I��͈͖���
			}
		}
		else if(cmd.equalsIgnoreCase("Reverse Selection")){

			if(PaintTool.owner.tool instanceof toolSelectInterface &&
				((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null &&
				(PaintTool.owner.tool.getClass()==LassoTool.class ||
				PaintTool.owner.tool.getClass()==SmartSelectTool.class))
			{
				//�I��͈͂��t�]���A���̉摜�͉��ɂ�����̂ɂ���
				
				BufferedImage srcbits = null;
				if(PaintTool.owner.tool.getClass()==LassoTool.class){
					LassoTool lasso = (LassoTool)PaintTool.owner.tool;
					srcbits = lasso.srcbits;
					Graphics2D g = (Graphics2D) PaintTool.owner.getSurface().getGraphics();
					g.drawImage(PaintTool.owner.redoBuf, lasso.movePoint.x, lasso.movePoint.y, null);
				}
				else if(PaintTool.owner.tool.getClass()==SmartSelectTool.class){
					SmartSelectTool smart = (SmartSelectTool)PaintTool.owner.tool;
					srcbits = smart.srcbits;
					Graphics2D g = (Graphics2D) PaintTool.owner.getSurface().getGraphics();
					g.drawImage(PaintTool.owner.redoBuf, smart.movePoint.x, smart.movePoint.y, null);
				}

				BufferedImage newbi = new BufferedImage(srcbits.getWidth(), srcbits.getHeight(), BufferedImage.TYPE_INT_ARGB);

				toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
				Rectangle rect1 = tl.getSelectedRect();
				Rectangle rect2 = tl.getMoveRect();
				
				//�t�]����
				DataBuffer srcdb = srcbits.getRaster().getDataBuffer();
				DataBuffer newdb = newbi.getRaster().getDataBuffer();
				int width = newbi.getWidth();
				int height = newbi.getHeight();
				for(int v=0; v<height; v++){
					for(int h=0; h<width; h++){
						int sh = h +rect1.x-rect2.x;
						int sv = v +rect1.y-rect2.y;
						if(sh<0 || sv<0 || sh>=width || sv>=height) {
							newdb.setElem(h+v*width, 0xFF000000);
						}
						else if((0xFF000000&srcdb.getElem(sh+sv*width))!=0){
							newdb.setElem(h+v*width, 0x00FFFFFF);
						}else{
							newdb.setElem(h+v*width, 0xFF000000);
						}
					}
				}
				
				if(PaintTool.owner.tool.getClass()==LassoTool.class){
					((LassoTool)PaintTool.owner.tool).srcbits = newbi;
					((LassoTool)PaintTool.owner.tool).movePoint = new Point(0,0);
				}
				else if(PaintTool.owner.tool.getClass()==SmartSelectTool.class){
					((SmartSelectTool)PaintTool.owner.tool).srcbits = newbi;
					((SmartSelectTool)PaintTool.owner.tool).movePoint = new Point(0,0);
				}
				

				//�s�b�N�A�b�v
				BufferedImage bi = tl.getSelectedSurface(PaintTool.owner);
				if(bi.hasTileWriters())
				{
					BufferedImage bi2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
					bi2.createGraphics().drawImage(bi,0,0,null);
					PaintTool.owner.redoBuf = bi2;
					bi = bi2;
				}
				
				//����
				DataBuffer redodb = bi.getRaster().getDataBuffer();
				DataBuffer maindb = PaintTool.owner.getSurface().getRaster().getDataBuffer();
				width = bi.getWidth();
				height = bi.getHeight();
				int mainwidth = PaintTool.owner.getSurface().getWidth();
				int mainheight = PaintTool.owner.getSurface().getHeight();
				for(int v=0; v<height; v++){
					for(int h=0; h<width; h++){
						if((0xFF000000&newdb.getElem(h+v*width))!=0){
							int nh = h /*+ rect2.x-rect1.x*/;
							int nv = v /*+ rect2.y-rect1.y*/;
							if(nh<0 || nh>=mainwidth || nv<0 || nv>=mainheight){
								continue;
							}
							redodb.setElem(h+v*width, maindb.getElem(nh+nv*width));
							maindb.setElem(nh+nv*width, 0x00FFFFFF);
						}
						else{
							redodb.setElem(h+v*width, 0x00FFFFFF);
						}
					}
				}
				
				PaintTool.owner.mainPane.repaint();
			}
			else{
				//�I��͈͂�����܂���!
			}
		}
		else if(cmd.equalsIgnoreCase("Scale Selection�c")){

			if(PaintTool.owner.tool instanceof toolSelectInterface &&
				((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				if(((toolSelectInterface)PaintTool.owner.tool).getSelectedRect().width>0){
					new PaintScaleDialog(PaintTool.owner);
				}
			}
			else{
				//�I��͈͂�����܂���!
			}
		}
		else if(cmd.equalsIgnoreCase("Opaque")){
			
			if(PaintTool.owner.tool instanceof toolSelectInterface &&
					((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{
				//�I��͈͂̓����𔒐F�ɂ���
				
				//����
				if(PaintTool.owner.tool.getClass()==SelectTool.class){
					toolSelectInterface tl = (toolSelectInterface)PaintTool.owner.tool;
					BufferedImage bi = tl.getSelectedSurface(PaintTool.owner);
					if(bi.hasTileWriters())
					{
						BufferedImage bi2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
						bi2.createGraphics().drawImage(bi,0,0,null);
						PaintTool.owner.redoBuf = bi2;
						bi = bi2;
					}
					
					DataBuffer db = bi.getRaster().getDataBuffer();
					int width = bi.getWidth();
					int width2 = tl.getSelectedRect().width;
					int height2 = tl.getSelectedRect().height;
					if(width2>=bi.getWidth()) width2=bi.getWidth();
					if(height2>=bi.getHeight()) height2=bi.getHeight();
					for(int v=0; v<height2; v++){
						for(int h=0; h<width2; h++){
							int c = db.getElem(h+v*width);
							if((c&0xFF000000)==0){
								db.setElem(h+v*width, 0xFFFFFFFF);
							}
						}
					}
				}
				
				if(PaintTool.owner.tool.getClass()==LassoTool.class ||
					PaintTool.owner.tool.getClass()==SmartSelectTool.class){
					BufferedImage srcbits = null;
					if(PaintTool.owner.tool.getClass()==LassoTool.class){
						srcbits = ((LassoTool)PaintTool.owner.tool).srcbits;
					}
					else if(PaintTool.owner.tool.getClass()==SmartSelectTool.class){
						srcbits = ((SmartSelectTool)PaintTool.owner.tool).srcbits;
					}

					DataBuffer mskbuf = srcbits.getRaster().getDataBuffer();
					DataBuffer movbuf = PaintTool.owner.redoBuf.getRaster().getDataBuffer();
					int width = srcbits.getWidth();
					int height = srcbits.getHeight();
					for(int v=0; v<height; v++){
						for(int h=0; h<width; h++){
							if((mskbuf.getElem(h+v*width)&0xFF000000)!=0 &&
								(movbuf.getElem(h+v*width)&0xFF000000)==0){
								movbuf.setElem(h+v*width, 0xFFFFFFFF);
							}
						}
					}
				}
				
				PaintTool.owner.mainPane.repaint();
			}
			else{
				//�I��͈͖���
			}
		}
		else if(cmd.equalsIgnoreCase("Expand Selection")){

			if(PaintTool.owner.tool instanceof toolSelectInterface &&
				((toolSelectInterface)PaintTool.owner.tool).getSelectedSurface(PaintTool.owner)!=null)
			{

				if(PaintTool.owner.tool.getClass()==SelectTool.class)
				{
					//�I��͈͂�1pixel�g��
					SelectTool tool = (SelectTool)PaintTool.owner.tool;
					tool.moveRect.x--;
					tool.moveRect.y--;
					tool.moveRect.width+=2;
					tool.moveRect.height+=2;
					tool.srcRect.width+=2;
					tool.srcRect.height+=2;

					BufferedImage newbi = new BufferedImage(PaintTool.owner.redoBuf.getWidth()+2, PaintTool.owner.redoBuf.getHeight()+2, BufferedImage.TYPE_INT_ARGB);
					Graphics2D newg = newbi.createGraphics();
					newg.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					newg.fillRect(0,0,newbi.getWidth(), newbi.getHeight());
					
					newg = newbi.createGraphics();
					newg.drawImage(PaintTool.owner.redoBuf, 1, 1, null);
					
					PaintTool.owner.redoBuf = newbi;
				}
				else if((PaintTool.owner.tool.getClass()==LassoTool.class ||
						PaintTool.owner.tool.getClass()==SmartSelectTool.class))
				{
					//�I��͈͂�1pixel�g��
					
					BufferedImage srcbits = null;
					if(PaintTool.owner.tool.getClass()==LassoTool.class){
						LassoTool lasso = (LassoTool)PaintTool.owner.tool;
						srcbits = lasso.srcbits;
						//Graphics2D g = (Graphics2D) PaintTool.owner.getSurface().getGraphics();
						//g.drawImage(PaintTool.owner.redoBuf, lasso.movePoint.x, lasso.movePoint.y, null);
					}
					else if(PaintTool.owner.tool.getClass()==SmartSelectTool.class){
						SmartSelectTool smart = (SmartSelectTool)PaintTool.owner.tool;
						srcbits = smart.srcbits;
						//Graphics2D g = (Graphics2D) PaintTool.owner.getSurface().getGraphics();
						//g.drawImage(PaintTool.owner.redoBuf, smart.movePoint.x, smart.movePoint.y, null);
					}

					BufferedImage newbi = new BufferedImage(PaintTool.owner.redoBuf.getWidth(), PaintTool.owner.redoBuf.getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D newg = newbi.createGraphics();
					newg.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					newg.fillRect(0,0,newbi.getWidth(), newbi.getHeight());
					
					//����
					DataBuffer srcdb = srcbits.getRaster().getDataBuffer();
					DataBuffer newdb = newbi.getRaster().getDataBuffer();
					int width = srcbits.getWidth();
					int height = srcbits.getHeight();
					int newwidth = newbi.getWidth();
					for(int v=0; v<height; v++){
						for(int h=0; h<width; h++){
							if(h-1>=0 && (0xFF000000&srcdb.getElem(h-1+v*width))!=0){
								newdb.setElem(h+v*width, 0xFFFFFFFF);
								continue;
							}
							if(h+1<width && (0xFF000000&srcdb.getElem(h+1+v*width))!=0){
								newdb.setElem(h+v*width, 0xFFFFFFFF);
								continue;
							}
							if(v-1>=0 && (0xFF000000&srcdb.getElem(h+(v-1)*width))!=0){
								newdb.setElem(h+v*width, 0xFFFFFFFF);
								continue;
							}
							if(v+1<height && (0xFF000000&srcdb.getElem(h+(v+1)*width))!=0){
								newdb.setElem(h+v*width, 0xFFFFFFFF);
								continue;
							}
							newdb.setElem(h+v*newwidth, 0x00FFFFFF);
						}
					}

					if(PaintTool.owner.tool.getClass()==LassoTool.class){
						LassoTool lasso = (LassoTool)PaintTool.owner.tool;
						srcbits = lasso.srcbits = newbi;
					}
					else if(PaintTool.owner.tool.getClass()==SmartSelectTool.class){
						SmartSelectTool smart = (SmartSelectTool)PaintTool.owner.tool;
						srcbits = smart.srcbits = newbi;
					}
				}

				PaintTool.owner.mainPane.repaint();
			}
			else{
				//�I��͈͂�����܂���!
			}
		}
		else if(cmd.equalsIgnoreCase("Filter�c")){
			
			new PaintFilter(PaintTool.owner);
		}
		else if(cmd.equalsIgnoreCase("Blending Mode�c")){
			
			PaintBlendDialog.showPaintBlendDialog(PaintTool.owner);
		}
		else if(cmd.equalsIgnoreCase("Use Grid")){
			IconEditor.useGrid = !IconEditor.useGrid;
			PaintTool.owner.repaint();
		}
		else if(cmd.equalsIgnoreCase("Grid Size 1")){
			IconEditor.gridSize = 1;
			PaintTool.owner.repaint();
		}
		else if(cmd.equalsIgnoreCase("Grid Size 16")){
			IconEditor.gridSize = 16;
			PaintTool.owner.repaint();
		}
		else{
			return false;
		}
		
		return true;
	}
	
	
	//�N���b�v�{�[�h�ɉ摜���R�s�[
	public static void setClipboardImage(BufferedImage img, int width, int height) {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Clipboard clip = kit.getSystemClipboard();

		ImageSelection is = new ImageSelection(img, width, height);
		clip.setContents(is, is);
	}
	
	
	static class ImageSelection implements Transferable, ClipboardOwner {

		protected Image data;

		public ImageSelection(BufferedImage image, int width, int height) {
			this.data = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)this.data.getGraphics();
			g.setColor(Color.WHITE);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			g.fillRect(0, 0, width, height);
			g = (Graphics2D)this.data.getGraphics();
			g.drawImage(image, 0, 0, PCARDFrame.pc);
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.imageFlavor };
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return DataFlavor.imageFlavor.equals(flavor);
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (DataFlavor.imageFlavor.equals(flavor)) {
				return data;
			}
			throw new UnsupportedFlavorException(flavor);
		}

		@Override
		public void lostOwnership(Clipboard clipboard, Transferable contents) {
			this.data = null;
		}
	}
	
	
	public static Image getClipboardImage() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Clipboard clip = kit.getSystemClipboard();

		try {
			return (Image) clip.getData(DataFlavor.imageFlavor);
		} catch (UnsupportedFlavorException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	
	
	static public void setUndo(){
		//undoBuf�ɃC���[�W���ڂ�
		PaintTool.owner.undoBuf.setData(PaintTool.owner.getSurface().getData());
		
		//PCARD.pc.redoBuf = null;
		if(PaintTool.owner == PCARDFrame.pc){
			GMenu.changeEnabled("Edit","Redo Paint",false);
			GMenu.changeEnabled("Edit","Undo Paint",true);
		}else{
			IEMenu.changeEnabled("Edit","Redo Paint",false);
			IEMenu.changeEnabled("Edit","Undo Paint",true);
		}
	}
}
