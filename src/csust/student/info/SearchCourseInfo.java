package csust.student.info;

import java.io.Serializable;

/**
 * 用于展示学生端搜索而得的courseinfo,和Android端来对应。
 * 
 * @author U-anLA
 *
 */
public class SearchCourseInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int course_id;
	private String course_name;
	private int teacher_id;
	private String teacher_name;

	public int getCourse_id() {
		return course_id;
	}

	public void setCourse_id(int course_id) {
		this.course_id = course_id;
	}

	public String getCourse_name() {
		return course_name;
	}

	public void setCourse_name(String course_name) {
		this.course_name = course_name;
	}

	public int getTeacher_id() {
		return teacher_id;
	}

	public void setTeacher_id(int teacher_id) {
		this.teacher_id = teacher_id;
	}

	public String getTeacher_name() {
		return teacher_name;
	}

	public void setTeacher_name(String teacher_name) {
		this.teacher_name = teacher_name;
	}

	@Override
	public String toString() {
		return "SearchCourseInfo [course_id=" + course_id + ", course_name="
				+ course_name + ", teacher_id=" + teacher_id
				+ ", teacher_name=" + teacher_name + "]";
	}

}
