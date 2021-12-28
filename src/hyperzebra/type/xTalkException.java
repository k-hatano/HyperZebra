package hyperzebra.type;

public class xTalkException extends Exception {
	private static final long serialVersionUID = 1L;

	public xTalkException(String msg) {
		super(msg);
	}

	public xTalkException(String msg, int line) {
		super(msg + "(" + line + "行目)");
	}
}
