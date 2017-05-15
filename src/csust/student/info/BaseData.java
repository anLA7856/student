package csust.student.info;

import java.io.Serializable;

/**
 * 
 * @author
 *
 */
public abstract class BaseData implements Serializable {

	protected static final int STATUS_OK = 0;
	protected static final int STATUS_ERROR = -1;
	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private transient int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
