package csust.student.info;

import java.io.Serializable;

public class UserInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int student_id;
	private String student_name;
	private String student_sex;
	private String student_num;
	private String student_username;
	private String student_password;

	public UserInfo() {
		super();

	}

	public UserInfo(int student_id, String student_name, String student_sex,
			String student_num, String student_username, String student_password) {
		super();
		this.student_id = student_id;
		this.student_name = student_name;
		this.student_sex = student_sex;
		this.student_num = student_num;
		this.student_username = student_username;
		this.student_password = student_password;
	}

	public int getStudent_id() {
		return student_id;
	}

	public void setStudent_id(int student_id) {
		this.student_id = student_id;
	}

	public String getStudent_name() {
		return student_name;
	}

	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}

	public String getStudent_sex() {
		return student_sex;
	}

	public void setStudent_sex(String student_sex) {
		this.student_sex = student_sex;
	}

	public String getStudent_num() {
		return student_num;
	}

	public void setStudent_num(String student_num) {
		this.student_num = student_num;
	}

	public String getStudent_username() {
		return student_username;
	}

	public void setStudent_username(String student_username) {
		this.student_username = student_username;
	}

	public String getStudent_password() {
		return student_password;
	}

	public void setStudent_password(String student_password) {
		this.student_password = student_password;
	}

	@Override
	public String toString() {
		return "Student [student_id=" + student_id + ", student_name="
				+ student_name + ", student_sex=" + student_sex
				+ ", student_num=" + student_num + ", student_username="
				+ student_username + "]";
	}

}
