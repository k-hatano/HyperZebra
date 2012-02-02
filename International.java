import java.util.HashMap;



class International {
	HashMap<String,String> menuHash = new HashMap<String,String>(); //�������̂��߂̃c���[
	HashMap<String,String> menuRHash = new HashMap<String,String>(); //�t����
	HashMap<String,String> dialogHash = new HashMap<String,String>(); //�������̂��߂̃c���[
	HashMap<String,String> dialogRHash = new HashMap<String,String>(); //�t����

	public International(String lang) throws Exception{
		if(lang.equals("Japanese") ){
			putHash(menuHash, menuRHash,"File","�t�@�C��");
			putHash(menuHash, menuRHash,"New Stack�c","�V�K�X�^�b�N�c");
			putHash(menuHash, menuRHash,"Open Stack�c","�X�^�b�N���J���c");
			putHash(menuHash, menuRHash,"Open Recent Stack","�ŋߎg�����X�^�b�N���J��");
			putHash(menuHash, menuRHash,"Clear This Menu","���j���[������");
			putHash(menuHash, menuRHash,"Close Stack","�X�^�b�N�����");
			putHash(menuHash, menuRHash,"Save a Copy�c","�ʖ��ŕۑ��c");
			putHash(menuHash, menuRHash,"Compact Stack","�X�^�b�N����");
			putHash(menuHash, menuRHash,"Protect Stack�c","�X�^�b�N�ی�c");
			putHash(menuHash, menuRHash,"Delete Stack�c","�X�^�b�N�폜�c");
			putHash(menuHash, menuRHash,"Print�c","�v�����g�c");
			putHash(menuHash, menuRHash,"Quit HyperCard","HyperCard�I��");
			putHash(menuHash, menuRHash,"Quit","�I��");
			
			putHash(menuHash, menuRHash,"Import Paint�c","�y�C���g��ǂݍ��ށc");
			putHash(menuHash, menuRHash,"Export Paint�c","�y�C���g�������o���c");
			putHash(menuHash, menuRHash,"Save as ppm�c","ppm�t�@�C���Ƃ��ĕۑ��c");
			
			putHash(menuHash, menuRHash,"Edit","�ҏW");
			putHash(menuHash, menuRHash,"Undo","������");
			putHash(menuHash, menuRHash,"Redo","��蒼��");
			putHash(menuHash, menuRHash,"Cut","�J�b�g");
			putHash(menuHash, menuRHash,"Copy","�R�s�[");
			putHash(menuHash, menuRHash,"Paste","�y�[�X�g");
			putHash(menuHash, menuRHash,"Delete","����");
			putHash(menuHash, menuRHash,"Clear Selection","�I��͈͂̏���");
			putHash(menuHash, menuRHash,"New Card","�V�K�J�[�h");
			putHash(menuHash, menuRHash,"Delete Card","�J�[�h�폜");
			putHash(menuHash, menuRHash,"Cut Card","�J�b�g �J�[�h");
			putHash(menuHash, menuRHash,"Copy Card","�R�s�[ �J�[�h");
			putHash(menuHash, menuRHash,"Background","�o�b�N�O���E���h");
			putHash(menuHash, menuRHash,"Icon�c","�A�C�R���ҏW�c");
			putHash(menuHash, menuRHash,"Sound�c","�T�E���h�ҏW�c");
			putHash(menuHash, menuRHash,"Resource�c","���\�[�X�ҏW�c");

			putHash(menuHash, menuRHash,"Cut Button","�J�b�g �{�^��");
			putHash(menuHash, menuRHash,"Copy Button","�R�s�[ �{�^��");
			putHash(menuHash, menuRHash,"Paste Button","�y�[�X�g �{�^��");
			putHash(menuHash, menuRHash,"Delete Button","���� �{�^��");

			putHash(menuHash, menuRHash,"Cut Field","�J�b�g �t�B�[���h");
			putHash(menuHash, menuRHash,"Copy Field","�R�s�[ �t�B�[���h");
			putHash(menuHash, menuRHash,"Paste Field","�y�[�X�g �t�B�[���h");
			putHash(menuHash, menuRHash,"Delete Field","���� �t�B�[���h");
			
			putHash(menuHash, menuRHash,"Undo Paint","�y�C���g�̎�����");
			putHash(menuHash, menuRHash,"Redo Paint","�y�C���g�̂�蒼��");
			putHash(menuHash, menuRHash,"Cut Picture","�J�b�g �s�N�`���A");
			putHash(menuHash, menuRHash,"Copy Picture","�R�s�[ �s�N�`���A");
			putHash(menuHash, menuRHash,"Paste Picture","�y�[�X�g �s�N�`���A");

			putHash(menuHash, menuRHash,"Go","�S�[");
			putHash(menuHash, menuRHash,"Back","�߂�");
			putHash(menuHash, menuRHash,"Home","�z�[��");
			putHash(menuHash, menuRHash,"Help","�w���v");
			putHash(menuHash, menuRHash,"Recent","���[�Z���g");
			putHash(menuHash, menuRHash,"First","�ŏ��̃J�[�h");
			putHash(menuHash, menuRHash,"Prev","�O�̃J�[�h");
			putHash(menuHash, menuRHash,"Next","���̃J�[�h");
			putHash(menuHash, menuRHash,"Last","�Ō�̃J�[�h");
			putHash(menuHash, menuRHash,"Find�c","�����c");
			putHash(menuHash, menuRHash,"Message","���b�Z�[�W");
			putHash(menuHash, menuRHash,"Next Window","���̃E�B���h�E");

			putHash(menuHash, menuRHash,"Tool","�c�[��");
			putHash(menuHash, menuRHash,"Hide ToolBar","�c�[���o�[���B��");
			putHash(menuHash, menuRHash,"Show ToolBar","�c�[���o�[��\��");
			putHash(menuHash, menuRHash,"Browse","�u���E�Y");
			putHash(menuHash, menuRHash,"Button","�{�^��");
			putHash(menuHash, menuRHash,"Field","�t�B�[���h");
			//putHash(menuHash, menuRHash,"Hide ColorPalette","�J���[�p���b�g���B��");
			//putHash(menuHash, menuRHash,"Show ColorPalette","�J���[�p���b�g��\��");
			//putHash(menuHash, menuRHash,"Hide PatternPalette","�p�^�[���p���b�g���B��");
			//putHash(menuHash, menuRHash,"Show PatternPalette","�p�^�[���p���b�g��\��");

			putHash(menuHash, menuRHash,"Objects","�I�u�W�F�N�g");
			
			putHash(menuHash, menuRHash,"Font","�t�H���g");
			
			putHash(menuHash, menuRHash,"Paint","�y�C���g");
			putHash(menuHash, menuRHash,"Select","�I��");
			putHash(menuHash, menuRHash,"Select All","���ׂĂ�I��");
			putHash(menuHash, menuRHash,"FatBits","�g��\��");
			putHash(menuHash, menuRHash,"Grid","�O���b�h");
			putHash(menuHash, menuRHash,"Use Grid","�O���b�h��\��");
			putHash(menuHash, menuRHash,"Grid Size 1","�O���b�h�T�C�Y1");
			putHash(menuHash, menuRHash,"Grid Size 16","�O���b�h�T�C�Y16");
			putHash(menuHash, menuRHash,"Antialias","�A���`�G�C���A�X");
			putHash(menuHash, menuRHash,"Fill","�h��Ԃ�");
			putHash(menuHash, menuRHash,"Invert","���]");
			putHash(menuHash, menuRHash,"Pickup","�s�b�N�A�b�v");
			putHash(menuHash, menuRHash,"Darken","�Â�����");
			putHash(menuHash, menuRHash,"Lighten","���邭����");
			putHash(menuHash, menuRHash,"Rotate Left","����]");
			putHash(menuHash, menuRHash,"Rotate Right","�E��]");
			putHash(menuHash, menuRHash,"Flip Horizontal","���E���]");
			putHash(menuHash, menuRHash,"Flip Vertical","�㉺���]");
			putHash(menuHash, menuRHash,"Opaque","�s����");
			putHash(menuHash, menuRHash,"Transparent","����");
			putHash(menuHash, menuRHash,"Keep","��Ƃ̕ۑ�");
			putHash(menuHash, menuRHash,"Revert","���A");
			putHash(menuHash, menuRHash,"Rotate","��");
			putHash(menuHash, menuRHash,"Distort","���R�ɕό`");
			putHash(menuHash, menuRHash,"Stretch","�X����");
			putHash(menuHash, menuRHash,"Perspective","���ߌ���");
			putHash(menuHash, menuRHash,"Color Convert�c","�F�̕ύX�c");
			putHash(menuHash, menuRHash,"Emboss�c","�����o�����ʁc");
			putHash(menuHash, menuRHash,"Scale Selection�c","�I��͈͂̊g��k���c");
			putHash(menuHash, menuRHash,"Reverse Selection","�I��͈͂̋t�]");
			putHash(menuHash, menuRHash,"Expand Selection","�I��͈͂��L����");
			putHash(menuHash, menuRHash,"Filter�c","�t�B���^�[�c");
			putHash(menuHash, menuRHash,"Blending Mode�c","�������[�h�c");

			putHash(menuHash, menuRHash,"Tool","�c�[��");
			putHash(menuHash, menuRHash,"Browse","�u���E�Y");
			putHash(menuHash, menuRHash,"Button","�{�^��");
			putHash(menuHash, menuRHash,"Field","�t�B�[���h");

			putHash(menuHash, menuRHash,"Select","�I��");
			putHash(menuHash, menuRHash,"Lasso","�����Ȃ�");
			putHash(menuHash, menuRHash,"MagicWand","�����I��");
			putHash(menuHash, menuRHash,"Pencil","���M");
			putHash(menuHash, menuRHash,"Brush","�u���V");
			putHash(menuHash, menuRHash,"Eraser","�����S��");
			putHash(menuHash, menuRHash,"Line","��");
			putHash(menuHash, menuRHash,"SprayCan","�X�v���[");
			putHash(menuHash, menuRHash,"Rect","�����`");
			putHash(menuHash, menuRHash,"RoundRect","�ۂ݂̂��钷���`");
			putHash(menuHash, menuRHash,"PaintBucket","�o�P�c");
			putHash(menuHash, menuRHash,"Oval","�ȉ~");
			putHash(menuHash, menuRHash,"Curve","�~��");
			putHash(menuHash, menuRHash,"Type","����");
			putHash(menuHash, menuRHash,"Polygon","���p�`");
			putHash(menuHash, menuRHash,"FreePolygon","�t���[���p�`");
			putHash(menuHash, menuRHash,"Spoit","�X�|�C�g");

			putHash(menuHash, menuRHash,"Transparency","�����x");
			putHash(menuHash, menuRHash,"Gradation","�O���f�[�V����");
			putHash(menuHash, menuRHash,"Angle","�p�x");
			putHash(menuHash, menuRHash,"Fill","�h��Ԃ�");
			putHash(menuHash, menuRHash,"Don't Fill","�h��Ԃ��Ȃ�");

			putHash(menuHash, menuRHash,"Button Info�c","�{�^�����c");
			putHash(menuHash, menuRHash,"Field Info�c","�t�B�[���h���c");
			putHash(menuHash, menuRHash,"Card Info�c","�J�[�h���c");
			putHash(menuHash, menuRHash,"Background Info�c","�o�b�N�O���E���h���c");
			putHash(menuHash, menuRHash,"Stack Info�c","�X�^�b�N���c");
			putHash(menuHash, menuRHash,"Bring Closer","�O�ʂɏo��");
			putHash(menuHash, menuRHash,"Send Farther","�w�ʂɑ���");
			putHash(menuHash, menuRHash,"New Button","�V�K�{�^��");
			putHash(menuHash, menuRHash,"New Field","�V�K�t�B�[���h");
			putHash(menuHash, menuRHash,"New Background","�V�K�o�b�N�O���E���h");
			
			putHash(menuHash, menuRHash,"Close","����");
			putHash(menuHash, menuRHash,"Save","�ۑ�");
			putHash(menuHash, menuRHash,"Find","����");
			putHash(menuHash, menuRHash,"Find Next","��������");
			putHash(menuHash, menuRHash,"Find Prev","�O������");
			putHash(menuHash, menuRHash,"Replace","�u������");
			putHash(menuHash, menuRHash,"Replace Next","����u������");
			putHash(menuHash, menuRHash,"Replace Prev","�O��u������");
			putHash(menuHash, menuRHash,"Script","�X�N���v�g");
			putHash(menuHash, menuRHash,"Edit Card Script","�J�[�h�X�N���v�g���J��");
			putHash(menuHash, menuRHash,"Edit Background Script","�o�b�N�O���E���h�X�N���v�g���J��");
			putHash(menuHash, menuRHash,"Edit Stack Script","�X�^�b�N�X�N���v�g���J��");
			putHash(menuHash, menuRHash,"Comment","�R�����g�ɂ���");
			putHash(menuHash, menuRHash,"Uncomment","�R�����g���O��");
			
			putHash(menuHash, menuRHash,"Debug","�f�o�b�O");
			putHash(menuHash, menuRHash,"Step","�X�e�b�v");
			putHash(menuHash, menuRHash,"Trace","�g���[�X");
			putHash(menuHash, menuRHash,"Run","���s");
			putHash(menuHash, menuRHash,"Variable Watcher","�ϐ��\���E�B���h�E");

			putHash(menuHash, menuRHash,"New Item","�V�K�쐬");
			putHash(menuHash, menuRHash,"Open","�J��");
			putHash(menuHash, menuRHash,"Image Size�c","�T�C�Y�ύX�c");
			putHash(menuHash, menuRHash,"View File","�t�@�C����\��");
			putHash(menuHash, menuRHash,"Hot Spot�c","�z�b�g�X�|�b�g�c");
			

			//�_�C�A���O�p
			putHash(dialogHash, dialogRHash,"Save File","�t�@�C���ɕۑ�");
			putHash(dialogHash, dialogRHash,"Script Editor","�X�N���v�g�G�f�B�^");
			putHash(dialogHash, dialogRHash,"Script is not saved.","�X�N���v�g���ۑ�����Ă��܂���");
			putHash(dialogHash, dialogRHash,"Save","�ۑ�����");
			putHash(dialogHash, dialogRHash,"Discard","�ۑ����Ȃ�");
			putHash(dialogHash, dialogRHash,"Find String","����");
			putHash(dialogHash, dialogRHash,"Find Prev","�O������");
			putHash(dialogHash, dialogRHash,"Find Next","��������");
			putHash(dialogHash, dialogRHash,"Replace String","�u��");
			putHash(dialogHash, dialogRHash,"Replace All","���ׂĒu��");
			putHash(dialogHash, dialogRHash,"Replace Prev","�O��u��");
			putHash(dialogHash, dialogRHash,"Replace Next","����u��");
			
			putHash(dialogHash, dialogRHash,"Icon Editor","�A�C�R���G�f�B�^");
			putHash(dialogHash, dialogRHash,"Name:","���O:");
			putHash(dialogHash, dialogRHash,"ID:","ID:");
			putHash(dialogHash, dialogRHash,"Width:","��:");
			putHash(dialogHash, dialogRHash,"Height:","����:");
			putHash(dialogHash, dialogRHash,"Cancel","�L�����Z��");

			putHash(dialogHash, dialogRHash,"New Button","�V�K�{�^��");
			
			putHash(dialogHash, dialogRHash,"Button Name:","�{�^����:");
			putHash(dialogHash, dialogRHash,"Card button ","�J�[�h�{�^�� ");
			putHash(dialogHash, dialogRHash,"Background button ","�o�b�N�O���E���h�{�^�� ");
			putHash(dialogHash, dialogRHash,"ID: ","ID: ");
			putHash(dialogHash, dialogRHash,"Number: ","�ԍ�: ");
			putHash(dialogHash, dialogRHash,"Part Number: ","���i�ԍ�: ");
			putHash(dialogHash, dialogRHash,"Style:","�`��:");
			putHash(dialogHash, dialogRHash,"Standard","�W��");
			putHash(dialogHash, dialogRHash,"Transparent","����");
			putHash(dialogHash, dialogRHash,"Opaque","�s����");
			putHash(dialogHash, dialogRHash,"Rectangle","�����`");
			putHash(dialogHash, dialogRHash,"Shadow","�V���h�E");
			putHash(dialogHash, dialogRHash,"RoundRect","�ۂ݂̂��钷���`");
			putHash(dialogHash, dialogRHash,"Default","�ȗ����ݒ�");
			putHash(dialogHash, dialogRHash,"Oval","�ȉ~");
			putHash(dialogHash, dialogRHash,"Popup","�|�b�v�A�b�v");
			putHash(dialogHash, dialogRHash,"CheckBox","�`�F�b�N�{�b�N�X");
			putHash(dialogHash, dialogRHash,"Radio","���W�I�{�^��");
			putHash(dialogHash, dialogRHash,"Family:","�t�@�~���[:");
			putHash(dialogHash, dialogRHash,"Show Name","���O��\��");
			putHash(dialogHash, dialogRHash,"Enabled","�g����悤��");
			putHash(dialogHash, dialogRHash,"Visible","������悤��");
			putHash(dialogHash, dialogRHash,"Auto Hilite","�I�[�g�n�C���C�g");
			putHash(dialogHash, dialogRHash,"Shared Hilite","�n�C���C�g�����L");
			putHash(dialogHash, dialogRHash,"Scale Icon","�A�C�R���̊g��k��");
			putHash(dialogHash, dialogRHash,"Font�c","�t�H���g�c");
			putHash(dialogHash, dialogRHash,"Icon�c","�A�C�R���c");
			putHash(dialogHash, dialogRHash,"Effect�c","���o���ʁc");
			putHash(dialogHash, dialogRHash,"LinkTo�c","�ړ��c");
			putHash(dialogHash, dialogRHash,"Script�c","�X�N���v�g�c");
			putHash(dialogHash, dialogRHash,"Content�c","���e�c");
			putHash(dialogHash, dialogRHash,"None","�Ȃ�");

			putHash(dialogHash, dialogRHash,"Field Name:","�t�B�[���h��:");
			putHash(dialogHash, dialogRHash,"Card field ","�J�[�h�t�B�[���h ");
			putHash(dialogHash, dialogRHash,"Background field ","�o�b�N�O���E���h�t�B�[���h ");
			putHash(dialogHash, dialogRHash,"Locked text","���b�N�e�L�X�g");
			putHash(dialogHash, dialogRHash,"Don't wrap","�s����荞�܂��Ȃ�");
			putHash(dialogHash, dialogRHash,"Auto select","�����I�ɑI��");
			putHash(dialogHash, dialogRHash,"Multiple lines","�����s");
			putHash(dialogHash, dialogRHash,"Wide margins","�]�����L��");
			putHash(dialogHash, dialogRHash,"Fixed line height","�s�̍������Œ�");
			putHash(dialogHash, dialogRHash,"Show lines","�s�\��");
			putHash(dialogHash, dialogRHash,"Auto tab","�I�[�g�^�u");
			putHash(dialogHash, dialogRHash,"Don't search","�������Ȃ�");
			putHash(dialogHash, dialogRHash,"Shared text","�e�L�X�g�����L");
			putHash(dialogHash, dialogRHash,"Scroll","�X�N���[��");

			putHash(dialogHash, dialogRHash,"Card Name:","�J�[�h��:");
			putHash(dialogHash, dialogRHash,"Card ","�J�[�h ");
			putHash(dialogHash, dialogRHash,"Background Name:","�o�b�N�O���E���h��:");
			putHash(dialogHash, dialogRHash,"Background ","�o�b�N�O���E���h ");
			putHash(dialogHash, dialogRHash,"Show picture","�s�N�`����\��");
			putHash(dialogHash, dialogRHash,"Marked","�}�[�N");
			putHash(dialogHash, dialogRHash,"Can't delete","�폜�s��");
			
			putHash(dialogHash, dialogRHash,"Stack Name:","�X�^�b�N��:");
			putHash(dialogHash, dialogRHash,"Stack Path: ","�X�^�b�N�̃p�X: ");
			putHash(dialogHash, dialogRHash,"Size�c","�傫���c");

			putHash(dialogHash, dialogRHash,"New Stack","�V�K�X�^�b�N");

			putHash(dialogHash, dialogRHash,"Color Convert","�F�̕ύX");
			putHash(dialogHash, dialogRHash,"Divide","������");
			putHash(dialogHash, dialogRHash,"Unite","���킹��");
			
			putHash(dialogHash, dialogRHash,"Emboss","�����o������");
			putHash(dialogHash, dialogRHash,"Thickness","����");
			putHash(dialogHash, dialogRHash,"Use","�g�p����");
			putHash(dialogHash, dialogRHash,"Brightness ","���邳 ");
			putHash(dialogHash, dialogRHash,"Width ","�� ");
			putHash(dialogHash, dialogRHash,"Gradation","�O���f�[�V����");
			putHash(dialogHash, dialogRHash,"Highlight","�n�C���C�g");
			putHash(dialogHash, dialogRHash,"Area ","�͈� ");
			putHash(dialogHash, dialogRHash,"Reflection","����");
			putHash(dialogHash, dialogRHash,"Line","����");
			putHash(dialogHash, dialogRHash,"Curve","��");
			putHash(dialogHash, dialogRHash,"Fit","�`��ɍ��킹��");
			putHash(dialogHash, dialogRHash,"Angle ","�p�x ");

			putHash(dialogHash, dialogRHash,"Scale Selection","�I��͈͂̊g��k��");
			putHash(dialogHash, dialogRHash,"Keep aspect ratio","�c������Œ�");
			putHash(dialogHash, dialogRHash,"Choose from Screen","��ʂ̐F���擾");

			putHash(dialogHash, dialogRHash,"Filter","�t�B���^�[");
			putHash(dialogHash, dialogRHash,"Auto"," ����");
			putHash(dialogHash, dialogRHash,"Trace Edges","�����");
			putHash(dialogHash, dialogRHash,"Trace Edges 2","�����2");
			putHash(dialogHash, dialogRHash,"Spread Edges Dark","�֊s�𑾂�(�Â��F)");
			putHash(dialogHash, dialogRHash,"Spread Edges","�֊s�𑾂�");
			putHash(dialogHash, dialogRHash,"Spread Edges Light","�֊s�𑾂�(���邢�F)");
			putHash(dialogHash, dialogRHash,"Small Median","���f�B�A����");
			putHash(dialogHash, dialogRHash,"Median","���f�B�A��");
			putHash(dialogHash, dialogRHash,"Large Median","���f�B�A����");
			putHash(dialogHash, dialogRHash,"Motion Blur","���[�V�����u���[");
			putHash(dialogHash, dialogRHash,"Sharpen","�V���[�v");
			putHash(dialogHash, dialogRHash,"Blur","�ڂ���");
			putHash(dialogHash, dialogRHash,"Glass Tile","�K���X�^�C��");
			putHash(dialogHash, dialogRHash,"Frosted Glass","����K���X");
			putHash(dialogHash, dialogRHash,"Horizontal Wave","�����E�F�[�u");
			putHash(dialogHash, dialogRHash,"Vertical Wave","�����E�F�[�u");
			putHash(dialogHash, dialogRHash,"Noise","�m�C�Y��������");
			putHash(dialogHash, dialogRHash,"Higher Contrast","�R���g���X�g������");
			putHash(dialogHash, dialogRHash,"Lower Contrast","�R���g���X�g���キ");
			putHash(dialogHash, dialogRHash,"Higher Saturation","�ʓx���グ��");
			putHash(dialogHash, dialogRHash,"Lower Saturation","�ʓx��������");
			putHash(dialogHash, dialogRHash,"Grayscale","�O���[�X�P�[��");
			putHash(dialogHash, dialogRHash,"Binarization","����(�f�B�U�����O����)");
			putHash(dialogHash, dialogRHash,"Binarization","����");
			putHash(dialogHash, dialogRHash,"Index Color with Dithering","���F(�f�B�U�����O����)");
			putHash(dialogHash, dialogRHash,"Index Color","���F");

			putHash(dialogHash, dialogRHash,"Blending Mode","�������[�h");
			putHash(dialogHash, dialogRHash,"Copy","�R�s�[");
			putHash(dialogHash, dialogRHash,"Blend","�u�����h");
			putHash(dialogHash, dialogRHash,"Add","����");
			putHash(dialogHash, dialogRHash,"Subtract","�A�e");
			putHash(dialogHash, dialogRHash,"Multiply","��Z");
			putHash(dialogHash, dialogRHash,"Screen","�X�N���[��");
			putHash(dialogHash, dialogRHash,"Darken","�Â��F���c��");
			putHash(dialogHash, dialogRHash,"Lighten","���邢�F���c��");
			putHash(dialogHash, dialogRHash,"Difference","���̐�Βl");
			putHash(dialogHash, dialogRHash,"Hue","�F��");
			putHash(dialogHash, dialogRHash,"Color","�F����");
			putHash(dialogHash, dialogRHash,"Saturation","�ʓx");
			putHash(dialogHash, dialogRHash,"Luminosity","�P�x");
			putHash(dialogHash, dialogRHash,"Alpha Channel","�����x");

			//�t�H���g�_�C�A���O
			putHash(dialogHash, dialogRHash,"Font","�t�H���g");
			putHash(dialogHash, dialogRHash,"Size","�T�C�Y");
			putHash(dialogHash, dialogRHash,"Style","�X�^�C��");
			putHash(dialogHash, dialogRHash,"Bold","����");
			putHash(dialogHash, dialogRHash,"Italic","�Α�");
			putHash(dialogHash, dialogRHash,"Underline","����");
			putHash(dialogHash, dialogRHash,"Outline","�A�E�g���C��");
			putHash(dialogHash, dialogRHash,"Shadow","�V���h�E");
			putHash(dialogHash, dialogRHash,"Condensed","���Ԃ�����");
			putHash(dialogHash, dialogRHash,"Extend","���Ԃ��L��");
			putHash(dialogHash, dialogRHash,"Align Left","����");
			putHash(dialogHash, dialogRHash,"Align Center","�Z���^�����O");
			putHash(dialogHash, dialogRHash,"Align Right","�E��");
			
			//��ʃG���[
			putHash(dialogHash, dialogRHash,"Could't open the file.","�t�@�C�����J���܂���ł���");
			putHash(dialogHash, dialogRHash,"Drop file here","�X�^�b�N�������Ƀh���b�v���Ă�������");
			putHash(dialogHash, dialogRHash,"Illegal size.","�T�C�Y���s���ł�");
			putHash(dialogHash, dialogRHash,"No selected button.","�{�^�����I������Ă��܂���");
			putHash(dialogHash, dialogRHash,"No selected field.","�t�B�[���h���I������Ă��܂���");
			putHash(dialogHash, dialogRHash,"Converting from HyperCard Stack is need to run on MacOSX.","HyperCard�X�^�b�N�̃R���o�[�g��MacOSX��ł̂ݗ��p�ł��܂�");
			putHash(dialogHash, dialogRHash,"Can't create a new folder.","�t�H���_���쐬�ł��܂���");
			putHash(dialogHash, dialogRHash,"Error occured at reading HyperCard stack data.","HyperCard�X�^�b�N�̓ǂݍ��݂ŃG���[���������܂���");
			putHash(dialogHash, dialogRHash,"Error occured at reading MacBinary HyperCard stack data.","MacBinary�G���R�[�h���ꂽHyperCard�X�^�b�N�̓ǂݍ��݂ŃG���[���������܂���");
			putHash(dialogHash, dialogRHash,"Resource data is not found.","���\�[�X�t�@�C����������܂���ł���");
			putHash(dialogHash, dialogRHash,"This version of Java Runtime is not supported streaming API for XML.", "Java�����^�C���̃o�[�W�������Â�����XML�ǂݍ��݋@�\���g���܂���");
			putHash(dialogHash, dialogRHash,"Error occured at reading XML file.", "XML�f�[�^�̓ǂݍ��ݒ��ɃG���[���������܂���");
			putHash(dialogHash, dialogRHash,"XML end tag is not found.", "XML�t�@�C�����Ō�܂œǂ߂܂���ł���");
			putHash(dialogHash, dialogRHash,"This file name already exists. If you continue, the existing file will be replaced.","���̃t�@�C�����͊��Ɏg���Ă��܂��B�㏑�����܂����H");
			putHash(dialogHash, dialogRHash,"At least one card is required.","�X�^�b�N�ɂ͕K���J�[�h���ꖇ�ȏ�K�v�ł�");
			putHash(dialogHash, dialogRHash,"Can't open the file '%1'.","�t�@�C��'%1'���J���܂���ł���");
			putHash(dialogHash, dialogRHash,"This resource is not in this stack. Make a copy?","���̃��\�[�X�͂��̃X�^�b�N�ɂ͂���܂���B�R�s�[���쐬���܂��B");
			
			//�X�N���v�g�G���[
			
		}
		else if(lang.equals("English") ){
			//�������Ȃ�
		}
		else {
			throw new Exception("Unsupported Language \""+lang+"\"");
		}
	}
	
	private void putHash(HashMap<String,String> nh, HashMap<String,String> rh, String eng, String other){
		nh.put(eng, other);
		if(rh!=null){
			rh.put(other, eng);
		}
	}
	
	public String getText(String name){
		String ret = menuHash.get(name);
		if(ret==null) return name;
		else return ret;
	}
	
	public String getEngText(String name){
		String ret = menuRHash.get(name);
		if(ret==null) return name;
		else return ret;
	}
	
	public String getToolText(String name){
		return getText(name);
	}
	
	public String getToolEngText(String name){
		return getEngText(name);
	}
	
	public String getDialogText(String name){
		String ret = dialogHash.get(name);
		if(ret==null) return name;
		else return ret;
	}
	
	public String getDialogEngText(String name){
		String ret = dialogRHash.get(name);
		if(ret==null) return name;
		else return ret;
	}
}
