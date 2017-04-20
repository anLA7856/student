package csust.student.info;

import java.io.Serializable;

public class SignInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String sign_date;
	private String sign_courseName;
	private String sign_courseNum;
	private String sign_teacherName;
	// 用于保存当前sign的教师的wifimac
	private String teacher_wifimac;
	//吧alowsignid传过去。
	private String alow_sign_id;

	public SignInfo() {

	}

	@Override
	public String toString() {
		return "SignInfo [sign_date=" + sign_date + ", sign_courseName="
				+ sign_courseName + ", sign_courseNum=" + sign_courseNum
				+ ", sign_teacherName=" + sign_teacherName
				+ ", teacher_wifimac=" + teacher_wifimac + ", alow_sign_id="
				+ alow_sign_id + "]";
	}

	public String getAlow_sign_id() {
		return alow_sign_id;
	}

	public void setAlow_sign_id(String alow_sign_id) {
		this.alow_sign_id = alow_sign_id;
	}

	public String getTeacher_wifimac() {
		return teacher_wifimac;
	}

	public void setTeacher_wifimac(String teacher_wifimac) {
		this.teacher_wifimac = teacher_wifimac;
	}

	public String getSign_date() {
		return sign_date;
	}

	public void setSign_date(String sign_date) {
		this.sign_date = sign_date;
	}

	public String getSign_courseName() {
		return sign_courseName;
	}

	public void setSign_courseName(String sign_courseName) {
		this.sign_courseName = sign_courseName;
	}

	public String getSign_courseNum() {
		return sign_courseNum;
	}

	public void setSign_courseNum(String sign_courseNum) {
		this.sign_courseNum = sign_courseNum;
	}

	public String getSign_teacherName() {
		return sign_teacherName;
	}

	public void setSign_teacherName(String sign_teacherName) {
		this.sign_teacherName = sign_teacherName;
	}

}
