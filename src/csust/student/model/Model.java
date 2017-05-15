package csust.student.model;

import android.os.Environment;
import csust.student.activity.ChatActivity;
import csust.student.info.UserInfo;

/**
 * 存放一些全局变量
 * 
 * @author anLA7856
 *
 */
public class Model {

	public static int INIT_COUNT = 15;

	// public static String BASEHTTPURL = "http://120.76.146.248:8080";
	public static String BASEHTTPURL = "http://192.168.191.1:8989";
	public static String HTTPURL = BASEHTTPURL + "/Sign1.1/";

	public static String BASELOCATION = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
			.getAbsolutePath();
	public static String LOCALSTORAGE = BASELOCATION + "/sign/";
	public static String REPORTDATALOCATION = LOCALSTORAGE + "download/";
	public static String UPLOADPIC = "stuUploadPic";
	public static String UPLOADSIGNINFO = "uploadSignInfo?";
	public static String GETNOTSIGNINFO = "getNotSignInfo?";
	public static String VERTIFYIFCANSIGN = "vertifyIfCanSign?";
	public static String SEARCHFORLIST = "searchForList?";
	public static String ADDNEWCOURSE = "addNewCourse?";
	public static String GETCOURSETOTALSIGNRATE = "getCourseTotalSignRate?";
	public static String GETSIGNINFOLISTOFCOURSE = "getSignInfoListOfCourse?";
	public static String GETSTUCOURSE = "getStuCourse?";
	public static String REGISTET = "stuAdd";
	public static String LOGIN = "stuLogin";
	public static String GETTEACHERLIST = "getTeacherList?";
	public static String STUMODIFYPASSWORD = "stuModifyPassword?";
	public static String STUDELETECOURSE = "stuDeleteCourse?";
	public static String STUCHATMESSAGEADD = "stuChatMessageAdd?";
	public static String STUGETALLCHATMESSAGE = "stuGetAllChatMessage?";
	public static String STUGETNEWCHATMESSAGE = "stuGetNewChatMessage?";

	// 用于加载图片的。
	public static String USERHEADURL = BASEHTTPURL + "/Sign1.1/stuPic/";
	public static String USERREPORTURL = BASEHTTPURL + "/Sign1.1/xls/";

	public static boolean IMGFLAG = false;
	public static UserInfo MYUSERINFO = null;

	/*
	 * 保存一个当前聊天界面的应用， 用途，当有新消息时候，如果是当前聊天界面的，就 直接修改。而其他的，则先存到数据库中。
	 */
	public static ChatActivity MYCHATACTIVITY = null;

}
