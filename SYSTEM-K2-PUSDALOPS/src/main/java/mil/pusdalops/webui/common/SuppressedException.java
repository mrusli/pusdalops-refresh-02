package mil.pusdalops.webui.common;

public class SuppressedException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1821043257335605085L;

	public SuppressedException(String errorMessage) {
		super(errorMessage);
		
		this.setStackTrace(new StackTraceElement[0]);
	}
}
