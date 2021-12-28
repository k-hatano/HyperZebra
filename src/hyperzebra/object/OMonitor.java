package hyperzebra.object;

public class OMonitor extends OObject {
	static OMonitor monitor = new OMonitor();

	// メイン
	public OMonitor() {
		objectType = "monitor";
	}
}