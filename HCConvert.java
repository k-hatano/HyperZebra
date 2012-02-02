import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.swing.JDialog;
import javax.swing.JProgressBar;

//import org.apache.tools.tar.TarEntry;
//import org.apache.tools.tar.TarInputStream;




public class HCConvert {
	public static boolean openHCStack(String path, OStack stack) {
		//System.out.println("openHCStack...");

    	//�i���\�����J�n
		stack.barDialog = new JDialog(stack.pcard);
		stack.barDialog.setUndecorated(true);
		stack.barDialog.getContentPane().setLayout(new BorderLayout());
		stack.bar = new JProgressBar();
		stack.bar.setStringPainted(true);
		PCARD.pc.stack.bar.setSize(new Dimension(PCARD.pc.getWidth(), 20));
		stack.bar.setString("");
		stack.bar.setValue(0);
		stack.barOffset = 0;
		stack.barDialog.add("Center", stack.bar);
		stack.barDialog.setBounds(stack.pcard.getBounds());
		stack.barDialog.setVisible(true);
		stack.bar.paintImmediately(stack.bar.getBounds());
		stack.bar.setString("Make tar archive");
    	
		//Java���烊�\�[�X��ǂނ��߂ɁAApple-Double�t�H�[�}�b�g��tar
		File tarFile = MakeAppleDoubleTar(path);
		
		//�f�B���N�g�����쐬
		if(!CreateStackFolder(stack, path)){
    		tarFile.delete();
    		stack.barDialog.remove(stack.bar);
    		stack.barDialog.dispose();
		}

		
		if(tarFile != null){
			stack.bar.setString("readFromTar");
			//tar���ꂽ�f�[�^�t�H�[�N�ƃ��\�[�X�t�H�[�N�����o��
			if(!readFromTar(tarFile, stack)){
				System.out.println("Error occured at read data from tar file.");
			}
			else{
				//�ǂݍ��ݐ���
				tarFile.delete();
				stack.barDialog.remove(stack.bar);
				stack.barDialog.dispose();
	
				stack.bar = null;
				stack.barDialog = null;
				
				stack.setName(new File(path).getName());
				
				return true;
			}
		}
		else{ //���\�[�X�t�H�[�N�����Œ��ړǂݍ���
			//System.out.println("Load File Direct");
			stack.bar.setString("Load File Direct");
			
			DataInputStream dis = null;
			try {
				FileInputStream fis = new FileInputStream(path);
				dis = new DataInputStream(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			//�f�[�^�t�H�[�N�ǂݍ���
			if(dis==null || !HCData.readDataFork(dis, stack)){
				System.out.println("Error occured at reading data from file.");
	    		new GDialog(PCARDFrame.pc, PCARDFrame.pc.intl.getDialogText("Error occured at reading HyperCard stack data."),
	    				null,"OK",null,null);
			}
			else{
				//���\�[�X�t�H�[�N���ۂ����O�̃t�@�C��������΃��\�[�X�t�H�[�N�ǂݍ���
				//1.AppleDoubleHeader�̉𓀎��̃t�@�C��
				String searchPath = new File(path).getParent()+File.separatorChar+"._"+new File(path).getName();
				File rsrcFile = new File(searchPath);
				if(!rsrcFile.exists()){
					searchPath = new File(path).getParent()+"__MACOSX"+File.separatorChar+new File(path).getName();
					rsrcFile = new File(searchPath);
				}
				if(rsrcFile.exists()){
					DataInputStream rsrc_dis = null;
					try {
						rsrc_dis = new DataInputStream(new FileInputStream(rsrcFile));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					//AppleDouble�̃w�b�_�t�@�C���Ƃ��ēǂݍ���
					HCResource.readAppleDoubleHeader(rsrc_dis, stack);
				}
				else{
					System.out.println("Resource file is not found.");
		    		new GDialog(PCARDFrame.pc, PCARDFrame.pc.intl.getDialogText("Resource data is not found."),
		    				null,"OK",null,null);
				}
				//���\�[�X�t�@�C�����Ȃ��Ă�����
				stack.barDialog.remove(stack.bar);
				stack.barDialog.dispose();
	
				stack.bar = null;
				stack.barDialog = null;
				
				stack.setName(new File(path).getName());
				return true;
			}
		}

		//�ǂݍ��݃G���[
		if(tarFile!=null){
			tarFile.delete();
		}
		stack.barDialog.remove(stack.bar);
		stack.barDialog.dispose();
		return false;
	}


	public static boolean openMacBinaryStack(String path, OStack stack) {
		//System.out.println("openMacBinaryHCStack...");

    	//�i���\�����J�n
		stack.barDialog = new JDialog(stack.pcard);
		stack.barDialog.setUndecorated(true);
		stack.bar = new JProgressBar();
		stack.bar.setStringPainted(true);
		PCARD.pc.stack.bar.setSize(new Dimension(PCARD.pc.getWidth(), 20));
		stack.bar.setString("");
		stack.bar.setValue(0);
		stack.barOffset = 0;
		stack.barDialog.add(stack.bar);
		stack.barDialog.setBounds(stack.pcard.getBounds());
		stack.barDialog.setVisible(true);
		stack.bar.paintImmediately(stack.bar.getBounds());

		//�f�B���N�g�����쐬
		if(!CreateStackFolder(stack, path)){
    		stack.barDialog.remove(stack.bar);
    		stack.barDialog.dispose();
		}

		//MacBinary��128�o�C�g�̃w�b�_
		//+ �f�[�^�t�H�[�N  (�f�[�^�t�H�[�N�̒�����128byte�Ő؂�グ��)
		//+ ���\�[�X�t�H�[�N
		//
		//lzh�`���̃t�@�C�����𓀂����MacBinary�`���������肷��B
		//Windows��StuffItExpander�ł͉𓀌��MacBinary�ɂ���I�v�V����������
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		byte[] header = new byte[128];
		try {
			fis.read(header, 0, 128);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(header[65]=='S'&&header[66]=='T'&&header[67]=='A'&&header[68]=='K'){
			int dataForkLen = ((0x00FF&header[83])<<24)+
				((0x00FF&header[84])<<16)+
				((0x00FF&header[85])<<8)+
				((0x00FF&header[86]));
			//�f�[�^�t�H�[�N������byte�z��ɓǂݍ���
			if(dataForkLen>100*1000*1000){
				//100M�ȏ�̂͂����Ȃ�
				dataForkLen = 10*1000*1000;//�Ƃ肠����10M�ŁB
			}
			byte[] dataBytes = new byte[dataForkLen];
			try {
				fis.read(dataBytes, 0, dataForkLen);
			} catch (IOException e) {
				e.printStackTrace();
			}
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(dataBytes));

			//�f�[�^�t�H�[�N�ǂݍ���
			if(HCData.readDataFork(dis, stack)){
				dataBytes = null;
				
				int rsrcForkLen = ((0x00FF&header[87])<<24)+
				((0x00FF&header[88])<<16)+
				((0x00FF&header[89])<<8)+
				((0x00FF&header[90]));

				byte[] rsrcBytes = null;
				if(rsrcForkLen>0){ //���\�[�X�t�H�[�N�����邩�H
					//�f�[�^�t�H�[�N�̎��̃u���b�N�܂œǂݔ�΂�
					byte[] nullBytes = new byte[128-((dataForkLen-1)%128+1)];
					try {
						fis.read(nullBytes, 0, 128-((dataForkLen-1)%128+1));
					} catch (IOException e) {
						e.printStackTrace();
					}
	
					//���\�[�X�t�H�[�N������byte�z��ɓǂݍ���
					if(rsrcForkLen>300*1000*1000){
						//300M�ȏ�̂͂����Ȃ�
						rsrcForkLen = 10*1000*1000;//�Ƃ肠����10M�ŁB
					}
					rsrcBytes = new byte[rsrcForkLen];
					try {
						fis.read(rsrcBytes, 0, rsrcForkLen);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(rsrcForkLen==0 || HCResource.readResourceFork(rsrcBytes, stack)){
					stack.barDialog.remove(stack.bar);
					stack.barDialog.dispose();
	
					stack.bar = null;
					stack.barDialog = null;
					
					stack.setName(new File(path).getName());
					return true;
				}
				else{
					System.out.println("Error occured at reading resource data from MacBinary file.");
		    		new GDialog(PCARDFrame.pc, PCARDFrame.pc.intl.getDialogText("Error occured at reading MacBinary HyperCard stack data."),
		    				null,"OK",null,null);
				}
			}
			else{
				System.out.println("Error occured at reading data from MacBinary file.");
	    		new GDialog(PCARDFrame.pc, PCARDFrame.pc.intl.getDialogText("Error occured at reading MacBinary HyperCard stack data."),
	    				null,"OK",null,null);
			}
		}
		
		//�ǂݍ��݃G���[
		stack.barDialog.remove(stack.bar);
		stack.barDialog.dispose();
		return false;
	}

	
	private static boolean CreateStackFolder(OStack stack, String path) {
		stack.bar.setValue(2);
		stack.bar.setString("Create a new folder");
		stack.bar.paintImmediately(stack.bar.getBounds());
		
		//�f�B���N�g�����쐬
		File newdir = new File(path+".hzb");
		int i=2;
		while(newdir.exists()){
			newdir = new File(path+" "+i+".hzb");
			i++;
		}
		if(!newdir.mkdir()){
    		new GDialog(PCARDFrame.pc, PCARDFrame.pc.intl.getDialogText("Can't create a new folder."),
    				null,"OK",null,null);
			return false;
		}
		stack.path = newdir.getPath()+File.separatorChar+"_stack.xml";
		stack.file = new File(stack.path);
		
		return true;
	}
	
	
	public static File MakeAppleDoubleTar(String path){
		//MacOSX�ł��邱��
		String os=System.getProperty("os.name");
        if(os!=null && os.startsWith("Mac OS X")){
        }
        else{
    		/*new GDialog(PCARD.pc, PCARD.pc.intl.getDialogText("Converting from HyperCard Stack is need to run on MacOSX."),
    				null,"OK",null,null);*/
        	return null;
        }

    	String srcpath = path;
        String tarpath = path;
        for(int i=0; i<srcpath.length(); i++){
        	if(srcpath.charAt(i)>=128 || srcpath.length()>=100-2){ //2byte������p�X��tar�Ɋi�[�ł����蒷��������
        		String renameStr;
        		int j=1;
        		while(true){
        			renameStr = System.getProperty("java.io.tmpdir")+File.separatorChar+"hyperzebra_renamed"+j;
        			if(!new File(renameStr).exists()){
        				break;
        			}
        			j++;
        		}
        		new File(srcpath).renameTo(new File(renameStr));
        		srcpath = renameStr;
        		
        		{
        			//�R�s�[�����ƃ��\�[�X�t�H�[�N�������Ă��܂�
        			
	        		//���{��p�X�΍�Ńe���|�����ɃX�^�b�N�̃R�s�[�����
	        		/*srcpath = System.getProperty("java.io.tmpdir")+File.separatorChar+"hyperzebra_stack";
	
					try {
		    		    FileChannel srcChannel = new FileInputStream(path).getChannel();
		    		    FileChannel destChannel = new FileOutputStream(srcpath).getChannel();
		    		    try {
		    		        srcChannel.transferTo(0, srcChannel.size(), destChannel);
		    		    } finally {
		    		        srcChannel.close();
							destChannel.close();
		    		    }
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}*/
        		}

        		//���{��p�X�΍�Ńe���|������tar�A�[�J�C�u�����
        		tarpath = System.getProperty("java.io.tmpdir")+File.separatorChar+"hyperzebra_work";
        		break;
        	}
        }
        
        String path2 = tarpath;
    	File file = new File(path2+".tar");
    	int i=1;
    	while(file.exists()){
    		path2 = tarpath+" "+i;
        	file = new File(path2+".tar");
    		i++;
    	}
    	
        //Apple��tar�ŕۑ�
		try{
			ProcessBuilder pb = new ProcessBuilder("tar", "-cvf", path2+".tar", srcpath);
			//ProcessBuilder pb = new ProcessBuilder("tar", "-cvf", path+".tar", path);
			Process p = pb.start();
			p.waitFor();
			InputStream is = p.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
	        boolean isSuccess = false;
			for (;;) {
				String line = br.readLine();
				if (line == null) break;
				//System.out.println("tar>"+line);
				if(line.startsWith("a "+srcpath)){
					isSuccess = true;
					break;
				}
			}
			if(!isSuccess){
				//success���Ȃ�
				//System.out.println("Error occured at creating tar archive.");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			file = null;
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			file = null;
		}
		
		if(!srcpath.equals(path)){
			//�t�@�C�����ړ����Ă����猳�ɖ߂�
			new File(srcpath).renameTo(new File(path));
		}
		
		if(file!=null){
	    	File ofile = new File(path2+".tar");
	    	if(ofile.exists()){
	    		return file;
	    	}
		}
    	return null;
	}
	
	
	static public String getConvertPath(String inPath){
		byte[] sjisByte = null;
		try {
			sjisByte = inPath.getBytes("SJIS");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String sjisPath = "";
		try {
			sjisPath = new String(sjisByte, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return sjisPath;
	}

	
	public static boolean readFromTar(File tarFile, OStack stack){
		//System.out.println("readFromTar...");
		FileInputStream fis;
		MyTarInputStream tin = null;
		try {
			fis = new FileInputStream(tarFile);
			tin = new HCConvert().new MyTarInputStream(fis); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//Apache ant�̃��C�u������tar������
		if(tin==null) return false;
		try {
			MyTarEntry tarEnt = tin.getNextEntry();
			while (tarEnt != null) {
				String name = tarEnt.getName();
				int size = tarEnt.getSize();
				ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
				tin.copyEntryContents(bos);
				byte[] data = bos.toByteArray();
				DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

				if(name.contains("._")){
					//Apple Double Header File(���\�[�X�t�H�[�N���܂�)
					if(!HCResource.readAppleDoubleHeader(dis, stack)){
						System.out.println("Error occured at reading resouce fork from tar file.");
					}
					stack.barOffset += 25;
				}
				else{
					//�f�[�^�t�H�[�N
					if(!HCData.readDataFork(dis, stack)){
						System.out.println("Error occured at reading data fork from tar file.");
			    		new GDialog(PCARDFrame.pc, PCARDFrame.pc.intl.getDialogText("Error occured at reading HyperCard stack data."),
			    				null,"OK",null,null);
					}
					stack.barOffset += 73;
				}
				
				dis.close();
				tarEnt = tin.getNextEntry();
			}
			tin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	class MyTarInputStream{
		private FileInputStream fis;
		private int size;
		
		MyTarInputStream(FileInputStream fis){
			this.fis = fis;
		}
		
		public void copyEntryContents(ByteArrayOutputStream bos) throws IOException {
			for(int i=0; i<size; i++){
				bos.write(fis.read());
			}
			//�p�f�B���O
			if(512-((size-1)%512+1)>0){
				for(int i=0; i<512-((size-1)%512+1); i++){
					fis.read();
				}
			}
		}
		
		public MyTarEntry getNextEntry() throws IOException{
			//�w�b�_512byte�ǂݍ���
			StringBuilder name = new StringBuilder(100);
			for(int i=0; i<100; i++){
				name.append((char)fis.read());
			}
			for(int i=0; i<24; i++){
				fis.read();
			}
			if(name.toString().equals("")){
				return null; //�I�[���ۂ�
			}
			
			StringBuilder sizeStrbldr = new StringBuilder(12);
			int j;
			for(j=0; j<11; j++){
				char c = (char)fis.read();
				if(c!='0'){
					sizeStrbldr.append(c);
					j++;
					break;
				}
			}
			for(; j<11; j++){
				char c = (char)fis.read();
				sizeStrbldr.append(c);
				if(c<=0){
					return null;
				}
			}
			String sizeStr=sizeStrbldr.toString();
			if(sizeStr.equals("")){
				return null; //�I�[���ۂ�
			}
			size = Integer.valueOf(sizeStr,8);
			for(int i=0; i<512-100-24-11; i++){
				fis.read();
			}
			
			//MyTarEntry�N���X���쐬���Ė��O�ƃT�C�Y��Ԃ�
			MyTarEntry entry = new MyTarEntry(name.toString(), size);
			return entry;
		}
		
		public void close() throws IOException{
			fis.close();
		}
	}
	
	class MyTarEntry{
		String name;
		int size;
		
		MyTarEntry(String name, int size){
			this.name = name;
			this.size = size;
		}
		
		public String getName() {
			return name;
		}

		public int getSize() {
			return size;
		}
	}
}
