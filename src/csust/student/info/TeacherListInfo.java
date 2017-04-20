package csust.student.info;

import java.io.Serializable;

/**
 * 用于Android端的学生端获得教师列表的
 * 
 * @author U-ANLA
 *
 */
public class TeacherListInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int teacher_id;
	private String teacher_username;
	private String teacher_name;

	public int getTeacher_id() {
		return teacher_id;
	}

	public void setTeacher_id(int teacher_id) {
		this.teacher_id = teacher_id;
	}

	public String getTeacher_username() {
		return teacher_username;
	}

	public void setTeacher_username(String teacher_username) {
		this.teacher_username = teacher_username;
	}

	public String getTeacher_name() {
		return teacher_name;
	}

	public void setTeacher_name(String teacher_name) {
		this.teacher_name = teacher_name;
	}

	@Override
	public String toString() {
		return "TeacherListInfo [teacher_id=" + teacher_id
				+ ", teacher_username=" + teacher_username + ", teacher_name="
				+ teacher_name + "]";
	}

}
