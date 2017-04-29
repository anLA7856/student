package csust.student.model;

import android.os.Environment;
import csust.student.activity.ChatActivity;
import csust.student.info.UserInfo;

public class Model {

	public static int INIT_COUNT = 15;

	public static String BASEHTTPURL = "http://10.129.92.21:8989";
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
	
	/*保存一个当前聊天界面的应用，
	 * 用途，当有新消息时候，如果是当前聊天界面的，就
	 * 直接修改。而其他的，则先存到数据库中。
	 * 
	 */
	public static ChatActivity MYCHATACTIVITY = null;
	
	// APP客服KEY
	public static String APPKEY = "f241ebf4d4a1e1dfae6f1a3e49aad2f5";
	/** 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY */
	public static final String APP_KEY = "3987368837";

	/**
	 * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
	 * 
	 * <p>
	 * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响， 但是没有定义将无法使用 SDK 认证登录。
	 * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
	 * </p>
	 */
	public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

	/**
	 * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
	 * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利 选择赋予应用的功能。
	 * 
	 * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的 使用权限，高级权限需要进行申请。
	 * 
	 * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
	 * 
	 * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
	 * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
	 */
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";
}
