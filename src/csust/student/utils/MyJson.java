package csust.student.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import csust.student.info.CourseInfo;
import csust.student.info.SearchCourseInfo;
import csust.student.info.SignInfo;
import csust.student.info.StudentSignDetail;
import csust.student.info.TeacherListInfo;
import csust.student.info.UserInfo;

public class MyJson {
	public List<UserInfo> getUserInfoList(String result) {
		List<UserInfo> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<UserInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				UserInfo info = new UserInfo();
				
				info.setStudent_id(Integer.parseInt(job.getString("student_id")));
				info.setStudent_name(job.getString("student_name"));
				info.setStudent_num(job.getString("student_num"));
				info.setStudent_sex(job.getString("student_sex"));
				info.setStudent_username(job.getString("student_username"));
				info.setStudent_password(job.getString("student_password"));
				
				list.add(info);
			}
		} catch (JSONException e) {
		}
		return list;
	}
	/**
	 * 用于把课程信息的json格式转化过来。
	 * @param result
	 * @return
	 */
	public List<CourseInfo> getCourseInfoList(String result) {
		List<CourseInfo> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<CourseInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				CourseInfo info = new CourseInfo();
				info.setCourse_id(job.getString("course_id"));
				info.setCourseName(job.getString("courseName"));
				info.setTeacherName(job.getString("teacherName"));
				info.setTeacherNum(job.getString("teacherNum"));
				list.add(info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 用于把待签到的信息列出来。
	 * @param result
	 * @return
	 */
	public List<SignInfo> getNotSignInfoList(String result) {
		List<SignInfo> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<SignInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				SignInfo info = new SignInfo();
				info.setSign_courseName(job.getString("sign_courseName"));
				info.setSign_courseNum(job.getString("sign_courseNum"));
				info.setSign_date(job.getString("sign_date"));
				info.setSign_teacherName(job.getString("sign_teacherName"));
				info.setTeacher_wifimac(job.getString("teacher_wifimac"));
				info.setAlow_sign_id(job.getString("alow_sign_id"));
				list.add(info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 用于解析并且获得searchinfolist，获取搜索的课程结果
	 * @param result
	 * @return
	 */
	public List<SearchCourseInfo> getSearchCourseInfoList(String result){
		List<SearchCourseInfo> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<SearchCourseInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				SearchCourseInfo info = new SearchCourseInfo();
				
				info.setCourse_id(job.getInt("course_id"));
				info.setCourse_name(job.getString("course_name"));
				info.setTeacher_id(job.getInt("teacher_id"));
				info.setTeacher_name(job.getString("teacher_name"));				
				
				list.add(info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 用于获得studentsigndetailinfolist的list，解析成list类。
	 * @param result
	 * @return
	 */
	public List<StudentSignDetail> getStudentSignDetailInfoList(String result){
		List<StudentSignDetail> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<StudentSignDetail>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				StudentSignDetail info = new StudentSignDetail();
				
				info.setSign_date(job.getString("sign_date"));
				info.setSign_state(job.getString("sign_state"));
				
		
				
				list.add(info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	public List<TeacherListInfo> getTeacherList(String result){
		List<TeacherListInfo> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<TeacherListInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				TeacherListInfo info = new TeacherListInfo();
		
				info.setTeacher_id(job.getInt("teacher_id"));
				info.setTeacher_name(job.getString("teacher_name"));
				info.setTeacher_username(job.getString("teacher_username"));
				
				list.add(info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
}
