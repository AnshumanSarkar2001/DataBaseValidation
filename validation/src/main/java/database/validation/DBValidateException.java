package database.validation;

/**
 * 
 * @author anshuman.sarkar
 * @since 1.5
 */
public class DBValidateException extends Exception {

	private static final long serialVersionUID = 1L;
	private Throwable cause = null;

	public DBValidateException() {
		super();
	}

	public DBValidateException(String s) {
		super(s);
	}

	public DBValidateException(String s, Throwable e) {
		super(s);
		this.cause = e;
	}

	public Throwable getCause() {
		return this.cause;
	}
}