package csust.student.info;

import java.io.Serializable;

public class CourseInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String course_id;
	private String teacherNum;
	private String teacherName;
	private String courseName;

	public String getCourse_id() {
		return course_id;
	}

	public void setCourse_id(String course_id) {
		this.course_id = course_id;
	}

	public String getTeacherNum() {
		return teacherNum;
	}

	public void setTeacherNum(String teacherNum) {
		this.teacherNum = teacherNum;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public CourseInfo(String teacherNum, String teacherName, String courseName) {
		super();
		this.teacherNum = teacherNum;
		this.teacherName = teacherName;
		this.courseName = courseName;
	}

	public CourseInfo() {

	}

	@Override
	public String toString() {
		return "CourseInfo [course_id=" + course_id + ", teacherNum="
				+ teacherNum + ", teacherName=" + teacherName + ", courseName="
				+ courseName + "]";
	}

}
