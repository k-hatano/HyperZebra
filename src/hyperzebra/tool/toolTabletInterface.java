package hyperzebra.tool;

interface toolTabletInterface extends toolInterface {
	public void penUp(float x, float y, float pressure, boolean eraser);

	public void penDown(float x, float y, float pressure, boolean eraser);

	public boolean penStillDown(float x, float y, float pressure, boolean eraser);
}