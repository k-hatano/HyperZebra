package hyperzebra.tool;

public interface toolInterface {
	public void mouseUp(int x, int y);

	public void mouseDown(int x, int y);

	public boolean mouseWithin(int x, int y);

	public boolean mouseStillDown(int x, int y);

	public void clear();

	public void end();

	public String getName();
}
