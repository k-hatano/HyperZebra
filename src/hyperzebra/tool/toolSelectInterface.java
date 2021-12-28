package hyperzebra.tool;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import hyperzebra.gui.PCARDFrame;

public interface toolSelectInterface extends toolInterface {
	public BufferedImage getSelectedSurface(PCARDFrame owner);

	public Rectangle getSelectedRect();

	public Rectangle getMoveRect();

	public boolean isMove();
}