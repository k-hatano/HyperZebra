package hyperzebra.object;

import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;

import hyperzebra.TMP3;
import hyperzebra.gui.dialog.GPictWindow;

public class OWindow extends OObject {
	public static OWindow cdwindow;
	public static OWindow msgwindow;
	public static ArrayList<OWindow> list = new ArrayList<OWindow>();
	public JFrame frame;
	public JDialog dlog;
	public TMP3 mp3;
	public GPictWindow gpw;

	// メイン
	public OWindow(JFrame inframe, boolean isCdWindow) {
		objectType = "window";
		frame = inframe;
		if (isCdWindow) {
			cdwindow = this;
			this.name = "cd";
		} else
			this.name = inframe.getTitle();

		list.add(this);
	}

	// メイン(Msg)
	public OWindow(JDialog inframe) {
		objectType = "window";
		dlog = inframe;
		this.name = inframe.getTitle();

		list.add(this);
	}

	// メイン(Movieウィンドウ)
	public OWindow(TMP3 in_mp3) {
		objectType = "window";
		mp3 = in_mp3;
		this.name = mp3.name;

		list.add(this);
	}

	public OWindow(GPictWindow in_gpw) {
		objectType = "window";
		gpw = in_gpw;
		this.name = gpw.name;

		list.add(this);
	}

	public void Command(String cmdStr) {
		if (mp3 != null) {
			if (cmdStr.equalsIgnoreCase("Play")) {
				mp3.mp3play();
			}
		}
	}

	public void Close() {
		if (mp3 != null) {
			mp3.mp3stop();
			list.remove(this);
		}
	}
}
