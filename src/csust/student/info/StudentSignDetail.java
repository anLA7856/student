package csust.student.info;

import java.io.Serializable;

/**
 * 用于Android端的展示每一门课的学生所有的签到情况。
 * 
 * @author U-anLA
 *
 */
public class StudentSignDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sign_state;
	private String sign_date;

	public String getSign_state() {
		return sign_state;
	}

	public void setSign_state(String sign_state) {
		this.sign_state = sign_state;
	}

	public String getSign_date() {
		return sign_date;
	}

	public void setSign_date(String sign_date) {
		this.sign_date = sign_date;
	}

}
